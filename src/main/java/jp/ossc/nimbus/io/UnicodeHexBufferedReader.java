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
import java.nio.CharBuffer;
import java.lang.reflect.*;

import jp.ossc.nimbus.util.converter.ConvertException;
import jp.ossc.nimbus.util.converter.ReversibleConverter;
import jp.ossc.nimbus.util.converter.StringConverter;

/**
 * 16�i�\���̃��j�R�[�h�������ǂݍ���Reader�N���X�B<p>
 * \u00df����16�i�\�����j�R�[�h����������j�R�[�h�����Ƃ��ēǂݍ��ށB
 *
 * @author H.Nakano
 */
public class UnicodeHexBufferedReader extends BufferedReader implements StringConverter, ReversibleConverter{
    
    /**
     * 16�i�\�����j�R�[�h�����񁨒ʏ핶����ϊ���\���ϊ���ʒ萔�B<p>
     */
    public static final int UNICODE_TO_STRING = POSITIVE_CONVERT;
    
    /**
     * �ʏ핶����16�i�\�����j�R�[�h������ϊ���\���ϊ���ʒ萔�B<p>
     */
    public static final int STRING_TO_UNICODE = REVERSE_CONVERT;
    
    private int convertType = UNICODE_TO_STRING;
    
    private ReaderWrapper readerWrapper;
    
    /**
     * �w�肳�ꂽReader�����b�v���A�f�t�H���g�T�C�Y�̃o�b�t�@�Ńo�b�t�@�����O���ꂽ�A16�i�\�����j�R�[�h�����^���̓X�g���[�����쐬����B<p>
     */
    public UnicodeHexBufferedReader(){
        super(new ReaderWrapper());
        readerWrapper = (ReaderWrapper)lock;
    }
    
    /**
     * �w�肳�ꂽReader�����b�v���A�f�t�H���g�T�C�Y�̃o�b�t�@�Ńo�b�t�@�����O���ꂽ�A16�i�\�����j�R�[�h�����^���̓X�g���[�����쐬����B<p>
     *
     * @param reader ���b�v����Reader
     */
    public UnicodeHexBufferedReader(Reader reader){
        super(new ReaderWrapper(reader));
        readerWrapper = (ReaderWrapper)lock;
    }
    
    /**
     * �w�肳�ꂽReader�����b�v���A�w�肳�ꂽ�T�C�Y�̃o�b�t�@�Ńo�b�t�@�����O���ꂽ�A16�i�\�����j�R�[�h�����^���̓X�g���[�����쐬����B
     *
     * @param reader ���b�v����Reader
     * @param size �o�b�t�@�T�C�Y
     */
    public UnicodeHexBufferedReader(Reader reader, int size){
        super(new ReaderWrapper(reader), size);
        readerWrapper = (ReaderWrapper)lock;
    }
    
    /**
     * Reader��ݒ肷��B<p>
     *
     * @param reader Reader
     * @exception IOException ����Reader���ݒ肳��Ă���ꍇ
     */
    public void setReader(Reader reader) throws IOException{
        readerWrapper.setReader(reader);
    }
    
    /**
     * 1 �s��16�i�\�����j�R�[�h������𕶎���Ƃ��ēǂݍ��ށB<p>
     * 1 �s�̏I�[�́A���s ('\n') ���A���A ('\r')�A�܂��͕��s�Ƃ���ɑ������s�̂ǂꂩ�ŔF�������B<br>
     *
     * @return �s�̓��e���܂ޕ�����A�������s�̏I�[�����͊܂߂Ȃ��B�X�g���[���̏I���ɒB���Ă���ꍇ�� null
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public String readLine() throws IOException{
        //��ʃN���X�̃��\�b�h�ōs�ǂݍ��݂���B
        String str = super.readLine() ;
        if(str!=null){    
            str = convertString(str) ;
        }    
        return str;
    }
    
    /**
     * 1 �s�̕������16�i�\�����j�R�[�h������Ƃ��ēǂݍ��ށB<p>
     * 1 �s�̏I�[�́A���s ('\n') ���A���A ('\r')�A�܂��͕��s�Ƃ���ɑ������s�̂ǂꂩ�ŔF�������B<br>
     *
     * @return �s�̓��e���܂ޕ�����A�������s�̏I�[�����͊܂߂Ȃ��B�X�g���[���̏I���ɒB���Ă���ꍇ�� null
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public String readLineInverse() throws IOException{
        //��ʃN���X�̃��\�b�h�ōs�ǂݍ��݂���B
        String str = super.readLine() ;
        if(str!=null){    
            str = convertUnicode(str) ;
        }    
        return str;
    }
    
    /**
     * �w�肳�ꂽ�������16�i�\�����j�R�[�h������ɕϊ�����B<p>
     *
     * @param unicodeStr ������
     * @return 16�i�\�����j�R�[�h������
     */
    public static String convertUnicode(String unicodeStr){
        String str = null;
        if(unicodeStr != null){
            final int len = unicodeStr.length();
            final StringBuilder buf = new StringBuilder(len*6);
            for(int i = 0;i<len;i++){
                convertUnicode(unicodeStr.charAt(i), buf);
            }
            str = buf.toString();
        }
        return str;
    }
    
