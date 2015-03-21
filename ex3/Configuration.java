/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class Configuration {
    public static final String DEFAULT_ADDRESS = "230.0.0.1";
    public static final int DEFAULT_PORT = 7777;

    public static final int MAX_NICKNAME_SIZE = 6;
    public static final int MAX_CONTENT_SIZE = 20;
    public static final int TIMESTAMP_SIZE = 8;
    public static final int CHECK_SUM_SIZE = 4;
    public static final int MAX_TOTAL_SIZE =
            MAX_CONTENT_SIZE + MAX_NICKNAME_SIZE + TIMESTAMP_SIZE + CHECK_SUM_SIZE;

    public static final String PROMPT_FORMAT = "(%-6s %-23s) >";

}
