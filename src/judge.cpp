#include <cstdlib>
#include <string>
#include <vector>

using namespace std;

/*
	Usage:
		./judge [output_1] [output_2]
*/

int main(int argc, char** argv){
	vector<string> args(argv, argv + argc);

	string diffCommand = "diff " + args[1] + " " + args[2] + " -wBb";

	int returnCode = system(diffCommand.c_str());

	return WEXITSTATUS(returnCode);
}