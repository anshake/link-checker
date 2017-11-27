package org.shake.linkcheck.model;

import java.util.List;

public class FieldsAwareConfigEntry extends EndpointsConfigEntry
{
    private List<String> fields;

    public List<String> getFields()
    {
        return fields;
    }

    public void setFields(List<String> fields)
    {
        this.fields = fields;
    }
}
