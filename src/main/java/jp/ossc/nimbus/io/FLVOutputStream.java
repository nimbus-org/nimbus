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
import java.util.List;

import jp.ossc.nimbus.util.converter.PaddingByteArrayConverter;

/**
 * FLV形式のWriterクラス。<p>
 * <pre>
 * import java.io.*;
 * import jp.ossc.nimbus.io.FLVOutputStream;
 *
 * FileOutputStream fos = new FileOutputStream("sample.csv");
 * FLVOutputStream flvos = new FLVOutputStream(
 *     fos,
 *     new PaddingByteArrayConverter[]{
 *         new PaddingByteArrayConverter(10),
 *         new PaddingByteArrayConverter(12),
 *     }
 * );
 * byte[][] flv = new byte[2][];
 * try{
 *     flv[0] = "hoge".getBytes();
 *     flv[1] = "100".getBytes();
 *     flvos.writeFLV(flv);
 *        :
 * }finally{
 *     flvos.close();
 * }
 * </pre>
 * 
 * @author M.Takata
 */
public class FLVOutputStream extends BufferedOutputStream{
    
    protected int fieldIndex;
    protected byte[] nullValue;
    protected PaddingByteArrayConverter[] converters;
    
    protected OutputStreamWrapper outputStreamWrapper;
    
    /**
     * デフォルトの書き込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     */
    public FLVOutputStream(){
        this(new OutputStreamWrapper());
    }
    
    /**
     * デフォルトの書き込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param os 書き込み先のOutputStream
     */
    public FLVOutputStream(OutputStream os){
        this(os, null);
    }
    
    /**
     * デフォルトの書き込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param os 書き込み先のOutputStream
     * @param convs フィールドをパディングするコンバータ配列
     */
    public FLVOutputStream(OutputStream os, PaddingByteArrayConverter[] convs){
        super(os instanceof OutputStreamWrapper ? os : new OutputStreamWrapper(os));
        outputStreamWrapper = (OutputStreamWrapper)out;
        converters = convs;
    }
    
    /**
     * 指定された書き込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     *
     * @param size 書き込みバッファサイズ
     */
    public FLVOutputStream(int size){
        this(new OutputStreamWrapper(), size);
    }
    
    /**
     * 指定された書き込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param os 書き込み先のOutputStream
     * @param size 書き込みバッファサイズ
     */
    public FLVOutputStream(OutputStream os, int size){
        this(os, null, size);
    }
    
    /**
     * 指定された書き込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param os 書き込み先のOutputStream
     * @param convs フィールドをパディングするコンバータ配列
     * @param size 書き込みバッファサイズ
     */
    public FLVOutputStream(OutputStream os, PaddingByteArrayConverter[] convs, int size){
        super(os instanceof OutputStreamWrapper ? os : new OutputStreamWrapper(os), size);
        outputStreamWrapper = (OutputStreamWrapper)out;
        converters = convs;
    }
    
    /**
     * OutputStreamを設定する。<p>
     *
     * @param os 書き込み先のOutputStream
     */
    public void setOutputStream(OutputStream os){
        outputStreamWrapper.setOutputStream(os);
        fieldIndex = 0;
    }
    
    /**
     * 各フィールドのパディングを行うコンバータを設定する。<p>
     *
     * @param convs パディングを行うコンバータの配列
     */
    public void setFieldPaddingByteArrayConverter(PaddingByteArrayConverter[] convs){
        converters = convs;
    }
    
    /**
     * 各フィールドのパディングを行うコンバータを取得する。<p>
     *
     * @return パディングを行うコンバータの配列
     */
    public PaddingByteArrayConverter[] getFieldPaddingByteArrayConverter(){
        return converters;
    }
    
    /**
     * nullをFLV要素として書き込もうとした場合に、出力するバイト配列を設定する。<p>
     * 設定しない場合は、NullPointerExceptionが発生する。<br>
     *
     * @param value バイト配列
     */
    public void setNullValue(byte[] value){
        nullValue = value;
    }
    
    /**
     * nullをFLV要素として書き込もうとした場合に、出力するバイト配列を取得する。<p>
     *
     * @return バイト配列
     */
    public byte[] getNullValue(){
        return nullValue;
    }
    
    /**
     * FLV要素バイト配列を書き込む。<p>
     * パディング処理を自動で行う。<br>
     * 
     * @param element FLV要素バイト配列
     * @exception IOException 入出力エラーが発生した場合
     */
    public void writeElement(byte[] element) throws IOException{
        if(element == null){
            element = nullValue;
        }
        if(converters != null && converters.length != 0 && converters[fieldIndex] != null){
            element = converters[fieldIndex].padding(element);
        }
        super.write(element);
        if(converters != null && converters.length != 0){
            if(converters.length > fieldIndex){
                fieldIndex++;
            }else{
                fieldIndex = 0;
            }
        }
    }
    
    /**
     * 指定されたバイト配列の配列をFLVとして書き込む。<p>
     *
     * @param elements FLV形式で出力するバイト配列の配列
     * @exception IOException 入出力エラーが発生した場合
     */
    public void writeFLV(byte[][] elements) throws IOException{
        for(int i = 0; i < elements.length; i++){
            writeElement(elements[i]);
        }
    }
    
    /**
     * 指定されたリストをFLVとして書き込む。<p>
     *
     * @param elements FLV形式で出力するリスト
     * @exception IOException 入出力エラーが発生した場合
     */
    public void writeFLV(List elements) throws IOException{
        for(int i = 0, imax = elements.size(); i < imax; i++){
            writeElement((byte[])elements.get(i));
        }
    }
    
    /**
     * 未接続の複製を生成する。<p>
     *
     * @return 未接続の複製
     */
    public FLVOutputStream cloneOutputStream(){
        return cloneOutputStream(new FLVOutputStream());
    }
    
    /**
     * 未接続の複製を生成する。<p>
     *
     * @param clone 未接続のインスタンス
     * @return 未接続の複製
     */
    protected FLVOutputStream cloneOutputStream(FLVOutputStream clone){
        if(converters != null && converters.length != 0){
            clone.converters = new PaddingByteArrayConverter[converters.length];
            System.arraycopy(converters, 0, clone.converters, 0, converters.length);
        }
        clone.nullValue = nullValue;
        return clone;
    }
    
    private static class OutputStreamWrapper extends OutputStream{
        
        private OutputStream realOutputStream;
        
        public OutputStreamWrapper(){
        }
        
        public OutputStreamWrapper(OutputStream os){
            realOutputStream = os;
        }
        
        public OutputStream getOutputStream(){
            return realOutputStream;
        }
        
        public void setOutputStream(OutputStream os){
            realOutputStream = os;
        }
        
        public void write(int b) throws IOException{
            if(realOutputStream == null){
                throw new IOException("OutputStream is null.");
            }
            realOutputStream.write(b);
        }
        
        public void write(byte[] b) throws IOException{
            if(realOutputStream == null){
                throw new IOException("OutputStream is null.");
            }
            realOutputStream.write(b);
        }
        
        public void write(byte[] b, int off, int len) throws IOException{
            if(realOutputStream == null){
                throw new IOException("OutputStream is null.");
            }
            realOutputStream.write(b, off, len);
        }
        
        public void flush() throws IOException{
            if(realOutputStream != null){
                realOutputStream.flush();
            }
        }
        
        public void close() throws IOException{
            if(realOutputStream != null){
                realOutputStream.close();
            }
        }
    }
}