package com.bd.bern.oatz.repository.search;
import com.bd.bern.oatz.domain.Skill;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Skill} entity.
 */
public interface SkillSearchRepository extends ElasticsearchRepository<Skill, Long> {
}
