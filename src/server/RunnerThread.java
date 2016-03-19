package server;

import java.io.FileOutputStream;
import java.io.PrintWriter;

public class RunnerThread extends Thread {

    public int submissionID;
    public int testCaseID;
    public String sourceCode;

    private String judgingDirectory;

    public RunnerThread(int _sid, int _tid, String _src){
        this.submissionID = _sid;
        this.testCaseID = _tid;
        this.sourceCode = _src;

        this.judgingDirectory = "submissions/" + _sid + "/" + _tid + "/";
    }

    public void run(){

    }
}
