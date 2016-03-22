package runners;

/*
    Runner interface that has 2 methods:
        compile(): Compiles the source code, returns CE if failed
        run(): Runs the source code, returns RE if failed. Returns AC or WA otherwise.
 */

public interface Runner {
    public ResultCode compile(String workingDirectory, String problemCode);
    public ResultCode run(String workingDirectory, String binaryName);
}
