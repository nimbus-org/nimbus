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

/**
 * CSV形式のReaderクラス。<p>
 * <pre>
 * import java.io.*;
 * import jp.ossc.nimbus.io.CSVReader;
 *
 * FileInputStream fis = new FileInputStream("sample.csv");
 * InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
 * CSVReader reader = new CSVReader(isr);
 * try{
 *     String[] csv = null;
 *     while((csv = reader.readCSVLine()) != null){
 *           :
 *     }
 * }finally{
 *     reader.close();
 * }
 * </pre>
 * 
 * @author M.Takata
 */
public class CSVReader extends LineNumberReader{
    
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
    public static final String LINE_SEPARATOR
         = System.getProperty("line.separator");
    
    protected char separator = DEFAULT_SEPARATOR;
    protected char separatorEscape = DEFAULT_SEPARATOR_ESCAPE;
    protected char enclosure = DEFAULT_ENCLOSURE;
    protected boolean isIgnoreEmptyLine;
    protected boolean isIgnoreLineEndSeparator;
    protected boolean isEnclosed;
    protected boolean isTrim;
    protected String nullValue;
    protected String commentPrefix;
    protected boolean isUnescapeLineSeparatorInEnclosure;
    
    protected CSVIterator iterator;
    
    protected ReaderWrapper readerWrapper;
    
    /**
     * デフォルトの読み込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     */
    public CSVReader(){
        super(new ReaderWrapper());
        readerWrapper = (ReaderWrapper)lock;
    }
    
    /**
     * デフォルトの読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param reader 読み込み元のReader
     */
    public CSVReader(Reader reader){
        super(new ReaderWrapper(reader));
        readerWrapper = (ReaderWrapper)lock;
    }
    
    /**
     * 指定された読み込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     *
     * @param size 読み込みバッファサイズ
     */
    public CSVReader(int size){
        super(new ReaderWrapper(), size);
        readerWrapper = (ReaderWrapper)lock;
    }
    
