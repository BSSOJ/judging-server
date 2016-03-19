package server;

public class LanguageUtils {
    public static String getCompileCommand(String language, String fileName){
        if (language.equals("cpp"))
            return "g++ -o main -O2 " + fileName;

        if (language.equals("cpp11"))
            return "g++ -o main -std=c++11 -O2 " + fileName;

        if (language.equals("java"))
            return "java " + fileName;

        return null;
    }

    public static String getFileName(String language, String problemName){
        if (language.equals("cpp") || language.equals("cpp11"))
            return problemName + ".cpp";

        if (language.equals("java"))
            return problemName + ".java";

        return null;
    }
}
