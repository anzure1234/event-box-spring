package fa.training.eventbox.model.entity;

import fa.training.eventbox.model.entity.AbstractAuditingEntity;
import fa.training.eventbox.model.enums.EventStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Event extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column
    private LocalDateTime openToRegistrationDateTime;

    @Column
    private LocalDateTime closeToRegistrationDateTime;

    @Column
    private LocalDateTime startDateTime;

    @Column
    private LocalDateTime endDateTime;

    @Column
    private String place;

    @Column
    private Boolean isPublic;

    @Column
    private Integer capacity;

    @Column
    @Enumerated(EnumType.STRING)
    private EventStatus status;

}

