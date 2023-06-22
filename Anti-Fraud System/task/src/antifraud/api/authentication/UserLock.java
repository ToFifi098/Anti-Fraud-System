package antifraud.api.authentication;

public class UserLock {
    private String username;
    private String operation;

    public UserLock() {
    }

    public UserLock(String username, String operation) {
        this.username = username;
        this.operation = operation;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
