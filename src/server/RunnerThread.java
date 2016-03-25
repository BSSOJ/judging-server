package server;

import data.Submission;
import data.Testcase;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

public class RunnerThread extends Thread {
    private Testcase tc;
    private Submission subm;
    private DatabaseAdapter dbAdapter;

    public int returnCode;

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

        returnCode = 7;

        try {
            if (subm.language.equals("cpp11")) {
                Process p = Runtime.getRuntime().exec("judge_scripts/cpp11.sh " + workingDirectory + " "
                        + LanguageUtils.getFileName(subm.language, dbAdapter.getProblem(subm.problemID).problemCode));

                returnCode = p.waitFor();
            } else if (subm.language.equals("cpp")) {
                Process p = Runtime.getRuntime().exec("judge_scripts/cpp.sh " + workingDirectory + " "
                        + LanguageUtils.getFileName(subm.language, dbAdapter.getProblem(subm.problemID).problemCode));

                returnCode = p.waitFor();
            } else if (subm.language.equals("java")) {
                Process p = Runtime.getRuntime().exec("judge_scripts/java.sh " + workingDirectory + " "
                        + LanguageUtils.getFileName(subm.language, dbAdapter.getProblem(subm.problemID).problemCode));

                returnCode = p.waitFor();
            }
        } catch (Exception ex){
            returnCode = 7;
        }

        switch (returnCode){
            case 0:
                this.dbAdapter.setSubmissionResult(this.subm.submissionID, "AC");
                break;
            case 1:
                this.dbAdapter.setSubmissionResult(this.subm.submissionID, "WA");
                break;
            case 2:
                this.dbAdapter.setSubmissionResult(this.subm.submissionID, "RE");
                break;
            case 3:
                this.dbAdapter.setSubmissionResult(this.subm.submissionID, "TLE");
                break;
            case 4:
                this.dbAdapter.setSubmissionResult(this.subm.submissionID, "MLE");
                break;
            case 5:
                this.dbAdapter.setSubmissionResult(this.subm.submissionID, "OLE");
                break;
            case 6:
                this.dbAdapter.setSubmissionResult(this.subm.submissionID, "CE");
                break;
            default:
                this.dbAdapter.setSubmissionResult(this.subm.submissionID, "IR");
                break;
        }
    }
}
