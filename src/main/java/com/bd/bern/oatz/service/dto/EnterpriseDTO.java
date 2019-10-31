package com.bd.bern.oatz.service.dto;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.bd.bern.oatz.domain.Enterprise} entity.
 */
public class EnterpriseDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EnterpriseDTO enterpriseDTO = (EnterpriseDTO) o;
        if (enterpriseDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), enterpriseDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EnterpriseDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            "}";
    }
}
