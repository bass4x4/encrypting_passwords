package Backend;

import java.util.Objects;

public class UserContext {
    private final String userName;
    private String password;
    private boolean isBlocked;
    private boolean passwordAllowed;
    private int minimumPasswordLength;

    public UserContext(String userName, String password, boolean isBlocked, boolean passwordAllowed, int minimumPasswordLength) {
        this.userName = userName;
        this.password = password;
        this.isBlocked = isBlocked;
        this.passwordAllowed = passwordAllowed;
        this.minimumPasswordLength = minimumPasswordLength;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        this.isBlocked = blocked;
    }

    public boolean isPasswordLimited() {
        return passwordAllowed;
    }

    public void setPasswordAllowed(boolean passwordAllowed) {
        this.passwordAllowed = passwordAllowed;
    }

    public int getMinimumPasswordLength() {
        return minimumPasswordLength;
    }

    public void setMinimumPasswordLength(int minimumPasswordLength) {
        this.minimumPasswordLength = minimumPasswordLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserContext that = (UserContext) o;
        return isBlocked == that.isBlocked &&
                passwordAllowed == that.passwordAllowed &&
                userName.equals(that.userName) &&
                password.equals(that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, password, isBlocked, passwordAllowed);
    }

    @Override
    public String toString() {
        return userName;
    }
}
