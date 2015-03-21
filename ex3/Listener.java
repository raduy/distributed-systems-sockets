import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.time.LocalDateTime;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class Listener implements Runnable {
    private final int port;
    private final String nickName;

    public Listener(int port, String nickName) {
        this.port = port;
        this.nickName = nickName;
    }

    @Override
    public void run() {
        try (MulticastSocket socket = new MulticastSocket(port)) {
            socket.joinGroup(ChatClient.INET_ADDRESS);

            listenForOthersMessages(socket);

        } catch (IOException e) {
            System.out.println("IO Exception!");
            System.out.println(e.getMessage());
        }
    }

    private void listenForOthersMessages(MulticastSocket socket) throws IOException {
        while (!Thread.interrupted()) {
            try {
                final byte[] dataBuffer = new byte[Configuration.MAX_TOTAL_SIZE];
                final DatagramPacket packet = new DatagramPacket(dataBuffer, Configuration.MAX_TOTAL_SIZE);
                socket.receive(packet);

                final Message message = Message.getFromBytes(dataBuffer);
                if (!message.getUsername().equals(nickName)) {
                    System.out.println();
                    System.out.println(message);
                    System.out.printf(Configuration.PROMPT_FORMAT, nickName, LocalDateTime.now());
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}