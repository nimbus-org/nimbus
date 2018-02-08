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
package jp.ossc.nimbus.servlet;

import java.io.*;
import java.util.*;
import java.net.URLEncoder;
import javax.servlet.*;
import javax.servlet.http.*;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.beans.dataset.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.context.*;
import jp.ossc.nimbus.service.interpreter.*;
import jp.ossc.nimbus.util.converter.*;

/**
 * 共有コンテキストサーブレット。<p>
 * HTTP経由での共有コンテキストの管理をサポートする管理コンソールを提供する。<br>
 * このサーブレットには、以下の初期化パラメータがある。<br>
 * <table border="1" width="90%">
 *     <tr bgcolor="#cccccc"><th>#</th><th>パラメータ名</th><th>値の説明</th><th>デフォルト</th></tr>
 *     <tr><td>1</td><td>ServiceNames</td><td>対象とする共有コンテキストのサービス名をカンマ区切りで指定する。</td><td>存在する全ての{@link SharedContext 共有コンテキストサービス}。</td></tr>
 *     <tr><td>2</td><td>InterpreterServiceName</td><td>入力フィールドでスクリプトを入力された場合に、その解釈を行う{@link Interpreter インタープリタ}のサービス名を指定する。</td><td>内部的に生成された{@link BeanShellInterpreterService}。</td></tr>
 *     <tr><td>3</td><td>PutEnabled</td><td>{@link SharedContext 共有コンテキスト}への追加操作を有効にするかどうかを指定する。</td><td>false</td></tr>
 *     <tr><td>4</td><td>UpdateEnabled</td><td>{@link SharedContext 共有コンテキスト}への更新操作を有効にするかどうかを指定する。</td><td>false</td></tr>
 *     <tr><td>5</td><td>RemoveEnabled</td><td>{@link SharedContext 共有コンテキスト}への削除操作を有効にするかどうかを指定する。</td><td>false</td></tr>
 *     <tr><td>6</td><td>ClearEnabled</td><td>{@link SharedContext 共有コンテキスト}への全削除操作を有効にするかどうかを指定する。</td><td>false</td></tr>
 *     <tr><td>7</td><td>LoadEnabled</td><td>{@link SharedContext 共有コンテキスト}への読込操作を有効にするかどうかを指定する。</td><td>false</td></tr>
 *     <tr><td>8</td><td>SaveEnabled</td><td>{@link SharedContext 共有コンテキスト}への永続化操作を有効にするかどうかを指定する。</td><td>false</td></tr>
 *     <tr><td>9</td><td>JSONConverterServiceName</td><td>JSON形式での応答を要求する場合に使用する{@link BeanJSONConverter}サービスのサービス名を指定する。</td><td>指定しない場合は、内部生成される。</td></tr>
 *     <tr><td>10</td><td>UnicodeEscape</td><td>JSON形式での応答を要求する場合に、２バイト文字をユニコードエスケープするかどうかを指定する。</td><td>true</td></tr>
 * </table>
 * <p>
 * Webサービスは、クエリ指定でのGETリクエストに対して、JSONでデータを応答する。<br>
 * <table border="1" width="90%">
 *     <tr bgcolor="#cccccc"><th rowspan="2">#</th><th rowspan="2">アクション</th><th colspan="2">クエリパラメータ</th><th rowspan="2">応答JSONの例</th></tr>
 *     <tr bgcolor="#cccccc"><th>パラメータ名</th><th>値</th></tr>
 *     <tr><td>1</td><td><nobr>共有コンテキストの一覧取得</nobr></td><td>responseType</td><td>json</td><td>
 *     <code><pre>
 *[
 *    {
 *        "clientNodeNum":0,
 *        "serverNodeNum":1,
 *        "nodeNum":1,
 *        "name":"Nimbus%23Context1",
 *        "distributeType":"replicated",
 *        "size":2
 *    },
 *    {
 *        "clientNodeNum":0,
 *        "serverNodeNum":1,
 *        "nodeNum":1,
 *        "name":"Nimbus%23Context2",
 *        "distributeType":"distributed",
 *        "size":5
 *    }
 *]
 *     </pre></code></td></tr>
 *     <tr><td rowspan="3">2</td><td rowspan="3"><nobr>共有コンテキストの属性取得</nobr></td><td>responseType</td><td>json</td><td rowspan="3">
 *     <code><pre>
 *{
 *    "id":"USER-PC\/192.168.1.1:4506101c:14d9ec2142a:-7ffd",
 *    "client":false,
 *    "mainId":"USER-PC\/192.168.1.1:4506101c:14d9ec2142a:-7ffd",
 *    "name":"Nimbus#Context",
 *    "localSize":2,
 *    "serverNodeMembers":["USER-PC\/192.168.1.1:4506101c:14d9ec2142a:-7ffd"],
 *    "clientNodeMembers":[],
 *    "nodeMembers":["USER-PC\/192.168.1.1:4506101c:14d9ec2142a:-7ffd"],
 *    "distributeType":"replicated",
 *    "size":2,
 *    "main":true
 *}
 *     </pre></code></td></tr>
 *     <tr><td>action</td><td>context</td></tr>
 *     <tr><td>name</td><td>対象の共有コンテキストのサービス名。</td></tr>
 *     <tr><td rowspan="4">3</td><td rowspan="4"><nobr>共有コンテキストの値取得</nobr></td><td>responseType</td><td>json</td><td rowspan="4"><code><pre>{"key":"hoge", "value":100}</pre></code></td></tr>
 *     <tr><td>action</td><td>get</td></tr>
 *     <tr><td>name</td><td>対象の共有コンテキストのサービス名。</td></tr>
 *     <tr><td>key</td><td>キー。{@link Interpreter インタープリタ}を使ったオブジェクト生成式も指定可能。</td></tr>
 *     <tr><td rowspan="4">4</td><td rowspan="4"><nobr>共有コンテキストのキー存在判定</nobr></td><td>responseType</td><td>json</td><td rowspan="4"><code><pre>{"key":"hoge", "contains":true}</pre></code></td></tr>
 *     <tr><td>action</td><td>containsKey</td></tr>
 *     <tr><td>name</td><td>対象の共有コンテキストのサービス名。</td></tr>
 *     <tr><td>key</td><td>キー。{@link Interpreter インタープリタ}を使ったオブジェクト生成式も指定可能。</td></tr>
 *     <tr><td rowspan="3">5</td><td rowspan="3"><nobr>共有コンテキストのキー集合取得</nobr></td><td>responseType</td><td>json</td><td rowspan="3"><code><pre>{"keys":["hoge", "fuga"]}</pre></code></td></tr>
 *     <tr><td>action</td><td>keySet</td></tr>
 *     <tr><td>name</td><td>対象の共有コンテキストのサービス名。</td></tr>
 *     <tr><td rowspan="6">6</td><td rowspan="6"><nobr>共有コンテキストへの検索クエリ実行</nobr></td><td>responseType</td><td>json</td><td rowspan="6">
 *     <code><pre>
 *{
 *    "query":"import java.util.*;Iterator vals=context.values().iterator();int result=0;while(vals.hasNext())result+=vals.next().intValue();return result;",
 *    "result":300
 *}
 *     </pre></code></td></tr>
 *     <tr><td>action</td><td>query</td></tr>
 *     <tr><td>name</td><td>対象の共有コンテキストのサービス名。</td></tr>
 *     <tr><td>query</td><td>検索クエリ</td></tr>
 *     <tr><td>timeout</td><td>検索クエリのタイムアウト[ms]</td></tr>
 *     <tr><td>mergeQuery</td><td>分散共有コンテキストへの検索結果マージクエリ</td></tr>
 *     <tr><td rowspan="5">7</td><td rowspan="5"><nobr>共有コンテキストへの追加</nobr></td><td>responseType</td><td>json</td><td rowspan="5"><code><pre>{"key":"hoge","value":150,"old":100}</pre></code></td></tr>
 *     <tr><td>action</td><td>put</td></tr>
 *     <tr><td>name</td><td>対象の共有コンテキストのサービス名。</td></tr>
 *     <tr><td>key</td><td>キー。{@link Interpreter インタープリタ}を使ったオブジェクト生成式も指定可能。</td></tr>
 *     <tr><td>value</td><td>値。{@link Interpreter インタープリタ}を使ったオブジェクト生成式も指定可能。</td></tr>
 *     <tr><td rowspan="8">8</td><td rowspan="8"><nobr>共有コンテキストへの更新</nobr></td><td>responseType</td><td>json</td><td rowspan="8"><code><pre>{}</pre></code></td></tr>
 *     <tr><td>action</td><td>update</td></tr>
 *     <tr><td>name</td><td>対象の共有コンテキストのサービス名。</td></tr>
 *     <tr><td>key</td><td>キー。{@link Interpreter インタープリタ}を使ったオブジェクト生成式も指定可能。</td></tr>
 *     <tr><td>value[index]</td><td>{@link SharedContextRecord}のindex番目のプロパティ値。{@link Interpreter インタープリタ}を使ったオブジェクト生成式も指定可能。</td></tr>
 *     <tr><td>value(propertyName)</td><td>{@link SharedContextRecord}のプロパティ名propertyNameのプロパティ値。{@link Interpreter インタープリタ}を使ったオブジェクト生成式も指定可能。</td></tr>
 *     <tr><td>value_listIndex[index]</td><td>{@link SharedContextRecordList}のlistIndex番目の{@link SharedContextRecord}のindex番目のプロパティ値。{@link Interpreter インタープリタ}を使ったオブジェクト生成式も指定可能。</td></tr>
 *     <tr><td>value_listIndex(propertyName)</td><td>{@link SharedContextRecordList}のlistIndex番目の{@link SharedContextRecord}のプロパティ名propertyNameのプロパティ値。{@link Interpreter インタープリタ}を使ったオブジェクト生成式も指定可能。</td></tr>
 *     <tr><td rowspan="6">9</td><td rowspan="6"><nobr>共有コンテキストからの削除</nobr></td><td>responseType</td><td>json</td><td rowspan="6"><code><pre>{"key":"hoge","old":100}</pre></code></td></tr>
 *     <tr><td>action</td><td>remove</td></tr>
 *     <tr><td>name</td><td>対象の共有コンテキストのサービス名。</td></tr>
 *     <tr><td>key</td><td>キー。{@link Interpreter インタープリタ}を使ったオブジェクト生成式も指定可能。</td></tr>
 *     <tr><td>index</td><td>{@link SharedContextRecordList}上のインデックス。指定しない場合は、キー毎削除。</td></tr>
 *     <tr><td>clear</td><td>{@link SharedContextRecordList}をクリアするかどうかを指定する。trueの場合クリアする。</td></tr>
 *     <tr><td rowspan="4">10</td><td rowspan="4"><nobr>共有コンテキストのクリア</nobr></td><td>responseType</td><td>json</td><td rowspan="4"><code><pre>{"local":false}</pre></code></td></tr>
 *     <tr><td>action</td><td>clear</td></tr>
 *     <tr><td>name</td><td>対象の共有コンテキストのサービス名。</td></tr>
 *     <tr><td>local</td><td>共有コンテキストのローカルデータのみクリアするかどうかを指定する。trueの場合、ローカルのみ。指定しない場合は、false。</td></tr>
 *     <tr><td rowspan="4">11</td><td rowspan="4"><nobr>共有コンテキストの読み込み</nobr></td><td>responseType</td><td>json</td><td rowspan="4"><code><pre>{}</pre></code></td></tr>
 *     <tr><td>action</td><td>load</td></tr>
 *     <tr><td>name</td><td>対象の共有コンテキストのサービス名。</td></tr>
 *     <tr><td>key</td><td>読み込み対象のキー。{@link Interpreter インタープリタ}を使ったオブジェクト生成式も指定可能。指定しない場合は、全て読み込む。</td></tr>
 *     <tr><td rowspan="3">12</td><td rowspan="3"><nobr>共有コンテキストの読み込み</nobr></td><td>responseType</td><td>json</td><td rowspan="3"><code><pre>{}</pre></code></td></tr>
 *     <tr><td>action</td><td>loadKey</td></tr>
 *     <tr><td>name</td><td>対象の共有コンテキストのサービス名。</td></tr>
 *     <tr><td rowspan="4">13</td><td rowspan="4"><nobr>共有コンテキストの保存</nobr></td><td>responseType</td><td>json</td><td rowspan="4"><code><pre>{}</pre></code></td></tr>
 *     <tr><td>action</td><td>save</td></tr>
 *     <tr><td>name</td><td>対象の共有コンテキストのサービス名。</td></tr>
 *     <tr><td>key</td><td>保存対象のキー。{@link Interpreter インタープリタ}を使ったオブジェクト生成式も指定可能。指定しない場合は、全て保存する。</td></tr>
 * </table>
 * <p>
 * 以下に、サーブレットのweb.xml定義例を示す。<br>
 * <pre>
 * &lt;servlet&gt;
 *     &lt;servlet-name&gt;SharedContextServlet&lt;/servlet-name&gt;
 *     &lt;servlet-class&gt;jp.ossc.nimbus.servlet.SharedContextServlet&lt;/servlet-class&gt;
 * &lt;/servlet&gt;
 * 
 * &lt;servlet-mapping&gt;
 *     &lt;servlet-name&gt;SharedContextServlet&lt;/servlet-name&gt;
 *     &lt;url-pattern&gt;/context-console&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 * 
 * @author M.Takata
 */
