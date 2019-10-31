package com.bd.bern.oatz.service.dto;
import java.time.LocalDate;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the {@link com.bd.bern.oatz.domain.SkillApplied} entity.
 */
public class SkillAppliedDTO implements Serializable {

    private Long id;

    private Long userId;

    @NotNull
    private LocalDate usedAt;

    private String description;


    private Long projectId;

    private Set<SkillDTO> skills = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDate usedAt) {
        this.usedAt = usedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Set<SkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(Set<SkillDTO> skills) {
        this.skills = skills;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SkillAppliedDTO skillAppliedDTO = (SkillAppliedDTO) o;
        if (skillAppliedDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), skillAppliedDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SkillAppliedDTO{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", usedAt='" + getUsedAt() + "'" +
            ", description='" + getDescription() + "'" +
            ", project=" + getProjectId() +
            "}";
    }
}
