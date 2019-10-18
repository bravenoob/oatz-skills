package com.bd.bern.oatz.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.bd.bern.oatz.domain.SkillApplied;
import com.bd.bern.oatz.domain.*; // for static metamodels
import com.bd.bern.oatz.repository.SkillAppliedRepository;
import com.bd.bern.oatz.repository.search.SkillAppliedSearchRepository;
import com.bd.bern.oatz.service.dto.SkillAppliedCriteria;

/**
 * Service for executing complex queries for {@link SkillApplied} entities in the database.
 * The main input is a {@link SkillAppliedCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SkillApplied} or a {@link Page} of {@link SkillApplied} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SkillAppliedQueryService extends QueryService<SkillApplied> {

    private final Logger log = LoggerFactory.getLogger(SkillAppliedQueryService.class);

    private final SkillAppliedRepository skillAppliedRepository;

    private final SkillAppliedSearchRepository skillAppliedSearchRepository;

    public SkillAppliedQueryService(SkillAppliedRepository skillAppliedRepository, SkillAppliedSearchRepository skillAppliedSearchRepository) {
        this.skillAppliedRepository = skillAppliedRepository;
        this.skillAppliedSearchRepository = skillAppliedSearchRepository;
    }

    /**
     * Return a {@link List} of {@link SkillApplied} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SkillApplied> findByCriteria(SkillAppliedCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SkillApplied> specification = createSpecification(criteria);
        return skillAppliedRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link SkillApplied} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SkillApplied> findByCriteria(SkillAppliedCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SkillApplied> specification = createSpecification(criteria);
        return skillAppliedRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SkillAppliedCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SkillApplied> specification = createSpecification(criteria);
        return skillAppliedRepository.count(specification);
    }

    /**
     * Function to convert {@link SkillAppliedCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SkillApplied> createSpecification(SkillAppliedCriteria criteria) {
        Specification<SkillApplied> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), SkillApplied_.id));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUserId(), SkillApplied_.userId));
            }
            if (criteria.getUsedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUsedAt(), SkillApplied_.usedAt));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), SkillApplied_.description));
            }
            if (criteria.getSkillId() != null) {
                specification = specification.and(buildSpecification(criteria.getSkillId(),
                    root -> root.join(SkillApplied_.skill, JoinType.LEFT).get(Skill_.id)));
            }
            if (criteria.getProjectId() != null) {
                specification = specification.and(buildSpecification(criteria.getProjectId(),
                    root -> root.join(SkillApplied_.project, JoinType.LEFT).get(Project_.id)));
            }
        }
        return specification;
    }
}
