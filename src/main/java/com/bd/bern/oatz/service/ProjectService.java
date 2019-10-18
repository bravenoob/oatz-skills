package com.bd.bern.oatz.service;

import com.bd.bern.oatz.domain.Project;
import com.bd.bern.oatz.repository.ProjectRepository;
import com.bd.bern.oatz.repository.search.ProjectSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Project}.
 */
@Service
@Transactional
public class ProjectService {

    private final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;

    private final ProjectSearchRepository projectSearchRepository;

    public ProjectService(ProjectRepository projectRepository, ProjectSearchRepository projectSearchRepository) {
        this.projectRepository = projectRepository;
        this.projectSearchRepository = projectSearchRepository;
    }

    /**
     * Save a project.
     *
     * @param project the entity to save.
     * @return the persisted entity.
     */
    public Project save(Project project) {
        log.debug("Request to save Project : {}", project);
        Project result = projectRepository.save(project);
        projectSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the projects.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Project> findAll(Pageable pageable) {
        log.debug("Request to get all Projects");
        return projectRepository.findAll(pageable);
    }


    /**
     * Get one project by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Project> findOne(Long id) {
        log.debug("Request to get Project : {}", id);
        return projectRepository.findById(id);
    }

    /**
     * Delete the project by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Project : {}", id);
        projectRepository.deleteById(id);
        projectSearchRepository.deleteById(id);
    }

    /**
     * Search for the project corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Project> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Projects for query {}", query);
        return projectSearchRepository.search(queryStringQuery(query), pageable);    }
}
