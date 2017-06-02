package njson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.Map;

public class Deserializer {
  private static final int TMP_BUFF_SISZE = 1024*1024;
  private static final int DEFAULT_INITIAL_CAPACITY = 16;

  /**
   * Current internal buffer.
   */
  protected BytesOp buffer;
  int delimiter = '.';
  byte[] tmpBytes;

  public Deserializer(){
    buffer = new BytesOp();
    tmpBytes = new byte[TMP_BUFF_SISZE];
  }

  public void init(byte[] bytes){
    init(bytes, 0, bytes.length);
  }

  public void init(byte[] bytes, int offset, int end){
    buffer.init(bytes, offset, end);
    unpackHeader();
  }

  public void setDelimiter(int delimiter){
    this.delimiter = delimiter;
  }

  private void unpackHeader(){
    byte version = buffer.get();
    buffer.setBigEndian(Code.getBigEndian(buffer.get()));
  }

  private void resetPostion() {
    buffer.position(buffer.offset()+Code.HEADER_LENGTH);
  }

  private int length(byte len){
    return len & 0xff;
  }

  private int length(short len){
    return len & 0xffff;
  }

  public int getValuePos(String key) throws Exception {
    resetPostion();
    //int pos = key.indexOf(delimiter);
    int len = 0;
    int pos = 0;
    byte b = buffer.get();
    Format f = Format.valueOf(b);
    switch (f) {
      case FIXMAP: {
        len = b & 0x0f;
        break;
      }
      case MAP16:{
        len = length(buffer.getShort());
        break;
      }
      case MAP32:{
        len = buffer.getInt();
        break;
      }
      default:
        throw new FormatException(f);
    }

    return getMapValuePos(key, len);
  }

  private String getString(int pos) throws Exception {
    int len = 0;
    byte b = buffer.get(pos++);
    Format f = Format.valueOf(b);
    switch (f) {
      case FIXSTR: {
        len = b & 0x0f;
        break;
      }
      case STR8:{
        len = length(buffer.get(pos));
        pos += 1;
        break;
      }
      case STR16:{
        len = length(buffer.getShort(pos));
        pos += 2;
        break;
      }
      case STR32:{
        len = buffer.getInt(pos);
        pos += 4;
        break;
      }
      case NIL:
        return null;
      default:
        throw new FormatException(f);
    }

    if (len > 0){
      return buffer.getString(pos, len);
    }else{
      return "";
    }
  }

  private int getInt(int pos) throws Exception {
    byte b = buffer.get(pos++);
    Format f = Format.valueOf(b);
    switch (f) {
      case POSFIXINT:
        return b & 0x7f;
      case NEGFIXINT:
        return -(b & 0x3f);
      case UINT8:
      case INT8:
        return buffer.get(pos);
      case UINT16:
      case INT16:
        return buffer.getShort(pos);
      case UINT32:
      case INT32:
        return buffer.getInt(pos);
      case UINT64:
      case INT64:
        return (int)buffer.getLong(pos);
      case FLOAT32:
        return (int)buffer.getFloat(pos);
      case FLOAT64:
        return (int)buffer.getDouble(pos);
      case NIL:
        throw new NullException();
      default:
        throw new FormatException(f);
    }
  }

  private long getLong(int pos) throws Exception {
    byte b = buffer.get(pos++);
    Format f = Format.valueOf(b);
    switch (f) {
      case POSFIXINT:
        return b & 0x7f;
      case NEGFIXINT:
        return -(b & 0x3f);
      case UINT8:
      case INT8:
        return buffer.get(pos);
      case UINT16:
      case INT16:
        return buffer.getShort(pos);
      case UINT32:
      case INT32:
        return buffer.getInt(pos);
      case UINT64:
      case INT64:
        return buffer.getLong(pos);
      case FLOAT32:
        return (long)buffer.getFloat(pos);
      case FLOAT64:
        return (long)buffer.getDouble(pos);
      case NIL:
        throw new NullException();
      default:
        throw new FormatException(f);
    }
  }

  private float getFloat(int pos) throws Exception {
    byte b = buffer.get(pos++);
    Format f = Format.valueOf(b);
    switch (f) {
      case POSFIXINT:
        return b & 0x7f;
      case NEGFIXINT:
        return -(b & 0x3f);
      case UINT8:
      case INT8:
        return buffer.get(pos);
      case UINT16:
      case INT16:
        return buffer.getShort(pos);
      case UINT32:
      case INT32:
        return buffer.getInt(pos);
      case UINT64:
      case INT64:
        return buffer.getLong(pos);
      case FLOAT32:
        return buffer.getFloat(pos);
      case FLOAT64:
        return (float)buffer.getDouble(pos);
      case NIL:
        throw new NullException();
      default:
        throw new FormatException(f);
    }
  }

