package njson;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

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

// Created by xuning on 2017/5/28

public class BytesOp {

  private byte[] bs;
  private boolean bigEndian;
  private int offset;
  private int position; //op index of bs(offset<=postion<end)
  private int end; //end of bs

  public BytesOp(){
  }

  public void init(){
    init(bs, offset, end);
  }

  public void init(byte[] bs){
    init(bs, 0, bs.length);
  }

  public void init(byte[] bs, int offset, int end){
    this.bs = bs;
    this.offset = offset;
    this.position = offset;
    this.end = end;
  }

  public void setBigEndian(boolean bigEndian){
    this.bigEndian = bigEndian;
  }

  /**
   * Checks the current position against the limit, throwing a {@link
   * BufferUnderflowException} if it is not smaller than the limit, and then
   * increments the position.
   *
   * @return  The current position value, before it is incremented
   */
  final int nextGetIndex() {                          // package-private
    if (position >= end)
      throw new BufferUnderflowException();
    return position++;
  }

  final int nextGetIndex(int nb) {                    // package-private
    if (end - position < nb)
      throw new BufferUnderflowException();
    int p = position;
    position += nb;
    return p;
  }

  /**
   * Checks the current position against the limit, throwing a {@link
   * BufferOverflowException} if it is not smaller than the limit, and then
   * increments the position.
   *
   * @return  The current position value, before it is incremented
   */
  final int nextPutIndex() {                          // package-private
    if (position >= end)
      throw new BufferOverflowException();
    return position++;
  }

  final int nextPutIndex(int nb) {                    // package-private
    if (end - position < nb)
      throw new BufferOverflowException();
    int p = position;
    position += nb;
    return p;
  }

  private byte _get(int i) {                          // package-private
    return bs[i];
  }

  private void _put(int i, byte b) {                  // package-private
    bs[i] = b;
  }

  public byte get() {
    return bs[nextGetIndex()];
  }

  public byte get(int i) {
    return bs[i];
  }

  public void put(byte x) {
    bs[nextPutIndex()] = x;
  }

  public void put(int i, byte x) {
    bs[i] = x;
  }

  // -- get/put char --
  static private char makeChar(byte b1, byte b0) {
    return (char)((b1 << 8) | (b0 & 0xff));
  }

  char getCharL(int bi) {
    return makeChar(_get(bi + 1),
        _get(bi    ));
  }

  char getCharB(int bi) {
    return makeChar(_get(bi    ),
        _get(bi + 1));
  }

  char getChar() {
    return getChar(nextGetIndex(2));
  }

  char getChar(int bi) {
    return bigEndian ? getCharB(bi) : getCharL(bi);
  }

  private static byte char1(char x) { return (byte)(x >> 8); }
  private static byte char0(char x) { return (byte)(x     ); }

  void putCharL(int bi, char x) {
    _put(bi    , char0(x));
    _put(bi + 1, char1(x));
  }

  void putCharB(int bi, char x) {
    _put(bi    , char1(x));
    _put(bi + 1, char0(x));
  }

  void putChar(char x) {
    putChar(nextPutIndex(2), x);
  }

  void putChar(int bi, char x) {
    if (bigEndian)
      putCharB(bi, x);
    else
      putCharL(bi, x);
  }

  // -- get/put short -

  static private short makeShort(byte b1, byte b0) {
    return (short)((b1 << 8) | (b0 & 0xff));
  }

  short getShortL(int bi) {
    return makeShort(_get(bi + 1),
        _get(bi    ));
  }


  short getShortB(int bi) {
    return makeShort(_get(bi    ),
        _get(bi + 1));
  }

  short getShort() {
    return getShort(nextGetIndex(2));
  }

  short getShort(int bi) {
    return bigEndian ? getShortB(bi) : getShortL(bi);
  }

  private static byte short1(short x) { return (byte)(x >> 8); }
  private static byte short0(short x) { return (byte)(x     ); }

  void putShortL(int bi, short x) {
    _put(bi    , short0(x));
    _put(bi + 1, short1(x));
  }

  void putShortB(int bi, short x) {
    _put(bi    , short1(x));
    _put(bi + 1, short0(x));
  }

  void putShort(int bi, short x) {
    if (bigEndian)
      putShortB(bi, x);
    else
      putShortL(bi, x);
  }

  void putShort(short x) {
    putShort(nextPutIndex(2), x);
  }

  // -- get/put int --

