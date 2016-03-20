package server;

import data.Submission;
import data.Testcase;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;

public class RunnerThread extends Thread {
    private Testcase tc;
    private Submission subm;
    private DatabaseAdapter dbAdapter;

    public RunnerThread(Testcase _tc, Submission _subm){
        this.tc = _tc;
        this.subm = _subm;
        dbAdapter = new DatabaseAdapter(JudgeServer.serverConfig);
    }

    public void run(){
        //Write the source, input and output
        System.out.println("Downloading judge data for submission #" + this.subm.submissionID);

        String workingDirectory = "submissions/" + subm.submissionID + "/" + tc.testcaseID + "/";

        String sourceFileName = workingDirectory + LanguageUtils.getFileName(subm.language,
                dbAdapter.getProblem(subm.problemID).problemCode);

        try {
            PrintWriter sourceWriter = new PrintWriter(sourceFileName, "UTF-8");
        } catch (Exception ex){
            System.err.println("Error writing source file to disk: " + ex.getLocalizedMessage());
        }

        try{
            FileUtils.copyURLToFile(new URL(tc.inputURL), new File(workingDirectory + "/input.txt"));
            FileUtils.copyURLToFile(new URL(tc.outputURL), new File(workingDirectory + "/output.txt"));
        } catch (Exception ex){
            System.err.println("Error downloading input/output files: " + ex.getLocalizedMessage());
        }

        //Compile the source files
        try {
            Process compile = Runtime.getRuntime().exec("./compile.sh " + sourceFileName + " " + subm.language +
                    " " + workingDirectory);

            int compileReturn = compile.waitFor();

            if (compileReturn != 0){
                this.dbAdapter.addTestcaseResults(subm.submissionID, tc.testcaseID, "CE");
                return;
            }
        } catch (Exception ex){
            this.dbAdapter.addTestcaseResults(subm.submissionID, tc.testcaseID, "CE");
            return;
        }

        //Run and judge the source files
        try{
            Process runcode = Runtime.getRuntime().exec("./runcode.sh " + workingDirectory + " main " + subm.language +
                    " " + workingDirectory + "/input.txt " + workingDirectory + "/output.txt");

            int runReturn = runcode.waitFor();

            if (runReturn != 0){
                this.dbAdapter.addTestcaseResults(subm.submissionID, tc.testcaseID, "WA");
            } else {
                this.dbAdapter.addTestcaseResults(subm.submissionID, tc.testcaseID, "AC");
            }
        } catch (Exception ex){
            this.dbAdapter.addTestcaseResults(subm.submissionID, tc.testcaseID, "RE");
        }
    }
}
