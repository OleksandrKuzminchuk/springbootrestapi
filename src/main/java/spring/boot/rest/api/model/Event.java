package spring.boot.rest.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import spring.boot.rest.api.service.Updatable;

import java.util.Objects;

@Entity
@Table(name = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event extends BaseEntity implements Updatable<Event> {
    @Column(name = "name", unique = true, nullable = false)
    private String name;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    @Override
    public void updateFrom(Event updatedEvent) {
        if (!Objects.equals(this.getName(), updatedEvent.getName())) {
            this.setName(updatedEvent.getName());
        }
        if (!Objects.equals(this.getUser(), updatedEvent.getUser())) {
            this.setUser(updatedEvent.getUser());
        }
        if (!Objects.equals(this.getFile(), updatedEvent.getFile())) {
            this.setFile(updatedEvent.getFile());
        }
    }
}
