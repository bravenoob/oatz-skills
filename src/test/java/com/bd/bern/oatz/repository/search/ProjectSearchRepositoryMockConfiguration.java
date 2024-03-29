package com.bd.bern.oatz.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link ProjectSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ProjectSearchRepositoryMockConfiguration {

    @MockBean
    private ProjectSearchRepository mockProjectSearchRepository;

}
