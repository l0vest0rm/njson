package njson;

/**
 * Copyright 2017 njson authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"): you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * <p>
 * http: *www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

// Created by xuning on 2017/5/27
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Testcase {
  @Test
  void testPaserObject() throws Exception {
    Gson gson = new Gson();
    String json = "{\"list\":[1,2,3,4],\"int\": 1,\"float\": 0.5,\"boolean\":true,\"null\":null,\"string\":\"foo bar\",\"array\":[\"foo\",\"bar\"],\"object\": {\"foo\": 1,\"baz\": 0.5}}";
    Map<String, Object> map = gson.fromJson(json, Map.class);

    if (map instanceof Map){
      System.out.format("map instanceof Map\n");
    }else{
      System.out.format("map instanceof other\n");
    }

    Object list = map.get("list");
    if (list instanceof List){
      System.out.format("list instanceof List\n");
    }else{
      System.out.format("list instanceof other\n");
    }

    Serializer ser = new Serializer();
    byte[] bytes = ser.packJsonObject(map).toBytes();
    System.out.format("jsonLen:%d,packLen:%d\n", json.length(), bytes.length);
    for (int j=0; j<bytes.length; j++) {
      System.out.format("%02X ", bytes[j]);
    }
    System.out.println();

    Deserializer deser = new Deserializer();
    deser.init(bytes);
    System.out.format("int:%d\n", deser.getInt("int"));
    System.out.format("float:%f\n", deser.getFloat("float"));
    System.out.format("boolean:%b\n", deser.getBoolean("boolean"));
    System.out.format("string:%s\n", deser.getString("string"));

    String key = "key中国";
    bytes = key.getBytes();
    System.out.format("strlen:%d,byteslen:%d\n", key.length(), bytes.length);
  }

  @Test
  void testSortBytesList() throws Exception {
    String[] strs = new String[]{"ab","中","a","cbce", "b", "d"};
    List<byte[]> bytesList = new ArrayList<>();
    for (String str: strs){
      bytesList.add(str.getBytes());
    }

    Collections.sort(bytesList, new BytesComparator());

    for (byte[] bytes: bytesList){
      System.out.format("%s,", new String(bytes));
    }
    System.out.println();
  }
}
