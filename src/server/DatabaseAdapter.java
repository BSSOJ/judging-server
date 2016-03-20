package server;

import data.Problem;
import data.Submission;
import data.Testcase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;

public class DatabaseAdapter {

    private Connection dbConnection;

    public DatabaseAdapter(Map<String, Object> serverConfig){
        try {
            String connectionString = "jdbc:mysql://" + serverConfig.get("db_address") + ":" + serverConfig.get("db_port")
                    + "/" + serverConfig.get("db_name");

            dbConnection = DriverManager.getConnection(connectionString, (String) serverConfig.get("db_username"),
                    (String) serverConfig.get("db_password"));


        } catch (Exception ex){
            System.err.println("Error initializing DatabaseAdapter: " + ex.getLocalizedMessage());
        }
    }

    public int getProblemID(String problemCode){
        try {
            ResultSet rs = this.dbConnection.createStatement().executeQuery("SELECT ProblemID FROM problems WHERE" +
                    "ProblemCode=" + problemCode);

            if (rs.next()){
                return Integer.parseInt(rs.getString("ProblemID"));
            }

            return -1;
        } catch (Exception ex){
            return -1;
        }
    }

    public Problem getProblem(int problemID){
        try {
            ResultSet rs = this.dbConnection.createStatement().executeQuery("SELCET * FROM problems WHERE " +
                    "ProblemID=" + problemID);

            if (rs.next()){
                Problem p = new Problem();
                p.problemID = rs.getInt("ProblemID");
                p.problemCode = rs.getString("ProblemCode");
                p.problemName = rs.getString("ProblemName");
                p.problemValue = rs.getInt("ProblemValue");

                return p;
            }

            return null;
        } catch (Exception ex){
            return null;
        }
    }

    public ArrayList<Testcase> getTestCases(int problemID){
        try{
            ResultSet rs = this.dbConnection.createStatement().executeQuery("SELECT * FROM testcases " +
                    "WHERE ProblemID=" + problemID);

            ArrayList<Testcase> testcases = new ArrayList<>();

            while(rs.next()){
                Testcase t = new Testcase();
                t.testcaseID = rs.getInt("TestcaseID");
                t.problemID = problemID;
                t.inputURL = rs.getString("Input");
                t.outputURL = rs.getString("Output");
                t.caseValue = rs.getInt("CaseValue");

                testcases.add(t);
            }

            return testcases;
        } catch (Exception ex){
            return null;
        }
    }

    public void addTestcaseResults(int submissionID, int testcaseID, String result){
        try{
            String query = "INSERT INTO judge_results (SubmissionID, TestcaseID, JudgeResult)" +
                    " VALUES (" + submissionID + ", " + testcaseID + ", " + result + ")";
            this.dbConnection.createStatement().executeUpdate(query);
        } catch (Exception ex){
            System.err.println("Error adding testcase results: " + ex.getLocalizedMessage());
        }
    }

    public void setSubmissionStatus(int submissionID, String newStatus){
        try{
            this.dbConnection.createStatement().executeUpdate("UPDATE submissions SET SubmissionStatus='" + newStatus + "'" +
                    " WHERE SubmissionID=" + submissionID);
        } catch (Exception ex){
            System.out.println("Error setting submission status: " + ex.getLocalizedMessage());
        }
    }

    public Submission nextSubmission(){
        try{
            ResultSet rs = this.dbConnection.createStatement().executeQuery("SELECT * FROM submissions WHERE " +
                    "SubmissionStatus='PEND' ORDER BY SubmissionDate ASC");

            if (rs.next()){
                Submission subm = new Submission();
                subm.submissionID = rs.getInt("SubmissionID");
                subm.problemID = rs.getInt("ProblemID");
                subm.userID = rs.getInt("UserID");
                subm.language = rs.getString("Language");
                subm.sourceCode = rs.getString("SourceCode");

                setSubmissionStatus(subm.submissionID, "PROG");

                return subm;
            } else {
                return null;
            }
        } catch (Exception ex){
            return null;
        }
    }
}
