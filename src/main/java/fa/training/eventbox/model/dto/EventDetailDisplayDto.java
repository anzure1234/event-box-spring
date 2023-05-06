package fa.training.eventbox.model.dto;

import fa.training.eventbox.model.enums.EventStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventDetailDisplayDto {
    private Long id;
    private String name;
    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;
    private EventStatus status;
}
