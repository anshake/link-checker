package org.shake.linkcheck.model;

import com.google.common.collect.Maps;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.Map;

public class CheckResult
{
    private final URI originalLink;
    private final HttpStatus status;
    private final String message;

    //TODO add support for multiple links per field (in case the field is array)
    private final Map<String, URI> collectedLinks = Maps.newHashMap();

    public CheckResult(URI link, String message) {
        this.originalLink = link;
        this.message = message;
        this.status = null;
    }


    public CheckResult(URI link, HttpStatus status) {
        this.originalLink = link;
        this.status = status;
        this.message = status.getReasonPhrase();
    }

    public CheckResult addCollectedLink(String field, URI link)
    {
        if (StringUtils.isEmpty(field) || link == null)
        {
            return this;
        }

        collectedLinks.put(field, link);
        return this;
    }

    public String getMessage()
    {
        return message;
    }

    public URI getOriginalLink()
    {
        return originalLink;
    }

    public HttpStatus getStatus()
    {
        return status;
    }

    public Map<String, URI> getCollectedLinks()
    {
        return collectedLinks;
    }
}
