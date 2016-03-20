package server;

import java.io.BufferedReader;
import java.io.IOException;

public class Console extends Thread{

    private BufferedReader reader;
    private String threadName;

    public Console(BufferedReader r, String s){
        this.reader = r;
        this.threadName = s;
    }

    public void run(){
        while(true){
            String line = null;
            try {
                line = reader.readLine();
                if (line != null){
                    System.out.printf("[%s]: %s\n", line);
                }
            } catch (IOException e) {
            }

        }
    }
}
