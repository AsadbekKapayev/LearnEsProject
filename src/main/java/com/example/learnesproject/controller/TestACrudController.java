package com.example.learnesproject.controller;

import com.example.learnesproject.model.elastic.TestModelAElastic;
import com.example.learnesproject.register.TestAElasticRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/a/crud")
@CrossOrigin("*")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class TestACrudController {

  @Autowired
  private TestAElasticRegister testAElasticRegister;

  @GetMapping("/load")
  public TestModelAElastic load(@RequestParam("id") String id) {
    return testAElasticRegister.loadByFilter(id);
  }

  @PostMapping("/create")
  public void create(@RequestBody TestModelAElastic testModel) {
    testAElasticRegister.create(testModel);
  }

  @PostMapping("/update")
  public void update(@RequestBody TestModelAElastic testModel) {
    testAElasticRegister.update(testModel);
  }

  @PostMapping("/delete")
  public void delete(@RequestParam("id") String id) {
    testAElasticRegister.delete(id);
  }

}
