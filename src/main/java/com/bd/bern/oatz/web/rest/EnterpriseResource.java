package com.bd.bern.oatz.web.rest;

import com.bd.bern.oatz.service.EnterpriseService;
import com.bd.bern.oatz.web.rest.errors.BadRequestAlertException;
import com.bd.bern.oatz.service.dto.EnterpriseDTO;
import com.bd.bern.oatz.service.dto.EnterpriseCriteria;
import com.bd.bern.oatz.service.EnterpriseQueryService;

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
 * REST controller for managing {@link com.bd.bern.oatz.domain.Enterprise}.
 */
@RestController
@RequestMapping("/api")
public class EnterpriseResource {

    private final Logger log = LoggerFactory.getLogger(EnterpriseResource.class);

    private static final String ENTITY_NAME = "oatzSkillEnterprise";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EnterpriseService enterpriseService;

    private final EnterpriseQueryService enterpriseQueryService;

    public EnterpriseResource(EnterpriseService enterpriseService, EnterpriseQueryService enterpriseQueryService) {
        this.enterpriseService = enterpriseService;
        this.enterpriseQueryService = enterpriseQueryService;
    }

    /**
     * {@code POST  /enterprises} : Create a new enterprise.
     *
     * @param enterpriseDTO the enterpriseDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new enterpriseDTO, or with status {@code 400 (Bad Request)} if the enterprise has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/enterprises")
    public ResponseEntity<EnterpriseDTO> createEnterprise(@Valid @RequestBody EnterpriseDTO enterpriseDTO) throws URISyntaxException {
        log.debug("REST request to save Enterprise : {}", enterpriseDTO);
        if (enterpriseDTO.getId() != null) {
            throw new BadRequestAlertException("A new enterprise cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EnterpriseDTO result = enterpriseService.save(enterpriseDTO);
        return ResponseEntity.created(new URI("/api/enterprises/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /enterprises} : Updates an existing enterprise.
     *
     * @param enterpriseDTO the enterpriseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated enterpriseDTO,
     * or with status {@code 400 (Bad Request)} if the enterpriseDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the enterpriseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/enterprises")
    public ResponseEntity<EnterpriseDTO> updateEnterprise(@Valid @RequestBody EnterpriseDTO enterpriseDTO) throws URISyntaxException {
        log.debug("REST request to update Enterprise : {}", enterpriseDTO);
        if (enterpriseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EnterpriseDTO result = enterpriseService.save(enterpriseDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, enterpriseDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /enterprises} : get all the enterprises.
     *

     * @param pageable the pagination information.

     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of enterprises in body.
     */
    @GetMapping("/enterprises")
    public ResponseEntity<List<EnterpriseDTO>> getAllEnterprises(EnterpriseCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Enterprises by criteria: {}", criteria);
        Page<EnterpriseDTO> page = enterpriseQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * {@code GET  /enterprises/count} : count all the enterprises.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
    */
    @GetMapping("/enterprises/count")
    public ResponseEntity<Long> countEnterprises(EnterpriseCriteria criteria) {
        log.debug("REST request to count Enterprises by criteria: {}", criteria);
        return ResponseEntity.ok().body(enterpriseQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /enterprises/:id} : get the "id" enterprise.
     *
     * @param id the id of the enterpriseDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the enterpriseDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/enterprises/{id}")
    public ResponseEntity<EnterpriseDTO> getEnterprise(@PathVariable Long id) {
        log.debug("REST request to get Enterprise : {}", id);
        Optional<EnterpriseDTO> enterpriseDTO = enterpriseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(enterpriseDTO);
    }

    /**
     * {@code DELETE  /enterprises/:id} : delete the "id" enterprise.
     *
     * @param id the id of the enterpriseDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/enterprises/{id}")
    public ResponseEntity<Void> deleteEnterprise(@PathVariable Long id) {
        log.debug("REST request to delete Enterprise : {}", id);
        enterpriseService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/enterprises?query=:query} : search for the enterprise corresponding
     * to the query.
     *
     * @param query the query of the enterprise search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/enterprises")
    public ResponseEntity<List<EnterpriseDTO>> searchEnterprises(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Enterprises for query {}", query);
        Page<EnterpriseDTO> page = enterpriseService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
