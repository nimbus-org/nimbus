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
 * CSV�`����Reader�N���X�B<p>
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
     * �f�t�H���g�̃Z�p���[�^�B<p>
     */
    public static final char DEFAULT_SEPARATOR = ',';
    
    /**
     * �f�t�H���g�̃Z�p���[�^�̃G�X�P�[�v�����B<p>
     * �G�X�P�[�v�������G�X�P�[�v�������ꍇ�́A�G�X�P�[�v�������d�˂�B<br>
     */
    public static final char DEFAULT_SEPARATOR_ESCAPE = '\\';
    
    /**
     * �f�t�H���g�̈͂ݕ����B<p>
     * �͂ݕ������G�X�P�[�v�������ꍇ�́A�͂ݕ������d�˂�B<br>
     */
    public static final char DEFAULT_ENCLOSURE = '"';
    
    /**
     * �f�t�H���g�̉��s�����B<p>
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
     * �f�t�H���g�̓ǂݍ��݃o�b�t�@�T�C�Y�������ڑ��̃C���X�^���X�𐶐�����B<p>
     */
    public CSVReader(){
        super(new ReaderWrapper());
        readerWrapper = (ReaderWrapper)lock;
    }
    
    /**
     * �f�t�H���g�̓ǂݍ��݃o�b�t�@�T�C�Y�����C���X�^���X�𐶐�����B<p>
     *
     * @param reader �ǂݍ��݌���Reader
     */
    public CSVReader(Reader reader){
        super(new ReaderWrapper(reader));
        readerWrapper = (ReaderWrapper)lock;
    }
    
    /**
     * �w�肳�ꂽ�ǂݍ��݃o�b�t�@�T�C�Y�������ڑ��̃C���X�^���X�𐶐�����B<p>
     *
     * @param size �ǂݍ��݃o�b�t�@�T�C�Y
     */
    public CSVReader(int size){
        super(new ReaderWrapper(), size);
        readerWrapper = (ReaderWrapper)lock;
    }
    
    /**
     * �w�肳�ꂽ�ǂݍ��݃o�b�t�@�T�C�Y�����C���X�^���X�𐶐�����B<p>
     *
     * @param reader �ǂݍ��݌���Reader
     * @param size �ǂݍ��݃o�b�t�@�T�C�Y
     */
    public CSVReader(Reader reader, int size){
        super(new ReaderWrapper(reader), size);
        readerWrapper = (ReaderWrapper)lock;
    }
    
    /**
     * Reader��ݒ肷��B<p>
     *
     * @param reader Reader
     */
    public void setReader(Reader reader){
        readerWrapper.setReader(reader);
    }
    
    /**
     * �Z�p���[�^��ݒ肷��B<p>
     *
     * @param separator �Z�p���[�^
     */
    public void setSeparator(char separator){
        this.separator = separator;
    }
    
    /**
     * �Z�p���[�^���擾����B<p>
     *
     * @return �Z�p���[�^
     */
    public char getSeparator(){
         return separator;
    }
    
    /**
     * �Z�p���[�^�̃G�X�P�[�v������ݒ肷��B<p>
     *
     * @param escape �G�X�P�[�v����
     */
    public void setSeparatorEscape(char escape){
        separatorEscape = escape;
    }
    
    /**
     * �Z�p���[�^�̃G�X�P�[�v�������擾����B<p>
     *
     * @return �G�X�P�[�v����
     */
    public char getSeparatorEscape(){
         return separatorEscape;
    }
    
    /**
     * �͂ݕ�����ݒ肷��B<p>
     *
     * @param enclosure �͂ݕ���
     */
    public void setEnclosure(char enclosure){
        this.enclosure = enclosure;
    }
    
    /**
     * �͂ݕ������擾����B<p>
     *
     * @return �͂ݕ���
     */
    public char getEnclosure(){
         return enclosure;
    }
    
    /**
     * ��s�𖳎����邩�ǂ�����ݒ肷��B<p>
     * ��s�𖳎�����悤�ɐݒ肵���ꍇ�A��s�͍s���Ƃ��Ă��J�E���g����Ȃ��B<br>
     * �f�t�H���g�́Afalse�Ŗ������Ȃ��B<br>
     *
     * @param isIgnore ��s�𖳎�����ꍇtrue
     */
    public void setIgnoreEmptyLine(boolean isIgnore){
        isIgnoreEmptyLine = isIgnore;
    }
    
    /**
     * ��s�𖳎����邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A��s�𖳎�����
     */
    public boolean isIgnoreEmptyLine(){
         return isIgnoreEmptyLine;
    }
    
    /**
     * �s�̍Ō�̃Z�p���[�^�𖳎����邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�Ŗ������Ȃ��B<br>
     *
     * @param isIgnore �s�̍Ō�̃Z�p���[�^�𖳎�����ꍇtrue
     */
    public void setIgnoreLineEndSeparator(boolean isIgnore){
        isIgnoreLineEndSeparator = isIgnore;
    }
    
    /**
     * �s�̍Ō�̃Z�p���[�^�𖳎����邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�s�̍Ō�̃Z�p���[�^�𖳎�����
     */
    public boolean isIgnoreLineEndSeparator(){
         return isIgnoreLineEndSeparator;
    }
    
    /**
     * �͂ݕ������L�����ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�ň͂ݕ����͖������Ȃ��B<br>
     *
     * @param isEnclosed �͂ݕ������L���ȏꍇtrue
     */
    public void setEnclosed(boolean isEnclosed){
        this.isEnclosed = isEnclosed;
    }
    
    /**
     * �͂ݕ������L�����ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�͂ݕ������L��
     */
    public boolean isEnclosed(){
         return isEnclosed;
    }
    
    /**
     * �g�������邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�Ńg�������Ȃ��B<br>
     *
     * @param isTrim �g��������ꍇtrue
     */
    public void setTrim(boolean isTrim){
        this.isTrim = isTrim;
    }
    
    /**
     * �g�������邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�g��������
     */
    public boolean isTrim(){
         return isTrim;
    }
    
    /**
     * CSV�v�f��ǂݍ��񂾏ꍇ�ɁAnull�l�������镶�����ݒ肷��B<p>
     *
     * @param value ������
     */
    public void setNullValue(String value){
        nullValue = value;
    }
    
    /**
     * CSV�v�f��ǂݍ��񂾏ꍇ�ɁAnull�l�������镶������擾����B<p>
     *
     * @return ������
     */
    public String getNullValue(){
        return nullValue;
    }
    
    /**
     * �R�����g�s�̑O�u�������ݒ肷��B<p>
     *
     * @param value �R�����g�s�̑O�u������
     */
    public void setCommentPrefix(String value){
        commentPrefix = value;
    }
    
    /**
     * �R�����g�s�̑O�u��������擾����B<p>
     *
     * @return �R�����g�s�̑O�u������
     */
    public String getCommentPrefix(){
        return commentPrefix;
    }
    
    /**
     * �͂ݕ����ň͂܂ꂽCSV�v�f�̏ꍇ�ɁA�G�X�P�[�v���ꂽ���s���A���G�X�P�[�v���邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�ŃA���G�X�P�[�v���Ȃ��B<br>
     * 
     * @param isUnescape �A���G�X�P�[�v����ꍇtrue
     */
    public void setUnescapeLineSeparatorInEnclosure(boolean isUnescape){
        isUnescapeLineSeparatorInEnclosure = isUnescape;
    }
    
    /**
     * �͂ݕ����ň͂܂ꂽCSV�v�f�̏ꍇ�ɁA�G�X�P�[�v���ꂽ���s���A���G�X�P�[�v���邩�ǂ����𔻒肷��B<p>
     * 
     * @return true�̏ꍇ�A�A���G�X�P�[�v����
     */
    public boolean isUnescapeLineSeparatorInEnclosure(){
        return isUnescapeLineSeparatorInEnclosure;
    }
    
    /**
     * �w�肳�ꂽ�s�����X�L�b�v����B<p>
     *
     * @param line �X�L�b�v����s��
     * @return �X�L�b�v���ꂽ�s��
     * @exception IOException ���o�̓G���[�����������ꍇ
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
     * �w�肳�ꂽCSV�s�����X�L�b�v����B<p>
     * {@link #isIgnoreEmptyLine()}��true�̏ꍇ�́A��s�̓X�L�b�v�s���̃J�E���g���珜�����B<br>
     * CSV�s���ŃJ�E���g����邽�߁A�͂ݕ����ň͂񂾒��ɉ��s�������Ă��A1�s�Ƃ��ăJ�E���g�����B<br>
     *
     * @param line �X�L�b�v����s��
     * @return �X�L�b�v���ꂽ�s��
     * @exception IOException ���o�̓G���[�����������ꍇ
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
     * CSV�s��1�s�ǂݍ��ށB<p>
     *
     * @return CSV�v�f�̕�����z��
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public String[] readCSVLine() throws IOException{
        final List csv = readCSVLineList();
        return csv == null ? null
             : (String[])csv.toArray(new String[csv.size()]);
    }
    
    /**
     * CSV�s��1�s�ǂݍ��ށB<p>
     *
     * @return CSV�v�f�̕����񃊃X�g
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public List readCSVLineList() throws IOException{
        return readCSVLineList(null);
    }
    
    /**
     * CSV�s��1�s�ǂݍ��ށB<p>
     * CSV�v�f�̕�������i�[���郊�X�g���ė��p���邽�߂̃��\�b�h�ł���B<br>
     *
     * @param csv CSV�v�f�̕�������i�[���郊�X�g
     * @return CSV�v�f�̕����񃊃X�g
     * @exception IOException ���o�̓G���[�����������ꍇ
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
        final StringBuffer buf = new StringBuffer();
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
                                || (!inEnclosure && (imax - 1 == i || buf.length() != 0))
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
     * �w�肳�ꂽ��������g��������B<p>
     * �g�����́A�w�肳�ꂽ������̑O��̋󔒕����i{@link Character#isWhitespace(char)}��true�ƂȂ镶���j���폜����B
     * 
     * @param buf ������
     * @return �g�������ꂽ������
     */
    protected static StringBuffer trim(StringBuffer buf){
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
            // �N����Ȃ��͂�
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
            // �N����Ȃ��͂�
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
            // �N����Ȃ��͂�
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
            // �N����Ȃ��͂�
            return result;
        }
        return result;
    }
    
    /**
     * {@link CSVReader.CSVElements}�̌J��Ԃ����擾����B<p>
     *
     * @return CSVElements�̌J��Ԃ�
     */
    public CSVIterator iterator(){
        if(iterator == null){
            iterator = new CSVIterator();
        }
        return iterator;
    }
    
    /**
     * {@link CSVReader.CSVElements}�̌J��Ԃ��B<p>
     *
     * @author M.Takata
     */
    public class CSVIterator{
        private boolean hasNext = false;
        private CSVElements elements = new CSVElements();
        
        private CSVIterator(){}
        
        /**
         * ����CSV�v�f�����邩�ǂ����𔻒肷��B<p>
         *
         * @return ����CSV�v�f������ꍇ��true
         * @exception IOException �ǂݍ��݂Ɏ��s�����ꍇ
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
         * ����CSV�v�f���擾����B<p>
         *
         * @return ����CSV�v�f�B����CSV�v�f���Ȃ��ꍇ��null
         * @exception IOException �ǂݍ��݂Ɏ��s�����ꍇ
         * @see #nextElements()
         */
        public Object next() throws IOException{
            return nextElements();
        }
        
        /**
         * ����CSV�v�f���擾����B<p>
         * �����Ŏ擾�����{@link CSVReader.CSVElements}�́A����ė��p�����B<br>
         *
         * @return ����CSV�v�f�B����CSV�v�f���Ȃ��ꍇ��null
         * @exception IOException �ǂݍ��݂Ɏ��s�����ꍇ
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
     * ���ڑ��̕����𐶐�����B<p>
     *
     * @return ���ڑ��̕���
     */
    public CSVReader cloneReader(){
        return cloneReader(new CSVReader());
    }
    
    /**
     * ���ڑ��̕����𐶐�����B<p>
     *
     * @param clone ���ڑ��̃C���X�^���X
     * @return ���ڑ��̕���
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
     * CSV�`���f�[�^��1�s��\��CSV�v�f�B<p>
     * 
     * @author M.Takata
     */
    public class CSVElements extends ArrayList{
        
        private static final long serialVersionUID = 6079322185163530516L;
        
        private boolean wasNull;
        
        private CSVElements(){}
        
        /**
         * ����CSV�v�f���N���A����B<p>
         */
        public void clear(){
            wasNull = false;
            super.clear();
        }
        
        /**
         * �擾�����l��null���������ǂ����𔻒肷��B<p>
         * {@link #getInt(int)}�Ȃǂ́A���l�n��getter�Œl���擾�����ꍇ�A�l��null��󕶎��������ꍇ�ɁA0��Ԃ��B���̎��A�l��0�������̂�null�܂��͋󕶎��������̂��𔻒f����̂Ɏg�p����B<br>
         *
         * @return �擾�����l��null�������ꍇtrue
         */
        public boolean wasNull(){
            return wasNull;
        }
        
        /**
         * �w�肳�ꂽ�C���f�b�N�X�̗v�f���擾����B<p>
         *
         * @param index �C���f�b�N�X
         * @return �w�肳�ꂽ�C���f�b�N�X�̗v�f
         */
        public Object get(int index){
            Object obj = super.get(index);
            wasNull = obj == null;
            return obj;
        }
        
        /**
         * �w�肳�ꂽ�C���f�b�N�X�̗v�f��������擾����B<p>
         *
         * @param index �C���f�b�N�X
         * @return �w�肳�ꂽ�C���f�b�N�X�̗v�f������
         */
        public String getString(int index){
            String str = (String)get(index);
            wasNull = str == null;
            return str;
        }
        
        /**
         * �w�肳�ꂽ�C���f�b�N�X�̗v�f�o�C�g���擾����B<p>
         *
         * @param index �C���f�b�N�X
         * @return �w�肳�ꂽ�C���f�b�N�X�̗v�f�o�C�g
         * @exception NumberFormatException �v�f���o�C�g������łȂ��ꍇ
         */
        public byte getByte(int index) throws NumberFormatException{
            return getByte(index, 10);
        }
        
        /**
         * �w�肳�ꂽ�C���f�b�N�X�̗v�f�o�C�g���擾����B<p>
         * �w�肳�ꂽ�v�f��null�܂��͋󕶎��̏ꍇ�́A0��Ԃ��A{@link #wasNull()}��true��Ԃ��B<br>
         *
         * @param index �C���f�b�N�X
         * @param radix �
         * @return �w�肳�ꂽ�C���f�b�N�X�̗v�f�o�C�g
         * @exception NumberFormatException �v�f���o�C�g������łȂ��ꍇ
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
         * �w�肳�ꂽ�C���f�b�N�X�̗v�f���l���擾����B<p>
         * �w�肳�ꂽ�v�f��null�܂��͋󕶎��̏ꍇ�́A0��Ԃ��A{@link #wasNull()}��true��Ԃ��B<br>
         *
         * @param index �C���f�b�N�X
         * @return �w�肳�ꂽ�C���f�b�N�X�̗v�f���l
         * @exception NumberFormatException �v�f�����l������łȂ��ꍇ
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
         * �w�肳�ꂽ�C���f�b�N�X�̗v�f�������擾����B<p>
         * �w�肳�ꂽ�v�f��null�܂��͋󕶎��̏ꍇ�́A0��Ԃ��A{@link #wasNull()}��true��Ԃ��B<br>
         * �܂��A�w�肳�ꂽ�v�f���A�����������琬��ꍇ�́A1�����ڂ�Ԃ��B<br>
         *
         * @param index �C���f�b�N�X
         * @return �w�肳�ꂽ�C���f�b�N�X�̗v�f����
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
         * �w�肳�ꂽ�C���f�b�N�X�̗v�f���l���擾����B<p>
         * �w�肳�ꂽ�v�f��null�܂��͋󕶎��̏ꍇ�́A0��Ԃ��A{@link #wasNull()}��true��Ԃ��B<br>
         *
         * @param index �C���f�b�N�X
         * @return �w�肳�ꂽ�C���f�b�N�X�̗v�f���l
         * @exception NumberFormatException �v�f�����l������łȂ��ꍇ
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
         * �w�肳�ꂽ�C���f�b�N�X�̗v�f���l���擾����B<p>
         * �w�肳�ꂽ�v�f��null�܂��͋󕶎��̏ꍇ�́A0��Ԃ��A{@link #wasNull()}��true��Ԃ��B<br>
         *
         * @param index �C���f�b�N�X
         * @return �w�肳�ꂽ�C���f�b�N�X�̗v�f���l
         * @exception NumberFormatException �v�f�����l������łȂ��ꍇ
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
         * �w�肳�ꂽ�C���f�b�N�X�̗v�f���l���擾����B<p>
         * �w�肳�ꂽ�v�f��null�܂��͋󕶎��̏ꍇ�́A0��Ԃ��A{@link #wasNull()}��true��Ԃ��B<br>
         *
         * @param index �C���f�b�N�X
         * @return �w�肳�ꂽ�C���f�b�N�X�̗v�f���l
         * @exception NumberFormatException �v�f�����l������łȂ��ꍇ
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
         * �w�肳�ꂽ�C���f�b�N�X�̗v�f���l���擾����B<p>
         * �w�肳�ꂽ�v�f��null�܂��͋󕶎��̏ꍇ�́A0��Ԃ��A{@link #wasNull()}��true��Ԃ��B<br>
         *
         * @param index �C���f�b�N�X
         * @return �w�肳�ꂽ�C���f�b�N�X�̗v�f���l
         * @exception NumberFormatException �v�f�����l������łȂ��ꍇ
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
         * �w�肳�ꂽ�C���f�b�N�X�̗v�f�t���O���擾����B<p>
         * �w�肳�ꂽ�v�f��null�܂��͋󕶎��̏ꍇ�́Afalse��Ԃ��A{@link #wasNull()}��true��Ԃ��B<br>
         *
         * @param index �C���f�b�N�X
         * @return �w�肳�ꂽ�C���f�b�N�X�̗v�f�t���O
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
         * �w�肳�ꂽ�C���f�b�N�X�̗v�f���l���擾����B<p>
         * �w�肳�ꂽ�v�f��null�܂��͋󕶎��̏ꍇ�́Anull��Ԃ��A{@link #wasNull()}��true��Ԃ��B<br>
         *
         * @param index �C���f�b�N�X
         * @return �w�肳�ꂽ�C���f�b�N�X�̗v�f���l
         * @exception NumberFormatException �v�f�����l������łȂ��ꍇ
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
         * �w�肳�ꂽ�C���f�b�N�X�̗v�f���l���擾����B<p>
         * �w�肳�ꂽ�v�f��null�܂��͋󕶎��̏ꍇ�́Anull��Ԃ��A{@link #wasNull()}��true��Ԃ��B<br>
         *
         * @param index �C���f�b�N�X
         * @return �w�肳�ꂽ�C���f�b�N�X�̗v�f���l
         * @exception NumberFormatException �v�f�����l������łȂ��ꍇ
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
