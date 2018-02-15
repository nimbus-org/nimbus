/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 * 
 * Copyright 2003 The Nimbus Project. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE NIMBUS PROJECT ``AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE NIMBUS PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of the Nimbus Project.
 */
package jp.ossc.nimbus.io;

import java.io.*;
import java.math.*;
import java.util.*;
import java.nio.*;
import java.lang.reflect.*;

import jp.ossc.nimbus.util.converter.PaddingStringConverter;

/**
 * FLV（Fixed Length Value）形式のReaderクラス。<p>
 * <pre>
 * import java.io.*;
 * import jp.ossc.nimbus.io.FLVReader;
 *
 * FileInputStream fis = new FileInputStream("sample.csv");
 * InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
 * FLVReader reader = new FLVReader(isr);
 * reader.setFieldLength(new int[]{5,10});
 * reader.setEncoding("UTF-8");
 * try{
 *     String[] flv = null;
 *     while((flv = reader.readFLVLine()) != null){
 *           :
 *     }
 * }finally{
 *     reader.close();
 * }
 * </pre>
 * 
 * @author M.Takata
 */
public class FLVReader extends LineNumberReader{
    
    /**
     * デフォルトの改行文字。<p>
     */
    public static final String LINE_SEPARATOR
         = System.getProperty("line.separator");
    
    protected String encoding;
    protected int[] fieldLength;
    protected PaddingStringConverter[] converters;
    
    protected boolean isIgnoreEmptyLine;
    protected String commentPrefix;
    
    protected FLVIterator iterator;
    
    protected ReaderWrapper readerWrapper;
    
    /**
     * デフォルトの読み込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     */
    public FLVReader(){
        this(new ReaderWrapper());
    }
    
    /**
     * デフォルトの読み込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     *
     * @param fieldLen フィールド長の配列
     */
    public FLVReader(int[] fieldLen){
        this(fieldLen, null);
    }
    
    /**
     * デフォルトの読み込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     *
     * @param fieldLen フィールド長の配列
     * @param encoding 文字エンコーディング
     */
    public FLVReader(int[] fieldLen, String encoding){
        this(new ReaderWrapper(), fieldLen, encoding);
    }
    
    /**
     * デフォルトの読み込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     *
     * @param fieldLen フィールド長の配列
     * @param convs フィールドのパディングを解除するコンバータ配列
     * @param encoding 文字エンコーディング
     */
    public FLVReader(int[] fieldLen, PaddingStringConverter[] convs, String encoding){
        this(new ReaderWrapper(), fieldLen, convs, encoding);
    }
    
    /**
     * デフォルトの読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param reader 読み込み元のReader
     */
    public FLVReader(Reader reader){
        this(reader, null);
    }
    
    /**
     * デフォルトの読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param reader 読み込み元のReader
     * @param fieldLen フィールド長の配列
     */
    public FLVReader(Reader reader, int[] fieldLen){
        this(reader, fieldLen, null);
    }
    
    /**
     * デフォルトの読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param reader 読み込み元のReader
     * @param fieldLen フィールド長の配列
     * @param encoding 文字エンコーディング
     */
    public FLVReader(Reader reader, int[] fieldLen, String encoding){
        this(reader, fieldLen, null, encoding);
    }
    
    /**
     * デフォルトの読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param reader 読み込み元のReader
     * @param fieldLen フィールド長の配列
     * @param convs フィールドのパディングを解除するコンバータ配列
     * @param encoding 文字エンコーディング
     */
    public FLVReader(Reader reader, int[] fieldLen, PaddingStringConverter[] convs, String encoding){
        super(reader instanceof ReaderWrapper ? reader : new ReaderWrapper(reader));
        readerWrapper = (ReaderWrapper)lock;
        fieldLength = fieldLen;
        converters = convs;
        this.encoding = encoding;
    }
    
    /**
     * 指定された読み込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     *
     * @param size 読み込みバッファサイズ
     */
    public FLVReader(int size){
        this(size, null);
    }
    
    /**
     * 指定された読み込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     *
     * @param size 読み込みバッファサイズ
     * @param fieldLen フィールド長の配列
     */
    public FLVReader(int size, int[] fieldLen){
        this(size, fieldLen, null);
    }
    
