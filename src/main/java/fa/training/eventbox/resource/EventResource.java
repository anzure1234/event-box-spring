package fa.training.eventbox.resource;

import fa.training.eventbox.constant.AppConstant;
import fa.training.eventbox.model.entity.Event;
import fa.training.eventbox.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class EventResource {
    private final EventService eventService;

    public EventResource(EventService eventService) {
        this.eventService = eventService;
    }


    @GetMapping("api/events") // HttpRequest GET /events
    public Page<Event> showList(
                           @RequestParam(required = false,defaultValue = AppConstant.DEFAULT_PAGE_STR) Integer page,
                           @RequestParam(required = false,defaultValue = AppConstant.DEFAULT_PAGE_SIZE_STR) Integer size,
                           @RequestParam(required = false,name="sort",defaultValue = AppConstant.DEFAULT_SORT_FIELD) List<String> sorts,
                           @RequestParam(required=false,name="q") Optional<String> keywordOpt){

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



        PageRequest pageRequest = PageRequest.of(page-1,size,Sort.by(orders));
        Page<Event> eventPage = eventService.findAllPaging(spec,pageRequest);
        return eventPage;
    }
}