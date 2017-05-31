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

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import static njson.Code.*;

public class Serializer {
  public static final int INIT_BUFF_SIZE = 1024*1024;

  /**
   * Current internal buffer.
   */
  protected BytesOp buffer;

  public Serializer()
  {
    this(INIT_BUFF_SIZE);
  }

  /**
   * Create an MessagePacker that outputs the packed data to the given {@link org.msgpack.core.buffer.MessageBufferOutput}.
   * This method is available for subclasses to override. Use MessagePack.PackerConfig.newPacker method to instanciate this implementation.
   *
   * @param out MessageBufferOutput. Use {@link org.msgpack.core.buffer.OutputStreamBufferOutput}, {@link org.msgpack.core.buffer.ChannelBufferOutput} or
   * your own implementation of {@link org.msgpack.core.buffer.MessageBufferOutput} interface.
   */
  public Serializer(int minimumSize)
  {
    byte[] bytes = new byte[minimumSize];
    buffer = new BytesOp();
    buffer.init(bytes);
    buffer.setBigEndian(true);
  }

  public void init(){
    buffer.init();
  }

  private void ensureCapacity(int minimumSize)
      throws IOException
  {
    if (minimumSize > buffer.remaining()){
      throw new IOException("not enough space");
    }
  }

  private void writeByte(byte b)
      throws IOException
  {
    ensureCapacity(1);
    buffer.put(b);
  }

  private void writeByteAndByte(byte b, byte v)
      throws IOException
  {
    ensureCapacity(2);
    buffer.put(b);
    buffer.put(v);
  }

  private void writeByteAndShort(byte b, short v)
      throws IOException
  {
    ensureCapacity(3);
    buffer.put(b);
    buffer.putShort(v);
  }

  private void writeByteAndInt(byte b, int v)
      throws IOException
  {
    ensureCapacity(5);
    buffer.put(b);
    buffer.putInt(v);
  }

  private void writeByteAndFloat(byte b, float v)
      throws IOException
  {
    ensureCapacity(5);
    buffer.put(b);
    buffer.putFloat(v);
  }

  private void writeByteAndDouble(byte b, double v)
      throws IOException
  {
    ensureCapacity(9);
    buffer.put(b);
    buffer.putDouble(v);
  }

  private void writeByteAndLong(byte b, long v)
      throws IOException
  {
    ensureCapacity(9);
    buffer.put(b);
    buffer.putLong(v);
  }

  private void writeShort(short v)
      throws IOException
  {
    ensureCapacity(2);
    buffer.putShort(v);
  }

  private void writeInt(int v)
      throws IOException
  {
    ensureCapacity(4);
    buffer.putInt(v);
  }

  private void writeLong(long v)
      throws IOException
  {
    ensureCapacity(8);
    buffer.putLong(v);
  }


  /**
   * Writes a Nil value.
   *
   * This method writes a nil byte.
   *
   * @return this
   * @throws IOException when underlying output throws IOException
   */
  public Serializer packNil()
      throws IOException
  {
    writeByte(NIL);
    return this;
  }

  /**
   * Writes a Boolean value.
   *
   * This method writes a true byte or a false byte.
   *
   * @return this
   * @throws IOException when underlying output throws IOException
   */
  public Serializer packBoolean(boolean b)
      throws IOException
  {
    writeByte(b ? TRUE : FALSE);
    return this;
  }

  /**
   * Writes an Integer value.
   *
   * <p>
   * This method writes an integer using the smallest format from the int format family.
   *
   * @param b the integer to be written
   * @return this
   * @throws IOException when underlying output throws IOException
   */
  public Serializer packByte(byte b)
      throws IOException
  {
    if (b < -(1 << 5)) {
      writeByteAndByte(INT8, b);
    }
    else {
      writeByte(b);
    }
    return this;
  }

  /**
   * Writes an Integer value.
   *
   * <p>
   * This method writes an integer using the smallest format from the int format family.
   *
   * @param v the integer to be written
   * @return this
   * @throws IOException when underlying output throws IOException
   */
  public Serializer packShort(short v)
      throws IOException
  {
    if (v < -(1 << 5)) {
      if (v < -(1 << 7)) {
        writeByteAndShort(INT16, v);
      }
      else {
        writeByteAndByte(INT8, (byte) v);
      }
    }
    else if (v < (1 << 7)) {
      writeByte((byte) v);
    }
    else {
      if (v < (1 << 8)) {
        writeByteAndByte(UINT8, (byte) v);
      }
      else {
        writeByteAndShort(UINT16, v);
      }
    }
    return this;
  }

