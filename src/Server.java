import java.io.IOException;
import java.io.PrintWriter;
import java.io.WriteAbortedException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Server {

    public ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
    }

    private int port;

    static private List<Socket> clients = new LinkedList<>();


    public static void main(String[] args) throws IOException {
        Server server = new Server(8189);
        server.start();
        while(true) {
            Socket client = server.serverSocket.accept();

            clients.add(client);
            new Thread(() -> handleClientInput(client)).start();
            
        }
    }

    private void start() throws IOException {
        serverSocket = new ServerSocket(port);
    }

    static private void handleClientInput(Socket clientSocket) {
        try {
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            writer.println("Welcome on chat server");
            Scanner scanner = new Scanner(clientSocket.getInputStream());
            while(true) {
                if(scanner.hasNextLine()) {
                    String message = scanner.nextLine();
                    System.out.println(message);
                    if ("exit".equalsIgnoreCase(message)) {
                        clients.remove(clientSocket);
                        try {
                            clientSocket.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    clients.stream().filter(client -> client!=clientSocket).forEach(client -> {
                        try {
                            new PrintWriter(client.getOutputStream(), true).println(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}