package org.shake.linkcheck.result;

public class MaxVisitedLimitPredicate implements FinishPredicate
{
    private final int maxVisited;

    public MaxVisitedLimitPredicate(int maxVisited)
    {
        this.maxVisited = maxVisited;
    }

    @Override
    public boolean test(CheckResultsCollector resultsCollector)
    {
        return resultsCollector.visitedCount() >= this.maxVisited;
    }
}
