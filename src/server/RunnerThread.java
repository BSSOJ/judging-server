package server;

import data.Submission;
import data.Testcase;
import org.apache.commons.io.FileUtils;
import runners.CodeRunner;
import runners.ResultCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

public class RunnerThread extends Thread {
    private Testcase tc;
    private Submission subm;
    private DatabaseAdapter dbAdapter;

    public ResultCode rc;

    public RunnerThread(Testcase _tc, Submission _subm){
        this.tc = _tc;
        this.subm = _subm;
        dbAdapter = new DatabaseAdapter(JudgeServer.serverConfig);
    }

    public void downloadFile(String url, String dest) throws Exception {
        Runtime.getRuntime().exec("wget " + url + " -O " + dest);
    }

    public void run(){
        //Setting up the directory for the submission

        String workingDirectory = "submissions/" + subm.submissionID + "/" + tc.testcaseID + "/";
        File directory = new File(workingDirectory);

        directory.mkdirs();

        //Write the source, input and output
        System.out.println("Downloading judge data for submission #" + this.subm.submissionID);

        String sourceFileName = workingDirectory + LanguageUtils.getFileName(subm.language,
                dbAdapter.getProblem(subm.problemID).problemCode);

        System.out.printf("sourceFileName=%s\n", sourceFileName);

        try {
            PrintWriter sourceWriter = new PrintWriter(sourceFileName, "UTF-8");
            sourceWriter.println(subm.sourceCode);

            sourceWriter.close();
        } catch (Exception ex){
            System.err.println("Error writing source file to disk: " + ex.getLocalizedMessage());
            this.dbAdapter.addTestcaseResults(subm.submissionID, tc.testcaseID, "IR");
            return;
        }

        System.out.println("Source file written to disk");

        try {
            downloadFile(tc.inputURL, workingDirectory + "/input.txt");
            downloadFile(tc.outputURL, workingDirectory + "/output.txt");
        } catch (Exception ex){
            this.dbAdapter.addTestcaseResults(subm.submissionID, tc.testcaseID, "IR");
            return;
        }

        System.out.println("Done downloading judge data");

        this.rc = new CodeRunner().judgeSolution(subm.language, this.dbAdapter.
                getProblem(subm.problemID).problemCode, workingDirectory);

        if (this.rc == ResultCode.AC) dbAdapter.addTestcaseResults(subm.submissionID, tc.testcaseID, "AC");
        if (this.rc == ResultCode.WA) dbAdapter.addTestcaseResults(subm.submissionID, tc.testcaseID, "WA");
        if (this.rc == ResultCode.CE) dbAdapter.addTestcaseResults(subm.submissionID, tc.testcaseID, "CE");
        if (this.rc == ResultCode.RE) dbAdapter.addTestcaseResults(subm.submissionID, tc.testcaseID, "RE");
    }
}
