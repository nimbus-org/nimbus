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
package jp.ossc.nimbus.service.http.proxy;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.interpreter.*;

/**
 * �e�X�g�p��HTTP�v���L�V�̃��N�G�X�g�����T�[�r�X�B<p>
 * HTTP���N�G�X�g�̓��e���t�@�C���ɏo�͂���B<br>
 * HTTP���X�|���X�̓��e�́AHTTP���N�G�X�g�̏����ɉ����āA�t�@�C������ǂݍ���ŉ�������B�܂��́A{@link #addAction(TestHttpProcessService.Action)}�Őݒ肳�ꂽ���ɁA�t�@�C������ǂݍ���ŉ�������B<br>
 * 
 * @author M.Takata
 */
public class TestHttpProcessService extends HttpProcessServiceBase
 implements TestHttpProcessServiceMBean{
    
    private static final long serialVersionUID = 8149760369064471308L;
    private static final String HTTP_METHOD_CONNECT = "CONNECT";
    
    private String requestOutputFileEncoding;
    private String responseInputFileEncoding;
    private List conditionActions = new ArrayList();
    private List actions = new ArrayList();
    
    // TestHttpProcessServiceMBean��JavaDoc
    public String getRequestOutputFileEncoding(){
        return requestOutputFileEncoding;
    }
    // TestHttpProcessServiceMBean��JavaDoc
    public void setRequestOutputFileEncoding(String encoding){
        requestOutputFileEncoding = encoding;
    }
    
    // TestHttpProcessServiceMBean��JavaDoc
    public String getResponseInputFileEncoding(){
        return responseInputFileEncoding;
    }
    // TestHttpProcessServiceMBean��JavaDoc
    public void setResponseInputFileEncoding(String encoding){
        responseInputFileEncoding = encoding;
    }
    
    /**
     * {@link HttpRequest}�ɑ΂���������ɃA�N�V������ݒ肷��B<p>
     *
     * @param condition ����
     * @param action �A�N�V����
     * @exception Exception �������̉�͂Ɏ��s�����ꍇ
     */
    public void setAction(String condition, Action action) throws Exception{
        final Condition cond = new Condition(condition, action);
        if(conditionActions.contains(cond)){
            conditionActions.remove(cond);
        }
        conditionActions.add(cond);
    }
    
    /**
     * ������������A�N�V������o�^����B<p>
     * �o�^���ꂽ�A�N�V�����́A�Ăяo����邽�тɏ����邽�߁A�Ăяo�����ɓo�^����K�v������B<br>
     * {@link #setAction(String, TestHttpProcessService.Action)}�����D�悳���B<br>
     *
     * @param action �A�N�V����
     */
    public void addAction(Action action){
        if(actions.contains(action)){
            actions.remove(action);
        }
        actions.add(action);
    }
    
    /**
     * ������������A�N�V�������N���A����B<p>
     */
    public void clearAction(){
        actions.clear();
    }
    
    /**
     * HTTP���N�G�X�g�̃v���L�V�������s���B<p>
     * HTTP���N�G�X�g�̓��e���t�@�C���ɏo�͂���B<br>
     * HTTP���X�|���X�̓��e�́A{@link #setAction(String, TestHttpProcessService.Action)}�Őݒ肳�ꂽHTTP���N�G�X�g�̏����ɉ����āA�t�@�C������ǂݍ���ŉ�������B�܂��́A{@link #addAction(TestHttpProcessService.Action)}�Őݒ肳�ꂽ���ɁA�t�@�C������ǂݍ���ŉ�������B<br>
     *
     * @param request HTTP���N�G�X�g
     * @param response HTTP���X�|���X
     * @exception Exception HTTP���N�G�X�g�̏����Ɏ��s�����ꍇ
     */
    public void doProcess(
        HttpRequest request,
        HttpResponse response
    ) throws Exception{
        
        if(request.getHeader().getMethod().equals(HTTP_METHOD_CONNECT)){
            response.setHeader("Connection", "Keep-Alive");
            return;
        }
        
        if(request.body != null){
            request.body.read();
        }
        
        Action targetAction = null;
        if(actions.size() != 0){
            targetAction = (Action)actions.remove(0);
        }else{
            
            for(int i = 0; i < conditionActions.size(); i++){
                Condition cond = (Condition)conditionActions.get(i);
                if(cond.matchRequest(request)){
                    targetAction = cond.action;
                    break;
                }
            }
        }
        if(targetAction == null){
            response.setStatusCode(404);
            response.setStatusMessage("No action.");
            return;
        }
        
        targetAction.processAction(
            request,
            requestOutputFileEncoding,
            response,
            responseInputFileEncoding
        );
    }
    
    /**
     * HTTP���N�G�X�g�̏����ݒ���s���N���X�B<p>
     * HTTP���N�G�X�g�̏o�̓t�@�C���AHTTP���X�|���X�w�b�_�̐ݒ�AHTTP���X�|���X�{�f�B�̓��̓t�@�C���̐ݒ肪�\�ł���B<br>
     *
     * @author M.Takata
     */
    public static class Action implements java.io.Serializable{
        
        private static final long serialVersionUID = 4155428986485777449L;
        
        /**
         * {@link Interpreter}�ŁA�����̃{�f�B��ҏW����ۂ̃X�N���v�g���ŎQ�Ɖ\�ȁA{@link HttpRequest}�̕ϐ����B<p>
         */
        public static final String INTERPRET_VAR_NAME_REQUEST = "request";
        /**
         * {@link Interpreter}�ŁA�����̃{�f�B��ҏW����ۂ̃X�N���v�g���ŎQ�Ɖ\�ȁA�{�f�B��InputStream�̕ϐ����B<p>
         */
        public static final String INTERPRET_VAR_NAME_RESPONSE_INPUT_STREAM = "inputStream";
        /**
         * {@link Interpreter}�ŁA�����̃{�f�B��ҏW����ۂ̃X�N���v�g���ŎQ�Ɖ\�ȁA�{�f�B��OutputStream�̕ϐ����B<p>
         */
        public static final String INTERPRET_VAR_NAME_RESPONSE_OUTPUT_STREAM = "outputStream";
        /**
         * {@link Interpreter}�ŁA�����̃{�f�B��ҏW����ۂ̃X�N���v�g���ŎQ�Ɖ\�ȁA�{�f�B�̕�����̕ϐ����B<p>
         */
        public static final String INTERPRET_VAR_NAME_RESPONSE_STRING = "response";
        
        protected String requestOutputFile;
        protected String requestHeaderOutputFile;
        protected String requestBodyOutputFile;
        protected String responseVersion;
        protected int responseStatusCode = -1;
        protected String responseStatusMessage;
        protected Map responseHeaderMap = new LinkedHashMap();
        protected String responseBodyInputFile;
        protected boolean isBinaryResponse;
        protected long processTime = 0;
        protected ServiceName interpreterServiceName;
        protected Interpreter interpreter;
        protected String responseBodyEditScript;
        
        /**
         * HTTP���N�G�X�g�̏o�̓t�@�C�����擾����B<p>
         *
         * @return HTTP���N�G�X�g�̏o�̓t�@�C��
         */
        public String getRequestOutputFile(){
            return requestOutputFile;
        }
        
        /**
         * HTTP���N�G�X�g�̏o�̓t�@�C����ݒ肷��B<p>
         * ���̐ݒ���s�����ꍇ�AHTTP�w�b�_�y��HTTP�{�f�B�̗������A���̃t�@�C���ɏo�͂����B<br>
         *
         * @param file HTTP���N�G�X�g�̏o�̓t�@�C��
         */
        public void setRequestOutputFile(String file){
            requestOutputFile = file;
        }
        
        /**
         * HTTP���N�G�X�g�w�b�_�̏o�̓t�@�C�����擾����B<p>
         *
         * @return HTTP���N�G�X�g�w�b�_�̏o�̓t�@�C��
         */
        public String getRequestHeaderOutputFile(){
            return requestHeaderOutputFile;
        }
        
        /**
         * HTTP���N�G�X�g�w�b�_�̏o�̓t�@�C����ݒ肷��B<p>
         * ���̐ݒ���s�����ꍇ�AHTTP�w�b�_�݂̂��A���̃t�@�C���ɏo�͂����B<br>
         *
         * @param file HTTP���N�G�X�g�w�b�_�̏o�̓t�@�C��
         */
        public void setRequestHeaderOutputFile(String file){
            requestHeaderOutputFile = file;
        }
        
        /**
         * HTTP���N�G�X�g�{�f�B�̏o�̓t�@�C�����擾����B<p>
         *
         * @return HTTP���N�G�X�g�{�f�B�̏o�̓t�@�C��
         */
        public String getRequestBodyOutputFile(){
            return requestBodyOutputFile;
        }
        
        /**
         * HTTP���N�G�X�g�{�f�B�̏o�̓t�@�C����ݒ肷��B<p>
         * ���̐ݒ���s�����ꍇ�AHTTP�{�f�B�݂̂��A���̃t�@�C���ɏo�͂����B<br>
         *
         * @param file HTTP���N�G�X�g�{�f�B�̏o�̓t�@�C��
         */
        public void setRequestBodyOutputFile(String file){
            requestBodyOutputFile = file;
        }
        
        /**
         * HTTP���X�|���X��HTTP�o�[�W������ݒ肷��B<p>
         * �ݒ肵�Ȃ��ꍇ�́AHTTP���N�G�X�g��HTTP�o�[�W�����Ɠ����l�ɂȂ�B<br>
         *
         * @param version HTTP�o�[�W����
         */
        public void setResponseVersion(String version){
            responseVersion = version;
        }
        
        /**
         * HTTP���X�|���X�̃X�e�[�^�X�R�[�h��ݒ肷��B<p>
         * �ݒ肵�Ȃ��ꍇ�́A{@link HttpResponse#getStatusCode()}�ɂȂ�B<br>
         *
         * @param code HTTP���X�|���X�̃X�e�[�^�X�R�[�h
         */
        public void setResponseStatusCode(int code){
           responseStatusCode = code;
        }
        
        /**
         * HTTP���X�|���X�̃X�e�[�^�X���b�Z�[�W��ݒ肷��B<p>
         * �ݒ肵�Ȃ��ꍇ�́A{@link HttpResponse#getStatusMessage()}�ɂȂ�B<br>
         *
         * @param message HTTP���X�|���X�̃X�e�[�^�X���b�Z�[�W
         */
        public void setResponseStatusMessage(String message){
            responseStatusMessage = message;
        }
        
        /**
         * ��������[ms]��ݒ肷��B<p>
         */
        public void setProcessTime(long time){
            processTime = time;
        }
        
        /**
         * �w�b�_��ݒ肷��B<p>
         *
         * @param name �w�b�_��
         * @param val �w�b�_�l
         */
        public void setHeader(String name, String val){
            String[] vals = (String[])responseHeaderMap.get(name);
            if(vals == null){
                vals = new String[1];
                vals[0] = val;
                responseHeaderMap.put(name, vals);
            }else{
                final String[] newVals = new String[vals.length + 1];
                System.arraycopy(vals, 0, newVals, 0, vals.length);
                newVals[newVals.length - 1] = val;
                responseHeaderMap.put(name, newVals);
            }
        }
        
        /**
         * �w�b�_��ݒ肷��B<p>
         *
         * @param name �w�b�_��
         * @param vals �w�b�_�l�z��
         */
        public void setHeaders(String name, String[] vals){
            responseHeaderMap.put(name, vals);
        }
        
        /**
         * �w�b�_���̏W�����擾����B<p>
         *
         * @return �w�b�_���̏W��
         */
        protected Set getHeaderNameSet(){
            return responseHeaderMap.keySet();
        }
        
        /**
         * �w�b�_���擾����B<p>
         *
         * @param name �w�b�_��
         * @return �w�b�_�l
         */
        protected String getHeader(String name){
            final String[] vals = (String[])responseHeaderMap.get(name);
            return vals == null ? null : vals[0];
        }
        
        /**
         * �w�b�_���擾����B<p>
         *
         * @param name �w�b�_��
         * @return �w�b�_�l�z��
         */
        protected String[] getHeaders(String name){
            return (String[])responseHeaderMap.get(name);
        }
        
        /**
         * HTTP���X�|���X�{�f�B�̓��̓t�@�C�����擾����B<p>
         *
         * @return HTTP���X�|���X�{�f�B�̓��̓t�@�C��
         */
        public String getResponseBodyInputFile(){
            return responseBodyInputFile;
        }
        
        /**
         * HTTP���X�|���X�{�f�B�̓��̓t�@�C����ݒ肷��B<p>
         *
         * @param file HTTP���X�|���X�{�f�B�̓��̓t�@�C��
         */
        public void setResponseBodyInputFile(String file){
            responseBodyInputFile = file;
        }
        
        /**
         * HTTP���X�|���X�{�f�B�̓��̓t�@�C�����o�C�i�����ǂ�����ݒ肷��B<p>
         *
         * @param isBinary �o�C�i���̏ꍇtrue
         */
        public void setBinaryResponse(boolean isBinary){
            isBinaryResponse = isBinary;
        }
        
        /**
         * HTTP���X�|���X�{�f�B�̓��̓t�@�C�����o�C�i�����ǂ����𔻒肷��B<p>
         *
         * @return true�̏ꍇ�A�o�C�i��
         */
        public boolean isBinaryResponse(){
            return isBinaryResponse;
        }
        
        /**
         * {@link Interpreter}�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
         *
         * @param name {@link Interpreter}�T�[�r�X�̃T�[�r�X��
         */
        public void setInterpreterServiceName(ServiceName name){
            interpreterServiceName = name;
        }
        
        /**
         * {@link Interpreter}�T�[�r�X��ݒ肷��B<p>
         *
         * @param interpreter {@link Interpreter}�T�[�r�X
         */
        public void setInterpreter(Interpreter interpreter){
            this.interpreter = interpreter;
        }
        
        protected Interpreter getInterpreter(){
            if(interpreterServiceName != null){
                return (Interpreter)ServiceManagerFactory.getServiceObject(interpreterServiceName);
            }
            return interpreter;
        }
        
        /**
         * {@link Interpreter}���g���āA�����̃{�f�B��ҏW���邽�߂̃X�N���v�g��ݒ肷��B<p>
         * {@link #setBinaryResponse(boolean) setBinaryResponse(true)}�ɐݒ肵���ꍇ�́A�X�N���v�g���ŁA�ϐ�{@link #INTERPRET_VAR_NAME_REQUEST}�A{@link #INTERPRET_VAR_NAME_RESPONSE_INPUT_STREAM}�A{@link #INTERPRET_VAR_NAME_RESPONSE_OUTPUT_STREAM}���Q�Ɖ\�ŁA�{�f�B�̕ҏW���ʂ�{@link #INTERPRET_VAR_NAME_RESPONSE_OUTPUT_STREAM}�ɏ������ށB<br>
         * {@link #setBinaryResponse(boolean) setBinaryResponse(false)}�ɐݒ肵���ꍇ�́A�X�N���v�g���ŁA�ϐ�{@link #INTERPRET_VAR_NAME_REQUEST}�A{@link #INTERPRET_VAR_NAME_RESPONSE_STRING}���Q�Ɖ\�ŁA�{�f�B�̕ҏW���ʂ�߂�l�ŕԂ��B<br>
         *
         * @param script �����̃{�f�B��ҏW���邽�߂̃X�N���v�g
         */
        public void setResponseBodyEditScript(String script){
            responseBodyEditScript = script;
        }
        
        private void mkdirs(String path) throws IOException{
            File file = new File(path);
            if(!file.exists()){
                File parent = file.getParentFile();
                if(parent != null && !parent.exists()){
                    parent.mkdirs();
                }
            }
        }
        
        public void processAction(
            HttpRequest request,
            String requestOutputFileEncoding,
            HttpResponse response,
            String responseInputFileEncoding
        ) throws Exception{
            if(processTime > 0){
                Thread.sleep(processTime);
            }
            writeRequest(request, requestOutputFileEncoding);
            writeResponse(request, response, responseInputFileEncoding);
        }
        
        /**
         * HTTP���N�G�X�g���t�@�C���ɏo�͂���B<p>
         *
         * @param request HTTP���N�G�X�g
         * @param requestOutputFileEncoding �o�̓t�@�C�������G���R�[�f�B���O
         * @exception IOException �t�@�C���̏o�͂Ɏ��s�����ꍇ
         */
        protected void writeRequest(
            HttpRequest request,
            String requestOutputFileEncoding
        ) throws IOException{
            if(requestOutputFile != null){
                mkdirs(requestOutputFile);
                final FileOutputStream fos
                     = new FileOutputStream(requestOutputFile);
                fos.write(
                    requestOutputFileEncoding == null
                         ? request.header.header.getBytes()
                            : request.header.header.getBytes(requestOutputFileEncoding)
                );
                if(request.body != null){
                    fos.write(request.body.body);
                }
                fos.close();
            }else{
                if(requestHeaderOutputFile != null){
                    mkdirs(requestHeaderOutputFile);
                    final FileOutputStream fos
                         = new FileOutputStream(requestHeaderOutputFile);
                    fos.write(
                        requestOutputFileEncoding == null
                             ? request.header.header.getBytes()
                                : request.header.header.getBytes(requestOutputFileEncoding)
                    );
                    fos.close();
                }
                if(request.body != null && requestBodyOutputFile != null){
                    mkdirs(requestBodyOutputFile);
                    final FileOutputStream fos
                         = new FileOutputStream(requestBodyOutputFile);
                    fos.write(request.body.body);
                    fos.close();
                }
            }
        }
        
        /**
         * HTTP���X�|���X���t�@�C������ǂݍ���ŁA���X�|���X�X�g���[���ɏo�͂���B<p>
         *
         * @param request HTTP���N�G�X�g
         * @param response HTTP���X�|���X
         * @param responseInputFileEncoding ���̓t�@�C�������G���R�[�f�B���O
         * @exception IOException �t�@�C���̓��͂Ɏ��s�����ꍇ
         * @exception EvaluateException �����ҏW�X�N���v�g�̕]���Ɏ��s�����ꍇ
         */
        protected void writeResponse(HttpRequest request, HttpResponse response, String responseInputFileEncoding) throws IOException, EvaluateException{
            if(responseVersion != null){
                response.setVersion(responseVersion);
            }
            if(responseStatusCode != -1){
                response.setStatusCode(responseStatusCode);
            }
            if(responseStatusMessage != null){
                response.setStatusMessage(responseStatusMessage);
            }
            final Iterator headerNames = getHeaderNameSet().iterator();
            while(headerNames.hasNext()){
                final String headerName = (String)headerNames.next();
                response.setHeaders(headerName, getHeaders(headerName));
            }
            if(responseBodyInputFile != null){
                final OutputStream os = response.getOutputStream();
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final FileInputStream fis
                     = new FileInputStream(responseBodyInputFile);
                try{
                    int length = 0;
                    byte[] buf = new byte[1024];
                    while((length = fis.read(buf)) != -1){
                        baos.write(buf, 0, length);
                    }
                    if(isBinaryResponse){
                        buf = baos.toByteArray();
                        Interpreter interpreter = getInterpreter();
                        if(interpreter != null && responseBodyEditScript != null){
                            Map variables = new HashMap();
                            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
                            variables.put(INTERPRET_VAR_NAME_RESPONSE_INPUT_STREAM, bais);
                            baos.reset();
                            variables.put(INTERPRET_VAR_NAME_RESPONSE_OUTPUT_STREAM, baos);
                            variables.put(INTERPRET_VAR_NAME_REQUEST, request);
                            interpreter.evaluate(responseBodyEditScript, variables);
                            buf = baos.toByteArray();
                        }
                    }else{
                        String responseStr = null;
                        if(responseInputFileEncoding == null){
                            responseStr = new String(baos.toByteArray());
                        }else{
                            responseStr = new String(
                                baos.toByteArray(),
                                responseInputFileEncoding
                            );
                        }
                        Interpreter interpreter = getInterpreter();
                        if(interpreter != null && responseBodyEditScript != null){
                            Map variables = new HashMap();
                            variables.put(INTERPRET_VAR_NAME_RESPONSE_STRING, responseStr);
                            variables.put(INTERPRET_VAR_NAME_REQUEST, request);
                            responseStr = interpreter.evaluate(responseBodyEditScript, variables).toString();
                        }
                        buf = responseStr.getBytes(response.getCharacterEncoding());
                    }
                    os.write(buf);
                }finally{
                    fis.close();
                }
            }
        }
    }
    
    /**
     * �����B<p>
     *
     * @author M.Takata
     */
    private static class Condition implements java.io.Serializable{
        
        private static final long serialVersionUID = -5495011425410843307L;
        
        private static final String DELIMITER = "@";
        
        private transient List properties;
        private transient Expression expression;
        private transient List keyList;
        private String condition;
        
        /**
         * ���̏����ɍ��v�����ꍇ�̃A�N�V�����B<p>
         */
        Action action;
        
        /**
         * �C���X�^���X�𐶐�����B<p>
         *
         * @param cond ������
         * @param action ���̏����ɍ��v�����ꍇ�̃A�N�V����
         * @exception Exception �������̉�͂Ɏ��s�����ꍇ
         */
        public Condition(String cond, Action action) throws Exception{
            initCondition(cond);
            this.action = action;
        }
        
        /**
         * ��������͂���B<p>
         *
         * @param cond ������
         * @exception Exception �������̉�͂Ɏ��s�����ꍇ
         */
        public void initCondition(String cond) throws Exception{
            keyList = new ArrayList();
            properties = new ArrayList();
            
            StringTokenizer token = new StringTokenizer(cond, DELIMITER, true);
            
            boolean keyFlg = false;
            
            String beforeToken = null;
            StringBuilder condBuf = new StringBuilder();
            
            while(token.hasMoreTokens()){
                String str = token.nextToken();
                if(!keyFlg){
                    if(DELIMITER.equals(str)){
                        keyFlg = true;
                    }else{
                        condBuf.append(str);
                    }
                }else if(DELIMITER.equals(str)){
                    keyFlg = false;
                    if(beforeToken != null){
                        final String tmpKey = "_conditionKey$" + keyList.size();
                         keyList.add(tmpKey);
                        condBuf.append(tmpKey);
                        Property prop = PropertyFactory.createProperty(beforeToken);
                        prop.setIgnoreNullProperty(true);
                        properties.add(prop);
                    }else{
                        condBuf.append(str);
                    }
                }
                beforeToken = str;    
            }
            
            expression = ExpressionFactory.createExpression(condBuf.toString());
            matchRequest(new HttpRequest(), true);
            condition = cond;
        }
        
        /**
         * �w�肳�ꂽHTTP���N�G�X�g�����̏����ɍ��v���邩���肷��B<p>
         *
         * @param request HTTP���N�G�X�g
         * @return �����ɍ��v����ꍇtrue
         */
        protected boolean matchRequest(HttpRequest request){
            return matchRequest(request, false);
        }
        
        /**
         * �w�肳�ꂽHTTP���N�G�X�g�����̏����ɍ��v���邩���肷��B<p>
         *
         * @param request HTTP���N�G�X�g
         * @param isTest ���������̃e�X�g���s���ǂ����Btrue�̏ꍇ�A�e�X�g���s�ł���A�n�����HTTP���N�G�X�g����ł��邽�߁A�]�����ʂ�boolean�ɂȂ�Ȃ��Ă���O��throw���Ȃ�
         * @return �����ɍ��v����ꍇtrue
         * @exception IllegalArgumentException �]�����ʂ�boolean�łȂ��ꍇ�B�A���A�e�X�g���s�̏ꍇ�́Athrow����Ȃ��B
         * @exception RuntimeException �������̕]�����ɗ�O�����������ꍇ
         */
        protected boolean matchRequest(HttpRequest request, boolean isTest){
            JexlContext jexlContext = JexlHelper.createContext();
            for(int i = 0, size = keyList.size(); i < size; i++){
                final String keyString = (String)keyList.get(i);
                final Property property = (Property)properties.get(i);
                Object val = null;
                try{
                    val = property.getProperty(request);
                }catch(NoSuchPropertyException e){
                }catch(InvocationTargetException e){
                }
                jexlContext.getVars().put(keyString, val);
            }
            
            try{
                Object exp = expression.evaluate(jexlContext);
                if(exp instanceof Boolean){
                    return ((Boolean)exp).booleanValue();
                }else{
                    if(exp == null && isTest){
                        return true;
                    }
                    throw new IllegalArgumentException(
                        "Result of condition is not boolean : " + condition
                    );
                }
            }catch(IllegalArgumentException e){
                throw e;
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }
        
        private void readObject(ObjectInputStream in)
         throws IOException, ClassNotFoundException{
            in.defaultReadObject();
            try{
                initCondition(condition);
            }catch(Exception e){
                // �N����Ȃ��͂�
            }
        }
        
        /**
         * �w�肳�ꂽ�I�u�W�F�N�g�����̃I�u�W�F�N�g�����������Ɠ���������������Condition�I�u�W�F�N�g���ǂ����𔻒肷��B<p>
         *
         * @param obj ��r�ΏۃI�u�W�F�N�g
         * @return ����������������Condition�I�u�W�F�N�g�̏ꍇtrue
         */
        public boolean equals(Object obj){
            if(obj == null){
                return false;
            }
            if(obj == this){
                return true;
            }
            if(!(obj instanceof Condition)){
                return false;
            }
            Condition comp = (Condition)obj;
            if(condition == null){
                return comp.condition == null;
            }else{
                return condition.equals(comp.condition);
            }
        }
        
        /**
         * �n�b�V���l���擾����B<p>
         *
         * @return �n�b�V���l
         */
        public int hashCode(){
            return condition == null ? 0 : condition.hashCode();
        }
    }
}