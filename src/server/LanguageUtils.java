package server;

public class LanguageUtils {
    public static String getFileName(String language, String problemName){
        if (language.equals("cpp") || language.equals("cpp11"))
            return problemName + ".cpp";

        if (language.equals("java"))
            return problemName + ".java";

        return null;
    }
}
