package source.code.wizzard.receiverapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import source.code.wizzard.receiverapp.Model.entity.DataEntity;


@Repository
public interface DataEntityRepository extends JpaRepository<DataEntity,Long> {
}
