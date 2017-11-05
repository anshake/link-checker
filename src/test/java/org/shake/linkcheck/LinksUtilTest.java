package org.shake.linkcheck;

import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;

public class LinksUtilTest
{
    @Test
    public void link() throws Exception
    {
        Map<String, Object> map = Maps.newHashMap();
        Map<String, Object> innerMap = Maps.newHashMap();
        Map<String, Object> innerInnerMap = Maps.newHashMap();

        map.put("a", innerMap);
        innerMap.put("b", innerInnerMap);

        innerInnerMap.put("c", "http://host");

        URI link = LinksUtil.link("a.b.c", map);

        assertNotNull(link);
        assertEquals("http://host", link.toString());
    }

}