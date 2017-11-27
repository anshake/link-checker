package org.shake.linkcheck;

import com.fasterxml.jackson.databind.JsonNode;
import org.shake.linkcheck.model.CheckResult;
import org.shake.linkcheck.model.EndpointsConfig;
import org.shake.linkcheck.model.FieldsAwareConfigEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * - executes request using provided link
 * - parses response
 * - result contains:
 * - if response is OK, original link and links from the response using provided endpoints config
 * - if response is not OK, error code and original link
 */
class LinkCheck
{
    private final RestTemplate rest;
    private final URI link;
    private final EndpointsConfig config;
    private final boolean stop;
    private Logger logger = LoggerFactory.getLogger(getClass());

    LinkCheck()
    {
        this(null, null, null, true);
    }

    LinkCheck(RestTemplate rest, URI link, EndpointsConfig config)
    {
        this(rest, link, config, false);
    }

    private LinkCheck(RestTemplate rest, URI link, EndpointsConfig config, boolean stop)
    {
        this.rest = rest;
        this.link = link;
        this.config = config;
        this.stop = stop;
    }

    URI getOriginalLink()
    {
        return link;
    }

    /**
     * Checks a give link and collects links from the response
     *
     * @return check result (collected links + original link + request status)
     * @throws Exception ..
     */
    @Nonnull
    CheckResult call() throws Exception
    {
        FieldsAwareConfigEntry endpoint = config.detectEndpoint(link);
        if (endpoint == null)
        {
            return new CheckResult(link, "Does not match any of specified endpoints");
        }

        CheckResult result;
        try
        {
            HttpHeaders headers = null;
            if (endpoint.getHeaders() != null)
            {
                headers = new HttpHeaders();
                for (Map.Entry<String, String> header : endpoint.getHeaders().entrySet())
                {
                    headers.add(header.getKey(), header.getValue());
                }
            }

            if (link.getScheme() == null)
            {
                throw new IllegalArgumentException("Link has be absolute");
            }

            logger.debug("Checking '{}' ...", link);
            HttpEntity<String> entity = new HttpEntity<>(endpoint.getBody(), headers);
            ResponseEntity<JsonNode> responseEntity = rest.exchange(link, endpoint.getMethod(), entity, JsonNode.class);
            result = new CheckResult(link, responseEntity.getStatusCode());
            List<String> fields = endpoint.getFields();
            if (fields != null && !fields.isEmpty())
            {
                JsonNode body = responseEntity.getBody();
                if (body == null)
                {
                    logger.warn("Request resulted in empty response");
                }
                else
                {
                    for (String fld : fields)
                    {
                        try
                        {
                            Collection<URI> links = LinksUtil.link(fld, body);
                            result.addCollectedLinks(fld, links);
                        }
                        catch (Exception ex)
                        {
                            logger.warn("Field {} does not seem to contains a link ({})", fld, ex.getMessage());
                        }
                    }
                }
            }
        }
        catch (ResourceAccessException raEx)
        {
            result = new CheckResult(link, raEx.getMessage());
        }
        catch (HttpStatusCodeException statusEx)
        {
            result = new CheckResult(link, statusEx.getStatusCode());
        }

        return result;
    }

    boolean isStopCheck()
    {
        return stop;
    }
}
