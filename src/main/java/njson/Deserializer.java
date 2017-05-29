package njson;

public class Deserializer {
  /**
   * Current internal buffer.
   */
  protected BytesOp buffer;
  int delimiter = '.';

  public Deserializer(){
    buffer = new BytesOp();
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
        len = buffer.getShort();
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
        len = buffer.get(pos);
        pos += 1;
        break;
      }
      case STR16:{
        len = buffer.getShort(pos);
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

    byte[] bytes = buffer.getBytes(pos, len);
    return new String(bytes);
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

  public int getMapValuePos(String key, int len) throws Exception {
    int strLen = 0;
    int comp = 0;
    byte[] keyBytes = key.getBytes();
    int end = buffer.position()+len;
    byte[] tmpBytes = new byte[1024];
    while (buffer.position() < end) {
      //key
      byte b = buffer.get();
      Format f = Format.valueOf(b);
      switch(f) {
        case FIXSTR:
          strLen = b & 0x1f;
          break;
        case STR8:
          strLen = buffer.get();
          break;
        case STR16:
          strLen = buffer.getShort();
          break;
        case STR32:
          strLen = buffer.getInt();
          break;
        default:
          throw new FormatException(f);
      }

      if (strLen > 0){
        buffer.getBytes(tmpBytes, strLen);
        comp = BytesComparator.compare(tmpBytes, strLen, keyBytes, keyBytes.length);
      }else {
        comp = -1;
      }

      if (comp < 0) {
        skipValue();
        continue;
      }else if (comp > 0) {
        return -1;
      }

      //get value
      return buffer.position();
    }

    return -1;
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
        skiplen = buffer.get();
        break;
      case STR16:
      case BIN16:
      case MAP16:
      case ARRAY16:
        skiplen = buffer.getShort();
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

/*
  public Object unpack() throws Exception {
    Object obj = null;
    int len = 0;
    byte b = buffer.get();
    Format f = Format.valueOf(b);
    switch (f) {
      case FIXMAP: {
        obj = new HashMap<String, Object>();
        int mapLen = b & 0x0f;
        count += mapLen * 2;
        break;
      }
      case MAP16:{

      }
      case FIXARRAY: {
        int arrayLen = b & 0x0f;
        count += arrayLen;
        break;
      }
      case FIXSTR: {
        int strLen = b & 0x1f;
        skipPayload(strLen);
        break;
      }
      case INT8:
      case UINT8:
        skipPayload(1);
        break;
      case INT16:
      case UINT16:
        skipPayload(2);
        break;
      case INT32:
      case UINT32:
      case FLOAT32:
        skipPayload(4);
        break;
      case INT64:
      case UINT64:
      case FLOAT64:
        skipPayload(8);
        break;
      case BIN8:
      case STR8:
        skipPayload(readNextLength8());
        break;
      case BIN16:
      case STR16:
        skipPayload(readNextLength16());
        break;
      case BIN32:
      case STR32:
        skipPayload(readNextLength32());
        break;
      case FIXEXT1:
        skipPayload(2);
        break;
      case FIXEXT2:
        skipPayload(3);
        break;
      case FIXEXT4:
        skipPayload(5);
        break;
      case FIXEXT8:
        skipPayload(9);
        break;
      case FIXEXT16:
        skipPayload(17);
        break;
      case EXT8:
        skipPayload(readNextLength8() + 1);
        break;
      case EXT16:
        skipPayload(readNextLength16() + 1);
        break;
      case EXT32:
        skipPayload(readNextLength32() + 1);
        break;
      case ARRAY16:
        count += readNextLength16();
        break;
      case ARRAY32:
        count += readNextLength32();
        break;
      case MAP16:
        count += readNextLength16() * 2;
        break;
      case MAP32:
        count += readNextLength32() * 2; // TODO check int overflow
        break;
      case NEVER_USED:
        throw new Exception("Encountered 0xC1 \"NEVER_USED\" byte");
    }
  }

  public void unpackMap(Map<String, Object> map, int len) throws Exception {
    int strLen = 0;
    int end = buffer.position()+len;
    String key = "";
    byte[] tmpBytes = new byte[1024];
    while (buffer.position() < end) {
      //key
      byte b = buffer.get();
      Format f = Format.valueOf(b);
      switch(f) {
        case FIXSTR:
          strLen = b & 0x1f;
          break;
        case STR8:
          strLen = buffer.get();
          break;
        case STR16:
          strLen = buffer.getShort();
          break;
        case STR32:
          strLen = buffer.getInt();
          break;
        default:
          throw new Exception("unknown map key format");
      }

      if (strLen > 0){
        buffer.getBytes(tmpBytes, strLen);
      }

      //value
    }
  }*/
}
