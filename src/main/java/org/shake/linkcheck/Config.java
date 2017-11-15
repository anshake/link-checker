package org.shake.linkcheck;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.shake.linkcheck.model.EndpointsConfig;
import org.shake.linkcheck.model.EndpointsConfigEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Configuration
public class Config
{
    @Bean
    public ObjectMapper yamlMapper()
    {
        return new YAMLMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    public RestTemplate restTemplate()
    {
        return new RestTemplate();
    }

    @Bean
    public EndpointsConfig endpoints(@Value("${api-endpoints}") Resource endpointsRes, ObjectMapper yamlMapper) throws
            IOException
    {
        TypeReference<Map<String, EndpointsConfigEntry>> tr = new TypeReference<Map<String, EndpointsConfigEntry>>()
        {
        };
        Map<String, EndpointsConfigEntry> endpointsConfig = yamlMapper.readValue(endpointsRes.getInputStream(), tr);
        EndpointsConfig config = new EndpointsConfig();
        config.setEndpoints(endpointsConfig);
        return config;
    }

}
