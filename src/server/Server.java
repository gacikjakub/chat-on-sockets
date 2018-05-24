package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

    public ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
    }

    private int port;

    private Set<Client> clients = new HashSet<>();

    public static void main(String[] args) throws IOException {
        int port = 8189;
        if (args.length == 0) {
            System.out.println("Run on default 8189");
        }
        try {
            port = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {
            System.out.println("Run on default 8189");
        }
        Server server = new Server(port);
        server.start();
        System.out.println("Server is up:");
        while(true) {
            Socket clientSocket = server.serverSocket.accept();
            Client client = server.registerClient(clientSocket);
            server.startClientInputHandling(client);
        }
    }


    private Client registerClient(Socket clientSocket) {
        Client result = null;
        try {
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            writer.println("Welcome on chat server");
            writer.println();
            boolean isInvalid = false;
            String name = "";
            do {
                isInvalid = false;
                writer.println("Give you name and press ENTER: ");
                Scanner scanner = new Scanner(clientSocket.getInputStream());
                if (scanner.hasNextLine()) {
                    name = scanner.nextLine();
                }
                Pattern noEmptyPattern = Pattern.compile("[\\s]*");
                Matcher matcher = noEmptyPattern.matcher(name);
                if (matcher.matches()) {
                    writer.println("Name cannot be empty !!!");
                    isInvalid = true;
                }
            } while (isInvalid);
            writer.println("Hello " + name);
            writer.println();
            writer.println("Write your message:");
            result = new Client(name,clientSocket);
            clients.add(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void start() throws IOException {
        serverSocket = new ServerSocket(port);
    }

    private boolean commandsHandling(Client client) throws IOException {
        String lastMessage = client.getLastMessage();
        switch (lastMessage.toLowerCase()) {
            case "/exit" :
                clients.remove(client);
                client.close();
                throw new IOException("Session with client finished");
            case "/whoami" :
                client.sendMessage(client.name);
                return true;
            default:
                return false;
        }
    }

    private void shareWithEveryone(Client client) {
        clients.stream().filter(c -> c!=client).forEach(c -> {
            c.sendMessage(client.name + ": " + client.getLastMessage());
        });
    }

    private void startClientInputHandling(Client client) {
        new Thread(() -> {
        try {
            while(true) {
               String message = client.getMessage();
                System.out.println(client.name + ": " + message);
                if (!commandsHandling(client)) {
                    shareWithEveryone(client);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        }).start();
    }

}