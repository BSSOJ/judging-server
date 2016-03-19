package server;

import data.Problem;
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

    public ArrayList<Testcase> getTestCases(int problemID){
        try{
            ResultSet rs = this.dbConnection.createStatement().executeQuery("SELECT * FROM testcases " +
                    "WHERE ProblemID=" + problemID);

            ArrayList<Testcase> testcases = new ArrayList<>();

            while(rs.next()){
                Testcase t = new Testcase();
                t.testcaseID = Integer.parseInt(rs.getString("TestcaseID"));
                t.problemID = problemID;
                t.inputURL = rs.getString("Input");
                t.outputURL = rs.getString("Output");
                t.caseValue = Integer.parseInt(rs.getString("CaseValue"));

                testcases.add(t);
            }

            return testcases;
        } catch (Exception ex){
            return null;
        }
    }


}
