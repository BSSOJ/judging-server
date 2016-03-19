package server;

import data.Submission;

import java.sql.ResultSet;

public class JudgeThread extends Thread{
    public Submission submission;

    public JudgeThread(Submission us){
        this.submission = us;
    }

    public void run() {
        System.out.println("Starting judging of submission #" + this.submission.submissionID);


    }
}
