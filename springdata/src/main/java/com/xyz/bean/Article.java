package com.xyz.bean;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.Date;

@Document(indexName="project_study", type="article", refreshInterval="-1")
public class Article implements Serializable {
  //id(需要添加@Id注解,或会自动识别名称为id的字段为id,其余字段没有限制)
  @Id
  private Long id;
  /**标题*/
  private String title;
  /**摘要*/
  private String abstracts;
  /**内容*/
  private String content;
  /**发表时间*/
  private Date postTime;
  /**点击率*/
  private Long clickCount;
  /**类型**/
  private int type;

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAbstracts() {
    return abstracts;
  }

  public void setAbstracts(String abstracts) {
    this.abstracts = abstracts;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Date getPostTime() {
    return postTime;
  }

  public void setPostTime(Date postTime) {
    this.postTime = postTime;
  }

  public Long getClickCount() {
    return clickCount;
  }

  public void setClickCount(Long clickCount) {
    this.clickCount = clickCount;
  }

  @Override
  public String toString() {
    return "Article{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", abstracts='" + abstracts + '\'' +
            ", content='" + content + '\'' +
            ", postTime=" + postTime +
            ", clickCount=" + clickCount +
            ", type=" + type +
            '}';
  }
}
