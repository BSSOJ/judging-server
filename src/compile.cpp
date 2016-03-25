#include <cstdlib>
#include <vector>
#include <string>

using namespace std;

/*
	Usage:
		./compile [source_file] [language] [output_dir]
*/

int main(int argc, char** argv) {
	vector<string> args(argv, argv + argc);

	string compileCommand;
	if (args[2] == "cpp") {
		compileCommand = "g++ -o " + args[3] + "/main -O2 " + args[1];
	} else if (args[2] == "cpp11") {
		compileCommand = "g++ -o " + args[3] + "/main -std=c++11 -O2 " + args[1];
	} else if (args[2] == "java") {
		compileCommand = "javac " + args[1] + " -d " + args[3];
	}

	int returnCode = system(compileCommand.c_str());

	return WEXITSTATUS(returnCode);
}