    /**
     * �w�肳�ꂽ������16�i�\�����j�R�[�h������ɕϊ�����B<p>
     *
     * @param unicodeChar ����
     * @return 16�i�\�����j�R�[�h������
     */
    public static String convertUnicode(char unicodeChar){
        return convertUnicode(unicodeChar, new StringBuilder(6)).toString();
    }
    
    /**
     * �w�肳�ꂽ������16�i�\�����j�R�[�h������ɕϊ�����B<p>
     *
     * @param unicodeChar ����
     * @param buf ������o�b�t�@
     * @return 16�i�\�����j�R�[�h������
     */
    public static StringBuilder convertUnicode(char unicodeChar, StringBuilder buf){
        char c = unicodeChar;
        buf.append('\\');
        buf.append('u');
        int mask = 0xf000;
        int ret = 0x0000;
        for(int j = 0; j < 4; j++){
            //�}�X�N���S�r�b�g�E�V�t�g����
            mask = 0xf000 >> (j*4);
            ret = c & mask;
            //AND�������̂��S�r�b�g���V�t�g����
            ret = ret << (j*4);
            switch(ret){
            case 0x0000:
                buf.append('0');
                break;
            case 0x1000:
                buf.append('1');
                break;
            case 0x2000:
                buf.append('2');
                break;
            case 0x3000:
                buf.append('3');
                break;
            case 0x4000:
                buf.append('4');
                break;
            case 0x5000:
                buf.append('5');
                break;
            case 0x6000:
                buf.append('6');
                break;
            case 0x7000:
                buf.append('7');
                break;
            case 0x8000:
                buf.append('8');
                break;
            case 0x9000:
                buf.append('9');
                break;
            case 0xa000:
                buf.append('a');
                break;
            case 0xb000:
                buf.append('b');
                break;
            case 0xc000:
                buf.append('c');
                break;
            case 0xd000:
                buf.append('d');
                break;
            case 0xe000:
                buf.append('e');
                break;
            case 0xf000:
                buf.append('f');
                break;
            default:
            }
        }
        return buf;
    }
    
    /**
     * �w�肳�ꂽ16�i�\�����j�R�[�h������𕶎���ɕϊ�����B<p>
     *
     * @param unicodeAry 16�i�\�����j�R�[�h������
     * @return ������
     */
    public static String convertString(String unicodeAry){
        String str = null;
        if(unicodeAry != null){
            char c;
            int len = unicodeAry.length();
            StringBuilder buf = new StringBuilder(len);
            for(int i = 0;i<len;){
                //�������؂���
                c = unicodeAry.charAt(i++);
                //�G�X�P�[�v�Ȃ�
                if(c == '\\' && (len - 1) > i){
                    c = unicodeAry.charAt(i++);
                    //UNICODE�}�[�N
                    if(c == 'u'){
                        int value = 0;
                        //�S�����ǂݍ���
                        for(int j=0;j<4;j++){
                            c = unicodeAry.charAt(i++);
                            switch(c){
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + (c - '0');
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + (c - 'a');
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + (c - 'A');
                                break;
                            default:
                                throw new IllegalArgumentException(
                                    "Failed to convert unicode char is " + c
                                );
                            }
                        }
                        buf.append((char)value);
                    }else{
                        switch(c){
                        case 't': c = '\t';break;
                        case 'r': c = '\r';break;
                        case 'n': c = '\n';break;
                        case 'f': c = '\f';break;
                        case '\\': c = '\\';break;
                        default:
                        }
                        buf.append(c);
                    }
                }else{
                    buf.append(c);
                }
            }
            str = buf.toString();
        }
        return str;
    }
    
    public Object convert(Object obj) throws ConvertException{
        return convert((String)(obj == null ? null : obj.toString()));
    }
    
    public String convert(String str) throws ConvertException{
        switch(convertType){
        case STRING_TO_UNICODE:
            return convertUnicode(str);
        case UNICODE_TO_STRING:
        default:
            return convertString(str);
        }
    }
    
    public void setConvertType(int type) {
        switch(type){
        case UNICODE_TO_STRING:
        case STRING_TO_UNICODE:
            convertType = type;
            break;
        default:
            throw new IllegalArgumentException(
                "Invalid convert type : " + type
            );
        }
    }
    
    /**
     * �ϊ���ʂ��擾����B<p>
     *
     * @return �ϊ����
     * @see #setConvertType(int)
     */
    public int getConvertType(){
        return convertType;
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
        
        public void setReader(Reader reader) throws IOException{
            if(realReader != null){
                throw new IOException("Reader is already commited.");
            }
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
