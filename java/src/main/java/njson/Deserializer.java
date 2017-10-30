package njson;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Deserializer implements Serializable {
    private static final int INIT_BUFF_SIZE = 16;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * Current internal buffer.
     */
    protected BytesBuffer buffer;
    byte delimiter = '.';
    byte[] tmpBytes;
    byte[] keyBytes;

    public Deserializer() {
        buffer = new BytesBuffer();
        tmpBytes = new byte[INIT_BUFF_SIZE];
        keyBytes = new byte[INIT_BUFF_SIZE];
    }

    public void init(byte[] bytes) {
        init(bytes, 0, bytes.length);
    }

    public void init(byte[] bytes, int offset, int end) {
        buffer.init(bytes, offset, end);
        unpackHeader();
    }

    public void setDelimiter(byte delimiter) {
        this.delimiter = delimiter;
    }

    private byte[] ensureTmpBytesCapacity(int minimumSize) throws IOException {
        if (tmpBytes.length < minimumSize) {
            tmpBytes = new byte[minimumSize / INIT_BUFF_SIZE * INIT_BUFF_SIZE + INIT_BUFF_SIZE];
        }
        return tmpBytes;
    }

    private byte[] ensureKeyBytesCapacity(int minimumSize) throws IOException {
        if (keyBytes.length < minimumSize) {
            keyBytes = new byte[minimumSize / INIT_BUFF_SIZE * INIT_BUFF_SIZE + INIT_BUFF_SIZE];
        }
        return keyBytes;
    }

    private void unpackHeader() {
        byte version = buffer.get();
        buffer.setBigEndian(Code.getBigEndian(buffer.get()));
    }

    private void resetPostion() {
        buffer.position(buffer.offset() + Code.HEADER_LENGTH);
    }

    private int length(byte len) {
        return len & 0xff;
    }

    private int length(short len) {
        return len & 0xffff;
    }

    private int indexOf(byte[] key, byte b, int fromIndex, int endIndex) {
        for (int i = fromIndex; i < endIndex; i++) {
            if (key[i] == b) {
                return i;
            }
        }
        return -1;
    }

    public int getValuePos(String key) throws Exception {
        resetPostion();
        //简单考虑了非ascii字符，但是2倍其实是不够的
        int keyLen = Utils.encodeUTF8(key, 0, key.length(), ensureKeyBytesCapacity(key.length() * 2));
        int fromIndex = 0;
        int endIndex;
        int pos;

        while (true) {
            endIndex = indexOf(keyBytes, delimiter, fromIndex, keyLen);
            if (endIndex < 0) {
                endIndex = keyLen;
            }
            pos = getMapValuePos(keyBytes, fromIndex, endIndex);
            if (pos < 0) {
                return pos;
            }
            if (endIndex == keyLen) {
                break;
            }

            fromIndex = endIndex + 1;
        }

        return pos;
    }

    private String getString(int pos) throws Exception {
        int len = 0;
        byte b = buffer.get(pos++);
        Format f = Format.valueOf(b);
        switch (f) {
            case FIXSTR:
                len = b & 0x1f;
                break;
            case STR8:
                len = length(buffer.get(pos));
                pos += 1;
                break;
            case STR16:
                len = length(buffer.getShort(pos));
                pos += 2;
                break;
            case STR32:
                len = buffer.getInt(pos);
                pos += 4;
                break;
            case POSFIXINT:
                return Integer.toString(b & 0x7f);
            case NEGFIXINT:
                return Integer.toString(-(b & 0x3f));
            case INT8:
                return Integer.toString(buffer.get(pos));
            case UINT8:
                return Integer.toString(0xff & buffer.get(pos));
            case INT16:
                return Integer.toString(buffer.getShort(pos));
            case UINT16:
                return Integer.toString(0xffff & buffer.getShort(pos));
            case INT32:
                return Integer.toString(buffer.getInt(pos));
            case UINT32:
                return Long.toString(0xffffffffL & buffer.getInt(pos));
            case UINT64:
            case INT64:
                return Long.toString(buffer.getLong(pos));
            case FLOAT32:
                return Float.toString(buffer.getFloat(pos));
            case FLOAT64:
                return Double.toString(buffer.getDouble(pos));
            case NIL:
                return null;
            default:
                throw new FormatException(f);
        }

        if (len > 0) {
            return buffer.getString(pos, len);
        } else {
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
            case INT8:
                return buffer.get(pos);
            case UINT8:
                return 0xff & buffer.get(pos);
            case INT16:
                return buffer.getShort(pos);
            case UINT16:
                return 0xffff & buffer.getShort(pos);
            case UINT32:
            case INT32:
                return buffer.getInt(pos);
            case UINT64:
            case INT64:
                return (int) buffer.getLong(pos);
            case FLOAT32:
                return (int) buffer.getFloat(pos);
            case FLOAT64:
                return (int) buffer.getDouble(pos);
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
            case INT8:
                return buffer.get(pos);
            case UINT8:
                return 0xff & buffer.get(pos);
            case INT16:
                return buffer.getShort(pos);
            case UINT16:
                return 0xffff & buffer.getShort(pos);
            case INT32:
                return buffer.getInt(pos);
            case UINT32:
                return 0xffffffffL & buffer.getInt(pos);
            case UINT64:
            case INT64:
                return buffer.getLong(pos);
            case FLOAT32:
                return (long) buffer.getFloat(pos);
            case FLOAT64:
                return (long) buffer.getDouble(pos);
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
            case INT8:
                return buffer.get(pos);
            case UINT8:
                return 0xff & buffer.get(pos);
            case INT16:
                return buffer.getShort(pos);
            case UINT16:
                return 0xffff & buffer.getShort(pos);
            case INT32:
                return buffer.getInt(pos);
            case UINT32:
                return 0xffffffffL & buffer.getInt(pos);
            case UINT64:
            case INT64:
                return buffer.getLong(pos);
            case FLOAT32:
                return buffer.getFloat(pos);
            case FLOAT64:
                return (float) buffer.getDouble(pos);
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
            case INT8:
                return buffer.get(pos);
            case UINT8:
                return 0xff & buffer.get(pos);
            case INT16:
                return buffer.getShort(pos);
            case UINT16:
                return 0xffff & buffer.getShort(pos);
            case INT32:
                return buffer.getInt(pos);
            case UINT32:
                return 0xffffffffL & buffer.getInt(pos);
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
        if (pos < 0) {
            throw new NotFoundException(key);
        }

        return getString(pos);
    }

    public int getInt(String key) throws Exception {
        int pos = getValuePos(key);
        if (pos < 0) {
            throw new NotFoundException(key);
        }

        return getInt(pos);
    }

    public long getLong(String key) throws Exception {
        int pos = getValuePos(key);
        if (pos < 0) {
            throw new NotFoundException(key);
        }

        return getLong(pos);
    }

    public float getFloat(String key) throws Exception {
        int pos = getValuePos(key);
        if (pos < 0) {
            throw new NotFoundException(key);
        }

        return getFloat(pos);
    }

    public double getDouble(String key) throws Exception {
        int pos = getValuePos(key);
        if (pos < 0) {
            throw new NotFoundException(key);
        }

        return getDouble(pos);
    }

    public boolean getBoolean(String key) throws Exception {
        int pos = getValuePos(key);
        if (pos < 0) {
            throw new NotFoundException(key);
        }

        return getBoolean(pos);
    }

    public Object getObject(String key) throws Exception {
        int pos = getValuePos(key);
        if (pos < 0) {
            throw new NotFoundException(key);
        }

        return unpackValue(pos);
    }

    public int getMapValuePos(byte[] key, int fromIndex, int endIndex) throws Exception {
        int len;
        byte b = buffer.get();
        Format f = Format.valueOf(b);
        switch (f) {
            case FIXMAP: {
                len = b & 0x0f;
                break;
            }
            case MAP16: {
                len = length(buffer.getShort());
                break;
            }
            case MAP32: {
                len = buffer.getInt();
                break;
            }
            default:
                throw new FormatException(f);
        }

        int strLen = 0;
        int comp = 0;
        int end = buffer.position() + len;
        while (buffer.position() < end) {
            //key
            b = buffer.get();
            f = Format.valueOf(b);
            switch (f) {
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

            if (strLen > 0) {
                buffer.getBytes(ensureTmpBytesCapacity(strLen), strLen);
            }

            if (Utils.bytesEquals(tmpBytes, 0, strLen, key, fromIndex, endIndex)) {
                return buffer.position();
            } else {
                skipValue();
            }
        }

        return -1;
    }

    public void skipValue() throws Exception {
        int skiplen = 0;
        byte b = buffer.get();
        Format f = Format.valueOf(b);
        switch (f) {
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

        if (skiplen > 0) {
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
        switch (f) {
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
                return -(b & 0x1f);
            case INT8:
                return buffer.get();
            case UINT8:
                return 0xff & buffer.get();
            case INT16:
                return buffer.getShort();
            case UINT16:
                return 0xffff & buffer.getShort();
            case INT32:
                return buffer.getInt();
            case UINT32:
                return 0xffffffffL & buffer.getInt();
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
        int end = buffer.position() + len;
        String key = null;
        Object value = null;
        Map<String, Object> map = new HashMap<>(DEFAULT_INITIAL_CAPACITY);
        while (buffer.position() < end) {
            //key
            byte b = buffer.get();
            Format f = Format.valueOf(b);
            switch (f) {
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

            if (strLen > 0) {
                key = buffer.getString(strLen);
            } else {
                key = "";
            }

            //value
            value = unpackValue();
            map.put(key, value);
        }

        return map;
    }

    private List<Object> unpackArray(int len) throws Exception {
        int strLen = 0;
        int end = buffer.position() + len;
        Object value = null;
        List<Object> list = new ArrayList<>();
        while (buffer.position() < end) {
            //value
            value = unpackValue();
            list.add(value);
        }

        return list;
    }
}
