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
 * 動的にファイル名を決めてファイルに出力する{@link MessageWriter}サービス。<p>
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

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public void setEncoding(String encoding){
        this.encoding = encoding;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public String getEncoding(){
        return encoding;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public void setFile(String file){
        this.file = file;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public String getFile(){
        return file;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public void setAppend(boolean isAppend){
        this.isAppend = isAppend;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public boolean isAppend(){
        return isAppend;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public void setEveryTimeCloseStream(boolean isClose){
        isEveryTimeCloseStream = isClose;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public boolean isEveryTimeCloseStream(){
        return isEveryTimeCloseStream;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public void setFilePrefix(String prefix){
        filePrefix = prefix;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public String getFilePrefix(){
        return filePrefix;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public void setFilePostfix(String postfix){
        filePostfix = postfix;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public String getFilePostfix(){
        return filePostfix;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public void setContextServiceName(ServiceName name){
        contextServiceName = name;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public ServiceName getContextServiceName(){
        return contextServiceName;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public void setOutputKey(boolean isOutput){
        isOutputKey = isOutput;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public boolean isOutputKey(){
        return isOutputKey;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public void setSeparator(String separator){
        this.separator = separator;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public String getSeparator(){
        return separator;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public void setHeader(String header){
        this.header = header;
    }

    // OneWriteFileMessageWriterServiceMBeanのJavaDoc
    public String getHeader(){
        return header;
    }

    /**
     * コンテキストサービスを設定する。<p>
     *
     * @param context コンテキストサービス
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
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
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        if(fos != null){
            fos.close();
            fos = null;
        }
    }

    /**
     * 指定されたレコードをファイルに出力する。<p>
     * {@link #isEveryTimeCloseStream()}がtrueの場合は、このメソッドが呼び出される度に、出力ストリームを開閉する。また、ファイル名に動的な要素を含む場合は、ファイル名もその都度決定する。<br>
     * {@link #isEveryTimeCloseStream()}がfalseの場合は、サービスの開始時に決められたファイル名の出力ストリームを開いたまま、出力していく。サービスの停止時に、出力ストリームを閉じる。<br>
     *
     * @param rec 出力するレコード
     * @exception MessageWriteException 出力に失敗した場合
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
            final StringBuilder buf = new StringBuilder();
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
            final StringBuilder buf = new StringBuilder();
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
