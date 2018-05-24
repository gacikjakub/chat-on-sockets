import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Test {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8189);   // open given socket on this machine
        Socket client = serverSocket.accept();   // wait for client - have to connect to serve i.e by telnet

        OutputStream outputStream = client.getOutputStream();            // get outputstream on client machine
        PrintWriter writer = new PrintWriter(outputStream,true);             // for more comfortable usage
        writer.println("Hi dude");
        /*
        writer.flush();             // it require to send it to cline    // but you can also set wrtiter on autoflush in constructorr
        */

        Scanner scanner = new Scanner(client.getInputStream());
        while(true) {
            if(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(line);         // show on server what client wrote
                if ("BYE".equalsIgnoreCase(line)) {
                    break;
                }
            }
        }

    }
}
