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
import java.beans.*;
import java.net.*;
import java.lang.reflect.*;
import javax.servlet.*;
import javax.servlet.http.*;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.core.ServiceLoader;
import jp.ossc.nimbus.util.ClassMappingTree;
import jp.ossc.nimbus.util.converter.*;

/**
 * ServiceManagerFactoryサーブレット。<p>
 * サーブレットコンテナ上でのサービスのロードをサポートするサーブレットである。<br>
 * また、JMXサーバが存在しない環境での、サービスの管理をサポートするために、
 * HTTP経由でのサービスの管理をサポートする管理コンソール及びWebサービスを提供する。<br>
 * このサーブレットには、以下の初期化パラメータがある。<br>
 * <table border="1" width="90%">
 *     <tr bgcolor="#cccccc"><th>#</th><th>パラメータ名</th><th>値の説明</th><th>デフォルト</th></tr>
 *     <tr><td>1</td><td>ServicePaths</td><td>ロードするサービス定義ファイルのパスをカンマ区切りで指定する。<br>パスは、絶対パス、相対パス、クラスパスが指定できる。<br>ここで指定されたサービス定義は、サーブレットの初期化時に指定された順番にロードされ、サーブレットの破棄時に指定された順と逆順でアンロードされる。</td><td></td></tr>
 *     <tr><td>2</td><td>ServiceDirs</td><td>ロードするサービス定義ファイルがあるディレクトリのパスをカンマ区切りで指定する。<br>パスは、絶対パス、相対パスが指定できる。<br>ここで指定されたディレクトリは、サーブレットの初期化時に指定された順番にロードされ、サーブレットの破棄時に指定された順と逆順でアンロードされる。<br>また、ServicePathsよりも先に処理される。</td><td></td></tr>
 *     <tr><td>3</td><td>ServiceFileFilter</td><td>ServiceDirsと共に指定することで、ServiceDirsで指定したディレクトリのサービス定義ファイルのファイル名を正規表現で絞り込む。<br>フィルターの指定方法は、{@link jp.ossc.nimbus.io.RecurciveSearchFile#listAllTreeFiles(String, int)}を参照。</td><td></td></tr>
 *     <tr><td>4</td><td>CheckLoadManagerCompleted</td><td>サービス定義のロード完了チェックを行うかどうかを指定する。<br>チェックを行う場合は、trueを指定する。</td><td>false</td></tr>
 *     <tr><td>5</td><td>CheckLoadManagerCompletedBy</td><td>サービス定義のロード完了チェックをServiceManager単位で行いたい場合に、ServiceManagerの名前を指定する。<br>初期化パラメータCheckLoadManagerCompletedがtrueの場合だけ、有効である。</td><td></td></tr>
 *     <tr><td>6</td><td>Validate</td><td>サービス定義の検証を行うかどうかを指定する。<br>検証を行う場合は、trueを指定する。</td><td>false</td></tr>
 *     <tr><td>7</td><td>ConsoleEnabled</td><td>このサーブレットが提供する管理コンソール及びWebサービスを有効にするかどうかを指定する。<br>有効にする場合は、trueを指定する。</td><td>false</td></tr>
 *     <tr><td>8</td><td>AttributeSetEnabled</td><td>このサーブレットが提供する管理コンソール及びWebサービスで、サービスの属性を変更する機能を有効にするかどうかを指定する。<br>有効にする場合は、trueを指定する。</td><td>false</td></tr>
 *     <tr><td>9</td><td>AttributeMaxLength</td><td>このサーブレットが提供する管理コンソールで、サービスの属性を表示する場合に表示する属性値の最大長を指定する。</td><td>制限なし</td></tr>
 *     <tr><td>10</td><td>MethodCallEnabled</td><td>このサーブレットが提供する管理コンソール及びWebサービスで、サービスの操作を実行する機能を有効にするかどうかを指定する。<br>有効にする場合は、trueを指定する。</td><td>false</td></tr>
 *     <tr><td>11</td><td>IgnoreMethods</td><td>このサーブレットが提供する管理コンソール及びWebサービスで、無効にしたいサービスのメソッドを指定する。</td><td>false</td></tr>
 *     <tr><td>12</td><td>JSONConverterServiceName</td><td>JSON形式での応答を要求する場合に使用する{@link BeanJSONConverter}サービスのサービス名を指定する。</td><td>指定しない場合は、内部生成される。</td></tr>
 *     <tr><td>13</td><td>UnicodeEscape</td><td>JSON形式での応答を要求する場合に、２バイト文字をユニコードエスケープするかどうかを指定する。</td><td>true</td></tr>
 * </table>
 * <p>
 * Webサービスは、クエリ指定でのGETリクエストに対して、JSONでデータを応答する。<br>
 * <table border="1" width="90%">
 *     <tr bgcolor="#cccccc"><th rowspan="2">#</th><th rowspan="2">アクション</th><th colspan="2">クエリパラメータ</th><th rowspan="2">応答JSONの例</th></tr>
 *     <tr bgcolor="#cccccc"><th>パラメータ名</th><th>値</th></tr>
 *     <tr><td>1</td><td><nobr>サービスマネージャー名の一覧取得</nobr></td><td>responseType</td><td>json</td><td><code>["Manager1","Manager2"]</code></td></tr>
 *     <tr><td rowspan="3">2</td><td rowspan="3"><nobr>サービス名の一覧取得</nobr></td><td>responseType</td><td>json</td><td rowspan="3"><code>["Manager1%23Service1","Manager1%23Service2"]</code></td></tr>
 *     <tr><td>action</td><td>manager</td></tr>
 *     <tr><td>name</td><td>絞り込み対象のサービスマネージャー名。指定しない場合は、全てのサービスマネージャが対象となる。</td></tr>
 *     <tr><td rowspan="4">3</td><td rowspan="4"><nobr>サービスの属性及び操作の一覧取得</nobr></td><td>responseType</td><td>json</td><td rowspan="4">
 *     <code><pre>
 *{
 *    "className":"sample.service.POJOService",
 *    "attributes":[
 *        {
 *            "name":"Message",
 *            "accessType":"rw",
 *            "type":"java.lang.String",
 *            "value":null
 *        }
 *    ],
 *    "operations":["displayMessage()"]
 *}
 *     </pre></code></td></tr>
 *     <tr><td>action</td><td>service</td></tr>
 *     <tr><td>name</td><td>サービス名</td></tr>
 *     <tr><td>getAttribute</td><td>属性の値を取得して結果に含めるかどうかのフラグ。指定しない場合、値は取得しない。</td></tr>
 *     <tr><td rowspan="4">4</td><td rowspan="4"><nobr>サービスの属性値取得</nobr></td><td>responseType</td><td>json</td><td rowspan="4"><code>{"value":null}</code></td></tr>
 *     <tr><td>action</td><td>get</td></tr>
 *     <tr><td>name</td><td>サービス名</td></tr>
 *     <tr><td>attribute</td><td>属性名</td></tr>
 *     <tr><td rowspan="5">5</td><td rowspan="5"><nobr>サービスの属性値設定</nobr></td><td>responseType</td><td>json</td><td rowspan="5"><code>{"result":"Success!!"}</code></td></tr>
 *     <tr><td>action</td><td>set</td></tr>
 *     <tr><td>name</td><td>サービス名</td></tr>
 *     <tr><td>attribute</td><td>属性名</td></tr>
 *     <tr><td>value</td><td>属性値</td></tr>
 *     <tr><td rowspan="6">6</td><td rowspan="6"><nobr>サービスの操作実行</nobr></td><td>responseType</td><td>json</td><td rowspan="6"><code>{"result":"Success!!","return":"hoge"}</code></td></tr>
 *     <tr><td>action</td><td>call</td></tr>
 *     <tr><td>name</td><td>サービス名</td></tr>
 *     <tr><td>method</td><td>メソッドのシグニチャ。メソッド名(引数の型,引数の型,...)</td></tr>
 *     <tr><td>args</td><td>引数の値。引数が複数存在する場合は、このパラメータを引数の順番通りに複数指定する。</td></tr>
 *     <tr><td>argTypes</td><td>argsで指定した引数の値を示す文字列を、引数の型にキャスト可能なオブジェクトに変換するPropertyEditorの型を指定する。空文字や指定しない場合は、引数の型に合うPropertyEditorで変換する。</td></tr>
 *     <tr><td rowspan="3">7</td><td rowspan="4"><nobr>サービスロード完了チェック</nobr></td><td>responseType</td><td>json</td><td rowspan="3"><code>{"causes":["Nimbus#Service1","Nimbus#Service2"]}</code></td></tr>
 *     <tr><td>action</td><td>checkLoadManagerCompleted</td></tr>
 *     <tr><td>managerName</td><td>マネージャ名。複数指定可能。指定しない場合は、全てのマネージャを対象とする。</td></tr>
 * </table>
 * <p>
 * 以下に、サーブレットのweb.xml定義例を示す。<br>
 * <pre>
 * &lt;servlet&gt;
 *     &lt;servlet-name&gt;NimbusServlet&lt;/servlet-name&gt;
 *     &lt;servlet-class&gt;jp.ossc.nimbus.servlet.ServiceManagerFactoryServlet&lt;/servlet-class&gt;
 *     &lt;init-param&gt;
 *         &lt;param-name&gt;ServicePaths&lt;/param-name&gt;
 *         &lt;param-value&gt;sample1-service.xml,sample2-service.xml&lt;/param-value&gt;
 *     &lt;/init-param&gt;
 *     &lt;init-param&gt;
 *         &lt;param-name&gt;CheckLoadManagerCompleted&lt;/param-name&gt;
 *         &lt;param-value&gt;true&lt;/param-value&gt;
 *     &lt;/init-param&gt;
 *     &lt;init-param&gt;
 *         &lt;param-name&gt;ConsoleEnabled&lt;/param-name&gt;
 *         &lt;param-value&gt;true&lt;/param-value&gt;
 *     &lt;/init-param&gt;
 *     &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;
 * &lt;/servlet&gt;
 * 
 * &lt;servlet-mapping&gt;
 *     &lt;servlet-name&gt;NimbusServlet&lt;/servlet-name&gt;
 *     &lt;url-pattern&gt;/nimbus-console&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 * 
 * @author M.Takata
 */