    /**
     * 指定された読み込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     *
     * @param size 読み込みバッファサイズ
     * @param fieldLen フィールド長の配列
     * @param encoding 文字エンコーディング
     */
    public FLVReader(int size, int[] fieldLen, String encoding){
        this(size, fieldLen, null, encoding);
    }
    
    /**
     * 指定された読み込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     *
     * @param size 読み込みバッファサイズ
     * @param fieldLen フィールド長の配列
     * @param convs フィールドのパディングを解除するコンバータ配列
     * @param encoding 文字エンコーディング
     */
    public FLVReader(int size, int[] fieldLen, PaddingStringConverter[] convs, String encoding){
        this(new ReaderWrapper(), size, fieldLen, convs, encoding);
    }
    
    /**
     * 指定された読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param reader 読み込み元のReader
     * @param size 読み込みバッファサイズ
     */
    public FLVReader(Reader reader, int size){
        this(reader, size, null);
    }
    
    /**
     * 指定された読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param reader 読み込み元のReader
     * @param size 読み込みバッファサイズ
     * @param fieldLen フィールド長の配列
     */
    public FLVReader(Reader reader, int size, int[] fieldLen){
        this(reader, size, fieldLen, null);
    }
    
    /**
     * 指定された読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param reader 読み込み元のReader
     * @param size 読み込みバッファサイズ
     * @param fieldLen フィールド長の配列
     * @param encoding 文字エンコーディング
     */
    public FLVReader(Reader reader, int size, int[] fieldLen, String encoding){
        this(reader, size, fieldLen, null, encoding);
    }
    
    /**
     * 指定された読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param reader 読み込み元のReader
     * @param size 読み込みバッファサイズ
     * @param fieldLen フィールド長の配列
     * @param convs フィールドのパディングを解除するコンバータ配列
     * @param encoding 文字エンコーディング
     */
    public FLVReader(Reader reader, int size, int[] fieldLen, PaddingStringConverter[] convs, String encoding){
        super(reader instanceof ReaderWrapper ? reader : new ReaderWrapper(reader), size);
        readerWrapper = (ReaderWrapper)lock;
        fieldLength = fieldLen;
        converters = convs;
        this.encoding = encoding;
    }
    
    /**
     * 各フィールドのバイト長を設定する。<p>
     *
     * @param length フィールド長の配列
     */
    public void setFieldLength(int[] length){
        fieldLength = length;
    }
    
    /**
     * 各フィールドのバイト長を取得する。<p>
     *
     * @return フィールド長の配列
     */
    public int[] getFieldLength(){
        return fieldLength;
    }
    
    /**
     * 各フィールドのパディングの解除を行うコンバータを設定する。<p>
     *
     * @param convs パディングの解除を行うコンバータの配列
     */
    public void setFieldPaddingStringConverter(PaddingStringConverter[] convs){
        converters = convs;
    }
    
    /**
     * 各フィールドのパディングの解除を行うコンバータを取得する。<p>
     *
     * @return パディングの解除を行うコンバータの配列
     */
    public PaddingStringConverter[] getFieldPaddingStringConverter(){
        return converters;
    }
    
    /**
     * 文字エンコーディングを設定する。<p>
     *
     * @param encoding 文字エンコーディング
     */
    public void setEncoding(String encoding){
        this.encoding = encoding;
    }
    
    /**
     * 文字エンコーディングを取得する。<p>
     *
     * @return 文字エンコーディング
     */
    public String getEncoding(){
        return encoding;
    }
    
    /**
     * Readerを設定する。<p>
     *
     * @param reader Reader
     */
    public void setReader(Reader reader){
        readerWrapper.setReader(reader);
    }
    
    /**
     * 空行を無視するかどうかを設定する。<p>
     * 空行を無視するように設定した場合、空行は行数としてもカウントされない。<br>
     * デフォルトは、falseで無視しない。<br>
     *
     * @param isIgnore 空行を無視する場合true
     */
    public void setIgnoreEmptyLine(boolean isIgnore){
        isIgnoreEmptyLine = isIgnore;
    }
    
    /**
     * 空行を無視するかどうかを判定する。<p>
     *
     * @return trueの場合、空行を無視する
     */
    public boolean isIgnoreEmptyLine(){
         return isIgnoreEmptyLine;
    }
    