  private double getDouble(int pos) throws Exception {
    byte b = buffer.get(pos++);
    Format f = Format.valueOf(b);
    switch (f) {
      case POSFIXINT:
        return b & 0x7f;
      case NEGFIXINT:
        return -(b & 0x3f);
      case UINT8:
      case INT8:
        return buffer.get(pos);
      case UINT16:
      case INT16:
        return buffer.getShort(pos);
      case UINT32:
      case INT32:
        return buffer.getInt(pos);
      case UINT64:
      case INT64:
        return buffer.getLong(pos);
      case FLOAT32:
        return buffer.getFloat(pos);
      case FLOAT64:
        return buffer.getDouble(pos);
      case NIL:
        throw new NullException();
      default:
        throw new FormatException(f);
    }
  }

  private boolean getBoolean(int pos) throws Exception {
    byte b = buffer.get(pos++);
    Format f = Format.valueOf(b);
    switch (f) {
      case BOOLEAN:
        return b == Code.TRUE;
      case POSFIXINT:
        return (b & 0x7f) > 0;
      case UINT8:
      case INT8:
        return buffer.get(pos) > 0;
      case UINT16:
      case INT16:
        return buffer.getShort(pos) > 0;
      case UINT32:
      case INT32:
        return buffer.getInt(pos) > 0;
      case UINT64:
      case INT64:
        return buffer.getLong(pos) > 0;
      case FLOAT32:
        return buffer.getFloat(pos) > 0;
      case FLOAT64:
        return buffer.getDouble(pos) > 0;
      case NIL:
        throw new NullException();
      default:
        throw new FormatException(f);
    }
  }

  public String getString(String key) throws Exception {
    int pos = getValuePos(key);
    if (pos < 0){
      throw new NotFoundException(key);
    }

    return getString(pos);
  }

  public int getInt(String key) throws Exception {
    int pos = getValuePos(key);
    if (pos < 0){
      throw new NotFoundException(key);
    }

    return getInt(pos);
  }

  public long getLong(String key) throws Exception {
    int pos = getValuePos(key);
    if (pos < 0){
      throw new NotFoundException(key);
    }

    return getLong(pos);
  }

  public float getFloat(String key) throws Exception {
    int pos = getValuePos(key);
    if (pos < 0){
      throw new NotFoundException(key);
    }

    return getFloat(pos);
  }

  public double getDouble(String key) throws Exception {
    int pos = getValuePos(key);
    if (pos < 0){
      throw new NotFoundException(key);
    }

    return getDouble(pos);
  }

  public boolean getBoolean(String key) throws Exception {
    int pos = getValuePos(key);
    if (pos < 0){
      throw new NotFoundException(key);
    }

    return getBoolean(pos);
  }

  public Object getObject(String key) throws Exception {
    int pos = getValuePos(key);
    if (pos < 0){
      throw new NotFoundException(key);
    }

    return unpackValue(pos);
  }

  public int getMapValuePos(String key, int len) throws Exception {
    int strLen = 0;
    int comp = 0;
    byte[] keyBytes = key.getBytes();
    int end = buffer.position()+len;
    while (buffer.position() < end) {
      //key
      byte b = buffer.get();
      Format f = Format.valueOf(b);
      switch(f) {
        case FIXSTR:
          strLen = b & 0x1f;
          break;
        case STR8:
          strLen = length(buffer.get());
          break;
        case STR16:
          strLen = length(buffer.getShort());
          break;
        case STR32:
          strLen = buffer.getInt();
          break;
        default:
          throw new FormatException(f);
      }

      if (strLen > 0){
        buffer.getBytes(tmpBytes, strLen);
      }

      if (bytesEquals(tmpBytes, strLen, keyBytes, keyBytes.length)){
        return buffer.position();
      }
    }

    return -1;
  }

  private static boolean bytesEquals(byte[] o1, int len1, byte[] o2, int len2){
    if (len1 != len2){
      return false;
    }

    for (int i=0; i < len2; i++){
      if (o1[i] != o2[i]) {
        return false;
      }
    }

    return true;
  }

