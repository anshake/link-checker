package org.shake.linkcheck;

import com.fasterxml.jackson.core.type.TypeReference;
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
    public YAMLMapper yamlMapper()
    {
        return new YAMLMapper();
    }

    @Bean
    public RestTemplate restTemplate()
    {
        return new RestTemplate();
    }

    @Bean
    public EndpointsConfig endpoints(@Value("${api-endpoints}") Resource endpointsRes, YAMLMapper mapper) throws IOException
    {
        TypeReference<Map<String, EndpointsConfigEntry>> tr = new TypeReference<Map<String, EndpointsConfigEntry>>(){};
        Map<String, EndpointsConfigEntry> endpointsConfig = mapper.readValue(endpointsRes.getInputStream(), tr);
        EndpointsConfig config = new EndpointsConfig();
        config.setEndpoints(endpointsConfig);
        return config;
    }

}
