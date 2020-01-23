package com.aws.apn.migration.serverPlugin.services;

import com.atlassian.jira.service.services.export.ExportService;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

@Scanned
public class DataExportService {

    @ComponentImport
    private final ExportService exportService;

    public DataExportService(ExportService exportService) {
        this.exportService = exportService;
    }

    public void doExport() {


        this.exportService.init();
    }
}
