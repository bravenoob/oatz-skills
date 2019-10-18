package com.bd.bern.oatz.repository;
import com.bd.bern.oatz.domain.Enterprise;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Enterprise entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, Long>, JpaSpecificationExecutor<Enterprise> {

}
