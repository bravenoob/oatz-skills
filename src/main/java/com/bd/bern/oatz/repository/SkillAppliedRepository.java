package com.bd.bern.oatz.repository;
import com.bd.bern.oatz.domain.SkillApplied;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the SkillApplied entity.
 */
@Repository
public interface SkillAppliedRepository extends JpaRepository<SkillApplied, Long>, JpaSpecificationExecutor<SkillApplied> {

    @Query(value = "select distinct skillApplied from SkillApplied skillApplied left join fetch skillApplied.skills",
        countQuery = "select count(distinct skillApplied) from SkillApplied skillApplied")
    Page<SkillApplied> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct skillApplied from SkillApplied skillApplied left join fetch skillApplied.skills")
    List<SkillApplied> findAllWithEagerRelationships();

    @Query("select skillApplied from SkillApplied skillApplied left join fetch skillApplied.skills where skillApplied.id =:id")
    Optional<SkillApplied> findOneWithEagerRelationships(@Param("id") Long id);

}
