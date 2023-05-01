package fa.training.eventbox.model.dto;

import fa.training.eventbox.model.enums.EventStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventFormDto {


    private Long id;

    @NotBlank(message = "{common.error.required}")
    private String name;
    private String description;
    private EventStatus status;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer capacity;
    private String place;
    private String startDateTimeFormatted;
    private String endDateTimeFormatted;

}