  /**
   * Writes an Integer value.
   *
   * <p>
   * This method writes an integer using the smallest format from the int format family.
   *
   * @param r the integer to be written
   * @return this
   * @throws IOException when underlying output throws IOException
   */
  public Serializer packInt(int r)
      throws IOException
  {
    if (r < -(1 << 5)) {
      if (r < -(1 << 15)) {
        writeByteAndInt(INT32, r);
      }
      else if (r < -(1 << 7)) {
        writeByteAndShort(INT16, (short) r);
      }
      else {
        writeByteAndByte(INT8, (byte) r);
      }
    }
    else if (r < (1 << 7)) {
      writeByte((byte) r);
    }
    else {
      if (r < (1 << 8)) {
        writeByteAndByte(UINT8, (byte) r);
      }
      else if (r < (1 << 16)) {
        writeByteAndShort(UINT16, (short) r);
      }
      else {
        // unsigned 32
        writeByteAndInt(UINT32, r);
      }
    }
    return this;
  }

  /**
   * Writes an Integer value.
   *
   * <p>
   * This method writes an integer using the smallest format from the int format family.
   *
   * @param v the integer to be written
   * @return this
   * @throws IOException when underlying output throws IOException
   */
  public Serializer packLong(long v)
      throws IOException
  {
    if (v < -(1L << 5)) {
      if (v < -(1L << 15)) {
        if (v < -(1L << 31)) {
          writeByteAndLong(INT64, v);
        }
        else {
          writeByteAndInt(INT32, (int) v);
        }
      }
      else {
        if (v < -(1 << 7)) {
          writeByteAndShort(INT16, (short) v);
        }
        else {
          writeByteAndByte(INT8, (byte) v);
        }
      }
    }
    else if (v < (1 << 7)) {
      // fixnum
      writeByte((byte) v);
    }
    else {
      if (v < (1L << 16)) {
        if (v < (1 << 8)) {
          writeByteAndByte(UINT8, (byte) v);
        }
        else {
          writeByteAndShort(UINT16, (short) v);
        }
      }
      else {
        if (v < (1L << 32)) {
          writeByteAndInt(UINT32, (int) v);
        }
        else {
          writeByteAndLong(UINT64, v);
        }
      }
    }
    return this;
  }

  /**
   * Writes an Integer value.
   *
   * <p>
   * This method writes an integer using the smallest format from the int format family.
   *
   * @param bi the integer to be written
   * @return this
   * @throws IOException when underlying output throws IOException
   */
  public Serializer packBigInteger(BigInteger bi)
      throws IOException
  {
    if (bi.bitLength() <= 63) {
      packLong(bi.longValue());
    }
    else if (bi.bitLength() == 64 && bi.signum() == 1) {
      writeByteAndLong(UINT64, bi.longValue());
    }
    else {
      throw new IllegalArgumentException("MessagePack cannot serialize BigInteger larger than 2^64-1");
    }
    return this;
  }

  /**
   * Writes a Float value.
   *
   * <p>
   * This method writes a float value using float format family.
   *
   * @param v the value to be written
   * @return this
   * @throws IOException when underlying output throws IOException
   */
  public Serializer packFloat(float v)
      throws IOException
  {
    writeByteAndFloat(FLOAT32, v);
    return this;
  }

  /**
   * Writes a Float value.
   *
   * <p>
   * This method writes a float value using float format family.
   *
   * @param v the value to be written
   * @return this
   * @throws IOException when underlying output throws IOException
   */
  public Serializer packDouble(double v)
      throws IOException
  {
    writeByteAndDouble(FLOAT64, v);
    return this;
  }

  /**
   * Writes a String vlaue in UTF-8 encoding.
   *
   * <p>
   * This method writes a UTF-8 string using the smallest format from the str format family by default. If {@link MessagePack.PackerConfig#withStr8FormatSupport(boolean)} is set to false, smallest format from the str format family excepting str8 format.
   *
   * @param s the string to be written
   * @return this
   * @throws IOException when underlying output throws IOException
   */
  public Serializer packString(String s)
      throws IOException
  {
    if (s.length() <= 0) {
      packRawStringHeader(0);
      return this;
    }

    byte[] bytes = s.getBytes();
    // Write the length and payload of small string to the buffer so that it avoids an extra flush of buffer
    packRawStringHeader(bytes.length);
    ensureCapacity(bytes.length);
    buffer.put(bytes);

    return this;
  }

  public Serializer packString(byte[] bytes)
      throws IOException
  {
    if (bytes.length <= 0) {
      packRawStringHeader(0);
      return this;
    }

    // Write the length and payload of small string to the buffer so that it avoids an extra flush of buffer
    packRawStringHeader(bytes.length);
    ensureCapacity(bytes.length);
    buffer.put(bytes);

    return this;
  }

