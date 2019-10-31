package com.bd.bern.oatz.web.rest;

import com.bd.bern.oatz.service.SkillAppliedService;
import com.bd.bern.oatz.web.rest.errors.BadRequestAlertException;
import com.bd.bern.oatz.service.dto.SkillAppliedDTO;
import com.bd.bern.oatz.service.dto.SkillAppliedCriteria;
import com.bd.bern.oatz.service.SkillAppliedQueryService;

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
 * REST controller for managing {@link com.bd.bern.oatz.domain.SkillApplied}.
 */
@RestController
@RequestMapping("/api")
public class SkillAppliedResource {

    private final Logger log = LoggerFactory.getLogger(SkillAppliedResource.class);

    private static final String ENTITY_NAME = "oatzSkillSkillApplied";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SkillAppliedService skillAppliedService;

    private final SkillAppliedQueryService skillAppliedQueryService;

    public SkillAppliedResource(SkillAppliedService skillAppliedService, SkillAppliedQueryService skillAppliedQueryService) {
        this.skillAppliedService = skillAppliedService;
        this.skillAppliedQueryService = skillAppliedQueryService;
    }

    /**
     * {@code POST  /skill-applieds} : Create a new skillApplied.
     *
     * @param skillAppliedDTO the skillAppliedDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new skillAppliedDTO, or with status {@code 400 (Bad Request)} if the skillApplied has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/skill-applieds")
    public ResponseEntity<SkillAppliedDTO> createSkillApplied(@Valid @RequestBody SkillAppliedDTO skillAppliedDTO) throws URISyntaxException {
        log.debug("REST request to save SkillApplied : {}", skillAppliedDTO);
        if (skillAppliedDTO.getId() != null) {
            throw new BadRequestAlertException("A new skillApplied cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SkillAppliedDTO result = skillAppliedService.save(skillAppliedDTO);
        return ResponseEntity.created(new URI("/api/skill-applieds/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /skill-applieds} : Updates an existing skillApplied.
     *
     * @param skillAppliedDTO the skillAppliedDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated skillAppliedDTO,
     * or with status {@code 400 (Bad Request)} if the skillAppliedDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the skillAppliedDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/skill-applieds")
    public ResponseEntity<SkillAppliedDTO> updateSkillApplied(@Valid @RequestBody SkillAppliedDTO skillAppliedDTO) throws URISyntaxException {
        log.debug("REST request to update SkillApplied : {}", skillAppliedDTO);
        if (skillAppliedDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SkillAppliedDTO result = skillAppliedService.save(skillAppliedDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, skillAppliedDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /skill-applieds} : get all the skillApplieds.
     *

     * @param pageable the pagination information.

     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of skillApplieds in body.
     */
    @GetMapping("/skill-applieds")
    public ResponseEntity<List<SkillAppliedDTO>> getAllSkillApplieds(SkillAppliedCriteria criteria, Pageable pageable) {
        log.debug("REST request to get SkillApplieds by criteria: {}", criteria);
        Page<SkillAppliedDTO> page = skillAppliedQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * {@code GET  /skill-applieds/count} : count all the skillApplieds.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
    */
    @GetMapping("/skill-applieds/count")
    public ResponseEntity<Long> countSkillApplieds(SkillAppliedCriteria criteria) {
        log.debug("REST request to count SkillApplieds by criteria: {}", criteria);
        return ResponseEntity.ok().body(skillAppliedQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /skill-applieds/:id} : get the "id" skillApplied.
     *
     * @param id the id of the skillAppliedDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the skillAppliedDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/skill-applieds/{id}")
    public ResponseEntity<SkillAppliedDTO> getSkillApplied(@PathVariable Long id) {
        log.debug("REST request to get SkillApplied : {}", id);
        Optional<SkillAppliedDTO> skillAppliedDTO = skillAppliedService.findOne(id);
        return ResponseUtil.wrapOrNotFound(skillAppliedDTO);
    }

    /**
     * {@code DELETE  /skill-applieds/:id} : delete the "id" skillApplied.
     *
     * @param id the id of the skillAppliedDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/skill-applieds/{id}")
    public ResponseEntity<Void> deleteSkillApplied(@PathVariable Long id) {
        log.debug("REST request to delete SkillApplied : {}", id);
        skillAppliedService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/skill-applieds?query=:query} : search for the skillApplied corresponding
     * to the query.
     *
     * @param query the query of the skillApplied search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/skill-applieds")
    public ResponseEntity<List<SkillAppliedDTO>> searchSkillApplieds(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of SkillApplieds for query {}", query);
        Page<SkillAppliedDTO> page = skillAppliedService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
