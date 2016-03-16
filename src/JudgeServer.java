import java.sql.*;
import java.util.LinkedList;
import java.util.Queue;

public class JudgeServer {

    public static final int MaxThreadCount = 1;
    public static final String mysql_dbUsername = "judgehost";
    public static final String mysql_dbPassword = "bssojPASSWORD123456";

    public static Queue<UserSubmission> submissions;


    public static void main(String[] args) {
        submissions = new LinkedList<UserSubmission>();

        while(true){
            if (submissions.size() < MaxThreadCount){
                try {
                    /*
                        Get new submission from database
                     */

                    Connection conn = DriverManager.getConnection("jdbc:mysql://bssoj-dev.ddns.net:3306/bssoj-sample-database",
                            mysql_dbUsername, mysql_dbPassword);

                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT SubmissionID, ProblemID, UserID, SourceCode FROM submissions "
                                                    + "WHERE SubmissionStatus='PEND'"
                                                    + "ORDER BY SubmissionDate ASC");

                    /*
                        If a new submission exists in the database, set it to "PROG" and
                        add it to the judging queue
                     */
                    if (rs.next()){
                        UserSubmission subm = new UserSubmission();
                        subm.submissionID = Integer.parseInt(rs.getString("SubmissionID"));
                        subm.problemID = Integer.parseInt(rs.getString("ProblemID"));
                        subm.userID = Integer.parseInt(rs.getString("UserID"));
                        subm.sourceCode = rs.getString("SourceCode");

                        //Update the submission status in database
                        conn.createStatement().executeQuery("UPDATE submissions SET SubmissionStats = 'PROG'"
                                                        + "WHERE SubmissionID = '" + subm.submissionID + "';");

                        submissions.add(subm);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