public class ServiceManagerFactoryServlet extends HttpServlet{
    
    private static final long serialVersionUID = 5668270241695101050L;
    
    /**
     * ロードするサービス定義ファイルを指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_SERVICE_PATHS = "ServicePaths";
    
    /**
     * ロードするサービス定義ファイルのあるディレクトリを指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_SERVICE_DIRS = "ServiceDirs";
    
    /**
     * ロードするサービス定義ファイルのあるディレクトリのファイル名フィルターを正規表現で指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_SERVICE_FILE_FILTER = "ServiceFileFilter";
    
    /**
     * サービス定義ロード完了チェックを行うかどうかを指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_CHECK_LOAD_MNG_CMP = "CheckLoadManagerCompleted";
    
    /**
     * サービス定義ロード完了チェックをServiceManager単位で行う場合のServiceManager名を指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_CHECK_LOAD_MNG_CMP_BY = "CheckLoadManagerCompletedBy";
    
    /**
     * サービス定義ファイルを検証するかどうかための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_VALIDATE = "Validate";
    
    /**
     * 管理コンソールを有効にするかどうかを指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_CONSOLE_ENABLED = "ConsoleEnabled";
    
    /**
     * 管理コンソールからの属性設定を有効にするかどうかを指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_ATTR_SET_ENABLED = "AttributeSetEnabled";
    
    /**
     * 管理コンソールに属性の値を表示する際の表示最大長を指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_ATTR_MAX_LENGTH = "AttributeMaxLength";
    
    /**
     * 管理コンソールからのメソッド呼び出しを有効にするかどうかを指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_METHOD_CALL_ENABLED = "MethodCallEnabled";
    
    /**
     * 無視するメソッドのシグニチャ配列を指定するための初期化パラメータ名。<p>
     */
    protected static final String INIT_PARAM_NAME_IGNORE_METHODS = "IgnoreMethods";
    
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
    
    private static Method[] DEFAULT_IGNORE_METHODS;
    
    static{
        List methods = new ArrayList();
        try{
            methods.add(
                jp.ossc.nimbus.service.semaphore.Semaphore.class.getMethod(
                    "getResource", (Class[])null
                )
            );
        }catch(NoClassDefFoundError e){
        }catch(NoSuchMethodException e){
            // 起こらないはず
            e.printStackTrace();
        }
        try{
            methods.add(
                jp.ossc.nimbus.service.semaphore.Semaphore.class.getMethod(
                    "getResource", new Class[]{Integer.TYPE}
                )
            );
        }catch(NoClassDefFoundError e){
        }catch(NoSuchMethodException e){
            // 起こらないはず
            e.printStackTrace();
        }
        try{
            methods.add(
                jp.ossc.nimbus.service.semaphore.Semaphore.class.getMethod(
                    "getResource", new Class[]{Long.TYPE}
                )
            );
        }catch(NoClassDefFoundError e){
        }catch(NoSuchMethodException e){
            // 起こらないはず
            e.printStackTrace();
        }
        try{
            methods.add(
                jp.ossc.nimbus.service.semaphore.Semaphore.class.getMethod(
                    "getResource", new Class[]{Long.TYPE, Integer.TYPE}
                )
            );
        }catch(NoClassDefFoundError e){
        }catch(NoSuchMethodException e){
            // 起こらないはず
            e.printStackTrace();
        }
        try{
            methods.add(
                jp.ossc.nimbus.service.semaphore.Semaphore.class.getMethod(
                    "getResource",
                    new Class[]{Long.TYPE, Integer.TYPE, Long.TYPE}
                )
            );
        }catch(NoClassDefFoundError e){
        }catch(NoSuchMethodException e){
            // 起こらないはず
            e.printStackTrace();
        }
        try{
            methods.add(
                jp.ossc.nimbus.service.resource.ResourceFactory.class.getMethod(
                    "makeResource", new Class[]{String.class}
                )
            );
        }catch(NoClassDefFoundError e){
        }catch(NoSuchMethodException e){
            // 起こらないはず
            e.printStackTrace();
        }
        try{
            methods.add(
                jp.ossc.nimbus.service.connection.ConnectionFactory.class.getMethod(
                    "getConnection", (Class[])null
                )
            );
        }catch(NoClassDefFoundError e){
        }catch(NoSuchMethodException e){
            // 起こらないはず
            e.printStackTrace();
        }
        try{
            methods.add(
                jp.ossc.nimbus.service.jms.JMSConnectionFactory.class.getMethod(
                    "getConnection", (Class[])null
                )
            );
        }catch(NoClassDefFoundError e){
        }catch(NoSuchMethodException e){
            // 起こらないはず
            e.printStackTrace();
        }
        try{
            methods.add(
                jp.ossc.nimbus.service.jms.JMSConnectionFactory.class.getMethod(
                    "getConnection", new Class[]{String.class, String.class}
                )
            );
        }catch(NoClassDefFoundError e){
        }catch(NoSuchMethodException e){
            // 起こらないはず
            e.printStackTrace();
        }
        try{
            methods.add(
                jp.ossc.nimbus.service.jms.JMSSessionFactory.class.getMethod(
                    "getSession", (Class[])null
                )
            );
        }catch(NoClassDefFoundError e){
        }catch(NoSuchMethodException e){
            // 起こらないはず
            e.printStackTrace();
        }
        try{
            methods.add(
                jp.ossc.nimbus.service.jms.JMSSessionFactory.class.getMethod("getSession", new Class[]{Boolean.TYPE, Integer.TYPE})
            );
        }catch(NoClassDefFoundError e){
        }catch(NoSuchMethodException e){
            // 起こらないはず
            e.printStackTrace();
        }
        try{
            methods.add(
                jp.ossc.nimbus.service.jms.JMSSessionFactory.class.getMethod(
                    "getSession",
                    new Class[]{
                        javax.jms.Connection.class
                    }
                )
            );
        }catch(NoClassDefFoundError e){
        }catch(NoSuchMethodException e){
            // 起こらないはず
            e.printStackTrace();
        }
        try{
            methods.add(
                jp.ossc.nimbus.service.jms.JMSSessionFactory.class.getMethod(
                    "getSession",
                    new Class[]{
                        javax.jms.Connection.class,
                        Boolean.TYPE,
                        Integer.TYPE
                    }
                )
            );
        }catch(NoClassDefFoundError e){
        }catch(NoSuchMethodException e){
            // 起こらないはず
            e.printStackTrace();
        }
        try{
            methods.add(
                jp.ossc.nimbus.service.resource.ResourceFactory.class.getMethod(
                    "makeResource",
                    new Class[]{
                        java.lang.String.class
                    }
                )
            );
        }catch(NoClassDefFoundError e){
        }catch(NoSuchMethodException e){
            // 起こらないはず
            e.printStackTrace();
        }
        if(methods.size() != 0){
            DEFAULT_IGNORE_METHODS = (Method[])methods.toArray(
                new Method[methods.size()]
            );
        }
    }
    
