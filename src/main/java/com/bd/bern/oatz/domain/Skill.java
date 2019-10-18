package com.bd.bern.oatz.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Skill.
 */
@Entity
@Table(name = "skill")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "skill")
public class Skill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "skill_name", nullable = false)
    private String skillName;

    @OneToMany(mappedBy = "skill")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<SkillApplied> appliedSkills = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkillName() {
        return skillName;
    }

    public Skill skillName(String skillName) {
        this.skillName = skillName;
        return this;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Set<SkillApplied> getAppliedSkills() {
        return appliedSkills;
    }

    public Skill appliedSkills(Set<SkillApplied> skillApplieds) {
        this.appliedSkills = skillApplieds;
        return this;
    }

    public Skill addAppliedSkills(SkillApplied skillApplied) {
        this.appliedSkills.add(skillApplied);
        skillApplied.setSkill(this);
        return this;
    }

    public Skill removeAppliedSkills(SkillApplied skillApplied) {
        this.appliedSkills.remove(skillApplied);
        skillApplied.setSkill(null);
        return this;
    }

    public void setAppliedSkills(Set<SkillApplied> skillApplieds) {
        this.appliedSkills = skillApplieds;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Skill)) {
            return false;
        }
        return id != null && id.equals(((Skill) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Skill{" +
            "id=" + getId() +
            ", skillName='" + getSkillName() + "'" +
            "}";
    }
}
