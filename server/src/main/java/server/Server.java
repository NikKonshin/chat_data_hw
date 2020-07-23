package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.List;
import java.util.Vector;



public class Server {
    private List<ClientHandler> clients;
    private AuthService authService;




    public AuthService getAuthService() {
        return authService;
    }

    public Server() {

        clients = new Vector<>();
//        authService = new SimpleAuthService();
        if (!SQLHandler.connect()){
            throw new RuntimeException("Не удалось подключиться к БД");
        }
        authService = new DBAuthService();

        ServerSocket server = null;
        Socket socket;

        final int PORT = 8189;

        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен!");
            //fillTable();

            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");
                System.out.println("socket.getRemoteSocketAddress(): " + socket.getRemoteSocketAddress());
                System.out.println("socket.getLocalSocketAddress() " + socket.getLocalSocketAddress());
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void broadcastMsg(ClientHandler sender, String msg) {
        String message = String.format("%s : %s", sender.getNick(), msg);
        MessageHistory.addMsgInHistory(sender.getNick(),message);
        for (ClientHandler client : clients) {
            client.sendMsg(message);
        }
    }

    void privateMsg(ClientHandler sender, String receiver, String msg) {
        String message = String.format("[%s] private [%s] : %s", sender.getNick(), receiver, msg);

        for (ClientHandler c : clients) {
            if(c.getNick().equals(receiver)){
                c.sendMsg(message);
                MessageHistory.addMsgInHistory(c.getLogin(),message);

                if (!sender.getNick().equals(receiver)) {
                    sender.sendMsg(message);
                    MessageHistory.addMsgInHistory(sender.getLogin(),message);
                }

                return;
            }
        }
        sender.sendMsg(String.format("Client %s not found", receiver));
    }


    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientList();
    }

    public boolean isLoginAuthorized(String login){
        for (ClientHandler c : clients) {
            if(c.getLogin().equals(login)){
                return true;
            }
        }
        return false;
    }

    void broadcastClientList() {
        StringBuilder sb = new StringBuilder("/clientlist ");

        for (ClientHandler c : clients) {
            sb.append(c.getNick()).append(" ");
        }

        String msg = sb.toString();

        for (ClientHandler c : clients) {
            c.sendMsg(msg);
        }
    }



}
