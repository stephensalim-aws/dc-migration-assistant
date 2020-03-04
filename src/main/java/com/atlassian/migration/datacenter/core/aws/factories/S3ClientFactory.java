package com.atlassian.migration.datacenter.core.aws.factories;

import com.atlassian.migration.datacenter.core.aws.auth.AtlassianPluginAWSCredentialsProvider;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import javax.inject.Inject;

@Component
public class S3ClientFactory implements FactoryBean<S3Client> {

    @Getter
    @Setter
    @Inject
    private AtlassianPluginAWSCredentialsProvider credentialsProvider;

    @Getter
    @Setter
    @Inject
    private RegionService regionService;

    public S3ClientFactory() {
    }

    @Override
    public S3Client getObject() throws Exception {
        return S3Client.builder().credentialsProvider(this.credentialsProvider).region(Region.of(this.regionService.getRegion())).build();
    }

    @Override
    public Class<?> getObjectType() {
        return S3Client.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