    /**
     * コメント行の前置文字列を設定する。<p>
     *
     * @param value コメント行の前置文字列
     */
    public void setCommentPrefix(String value){
        commentPrefix = value;
    }
    
    /**
     * コメント行の前置文字列を取得する。<p>
     *
     * @return コメント行の前置文字列
     */
    public String getCommentPrefix(){
        return commentPrefix;
    }
    
    /**
     * 指定された行数分スキップする。<p>
     *
     * @param line スキップする行数
     * @return スキップされた行数
     * @exception IOException 入出力エラーが発生した場合
     */
    public long skipLine(long line) throws IOException{
        int result = 0;
        for(result = 0; result < line; result++){
            if(super.readLine() == null){
                break;
            }
        }
        return result;
    }
    
    /**
     * 指定されたFLV行数分スキップする。<p>
     * {@link #isIgnoreEmptyLine()}がtrueの場合は、空行はスキップ行数のカウントから除かれる。<br>
     *
     * @param line スキップする行数
     * @return スキップされた行数
     * @exception IOException 入出力エラーが発生した場合
     */
    public long skipFLVLine(long line) throws IOException{
        List flv = null;
        int result = 0;
        for(result = 0; result < line; result++){
            flv = readFLVLineList(flv);
            if(flv == null){
                break;
            }
        }
        return result;
    }
    
    /**
     * FLV行を1行読み込む。<p>
     *
     * @return FLV要素の文字列配列
     * @exception IOException 入出力エラーが発生した場合
     */
    public String[] readFLVLine() throws IOException{
        final List flv = readFLVLineList();
        return flv == null ? null
             : (String[])flv.toArray(new String[flv.size()]);
    }
    
    /**
     * FLV行を1行読み込む。<p>
     *
     * @return FLV要素の文字列リスト
     * @exception IOException 入出力エラーが発生した場合
     */
    public List readFLVLineList() throws IOException{
        return readFLVLineList(null);
    }
    
    /**
     * FLV行を1行読み込む。<p>
     * FLV要素の文字列を格納するリストを再利用するためのメソッドである。<br>
     *
     * @param flv FLV要素の文字列を格納するリスト
     * @return FLV要素の文字列リスト
     * @exception IOException 入出力エラーが発生した場合
     */
    public List readFLVLineList(List flv) throws IOException{
        String line = null;
        do{
            line = readLine();
            if(line == null){
                if(flv != null){
                    flv.clear();
                }
                return null;
            }
            if((isIgnoreEmptyLine && line.length() == 0)
                    || (commentPrefix != null && line.startsWith(commentPrefix))
            ){
                line = null;
                setLineNumber(getLineNumber() - 1);
            }
        }while(line == null);
        if(flv == null){
            flv = new ArrayList();
        }else{
            flv.clear();
        }
        if(line.length() == 0){
            return flv;
        }
        byte[] bytes = null;
        if(encoding == null){
            bytes = line.getBytes();
        }else{
            bytes = line.getBytes(encoding);
        }
        int offset = 0;
        for(int i = 0; i < fieldLength.length; i++){
            if(bytes.length < offset + fieldLength[i]){
                throw new EOFException();
            }
            String element = null;
            if(encoding == null){
                element = new String(bytes, offset, fieldLength[i]);
            }else{
                element = new String(bytes, offset, fieldLength[i], encoding);
            }
            if(converters != null && converters.length != 0 && converters[i] != null){
                element = converters[i].parse(element);
            }
            flv.add(element);
            offset += fieldLength[i];
        }
        return flv;
    }
    
    public String readLine() throws IOException{
        if(readerWrapper.getReader() instanceof BufferedReader){
            return ((BufferedReader)readerWrapper.getReader()).readLine();
        }else{
            return super.readLine();
        }
    }
    
    /**
     * {@link FLVReader.FLVElements}の繰り返しを取得する。<p>
     *
     * @return FLVElementsの繰り返し
     */
    public FLVIterator iterator(){
        if(iterator == null){
            iterator = new FLVIterator();
        }
        return iterator;
    }
    
    /**
     * {@link FLVReader.FLVElements}の繰り返し。<p>
     *
     * @author M.Takata
     */
    public class FLVIterator{
        private boolean hasNext = false;
        private FLVElements elements = new FLVElements();
        
