package spring.boot.rest.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.boot.rest.api.service.Updatable;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Updatable<User> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "user")
    private List<Event> events;
    @Enumerated(EnumType.STRING)
    private Status status;

    @Override
    public void updateFrom(User updatedUser) {
        if (!Objects.equals(this.getFirstName(), updatedUser.getFirstName())){
            this.setFirstName(updatedUser.getFirstName());
        }
        if (!Objects.equals(this.getLastName(), updatedUser.getLastName())){
            this.setLastName(updatedUser.getLastName());
        }
        if (!Objects.equals(this.getEmail(), updatedUser.getEmail())){
            this.setEmail(updatedUser.getEmail());
        }
    }
}
