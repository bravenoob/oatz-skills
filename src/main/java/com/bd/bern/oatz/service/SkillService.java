package com.bd.bern.oatz.service;

import com.bd.bern.oatz.domain.Skill;
import com.bd.bern.oatz.repository.SkillRepository;
import com.bd.bern.oatz.repository.search.SkillSearchRepository;
import com.bd.bern.oatz.service.dto.SkillDTO;
import com.bd.bern.oatz.service.mapper.SkillMapper;
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

    private final SkillMapper skillMapper;

    private final SkillSearchRepository skillSearchRepository;

    public SkillService(SkillRepository skillRepository, SkillMapper skillMapper, SkillSearchRepository skillSearchRepository) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
        this.skillSearchRepository = skillSearchRepository;
    }

    /**
     * Save a skill.
     *
     * @param skillDTO the entity to save.
     * @return the persisted entity.
     */
    public SkillDTO save(SkillDTO skillDTO) {
        log.debug("Request to save Skill : {}", skillDTO);
        Skill skill = skillMapper.toEntity(skillDTO);
        skill = skillRepository.save(skill);
        SkillDTO result = skillMapper.toDto(skill);
        skillSearchRepository.save(skill);
        return result;
    }

    /**
     * Get all the skills.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SkillDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Skills");
        return skillRepository.findAll(pageable)
            .map(skillMapper::toDto);
    }


    /**
     * Get one skill by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SkillDTO> findOne(Long id) {
        log.debug("Request to get Skill : {}", id);
        return skillRepository.findById(id)
            .map(skillMapper::toDto);
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
    public Page<SkillDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Skills for query {}", query);
        return skillSearchRepository.search(queryStringQuery(query), pageable)
            .map(skillMapper::toDto);
    }
}
