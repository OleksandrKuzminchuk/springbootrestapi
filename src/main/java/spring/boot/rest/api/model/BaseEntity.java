package spring.boot.rest.api.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

import static spring.boot.rest.api.util.Constants.*;

@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = CREATED_AT, nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;
    @Column(name = UPDATED_AT, nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Column(name = STATUS, nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
}
