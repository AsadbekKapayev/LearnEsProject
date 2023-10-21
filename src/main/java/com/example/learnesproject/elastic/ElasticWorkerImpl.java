package com.example.learnesproject.elastic;

import com.example.learnesproject.elastic.model.EsBodyWrapper;
import com.example.learnesproject.elastic.model.EsHit;
import com.example.learnesproject.model.Paging;
import com.example.learnesproject.util.jackson.ObjectMapperHolder;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class ElasticWorkerImpl implements InitializingBean, DisposableBean, ElasticWorker {

  @Value("${sandbox.elastic.schema}")
  private String schema;

  @Value("${sandbox.elastic.host}")
  private String host;

  @Value("${sandbox.elastic.port}")
  private String port;

  private final boolean updateImmediately = true;

  private RestClient restClient;

  @Override
  public void afterPropertiesSet() {
    restClient = RestClient.builder(
            new HttpHost(host, Integer.parseInt(port), schema)
    ).build();

  }

  @Override
  public void destroy() throws Exception {
    if (restClient != null) {
      restClient.close();
    }
  }

  @Override
  public Response performRequest(Request request) {
    try {
      return restClient.performRequest(request);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Response createIndex(String indexName, String mapping) {
    Request request = new Request("PUT", "/" + indexName);

    request.setJsonEntity(mapping);

    return performRequest(request);
  }

  @Override
  public Response refresh(String indexName) {
    Request request = new Request("POST", "/_refresh");

    return performRequest(request);
  }

  @Override
  public boolean doesIndexExists(String indexName) {
    Request request = new Request("HEAD", "/" + indexName);
    Response response = performRequest(request);
    return response.getStatusLine().getStatusCode() == 200;
  }

  @SneakyThrows
  @Override
  public EsHit findById(String indexName, String id) {
    Request request = new Request("GET", "/" + indexName + "/_doc/" + id);

    Response response = performRequest(request);

    String body = EntityUtils.toString(response.getEntity());

    EsHit esHit = ObjectMapperHolder.readJson(body, EsHit.class);

    if (!esHit.found) {
      throw new RuntimeException("Entity is not exist with id: " + id);
    }

    return esHit;
  }

  @SneakyThrows
  @Override
  public EsBodyWrapper findAll(String indexName, Paging paging) {
    Request request = new Request("GET", "/" + indexName + "/_search");

    String searchQuery = "{\"query\": {\"match_all\": {}}, \"size\": " + paging.limit + ", \"from\": " + paging.offset + "}";

    HttpEntity entity = new NStringEntity(searchQuery, ContentType.APPLICATION_JSON);
    request.setEntity(entity);

    Response response = performRequest(request);

    String body = EntityUtils.toString(response.getEntity());

    EsBodyWrapper bodyWrapper = ObjectMapperHolder.readJson(body, EsBodyWrapper.class);

    if (bodyWrapper.timed_out) {
      throw new RuntimeException("Request to elastic has been timed out");
    }

    return bodyWrapper;
  }

  @SneakyThrows
  @Override
  public EsBodyWrapper find(String indexName, Map<String, String> valueMap, Paging paging) {
    if (valueMap.isEmpty()) {
      return findAll(indexName, paging);
    }

    String requestBody = prefixMatch(valueMap);

    Request request = new Request("POST", "/" + indexName + "/_search");

    request.setJsonEntity(requestBody);

    Response response = performRequest(request);

    String body = EntityUtils.toString(response.getEntity());

    EsBodyWrapper bodyWrapper = ObjectMapperHolder.readJson(body, EsBodyWrapper.class);

    if (bodyWrapper.timed_out) {
      throw new RuntimeException("Request to elastic has been timed out");
    }

    return bodyWrapper;
  }

  /**
   * Метод использует should и находит матчы (match) по префиксу и
   * <br>
   * по wildcardy (что позволяет находит матчы по внутри одного слова)
   * <br>
   * Нюанс: Метод вернет true, если если есть match хотя бы по одному значению
   *
   * @param valueMap ключ - название поля, значение - значения поля
   * @return запрос
   */
  private String prefixAndMiddleMatch(Map<String, String> valueMap) {

    if (valueMap == null || valueMap.isEmpty()) {
      throw new RuntimeException("Value map is expected to have at least one value");
    }

    StringBuilder filterQueryBuilder = new StringBuilder("{\"query\": {\"bool\": {\"should\": [");

    for (Map.Entry<String, String> entry : valueMap.entrySet()) {
      String field = entry.getKey();
      String value = entry.getValue();

      filterQueryBuilder.append("{\"match_phrase_prefix\": {\"").append(field).append("\": \"").append(value).append("\"}},");
      filterQueryBuilder.append("{\"wildcard\": {\"").append(field).append("\": \"*").append(value).append("*\"}},");
    }

    // Remove the trailing comma
    filterQueryBuilder.deleteCharAt(filterQueryBuilder.length() - 1);

    filterQueryBuilder.append("]}}}");

    return filterQueryBuilder.toString();
  }

  /**
   * Метод использует must и находит матчы (match) по префиксу
   * <br>
   * Нюанс: Метод не может найти матчы, если текст не начинается со значения (то есть префикс)
   *
   * @param valueMap ключ - название поля, значение - значения поля
   * @return запрос
   */
  private String prefixMatch(Map<String, String> valueMap) {

    if (valueMap == null || valueMap.isEmpty()) {
      throw new RuntimeException("Value map is expected to have at least one value");
    }

    StringBuilder filterQueryBuilder = new StringBuilder("{\"query\": {\"bool\": {\"must\": [");

    for (Map.Entry<String, String> entry : valueMap.entrySet()) {
      String field = entry.getKey();
      String value = entry.getValue();

      filterQueryBuilder.append("{\"match_phrase_prefix\": {\"").append(field).append("\": \"").append(value).append("\"}},");
    }

    // Remove the trailing comma
    filterQueryBuilder.deleteCharAt(filterQueryBuilder.length() - 1);

    filterQueryBuilder.append("]}}}");

    return filterQueryBuilder.toString();
  }

  @Override
  public Response insertDocument(String indexName, String documentId, String jsonifiedString) {
    Request request = new Request("POST", "/" + indexName + "/_doc/" + documentId);
    HttpEntity entity = new NStringEntity(jsonifiedString, ContentType.APPLICATION_JSON);
    request.setEntity(entity);

    Response response = performRequest(request);

    if (updateImmediately) {
      refresh(indexName);
    }

    return response;
  }

  @Override
  public Response updateDocument(String indexName, String documentId, String jsonifiedString) {
    Request request = new Request("PUT", "/" + indexName + "/_doc/" + documentId);
    HttpEntity entity = new NStringEntity(jsonifiedString, ContentType.APPLICATION_JSON);
    request.setEntity(entity);

    Response response = performRequest(request);

    if (updateImmediately) {
      refresh(indexName);
    }

    return response;
  }

  @Override
  public Response deleteDocument(String indexName, String documentId) {
    Request request = new Request("DELETE", "/" + indexName + "/_doc/" + documentId);

    Response response = performRequest(request);

    if (updateImmediately) {
      refresh(indexName);
    }

    return response;
  }

}