  static private int makeInt(byte b3, byte b2, byte b1, byte b0) {
    return (((b3       ) << 24) |
        ((b2 & 0xff) << 16) |
        ((b1 & 0xff) <<  8) |
        ((b0 & 0xff)      ));
  }

  int getIntL(int bi) {
    return makeInt(_get(bi + 3),
        _get(bi + 2),
        _get(bi + 1),
        _get(bi    ));
  }

  int getIntB(int bi) {
    return makeInt(_get(bi    ),
        _get(bi + 1),
        _get(bi + 2),
        _get(bi + 3));
  }

  int getInt() {
    return getInt(nextGetIndex(4));
  }

  int getInt(int bi) {
    return bigEndian ? getIntB(bi) : getIntL(bi);
  }

  private static byte int3(int x) { return (byte)(x >> 24); }
  private static byte int2(int x) { return (byte)(x >> 16); }
  private static byte int1(int x) { return (byte)(x >>  8); }
  private static byte int0(int x) { return (byte)(x      ); }

  void putIntL(int bi, int x) {
    _put(bi + 3, int3(x));
    _put(bi + 2, int2(x));
    _put(bi + 1, int1(x));
    _put(bi    , int0(x));
  }

  void putIntB(int bi, int x) {
    _put(bi    , int3(x));
    _put(bi + 1, int2(x));
    _put(bi + 2, int1(x));
    _put(bi + 3, int0(x));
  }

  void putInt(int bi, int x) {
    if (bigEndian)
      putIntB(bi, x);
    else
      putIntL(bi, x);
  }

  void putInt(int x) {
    putInt(nextPutIndex(4), x);
  }

  // -- get/put long --

  static private long makeLong(byte b7, byte b6, byte b5, byte b4,
                               byte b3, byte b2, byte b1, byte b0)
  {
    return ((((long)b7       ) << 56) |
        (((long)b6 & 0xff) << 48) |
        (((long)b5 & 0xff) << 40) |
        (((long)b4 & 0xff) << 32) |
        (((long)b3 & 0xff) << 24) |
        (((long)b2 & 0xff) << 16) |
        (((long)b1 & 0xff) <<  8) |
        (((long)b0 & 0xff)      ));
  }

  long getLongL(int bi) {
    return makeLong(_get(bi + 7),
        _get(bi + 6),
        _get(bi + 5),
        _get(bi + 4),
        _get(bi + 3),
        _get(bi + 2),
        _get(bi + 1),
        _get(bi    ));
  }

  long getLongB(int bi) {
    return makeLong(_get(bi    ),
        _get(bi + 1),
        _get(bi + 2),
        _get(bi + 3),
        _get(bi + 4),
        _get(bi + 5),
        _get(bi + 6),
        _get(bi + 7));
  }

  long getLong() {
    return getLong(nextGetIndex(8));
  }

  long getLong(int bi) {
    return bigEndian ? getLongB(bi) : getLongL(bi);
  }

  private static byte long7(long x) { return (byte)(x >> 56); }
  private static byte long6(long x) { return (byte)(x >> 48); }
  private static byte long5(long x) { return (byte)(x >> 40); }
  private static byte long4(long x) { return (byte)(x >> 32); }
  private static byte long3(long x) { return (byte)(x >> 24); }
  private static byte long2(long x) { return (byte)(x >> 16); }
  private static byte long1(long x) { return (byte)(x >>  8); }
  private static byte long0(long x) { return (byte)(x      ); }

  void putLongL(int bi, long x) {
    _put(bi + 7, long7(x));
    _put(bi + 6, long6(x));
    _put(bi + 5, long5(x));
    _put(bi + 4, long4(x));
    _put(bi + 3, long3(x));
    _put(bi + 2, long2(x));
    _put(bi + 1, long1(x));
    _put(bi    , long0(x));
  }

  void putLongB(int bi, long x) {
    _put(bi    , long7(x));
    _put(bi + 1, long6(x));
    _put(bi + 2, long5(x));
    _put(bi + 3, long4(x));
    _put(bi + 4, long3(x));
    _put(bi + 5, long2(x));
    _put(bi + 6, long1(x));
    _put(bi + 7, long0(x));
  }

  void putLong(int bi, long x) {
    if (bigEndian)
      putLongB(bi, x);
    else
      putLongL(bi, x);
  }

  void putLong(long x) {
    putLong(nextPutIndex(8), x);
  }

  // -- get/put float --

