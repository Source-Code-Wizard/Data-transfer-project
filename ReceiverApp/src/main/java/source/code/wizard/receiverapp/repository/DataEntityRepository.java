package source.code.wizard.receiverapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import source.code.wizard.receiverapp.Model.entity.DataEntity;


@Repository
public interface DataEntityRepository extends JpaRepository<DataEntity,Long> {
}
