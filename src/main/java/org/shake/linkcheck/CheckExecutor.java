package org.shake.linkcheck;

import org.shake.linkcheck.model.CheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class CheckExecutor
{
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final LinkCheckFactory checkFactory;
    private final String startUrl;
    private final CheckResultsCollector resultsCollector;

    private final ExecutorService checkThreads = Executors.newFixedThreadPool(8);
    private final ExecutorService orchestratorThread = Executors.newFixedThreadPool(1);
    private Queue<LinkCheck> linksToCheck = new ArrayBlockingQueue<>(10000);
    private Queue<CheckResult> executedChecks = new ArrayBlockingQueue<>(10000);

    public CheckExecutor(LinkCheckFactory checkFactory,
                         @Value("${start-url}") String startUrl,
                         CheckResultsCollector resultsCollector)
    {
        this.checkFactory = checkFactory;
        this.startUrl = startUrl;
        this.resultsCollector = resultsCollector;
    }

    void start() throws URISyntaxException
    {
        orchestratorThread.submit(new CheckOrchestrator());

        linksToCheck.offer(checkFactory.createCheck(startUrl));
        checkThreads.submit(new InnerExecutor());

        orchestratorThread.shutdown();
    }

    private final class CheckOrchestrator implements Runnable
    {
        @Override
        public void run()
        {
            while (true)
            {
                CheckResult checkResult = executedChecks.poll();
                if (checkResult == null)
                {
                    continue;
                }
                resultsCollector.storeCheckResult(checkResult);
                Map<String, Collection<URI>> collectedLinks = checkResult.getCollectedLinks();
                if (collectedLinks != null)
                {
                    for (Collection<URI> links : collectedLinks.values())
                    {
                        for (URI link : links)
                        {
                            if (resultsCollector.registerToBeChecked(link))
                            {
                                linksToCheck.offer(checkFactory.createCheck(link));
                            }
                        }
                    }
                }

                if (resultsCollector.noMoreChecks())
                {
                    linksToCheck.offer(checkFactory.stopCheck());
                    break;
                }
            }
        }
    }

    private final class InnerExecutor implements Runnable
    {
        @Override
        public void run()
        {
            while (true)
            {
                LinkCheck check = linksToCheck.poll();
                if (check == null)
                {
                    continue;
                }

                if (!check.isStopCheck())
                {
                    checkThreads.submit(new InnerChecker(check));
                } else
                {
                    logger.debug("Got STOP signal. No more checks");
                    break;
                }
            }

            checkThreads.shutdown();
        }
    }

    private final class InnerChecker implements Runnable
    {
        final LinkCheck linkCheck;

        InnerChecker(LinkCheck linkToCheck)
        {
            this.linkCheck = linkToCheck;
        }

        @Override
        public void run()
        {
            CheckResult result;
            try
            {
                result = linkCheck.call();
            }
            catch (Exception ex)
            {
                logger.debug("ERROR when checking", ex);
                result = new CheckResult(linkCheck.getOriginalLink(), ex.getMessage());
            }

            executedChecks.offer(result);
        }
    }

}
