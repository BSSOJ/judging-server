package server;

import data.Problem;
import data.Submission;
import data.Testcase;
import runners.ResultCode;

import java.util.ArrayList;

public class JudgeThread extends Thread{
    private Submission submission;
    private Problem curProblem;
    private ArrayList<Testcase> testcases;
    private DatabaseAdapter dbAdapter;

    private ResultCode submissionResult;

    public JudgeThread(Submission us){
        this.submission = us;
        this.dbAdapter = new DatabaseAdapter(JudgeServer.serverConfig);
    }

    public void run() {
        System.out.println("Started judging thread: " + this.submission.toString());

        this.submissionResult = ResultCode.AC;

        this.curProblem = dbAdapter.getProblem(this.submission.problemID);
        this.testcases = dbAdapter.getTestCases(this.submission.problemID);

        System.out.println("Running testcases for submission #" + this.submission.submissionID);
        for (Testcase tc : this.testcases){
            System.out.println("Running testcase #" + tc.testcaseID);

            RunnerThread rt = new RunnerThread(tc, this.submission);
            rt.run();

            if (rt.rc == ResultCode.WA) this.submissionResult = ResultCode.WA;
            if (rt.rc == ResultCode.RE) this.submissionResult = ResultCode.RE;
            if (rt.rc == ResultCode.CE) this.submissionResult = ResultCode.CE;
            if (rt.rc == ResultCode.IR) this.submissionResult = ResultCode.IR;
        }

        if (this.submissionResult == ResultCode.AC)
            this.dbAdapter.setSubmissionResult(this.submission.submissionID, "AC");

        if (this.submissionResult == ResultCode.WA)
            this.dbAdapter.setSubmissionResult(this.submission.submissionID, "WA");

        if (this.submissionResult == ResultCode.TLE)
            this.dbAdapter.setSubmissionResult(this.submission.submissionID, "TLE");

        if (this.submissionResult == ResultCode.MLE)
            this.dbAdapter.setSubmissionResult(this.submission.submissionID, "MLE");

        if (this.submissionResult == ResultCode.OLE)
            this.dbAdapter.setSubmissionResult(this.submission.submissionID, "OLE");

        if (this.submissionResult == ResultCode.CE)
            this.dbAdapter.setSubmissionResult(this.submission.submissionID, "CE");

        if (this.submissionResult == ResultCode.RE)
            this.dbAdapter.setSubmissionResult(this.submission.submissionID, "RE");

        if (this.submissionResult == ResultCode.IR)
            this.dbAdapter.setSubmissionResult(this.submission.submissionID, "IR");

        this.dbAdapter.setSubmissionStatus(this.submission.submissionID, "DONE");
        System.out.println("Finished judging submission #" + this.submission.submissionID);
    }
}
