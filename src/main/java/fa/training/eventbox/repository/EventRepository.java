package fa.training.eventbox.repository;

import fa.training.eventbox.model.entity.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends BaseRepository<Event,Long> { //EventDao
    boolean existsByNameAndDeletedFalse(String name);
    // Select 1 from event where name = ? and deleted = 0

    @Query(value ="select e from Event e where (e.isPublic=true and e.capacity >= :capacity) or (e.isPublic=false and e.capacity < :capacity)")
    List<Event> findAllEvent(@Param("capacity") Integer capacity);

}
