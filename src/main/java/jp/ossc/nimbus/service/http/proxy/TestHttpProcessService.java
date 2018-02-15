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
 * テスト用のHTTPプロキシのリクエスト処理サービス。<p>
 * HTTPリクエストの内容をファイルに出力する。<br>
 * HTTPレスポンスの内容は、HTTPリクエストの条件に応じて、ファイルから読み込んで応答する。または、{@link #addAction(TestHttpProcessService.Action)}で設定された順に、ファイルから読み込んで応答する。<br>
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
    
    // TestHttpProcessServiceMBeanのJavaDoc
    public String getRequestOutputFileEncoding(){
        return requestOutputFileEncoding;
    }
    // TestHttpProcessServiceMBeanのJavaDoc
    public void setRequestOutputFileEncoding(String encoding){
        requestOutputFileEncoding = encoding;
    }
    
    // TestHttpProcessServiceMBeanのJavaDoc
    public String getResponseInputFileEncoding(){
        return responseInputFileEncoding;
    }
    // TestHttpProcessServiceMBeanのJavaDoc
    public void setResponseInputFileEncoding(String encoding){
        responseInputFileEncoding = encoding;
    }
    
    /**
     * {@link HttpRequest}に対する条件毎にアクションを設定する。<p>
     *
     * @param condition 条件
     * @param action アクション
     * @exception Exception 条件式の解析に失敗した場合
     */
    public void setAction(String condition, Action action) throws Exception{
        final Condition cond = new Condition(condition, action);
        if(conditionActions.contains(cond)){
            conditionActions.remove(cond);
        }
        conditionActions.add(cond);
    }
    
    /**
     * 順次応答するアクションを登録する。<p>
     * 登録されたアクションは、呼び出されるたびに消費されるため、呼び出し毎に登録する必要がある。<br>
     * {@link #setAction(String, TestHttpProcessService.Action)}よりも優先される。<br>
     *
     * @param action アクション
     */
    public void addAction(Action action){
        if(actions.contains(action)){
            actions.remove(action);
        }
        actions.add(action);
    }
    
    /**
     * 順次応答するアクションをクリアする。<p>
     */
    public void clearAction(){
        actions.clear();
    }
    
    /**
     * HTTPリクエストのプロキシ処理を行う。<p>
     * HTTPリクエストの内容をファイルに出力する。<br>
     * HTTPレスポンスの内容は、{@link #setAction(String, TestHttpProcessService.Action)}で設定されたHTTPリクエストの条件に応じて、ファイルから読み込んで応答する。または、{@link #addAction(TestHttpProcessService.Action)}で設定された順に、ファイルから読み込んで応答する。<br>
     *
     * @param request HTTPリクエスト
     * @param response HTTPレスポンス
     * @exception Exception HTTPリクエストの処理に失敗した場合
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
     * HTTPリクエストの処理設定を行うクラス。<p>
     * HTTPリクエストの出力ファイル、HTTPレスポンスヘッダの設定、HTTPレスポンスボディの入力ファイルの設定が可能である。<br>
     *
     * @author M.Takata
     */
    public static class Action implements java.io.Serializable{
        
        private static final long serialVersionUID = 4155428986485777449L;
        
        /**
         * {@link Interpreter}で、応答のボディを編集する際のスクリプト内で参照可能な、{@link HttpRequest}の変数名。<p>
         */
        public static final String INTERPRET_VAR_NAME_REQUEST = "request";
        /**
         * {@link Interpreter}で、応答のボディを編集する際のスクリプト内で参照可能な、ボディのInputStreamの変数名。<p>
         */
        public static final String INTERPRET_VAR_NAME_RESPONSE_INPUT_STREAM = "inputStream";
        /**
         * {@link Interpreter}で、応答のボディを編集する際のスクリプト内で参照可能な、ボディのOutputStreamの変数名。<p>
         */
        public static final String INTERPRET_VAR_NAME_RESPONSE_OUTPUT_STREAM = "outputStream";
        /**
         * {@link Interpreter}で、応答のボディを編集する際のスクリプト内で参照可能な、ボディの文字列の変数名。<p>
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
         * HTTPリクエストの出力ファイルを取得する。<p>
         *
         * @return HTTPリクエストの出力ファイル
         */
        public String getRequestOutputFile(){
            return requestOutputFile;
        }
        
        /**
         * HTTPリクエストの出力ファイルを設定する。<p>
         * この設定を行った場合、HTTPヘッダ及びHTTPボディの両方が、このファイルに出力される。<br>
         *
         * @param file HTTPリクエストの出力ファイル
         */
        public void setRequestOutputFile(String file){
            requestOutputFile = file;
        }
        
        /**
         * HTTPリクエストヘッダの出力ファイルを取得する。<p>
         *
         * @return HTTPリクエストヘッダの出力ファイル
         */
        public String getRequestHeaderOutputFile(){
            return requestHeaderOutputFile;
        }
        
        /**
         * HTTPリクエストヘッダの出力ファイルを設定する。<p>
         * この設定を行った場合、HTTPヘッダのみが、このファイルに出力される。<br>
         *
         * @param file HTTPリクエストヘッダの出力ファイル
         */
        public void setRequestHeaderOutputFile(String file){
            requestHeaderOutputFile = file;
        }
        
        /**
         * HTTPリクエストボディの出力ファイルを取得する。<p>
         *
         * @return HTTPリクエストボディの出力ファイル
         */
        public String getRequestBodyOutputFile(){
            return requestBodyOutputFile;
        }
        
        /**
         * HTTPリクエストボディの出力ファイルを設定する。<p>
         * この設定を行った場合、HTTPボディのみが、このファイルに出力される。<br>
         *
         * @param file HTTPリクエストボディの出力ファイル
         */
        public void setRequestBodyOutputFile(String file){
            requestBodyOutputFile = file;
        }
        
        /**
         * HTTPレスポンスのHTTPバージョンを設定する。<p>
         * 設定しない場合は、HTTPリクエストのHTTPバージョンと同じ値になる。<br>
         *
         * @param version HTTPバージョン
         */
        public void setResponseVersion(String version){
            responseVersion = version;
        }
        
        /**
         * HTTPレスポンスのステータスコードを設定する。<p>
         * 設定しない場合は、{@link HttpResponse#getStatusCode()}になる。<br>
         *
         * @param code HTTPレスポンスのステータスコード
         */
        public void setResponseStatusCode(int code){
           responseStatusCode = code;
        }
        
        /**
         * HTTPレスポンスのステータスメッセージを設定する。<p>
         * 設定しない場合は、{@link HttpResponse#getStatusMessage()}になる。<br>
         *
         * @param message HTTPレスポンスのステータスメッセージ
         */
        public void setResponseStatusMessage(String message){
            responseStatusMessage = message;
        }
        
        /**
         * 処理時間[ms]を設定する。<p>
         */
        public void setProcessTime(long time){
            processTime = time;
        }
        
        /**
         * ヘッダを設定する。<p>
         *
         * @param name ヘッダ名
         * @param val ヘッダ値
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
         * ヘッダを設定する。<p>
         *
         * @param name ヘッダ名
         * @param vals ヘッダ値配列
         */
        public void setHeaders(String name, String[] vals){
            responseHeaderMap.put(name, vals);
        }
        
        /**
         * ヘッダ名の集合を取得する。<p>
         *
         * @return ヘッダ名の集合
         */
        protected Set getHeaderNameSet(){
            return responseHeaderMap.keySet();
        }
        
        /**
         * ヘッダを取得する。<p>
         *
         * @param name ヘッダ名
         * @return ヘッダ値
         */
        protected String getHeader(String name){
            final String[] vals = (String[])responseHeaderMap.get(name);
            return vals == null ? null : vals[0];
        }
        
        /**
         * ヘッダを取得する。<p>
         *
         * @param name ヘッダ名
         * @return ヘッダ値配列
         */
        protected String[] getHeaders(String name){
            return (String[])responseHeaderMap.get(name);
        }
        
        /**
         * HTTPレスポンスボディの入力ファイルを取得する。<p>
         *
         * @return HTTPレスポンスボディの入力ファイル
         */
        public String getResponseBodyInputFile(){
            return responseBodyInputFile;
        }
        
        /**
         * HTTPレスポンスボディの入力ファイルを設定する。<p>
         *
         * @param file HTTPレスポンスボディの入力ファイル
         */
        public void setResponseBodyInputFile(String file){
            responseBodyInputFile = file;
        }
        
        /**
         * HTTPレスポンスボディの入力ファイルがバイナリかどうかを設定する。<p>
         *
         * @param isBinary バイナリの場合true
         */
        public void setBinaryResponse(boolean isBinary){
            isBinaryResponse = isBinary;
        }
        
        /**
         * HTTPレスポンスボディの入力ファイルがバイナリかどうかを判定する。<p>
         *
         * @return trueの場合、バイナリ
         */
        public boolean isBinaryResponse(){
            return isBinaryResponse;
        }
        
        /**
         * {@link Interpreter}サービスのサービス名を設定する。<p>
         *
         * @param name {@link Interpreter}サービスのサービス名
         */
        public void setInterpreterServiceName(ServiceName name){
            interpreterServiceName = name;
        }
        
        /**
         * {@link Interpreter}サービスを設定する。<p>
         *
         * @param interpreter {@link Interpreter}サービス
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
         * {@link Interpreter}を使って、応答のボディを編集するためのスクリプトを設定する。<p>
         * {@link #setBinaryResponse(boolean) setBinaryResponse(true)}に設定した場合は、スクリプト内で、変数{@link #INTERPRET_VAR_NAME_REQUEST}、{@link #INTERPRET_VAR_NAME_RESPONSE_INPUT_STREAM}、{@link #INTERPRET_VAR_NAME_RESPONSE_OUTPUT_STREAM}が参照可能で、ボディの編集結果を{@link #INTERPRET_VAR_NAME_RESPONSE_OUTPUT_STREAM}に書き込む。<br>
         * {@link #setBinaryResponse(boolean) setBinaryResponse(false)}に設定した場合は、スクリプト内で、変数{@link #INTERPRET_VAR_NAME_REQUEST}、{@link #INTERPRET_VAR_NAME_RESPONSE_STRING}が参照可能で、ボディの編集結果を戻り値で返す。<br>
         *
         * @param script 応答のボディを編集するためのスクリプト
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
         * HTTPリクエストをファイルに出力する。<p>
         *
         * @param request HTTPリクエスト
         * @param requestOutputFileEncoding 出力ファイル文字エンコーディング
         * @exception IOException ファイルの出力に失敗した場合
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
         * HTTPレスポンスをファイルから読み込んで、レスポンスストリームに出力する。<p>
         *
         * @param request HTTPリクエスト
         * @param response HTTPレスポンス
         * @param responseInputFileEncoding 入力ファイル文字エンコーディング
         * @exception IOException ファイルの入力に失敗した場合
         * @exception EvaluateException 応答編集スクリプトの評価に失敗した場合
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
     * 条件。<p>
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
         * この条件に合致した場合のアクション。<p>
         */
        Action action;
        
        /**
         * インスタンスを生成する。<p>
         *
         * @param cond 条件式
         * @param action この条件に合致した場合のアクション
         * @exception Exception 条件式の解析に失敗した場合
         */
        public Condition(String cond, Action action) throws Exception{
            initCondition(cond);
            this.action = action;
        }
        
        /**
         * 条件を解析する。<p>
         *
         * @param cond 条件式
         * @exception Exception 条件式の解析に失敗した場合
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
         * 指定されたHTTPリクエストがこの条件に合致するか判定する。<p>
         *
         * @param request HTTPリクエスト
         * @return 条件に合致する場合true
         */
        protected boolean matchRequest(HttpRequest request){
            return matchRequest(request, false);
        }
        
        /**
         * 指定されたHTTPリクエストがこの条件に合致するか判定する。<p>
         *
         * @param request HTTPリクエスト
         * @param isTest 初期化時のテスト実行かどうか。trueの場合、テスト実行であり、渡されるHTTPリクエストが空であるため、評価結果がbooleanにならなくても例外はthrowしない
         * @return 条件に合致する場合true
         * @exception IllegalArgumentException 評価結果がbooleanでない場合。但し、テスト実行の場合は、throwされない。
         * @exception RuntimeException 条件式の評価中に例外が発生した場合
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
                // 起こらないはず
            }
        }
        
        /**
         * 指定されたオブジェクトがこのオブジェクトが持つ条件式と同じ条件式を持つConditionオブジェクトかどうかを判定する。<p>
         *
         * @param obj 比較対象オブジェクト
         * @return 同じ条件式を持つConditionオブジェクトの場合true
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
         * ハッシュ値を取得する。<p>
         *
         * @return ハッシュ値
         */
        public int hashCode(){
            return condition == null ? 0 : condition.hashCode();
        }
    }
}