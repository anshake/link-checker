package org.shake.linkcheck.result;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.shake.linkcheck.model.CheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;
import java.util.Set;

@Service
public class CheckResultsCollector
{
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Set<URI> toBeChecked = Sets.newLinkedHashSet();
    private Map<URI, CheckResult> visited = Maps.newLinkedHashMap();

    /**
     *
     * @param checkResult
     * @return <code>false</code> if the collector accepts no more check results
     */
    public synchronized boolean storeCheckResult(CheckResult checkResult)
    {
        if (checkResult != null)
        {
            logger.debug("[checked  ] {} '{}' {}", checkResult.getStatus(), checkResult.getMessage(), checkResult.getOriginalLink());
            toBeChecked.remove(checkResult.getOriginalLink());
            visited.put(checkResult.getOriginalLink(), checkResult);
        }

        return !noMoreChecks();
    }

    public synchronized boolean registerToBeChecked(URI originalLink)
    {
        if (visited.containsKey(originalLink))
        {
            logger.debug("{} has already been visited", originalLink);
            toBeChecked.remove(originalLink);
            return false;
        }
        boolean added = toBeChecked.add(originalLink);
        if (!added)
        {
            logger.debug("{} has already been prepared (not visited yet)", originalLink);
        }
        return added;
    }

    public boolean noMoreChecks()
    {
        return toBeChecked.isEmpty();
    }

    public int visitedCount()
    {
        return this.visited.size();
    }
}