package com.xyz.repository;

import com.xyz.bean.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArticleSearchRepository extends ElasticsearchRepository<Article, Long> {
}