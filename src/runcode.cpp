#include <cstdlib>
#include <string>
#include <vector>

using namespace std;

/*
	Usage:
		./runcode [working_directory] [binary_name] [language] [input_file] [output_file]
*/

int main(int argc, char** argv) {
	vector<string> args(argv, argv + argc);

	string inputPath = args[1] + "/" + args[4];
	string outputPath = args[1] + "/" + args[5];
	string binaryPath = args[1] + "/" + args[2];

	string runCommand;
	if (args[3] == "cpp" || args[3] == "cpp11") {
		runCommand = binaryPath;
	} else if (args[3] == "java") {
		runCommand = "java " + binaryPath;
	}

	//Include EasySandbox Security
	runCommand = "LD_PERLOAD=./EasySandbox.so " + runCommand;

    //Pipe input and output to the run command
    runCommand = runCommand + " < " + inputPath + " > " + outputPath;

    //Stripping first line from EasySandbox
    //See https://github.com/daveho/EasySandbox#limitations

    string stripCommand =" echo \"$(tail -n +2 " + outputPath + " )\" > " + outputPath;

    int returnCode = system(runCommand.c_str());
	if (WEXITSTATUS(returnCode) != 0) return WEXITSTATUS(returnCode);

	returnCode = system(stripCommand.c_str());
	return WEXITSTATUS(returnCode);
}