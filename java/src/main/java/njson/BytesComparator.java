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

// Created by xuning on 2017/5/29
import java.util.Comparator;

public class BytesComparator implements Comparator<byte[]> {
  /**
   * Compares its two arguments for order.  Returns a negative integer,
   * zero, or a positive integer as the first argument is less than, equal
   * to, or greater than the second.<p>
   * it compare length fist then the bytes.
   *
   * @param o1 the first object to be compared.
   * @param o2 the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the
   *         first argument is less than, equal to, or greater than the
   *         second.
   * @throws NullPointerException if an argument is null and this
   *         comparator does not permit null arguments
   * @throws ClassCastException if the arguments' types prevent them from
   *         being compared by this comparator.
   */
  @Override
  public int compare(byte[] o1, byte[] o2){
    if (o1.length < o2.length){
      return -1;
    }else if (o1.length > o2.length){
      return 1;
    }

    for (int i=0; i < o1.length; i++){
      if (o1[i] < o2[i]) {
        return -1;
      }else if (o1[i] > o2[i]){
        return 1;
      }
    }

    return 0;
  }

  public static int compare(byte[] o1, int len1, byte[] o2, int len2){
    if (len1 < len2){
      return -1;
    }else if (len1 > len2){
      return 1;
    }

    for (int i=0; i < len2; i++){
      if (o1[i] < o2[i]) {
        return -1;
      }else if (o1[i] > o2[i]){
        return 1;
      }
    }

    return 0;
  }
}
