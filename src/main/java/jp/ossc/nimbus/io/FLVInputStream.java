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
import java.util.*;

import jp.ossc.nimbus.util.converter.PaddingByteArrayConverter;

/**
 * FLV（Fixed Length Value）形式のInputStreamクラス。<p>
 * <pre>
 * import java.io.*;
 * import jp.ossc.nimbus.io.FLVInputStream;
 *
 * FileInputStream fis = new FileInputStream("sample.csv");
 * FLVInputStream flvis = new FLVInputStream(fis);
 * flvis.setFieldLength(new int[]{5,10});
 * try{
 *     byte[][] flv = null;
 *     while((flv = flvis.readFLV()) != null){
 *           :
 *     }
 * }finally{
 *     flvis.close();
 * }
 * </pre>
 * 
 * @author M.Takata
 */
public class FLVInputStream extends BufferedInputStream{
    
    protected int[] fieldLength;
    protected PaddingByteArrayConverter[] converters;
    
    protected FLVIterator iterator;
    
    protected InputStreamWrapper inputStreamWrapper;
    
    /**
     * デフォルトの読み込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     */
    public FLVInputStream(){
        this(new InputStreamWrapper());
    }
    
    /**
     * デフォルトの読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param in 読み込み元のInputStream
     */
    public FLVInputStream(InputStream in){
        this(in, null, null);
    }
    
    /**
     * デフォルトの読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param in 読み込み元のInputStream
     * @param fieldLen フィールド長の配列
     */
    public FLVInputStream(InputStream in, int[] fieldLen){
        this(in, fieldLen, null);
    }
    
    /**
     * デフォルトの読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param fieldLen フィールド長の配列
     */
    public FLVInputStream(int[] fieldLen){
        this(new InputStreamWrapper(), fieldLen, null);
    }
    
    /**
     * デフォルトの読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param fieldLen フィールド長の配列
     * @param convs フィールドのパディングを解除するコンバータ配列
     */
    public FLVInputStream(int[] fieldLen, PaddingByteArrayConverter[] convs){
        this(new InputStreamWrapper(), fieldLen, convs);
    }
    
    /**
     * デフォルトの読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param in 読み込み元のInputStream
     * @param fieldLen フィールド長の配列
     * @param convs フィールドのパディングを解除するコンバータ配列
     */
    public FLVInputStream(InputStream in, int[] fieldLen, PaddingByteArrayConverter[] convs){
        super(in instanceof InputStreamWrapper ? in : new InputStreamWrapper(in));
        inputStreamWrapper = (InputStreamWrapper)this.in;
        setFieldLength(fieldLen);
        converters = convs;
    }
    
    /**
     * 指定された読み込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     *
     * @param size バッファサイズ
     */
    public FLVInputStream(int size){
        this(new InputStreamWrapper(), size);
    }
    
    /**
     * 指定された読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param in 読み込み元のInputStream
     * @param size バッファサイズ
     */
    public FLVInputStream(InputStream in, int size){
        this(in, size, null, null);
    }
    
    /**
     * 指定された読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param in 読み込み元のInputStream
     * @param size バッファサイズ
     * @param fieldLen フィールド長の配列
     */
    public FLVInputStream(InputStream in, int size, int[] fieldLen){
        this(in, size, fieldLen, null);
    }
    
    /**
     * 指定された読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param size バッファサイズ
     * @param fieldLen フィールド長の配列
     */
    public FLVInputStream(int size, int[] fieldLen){
        this(new InputStreamWrapper(), size, fieldLen, null);
    }
    
    /**
     * 指定された読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param size バッファサイズ
     * @param fieldLen フィールド長の配列
     * @param convs フィールドのパディングを解除するコンバータ配列
     */
    public FLVInputStream(int size, int[] fieldLen, PaddingByteArrayConverter[] convs){
        this(new InputStreamWrapper(), size, fieldLen, convs);
    }
    
