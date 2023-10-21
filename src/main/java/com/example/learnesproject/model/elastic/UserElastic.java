package com.example.learnesproject.model.elastic;

import com.example.learnesproject.elastic.ElasticIndexes;
import lombok.experimental.FieldNameConstants;

import java.util.Map;

@FieldNameConstants
public class UserElastic {

  public Integer id;

  public String username;

  public String email;

  public String password_hash;

  public String name;

  public String surname;

  public static UserElastic fromMap(Map<String, String> map) {
    UserElastic model = new UserElastic();

    model.id = Integer.valueOf(map.get(Fields.id));
    model.username = map.get(Fields.username);
    model.email = map.get(Fields.email);
    model.password_hash = map.get(Fields.password_hash);
    model.name = map.get(Fields.name);
    model.surname = map.get(Fields.surname);

    return model;
  }

  public static String indexName() {
    return ElasticIndexes.INDEX_MODEL_A;
  }

  public static String mapping() {
    return "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"strField\": {\n" +
            "        \"type\": \"text\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
  }

}
