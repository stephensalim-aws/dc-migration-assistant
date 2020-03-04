package com.atlassian.migration.datacenter.core.database.properties;

import com.atlassian.migration.datacenter.core.database.enums.BackupFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class LiquibaseBackupProperties {
    private boolean enabled = true;
    private BackupFormat format = BackupFormat.XML;
    private List<String> tables;
    private String s3BucketName = "jira-db-restore-backupbucket-m9u3zolusv9o";
    private String author = "jira-dc-migration-plugin";
    private boolean deleteFileAfterSend = true;

    public void setFormat(String format) {
        this.format = BackupFormat.valueOf(format.toUpperCase());
    }
}
