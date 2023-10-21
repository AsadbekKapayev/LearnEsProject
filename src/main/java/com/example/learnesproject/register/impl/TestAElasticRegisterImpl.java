package com.example.learnesproject.register.impl;

import com.example.learnesproject.elastic.ElasticIndexes;
import com.example.learnesproject.elastic.ElasticWorker;
import com.example.learnesproject.elastic.model.EsBodyWrapper;
import com.example.learnesproject.elastic.model.EsHit;
import com.example.learnesproject.model.Paging;
import com.example.learnesproject.model.elastic.TestModelAElastic;
import com.example.learnesproject.model.web.TableRequest;
import com.example.learnesproject.register.TestAElasticRegister;
import com.example.learnesproject.util.jackson.ObjectMapperHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class TestAElasticRegisterImpl implements TestAElasticRegister {

  @Autowired
  private ElasticWorker elasticWorker;

  @Override
  public TestModelAElastic loadByFilter(String id) {
    EsHit esHit = elasticWorker.findById(ElasticIndexes.INDEX_MODEL_A, id);

    return TestModelAElastic.fromMap(esHit._source);
  }

  @Override
  public List<TestModelAElastic> loadAll(Paging paging) {
    EsBodyWrapper bodyWrapper = elasticWorker.findAll(ElasticIndexes.INDEX_MODEL_A, paging);

    return bodyWrapper.hits.hits()
            .stream()
            .map(hit -> hit._source)
            .map(TestModelAElastic::fromMap)
            .collect(Collectors.toList());
  }

  @Override
  public List<TestModelAElastic> loadByFilter(TableRequest tableRequest, Paging paging) {
    EsBodyWrapper bodyWrapper = elasticWorker.find(ElasticIndexes.INDEX_MODEL_A, tableRequest.toMap(), paging);

    return bodyWrapper.hits.hits()
            .stream()
            .map(hit -> hit._source)
            .map(TestModelAElastic::fromMap)
            .collect(Collectors.toList());
  }

  @Override
  public void create(TestModelAElastic modelA) {
    elasticWorker.insertDocument(ElasticIndexes.INDEX_MODEL_A, modelA.id, ObjectMapperHolder.writeJson(modelA));
  }

  @Override
  public void update(TestModelAElastic modelA) {
    elasticWorker.updateDocument(ElasticIndexes.INDEX_MODEL_A, modelA.id, ObjectMapperHolder.writeJson(modelA));
  }

  @Override
  public void delete(String id) {
    elasticWorker.deleteDocument(ElasticIndexes.INDEX_MODEL_A, id);
  }

}
