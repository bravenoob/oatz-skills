package com.bd.bern.oatz.repository;
import com.bd.bern.oatz.domain.SkillApplied;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the SkillApplied entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SkillAppliedRepository extends JpaRepository<SkillApplied, Long>, JpaSpecificationExecutor<SkillApplied> {

}