  float getFloatL(int bi) {
    return Float.intBitsToFloat(getIntL(bi));
  }

  float getFloatB(int bi) {
    return Float.intBitsToFloat(getIntB(bi));
  }

  float getFloat() {
    return getFloat(nextGetIndex(4));
  }

  float getFloat(int bi) {
    return bigEndian ? getFloatB(bi) : getFloatL(bi);
  }

  void putFloatL(int bi, float x) {
    putIntL(bi, Float.floatToRawIntBits(x));
  }

  void putFloatB(int bi, float x) {
    putIntB(bi, Float.floatToRawIntBits(x));
  }

  void putFloat(int bi, float x) {
    if (bigEndian)
      putFloatB(bi, x);
    else
      putFloatL(bi, x);
  }

  void putFloat(float x) {
    putFloat(nextPutIndex(4), x);
  }

  // -- get/put double --

  double getDoubleL(int bi) {
    return Double.longBitsToDouble(getLongL(bi));
  }

  double getDoubleB(int bi) {
    return Double.longBitsToDouble(getLongB(bi));
  }

  double getDouble() {
    return getDouble(nextGetIndex(8));
  }

  double getDouble(int bi) {
    return bigEndian ? getDoubleB(bi) : getDoubleL(bi);
  }

  void putDoubleL(int bi, double x) {
    putLongL(bi, Double.doubleToRawLongBits(x));
  }

  void putDoubleB(int bi, double x) {
    putLongB(bi, Double.doubleToRawLongBits(x));
  }

  void putDouble(int bi, double x) {
    if (bigEndian)
      putDoubleB(bi, x);
    else
      putDoubleL(bi, x);
  }

  void putDouble(double x) {
    putDouble(nextPutIndex(8), x);
  }

  static void checkBounds(int off, int len, int size) { // package-private
    if ((off | len | (off + len) | (size - (off + len))) < 0)
      throw new IndexOutOfBoundsException();
  }

  /**
   * Returns the number of elements between the current position and the
   * limit.
   *
   * @return  The number of elements remaining in this buffer
   */
  public final int remaining() {
    return end - position;
  }

  /**
   * Sets this buffer's position.  If the mark is defined and larger than the
   * new position then it is discarded.
   *
   * @param  newPosition
   *         The new position value; must be non-negative
   *         and no larger than the current limit
   *
   * @return  This buffer
   *
   * @throws  IllegalArgumentException
   *          If the preconditions on <tt>newPosition</tt> do not hold
   */
  public final int position(int newPosition) {
    if ((newPosition > end) || (newPosition < offset))
      throw new IllegalArgumentException();
    position = newPosition;
    return position;
  }

  public final int position() {
    return position;
  }

  public int length() {
    return position - offset;
  }

  public final void skip(int len) {
    if ((position + len > end) || (position + len < offset))
      throw new IllegalArgumentException();
    position += len;
  }

  public final int offset() {
    return offset;
  }

  public final boolean bigEndian() {
    return bigEndian;
  }

  public void getBytes(byte[] dst, int length) {
    if (length > remaining())
      throw new BufferOverflowException();
    System.arraycopy(bs, nextGetIndex(length), dst, 0, length);
  }

  public byte[] getBytes(int length) {
    if (length > remaining())
      throw new BufferOverflowException();
    return getBytes(nextGetIndex(length), length);
  }

  public byte[] getBytes(int pos, int length) {
    if (pos + length > end)
      throw new BufferOverflowException();
    byte[] dst = new byte[length];
    System.arraycopy(bs, pos, dst, 0, length);
    return dst;
  }

  public String getString(int length) {
    return getString(nextGetIndex(length), length);
  }

  public String getString(int pos, int length) {
    if (pos + length > end)
      throw new BufferOverflowException();
    return new String(bs, pos, length);
  }

  public void put(byte[] src, int offset, int length) {
    checkBounds(offset, length, src.length);
    if (length > remaining())
      throw new BufferOverflowException();
    System.arraycopy(src, offset, bs, position(), length);
    position(position + length);
  }

  public void put(byte[] src) {
    put(src, 0, src.length);
  }

  public byte[] toBytes() {
    byte[] bytes = new byte[position-offset];
    System.arraycopy(bs, offset, bytes, 0, position-offset);
    return bytes;
  }

  public void toBytes(byte[] dst, int dstOffset) {
    System.arraycopy(bs, offset, dst, dstOffset, position-offset);
  }
}
