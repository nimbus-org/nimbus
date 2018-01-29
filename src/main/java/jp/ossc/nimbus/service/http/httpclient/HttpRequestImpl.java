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
package jp.ossc.nimbus.service.http.httpclient;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.params.*;
import org.apache.commons.httpclient.methods.multipart.FilePart;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.http.*;
import jp.ossc.nimbus.util.converter.*;


import org.xerial.snappy.SnappyOutputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;


/**
 * Jakarta HttpClient���g����HTTP���N�G�X�g���ۃN���X�B<p>
 *
 * @author M.Takata
 */
public abstract class HttpRequestImpl implements HttpRequest, Cloneable{
    
    public static final String HTTP_VERSION_0_9 = "0.9";
    public static final String HTTP_VERSION_1_0 = "1.0";
    public static final String HTTP_VERSION_1_1 = "1.1";
    
    /** �w�b�_�[ : Content-Type */
    protected static final String HEADER_CONTENT_TYPE = "Content-Type";
    /** �w�b�_�[ : charset */
    protected static final String HEADER_CHARSET = "charset";
    /** �w�b�_�[ : Content-Encoding */
    protected static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    /** Content-Encoding : deflate */
    protected static final String CONTENT_ENCODING_DEFLATE = "deflate";
    /** Content-Encoding : gzip */
    protected static final String CONTENT_ENCODING_GZIP = "gzip";
    /** Content-Encoding : x-zip */
    protected static final String CONTENT_ENCODING_X_GZIP = "x-gzip";

    protected static final String CONTENT_ENCODING_SNAPPY = "snappy";
    protected static final String CONTENT_ENCODING_LZ4 = "lz4";

    
    protected String actionName;
    protected String url;
    protected String httpVersion;
    protected Map headerMap;
    protected String contentType;
    protected String characterEncoding;
    protected String queryString;
    protected Map parameterMap;
    protected InputStream inputStream;
    protected ByteArrayOutputStream outputStream;
    protected boolean isDoAuthentication;
    protected boolean isFollowRedirects;
    protected Object inputObject;
    protected ServiceName streamConverterServiceName;
    protected StreamConverter streamConverter;
    protected byte[] inputBytes;
    protected int deflateLength = -1;
    protected Map httpMethodParamMap;
    
    // HttpRequest��JavaDoc
    public String getActionName(){
        return actionName;
    }
    
    /**
     * ���N�G�X�g����ӂɎ��ʂ���_���A�N�V��������ݒ肷��B<p>
     *
     * @param name �A�N�V������
     */
    public void setActionName(String name){
        actionName = name;
    }
    
    // HttpRequest��JavaDoc
    public String getURL(){
        return url;
    }
    
    // HttpRequest��JavaDoc
    public void setURL(String url){
        this.url = url;
    }
    
    // HttpRequest��JavaDoc
    public String getHttpVersion(){
        return httpVersion;
    }
    
    // HttpRequest��JavaDoc
    public void setHttpVersion(String version){
        httpVersion = version;
    }
    
    // HttpRequest��JavaDoc
    public Set getHeaderNameSet(){
        return headerMap == null ? new HashSet() : headerMap.keySet();
    }
    
    // HttpRequest��JavaDoc
    public String getHeader(String name){
        final String[] headers = getHeaders(name);
        if(headers == null){
            return null;
        }
        return headers[0];
    }
    
    // HttpRequest��JavaDoc
    public String[] getHeaders(String name){
        if(headerMap == null){
            return null;
        }
        return (String[])headerMap.get(name);
    }
    
    /**
     * HTTP�w�b�_�̃}�b�v���擾����B<p>
     * HTTP�w�b�_���ݒ肳��Ă��Ȃ��ꍇ�́Anull�B<br>
     *
     * @return HTTP�w�b�_�̃}�b�v
     */
    public Map getHeaderMap(){
        return headerMap;
    }
    
    // HttpRequest��JavaDoc
    public void setHeader(String name, String value){
        if(headerMap == null){
            headerMap = new HashMap();
        }
        headerMap.put(name, new String[]{value});
    }
    
    // HttpRequest��JavaDoc
    public void setHeaders(String name, String[] value){
        if(headerMap == null){
            headerMap = new HashMap();
        }
        headerMap.put(name, value);
    }
    
