package org.shake.linkcheck;

import org.shake.linkcheck.model.EndpointsConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class LinkCheckFactory
{
    private final RestTemplate rest;
    private final EndpointsConfig endpointsConfig;

    public LinkCheckFactory(RestTemplate rest, EndpointsConfig endpointsConfig)
    {
        this.rest = rest;
        this.endpointsConfig = endpointsConfig;
    }

    LinkCheck createCheck(String link) throws URISyntaxException
    {
        return createCheck(new URI(link));
    }

    public LinkCheck createCheck(URI link)
    {
        return new LinkCheck(rest, link, endpointsConfig);
    }

    public LinkCheck stopCheck()
    {
        return new LinkCheck();
    }
}
