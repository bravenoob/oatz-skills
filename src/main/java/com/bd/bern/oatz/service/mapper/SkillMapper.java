package com.bd.bern.oatz.service.mapper;

import com.bd.bern.oatz.domain.*;
import com.bd.bern.oatz.service.dto.SkillDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Skill} and its DTO {@link SkillDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SkillMapper extends EntityMapper<SkillDTO, Skill> {



    default Skill fromId(Long id) {
        if (id == null) {
            return null;
        }
        Skill skill = new Skill();
        skill.setId(id);
        return skill;
    }
}
