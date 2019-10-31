package com.bd.bern.oatz.web.rest;

import com.bd.bern.oatz.OatzSkillApp;
import com.bd.bern.oatz.domain.Skill;
import com.bd.bern.oatz.repository.SkillRepository;
import com.bd.bern.oatz.repository.search.SkillSearchRepository;
import com.bd.bern.oatz.service.SkillService;
import com.bd.bern.oatz.service.dto.SkillDTO;
import com.bd.bern.oatz.service.mapper.SkillMapper;
import com.bd.bern.oatz.web.rest.errors.ExceptionTranslator;
import com.bd.bern.oatz.service.dto.SkillCriteria;
import com.bd.bern.oatz.service.SkillQueryService;

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
 * Integration tests for the {@link SkillResource} REST controller.
 */
@SpringBootTest(classes = OatzSkillApp.class)
public class SkillResourceIT {

    private static final String DEFAULT_SKILL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SKILL_NAME = "BBBBBBBBBB";

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private SkillMapper skillMapper;

    @Autowired
    private SkillService skillService;

    /**
     * This repository is mocked in the com.bd.bern.oatz.repository.search test package.
     *
     * @see com.bd.bern.oatz.repository.search.SkillSearchRepositoryMockConfiguration
     */
    @Autowired
    private SkillSearchRepository mockSkillSearchRepository;

    @Autowired
    private SkillQueryService skillQueryService;

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

    private MockMvc restSkillMockMvc;

    private Skill skill;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SkillResource skillResource = new SkillResource(skillService, skillQueryService);
        this.restSkillMockMvc = MockMvcBuilders.standaloneSetup(skillResource)
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
    public static Skill createEntity(EntityManager em) {
        Skill skill = new Skill()
            .skillName(DEFAULT_SKILL_NAME);
        return skill;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Skill createUpdatedEntity(EntityManager em) {
        Skill skill = new Skill()
            .skillName(UPDATED_SKILL_NAME);
        return skill;
    }

    @BeforeEach
    public void initTest() {
        skill = createEntity(em);
    }

    @Test
    @Transactional
    public void createSkill() throws Exception {
        int databaseSizeBeforeCreate = skillRepository.findAll().size();

        // Create the Skill
        SkillDTO skillDTO = skillMapper.toDto(skill);
        restSkillMockMvc.perform(post("/api/skills")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(skillDTO)))
            .andExpect(status().isCreated());

        // Validate the Skill in the database
        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeCreate + 1);
        Skill testSkill = skillList.get(skillList.size() - 1);
        assertThat(testSkill.getSkillName()).isEqualTo(DEFAULT_SKILL_NAME);

