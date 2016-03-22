package runners;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class JavaRunner implements Runner{

    @Override
    public ResultCode compile(String workingDirectory, String problemCode) {
        try{
            Process p = Runtime.getRuntime().exec("javac -d " + workingDirectory + " " + problemCode + ".java");

            int returnCode = p.waitFor();

            return (returnCode == 0 ? ResultCode.COMPILE_SUCCESS : ResultCode.CE);
        } catch (Exception ex){
            return ResultCode.IR;
        }
    }

    @Override
    public ResultCode run(String workingDirectory, String binaryName) {
        try{
            Process p = Runtime.getRuntime().exec("LD_PRELOAD=./EasySandbox.so java -cp " + workingDirectory +
                    " " + binaryName);

            //Initialize IO Streams
            BufferedReader processReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            PrintWriter processWriter = new PrintWriter(p.getOutputStream(), true);

            BufferedReader inputFileReader = new BufferedReader(new FileReader(workingDirectory + "/input.txt"));
            PrintWriter tempOutputWriter = new PrintWriter(workingDirectory + "/temp_output.txt", "UTF-8");

            //Enter the test data and capture output
            while(true){
                String line = inputFileReader.readLine();
                if (line == null) break;

                processWriter.println(line);
            }

            while(true){
                String line = processReader.readLine();
                if (line == null) break;

                tempOutputWriter.println(line);
            }

            if (p.waitFor() != 0){
                return ResultCode.RE;
            }

            //Judge the output
            String output1 = workingDirectory + "/output.txt";
            String output2 = workingDirectory + "/temp_output.txt";

            Process judge = Runtime.getRuntime().exec("diff -wBb " + output1 + " " + output2);

            return (judge.waitFor() == 0 ? ResultCode.AC : ResultCode.WA);
        } catch (Exception ex){
            return ResultCode.IR;
        }
    }
}
