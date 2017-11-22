package org.shake.linkcheck.model;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.junit.Test;
import org.springframework.http.HttpMethod;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class EndpointsConfigTest
{
    @Test
    public void detectEndpoint() throws Exception
    {
        YAMLMapper yamlMapper = new YAMLMapper();
        EndpointsConfig config = yamlMapper.readValue(getClass().getResourceAsStream
                ("EndpointsConfigTest.yml"), EndpointsConfig.class);

        URI uri = new URI("http://host:8989/path/api/v1/subbase/endpoint1?p=a");
        EndpointsConfigEntry endpoint = config.detectEndpoint(uri);
        assertEquals(HttpMethod.GET, endpoint.getMethod());
    }

}