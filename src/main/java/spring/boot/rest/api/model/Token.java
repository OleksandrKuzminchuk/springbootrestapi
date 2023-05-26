package spring.boot.rest.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static spring.boot.rest.api.util.Constants.*;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TOKENS)
@EqualsAndHashCode(callSuper = true)
public class Token extends BaseEntity {
    @Column(name = TOKEN)
    private String nameToken;
    @Enumerated(EnumType.STRING)
    @Column(name = TOKEN_TYPE)
    private TokenType tokenType;
    @Column(name = EXPIRED)
    private boolean expired;
    @Column(name = REVOKED)
    private boolean revoked;
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = USER_ID)
    private User user;
}
