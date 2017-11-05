package org.shake.linkcheck;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.net.URI;
import java.util.*;

class LinksUtil
{
    static Collection<URI> link(String path, JsonNode body) throws Exception
    {
        return link(asPath(path), body);
    }

    private static Collection<URI> link(List<String> path, JsonNode body) throws Exception
    {
        Set<URI> result = Sets.newLinkedHashSet();
        if (body.isObject())
        {
            result.addAll(linkFromObject(path, (ObjectNode) body));
        } else if (body.isArray())
        {
            result.addAll(linkFromArray(path, (ArrayNode) body));
        }

        return result;
    }

    private static Collection<URI> linkFromObject(List<String> path, ObjectNode node) throws Exception
    {
        String field = path.get(0);
        JsonNode jsonNode = node.get(field);
        if (jsonNode != null)
        {
            if (jsonNode.isTextual())
            {
                return Lists.newArrayList(new URI(jsonNode.textValue()));
            } else if (node.isContainerNode() && path.size() > 1)
            {
                return link(path.subList(1, path.size()), jsonNode);
            }
        }

        return Collections.emptyList();
    }

    private static Collection<URI> linkFromArray(List<String> path, ArrayNode node) throws Exception
    {
        Set<URI> result = Sets.newLinkedHashSet();
        Iterator<JsonNode> elements = node.elements();
        while (elements.hasNext())
        {
            JsonNode next = elements.next();
            result.addAll(link(path, next));
        }

        return result;
    }

    private static List<String> asPath(String strPath)
    {
        String[] split = strPath.split("\\.");
        return Arrays.asList(split);
    }
}
