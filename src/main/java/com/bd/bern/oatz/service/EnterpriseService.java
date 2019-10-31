package com.bd.bern.oatz.service;

import com.bd.bern.oatz.domain.Enterprise;
import com.bd.bern.oatz.repository.EnterpriseRepository;
import com.bd.bern.oatz.repository.search.EnterpriseSearchRepository;
import com.bd.bern.oatz.service.dto.EnterpriseDTO;
import com.bd.bern.oatz.service.mapper.EnterpriseMapper;
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

    private final EnterpriseMapper enterpriseMapper;

    private final EnterpriseSearchRepository enterpriseSearchRepository;

    public EnterpriseService(EnterpriseRepository enterpriseRepository, EnterpriseMapper enterpriseMapper, EnterpriseSearchRepository enterpriseSearchRepository) {
        this.enterpriseRepository = enterpriseRepository;
        this.enterpriseMapper = enterpriseMapper;
        this.enterpriseSearchRepository = enterpriseSearchRepository;
    }

    /**
     * Save a enterprise.
     *
     * @param enterpriseDTO the entity to save.
     * @return the persisted entity.
     */
    public EnterpriseDTO save(EnterpriseDTO enterpriseDTO) {
        log.debug("Request to save Enterprise : {}", enterpriseDTO);
        Enterprise enterprise = enterpriseMapper.toEntity(enterpriseDTO);
        enterprise = enterpriseRepository.save(enterprise);
        EnterpriseDTO result = enterpriseMapper.toDto(enterprise);
        enterpriseSearchRepository.save(enterprise);
        return result;
    }

    /**
     * Get all the enterprises.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EnterpriseDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Enterprises");
        return enterpriseRepository.findAll(pageable)
            .map(enterpriseMapper::toDto);
    }


    /**
     * Get one enterprise by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EnterpriseDTO> findOne(Long id) {
        log.debug("Request to get Enterprise : {}", id);
        return enterpriseRepository.findById(id)
            .map(enterpriseMapper::toDto);
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
    public Page<EnterpriseDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Enterprises for query {}", query);
        return enterpriseSearchRepository.search(queryStringQuery(query), pageable)
            .map(enterpriseMapper::toDto);
    }
}
