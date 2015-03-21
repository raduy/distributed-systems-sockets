import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class Sender implements Runnable {
    private final int port;
    private final String username;

    public Sender(int port, String username) {
        this.port = port;
        this.username = username;
    }

    @Override
    public void run() {
        try (MulticastSocket socket = new MulticastSocket(port)) {
            socket.joinGroup(ChatClient.INET_ADDRESS);

            while (!Thread.interrupted()) {
                sendUserMessage(socket);
            }

        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("Could not open connection");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("Could not resolve host");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not send message");
        }
    }

    private void sendUserMessage(MulticastSocket socket) throws IOException {
        System.out.printf(Configuration.PROMPT_FORMAT, username, LocalDateTime.now());

        Scanner scanner = new Scanner(System.in);
        final String content = scanner.nextLine();

        if (content.isEmpty()) {
            return;
        }

        final Message message = new Message(username, content);
        byte[] bytes = message.getAsBytes();
        final DatagramPacket packet
                = new DatagramPacket(bytes, bytes.length, ChatClient.INET_ADDRESS, port);
        socket.send(packet);
    }
}
