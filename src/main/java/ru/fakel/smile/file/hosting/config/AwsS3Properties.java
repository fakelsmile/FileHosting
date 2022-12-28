package ru.fakel.smile.file.hosting.config;


import com.amazonaws.regions.Regions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties("app.amazon.s3")
@Data
public class AwsS3Properties {

    private String accessKeyId;
    private String secretAccessKey;
    private String serviceEndpoint;
    private String signerType;
    private String region;
}
