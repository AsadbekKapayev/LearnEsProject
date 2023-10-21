package com.example.learnesproject.elastic;

import com.example.learnesproject.model.elastic.ElasticModel;
import com.example.learnesproject.model.elastic.TestModelAElastic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ElasticCreator {

  @Autowired
  private ElasticWorker elasticWorker;

  private static final List<ElasticModel> ELASTIC_MODELS = List.of(new ElasticModel() {
    @Override
    public String indexName() {
      return TestModelAElastic.indexName();
    }

    @Override
    public String mapping() {
      return TestModelAElastic.mapping();
    }
  });

  public void createNeededIndexes() {
    createIndexes(ELASTIC_MODELS);
  }

  private void createIndexes(List<ElasticModel> elasticModels) {
    elasticModels.stream()
            .filter(Predicate.not(elasticModel -> elasticWorker.doesIndexExists(elasticModel.indexName())))
            .forEach(elasticModel -> elasticWorker.createIndex(elasticModel.indexName(), elasticModel.mapping()));
  }

}
