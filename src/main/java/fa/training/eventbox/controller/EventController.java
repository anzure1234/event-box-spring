package fa.training.eventbox.controller;

import fa.training.eventbox.constant.AppConstant;
import fa.training.eventbox.model.dto.EventFormDto;
import fa.training.eventbox.model.entity.Event;
import fa.training.eventbox.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }


    @GetMapping("/events") // HttpRequest GET /events
    public String showList(Model model,
                           @RequestParam(required = false,defaultValue = AppConstant.DEFAULT_PAGE_STR) Integer page,
                           @RequestParam(required = false,defaultValue = AppConstant.DEFAULT_PAGE_SIZE_STR) Integer size,
                            @RequestParam(required = false,name="sort",defaultValue = AppConstant.DEFAULT_SORT_FIELD) List<String> sorts,
                           @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_SORT_DIRECTION) String direction,
                           @RequestParam(required=false,name="q")Optional<String> keywordOpt){

        List<Sort.Order> orders = new ArrayList<>();
        for (String sortField : sorts){
            boolean isDesc = sortField.startsWith("-");
            orders.add(isDesc ? Sort.Order.desc(sortField.substring(1)):Sort.Order.asc(sortField));
        }

        Specification<Event> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("deleted"), false);
        if(keywordOpt.isPresent()){
            // where name like '%keyword%' or place like '%keyword%')
            Specification<Event> specByKeyWord=(root,query,criteriaBuilder)->
                    criteriaBuilder.or(
                            criteriaBuilder.like(root.get("name"),"%"+keywordOpt.get()+"%"),
                            criteriaBuilder.like(root.get("place"),"%"+keywordOpt.get()+"%")
                    );
            //where deleted=0 and (name like '%keyword%' or place like '%keyword%')
            spec=spec.and(specByKeyWord);
        }


        Sort sorting = Sort.by(orders);
        if (direction.equalsIgnoreCase("desc")) {
            sorting = sorting.descending();
        } else {
            sorting = sorting.ascending();
        }
        PageRequest pageRequest = PageRequest.of(page - 1, size, sorting);

        Page<Event> eventPage = eventService.findAllPaging(spec,pageRequest);
        model.addAttribute("eventPage", eventPage);
        model.addAttribute("direction", direction);
        return "events/list"; // forward to view
    }

    @GetMapping("/events/detail") // HttpRequest GET /events/detail
    public String showDetail() {
        return null;
    }

    @GetMapping("/events/create") // HttpRequest GET /events/add
    public String showCreate(Model model) {
        model.addAttribute("eventFormDto", new EventFormDto());
        return "events/form";
    }


    @PostMapping("/events/create") // HttpRequest POST /events/add
    public String create(@Valid EventFormDto eventFormDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "events/form";
        }
        if (eventService.existEventName(eventFormDto.getName())) {
            bindingResult.rejectValue("name", "event.name.duplicate");
        }
        if (eventFormDto.getStartDateTime().isAfter(eventFormDto.getEndDateTime())) {
            bindingResult.rejectValue("startDateTime", "event.startDateTime.before.endDateTime");
        }
        if (bindingResult.hasErrors()) {
            return "events/form";
        }

        Event event = new Event(); //Convert eventFormDto to Event
        BeanUtils.copyProperties(eventFormDto, event);

        eventService.create(event);

        redirectAttributes.addFlashAttribute("successMessage", "event.create.success");

        return "redirect:/events";
    }

//    @GetMapping("/events/update/")
//    public String showUpdate(Model model, @RequestParam) Long id){
//            System.out.println(id);
//            return "events/form";
//        }

    @GetMapping("/events/update/{id}")
    public String showUpdate(Model model, @PathVariable(name = "id") Long eventId) {
        Optional<Event> eventOptional = eventService.findById(eventId);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            EventFormDto eventFormDto = new EventFormDto();
            BeanUtils.copyProperties(event, eventFormDto);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            eventFormDto.setStartDateTimeFormatted(event.getStartDateTime().format(formatter));
            eventFormDto.setEndDateTimeFormatted(event.getEndDateTime().format(formatter));

            model.addAttribute("eventFormDto", eventFormDto);

            return "events/form";
        }
        return "redirect:/events";
    }

    @PostMapping("/events/update/{id}")
    public String update(@PathVariable(name="id") Long eventId, @Valid EventFormDto eventFormDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (eventFormDto.getStartDateTime().isAfter(eventFormDto.getEndDateTime())) {
            bindingResult.rejectValue("startDateTime", "event.startDateTime.before.endDateTime");
        }
        if (bindingResult.hasErrors()) {
            return "events/form";
        }

        Optional<Event> eventOptional = eventService.findById(eventId);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            BeanUtils.copyProperties(eventFormDto, event);
            eventService.save(event);
            redirectAttributes.addFlashAttribute("successMessage", "event.update.success");
        }
        return "redirect:/events";
    }

    @PostMapping("/events/delete/{id}")
    public String delete(@PathVariable(name="id") Long eventId, RedirectAttributes redirectAttributes) {
        boolean isDeleted = eventService.deleteById(eventId);
        if (isDeleted) {
            redirectAttributes.addFlashAttribute("successMessage", "event.delete.success");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "event.delete.error");
        }
        return "redirect:/events";
    }
}


