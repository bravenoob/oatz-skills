package com.bd.bern.oatz.service.mapper;

import com.bd.bern.oatz.domain.*;
import com.bd.bern.oatz.service.dto.ProjectDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Project} and its DTO {@link ProjectDTO}.
 */
@Mapper(componentModel = "spring", uses = {EnterpriseMapper.class})
public interface ProjectMapper extends EntityMapper<ProjectDTO, Project> {

    @Mapping(source = "enterprise.id", target = "enterpriseId")
    ProjectDTO toDto(Project project);

    @Mapping(source = "enterpriseId", target = "enterprise")
    Project toEntity(ProjectDTO projectDTO);

    default Project fromId(Long id) {
        if (id == null) {
            return null;
        }
        Project project = new Project();
        project.setId(id);
        return project;
    }
}