    private ClassMappingTree ignoreMethodMap;
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
        
        final boolean isValidate = isValidate();
        
        final String[] serviceDirs = getServiceDirs();
        if(serviceDirs != null && serviceDirs.length != 0){
            final String serviceFileFilter = getServiceFileFilter();
            for(int i = 0; i < serviceDirs.length; i++){
                ServiceManagerFactory.loadManagers(
                    serviceDirs[i],
                    serviceFileFilter,
                    true,
                    isValidate
                );
            }
        }
        
        final String[] servicePaths = getServicePaths();
        if(servicePaths != null && servicePaths.length != 0){
            for(int i = 0; i < servicePaths.length; i++){
                ServiceManagerFactory.loadManager(
                    servicePaths[i],
                    true,
                    isValidate
                );
            }
        }
        
        if(isCheckLoadManagerCompleted()){
            final String[] managerNames = getCheckLoadManagerCompletedBy();
            if(managerNames == null || managerNames.length == 0){
                ServiceManagerFactory.checkLoadManagerCompleted();
            }else{
                for(int i = 0; i < managerNames.length; i++){
                    ServiceManagerFactory.checkLoadManagerCompletedBy(
                        managerNames[i]
                    );
                }
            }
        }
        
        if(DEFAULT_IGNORE_METHODS != null){
            ignoreMethodMap = new ClassMappingTree();
            for(int i = 0; i < DEFAULT_IGNORE_METHODS.length; i++){
                ignoreMethodMap.add(
                    DEFAULT_IGNORE_METHODS[i].getDeclaringClass(),
                    DEFAULT_IGNORE_METHODS[i]
                );
            }
        }
        
