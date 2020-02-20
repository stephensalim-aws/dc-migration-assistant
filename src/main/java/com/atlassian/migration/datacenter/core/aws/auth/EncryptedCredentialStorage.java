package com.atlassian.migration.datacenter.core.aws.auth;

import com.atlassian.jira.bc.license.JiraLicenseService;
import com.atlassian.jira.license.LicenseDetails;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Iterator;

/**
 * Class for managing the storage and retrieval of AWS Credentials. Should not be used for direct access to credentials
 * except for in a CredentialsProvider implementation. This class stores credentials encrypted with the server id as the
 * key and uses password based encryption.
 */
@Component
@Primary
public class EncryptedCredentialStorage implements CredentialStorage {

    private final static Logger LOGGER = Logger.getLogger(EncryptedCredentialStorage.class);
    private final static String CHAR_SET = "UTF-8";
    private final static byte[] salt = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };

    private final JiraLicenseService jiraLicenseService;
    private final PluginSettings pluginSettings;

    @Autowired
    public EncryptedCredentialStorage(@ComponentImport PluginSettingsFactory pluginSettingsFactory, @ComponentImport JiraLicenseService jiraLicenseService) {
        this.jiraLicenseService = jiraLicenseService;
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
    }

    /**
     * Creates the Cipher needed to perform the encyption / decryption offerings
     *
     * @param secretKey the string to use as the secret
     * @param opMode    the Cipher operation mode.
     * @return the Cipher object to act on the given string
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     */
    private static Cipher getCipher(final String secretKey, final int opMode) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        int iterationCount = 19;
        KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
        SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(opMode, key, paramSpec);
        return cipher;
    }

    private String getEncryptionKey() throws InvalidKeyException {
        Iterator<LicenseDetails> licenseIterator = this.jiraLicenseService.getLicenses().iterator();
        if (licenseIterator.hasNext()) {
            return licenseIterator.next().getLicenseString();
        } else {
            throw new InvalidKeyException("Jira License does not exist to use as encryption key.");
        }
    }

    @Override
    public String getAccessKeyId() {
        String raw = (String) this.pluginSettings.get(AWS_CREDS_PLUGIN_STORAGE_KEY + ACCESS_KEY_ID_PLUGIN_STORAGE_SUFFIX);
        return this.decryptString(raw);
    }

    @Override
    public void setAccessKeyId(String accessKeyId) {
        this.pluginSettings.put(AWS_CREDS_PLUGIN_STORAGE_KEY + ACCESS_KEY_ID_PLUGIN_STORAGE_SUFFIX, this.encryptString(accessKeyId));
    }

    @Override
    public String getSecretAccessKey() {
        String raw = (String) this.pluginSettings.get(AWS_CREDS_PLUGIN_STORAGE_KEY + SECRET_ACCESS_KEY_PLUGIN_STORAGE_SUFFIX);
        return this.decryptString(raw);
    }

    @Override
    public void setSecretAccessKey(String secretAccessKey) {
        this.pluginSettings.put(AWS_CREDS_PLUGIN_STORAGE_KEY + SECRET_ACCESS_KEY_PLUGIN_STORAGE_SUFFIX, this.encryptString(secretAccessKey));
    }

    /**
     * The string encryption function
     *
     * @param raw the string to be encrypted
     * @return the encrypted string
     */
    private String encryptString(final String raw) {
        try {
            final String key = this.getEncryptionKey();
            Cipher cipher = getCipher(key, Cipher.ENCRYPT_MODE);
            byte[] encrypted = cipher.doFinal(raw.getBytes(CHAR_SET));
            return new String(Base64.getEncoder().encode(encrypted));
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * The string decryption function
     *
     * @param encrypted string to be decrypted
     * @return the decrypted plaintext string
     */
    private String decryptString(final String encrypted) {
        try {
            final String key = this.getEncryptionKey();
            Cipher cipher = getCipher(key, Cipher.DECRYPT_MODE);
            byte[] enc = Base64.getDecoder().decode(encrypted);
            byte[] utf8 = cipher.doFinal(enc);
            return new String(utf8, CHAR_SET);
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
            return null;
        }
    }


}
