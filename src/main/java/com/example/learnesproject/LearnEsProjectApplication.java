package com.example.learnesproject;

import com.example.learnesproject.elastic.ElasticCreator;
import com.example.learnesproject.spring_config.connection.Connections;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Lazy;

@SpringBootApplication
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class LearnEsProjectApplication extends SpringBootServletInitializer {

  @Lazy
  @Autowired
  private ElasticCreator elasticCreator;

  @Lazy
  @Autowired
  private Connections connections;

  public static void main(String[] args) {
    SpringApplication.run(LearnEsProjectApplication.class, args);
  }

  @PostConstruct
  public void initializeOrFinish() {

    connections.waitForAll();

    elasticCreator.createNeededIndexes();

  }

}