    // HttpRequest��JavaDoc
    public void addHeader(String name, String value){
        if(headerMap == null){
            headerMap = new HashMap();
        }
        String[] vals = (String[])headerMap.get(name);
        if(vals == null){
            vals = new String[]{value};
            headerMap.put(name, vals);
        }else{
            final String[] newVals = new String[vals.length + 1];
            System.arraycopy(vals, 0, newVals, 0, vals.length);
            newVals[newVals.length - 1] = value;
            headerMap.put(name, newVals);
        }
    }
    
    /**
     * �w�肳�ꂽ�w�b�_���폜����B<p>
     *
     * @param name �w�b�_��
     */
    public void removeHeader(String name){
        if(headerMap == null){
            return;
        }
        headerMap.remove(name);
    }
    
    // HttpRequest��JavaDoc
    public String getContentType(){
        return contentType;
    }
    
    // HttpRequest��JavaDoc
    public void setContentType(String type){
        contentType = type;
    }
    
    // HttpRequest��JavaDoc
    public String getCharacterEncoding(){
        return characterEncoding;
    }
    
    // HttpRequest��JavaDoc
    public void setCharacterEncoding(String encoding){
        characterEncoding = encoding;
    }
    
    // HttpRequest��JavaDoc
    public String getQueryString(){
        return queryString;
    }
    
    // HttpRequest��JavaDoc
    public void setQueryString(String query){
        queryString = query;
    }
    
    // HttpRequest��JavaDoc
    public Set getParameterNameSet(){
        return parameterMap == null ? new HashSet() : parameterMap.keySet();
    }
    
    // HttpRequest��JavaDoc
    public String getParameter(String name){
        final String[] params = getParameters(name);
        if(params == null){
            return null;
        }
        return params[0];
    }
    
    // HttpRequest��JavaDoc
    public String[] getParameters(String name){
        if(parameterMap == null){
            return null;
        }
        return (String[])parameterMap.get(name);
    }
    
    /**
     * HTTP���N�G�X�g�p�����[�^�̃}�b�v���擾����B<p>
     * HTTP���N�G�X�g�p�����[�^���ݒ肳��Ă��Ȃ��ꍇ�́Anull�B<br>
     *
     * @return HTTP���N�G�X�g�p�����[�^�̃}�b�v
     */
    public Map getParameterMap(){
        return parameterMap;
    }
    
    // HttpRequest��JavaDoc
    public void setParameter(String name, String value){
        if(parameterMap == null){
            parameterMap = new LinkedHashMap();
        }
        String[] vals = (String[])parameterMap.get(name);
        if(vals == null){
            vals = new String[]{value};
            parameterMap.put(name, vals);
        }else{
            final String[] newVals = new String[vals.length + 1];
            System.arraycopy(vals, 0, newVals, 0, vals.length);
            newVals[newVals.length - 1] = value;
            parameterMap.put(name, newVals);
        }
    }
    
    // HttpRequest��JavaDoc
    public void setParameters(String name, String[] value){
        if(parameterMap == null){
            parameterMap = new LinkedHashMap();
        }
        parameterMap.put(name, value);
    }
    
    // HttpRequest��JavaDoc
    public void setFileParameter(String name, File file) throws java.io.FileNotFoundException{
        setFileParameter(name, file, null, null);
    }
    
    // HttpRequest��JavaDoc
    public void setFileParameter(String name, File file, String fileName, String contentType) throws java.io.FileNotFoundException{
        FilePart part = new FilePart(
            name,
            fileName,
            file,
            contentType == null ? FilePart.DEFAULT_CONTENT_TYPE : contentType,
            characterEncoding == null ? FilePart.DEFAULT_CHARSET : characterEncoding
        );
        if(parameterMap == null){
            parameterMap = new LinkedHashMap();
        }
        parameterMap.put(name, part);
    }
    
    // HttpRequest��JavaDoc
    public void setInputStream(InputStream is){
        inputStream = is;
    }
    
    // HttpRequest��JavaDoc
    public OutputStream getOutputStream(){
        if(outputStream == null){
            outputStream = new ByteArrayOutputStream();
        }
        return outputStream;
    }
    
    // HttpRequest��JavaDoc
    public void setObject(Object input){
        inputObject = input;
    }
    
    // HttpRequest��JavaDoc
    public Object getObject(){
        return inputObject;
    }
    
    /**
     * �F�؏��𑗐M���邩�ǂ�����ݒ肷��B<p>
     *
     * @param isDo �F�؏��𑗐M����ꍇtrue
     */
    public void setDoAuthentication(boolean isDo){
        isDoAuthentication = isDo;
    }
    
