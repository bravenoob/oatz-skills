package com.bd.bern.oatz.web.rest;

import com.bd.bern.oatz.OatzSkillApp;
import com.bd.bern.oatz.domain.Project;
import com.bd.bern.oatz.domain.Enterprise;
import com.bd.bern.oatz.domain.SkillApplied;
import com.bd.bern.oatz.repository.ProjectRepository;
import com.bd.bern.oatz.repository.search.ProjectSearchRepository;
import com.bd.bern.oatz.service.ProjectService;
import com.bd.bern.oatz.web.rest.errors.ExceptionTranslator;
import com.bd.bern.oatz.service.dto.ProjectCriteria;
import com.bd.bern.oatz.service.ProjectQueryService;

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

import com.bd.bern.oatz.domain.enumeration.Type;
/**
 * Integration tests for the {@link ProjectResource} REST controller.
 */
@SpringBootTest(classes = OatzSkillApp.class)
public class ProjectResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Type DEFAULT_TYPE = Type.PROJECT;
    private static final Type UPDATED_TYPE = Type.COURSE;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    /**
     * This repository is mocked in the com.bd.bern.oatz.repository.search test package.
     *
     * @see com.bd.bern.oatz.repository.search.ProjectSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProjectSearchRepository mockProjectSearchRepository;

    @Autowired
    private ProjectQueryService projectQueryService;

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

    private MockMvc restProjectMockMvc;

    private Project project;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ProjectResource projectResource = new ProjectResource(projectService, projectQueryService);
        this.restProjectMockMvc = MockMvcBuilders.standaloneSetup(projectResource)
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
    public static Project createEntity(EntityManager em) {
        Project project = new Project()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .type(DEFAULT_TYPE);
        // Add required entity
        Enterprise enterprise;
        if (TestUtil.findAll(em, Enterprise.class).isEmpty()) {
            enterprise = EnterpriseResourceIT.createEntity(em);
            em.persist(enterprise);
            em.flush();
        } else {
            enterprise = TestUtil.findAll(em, Enterprise.class).get(0);
        }
        project.setEnterprise(enterprise);
        return project;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Project createUpdatedEntity(EntityManager em) {
        Project project = new Project()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE);
        // Add required entity
        Enterprise enterprise;
        if (TestUtil.findAll(em, Enterprise.class).isEmpty()) {
            enterprise = EnterpriseResourceIT.createUpdatedEntity(em);
            em.persist(enterprise);
            em.flush();
        } else {
            enterprise = TestUtil.findAll(em, Enterprise.class).get(0);
        }
        project.setEnterprise(enterprise);
        return project;
    }

    @BeforeEach
    public void initTest() {
        project = createEntity(em);
    }

    @Test
    @Transactional
    public void createProject() throws Exception {
        int databaseSizeBeforeCreate = projectRepository.findAll().size();

        // Create the Project
        restProjectMockMvc.perform(post("/api/projects")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(project)))
            .andExpect(status().isCreated());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeCreate + 1);
        Project testProject = projectList.get(projectList.size() - 1);
        assertThat(testProject.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testProject.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProject.getType()).isEqualTo(DEFAULT_TYPE);

        // Validate the Project in Elasticsearch
        verify(mockProjectSearchRepository, times(1)).save(testProject);
    }

    @Test
    @Transactional
    public void createProjectWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = projectRepository.findAll().size();

        // Create the Project with an existing ID
        project.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectMockMvc.perform(post("/api/projects")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(project)))
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeCreate);

        // Validate the Project in Elasticsearch
        verify(mockProjectSearchRepository, times(0)).save(project);
    }


    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectRepository.findAll().size();
        // set the field null
        project.setTitle(null);

        // Create the Project, which fails.

        restProjectMockMvc.perform(post("/api/projects")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(project)))
            .andExpect(status().isBadRequest());

        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectRepository.findAll().size();
        // set the field null
        project.setType(null);

        // Create the Project, which fails.

        restProjectMockMvc.perform(post("/api/projects")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(project)))
            .andExpect(status().isBadRequest());

        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProjects() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList
        restProjectMockMvc.perform(get("/api/projects?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(project.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
    
    @Test
    @Transactional
    public void getProject() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get the project
        restProjectMockMvc.perform(get("/api/projects/{id}", project.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(project.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getAllProjectsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where title equals to DEFAULT_TITLE
        defaultProjectShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the projectList where title equals to UPDATED_TITLE
        defaultProjectShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllProjectsByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where title not equals to DEFAULT_TITLE
        defaultProjectShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the projectList where title not equals to UPDATED_TITLE
        defaultProjectShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllProjectsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultProjectShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the projectList where title equals to UPDATED_TITLE
        defaultProjectShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllProjectsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where title is not null
        defaultProjectShouldBeFound("title.specified=true");

        // Get all the projectList where title is null
        defaultProjectShouldNotBeFound("title.specified=false");
    }
                @Test
    @Transactional
    public void getAllProjectsByTitleContainsSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where title contains DEFAULT_TITLE
        defaultProjectShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the projectList where title contains UPDATED_TITLE
        defaultProjectShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllProjectsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where title does not contain DEFAULT_TITLE
        defaultProjectShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the projectList where title does not contain UPDATED_TITLE
        defaultProjectShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }


    @Test
    @Transactional
    public void getAllProjectsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where description equals to DEFAULT_DESCRIPTION
        defaultProjectShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the projectList where description equals to UPDATED_DESCRIPTION
        defaultProjectShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllProjectsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where description not equals to DEFAULT_DESCRIPTION
        defaultProjectShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the projectList where description not equals to UPDATED_DESCRIPTION
        defaultProjectShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllProjectsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultProjectShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the projectList where description equals to UPDATED_DESCRIPTION
        defaultProjectShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllProjectsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where description is not null
        defaultProjectShouldBeFound("description.specified=true");

        // Get all the projectList where description is null
        defaultProjectShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllProjectsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where description contains DEFAULT_DESCRIPTION
        defaultProjectShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the projectList where description contains UPDATED_DESCRIPTION
        defaultProjectShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllProjectsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where description does not contain DEFAULT_DESCRIPTION
        defaultProjectShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the projectList where description does not contain UPDATED_DESCRIPTION
        defaultProjectShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllProjectsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where type equals to DEFAULT_TYPE
        defaultProjectShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the projectList where type equals to UPDATED_TYPE
        defaultProjectShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllProjectsByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where type not equals to DEFAULT_TYPE
        defaultProjectShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the projectList where type not equals to UPDATED_TYPE
        defaultProjectShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllProjectsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultProjectShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the projectList where type equals to UPDATED_TYPE
        defaultProjectShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllProjectsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where type is not null
        defaultProjectShouldBeFound("type.specified=true");

        // Get all the projectList where type is null
        defaultProjectShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllProjectsByEnterpriseIsEqualToSomething() throws Exception {
        // Get already existing entity
        Enterprise enterprise = project.getEnterprise();
        projectRepository.saveAndFlush(project);
        Long enterpriseId = enterprise.getId();

        // Get all the projectList where enterprise equals to enterpriseId
        defaultProjectShouldBeFound("enterpriseId.equals=" + enterpriseId);

        // Get all the projectList where enterprise equals to enterpriseId + 1
        defaultProjectShouldNotBeFound("enterpriseId.equals=" + (enterpriseId + 1));
    }


    @Test
    @Transactional
    public void getAllProjectsByAppliedSkillsIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);
        SkillApplied appliedSkills = SkillAppliedResourceIT.createEntity(em);
        em.persist(appliedSkills);
        em.flush();
        project.addAppliedSkills(appliedSkills);
        projectRepository.saveAndFlush(project);
        Long appliedSkillsId = appliedSkills.getId();

        // Get all the projectList where appliedSkills equals to appliedSkillsId
        defaultProjectShouldBeFound("appliedSkillsId.equals=" + appliedSkillsId);

        // Get all the projectList where appliedSkills equals to appliedSkillsId + 1
        defaultProjectShouldNotBeFound("appliedSkillsId.equals=" + (appliedSkillsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProjectShouldBeFound(String filter) throws Exception {
        restProjectMockMvc.perform(get("/api/projects?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(project.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));

        // Check, that the count call also returns 1
        restProjectMockMvc.perform(get("/api/projects/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProjectShouldNotBeFound(String filter) throws Exception {
        restProjectMockMvc.perform(get("/api/projects?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProjectMockMvc.perform(get("/api/projects/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingProject() throws Exception {
        // Get the project
        restProjectMockMvc.perform(get("/api/projects/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProject() throws Exception {
        // Initialize the database
        projectService.save(project);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockProjectSearchRepository);

        int databaseSizeBeforeUpdate = projectRepository.findAll().size();

        // Update the project
        Project updatedProject = projectRepository.findById(project.getId()).get();
        // Disconnect from session so that the updates on updatedProject are not directly saved in db
        em.detach(updatedProject);
        updatedProject
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE);

        restProjectMockMvc.perform(put("/api/projects")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedProject)))
            .andExpect(status().isOk());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
        Project testProject = projectList.get(projectList.size() - 1);
        assertThat(testProject.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProject.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProject.getType()).isEqualTo(UPDATED_TYPE);

        // Validate the Project in Elasticsearch
        verify(mockProjectSearchRepository, times(1)).save(testProject);
    }

    @Test
    @Transactional
    public void updateNonExistingProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().size();

        // Create the Project

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMockMvc.perform(put("/api/projects")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(project)))
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Project in Elasticsearch
        verify(mockProjectSearchRepository, times(0)).save(project);
    }

    @Test
    @Transactional
    public void deleteProject() throws Exception {
        // Initialize the database
        projectService.save(project);

        int databaseSizeBeforeDelete = projectRepository.findAll().size();

        // Delete the project
        restProjectMockMvc.perform(delete("/api/projects/{id}", project.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Project in Elasticsearch
        verify(mockProjectSearchRepository, times(1)).deleteById(project.getId());
    }

    @Test
    @Transactional
    public void searchProject() throws Exception {
        // Initialize the database
        projectService.save(project);
        when(mockProjectSearchRepository.search(queryStringQuery("id:" + project.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(project), PageRequest.of(0, 1), 1));
        // Search the project
        restProjectMockMvc.perform(get("/api/_search/projects?query=id:" + project.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(project.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Project.class);
        Project project1 = new Project();
        project1.setId(1L);
        Project project2 = new Project();
        project2.setId(project1.getId());
        assertThat(project1).isEqualTo(project2);
        project2.setId(2L);
        assertThat(project1).isNotEqualTo(project2);
        project1.setId(null);
        assertThat(project1).isNotEqualTo(project2);
    }
}
