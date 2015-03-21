import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class Message {
    private final Long timestamp;
    private final String username;
    private final String content;

    public Message(String username, String content) {
        this.username = username;
        this.content = content;
        this.timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

    public Message(String username, String content, long timestamp) {
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public static Message getFromBytes(byte[] dataBuffer) throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(dataBuffer);
        DataInputStream in = new DataInputStream(byteStream);

        byte[] nickNameBuffer = new byte[Configuration.MAX_NICKNAME_SIZE];
        byte[] contentBuffer = new byte[Configuration.MAX_CONTENT_SIZE];

        in.read(nickNameBuffer);
        String username = new String(nickNameBuffer).trim();

        in.read(contentBuffer);
        String line = new String(contentBuffer).trim();

        long timestamp = in.readLong();
        int checksum = in.readInt();

        final Message message = new Message(username, line, timestamp);

        if (message.hashCode() != checksum) {
            System.out.println("Wrong checksum! Message skipped!");
            throw new BrokenDatagramException();
        }

        return message;
    }

    public byte[] getAsBytes() throws IOException {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream(Configuration.MAX_TOTAL_SIZE);
        final DataOutputStream out = new DataOutputStream(byteStream);

        out.writeBytes(trimToSize(username, Configuration.MAX_NICKNAME_SIZE));
        out.writeBytes(trimToSize(content, Configuration.MAX_CONTENT_SIZE));
        out.writeLong(timestamp);
        out.writeInt(hashCode());

        return byteStream.toByteArray();
    }

    private String trimToSize(String content, int length) {
        if (content.length() < length) {
            return fullFillWithPadding(content, length);
        }
        return content.substring(0, length);
    }

    public static String fullFillWithPadding(String content, int expectedSize) {
        return String.format("%" + expectedSize + "s", content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (!content.equals(message.content)) return false;
        if (!timestamp.equals(message.timestamp)) return false;
        if (!username.equals(message.username)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = timestamp.hashCode();
        result = 31 * result + username.hashCode();
        result = 31 * result + content.hashCode();
        return result;
    }

    @Override
    public String toString() {
        LocalDateTime date = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC);
        return String.format(Configuration.PROMPT_FORMAT + "%s", username, date, content);
    }
}
