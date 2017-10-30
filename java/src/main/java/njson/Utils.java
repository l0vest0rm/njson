/*
 * Copyright 2017 njson authors
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

// Created by xuning on 2017/6/7

package njson;

import java.io.IOException;
import java.nio.charset.MalformedInputException;

public class Utils {

    //将String转换成bytes
    public static int encodeUTF8(String sa, int sp, int len, byte[] da) throws IOException {
        int sl = sp + len;
        int dp = 0;
        int dlASCII = dp + Math.min(len, da.length);

        // ASCII only optimized loop
        while (dp < dlASCII && sa.charAt(sp) < '\u0080') {
            da[dp++] = (byte) sa.charAt(sp++);
        }

        while (sp < sl) {
            char c = sa.charAt(sp++);
            if (c < 0x80) {
                // Have at most seven bits
                da[dp++] = (byte) c;
            } else if (c < 0x800) {
                // 2 bytes, 11 bits
                da[dp++] = (byte) (0xc0 | (c >> 6));
                da[dp++] = (byte) (0x80 | (c & 0x3f));
            } else if (c >= '\uD800' && c < ('\uDFFF' + 1)) { //Character.isSurrogate(c) but 1.7
                final int uc;
                int ip = sp - 1;
                if (Character.isHighSurrogate(c)) {
                    if (sl - ip < 2) {
                        uc = -1;
                    } else {
                        char d = sa.charAt(ip + 1);
                        if (Character.isLowSurrogate(d)) {
                            uc = Character.toCodePoint(c, d);
                        } else {
                            throw new IOException("encodeUTF8 error", new MalformedInputException(1));
                        }
                    }
                } else {
                    if (Character.isLowSurrogate(c)) {
                        throw new IOException("encodeUTF8 error", new MalformedInputException(1));
                    } else {
                        uc = c;
                    }
                }

                if (uc < 0) {
                    da[dp++] = (byte) '?';
                } else {
                    da[dp++] = (byte) (0xf0 | ((uc >> 18)));
                    da[dp++] = (byte) (0x80 | ((uc >> 12) & 0x3f));
                    da[dp++] = (byte) (0x80 | ((uc >> 6) & 0x3f));
                    da[dp++] = (byte) (0x80 | (uc & 0x3f));
                    sp++; // 2 chars
                }
            } else {
                // 3 bytes, 16 bits
                da[dp++] = (byte) (0xe0 | ((c >> 12)));
                da[dp++] = (byte) (0x80 | ((c >> 6) & 0x3f));
                da[dp++] = (byte) (0x80 | (c & 0x3f));
            }
        }
        return dp;
    }

    public static boolean bytesEquals(byte[] o1, int len1, byte[] o2, int len2) {
        if (len1 != len2) {
            return false;
        }

        for (int i = 0; i < len2; i++) {
            if (o1[i] != o2[i]) {
                return false;
            }
        }

        return true;
    }

    public static boolean bytesEquals(byte[] o1, int fromIndex1, int endIndex1, byte[] o2, int fromIndex2, int endIndex2) {
        if ((endIndex1 - fromIndex1) != (endIndex2 - fromIndex2)) {
            return false;
        }

        for (int i = 0; i < (endIndex1 - fromIndex1); i++) {
            if (o1[fromIndex1 + i] != o2[fromIndex2 + i]) {
                return false;
            }
        }

        return true;
    }
}
