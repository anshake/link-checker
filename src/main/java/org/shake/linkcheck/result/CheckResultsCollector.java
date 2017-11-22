package org.shake.linkcheck.result;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.shake.linkcheck.model.CheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;
import java.util.Set;

public class CheckResultsCollector
{
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Set<URI> toBeChecked = Sets.newLinkedHashSet();
    private Map<URI, CheckResult> visited = Maps.newLinkedHashMap();

    /**
     * @param checkResult result of a link check
     */
    public synchronized void storeCheckResult(CheckResult checkResult)
    {
        if (checkResult == null)
        {
            return;
        }

        logger.debug("[checked] {} '{}' {}", checkResult.getStatus(), checkResult.getMessage(), checkResult
                .getOriginalLink());
        toBeChecked.remove(checkResult.getOriginalLink());
        visited.put(checkResult.getOriginalLink(), checkResult);
    }

    public synchronized boolean registerToBeChecked(URI originalLink)
    {
        if (visited.containsKey(originalLink))
        {
            toBeChecked.remove(originalLink);
            return false;
        }
        boolean added = toBeChecked.add(originalLink);
        if (added)
        {
            logger.debug("[scheduled] {}", originalLink);
        }
        return added;
    }

    synchronized boolean noMoreChecks()
    {
        return toBeChecked.isEmpty();
    }

    synchronized int visitedCount()
    {
        return this.visited.size();
    }
}
