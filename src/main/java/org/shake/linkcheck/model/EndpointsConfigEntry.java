package org.shake.linkcheck.model;

import org.springframework.http.HttpMethod;

import java.util.Set;

public class EndpointsConfigEntry
{
    private HttpMethod method;
    private Set<String> fields;

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
}
