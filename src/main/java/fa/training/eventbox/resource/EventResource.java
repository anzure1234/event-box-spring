package fa.training.eventbox.resource;

import fa.training.eventbox.constant.AppConstant;
import fa.training.eventbox.exception.ResourceNotFoundException;
import fa.training.eventbox.model.dto.EventDetailDisplayDto;
import fa.training.eventbox.model.dto.EventFormDto;
import fa.training.eventbox.model.dto.EventListDisplayDto;
import fa.training.eventbox.model.entity.Event;
import fa.training.eventbox.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/events")
public class EventResource {
    private final EventService eventService;

    public EventResource(EventService eventService) {
        this.eventService = eventService;
    }


    @GetMapping // HttpRequest GET /events
    public ResponseEntity<Page<EventListDisplayDto>> showList(
            @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_PAGE_STR) Integer page,
            @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_PAGE_SIZE_STR) Integer size,
            @RequestParam(required = false, name = "sort", defaultValue = AppConstant.DEFAULT_SORT_FIELD) List<String> sorts,
            @RequestParam(required = false, name = "q") Optional<String> keywordOpt) {

        List<Sort.Order> orders = new ArrayList<>();
        for (String sortField : sorts) {
            boolean isDesc = sortField.startsWith("-");
            orders.add(isDesc ? Sort.Order.desc(sortField.substring(1)) : Sort.Order.asc(sortField));
        }

        Specification<Event> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("deleted"), false);
        if (keywordOpt.isPresent()) {
            // where name like '%keyword%' or place like '%keyword%')
            Specification<Event> specByKeyWord = (root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(root.get("name"), "%" + keywordOpt.get() + "%"),
                            criteriaBuilder.like(root.get("place"), "%" + keywordOpt.get() + "%")
                    );
            //where deleted=0 and (name like '%keyword%' or place like '%keyword%')
            spec = spec.and(specByKeyWord);
        }


        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(orders));
        Page<Event> eventPage = eventService.findAllPaging(spec, pageRequest);

        List<EventListDisplayDto> displayDtos = eventPage.getContent().stream()
                .map(event -> {
                    EventListDisplayDto eventListDisplayDto = new EventListDisplayDto();
                    BeanUtils.copyProperties(event, eventListDisplayDto);
                    return eventListDisplayDto;
                }).collect(Collectors.toList());
        //Page<Event> -> Page<EventListDisplayDto>
        Page<EventListDisplayDto> result = new PageImpl<>(displayDtos, pageRequest, eventPage.getTotalElements());
        return ResponseEntity.ok(result);
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}") // "api/events/" + "/id" = "api/events/id"
    public void delete(@PathVariable Long id) {
        Optional<Event> eventOpt = eventService.findById(id);
        Event event = eventOpt.orElseThrow(ResourceNotFoundException::new);
        eventService.delete(event);

    }

    @PostMapping
    public ResponseEntity<EventDetailDisplayDto> create(@RequestBody @Valid EventFormDto eventFormDto) {
        Event event = new Event(); //Convert eventFormDto to Event
        BeanUtils.copyProperties(eventFormDto, event);
        eventService.create(event);
        EventDetailDisplayDto  eventDetailDisplayDto = new EventDetailDisplayDto();
        BeanUtils.copyProperties(event, eventDetailDisplayDto);


        return ResponseEntity.ok(eventDetailDisplayDto);
    }


//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @DeleteMapping("api/events/{id}")
//    public ResponseEntity<> delete1(){
//
//    }

}
