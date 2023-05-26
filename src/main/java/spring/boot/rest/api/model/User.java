package spring.boot.rest.api.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static spring.boot.rest.api.util.Constants.*;

@Entity
@Table(name = USERS)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    @Column(name = FIRST_NAME, nullable = false)
    private String firstName;
    @Column(name = LAST_NAME, nullable = false)
    private String lastName;
    @Column(name = EMAIL, unique = true, nullable = false)
    private String email;
    @Column(name = PASSWORD, nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = ROLE, nullable = false)
    private Role role;
    @OneToMany(mappedBy = USER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<Token> tokens;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = USER)
    private List<Event> events;
}
