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
import java.beans.PropertyEditor;
import java.net.*;
import java.lang.reflect.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.openmbean.*;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.jmx.*;
import jp.ossc.nimbus.util.converter.*;

/**
 * JMXコンソールサーブレット。<p>
 * ローカル及び、リモートに存在する複数のJMXサーバを管理し、MBeanを操作するコンソール画面を提供する。<br>
 * HTTP経由でのMBeanの管理をサポートする管理コンソール及びWebサービスを提供する。<br>
 * このサーブレットには、以下の初期化パラメータがある。<br>
 * <table border="1" width="90%">
 *     <tr bgcolor="#cccccc"><th>#</th><th>パラメータ名</th><th>値の説明</th><th>デフォルト</th></tr>
 *     <tr><td>1</td><td>MBeanServerConnectionFactoryManagerNames</td><td>{@link MBeanServerConnectionFactory}サービスが登録されているサービスマネージャの名前をカンマ区切りで指定する。<br>指定されたサービスマネージャに登録されているMBeanServerConnectionFactoryを検索して、そのJMX接続を利用して、JMXサーバに接続する。</td><td></td></tr>
 *     <tr><td>2</td><td>MBeanServerConnectionFactoryServiceNames</td><td>{@link MBeanServerConnectionFactory}サービスのサービス名をカンマ区切りで指定する。<br>指定されたMBeanServerConnectionFactoryのJMX接続を利用して、JMXサーバに接続する。</td><td></td></tr>
 *     <tr><td>3</td><td>AttributeSetEnabled</td><td>このサーブレットが提供する管理コンソール及びWebサービスで、MBeanの属性を変更する機能を有効にするかどうかを指定する。<br>有効にする場合は、trueを指定する。</td><td>false</td></tr>
 *     <tr><td>4</td><td>AttributeMaxLength</td><td>このサーブレットが提供する管理コンソールで、MBeanの属性を表示する場合に表示する属性値の最大長を指定する。</td><td>制限なし</td></tr>
 *     <tr><td>5</td><td>OperationEnabled</td><td>このサーブレットが提供する管理コンソール及びWebサービスで、MBeanの操作を実行する機能を有効にするかどうかを指定する。<br>有効にする場合は、trueを指定する。</td><td>false</td></tr>
 *     <tr><td>6</td><td>JSONConverterServiceName</td><td>JSON形式での応答を要求する場合に使用する{@link BeanJSONConverter}サービスのサービス名を指定する。</td><td>指定しない場合は、内部生成される。</td></tr>
 *     <tr><td>7</td><td>UnicodeEscape</td><td>JSON形式での応答を要求する場合に、２バイト文字をユニコードエスケープするかどうかを指定する。</td><td>true</td></tr>
 * </table>
 * <p>
 * <p>
 * Webサービスは、クエリ指定でのGETリクエストに対して、JSONでデータを応答する。<br>
 * <table border="1" width="90%">
 *     <tr bgcolor="#cccccc"><th rowspan="2">#</th><th rowspan="2">アクション</th><th colspan="2">クエリパラメータ</th><th rowspan="2">応答JSONの例</th></tr>
 *     <tr bgcolor="#cccccc"><th>パラメータ名</th><th>値</th></tr>
 *     <tr><td>1</td><td><nobr>JMXサーバの一覧取得</nobr></td><td>responseType</td><td>json</td><td><code>["WebServer%23web01","WebServer%23web02","BatchServer%23batch01","BatchServer%23batch02"]</code></td></tr>
 *     <tr><td rowspan="3">2</td><td rowspan="3"><nobr>指定JMXサーバ内のドメイン名の一覧取得</nobr></td><td>responseType</td><td>json</td><td rowspan="3"><code>[["JMImplementation","com.sun.management","java.lang","java.util.logging"],["myDomain"]]</code></td></tr>
 *     <tr><td>action</td><td>server</td></tr>
 *     <tr><td>name</td><td>JMXサーバ名。指定しない場合は、ローカルホストが対象となる。</td></tr>
 *     <tr><td rowspan="5">3</td><td rowspan="5"><nobr>指定ドメイン内のMBeanの一覧取得</nobr></td><td>responseType</td><td>json</td><td rowspan="5"><code>["java.lang:type=ClassLoading","java.lang:type=Compilation","java.lang:type=GarbageCollector,name=PS MarkSweep","java.lang:type=GarbageCollector,name=PS Scavenge","java.lang:type=Memory","java.lang:type=MemoryManager,name=CodeCacheManager","java.lang:type=MemoryPool,name=Code Cache","java.lang:type=MemoryPool,name=PS Eden Space","java.lang:type=MemoryPool,name=PS Old Gen","java.lang:type=MemoryPool,name=PS Perm Gen","java.lang:type=MemoryPool,name=PS Survivor Space","java.lang:type=OperatingSystem","java.lang:type=Runtime","java.lang:type=Threading"]</code></td></tr>
 *     <tr><td>action</td><td>domain</td></tr>
 *     <tr><td>serverName</td><td>JMXサーバ名</td></tr>
 *     <tr><td>index</td><td>ローカルホストにJMXサーバが複数存在する場合に、何番目のJMXサーバかを指定するインデックス。</td></tr>
 *     <tr><td>name</td><td>JMXドメイン名</td></tr>
 *     <tr><td rowspan="5">4</td><td rowspan="5"><nobr>MBeanの属性及び操作一覧</nobr></td><td>responseType</td><td>json</td><td rowspan="5"><code>{"attributes":[{"description":"Verbose","descriptor":{"fieldNames":["openType","originalType"],"fields":["openType=(javax.management.openmbean.SimpleType(name=java.lang.Boolean))","originalType=boolean"],"valid":true},"is":true,"name":"Verbose","readable":true,"type":"boolean","writable":true},{"defaultValue":null,"description":"HeapMemoryUsage","descriptor":{"fieldNames":["openType","originalType"],"fields":["openType=(javax.management.openmbean.CompositeType(name=java.lang.management.MemoryUsage,items=((itemName=committed,itemType=javax.management.openmbean.SimpleType(name=java.lang.Long)),(itemName=init,itemType=javax.management.openmbean.SimpleType(name=java.lang.Long)),(itemName=max,itemType=javax.management.openmbean.SimpleType(name=java.lang.Long)),(itemName=used,itemType=javax.management.openmbean.SimpleType(name=java.lang.Long)))))","originalType=java.lang.management.MemoryUsage"],"valid":true},"is":false,"legalValues":null,"maxValue":null,"minValue":null,"name":"HeapMemoryUsage","openType":{"array":false,"className":"javax.management.openmbean.CompositeData","description":"java.lang.management.MemoryUsage","typeName":"java.lang.management.MemoryUsage"},"readable":true,"type":"javax.management.openmbean.CompositeData","writable":false},{"defaultValue":null,"description":"NonHeapMemoryUsage","descriptor":{"fieldNames":["openType","originalType"],"fields":["openType=(javax.management.openmbean.CompositeType(name=java.lang.management.MemoryUsage,items=((itemName=committed,itemType=javax.management.openmbean.SimpleType(name=java.lang.Long)),(itemName=init,itemType=javax.management.openmbean.SimpleType(name=java.lang.Long)),(itemName=max,itemType=javax.management.openmbean.SimpleType(name=java.lang.Long)),(itemName=used,itemType=javax.management.openmbean.SimpleType(name=java.lang.Long)))))","originalType=java.lang.management.MemoryUsage"],"valid":true},"is":false,"legalValues":null,"maxValue":null,"minValue":null,"name":"NonHeapMemoryUsage","openType":{"array":false,"className":"javax.management.openmbean.CompositeData","description":"java.lang.management.MemoryUsage","typeName":"java.lang.management.MemoryUsage"},"readable":true,"type":"javax.management.openmbean.CompositeData","writable":false},{"description":"ObjectPendingFinalizationCount","descriptor":{"fieldNames":["openType","originalType"],"fields":["openType=(javax.management.openmbean.SimpleType(name=java.lang.Integer))","originalType=int"],"valid":true},"is":false,"name":"ObjectPendingFinalizationCount","readable":true,"type":"int","writable":false}],"className":"sun.management.MemoryImpl","constructors":[],"description":"Information on the management interface of the MBean","descriptor":{"fieldNames":["immutableInfo","interfaceClassName","mxbean"],"fields":["immutableInfo=true","interfaceClassName=java.lang.management.MemoryMXBean","mxbean=true"],"valid":true},"notifications":[{"description":"Memory Notification","descriptor":{"fieldNames":[],"fields":[],"valid":true},"name":"javax.management.Notification","notifTypes":["java.management.memory.threshold.exceeded","java.management.memory.collection.threshold.exceeded"]}],"operations":[{"description":"gc","descriptor":{"fieldNames":["openType","originalType"],"fields":["openType=(javax.management.openmbean.SimpleType(name=java.lang.Void))","originalType=void"],"valid":true},"impact":3,"name":"gc","returnType":"void","signature":[]}]}</code></td></tr>
 *     <tr><td>action</td><td>mbean</td></tr>
 *     <tr><td>serverName</td><td>JMXサーバ名</td></tr>
 *     <tr><td>index</td><td>ローカルホストにJMXサーバが複数存在する場合に、何番目のJMXサーバかを指定するインデックス。</td></tr>
 *     <tr><td>name</td><td>MBeanのJMXオブジェクト名</td></tr>
 *     <tr><td rowspan="8">5</td><td rowspan="8"><nobr>MBeanの属性を設定する</nobr></td><td>responseType</td><td>json</td><td rowspan="8"><code>{"result":"Success!!"}</code></td></tr>
 *     <tr><td>action</td><td>set</td></tr>
 *     <tr><td>serverName</td><td>JMXサーバ名</td></tr>
 *     <tr><td>index</td><td>ローカルホストにJMXサーバが複数存在する場合に、何番目のJMXサーバかを指定するインデックス。</td></tr>
 *     <tr><td>name</td><td>MBeanのJMXオブジェクト名</td></tr>
 *     <tr><td>attribute</td><td>MBeanの属性名</td></tr>
 *     <tr><td>attributeType</td><td>MBeanの属性型。valueを文字列から属性型に編集するために指定する。</td></tr>
 *     <tr><td>value</td><td>MBeanの属性値</td></tr>
 *     <tr><td rowspan="6">6</td><td rowspan="6"><nobr>MBeanの属性を取得する</nobr></td><td>responseType</td><td>json</td><td rowspan="6"><code>{"value":false}</code></td></tr>
 *     <tr><td>action</td><td>get</td></tr>
 *     <tr><td>serverName</td><td>JMXサーバ名</td></tr>
 *     <tr><td>index</td><td>ローカルホストにJMXサーバが複数存在する場合に、何番目のJMXサーバかを指定するインデックス。</td></tr>
 *     <tr><td>name</td><td>MBeanのJMXオブジェクト名</td></tr>
 *     <tr><td>attribute</td><td>MBeanの属性名</td></tr>
 *     <tr><td rowspan="8">7</td><td rowspan="8"><nobr>MBeanの操作を実行する</nobr></td><td>responseType</td><td>json</td><td rowspan="8"><code>{"result":null}</code></td></tr>
 *     <tr><td>action</td><td>operation</td></tr>
 *     <tr><td>serverName</td><td>JMXサーバ名</td></tr>
 *     <tr><td>index</td><td>ローカルホストにJMXサーバが複数存在する場合に、何番目のJMXサーバかを指定するインデックス。</td></tr>
 *     <tr><td>name</td><td>MBeanのJMXオブジェクト名</td></tr>
 *     <tr><td>operation</td><td>MBeanの操作名シグニチャ。メソッド名(引数型1,引数型2,...)</td></tr>
 *     <tr><td>argTypes</td><td>引数型。argsを文字列から引数型に編集するために指定する。</td></tr>
 *     <tr><td>args</td><td>操作の引数</td></tr>
 * </table>
 * <p>
 * 以下に、サーブレットのweb.xml定義例を示す。<br>
 * <pre>
 * &lt;servlet&gt;
 *     &lt;servlet-name&gt;JMXConsoleServlet&lt;/servlet-name&gt;
 *     &lt;servlet-class&gt;jp.ossc.nimbus.servlet.JMXConsoleServlet&lt;/servlet-class&gt;
 *     &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;
 * &lt;/servlet&gt;
 * 
 * &lt;servlet-mapping&gt;
 *     &lt;servlet-name&gt;JMXConsoleServlet&lt;/servlet-name&gt;
 *     &lt;url-pattern&gt;/jmx-console&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 * 
 * @author M.Takata
 */
