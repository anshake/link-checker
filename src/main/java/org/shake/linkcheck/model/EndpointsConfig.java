package org.shake.linkcheck.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

@ConfigurationProperties("setup")
public class EndpointsConfig
{
    private Map<String, EndpointsConfigEntry> endpoints;


    public Map<String, EndpointsConfigEntry> getEndpoints()
    {
        return endpoints;
    }

    public void setEndpoints(Map<String, EndpointsConfigEntry> endpoints)
    {
        this.endpoints = endpoints;
    }

    public Collection<String> keys()
    {
        return this.endpoints.keySet();
    }

    public EndpointsConfigEntry detectEndpoint(URI link)
    {
        String rawPath = link.getRawPath();
        for (String path : this.endpoints.keySet())
        {
            if (rawPath.contains(path))
            {
                return endpoints.get(path);
            }
        }
        return null;
    }
}
