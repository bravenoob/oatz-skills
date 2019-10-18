package com.bd.bern.oatz.web.rest;

import com.bd.bern.oatz.OatzSkillApp;
import com.bd.bern.oatz.domain.SkillApplied;
import com.bd.bern.oatz.domain.Skill;
import com.bd.bern.oatz.domain.Project;
import com.bd.bern.oatz.repository.SkillAppliedRepository;
import com.bd.bern.oatz.repository.search.SkillAppliedSearchRepository;
import com.bd.bern.oatz.service.SkillAppliedService;
import com.bd.bern.oatz.web.rest.errors.ExceptionTranslator;
import com.bd.bern.oatz.service.dto.SkillAppliedCriteria;
import com.bd.bern.oatz.service.SkillAppliedQueryService;

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
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link SkillAppliedResource} REST controller.
 */
@SpringBootTest(classes = OatzSkillApp.class)
public class SkillAppliedResourceIT {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;
    private static final Long SMALLER_USER_ID = 1L - 1L;

    private static final LocalDate DEFAULT_USED_AT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_USED_AT = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_USED_AT = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private SkillAppliedRepository skillAppliedRepository;

    @Autowired
    private SkillAppliedService skillAppliedService;

    /**
     * This repository is mocked in the com.bd.bern.oatz.repository.search test package.
     *
     * @see com.bd.bern.oatz.repository.search.SkillAppliedSearchRepositoryMockConfiguration
     */
    @Autowired
    private SkillAppliedSearchRepository mockSkillAppliedSearchRepository;

    @Autowired
    private SkillAppliedQueryService skillAppliedQueryService;

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

    private MockMvc restSkillAppliedMockMvc;

