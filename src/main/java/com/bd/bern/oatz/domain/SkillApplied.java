package com.bd.bern.oatz.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A SkillApplied.
 */
@Entity
@Table(name = "skill_applied")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "skillapplied")
public class SkillApplied implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @NotNull
    @Column(name = "used_at", nullable = false)
    private LocalDate usedAt;

    @Column(name = "description")
    private String description;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("appliedSkills")
    private Skill skill;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("appliedSkills")
    private Project project;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public SkillApplied userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getUsedAt() {
        return usedAt;
    }

    public SkillApplied usedAt(LocalDate usedAt) {
        this.usedAt = usedAt;
        return this;
    }

    public void setUsedAt(LocalDate usedAt) {
        this.usedAt = usedAt;
    }

    public String getDescription() {
        return description;
    }

    public SkillApplied description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Skill getSkill() {
        return skill;
    }

    public SkillApplied skill(Skill skill) {
        this.skill = skill;
        return this;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Project getProject() {
        return project;
    }

    public SkillApplied project(Project project) {
        this.project = project;
        return this;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SkillApplied)) {
            return false;
        }
        return id != null && id.equals(((SkillApplied) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "SkillApplied{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", usedAt='" + getUsedAt() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