public class JMXConsoleServlet extends HttpServlet{
    
    /**
     * MBeanServerConnectionFactoryのマネージャ名を指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_MBEAN_SERVER_CONNECTION_FACTORY_MANAGER_NAMES = "MBeanServerConnectionFactoryManagerNames";
    
    /**
     * MBeanServerConnectionFactoryのサービス名を指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_MBEAN_SERVER_CONNECTION_FACTORY_SERVICE_NAMES = "MBeanServerConnectionFactoryServiceNames";
    
    /**
     * 管理コンソールからの属性設定を有効にするかどうかを指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_ATTR_SET_ENABLED = "AttributeSetEnabled";
    
    /**
     * 管理コンソールに属性の値を表示する際の表示最大長を指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_ATTR_MAX_LENGTH = "AttributeMaxLength";
    
    /**
     * 管理コンソールからの操作呼び出しを有効にするかどうかを指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_OPERATION_ENABLED = "OperationEnabled";
    
    /**
     * JSONコンバータのサービス名を指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_JSON_CONVERTER_SERVICE_NAME = "JSONConverterServiceName";
    
    /**
     * JSON応答時に２バイト文字をユニコードエスケープするかどうかのフラグを指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_UNICODE_ESCAPE = "UnicodeEscape";
    
    private static final String ATTRIBUTE_READ_ONLY = "r";
    private static final String ATTRIBUTE_WRITE_ONLY = "w";
    private static final String ATTRIBUTE_READ_AND_WRITE = "rw";
    
    private Map mbeanServerConnectionFactories;
    private BeanJSONConverter jsonConverter;
    private StringStreamConverter toStringConverter;
    
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
        mbeanServerConnectionFactories = getMBeanServerConnectionFactories();
    }
    
    private Map getMBeanServerConnectionFactories(){
        final Map factories = new LinkedHashMap();
        
        final ServletConfig config = getServletConfig();
        final StringArrayEditor editor = new StringArrayEditor();
        final ServiceNameEditor nameEditor = new ServiceNameEditor();
        
        final String managerNamesStr = config.getInitParameter(INIT_PARAM_NAME_MBEAN_SERVER_CONNECTION_FACTORY_MANAGER_NAMES);
        if(managerNamesStr != null){
            editor.setAsText(managerNamesStr);
            String[] managerNames = (String[])editor.getValue();
            if(managerNames != null && managerNames.length != 0){
                for(int i = 0; i < managerNames.length; i++){
                    ServiceManager manager = ServiceManagerFactory.findManager(managerNames[i]);
                    if(manager == null){
                        continue;
                    }
                    final Iterator serviceNames = manager.serviceNameSet().iterator();
                    while(serviceNames.hasNext()){
                        String serviceName = (String)serviceNames.next();
                        Service service = manager.getService(serviceName);
                        if(service instanceof MBeanServerConnectionFactory){
                            factories.put(
                                service.getServiceNameObject(),
                                service
                            );
                        }
                    }
                }
            }
        }
        
        final String serviceNamesStr = config.getInitParameter(INIT_PARAM_NAME_MBEAN_SERVER_CONNECTION_FACTORY_SERVICE_NAMES);
        if(serviceNamesStr != null){
            editor.setAsText(serviceNamesStr);
            String[] serviceNames = (String[])editor.getValue();
            if(serviceNames != null && serviceNames.length != 0){
                for(int i = 0; i < serviceNames.length; i++){
                    nameEditor.setAsText(serviceNames[i]);
                    ServiceName name = (ServiceName)nameEditor.getValue();
                    factories.put(
                        name,
                        (MBeanServerConnectionFactory)ServiceManagerFactory.getServiceObject(name)
                    );
                }
            }
        }
        return factories;
    }
    
    private boolean isAttributeSetEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_ATTR_SET_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private int getAttributeMaxLength(){
        final ServletConfig config = getServletConfig();
        final String maxLengthStr = config.getInitParameter(INIT_PARAM_NAME_ATTR_MAX_LENGTH);
        int maxLength = -1;
        if(maxLengthStr != null && maxLengthStr.length() != 0){
            try{
                maxLength = Integer.parseInt(maxLengthStr);
            }catch(NumberFormatException e){
            }
        }
        return maxLength;
    }
    
    private boolean isOperationEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_OPERATION_ENABLED);
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
        }else if(action.equals("server")){
            processServerResponse(req, resp, responseType);
        }else if(action.equals("domain")){
            processDomainResponse(req, resp, responseType);
        }else if(action.equals("mbean")){
            processMBeanResponse(req, resp, responseType);
        }else if(action.equals("set")){
            if(!isAttributeSetEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processSetAttributeResponse(req, resp, responseType);
        }else if(action.equals("get")){
            processGetAttributeResponse(req, resp, responseType);
        }else if(action.equals("operation")){
            if(!isOperationEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processOperationResponse(req, resp, responseType);
        }else{
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private String getCurrentPath(HttpServletRequest req){
        return req.getContextPath() + req.getServletPath();
    }
    
    /**
     * 管理コンソールのトップ画面リクエスト処理を行う。<p>
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
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            final String[] serverNames = new String[mbeanServerConnectionFactories.size() + 1];
            serverNames[0] = "localhost";
            final Iterator serviceNames = mbeanServerConnectionFactories.keySet().iterator();
            int i = 1;
            while(serviceNames.hasNext()){
                serverNames[i++] = serviceNames.next().toString();
            }
            Arrays.sort(serverNames);
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(serverNames))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>JMX Server List</title></head>");
            buf.append("<body>");
            
            buf.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\" width=\"90%\">");
            buf.append("<tr bgcolor=\"#cccccc\"><th>group</th><th>server</th></tr>");
            final StringBuffer url = new StringBuffer();
            buf.append("<tr>");
            buf.append("<td>localhost</td>");
            url.append(getCurrentPath(req))
               .append("?action=server&name=localhost");
            buf.append("<td><a href=\"")
               .append(resp.encodeURL(url.toString()))
               .append("\">localhost</a></td>");
            buf.append("</tr>");
            
            Iterator serviceNames = mbeanServerConnectionFactories.keySet().iterator();
            final Map groupList = new TreeMap();
            while(serviceNames.hasNext()){
                ServiceName serviceName = (ServiceName)serviceNames.next();
                Set serverList = (Set)groupList.get(serviceName.getServiceManagerName());
                if(serverList == null){
                    serverList = new TreeSet();
                    groupList.put(serviceName.getServiceManagerName(), serverList);
                }
                serverList.add(serviceName);
            }
            final Iterator entries = groupList.entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                String group = (String)entry.getKey();
                Set serverList = (Set)entry.getValue();
                serviceNames = serverList.iterator();
                boolean isFirst = true;
                while(serviceNames.hasNext()){
                    ServiceName serviceName = (ServiceName)serviceNames.next();
                    buf.append("<tr>");
                    if(isFirst){
                        if(serverList.size() > 1){
                            buf.append("<td rowspan=\"").append(serverList.size()).append("\">");
                        }else{
                            buf.append("<td>");
                        }
                        buf.append(group).append("</td>");
                        isFirst = false;
                    }
                    url.setLength(0);
                    url.append(getCurrentPath(req))
                       .append("?action=server&name=")
                       .append(URLEncoder.encode(serviceName.toString(), "UTF-8"));
                    buf.append("<td><a href=\"")
                       .append(resp.encodeURL(url.toString()))
                       .append("\">");
                    buf.append(serviceName.getServiceName()).append("</a></td>");
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
     * MBeanサーバのドメイン一覧画面リクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processServerResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String serverName = req.getParameter("name");
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            List domainNameList = new ArrayList();
            if(serverName == null || "localhost".equals(serverName)){
                if(serverName == null){
                    serverName = "localhost";
                }
                final List mbeanServers = MBeanServerFactory.findMBeanServer(null);
                if(mbeanServers == null || mbeanServers.size() == 0){
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                for(int i = 0; i < mbeanServers.size(); i++){
                    MBeanServer mbeanServer = (MBeanServer)mbeanServers.get(i);
                    String[] domains = mbeanServer.getDomains();
                    if(domains != null && domains.length != 0){
                        List domainNameSubList = new ArrayList();
                        for(int j = 0; j < domains.length; j++){
                            domainNameSubList.add(domains[j]);
                        }
                        Collections.sort(domainNameSubList);
                        domainNameList.add(domainNameSubList);
                    }
                }
            }else{
                ServiceNameEditor editor = new ServiceNameEditor();
                editor.setAsText(serverName);
                ServiceName serviceName = (ServiceName)editor.getValue();
                MBeanServerConnectionFactory factory = (MBeanServerConnectionFactory)mbeanServerConnectionFactories.get(serviceName);
                if(factory == null){
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                JMXConnector connector = null;
                try{
                    connector = factory.getJMXConnector();
                    connector.connect();
                    MBeanServerConnection mbeanServer = connector.getMBeanServerConnection();
                    String[] domains = mbeanServer.getDomains();
                    if(domains != null && domains.length != 0){
                        List domainNameSubList = new ArrayList();
                        for(int i = 0; i < domains.length; i++){
                            domainNameList.add(domains[i]);
                        }
                    }
                    Collections.sort(domainNameList);
                }catch(MBeanServerConnectionFactoryException e){
                    throw new ServletException(e);
                }finally{
                    if(connector != null){
                        connector.close();
                    }
                }
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(domainNameList))
            );
        }else{
            if(serverName == null || "localhost".equals(serverName)){
                if(serverName == null){
                    serverName = "localhost";
                }
                final List mbeanServers = MBeanServerFactory.findMBeanServer(null);
                if(mbeanServers == null || mbeanServers.size() == 0){
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                
                resp.setContentType("text/html;charset=UTF-8");
                buf.append("<html>");
                buf.append("<head><title>JMX Server localhost</title></head>");
                buf.append("<body>");
                
                buf.append("<a href=\"").append(getCurrentPath(req)).append("\">JMX Server List</a>");
                buf.append("<hr>");
                
                buf.append("<b>JMX Server name : </b>localhost<p>");
                
                buf.append("<b>Domains</b><br>");
                buf.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\" width=\"90%\">");
                buf.append("<tr bgcolor=\"#cccccc\"><th>index</th><th>domain</th></tr>");
                
                for(int i = 0; i < mbeanServers.size(); i++){
                    MBeanServer mbeanServer = (MBeanServer)mbeanServers.get(i);
                    String[] domains = mbeanServer.getDomains();
                    if(domains != null && domains.length != 0){
                        Arrays.sort(domains);
                        final StringBuffer url = new StringBuffer();
                        for(int j = 0; j < domains.length; j++){
                            buf.append("<tr>");
                            if(j == 0){
                                if(domains.length > 1){
                                    buf.append("<td rowspan=\"").append(domains.length).append("\">");
                                }else{
                                    buf.append("<td>");
                                }
                                buf.append(i + 1).append("</td>");
                            }
                            url.setLength(0);
                            url.append(getCurrentPath(req))
                               .append("?action=domain&index=").append(i + 1)
                               .append("&serverName=localhost")
                               .append("&name=")
                               .append(URLEncoder.encode(domains[j], "UTF-8"));
                            buf.append("<td><a href=\"")
                               .append(resp.encodeURL(url.toString()))
                               .append("\">");
                            buf.append(domains[j]).append("</a></td>");
                            buf.append("</tr>");
                        }
                    }
                }
                
                buf.append("</table>");
            }else{
                ServiceNameEditor editor = new ServiceNameEditor();
                editor.setAsText(serverName);
                ServiceName serviceName = (ServiceName)editor.getValue();
                MBeanServerConnectionFactory factory = (MBeanServerConnectionFactory)mbeanServerConnectionFactories.get(serviceName);
                if(factory == null){
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                
                resp.setContentType("text/html;charset=UTF-8");
                buf.append("<html>");
                buf.append("<head><title>JMX Server ").append(serverName).append("</title></head>");
                buf.append("<body>");
                
                buf.append("<a href=\"").append(getCurrentPath(req)).append("\">JMX Server List</a>");
                buf.append("<hr>");
                
                buf.append("<b>JMX Server name : </b>").append(serverName).append("<p>");
                
                buf.append("<b>Domains</b><br>");
                buf.append("<ul>");
                
                JMXConnector connector = null;
                try{
                    connector = factory.getJMXConnector();
                    connector.connect();
                    MBeanServerConnection mbeanServer = connector.getMBeanServerConnection();
                    String[] domains = mbeanServer.getDomains();
                    if(domains != null && domains.length != 0){
                        Arrays.sort(domains);
                        final StringBuffer url = new StringBuffer();
                        for(int i = 0; i < domains.length; i++){
                            url.setLength(0);
                            url.append(getCurrentPath(req))
                               .append("?action=domain&serverName=")
                               .append(URLEncoder.encode(serverName, "UTF-8"))
                               .append("&name=")
                               .append(URLEncoder.encode(domains[i], "UTF-8"));
                            buf.append("<li>");
                            buf.append("<a href=\"")
                               .append(resp.encodeURL(url.toString()))
                               .append("\">");
                            buf.append(domains[i]).append("</a>");
                            buf.append("</li>");
                        }
                    }
                }catch(MBeanServerConnectionFactoryException e){
                    throw new ServletException(e);
                }finally{
                    if(connector != null){
                        connector.close();
                    }
                }
                
                buf.append("</ul>");
            }
            
            buf.append("<hr>");
            buf.append("<a href=\"").append(getCurrentPath(req)).append("\">JMX Server List</a>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * ドメイン内のMBean一覧画面リクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processDomainResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String serverName = req.getParameter("serverName");
        final String indexStr = req.getParameter("index");
        int index = 1;
        if(indexStr != null){
            index = Integer.parseInt(indexStr);
        }
        final String domainName = req.getParameter("name");
        List objectNameList = new ArrayList();
        if(serverName == null || "localhost".equals(serverName)){
            if(serverName == null){
                serverName = "localhost";
            }
            final List mbeanServers = MBeanServerFactory.findMBeanServer(null);
            if(mbeanServers == null || mbeanServers.size() < index){
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            MBeanServer mbeanServer = (MBeanServer)mbeanServers.get(index - 1);
            ObjectName objectName = null;
            try{
                objectName = new ObjectName(domainName + ":*");
            }catch(MalformedObjectNameException e){
                throw new ServletException(e);
            }
            Set objectNameSet = mbeanServer.queryNames(objectName, null);
            if(objectNameSet != null && objectNameSet.size() != 0){
                Object[] objectNames = objectNameSet.toArray();
                Arrays.sort(objectNames);
                for(int j = 0; j < objectNames.length; j++){
                    objectNameList.add(objectNames[j].toString());
                }
            }
        }else{
            ServiceNameEditor editor = new ServiceNameEditor();
            editor.setAsText(serverName);
            ServiceName serviceName = (ServiceName)editor.getValue();
            MBeanServerConnectionFactory factory = (MBeanServerConnectionFactory)mbeanServerConnectionFactories.get(serviceName);
            if(factory == null){
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            JMXConnector connector = null;
            try{
                connector = factory.getJMXConnector();
                connector.connect();
                MBeanServerConnection mbeanServer = connector.getMBeanServerConnection();
                Set objectNameSet = mbeanServer.queryNames(new ObjectName(domainName + ":*"), null);
                if(objectNameSet != null && objectNameSet.size() != 0){
                    Object[] objectNames = objectNameSet.toArray();
                    Arrays.sort(objectNames);
                    for(int j = 0; j < objectNames.length; j++){
                        objectNameList.add(objectNames[j].toString());
                    }
                }
            }catch(MBeanServerConnectionFactoryException e){
                throw new ServletException(e);
            }catch(MalformedObjectNameException e){
                throw new ServletException(e);
            }finally{
                if(connector != null){
                    connector.close();
                }
            }
        }
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(objectNameList))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>JMX Domain ").append(domainName).append("</title></head>");
            buf.append("<body>");
            
            buf.append("<a href=\"")
               .append(getCurrentPath(req))
               .append("?action=server&name=")
               .append(URLEncoder.encode(serverName, "UTF-8"))
               .append("&index=").append(index)
               .append("\">JMX Server ").append(serverName).append("</a>");
            buf.append("<hr>");
            
            buf.append("<b>JMX Domain name : </b>").append(domainName).append("<p>");
            
            buf.append("<b>MBean List</b><br>");
            buf.append("<ul>");
            if(objectNameList.size() != 0){
                final StringBuffer url = new StringBuffer();
                for(int i = 0; i < objectNameList.size(); i++){
                    url.setLength(0);
                    url.append(getCurrentPath(req))
                       .append("?action=mbean&serverName=")
                       .append(URLEncoder.encode(serverName, "UTF-8"))
                       .append("&index=").append(index)
                       .append("&name=")
                       .append(URLEncoder.encode((String)objectNameList.get(i), "UTF-8"));
                    buf.append("<li>");
                    buf.append("<a href=\"")
                       .append(resp.encodeURL(url.toString()))
                       .append("\">");
                    buf.append(objectNameList.get(i)).append("</a>");
                    buf.append("</li>");
                }
            }
            buf.append("</ul>");
            
            buf.append("<hr>");
            buf.append("<a href=\"")
               .append(getCurrentPath(req))
               .append("?action=server&name=")
               .append(URLEncoder.encode(serverName, "UTF-8"))
               .append("&index=").append(index)
               .append("\">JMX Server ").append(serverName).append("</a>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * MBean画面リクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processMBeanResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String serverName = req.getParameter("serverName");
        final String indexStr = req.getParameter("index");
        int index = 1;
        if(indexStr != null){
            index = Integer.parseInt(indexStr);
        }
        final String objectNameStr = req.getParameter("name");
        ObjectName objectName = null;
        try{
            objectName = new ObjectName(objectNameStr);
        }catch(MalformedObjectNameException e){
            throw new ServletException(e);
        }
        JMXConnector connector = null;
        final StringBuffer buf = new StringBuffer();
        try{
            MBeanServerConnection mbeanServer = null;
            if(serverName == null || "localhost".equals(serverName)){
                if(serverName == null){
                    serverName = "localhost";
                }
                final List mbeanServers = MBeanServerFactory.findMBeanServer(null);
                if(mbeanServers == null || mbeanServers.size() < index){
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                mbeanServer = (MBeanServer)mbeanServers.get(index - 1);
            }else{
                ServiceNameEditor editor = new ServiceNameEditor();
                editor.setAsText(serverName);
                ServiceName serviceName = (ServiceName)editor.getValue();
                MBeanServerConnectionFactory factory = (MBeanServerConnectionFactory)mbeanServerConnectionFactories.get(serviceName);
                if(factory == null){
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                try{
                    connector = factory.getJMXConnector();
                    connector.connect();
                    mbeanServer = connector.getMBeanServerConnection();
                }catch(MBeanServerConnectionFactoryException e){
                    throw new ServletException(e);
                }
            }
            MBeanInfo mbeanInfo = null;
            try{
                mbeanInfo = mbeanServer.getMBeanInfo(objectName);
            }catch(InstanceNotFoundException e){
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }catch(IntrospectionException e){
                throw new ServletException(e);
            }catch(ReflectionException e){
                throw new ServletException(e);
            }
            
            if("json".equals(responseType)){
                resp.setContentType("application/json;charset=UTF-8");
                buf.append(
                    toStringConverter.convertToObject(jsonConverter.convertToStream(mbeanInfo))
                );
            }else{
                resp.setContentType("text/html;charset=UTF-8");
                buf.append("<html>");
                buf.append("<head><title>MBean ").append(objectNameStr).append("</title></head>");
                buf.append("<body>");
                
                buf.append("<a href=\"")
                   .append(getCurrentPath(req))
                   .append("?action=domain&serverName=")
                   .append(URLEncoder.encode(serverName, "UTF-8"))
                   .append("&index=").append(index)
                   .append("&name=")
                   .append(URLEncoder.encode(objectName.getDomain(), "UTF-8"))
                   .append("\">JMX Domain ").append(objectName.getDomain()).append("</a>");
                buf.append("<hr>");
                
                buf.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\" width=\"90%\">");
                buf.append("<tr><th bgcolor=\"#cccccc\">Object name</th><td>").append(objectName).append("</td></tr>");
                buf.append("<tr><th bgcolor=\"#cccccc\">Class name</th><td>").append(mbeanInfo.getClassName()).append("</td></tr>");
                String description = mbeanInfo.getDescription();
                buf.append("<tr><th bgcolor=\"#cccccc\">Description</th><td>").append(description == null ? "-" : description).append("</td></tr>");
                buf.append("</table>");
                buf.append("<p>");
                
                buf.append("<b>Attributes</b><br>");
                buf.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\" width=\"90%\">");
                buf.append("<tr bgcolor=\"#cccccc\"><th>name</th><th>description</th><th>r/w</th><th>type</th><th>value</th><th>apply</th></tr>");
                final int maxLength = getAttributeMaxLength();
                final MBeanAttributeInfo[] attributes = mbeanInfo.getAttributes();
                Map attrInfoMap = new TreeMap();
                Map attrInfoMapForNoData = new HashMap();
                Map attrMap = new HashMap();
                if(attributes != null && attributes.length != 0){
                    for(int i = 0; i < attributes.length; i++){
                        Class type = null;
                        try{
                            type = Utility.convertStringToClass(attributes[i].getType());
                        }catch(ClassNotFoundException e){
                            attrInfoMapForNoData.put(attributes[i].getName(), attributes[i]);
                            continue;
                        }
                        attrInfoMap.put(attributes[i].getName(), attributes[i]);
                    }
                    List attrList = null;
                    String[] attrNames = (String[])attrInfoMap.keySet().toArray(new String[attrInfoMap.size()]);
                    try{
                        attrList = mbeanServer.getAttributes(objectName, attrNames);
                        for(int i = 0; i < attrList.size(); i++){
                            Attribute attr = (Attribute)attrList.get(i);
                            attrMap.put(attr.getName(), attr);
                        }
                    }catch(InstanceNotFoundException e){
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }catch(ReflectionException e){
                        throw new ServletException(e);
                    }catch(java.rmi.UnmarshalException e){
                        for(int i = 0; i < attrNames.length; i++){
                            Object val = null;
                            try{
                                val = mbeanServer.getAttribute(objectName, attrNames[i]);
                            }catch(InstanceNotFoundException e2){
                                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                                return;
                            }catch(AttributeNotFoundException e2){
                                throw new ServletException(e2);
                            }catch(MBeanException e2){
                                throw new ServletException(e2);
                            }catch(ReflectionException e2){
                                throw new ServletException(e2);
                            }catch(java.rmi.UnmarshalException e2){
                                final StringWriter sw = new StringWriter();
                                final PrintWriter writer = new PrintWriter(sw);
                                e2.printStackTrace(writer);
                                val = sw.toString();
                            }
                            attrMap.put(attrNames[i], new Attribute(attrNames[i], val));
                        }
                    }
                    attrInfoMap.putAll(attrInfoMapForNoData);
                }
                Iterator attrs = attrInfoMap.values().iterator();
                while(attrs.hasNext()){
                    MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo)attrs.next();
                    Attribute attribute = (Attribute)attrMap.get(attributeInfo.getName());
                    buf.append("<form name=\"").append(attributeInfo.getName())
                        .append("\" action=\"").append(getCurrentPath(req)).append("\" method=\"POST\">");
                    buf.append("<input type=\"hidden\" name=\"action\" value=\"set\">");
                    buf.append("<input type=\"hidden\" name=\"serverName\" value=\"").append(serverName).append("\">");
                    buf.append("<input type=\"hidden\" name=\"index\" value=\"").append(index).append("\">");
                    buf.append("<input type=\"hidden\" name=\"name\" value=\"").append(objectNameStr).append("\">");
                    if(attributeInfo.isWritable()){
                        buf.append("<input type=\"hidden\" name=\"attribute\" value=\"").append(attributeInfo.getName()).append("\">");
                        buf.append("<input type=\"hidden\" name=\"attributeType\" value=\"").append(attributeInfo.getType()).append("\">");
                    }
                    
                    buf.append("<tr>");
                    buf.append("<td>").append(attributeInfo.getName()).append("</td>");
                    description = attributeInfo.getDescription();
                    buf.append("<td>").append(description == null ? "-" : description).append("</td>");
                    buf.append("<td>").append(getAttributeType(attributeInfo)).append("</td>");
                    buf.append("<td>").append(attributeInfo.getType()).append("</td>");
                    buf.append("<td>");
                    if(attribute == null && attrInfoMapForNoData.containsKey(attributeInfo.getName())){
                        buf.append("Not supported attribute");
                    }else if(!attributeInfo.isReadable() && attributeInfo.isWritable()){
                        buf.append("<textarea name=\"value\" cols=\"40\" rows=\"2\">").append("</textarea>");
                    }else{
                        buf.append(getAttributeValue(attributeInfo, attribute == null ? null : attribute.getValue(), true));
                    }
                    buf.append("</td>");
                    buf.append("<td>");
                    if(attributeInfo.isWritable()){
                        buf.append("<input type=\"submit\" value=\"apply\">");
                    }else{
                        buf.append("　");
                    }
                    buf.append("</td>");
                    buf.append("</tr>");
                    buf.append("</form>");
                }
                buf.append("</table>");
                buf.append("<p>");
                
                MBeanOperationInfo[] operations = mbeanInfo.getOperations();
                buf.append("<b>Operations</b><br>");
                buf.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\" width=\"90%\">");
                buf.append("<tr bgcolor=\"#cccccc\"><th rowspan=\"2\">method</th><th colspan=\"2\">arguments</th><th rowspan=\"2\">call</th></tr>");
                buf.append("<tr bgcolor=\"#cccccc\"><th>value</th><th>type</th></tr>");
                StringBuffer signature = new StringBuffer();
                for(int i = 0; i < operations.length; i++){
                    MBeanOperationInfo operation = operations[i];
                    MBeanParameterInfo[] paramInfos = operation.getSignature();
                    signature.setLength(0);
                    signature.append(operation.getName()).append('(');
                    if(paramInfos != null && paramInfos.length != 0){
                        for(int j = 0; j < paramInfos.length; j++){
                            if(j != 0){
                                signature.append(',');
                            }
                            signature.append(paramInfos[j].getType());
                        }
                    }
                    signature.append(')');
                    buf.append("<form name=\"").append(signature)
                        .append("\" action=\"").append(getCurrentPath(req)).append("\" method=\"POST\">");
                    buf.append("<input type=\"hidden\" name=\"action\" value=\"operation\">");
                    buf.append("<input type=\"hidden\" name=\"serverName\" value=\"").append(serverName).append("\">");
                    buf.append("<input type=\"hidden\" name=\"index\" value=\"").append(index).append("\">");
                    buf.append("<input type=\"hidden\" name=\"name\" value=\"").append(objectNameStr).append("\">");
                    buf.append("<input type=\"hidden\" name=\"operation\" value=\"").append(signature).append("\">");
                    
                    buf.append("<tr>");
                    buf.append("<td>").append(signature).append("</td>");
                    buf.append("<td>");
                    if(paramInfos == null || paramInfos.length == 0){
                        buf.append("　");
                    }else{
                        for(int j = 0, max = paramInfos.length; j < max; j++){
                            buf.append(paramInfos[j].getName())
                                .append(":<textarea name=\"args\" cols=\"40\" rows=\"2\"></textarea>");
                            if(j != max - 1){
                                buf.append("<br>");
                            }
                        }
                    }
                    buf.append("</td>");
                    buf.append("<td>");
                    if(paramInfos == null || paramInfos.length == 0){
                        buf.append("　");
                    }else{
                        for(int j = 0, max = paramInfos.length; j < max; j++){
                            buf.append("<input type=\"text\" name=\"argTypes\">");
                            if(j != max - 1){
                                buf.append("<br>");
                            }
                        }
                    }
                    buf.append("</td>");
                    buf.append("<td>").append("<input type=\"submit\" value=\"call\">")
                        .append("</td>");
                    buf.append("</tr>");
                    buf.append("</form>");
                }
                buf.append("</table>");
                
                buf.append("<hr>");
                buf.append("<a href=\"")
                   .append(getCurrentPath(req))
                   .append("?action=domain&serverName=")
                   .append(URLEncoder.encode(serverName, "UTF-8"))
                   .append("&index=").append(index)
                   .append("&name=")
                   .append(URLEncoder.encode(objectName.getDomain(), "UTF-8"))
                   .append("\">JMX Domain ").append(objectName.getDomain()).append("</a>");
                
                buf.append("</body>");
                buf.append("</html>");
            }
        }finally{
            if(connector != null){
                connector.close();
            }
        }
        resp.getWriter().println(buf.toString());
    }
    
    private Object getAttributeValue(MBeanServer server, ObjectName name, MBeanAttributeInfo attribute, boolean isHtml){
        Object val = null;
        try{
            val = server.getAttribute(name, attribute.getName());
        }catch(MBeanException e){
            e.printStackTrace();
            return "Can not get!!";
        }catch(AttributeNotFoundException e){
            e.printStackTrace();
            return "Can not get!!";
        }catch(InstanceNotFoundException e){
            e.printStackTrace();
            return "Can not get!!";
        }catch(ReflectionException e){
            e.printStackTrace();
            return "Can not get!!";
        }
        return getAttributeValue(attribute, val, isHtml);
    }
    
    private Object getAttributeValue(MBeanAttributeInfo attribute, Object val, boolean isHtml){
        Class type = null;
        try{
            type = Utility.convertStringToClass(attribute.getType());
        }catch(ClassNotFoundException e){
        }
        Object result = toAttributeValue(attribute, type, val, isHtml);
        if(result == null){
            return null;
        }
        return result;
    }
    
    private Object toAttributeValue(MBeanAttributeInfo attribute, Class type, Object val, boolean isHtml){
        return formatValue(attribute.isWritable(), type, val, isHtml);
    }
    private Object formatValue(boolean isWritable, Class type, Object val, boolean isHtml){
        if(val == null){
            if(isHtml){
                return new StringBuffer().append("<textarea name=\"value\" cols=\"40\" rows=\"2\"")
                    .append(!isWritable ? "readonly>" : ">")
                    .append(val).append("</textarea>").toString();
            }else{
                return null;
            }
        }
        
        if(val instanceof String){
            if(isHtml){
                String resultStr = val.toString();
                final int maxLength = getAttributeMaxLength();
                if(maxLength > 0 && resultStr.length() > maxLength){
                    resultStr = resultStr.substring(0, maxLength);
                }
                val = resultStr;
                return new StringBuffer().append("<textarea name=\"value\" cols=\"40\" rows=\"2\"")
                    .append(!isWritable ? "readonly>" : ">")
                    .append(val).append("</textarea>").toString();
            }else{
                return val.toString();
            }
        }else if(type.isArray()){
            Class componentType = type.getComponentType();
            if(CompositeData.class.isAssignableFrom(componentType)
                || TabularData.class.isAssignableFrom(componentType)
            ){
                if(isHtml){
                    StringBuffer buf = new StringBuffer();
                    for(int i = 0, imax = Array.getLength(val); i < imax; i++){
                        Object element = Array.get(val, i);
                        buf.append(formatValue(isWritable, type.getComponentType(), element, isHtml));
                    }
                    return buf.toString();
                }else{
                    List list = new ArrayList();
                    for(int i = 0, imax = Array.getLength(val); i < imax; i++){
                        Object element = Array.get(val, i);
                        list.add(formatValue(isWritable, type.getComponentType(), element, isHtml));
                    }
                    return list;
                }
            }else{
                if(isHtml){
                    final PropertyEditor editor = type == null ? null : NimbusPropertyEditorManager.findEditor(type);
                    if(editor != null){
                        editor.setValue(val);
                        String resultStr = editor.getAsText();
                        final int maxLength = getAttributeMaxLength();
                        if(maxLength > 0 && resultStr.length() > maxLength){
                            resultStr = resultStr.substring(0, maxLength);
                        }
                        val = resultStr;
                    }
                    return new StringBuffer().append("<textarea name=\"value\" cols=\"40\" rows=\"2\"")
                        .append(!isWritable ? "readonly>" : ">")
                        .append(val).append("</textarea>").toString();
                }else{
                    List list = new ArrayList();
                    for(int i = 0, imax = Array.getLength(val); i < imax; i++){
                        Object element = Array.get(val, i);
                        list.add(formatValue(isWritable, type.getComponentType(), element, isHtml));
                    }
                    return list;
                }
            }
        }else if(val instanceof CompositeData){
            CompositeData compositeData = (CompositeData)val;
            CompositeType compositeType = compositeData.getCompositeType();
            if(isHtml){
                StringBuffer buf = new StringBuffer();
                buf.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\" width=\"90%\">");
                final Iterator keys = compositeType.keySet().iterator();
                while(keys.hasNext()){
                    String key = (String)keys.next();
                    Object element = compositeData.get(key);
                    buf.append("<tr><th bgcolor=\"#cccccc\">").append(key).append("</th>");
                    final Class elementType = element == null ? null : element.getClass();
                    buf.append("<td>").append(formatValue(isWritable, elementType, element, isHtml)).append("</td></tr>");
                }
                buf.append("</table>");
                return buf.toString();
            }else{
                Map map = new LinkedHashMap();
                final Iterator keys = compositeType.keySet().iterator();
                while(keys.hasNext()){
                    String key = (String)keys.next();
                    Object element = compositeData.get(key);
                    final Class elementType = element == null ? null : element.getClass();
                    map.put(key, formatValue(isWritable, elementType, element, isHtml));
                }
                return map;
            }
        }else if(val instanceof TabularData){
            TabularData tabularData = (TabularData)val;
            TabularType tabularType = tabularData.getTabularType();
            if(isHtml){
                StringBuffer buf = new StringBuffer();
                buf.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\" width=\"90%\">");
                buf.append("<tr>");
                List colNames = tabularType.getIndexNames();
                for(int i = 0; i < colNames.size(); i++){
                    buf.append("<th bgcolor=\"#cccccc\">").append(colNames.get(i)).append("</th>");
                }
                buf.append("</tr>");
                CompositeType rowType = tabularType.getRowType();
                Iterator rows = tabularData.values().iterator();
                while(rows.hasNext()){
                    CompositeData compositeData = (CompositeData)rows.next();
                    buf.append("<tr>");
                    for(int i = 0; i < colNames.size(); i++){
                        Object element = compositeData.get((String)colNames.get(i));
                        final Class elementType = element == null ? null : element.getClass();
                        buf.append("<td>").append(formatValue(isWritable, elementType, element, isHtml)).append("</td>");
                    }
                    buf.append("</tr>");
                }
                buf.append("</table>");
                return buf.toString();
            }else{
                List records = new ArrayList();
                List colNames = tabularType.getIndexNames();
                CompositeType rowType = tabularType.getRowType();
                Iterator rows = tabularData.values().iterator();
                while(rows.hasNext()){
                    CompositeData compositeData = (CompositeData)rows.next();
                    Map record = new LinkedHashMap();
                    for(int i = 0; i < colNames.size(); i++){
                        Object element = compositeData.get((String)colNames.get(i));
                        final Class elementType = element == null ? null : element.getClass();
                        record.put((String)colNames.get(i), formatValue(isWritable, elementType, element, isHtml));
                    }
                    records.add(record);
                }
                return records;
            }
        }else{
            final PropertyEditor editor = type == null ? null : NimbusPropertyEditorManager.findEditor(type);
            if(editor != null){
                editor.setValue(val);
                val = editor.getAsText();
            }
            if(isHtml){
                String resultStr = val.toString();
                final int maxLength = getAttributeMaxLength();
                if(maxLength > 0 && resultStr.length() > maxLength){
                    resultStr = resultStr.substring(0, maxLength);
                }
                val = resultStr;
                return new StringBuffer().append("<textarea name=\"value\" cols=\"40\" rows=\"2\"")
                    .append(!isWritable ? "readonly>" : ">")
                    .append(val).append("</textarea>").toString();
            }else{
                return val.toString();
            }
        }
    }
    
    private String getAttributeType(MBeanAttributeInfo  attribute){
        if(attribute.isWritable() && attribute.isReadable()){
            return ATTRIBUTE_READ_AND_WRITE;
        }else if(attribute.isWritable()){
            return ATTRIBUTE_WRITE_ONLY;
        }else{
            return ATTRIBUTE_READ_ONLY;
        }
    }
    
    /**
     * MBean属性設定リクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processSetAttributeResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String serverName = req.getParameter("serverName");
        final String indexStr = req.getParameter("index");
        int index = 1;
        if(indexStr != null){
            index = Integer.parseInt(indexStr);
        }
        final String objectNameStr = req.getParameter("name");
        ObjectName objectName = null;
        try{
            objectName = new ObjectName(objectNameStr);
        }catch(MalformedObjectNameException e){
            throw new ServletException(e);
        }
        final String attributeName = req.getParameter("attribute");
        if(attributeName == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final String attributeType = req.getParameter("attributeType");
        if(attributeType == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final String attributeValue = req.getParameter("value");
        final StringBuffer buf = new StringBuffer();
        JMXConnector connector = null;
        try{
            MBeanServerConnection mbeanServer = null;
            if(serverName == null || "localhost".equals(serverName)){
                if(serverName == null){
                    serverName = "localhost";
                }
                final List mbeanServers = MBeanServerFactory.findMBeanServer(null);
                if(mbeanServers == null || mbeanServers.size() < index){
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                mbeanServer = (MBeanServer)mbeanServers.get(index - 1);
            }else{
                ServiceNameEditor editor = new ServiceNameEditor();
                editor.setAsText(serverName);
                ServiceName serviceName = (ServiceName)editor.getValue();
                MBeanServerConnectionFactory factory = (MBeanServerConnectionFactory)mbeanServerConnectionFactories.get(serviceName);
                if(factory == null){
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                try{
                    connector = factory.getJMXConnector();
                    connector.connect();
                    mbeanServer = connector.getMBeanServerConnection();
                }catch(MBeanServerConnectionFactoryException e){
                    throw new ServletException(e);
                }
            }
            String result = setAttributeValue(mbeanServer, objectName, attributeName, attributeType, attributeValue);
            if("json".equals(responseType)){
                resp.setContentType("application/json;charset=UTF-8");
                Map json = new HashMap();
                json.put("result", result);
                buf.append(
                    toStringConverter.convertToObject(jsonConverter.convertToStream(json))
                );
            }else{
                resp.setContentType("text/html;charset=UTF-8");
                buf.append("<html>");
                buf.append("<head><title>JMX Set Attribute</title></head>");
                buf.append("<body>");
                
                buf.append("<a href=\"")
                   .append(getCurrentPath(req))
                   .append("?action=mbean&serverName=")
                   .append(URLEncoder.encode(serverName, "UTF-8"))
                   .append("&index=").append(index)
                   .append("&name=")
                   .append(URLEncoder.encode(objectNameStr, "UTF-8"))
                   .append("\">MBean ").append(objectNameStr).append("</a>");
                buf.append("<hr>");
                
                buf.append("<pre>").append(result).append("</pre>");
                
                buf.append("<hr>");
                buf.append("<a href=\"")
                   .append(getCurrentPath(req))
                   .append("?action=mbean&serverName=")
                   .append(URLEncoder.encode(serverName, "UTF-8"))
                   .append("&index=").append(index)
                   .append("&name=")
                   .append(URLEncoder.encode(objectNameStr, "UTF-8"))
                   .append("\">MBean ").append(objectNameStr).append("</a>");
                
                buf.append("</body>");
                buf.append("</html>");
            }
        }finally{
            if(connector != null){
                connector.close();
            }
        }
        resp.getWriter().println(buf.toString());
    }
    
    private String setAttributeValue(
        MBeanServerConnection mbeanServer,
        ObjectName objectName,
        String attributeName,
        String attributeType,
        String value
    ){
        try{
            Class type = Utility.convertStringToClass(attributeType);
            final PropertyEditor editor = NimbusPropertyEditorManager.findEditor(type);
            Object attributeValue = null;
            if(editor == null){
                return "Failed!! PropertyEditor " + type + " not found.";
            }else if(value == null || value.equals("null")){
                attributeValue = null;
            }else{
                editor.setAsText(value);
                attributeValue = editor.getValue();
            }
            mbeanServer.setAttribute(objectName, new Attribute(attributeName, attributeValue));
        }catch(Exception e){
            final StringWriter sw = new StringWriter();
            final PrintWriter writer = new PrintWriter(sw);
            e.printStackTrace(writer);
            return sw.toString();
        }
        return "Success!!";
    }
    
    /**
     * MBean属性取得リクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processGetAttributeResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String serverName = req.getParameter("serverName");
        final String indexStr = req.getParameter("index");
        int index = 1;
        if(indexStr != null){
            index = Integer.parseInt(indexStr);
        }
        final String objectNameStr = req.getParameter("name");
        ObjectName objectName = null;
        try{
            objectName = new ObjectName(objectNameStr);
        }catch(MalformedObjectNameException e){
            throw new ServletException(e);
        }
        final String attributeName = req.getParameter("attribute");
        if(attributeName == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        JMXConnector connector = null;
        try{
            MBeanServerConnection mbeanServer = null;
            if(serverName == null || "localhost".equals(serverName)){
                if(serverName == null){
                    serverName = "localhost";
                }
                final List mbeanServers = MBeanServerFactory.findMBeanServer(null);
                if(mbeanServers == null || mbeanServers.size() < index){
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                mbeanServer = (MBeanServer)mbeanServers.get(index - 1);
            }else{
                ServiceNameEditor editor = new ServiceNameEditor();
                editor.setAsText(serverName);
                ServiceName serviceName = (ServiceName)editor.getValue();
                MBeanServerConnectionFactory factory = (MBeanServerConnectionFactory)mbeanServerConnectionFactories.get(serviceName);
                if(factory == null){
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                try{
                    connector = factory.getJMXConnector();
                    connector.connect();
                    mbeanServer = connector.getMBeanServerConnection();
                }catch(MBeanServerConnectionFactoryException e){
                    throw new ServletException(e);
                }
            }
            Object attribute = null;
            try{
                attribute = mbeanServer.getAttribute(objectName, attributeName);
            }catch(MBeanException e){
                throw new ServletException(e);
            }catch(AttributeNotFoundException e){
                throw new ServletException(e);
            }catch(InstanceNotFoundException e){
                throw new ServletException(e);
            }catch(ReflectionException e){
                throw new ServletException(e);
            }
            final StringBuffer buf = new StringBuffer();
            if("json".equals(responseType)){
                resp.setContentType("application/json;charset=UTF-8");
                Map json = new HashMap();
                json.put("value", attribute);
                buf.append(
                    toStringConverter.convertToObject(jsonConverter.convertToStream(json))
                );
                resp.getWriter().println(buf.toString());
            }else{
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }finally{
            if(connector != null){
                connector.close();
            }
        }
    }
    
    /**
     * MBean操作リクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processOperationResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String serverName = req.getParameter("serverName");
        final String indexStr = req.getParameter("index");
        int index = 1;
        if(indexStr != null){
            index = Integer.parseInt(indexStr);
        }
        final String objectNameStr = req.getParameter("name");
        ObjectName objectName = null;
        try{
            objectName = new ObjectName(objectNameStr);
        }catch(MalformedObjectNameException e){
            throw new ServletException(e);
        }
        final String operation = req.getParameter("operation");
        if(operation == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final int startIndex = operation.indexOf('(');
        String operationName = null;
        String[] signature = null;
        if(startIndex == -1){
            operationName = operation;
        }else{
            operationName = operation.substring(0, startIndex);
            if(operation.charAt(operation.length() - 1) != ')'){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            String params = operation.substring(startIndex + 1, operation.length() - 1).trim();
            if(params.length() != 0){
                signature = params.split(",");
                for(int i = 0; i < signature.length; i++){
                    signature[i] = signature[i].trim();
                }
            }
        }
        final String[] argsStr = req.getParameterValues("args");
        final String[] argTypesStr = req.getParameterValues("argTypes");
        if(signature != null
            && (argsStr == null || argsStr.length != signature.length
                || (argTypesStr != null && argTypesStr.length != signature.length))){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Object[] params = signature == null ? null : new Object[signature.length];
        if(params != null){
            for(int i = 0; i < signature.length; i++){
                Class editType = null;
                try{
                    editType = Utility.convertStringToClass(signature[i]);
                }catch(ClassNotFoundException e){
                    throw new ServletException(e);
                }
                if(argTypesStr != null && argTypesStr[i] != null && argTypesStr[i].length() != 0){
                    try{
                        editType = Utility.convertStringToClass(argTypesStr[i]);
                    }catch(ClassNotFoundException e){
                    }
                }
                if(argsStr[i] == null || argsStr[i].equals("null")){
                    params[i] = null;
                }else{
                    final PropertyEditor propEditor = NimbusPropertyEditorManager.findEditor(editType);
                    if(propEditor == null){
                        if(editType.equals(Object.class)){
                            params[i] = argsStr[i];
                        }else{
                            throw new IllegalArgumentException("PropertyEditor for " + editType + " not found.");
                        }
                    }else{
                        propEditor.setAsText(argsStr[i]);
                        params[i] = propEditor.getValue();
                    }
                }
            }
        }
        
        final StringBuffer buf = new StringBuffer();
        JMXConnector connector = null;
        try{
            MBeanServerConnection mbeanServer = null;
            if(serverName == null || "localhost".equals(serverName)){
                if(serverName == null){
                    serverName = "localhost";
                }
                final List mbeanServers = MBeanServerFactory.findMBeanServer(null);
                if(mbeanServers == null || mbeanServers.size() < index){
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                mbeanServer = (MBeanServer)mbeanServers.get(index - 1);
            }else{
                ServiceNameEditor editor = new ServiceNameEditor();
                editor.setAsText(serverName);
                ServiceName serviceName = (ServiceName)editor.getValue();
                MBeanServerConnectionFactory factory = (MBeanServerConnectionFactory)mbeanServerConnectionFactories.get(serviceName);
                if(factory == null){
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                try{
                    connector = factory.getJMXConnector();
                    connector.connect();
                    mbeanServer = connector.getMBeanServerConnection();
                }catch(MBeanServerConnectionFactoryException e){
                    throw new ServletException(e);
                }
            }
            Object result = null;
            Class returnType = Object.class;
            try{
                result = mbeanServer.invoke(objectName, operationName, params, signature);
                if(result != null){
                    returnType = result.getClass();
                }
            }catch(Exception e){
                final StringWriter sw = new StringWriter();
                final PrintWriter writer = new PrintWriter(sw);
                e.printStackTrace(writer);
                result = sw.toString();
            }
            if("json".equals(responseType)){
                resp.setContentType("application/json;charset=UTF-8");
                Map json = new HashMap();
                json.put("result", result);
                buf.append(
                    toStringConverter.convertToObject(jsonConverter.convertToStream(json))
                );
            }else{
                resp.setContentType("text/html;charset=UTF-8");
                buf.append("<html>");
                buf.append("<head><title>JMX Operation Result</title></head>");
                buf.append("<body>");
                
                buf.append("<a href=\"")
                   .append(getCurrentPath(req))
                   .append("?action=mbean&serverName=")
                   .append(URLEncoder.encode(serverName, "UTF-8"))
                   .append("&index=").append(index)
                   .append("&name=")
                   .append(URLEncoder.encode(objectNameStr, "UTF-8"))
                   .append("\">MBean ").append(objectNameStr).append("</a>");
                buf.append("<hr>");
                
                buf.append(formatValue(false, returnType, result, true));
                
                buf.append("<hr>");
                buf.append("<a href=\"")
                   .append(getCurrentPath(req))
                   .append("?action=mbean&serverName=")
                   .append(URLEncoder.encode(serverName, "UTF-8"))
                   .append("&index=").append(index)
                   .append("&name=")
                   .append(URLEncoder.encode(objectNameStr, "UTF-8"))
                   .append("\">MBean ").append(objectNameStr).append("</a>");
                
                buf.append("</body>");
                buf.append("</html>");
            }
        }finally{
            if(connector != null){
                connector.close();
            }
        }
        resp.getWriter().println(buf.toString());
    }
}