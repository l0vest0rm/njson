/*
 * Copyright 2017 njson.writer authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"): you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http: *www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

// Created by xuning on 2017/6/2

package njson.writer;

import njson.BytesBuffer;

import java.io.IOException;

import static njson.Code.FALSE;
import static njson.Code.TRUE;

public class BooleanWriter  implements WriterIntf {
  public void write(Object object, BytesBuffer out) throws IOException{
    out.put((Boolean)object ? TRUE : FALSE);
  }
}
