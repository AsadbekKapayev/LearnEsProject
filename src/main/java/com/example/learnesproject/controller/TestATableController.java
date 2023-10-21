package com.example.learnesproject.controller;

import com.example.learnesproject.model.Paging;
import com.example.learnesproject.model.elastic.TestModelAElastic;
import com.example.learnesproject.model.web.TableRequest;
import com.example.learnesproject.register.TestAElasticRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/a/table")
@CrossOrigin("*")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class TestATableController {

  @Autowired
  private TestAElasticRegister testAElasticRegister;

  @GetMapping("/all")
  public List<TestModelAElastic> loadAll(
          @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
          @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
    return testAElasticRegister.loadAll(Paging.of(offset, limit));
  }

  @PostMapping("/filtered")
  public List<TestModelAElastic> load(@RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
                                      @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                      @RequestBody TableRequest tableRequest) {
    return testAElasticRegister.loadByFilter(tableRequest, Paging.of(offset, limit));
  }

}
