import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChatClient {
    public static InetAddress INET_ADDRESS;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final String name;

    public ChatClient(String name) {
        this.name = name;
    }

    public static void main(String[] args) {

        final String name = loadNickName(args);
        try {
            INET_ADDRESS = InetAddress.getByName(Configuration.DEFAULT_ADDRESS);
        } catch (UnknownHostException e) {
            System.out.printf("Unknown Host Exception! %s", e.getMessage());
        }

        new ChatClient(name).joinChat();
    }

    private static String loadNickName(String[] args) {
        String name;
        final Scanner scanner = new Scanner(System.in);
        if (args.length > 0) {
            name = args[0];
        } else {
            System.out.println("What's your name?");
            name = scanner.nextLine();
        }

        while (name.length() > 6) {
            System.out.println("Your username is too long!");
            System.out.printf("Put something shorter (MAX %d chars)%n", Configuration.MAX_NICKNAME_SIZE);
            name = scanner.nextLine();
        }
        return name;
    }

    private void joinChat() {
        executorService.submit(new Listener(Configuration.DEFAULT_PORT, name));
        executorService.submit(new Sender(Configuration.DEFAULT_PORT, name));
    }

}
