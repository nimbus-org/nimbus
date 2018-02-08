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

import jp.ossc.nimbus.util.converter.StringConverter;
import jp.ossc.nimbus.util.converter.ConvertException;

/**
 * CSV形式のWriterクラス。<p>
 * <pre>
 * import java.io.*;
 * import jp.ossc.nimbus.io.CSVWriter;
 *
 * FileOutputStream fos = new FileOutputStream("sample.csv");
 * OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
 * CSVWriter writer = new CSVWriter(osw);
 * String[] csv = new String[2];
 * try{
 *     csv[0] = "hoge";
 *     csv[1] = "100";
 *     writer.writeCSV(csv);
 *        :
 * }finally{
 *     writer.close();
 * }
 * </pre>
 * 
 * @author M.Takata
 */
public class CSVWriter extends BufferedWriter implements StringConverter{
    
    /**
     * デフォルトのセパレータ。<p>
     */
    public static final char DEFAULT_SEPARATOR = ',';
    
    /**
     * デフォルトのセパレータのエスケープ文字。<p>
     * エスケープ文字をエスケープしたい場合は、エスケープ文字を重ねる。<br>
     */
    public static final char DEFAULT_SEPARATOR_ESCAPE = '\\';
    
    /**
     * デフォルトの囲み文字。<p>
     * 囲み文字をエスケープしたい場合は、囲み文字を重ねる。<br>
     */
    public static final char DEFAULT_ENCLOSURE = '"';
    
    /**
     * デフォルトの改行文字。<p>
     */
    public static final String DEFAULT_LINE_SEPARATOR
         = System.getProperty("line.separator");
    
    private static final String REPLACE_CR = "\\r";
    private static final String REPLACE_LF = "\\n";
    
    protected char separator = DEFAULT_SEPARATOR;
    protected char separatorEscape = DEFAULT_SEPARATOR_ESCAPE;
    protected char enclosure = DEFAULT_ENCLOSURE;
    protected boolean isEnclose;
    protected String lineSeparator = DEFAULT_LINE_SEPARATOR;
    protected boolean isAppendElement;
    protected String nullValue;
    protected boolean isEscapeLineSeparatorInEnclosure;
    
    protected WriterWrapper writerWrapper;
    
    /**
     * デフォルトの書き込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     */
    public CSVWriter(){
        super(new WriterWrapper());
        writerWrapper = (WriterWrapper)lock;
    }
    
    /**
     * デフォルトの書き込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param writer 書き込み先のWriter
     */
    public CSVWriter(Writer writer){
        super(new WriterWrapper(writer));
        writerWrapper = (WriterWrapper)lock;
    }
    
    /**
     * 指定された書き込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     *
     * @param size 書き込みバッファサイズ
     */
    public CSVWriter(int size){
        super(new WriterWrapper(), size);
        writerWrapper = (WriterWrapper)lock;
    }
    
    /**
     * 指定された書き込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param writer 書き込み先のWriter
     * @param size 書き込みバッファサイズ
     */
    public CSVWriter(Writer writer, int size){
        super(new WriterWrapper(writer), size);
        writerWrapper = (WriterWrapper)lock;
    }
    
    /**
     * Writerを設定する。<p>
     *
     * @param writer Writer
     */
    public void setWriter(Writer writer){
        writerWrapper.setWriter(writer);
        isAppendElement = false;
    }
    
    /**
     * セパレータを設定する。<p>
     *
     * @param separator セパレータ
     */
    public void setSeparator(char separator){
        this.separator = separator;
    }
    
    /**
     * セパレータを取得する。<p>
     *
     * @return セパレータ
     */
    public char getSeparator(){
         return separator;
    }
    
    /**
     * セパレータのエスケープ文字を設定する。<p>
     *
     * @param escape エスケープ文字
     */
    public void setSeparatorEscape(char escape){
        separatorEscape = escape;
    }
    
