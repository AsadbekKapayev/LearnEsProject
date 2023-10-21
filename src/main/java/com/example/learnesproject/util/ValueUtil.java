package com.example.learnesproject.util;

import java.util.HashMap;
import java.util.Map;

public class ValueUtil {

  public static Map<String, String> idFilter(String id) {
    Map<String, String> map = new HashMap<>();

    if (isNullOrBlank(id)) {
      throw new RuntimeException("8XjwB22gVP :: id cannot be null or blank");
    }

    map.put("id", id);

    return map;
  }

  public static boolean isNullOrBlank(String str) {
    return str == null || str.isBlank();
  }

}
