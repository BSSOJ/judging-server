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

    private int submissionResult;

    public JudgeThread(Submission us){
        this.submission = us;
        this.dbAdapter = new DatabaseAdapter(JudgeServer.serverConfig);
    }

    public void run() {
        System.out.println("Started judging thread: " + this.submission.toString());

        this.submissionResult = 0;

        this.curProblem = dbAdapter.getProblem(this.submission.problemID);
        this.testcases = dbAdapter.getTestCases(this.submission.problemID);

        System.out.println("Running testcases for submission #" + this.submission.submissionID);
        for (Testcase tc : this.testcases){
            System.out.println("Running testcase #" + tc.testcaseID);

            RunnerThread rt = new RunnerThread(tc, this.submission);
            rt.run();

            for (int i = 1; i < 8; i++)
                if (rt.returnCode == i)
                    this.submissionResult = i;
        }

        switch (this.submissionResult){
            case 0:
                this.dbAdapter.setSubmissionStatus(this.submission.submissionID, "AC");
                break;
            case 1:
                this.dbAdapter.setSubmissionStatus(this.submission.submissionID, "WA");
                break;
            case 2:
                this.dbAdapter.setSubmissionStatus(this.submission.submissionID, "RE");
                break;
            case 3:
                this.dbAdapter.setSubmissionStatus(this.submission.submissionID, "TLE");
                break;
            case 4:
                this.dbAdapter.setSubmissionStatus(this.submission.submissionID, "MLE");
                break;
            case 5:
                this.dbAdapter.setSubmissionStatus(this.submission.submissionID, "OLE");
                break;
            case 6:
                this.dbAdapter.setSubmissionStatus(this.submission.submissionID, "CE");
                break;
            default:
                this.dbAdapter.setSubmissionStatus(this.submission.submissionID, "IR");
                break;
        }

        this.dbAdapter.setSubmissionStatus(this.submission.submissionID, "DONE");
        System.out.println("Finished judging submission #" + this.submission.submissionID);
    }
}