public class SharedContextServlet extends HttpServlet{
    
    private static final long serialVersionUID = -6992362984683159336L;

    /**
     * 対象とする共有コンテキストのサービス名を指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_SERVICE_NAMES = "ServiceNames";
    
    /**
     * インタプリタのサービス名を指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_INTERPRETER_SERVICE_NAME = "InterpreterServiceName";
    
    /**
     * 追加を有効にするかどうかを指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_PUT_ENABLED = "PutEnabled";
    
    /**
     * 更新を有効にするかどうかを指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_UPDATE_ENABLED = "UpdateEnabled";
    
    /**
     * 削除を有効にするかどうかを指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_REMOVE_ENABLED = "RemoveEnabled";
    
    /**
     * 全削除を有効にするかどうかを指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_CLEAR_ENABLED = "ClearEnabled";
    
    /**
     * 読込みを有効にするかどうかを指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_LOAD_ENABLED = "LoadEnabled";
    
    /**
     * 書込みを有効にするかどうかを指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_SAVE_ENABLED = "SaveEnabled";
    
    /**
     * JSONコンバータのサービス名を指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_JSON_CONVERTER_SERVICE_NAME = "JSONConverterServiceName";
    
    /**
     * JSON応答時に２バイト文字をユニコードエスケープするかどうかのフラグを指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_UNICODE_ESCAPE = "UnicodeEscape";
    
    private Interpreter interpreter;
    private BeanJSONConverter jsonConverter;
    private StringStreamConverter toStringConverter;
    
    private ServiceName[] getServiceNames(){
        final ServletConfig config = getServletConfig();
        final String serviceNamesStr = config.getInitParameter(INIT_PARAM_NAME_SERVICE_NAMES);
        if(serviceNamesStr == null){
            return null;
        }
        final ServiceNameArrayEditor editor = new ServiceNameArrayEditor();
        editor.setAsText(serviceNamesStr);
        return (ServiceName[])editor.getValue();
    }
    
    private ServiceName getInterpreterServiceName(){
        final ServletConfig config = getServletConfig();
        final String serviceNameStr = config.getInitParameter(INIT_PARAM_NAME_INTERPRETER_SERVICE_NAME);
        if(serviceNameStr == null){
            return null;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        return (ServiceName)editor.getValue();
    }
    
    private boolean isPutEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_PUT_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private boolean isUpdateEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_UPDATE_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private boolean isRemoveEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_REMOVE_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private boolean isClearEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_CLEAR_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private boolean isLoadEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_LOAD_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private boolean isSaveEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_SAVE_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private ServiceName getJSONConverterServiceName(){
        final ServletConfig config = getServletConfig();
        final String serviceNameStr = config.getInitParameter(INIT_PARAM_NAME_JSON_CONVERTER_SERVICE_NAME);
        if(serviceNameStr == null){
            return null;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        return (ServiceName)editor.getValue();
    }
    
    private boolean isUnicodeEscape(){
        final ServletConfig config = getServletConfig();
        final String isEscape = config.getInitParameter(INIT_PARAM_NAME_UNICODE_ESCAPE);
        return isEscape == null ? true : Boolean.valueOf(isEscape).booleanValue();
    }
    
    /**
     * サーブレットの初期化を行う。<p>
     * サービス定義のロード及びロード完了チェックを行う。
     *
     * @exception ServletException サーブレットの初期化に失敗した場合
     */
    public synchronized void init() throws ServletException{
        ServiceName jsonConverterServiceName = getJSONConverterServiceName();
        if(jsonConverterServiceName == null){
            jsonConverter = new BeanJSONConverter();
        }else{
            jsonConverter = (BeanJSONConverter)ServiceManagerFactory.getServiceObject(jsonConverterServiceName);
        }
        jsonConverter.setCharacterEncodingToStream("UTF-8");
        jsonConverter.setUnicodeEscape(isUnicodeEscape());
        toStringConverter = new StringStreamConverter(StringStreamConverter.STREAM_TO_STRING);
        toStringConverter.setCharacterEncodingToObject("UTF-8");
        
        ServiceName interpreterServiceName = getInterpreterServiceName();
        if(interpreterServiceName == null){
            BeanShellInterpreterService bshInterpreter = new BeanShellInterpreterService();
            try{
                bshInterpreter.create();
                bshInterpreter.start();
            }catch(Exception e){
                throw new ServletException(e);
            }
            interpreter = bshInterpreter;
        }else{
            interpreter = (Interpreter)ServiceManagerFactory.getServiceObject(interpreterServiceName);
        }
    }
    
