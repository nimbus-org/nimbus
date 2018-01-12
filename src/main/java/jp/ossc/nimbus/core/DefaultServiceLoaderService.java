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
package jp.ossc.nimbus.core;

import java.beans.*;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.util.*;
import jp.ossc.nimbus.service.log.*;
import jp.ossc.nimbus.service.message.MessageRecordFactory;
import jp.ossc.nimbus.service.repository.Repository;

/**
 * サービスローダ。<p>
 * サービス定義を読み込み、サービスを登録するローダである。<br>
 * サービスローダは、サービス{@link Service}として実装され、サービスの生成、起動と共に、サービス基盤を起動し、そこに配置される各サービスをホスティングする。<br>
 * 
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">サービス定義ファイルDTD</a>
 */
public class DefaultServiceLoaderService extends ServiceBase
 implements ServiceLoader, DefaultServiceLoaderServiceMBean{
    
    private static final long serialVersionUID = 7335188900913701079L;
    
    // メッセージID定義
    private static final String SVCL_ = "SVCL_";
    private static final String SVCL_0 = SVCL_ + 0;
    private static final String SVCL_00 = SVCL_0 + 0;
    private static final String SVCL_000 = SVCL_00 + 0;
    private static final String SVCL_0000 = SVCL_000 + 0;
    private static final String SVCL_00001 = SVCL_0000 + 1;
    private static final String SVCL_00002 = SVCL_0000 + 2;
    private static final String SVCL_00003 = SVCL_0000 + 3;
    private static final String SVCL_00004 = SVCL_0000 + 4;
    private static final String SVCL_00005 = SVCL_0000 + 5;
    private static final String SVCL_00006 = SVCL_0000 + 6;
    private static final String SVCL_00007 = SVCL_0000 + 7;
    private static final String SVCL_00008 = SVCL_0000 + 8;
    private static final String SVCL_00009 = SVCL_0000 + 9;
    private static final String SVCL_00010 = SVCL_000 + 10;
    private static final String SVCL_00011 = SVCL_000 + 11;
    private static final String SVCL_00012 = SVCL_000 + 12;
    private static final String SVCL_00013 = SVCL_000 + 13;
    private static final String SVCL_00014 = SVCL_000 + 14;
    private static final String SVCL_00015 = SVCL_000 + 15;
    private static final String SVCL_00016 = SVCL_000 + 16;
    private static final String SVCL_00017 = SVCL_000 + 17;
    private static final String SVCL_00018 = SVCL_000 + 18;
    private static final String SVCL_00019 = SVCL_000 + 19;
    private static final String SVCL_00020 = SVCL_000 + 20;
    private static final String SVCL_00021 = SVCL_000 + 21;
    private static final String SVCL_00022 = SVCL_000 + 22;
    private static final String SVCL_00023 = SVCL_000 + 23;
    private static final String SVCL_00024 = SVCL_000 + 24;
    private static final String SVCL_00025 = SVCL_000 + 25;
    private static final String SVCL_00026 = SVCL_000 + 26;
//    private static final String SVCL_00027 = SVCL_000 + 27;
    private static final String SVCL_00028 = SVCL_000 + 28;
    private static final String SVCL_00029 = SVCL_000 + 29;
    private static final String SVCL_00030 = SVCL_000 + 30;
    private static final String SVCL_00031 = SVCL_000 + 31;
    private static final String SVCL_00032 = SVCL_000 + 32;
    private static final String SVCL_00033 = SVCL_000 + 33;
    private static final String SVCL_00034 = SVCL_000 + 34;
    private static final String SVCL_00035 = SVCL_000 + 35;
    private static final String SVCL_00036 = SVCL_000 + 36;
    private static final String SVCL_00037 = SVCL_000 + 37;
    private static final String SVCL_00038 = SVCL_000 + 38;
    
    /**
     * デフォルトのサービス名。<p>
     * {@link #setServiceName(String)}されない場合は、このデフォルト名の後に、"{サービス定義のURL}"を付加したサービス名となる。<br>
     *
     * @see #setServiceName(String)
     */
    protected static final String DEFAULT_NAME
     = DefaultServiceLoaderService.class.getName();
    
    /**
     * デフォルトの{@link ServiceManager}インタフェース実装クラス。<p>
     * 
     * @see #setServiceManagerClassName(String)
     */
    private static final String DEFAULT_SERVICE_MANAGER_CLASS_NAME
     = jp.ossc.nimbus.core.DefaultServiceManagerService.class.getName();
    
    /**
     * VMに登録したシャットダウンフックのマッピング。<p>
     * &lt;manager&gt;要素のshutdown-hook属性にtrueが設定されていた場合、{@link Runtime#addShutdownHook(Thread)}で、ServiceManager毎に停止、破棄を行うスレッドを登録する。<br>
     * VMが起動したままで、ServiceManagerを破棄した場合などは、登録したスレッドが不要になるため、登録したスレッドをstaticに管理している。<br>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="2">値</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
     *   <tr><td>String</td><td>ServiceManagerの名前</td><td>Thread</td><td>シャットダウンフックのスレッド</td></tr>
     * </table>
     */
    private static final Map shutdownHooks = new HashMap();
    
    /**
     * このローダがロードするサービス定義ファイルのURL。<p>
     *
     * @see #setServiceURL(URL)
     * @see #getServiceURL()
     */
    private URL serviceURL;
    
    /**
     * ServiceManagerインタフェースの実装クラス名。<p>
     * デフォルトは、DEFAULT_SERVICE_MANAGER_CLASS_NAME。<br>
     *
     * @see #setServiceManagerClassName(String)
     * @see #getServiceManagerClassName()
     */
    private String serviceManagerClassName = DEFAULT_SERVICE_MANAGER_CLASS_NAME;
    
    /**
     * このローダでロードしたサービス定義のルートメタデータ。<p>
     *
     * @see #getServerMetaData()
     */
    private ServerMetaData serverData;
    
    /**
     * &lt;manager&gt;要素で定義されたServiceManagerの名前と、そのServiceManagerを格納するマップ。<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="2">値</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
     *   <tr><td>String</td><td>ServiceManagerの名前</td><td>{@link ServiceManager}</td><td>ServiceManager</td></tr>
     * </table>
     * 
     * @see #getServiceManagers()
     */
    private Map managerMap;
    
    /**
     * &lt;manager&gt;要素で定義されたServiceManagerと、その子要素として定義された&lt;service&gt;要素のServiceを格納するマップ。<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="5">値</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th colspan="4">内容</th></tr>
     *   <tr rowspan="3"><td rowspan="3">String</td><td rowspan="3">ServiceManagerの名前</td><td rowspan="3">java.util.Map</td><td colspan="4">ServiceMetaDataを格納するマップ</td></tr>
     *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
     *   <tr><td>String</td><td>サービス名</td><td>{@link ServiceMetaData}</td><td>サービスの定義情報</td></tr>
     * </table>
     */
    private Map managersServiceMetaMap;
    
    /**
     * &lt;property-editors&gt;要素で設定されたjava.beans.PropertyEditorを保持するマップ。<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="2">値</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
     *   <tr><td>java.lang.Class</td><td>java.beans.PropertyEditorが編集する型のクラス</td><td>java.lang.Class</td>java.beans.PropertyEditorのクラス<td></td></tr>
     * </table>
     */
    private ClassMappingTree propertyEditors;
    
    /**
     * サービス定義XMLをDTDで評価するかどうかを示すフラグ。<p>
     * 評価する場合true。<br>
     */
    private boolean isValidate;
    
    /**
     * ここまでにロードしたサービスが全て正常に開始できているかをチェックするかどうかを示すフラグ。<p>
     * チェックする場合true。
     */
    private boolean isCheckLoadManagerCompleted;
    
    /**
     * サービスが全て正常に開始できているかをチェックするマネージャ名の集合。<p>
     */
    private Set checkLoadManagerNames;
    
    /**
     * サービスロード構成情報。<p>
     */
    private ServiceLoaderConfig loaderConfig;
    
    /**
     * ロード前のdefault-log要素の情報。<p>
     */
    private DefaultLogMetaData preDefaultLogData;
    
    /**
     * コンストラクタ。<p>
     */
    public DefaultServiceLoaderService(){
        super();
        setServiceName(DEFAULT_NAME);
    }
    
    // ServiceLoaderのJavaDoc
    public void setConfig(ServiceLoaderConfig config){
        loaderConfig = config;
    }
    
    // ServiceLoaderのJavaDoc
    public ServiceLoaderConfig getConfig(){
        return loaderConfig;
    }
    
    // ServiceLoaderのJavaDoc
    public ServerMetaData getServerMetaData(){
        return serverData;
    }
    
    // ServiceLoaderのJavaDoc
    public void setServerMetaData(ServerMetaData data){
        serverData = data;
    }
    
    // ServiceLoaderのJavaDoc
    public void setServiceManagerClassName(String className)
     throws ClassNotFoundException, IllegalArgumentException{
        final Logger logger = getLogger();
        if(className != null && className.length() != 0){
            Class clazz = null;
            try{
                clazz = Class.forName(
                    className,
                    true,
                    NimbusClassLoader.getInstance()
                );
            }catch(ClassNotFoundException e){
                logger.write(
                    SVCL_00001,
                    new Object[]{ServiceManager.class, className},
                    e
                );
                throw e;
            }
            if(ServiceManager.class.isAssignableFrom(clazz)){
                serviceManagerClassName = className;
                logger.write(SVCL_00002, className);
            }else{
                final MessageRecordFactory message = getMessageRecordFactory();
                throw new IllegalArgumentException(
                    message.findEmbedMessage(SVCL_00003, className)
                );
            }
        }
    }
    
    // ServiceLoaderのJavaDoc
    public String getServiceManagerClassName(){
        return serviceManagerClassName;
    }
    
    // ServiceLoaderのJavaDoc
    public void setServiceURL(URL url) throws IllegalArgumentException{
        final Logger logger = getLogger();
        try{
            url.openConnection();
        }catch(IOException e){
            final MessageRecordFactory message = getMessageRecordFactory();
            throw new IllegalArgumentException(
                message.findEmbedMessage(SVCL_00004, url)
            );
        }
        serviceURL = url;
        logger.write(SVCL_00005, serviceURL);
    }
    
    // ServiceLoaderのJavaDoc
    public URL getServiceURL(){
        return serviceURL;
    }
    
    // ServiceLoaderのJavaDoc
    public void setServicePath(String path) throws IllegalArgumentException{
        final Logger logger = getLogger();
        final URL url = Utility.convertServicePathToURL(path);
        try{
            setServiceURL(url);
            logger.write(SVCL_00006, url);
        }catch(IllegalArgumentException e){
            final MessageRecordFactory message = getMessageRecordFactory();
            throw new IllegalArgumentException(
                message.findEmbedMessage(SVCL_00007, path)
            );
        }
    }
    
    /**
     * サービス定義を読み込み、サービス定義&lt;server&gt;要素メタデータを構築する。<p>
     * ここでは、以下の処理を行う。<br>
     * <ol>
     *   <li>{@link #setServiceURL(URL)}で設定されたURLからサービス定義XMLを読み込み、パースする。</li>
     *   <li>パースしたサービス定義XMLから、{@link ServerMetaData}を生成する。ServerMetaDataの生成の過程で、各要素に対応するメタデータも生成される。</li>
     * </ol>
     *
     * @exception ParserConfigurationException XMLパーサの生成に失敗した場合
     * @exception IOException {@link #setServiceURL(URL)}で設定されたURLにサービス定義XMLが見つからない場合
     * @exception SAXException サービス定義XMLのパースに失敗した場合
     * @exception DeploymentException サービス定義の設定に誤りがある場合
     */
    public void loadServerMetaData()
     throws IOException, ParserConfigurationException, SAXException,
            DeploymentException{
        final Logger logger = getLogger();
        
        logger.write(SVCL_00008);
        
        if(serverData != null && serviceURL == null){
            return;
        }
        
        if(serviceURL == null){
            serviceURL = Utility.getDefaultServiceURL();
            logger.write(SVCL_00009, serviceURL);
        }
        serverData = loadServerMetaData(serviceURL.openStream());
    }
    
    /**
     * サービス定義を読み込み、サービス定義&lt;server&gt;要素メタデータを構築する。<p>
     * ここでは、以下の処理を行う。<br>
     * <ol>
     *   <li>指定されたURLからサービス定義XMLを読み込み、パースする。</li>
     *   <li>パースしたサービス定義XMLから、{@link ServerMetaData}を生成する。ServerMetaDataの生成の過程で、各要素に対応するメタデータも生成される。</li>
     * </ol>
     *
     * @param url サービス定義URL
     * @exception ParserConfigurationException XMLパーサの生成に失敗した場合
     * @exception IOException {@link #setServiceURL(URL)}で設定されたURLにサービス定義XMLが見つからない場合
     * @exception SAXException サービス定義XMLのパースに失敗した場合
     * @exception DeploymentException サービス定義の設定に誤りがある場合
     */
    protected ServerMetaData loadServerMetaData(URL url)
     throws IOException, ParserConfigurationException, SAXException,
            DeploymentException{
        
        if(url == null){
            return null;
        }
        return loadServerMetaData(url.openStream());
    }
    
    /**
     * サービス定義を読み込み、サービス定義&lt;server&gt;要素メタデータを構築する。<p>
     * ここでは、以下の処理を行う。<br>
     * <ol>
     *   <li>指定されたストリームからサービス定義XMLを読み込み、パースする。</li>
     *   <li>パースしたサービス定義XMLから、{@link ServerMetaData}を生成する。ServerMetaDataの生成の過程で、各要素に対応するメタデータも生成される。</li>
     * </ol>
     *
     * @param is サービス定義入力ストリーム
     * @exception ParserConfigurationException XMLパーサの生成に失敗した場合
     * @exception IOException {@link #setServiceURL(URL)}で設定されたURLにサービス定義XMLが見つからない場合
     * @exception SAXException サービス定義XMLのパースに失敗した場合
     * @exception DeploymentException サービス定義の設定に誤りがある場合
     */
    public ServerMetaData loadServerMetaData(InputStream is)
     throws IOException, ParserConfigurationException, SAXException,
            DeploymentException{
        final InputSource inputSource = new InputSource(is);
        final DocumentBuilderFactory domFactory
             = DocumentBuilderFactory.newInstance();
        domFactory.setValidating(isValidate());
        final DocumentBuilder builder = domFactory.newDocumentBuilder();
        final NimbusEntityResolver resolver = new NimbusEntityResolver();
        builder.setEntityResolver(resolver);
        final MyErrorHandler handler = new MyErrorHandler();
        builder.setErrorHandler(handler);
        final Document doc = builder.parse(inputSource);
        if(handler.isError()){
            final MessageRecordFactory message = getMessageRecordFactory();
            throw new DeploymentException(
                message.findEmbedMessage(SVCL_00033, serviceURL)
            );
        }
        
        final ServerMetaData serverData = new ServerMetaData(this, serviceURL);
        final DocumentType docType = doc.getDoctype();
        if(docType != null){
            serverData.setDocType(
                "<!DOCTYPE " + docType.getName() + " PUBLIC \""
                     + docType.getPublicId() + "\" \""
                     + docType.getSystemId() + "\">"
            );
        }
        if(inputSource.getEncoding() != null){
            serverData.setEncoding(inputSource.getEncoding());
        }
        serverData.importXML(doc.getDocumentElement());
        
        final Properties props = serverData.getProperties();
        final Object[] propKeys = props.keySet().toArray();
        for(int i = 0; i < propKeys.length; i++){
            final String propKey = (String)propKeys[i];
            String prop = props.getProperty(propKey);
            // システムプロパティの置換
            prop = Utility.replaceSystemProperty(prop);
            // サービスローダ構成プロパティの置換
            prop = Utility.replaceServiceLoderConfig(
                prop,
                getConfig()
            );
            // サーバプロパティの置換
            prop = Utility.replaceServerProperty(prop);
            ServiceManagerFactory.setProperty(propKey, prop);
        }
        return serverData;
    }
    
    /**
     * サービス定義&lt;server&gt;要素メタデータを配置する。<p>
     *
     * @param serverData サービス定義&lt;server&gt;要素メタデータ
     * @exception ParserConfigurationException XMLパーサの生成に失敗した場合
     * @exception IOException &lt;ref-url&gt;要素で指定されたURLのサービス定義XMLが見つからない場合
     * @exception SAXException サービス定義XMLのパースに失敗した場合
     * @exception DeploymentException サービス定義の設定に誤りがある場合
     */
    protected void deployServerMetaData(ServerMetaData serverData)
     throws IOException, ParserConfigurationException, SAXException,
            DeploymentException{
        final Logger logger = getLogger();
        
        logger.write(SVCL_00011, serverData);
        
        checkRefURL(serverData);
        
        final DefaultLogMetaData defaultLogData
             = serverData.getDefaultLog();
        deployDefaultLogMetaData(defaultLogData);
        
        deployPropertyEditors();
        
        // サーバLoggerの設定
        final ServiceNameMetaData logData = serverData.getLog();
        if(logData != null){
            final String managerName = logData.getManagerName();
            final String logName = logData.getServiceName();
            if(managerName != null && logName != null){
                ServiceManagerFactory.setLogger(managerName, logName);
            }
        }
        
        // サーバMessageRecordFactoryの設定
        final ServiceNameMetaData messageData = serverData.getMessage();
        if(messageData != null){
            final String managerName = messageData.getManagerName();
            final String messageName = messageData.getServiceName();
            if(managerName != null && messageName != null){
                ServiceManagerFactory.setMessageRecordFactory(
                    managerName,
                    messageName
                );
            }
        }
        
        // サーバRepositoryの設定
        final ServiceNameMetaData repositoryData = serverData.getRepository();
        if(repositoryData != null){
            final String managerName = repositoryData.getManagerName();
            final String repositoryName = repositoryData.getServiceName();
            if(managerName != null && repositoryName != null){
                ServiceManagerFactory.setManagerRepository(
                    managerName,
                    repositoryName
                );
            }
        }
        
        final Iterator managers = serverData.getManagers().iterator();
        while(managers.hasNext()){
            final ManagerMetaData managerData
                 = (ManagerMetaData)managers.next();
            deployManagerMetaData(managerData);
        }
    }
    
    /**
     * &lt;ref-url&gt;要素で指定されたURLのサービス定義と、このローダでロードするサービス定義の相関チェックを行う。<p>
     * 定義されているサービス、依存関係に定義されているサービスが、このサービス定義内と、&lt;ref-url&gt;要素で指定されたURLのサービス定義内に全て存在するかチェックする。また、依存関係が、相互に行われていないかチェックする。<br>
     *
     * @exception ParserConfigurationException XMLパーサの生成に失敗した場合
     * @exception IOException &lt;ref-url&gt;要素で指定されたURLのサービス定義XMLが見つからない場合
     * @exception SAXException サービス定義XMLのパースに失敗した場合
     * @exception DeploymentException サービス定義の設定に誤りがある場合
     */
    protected void checkRefURL(ServerMetaData serverData)
     throws IOException, ParserConfigurationException, SAXException,
            DeploymentException{
        
        final Set refURLSet = serverData.getReferenceURL();
        if(refURLSet == null || refURLSet.size() == 0){
            return;
        }
        
        final Map refServices = new HashMap();
        
        final Iterator refURLs = refURLSet.iterator();
        while(refURLs.hasNext()){
            final String refURLStr = (String)refURLs.next();
            URL refURL = null;
            try{
                refURL = new URL(refURLStr);
                refURL.openConnection();
            }catch(MalformedURLException e){
                refURL = null;
            }catch(IOException e){
                refURL = null;
            }
            if(refURL == null){
                 String urlString = serviceURL.toString();
                urlString = urlString.substring(
                    0,
                    urlString.lastIndexOf('/') + 1
                );
                try{
                    refURL = new URL(urlString + refURLStr);
                    refURL.openConnection();
                }catch(MalformedURLException e){
                    refURL = null;
                }catch(IOException e){
                    refURL = null;
                }
                if(refURL == null){
                    try{
                        refURL = Utility.convertServicePathToURL(refURLStr);
                    }catch(IllegalArgumentException e){
                        throw new DeploymentException(
                            "ref-url tag is illegal value : " + refURLStr
                        );
                    }
                }
            }
            final ServerMetaData refServerData = loadServerMetaData(refURL);
            final Iterator managers = refServerData.getManagers().iterator();
            while(managers.hasNext()){
                final ManagerMetaData managerData
                     = (ManagerMetaData)managers.next();
                final String managerName = managerData.getName();
                if(refServices.containsKey(managerName)){
                    ((Map)refServices.get(managerName))
                        .putAll(managerData.getServices());
                }else{
                    refServices.put(
                        managerName,
                        new HashMap(managerData.getServices())
                    );
                }
            }
        }
        
        Iterator managers = serverData.getManagers().iterator();
        while(managers.hasNext()){
            final ManagerMetaData managerData
                 = (ManagerMetaData)managers.next();
            final String managerName = managerData.getName();
            if(refServices.containsKey(managerName)){
                ((Map)refServices.get(managerName))
                    .putAll(managerData.getServices());
            }else{
                refServices.put(
                    managerName,
                    new HashMap(managerData.getServices())
                );
            }
        }
        
        // server要素の子要素manager-repositoryの依存関係のチェック
        checkDepends(refServices, serverData.getRepository());
        
        // server要素の子要素logの依存関係のチェック
        checkDepends(refServices, serverData.getLog());
        
        // server要素の子要素logの依存関係のチェック
        checkDepends(refServices, serverData.getMessage());
        
        managers = serverData.getManagers().iterator();
        while(managers.hasNext()){
            final ManagerMetaData managerData
                 = (ManagerMetaData)managers.next();
            
            // manager要素の子要素repositoryの依存関係のチェック
            checkDepends(refServices, managerData.getRepository());
            
            // manager要素の子要素logの依存関係のチェック
            checkDepends(refServices, managerData.getLog());
            
            // manager要素の子要素logの依存関係のチェック
            checkDepends(refServices, managerData.getMessage());
            
            // service要素の子要素dependsの依存関係のチェック
            final Map services = managerData.getServices();
            final Iterator serviceDatas = services.values().iterator();
            while(serviceDatas.hasNext()){
                final ServiceMetaData serviceData
                     = (ServiceMetaData)serviceDatas.next();
                final Iterator depends
                     = serviceData.getDepends().iterator();
                while(depends.hasNext()){
                    final ServiceMetaData.DependsMetaData dependsData
                         = (ServiceMetaData.DependsMetaData)depends.next();
                    checkDepends(refServices, serviceData, dependsData);
                }
            }
        }
    }
    
    private void checkDepends(
        Map refServices,
        ServiceNameMetaData dependsData
    )throws DeploymentException{
        if(dependsData == null){
            return;
        }
        final String depManagerName = dependsData.getManagerName();
        final String depServiceName = dependsData.getServiceName();
        if(refServices.containsKey(depManagerName)){
            final Map services = (Map)refServices.get(depManagerName);
            if(services.containsKey(depServiceName)){
                return;
            }
        }
        final MessageRecordFactory message = getMessageRecordFactory();
        throw new DeploymentException(
            message.findEmbedMessage(
                SVCL_00036,
                new Object[]{depManagerName, depServiceName}
            )
        );
    }
    
    private void checkDepends(
        Map refServices,
        ServiceMetaData serviceData,
        ServiceMetaData.DependsMetaData dependsData
    )throws DeploymentException{
        final ManagerMetaData managerData = serviceData.getManager();
        final String managerName = managerData.getName();
        final String serviceName = serviceData.getName();
        
        final String depManagerName = dependsData.getManagerName();
        final String depServiceName = dependsData.getServiceName();
        
        final Map ref = (Map)refServices.get(depManagerName);
        if(ref == null || !ref.containsKey(depServiceName)){
            final MessageRecordFactory message = getMessageRecordFactory();
            throw new DeploymentException(
                message.findEmbedMessage(
                    SVCL_00036,
                    new Object[]{depManagerName, depServiceName}
                )
            );
        }else{
            final ServiceMetaData depServiceData
                 = (ServiceMetaData)ref.get(depServiceName);
            final Iterator deps = depServiceData.getDepends().iterator();
            while(deps.hasNext()){
                final ServiceMetaData.DependsMetaData depdepServiceData
                     = (ServiceMetaData.DependsMetaData)deps.next();
                final String depdepManagerName
                     = depdepServiceData.getManagerName();
                final String depdepServiceName
                     = depdepServiceData.getServiceName();
                if(depdepManagerName.equals(managerName)
                   && depdepServiceName.equals(serviceName)
                ){
                    final MessageRecordFactory message
                         = getMessageRecordFactory();
                    throw new DeploymentException(
                        message.findEmbedMessage(
                            SVCL_00037,
                            new Object[]{
                                managerName, serviceName,
                                depdepManagerName, depdepServiceName
                            }
                        )
                    );
                }
                checkDepends(refServices, serviceData, depdepServiceData);
            }
        }
    }
    
    /**
     * サービス定義&lt;server&gt;要素メタデータを削除する。<p>
     *
     * @param serverData サービス定義&lt;server&gt;要素メタデータ
     * @exception DeploymentException サービス定義の設定に誤りがある場合
     */
    protected void undeployServerMetaData(ServerMetaData serverData)
     throws DeploymentException{
        
        final Iterator managers = managerMap.values().iterator();
        while(managers.hasNext()){
            final ServiceManager manager = (ServiceManager)managers.next();
            shutdownServiceManager(manager);
        }
        
        // サーバRepositoryの設定解除
        final ServiceNameMetaData repositoryData = serverData.getRepository();
        if(repositoryData != null){
            final String managerName = repositoryData.getManagerName();
            final String repositoryName = repositoryData.getServiceName();
            if(managerName != null && repositoryName != null){
                if(ServiceManagerFactory
                    .isRegisteredService(managerName, repositoryName)){
                    ServiceManagerFactory.setManagerRepository(
                        (Repository)null
                    );
                }
            }
        }
        
        final DefaultLogMetaData defaultLogData
             = serverData.getDefaultLog();
        undeployDefaultLogMetaData(defaultLogData);
    }
    
    /**
     * サーバLoggerの設定を行う。<p>
     *
     * @param defaultLogData サービス定義&lt;default-log&gt;要素メタデータ
     */
    protected void deployDefaultLogMetaData(DefaultLogMetaData defaultLogData){
        if(defaultLogData == null){
            return;
        }
        
        preDefaultLogData = new DefaultLogMetaData(defaultLogData.getParent());
        
        final Logger logger = getLogger();
        DefaultLogMetaData.LogCategoryMetaData categoryData
             = defaultLogData.getDebug();
        if(categoryData != null){
            final DefaultLogMetaData.LogCategoryMetaData preDebugData
                 = new DefaultLogMetaData.LogCategoryMetaData(preDefaultLogData);
            preDebugData.setOutput(
                ServiceManagerFactory.DEFAULT_LOGGER.isSystemDebugEnabled()
            );
            ServiceManagerFactory.DEFAULT_LOGGER
                .setSystemDebugEnabled(categoryData.isOutput());
            ServiceManagerFactory.DEFAULT_LOGGER
                .setDebugEnabled(categoryData.isOutput());
            preDefaultLogData.setDebug(preDebugData);
            if(ServiceManagerFactory.DEFAULT_LOGGER.isSystemDebugEnabled()){
                logger.write(SVCL_00012, LogService.SYSTEM_DEBUG_CATEGORY_LABEL);
            }else{
                logger.write(SVCL_00013, LogService.SYSTEM_DEBUG_CATEGORY_LABEL);
            }
        }else{
            ServiceManagerFactory.DEFAULT_LOGGER.setSystemDebugEnabled(false);
            ServiceManagerFactory.DEFAULT_LOGGER.setDebugEnabled(false);
        }
        categoryData = defaultLogData.getInformation();
        if(categoryData != null){
            final DefaultLogMetaData.LogCategoryMetaData preInfoData
                 = new DefaultLogMetaData.LogCategoryMetaData(preDefaultLogData);
            preInfoData.setOutput(
                ServiceManagerFactory.DEFAULT_LOGGER.isSystemInfoEnabled()
            );
            ServiceManagerFactory.DEFAULT_LOGGER
                .setSystemInfoEnabled(categoryData.isOutput());
            preDefaultLogData.setInformation(preInfoData);
            if(ServiceManagerFactory.DEFAULT_LOGGER.isSystemInfoEnabled()){
                logger.write(SVCL_00012, LogService.SYSTEM_INFO_CATEGORY_LABEL);
            }else{
                logger.write(SVCL_00013, LogService.SYSTEM_INFO_CATEGORY_LABEL);
            }
        }else{
            ServiceManagerFactory.DEFAULT_LOGGER.setSystemInfoEnabled(true);
        }
        categoryData = defaultLogData.getWarning();
        if(categoryData != null){
            final DefaultLogMetaData.LogCategoryMetaData preWarnData
                 = new DefaultLogMetaData.LogCategoryMetaData(preDefaultLogData);
            preWarnData.setOutput(
                ServiceManagerFactory.DEFAULT_LOGGER.isSystemWarnEnabled()
            );
            ServiceManagerFactory.DEFAULT_LOGGER
                .setSystemWarnEnabled(categoryData.isOutput());
            preDefaultLogData.setWarning(preWarnData);
            if(ServiceManagerFactory.DEFAULT_LOGGER.isSystemWarnEnabled()){
                logger.write(SVCL_00012, LogService.SYSTEM_WARN_CATEGORY_LABEL);
            }else{
                logger.write(SVCL_00013, LogService.SYSTEM_WARN_CATEGORY_LABEL);
            }
        }else{
            ServiceManagerFactory.DEFAULT_LOGGER.setSystemWarnEnabled(true);
        }
        categoryData = defaultLogData.getError();
        if(categoryData != null){
            final DefaultLogMetaData.LogCategoryMetaData preErrorData
                 = new DefaultLogMetaData.LogCategoryMetaData(preDefaultLogData);
            preErrorData.setOutput(
                ServiceManagerFactory.DEFAULT_LOGGER.isSystemErrorEnabled()
            );
            ServiceManagerFactory.DEFAULT_LOGGER
                .setSystemErrorEnabled(categoryData.isOutput());
            preDefaultLogData.setError(preErrorData);
            if(ServiceManagerFactory.DEFAULT_LOGGER.isSystemErrorEnabled()){
                logger.write(SVCL_00012, LogService.SYSTEM_ERROR_CATEGORY_LABEL);
            }else{
                logger.write(SVCL_00013, LogService.SYSTEM_ERROR_CATEGORY_LABEL);
            }
        }else{
            ServiceManagerFactory.DEFAULT_LOGGER.setSystemErrorEnabled(true);
        }
        categoryData = defaultLogData.getFatal();
        if(categoryData != null){
            final DefaultLogMetaData.LogCategoryMetaData preFatalData
                 = new DefaultLogMetaData.LogCategoryMetaData(preDefaultLogData);
            preFatalData.setOutput(
                ServiceManagerFactory.DEFAULT_LOGGER.isSystemFatalEnabled()
            );
            ServiceManagerFactory.DEFAULT_LOGGER
                .setSystemFatalEnabled(categoryData.isOutput());
            preDefaultLogData.setFatal(preFatalData);
            if(ServiceManagerFactory.DEFAULT_LOGGER.isSystemFatalEnabled()){
                logger.write(SVCL_00012, LogService.SYSTEM_FATAL_CATEGORY_LABEL);
            }else{
                logger.write(SVCL_00013, LogService.SYSTEM_FATAL_CATEGORY_LABEL);
            }
        }else{
            ServiceManagerFactory.DEFAULT_LOGGER.setSystemFatalEnabled(true);
        }
    }
    
    /**
     * サーバLoggerの設定をデフォルトの設定に戻す。<p>
     *
     * @param defaultLogData サービス定義&lt;default-log&gt;要素メタデータ
     */
    protected void undeployDefaultLogMetaData(
        DefaultLogMetaData defaultLogData
    ){
        if(defaultLogData == null && preDefaultLogData == null){
            return;
        }
        if(preDefaultLogData.getDebug() != null){
            ServiceManagerFactory.DEFAULT_LOGGER.setSystemDebugEnabled(
                preDefaultLogData.getDebug().isOutput()
            );
            ServiceManagerFactory.DEFAULT_LOGGER.setDebugEnabled(
                preDefaultLogData.getDebug().isOutput()
            );
        }
        if(preDefaultLogData.getInformation() != null){
            ServiceManagerFactory.DEFAULT_LOGGER.setSystemInfoEnabled(
                preDefaultLogData.getInformation().isOutput()
            );
        }
        if(preDefaultLogData.getWarning() != null){
            ServiceManagerFactory.DEFAULT_LOGGER.setSystemWarnEnabled(
                preDefaultLogData.getWarning().isOutput()
            );
        }
        if(preDefaultLogData.getError() != null){
            ServiceManagerFactory.DEFAULT_LOGGER.setSystemErrorEnabled(
                preDefaultLogData.getError().isOutput()
            );
        }
        if(preDefaultLogData.getFatal() != null){
            ServiceManagerFactory.DEFAULT_LOGGER.setSystemFatalEnabled(
                preDefaultLogData.getFatal().isOutput()
            );
        }
    }
    
    /**
     * サービス定義&lt;property-editors&gt;要素で定義されたjava.beans.PropertyEditorをロードして、java.beans.PropertyEditorManagerに登録する。<p>
     */
    protected void deployPropertyEditors(){
        final Logger logger = getLogger();
        
        logger.write(SVCL_00014);
        
        final Map propertyEditors = serverData.getPropertyEditors();
        final Iterator editTypes = propertyEditors.keySet().iterator();
        final ClassLoader loader = NimbusClassLoader.getInstance();
        while(editTypes.hasNext()){
            final String typeName = (String)editTypes.next();
            final String editorClassName
                 = (String)propertyEditors.get(typeName);
            
            Class type = null;
            Class editorClass = null;
            try{
                type = Class.forName(typeName, true, loader);
            }catch(ClassNotFoundException e){
                logger.write(SVCL_00015, typeName, e);
                continue;
            }
            try{
                editorClass = Class.forName(editorClassName, true, loader);
            }catch(ClassNotFoundException e){
                logger.write(SVCL_00016, editorClassName, e);
                continue;
            }
            this.propertyEditors.add(type, editorClass);
            logger.write(SVCL_00017, new Object[]{type, editorClass});
        }
    }
    
    /**
     * サービス定義&lt;manager&gt;要素メタデータを配置する。<p>
     *
     * @param managerData サービス定義&lt;manager&gt;要素メタデータ
     * @exception DeploymentException サービス定義の設定に誤りがある場合
     */
    protected void deployManagerMetaData(ManagerMetaData managerData)
     throws DeploymentException{
        final Logger logger = getLogger();
        
        logger.write(SVCL_00018, managerData);
        
        final String name = managerData.getName();
        ServiceManager manager = ServiceManagerFactory.findManager(name);
        if(manager == null){
            manager = (ServiceManager)managerMap.get(name);
        }
        if(manager == null){
            try{
                final Class clazz = Class.forName(
                    serviceManagerClassName,
                    true,
                    NimbusClassLoader.getInstance()
                );
                manager = (ServiceManager)clazz.newInstance();
            }catch(Exception e){
                final MessageRecordFactory message = getMessageRecordFactory();
                throw new DeploymentException(
                    message.findEmbedMessage(SVCL_00019, name),
                    e
                );
            }
            manager.setServiceName(name);
            manager.setServiceManagerName(name);
            logger.write(SVCL_00020, name);
        }
        manager.addServiceLoader(this);
        managerMap.put(name, manager);
        
        // ServiceManagerのLoggerの設定
        final ServiceNameMetaData logData = managerData.getLog();
        if(logData != null){
            final String managerName = logData.getManagerName();
            final String logName = logData.getServiceName();
            if(managerName != null && logName != null){
                manager.setSystemLoggerServiceName(
                    new ServiceName(managerName, logName)
                );
            }
        }
        
        // ServiceManagerのMessageRecordFactoryの設定
        final ServiceNameMetaData messageData = managerData.getMessage();
        if(messageData != null){
            final String managerName = messageData.getManagerName();
            final String messageName = messageData.getServiceName();
            if(managerName != null && messageName != null){
                manager.setSystemMessageRecordFactoryServiceName(
                    new ServiceName(managerName, messageName)
                );
            }
        }
        
        // ServiceManagerのRepositoryの設定
        final ServiceNameMetaData repositoryData = managerData.getRepository();
        if(repositoryData != null){
            final String managerName = repositoryData.getManagerName();
            final String repositoryName = repositoryData.getServiceName();
            if(managerName != null && repositoryName != null){
                manager.setServiceRepository(managerName, repositoryName);
            }
        }
        
        if(!managersServiceMetaMap.containsKey(name)){
            final Map serviceMetaMap = new HashMap();
            managersServiceMetaMap.put(name, serviceMetaMap);
        }
        final Map serviceDataMap = managerData.getServices();
        final Iterator serviceNames = serviceDataMap.keySet().iterator();
        while(serviceNames.hasNext()){
            final String serviceName = (String)serviceNames.next();
            final ServiceMetaData serviceData
                 = (ServiceMetaData)serviceDataMap.get(serviceName);
            try{
                deployServiceMetaData(serviceData);
            }catch(DeploymentException e){
                logger.write(SVCL_00038, new Object[]{managerName, serviceName}, e);
            }
        }
    }
    
    /**
     * サービス定義&lt;service&gt;要素メタデータを配置する。<p>
     *
     * @param serviceData サービス定義&lt;service&gt;要素メタデータ
     * @exception DeploymentException サービス定義の設定に誤りがある場合
     */
    protected void deployServiceMetaData(ServiceMetaData serviceData)
     throws DeploymentException{
        final Logger logger = getLogger();
        
        logger.write(SVCL_00021, serviceData);
        
        final String managerName = serviceData.getManager().getName();
        final String serviceName = serviceData.getName();
        
        ServiceManager manager
             = ServiceManagerFactory.findManager(managerName);
        if(manager == null){
            manager = (ServiceManager)managerMap.get(managerName);
        }
        if(manager == null){
            final MessageRecordFactory message = getMessageRecordFactory();
            throw new DeploymentException(
                message.findEmbedMessage(SVCL_00029, managerName)
            );
        }
        
        final Map serviceMetaMap
             = (Map)managersServiceMetaMap.get(managerName);
        if(serviceData.isTemplate()){
            if(manager.isRegisteredService(serviceName) || serviceMetaMap.containsKey(serviceName)){
                final MessageRecordFactory message = getMessageRecordFactory();
                throw new DeploymentException(
                    message.findEmbedMessage(SVCL_00034, new Object[]{managerName, serviceName})
                );
            }
            serviceMetaMap.put(serviceName, serviceData);
        }else{
            serviceData = serviceData.applyTemplate(this);
            
            if(manager.isRegisteredService(serviceName) || serviceMetaMap.containsKey(serviceName)){
                final MessageRecordFactory message = getMessageRecordFactory();
                throw new DeploymentException(
                    message.findEmbedMessage(SVCL_00034, new Object[]{managerName, serviceName})
                );
            }
            
            logger.write(SVCL_00023, new Object[]{managerName, serviceName});
            
            serviceMetaMap.put(serviceName, serviceData);
            try{
                manager.registerService(serviceData);
            }catch(Exception e){
                logger.write(SVCL_00022, new Object[]{managerName, serviceName}, e);
                throw new DeploymentException(e);
            }
        }
    }
    
    // ServiceLoaderのJavaDoc
    public void loadService(String managerName, String serviceName)
     throws DeploymentException{
        final ServiceMetaData serviceData
             = getServiceMetaData(managerName, serviceName);
        if(serviceData == null){
            final MessageRecordFactory message = getMessageRecordFactory();
            throw new DeploymentException(
                message.findEmbedMessage(
                    SVCL_00035,
                    new Object[]{managerName, serviceName}
                )
            );
        }
        deployServiceMetaData(serviceData);
    }
    
    // ServiceLoaderのJavaDoc
    public void deployService(ServiceMetaData serviceData)
     throws DeploymentException{
        deployServiceMetaData(serviceData);
    }
    
    /**
     * ローダの初期化処理を行う。<p>
     * ここでは、以下の処理を行う。<br>
     * <ol>
     *   <li>{@link ServiceManagerFactory}へ自分自身を登録する。</li>
     *   <li>インスタンス変数を生成する。</li>
     * </ol>
     */
    public void createService(){
        final Logger logger = getLogger();
        
        if(serverData == null && serviceURL == null){
            serviceURL = Utility.getDefaultServiceURL();
            logger.write(SVCL_00009, serviceURL);
        }
        
        if(serviceURL != null){
            final String myName = getServiceName();
            setServiceName(
                myName == null ? (DEFAULT_NAME + '{' + serviceURL + '}') : myName
            );
        }
        
        if(serviceURL != null){
            ServiceManagerFactory.registerLoader(this);
        }
        
        managerMap = new LinkedHashMap();
        managersServiceMetaMap = new HashMap();
        propertyEditors = new ClassMappingTree();
    }
    
    /**
     * ローダの開始処理を行う。<p>
     * ここでは、以下の処理を行う。<br>
     * <ol>
     *   <li>{@link #loadServerMetaData()}を呼び出す。</li>
     *   <li>インスタンス変数を初期化する。</li>
     *   <li>{@link #deployServerMetaData(ServerMetaData)}を呼び出す。</li>
     *   <li>&lt;manager&gt;要素で定義された{@link ServiceManager}を起動する。</li>
     * </ol>
     * @exception Exception サービス定義の読み込み、配置に失敗した場合。または、ServiceManagerの生成、開始に失敗した場合
     */
    public void startService() throws Exception{
        final Logger logger = getLogger();
        
        try{
            loadServerMetaData();
        }catch(Exception e){
            logger.write(SVCL_00010, serviceURL, e);
            throw e;
        }
        
        managerMap.clear();
        managersServiceMetaMap.clear();
        propertyEditors.clear();
        
        try{
            deployServerMetaData(serverData);
        }catch(Exception e){
            logger.write(SVCL_00024, serviceURL, e);
            throw e;
        }
        
        final Iterator managers = managerMap.values().iterator();
        while(managers.hasNext()){
            final ServiceManager manager = (ServiceManager)managers.next();
            try{
                startupServiceManager(manager);
            }catch(Exception e){
                logger.write(SVCL_00025, manager.getServiceName(), e);
                manager.destroy();
            }
        }
        if(isCheckLoadManagerCompleted){
            if(checkLoadManagerNames == null){
                ServiceManagerFactory.checkLoadManagerCompleted();
            }else{
                ServiceManagerFactory.checkLoadManagerCompletedBy(
                    checkLoadManagerNames
                );
            }
        }
    }
    
    /**
     * ローダの停止処理を行う。<p>
     * <ol>
     *   <li>&lt;manager&gt;要素で定義された{@link ServiceManager}を停止する。</li>
     *   <li>&lt;manager&gt;要素のshutdown-hook属性で定義された値に従って、{@link Runtime#removeShutdownHook(Thread)}を行う。</li>
     *   <li>&lt;manager&gt;要素で定義された{@link ServiceManager}を破棄する。</li>
     *   <li>インスタンス変数を初期化する。</li>
     * </ol>
     *
     * @exception Exception ServiceManagerの停止に失敗した場合
     */
    public void stopService() throws Exception{
        
        undeployServerMetaData(serverData);
        
        managerMap.clear();
        managersServiceMetaMap.clear();
        propertyEditors.clear();
    }
    
    /**
     * ローダの破棄処理を行う。<p>
     * <ol>
     *   <li>インスタンス変数を破棄する。</li>
     *   <li>{@link ServiceManagerFactory}から自分自身を削除する。</li>
     * </ol>
     *
     * @exception Exception ServiceManagerの破棄に失敗した場合
     */
    public void destroyService() throws Exception{
        
        managerMap = null;
        managersServiceMetaMap = null;
        propertyEditors = null;
        
        ServiceManagerFactory.unregisterLoader(this);
    }
    
    // ServiceLoaderのJavaDoc
    public Set getServiceManagers(){
        return new HashSet(managerMap.values());
    }
    
    // ServiceLoaderのJavaDoc
    public List getDepends(
        String managerName,
        String serviceName
    ){
        if(!managersServiceMetaMap.containsKey(managerName)){
            return null;
        }
        final Map serviceMetaMap = (Map)managersServiceMetaMap.get(managerName);
        if(!serviceMetaMap.containsKey(serviceName)){
            return null;
        }
        final List result = new ArrayList();
        final ServiceMetaData serviceData
             = (ServiceMetaData)serviceMetaMap.get(serviceName);
        result.addAll(serviceData.getDepends());
        return result;
    }
    
    // ServiceLoaderのJavaDoc
    public List getDependedServices(
        String managerName,
        String serviceName
    ){
        final List result = new ArrayList();
        final Iterator managerNames
             = managersServiceMetaMap.keySet().iterator();
        while(managerNames.hasNext()){
            final String mngName = (String)managerNames.next();
            final Map serviceMetaMap = (Map)managersServiceMetaMap.get(mngName);
            final Iterator serviceNames = serviceMetaMap.keySet().iterator();
            while(serviceNames.hasNext()){
                final String name = (String)serviceNames.next();
                if(name.equals(serviceName)
                    && mngName.equals(managerName)){
                    continue;
                }
                final Iterator dependsDatas
                     = getDepends(mngName, name).iterator();
                while(dependsDatas.hasNext()){
                    final ServiceMetaData.DependsMetaData dependsData
                         = (ServiceMetaData.DependsMetaData)dependsDatas.next();
                    if(dependsData.getServiceName().equals(serviceName)
                        && dependsData.getManagerName().equals(managerName)){
                        final ServiceMetaData serviceData
                             = (ServiceMetaData)serviceMetaMap.get(name);
                        result.add(serviceData);
                        break;
                    }
                }
            }
        }
        return result;
    }
    
    // ServiceLoaderのJavaDoc
    public ServiceMetaData getServiceMetaData(
        String managerName,
        String serviceName
    ){
        if(!managersServiceMetaMap.containsKey(managerName)){
            return null;
        }
        final Map serviceMetaMap = (Map)managersServiceMetaMap.get(managerName);
        return (ServiceMetaData)serviceMetaMap.get(serviceName);
    }
    
    // ServiceLoaderのJavaDoc
    public void setServiceMetaData(
        String managerName,
        ServiceMetaData serviceData
    ){
        ManagerMetaData manager = serverData.getManager(managerName);
        serviceData.setParent(manager);
        serviceData.setManager(manager);
        serviceData.setServiceLoader(this);
        final Map serviceMetaMap = (Map)managersServiceMetaMap.get(managerName);
        serviceMetaMap.put(serviceData.getName(), serviceData);
    }
    
    // ServiceLoaderのJavaDoc
    public PropertyEditor findEditor(Class type){
        final Logger logger = getLogger();
        if(type == null){
            return null;
        }
        PropertyEditor editor = null;
        Class clazz = (Class)propertyEditors.getValue(type);
        if(clazz == null){
            editor = NimbusPropertyEditorManager.findEditor(type);
        }else{
            try{
                editor = (PropertyEditor)clazz.newInstance();
            }catch(InstantiationException e){
                logger.write(SVCL_00028, new Object[]{type, clazz}, e);
                return null;
            }catch(IllegalAccessException e){
                logger.write(SVCL_00028, new Object[]{type, clazz}, e);
                return null;
            }
        }
        return editor;
    }
    
    /**
     * このローダでロードされたサービスの内で、指定されたServiceManagerに登録されたサービスの生成、開始を行う。<p>
     * ServiceManagerが破棄されている状態の場合は、{@link ServiceManager#create()}、{@link ServiceManager#start()}を順次呼び出す。<br>
     * ServiceManagerが停止されている状態の場合は、{@link ServiceManager#start()}を呼び出す。<br>
     * ServiceManagerが開始されている状態の場合は、このローダでロードされたサービスを順次{@link ServiceManager#createService(Set)}、{@link ServiceManager#startService(Set)}を呼び出して生成、開始する。<br>
     * ServiceManagerが生成されている状態の場合は、{@link ServiceManager#start()}を呼び出す。<br>
     * また、ServiceManagerが開始されていない状態の場合で、&lt;manager&gt;要素のshutdown-hook属性がtrueの場合は、VMの終了をフックして{@link ServiceManager#stop()}、{@link ServiceManager#destroy()}を呼び出すスレッドを、{@link Runtime#addShutdownHook(Thread)}で設定する。<br>
     *
     * @param manager 生成、開始するServiceManager
     * @exception Exception ServiceManagerの生成、開始に失敗した場合
     */
    private void startupServiceManager(final ServiceManager manager)
     throws Exception{
        final Logger logger = getLogger();
        final int state = manager.getState();
        if(state != STARTED){
            if(state == DESTROYED){
                manager.create();
            }
            manager.start();
            
            final ManagerMetaData managerData
                 = serverData.getManager(manager.getServiceName());
            if(managerData.isExistShutdownHook()){
                final String managerName = manager.getServiceName();
                if(shutdownHooks.containsKey(managerName)){
                    Runtime.getRuntime().removeShutdownHook(
                        (Thread)shutdownHooks.get(managerName)
                    );
                }
                final Thread shutdownHook = new Thread(
                    new Runnable(){
                        public void run(){
                            manager.stop();
                            manager.destroy();
                        }
                    },
                    "Nimbus ShutdownHook " + (getServiceNameObject() == null ? (serviceURL == null ? null : serviceURL.toString()) : getServiceNameObject().toString())
                );
                Runtime.getRuntime().addShutdownHook(shutdownHook);
                shutdownHooks.put(manager.getServiceName(), shutdownHook);
                logger.write(SVCL_00026, manager.getServiceName());
            }
        }else{
            final Map serviceMetaMap = (Map)managersServiceMetaMap.get(
                manager.getServiceName()
            );
            final Set serviceNames = new HashSet(serviceMetaMap.keySet());
            manager.createService(serviceNames);
            manager.startService(serviceNames);
        }
    }
    
    /**
     * このローダでロードされたサービスの内で、指定されたServiceManagerに登録されたサービスの停止、破棄を行う。<p>
     * このローダでロードされたサービスの内で、指定されたServiceManagerに登録されたサービスを{@link ServiceManager#destroyService(Set)}で停止、破棄する。<br>
     * その後、指定されたServiceManagerに、他のローダでロードされたサービスが登録されていない場合は、{@link ServiceManager#stop()}、{@link ServiceManager#destroy()}を呼び出し、ServiceManager自身も停止、破棄する。また、シャットダウンフックが設定されている場合は、除去する。<br>
     *
     * @param manager ServiceManagerオブジェクト
     */
    private void shutdownServiceManager(ServiceManager manager){
        final Map serviceMetaMap = (Map)managersServiceMetaMap.get(
            manager.getServiceName()
        );
        Set serviceNames = new HashSet(serviceMetaMap.keySet());
        manager.destroyService(serviceNames);
        
        if(manager.getServiceLoaders().size() == 1){
            manager.stop();
            final Thread hook
                 = (Thread)shutdownHooks.remove(manager.getServiceName());
            if(hook != null){
                try{
                    Runtime.getRuntime().removeShutdownHook(hook);
                }catch(IllegalStateException e){}
            }
            manager.destroy();
        }
        manager.removeServiceLoader(this);
    }
    
    /**
     * ハッシュ値を取得する。<p>
     * サービス定義ファイルのURLのハッシュ値を返す。サービス定義ファイルのURLが指定されていない場合は、{@link ServiceBase#hashCode() super.hashCode()}の戻り値を返す。<br>
     *
     * @return ハッシュ値
     */
    public int hashCode(){
        return serviceURL == null ? super.hashCode() : serviceURL.hashCode();
    }
    
    /**
     * このインスタンスと等しいか調べる。<p>
     *
     * @param obj 比較対象のオブジェクト
     * @return 等しい場合、true
     */
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(obj instanceof DefaultServiceLoaderService){
            final DefaultServiceLoaderService loader
                 = (DefaultServiceLoaderService)obj;
            if((serviceURL == null && loader.serviceURL != null)
                || (serviceURL != null && loader.serviceURL == null)){
                return false;
            }else if(serviceURL != null && loader.serviceURL != null
                && !serviceURL.equals(loader.serviceURL)){
                return false;
            }
            return true;
        }
        return false;
    }
    
    // DefaultServiceLoaderServiceMBeanのJavaDoc
    public void setValidate(boolean validate){
        isValidate = validate;
    }
    
    // DefaultServiceLoaderServiceMBeanのJavaDoc
    public boolean isValidate(){
        return isValidate;
    }
    
    // DefaultServiceLoaderServiceMBeanのJavaDoc
    public void setCheckLoadManagerCompleted(boolean isCheck){
        isCheckLoadManagerCompleted = isCheck;
    }
    
    // DefaultServiceLoaderServiceMBeanのJavaDoc
    public boolean isCheckLoadManagerCompleted(){
        return isCheckLoadManagerCompleted;
    }
    
    // DefaultServiceLoaderServiceMBeanのJavaDoc
    public void setCheckLoadManagerCompletedBy(String[] managerNames){
        if(managerNames != null && managerNames.length != 0){
            checkLoadManagerNames = new HashSet();
            for(int i = 0; i < managerNames.length; i++){
                checkLoadManagerNames.add(managerNames[i]);
            }
        }else{
            checkLoadManagerNames = null;
        }
    }
    
    // DefaultServiceLoaderServiceMBeanのJavaDoc
    public String[] getCheckLoadManagerCompletedBy(){
        return checkLoadManagerNames == null
             ? new String[0] : (String[])checkLoadManagerNames
                .toArray(new String[checkLoadManagerNames.size()]);
    }
    
    private class MyErrorHandler implements ErrorHandler{
        
        private boolean isError;
        
        public void warning(SAXParseException e) throws SAXException{
            getLogger().write(SVCL_00030, new Object[]{e.getMessage(), Integer.toString(e.getLineNumber()), Integer.toString(e.getColumnNumber())});
        }
        public void error(SAXParseException e) throws SAXException{
            isError = true;
            getLogger().write(SVCL_00031, new Object[]{e.getMessage(), Integer.toString(e.getLineNumber()), Integer.toString(e.getColumnNumber())});
        }
        public void fatalError(SAXParseException e) throws SAXException{
            isError = true;
            getLogger().write(SVCL_00032, new Object[]{e.getMessage(), Integer.toString(e.getLineNumber()), Integer.toString(e.getColumnNumber())});
        }
        public boolean isError(){
            return isError;
        }
    }
}