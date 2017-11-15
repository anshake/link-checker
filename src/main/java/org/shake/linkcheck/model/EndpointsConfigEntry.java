package org.shake.linkcheck.model;

import org.springframework.http.HttpMethod;

import java.util.Map;
import java.util.Set;

public class EndpointsConfigEntry
{
    private HttpMethod method;
    private Set<String> fields;
    private Map<String, String> headers;
    private String body;

    public HttpMethod getMethod()
    {
        return method;
    }

    public void setMethod(HttpMethod method)
    {
        this.method = method;
    }

    public Set<String> getFields()
    {
        return fields;
    }

    public void setFields(Set<String> fields)
    {
        this.fields = fields;
    }

    public Map<String, String> getHeaders()
    {
        return headers;
    }

    public void setHeaders(Map<String, String> headers)
    {
        this.headers = headers;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }
}
