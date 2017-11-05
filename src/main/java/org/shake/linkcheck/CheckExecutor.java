package org.shake.linkcheck;

import org.shake.linkcheck.model.CheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
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

    private Queue<LinkCheck> linksToCheck = new ArrayBlockingQueue<LinkCheck>(10000);
    private final ExecutorService executorService = Executors.newFixedThreadPool(8);

    public CheckExecutor(LinkCheckFactory checkFactory,
                         @Value("${config.startUrl}") String startUrl,
                         CheckResultsCollector resultsCollector)
    {
        this.checkFactory = checkFactory;
        this.startUrl = startUrl;
        this.resultsCollector = resultsCollector;
    }

    public void start() throws URISyntaxException
    {
        linksToCheck.offer(checkFactory.createCheck(startUrl));
        executorService.submit(new InnerExecutor());
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
                    executorService.submit(new InnerChecker(check));
                }
                else
                {
                    logger.debug("Got STOP signal. No more checks");
                    break;
                }
            }

            executorService.shutdown();
        }
    }


    private final class InnerChecker implements Runnable
    {
        final LinkCheck linkCheck;

        public InnerChecker(LinkCheck linkToCheck)
        {
            this.linkCheck = linkToCheck;
        }

        @Override
        public void run()
        {
            try
            {
                // resultsCollector should be given original link before the check

                resultsCollector.registerToBeChecked(linkCheck.getOriginalLink());
                if (resultsCollector.noMoreChecks())
                {
                    logger.debug("Offering STOP signal");
                    linksToCheck.offer(checkFactory.stopCheck());
                }

                CheckResult checkResult = linkCheck.call();
                Map<String, URI> collectedLinks = checkResult.getCollectedLinks();
                if (collectedLinks != null)
                {
                    for (URI link : collectedLinks.values())
                    {
                        resultsCollector.registerToBeChecked(link);
                    }
                }

                if (!resultsCollector.storeCheckResult(checkResult))
                {
                    linksToCheck.offer(checkFactory.stopCheck());
                }
                else
                {
                    if (collectedLinks != null)
                    {
                        for (URI link : collectedLinks.values())
                        {
                            linksToCheck.offer(checkFactory.createCheck(link));
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
    }

}
