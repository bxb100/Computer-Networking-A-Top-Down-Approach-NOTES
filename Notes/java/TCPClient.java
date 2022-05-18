import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author John Bi
 */
public class TCPClient {
    public static void main(String[] args) throws IOException {
        InetAddress serverName = InetAddress.getByName("localhost");
        int serverPort = 12000;

        try (Socket socket = new Socket(serverName, serverPort)) {
            System.out.println("Input lowercase sentence:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String sentence = reader.readLine();
            reader.close();

            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.print(sentence);
            printWriter.flush();

            String modifiedSentence = new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine();
            System.out.println("From Server: " + modifiedSentence);
        }
    }
}
