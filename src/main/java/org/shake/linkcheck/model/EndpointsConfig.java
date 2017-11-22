package org.shake.linkcheck.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.AntPathMatcher;

import java.net.URI;
import java.util.List;

@ConfigurationProperties("setup")
public class EndpointsConfig
{
    private List<FieldsAwareConfigEntry> endpoints;
    private EndpointsConfigEntry start;

    public List<FieldsAwareConfigEntry> getEndpoints()
    {
        return endpoints;
    }

    public void setEndpoints(List<FieldsAwareConfigEntry> endpoints)
    {
        this.endpoints = endpoints;
    }

    public EndpointsConfigEntry getStart()
    {
        return start;
    }

    public void setStart(EndpointsConfigEntry start)
    {
        this.start = start;
    }

    public FieldsAwareConfigEntry detectEndpoint(URI link)
    {
        AntPathMatcher matcher = new AntPathMatcher();
        String rawPath = link.getRawPath();

        for (FieldsAwareConfigEntry entry : this.endpoints)
        {
            if (matcher.match(entry.getUrl(), rawPath))
            {
                return entry;
            }
        }
        return null;
    }
}
