package com.bd.bern.oatz.service;

import com.bd.bern.oatz.domain.Enterprise;
import com.bd.bern.oatz.repository.EnterpriseRepository;
import com.bd.bern.oatz.repository.search.EnterpriseSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Enterprise}.
 */
@Service
@Transactional
public class EnterpriseService {

    private final Logger log = LoggerFactory.getLogger(EnterpriseService.class);

    private final EnterpriseRepository enterpriseRepository;

    private final EnterpriseSearchRepository enterpriseSearchRepository;

    public EnterpriseService(EnterpriseRepository enterpriseRepository, EnterpriseSearchRepository enterpriseSearchRepository) {
        this.enterpriseRepository = enterpriseRepository;
        this.enterpriseSearchRepository = enterpriseSearchRepository;
    }

    /**
     * Save a enterprise.
     *
     * @param enterprise the entity to save.
     * @return the persisted entity.
     */
    public Enterprise save(Enterprise enterprise) {
        log.debug("Request to save Enterprise : {}", enterprise);
        Enterprise result = enterpriseRepository.save(enterprise);
        enterpriseSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the enterprises.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Enterprise> findAll(Pageable pageable) {
        log.debug("Request to get all Enterprises");
        return enterpriseRepository.findAll(pageable);
    }


    /**
     * Get one enterprise by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Enterprise> findOne(Long id) {
        log.debug("Request to get Enterprise : {}", id);
        return enterpriseRepository.findById(id);
    }

    /**
     * Delete the enterprise by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Enterprise : {}", id);
        enterpriseRepository.deleteById(id);
        enterpriseSearchRepository.deleteById(id);
    }

    /**
     * Search for the enterprise corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Enterprise> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Enterprises for query {}", query);
        return enterpriseSearchRepository.search(queryStringQuery(query), pageable);    }
}
