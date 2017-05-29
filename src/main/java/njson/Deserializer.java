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
/*
  public String getString(String key) {
    resetPostion();
    int pos = key.indexOf(delimiter);

  }

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
