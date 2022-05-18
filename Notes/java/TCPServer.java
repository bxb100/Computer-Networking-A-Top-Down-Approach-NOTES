import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

/**
 * @author John Bi
 */
public class TCPServer {
    public static void main(String[] args) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(12000, 1)) {
            System.out.println("The server is ready to receive");
            while (true) {
                serverSocket
                Socket connectionSocket = serverSocket.accept();
//                the backlog just limit queue number, set 1 you can have 2 clients, so if you
//                want listen 1 connection, you can just close welcome socket when accept one connection socket.
                serverSocket.close();
                byte[] buf = new byte[1024];
                int read = connectionSocket.getInputStream().read(buf);
                String capitalizedSentence = new String(buf, 0, read).toUpperCase(Locale.ROOT);
                PrintWriter printWriter = new PrintWriter(connectionSocket.getOutputStream(), true);
                printWriter.println(capitalizedSentence);
                connectionSocket.close();
            }
        }
    }
}
