package com.bd.bern.oatz.repository.search;
import com.bd.bern.oatz.domain.Enterprise;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Enterprise} entity.
 */
public interface EnterpriseSearchRepository extends ElasticsearchRepository<Enterprise, Long> {
}
