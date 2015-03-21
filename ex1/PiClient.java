import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class PiClient {
    private static final String DEFAULT_HOSTNAME = "127.0.0.1";
    private static final int DEFAULT_PORT = 4444;

    public static void main(String[] args) {

        String hostName = DEFAULT_HOSTNAME;
        int portNumber = DEFAULT_PORT;

        if (args.length == 2) {
            hostName = args[0];
            portNumber = Integer.parseInt(args[1]);
        }

        try (Socket socket = new Socket(hostName, portNumber);
             OutputStream outputStream = socket.getOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

             InputStream inputStream = socket.getInputStream();
             DataInputStream in =
                     new DataInputStream(inputStream)
        ) {
            Scanner scanner = new Scanner(System.in);

            System.out.printf("Type some number:\n");
            while (scanner.hasNext()) {
                long askedDigit = scanner.nextLong();
                System.out.printf("Asking for %d th digit of PI\n", askedDigit);

                sendRequest(askedDigit, dataOutputStream);
                byte serverResponse = in.readByte();

                System.out.printf("Server claims that it is: %d\n", serverResponse);
                System.out.printf("Next number?\n");
            }
        } catch (UnknownHostException e) {
            System.out.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        } catch (Exception e) {
            System.out.println("ERROROR" + e);
            System.exit(1);
        }
    }

    private static void sendRequest(long askedDigit, DataOutputStream out) throws IOException {
        byte sizePart = prepareSizePart(askedDigit);
        out.writeByte(sizePart);

        switch (sizePart) {
            case 1:
                out.writeByte((int) askedDigit);
                break;
            case 2:
                out.writeShort((int) askedDigit);
                break;
            case 4:
                out.writeInt((int) askedDigit);
                break;
            case 8:
                out.writeLong(askedDigit);
                break;
            default:
                throw new IllegalArgumentException("Unsupported data type");
        }
    }

    private static byte prepareSizePart(long i) {
        byte sizePart;
        if (i <= 127) {
            sizePart = (byte) 1;
        } else if (i <= 32767) {
            sizePart = (byte) 2;
        } else if (i <= Math.pow(2, 31) - 1) {
            sizePart = (byte) 4;
        } else {
            sizePart = (byte) 8;
        }
        System.out.printf("Your var is %d byte in size\n", sizePart);
        return sizePart;
    }

}
