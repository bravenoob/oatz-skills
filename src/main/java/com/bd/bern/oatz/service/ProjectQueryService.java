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

import com.bd.bern.oatz.domain.Project;
import com.bd.bern.oatz.domain.*; // for static metamodels
import com.bd.bern.oatz.repository.ProjectRepository;
import com.bd.bern.oatz.repository.search.ProjectSearchRepository;
import com.bd.bern.oatz.service.dto.ProjectCriteria;

/**
 * Service for executing complex queries for {@link Project} entities in the database.
 * The main input is a {@link ProjectCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Project} or a {@link Page} of {@link Project} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProjectQueryService extends QueryService<Project> {

    private final Logger log = LoggerFactory.getLogger(ProjectQueryService.class);

    private final ProjectRepository projectRepository;

    private final ProjectSearchRepository projectSearchRepository;

    public ProjectQueryService(ProjectRepository projectRepository, ProjectSearchRepository projectSearchRepository) {
        this.projectRepository = projectRepository;
        this.projectSearchRepository = projectSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Project} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Project> findByCriteria(ProjectCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Project> specification = createSpecification(criteria);
        return projectRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Project} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Project> findByCriteria(ProjectCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Project> specification = createSpecification(criteria);
        return projectRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProjectCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Project> specification = createSpecification(criteria);
        return projectRepository.count(specification);
    }

    /**
     * Function to convert {@link ProjectCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Project> createSpecification(ProjectCriteria criteria) {
        Specification<Project> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Project_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Project_.title));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Project_.description));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), Project_.type));
            }
            if (criteria.getEnterpriseId() != null) {
                specification = specification.and(buildSpecification(criteria.getEnterpriseId(),
                    root -> root.join(Project_.enterprise, JoinType.LEFT).get(Enterprise_.id)));
            }
            if (criteria.getAppliedSkillsId() != null) {
                specification = specification.and(buildSpecification(criteria.getAppliedSkillsId(),
                    root -> root.join(Project_.appliedSkills, JoinType.LEFT).get(SkillApplied_.id)));
            }
        }
        return specification;
    }
}