    /**
     * �F�؏��𑗐M���邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�F�؏��𑗐M����
     */
    public boolean isDoAuthentication(){
        return isDoAuthentication;
    }
    
    /**
     * 304���X�|���X����M�����ꍇ�ɁA�w�肳�ꂽURL�Ƀ��_�C���N�g���邩�ǂ�����ݒ肷��B<p>
     *
     * @param isRedirects ���_�C���N�g����ꍇtrue
     */
    public void setFollowRedirects(boolean isRedirects){
        isFollowRedirects = isRedirects;
    }
    
    /**
     * 304���X�|���X����M�����ꍇ�ɁA�w�肳�ꂽURL�Ƀ��_�C���N�g���邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A���_�C���N�g����
     */
    public boolean isFollowRedirects(){
        return isFollowRedirects;
    }
    
    /**
     * Jakarta HttpClient��HttpMethodParams�ɐݒ肷��p�����[�^���̏W�����擾����B<p>
     *
     * @return �p�����[�^���̏W��
     */
    public Set getHttpMethodParamNameSet(){
        return httpMethodParamMap == null ? new HashSet() : httpMethodParamMap.keySet();
    }
    
    /**
     * Jakarta HttpClient��HttpMethodParams�ɐݒ肷��p�����[�^��ݒ肷��B<p>
     *
     * @param name �p�����[�^��
     * @param value �l
     */
    public void setHttpMethodParam(String name, Object value){
        if(httpMethodParamMap == null){
            httpMethodParamMap = new HashMap();
        }
        httpMethodParamMap.put(name, value);
    }
    
    /**
     * Jakarta HttpClient��HttpMethodParams�ɐݒ肷��p�����[�^���擾����B<p>
     *
     * @param name �p�����[�^��
     * @return �l
     */
    public Object getHttpMethodParam(String name){
        if(httpMethodParamMap == null){
            return null;
        }
        return httpMethodParamMap.get(name);
    }
    
    /**
     * Jakarta HttpClient��HttpMethodParams�ɐݒ肷��p�����[�^�̃}�b�v���擾����B<p>
     *
     * @return HttpMethodParams�ɐݒ肷��p�����[�^�̃}�b�v
     */
    public Map getHttpMethodParamMap(){
        if(httpMethodParamMap == null){
            httpMethodParamMap = new HashMap();
        }
        return httpMethodParamMap;
    }
    
    /**
     * HTTP���N�G�X�g�ɐݒ肳�ꂽ���̓I�u�W�F�N�g���X�g���[���ɕϊ�����{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
     *
     * @param name StreamConverter�T�[�r�X�̃T�[�r�X��
     */
    public void setStreamConverterServiceName(ServiceName name){
        streamConverterServiceName = name;
    }
    
    /**
     * HTTP���N�G�X�g�ɐݒ肳�ꂽ���̓I�u�W�F�N�g���X�g���[���ɕϊ�����{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}�T�[�r�X�̃T�[�r�X�����擾����B<p>
     *
     * @return StreamConverter�T�[�r�X�̃T�[�r�X��
     */
    public ServiceName getStreamConverterServiceName(){
        return streamConverterServiceName;
    }
    
    /**
     * HTTP���N�G�X�g�ɐݒ肳�ꂽ���̓I�u�W�F�N�g���X�g���[���ɕϊ�����{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}��ݒ肷��B<p>
     *
     * @param converter StreamConverter
     */
    public void setStreamConverter(StreamConverter converter){
        streamConverter = converter;
    }
    
    /**
     * HTTP���N�G�X�g�ɐݒ肳�ꂽ���̓I�u�W�F�N�g���X�g���[���ɕϊ�����{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}���擾����B<p>
     *
     * @return StreamConverter
     */
    public StreamConverter getStreamConverter(){
        return streamConverter;
    }
    
    /**
     * ���̓X�g���[�������k����ꍇ��臒l[byte]��ݒ肷��B<p>
     * �ݒ肵�Ȃ��ꍇ�́A���̓X�g���[���̃T�C�Y�Ɋւ�炸���k����B<br>
     *
     * @param length 臒l[byte]
     */
    public void setDeflateLength(int length){
        deflateLength = length;
    }
    
    /**
     * ���̓X�g���[�������k����ꍇ��臒l[byte]���擾����B<p>
     *
     * @return 臒l[byte]
     */
    public int getDeflateLength(){
        return deflateLength;
    }
    