    /**
     * 指定された読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param in 読み込み元のInputStream
     * @param size バッファサイズ
     * @param fieldLen フィールド長の配列
     * @param convs フィールドのパディングを解除するコンバータ配列
     */
    public FLVInputStream(InputStream in, int size, int[] fieldLen, PaddingByteArrayConverter[] convs){
        super(in instanceof InputStreamWrapper ? in : new InputStreamWrapper(in), size);
        inputStreamWrapper = (InputStreamWrapper)this.in;
        setFieldLength(fieldLen);
        converters = convs;
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
    public void setFieldPaddingByteArrayConverter(PaddingByteArrayConverter[] convs){
        converters = convs;
    }
    
    /**
     * 各フィールドのパディングの解除を行うコンバータを取得する。<p>
     *
     * @return パディングの解除を行うコンバータの配列
     */
    public PaddingByteArrayConverter[] getPaddingByteArrayConverter(){
        return converters;
    }
    
    /**
     * InputStreamを設定する。<p>
     *
     * @param is InputStream
     */
    public void setInputStream(InputStream is){
        inputStreamWrapper.setInputStream(is);
    }
    
    /**
     * 指定されたFLV数分スキップする。<p>
     *
     * @param num スキップするFLV数
     * @return スキップされたFLV数
     * @exception IOException 入出力エラーが発生した場合
     */
    public long skipFLV(long num) throws IOException{
        List flv = null;
        int result = 0;
        for(result = 0; result < num; result++){
            flv = readFLVList(flv);
            if(flv == null){
                break;
            }
        }
        return result;
    }
    
    /**
     * FLVを1件読み込む。<p>
     *
     * @return FLV要素のバイト配列の配列
     * @exception IOException 入出力エラーが発生した場合
     */
    public byte[][] readFLV() throws IOException{
        final List flv = readFLVList();
        return flv == null ? null
             : (byte[][])flv.toArray(new byte[flv.size()][]);
    }
    
    /**
     * FLVを1件読み込む。<p>
     *
     * @return FLV要素のバイト配列のリスト
     * @exception IOException 入出力エラーが発生した場合
     */
    public List readFLVList() throws IOException{
        return readFLVList(null);
    }
    
    /**
     * FLVを1件読み込む。<p>
     * FLV要素の文字列を格納するリストを再利用するためのメソッドである。<br>
     *
     * @param flv FLV要素のバイト配列を格納するリスト
     * @return FLV要素のバイト配列リスト
     * @exception IOException 入出力エラーが発生した場合
     */
    public List readFLVList(List flv) throws IOException{
        if(flv == null){
            flv = new ArrayList();
        }else{
            flv.clear();
        }
        for(int i = 0; i < fieldLength.length; i++){
            byte[] element = new byte[fieldLength[i]];
            int offset = 0;
            int readLength = 0;
            while(offset < element.length && (readLength = read(element, offset, element.length - offset)) != -1){
                offset += readLength;
            }
            if(readLength >= 0){
                if(converters != null && converters.length != 0 && converters[i] != null){
                    element = converters[i].parse(element);
                }
                flv.add(element);
            }else if(flv.size() == 0){
                return null;
            }else{
                throw new EOFException();
            }
        }
        return flv;
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
     * FLV要素の繰り返し。<p>
     *
     * @author M.Takata
     */
    public class FLVIterator{
        private boolean hasNext = false;
        private List elements = new ArrayList();
        
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
            List result = readFLVList(elements);
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
         * ここで取得されるListは、毎回再利用される。<br>
         *
         * @return 次のFLV要素。次のFLV要素がない場合はnull
         * @exception IOException 読み込みに失敗した場合
         */
        public List nextElements() throws IOException{
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
    public FLVInputStream cloneInputStream(){
        return cloneInputStream(new FLVInputStream());
    }
    
    /**
     * 未接続の複製を生成する。<p>
     *
     * @param clone 未接続のインスタンス
     * @return 未接続の複製
     */
    protected FLVInputStream cloneInputStream(FLVInputStream clone){
        clone.setFieldLength(fieldLength);
        if(converters != null && converters.length != 0){
            clone.converters = new PaddingByteArrayConverter[converters.length];
            System.arraycopy(converters, 0, clone.converters, 0, converters.length);
        }
        return clone;
    }
    
    private static class InputStreamWrapper extends InputStream{
        
        private InputStream realInputStream;
        
        public InputStreamWrapper(){
        }
        
        public InputStreamWrapper(InputStream is){
            realInputStream = is;
        }
        
        public InputStream getInputStream(){
            return realInputStream;
        }
        
        public void setInputStream(InputStream is){
            realInputStream = is;
        }
        
        public int read() throws IOException{
            if(realInputStream == null){
                return -1;
            }else{
                return realInputStream.read();
            }
        }
        
        public int read(byte[] b) throws IOException{
            if(realInputStream == null){
                return -1;
            }else{
                return realInputStream.read(b);
            }
        }
        
        public int read(byte[] b, int off, int len) throws IOException{
            if(realInputStream == null){
                return -1;
            }else{
                return realInputStream.read(b, off, len);
            }
        }
        
        public long skip(long n) throws IOException{
            if(realInputStream == null){
                return 0;
            }else{
                return realInputStream.skip(n);
            }
        }
        
        public int available() throws IOException{
            if(realInputStream == null){
                return 0;
            }else{
                return realInputStream.available();
            }
        }
        
        public void mark(int readlimit){
            if(realInputStream != null){
                realInputStream.mark(readlimit);
            }
        }
        
        public void reset() throws IOException{
            if(realInputStream != null){
                realInputStream.reset();
            }
        }
        
        public boolean markSupported(){
            if(realInputStream == null){
                return false;
            }else{
                return realInputStream.markSupported();
            }
        }
        
        public void close() throws IOException{
            if(realInputStream != null){
                realInputStream.close();
            }
        }
    }
}