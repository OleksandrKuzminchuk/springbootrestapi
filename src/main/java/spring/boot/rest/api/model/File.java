
package spring.boot.rest.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "s3_secret", unique = true, nullable = false)
    private String s3Secret;
    @Column(name = "s3_bucket", nullable = false)
    private String s3Bucket;
    @Column(name = "location", nullable = false)
    private String location;
}