        final Method[] ignoreMethods = getIgnoreMethods();
        if(ignoreMethods != null && ignoreMethods.length != 0){
            if(ignoreMethodMap == null){
                ignoreMethodMap = new ClassMappingTree();
            }
            for(int i = 0; i < ignoreMethods.length; i++){
                ignoreMethodMap.add(
                    ignoreMethods[i].getDeclaringClass(),
                    ignoreMethods[i]
                );
            }
        }
    }
    
    private boolean isConsoleEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_CONSOLE_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
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
    
    private boolean isMethodCallEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_METHOD_CALL_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private String getRealPath(String path){
        final ServletContext context
             = getServletConfig().getServletContext();
        String result = context.getRealPath(path);
        if(result == null){
            result = path;
        }
        return result;
    }
    
    private String[] getServicePaths(){
        final ServletConfig config = getServletConfig();
        final String servicePathString = config.getInitParameter(INIT_PARAM_NAME_SERVICE_PATHS);
        if(servicePathString == null){
            return null;
        }
        final StringArrayEditor editor = new StringArrayEditor();
        editor.setAsText(servicePathString);
        String[] servicePaths = (String[])editor.getValue();
        for(int i = 0; i < servicePaths.length; i++){
            servicePaths[i] = getRealPath(servicePaths[i]);
        }
        return servicePaths;
    }
    
    private String[] getServiceDirs(){
        final ServletConfig config = getServletConfig();
        final String serviceDirString = config.getInitParameter(INIT_PARAM_NAME_SERVICE_DIRS);
        if(serviceDirString == null){
            return null;
        }
        final StringArrayEditor editor = new StringArrayEditor();
        editor.setAsText(serviceDirString);
        String[] serviceDirs = (String[])editor.getValue();
        for(int i = 0; i < serviceDirs.length; i++){
            serviceDirs[i] = getRealPath(serviceDirs[i]);
        }
        return serviceDirs;
    }
    
    private String getServiceFileFilter(){
        return getServletConfig().getInitParameter(INIT_PARAM_NAME_SERVICE_FILE_FILTER);
    }
    
    private boolean isCheckLoadManagerCompleted(){
        final ServletConfig config = getServletConfig();
        final String isCheck = config.getInitParameter(INIT_PARAM_NAME_CHECK_LOAD_MNG_CMP);
        return isCheck == null ? false : Boolean.valueOf(isCheck).booleanValue();
    }
    
    private boolean isValidate(){
        final ServletConfig config = getServletConfig();
        final String isValidate = config.getInitParameter(INIT_PARAM_NAME_VALIDATE);
        return isValidate == null ?
             false : Boolean.valueOf(isValidate).booleanValue();
    }
    
    private String[] getCheckLoadManagerCompletedBy(){
        final ServletConfig config = getServletConfig();
        final String managerNames
            = config.getInitParameter(INIT_PARAM_NAME_CHECK_LOAD_MNG_CMP_BY);
        final StringArrayEditor editor = new StringArrayEditor();
        editor.setAsText(managerNames);
        return (String[])editor.getValue();
    }
    
    private Method[] getIgnoreMethods(){
        final ServletConfig config = getServletConfig();
        final String ignoreMethodsStr = config.getInitParameter(INIT_PARAM_NAME_IGNORE_METHODS);
        if(ignoreMethodsStr == null){
            return null;
        }
        final MethodArrayEditor editor = new MethodArrayEditor();
        editor.setAsText(ignoreMethodsStr);
        return (Method[])editor.getValue();
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
        
        if(!isConsoleEnabled()){
            resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }
        req.setCharacterEncoding("UTF-8");
        
        final String action = req.getParameter("action");
        final String responseType = req.getParameter("responseType");
        if(action == null){
            processIndexResponse(req, resp, responseType);
        }else if(action.equals("manager")){
            processManagerResponse(req, resp, responseType);
        }else if(action.equals("service")){
            processServiceResponse(req, resp, responseType);
        }else if(action.equals("set")){
            if(!isAttributeSetEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processSetAttributeResponse(req, resp, responseType);
        }else if(action.equals("get")){
            processGetAttributeResponse(req, resp, responseType);
        }else if(action.equals("call")){
            if(!isMethodCallEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processCallOperationResponse(req, resp, responseType);
        }else if(action.equals("checkLoadManagerCompleted")){
            processCheckLoadManagerCompleted(req, resp, responseType);
        }else{
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
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
        final StringBuilder buf = new StringBuilder();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            final ServiceManager[] managers = ServiceManagerFactory.findManagers();
            final String[] managerNames = new String[managers.length];
            for(int i = 0; i < managers.length; i++){
                managerNames[i] = managers[i].getServiceName();
            }
            Arrays.sort(managerNames);
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(managerNames))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>Nimbus ServiceManagerFactory</title></head>");
            buf.append("<body>");
            
            buf.append("<b>Service definition paths</b><br>");
            buf.append("<ul>");
            final Collection loaderSet = ServiceManagerFactory.getLoaders();
            final String[] serviceURLs = new String[loaderSet.size()];
            final Iterator loaders = loaderSet.iterator();
            int count = 0;
            while(loaders.hasNext()){
                final ServiceLoader loader = (ServiceLoader)loaders.next();
                serviceURLs[count++] = loader.getServiceURL().toString();
            }
            Arrays.sort(serviceURLs);
            for(int i = 0; i < serviceURLs.length; i++){
                String fileName = serviceURLs[i];
                final int index = fileName.lastIndexOf('/');
                if(index != -1){
                    fileName = fileName.substring(index + 1);
                }
                buf.append("<li>").append("<a href=\"").append(serviceURLs[i])
                    .append("\">").append(fileName)
                    .append("</a>").append("</li>");
            }
            buf.append("</ul>");
            buf.append("<p>");
            
            buf.append("<b>Service managers</b><br>");
            buf.append("<ul>");
            final ServiceManager[] managers = ServiceManagerFactory.findManagers();
            final String[] managerNames = new String[managers.length];
            for(int i = 0; i < managers.length; i++){
                managerNames[i] = managers[i].getServiceName();
            }
            Arrays.sort(managerNames);
            StringBuilder url = new StringBuilder();
            for(int i = 0; i < managerNames.length; i++){
                url.setLength(0);
                url.append(getCurrentPath(req))
                   .append("?action=manager&name=")
                   .append(managerNames[i]);
                buf.append("<li>");
                buf.append("<a href=\"")
                   .append(resp.encodeURL(url.toString()))
                   .append("\">");
                buf.append(managerNames[i]).append("</a>");
                buf.append("</li>");
            }
            buf.append("</ul>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    private String getCurrentPath(HttpServletRequest req){
        return req.getContextPath() + req.getServletPath();
    }
    
    /**
     * 管理コンソールのServiceManager画面リクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processManagerResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        final String managerName = req.getParameter("name");
        final StringBuilder buf = new StringBuilder();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            List serviceNameList = new ArrayList();
            if(managerName == null){
                final ServiceManager[] managers = ServiceManagerFactory.findManagers();
                for(int i = 0; i < managers.length; i++){
                    ServiceManager manager = managers[i];
                    Iterator names = manager.serviceNameSet().iterator();
                    while(names.hasNext()){
                        String serviceNameStr = (String)names.next();
                        serviceNameList.add(new ServiceName(manager.getServiceName(), serviceNameStr).toString());
                    }
                }
            }else{
                final ServiceManager manager = ServiceManagerFactory.findManager(managerName);
                if(manager == null){
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                Iterator names = manager.serviceNameSet().iterator();
                while(names.hasNext()){
                    String serviceNameStr = (String)names.next();
                    serviceNameList.add(new ServiceName(managerName, serviceNameStr).toString());
                }
            }
            Collections.sort(serviceNameList);
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(serviceNameList))
            );
        }else{
            if(managerName == null){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            final ServiceManager manager = ServiceManagerFactory.findManager(managerName);
            if(manager == null){
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>Nimbus ServiceManager ")
                .append(managerName).append("</title></head>");
            buf.append("<body>");
            
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("\">ServiceManagerFactory</a>");
            buf.append("<hr>");
            
            buf.append("<b>ServiceManager name : </b>").append(managerName);
            
            buf.append("<p>");
            buf.append("<b>Services</b><br>");
            buf.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\" width=\"90%\">");
            buf.append("<tr bgcolor=\"#cccccc\"><th>name</th><th>class</th></tr>");
            final Set serviceNameSet = manager.serviceNameSet();
            final ServiceName[] serviceNames = new ServiceName[serviceNameSet.size()];
            final Iterator nameItr = manager.serviceNameSet().iterator();
            int count = 0;
            while(nameItr.hasNext()){
                final String serviceNameStr = (String)nameItr.next();
                serviceNames[count++] = new ServiceName(managerName, serviceNameStr);
            }
            Arrays.sort(serviceNames);
            final StringBuilder url = new StringBuilder();
            for(int i = 0; i < serviceNames.length; i++){
                final ServiceName serviceName = serviceNames[i];
                url.setLength(0);
                url.append(getCurrentPath(req))
                   .append("?action=service&name=")
                   .append(URLEncoder.encode(serviceName.toString(), "UTF-8"));
                buf.append("<tr>");
                buf.append("<td><a href=\"")
                   .append(resp.encodeURL(url.toString()))
                   .append("\">");
                buf.append(serviceName.getServiceName()).append("</a></td><td>&nbsp;")
                    .append(getTargetObject(ServiceManagerFactory.getService(serviceName)).getClass().getName())
                    .append("</td>");
                buf.append("</tr>");
            }
            buf.append("</table>");
            
            buf.append("<hr>");
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("\">ServiceManagerFactory</a>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    private Object getTargetObject(Service service){
        Object targetObj = service;
        if(service instanceof GenericsServiceProxy){
            targetObj = ServiceManagerFactory.getServiceObject(
                service.getServiceManagerName(),
                service.getServiceName()
            );
        }
        return targetObj;
    }
    
    /**
     * 管理コンソールのService画面リクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processServiceResponse(
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
        Service service = null;
        try{
            service = ServiceManagerFactory.getService(serviceName);
        }catch(ServiceNotFoundException e){
        }
        if(service == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        final Class targetClass = getTargetObject(service).getClass();
        final Method[] methods = targetClass.getMethods();
        final Map attributeMap = getAttributes(methods);
        final String[] attributeNames = (String[])attributeMap.keySet()
            .toArray(new String[attributeMap.size()]);
        Arrays.sort(attributeNames);
        final Map methodMap = new HashMap();
        for(int i = 0; i < methods.length; i++){
            if(isIgnoreMethod(methods[i]) || isAttributeMethod(methods[i])){
                continue;
            }
            methodMap.put(new MethodSignature(methods[i]), methods[i]);
        }
        final MethodSignature[] sigs = (MethodSignature[])methodMap.keySet()
            .toArray(new MethodSignature[methodMap.size()]);
        Arrays.sort(sigs);
        final StringBuilder buf = new StringBuilder();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            
            final String getAttributeStr = req.getParameter("getAttribute");
            final boolean isGetAttribute = getAttributeStr != null
                && Boolean.valueOf(getAttributeStr).booleanValue() ? true : false;
            Map serviceMap = new LinkedHashMap();
            serviceMap.put("className", targetClass.getName());
            List attributeList = new ArrayList();
            for(int i = 0; i < attributeNames.length; i++){
                Map attribute = new LinkedHashMap();
                final String attributeName = attributeNames[i];
                final AttributeMethod attrMethod
                     = (AttributeMethod)attributeMap.get(attributeName);
                final String attrType = getAttributeType(attrMethod);
                attribute.put("name", attributeName);
                attribute.put("accessType", attrType);
                attribute.put("type", attrMethod.getType().getName());
                if(isGetAttribute && !attrType.equals(ATTRIBUTE_WRITE_ONLY)){
                    final String attrValue = getAttributeValue(service, attrMethod);
                    attribute.put("value", attrValue);
                }
                attributeList.add(attribute);
            }
            serviceMap.put("attributes", attributeList);
            List operationList = new ArrayList();
            for(int i = 0; i < sigs.length; i++){
                operationList.add(sigs[i].toString());
            }
            serviceMap.put("operations", operationList);
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(serviceMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>Nimbus Service ")
                .append(serviceNameStr).append("</title></head>");
            buf.append("<body>");
            
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("\">ServiceManagerFactory</a>");
            buf.append("/");
            final StringBuilder url = new StringBuilder();
            url.append(getCurrentPath(req))
                .append("?action=manager&name=")
                .append(serviceName.getServiceManagerName());
            buf.append("<a href=\"").append(resp.encodeURL(url.toString()))
                .append("\">ServiceManager</a>");
            buf.append("<hr>");
            
            buf.append("<b>Service name : </b>").append(serviceNameStr);
            buf.append("<p>");
            
            buf.append("<b>Service class : </b>").append(targetClass);
            buf.append("<p>");
            
            buf.append("<b>Attributes</b><br>");
            buf.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\" width=\"90%\">");
            buf.append("<tr bgcolor=\"#cccccc\"><th>name</th><th>r/w</th><th>type</th><th>value</th><th>apply</th></tr>");
            for(int i = 0; i < attributeNames.length; i++){
                final String attributeName = attributeNames[i];
                final AttributeMethod attrMethod
                     = (AttributeMethod)attributeMap.get(attributeName);
                final String attrType = getAttributeType(attrMethod);
                buf.append("<form name=\"").append(attributeName)
                    .append("\" action=\"").append(getCurrentPath(req)).append("\" method=\"POST\">");
                buf.append("<input type=\"hidden\" name=\"action\" value=\"set\">");
                buf.append("<input type=\"hidden\" name=\"name\" value=\"")
                    .append(serviceNameStr).append("\">");
                if(attrType.equals(ATTRIBUTE_READ_AND_WRITE)
                    || attrType.equals(ATTRIBUTE_WRITE_ONLY)){
                    final MethodSignature signature = new MethodSignature(
                        attrMethod.getSetter()
                    );
                    buf.append("<input type=\"hidden\" name=\"method\" value=\"")
                        .append(signature).append("\">");
                }
                
                buf.append("<tr>");
                buf.append("<td>").append(attributeName).append("</td>");
                buf.append("<td>").append(attrType).append("</td>");
                buf.append("<td>").append(attrMethod.getType().getName()).append("</td>");
                buf.append("<td>");
                if(attrType.equals(ATTRIBUTE_READ_AND_WRITE)){
                    final String attrValue = getAttributeValue(service, attrMethod);
                    buf.append("<textarea name=\"value\" cols=\"40\" rows=\"2\">")
                        .append(attrValue).append("</textarea>");
                }else if(attrType.equals(ATTRIBUTE_WRITE_ONLY)){
                    buf.append("<textarea name=\"value\" cols=\"40\" rows=\"2\">")
                        .append("</textarea>");
                }else{
                    final String attrValue = getAttributeValue(service, attrMethod);
                    buf.append("<textarea name=\"value\" readonly cols=\"40\" rows=\"2\">")
                        .append(attrValue).append("</textarea>");
                }
                buf.append("</td>");
                buf.append("<td>");
                if(attrType.equals(ATTRIBUTE_READ_AND_WRITE)
                    || attrType.equals(ATTRIBUTE_WRITE_ONLY)){
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
            
            buf.append("<b>Methods</b><br>");
            buf.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\" width=\"90%\">");
            buf.append("<tr bgcolor=\"#cccccc\"><th rowspan=\"2\">method</th><th colspan=\"2\">arguments</th><th rowspan=\"2\">call</th></tr>");
            buf.append("<tr bgcolor=\"#cccccc\"><th>value</th><th>type</th></tr>");
            for(int i = 0; i < sigs.length; i++){
                buf.append("<form name=\"").append(sigs[i])
                    .append("\" action=\"").append(getCurrentPath(req)).append("\" method=\"POST\">");
                buf.append("<input type=\"hidden\" name=\"action\" value=\"call\">");
                buf.append("<input type=\"hidden\" name=\"name\" value=\"")
                    .append(serviceNameStr).append("\">");
                buf.append("<input type=\"hidden\" name=\"method\" value=\"")
                    .append(sigs[i]).append("\">");
                
                buf.append("<tr>");
                buf.append("<td>").append(sigs[i]).append("</td>");
                buf.append("<td>");
                final Class[] paramTypes
                     = ((Method)methodMap.get(sigs[i])).getParameterTypes();
                if(paramTypes.length == 0){
                    buf.append("　");
                }else{
                    for(int j = 0, max = paramTypes.length; j < max; j++){
                        buf.append("<textarea name=\"args\" cols=\"40\" rows=\"2\"></textarea>");
                        if(j != max - 1){
                            buf.append("<br>");
                        }
                    }
                }
                buf.append("</td>");
                buf.append("<td>");
                if(paramTypes.length == 0){
                    buf.append("　");
                }else{
                    for(int j = 0, max = paramTypes.length; j < max; j++){
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
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("\">ServiceManagerFactory</a>");
            buf.append("/");
            buf.append("<a href=\"").append(resp.encodeURL(url.toString()))
                .append("\">ServiceManager</a>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * 管理コンソールのサービス属性取得リクエスト処理を行う。<p>
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
        final String serviceNameStr = req.getParameter("name");
        if(serviceNameStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        final ServiceName serviceName = (ServiceName)editor.getValue();
        Service service = null;
        try{
            service = ServiceManagerFactory.getService(serviceName);
        }catch(ServiceNotFoundException e){
        }
        if(service == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        final String attributeName = req.getParameter("attribute");
        if(attributeName == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        AttributeMethod attrMethod = getAttribute(getTargetObject(service).getClass(), attributeName);
        if(attrMethod == null || getAttributeType(attrMethod).equals(ATTRIBUTE_WRITE_ONLY)){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        final StringBuilder buf = new StringBuilder();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map json = new HashMap();
            json.put("value", getAttributeValue(service, attrMethod));
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(json))
            );
        }else{
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        resp.getWriter().println(buf.toString());
    }
    /**
     * 管理コンソールのサービス属性設定リクエスト処理を行う。<p>
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
        final String serviceNameStr = req.getParameter("name");
        if(serviceNameStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        final ServiceName serviceName = (ServiceName)editor.getValue();
        Service service = null;
        try{
            service = ServiceManagerFactory.getService(serviceName);
        }catch(ServiceNotFoundException e){
        }
        if(service == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        final String method = req.getParameter("method");
        final String attributeValueStr = req.getParameter("value");
        String result = null;
        if(method == null){
            final String attributeName = req.getParameter("attribute");
            if(attributeName == null){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            AttributeMethod attrMethod = getAttribute(getTargetObject(service).getClass(), attributeName);
            if(attrMethod == null || getAttributeType(attrMethod).equals(ATTRIBUTE_READ_ONLY)){
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            result = setAttributeValue(
                service,
                attrMethod.getSetter(),
                attributeValueStr
            );
        }else{
            result = setAttributeValue(
                service,
                method,
                attributeValueStr
            );
        }
        final StringBuilder buf = new StringBuilder();
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
            buf.append("<head><title>Nimbus Set Attribute</title></head>");
            buf.append("<body>");
            
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("\">ServiceManagerFactory</a>");
            buf.append("/");
            final StringBuilder url = new StringBuilder();
            url.append(getCurrentPath(req))
                .append("?action=manager&name=")
                .append(serviceName.getServiceManagerName());
            buf.append("<a href=\"").append(resp.encodeURL(url.toString()))
                .append("\">ServiceManager</a>");
            buf.append("/");
            url.setLength(0);
            url.append(getCurrentPath(req))
                .append("?action=service&name=")
                .append(URLEncoder.encode(serviceNameStr, "UTF-8"));
            buf.append("<a href=\"").append(resp.encodeURL(url.toString()))
                .append("\">Service</a>");
            buf.append("<hr>");
            
            buf.append("<pre>").append(result).append("</pre>");
            
            buf.append("<hr>");
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("\">ServiceManagerFactory</a>");
            buf.append("/");
            url.setLength(0);
            url.append(getCurrentPath(req))
                .append("?action=manager&name=")
                .append(serviceName.getServiceManagerName());
            buf.append("<a href=\"").append(resp.encodeURL(url.toString()))
                .append("\">ServiceManager</a>");
            buf.append("/");
            url.setLength(0);
            url.append(getCurrentPath(req))
                .append("?action=service&name=")
                .append(URLEncoder.encode(serviceNameStr, "UTF-8"));
            buf.append("<a href=\"").append(resp.encodeURL(url.toString()))
                .append("\">Service</a>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * サービスロード完了チェックリクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processCheckLoadManagerCompleted(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        final String[] managerNames = req.getParameterValues("managerName");
        boolean result = true;
        Set notStarted = new LinkedHashSet();
        if(managerNames == null || managerNames.length == 0){
            result &= ServiceManagerFactory.checkLoadManagerCompleted(notStarted, false);
        }else{
            for(int i = 0; i < managerNames.length; i++){
                result &= ServiceManagerFactory.checkLoadManagerCompletedBy(
                    managerNames[i],
                    notStarted,
                    false
                );
            }
        }
        final StringBuilder buf = new StringBuilder();
        if(result){
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
                buf.append("<head><title>Nimbus CheckLoadManagerCompleted</title></head>");
                buf.append("<body>");
                buf.append("Completed");
                buf.append("</body>");
                buf.append("</html>");
            }
            resp.getWriter().println(buf.toString());
        }else{
            if("json".equals(responseType)){
                resp.setContentType("application/json;charset=UTF-8");
                Map json = new HashMap();
                json.put("result", result);
                json.put("causes", notStarted);
                buf.append(
                    toStringConverter.convertToObject(jsonConverter.convertToStream(json))
                );
            }else{
                resp.setContentType("text/html;charset=UTF-8");
                buf.append("<html>");
                buf.append("<head><title>Nimbus CheckLoadManagerCompleted</title></head>");
                buf.append("<body>");
                buf.append("causes : ").append(notStarted);
                buf.append("</body>");
                buf.append("</html>");
            }
            resp.getWriter().println(buf.toString());
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
    }
    
    /**
     * 管理コンソールのサービスメソッド呼び出しリクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param responseType レスポンス種別
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processCallOperationResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        final String serviceNameStr = req.getParameter("name");
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        final ServiceName serviceName = (ServiceName)editor.getValue();
        Service service = null;
        try{
            service = ServiceManagerFactory.getService(serviceName);
        }catch(ServiceNotFoundException e){
        }
        if(service == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        final String methodStr = req.getParameter("method");
        final String[] argsStr = req.getParameterValues("args");
        final String[] argTypesStr = req.getParameterValues("argTypes");
        if(methodStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Object ret = null;
        Exception exception = null;
        Method method = null;
        try{
            final Object targetObj = getTargetObject(service);
            final ClassLoader loader = NimbusClassLoader.getInstance();
            final MethodSignature signature = new MethodSignature(methodStr);
            method = signature.getMethod(targetObj);
            final Class[] paramTypes = method.getParameterTypes();
            Object[] params = new Object[paramTypes.length];
            for(int i = 0; i < paramTypes.length; i++){
                Class editType = paramTypes[i];
                if(argTypesStr[i] != null && argTypesStr[i].length() != 0){
                    try{
                        editType = Class.forName(argTypesStr[i], true, loader);
                    }catch(ClassNotFoundException e){
                    }
                }
                if(argsStr[i] == null || argsStr[i].equals("null")){
                    params[i] = null;
                }else{
                    final PropertyEditor propEditor = findEditor(service, editType);
                    if(propEditor == null){
                        if(paramTypes[i].equals(Object.class)){
                            params[i] = argsStr[i];
                        }else{
                            throw new IllegalArgumentException("PropertyEditor for " + paramTypes[i] + " not found.");
                        }
                    }else if(argsStr.length > i){
                        propEditor.setAsText(argsStr[i]);
                        params[i] = propEditor.getValue();
                    }else{
                        params[i] = null;
                    }
                }
            }
            ret = method.invoke(targetObj, params);
        }catch(Exception e){
            exception = e;
        }
        
        final StringBuilder buf = new StringBuilder();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map json = new LinkedHashMap();
            final StringWriter result = new StringWriter();
            final PrintWriter writer = new PrintWriter(result);
            if(exception == null){
                writer.print("Success!!");
            }else{
                writer.println("Failed!!");
                exception.printStackTrace(writer);
            }
            json.put("result", result.toString());
            json.put("return", ret);
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(json))
            );
        }else{
            final StringWriter result = new StringWriter();
            final PrintWriter writer = new PrintWriter(result);
            if(exception == null){
                writer.println("Success!!");
                final Class retType = method.getReturnType();
                if(!retType.equals(Void.TYPE)){
                    final PropertyEditor propEditor
                         = findEditor(service, retType);
                    if(propEditor == null){
                        writer.println(ret);
                    }else{
                        propEditor.setValue(ret);
                        writer.println(propEditor.getValue());
                    }
                }
            }else{
                writer.println("Failed!!");
                exception.printStackTrace(writer);
            }
            resp.setContentType("text/html;charset=UTF-8");
            buf.append("<html>");
            buf.append("<head><title>Nimbus Call Method</title></head>");
            buf.append("<body>");
            
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("\">ServiceManagerFactory</a>");
            buf.append("/");
            final StringBuilder url = new StringBuilder();
            url.append(getCurrentPath(req))
                .append("?action=manager&name=")
                .append(serviceName.getServiceManagerName());
            buf.append("<a href=\"").append(resp.encodeURL(url.toString()))
                .append("\">ServiceManager</a>");
            buf.append("/");
            url.setLength(0);
            url.append(getCurrentPath(req))
                .append("?action=service&name=")
                .append(URLEncoder.encode(serviceNameStr, "UTF-8"));
            buf.append("<a href=\"").append(resp.encodeURL(url.toString()))
                .append("\">Service</a>");
            buf.append("<hr>");
            
            buf.append("<pre>").append(result).append("</pre>");
            
            buf.append("<hr>");
            buf.append("<a href=\"").append(getCurrentPath(req))
                .append("\">ServiceManagerFactory</a>");
            buf.append("/");
            url.setLength(0);
            url.append(getCurrentPath(req))
                .append("?action=manager&name=")
                .append(serviceName.getServiceManagerName());
            buf.append("<a href=\"").append(resp.encodeURL(url.toString()))
                .append("\">ServiceManager</a>");
            buf.append("/");
            url.setLength(0);
            url.append(getCurrentPath(req))
                .append("?action=service&name=")
                .append(URLEncoder.encode(serviceNameStr, "UTF-8"));
            buf.append("<a href=\"").append(resp.encodeURL(url.toString()))
                .append("\">Service</a>");
            
            buf.append("</body>");
            buf.append("</html>");
        }
        resp.getWriter().println(buf.toString());
    }
    
    private String setAttributeValue(
        Service service,
        Method method,
        String value
    ){
        final Object targetObj = getTargetObject(service);
        try{
            final Class[] paramTypes = method.getParameterTypes();
            final PropertyEditor editor
                 = findEditor(service, paramTypes[0]);
            Object[] params = null;
            if(editor == null){
                return "Failed!! PropertyEditor " + paramTypes[0] + " not found.";
            }else if(value == null || value.equals("null")){
                params = new Object[]{null};
            }else{
                editor.setAsText(value);
                params = new Object[]{editor.getValue()};
            }
            method.invoke(targetObj, params);
        }catch(Exception e){
            final StringWriter sw = new StringWriter();
            final PrintWriter writer = new PrintWriter(sw);
            e.printStackTrace(writer);
            return sw.toString();
        }
        return "Success!!";
    }
    
    private String setAttributeValue(
        Service service,
        String methodStr,
        String value
    ){
        final Object targetObj = getTargetObject(service);
        try{
            final MethodSignature signature = new MethodSignature(methodStr);
            final Method method = signature.getMethod(targetObj);
            return setAttributeValue(service, method, value);
        }catch(Exception e){
            final StringWriter sw = new StringWriter();
            final PrintWriter writer = new PrintWriter(sw);
            e.printStackTrace(writer);
            return sw.toString();
        }
    }
    
    private PropertyEditor findEditor(Service service, Class type){
        PropertyEditor editor = null;
        if(service instanceof ServiceManager){
            final Iterator loaders = ((ServiceManager)service)
                .getServiceLoaders().iterator();
            while(loaders.hasNext()){
                final ServiceLoader loader = (ServiceLoader)loaders.next();
                editor = loader.findEditor(type);
                if(editor != null){
                    break;
                }
            }
        }else{
            ServiceMetaData metaData = null;
            try{
                metaData = ServiceManagerFactory.getServiceMetaData(
                    service.getServiceManagerName(),
                    service.getServiceName()
                );
            }catch(ServiceNotFoundException e){
            }
            if(metaData == null){
                editor = NimbusPropertyEditorManager.findEditor(type);
            }else{
                final ServiceLoader loader = metaData.getServiceLoader();
                editor = loader.findEditor(type);
            }
        }
        return editor;
    }
    
    private String getAttributeValue(Service service, AttributeMethod method){
        final Object targetObj = getTargetObject(service);
        String result = null;
        try{
            final Object val = method.getGetter().invoke(targetObj, (Object[])null);
            if(val == null){
                return null;
            }
            final PropertyEditor editor
                 = findEditor(service, method.getGetter().getReturnType());
            
            if(editor == null){
                result = val.toString();
            }else{
                editor.setValue(val);
                result = editor.getAsText();
            }
            if(result == null){
                return null;
            }
            int maxLength = getAttributeMaxLength();
            if(maxLength > 0 && result.length() > maxLength){
                result = result.substring(0, maxLength);
            }
        }catch(IllegalAccessException e){
            e.printStackTrace();
            return "Can not get!!";
        }catch(InvocationTargetException e){
            e.printStackTrace();
            return "Can not get!!";
        }catch(ServiceNotFoundException e){
            e.printStackTrace();
            return "Can not get!!";
        }
        return result;
    }
    
    private AttributeMethod getAttribute(Class clazz, String name){
        final Method[] methods = clazz.getMethods();
        return (AttributeMethod)getAttributes(methods).get(name);
    }
    
    private Map getAttributes(Method[] methods){
        final Map result = new HashMap();
        for(int i = 0; i < methods.length; i++){
            if(isIgnoreMethod(methods[i])){
                continue;
            }
            final String methodName = methods[i].getName();
            final Class retType = methods[i].getReturnType();
            final Class[] paramTypes = methods[i].getParameterTypes();
            if(!isAttributeMethod(methods[i])){
                continue;
            }
            boolean isAttributeSet = false;
            boolean isAttributeGet = false;
            isAttributeSet = isAttributeSetMethod(
                methodName,
                retType,
                paramTypes
            );
            if(!isAttributeSet){
                isAttributeGet = isAttributeGetMethod(
                    methodName,
                    retType,
                    paramTypes
                );
                if(!isAttributeGet){
                    isAttributeGet = isAttributeIsMethod(
                        methodName,
                        retType,
                        paramTypes
                    );
                }
            }
            if(!isAttributeSet && !isAttributeGet){
                continue;
            }
            final String attrName = getAttributeName(methodName);
            AttributeMethod attrMethod = null;
            if(result.containsKey(attrName)){
                attrMethod = (AttributeMethod)result.get(attrName);
            }else{
                attrMethod = new AttributeMethod();
                result.put(attrName, attrMethod);
            }
            if(isAttributeSet){
                attrMethod.setSetter(methods[i]);
            }else{
                attrMethod.setGetter(methods[i]);
            }
        }
        return result;
    }
    
    private boolean isAttributeMethod(Method method){
        final String methodName = method.getName();
        final Class retType = method.getReturnType();
        final Class[] paramTypes = method.getParameterTypes();
        return isAttributeGetMethod(methodName, retType, paramTypes)
            || isAttributeIsMethod(methodName, retType, paramTypes)
            || isAttributeSetMethod(methodName, retType, paramTypes);
    }
    
    private boolean isAttributeGetMethod(
        String methodName,
        Class retType,
        Class[] paramTypes
    ){
        return methodName.startsWith("get")
             && methodName.length() > 3
             && !retType.equals(Void.TYPE)
             && paramTypes.length == 0;
    }
    
    private boolean isAttributeIsMethod(
        String methodName,
        Class retType,
        Class[] paramTypes
    ){
        return methodName.startsWith("is")
             && methodName.length() > 2
             && retType.equals(Boolean.TYPE)
             && paramTypes.length == 0;
    }
    
    private boolean isAttributeSetMethod(
        String methodName,
        Class retType,
        Class[] paramTypes
    ){
        return methodName.startsWith("set")
             && methodName.length() > 3
             && retType.equals(Void.TYPE)
             && paramTypes.length == 1;
    }
    
    private String getAttributeName(String methodName){
        final int length = methodName.length();
        if(methodName.startsWith("get") && length > 3){
            return methodName.substring(3);
        }else if(methodName.startsWith("is") && length > 2){
            return methodName.substring(2);
        }else if(methodName.startsWith("set") && length > 3){
            return methodName.substring(3);
        }
        return null;
    }
    
    private String getAttributeType(AttributeMethod method){
        final boolean hasSetter = method.getSetter() != null;
        final boolean hasGetter = method.getGetter() != null;
        if(hasSetter && hasGetter){
            return ATTRIBUTE_READ_AND_WRITE;
        }else if(hasSetter){
            return ATTRIBUTE_WRITE_ONLY;
        }else{
            return ATTRIBUTE_READ_ONLY;
        }
    }
    
    private boolean isIgnoreMethod(Method method){
        Class declaringClass = method.getDeclaringClass();
        if(declaringClass.equals(Object.class)){
            return true;
        }
        if(ignoreMethodMap == null){
            return false;
        }
        final List ignoreMethods = ignoreMethodMap.getValueList(
            declaringClass
        );
        if(ignoreMethods == null || ignoreMethods.size() == 0){
            return false;
        }
        for(int i = 0, imax = ignoreMethods.size(); i < imax; i++){
            final Method ignoreMethod = (Method)ignoreMethods.get(i);
            if(ignoreMethod.equals(method)){
                return true;
            }else if(ignoreMethod.getName().equals(method.getName())
                && ignoreMethod.getParameterTypes().length
                        == method.getParameterTypes().length
                && ignoreMethod.getReturnType().equals(method.getReturnType())
            ){
                Class[] ignoreParamTypes = ignoreMethod.getParameterTypes();
                Class[] paramTypes = method.getParameterTypes();
                if(ignoreParamTypes.length == 0){
                    return true;
                }
                boolean isMatch = true;
                for(int j = 0; j < ignoreParamTypes.length; j++){
                    if(!ignoreParamTypes[j].equals(paramTypes[j])){
                        isMatch = false;
                        break;
                    }
                }
                if(isMatch){
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * サーブレットの破棄処理を行う。<p>
     * サービス定義のアンロードを行う。<br>
     */
    public void destroy(){
        final String[] servicePaths = getServicePaths();
        if(servicePaths != null && servicePaths.length != 0){
            for(int i = servicePaths.length; --i >= 0;){
                ServiceManagerFactory.unloadManager(servicePaths[i]);
            }
        }
        
        final String[] serviceDirs = getServiceDirs();
        if(serviceDirs != null && serviceDirs.length != 0){
            final String serviceFileFilter = getServiceFileFilter();
            for(int i = serviceDirs.length; --i >= 0;){
                ServiceManagerFactory.unloadManagers(
                    serviceDirs[i],
                    serviceFileFilter
                );
            }
        }
    }
    
    private class AttributeMethod{
        private Method getterMethod;
        private Method setterMethod;
        private Class attributeType;
        public void setGetter(Method getter){
            getterMethod = getter;
            if(getter != null && attributeType == null){
                attributeType = getter.getReturnType();
            }
        }
        public Method getGetter(){
            return getterMethod;
        }
        public void setSetter(Method setter){
            setterMethod = setter;
            if(setter != null && attributeType == null){
                attributeType = setter.getParameterTypes()[0];
            }
        }
        public Method getSetter(){
            return setterMethod;
        }
        public Class getType(){
            return attributeType;
        }
    }
    
    private class MethodSignature implements Comparable{
        private String methodName;
        private Class[] paramTypes;
        
        public MethodSignature(Method method){
            methodName = method.getName();
            paramTypes = method.getParameterTypes();
        }
        
        public MethodSignature(String method)
         throws IllegalArgumentException, ClassNotFoundException{
            String tmp = method;
            int index = tmp.indexOf('(');
            if(index == -1 || index == 0 || index == tmp.length() - 1){
                throw new IllegalArgumentException("Invalid method : " + method);
            }
            methodName = tmp.substring(0, index);
            tmp = tmp.substring(index + 1);
            index = tmp.indexOf(')');
            if(index == -1 || index != tmp.length() - 1){
                throw new IllegalArgumentException("Invalid method : " + method);
            }
            if(index == 0){
                paramTypes = null;
            }else{
                tmp = tmp.substring(0, index);
                final StringTokenizer tokens = new StringTokenizer(tmp, ",");
                final List paramTypeList = new ArrayList();
                final ClassLoader loader = NimbusClassLoader.getInstance();
                while(tokens.hasMoreTokens()){
                    final String paramTypeStr = tokens.nextToken().trim();
                    if(paramTypeStr.length() == 0){
                        throw new IllegalArgumentException("Invalid method : " + method);
                    }
                    Class paramType = null;
                    if(paramTypeStr.equals("boolean")){
                        paramType = Boolean.TYPE;
                    }else if(paramTypeStr.equals("byte")){
                        paramType = Byte.TYPE;
                    }else if(paramTypeStr.equals("char")){
                        paramType = Character.TYPE;
                    }else if(paramTypeStr.equals("short")){
                        paramType = Short.TYPE;
                    }else if(paramTypeStr.equals("int")){
                        paramType = Integer.TYPE;
                    }else if(paramTypeStr.equals("long")){
                        paramType = Long.TYPE;
                    }else if(paramTypeStr.equals("float")){
                        paramType = Float.TYPE;
                    }else if(paramTypeStr.equals("double")){
                        paramType = Double.TYPE;
                    }else{
                        paramType = Class.forName(paramTypeStr, true, loader);
                    }
                    paramTypeList.add(paramType);
                }
                paramTypes = (Class[])paramTypeList.toArray(new Class[paramTypeList.size()]);
            }
        }
        
        public Method getMethod(Object obj) throws NoSuchMethodException{
            return obj.getClass().getMethod(methodName, paramTypes);
        }
        
        public boolean equals(Object o){
            if(o == null){
                return false;
            }
            if(o == this){
                return true;
            }
            if(o instanceof MethodSignature){
                final MethodSignature comp = (MethodSignature)o;
                if(!methodName.equals(comp.methodName)){
                    return false;
                }
                if(paramTypes == comp.paramTypes){
                    return true;
                }
                if((paramTypes == null && comp.paramTypes != null)
                    || (paramTypes != null && comp.paramTypes == null)
                    || (paramTypes.length != comp.paramTypes.length)
                ){
                    return false;
                }
                for(int i = 0; i < paramTypes.length; i++){
                    if(!paramTypes[i].equals(comp.paramTypes[i])){
                        return false;
                    }
                }
                return true;
            }else{
                return false;
            }
        }
        
        public int hashCode(){
            int hashCode = methodName.hashCode();
            if(paramTypes != null){
                for(int i = 0; i < paramTypes.length; i++){
                    hashCode += paramTypes[i].hashCode();
                }
            }
            return hashCode;
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder(methodName);
            buf.append('(');
            if(paramTypes != null){
                for(int i = 0, max = paramTypes.length; i < max; i++){
                    buf.append(paramTypes[i].getName());
                    if(i != max - 1){
                        buf.append(',');
                    }
                }
            }
            buf.append(')');
            return buf.toString();
        }
        
        public int compareTo(Object obj){
            if(obj instanceof MethodSignature){
                final MethodSignature sig = (MethodSignature)obj;
                if(methodName.equals(sig.methodName)){
                    if(paramTypes == null){
                        if(sig.paramTypes == null){
                            return 0;
                        }else{
                            return -1;
                        }
                    }else{
                        if(sig.paramTypes == null){
                            return 1;
                        }else if(paramTypes.length == sig.paramTypes.length){
                            for(int i = 0; i < paramTypes.length; i++){
                                final int ret = paramTypes[i].getName()
                                    .compareTo(sig.paramTypes[i].getName());
                                if(ret != 0){
                                    return ret;
                                }
                            }
                            return 0;
                        }else{
                            return paramTypes.length > sig.paramTypes.length ? 1 : -1;
                        }
                    }
                }else{
                    return methodName.compareTo(sig.methodName);
                }
            }
            return -1;
        }
    }
}
