package fa.training.eventbox.service;

import fa.training.eventbox.model.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface EventService {
    List<Event> findAllByDeletedFalseOrderByLastModifiedDateDesc();

    Page<Event> findAllPaging(Pageable pageable);
    Page<Event> findAllPaging(Specification<Event> specification, Pageable pageable);

    void create(Event event);

    void save(Event event);

    boolean existEventName(String name);

    Optional<Event> findById(Long id);

    boolean deleteById(Long id);

    Optional<Event> findByID(Long id);

    public void delete(Event event);



}
