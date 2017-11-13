package org.shake.linkcheck;

import org.shake.linkcheck.model.CheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
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

    private final ExecutorService checkThreads = Executors.newFixedThreadPool(8);
    private final ExecutorService orchestratorThread = Executors.newFixedThreadPool(1);
    private final ExecutorService executorThread = Executors.newFixedThreadPool(1);

    private Queue<LinkCheck> linksToCheck = new ArrayBlockingQueue<>(10000);
    private Queue<CheckResult> checkResults = new ArrayBlockingQueue<>(10000);

    public CheckExecutor(LinkCheckFactory checkFactory,
                         @Value("${start-url}") String startUrl)
    {
        this.checkFactory = checkFactory;
        this.startUrl = startUrl;
    }

    void start() throws URISyntaxException
    {
        orchestratorThread.submit(new CheckOrchestrator(linksToCheck, checkResults, checkFactory, null));

        linksToCheck.offer(checkFactory.createCheck(startUrl));
        executorThread.submit(new InnerExecutor());

        orchestratorThread.shutdown();
        executorThread.shutdown();
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

                if (check.isStopCheck())
                {
                    logger.debug("Got STOP signal. No more checks");
                    break;
                } else
                {
                    checkThreads.submit(new InnerChecker(check));
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

            checkResults.offer(result);
        }
    }

}
