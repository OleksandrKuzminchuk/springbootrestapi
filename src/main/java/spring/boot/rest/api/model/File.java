
package spring.boot.rest.api.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static spring.boot.rest.api.util.Constants.*;

@Entity
@Table(name = FILES)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class File extends BaseEntity {
    @Column(name = NAME, nullable = false)
    private String name;
    @Column(name = S3_SECRET, unique = true, nullable = false)
    private String s3Secret;
    @Column(name = S3_BUCKET, nullable = false)
    private String s3Bucket;
    @Column(name = LOCATION, nullable = false)
    private String location;
}

