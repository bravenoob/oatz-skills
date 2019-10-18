package com.bd.bern.oatz.service;

import com.bd.bern.oatz.domain.Skill;
import com.bd.bern.oatz.repository.SkillRepository;
import com.bd.bern.oatz.repository.search.SkillSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Skill}.
 */
@Service
@Transactional
public class SkillService {

    private final Logger log = LoggerFactory.getLogger(SkillService.class);

    private final SkillRepository skillRepository;

    private final SkillSearchRepository skillSearchRepository;

    public SkillService(SkillRepository skillRepository, SkillSearchRepository skillSearchRepository) {
        this.skillRepository = skillRepository;
        this.skillSearchRepository = skillSearchRepository;
    }

    /**
     * Save a skill.
     *
     * @param skill the entity to save.
     * @return the persisted entity.
     */
    public Skill save(Skill skill) {
        log.debug("Request to save Skill : {}", skill);
        Skill result = skillRepository.save(skill);
        skillSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the skills.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Skill> findAll(Pageable pageable) {
        log.debug("Request to get all Skills");
        return skillRepository.findAll(pageable);
    }


    /**
     * Get one skill by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Skill> findOne(Long id) {
        log.debug("Request to get Skill : {}", id);
        return skillRepository.findById(id);
    }

    /**
     * Delete the skill by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Skill : {}", id);
        skillRepository.deleteById(id);
        skillSearchRepository.deleteById(id);
    }

    /**
     * Search for the skill corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Skill> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Skills for query {}", query);
        return skillSearchRepository.search(queryStringQuery(query), pageable);    }
}
