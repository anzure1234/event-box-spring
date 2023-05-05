package fa.training.eventbox.model.dto;

import fa.training.eventbox.model.enums.EventStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventListDisplayDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime openToRegistrationDateTime;
    private LocalDateTime closeToRegistrationDateTime;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private String place;

    private Boolean isPublic;

    private Integer capacity;

    private EventStatus status;



}
