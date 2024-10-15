package source.code.wizzard.senderapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import source.code.wizzard.senderapp.model.DataEntity;

@Repository
public interface DataEntityRepository extends JpaRepository<DataEntity, Long> {
}
