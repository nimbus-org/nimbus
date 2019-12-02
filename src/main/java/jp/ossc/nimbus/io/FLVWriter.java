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

import java.lang.reflect.*;
import java.io.*;
import java.util.List;

import jp.ossc.nimbus.util.converter.PaddingStringConverter;

/**
 * FLV形式のWriterクラス。<p>
 * <pre>
 * import java.io.*;
 * import jp.ossc.nimbus.io.FLVWriter;
 *
 * FileOutputStream fos = new FileOutputStream("sample.csv");
 * OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
 * FLVWriter writer = new FLVWriter(
 *     osw,
 *     new PaddingStringConverter[]{
 *         new PaddingStringConverter(10),
 *         new PaddingStringConverter(12),
 *     }
 * );
 * String[] flv = new String[2];
 * try{
 *     flv[0] = "hoge";
 *     flv[1] = "100";
 *     writer.writeFLV(flv);
 *        :
 * }finally{
 *     writer.close();
 * }
 * </pre>
 * 
 * @author M.Takata
 */
public class FLVWriter extends BufferedWriter{
    
    /**
     * デフォルトの改行文字。<p>
     */
    public static final String DEFAULT_LINE_SEPARATOR
         = System.getProperty("line.separator");
    protected String lineSeparator = DEFAULT_LINE_SEPARATOR;
    protected boolean isAppendElement;
    protected int fieldIndex;
    protected String nullValue;
    protected PaddingStringConverter[] converters;
    
    protected WriterWrapper writerWrapper;
    
    /**
     * デフォルトの書き込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     */
    public FLVWriter(){
        this(new WriterWrapper());
    }
    
    /**
     * デフォルトの書き込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param writer 書き込み先のWriter
     */
    public FLVWriter(Writer writer){
        this(writer, null);
    }
    
    /**
     * デフォルトの書き込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param writer 書き込み先のWriter
     * @param convs フィールドをパディングするコンバータ配列
     */
    public FLVWriter(Writer writer, PaddingStringConverter[] convs){
        super(writer instanceof WriterWrapper ? writer : new WriterWrapper(writer));
        writerWrapper = (WriterWrapper)lock;
        converters = convs;
    }
    
    /**
     * 指定された書き込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     *
     * @param size 書き込みバッファサイズ
     */
    public FLVWriter(int size){
        this(new WriterWrapper(), size);
    }
    
    /**
     * 指定された書き込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param writer 書き込み先のWriter
     * @param size 書き込みバッファサイズ
     */
    public FLVWriter(Writer writer, int size){
        this(writer, null, size);
    }
    
    /**
     * 指定された書き込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param writer 書き込み先のWriter
     * @param convs フィールドをパディングするコンバータ配列
     * @param size 書き込みバッファサイズ
     */
    public FLVWriter(Writer writer, PaddingStringConverter[] convs, int size){
        super(writer instanceof WriterWrapper ? writer : new WriterWrapper(writer), size);
        writerWrapper = (WriterWrapper)lock;
        converters = convs;
    }
    
    /**
     * Writerを設定する。<p>
     *
     * @param writer Writer
     */
    public void setWriter(Writer writer){
        writerWrapper.setWriter(writer);
        isAppendElement = false;
        fieldIndex = 0;
    }
    
    /**
     * 各フィールドのパディングを行うコンバータを設定する。<p>
     *
     * @param convs パディングを行うコンバータの配列
     */
    public void setFieldPaddingStringConverter(PaddingStringConverter[] convs){
        converters = convs;
    }
    
    /**
     * 各フィールドのパディングを行うコンバータを取得する。<p>
     *
     * @return パディングを行うコンバータの配列
     */
    public PaddingStringConverter[] getFieldPaddingStringConverter(){
        return converters;
    }
    
    /**
     * 改行セパレータを設定する。<p>
     *
     * @param separator 改行セパレータ
     */
    public void setLineSeparator(String separator){
        this.lineSeparator = separator;
    }
    
    /**
     * 改行セパレータを取得する。<p>
     *
     * @return 改行セパレータ
     */
    public String getLineSeparator(){
         return lineSeparator;
    }
    