        private FLVIterator(){}
        
        /**
         * 次のFLV要素があるかどうかを判定する。<p>
         *
         * @return 次のFLV要素がある場合はtrue
         * @exception IOException 読み込みに失敗した場合
         */
        public boolean hasNext() throws IOException{
            if(hasNext){
                return hasNext;
            }
            List result = readFLVLineList(elements);
            hasNext = result != null;
            return hasNext;
        }
        
        /**
         * 次のFLV要素を取得する。<p>
         *
         * @return 次のFLV要素。次のFLV要素がない場合はnull
         * @exception IOException 読み込みに失敗した場合
         * @see #nextElements()
         */
        public Object next() throws IOException{
            return nextElements();
        }
        
        /**
         * 次のFLV要素を取得する。<p>
         * ここで取得される{@link FLVReader.FLVElements}は、毎回再利用される。<br>
         *
         * @return 次のFLV要素。次のFLV要素がない場合はnull
         * @exception IOException 読み込みに失敗した場合
         */
        public FLVElements nextElements() throws IOException{
            if(!hasNext){
                if(!hasNext()){
                    return null;
                }
            }
            hasNext = false;
            return elements;
        }
    }
    
    /**
     * 未接続の複製を生成する。<p>
     *
     * @return 未接続の複製
     */
    public FLVReader cloneReader(){
        return cloneReader(new FLVReader());
    }
    
    /**
     * 未接続の複製を生成する。<p>
     *
     * @param clone 未接続のインスタンス
     * @return 未接続の複製
     */
    protected FLVReader cloneReader(FLVReader clone){
        clone.encoding = encoding;
        clone.fieldLength = fieldLength;
        clone.isIgnoreEmptyLine = isIgnoreEmptyLine;
        clone.commentPrefix = commentPrefix;
        if(converters != null && converters.length != 0){
            clone.converters = new PaddingStringConverter[converters.length];
            System.arraycopy(converters, 0, clone.converters, 0, converters.length);
        }
        return clone;
    }
    
    /**
     * FLV形式データの1行を表すFLV要素。<p>
     * 
     * @author M.Takata
     */
    public class FLVElements extends ArrayList{
        
        private static final long serialVersionUID = 4888164167750345490L;
        
        private boolean wasNull;
        
        private FLVElements(){}
        
        /**
         * このFLV要素をクリアする。<p>
         */
        public void clear(){
            wasNull = false;
            super.clear();
        }
        
        /**
         * 取得した値がnullだったかどうかを判定する。<p>
         * {@link #getInt(int)}などの、数値系のgetterで値を取得した場合、値がnullや空文字だった場合に、0を返す。その時、値が0だったのかnullまたは空文字だったのかを判断するのに使用する。<br>
         *
         * @return 取得した値がnullだった場合true
         */
        public boolean wasNull(){
            return wasNull;
        }
        
        /**
         * 指定されたインデックスの要素を取得する。<p>
         *
         * @param index インデックス
         * @return 指定されたインデックスの要素
         */
        public Object get(int index){
            Object obj = super.get(index);
            wasNull = obj == null;
            return obj;
        }
        
        /**
         * 指定されたインデックスの要素文字列を取得する。<p>
         *
         * @param index インデックス
         * @return 指定されたインデックスの要素文字列
         */
        public String getString(int index){
            String str = (String)get(index);
            wasNull = str == null;
            return str;
        }
        
        /**
         * 指定されたインデックスの要素バイトを取得する。<p>
         *
         * @param index インデックス
         * @return 指定されたインデックスの要素バイト
         * @exception NumberFormatException 要素がバイト文字列でない場合
         */
        public byte getByte(int index) throws NumberFormatException{
            return getByte(index, 10);
        }
        
        /**
         * 指定されたインデックスの要素バイトを取得する。<p>
         * 指定された要素がnullまたは空文字の場合は、0を返し、{@link #wasNull()}がtrueを返す。<br>
         *
         * @param index インデックス
         * @param radix 基数
         * @return 指定されたインデックスの要素バイト
         * @exception NumberFormatException 要素がバイト文字列でない場合
         */
        public byte getByte(int index, int radix) throws NumberFormatException{
            String str = getString(index);
            if(str != null && str.length() == 0){
                str = str.trim();
            }
            if(str == null || str.length() == 0){
                wasNull = true;
                return (byte)0;
            }
            return Byte.parseByte(str, radix);
        }
        
