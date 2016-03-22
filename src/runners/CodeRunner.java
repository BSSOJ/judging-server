package runners;

import data.Submission;
import data.Testcase;

public class CodeRunner {
    public ResultCode judgeSolution(String language, String problemCode, String workingDirectory){
        Runner runner = null;

        if (language.equals("cpp")){
            runner = new CPPRunner();
        } else if (language.equals("cpp11")){
            runner = new CPP11Runner();
        } else if (language.equals("java")){
            runner = new JavaRunner();
        }

        if (runner == null){
            return ResultCode.IR;
        }

        if (runner.compile(workingDirectory, problemCode) == ResultCode.COMPILE_SUCCESS){
            return runner.run(workingDirectory, problemCode);
        } else {
            return ResultCode.CE;
        }
    }
}
