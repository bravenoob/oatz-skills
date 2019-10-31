package com.bd.bern.oatz.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.LocalDateFilter;

/**
 * Criteria class for the {@link com.bd.bern.oatz.domain.SkillApplied} entity. This class is used
 * in {@link com.bd.bern.oatz.web.rest.SkillAppliedResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /skill-applieds?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class SkillAppliedCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter userId;

    private LocalDateFilter usedAt;

    private StringFilter description;

    private LongFilter projectId;

    private LongFilter skillId;

    public SkillAppliedCriteria(){
    }

    public SkillAppliedCriteria(SkillAppliedCriteria other){
        this.id = other.id == null ? null : other.id.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.usedAt = other.usedAt == null ? null : other.usedAt.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.projectId = other.projectId == null ? null : other.projectId.copy();
        this.skillId = other.skillId == null ? null : other.skillId.copy();
    }

    @Override
    public SkillAppliedCriteria copy() {
        return new SkillAppliedCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LocalDateFilter getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateFilter usedAt) {
        this.usedAt = usedAt;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public LongFilter getProjectId() {
        return projectId;
    }

    public void setProjectId(LongFilter projectId) {
        this.projectId = projectId;
    }

    public LongFilter getSkillId() {
        return skillId;
    }

    public void setSkillId(LongFilter skillId) {
        this.skillId = skillId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SkillAppliedCriteria that = (SkillAppliedCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(usedAt, that.usedAt) &&
            Objects.equals(description, that.description) &&
            Objects.equals(projectId, that.projectId) &&
            Objects.equals(skillId, that.skillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        userId,
        usedAt,
        description,
        projectId,
        skillId
        );
    }

    @Override
    public String toString() {
        return "SkillAppliedCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
                (usedAt != null ? "usedAt=" + usedAt + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (projectId != null ? "projectId=" + projectId + ", " : "") +
                (skillId != null ? "skillId=" + skillId + ", " : "") +
            "}";
    }

}
