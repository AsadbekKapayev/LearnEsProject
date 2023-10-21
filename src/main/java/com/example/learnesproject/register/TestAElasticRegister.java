package com.example.learnesproject.register;

import com.example.learnesproject.model.Paging;
import com.example.learnesproject.model.elastic.TestModelAElastic;
import com.example.learnesproject.model.web.TableRequest;

import java.util.List;

public interface TestAElasticRegister {

  TestModelAElastic loadByFilter(String id);

  List<TestModelAElastic> loadAll(Paging paging);

  List<TestModelAElastic> loadByFilter(TableRequest tableRequest, Paging paging);

  void create(TestModelAElastic modelA);

  void update(TestModelAElastic modelA);

  void delete(String id);

}
