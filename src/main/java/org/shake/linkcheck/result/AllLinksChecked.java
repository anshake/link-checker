package org.shake.linkcheck.result;

public class AllLinksChecked implements FinishPredicate
{
    @Override
    public boolean test(CheckResultsCollector results)
    {
        return results.noMoreChecks();
    }
}
