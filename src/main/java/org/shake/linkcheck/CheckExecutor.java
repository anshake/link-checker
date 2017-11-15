package org.shake.linkcheck;

import org.shake.linkcheck.model.CheckResult;
import org.shake.linkcheck.result.CheckResultsCollector;
import org.shake.linkcheck.result.CheckStatusReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.Queue;
import java.util.concurrent.*;

@Service
public class CheckExecutor
{
    private final LinkCheckFactory checkFactory;
    private final String startUrl;
    private final CheckStatusReporter reporter;
    private final ExecutorService checkThreads = Executors.newFixedThreadPool(8);
    private final ExecutorService serviceThreads = Executors.newCachedThreadPool();
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Queue<LinkCheck> linksToCheck = new ArrayBlockingQueue<>(10000);
    private Queue<CheckResult> checkResults = new ArrayBlockingQueue<>(10000);

    public CheckExecutor(LinkCheckFactory checkFactory,
                         @Value("${start-url}") String startUrl,
                         CheckStatusReporter reporter)
    {
        this.checkFactory = checkFactory;
        this.startUrl = startUrl;
        this.reporter = reporter;
    }

    void start() throws URISyntaxException, ExecutionException, InterruptedException
    {
        Future<CheckResultsCollector> orchestrator = serviceThreads.submit(
                new CheckOrchestrator(linksToCheck, checkResults, checkFactory, null));

        linksToCheck.offer(checkFactory.createCheck(startUrl));
        serviceThreads.submit(new InnerExecutor());
        serviceThreads.shutdown();

        try
        {
            reporter.writeReport(orchestrator.get());
        }
        catch (Exception ex)
        {
            //
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

                if (check.isStopCheck())
                {
                    logger.debug("Got STOP signal. No more checks");
                    break;
                }
                else
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
