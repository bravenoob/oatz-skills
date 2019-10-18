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
 * Criteria class for the {@link com.bd.bern.oatz.domain.Enterprise} entity. This class is used
 * in {@link com.bd.bern.oatz.web.rest.EnterpriseResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /enterprises?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class EnterpriseCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private LongFilter projectsId;

    public EnterpriseCriteria(){
    }

    public EnterpriseCriteria(EnterpriseCriteria other){
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.projectsId = other.projectsId == null ? null : other.projectsId.copy();
    }

    @Override
    public EnterpriseCriteria copy() {
        return new EnterpriseCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitle() {
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public LongFilter getProjectsId() {
        return projectsId;
    }

    public void setProjectsId(LongFilter projectsId) {
        this.projectsId = projectsId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EnterpriseCriteria that = (EnterpriseCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(projectsId, that.projectsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        title,
        projectsId
        );
    }

    @Override
    public String toString() {
        return "EnterpriseCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (title != null ? "title=" + title + ", " : "") +
                (projectsId != null ? "projectsId=" + projectsId + ", " : "") +
            "}";
    }

}
