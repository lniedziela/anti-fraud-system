package antifraud.exception;

public class TheSameRoleException extends RuntimeException{
    public TheSameRoleException() {
        super("Role already assigned to the given user, choose another role please!");
    }
}
