package org.shake.linkcheck.model;

import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

public class EndpointsConfigEntry
{
    private HttpMethod method;
    private List<String> fields;
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

    public List<String> getFields()
    {
        return fields;
    }

    public void setFields(List<String> fields)
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
