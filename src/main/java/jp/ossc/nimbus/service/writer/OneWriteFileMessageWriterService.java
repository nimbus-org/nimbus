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
package jp.ossc.nimbus.service.writer;

import java.io.*;
import java.util.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.context.Context;

/**
 * ���I�Ƀt�@�C���������߂ăt�@�C���ɏo�͂���{@link MessageWriter}�T�[�r�X�B<p>
 *
 * @author M.Takata
 */
public class OneWriteFileMessageWriterService extends ServiceBase
 implements OneWriteFileMessageWriterServiceMBean, MessageWriter{

    private static final long serialVersionUID = -6432768686393327903L;

    private String encoding;

    private String file;

    private boolean isEveryTimeCloseStream = true;

    private FileName fileName;

    private FileOutputStream fos;
    private boolean isAppend;

    private String filePrefix;

    private String filePostfix;

    private ServiceName contextServiceName;
    private Context context;

    private boolean isOutputKey = true;

    private String separator;

    private String header;

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public void setEncoding(String encoding){
        this.encoding = encoding;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public String getEncoding(){
        return encoding;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public void setFile(String file){
        this.file = file;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public String getFile(){
        return file;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public void setAppend(boolean isAppend){
        this.isAppend = isAppend;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public boolean isAppend(){
        return isAppend;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public void setEveryTimeCloseStream(boolean isClose){
        isEveryTimeCloseStream = isClose;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public boolean isEveryTimeCloseStream(){
        return isEveryTimeCloseStream;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public void setFilePrefix(String prefix){
        filePrefix = prefix;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public String getFilePrefix(){
        return filePrefix;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public void setFilePostfix(String postfix){
        filePostfix = postfix;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public String getFilePostfix(){
        return filePostfix;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public void setContextServiceName(ServiceName name){
        contextServiceName = name;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public ServiceName getContextServiceName(){
        return contextServiceName;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public void setOutputKey(boolean isOutput){
        isOutputKey = isOutput;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public boolean isOutputKey(){
        return isOutputKey;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public void setSeparator(String separator){
        this.separator = separator;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public String getSeparator(){
        return separator;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public void setHeader(String header){
        this.header = header;
    }

    // OneWriteFileMessageWriterServiceMBean��JavaDoc
    public String getHeader(){
        return header;
    }

    /**
     * �R���e�L�X�g�T�[�r�X��ݒ肷��B<p>
     *
     * @param context �R���e�L�X�g�T�[�r�X
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * �T�[�r�X�̊J�n�������s���B<p>
     *
     * @exception Exception �T�[�r�X�̊J�n�����Ɏ��s�����ꍇ
     */
    public void startService() throws Exception{
        if(encoding != null){
            new String().getBytes(encoding);
        }
        if(file == null || file.length() == 0){
            throw new IllegalArgumentException("file must be specified.");
        }
        final File f = new File(file);
        if(f.getParentFile() != null && !f.getParentFile().exists()){
            if(!f.getParentFile().mkdirs()){
                throw new IllegalArgumentException(
                    "file is illegal path." + file
                );
            }
        }
        fileName = new FileName(file, filePrefix, filePostfix);
        if(!isEveryTimeCloseStream){
            if(!fileName.isStatic()){
                throw new IllegalArgumentException(
                    "Dynamic filename!"
                );
            }
            final boolean isExistsFile = new File(fileName.toString(null)).exists();
            fos = new FileOutputStream(fileName.toString(null), isAppend);
            if(header != null && (!isExistsFile || !isAppend)){
                WritableRecord headerRecord = new WritableRecord();
                headerRecord.addElement(new SimpleElement(null, header));
                write(headerRecord);
            }
        }

        if(contextServiceName != null){
            context = (Context)ServiceManagerFactory
                .getServiceObject(contextServiceName);
        }
    }

    /**
     * �T�[�r�X�̒�~�������s���B<p>
     *
     * @exception Exception �T�[�r�X�̒�~�����Ɏ��s�����ꍇ
     */
    public void stopService() throws Exception{
        if(fos != null){
            fos.close();
            fos = null;
        }
    }

    /**
     * �w�肳�ꂽ���R�[�h���t�@�C���ɏo�͂���B<p>
     * {@link #isEveryTimeCloseStream()}��true�̏ꍇ�́A���̃��\�b�h���Ăяo�����x�ɁA�o�̓X�g���[�����J����B�܂��A�t�@�C�����ɓ��I�ȗv�f���܂ޏꍇ�́A�t�@�C���������̓s�x���肷��B<br>
     * {@link #isEveryTimeCloseStream()}��false�̏ꍇ�́A�T�[�r�X�̊J�n���Ɍ��߂�ꂽ�t�@�C�����̏o�̓X�g���[�����J�����܂܁A�o�͂��Ă����B�T�[�r�X�̒�~���ɁA�o�̓X�g���[�������B<br>
     *
     * @param rec �o�͂��郌�R�[�h
     * @exception MessageWriteException �o�͂Ɏ��s�����ꍇ
     */
    public void write(WritableRecord rec) throws MessageWriteException{
        FileOutputStream tmpFos = null;
        try{
            if(isEveryTimeCloseStream){
                String path = fileName.toString(rec);
                File file = new File(path);
                if(header != null && (!file.exists() || file.length() == 0)){
                    WritableRecord headerRecord = new WritableRecord();
                    headerRecord.addElement(new SimpleElement(null, header));
                    write(headerRecord);
                }
                tmpFos = new FileOutputStream(path, isAppend);
            }else{
                if(fos == null){
                    return;
                }
                tmpFos = fos;
            }
            if(getState() == STOPPING || getState() == STOPPED || getState() == DESTROYING || getState() == DESTROYED){
                return;
            }
            if(encoding == null){
                tmpFos.write(rec.toString().getBytes());
                if(isAppend && separator != null){
                    tmpFos.write(separator.getBytes());
                }
            }else{
                tmpFos.write(rec.toString().getBytes(encoding));
                if(isAppend && separator != null){
                    tmpFos.write(separator.getBytes(encoding));
                }
            }
        }catch(IOException e){
            throw new MessageWriteException(e);
        }finally{
            if(isEveryTimeCloseStream && tmpFos != null){
                try{
                    tmpFos.close();
                }catch(IOException e){
                }
            }
        }
    }

    private class FileName implements Serializable, FileNameElement{

        private static final long serialVersionUID = 8733352570482641858L;

        private List fileNameElements = new ArrayList();
        private boolean isStatic = true;

        FileName(String path, String prefix, String postfix) throws IOException{
            final File file = new File(path);
            if(file.getParentFile() != null){
                String dir = file.getParentFile().getAbsolutePath();
                if(dir.charAt(dir.length() - 1) != '/'){
                    dir = dir + '/';
                }
                fileNameElements.add(new StaticFileNameElement(dir));
            }
            parse(prefix);
            fileNameElements.add(new StaticFileNameElement(file.getName()));
            parse(postfix);
        }

        public boolean isStatic(){
            return isStatic;
        }

        private void parse(String val){
            if(val == null || val.length() == 0){
                return;
            }
            boolean isEscape = false;
            boolean isKey = false;
            final StringBuffer buf = new StringBuffer();
            for(int i = 0, max = val.length(); i < max; i++){
                final char c = val.charAt(i);
                switch(c){
                case '%':
                    if(isEscape){
                        buf.append(c);
                    }else{
                        if(isKey){
                            fileNameElements.add(
                                new DynamicFileNameElement(buf.toString())
                            );
                            isStatic = false;
                            buf.setLength(0);
                            isKey = false;
                        }else{
                            if(buf.length() != 0){
                                fileNameElements.add(
                                    new StaticFileNameElement(buf.toString())
                                );
                                buf.setLength(0);
                            }
                            isKey = true;
                        }
                    }
                    isEscape = false;
                    break;
                case '\\':
                    if(isEscape){
                        buf.append(c);
                        isEscape = false;
                    }else{
                        isEscape = true;
                    }
                    break;
                default:
                    buf.append(c);
                    isEscape = false;
                    break;
                }
            }
            if(isKey || isEscape){
                throw new IllegalArgumentException(
                    "Illegal format : " + val
                );
            }
            if(buf.length() != 0){
                fileNameElements.add(
                    new StaticFileNameElement(buf.toString())
                );
                buf.setLength(0);
            }
        }

        public String toString(WritableRecord rec){
            final StringBuffer buf = new StringBuffer();
            final Iterator elements = fileNameElements.iterator();
            while(elements.hasNext()){
                buf.append(((FileNameElement)elements.next()).toString(rec));
            }
            return buf.toString();
        }
    }

    private interface FileNameElement{
        public String toString(WritableRecord rec);
    }

    private static class StaticFileNameElement
     implements FileNameElement, Serializable{

        private static final long serialVersionUID = 6476024043797533112L;

        private String value;
        StaticFileNameElement(String val){
            value = val;
        }
        public String toString(WritableRecord rec){
            return value;
        }
    }

    private class DynamicFileNameElement
     implements FileNameElement, Serializable{

        private static final long serialVersionUID = -3199326390559770733L;

        private String elementKey;
        DynamicFileNameElement(String key){
            elementKey = key;
        }
        public String toString(WritableRecord rec){
            if(rec != null){
                WritableElement element = null;
                if(isOutputKey){
                    element = (WritableElement)rec
                        .getElementMap().get(elementKey);
                }else{
                    element = (WritableElement)rec
                        .getElementMap().remove(elementKey);
                }
                if(element != null){
                    return element.toString();
                }
            }
            if(context != null){
                Object val = context.get(elementKey);
                if(val != null){
                    return val.toString();
                }
            }
            return elementKey;
        }
    }
}