    /**
     * nullをFLV要素として書き込もうとした場合に、出力する文字列を設定する。<p>
     * 設定しない場合は、NullPointerExceptionが発生する。<br>
     *
     * @param value 文字列
     */
    public void setNullValue(String value){
        nullValue = value;
    }
    
    /**
     * nullをFLV要素として書き込もうとした場合に、出力する文字列を取得する。<p>
     *
     * @return 文字列
     */
    public String getNullValue(){
        return nullValue;
    }
    
    /**
     * 行区切り文字を書き込む。<p>
     * 行区切り文字は、{@link #getLineSeparator()}を使用する。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void newLine() throws IOException{
        super.write(lineSeparator);
        isAppendElement = false;
        fieldIndex = 0;
    }
    
    /**
     * FLV要素文字列を書き込む。<p>
     * パディング処理を自動で行う。<br>
     * 
     * @param element FLV要素文字列
     * @exception IOException 入出力エラーが発生した場合
     */
    public void writeElement(String element) throws IOException{
        if(element == null){
            element = nullValue;
        }
        if(converters != null && converters.length != 0 && converters[fieldIndex] != null){
            element = converters[fieldIndex].padding(element);
        }
        super.write(element);
        isAppendElement = true;
        fieldIndex++;
    }
    
    /**
     * FLV要素を書き込む。<p>
     * 
     * @param element FLV要素
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeElement(String)
     */
    public void writeElement(boolean element) throws IOException{
        writeElement(Boolean.toString(element));
    }
    
    /**
     * FLV要素を書き込む。<p>
     * 
     * @param element FLV要素
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeElement(String)
     */
    public void writeElement(byte element) throws IOException{
        writeElement(Byte.toString(element));
    }
    
    /**
     * FLV要素を書き込む。<p>
     * 
     * @param element FLV要素
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeElement(String)
     */
    public void writeElement(char element) throws IOException{
        writeElement(Character.toString(element));
    }
    
    /**
     * FLV要素を書き込む。<p>
     * 
     * @param element FLV要素
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeElement(String)
     */
    public void writeElement(short element) throws IOException{
        writeElement(Short.toString(element));
    }
    
    /**
     * FLV要素を書き込む。<p>
     * 
     * @param element FLV要素
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeElement(String)
     */
    public void writeElement(int element) throws IOException{
        writeElement(Integer.toString(element));
    }
    
    /**
     * FLV要素を書き込む。<p>
     * 
     * @param element FLV要素
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeElement(String)
     */
    public void writeElement(long element) throws IOException{
        writeElement(Long.toString(element));
    }
    
    /**
     * FLV要素を書き込む。<p>
     * 
     * @param element FLV要素
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeElement(String)
     */
    public void writeElement(float element) throws IOException{
        writeElement(Float.toString(element));
    }
    
    /**
     * FLV要素を書き込む。<p>
     * 
     * @param element FLV要素
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeElement(String)
     */
    public void writeElement(double element) throws IOException{
        writeElement(Double.toString(element));
    }
    
    /**
     * FLV要素を書き込む。<p>
     * 
     * @param element FLV要素
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeElement(String)
     */
    public void writeElement(Object element) throws IOException{
        writeElement(element == null ? (String)null : element.toString());
    }
    
    /**
     * 指定された文字列配列をFLVとして書き込む。<p>
     * 改行文字の追加、セパレータの追加、セパレータ文字が含まれている場合のエスケープ、囲み文字での囲み処理を自動で行う。<br>
     *
     * @param elements FLV形式で出力する文字列配列
     * @exception IOException 入出力エラーが発生した場合
     */
    public void writeFLV(String[] elements) throws IOException{
        for(int i = 0; i < elements.length; i++){
            writeElement(elements[i]);
        }
        newLine();
    }
    
    /**
     * 指定された配列をFLVとして書き込む。<p>
     *
     * @param elements FLV形式で出力する配列
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeFLV(String[])
     */
    public void writeFLV(Object[] elements) throws IOException{
        for(int i = 0; i < elements.length; i++){
            writeElement(elements[i]);
        }
        newLine();
    }
    