  public void skipValue() throws Exception {
    int skiplen = 0;
    byte b = buffer.get();
    Format f = Format.valueOf(b);
    switch(f) {
      case FIXSTR:
        skiplen = b & 0x1f;
        break;
      case FIXMAP:
        skiplen = b & 0x0f;
        break;
      case FIXARRAY:
        skiplen = b & 0x0f;
        break;
      case STR8:
      case BIN8:
        skiplen = length(buffer.get());
        break;
      case STR16:
      case BIN16:
      case MAP16:
      case ARRAY16:
        skiplen = length(buffer.getShort());
        break;
      case STR32:
      case BIN32:
      case MAP32:
      case ARRAY32:
        skiplen = buffer.getInt();
        break;
      case POSFIXINT:
        skiplen = 0;
        break;
      case NEGFIXINT:
        skiplen = 0;
        break;
      case UINT8:
      case INT8:
        skiplen = 1;
        break;
      case UINT16:
      case INT16:
        skiplen = 2;
        break;
      case UINT32:
      case INT32:
        skiplen = 4;
        break;
      case UINT64:
      case INT64:
        skiplen = 8;
        break;
      case FLOAT32:
        skiplen = 4;
        break;
      case FLOAT64:
        skiplen = 8;
        break;
      case NIL:
        skiplen = 0;
        break;
      case BOOLEAN:
        skiplen = 0;
        break;
      default:
        throw new FormatException(f);
    }

    if (skiplen > 0){
      buffer.skip(skiplen);
    }
  }

  public Object unpackJsonObject() throws Exception {
    int len;
    byte b = buffer.get();
    Format f = Format.valueOf(b);
    switch (f) {
      case FIXMAP:
        len = b & 0x0f;
        return unpackMap(len);
      case MAP16:
        len = length(buffer.getShort());
        return unpackMap(len);
      case MAP32:
        len = buffer.getInt();
        return unpackMap(len);
      case FIXARRAY:
        len = b & 0x0f;
        return unpackArray(len);
      case ARRAY16:
        len = length(buffer.getShort());
        return unpackArray(len);
      case ARRAY32:
        len = buffer.getInt();
        return unpackArray(len);
      default:
        throw new FormatException(f);
    }
  }

  private Object unpackValue() throws Exception {
    return unpackValue(buffer.position());
  }

  private Object unpackValue(int pos) throws Exception {
    int len;
    buffer.position(pos);
    byte b = buffer.get();
    Format f = Format.valueOf(b);
    switch(f) {
      case FIXSTR:
        len = b & 0x1f;
        return buffer.getString(len);
      case STR8:
        len = length(buffer.get());
        return buffer.getString(len);
      case STR16:
        len = length(buffer.getShort());
        return buffer.getString(len);
      case STR32:
        len = buffer.getInt();
        return buffer.getString(len);
      case BIN8:
        len = length(buffer.get());
        return buffer.getBytes(len);
      case BIN16:
        len = length(buffer.getShort());
        return buffer.getBytes(len);
      case BIN32:
        len = buffer.getInt();
        return buffer.getBytes(len);
      case POSFIXINT:
        return b & 0x7f;
      case NEGFIXINT:
        return -(b & 0x3f);
      case UINT8:
      case INT8:
        return buffer.get();
      case UINT16:
      case INT16:
        return buffer.getShort();
      case UINT32:
      case INT32:
        return buffer.getInt();
      case UINT64:
      case INT64:
        return buffer.getLong();
      case FLOAT32:
        return buffer.getFloat();
      case FLOAT64:
        return buffer.getDouble();
      case BOOLEAN:
        return b == Code.TRUE;
      case NIL:
        return null;
      case FIXMAP:
        len = b & 0x0f;
        return unpackMap(len);
      case MAP16:
        len = length(buffer.getShort());
        return unpackMap(len);
      case MAP32:
        len = buffer.getInt();
        return unpackMap(len);
      case FIXARRAY:
        len = b & 0x0f;
        return unpackArray(len);
      case ARRAY16:
        len = length(buffer.getShort());
        return unpackArray(len);
      case ARRAY32:
        len = buffer.getInt();
        return unpackArray(len);
      default:
        throw new FormatException(f);
    }
  }

  private Map<String, Object> unpackMap(int len) throws Exception {
    int strLen = 0;
    int end = buffer.position()+len;
    String key = null;
    Object value = null;
    Map<String, Object> map = new HashMap<>(DEFAULT_INITIAL_CAPACITY);
    while (buffer.position() < end) {
      //key
      byte b = buffer.get();
      Format f = Format.valueOf(b);
      switch(f) {
        case FIXSTR:
          strLen = b & 0x1f;
          break;
        case STR8:
          strLen = length(buffer.get());
          break;
        case STR16:
          strLen = length(buffer.getShort());
          break;
        case STR32:
          strLen = buffer.getInt();
          break;
        default:
          throw new FormatException(f);
      }

      if (strLen > 0){
        key = buffer.getString(strLen);
      }else{
        key = "";
      }

      //value
      value = unpackValue();
      map.put(key, value);
    }

    return map;
  }

  private Collection<Object> unpackArray(int len) throws Exception {
    int strLen = 0;
    int end = buffer.position()+len;
    Object value = null;
    Collection<Object> list = new ArrayList<>();
    while (buffer.position() < end) {
      //value
      value = unpackValue();
      list.add(value);
    }

    return list;
  }
}
