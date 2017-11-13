package org.shake.linkcheck;

import com.google.common.collect.Lists;
import org.shake.linkcheck.model.CheckResult;
import org.shake.linkcheck.result.AllLinksChecked;
import org.shake.linkcheck.result.CheckResultsCollector;
import org.shake.linkcheck.result.FinishPredicate;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Predicate;

public class CheckOrchestrator implements Runnable
{
    private final Queue<LinkCheck> toBeChecked;
    private final Queue<CheckResult> resultsQueue;
    private final LinkCheckFactory checkFactory;

    private final CheckResultsCollector resultsCollector;
    private final Predicate<CheckResultsCollector> finishPredicate;

    CheckOrchestrator(Queue<LinkCheck> toBeChecked, Queue<CheckResult> resultsQueue, LinkCheckFactory checkFactory,
                      List<FinishPredicate> finishPredicates)
    {
        this.toBeChecked = toBeChecked;
        this.resultsQueue = resultsQueue;
        this.checkFactory = checkFactory;

        this.resultsCollector = new CheckResultsCollector();

        Predicate<CheckResultsCollector> mainPredicate = new AllLinksChecked();
        if (finishPredicates != null)
        {
            for (FinishPredicate pr : finishPredicates)
            {
                mainPredicate = mainPredicate.or(pr);
            }
        }

        this.finishPredicate = mainPredicate;
    }

    @Override
    public void run()
    {
        while (true)
        {
            CheckResult checkResult = resultsQueue.poll();
            if (checkResult == null)
            {
                continue;
            }
            resultsCollector.storeCheckResult(checkResult);
            Map<String, Collection<URI>> collectedLinks = checkResult.getCollectedLinks();
            List<URI> moreToCheck = Lists.newArrayList();
            if (collectedLinks != null)
            {
                for (Collection<URI> links : collectedLinks.values())
                {
                    for (URI link : links)
                    {
                        if (resultsCollector.registerToBeChecked(link))
                        {
                            moreToCheck.add(link);
                        }
                    }
                }
            }

            if (finishPredicate.test(resultsCollector))
            {
                stopSignal();
                break;
            }

            for (URI link : moreToCheck)
            {
                toBeChecked.offer(checkFactory.createCheck(link));
            }
        }
    }

    private void stopSignal()
    {
        toBeChecked.offer(checkFactory.stopCheck());
    }

}