  /**
   * Writes header of a Binary value.
   * <p>
   * You MUST call {@link #writePayload(byte[])} or {@link #addPayload(byte[])} method to write body binary.
   *
   * @param len number of bytes of a binary to be written
   * @return this
   * @throws IOException when underlying output throws IOException
   */
  public Serializer packBinaryHeader(int len)
      throws IOException
  {
    if (len < (1 << 8)) {
      writeByteAndByte(BIN8, (byte) len);
    }
    else if (len < (1 << 16)) {
      writeByteAndShort(BIN16, (short) len);
    }
    else {
      writeByteAndInt(BIN32, len);
    }
    return this;
  }

  /**
   * Writes header of a String value.
   * <p>
   * Length must be number of bytes of a string in UTF-8 encoding.
   * <p>
   * You MUST call {@link #writePayload(byte[])} or {@link #addPayload(byte[])} method to write body of the
   * UTF-8 encoded string.
   *
   * @param len number of bytes of a UTF-8 string to be written
   * @return this
   * @throws IOException when underlying output throws IOException
   */
  public Serializer packRawStringHeader(int len)
      throws IOException
  {
    if (len < (1 << 5)) {
      writeByte((byte) (FIXSTR_PREFIX | len));
    }
    else if (len < (1 << 8)) {
      writeByteAndByte(STR8, (byte) len);
    }
    else if (len < (1 << 16)) {
      writeByteAndShort(STR16, (short) len);
    }
    else {
      writeByteAndInt(STR32, len);
    }
    return this;
  }

  public int reserveArrayHeader()
      throws IOException
  {
    writeByte(ARRAY32);
    int pos = buffer.position();
    buffer.putInt(0);
    return pos;
  }

  public int reserveMapHeader()
      throws IOException
  {
    writeByte(MAP32);
    int pos = buffer.position();
    buffer.putInt(0);
    return pos;
  }

  public Serializer packHeader() throws Exception {
    //pack version and bigendian
    writeByte(Code.SER_VERSION);
    writeByte(Code.setBigEndian((byte)0, buffer.bigEndian()));
    return this;
  }

  public Serializer packJsonObject(Object v) throws Exception {
    packHeader();

    if (v instanceof List) {
      packList((List<Object>) v);
    }
    else if (v instanceof Map) {
      packMap((Map<String, Object>) v);
    }
    else {
      throw new Exception(String.format("unknown class:%s", this.getClass().getSimpleName()));
    }

    return this;
  }

  public Serializer packList(List<Object> objs) throws Exception {
    int position = reserveArrayHeader();
    for (Object v: objs){
      packObject(v);
    }

    buffer.putInt(position, buffer.position()-position - 4);

    return this;
  }

  public Serializer packMap(Map<String, Object> map) throws Exception {
    if (map.size() == 0) {
      packNil();
    }

    int position = reserveMapHeader();
    Set<String> keySet = map.keySet();
    List<byte[]> bytesList = new ArrayList<>();
    for (String key: keySet){
      bytesList.add(key.getBytes());
    }

    Collections.sort(bytesList, new BytesComparator());
    for (byte[] key: bytesList){
      packString(key);
      packObject(map.get(new String(key)));
    }

    buffer.putInt(position, buffer.position()-position - 4);

    return this;
  }

  public Serializer packObject(Object v) throws Exception {
    if (v == null) {
      packNil();
    }else if (v instanceof String) {
      packString((String) v);
    }else if (v instanceof Integer) {
      packInt((int) v);
    }else if (v instanceof Long) {
      packLong((long) v);
    }else if (v instanceof Float) {
      if ((float)v == (float)(int)(float)v){
        packInt((int) v);
      }else {
        packFloat((float) v);
      }
    }else if (v instanceof Double) {
      if ((double)v == (double)(int)(double)v){
        packInt((int)(double) v);
      }else if ((double)v == (double)(long)(double)v) {
        packLong((long)(double) v);
      }else if ((double)v == (double)(float)(double)v){
        packFloat((float)(double) v);
      }else{
        packDouble((double)v);
      }
    }else if (v instanceof BigInteger) {
      packBigInteger((BigInteger) v);
    }else if (v instanceof Boolean) {
      packBoolean((Boolean) v);
    }else if (v instanceof List) {
      packList((List<Object>) v);
    }else if (v instanceof Map) {
      packMap((Map<String, Object>) v);
    }
    else {
      throw new Exception(String.format("unknown class:%s", this.getClass().getSimpleName()));
    }

    return this;
  }

  public byte[] toBytes() {
    return buffer.toBytes();
  }

  public void toBytes(byte[] dst) {
    toBytes(dst, 0);
  }

  public void toBytes(byte[] dst, int dstOffset) {
    buffer.toBytes(dst, dstOffset);
  }

}
