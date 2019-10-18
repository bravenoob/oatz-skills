package com.bd.bern.oatz.repository.search;
import com.bd.bern.oatz.domain.SkillApplied;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link SkillApplied} entity.
 */
public interface SkillAppliedSearchRepository extends ElasticsearchRepository<SkillApplied, Long> {
}
