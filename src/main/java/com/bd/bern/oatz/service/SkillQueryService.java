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

import com.bd.bern.oatz.domain.Skill;
import com.bd.bern.oatz.domain.*; // for static metamodels
import com.bd.bern.oatz.repository.SkillRepository;
import com.bd.bern.oatz.repository.search.SkillSearchRepository;
import com.bd.bern.oatz.service.dto.SkillCriteria;

/**
 * Service for executing complex queries for {@link Skill} entities in the database.
 * The main input is a {@link SkillCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Skill} or a {@link Page} of {@link Skill} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SkillQueryService extends QueryService<Skill> {

    private final Logger log = LoggerFactory.getLogger(SkillQueryService.class);

    private final SkillRepository skillRepository;

    private final SkillSearchRepository skillSearchRepository;

    public SkillQueryService(SkillRepository skillRepository, SkillSearchRepository skillSearchRepository) {
        this.skillRepository = skillRepository;
        this.skillSearchRepository = skillSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Skill} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Skill> findByCriteria(SkillCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Skill> specification = createSpecification(criteria);
        return skillRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Skill} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Skill> findByCriteria(SkillCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Skill> specification = createSpecification(criteria);
        return skillRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SkillCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Skill> specification = createSpecification(criteria);
        return skillRepository.count(specification);
    }

    /**
     * Function to convert {@link SkillCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Skill> createSpecification(SkillCriteria criteria) {
        Specification<Skill> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Skill_.id));
            }
            if (criteria.getSkillName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSkillName(), Skill_.skillName));
            }
        }
        return specification;
    }
}