    /**
     * ���̓I�u�W�F�N�g���X�g���[���ɕϊ������ۂ̃o�C�g�z����擾����B<p>
     *
     * @return ���̓I�u�W�F�N�g���X�g���[���ɕϊ������ۂ̃o�C�g�z��
     */
    public byte[] getInputBytes(){
        return inputBytes;
    }
    
    /**
     * ���HTTP���\�b�h�𐶐�����B<p>
     *
     * @return HTTP���\�b�h
     * @exception Exception HTTP���\�b�h�̐����Ɏ��s�����ꍇ
     */
    protected abstract HttpMethodBase instanciateHttpMethod() throws Exception;
    
    /**
     * HTTP���\�b�h������������B<p>
     *
     * @param method HTTP���\�b�h
     * @exception Exception HTTP���\�b�h�̏������Ɏ��s�����ꍇ
     */
    protected void initHttpMethod(HttpMethodBase method) throws Exception{
        if(url != null){
            method.setURI(new URI(url, true));
        }
        final HttpMethodParams params = method.getParams();
        if(httpMethodParamMap != null){
            final Iterator names = httpMethodParamMap.keySet().iterator();
            while(names.hasNext()){
                final String name = (String)names.next();
                final Object value = httpMethodParamMap.get(name);
                params.setParameter(name, value);
            }
        }
        if(httpVersion != null){
            if(HTTP_VERSION_0_9.equals(httpVersion)){
                params.setVersion(HttpVersion.HTTP_0_9);
            }else if(HTTP_VERSION_1_0.equals(httpVersion)){
                params.setVersion(HttpVersion.HTTP_1_0);
            }else if(HTTP_VERSION_1_1.equals(httpVersion)){
                params.setVersion(HttpVersion.HTTP_1_1);
            }
        }
        if(contentType != null){
            final StringBuilder buf = new StringBuilder(contentType);
            if(characterEncoding != null){
                buf.append(';')
                   .append(HEADER_CHARSET)
                   .append('=')
                   .append(characterEncoding);
            }
            method.addRequestHeader(
                HEADER_CONTENT_TYPE,
                buf.toString()
            );
        }
        if(queryString != null){
            method.setQueryString(queryString);
        }
        if(parameterMap != null){
            initParameter(method, parameterMap);
        }
        if(inputStream != null){
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final byte[] bytes = new byte[1024];
            int length = 0;
            while((length = inputStream.read(bytes)) != -1){
                baos.write(bytes, 0, length);
            }
            inputBytes = baos.toByteArray();
        }else if(outputStream != null && outputStream.size() != 0){
            inputBytes = outputStream.toByteArray();
        }else if(inputObject != null){
            if(streamConverter == null && streamConverterServiceName == null){
                throw new HttpRequestCreateException(
                    "StreamConverter is null."
                );
            }else{
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final byte[] bytes = new byte[1024];
                int length = 0;
                StreamConverter converter = streamConverter;
                if(streamConverterServiceName != null){
                    converter = (StreamConverter)ServiceManagerFactory
                        .getServiceObject(streamConverterServiceName);
                }
                if(characterEncoding != null
                    && converter instanceof StreamStringConverter){
                    converter = ((StreamStringConverter)converter)
                        .cloneCharacterEncodingToStream(characterEncoding);
                }
                InputStream is = converter.convertToStream(inputObject);
                while((length = is.read(bytes)) != -1){
                    baos.write(bytes, 0, length);
                }
                inputBytes = baos.toByteArray();
            }
        }
        if(inputBytes == null){
            removeHeader(HEADER_CONTENT_ENCODING);
        }else{
            initInputStream(
                method,
                compress(inputBytes)
            );
        }
        if(headerMap != null){
            final Iterator names = headerMap.keySet().iterator();
            while(names.hasNext()){
                final String name = (String)names.next();
                final String[] vals = (String[])headerMap.get(name);
                for(int i = 0; i < vals.length; i++){
                    if(HEADER_CONTENT_TYPE.equals(name)
                        && method.getRequestHeader(name) != null){
                        continue;
                    }
                    method.addRequestHeader(name, vals[i]);
                }
            }
        }
        if(isDoAuthentication != method.getDoAuthentication()){
            method.setDoAuthentication(isDoAuthentication);
        }
        if(isFollowRedirects != method.getFollowRedirects()){
            method.setFollowRedirects(isFollowRedirects);
        }
    }
    