        /**
         * 指定されたインデックスの要素数値を取得する。<p>
         * 指定された要素がnullまたは空文字の場合は、0を返し、{@link #wasNull()}がtrueを返す。<br>
         *
         * @param index インデックス
         * @return 指定されたインデックスの要素数値
         * @exception NumberFormatException 要素が数値文字列でない場合
         */
        public short getShort(int index) throws NumberFormatException{
            String str = getString(index);
            if(str != null && str.length() == 0){
                str = str.trim();
            }
            if(str == null || str.length() == 0){
                wasNull = true;
                return (short)0;
            }
            return Short.parseShort(str);
        }
        
        /**
         * 指定されたインデックスの要素文字を取得する。<p>
         * 指定された要素がnullまたは空文字の場合は、0を返し、{@link #wasNull()}がtrueを返す。<br>
         * また、指定された要素が、複数文字から成る場合は、1文字目を返す。<br>
         *
         * @param index インデックス
         * @return 指定されたインデックスの要素文字
         */
        public char getChar(int index){
            final String str = getString(index);
            if(str == null || str.length() == 0){
                wasNull = true;
                return (char)0;
            }
            return str.charAt(0);
        }
        
        /**
         * 指定されたインデックスの要素数値を取得する。<p>
         * 指定された要素がnullまたは空文字の場合は、0を返し、{@link #wasNull()}がtrueを返す。<br>
         *
         * @param index インデックス
         * @return 指定されたインデックスの要素数値
         * @exception NumberFormatException 要素が数値文字列でない場合
         */
        public int getInt(int index) throws NumberFormatException{
            String str = getString(index);
            if(str != null && str.length() == 0){
                str = str.trim();
            }
            if(str == null || str.length() == 0){
                wasNull = true;
                return (int)0;
            }
            return Integer.parseInt(str);
        }
        
        /**
         * 指定されたインデックスの要素数値を取得する。<p>
         * 指定された要素がnullまたは空文字の場合は、0を返し、{@link #wasNull()}がtrueを返す。<br>
         *
         * @param index インデックス
         * @return 指定されたインデックスの要素数値
         * @exception NumberFormatException 要素が数値文字列でない場合
         */
        public long getLong(int index) throws NumberFormatException{
            String str = getString(index);
            if(str != null && str.length() == 0){
                str = str.trim();
            }
            if(str == null || str.length() == 0){
                wasNull = true;
                return 0l;
            }
            return Long.parseLong(str);
        }
        
        /**
         * 指定されたインデックスの要素数値を取得する。<p>
         * 指定された要素がnullまたは空文字の場合は、0を返し、{@link #wasNull()}がtrueを返す。<br>
         *
         * @param index インデックス
         * @return 指定されたインデックスの要素数値
         * @exception NumberFormatException 要素が数値文字列でない場合
         */
        public float getFloat(int index) throws NumberFormatException{
            String str = getString(index);
            if(str != null && str.length() == 0){
                str = str.trim();
            }
            if(str == null || str.length() == 0){
                wasNull = true;
                return 0.0f;
            }
            return Float.parseFloat(str);
        }
        
        /**
         * 指定されたインデックスの要素数値を取得する。<p>
         * 指定された要素がnullまたは空文字の場合は、0を返し、{@link #wasNull()}がtrueを返す。<br>
         *
         * @param index インデックス
         * @return 指定されたインデックスの要素数値
         * @exception NumberFormatException 要素が数値文字列でない場合
         */
        public double getDouble(int index) throws NumberFormatException{
            String str = getString(index);
            if(str != null && str.length() == 0){
                str = str.trim();
            }
            if(str == null || str.length() == 0){
                wasNull = true;
                return 0.0d;
            }
            return Double.parseDouble(str);
        }
        
        /**
         * 指定されたインデックスの要素フラグを取得する。<p>
         * 指定された要素がnullまたは空文字の場合は、falseを返し、{@link #wasNull()}がtrueを返す。<br>
         *
         * @param index インデックス
         * @return 指定されたインデックスの要素フラグ
         */
        public boolean getBoolean(int index){
            String str = getString(index);
            if(str != null && str.length() == 0){
                str = str.trim();
            }
            if(str == null || str.length() == 0){
                wasNull = true;
                return false;
            }
            return Boolean.valueOf(str).booleanValue();
        }
        
