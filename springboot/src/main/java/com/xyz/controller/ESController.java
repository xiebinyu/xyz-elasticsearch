package com.xyz.controller;

import com.xyz.bean.Article;
import com.xyz.repository.ArticleSearchRepository;
import jdk.nashorn.internal.runtime.options.Option;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequestMapping("/es")
@RestController
public class ESController {

  @Autowired
  private ArticleSearchRepository articleSearchRepository;

  /**
   * 添加
   */
  @RequestMapping("/add")
  public void add(@RequestParam(defaultValue = "1L") Long id,
                  @RequestParam(defaultValue = "springboot + elasticsearch") String title,
                  @RequestParam(defaultValue = "0") int type) {
    Article article = new Article();
    article.setId(id);
    article.setTitle(title);
    article.setAbstracts("springboot integreate elasticsearch is very easy");
    article.setContent("elasticsearch based on lucene,"
            + "spring-data-elastichsearch based on elaticsearch"
            + ",this tutorial tell you how to integrete springboot with spring-data-elasticsearch");
    article.setPostTime(new Date());
    article.setClickCount(1L);
    article.setType(type);
    articleSearchRepository.save(article);
  }

  /**
   * 删除
   */
  @RequestMapping("/delete/{id}")
  public void delete(@PathVariable Long id,
                         HttpServletResponse response) throws IOException {
    articleSearchRepository.deleteById(id);
  }

  /**
   * 根据id查询
   */
  @RequestMapping("/queryById/{id}")
  public void queryById(@PathVariable Long id,
                     HttpServletResponse response) throws IOException {
    Optional<Article> article = articleSearchRepository.findById(id);
    response.setCharacterEncoding("utf-8");
    response.getWriter().write(article.get().toString());
  }

  /**
   * 全文搜索
   */
  @RequestMapping("/query/{title}")
  public void query(@PathVariable String title, HttpServletResponse response) throws IOException {
//    String builder = "springboot";//搜索关键字
    QueryStringQueryBuilder builder1 = new QueryStringQueryBuilder(title);
    QueryBuilder builder2 = QueryBuilders.multiMatchQuery(title,
            "title", "content");//搜索title中或content中包含有title的文档
    Iterable<Article> searchResult = articleSearchRepository.search(builder2);
    response.setCharacterEncoding("utf-8");
    response.getWriter().write(searchResult.toString());
  }


  /**
   * 全文搜索
   */
  @RequestMapping("/query")
  public void query(HttpServletResponse response,
                    @RequestParam String title, @RequestParam String type) throws IOException {
    //创建builder
    BoolQueryBuilder builder = QueryBuilders.boolQuery();
    //builder下有must、should以及mustNot 相当于sql中的and、or以及not
    //设置模糊搜索
    // title或者content等于限定值，同时type等于限定值
    // 错误（只会匹配must）
//    builder.should(QueryBuilders.fuzzyQuery("title", title)).should(QueryBuilders.fuzzyQuery("content", title)).must(QueryBuilders.termQuery("type", type));
    // 正确（嵌套）
    builder.must(QueryBuilders.boolQuery().should(QueryBuilders.fuzzyQuery("title", title)).should(QueryBuilders.fuzzyQuery("content", title)))
            .must(QueryBuilders.termQuery("type", type));
    //设置类型必须
//    builder.must(new QueryStringQueryBuilder(type).field("type"));

    //按照id从高到低
    FieldSortBuilder sort = SortBuilders.fieldSort("id").order(SortOrder.DESC);

    //设置分页(拿第一页，一页显示两条)
    //注意!es的分页api是从第0页开始的(坑)
    PageRequest page = new PageRequest(0, 10);

    //构建查询
    NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
    //将搜索条件设置到构建中
    nativeSearchQueryBuilder.withQuery(builder);
    //将分页设置到构建中
    nativeSearchQueryBuilder.withPageable(page);
    //将排序设置到构建中
    nativeSearchQueryBuilder.withSort(sort);
    //生产NativeSearchQuery
    NativeSearchQuery query = nativeSearchQueryBuilder.build();

    //执行
    Page<Article> search = articleSearchRepository.search(query);

    //获取总条数(前端分页需要使用)
    int total = (int) search.getTotalElements();

    //获取查询到的数据内容
    List<Article> content = search.getContent();

    System.out.println(total);
    response.setCharacterEncoding("utf-8");
    response.getWriter().write(content.toString());
  }
}