    /**
     * 指定された読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param reader 読み込み元のReader
     * @param size 読み込みバッファサイズ
     */
    public CSVReader(Reader reader, int size){
        super(new ReaderWrapper(reader), size);
        readerWrapper = (ReaderWrapper)lock;
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
     * 行の最後のセパレータを無視するかどうかを設定する。<p>
     * デフォルトは、falseで無視しない。<br>
     *
     * @param isIgnore 行の最後のセパレータを無視する場合true
     */
    public void setIgnoreLineEndSeparator(boolean isIgnore){
        isIgnoreLineEndSeparator = isIgnore;
    }
    
    /**
     * 行の最後のセパレータを無視するかどうかを判定する。<p>
     *
     * @return trueの場合、行の最後のセパレータを無視する
     */
    public boolean isIgnoreLineEndSeparator(){
         return isIgnoreLineEndSeparator;
    }
    
    /**
     * 囲み文字が有効かどうかを設定する。<p>
     * デフォルトは、falseで囲み文字は無効しない。<br>
     *
     * @param isEnclosed 囲み文字が有効な場合true
     */
    public void setEnclosed(boolean isEnclosed){
        this.isEnclosed = isEnclosed;
    }
    
    /**
     * 囲み文字が有効かどうかを判定する。<p>
     *
     * @return trueの場合、囲み文字が有効
     */
    public boolean isEnclosed(){
         return isEnclosed;
    }
    
    /**
     * トリムするかどうかを設定する。<p>
     * デフォルトは、falseでトリムしない。<br>
     *
     * @param isTrim トリムする場合true
     */
    public void setTrim(boolean isTrim){
        this.isTrim = isTrim;
    }
    
    /**
     * トリムするかどうかを判定する。<p>
     *
     * @return trueの場合、トリムする
     */
    public boolean isTrim(){
         return isTrim;
    }
    
    /**
     * CSV要素を読み込んだ場合に、null値扱いする文字列を設定する。<p>
     *
     * @param value 文字列
     */
    public void setNullValue(String value){
        nullValue = value;
    }
    
    /**
     * CSV要素を読み込んだ場合に、null値扱いする文字列を取得する。<p>
     *
     * @return 文字列
     */
    public String getNullValue(){
        return nullValue;
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
     * 囲み文字で囲まれたCSV要素の場合に、エスケープされた改行をアンエスケープするかどうかを設定する。<p>
     * デフォルトは、falseでアンエスケープしない。<br>
     * 
     * @param isUnescape アンエスケープする場合true
     */
    public void setUnescapeLineSeparatorInEnclosure(boolean isUnescape){
        isUnescapeLineSeparatorInEnclosure = isUnescape;
    }
    
    /**
     * 囲み文字で囲まれたCSV要素の場合に、エスケープされた改行をアンエスケープするかどうかを判定する。<p>
     * 
     * @return trueの場合、アンエスケープする
     */
    public boolean isUnescapeLineSeparatorInEnclosure(){
        return isUnescapeLineSeparatorInEnclosure;
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
     * 指定されたCSV行数分スキップする。<p>
     * {@link #isIgnoreEmptyLine()}がtrueの場合は、空行はスキップ行数のカウントから除かれる。<br>
     * CSV行数でカウントされるため、囲み文字で囲んだ中に改行があっても、1行としてカウントされる。<br>
     *
     * @param line スキップする行数
     * @return スキップされた行数
     * @exception IOException 入出力エラーが発生した場合
     */
    public long skipCSVLine(long line) throws IOException{
        List csv = null;
        int result = 0;
        for(result = 0; result < line; result++){
            csv = readCSVLineList(csv);
            if(csv == null){
                break;
            }
        }
        return result;
    }
    
    /**
     * CSV行を1行読み込む。<p>
     *
     * @return CSV要素の文字列配列
     * @exception IOException 入出力エラーが発生した場合
     */
    public String[] readCSVLine() throws IOException{
        final List csv = readCSVLineList();
        return csv == null ? null
             : (String[])csv.toArray(new String[csv.size()]);
    }
    
    /**
     * CSV行を1行読み込む。<p>
     *
     * @return CSV要素の文字列リスト
     * @exception IOException 入出力エラーが発生した場合
     */
    public List readCSVLineList() throws IOException{
        return readCSVLineList(null);
    }
    
    /**
     * CSV行を1行読み込む。<p>
     * CSV要素の文字列を格納するリストを再利用するためのメソッドである。<br>
     *
     * @param csv CSV要素の文字列を格納するリスト
     * @return CSV要素の文字列リスト
     * @exception IOException 入出力エラーが発生した場合
     */
    public List readCSVLineList(List csv) throws IOException{
        return toList(
            this,
            csv,
            separator,
            separatorEscape,
            isEnclosed,
            enclosure,
            nullValue,
            commentPrefix,
            isIgnoreEmptyLine,
            isIgnoreLineEndSeparator,
            isTrim,
            isUnescapeLineSeparatorInEnclosure
        );
    }
    
    public String readLine() throws IOException{
        if(readerWrapper.getReader() instanceof BufferedReader){
            return ((BufferedReader)readerWrapper.getReader()).readLine();
        }else{
            return super.readLine();
        }
    }
    
    private static List toList(
        BufferedReader reader,
        List csv,
        char separator,
        char separatorEscape,
        boolean isEnclosed,
        char enclosure,
        String nullValue,
        String commentPrefix,
        boolean isIgnoreEmptyLine,
        boolean isIgnoreLineEndSeparator,
        boolean isTrim,
        boolean isUnescapeLineSeparatorInEnclosure
    ) throws IOException{
        String line = null;
        do{
            line = reader.readLine();
            if(line == null){
                if(csv != null){
                    csv.clear();
                }
                return null;
            }
            if((isIgnoreEmptyLine && line.length() == 0)
                    || (commentPrefix != null && line.startsWith(commentPrefix))
            ){
                line = null;
                if(reader instanceof LineNumberReader){
                    LineNumberReader lnr = (LineNumberReader)reader;
                    lnr.setLineNumber(lnr.getLineNumber() - 1);
                }
            }
        }while(line == null);
        if(csv == null){
            csv = new ArrayList();
        }else{
            csv.clear();
        }
        if(line.length() == 0){
            return csv;
        }
        final StringBuilder buf = new StringBuilder();
        boolean inEnclosure = false;
        boolean isEncElement = false;
        do{
            if(inEnclosure){
                line = reader.readLine();
                if(line == null){
                    break;
                }
                if(reader instanceof LineNumberReader){
                    LineNumberReader lnr = (LineNumberReader)reader;
                    lnr.setLineNumber(lnr.getLineNumber() - 1);
                }
                buf.append(LINE_SEPARATOR);
            }
            char c = 0;
            for(int i = 0, imax = line.length(); i < imax; i++){
                c = line.charAt(i);
                if(c == enclosure){
                    if(isEnclosed){
                        if(inEnclosure
                            && imax - 1 != i
                            && line.charAt(i + 1) == enclosure
                        ){
                            buf.append(enclosure);
                            i++;
                        }else{
                            if((i > 2
                                && line.charAt(i - 1) != separator
                                && imax - 1 != i
                                && line.charAt(i + 1) != separator)
                            ){
                                buf.append(c);
                            }else{
                                inEnclosure = !inEnclosure;
                                if(!inEnclosure){
                                    isEncElement = true;
                                }
                            }
                        }
                    }else{
                        buf.append(c);
                    }
                }else if(c == separator){
                    if(inEnclosure){
                        buf.append(c);
                    }else{
                        if(!isEncElement && isTrim){
                            trim(buf);
                        }
                        final String element = buf.toString();
                        if(nullValue == null){
                            csv.add(element);
                        }else{
                            if(nullValue.equals(element)){
                                csv.add(null);
                            }else{
                                csv.add(element);
                            }
                        }
                        buf.setLength(0);
                        isEncElement = false;
                    }
                }else if(c == separatorEscape){
                    if(imax - 1 != i){
                        final char nextChar = line.charAt(i + 1);
                        if(!inEnclosure){
                            if(nextChar == separator
                                     || nextChar == separatorEscape
                                     || nextChar == enclosure){
                                buf.append(nextChar);
                                i++;
                            }else if(nextChar == 'r'){
                                buf.append('\r');
                                i++;
                            }else if(nextChar == 'n'){
                                buf.append('\n');
                                i++;
                            }else{
                                buf.append(c);
                            }
                        }else if(isUnescapeLineSeparatorInEnclosure){
                            if(nextChar == 'r'){
                                buf.append('\r');
                                i++;
                            }else if(nextChar == 'n'){
                                buf.append('\n');
                                i++;
                            }else{
                                buf.append(c);
                            }
                        }else{
                            buf.append(c);
                        }
                    }else{
                        buf.append(c);
                    }
                }else{
                    buf.append(c);
                }
            }
            if(!inEnclosure
                 && (c != separator || !isIgnoreLineEndSeparator
                     || buf.length() != 0)){
                final String element = buf.toString();
                if(nullValue == null){
                    csv.add(element);
                }else{
                    if(nullValue.equals(element)){
                        csv.add(null);
                    }else{
                        csv.add(element);
                    }
                }
            }
        }while(inEnclosure);
        return csv;
    }
    
    /**
     * 指定された文字列をトリムする。<p>
     * トリムは、指定された文字列の前後の空白文字（{@link Character#isWhitespace(char)}がtrueとなる文字）を削除する。
     * 
     * @param buf 文字列
     * @return トリムされた文字列
     */
    protected static StringBuilder trim(StringBuilder buf){
        int index = 0;
        for(int i = 0, max = buf.length(); i < max; i++){
            final char c = buf.charAt(i);
            if(!Character.isWhitespace(c)){
                index = i;
                break;
            }
        }
        if(index != 0){
            buf.delete(0, index);
        }
        index = buf.length();
        for(int i = buf.length(); --i >= 0;){
            final char c = buf.charAt(i);
            if(!Character.isWhitespace(c)){
                index = i;
                break;
            }
        }
        if(index != buf.length() - 1){
            buf.delete(index, buf.length());
        }
        return buf;
    }
    
    public static String[] toArray(
        String str,
        char separator,
        char separatorEscape,
        char enclosure,
        String nullValue,
        String commentPrefix,
        boolean isIgnoreEmptyLine,
        boolean isIgnoreLineEndSeparator,
        boolean isTrim,
        boolean isUnescapeLineSeparatorInEnclosure
    ){
        if(str == null || str.length() == 0){
            return new String[0];
        }
        final StringReader sr = new StringReader(str);
        final BufferedReader br = new BufferedReader(sr);
        List list = null;
        try{
            list = toList(
                br,
                null,
                separator,
                separatorEscape,
                true,
                enclosure,
                nullValue,
                commentPrefix,
                isIgnoreEmptyLine,
                isIgnoreLineEndSeparator,
                isTrim,
                isUnescapeLineSeparatorInEnclosure
            );
        }catch(IOException e){
            // 起こらないはず
            return new String[0];
        }
        return (String[])list.toArray(new String[list.size()]);
    }
    
    public static String[] toArray(
        String str,
        char separator,
        char separatorEscape,
        String nullValue,
        String commentPrefix,
        boolean isIgnoreEmptyLine,
        boolean isIgnoreLineEndSeparator,
        boolean isTrim,
        boolean isUnescapeLineSeparatorInEnclosure
    ){
        if(str == null || str.length() == 0){
            return new String[0];
        }
        final StringReader sr = new StringReader(str);
        final BufferedReader br = new BufferedReader(sr);
        List list = null;
        try{
            list = toList(
                br,
                null,
                separator,
                separatorEscape,
                false,
                '"',
                nullValue,
                commentPrefix,
                isIgnoreEmptyLine,
                isIgnoreLineEndSeparator,
                isTrim,
                isUnescapeLineSeparatorInEnclosure
            );
        }catch(IOException e){
            // 起こらないはず
            return new String[0];
        }
        return (String[])list.toArray(new String[list.size()]);
    }
    
    public static List toList(
        String str,
        List result,
        char separator,
        char separatorEscape,
        char enclosure,
        String nullValue,
        String commentPrefix,
        boolean isIgnoreEmptyLine,
        boolean isIgnoreLineEndSeparator,
        boolean isTrim,
        boolean isUnescapeLineSeparatorInEnclosure
    ){
        if(result == null){
            result = new ArrayList();
        }
        if(str == null || str.length() == 0){
            result.clear();
            return result;
        }
        final StringReader sr = new StringReader(str);
        final BufferedReader br = new BufferedReader(sr);
        try{
            result = toList(
                br,
                result,
                separator,
                separatorEscape,
                true,
                enclosure,
                nullValue,
                commentPrefix,
                isIgnoreEmptyLine,
                isIgnoreLineEndSeparator,
                isTrim,
                isUnescapeLineSeparatorInEnclosure
            );
        }catch(IOException e){
            // 起こらないはず
            return result;
        }
        return result;
    }
    
    public static List toList(
        String str,
        List result,
        char separator,
        char separatorEscape,
        String nullValue,
        String commentPrefix,
        boolean isIgnoreEmptyLine,
        boolean isIgnoreLineEndSeparator,
        boolean isTrim,
        boolean isUnescapeLineSeparatorInEnclosure
    ){
        if(result == null){
            result = new ArrayList();
        }
        if(str == null || str.length() == 0){
            result.clear();
            return result;
        }
        final StringReader sr = new StringReader(str);
        final BufferedReader br = new BufferedReader(sr);
        try{
            result = toList(
                br,
                result,
                separator,
                separatorEscape,
                false,
                '"',
                nullValue,
                commentPrefix,
                isIgnoreEmptyLine,
                isIgnoreLineEndSeparator,
                isTrim,
                isUnescapeLineSeparatorInEnclosure
            );
        }catch(IOException e){
            // 起こらないはず
            return result;
        }
        return result;
    }
    
    /**
     * {@link CSVReader.CSVElements}の繰り返しを取得する。<p>
     *
     * @return CSVElementsの繰り返し
     */
    public CSVIterator iterator(){
        if(iterator == null){
            iterator = new CSVIterator();
        }
        return iterator;
    }
    
    /**
     * {@link CSVReader.CSVElements}の繰り返し。<p>
     *
     * @author M.Takata
     */
    public class CSVIterator{
        private boolean hasNext = false;
        private CSVElements elements = new CSVElements();
        
        private CSVIterator(){}
        
        /**
         * 次のCSV要素があるかどうかを判定する。<p>
         *
         * @return 次のCSV要素がある場合はtrue
         * @exception IOException 読み込みに失敗した場合
         */
        public boolean hasNext() throws IOException{
            if(hasNext){
                return hasNext;
            }
            List result = readCSVLineList(elements);
            hasNext = result != null;
            return hasNext;
        }
        
        /**
         * 次のCSV要素を取得する。<p>
         *
         * @return 次のCSV要素。次のCSV要素がない場合はnull
         * @exception IOException 読み込みに失敗した場合
         * @see #nextElements()
         */
        public Object next() throws IOException{
            return nextElements();
        }
        
        /**
         * 次のCSV要素を取得する。<p>
         * ここで取得される{@link CSVReader.CSVElements}は、毎回再利用される。<br>
         *
         * @return 次のCSV要素。次のCSV要素がない場合はnull
         * @exception IOException 読み込みに失敗した場合
         */
        public CSVElements nextElements() throws IOException{
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
    public CSVReader cloneReader(){
        return cloneReader(new CSVReader());
    }
    
    /**
     * 未接続の複製を生成する。<p>
     *
     * @param clone 未接続のインスタンス
     * @return 未接続の複製
     */
    protected CSVReader cloneReader(CSVReader clone){
        clone.separator = separator;
        clone.separatorEscape = separatorEscape;
        clone.enclosure = enclosure;
        clone.isIgnoreEmptyLine = isIgnoreEmptyLine;
        clone.isIgnoreLineEndSeparator = isIgnoreLineEndSeparator;
        clone.isEnclosed = isEnclosed;
        clone.nullValue = nullValue;
        clone.commentPrefix = commentPrefix;
        clone.isUnescapeLineSeparatorInEnclosure = isUnescapeLineSeparatorInEnclosure;
        return clone;
    }
    
    /**
     * CSV形式データの1行を表すCSV要素。<p>
     * 
     * @author M.Takata
     */
    public class CSVElements extends ArrayList{
        
        private static final long serialVersionUID = 6079322185163530516L;
        
        private boolean wasNull;
        
        private CSVElements(){}
        
        /**
         * このCSV要素をクリアする。<p>
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
            final String str = getString(index);
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
            final String str = getString(index);
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
            final String str = getString(index);
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
            final String str = getString(index);
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
            final String str = getString(index);
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
            final String str = getString(index);
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
            final String str = getString(index);
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
            final String str = getString(index);
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
            final String str = getString(index);
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
