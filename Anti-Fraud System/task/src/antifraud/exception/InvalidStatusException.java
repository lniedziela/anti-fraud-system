package antifraud.exception;

public class InvalidStatusException extends RuntimeException{
    public InvalidStatusException() {
        super("Choose among available statuses ('LOCK'/'UNLOCK') please!");
    }
}
