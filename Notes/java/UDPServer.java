import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * @author John Bi
 */
public class UDPServer {
    public static void main(String[] args) throws Exception {

        int serverPort = 12000;

        try (DatagramSocket server = new DatagramSocket(serverPort)) {
            System.out.println("The server is ready to receive");
            while (true) {
                byte[] buf = new byte[2048];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                server.receive(packet);

                String modifiedMessage = new String(packet.getData(), 0, packet.getLength()).toUpperCase(Locale.ROOT);
                byte[] bytes = modifiedMessage.getBytes(StandardCharsets.UTF_8);
                server.send(new DatagramPacket(bytes, bytes.length, packet.getSocketAddress()));
            }
        }
    }
}
