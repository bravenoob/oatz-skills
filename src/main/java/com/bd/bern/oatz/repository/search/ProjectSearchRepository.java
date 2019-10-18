package com.bd.bern.oatz.repository.search;
import com.bd.bern.oatz.domain.Project;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Project} entity.
 */
public interface ProjectSearchRepository extends ElasticsearchRepository<Project, Long> {
}
