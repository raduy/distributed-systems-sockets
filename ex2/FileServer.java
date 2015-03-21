import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class FileServer {

    private static final int DEFAULT_PORT = 4444;
    private static final int CHUNK_SIZE = 1024;
    private static final String FILE_NAME = "names.txt";
    private static final String PATH_TO_FILE = "./cloud";

    public static void main(String[] args) throws IOException {

        int portNumber;
        if (args.length == 1) {
            portNumber = readPortNumberFromCmd(args);
        } else {
            portNumber = DEFAULT_PORT;
        }

        startListening(portNumber);
    }

    private static void startListening(int portNumber) {
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {

            println("Server listening on port # %d", portNumber);
            while (!Thread.interrupted()) {
                listenForClient(serverSocket);

            }
        } catch (IOException e) {
            println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            println(e.getMessage());
        }
    }

    private static int readPortNumberFromCmd(String[] args) {
        try {
            return Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            println("Invalid port number! Starting on default port...");
            return DEFAULT_PORT;
        }
    }

    private static void listenForClient(ServerSocket serverSocket) {
        try (Socket clientSocket = serverSocket.accept();
             OutputStream outputStream = clientSocket.getOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {

            println("Client connected on socket! %s", clientSocket);

            File file = openFile();
            long length = file.length();
            String fileName = file.getName();

            println("Sending %s file...", fileName);

            println("Sending fileName size which is %d bytes", fileName.length());
            sendFileNameLength(dataOutputStream, fileName);

            println("Sending fileName which is %s", fileName);
            sendFileName(outputStream, fileName);

            println("Sending file length in bytes. It is: %d", length);
            sendFileLength(dataOutputStream, (int) length);

            println("Sending file content...");
            sendFileContent(outputStream, file);

        } catch (Exception e) {
            println("Exception on connection with client. Cause: %s", e);
        }

        println("Waiting for another request...");
    }

    private static void sendFileContent(OutputStream outputStream, File file) throws IOException {
        int sentBytes = 0;

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[CHUNK_SIZE];
            long length = file.length();
            int offset = 0;

            while (sentBytes < length) {
                int read_from_file = fileInputStream.read(buffer);
                outputStream.write(buffer, offset, read_from_file);
                sentBytes += read_from_file;
            }
        }
    }

    private static void sendFileLength(DataOutputStream out, int length) throws IOException {
        out.writeInt(length);
    }

    private static void sendFileName(OutputStream outputStream, String fileName) throws IOException {
        outputStream.write(fileName.getBytes());
    }

    private static void sendFileNameLength(DataOutputStream out, String fileName) throws IOException {
        int fileNameLength = fileName.getBytes().length;
        out.writeInt(fileNameLength);
    }

    private static File openFile() {
        Path path = Paths.get(PATH_TO_FILE, FILE_NAME);

        return path.toFile();
    }

    private static void println(String toPrint, Object... args) {
//        Replace with logger if needed
        System.out.println(String.format(toPrint, args));
    }
}