        /**
         * 指定されたインデックスの要素数値を取得する。<p>
         * 指定された要素がnullまたは空文字の場合は、nullを返し、{@link #wasNull()}がtrueを返す。<br>
         *
         * @param index インデックス
         * @return 指定されたインデックスの要素数値
         * @exception NumberFormatException 要素が数値文字列でない場合
         */
        public BigInteger getBigInteger(int index) throws NumberFormatException{
            String str = getString(index);
            if(str != null && str.length() == 0){
                str = str.trim();
            }
            if(str == null || str.length() == 0){
                wasNull = true;
                return null;
            }
            return new BigInteger(str);
        }
        
        /**
         * 指定されたインデックスの要素数値を取得する。<p>
         * 指定された要素がnullまたは空文字の場合は、nullを返し、{@link #wasNull()}がtrueを返す。<br>
         *
         * @param index インデックス
         * @return 指定されたインデックスの要素数値
         * @exception NumberFormatException 要素が数値文字列でない場合
         */
        public BigDecimal getBigDecimal(int index) throws NumberFormatException{
            String str = getString(index);
            if(str != null && str.length() == 0){
                str = str.trim();
            }
            if(str == null || str.length() == 0){
                wasNull = true;
                return null;
            }
            return new BigDecimal(str);
        }
    }
    
    private static class ReaderWrapper extends Reader{
        
        private Reader realReader;
        private static Method READ_CHARBUFFER_METHOD = null;
        static{
            try{
                READ_CHARBUFFER_METHOD = Reader.class.getMethod(
                    "read",
                    new Class[]{CharBuffer.class}
                );
            }catch(NoSuchMethodException e){
            }
        }
        
        public ReaderWrapper(){
        }
        
        public ReaderWrapper(Reader reader){
            realReader = reader;
        }
        
        public Reader getReader(){
            return realReader;
        }
        
        public void setReader(Reader reader){
            realReader = reader;
        }
        
        public int read(CharBuffer target) throws IOException{
            if(READ_CHARBUFFER_METHOD == null){
                throw new UnsupportedOperationException("No such method.");
            }
            if(realReader == null){
                return -1;
            }else{
                try{
                    return ((Integer)READ_CHARBUFFER_METHOD.invoke(realReader, new Object[]{target})).intValue();
                }catch(InvocationTargetException e){
                    Throwable th = e.getTargetException();
                    if(th instanceof IOException){
                        throw (IOException)th;
                    }else if(th instanceof RuntimeException){
                        throw (RuntimeException)th;
                    }else if(th instanceof Error){
                        throw (Error)th;
                    }else{
                        throw new UndeclaredThrowableException(th);
                    }
                }catch(IllegalAccessException e){
                    throw new UnsupportedOperationException(e.toString());
                }
            }
        }
        
        public int read() throws IOException{
            if(realReader == null){
                return -1;
            }else{
                return realReader.read();
            }
        }
        
        public int read(char[] cbuf) throws IOException{
            if(realReader == null){
                return -1;
            }else{
                return realReader.read(cbuf);
            }
        }
        
        public int read(char[] cbuf, int off, int len) throws IOException{
            if(realReader == null){
                return -1;
            }else{
                return realReader.read(cbuf, off, len);
            }
        }
        
        public long skip(long n) throws IOException{
            if(realReader == null){
                return 0;
            }else{
                return realReader.skip(n);
            }
        }
        
        public boolean ready() throws IOException{
            if(realReader == null){
                return false;
            }else{
                return realReader.ready();
            }
        }
        
        public boolean markSupported(){
            if(realReader == null){
                return false;
            }else{
                return realReader.markSupported();
            }
        }
        
        public void mark(int readAheadLimit) throws IOException{
            if(realReader == null){
                throw new IOException("Reader is null.");
            }else{
                realReader.mark(readAheadLimit);
            }
        }
        
        public void reset() throws IOException{
            if(realReader != null){
                realReader.reset();
            }
        }
        
        public void close() throws IOException{
            if(realReader != null){
                realReader.close();
            }
        }
    }
}
