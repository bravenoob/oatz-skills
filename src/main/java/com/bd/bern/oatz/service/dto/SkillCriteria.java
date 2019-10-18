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

/**
 * Criteria class for the {@link com.bd.bern.oatz.domain.Skill} entity. This class is used
 * in {@link com.bd.bern.oatz.web.rest.SkillResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /skills?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class SkillCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter skillName;

    private LongFilter appliedSkillsId;

    public SkillCriteria(){
    }

    public SkillCriteria(SkillCriteria other){
        this.id = other.id == null ? null : other.id.copy();
        this.skillName = other.skillName == null ? null : other.skillName.copy();
        this.appliedSkillsId = other.appliedSkillsId == null ? null : other.appliedSkillsId.copy();
    }

    @Override
    public SkillCriteria copy() {
        return new SkillCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getSkillName() {
        return skillName;
    }

    public void setSkillName(StringFilter skillName) {
        this.skillName = skillName;
    }

    public LongFilter getAppliedSkillsId() {
        return appliedSkillsId;
    }

    public void setAppliedSkillsId(LongFilter appliedSkillsId) {
        this.appliedSkillsId = appliedSkillsId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SkillCriteria that = (SkillCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(skillName, that.skillName) &&
            Objects.equals(appliedSkillsId, that.appliedSkillsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        skillName,
        appliedSkillsId
        );
    }

    @Override
    public String toString() {
        return "SkillCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (skillName != null ? "skillName=" + skillName + ", " : "") +
                (appliedSkillsId != null ? "appliedSkillsId=" + appliedSkillsId + ", " : "") +
            "}";
    }

}