    /**
     * POSTリクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @exception ServletException 
     * @exception IOException 
     */
    protected void doPost(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException{
        process(req, resp);
    }
    
    /**
     * GETリクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @exception ServletException 
     * @exception IOException 
     */
    protected void doGet(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException{
        process(req, resp);
    }
    
    /**
     * リクエスト処理を行う。<p>
     * 管理コンソール処理を行う。
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @exception ServletException 
     * @exception IOException 
     */
    protected void process(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException{
        req.setCharacterEncoding("UTF-8");
        
        final String action = req.getParameter("action");
        final String responseType = req.getParameter("responseType");
        if(action == null){
            processIndexResponse(req, resp, responseType);
        }else if(action.equals("context")){
            processContextResponse(req, resp, responseType);
        }else if(action.equals("get")){
            processGetResponse(req, resp, responseType);
        }else if(action.equals("containsKey")){
            processContainsKeyResponse(req, resp, responseType);
        }else if(action.equals("keySet")){
            processKeySetResponse(req, resp, responseType);
        }else if(action.equals("query")){
            processQueryResponse(req, resp, responseType);
        }else if(action.equals("put")){
            if(!isPutEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processPutResponse(req, resp, responseType);
        }else if(action.equals("update")){
            if(!isUpdateEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processUpdateResponse(req, resp, responseType);
        }else if(action.equals("remove")){
            if(!isRemoveEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processRemoveResponse(req, resp, responseType);
        }else if(action.equals("clear")){
            if(!isClearEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processClearResponse(req, resp, responseType);
        }else if(action.equals("load")){
            if(!isLoadEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processLoadResponse(req, resp, responseType);
        }else if(action.equals("loadKey")){
            if(!isLoadEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processLoadKeyResponse(req, resp, responseType);
        }else if(action.equals("save")){
            if(!isSaveEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processSaveResponse(req, resp, responseType);
        }else{
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private String getCurrentPath(HttpServletRequest req){
        return req.getContextPath() + req.getServletPath();
    }
    
    /**
     * インデックス画面リクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processIndexResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        
        ServiceName[] contextServiceNames = getServiceNames();
        if(contextServiceNames == null){
            ServiceManager[] managers = ServiceManagerFactory.findManagers();
            List names = new ArrayList();
            for(int i = 0; i < managers.length; i++){
                Set services = managers[i].serviceSet();
                Iterator itr = services.iterator();
                while(itr.hasNext()){
                    Service service = (Service)itr.next();
                    if(service instanceof SharedContext){
                        names.add(service.getServiceNameObject());
                    }
                }
            }
            contextServiceNames = (ServiceName[])names.toArray(new ServiceName[names.size()]);
        }
        if(contextServiceNames != null){
            Arrays.sort(contextServiceNames);
        }
        
        final StringBuilder buf = new StringBuilder();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            List jsonList = new ArrayList();
            if(contextServiceNames != null){
                for(int i = 0; i < contextServiceNames.length; i++){
                    final ServiceName serviceName = contextServiceNames[i];
                    Service service = ServiceManagerFactory.getService(serviceName);
                    boolean isDistributed = false;
                    if(service instanceof DistributedSharedContext){
                        isDistributed = true;
                    }else if(service instanceof SharedContext){
                        isDistributed = false;
                    }else{
                        continue;
                    }
                    SharedContext context = (SharedContext)service;
                    Map jsonMap = new HashMap();
                    jsonMap.put("name", serviceName.toString());
                    jsonMap.put("distributeType", isDistributed ? "distributed" : "replicated");
                    jsonMap.put("nodeNum", new Integer(context.getMemberIdList().size()));
                    jsonMap.put("clientNodeNum", new Integer(context.getClientMemberIdSet().size()));
                    jsonMap.put("serverNodeNum", new Integer(context.getServerMemberIdSet().size()));
                    jsonMap.put("size", new Integer(context.size()));
                    jsonList.add(jsonMap);
                }
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonList))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>Nimbus SharedContexts</title></head>");
            buf.append("<body>");
            
            buf.append("<b>Contexts</b><br>");
            buf.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\" width=\"90%\">");
            buf.append("<tr bgcolor=\"#cccccc\"><th>name</th><th>replicated/distributed</th><th>client/server</th><th>node number</th><th>size</th></tr>");
            final StringBuilder url = new StringBuilder();
            if(contextServiceNames != null){
                for(int i = 0; i < contextServiceNames.length; i++){
                    final ServiceName serviceName = contextServiceNames[i];
                    Service service = ServiceManagerFactory.getService(serviceName);
                    boolean isDistributed = false;
                    if(service instanceof DistributedSharedContext){
                        isDistributed = true;
                    }else if(service instanceof SharedContext){
                        isDistributed = false;
                    }else{
                        continue;
                    }
                    SharedContext context = (SharedContext)service;
                    url.setLength(0);
                    url.append(getCurrentPath(req))
                       .append("?action=context&name=")
                       .append(URLEncoder.encode(serviceName.toString(), "UTF-8"));
                    buf.append("<tr>");
                    buf.append("<td><a href=\"")
                       .append(resp.encodeURL(url.toString()))
                       .append("\">");
                    buf.append(serviceName).append("</a></td>");
                    buf.append("<td>").append(isDistributed ? "distributed" : "replicated").append("</td>");
                    buf.append("<td>").append(context.isClient() ? "client" : ("server" + (context.isMain() ? "(main)" : "(sub)"))).append("</td>");
                    buf.append("<td>").append(context.getMemberIdList().size() + "(" + context.getServerMemberIdSet().size() + ")").append("</td>");
                    buf.append("<td>").append(context.size() + (context.isClient() || isDistributed ? ("(" + context.sizeLocal() + ")") : "")).append("</td>");
                    buf.append("</tr>");
                }
            }
            buf.append("</table>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * コンテキスト画面リクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processContextResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        final String serviceNameStr = req.getParameter("name");
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        final ServiceName serviceName = (ServiceName)editor.getValue();
        SharedContext context = (SharedContext)ServiceManagerFactory.getServiceObject(serviceName);
        if(context == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        boolean isDistributed = context instanceof DistributedSharedContext ? true : false;
        final StringBuilder buf = new StringBuilder();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            jsonMap.put("name", serviceName.toString());
            jsonMap.put("id", context.getId().toString());
            jsonMap.put("distributeType", isDistributed ? "distributed" : "replicated");
            jsonMap.put("client", context.isClient() ? Boolean.TRUE : Boolean.FALSE);
            jsonMap.put("main", context.isMain() ? Boolean.TRUE : Boolean.FALSE);
            List nodeMembers = new ArrayList();
            List memberIdList = context.getMemberIdList();
            for(int i = 0; i < memberIdList.size(); i++){
                nodeMembers.add(memberIdList.get(i).toString());
            }
            jsonMap.put("nodeMembers", nodeMembers);
            List clientNodeMembers = new ArrayList();
            Iterator clientMemberIds = context.getClientMemberIdSet().iterator();
            while(clientMemberIds.hasNext()){
                Object id = clientMemberIds.next();
                clientNodeMembers.add(id.toString());
            }
            Collections.sort(clientNodeMembers);
            jsonMap.put("clientNodeMembers", clientNodeMembers);
            List serverNodeMembers = new ArrayList();
            Iterator serverMemberIds = context.getServerMemberIdSet().iterator();
            while(serverMemberIds.hasNext()){
                Object id = serverMemberIds.next();
                serverNodeMembers.add(id.toString());
            }
            Collections.sort(serverNodeMembers);
            jsonMap.put("serverNodeMembers", serverNodeMembers);
            jsonMap.put("mainId", context.getMainId().toString());
            jsonMap.put("size", new Integer(context.size()));
            jsonMap.put("localSize", new Integer(context.sizeLocal()));
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>Nimbus SharedContext " + serviceName + "</title></head>");
            buf.append("<body>");
            
            buf.append("<b>Context " + serviceName + "</b><br>");
            buf.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\" width=\"90%\">");
            buf.append("<tr><th bgcolor=\"#cccccc\">replicated/distributed</th><td colspan=\"2\">").append(isDistributed ? "distributed" : "replicated").append("</td></tr>");
            buf.append("<tr><th bgcolor=\"#cccccc\">client/server</th><td colspan=\"2\">").append(context.isClient() ? "client" : ("server" + (context.isMain() ? "(main)" : "(sub)"))).append("</td></tr>");
            buf.append("<tr><th bgcolor=\"#cccccc\">node member</th><td colspan=\"2\">").append(context.getMemberIdList() + "(" + context.getServerMemberIdSet() + ")").append("</td></tr>");
            if(context instanceof DistributedSharedContextService){
                buf.append("<tr><th bgcolor=\"#cccccc\">distribute</th><td colspan=\"2\"><pre>").append(((DistributedSharedContextService)context).displayDistributeInfo()).append("</pre></td></tr>");
            }
            buf.append("<tr><th bgcolor=\"#cccccc\">size</th><td colspan=\"2\">").append(context.size() + (context.isClient() || isDistributed ? ("(" + context.sizeLocal() + ")") : "")).append("</td></tr>");
            
            buf.append("<form method=\"POST\" action=\"").append(getCurrentPath(req)).append("\">");
            buf.append("<input type=\"hidden\" name=\"action\" value=\"get\"/>");
            buf.append("<input type=\"hidden\" name=\"name\" value=\"").append(serviceNameStr).append("\"/>");
            buf.append("<tr><th bgcolor=\"#cccccc\"><input type=\"submit\" value=\"get\"/></th><td colspan=\"2\"><table><tr><td>key:</td><td><textarea name=\"key\" cols=\"40\" rows=\"4\"></textarea></td></tr></table></td></tr>");
            buf.append("</form>");
            
            buf.append("<form method=\"POST\" action=\"").append(getCurrentPath(req)).append("\">");
            buf.append("<input type=\"hidden\" name=\"action\" value=\"containsKey\"/>");
            buf.append("<input type=\"hidden\" name=\"name\" value=\"").append(serviceNameStr).append("\"/>");
            buf.append("<tr><th bgcolor=\"#cccccc\"><input type=\"submit\" value=\"containsKey\"/></th><td colspan=\"2\"><table><tr><td>key:</td><td><textarea name=\"key\" cols=\"40\" rows=\"4\"></textarea></td></tr></table></td></tr>");
            buf.append("</form>");
            
            buf.append("<form method=\"POST\" action=\"").append(getCurrentPath(req)).append("\">");
            buf.append("<input type=\"hidden\" name=\"action\" value=\"keySet\"/>");
            buf.append("<input type=\"hidden\" name=\"name\" value=\"").append(serviceNameStr).append("\"/>");
            buf.append("<tr><th bgcolor=\"#cccccc\"><input type=\"submit\" value=\"keySet\"/></th><td colspan=\"2\">&nbsp;</td></tr>");
            buf.append("</form>");
            
            buf.append("<form method=\"POST\" action=\"").append(getCurrentPath(req)).append("\">");
            buf.append("<input type=\"hidden\" name=\"action\" value=\"query\"/>");
            buf.append("<input type=\"hidden\" name=\"name\" value=\"").append(serviceNameStr).append("\"/>");
            if(isDistributed){
                buf.append("<tr><th bgcolor=\"#cccccc\"><input type=\"submit\" value=\"query\"/></th><td><table><tr><td>query:</td><td><textarea name=\"query\" cols=\"40\" rows=\"4\"></textarea></td></tr><tr><td>mergeQuery:</td><td><textarea name=\"mergeQuery\" cols=\"40\" rows=\"4\"></textarea></td></tr></table></td><td><table><tr><td>timeout:</td><td><textarea name=\"timeout\"></textarea></td></tr></table></td></tr>");
            }else{
                buf.append("<tr><th bgcolor=\"#cccccc\"><input type=\"submit\" value=\"query\"/></th><td><table><tr><td>query:</td><td><textarea name=\"query\" cols=\"40\" rows=\"4\"></textarea></td></tr></table></td><td><table><tr><td>timeout:</td><td><textarea name=\"timeout\"></textarea></td></tr></table></td></tr>");
            }
            buf.append("</form>");
            
            if(isRemoveEnabled()){
                buf.append("<form method=\"POST\" action=\"").append(getCurrentPath(req)).append("\">");
                buf.append("<input type=\"hidden\" name=\"action\" value=\"remove\"/>");
                buf.append("<input type=\"hidden\" name=\"name\" value=\"").append(serviceNameStr).append("\"/>");
                buf.append("<tr><th bgcolor=\"#cccccc\"><input type=\"submit\" value=\"remove\"/></th><td colspan=\"2\"><table><tr><td>key:</td><td><textarea name=\"key\" cols=\"40\" rows=\"4\"></textarea></td></tr></table></td></tr>");
                buf.append("</form>");
            }
            if(isPutEnabled()){
                buf.append("<form method=\"POST\" action=\"").append(getCurrentPath(req)).append("\">");
                buf.append("<input type=\"hidden\" name=\"action\" value=\"put\"/>");
                buf.append("<input type=\"hidden\" name=\"name\" value=\"").append(serviceNameStr).append("\"/>");
                buf.append("<tr><th bgcolor=\"#cccccc\"><input type=\"submit\" value=\"put\"/></th><td><table><tr><td>key:</td><td><textarea name=\"key\" cols=\"40\" rows=\"4\"></textarea></td></tr></table></td><td><table><tr><td>value:</td><td><textarea name=\"value\"></textarea></td></tr></table></td></tr>");
                buf.append("</form>");
            }
            if(isClearEnabled()){
                buf.append("<form method=\"POST\" action=\"").append(getCurrentPath(req)).append("\">");
                buf.append("<input type=\"hidden\" name=\"action\" value=\"clear\"/>");
                buf.append("<input type=\"hidden\" name=\"name\" value=\"").append(serviceNameStr).append("\"/>");
                buf.append("<tr><th bgcolor=\"#cccccc\"><input type=\"submit\" value=\"clear\"/></th><td colspan=\"2\"><input type=\"radio\" name=\"local\" value=\"true\" checked>local<input type=\"radio\" name=\"local\" value=\"false\">all</td></tr>");
                buf.append("</form>");
            }
            if(isLoadEnabled()){
                buf.append("<form method=\"POST\" action=\"").append(getCurrentPath(req)).append("\">");
                buf.append("<input type=\"hidden\" name=\"action\" value=\"load\"/>");
                buf.append("<input type=\"hidden\" name=\"name\" value=\"").append(serviceNameStr).append("\"/>");
                buf.append("<tr><th bgcolor=\"#cccccc\"><input type=\"submit\" value=\"load\"/></th><td colspan=\"2\">&nbsp;</td></tr>");
                buf.append("</form>");
                buf.append("<form method=\"POST\" action=\"").append(getCurrentPath(req)).append("\">");
                buf.append("<input type=\"hidden\" name=\"action\" value=\"load\"/>");
                buf.append("<input type=\"hidden\" name=\"name\" value=\"").append(serviceNameStr).append("\"/>");
                buf.append("<tr><th bgcolor=\"#cccccc\"><input type=\"submit\" value=\"load\"/></th><td colspan=\"2\"><table><tr><td>key:</td><td><textarea name=\"key\" cols=\"40\" rows=\"4\"></textarea></td></tr></table></td></tr>");
                buf.append("</form>");
                buf.append("<form method=\"POST\" action=\"").append(getCurrentPath(req)).append("\">");
                buf.append("<input type=\"hidden\" name=\"action\" value=\"loadKey\"/>");
                buf.append("<input type=\"hidden\" name=\"name\" value=\"").append(serviceNameStr).append("\"/>");
                buf.append("<tr><th bgcolor=\"#cccccc\"><input type=\"submit\" value=\"loadKey\"/></th><td colspan=\"2\">&nbsp;</td></tr>");
                buf.append("</form>");
            }
            if(isSaveEnabled()){
                buf.append("<form method=\"POST\" action=\"").append(getCurrentPath(req)).append("\">");
                buf.append("<input type=\"hidden\" name=\"action\" value=\"save\"/>");
                buf.append("<input type=\"hidden\" name=\"name\" value=\"").append(serviceNameStr).append("\"/>");
                buf.append("<tr><th bgcolor=\"#cccccc\"><input type=\"submit\" value=\"save\"/></th><td colspan=\"2\"><table><tr><td>key:</td><td><textarea name=\"key\" cols=\"40\" rows=\"4\"></textarea></td></tr></table></td></tr>");
                buf.append("</form>");
                buf.append("<form method=\"POST\" action=\"").append(getCurrentPath(req)).append("\">");
                buf.append("<input type=\"hidden\" name=\"action\" value=\"save\"/>");
                buf.append("<input type=\"hidden\" name=\"name\" value=\"").append(serviceNameStr).append("\"/>");
                buf.append("<tr><th bgcolor=\"#cccccc\"><input type=\"submit\" value=\"save\"/></th><td colspan=\"2\">&nbsp;</td></tr>");
                buf.append("</form>");
            }
            buf.append("</table>")
            ;
            buf.append("<hr>");
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("\">Contexts</a>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * コンテキストの値を取得するリクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processGetResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        final String serviceNameStr = req.getParameter("name");
        if(serviceNameStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        final ServiceName serviceName = (ServiceName)editor.getValue();
        SharedContext context = (SharedContext)ServiceManagerFactory.getServiceObject(serviceName);
        if(context == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String keyStr = req.getParameter("key");
        if(keyStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Object key = null;
        if(keyStr.length() >= 2
            && ((keyStr.charAt(0) == '"'
                    && keyStr.charAt(keyStr.length() - 1) == '"')
                || (keyStr.charAt(0) == '\''
                    && keyStr.charAt(keyStr.length() - 1) == '\''))
        ){
            key = keyStr.substring(1, keyStr.length() - 1);
        }
        
        final StringBuilder buf = new StringBuilder();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            
            try{
                if(key == null){
                    if(keyStr.indexOf("\n") == -1
                        && keyStr.indexOf("\r") == -1){
                        keyStr = "return " + keyStr;
                    }
                    key = interpreter.evaluate(keyStr);
                }
                jsonMap.put("key", key);
                if(context.containsKey(key)){
                    Object value = context.get(key);
                    jsonMap.put("value", value);
                }
            }catch(Exception e){
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>Nimbus SharedContext " + serviceName + " Get </title>");
            buf.append("<meta http-equiv=\"Content-Script-Type\" content=\"text/javascript\">");
            buf.append("<script type=\"text/javascript\">\n<!--\n");
            buf.append("function removeRecord(index){");
            buf.append("document.forms[0].elements[\"action\"].value=\"remove\";");
            buf.append("document.forms[0].elements[\"index\"].value=index;");
            buf.append("document.forms[0].submit();");
            buf.append("}\n");
            buf.append("function clearList(){");
            buf.append("document.forms[0].elements[\"action\"].value=\"remove\";");
            buf.append("document.forms[0].elements[\"clear\"].value=\"true\";");
            buf.append("document.forms[0].submit();");
            buf.append("}\n");
            buf.append("// -->\n</script></head>");
            buf.append("<body>");
            
            try{
                if(key == null){
                    if(keyStr.indexOf("\n") == -1
                        && keyStr.indexOf("\r") == -1){
                        keyStr = "return " + keyStr;
                    }
                    key = interpreter.evaluate(keyStr);
                }
                if(context.containsKey(key)){
                    Object value = context.get(key);
                    writeValue(req, resp, serviceNameStr, keyStr, buf, value, true);
                }else{
                    buf.append("not contains key : ").append(key);
                }
            }catch(Exception e){
                writeThrowable(buf, e);
            }
            
            buf.append("<hr>");
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("?action=context")
                .append("&name=").append(URLEncoder.encode(serviceNameStr, "UTF-8"))
                .append("\">Context</a>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    private StringBuilder writeThrowable(StringBuilder buf, Throwable th){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        th.printStackTrace(pw);
        pw.flush();
        String stackTrace = sw.toString();
        return buf.append("<pre>").append(stackTrace).append("</pre>");
    }
    
    private StringBuilder writeValue(
        HttpServletRequest req,
        HttpServletResponse resp,
        String contextName,
        String key,
        StringBuilder buf,
        Object value,
        boolean isLink
    ) throws IOException{
        if(value == null){
            buf.append("null");
        }else if(value instanceof Record){
            Record record = (Record)value;
            boolean isUpdateSupport = isLink && (record instanceof SharedContextRecord) && isUpdateEnabled();
            RecordSchema schema = record.getRecordSchema();
            PropertySchema[] propSchemata = schema.getPropertySchemata();
            if(isUpdateSupport){
                buf.append("<form method=\"POST\" action=\"").append(getCurrentPath(req)).append("\">");
                buf.append("<input type=\"hidden\" name=\"action\" value=\"update\"/>");
                buf.append("<input type=\"hidden\" name=\"name\" value=\"").append(contextName).append("\"/>");
                buf.append("<textarea name=\"key\" hidden>").append(URLEncoder.encode(key, "UTF-8")).append("</textarea>");
            }
            buf.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\" width=\"90%\">");
            buf.append("<tr><th bgcolor=\"#cccccc\">type</th><td colspan=\"4\">").append(value.getClass().getName()).append("</td></tr>");
            if(isUpdateSupport){
                buf.append("<tr bgcolor=\"#cccccc\"><th rowspan=\"2\">name</th><th rowspan=\"2\">type</th><th colspan=\"2\">value</th></tr>");
                buf.append("<tr bgcolor=\"#cccccc\"><th>current</th><th>new</th></tr>");
            }else{
                buf.append("<tr bgcolor=\"#cccccc\"><th>name</th><th>type</th><th>value</th></tr>");
            }
            for(int i = 0; i < propSchemata.length; i++){
                buf.append("<tr><th bgcolor=\"#cccccc\">").append(propSchemata[i].getName()).append("</th>")
                   .append("<td>").append(propSchemata[i].getType().getName()).append("</td>")
                   .append("<td>").append(record.getProperty(i)).append("</td>");
                if(isUpdateSupport){
                    buf.append("<td><table><tr><td>→</td><td><textarea name=\"value\" cols=\"40\" rows=\"4\"></textarea></td></tr></table></td>");
                }
                buf.append("</tr>");
            }
            buf.append("</table>");
            if(isUpdateSupport){
                buf.append("<input type=\"submit\" value=\"update\">");
                buf.append("</form>");
            }
        }else if(value instanceof RecordList){
            RecordList recordList = (RecordList)value;
            boolean isUpdateSupport = isLink && (recordList instanceof SharedContextRecordList) && isUpdateEnabled();
            RecordSchema schema = recordList.getRecordSchema();
            PropertySchema[] propSchemata = schema.getPropertySchemata();
            buf.append("<form method=\"POST\" action=\"").append(getCurrentPath(req)).append("\">");
            buf.append("<input type=\"hidden\" name=\"action\" value=\"update\"/>");
            buf.append("<input type=\"hidden\" name=\"name\" value=\"").append(contextName).append("\"/>");
            buf.append("<textarea name=\"key\" hidden>").append(key).append("</textarea>");
            buf.append("<input type=\"hidden\" name=\"index\" value=\"\"/>");
            buf.append("<input type=\"hidden\" name=\"clear\" value=\"false\"/>");
            buf.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\" width=\"90%\">");
            if(isUpdateSupport){
                buf.append("<tr><th bgcolor=\"#cccccc\" colspan=\"2\">type</th><td colspan=\"")
                   .append(propSchemata.length + 1).append("\">")
                   .append(value.getClass().getName()).append("</td></tr>");
            }else{
                buf.append("<tr><th bgcolor=\"#cccccc\">type</th><td colspan=\"")
                   .append(propSchemata.length + 1).append("\">")
                   .append(value.getClass().getName()).append("</td></tr>");
            }
            buf.append("<tr bgcolor=\"#cccccc\">");
            if(isUpdateSupport){
                buf.append("<th rowspan=\"2\" colspan=\"2\">Index</th>");
            }else{
                buf.append("<th rowspan=\"2\">Index</th>");
            }
            for(int i = 0; i < propSchemata.length; i++){
                buf.append("<th>").append(propSchemata[i].getName()).append("</th>");
            }
            buf.append("<th rowspan=\"2\">　</th>");
            buf.append("</tr>");
            buf.append("<tr bgcolor=\"#cccccc\">");
            for(int i = 0; i < propSchemata.length; i++){
                buf.append("<th>").append(propSchemata[i].getType().getName()).append("</th>");
            }
            buf.append("</tr>");
            for(int i = 0; i < recordList.size(); i++){
                Record record = (Record)recordList.get(i);
                if(isUpdateSupport){
                    buf.append("<tr><td rowspan=\"2\">").append(i).append("</td>").append("<td>current</td>");
                }else{
                    buf.append("<tr><td>").append(i).append("</td>");
                }
                for(int j = 0; j < propSchemata.length; j++){
                    buf.append("<td>").append(record.getProperty(j)).append("</td>");
                }
                buf.append("<td>");
                buf.append("<input type=\"button\" value=\"remove\" onclick=\"removeRecord(").append(i).append(");\">");
                buf.append("</td>");
                buf.append("</tr>");
                if(isUpdateSupport){
                    buf.append("<tr>").append("<td>new</td>");
                    for(int j = 0; j < propSchemata.length; j++){
                        buf.append("<td><table><tr><td style=\"text-align:center;\">↓</td></tr><tr><td><textarea name=\"value_").append(i).append("\" cols=\"40\" rows=\"4\"></textarea></td></tr></table></td>");
                    }
                    buf.append("</tr>");
                }
            }
            buf.append("</table>");
            if(isUpdateSupport){
                buf.append("<input type=\"submit\" value=\"update\">");
                buf.append("<input type=\"button\" value=\"clear\" onclick=\"clearList();\">");
            }
            buf.append("</form>");
        }else{
            if(isLink && isPutEnabled()){
                buf.append("<form method=\"POST\" action=\"").append(getCurrentPath(req)).append("\">");
                buf.append("<input type=\"hidden\" name=\"action\" value=\"put\"/>");
                buf.append("<input type=\"hidden\" name=\"name\" value=\"").append(contextName).append("\"/>");
                buf.append("<textarea name=\"key\" hidden>").append(key).append("</textarea>");
            }
            buf.append("<table><tr><td>").append(value.toString()).append("</td>");
            if(isLink && isPutEnabled()){
                buf.append("<td>→</td><td><textarea name=\"value\" cols=\"40\" rows=\"4\"></textarea></td><td><input type=\"submit\" value=\"put\"></td></tr></table>");
                buf.append("</form>");
            }else{
                buf.append("</tr></table>");
            }
        }
        return buf;
    }
    
    /**
     * コンテキストのキーが存在するか確認するリクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processContainsKeyResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        final String serviceNameStr = req.getParameter("name");
        if(serviceNameStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        final ServiceName serviceName = (ServiceName)editor.getValue();
        SharedContext context = (SharedContext)ServiceManagerFactory.getServiceObject(serviceName);
        if(context == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String keyStr = req.getParameter("key");
        if(keyStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Object key = null;
        if(keyStr.length() >= 2
            && ((keyStr.charAt(0) == '"'
                    && keyStr.charAt(keyStr.length() - 1) == '"')
                || (keyStr.charAt(0) == '\''
                    && keyStr.charAt(keyStr.length() - 1) == '\''))
        ){
            key = keyStr.substring(1, keyStr.length() - 1);
        }
        
        final StringBuilder buf = new StringBuilder();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            try{
                if(key == null){
                    if(keyStr.indexOf("\n") == -1
                        && keyStr.indexOf("\r") == -1){
                        keyStr = "return " + keyStr;
                    }
                    key = interpreter.evaluate(keyStr);
                }
                jsonMap.put("key", key);
                if(context.containsKey(key)){
                    jsonMap.put("contains", Boolean.TRUE);
                }else{
                    jsonMap.put("contains", Boolean.FALSE);
                }
            }catch(Exception e){
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>Nimbus SharedContext " + serviceName + " ContainsKey</title></head>");
            buf.append("<body>");
            
            try{
                if(key == null){
                    if(keyStr.indexOf("\n") == -1
                        && keyStr.indexOf("\r") == -1){
                        keyStr = "return " + keyStr;
                    }
                    key = interpreter.evaluate(keyStr);
                }
                if(context.containsKey(key)){
                    buf.append("contains key : ").append(key);
                    if(context.containsKeyLocal(key)){
                        buf.append(" on local");
                    }
                }else{
                    buf.append("not contains key : ").append(key);
                }
            }catch(Exception e){
                writeThrowable(buf, e);
            }
            
            buf.append("<hr>");
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("?action=context")
                .append("&name=").append(URLEncoder.encode(serviceNameStr, "UTF-8"))
                .append("\">Context</a>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * コンテキストのキー集合を取得するリクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processKeySetResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        final String serviceNameStr = req.getParameter("name");
        if(serviceNameStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        final ServiceName serviceName = (ServiceName)editor.getValue();
        SharedContext context = (SharedContext)ServiceManagerFactory.getServiceObject(serviceName);
        if(context == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        List keys = null;
        Exception exception = null;
        try{
            keys = new ArrayList(context.keySet());
            if(keys.size() != 0){
                if(keys.get(0) instanceof Comparable){
                    Collections.sort(keys);
                }else{
                    Collections.sort(
                        keys,
                        new Comparator(){
                            public int compare(Object o1, Object o2){
                                if(o1 == null && o2 == null){
                                    return 0;
                                }else if(o1 == null && o2 != null){
                                    return -1;
                                }else if(o1 != null && o2 == null){
                                    return 1;
                                }
                                return o1.toString().compareTo(o2.toString());
                            }
                        }
                    );
                }
            }
        }catch(Exception e){
            exception = e;
        }
        
        final StringBuilder buf = new StringBuilder();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception == null){
                jsonMap.put("keys", keys);
            }else{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>Nimbus SharedContext " + serviceName + " KeySet</title></head>");
            buf.append("<body>");
            if(exception == null){
                buf.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\" width=\"90%\">");
                final boolean isDistributed = (context instanceof DistributedSharedContext);
                if(isDistributed){
                    buf.append("<tr bgcolor=\"#cccccc\"><th colspan=\"2\">key</th><th rowspan=\"2\">local/remote</th><th rowspan=\"2\">main/sub</th><th rowspan=\"2\">node no</th><th rowspan=\"2\">&nbsp;</th></tr>");
                }else{
                    buf.append("<tr bgcolor=\"#cccccc\"><th colspan=\"2\">key</th><th rowspan=\"2\">local/remote</th><th rowspan=\"2\">&nbsp;</th></tr>");
                }
                buf.append("<tr bgcolor=\"#cccccc\"><th>value</th><th>type</th></tr>");
                for(int i = 0; i < keys.size(); i++){
                    Object key = keys.get(i);
                    
                    buf.append("<tr><td>").append(key).append("</td>");
                    buf.append("<td>").append(key == null ? "&nbsp;" : key.getClass().getName()).append("</td>");
                    buf.append("<td>").append(context.containsKeyLocal(key) ? "local" : "remote").append("</td>");
                    if(isDistributed){
                        buf.append("<td>").append(((DistributedSharedContext)context).isMain(key) ? "main" : "sub").append("</td>");
                        buf.append("<td>").append(((DistributedSharedContext)context).getDataNodeIndex(key)).append("</td>");
                    }
                    buf.append("<form method=\"POST\" action=\"").append(getCurrentPath(req)).append("\" name=\"key_").append(i).append("\">");
                    buf.append("<input type=\"hidden\" name=\"action\" value=\"\"/>");
                    buf.append("<input type=\"hidden\" name=\"name\" value=\"").append(serviceNameStr).append("\"/>");
                    buf.append("<td><table><tr><td>key:</td><td><textarea name=\"key\" cols=\"40\" rows=\"4\">");
                    if(key instanceof String){
                        buf.append('"').append(key).append('"');
                    }
                    buf.append("</textarea></td>");
                    buf.append("<td><input type=\"submit\" value=\"get\" onclick=\"document.key_").append(i).append(".action.value='get'\"/></td>");
                    if(isRemoveEnabled()){
                        buf.append("<td><input type=\"submit\" value=\"remove\" onclick=\"document.key_").append(i).append(".action.value='remove'\"/></td>");
                    }
                    if(isLoadEnabled()){
                        buf.append("<td><input type=\"submit\" value=\"load\" onclick=\"document.key_").append(i).append(".action.value='load'\"/></td>");
                    }
                    if(isSaveEnabled()){
                        buf.append("<td><input type=\"submit\" value=\"save\" onclick=\"document.key_").append(i).append(".action.value='save'\"/></td>");
                    }
                    buf.append("</tr></table></td></form>");
                    buf.append("</tr>");
                }
                buf.append("</table>");
            }else{
                writeThrowable(buf, exception);
            }
            
            buf.append("<hr>");
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("?action=context")
                .append("&name=").append(URLEncoder.encode(serviceNameStr, "UTF-8"))
                .append("\">Context</a>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * コンテキストにクエリを実行するリクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processQueryResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        final String serviceNameStr = req.getParameter("name");
        if(serviceNameStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        final ServiceName serviceName = (ServiceName)editor.getValue();
        SharedContext context = (SharedContext)ServiceManagerFactory.getServiceObject(serviceName);
        if(context == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String query = req.getParameter("query");
        if(query == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String mergeQuery = req.getParameter("mergeQuery");
        String timeoutStr = req.getParameter("timeout");
        long timeout = 0;
        if(timeoutStr != null && timeoutStr.length() != 0){
            try{
                timeout = Long.parseLong(timeoutStr);
            }catch(NumberFormatException e){
                timeoutStr = null;
            }
        }
        
        Object ret = null;
        Exception exception = null;
        try{
            if(mergeQuery != null && mergeQuery.length() != 0 && (context instanceof DistributedSharedContext)){
                DistributedSharedContext distContext = (DistributedSharedContext)context;
                ret = timeoutStr == null ? distContext.executeInterpretQuery(query, mergeQuery, null) : distContext.executeInterpretQuery(query, mergeQuery, null, timeout);
            }else{
                ret = timeoutStr == null ? context.executeInterpretQuery(query, null) : context.executeInterpretQuery(query, null, timeout);
            }
        }catch(Exception e){
            exception = e;
        }
        
        final StringBuilder buf = new StringBuilder();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            jsonMap.put("query", query);
            if(mergeQuery != null){
                jsonMap.put("mergeQuery", mergeQuery);
            }
            if(exception == null){
                jsonMap.put("result", ret);
            }else{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>Nimbus SharedContext " + serviceName + " Query</title></head>");
            buf.append("<body>");
            
            if(exception == null){
                writeValue(req, resp, null, null, buf, ret, false);
            }else{
                writeThrowable(buf, exception);
            }
            
            buf.append("<hr>");
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("?action=context")
                .append("&name=").append(URLEncoder.encode(serviceNameStr, "UTF-8"))
                .append("\">Context</a>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * コンテキストの値を削除するリクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processRemoveResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        final String serviceNameStr = req.getParameter("name");
        if(serviceNameStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        final ServiceName serviceName = (ServiceName)editor.getValue();
        SharedContext context = (SharedContext)ServiceManagerFactory.getServiceObject(serviceName);
        if(context == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String keyStr = req.getParameter("key");
        if(keyStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Object key = null;
        if(keyStr.length() >= 2
            && ((keyStr.charAt(0) == '"'
                    && keyStr.charAt(keyStr.length() - 1) == '"')
                || (keyStr.charAt(0) == '\''
                    && keyStr.charAt(keyStr.length() - 1) == '\''))
        ){
            key = keyStr.substring(1, keyStr.length() - 1);
        }
        String indexStr = req.getParameter("index");
        int index = -1;
        if(indexStr != null && indexStr.length() != 0){
            try{
                index = Integer.parseInt(indexStr);
            }catch(NumberFormatException e){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        String clearStr = req.getParameter("clear");
        boolean isClear = false;
        if(clearStr != null && clearStr.length() != 0){
            isClear = Boolean.valueOf(clearStr).booleanValue();
        }
        
        Object old = null;
        Exception exception = null;
        try{
            if(key == null){
                if(keyStr.indexOf("\n") == -1
                    && keyStr.indexOf("\r") == -1){
                    keyStr = "return " + keyStr;
                }
                key = interpreter.evaluate(keyStr);
            }
            if(isClear){
                Object value = context.get(key);
                if(value instanceof SharedContextRecordList){
                    SharedContextRecordList list = (SharedContextRecordList)value;
                    SharedContextValueDifference diff = list.updateClear(null);
                    context.update(key, diff);
                }else if(value instanceof Collection){
                    Collection col = (Collection)value;
                    col.clear();
                    context.put(key, col);
                }else{
                    throw new UnsupportedOperationException("Remove to this type is not supported. type=" + (value == null ? "null value" : value.getClass().getName()));
                }
            }else if(index < 0){
                old = context.remove(key);
            }else{
                Object value = context.get(key);
                if(value instanceof SharedContextRecordList){
                    SharedContextRecordList list = (SharedContextRecordList)value;
                    old = list.get(index);
                    SharedContextValueDifference diff = list.updateRemove(index, null);
                    context.update(key, diff);
                }else if(value instanceof List){
                    List list = (List)value;
                    old = list.remove(index);
                    context.put(key, list);
                }else{
                    throw new UnsupportedOperationException("Remove to this type is not supported. type=" + (value == null ? "null value" : value.getClass().getName()));
                }
            }
        }catch(Exception e){
            exception = e;
        }
        
        final StringBuilder buf = new StringBuilder();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            jsonMap.put("key", key);
            if(index >= 0){
                jsonMap.put("index", new Integer(index));
            }
            if(exception == null){
                if(!isClear){
                    jsonMap.put("old", old);
                }
            }else{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>Nimbus SharedContext " + serviceName + " Remove</title></head>");
            buf.append("<body>");
            
            buf.append("key : ").append(key).append("<br>");
            if(index >= 0){
                buf.append("index : ").append(index).append("<br>");
            }
            if(exception == null){
                if(isClear){
                    buf.append("clear").append("<br>");
                }else{
                    buf.append("removed : ").append(old).append("<br>");
                }
            }else{
                writeThrowable(buf, exception);
            }
            
            buf.append("<hr>");
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("?action=context")
                .append("&name=").append(URLEncoder.encode(serviceNameStr, "UTF-8"))
                .append("\">Context</a>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * コンテキストに値を追加するリクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processPutResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        final String serviceNameStr = req.getParameter("name");
        if(serviceNameStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        final ServiceName serviceName = (ServiceName)editor.getValue();
        SharedContext context = (SharedContext)ServiceManagerFactory.getServiceObject(serviceName);
        if(context == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String keyStr = req.getParameter("key");
        if(keyStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Object key = null;
        if(keyStr.length() >= 2
            && ((keyStr.charAt(0) == '"'
                    && keyStr.charAt(keyStr.length() - 1) == '"')
                || (keyStr.charAt(0) == '\''
                    && keyStr.charAt(keyStr.length() - 1) == '\''))
        ){
            key = keyStr.substring(1, keyStr.length() - 1);
        }
        String valueStr = req.getParameter("value");
        if(valueStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Object value = null;
        if(valueStr.length() >= 2
            && ((valueStr.charAt(0) == '"'
                    && valueStr.charAt(valueStr.length() - 1) == '"')
                || (valueStr.charAt(0) == '\''
                    && valueStr.charAt(valueStr.length() - 1) == '\''))
        ){
            value = valueStr.substring(1, valueStr.length() - 1);
        }
        
        Object old = null;
        Exception exception = null;
        try{
            if(key == null){
                if(keyStr.indexOf("\n") == -1
                    && keyStr.indexOf("\r") == -1){
                    keyStr = "return " + keyStr;
                }
                key = interpreter.evaluate(keyStr);
            }
            if(value == null){
                value = interpreter.evaluate(valueStr);
            }
            old = context.put(key, value);
        }catch(Exception e){
            exception = e;
        }
        
        final StringBuilder buf = new StringBuilder();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            jsonMap.put("key", key);
            jsonMap.put("value", value);
            if(exception == null){
                jsonMap.put("old", old);
            }else{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>Nimbus SharedContext " + serviceName + " Put</title></head>");
            buf.append("<body>");
            
            if(exception == null){
                buf.append("put key : ").append(key).append(" value : ").append(value);
            }else{
                writeThrowable(buf, exception);
            }
            
            buf.append("<hr>");
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("?action=context")
                .append("&name=").append(URLEncoder.encode(serviceNameStr, "UTF-8"))
                .append("\">Context</a>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * コンテキストの値を全削除するリクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processClearResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        final String serviceNameStr = req.getParameter("name");
        if(serviceNameStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        final ServiceName serviceName = (ServiceName)editor.getValue();
        SharedContext context = (SharedContext)ServiceManagerFactory.getServiceObject(serviceName);
        if(context == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String localStr = req.getParameter("local");
        boolean isLocal = false;
        if(localStr != null && localStr.length() != 0){
            isLocal = Boolean.valueOf(localStr).booleanValue();
        }
        
        Exception exception = null;
        try{
            if(isLocal){
                context.clearLocal();
            }else{
                context.clear();
            }
        }catch(Exception e){
            exception = e;
        }
        
        final StringBuilder buf = new StringBuilder();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            jsonMap.put("local", isLocal ? Boolean.TRUE : Boolean.FALSE);
            if(exception != null){
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>Nimbus SharedContext " + serviceName + " Clear</title></head>");
            buf.append("<body>");
            
            if(exception == null){
                buf.append("clear ").append(isLocal ? "local" : "all");
            }else{
                writeThrowable(buf, exception);
            }
            
            buf.append("<hr>");
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("?action=context")
                .append("&name=").append(URLEncoder.encode(serviceNameStr, "UTF-8"))
                .append("\">Context</a>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * コンテキストを読み込むリクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processLoadResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        final String serviceNameStr = req.getParameter("name");
        if(serviceNameStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        final ServiceName serviceName = (ServiceName)editor.getValue();
        SharedContext context = (SharedContext)ServiceManagerFactory.getServiceObject(serviceName);
        if(context == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String keyStr = req.getParameter("key");
        Object key = null;
        if(keyStr != null && keyStr.length() >= 2
            && ((keyStr.charAt(0) == '"'
                    && keyStr.charAt(keyStr.length() - 1) == '"')
                || (keyStr.charAt(0) == '\''
                    && keyStr.charAt(keyStr.length() - 1) == '\''))
        ){
            key = keyStr.substring(1, keyStr.length() - 1);
        }
        
        Exception exception = null;
        try{
            if(keyStr != null && keyStr.length() != 0){
                if(key == null){
                    if(keyStr.indexOf("\n") == -1
                        && keyStr.indexOf("\r") == -1){
                        keyStr = "return " + keyStr;
                    }
                    key = interpreter.evaluate(keyStr);
                }
                context.load(key);
            }else{
                context.load();
            }
        }catch(Exception e){
            exception = e;
        }
        
        final StringBuilder buf = new StringBuilder();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception != null){
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>Nimbus SharedContext " + serviceName + " Load</title></head>");
            buf.append("<body>");
            
            if(exception == null){
                if(keyStr != null && keyStr.length() != 0){
                    buf.append("load ").append(key).append(" complete");
                }else{
                    buf.append("load complete");
                }
            }else{
                writeThrowable(buf, exception);
            }
            
            buf.append("<hr>");
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("?action=context")
                .append("&name=").append(URLEncoder.encode(serviceNameStr, "UTF-8"))
                .append("\">Context</a>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * コンテキストのキーを読み込むリクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processLoadKeyResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        final String serviceNameStr = req.getParameter("name");
        if(serviceNameStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        final ServiceName serviceName = (ServiceName)editor.getValue();
        SharedContext context = (SharedContext)ServiceManagerFactory.getServiceObject(serviceName);
        if(context == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        Exception exception = null;
        try{
            context.loadKey();
        }catch(Exception e){
            exception = e;
        }
        
        final StringBuilder buf = new StringBuilder();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception != null){
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>Nimbus SharedContext " + serviceName + " LoadKey</title></head>");
            buf.append("<body>");
            
            if(exception == null){
                buf.append("load key complete");
            }else{
                writeThrowable(buf, exception);
            }
            
            buf.append("<hr>");
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("?action=context")
                .append("&name=").append(URLEncoder.encode(serviceNameStr, "UTF-8"))
                .append("\">Context</a>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * コンテキストを書き込むリクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processSaveResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        final String serviceNameStr = req.getParameter("name");
        if(serviceNameStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        final ServiceName serviceName = (ServiceName)editor.getValue();
        SharedContext context = (SharedContext)ServiceManagerFactory.getServiceObject(serviceName);
        if(context == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        String keyStr = req.getParameter("key");
        Object key = null;
        if(keyStr != null && keyStr.length() >= 2
            && ((keyStr.charAt(0) == '"'
                    && keyStr.charAt(keyStr.length() - 1) == '"')
                || (keyStr.charAt(0) == '\''
                    && keyStr.charAt(keyStr.length() - 1) == '\''))
        ){
            key = keyStr.substring(1, keyStr.length() - 1);
        }
        
        Exception exception = null;
        try{
            if(keyStr != null && keyStr.length() != 0){
                if(key == null){
                    if(keyStr.indexOf("\n") == -1
                        && keyStr.indexOf("\r") == -1){
                        keyStr = "return " + keyStr;
                    }
                    key = interpreter.evaluate(keyStr);
                }
                context.save(key);
            }else{
                context.save();
            }
        }catch(Exception e){
            exception = e;
        }
        
        final StringBuilder buf = new StringBuilder();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception != null){
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>Nimbus SharedContext " + serviceName + " Save</title></head>");
            buf.append("<body>");
            
            if(exception == null){
                if(keyStr != null && keyStr.length() != 0){
                    buf.append("save ").append(key).append(" complete");
                }else{
                    buf.append("save complete");
                }
            }else{
                writeThrowable(buf, exception);
            }
            
            buf.append("<hr>");
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("?action=context")
                .append("&name=").append(URLEncoder.encode(serviceNameStr, "UTF-8"))
                .append("\">Context</a>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * コンテキストの値を更新するリクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processUpdateResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        final String serviceNameStr = req.getParameter("name");
        if(serviceNameStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        final ServiceName serviceName = (ServiceName)editor.getValue();
        SharedContext context = (SharedContext)ServiceManagerFactory.getServiceObject(serviceName);
        if(context == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String keyStr = req.getParameter("key");
        if(keyStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Object key = null;
        if(keyStr.length() >= 2
            && ((keyStr.charAt(0) == '"'
                    && keyStr.charAt(keyStr.length() - 1) == '"')
                || (keyStr.charAt(0) == '\''
                    && keyStr.charAt(keyStr.length() - 1) == '\''))
        ){
            key = keyStr.substring(1, keyStr.length() - 1);
        }
        
        Exception exception = null;
        SharedContextValueDifference diff = null;
        try{
            if(key == null){
                if(keyStr.indexOf("\n") == -1
                    && keyStr.indexOf("\r") == -1){
                    keyStr = "return " + keyStr;
                }
                key = interpreter.evaluate(keyStr);
            }
            Object value = context.get(key);
            if(value instanceof SharedContextRecord){
                SharedContextRecord record = (SharedContextRecord)value;
                Enumeration names = req.getParameterNames();
                while(names.hasMoreElements()){
                    String name = (String)names.nextElement();
                    if(!name.startsWith("value")){
                        continue;
                    }
                    if(name.equals("value")){
                        String[] values = req.getParameterValues("value");
                        for(int i = 0; i < values.length; i++){
                            if(values[i] != null && values[i].length() != 0){
                                String valueStr = values[i];
                                if(valueStr.indexOf("\n") == -1
                                    && valueStr.indexOf("\r") == -1){
                                    valueStr = "return " + valueStr;
                                }
                                Object updateValue = interpreter.evaluate(valueStr);
                                diff = record.updateProperty(i, updateValue, diff);
                            }
                        }
                    }else if(name.matches("value\\[[0-9]+\\]")){
                        String indexStr = name.substring(name.indexOf("[") + 1, name.length() - 1);
                        int index = 0;
                        try{
                            index = Integer.parseInt(indexStr);
                        }catch(NumberFormatException e){continue;}
                        String valueStr = req.getParameter(name);
                        if(valueStr.indexOf("\n") == -1
                            && valueStr.indexOf("\r") == -1){
                            valueStr = "return " + valueStr;
                        }
                        Object updateValue = interpreter.evaluate(valueStr);
                        diff = record.updateProperty(index, updateValue, diff);
                    }else if(name.matches("value\\(.+\\)")){
                        String propName = name.substring(name.indexOf("(") + 1, name.length() - 1);
                        String valueStr = req.getParameter(name);
                        if(valueStr.indexOf("\n") == -1
                            && valueStr.indexOf("\r") == -1){
                            valueStr = "return " + valueStr;
                        }
                        Object updateValue = interpreter.evaluate(valueStr);
                        diff = record.updateProperty(propName, updateValue, diff);
                    }
                }
            }else if(value instanceof SharedContextRecordList){
                SharedContextRecordList recordList = (SharedContextRecordList)value;
                Enumeration names = req.getParameterNames();
                while(names.hasMoreElements()){
                    String name = (String)names.nextElement();
                    if(!name.startsWith("value_")){
                        continue;
                    }
                    if(name.matches("value_[0-9]+")){
                        String indexStr = name.substring(name.indexOf("_") + 1);
                        int index = 0;
                        try{
                            index = Integer.parseInt(indexStr);
                        }catch(NumberFormatException e){continue;}
                        SharedContextRecord record = (SharedContextRecord)recordList.get(index);
                        String[] values = req.getParameterValues(name);
                        for(int i = 0; i < values.length; i++){
                            if(values[i] != null && values[i].length() != 0){
                                String valueStr = values[i];
                                if(valueStr.indexOf("\n") == -1
                                    && valueStr.indexOf("\r") == -1){
                                    valueStr = "return " + valueStr;
                                }
                                Object updateValue = interpreter.evaluate(valueStr);
                                diff = record.updateProperty(i, updateValue, diff);
                            }
                        }
                    }else if(name.matches("value_[0-9]+\\[[0-9]+\\]")){
                        String indexStr = name.substring(name.indexOf("_") + 1, name.indexOf("["));
                        int index = 0;
                        try{
                            index = Integer.parseInt(indexStr);
                        }catch(NumberFormatException e){continue;}
                        SharedContextRecord record = (SharedContextRecord)recordList.get(index);
                        indexStr = name.substring(name.indexOf("[") + 1, name.length() - 1);
                        try{
                            index = Integer.parseInt(indexStr);
                        }catch(NumberFormatException e){continue;}
                        String valueStr = req.getParameter(name);
                        if(valueStr.indexOf("\n") == -1
                            && valueStr.indexOf("\r") == -1){
                            valueStr = "return " + valueStr;
                        }
                        Object updateValue = interpreter.evaluate(valueStr);
                        diff = record.updateProperty(index, updateValue, diff);
                    }else if(name.matches("value_[0-9]+\\(.+\\)")){
                        String indexStr = name.substring(name.indexOf("_") + 1, name.indexOf("("));
                        int index = 0;
                        try{
                            index = Integer.parseInt(indexStr);
                        }catch(NumberFormatException e){continue;}
                        SharedContextRecord record = (SharedContextRecord)recordList.get(index);
                        String propName = name.substring(name.indexOf("(") + 1, name.length() - 1);
                        String valueStr = req.getParameter(name);
                        if(valueStr.indexOf("\n") == -1
                            && valueStr.indexOf("\r") == -1){
                            valueStr = "return " + valueStr;
                        }
                        Object updateValue = interpreter.evaluate(valueStr);
                        diff = record.updateProperty(propName, updateValue, diff);
                    }
                }
            }else{
                throw new UnsupportedOperationException("Updates to this type is not supported. type=" + (value == null ? "null value" : value.getClass().getName()));
            }
            if(diff != null){
                context.update(key, diff);
            }
        }catch(Exception e){
            exception = e;
        }
        
        final StringBuilder buf = new StringBuilder();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception != null){
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>Nimbus SharedContext " + serviceName + " Update</title></head>");
            buf.append("<body>");
            
            if(exception == null){
                if(diff != null){
                    buf.append("update " + key + " complete");
                }else{
                    buf.append("no difference " + key);
                }
            }else{
                writeThrowable(buf, exception);
            }
            
            buf.append("<hr>");
            buf.append("<a href=\"").append(getCurrentPath(req))
                    .append("?action=context")
                    .append("&name=").append(URLEncoder.encode(serviceNameStr, "UTF-8"))
                    .append("\">Context</a>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
}
