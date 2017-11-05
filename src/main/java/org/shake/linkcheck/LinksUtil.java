package org.shake.linkcheck;

import org.apache.commons.beanutils.PropertyUtils;

import java.net.URI;
import java.util.Map;

class LinksUtil
{
    //TODO add support for fields of type array (multiple links per field)
    static URI link(String path, Map body) throws Exception
    {
        Object property = PropertyUtils.getProperty(body, path);
        if (property instanceof String)
        {
            return new URI((String) property);
        }

        return null;
    }

}
