package com.bd.bern.oatz.service.mapper;

import com.bd.bern.oatz.domain.*;
import com.bd.bern.oatz.service.dto.SkillAppliedDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link SkillApplied} and its DTO {@link SkillAppliedDTO}.
 */
@Mapper(componentModel = "spring", uses = {ProjectMapper.class, SkillMapper.class})
public interface SkillAppliedMapper extends EntityMapper<SkillAppliedDTO, SkillApplied> {

    @Mapping(source = "project.id", target = "projectId")
    SkillAppliedDTO toDto(SkillApplied skillApplied);

    @Mapping(source = "projectId", target = "project")
    @Mapping(target = "removeSkill", ignore = true)
    SkillApplied toEntity(SkillAppliedDTO skillAppliedDTO);

    default SkillApplied fromId(Long id) {
        if (id == null) {
            return null;
        }
        SkillApplied skillApplied = new SkillApplied();
        skillApplied.setId(id);
        return skillApplied;
    }
}
