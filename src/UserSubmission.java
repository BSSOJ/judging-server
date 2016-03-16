
public class UserSubmission {
    public int submissionID;
    public int problemID;
    public int userID;
    public String sourceCode;

    public UserSubmission(int _sid, int _pid, int _uid, String _src){
        this.submissionID = _sid;
        this.problemID = _pid;
        this.userID = _uid;
        this.sourceCode = _src;
    }
}