    /**
     * 指定されたリストをFLVとして書き込む。<p>
     * 改行文字の追加を自動で行う。<br>
     *
     * @param elements FLV形式で出力するリスト
     * @exception IOException 入出力エラーが発生した場合
     */
    public void writeFLV(List elements) throws IOException{
        for(int i = 0, imax = elements.size(); i < imax; i++){
            writeElement(elements.get(i));
        }
        newLine();
    }
    
    /**
     * 未接続の複製を生成する。<p>
     *
     * @return 未接続の複製
     */
    public FLVWriter cloneWriter(){
        return cloneWriter(new FLVWriter());
    }
    
    /**
     * 未接続の複製を生成する。<p>
     *
     * @param clone 未接続のインスタンス
     * @return 未接続の複製
     */
    protected FLVWriter cloneWriter(FLVWriter clone){
        clone.lineSeparator = lineSeparator;
        if(converters != null && converters.length != 0){
            clone.converters = new PaddingStringConverter[converters.length];
            System.arraycopy(converters, 0, clone.converters, 0, converters.length);
        }
        clone.nullValue = nullValue;
        return clone;
    }
    
    private static class WriterWrapper extends Writer{
        
        private Writer realWriter;
        private static Method APPEND1 = null;
        private static Method APPEND2 = null;
        private static Method APPEND3 = null;
        static{
            try{
                APPEND1 = Writer.class.getMethod(
                    "append",
                    new Class[]{CharSequence.class}
                );
            }catch(NoSuchMethodException e){
            }
            try{
                APPEND2 = Writer.class.getMethod(
                    "append",
                    new Class[]{CharSequence.class, Integer.TYPE, Integer.TYPE}
                );
            }catch(NoSuchMethodException e){
            }
            try{
                APPEND3 = Writer.class.getMethod(
                    "append",
                    new Class[]{Character.TYPE}
                );
            }catch(NoSuchMethodException e){
            }
        }
        
        public WriterWrapper(){
        }
        
        public WriterWrapper(Writer writer){
            realWriter = writer;
        }
        
        public Writer getWriter(){
            return realWriter;
        }
        
        public void setWriter(Writer writer){
            realWriter = writer;
        }
        
        public void write(int c) throws IOException{
            if(realWriter == null){
                throw new IOException("Writer is null.");
            }
            realWriter.write(c);
        }
        
        public void write(char[] cbuf) throws IOException{
            if(realWriter == null){
                throw new IOException("Writer is null.");
            }
            realWriter.write(cbuf);
        }
        
        public void write(char[] cbuf, int off, int len) throws IOException{
            if(realWriter == null){
                throw new IOException("Writer is null.");
            }
            realWriter.write(cbuf, off, len);
        }
        
        public void write(String str) throws IOException{
            if(realWriter == null){
                throw new IOException("Writer is null.");
            }
            realWriter.write(str);
        }
        
        public void write(String str, int off, int len) throws IOException{
            if(realWriter == null){
                throw new IOException("Writer is null.");
            }
            realWriter.write(str, off, len);
        }
        
        public Writer append(CharSequence csq) throws IOException{
            if(realWriter == null){
                throw new IOException("Writer is null.");
            }
            if(APPEND1 == null){
                throw new UnsupportedOperationException("No such method.");
            }
            try{
                return (Writer)APPEND1.invoke(realWriter, new Object[]{csq});
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
        
        public Writer append(CharSequence csq, int off, int len) throws IOException{
            if(realWriter == null){
                throw new IOException("Writer is null.");
            }
            if(APPEND2 == null){
                throw new UnsupportedOperationException("No such method.");
            }
            try{
                return (Writer)APPEND2.invoke(
                    realWriter,
                    new Object[]{csq, new Integer(off), new Integer(len)});
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
        
        public Writer append(char c) throws IOException{
            if(realWriter == null){
                throw new IOException("Writer is null.");
            }
            if(APPEND3 == null){
                throw new UnsupportedOperationException("No such method.");
            }
            try{
                return (Writer)APPEND3.invoke(
                    realWriter,
                    new Object[]{new Character(c)}
                );
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
        
        public void flush() throws IOException{
            if(realWriter != null){
                realWriter.flush();
            }
        }
        
        public void close() throws IOException{
            if(realWriter != null){
                realWriter.close();
            }
        }
    }
}