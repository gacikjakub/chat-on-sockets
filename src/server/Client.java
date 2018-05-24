package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.function.Supplier;

public class Client {

    public final String name;

    public final Socket clientSocket;

    private PrintWriter writer;

    private Scanner scanner;

    private String lastMessage = "";

    public Client(String name, Socket clientSocket) throws IOException {
        this.name = name;
        this.clientSocket = clientSocket;
        if (clientSocket.isConnected()) {
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            scanner = new Scanner(clientSocket.getInputStream());
        }
    }

    public boolean sendMessage(String message) {
        if (clientSocket.isConnected()) {
            writer.println(message);
            return true;
        }
        return false;
    }

    public String getMessage() {
        if (scanner.hasNextLine()) {
            lastMessage =  scanner.nextLine();
            return lastMessage;
        }
        return "";
    }

    public String getLastMessage() {
        return lastMessage;
    }



    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Client) {
            return (clientSocket == ((Client) obj).clientSocket);
        }
        return false;
    }

    public void close() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
