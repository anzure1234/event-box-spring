package fa.training.eventbox.repository;

import fa.training.eventbox.model.entity.Customer;
import fa.training.eventbox.model.entity.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends BaseRepository<Customer,Long> {


}
