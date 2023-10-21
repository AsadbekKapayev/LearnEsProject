package com.example.learnesproject.spring_config.connection;

import com.example.learnesproject.elastic.ElasticWorker;
import com.example.learnesproject.spring_config.connection.checkers.ConnectionChecker_Elastic;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Component
public class Connections implements InitializingBean {

  @Autowired
  private ElasticWorker elasticWorker;

  private final ConnectionWaiter waiter = new ConnectionWaiter();

  @Override
  public void afterPropertiesSet() {
    waiter.add(new ConnectionChecker_Elastic(elasticWorker));
  }

  public void waitForAll() {
    waiter.waitForAll();
  }

}
