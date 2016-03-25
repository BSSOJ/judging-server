package data;

public class Submission {
    public int submissionID;
    public int problemID;
    public int userID;
    public String language;
    public String sourceCode;

    public String toString(){
        return "[" + submissionID + "," + problemID + "," + userID + "]";
    }
}
