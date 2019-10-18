package com.bd.bern.oatz.service;

import com.bd.bern.oatz.domain.SkillApplied;
import com.bd.bern.oatz.repository.SkillAppliedRepository;
import com.bd.bern.oatz.repository.search.SkillAppliedSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link SkillApplied}.
 */
@Service
@Transactional
public class SkillAppliedService {

    private final Logger log = LoggerFactory.getLogger(SkillAppliedService.class);

    private final SkillAppliedRepository skillAppliedRepository;

    private final SkillAppliedSearchRepository skillAppliedSearchRepository;

    public SkillAppliedService(SkillAppliedRepository skillAppliedRepository, SkillAppliedSearchRepository skillAppliedSearchRepository) {
        this.skillAppliedRepository = skillAppliedRepository;
        this.skillAppliedSearchRepository = skillAppliedSearchRepository;
    }

    /**
     * Save a skillApplied.
     *
     * @param skillApplied the entity to save.
     * @return the persisted entity.
     */
    public SkillApplied save(SkillApplied skillApplied) {
        log.debug("Request to save SkillApplied : {}", skillApplied);
        SkillApplied result = skillAppliedRepository.save(skillApplied);
        skillAppliedSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the skillApplieds.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SkillApplied> findAll(Pageable pageable) {
        log.debug("Request to get all SkillApplieds");
        return skillAppliedRepository.findAll(pageable);
    }


    /**
     * Get one skillApplied by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SkillApplied> findOne(Long id) {
        log.debug("Request to get SkillApplied : {}", id);
        return skillAppliedRepository.findById(id);
    }

    /**
     * Delete the skillApplied by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete SkillApplied : {}", id);
        skillAppliedRepository.deleteById(id);
        skillAppliedSearchRepository.deleteById(id);
    }

    /**
     * Search for the skillApplied corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SkillApplied> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SkillApplieds for query {}", query);
        return skillAppliedSearchRepository.search(queryStringQuery(query), pageable);    }
}
