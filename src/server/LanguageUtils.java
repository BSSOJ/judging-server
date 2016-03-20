package server;

public class LanguageUtils {
    public static String getFileName(String language, String problemName){
        if (language.equals("cpp") || language.equals("cpp11"))
            return problemName + ".cpp";

        if (language.equals("java"))
            return problemName + ".java";

        return null;
    }

    public static boolean compileSourceFile(String workingDirectory, String problemCode, String language){
        try {
            if (language.equals("cpp")) {
                String cmd = "g++ -o " + workingDirectory + problemCode + " " + workingDirectory + language + ".cpp";
                Process p = Runtime.getRuntime().exec(cmd);
                int returnCode = p.waitFor();

                if (returnCode == 0)
                    return true;
                return false;
            } else if (language.equals("cpp11")){
                String cmd = "g++ -o -std=c++11" + workingDirectory + problemCode + " " + workingDirectory +
                        problemCode + ".cpp";

                Process p = Runtime.getRuntime().exec(cmd);
                int returnCode = p.waitFor();

                if (returnCode == 0)
                    return true;
                return false;
            } else if (language.equals("java")){
                String cmd = "javac -d " + workingDirectory + " " + problemCode + ".java";
                Process p = Runtime.getRuntime().exec(cmd);

                int returnCode = p.waitFor();

                if (returnCode == 0)
                    return true;
                return false;
            }

            return false;
        } catch (Exception ex){
            System.err.println("Compilation Error: " + ex.getLocalizedMessage());
            return false;
        }
    }
}
