package org.shake.linkcheck.result;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CheckStatusReporter
{
    private final File reportFile;

    public CheckStatusReporter(@Value("${output.report}") File reportFile)
    {
        this.reportFile = reportFile;
    }

    public void writeReport(CheckResultsCollector resultsCollector) throws IOException
    {

        Path path = Paths.get(reportFile.toURI());
        Files.createDirectories(path.getParent());
        //  TODO
    }

}
