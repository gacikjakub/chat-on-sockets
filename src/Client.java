import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {


    Socket serverSocket;

    public Client(String name) {
        this.name = name;
    }

    public final String name;

    public void connectToServer(String address, int port) {
        try {
            serverSocket = new Socket(address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        Client client = new Client("NJ");
        client.connectToServer("localhost", 8189);
        new Thread(() -> {
            try {
                handleMyOutputAndSend(client);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                handleServerInput(client);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    static private void handleServerInput(Client client) throws IOException {
        Scanner serverInput = new Scanner(client.serverSocket.getInputStream());
        while (client.serverSocket.isConnected()) {
            if (serverInput.hasNextLine()) {
                System.out.println((serverInput.nextLine()));
            }
        }
    }

    static private void handleMyOutputAndSend(Client client) throws IOException {
        PrintWriter writer = new PrintWriter(client.serverSocket.getOutputStream(), true);
        Scanner scanner = new Scanner(System.in);
        while (client.serverSocket.isConnected()) {
            if(scanner.hasNextLine()) {
                writer.println(client.name + ": " + scanner.nextLine());
            }
        }
    }

}