        // Validate the Skill in Elasticsearch
        verify(mockSkillSearchRepository, times(1)).save(testSkill);
    }

    @Test
    @Transactional
    public void createSkillWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = skillRepository.findAll().size();

        // Create the Skill with an existing ID
        skill.setId(1L);
        SkillDTO skillDTO = skillMapper.toDto(skill);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSkillMockMvc.perform(post("/api/skills")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(skillDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Skill in the database
        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeCreate);

        // Validate the Skill in Elasticsearch
        verify(mockSkillSearchRepository, times(0)).save(skill);
    }


    @Test
    @Transactional
    public void checkSkillNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = skillRepository.findAll().size();
        // set the field null
        skill.setSkillName(null);

        // Create the Skill, which fails.
        SkillDTO skillDTO = skillMapper.toDto(skill);

        restSkillMockMvc.perform(post("/api/skills")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(skillDTO)))
            .andExpect(status().isBadRequest());

        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSkills() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        // Get all the skillList
        restSkillMockMvc.perform(get("/api/skills?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(skill.getId().intValue())))
            .andExpect(jsonPath("$.[*].skillName").value(hasItem(DEFAULT_SKILL_NAME)));
    }
    
    @Test
    @Transactional
    public void getSkill() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        // Get the skill
        restSkillMockMvc.perform(get("/api/skills/{id}", skill.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(skill.getId().intValue()))
            .andExpect(jsonPath("$.skillName").value(DEFAULT_SKILL_NAME));
    }

    @Test
    @Transactional
    public void getAllSkillsBySkillNameIsEqualToSomething() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        // Get all the skillList where skillName equals to DEFAULT_SKILL_NAME
        defaultSkillShouldBeFound("skillName.equals=" + DEFAULT_SKILL_NAME);

        // Get all the skillList where skillName equals to UPDATED_SKILL_NAME
        defaultSkillShouldNotBeFound("skillName.equals=" + UPDATED_SKILL_NAME);
    }

    @Test
    @Transactional
    public void getAllSkillsBySkillNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        // Get all the skillList where skillName not equals to DEFAULT_SKILL_NAME
        defaultSkillShouldNotBeFound("skillName.notEquals=" + DEFAULT_SKILL_NAME);

        // Get all the skillList where skillName not equals to UPDATED_SKILL_NAME
        defaultSkillShouldBeFound("skillName.notEquals=" + UPDATED_SKILL_NAME);
    }

    @Test
    @Transactional
    public void getAllSkillsBySkillNameIsInShouldWork() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        // Get all the skillList where skillName in DEFAULT_SKILL_NAME or UPDATED_SKILL_NAME
        defaultSkillShouldBeFound("skillName.in=" + DEFAULT_SKILL_NAME + "," + UPDATED_SKILL_NAME);

        // Get all the skillList where skillName equals to UPDATED_SKILL_NAME
        defaultSkillShouldNotBeFound("skillName.in=" + UPDATED_SKILL_NAME);
    }

    @Test
    @Transactional
    public void getAllSkillsBySkillNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        // Get all the skillList where skillName is not null
        defaultSkillShouldBeFound("skillName.specified=true");

        // Get all the skillList where skillName is null
        defaultSkillShouldNotBeFound("skillName.specified=false");
    }
                @Test
    @Transactional
    public void getAllSkillsBySkillNameContainsSomething() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        // Get all the skillList where skillName contains DEFAULT_SKILL_NAME
        defaultSkillShouldBeFound("skillName.contains=" + DEFAULT_SKILL_NAME);

        // Get all the skillList where skillName contains UPDATED_SKILL_NAME
        defaultSkillShouldNotBeFound("skillName.contains=" + UPDATED_SKILL_NAME);
    }

    @Test
    @Transactional
    public void getAllSkillsBySkillNameNotContainsSomething() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        // Get all the skillList where skillName does not contain DEFAULT_SKILL_NAME
        defaultSkillShouldNotBeFound("skillName.doesNotContain=" + DEFAULT_SKILL_NAME);

        // Get all the skillList where skillName does not contain UPDATED_SKILL_NAME
        defaultSkillShouldBeFound("skillName.doesNotContain=" + UPDATED_SKILL_NAME);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSkillShouldBeFound(String filter) throws Exception {
        restSkillMockMvc.perform(get("/api/skills?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(skill.getId().intValue())))
            .andExpect(jsonPath("$.[*].skillName").value(hasItem(DEFAULT_SKILL_NAME)));

        // Check, that the count call also returns 1
        restSkillMockMvc.perform(get("/api/skills/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSkillShouldNotBeFound(String filter) throws Exception {
        restSkillMockMvc.perform(get("/api/skills?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSkillMockMvc.perform(get("/api/skills/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingSkill() throws Exception {
        // Get the skill
        restSkillMockMvc.perform(get("/api/skills/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSkill() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        int databaseSizeBeforeUpdate = skillRepository.findAll().size();

        // Update the skill
        Skill updatedSkill = skillRepository.findById(skill.getId()).get();
        // Disconnect from session so that the updates on updatedSkill are not directly saved in db
        em.detach(updatedSkill);
        updatedSkill
            .skillName(UPDATED_SKILL_NAME);
        SkillDTO skillDTO = skillMapper.toDto(updatedSkill);

        restSkillMockMvc.perform(put("/api/skills")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(skillDTO)))
            .andExpect(status().isOk());

        // Validate the Skill in the database
        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeUpdate);
        Skill testSkill = skillList.get(skillList.size() - 1);
        assertThat(testSkill.getSkillName()).isEqualTo(UPDATED_SKILL_NAME);

        // Validate the Skill in Elasticsearch
        verify(mockSkillSearchRepository, times(1)).save(testSkill);
    }

    @Test
    @Transactional
    public void updateNonExistingSkill() throws Exception {
        int databaseSizeBeforeUpdate = skillRepository.findAll().size();

        // Create the Skill
        SkillDTO skillDTO = skillMapper.toDto(skill);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSkillMockMvc.perform(put("/api/skills")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(skillDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Skill in the database
        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Skill in Elasticsearch
        verify(mockSkillSearchRepository, times(0)).save(skill);
    }

    @Test
    @Transactional
    public void deleteSkill() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        int databaseSizeBeforeDelete = skillRepository.findAll().size();

        // Delete the skill
        restSkillMockMvc.perform(delete("/api/skills/{id}", skill.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Skill in Elasticsearch
        verify(mockSkillSearchRepository, times(1)).deleteById(skill.getId());
    }

    @Test
    @Transactional
    public void searchSkill() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);
        when(mockSkillSearchRepository.search(queryStringQuery("id:" + skill.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(skill), PageRequest.of(0, 1), 1));
        // Search the skill
        restSkillMockMvc.perform(get("/api/_search/skills?query=id:" + skill.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(skill.getId().intValue())))
            .andExpect(jsonPath("$.[*].skillName").value(hasItem(DEFAULT_SKILL_NAME)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Skill.class);
        Skill skill1 = new Skill();
        skill1.setId(1L);
        Skill skill2 = new Skill();
        skill2.setId(skill1.getId());
        assertThat(skill1).isEqualTo(skill2);
        skill2.setId(2L);
        assertThat(skill1).isNotEqualTo(skill2);
        skill1.setId(null);
        assertThat(skill1).isNotEqualTo(skill2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SkillDTO.class);
        SkillDTO skillDTO1 = new SkillDTO();
        skillDTO1.setId(1L);
        SkillDTO skillDTO2 = new SkillDTO();
        assertThat(skillDTO1).isNotEqualTo(skillDTO2);
        skillDTO2.setId(skillDTO1.getId());
        assertThat(skillDTO1).isEqualTo(skillDTO2);
        skillDTO2.setId(2L);
        assertThat(skillDTO1).isNotEqualTo(skillDTO2);
        skillDTO1.setId(null);
        assertThat(skillDTO1).isNotEqualTo(skillDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(skillMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(skillMapper.fromId(null)).isNull();
    }
}