    /**
     * セパレータのエスケープ文字を取得する。<p>
     *
     * @return エスケープ文字
     */
    public char getSeparatorEscape(){
         return separatorEscape;
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
     * 囲み文字を設定する。<p>
     *
     * @param enclosure 囲み文字
     */
    public void setEnclosure(char enclosure){
        this.enclosure = enclosure;
    }
    
    /**
     * 囲み文字を取得する。<p>
     *
     * @return 囲み文字
     */
    public char getEnclosure(){
         return enclosure;
    }
    
    /**
     * CSVの要素を囲み文字で囲むかどうかを設定する。<p>
     * デフォルトは、falseで囲まない。<br>
     *
     * @param isEnclose 囲み文字で囲む場合true
     */
    public void setEnclose(boolean isEnclose){
        this.isEnclose = isEnclose;
    }
    
    /**
     * CSVの要素を囲み文字で囲むかどうかを判定する。<p>
     *
     * @return trueの場合、囲み文字で囲む
     */
    public boolean isEnclose(){
         return isEnclose;
    }
    
    /**
     * nullをCSV要素として書き込もうとした場合に、出力する文字列を設定する。<p>
     * 設定しない場合は、NullPointerExceptionが発生する。<br>
     *
     * @param value 文字列
     */
    public void setNullValue(String value){
        nullValue = value;
    }
    
    /**
     * nullをCSV要素として書き込もうとした場合に、出力する文字列を取得する。<p>
     *
     * @return 文字列
     */
    public String getNullValue(){
        return nullValue;
    }
    
    /**
     * CSVの要素を囲み文字で囲む場合に、改行をエスケープするかどうかを設定する。<p>
     * デフォルトは、falseでエスケープしない。<br>
     * 
     * @param isEscape エスケープする場合true
     */
    public void setEscapeLineSeparatorInEnclosure(boolean isEscape){
        isEscapeLineSeparatorInEnclosure = isEscape;
    }
    
    /**
     * CSVの要素を囲み文字で囲む場合に、改行をエスケープするかどうかを判定する。<p>
     * 
     * @return trueの場合、エスケープする
     */
    public boolean isEscapeLineSeparatorInEnclosure(){
        return isEscapeLineSeparatorInEnclosure;
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
    }
    
    /**
     * CSV要素文字列を書き込む。<p>
     * セパレータの追加、セパレータ文字が含まれている場合のエスケープ、囲み文字での囲み処理を自動で行う。<br>
     * 
     * @param element CSV要素文字列
     * @exception IOException 入出力エラーが発生した場合
     */
    public void writeElement(String element) throws IOException{
        if(isAppendElement){
            super.write(separator);
        }
        if(isEnclose){
            super.write(enclosure);
        }
        super.write(escape(element));
        if(isEnclose){
            super.write(enclosure);
        }
        isAppendElement = true;
    }
    
    /**
     * CSV要素を書き込む。<p>
     * 
     * @param element CSV要素
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeElement(String)
     */
    public void writeElement(boolean element) throws IOException{
        writeElement(Boolean.toString(element));
    }
    
    /**
     * CSV要素を書き込む。<p>
     * 
     * @param element CSV要素
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeElement(String)
     */
    public void writeElement(byte element) throws IOException{
        writeElement(Byte.toString(element));
    }
    
    /**
     * CSV要素を書き込む。<p>
     * 
     * @param element CSV要素
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeElement(String)
     */
    public void writeElement(char element) throws IOException{
        writeElement(Character.toString(element));
    }
    
    /**
     * CSV要素を書き込む。<p>
     * 
     * @param element CSV要素
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeElement(String)
     */
    public void writeElement(short element) throws IOException{
        writeElement(Short.toString(element));
    }
    
    /**
     * CSV要素を書き込む。<p>
     * 
     * @param element CSV要素
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeElement(String)
     */
    public void writeElement(int element) throws IOException{
        writeElement(Integer.toString(element));
    }
    
    /**
     * CSV要素を書き込む。<p>
     * 
     * @param element CSV要素
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeElement(String)
     */
    public void writeElement(long element) throws IOException{
        writeElement(Long.toString(element));
    }
    
    /**
     * CSV要素を書き込む。<p>
     * 
     * @param element CSV要素
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeElement(String)
     */
    public void writeElement(float element) throws IOException{
        writeElement(Float.toString(element));
    }
    
    /**
     * CSV要素を書き込む。<p>
     * 
     * @param element CSV要素
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeElement(String)
     */
    public void writeElement(double element) throws IOException{
        writeElement(Double.toString(element));
    }
    
    /**
     * CSV要素を書き込む。<p>
     * 
     * @param element CSV要素
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeElement(String)
     */
    public void writeElement(Object element) throws IOException{
        writeElement(element == null ? (String)null : element.toString());
    }
    
    private String escape(String element){
        if(isEnclose){
            return escape(element, enclosure, nullValue, isEscapeLineSeparatorInEnclosure);
        }else{
            return escape(element, separator, separatorEscape, nullValue);
        }
    }
    
    private static String escape(
        String element,
        char separator,
        char separatorEscape,
        String nullValue
    ){
        if(element == null){
            return nullValue;
        }
        final int index1 = element.indexOf(separator);
        final int index2 = element.indexOf(separatorEscape);
        final int index3 = element.indexOf('\r');
        final int index4 = element.indexOf('\n');
        if(index1 == -1 && index2 == -1 && index3 == -1 && index4 == -1){
            return element;
        }
        int index = index1 == -1 ? index2
             : (index2 == -1 ? index1 : Math.min(index1, index2));
        index = index == -1 ? index3
             : (index3 == -1 ? index : Math.min(index, index3));
        index = index == -1 ? index4
             : (index4 == -1 ? index : Math.min(index, index4));
        
        final StringBuilder buf = new StringBuilder();
        for(int i = 0; i < index; i++){
            char c = element.charAt(i);
            buf.append(c);
        }
        for(int i = index, imax = element.length(); i < imax; i++){
            char c = element.charAt(i);
            if(c == separator || c == separatorEscape){
                buf.append(separatorEscape);
                buf.append(c);
            }else if(c == '\r'){
                buf.append(REPLACE_CR);
            }else if(c == '\n'){
                buf.append(REPLACE_LF);
            }else{
                buf.append(c);
            }
        }
        return buf.toString();
    }
    
    private static String escape(
        String element,
        char enclosure,
        String nullValue,
        boolean isEscapeLineSeparator
    ){
        if(element == null){
            return nullValue;
        }
        final int index1 = element.indexOf(enclosure);
        final int index2 = isEscapeLineSeparator ? element.indexOf('\r') : -1;
        final int index3 = isEscapeLineSeparator ? element.indexOf('\n') : -1;
        if(index1 == -1 && index2 == -1 && index3 == -1){
            return element;
        }
        int index = index1 == -1 ? index2
             : (index2 == -1 ? index1 : Math.min(index1, index2));
        index = index == -1 ? index3
             : (index3 == -1 ? index : Math.min(index, index3));
        final StringBuilder buf = new StringBuilder();
        for(int i = 0; i < index; i++){
            char c = element.charAt(i);
            buf.append(c);
        }
        for(int i = index, imax = element.length(); i < imax; i++){
            char c = element.charAt(i);
            if(c == enclosure){
                buf.append(enclosure);
                buf.append(c);
            }else if(isEscapeLineSeparator){
                if(c == '\r'){
                    buf.append(REPLACE_CR);
                }else if(c == '\n'){
                    buf.append(REPLACE_LF);
                }else{
                    buf.append(c);
                }
            }else{
                buf.append(c);
            }
        }
        return buf.toString();
    }
    
    /**
     * 指定された文字列配列をCSVとして書き込む。<p>
     * 改行文字の追加、セパレータの追加、セパレータ文字が含まれている場合のエスケープ、囲み文字での囲み処理を自動で行う。<br>
     *
     * @param elements CSV形式で出力する文字列配列
     * @exception IOException 入出力エラーが発生した場合
     */
    public void writeCSV(String[] elements) throws IOException{
        for(int i = 0; i < elements.length; i++){
            writeElement(elements[i]);
        }
        newLine();
    }
    
    /**
     * 指定された配列をCSVとして書き込む。<p>
     *
     * @param elements CSV形式で出力する配列
     * @exception IOException 入出力エラーが発生した場合
     * @see #writeCSV(String[])
     */
    public void writeCSV(Object[] elements) throws IOException{
        for(int i = 0; i < elements.length; i++){
            writeElement(elements[i]);
        }
        newLine();
    }
    
    /**
     * 指定されたリストをCSVとして書き込む。<p>
     * 改行文字の追加、セパレータの追加、セパレータ文字が含まれている場合のエスケープ、囲み文字での囲み処理を自動で行う。<br>
     *
     * @param elements CSV形式で出力するリスト
     * @exception IOException 入出力エラーが発生した場合
     */
    public void writeCSV(List elements) throws IOException{
        for(int i = 0, imax = elements.size(); i < imax; i++){
            writeElement(elements.get(i));
        }
        newLine();
    }
    
    /**
     * 文字列配列を指定されたCSV形式文字列に変換する。<p>
     *
     * @param separator セパレータ
     * @param escape エスケープ文字
     * @param nullValue nullをCSV要素として書き込もうとした場合に、出力する文字列
     * @param elements CSVの要素となる文字列配列
     * @return CSV形式文字列
     */
    public static String toCSV(
        String[] elements,
        char separator,
        char escape,
        String nullValue
    ){
        final StringBuilder buf = new StringBuilder();
        if(elements != null){
            for(int i = 0; i < elements.length; i++){
                buf.append(
                    escape(elements[i], separator, escape, nullValue)
                );
                if(i != elements.length - 1){
                    buf.append(separator);
                }
            }
        }
        return buf.toString();
    }
    
    /**
     * 文字列配列を指定されたCSV形式文字列に変換する。<p>
     *
     * @param elements CSVの要素となる文字列配列
     * @param separator セパレータ
     * @param enclosure 囲み文字
     * @param nullValue nullをCSV要素として書き込もうとした場合に、出力する文字列
     * @param isEscapeLineSeparator 改行をエスケープするかどうか。エスケープする場合true
     * @return CSV形式文字列
     */
    public static String toEnclosedCSV(
        String[] elements,
        char separator,
        char enclosure,
        String nullValue,
        boolean isEscapeLineSeparator
    ){
        final StringBuilder buf = new StringBuilder();
        if(elements != null){
            for(int i = 0; i < elements.length; i++){
                buf.append(
                    escape(elements[i], enclosure, nullValue, isEscapeLineSeparator)
                );
                if(i != elements.length - 1){
                    buf.append(separator);
                }
            }
        }
        return buf.toString();
    }
    
    /**
     * 文字列リストを指定されたCSV形式文字列に変換する。<p>
     *
     * @param elements CSVの要素となる文字列リスト
     * @param separator セパレータ
     * @param separatorEscape エスケープ文字
     * @param nullValue nullをCSV要素として書き込もうとした場合に、出力する文字列
     * @return CSV形式文字列
     */
    public static String toCSV(
        List elements,
        char separator,
        char separatorEscape,
        String nullValue
    ){
        final StringBuilder buf = new StringBuilder();
        if(elements != null){
            for(int i = 0, imax = elements.size(); i < imax; i++){
                buf.append(
                    escape(
                        (String)elements.get(i),
                        separator,
                        separatorEscape,
                        nullValue
                    )
                );
                if(i != imax - 1){
                    buf.append(separator);
                }
            }
        }
        return buf.toString();
    }
    
    /**
     * 文字列リストを指定されたCSV形式文字列に変換する。<p>
     *
     * @param elements CSVの要素となる文字列リスト
     * @param separator セパレータ
     * @param enclosure 囲み文字
     * @param nullValue nullをCSV要素として書き込もうとした場合に、出力する文字列
     * @param isEscapeLineSeparator 改行をエスケープするかどうか。エスケープする場合true
     * @return CSV形式文字列
     */
    public static String toEnclosedCSV(
        List elements,
        char separator,
        char enclosure,
        String nullValue,
        boolean isEscapeLineSeparator
    ){
        final StringBuilder buf = new StringBuilder();
        if(elements != null){
            for(int i = 0, imax = elements.size(); i < imax; i++){
                buf.append(
                    escape((String)elements.get(i), enclosure, nullValue, isEscapeLineSeparator)
                );
                if(i != imax - 1){
                    buf.append(separator);
                }
            }
        }
        return buf.toString();
    }
    
    /**
     * 未接続の複製を生成する。<p>
     *
     * @return 未接続の複製
     */
    public CSVWriter cloneWriter(){
        return cloneWriter(new CSVWriter());
    }
    
    /**
     * 未接続の複製を生成する。<p>
     *
     * @param clone 未接続のインスタンス
     * @return 未接続の複製
     */
    protected CSVWriter cloneWriter(CSVWriter clone){
        clone.separator = separator;
        clone.separatorEscape = separatorEscape;
        clone.enclosure = enclosure;
        clone.isEnclose = isEnclose;
        clone.lineSeparator = lineSeparator;
        clone.nullValue = nullValue;
        return clone;
    }
    
    public Object convert(Object obj) throws ConvertException{
        return convert((String)(obj == null ? null : obj.toString()));
    }
    
    public String convert(String str) throws ConvertException{
        return escape(str);
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