package org.shake.linkcheck.model;

import java.util.Set;

public class FieldsAwareConfigEntry extends EndpointsConfigEntry
{
    private Set<String> fields;

    public Set<String> getFields()
    {
        return fields;
    }

    public void setFields(Set<String> fields)
    {
        this.fields = fields;
    }
}
