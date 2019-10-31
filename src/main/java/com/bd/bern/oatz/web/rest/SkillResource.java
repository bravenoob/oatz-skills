package com.bd.bern.oatz.web.rest;

import com.bd.bern.oatz.service.SkillService;
import com.bd.bern.oatz.web.rest.errors.BadRequestAlertException;
import com.bd.bern.oatz.service.dto.SkillDTO;
import com.bd.bern.oatz.service.dto.SkillCriteria;
import com.bd.bern.oatz.service.SkillQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.bd.bern.oatz.domain.Skill}.
 */
@RestController
@RequestMapping("/api")
public class SkillResource {

    private final Logger log = LoggerFactory.getLogger(SkillResource.class);

    private static final String ENTITY_NAME = "oatzSkillSkill";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SkillService skillService;

    private final SkillQueryService skillQueryService;

    public SkillResource(SkillService skillService, SkillQueryService skillQueryService) {
        this.skillService = skillService;
        this.skillQueryService = skillQueryService;
    }

    /**
     * {@code POST  /skills} : Create a new skill.
     *
     * @param skillDTO the skillDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new skillDTO, or with status {@code 400 (Bad Request)} if the skill has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/skills")
    public ResponseEntity<SkillDTO> createSkill(@Valid @RequestBody SkillDTO skillDTO) throws URISyntaxException {
        log.debug("REST request to save Skill : {}", skillDTO);
        if (skillDTO.getId() != null) {
            throw new BadRequestAlertException("A new skill cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SkillDTO result = skillService.save(skillDTO);
        return ResponseEntity.created(new URI("/api/skills/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /skills} : Updates an existing skill.
     *
     * @param skillDTO the skillDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated skillDTO,
     * or with status {@code 400 (Bad Request)} if the skillDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the skillDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/skills")
    public ResponseEntity<SkillDTO> updateSkill(@Valid @RequestBody SkillDTO skillDTO) throws URISyntaxException {
        log.debug("REST request to update Skill : {}", skillDTO);
        if (skillDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SkillDTO result = skillService.save(skillDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, skillDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /skills} : get all the skills.
     *

     * @param pageable the pagination information.

     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of skills in body.
     */
    @GetMapping("/skills")
    public ResponseEntity<List<SkillDTO>> getAllSkills(SkillCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Skills by criteria: {}", criteria);
        Page<SkillDTO> page = skillQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * {@code GET  /skills/count} : count all the skills.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
    */
    @GetMapping("/skills/count")
    public ResponseEntity<Long> countSkills(SkillCriteria criteria) {
        log.debug("REST request to count Skills by criteria: {}", criteria);
        return ResponseEntity.ok().body(skillQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /skills/:id} : get the "id" skill.
     *
     * @param id the id of the skillDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the skillDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/skills/{id}")
    public ResponseEntity<SkillDTO> getSkill(@PathVariable Long id) {
        log.debug("REST request to get Skill : {}", id);
        Optional<SkillDTO> skillDTO = skillService.findOne(id);
        return ResponseUtil.wrapOrNotFound(skillDTO);
    }

    /**
     * {@code DELETE  /skills/:id} : delete the "id" skill.
     *
     * @param id the id of the skillDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/skills/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        log.debug("REST request to delete Skill : {}", id);
        skillService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/skills?query=:query} : search for the skill corresponding
     * to the query.
     *
     * @param query the query of the skill search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/skills")
    public ResponseEntity<List<SkillDTO>> searchSkills(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Skills for query {}", query);
        Page<SkillDTO> page = skillService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
