import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author John Bi
 */
public class UDPClient {
    public static void main(String[] args) throws IOException {
        InetAddress inetAddress = Inet4Address.getByName("localhost");
        int serverPort = 12000;

        try (DatagramSocket client = new DatagramSocket()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Input lowercase sentence");
            String message = scanner.nextLine();
            scanner.close();

            byte[] buf = message.getBytes(StandardCharsets.UTF_8);

            client.send(new DatagramPacket(buf, buf.length, inetAddress, serverPort));

            byte[] buf2 = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buf2, 2048);
            client.receive(packet);
            String modifiedMessage = new String(packet.getData(), 0, packet.getLength());
            System.out.println(modifiedMessage);
        }
    }
}