    /**
     * ���̓X�g���[�������k����B<p>
     * (Content-Encoding�Ɏw�肳�ꂽ���ň��k)
     * 
     * @param inputBytes ���̓o�C�g�z��
     * @return ���k���ꂽ���̓X�g���[��
     * @throws IOException �T�|�[�g���Ă��Ȃ����k�`��(deflate, gzip�ȊO)���w�肳�ꂽ�ꍇ
     */
    protected InputStream compress(byte[] inputBytes) throws IOException {
        // �w�b�_�[[Content-Encoding]�̒l���擾
        String encode = getHeader(HEADER_CONTENT_ENCODING);
        if(encode == null){
            return new ByteArrayInputStream(inputBytes);
        }
        if((encode.indexOf(CONTENT_ENCODING_DEFLATE) == -1
                && encode.indexOf(CONTENT_ENCODING_GZIP) == -1

                && encode.indexOf(CONTENT_ENCODING_SNAPPY) == -1
                && encode.indexOf(CONTENT_ENCODING_LZ4) == -1

                && encode.indexOf(CONTENT_ENCODING_X_GZIP) == -1)
             || (deflateLength != -1 && inputBytes.length < deflateLength)){
            removeHeader(HEADER_CONTENT_ENCODING);
            return new ByteArrayInputStream(inputBytes);
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream os = baos;
        if(encode.indexOf(CONTENT_ENCODING_DEFLATE) != -1){
            // deflate���k
            os = new DeflaterOutputStream(os);
        }else if(encode.indexOf(CONTENT_ENCODING_GZIP) != -1
                    || encode.indexOf(CONTENT_ENCODING_X_GZIP) != -1){
            // gzip���k
            os = new GZIPOutputStream(os);

        }else if(encode.indexOf(CONTENT_ENCODING_SNAPPY) != -1){
            os = new SnappyOutputStream(os);
        }else if(encode.indexOf(CONTENT_ENCODING_LZ4) != -1){
            os = new LZ4BlockOutputStream(os);

        }else{
            throw new IOException("Can not compress. [" + encode + "]");
        }
        os.write(inputBytes, 0, inputBytes.length);
        os.flush();
        if(os instanceof DeflaterOutputStream){
            ((DeflaterOutputStream)os).finish();
        }
        os.close();
        return new ByteArrayInputStream(baos.toByteArray());
    }
    
    /**
     * HTTP���\�b�h�̃��N�G�X�g�p�����[�^������������B<p>
     *
     * @param method HTTP���\�b�h
     * @param params ���N�G�X�g�p�����[�^
     * @exception Exception HTTP���\�b�h�̃��N�G�X�g�p�����[�^�̏������Ɏ��s�����ꍇ
     */
    protected abstract void initParameter(
        HttpMethodBase method,
        Map params
    ) throws Exception;
    
    /**
     * HTTP���\�b�h�̃��N�G�X�g�{�f�B������������B<p>
     *
     * @param method HTTP���\�b�h
     * @param is ���̓X�g���[��
     * @exception Exception HTTP���\�b�h�̃��N�G�X�g�{�f�B�̏������Ɏ��s�����ꍇ
     */
    protected abstract void initInputStream(
        HttpMethodBase method,
        InputStream is
    ) throws Exception;
    
    /**
     * HTTP���\�b�h�𐶐�����B<p>
     *
     * @return HTTP���\�b�h
     * @exception HttpRequestCreateException HTTP���\�b�h�̐����Ɏ��s�����ꍇ
     */
    public HttpMethodBase createHttpMethod() throws HttpRequestCreateException{
        HttpMethodBase httpMethod = null;
        try{
            httpMethod = instanciateHttpMethod();
            initHttpMethod(httpMethod);
        }catch(HttpRequestCreateException e){
            throw e;
        }catch(Exception e){
            throw new HttpRequestCreateException(e);
        }
        return httpMethod;
    }
    
    /**
     * �����𐶐�����B<p>
     *
     * @return ����
     * @exception CloneNotSupportedException �����Ɏ��s�����ꍇ
     */
    public Object clone() throws CloneNotSupportedException{
        final HttpRequestImpl clone = (HttpRequestImpl)super.clone();
        if(clone.headerMap != null){
            clone.headerMap = new HashMap(headerMap);
        }
        if(clone.parameterMap != null){
            clone.parameterMap = new LinkedHashMap(parameterMap);
        }
        return clone;
    }
}
