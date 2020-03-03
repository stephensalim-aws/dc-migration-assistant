package com.atlassian.migration.datacenter.core.aws.auth;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class EncryptedCredentialsStorageTest {

    @InjectMocks
    private EncryptedCredentialsStorage encryptedCredentialsStorage;

    @Test
    public void testEncryption() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final String testString = RandomStringUtils.randomAlphanumeric(new Random().nextInt(50));

        Method encryptMethod = encryptedCredentialsStorage.getClass().getDeclaredMethod("encryptString", String.class);
        encryptMethod.setAccessible(true);

        Method decryptMethod = encryptedCredentialsStorage.getClass().getDeclaredMethod("decryptString", String.class);
        decryptMethod.setAccessible(true);

        String encrypted = (String) encryptMethod.invoke(encryptedCredentialsStorage, testString);
        String decrypted = (String) decryptMethod.invoke(encryptedCredentialsStorage, encrypted);
        assertNotNull(encrypted);
        assertNotNull(decrypted);
        assertEquals(decrypted, testString);
    }

}
