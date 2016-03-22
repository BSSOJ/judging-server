package runners;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class CPP11Runner implements Runner{

    @Override
    public ResultCode compile(String workingDirectory, String problemCode) {
        try{
            Process p = Runtime.getRuntime().exec("g++ -o " + workingDirectory + "/" + problemCode + " -O2 -std=c++11 " +
                    workingDirectory + "/" + problemCode + ".cpp");

            System.out.println("Command: " + "g++ -o " + workingDirectory + "/" + problemCode + " -O2 -std=c++11 " +
                    workingDirectory + "/" + problemCode + ".cpp");

            int returnCode = p.waitFor();
            System.out.println("Return code: " + returnCode);

            return (returnCode == 0 ? ResultCode.COMPILE_SUCCESS : ResultCode.CE);
        } catch (Exception ex){
            ex.printStackTrace();
            return ResultCode.IR;
        }
    }

    @Override
    public ResultCode run(String workingDirectory, String binaryName) {
        try{
            Process p = Runtime.getRuntime().exec("bash -c \"LD_PRELOAD=./EasySandbox.so " + workingDirectory + "/" +
                    binaryName + "\" < " + workingDirectory + "/input.txt > " + workingDirectory + "/temp_output.txt");

            System.out.println("bash -c \"LD_PRELOAD=./EasySandbox.so " + workingDirectory + "/" +
                    binaryName + "\" < " + workingDirectory + "/input.txt > " + workingDirectory + "/temp_output.txt");



            p.waitFor();
            int runReturnCode = p.exitValue();
            System.out.println("runReturnCode=" + runReturnCode);

            if (runReturnCode != 0) return ResultCode.RE;

            Runtime.getRuntime().exec("sed -i '1d' " + workingDirectory + "/temp_output.txt");

            //Judge the output
            String output1 = workingDirectory + "/output.txt";
            String output2 = workingDirectory + "/temp_output.txt";

            Process judge = Runtime.getRuntime().exec("diff -wBb " + output1 + " " + output2);

            return (judge.waitFor() == 0 ? ResultCode.AC : ResultCode.WA);
        } catch (Exception ex){
            ex.printStackTrace();

            return ResultCode.IR;
        }
    }
}
