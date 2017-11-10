package org.shake.linkcheck;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.AssertionFailedError;
import org.junit.Test;

import java.net.URI;
import java.util.Collection;

import static org.junit.Assert.*;

public class LinksUtilTest
{
    @Test
    public void linksFromObject() throws Exception
    {
        String json = "{\"a\": 1, \"b\": {\"c1\": 22, \"c2\" : \"http://host\"}}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readValue(json, JsonNode.class);
        Collection<URI> uris = LinksUtil.link("a.b.c2", jsonNode);

        assertNotNull(uris);
        assertTrue(uris.isEmpty());

        uris = LinksUtil.link("b.c2", jsonNode);
        assertEquals("http://host", uris.iterator().next().toString());
    }

    @Test
    public void linksFromArray() throws Exception
    {
        String json = "[{\"a\": 1, \"b\": {\"c1\": 22, \"c2\" : \"http://host\"}}, {\"b\": {\"c2\":  \"https://host2\"}}]";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readValue(json, JsonNode.class);
        Collection<URI> uris = LinksUtil.link("b.c2", jsonNode);

        assertContainsUri(uris, "http://host");
        assertContainsUri(uris, "https://host2");
    }

    private void assertContainsUri(Collection<URI> uris, String strUri)
    {
        for (URI uri : uris)
        {
            if (uri.toString().equals(strUri))
            {
                return;
            }
        }

        throw new AssertionFailedError();
    }

}