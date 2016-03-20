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

    public void downloadFile(String url, String dest){
        try{
            Runtime.getRuntime().exec("wget " + url + " -O " + dest);
        } catch (Exception ex){

        }
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
        }

        System.out.println("Source file written to disk");

        downloadFile(tc.inputURL, workingDirectory + "/input.txt");
        downloadFile(tc.outputURL, workingDirectory + "/output.txt");

        System.out.println("Done downloading judge data");

        //Compile the source files
        System.out.println("Compiling source files for testcase #" + this.tc.testcaseID);

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
        System.out.println("Running and judging files for testcase #" + this.tc.testcaseID);
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
