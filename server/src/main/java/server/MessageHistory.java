package server;

import java.io.*;

public class MessageHistory {
   private static File file;

    public static void addFile(String login){
       try {
           file = new File("historyMsg/history_" + login + ".txt");
           file.createNewFile();
       } catch (IOException e) {
           e.printStackTrace();
       }

   }

    public static  void addMsgInHistory(String login, String msg) {
        try{
            FileOutputStream fos = new FileOutputStream("historyMsg/history_" + login + ".txt",true);
            msg = msg + "\n";
            fos.write(msg.getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }
   }


