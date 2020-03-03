package com.atlassian.migration.datacenter.core.aws.deployment;

import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.infrastructure.AppProvisioningService;
import com.atlassian.migration.datacenter.spi.infrastructure.ProvisioningConfig;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.atlassian.migration.datacenter.spi.MigrationStage.APP_STACK_FORM;

@Component
public class QuickStartDeploymentService implements AppProvisioningService {

    private CfnApi cloudformation;
    private MigrationService migrationService;

    private String currentStack;

    public QuickStartDeploymentService(CfnApi cloudformation, MigrationService migrationService) {
        this.cloudformation = cloudformation;
        this.migrationService = migrationService;
    }

    @Override
    public void provisionApp(ProvisioningConfig provisioningConfig) throws InvalidMigrationStageError {
        if (!migrationService.getMigrationStage().equals(APP_STACK_FORM)) {
            throw new InvalidMigrationStageError(String.format("must be in %s to provision app", APP_STACK_FORM));
        } else {
            // Do the action for this state
            migrationService.setStage(MigrationStage.APP_STACK_DEPLOY);

            // We would drop the template URL from provisioning config
            cloudformation.provisionStack("https://aws-quickstart/<the quickstart url", provisioningConfig.getStackName(), provisioningConfig.getParams());
            currentStack = provisioningConfig.getStackName();

            // Wait for stack to finish deploying
            ExecutorService service = Executors.newFixedThreadPool(1);
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            service.submit(() -> {
                while (Double.compare(getProvisioningProgress(), 1d) < 0) {
                    try {
                        Thread.sleep(1000 * 60);
                    } catch (InterruptedException e) {
                        future.complete(false);
                    }
                }
                future.complete(true);
            });

            future.thenAccept(success -> {
                if (success) {
                    migrationService.setStage(MigrationStage.MIGRATION_STACK_DEPLOY);
                } else {
                    migrationService.setStage(MigrationStage.PROVISIONING_ERROR);
                }
            });
        }
    }

    @Override
    public double getProvisioningProgress() {
        if (cloudformation.getStatus(currentStack).equals(StackStatus.CREATE_COMPLETE) || cloudformation.getStatus(currentStack).equals(StackStatus.CREATE_FAILED)) {
            return 1d;
        }
        return 0d;
    }
}
