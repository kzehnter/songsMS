package songsMS;

import com.sun.istack.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "auth", schema = "auth")
public class Auth {
    @Id
    @NotNull
    private String userId;
    @NotNull
    private String password;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;

    public Auth() {
    }

    public Auth(String userId, String password, String firstName, String lastName) {
        this.userId = userId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Auth)) return false;
        Auth auth = (Auth) o;
        return userId.equals(auth.userId) &&
                password.equals(auth.password) &&
                firstName.equals(auth.firstName) &&
                lastName.equals(auth.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, password, firstName, lastName);
    }
}
