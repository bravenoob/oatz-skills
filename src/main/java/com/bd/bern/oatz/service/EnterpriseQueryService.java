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

import com.bd.bern.oatz.domain.Enterprise;
import com.bd.bern.oatz.domain.*; // for static metamodels
import com.bd.bern.oatz.repository.EnterpriseRepository;
import com.bd.bern.oatz.repository.search.EnterpriseSearchRepository;
import com.bd.bern.oatz.service.dto.EnterpriseCriteria;

/**
 * Service for executing complex queries for {@link Enterprise} entities in the database.
 * The main input is a {@link EnterpriseCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Enterprise} or a {@link Page} of {@link Enterprise} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EnterpriseQueryService extends QueryService<Enterprise> {

    private final Logger log = LoggerFactory.getLogger(EnterpriseQueryService.class);

    private final EnterpriseRepository enterpriseRepository;

    private final EnterpriseSearchRepository enterpriseSearchRepository;

    public EnterpriseQueryService(EnterpriseRepository enterpriseRepository, EnterpriseSearchRepository enterpriseSearchRepository) {
        this.enterpriseRepository = enterpriseRepository;
        this.enterpriseSearchRepository = enterpriseSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Enterprise} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Enterprise> findByCriteria(EnterpriseCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Enterprise> specification = createSpecification(criteria);
        return enterpriseRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Enterprise} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Enterprise> findByCriteria(EnterpriseCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Enterprise> specification = createSpecification(criteria);
        return enterpriseRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EnterpriseCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Enterprise> specification = createSpecification(criteria);
        return enterpriseRepository.count(specification);
    }

    /**
     * Function to convert {@link EnterpriseCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Enterprise> createSpecification(EnterpriseCriteria criteria) {
        Specification<Enterprise> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Enterprise_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Enterprise_.title));
            }
            if (criteria.getProjectsId() != null) {
                specification = specification.and(buildSpecification(criteria.getProjectsId(),
                    root -> root.join(Enterprise_.projects, JoinType.LEFT).get(Project_.id)));
            }
        }
        return specification;
    }
}
