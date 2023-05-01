package fa.training.eventbox.service.impl;

import fa.training.eventbox.model.entity.Event;
import fa.training.eventbox.repository.EventRepository;
import fa.training.eventbox.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> findAllByDeletedFalseOrderByLastModifiedDateDesc() {
        return eventRepository.findAllByDeletedFalseOrderByLastModifiedDateDesc();
    }

    @Override
    public Page<Event> findAllPaging(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    @Override
    public Page<Event> findAllPaging(Specification<Event> spec, Pageable pageable) {
        return eventRepository.findAll(spec, pageable);
    }


    @Override
    public void create(Event event) {
        event.setCreatedDate(LocalDateTime.now());
        event.setLastModifiedDate(LocalDateTime.now());
        event.setDeleted(false);
        eventRepository.save(event);
    }

    @Override
    public void save(Event event) {
        event.setLastModifiedDate(LocalDateTime.now());
        eventRepository.save(event);
    }

    @Override
    public boolean existEventName(String name) {
        Objects.requireNonNull(name);
        return eventRepository.existsByNameAndDeletedFalse(name);
    }

    @Override
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public boolean deleteById(Long id) {

        if (eventRepository.findById(id).isPresent()) {
            Event event = eventRepository.findById(id).get();
            event.setDeleted(true);
            eventRepository.save(event);
            return true;
        }
        return false;
    }


}
