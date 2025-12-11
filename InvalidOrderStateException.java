package exp;

public class InvalidOrderStateException extends RuntimeException {
    public InvalidOrderStateException(String msg) {
        super(msg);
    }
}