    private SkillApplied skillApplied;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SkillAppliedResource skillAppliedResource = new SkillAppliedResource(skillAppliedService, skillAppliedQueryService);
        this.restSkillAppliedMockMvc = MockMvcBuilders.standaloneSetup(skillAppliedResource)
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
    public static SkillApplied createEntity(EntityManager em) {
        SkillApplied skillApplied = new SkillApplied()
            .userId(DEFAULT_USER_ID)
            .usedAt(DEFAULT_USED_AT)
            .description(DEFAULT_DESCRIPTION);
        // Add required entity
        Skill skill;
        if (TestUtil.findAll(em, Skill.class).isEmpty()) {
            skill = SkillResourceIT.createEntity(em);
            em.persist(skill);
            em.flush();
        } else {
            skill = TestUtil.findAll(em, Skill.class).get(0);
        }
        skillApplied.setSkill(skill);
        // Add required entity
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            project = ProjectResourceIT.createEntity(em);
            em.persist(project);
            em.flush();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        skillApplied.setProject(project);
        return skillApplied;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SkillApplied createUpdatedEntity(EntityManager em) {
        SkillApplied skillApplied = new SkillApplied()
            .userId(UPDATED_USER_ID)
            .usedAt(UPDATED_USED_AT)
            .description(UPDATED_DESCRIPTION);
        // Add required entity
        Skill skill;
        if (TestUtil.findAll(em, Skill.class).isEmpty()) {
            skill = SkillResourceIT.createUpdatedEntity(em);
            em.persist(skill);
            em.flush();
        } else {
            skill = TestUtil.findAll(em, Skill.class).get(0);
        }
        skillApplied.setSkill(skill);
        // Add required entity
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            project = ProjectResourceIT.createUpdatedEntity(em);
            em.persist(project);
            em.flush();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        skillApplied.setProject(project);
        return skillApplied;
    }

    @BeforeEach
    public void initTest() {
        skillApplied = createEntity(em);
    }

    @Test
    @Transactional
    public void createSkillApplied() throws Exception {
        int databaseSizeBeforeCreate = skillAppliedRepository.findAll().size();

        // Create the SkillApplied
        restSkillAppliedMockMvc.perform(post("/api/skill-applieds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(skillApplied)))
            .andExpect(status().isCreated());

        // Validate the SkillApplied in the database
        List<SkillApplied> skillAppliedList = skillAppliedRepository.findAll();
        assertThat(skillAppliedList).hasSize(databaseSizeBeforeCreate + 1);
        SkillApplied testSkillApplied = skillAppliedList.get(skillAppliedList.size() - 1);
        assertThat(testSkillApplied.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testSkillApplied.getUsedAt()).isEqualTo(DEFAULT_USED_AT);
        assertThat(testSkillApplied.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the SkillApplied in Elasticsearch
        verify(mockSkillAppliedSearchRepository, times(1)).save(testSkillApplied);
    }

    @Test
    @Transactional
    public void createSkillAppliedWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = skillAppliedRepository.findAll().size();

        // Create the SkillApplied with an existing ID
        skillApplied.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSkillAppliedMockMvc.perform(post("/api/skill-applieds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(skillApplied)))
            .andExpect(status().isBadRequest());

        // Validate the SkillApplied in the database
        List<SkillApplied> skillAppliedList = skillAppliedRepository.findAll();
        assertThat(skillAppliedList).hasSize(databaseSizeBeforeCreate);

        // Validate the SkillApplied in Elasticsearch
        verify(mockSkillAppliedSearchRepository, times(0)).save(skillApplied);
    }


    @Test
    @Transactional
    public void checkUsedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = skillAppliedRepository.findAll().size();
        // set the field null
        skillApplied.setUsedAt(null);

        // Create the SkillApplied, which fails.

        restSkillAppliedMockMvc.perform(post("/api/skill-applieds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(skillApplied)))
            .andExpect(status().isBadRequest());

        List<SkillApplied> skillAppliedList = skillAppliedRepository.findAll();
        assertThat(skillAppliedList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSkillApplieds() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList
        restSkillAppliedMockMvc.perform(get("/api/skill-applieds?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(skillApplied.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].usedAt").value(hasItem(DEFAULT_USED_AT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
    
    @Test
    @Transactional
    public void getSkillApplied() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get the skillApplied
        restSkillAppliedMockMvc.perform(get("/api/skill-applieds/{id}", skillApplied.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(skillApplied.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.usedAt").value(DEFAULT_USED_AT.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByUserIdIsEqualToSomething() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where userId equals to DEFAULT_USER_ID
        defaultSkillAppliedShouldBeFound("userId.equals=" + DEFAULT_USER_ID);

        // Get all the skillAppliedList where userId equals to UPDATED_USER_ID
        defaultSkillAppliedShouldNotBeFound("userId.equals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByUserIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where userId not equals to DEFAULT_USER_ID
        defaultSkillAppliedShouldNotBeFound("userId.notEquals=" + DEFAULT_USER_ID);

        // Get all the skillAppliedList where userId not equals to UPDATED_USER_ID
        defaultSkillAppliedShouldBeFound("userId.notEquals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByUserIdIsInShouldWork() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where userId in DEFAULT_USER_ID or UPDATED_USER_ID
        defaultSkillAppliedShouldBeFound("userId.in=" + DEFAULT_USER_ID + "," + UPDATED_USER_ID);

        // Get all the skillAppliedList where userId equals to UPDATED_USER_ID
        defaultSkillAppliedShouldNotBeFound("userId.in=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByUserIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where userId is not null
        defaultSkillAppliedShouldBeFound("userId.specified=true");

        // Get all the skillAppliedList where userId is null
        defaultSkillAppliedShouldNotBeFound("userId.specified=false");
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByUserIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where userId is greater than or equal to DEFAULT_USER_ID
        defaultSkillAppliedShouldBeFound("userId.greaterThanOrEqual=" + DEFAULT_USER_ID);

        // Get all the skillAppliedList where userId is greater than or equal to UPDATED_USER_ID
        defaultSkillAppliedShouldNotBeFound("userId.greaterThanOrEqual=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByUserIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where userId is less than or equal to DEFAULT_USER_ID
        defaultSkillAppliedShouldBeFound("userId.lessThanOrEqual=" + DEFAULT_USER_ID);

        // Get all the skillAppliedList where userId is less than or equal to SMALLER_USER_ID
        defaultSkillAppliedShouldNotBeFound("userId.lessThanOrEqual=" + SMALLER_USER_ID);
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByUserIdIsLessThanSomething() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where userId is less than DEFAULT_USER_ID
        defaultSkillAppliedShouldNotBeFound("userId.lessThan=" + DEFAULT_USER_ID);

        // Get all the skillAppliedList where userId is less than UPDATED_USER_ID
        defaultSkillAppliedShouldBeFound("userId.lessThan=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByUserIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where userId is greater than DEFAULT_USER_ID
        defaultSkillAppliedShouldNotBeFound("userId.greaterThan=" + DEFAULT_USER_ID);

        // Get all the skillAppliedList where userId is greater than SMALLER_USER_ID
        defaultSkillAppliedShouldBeFound("userId.greaterThan=" + SMALLER_USER_ID);
    }


    @Test
    @Transactional
    public void getAllSkillAppliedsByUsedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where usedAt equals to DEFAULT_USED_AT
        defaultSkillAppliedShouldBeFound("usedAt.equals=" + DEFAULT_USED_AT);

        // Get all the skillAppliedList where usedAt equals to UPDATED_USED_AT
        defaultSkillAppliedShouldNotBeFound("usedAt.equals=" + UPDATED_USED_AT);
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByUsedAtIsNotEqualToSomething() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where usedAt not equals to DEFAULT_USED_AT
        defaultSkillAppliedShouldNotBeFound("usedAt.notEquals=" + DEFAULT_USED_AT);

        // Get all the skillAppliedList where usedAt not equals to UPDATED_USED_AT
        defaultSkillAppliedShouldBeFound("usedAt.notEquals=" + UPDATED_USED_AT);
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByUsedAtIsInShouldWork() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where usedAt in DEFAULT_USED_AT or UPDATED_USED_AT
        defaultSkillAppliedShouldBeFound("usedAt.in=" + DEFAULT_USED_AT + "," + UPDATED_USED_AT);

        // Get all the skillAppliedList where usedAt equals to UPDATED_USED_AT
        defaultSkillAppliedShouldNotBeFound("usedAt.in=" + UPDATED_USED_AT);
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByUsedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where usedAt is not null
        defaultSkillAppliedShouldBeFound("usedAt.specified=true");

        // Get all the skillAppliedList where usedAt is null
        defaultSkillAppliedShouldNotBeFound("usedAt.specified=false");
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByUsedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where usedAt is greater than or equal to DEFAULT_USED_AT
        defaultSkillAppliedShouldBeFound("usedAt.greaterThanOrEqual=" + DEFAULT_USED_AT);

        // Get all the skillAppliedList where usedAt is greater than or equal to UPDATED_USED_AT
        defaultSkillAppliedShouldNotBeFound("usedAt.greaterThanOrEqual=" + UPDATED_USED_AT);
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByUsedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where usedAt is less than or equal to DEFAULT_USED_AT
        defaultSkillAppliedShouldBeFound("usedAt.lessThanOrEqual=" + DEFAULT_USED_AT);

        // Get all the skillAppliedList where usedAt is less than or equal to SMALLER_USED_AT
        defaultSkillAppliedShouldNotBeFound("usedAt.lessThanOrEqual=" + SMALLER_USED_AT);
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByUsedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where usedAt is less than DEFAULT_USED_AT
        defaultSkillAppliedShouldNotBeFound("usedAt.lessThan=" + DEFAULT_USED_AT);

        // Get all the skillAppliedList where usedAt is less than UPDATED_USED_AT
        defaultSkillAppliedShouldBeFound("usedAt.lessThan=" + UPDATED_USED_AT);
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByUsedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where usedAt is greater than DEFAULT_USED_AT
        defaultSkillAppliedShouldNotBeFound("usedAt.greaterThan=" + DEFAULT_USED_AT);

        // Get all the skillAppliedList where usedAt is greater than SMALLER_USED_AT
        defaultSkillAppliedShouldBeFound("usedAt.greaterThan=" + SMALLER_USED_AT);
    }


    @Test
    @Transactional
    public void getAllSkillAppliedsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where description equals to DEFAULT_DESCRIPTION
        defaultSkillAppliedShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the skillAppliedList where description equals to UPDATED_DESCRIPTION
        defaultSkillAppliedShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where description not equals to DEFAULT_DESCRIPTION
        defaultSkillAppliedShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the skillAppliedList where description not equals to UPDATED_DESCRIPTION
        defaultSkillAppliedShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultSkillAppliedShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the skillAppliedList where description equals to UPDATED_DESCRIPTION
        defaultSkillAppliedShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where description is not null
        defaultSkillAppliedShouldBeFound("description.specified=true");

        // Get all the skillAppliedList where description is null
        defaultSkillAppliedShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllSkillAppliedsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where description contains DEFAULT_DESCRIPTION
        defaultSkillAppliedShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the skillAppliedList where description contains UPDATED_DESCRIPTION
        defaultSkillAppliedShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllSkillAppliedsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        skillAppliedRepository.saveAndFlush(skillApplied);

        // Get all the skillAppliedList where description does not contain DEFAULT_DESCRIPTION
        defaultSkillAppliedShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the skillAppliedList where description does not contain UPDATED_DESCRIPTION
        defaultSkillAppliedShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllSkillAppliedsBySkillIsEqualToSomething() throws Exception {
        // Get already existing entity
        Skill skill = skillApplied.getSkill();
        skillAppliedRepository.saveAndFlush(skillApplied);
        Long skillId = skill.getId();

        // Get all the skillAppliedList where skill equals to skillId
        defaultSkillAppliedShouldBeFound("skillId.equals=" + skillId);

        // Get all the skillAppliedList where skill equals to skillId + 1
        defaultSkillAppliedShouldNotBeFound("skillId.equals=" + (skillId + 1));
    }


    @Test
    @Transactional
    public void getAllSkillAppliedsByProjectIsEqualToSomething() throws Exception {
        // Get already existing entity
        Project project = skillApplied.getProject();
        skillAppliedRepository.saveAndFlush(skillApplied);
        Long projectId = project.getId();

        // Get all the skillAppliedList where project equals to projectId
        defaultSkillAppliedShouldBeFound("projectId.equals=" + projectId);

        // Get all the skillAppliedList where project equals to projectId + 1
        defaultSkillAppliedShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSkillAppliedShouldBeFound(String filter) throws Exception {
        restSkillAppliedMockMvc.perform(get("/api/skill-applieds?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(skillApplied.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].usedAt").value(hasItem(DEFAULT_USED_AT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restSkillAppliedMockMvc.perform(get("/api/skill-applieds/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSkillAppliedShouldNotBeFound(String filter) throws Exception {
        restSkillAppliedMockMvc.perform(get("/api/skill-applieds?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSkillAppliedMockMvc.perform(get("/api/skill-applieds/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingSkillApplied() throws Exception {
        // Get the skillApplied
        restSkillAppliedMockMvc.perform(get("/api/skill-applieds/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSkillApplied() throws Exception {
        // Initialize the database
        skillAppliedService.save(skillApplied);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockSkillAppliedSearchRepository);

        int databaseSizeBeforeUpdate = skillAppliedRepository.findAll().size();

        // Update the skillApplied
        SkillApplied updatedSkillApplied = skillAppliedRepository.findById(skillApplied.getId()).get();
        // Disconnect from session so that the updates on updatedSkillApplied are not directly saved in db
        em.detach(updatedSkillApplied);
        updatedSkillApplied
            .userId(UPDATED_USER_ID)
            .usedAt(UPDATED_USED_AT)
            .description(UPDATED_DESCRIPTION);

        restSkillAppliedMockMvc.perform(put("/api/skill-applieds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSkillApplied)))
            .andExpect(status().isOk());

        // Validate the SkillApplied in the database
        List<SkillApplied> skillAppliedList = skillAppliedRepository.findAll();
        assertThat(skillAppliedList).hasSize(databaseSizeBeforeUpdate);
        SkillApplied testSkillApplied = skillAppliedList.get(skillAppliedList.size() - 1);
        assertThat(testSkillApplied.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testSkillApplied.getUsedAt()).isEqualTo(UPDATED_USED_AT);
        assertThat(testSkillApplied.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the SkillApplied in Elasticsearch
        verify(mockSkillAppliedSearchRepository, times(1)).save(testSkillApplied);
    }

    @Test
    @Transactional
    public void updateNonExistingSkillApplied() throws Exception {
        int databaseSizeBeforeUpdate = skillAppliedRepository.findAll().size();

        // Create the SkillApplied

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSkillAppliedMockMvc.perform(put("/api/skill-applieds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(skillApplied)))
            .andExpect(status().isBadRequest());

        // Validate the SkillApplied in the database
        List<SkillApplied> skillAppliedList = skillAppliedRepository.findAll();
        assertThat(skillAppliedList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SkillApplied in Elasticsearch
        verify(mockSkillAppliedSearchRepository, times(0)).save(skillApplied);
    }

    @Test
    @Transactional
    public void deleteSkillApplied() throws Exception {
        // Initialize the database
        skillAppliedService.save(skillApplied);

        int databaseSizeBeforeDelete = skillAppliedRepository.findAll().size();

        // Delete the skillApplied
        restSkillAppliedMockMvc.perform(delete("/api/skill-applieds/{id}", skillApplied.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SkillApplied> skillAppliedList = skillAppliedRepository.findAll();
        assertThat(skillAppliedList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the SkillApplied in Elasticsearch
        verify(mockSkillAppliedSearchRepository, times(1)).deleteById(skillApplied.getId());
    }

    @Test
    @Transactional
    public void searchSkillApplied() throws Exception {
        // Initialize the database
        skillAppliedService.save(skillApplied);
        when(mockSkillAppliedSearchRepository.search(queryStringQuery("id:" + skillApplied.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(skillApplied), PageRequest.of(0, 1), 1));
        // Search the skillApplied
        restSkillAppliedMockMvc.perform(get("/api/_search/skill-applieds?query=id:" + skillApplied.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(skillApplied.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].usedAt").value(hasItem(DEFAULT_USED_AT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SkillApplied.class);
        SkillApplied skillApplied1 = new SkillApplied();
        skillApplied1.setId(1L);
        SkillApplied skillApplied2 = new SkillApplied();
        skillApplied2.setId(skillApplied1.getId());
        assertThat(skillApplied1).isEqualTo(skillApplied2);
        skillApplied2.setId(2L);
        assertThat(skillApplied1).isNotEqualTo(skillApplied2);
        skillApplied1.setId(null);
        assertThat(skillApplied1).isNotEqualTo(skillApplied2);
    }
}
