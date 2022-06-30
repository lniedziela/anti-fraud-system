package antifraud.exception;

public class InvalidRoleException extends RuntimeException{
    public InvalidRoleException() {
        super("Choose among available roles please!");
    }
}
