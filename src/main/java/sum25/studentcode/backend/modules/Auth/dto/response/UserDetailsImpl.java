

import sum25.studentcode.backend.model.User; // Import your User Entity
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// Lombok annotations are often included here, but we will write the methods explicitly for clarity.
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String password; // Stored hash for security checks
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Constructor for creating a UserDetailsImpl object from a User Entity.
     * @param user The User Entity fetched from the database.
     */
    public UserDetailsImpl(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.password = user.getPassword();

        // Map the Role enum to Spring Security's GrantedAuthority
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
        this.authorities = Collections.singletonList(authority);
    }

    // --- Custom Getters ---

    // IMPORTANT: Provide a getter for the custom primary key (userId)
    public Long getUserId() {
        return userId;
    }

    // --- Implementation of UserDetails Interface ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // You can customize these based on your business logic (e.g., locking accounts)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Assuming your User entity doesn't have an 'isEnabled' field,
        // we default to true, or you could check a status field if one exists.
        return true;
    }

    // Optional: Overriding equals and hashCode is good practice
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl that = (UserDetailsImpl) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}