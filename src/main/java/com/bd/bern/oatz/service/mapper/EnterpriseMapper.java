package com.bd.bern.oatz.service.mapper;

import com.bd.bern.oatz.domain.*;
import com.bd.bern.oatz.service.dto.EnterpriseDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Enterprise} and its DTO {@link EnterpriseDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface EnterpriseMapper extends EntityMapper<EnterpriseDTO, Enterprise> {


    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "removeProjects", ignore = true)
    Enterprise toEntity(EnterpriseDTO enterpriseDTO);

    default Enterprise fromId(Long id) {
        if (id == null) {
            return null;
        }
        Enterprise enterprise = new Enterprise();
        enterprise.setId(id);
        return enterprise;
    }
}
