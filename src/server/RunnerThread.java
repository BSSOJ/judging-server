package server;

import data.Submission;
import data.Testcase;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
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
            Process compile = Runtime.getRuntime().exec("compile " + sourceFileName + " " + subm.language +
                    " " + workingDirectory);

            BufferedReader compileReader = new BufferedReader(new InputStreamReader(compile.getInputStream()));
            new Console(compileReader, "compiler").start();

            int compileReturn = compile.waitFor();

            if (compileReturn != 0){
                this.dbAdapter.addTestcaseResults(subm.submissionID, tc.testcaseID, "CE");
                return;
            }
        } catch (Exception ex){
            this.dbAdapter.addTestcaseResults(subm.submissionID, tc.testcaseID, "IR");
            System.out.println("Error compiling source file: " + ex.getLocalizedMessage());
            return;
        }

        //Run and judge the source files
        System.out.println("Running testcase #" + this.tc.testcaseID);
        try{
            Process runcode = Runtime.getRuntime().exec("runcode " + workingDirectory + " main " + subm.language +
                    " " + workingDirectory + "/input.txt " + workingDirectory + "/output.txt");

            int runReturn = runcode.waitFor();

            if (runReturn != 0){
                this.dbAdapter.addTestcaseResults(subm.submissionID, tc.testcaseID, "RE");
                return;
            }
        } catch (Exception ex){
            this.dbAdapter.addTestcaseResults(subm.submissionID, tc.testcaseID, "IR");
            return;
        }

        //Judge the solutions
        System.out.println("Judging submission #" + subm.submissionID);
        try{
            Process judge = Runtime.getRuntime().exec("judge " + workingDirectory + "output.txt " + workingDirectory
                    + "temp_output.txt");

            System.out.println("fuck me");
            System.out.println("judge " + workingDirectory + "output.txt " + workingDirectory
                    + "temp_output.txt");

            int judgeReturn = judge.waitFor();
            System.out.println("DiffReturnCode=" + judgeReturn);

            if (judgeReturn == 0){
                this.dbAdapter.addTestcaseResults(subm.submissionID, tc.testcaseID, "AC");
            } else {
                this.dbAdapter.addTestcaseResults(subm.submissionID, tc.testcaseID, "WA");
            }
        } catch (Exception ex){
            this.dbAdapter.addTestcaseResults(subm.submissionID, tc.testcaseID, "IR");
        }
    }
}
