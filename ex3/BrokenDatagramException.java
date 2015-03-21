/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class BrokenDatagramException extends RuntimeException {
    public BrokenDatagramException() {
        super("Broken UDP datagram exception");
    }
}
