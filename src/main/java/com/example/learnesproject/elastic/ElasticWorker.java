package com.example.learnesproject.elastic;

import com.example.learnesproject.elastic.model.EsBodyWrapper;
import com.example.learnesproject.elastic.model.EsHit;
import com.example.learnesproject.model.Paging;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;

import java.util.Map;

public interface ElasticWorker {

  Response performRequest(Request request);

  Response createIndex(String indexName, String mapping);

  Response refresh(String indexName);

  boolean doesIndexExists(String indexName);

  EsHit findById(String indexName, String id);

  EsBodyWrapper findAll(String indexName, Paging paging);

  EsBodyWrapper find(String indexName, Map<String, String> valueMap, Paging paging);

  Response insertDocument(String indexName, String documentId, String jsonifiedString);

  Response updateDocument(String indexName, String documentId, String jsonifiedString);

  Response deleteDocument(String indexName, String documentId);

}
