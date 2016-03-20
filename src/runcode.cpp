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
	string binaryOutputPath = args[1] + "/temp_output.txt";
	string binaryPath = args[1] + "/" + args[2];

	string runCommand;
	if (args[3] == "cpp" || args[3] == "cpp11") {
		runCommand = "cat " + inputPath + " | " + binaryPath + " > " + binaryOutputPath;
	} else if (args[3] == "java") {
		runCommand = "cat " + inputPath + " | java " + binaryPath + " > " + binaryOutputPath;
	}

	int returnCode = system(runCommand.c_str());

	return WEXITSTATUS(returnCode);
}