package com.bd.bern.oatz.web.rest;

import com.bd.bern.oatz.OatzSkillApp;
import com.bd.bern.oatz.domain.Enterprise;
import com.bd.bern.oatz.domain.Project;
import com.bd.bern.oatz.repository.EnterpriseRepository;
import com.bd.bern.oatz.repository.search.EnterpriseSearchRepository;
import com.bd.bern.oatz.service.EnterpriseService;
import com.bd.bern.oatz.web.rest.errors.ExceptionTranslator;
import com.bd.bern.oatz.service.dto.EnterpriseCriteria;
import com.bd.bern.oatz.service.EnterpriseQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static com.bd.bern.oatz.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link EnterpriseResource} REST controller.
 */
@SpringBootTest(classes = OatzSkillApp.class)
public class EnterpriseResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private EnterpriseService enterpriseService;

    /**
     * This repository is mocked in the com.bd.bern.oatz.repository.search test package.
     *
     * @see com.bd.bern.oatz.repository.search.EnterpriseSearchRepositoryMockConfiguration
     */
    @Autowired
    private EnterpriseSearchRepository mockEnterpriseSearchRepository;

    @Autowired
    private EnterpriseQueryService enterpriseQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restEnterpriseMockMvc;

    private Enterprise enterprise;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final EnterpriseResource enterpriseResource = new EnterpriseResource(enterpriseService, enterpriseQueryService);
        this.restEnterpriseMockMvc = MockMvcBuilders.standaloneSetup(enterpriseResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Enterprise createEntity(EntityManager em) {
        Enterprise enterprise = new Enterprise()
            .title(DEFAULT_TITLE);
        return enterprise;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Enterprise createUpdatedEntity(EntityManager em) {
        Enterprise enterprise = new Enterprise()
            .title(UPDATED_TITLE);
        return enterprise;
    }

    @BeforeEach
    public void initTest() {
        enterprise = createEntity(em);
    }

    @Test
    @Transactional
    public void createEnterprise() throws Exception {
        int databaseSizeBeforeCreate = enterpriseRepository.findAll().size();

        // Create the Enterprise
        restEnterpriseMockMvc.perform(post("/api/enterprises")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(enterprise)))
            .andExpect(status().isCreated());

        // Validate the Enterprise in the database
        List<Enterprise> enterpriseList = enterpriseRepository.findAll();
        assertThat(enterpriseList).hasSize(databaseSizeBeforeCreate + 1);
        Enterprise testEnterprise = enterpriseList.get(enterpriseList.size() - 1);
        assertThat(testEnterprise.getTitle()).isEqualTo(DEFAULT_TITLE);

        // Validate the Enterprise in Elasticsearch
        verify(mockEnterpriseSearchRepository, times(1)).save(testEnterprise);
    }

    @Test
    @Transactional
    public void createEnterpriseWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = enterpriseRepository.findAll().size();

        // Create the Enterprise with an existing ID
        enterprise.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnterpriseMockMvc.perform(post("/api/enterprises")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(enterprise)))
            .andExpect(status().isBadRequest());

        // Validate the Enterprise in the database
        List<Enterprise> enterpriseList = enterpriseRepository.findAll();
        assertThat(enterpriseList).hasSize(databaseSizeBeforeCreate);

        // Validate the Enterprise in Elasticsearch
        verify(mockEnterpriseSearchRepository, times(0)).save(enterprise);
    }


    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = enterpriseRepository.findAll().size();
        // set the field null
        enterprise.setTitle(null);

        // Create the Enterprise, which fails.

        restEnterpriseMockMvc.perform(post("/api/enterprises")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(enterprise)))
            .andExpect(status().isBadRequest());

        List<Enterprise> enterpriseList = enterpriseRepository.findAll();
        assertThat(enterpriseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEnterprises() throws Exception {
        // Initialize the database
        enterpriseRepository.saveAndFlush(enterprise);

        // Get all the enterpriseList
        restEnterpriseMockMvc.perform(get("/api/enterprises?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(enterprise.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)));
    }
    
    @Test
    @Transactional
    public void getEnterprise() throws Exception {
        // Initialize the database
        enterpriseRepository.saveAndFlush(enterprise);

        // Get the enterprise
        restEnterpriseMockMvc.perform(get("/api/enterprises/{id}", enterprise.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(enterprise.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE));
    }

    @Test
    @Transactional
    public void getAllEnterprisesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        enterpriseRepository.saveAndFlush(enterprise);

        // Get all the enterpriseList where title equals to DEFAULT_TITLE
        defaultEnterpriseShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the enterpriseList where title equals to UPDATED_TITLE
        defaultEnterpriseShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllEnterprisesByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        enterpriseRepository.saveAndFlush(enterprise);

        // Get all the enterpriseList where title not equals to DEFAULT_TITLE
        defaultEnterpriseShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the enterpriseList where title not equals to UPDATED_TITLE
        defaultEnterpriseShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllEnterprisesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        enterpriseRepository.saveAndFlush(enterprise);

        // Get all the enterpriseList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultEnterpriseShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the enterpriseList where title equals to UPDATED_TITLE
        defaultEnterpriseShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllEnterprisesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        enterpriseRepository.saveAndFlush(enterprise);

        // Get all the enterpriseList where title is not null
        defaultEnterpriseShouldBeFound("title.specified=true");

        // Get all the enterpriseList where title is null
        defaultEnterpriseShouldNotBeFound("title.specified=false");
    }
                @Test
    @Transactional
    public void getAllEnterprisesByTitleContainsSomething() throws Exception {
        // Initialize the database
        enterpriseRepository.saveAndFlush(enterprise);

        // Get all the enterpriseList where title contains DEFAULT_TITLE
        defaultEnterpriseShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the enterpriseList where title contains UPDATED_TITLE
        defaultEnterpriseShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllEnterprisesByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        enterpriseRepository.saveAndFlush(enterprise);

        // Get all the enterpriseList where title does not contain DEFAULT_TITLE
        defaultEnterpriseShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the enterpriseList where title does not contain UPDATED_TITLE
        defaultEnterpriseShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }


    @Test
    @Transactional
    public void getAllEnterprisesByProjectsIsEqualToSomething() throws Exception {
        // Initialize the database
        enterpriseRepository.saveAndFlush(enterprise);
        Project projects = ProjectResourceIT.createEntity(em);
        em.persist(projects);
        em.flush();
        enterprise.addProjects(projects);
        enterpriseRepository.saveAndFlush(enterprise);
        Long projectsId = projects.getId();

        // Get all the enterpriseList where projects equals to projectsId
        defaultEnterpriseShouldBeFound("projectsId.equals=" + projectsId);

        // Get all the enterpriseList where projects equals to projectsId + 1
        defaultEnterpriseShouldNotBeFound("projectsId.equals=" + (projectsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEnterpriseShouldBeFound(String filter) throws Exception {
        restEnterpriseMockMvc.perform(get("/api/enterprises?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(enterprise.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)));

        // Check, that the count call also returns 1
        restEnterpriseMockMvc.perform(get("/api/enterprises/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEnterpriseShouldNotBeFound(String filter) throws Exception {
        restEnterpriseMockMvc.perform(get("/api/enterprises?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEnterpriseMockMvc.perform(get("/api/enterprises/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingEnterprise() throws Exception {
        // Get the enterprise
        restEnterpriseMockMvc.perform(get("/api/enterprises/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEnterprise() throws Exception {
        // Initialize the database
        enterpriseService.save(enterprise);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockEnterpriseSearchRepository);

        int databaseSizeBeforeUpdate = enterpriseRepository.findAll().size();

        // Update the enterprise
        Enterprise updatedEnterprise = enterpriseRepository.findById(enterprise.getId()).get();
        // Disconnect from session so that the updates on updatedEnterprise are not directly saved in db
        em.detach(updatedEnterprise);
        updatedEnterprise
            .title(UPDATED_TITLE);

        restEnterpriseMockMvc.perform(put("/api/enterprises")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedEnterprise)))
            .andExpect(status().isOk());

        // Validate the Enterprise in the database
        List<Enterprise> enterpriseList = enterpriseRepository.findAll();
        assertThat(enterpriseList).hasSize(databaseSizeBeforeUpdate);
        Enterprise testEnterprise = enterpriseList.get(enterpriseList.size() - 1);
        assertThat(testEnterprise.getTitle()).isEqualTo(UPDATED_TITLE);

        // Validate the Enterprise in Elasticsearch
        verify(mockEnterpriseSearchRepository, times(1)).save(testEnterprise);
    }

    @Test
    @Transactional
    public void updateNonExistingEnterprise() throws Exception {
        int databaseSizeBeforeUpdate = enterpriseRepository.findAll().size();

        // Create the Enterprise

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnterpriseMockMvc.perform(put("/api/enterprises")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(enterprise)))
            .andExpect(status().isBadRequest());

        // Validate the Enterprise in the database
        List<Enterprise> enterpriseList = enterpriseRepository.findAll();
        assertThat(enterpriseList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Enterprise in Elasticsearch
        verify(mockEnterpriseSearchRepository, times(0)).save(enterprise);
    }

    @Test
    @Transactional
    public void deleteEnterprise() throws Exception {
        // Initialize the database
        enterpriseService.save(enterprise);

        int databaseSizeBeforeDelete = enterpriseRepository.findAll().size();

        // Delete the enterprise
        restEnterpriseMockMvc.perform(delete("/api/enterprises/{id}", enterprise.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Enterprise> enterpriseList = enterpriseRepository.findAll();
        assertThat(enterpriseList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Enterprise in Elasticsearch
        verify(mockEnterpriseSearchRepository, times(1)).deleteById(enterprise.getId());
    }

    @Test
    @Transactional
    public void searchEnterprise() throws Exception {
        // Initialize the database
        enterpriseService.save(enterprise);
        when(mockEnterpriseSearchRepository.search(queryStringQuery("id:" + enterprise.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(enterprise), PageRequest.of(0, 1), 1));
        // Search the enterprise
        restEnterpriseMockMvc.perform(get("/api/_search/enterprises?query=id:" + enterprise.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(enterprise.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Enterprise.class);
        Enterprise enterprise1 = new Enterprise();
        enterprise1.setId(1L);
        Enterprise enterprise2 = new Enterprise();
        enterprise2.setId(enterprise1.getId());
        assertThat(enterprise1).isEqualTo(enterprise2);
        enterprise2.setId(2L);
        assertThat(enterprise1).isNotEqualTo(enterprise2);
        enterprise1.setId(null);
        assertThat(enterprise1).isNotEqualTo(enterprise2);
    }
}
