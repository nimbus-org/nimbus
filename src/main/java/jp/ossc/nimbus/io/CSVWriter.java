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
 * CSV�`����Writer�N���X�B<p>
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
     * �f�t�H���g�̏������݃o�b�t�@�T�C�Y�������ڑ��̃C���X�^���X�𐶐�����B<p>
     */
    public CSVWriter(){
        super(new WriterWrapper());
        writerWrapper = (WriterWrapper)lock;
    }
    
    /**
     * �f�t�H���g�̏������݃o�b�t�@�T�C�Y�����C���X�^���X�𐶐�����B<p>
     *
     * @param writer �������ݐ��Writer
     */
    public CSVWriter(Writer writer){
        super(new WriterWrapper(writer));
        writerWrapper = (WriterWrapper)lock;
    }
    
    /**
     * �w�肳�ꂽ�������݃o�b�t�@�T�C�Y�������ڑ��̃C���X�^���X�𐶐�����B<p>
     *
     * @param size �������݃o�b�t�@�T�C�Y
     */
    public CSVWriter(int size){
        super(new WriterWrapper(), size);
        writerWrapper = (WriterWrapper)lock;
    }
    
    /**
     * �w�肳�ꂽ�������݃o�b�t�@�T�C�Y�����C���X�^���X�𐶐�����B<p>
     *
     * @param writer �������ݐ��Writer
     * @param size �������݃o�b�t�@�T�C�Y
     */
    public CSVWriter(Writer writer, int size){
        super(new WriterWrapper(writer), size);
        writerWrapper = (WriterWrapper)lock;
    }
    
    /**
     * Writer��ݒ肷��B<p>
     *
     * @param writer Writer
     */
    public void setWriter(Writer writer){
        writerWrapper.setWriter(writer);
        isAppendElement = false;
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
     * ���s�Z�p���[�^��ݒ肷��B<p>
     *
     * @param separator ���s�Z�p���[�^
     */
    public void setLineSeparator(String separator){
        this.lineSeparator = separator;
    }
    
    /**
     * ���s�Z�p���[�^���擾����B<p>
     *
     * @return ���s�Z�p���[�^
     */
    public String getLineSeparator(){
         return lineSeparator;
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
     * CSV�̗v�f���͂ݕ����ň͂ނ��ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�ň͂܂Ȃ��B<br>
     *
     * @param isEnclose �͂ݕ����ň͂ޏꍇtrue
     */
    public void setEnclose(boolean isEnclose){
        this.isEnclose = isEnclose;
    }
    
    /**
     * CSV�̗v�f���͂ݕ����ň͂ނ��ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�͂ݕ����ň͂�
     */
    public boolean isEnclose(){
         return isEnclose;
    }
    
    /**
     * null��CSV�v�f�Ƃ��ď����������Ƃ����ꍇ�ɁA�o�͂��镶�����ݒ肷��B<p>
     * �ݒ肵�Ȃ��ꍇ�́ANullPointerException����������B<br>
     *
     * @param value ������
     */
    public void setNullValue(String value){
        nullValue = value;
    }
    
    /**
     * null��CSV�v�f�Ƃ��ď����������Ƃ����ꍇ�ɁA�o�͂��镶������擾����B<p>
     *
     * @return ������
     */
    public String getNullValue(){
        return nullValue;
    }
    
    /**
     * CSV�̗v�f���͂ݕ����ň͂ޏꍇ�ɁA���s���G�X�P�[�v���邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�ŃG�X�P�[�v���Ȃ��B<br>
     * 
     * @param isEscape �G�X�P�[�v����ꍇtrue
     */
    public void setEscapeLineSeparatorInEnclosure(boolean isEscape){
        isEscapeLineSeparatorInEnclosure = isEscape;
    }
    
    /**
     * CSV�̗v�f���͂ݕ����ň͂ޏꍇ�ɁA���s���G�X�P�[�v���邩�ǂ����𔻒肷��B<p>
     * 
     * @return true�̏ꍇ�A�G�X�P�[�v����
     */
    public boolean isEscapeLineSeparatorInEnclosure(){
        return isEscapeLineSeparatorInEnclosure;
    }
    
    /**
     * �s��؂蕶�����������ށB<p>
     * �s��؂蕶���́A{@link #getLineSeparator()}���g�p����B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void newLine() throws IOException{
        super.write(lineSeparator);
        isAppendElement = false;
    }
    
    /**
     * CSV�v�f��������������ށB<p>
     * �Z�p���[�^�̒ǉ��A�Z�p���[�^�������܂܂�Ă���ꍇ�̃G�X�P�[�v�A�͂ݕ����ł̈͂ݏ����������ōs���B<br>
     * 
     * @param element CSV�v�f������
     * @exception IOException ���o�̓G���[�����������ꍇ
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
     * CSV�v�f���������ށB<p>
     * 
     * @param element CSV�v�f
     * @exception IOException ���o�̓G���[�����������ꍇ
     * @see #writeElement(String)
     */
    public void writeElement(boolean element) throws IOException{
        writeElement(Boolean.toString(element));
    }
    
    /**
     * CSV�v�f���������ށB<p>
     * 
     * @param element CSV�v�f
     * @exception IOException ���o�̓G���[�����������ꍇ
     * @see #writeElement(String)
     */
    public void writeElement(byte element) throws IOException{
        writeElement(Byte.toString(element));
    }
    
    /**
     * CSV�v�f���������ށB<p>
     * 
     * @param element CSV�v�f
     * @exception IOException ���o�̓G���[�����������ꍇ
     * @see #writeElement(String)
     */
    public void writeElement(char element) throws IOException{
        writeElement(Character.toString(element));
    }
    
    /**
     * CSV�v�f���������ށB<p>
     * 
     * @param element CSV�v�f
     * @exception IOException ���o�̓G���[�����������ꍇ
     * @see #writeElement(String)
     */
    public void writeElement(short element) throws IOException{
        writeElement(Short.toString(element));
    }
    
    /**
     * CSV�v�f���������ށB<p>
     * 
     * @param element CSV�v�f
     * @exception IOException ���o�̓G���[�����������ꍇ
     * @see #writeElement(String)
     */
    public void writeElement(int element) throws IOException{
        writeElement(Integer.toString(element));
    }
    
    /**
     * CSV�v�f���������ށB<p>
     * 
     * @param element CSV�v�f
     * @exception IOException ���o�̓G���[�����������ꍇ
     * @see #writeElement(String)
     */
    public void writeElement(long element) throws IOException{
        writeElement(Long.toString(element));
    }
    
    /**
     * CSV�v�f���������ށB<p>
     * 
     * @param element CSV�v�f
     * @exception IOException ���o�̓G���[�����������ꍇ
     * @see #writeElement(String)
     */
    public void writeElement(float element) throws IOException{
        writeElement(Float.toString(element));
    }
    
    /**
     * CSV�v�f���������ށB<p>
     * 
     * @param element CSV�v�f
     * @exception IOException ���o�̓G���[�����������ꍇ
     * @see #writeElement(String)
     */
    public void writeElement(double element) throws IOException{
        writeElement(Double.toString(element));
    }
    
    /**
     * CSV�v�f���������ށB<p>
     * 
     * @param element CSV�v�f
     * @exception IOException ���o�̓G���[�����������ꍇ
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
     * �w�肳�ꂽ������z���CSV�Ƃ��ď������ށB<p>
     * ���s�����̒ǉ��A�Z�p���[�^�̒ǉ��A�Z�p���[�^�������܂܂�Ă���ꍇ�̃G�X�P�[�v�A�͂ݕ����ł̈͂ݏ����������ōs���B<br>
     *
     * @param elements CSV�`���ŏo�͂��镶����z��
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void writeCSV(String[] elements) throws IOException{
        for(int i = 0; i < elements.length; i++){
            writeElement(elements[i]);
        }
        newLine();
    }
    
    /**
     * �w�肳�ꂽ�z���CSV�Ƃ��ď������ށB<p>
     *
     * @param elements CSV�`���ŏo�͂���z��
     * @exception IOException ���o�̓G���[�����������ꍇ
     * @see #writeCSV(String[])
     */
    public void writeCSV(Object[] elements) throws IOException{
        for(int i = 0; i < elements.length; i++){
            writeElement(elements[i]);
        }
        newLine();
    }
    
    /**
     * �w�肳�ꂽ���X�g��CSV�Ƃ��ď������ށB<p>
     * ���s�����̒ǉ��A�Z�p���[�^�̒ǉ��A�Z�p���[�^�������܂܂�Ă���ꍇ�̃G�X�P�[�v�A�͂ݕ����ł̈͂ݏ����������ōs���B<br>
     *
     * @param elements CSV�`���ŏo�͂��郊�X�g
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void writeCSV(List elements) throws IOException{
        for(int i = 0, imax = elements.size(); i < imax; i++){
            writeElement(elements.get(i));
        }
        newLine();
    }
    
    /**
     * ������z����w�肳�ꂽCSV�`��������ɕϊ�����B<p>
     *
     * @param separator �Z�p���[�^
     * @param escape �G�X�P�[�v����
     * @param nullValue null��CSV�v�f�Ƃ��ď����������Ƃ����ꍇ�ɁA�o�͂��镶����
     * @param elements CSV�̗v�f�ƂȂ镶����z��
     * @return CSV�`��������
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
     * ������z����w�肳�ꂽCSV�`��������ɕϊ�����B<p>
     *
     * @param elements CSV�̗v�f�ƂȂ镶����z��
     * @param separator �Z�p���[�^
     * @param enclosure �͂ݕ���
     * @param nullValue null��CSV�v�f�Ƃ��ď����������Ƃ����ꍇ�ɁA�o�͂��镶����
     * @param isEscapeLineSeparator ���s���G�X�P�[�v���邩�ǂ����B�G�X�P�[�v����ꍇtrue
     * @return CSV�`��������
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
     * �����񃊃X�g���w�肳�ꂽCSV�`��������ɕϊ�����B<p>
     *
     * @param elements CSV�̗v�f�ƂȂ镶���񃊃X�g
     * @param separator �Z�p���[�^
     * @param separatorEscape �G�X�P�[�v����
     * @param nullValue null��CSV�v�f�Ƃ��ď����������Ƃ����ꍇ�ɁA�o�͂��镶����
     * @return CSV�`��������
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
     * �����񃊃X�g���w�肳�ꂽCSV�`��������ɕϊ�����B<p>
     *
     * @param elements CSV�̗v�f�ƂȂ镶���񃊃X�g
     * @param separator �Z�p���[�^
     * @param enclosure �͂ݕ���
     * @param nullValue null��CSV�v�f�Ƃ��ď����������Ƃ����ꍇ�ɁA�o�͂��镶����
     * @param isEscapeLineSeparator ���s���G�X�P�[�v���邩�ǂ����B�G�X�P�[�v����ꍇtrue
     * @return CSV�`��������
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
     * ���ڑ��̕����𐶐�����B<p>
     *
     * @return ���ڑ��̕���
     */
    public CSVWriter cloneWriter(){
        return cloneWriter(new CSVWriter());
    }
    
    /**
     * ���ڑ��̕����𐶐�����B<p>
     *
     * @param clone ���ڑ��̃C���X�^���X
     * @return ���ڑ��̕���
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