package fa.training.eventbox.repository;

import fa.training.eventbox.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T,ID> extends CrudRepository<T,ID>,
        PagingAndSortingRepository<T,ID>,
        JpaSpecificationExecutor<T> {
    Optional<T> findByIdAndDeletedFalse(ID id);

    List<T> findAllByDeletedFalseOrderByLastModifiedDateDesc();
    List<T> findAllByDeletedFalse();
}
