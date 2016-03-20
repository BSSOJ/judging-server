package server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.util.*;

public class JudgeServer {

    public static Map<String, Object> serverConfig = new HashMap<>();

    public static void loadConfigFromFile(String fileName){
        try{
            Scanner configFile = new Scanner(new File(fileName));
            String configContent = "";

            while(configFile.hasNext()){
                configContent += configFile.nextLine();
            }

            configFile.close();

            JSONObject config = (JSONObject) new JSONParser().parse(configContent);

            serverConfig.put("db_address", config.get("db_address"));
            serverConfig.put("db_port", config.get("db_port"));
            serverConfig.put("db_name", config.get("db_name"));
            serverConfig.put("db_username", config.get("db_username"));
            serverConfig.put("db_password", config.get("db_password"));
            serverConfig.put("fetch_interval", config.get("fetch_interval"));

            for (Map.Entry<String, Object> key : serverConfig.entrySet()){
                System.out.printf("[%s]: %s\n", key.getKey(), key.getValue());
            }

        } catch (Exception ex){
            System.err.println("Error loading config files: " + ex.getLocalizedMessage());
        }
    }

    public static void main(String[] args) {
        loadConfigFromFile("config.json");

        while(true){
            try{

            } catch (Exception ex){

            }
        }
    }
}
