package server;

import data.Problem;
import data.Submission;
import data.Testcase;

import java.util.ArrayList;

public class JudgeThread extends Thread{
    private Submission submission;
    private Problem curProblem;
    private ArrayList<Testcase> testcases;
    private DatabaseAdapter dbAdapter;

    public JudgeThread(Submission us){
        this.submission = us;
        this.dbAdapter = new DatabaseAdapter(JudgeServer.serverConfig);
    }

    public void run() {
        System.out.println("Started judgin thread: " + this.submission.toString());

        this.curProblem = dbAdapter.getProblem(this.submission.problemID);
        this.testcases = dbAdapter.getTestCases(this.submission.problemID);

        System.out.println("Running testcases for submission #" + this.submission.submissionID);
        for (Testcase tc : this.testcases){
            System.out.println("Running testcase #" + tc.testcaseID);

            RunnerThread rt = new RunnerThread(tc, this.submission);
            rt.run();
        }

        this.dbAdapter.setSubmissionStatus(this.submission.submissionID, "DONE");
        System.out.println("Finished judging submission #" + this.submission.submissionID);
    }
}
