package com.bd.bern.oatz.service.dto;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.bd.bern.oatz.domain.Skill} entity.
 */
public class SkillDTO implements Serializable {

    private Long id;

    @NotNull
    private String skillName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SkillDTO skillDTO = (SkillDTO) o;
        if (skillDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), skillDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SkillDTO{" +
            "id=" + getId() +
            ", skillName='" + getSkillName() + "'" +
            "}";
    }
}
