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

import java.io.*;
import java.util.*;
import org.w3c.dom.*;

/**
 * サービス定義&lt;manager&gt;要素メタデータ。<p>
 * サービス定義ファイルの&lt;manager&gt;要素に記述された内容を格納するメタデータコンテナである。<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">サービス定義ファイルDTD</a>
 */
public class ManagerMetaData extends MetaData implements Serializable{
    
    private static final long serialVersionUID = 710269838167389543L;
    
    /**
     * &lt;manager&gt;要素の要素名文字列。<p>
     */
    public static final String MANAGER_TAG_NAME = "manager";
    
    /**
     * &lt;manager&gt;要素の子要素&lt;repository&gt;要素の要素名文字列。<p>
     */
    private static final String REPOSITORY_TAG_NAME = "repository";
    
    /**
     * &lt;manager&gt;要素の子要素&lt;log&gt;要素の要素名文字列。<p>
     */
    private static final String LOG_TAG_NAME = "log";
    
    /**
     * &lt;manager&gt;要素の子要素&lt;message&gt;要素の要素名文字列。<p>
     */
    private static final String MESSAGE_TAG_NAME = "message";
    
    /**
     * &lt;manager&gt;要素のname属性の属性名文字列。<p>
     */
    private static final String NAME_ATTRIBUTE_NAME = "name";
    
    /**
     * &lt;manager&gt;要素のshutdown-hook属性の属性名文字列。<p>
     */
    private static final String SHUTDOWN_HOOK_ATTRIBUTE_NAME = "shutdown-hook";
    
    /**
     * name属性。<p>
     *
     * @see #getName()
     */
    private String name;
    
    /**
     * shutdown-hook属性。<p>
     *
     * @see #isExistShutdownHook()
     */
    private boolean isExistShutdownHook;
    
    /**
     * &lt;manager&gt;要素の子要素として定義された&lt;service&gt;要素のメタデータを格納するマップ。<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="2">値</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
     *   <tr><td>String</td><td>&lt;service&gt;要素のname属性の値</td><td>{@link ServiceMetaData}</td><td>&lt;service&gt;要素のメタデータ</td></tr>
     * </table>
     *
     * @see #getService(String)
     * @see #getServices()
     * @see #addService(ServiceMetaData)
     */
    private final Map services = new LinkedHashMap();
    
    /**
     * &lt;repository&gt;要素のメタデータ。<p>
     *
     * @see #getRepository()
     */
    private ServiceNameMetaData repository;
    
    /**
     * &lt;log&gt;要素で指定されたLoggerのメタデータ。<p>
     *
     * @see #getLog()
     */
    private ServiceNameMetaData log;
    
    /**
     * &lt;message&gt;要素で指定されたMessageRecordFactoryのメタデータ。<p>
     *
     * @see #getMessage()
     */
    private ServiceNameMetaData message;
    
    /**
     * 自分をロードしたServiceLoader
     */
    private ServiceLoader myLoader;
    
    /**
     * &lt;manager-property&gt;要素で指定されたプロパティ。<p>
     */
    private Map properties = new LinkedHashMap();
    
    private List ifDefMetaDataList;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public ManagerMetaData(){
    }
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * ManagerMetaDataの親要素は、&lt;server&gt;要素を表すServerMetaDataである。<br>
     * 
     * @param loader 自分をロードしたServiceLoader
     * @param parent 親要素のメタデータ
     * @see ServerMetaData
     */
    public ManagerMetaData(ServiceLoader loader, MetaData parent){
        super(parent);
        myLoader = loader;
    }
    
    /**
     * 自分をロードした{@link ServiceLoader}を取得する。<p>
     *
     * @return 自分をロードしたServiceLoader 
     */
    public ServiceLoader getServiceLoader(){
        return myLoader;
    }
    
    /**
     * 自分をロードした{@link ServiceLoader}を設定する。<p>
     *
     * @param loader 自分をロードしたServiceLoader 
     */
    public void setServiceLoader(ServiceLoader loader){
        myLoader = loader;
    }
    
    /**
     * この&lt;manager&gt;要素のname属性の値を取得する。<p>
     * name属性が省略されていた場合は、{@link ServiceManager#DEFAULT_NAME}を返す。<br>
     * 
     * @return name属性の値
     */
    public String getName(){
        if(name == null){
            return ServiceManager.DEFAULT_NAME;
        }
        return name;
    }
    
    /**
     * この&lt;manager&gt;要素のname属性の値を設定する。<p>
     * 
     * @param name name属性の値
     */
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * この&lt;manager&gt;要素のshutdown-hook属性の値を取得する。<p>
     * trueの場合は、この&lt;manager&gt;要素が表す{@link ServiceManager}の廃棄処理を行うShutdownHookを生成して設定する。falseの場合は、ShutdownHookを生成しない。<br>
     * shutdown-hook属性が省略されていた場合は、falseを返す。<br>
     * 
     * @return shutdown-hook属性の値
     */
    public boolean isExistShutdownHook(){
        return isExistShutdownHook;
    }
    
    /**
     * この&lt;manager&gt;要素のshutdown-hook属性の値を設定する。<p>
     * 
     * @param flg shutdown-hook属性の値
     */
    public void setExistShutdownHook(boolean flg){
        isExistShutdownHook = flg;
    }
    
    /**
     * この&lt;manager&gt;要素の子要素&lt;repository&gt;要素に指定されたリポジトリのメタデータを取得する。<p>
     * &lt;repository&gt;要素が指定されていない場合は、nullを返す。その場合、Repositoryはサービスとして起動されず、Map実装の{@link jp.ossc.nimbus.service.repository.Repository Repository}が使用される。<br>
     *
     * @return &lt;repository&gt;要素に指定されたリポジトリのメタデータ
     * @see jp.ossc.nimbus.service.repository.Repository Repository
     */
    public ServiceNameMetaData getRepository(){
        return repository;
    }
    
    /**
     * この&lt;manager&gt;要素の子要素&lt;repository&gt;要素に指定されたリポジトリのメタデータを設定する。<p>
     *
     * @param data &lt;repository&gt;要素に指定されたリポジトリのメタデータ
     */
    public void setRepository(ServiceNameMetaData data){
        repository = data;
    }
    
    /**
     * &lt;log&gt;要素で指定されたLoggerのメタデータを取得する。<p>
     * &lt;log&gt;要素が指定されていない場合は、nullを返す。<br>
     *
     * @return &lt;log&gt;要素で指定されたLoggerのメタデータ
     */
    public ServiceNameMetaData getLog(){
        return log;
    }
    
    /**
     * &lt;log&gt;要素で指定されたLoggerのメタデータを設定する。<p>
     *
     * @param data &lt;log&gt;要素で指定されたLoggerのメタデータ
     */
    public void setLog(ServiceNameMetaData data){
        log = data;
    }
    
    /**
     * &lt;message&gt;要素で指定されたMessageRecordFactoryのメタデータを取得する。<p>
     * &lt;message&gt;要素が指定されていない場合は、nullを返す。<br>
     *
     * @return &lt;log&gt;要素で指定されたLoggerのメタデータ
     */
    public ServiceNameMetaData getMessage(){
        return message;
    }
    
    /**
     * &lt;message&gt;要素で指定されたMessageRecordFactoryのメタデータを設定する。<p>
     *
     * @param data &lt;log&gt;要素で指定されたLoggerのメタデータ
     */
    public void setMessage(ServiceNameMetaData data){
        message = data;
    }
    
    /**
     * この&lt;manager&gt;要素の子要素から、指定されたサービス名を持つ&lt;service&gt;要素のメタデータを取得する。<p>
     * 指定されたサービス名の&lt;service&gt;要素が、この&lt;manager&gt;要素の子要素に定義されてない場合は、nullを返す。<br>
     *
     * @param name サービス名
     * @return &lt;service&gt;要素のメタデータ
     */
    public ServiceMetaData getService(String name){
        return (ServiceMetaData)services.get(name);
    }
    
    /**
     * この&lt;manager&gt;要素の子要素として定義されている&lt;service&gt;要素のマッピングを取得する。<p>
     * この&lt;manager&gt;要素の子要素に&lt;service&gt;要素が１つも定義されていない場合は、空のMapインスタンスを返す。<br>
     * <p>
     * ここで、取得できるMapは、以下のようなマッピングである。<br>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="2">値</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
     *   <tr><td>String</td><td>&lt;service&gt;要素のname属性の値</td><td>{@link ServiceMetaData}</td><td>&lt;service&gt;要素のメタデータ</td></tr>
     * </table>
     * また、取得されるMapインスタンスは、このManagerMetaDataインスタンスの内部に保持するMapの参照である。そのため、このMapに対する変更は、このインスタンスに影響を及ぼす。<br>
     * 
     * @return &lt;service&gt;要素のマッピング
     */
    public Map getServices(){
        return services;
    }
    
    /**
     * &lt;manager&gt;要素の子要素として定義されている&lt;service&gt;要素のメタデータを登録する。<p>
     *
     * @param service &lt;service&gt;要素のメタデータ
     */
    public void addService(ServiceMetaData service){
        service.setServiceLoader(getServiceLoader());
        service.setManager(this);
        services.put(service.getName(), service);
    }
    
    /**
     * &lt;manager&gt;要素の子要素として定義されている&lt;service&gt;要素のメタデータを削除する。<p>
     *
     * @param name &lt;service&gt;要素のname属性の値
     */
    public void removeService(String name){
        services.remove(name);
    }
    
    /**
     * &lt;manager&gt;要素の子要素として定義されている&lt;service&gt;要素のメタデータを全て削除する。<p>
     */
    public void clearServices(){
        services.clear();
    }
    
    /**
     * プロパティ名の集合を取得する。<p>
     *
     * @return &lt;manager-property&gt;要素で指定されたプロパティ名の集合
     */
    public Set getPropertyNameSet(){
        return properties.keySet();
    }
    
    /**
     * 指定されたプロパティ名の&lt;manager-property&gt;要素で指定されたプロパティが存在するか判定する。<p>
     *
     * @param property プロパティ名
     * @return &lt;manager-property&gt;要素で指定されたプロパティ名が存在する場合true
     */
    public boolean existsProperty(String property){
        return properties.containsKey(property);
    }
    
    /**
     * 指定されたプロパティ名の&lt;manager-property&gt;要素で指定されたプロパティ値を取得する。<p>
     * 該当するプロパティ名の&lt;manager-property&gt;要素が指定されていない場合は、nullを返す。<br>
     *
     * @param property プロパティ名
     * @return &lt;manager-property&gt;要素で指定されたプロパティ値
     */
    public String getProperty(String property){
        ManagerPropertyMetaData propData
            = (ManagerPropertyMetaData)properties.get(property);
        return propData == null ? null : propData.getValue();
    }
    
    /**
     * &lt;manager-property&gt;要素で指定されたプロパティを取得する。<p>
     * &lt;manager-property&gt;要素が指定されていない場合は、空のPropertiesを返す。<br>
     *
     * @return &lt;manager-property&gt;要素で指定されたプロパティ
     */
    public Properties getProperties(){
        final Properties props = new Properties();
        Iterator propDatas = properties.values().iterator();
        while(propDatas.hasNext()){
            ManagerPropertyMetaData propData = (ManagerPropertyMetaData)propDatas.next();
            props.setProperty(propData.getName(), propData.getValue() == null ? "" : propData.getValue());
        }
        return props;
    }
    
    /**
     * 指定されたプロパティ名の&lt;manager-property&gt;要素で指定されたプロパティ値を設定する。<p>
     *
     * @param property プロパティ名
     * @param value &lt;manager-property&gt;要素で指定されたプロパティ値
     */
    public void setProperty(String property, String value){
        ManagerPropertyMetaData propData
            = (ManagerPropertyMetaData)properties.get(property);
        if(propData == null){
            propData = new ManagerPropertyMetaData(this);
        }
        propData.setName(property);
        propData.setValue(value);
        properties.put(property, propData);
    }
    
    /**
     * 指定されたプロパティ名の&lt;manager-property&gt;要素で指定されたプロパティを削除する。<p>
     *
     * @param property プロパティ名
     */
    public void removeProperty(String property){
        properties.remove(property);
    }
    
    /**
     * &lt;manager-property&gt;要素で指定された全てのプロパティを削除する。<p>
     */
    public void clearProperties(){
        properties.clear();
    }
    
    /**
     * &lt;repository&gt;要素を生成する。<p>
     *
     * @param managerName リポジトリサービスが登録されているマネージャ名
     * @param serviceName リポジトリサービスのサービス名
     * @return &lt;repository&gt;要素のメタデータ
     */
    public ServiceNameMetaData createRepositoryMetaData(
        String managerName,
        String serviceName
    ){
        return new ServiceNameMetaData(
            this,
            REPOSITORY_TAG_NAME,
            managerName,
            serviceName
        );
    }
    
    /**
     * &lt;log&gt;要素を生成する。<p>
     *
     * @param managerName ログサービスが登録されているマネージャ名
     * @param serviceName ログサービスのサービス名
     * @return &lt;log&gt;要素のメタデータ
     */
    public ServiceNameMetaData createLogMetaData(
        String managerName,
        String serviceName
    ){
        return new ServiceNameMetaData(
            this,
            LOG_TAG_NAME,
            managerName,
            serviceName
        );
    }
    
    /**
     * &lt;message&gt;要素を生成する。<p>
     *
     * @param managerName メッセージサービスが登録されているマネージャ名
     * @param serviceName メッセージサービスのサービス名
     * @return &lt;message&gt;要素のメタデータ
     */
    public ServiceNameMetaData createMessageMetaData(
        String managerName,
        String serviceName
    ){
        return new ServiceNameMetaData(
            this,
            MESSAGE_TAG_NAME,
            managerName,
            serviceName
        );
    }
    
    /**
     * &lt;manager&gt;要素のElementをパースして、自分自身の初期化、及び子要素のメタデータの生成を行う。<p>
     *
     * @param element &lt;manager&gt;要素のElement
     * @exception DeploymentException &lt;manager&gt;要素の解析、その結果によるメタデータの生成に失敗した場合
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        
        if(!element.getTagName().equals(MANAGER_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + MANAGER_TAG_NAME + " : "
                 + element.getTagName()
            );
        }
        
        name = getOptionalAttribute(element, NAME_ATTRIBUTE_NAME);
        final String shutdownHook
             = getOptionalAttribute(element, SHUTDOWN_HOOK_ATTRIBUTE_NAME);
        if(shutdownHook != null && shutdownHook.length() != 0){
            isExistShutdownHook = Boolean.valueOf(shutdownHook).booleanValue();
        }
        
        importXMLInner(element, null);
        
        final Iterator ifDefElements = getChildrenByTagName(
            element,
            IfDefMetaData.IFDEF_TAG_NAME
        );
        while(ifDefElements.hasNext()){
            if(ifDefMetaDataList == null){
                ifDefMetaDataList = new ArrayList();
            }
            final IfDefMetaData ifdefData
                 = new IfDefMetaData(this);
            ifdefData.importXML((Element)ifDefElements.next());
            ifDefMetaDataList.add(ifdefData);
        }
        
        importIfDef();
    }
    
    public void importIfDef() throws DeploymentException{
        
        if(ifDefMetaDataList == null || ifDefMetaDataList.size() == 0){
            return;
        }
        
        for(int i = 0, imax = ifDefMetaDataList.size(); i < imax; i++){
            IfDefMetaData ifdefData = (IfDefMetaData)ifDefMetaDataList.get(i);
            Element ifDefElement = ifdefData.getElement();
            if(ifDefElement == null){
                continue;
            }
            
            importXMLInner(ifDefElement, ifdefData);
            
            ifdefData.setElement(null);
        }
    }
    
    protected void importXMLInner(Element element, IfDefMetaData ifdefData) throws DeploymentException{
        
        final boolean ifdefMatch
            = ifdefData == null ? true : ifdefData.isMatch();
        
        final Iterator propElements = getChildrenByTagName(
            element,
            ManagerPropertyMetaData.MANAGER_PROPERTY_TAG_NAME
        );
        while(propElements.hasNext()){
            ManagerPropertyMetaData propData
                = new ManagerPropertyMetaData(this);
            if(ifdefData != null){
                propData.setIfDefMetaData(ifdefData);
                ifdefData.addChild(propData);
            }
            propData.importXML((Element)propElements.next());
            if(ifdefMatch){
                properties.put(
                    propData.getName(),
                    propData
                );
                String prop = propData.getValue();
                // システムプロパティの置換
                prop = Utility.replaceSystemProperty(prop);
                // サービスローダ構成プロパティの置換
                prop = Utility.replaceServiceLoderConfig(
                    prop,
                    myLoader == null ? null : myLoader.getConfig()
                );
                // サーバプロパティの置換
                prop = Utility.replaceServerProperty(prop);
                propData.setValue(prop);
            }
        }
        
        final Element repositoryElement
             = getOptionalChild(element, REPOSITORY_TAG_NAME);
        if(repositoryElement != null){
            if(ifdefMatch && repository != null){
                throw new DeploymentException("Element of " + REPOSITORY_TAG_NAME + " is duplicated.");
            }
            ServiceNameMetaData tmp = new ServiceNameMetaData(this);
            if(ifdefData != null){
                tmp.setIfDefMetaData(ifdefData);
                ifdefData.addChild(tmp);
            }
            tmp.importXML(repositoryElement);
            if(ifdefMatch){
                repository = tmp;
            }
        }
        
        final Element logElement
             = getOptionalChild(element, LOG_TAG_NAME);
        if(logElement != null){
            if(ifdefMatch && log != null){
                throw new DeploymentException("Element of " + LOG_TAG_NAME + " is duplicated.");
            }
            ServiceNameMetaData tmp = new ServiceNameMetaData(this);
            if(ifdefData != null){
                tmp.setIfDefMetaData(ifdefData);
                ifdefData.addChild(tmp);
            }
            tmp.importXML(logElement);
            if(ifdefMatch){
                log = tmp;
            }
        }
        
        final Element messageElement = getOptionalChild(
            element,
            MESSAGE_TAG_NAME
        );
        if(messageElement != null){
            if(ifdefMatch && message != null){
                throw new DeploymentException("Element of " + MESSAGE_TAG_NAME + " is duplicated.");
            }
            ServiceNameMetaData tmp = new ServiceNameMetaData(this);
            if(ifdefData != null){
                tmp.setIfDefMetaData(ifdefData);
                ifdefData.addChild(tmp);
            }
            tmp.importXML(messageElement);
            if(ifdefMatch){
                message = tmp;
            }
        }
        
        final Iterator serviceElements = getChildrenByTagName(
            element,
            ServiceMetaData.SERVICE_TAG_NAME
        );
        while(serviceElements.hasNext()){
            final ServiceMetaData serviceData
                 = new ServiceMetaData(myLoader, this, this);
            if(ifdefData != null){
                serviceData.setIfDefMetaData(ifdefData);
                ifdefData.addChild(serviceData);
            }
            serviceData.importXML((Element)serviceElements.next());
            if(ifdefMatch && services.containsKey(serviceData.getName())){
                throw new DeploymentException(
                    "Dupulicated service name : " + serviceData.getName()
                );
            }
            if(ifdefMatch){
                addService(serviceData);
                addDependsService(serviceData);
            }
        }
    }
    
    protected void addDependsService(ServiceMetaData serviceData) throws DeploymentException{
        List dependsList = serviceData.getDepends();
        for(int i = 0; i < dependsList.size(); i++){
            ServiceMetaData.DependsMetaData dependsData = (ServiceMetaData.DependsMetaData)dependsList.get(i);
            ServiceMetaData dependsServiceData = dependsData.getService();
            if(dependsServiceData != null){
                if(services.containsKey(dependsServiceData.getName())){
                    throw new DeploymentException(
                        "Dupulicated service name : " + dependsServiceData.getName()
                    );
                }
                addService(dependsServiceData);
                addDependsService(dependsServiceData);
            }
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append('<').append(MANAGER_TAG_NAME);
        if(name != null){
            buf.append(' ').append(NAME_ATTRIBUTE_NAME)
                .append("=\"").append(name).append("\"");
        }
        if(isExistShutdownHook){
            buf.append(' ').append(SHUTDOWN_HOOK_ATTRIBUTE_NAME)
                .append("=\"").append(isExistShutdownHook).append("\"");
        }
        buf.append('>');
        if(properties.size() != 0){
            buf.append(LINE_SEPARATOR);
            final StringBuilder subBuf = new StringBuilder();
            final Iterator props = properties.values().iterator();
            while(props.hasNext()){
                final ManagerPropertyMetaData propData
                    = (ManagerPropertyMetaData)props.next();
                if(propData.getIfDefMetaData() != null){
                    continue;
                }
                propData.toXML(subBuf);
                if(props.hasNext()){
                    subBuf.append(LINE_SEPARATOR);
                }
            }
            buf.append(addIndent(subBuf));
        }
        if(repository != null && repository.getIfDefMetaData() == null){
            buf.append(LINE_SEPARATOR);
            buf.append(
                addIndent(repository.toXML(new StringBuilder()))
            );
        }
        if(log != null && log.getIfDefMetaData() == null){
            buf.append(LINE_SEPARATOR);
            buf.append(
                addIndent(log.toXML(new StringBuilder()))
            );
        }
        if(message != null && message.getIfDefMetaData() == null){
            buf.append(LINE_SEPARATOR);
            buf.append(
                addIndent(message.toXML(new StringBuilder()))
            );
        }
        if(services.size() != 0){
            buf.append(LINE_SEPARATOR);
            final Iterator datas = services.values().iterator();
            while(datas.hasNext()){
                MetaData data = (MetaData)datas.next();
                if(data.getIfDefMetaData() != null){
                    continue;
                }
                buf.append(
                    addIndent(data.toXML(new StringBuilder()))
                );
                if(datas.hasNext()){
                    buf.append(LINE_SEPARATOR);
                }
            }
        }
        buf.append(LINE_SEPARATOR);
        if(ifDefMetaDataList != null && ifDefMetaDataList.size() != 0){
            for(int i = 0, imax = ifDefMetaDataList.size(); i < imax; i++){
                IfDefMetaData ifdefData = (IfDefMetaData)ifDefMetaDataList.get(i);
                buf.append(
                    addIndent(ifdefData.toXML(new StringBuilder()))
                );
                buf.append(LINE_SEPARATOR);
            }
        }
        buf.append("</").append(MANAGER_TAG_NAME).append('>');
        return buf;
    }
    
    /**
     * このインスタンスの文字列表現を取得する。<p>
     *
     * @return 文字列表現
     */
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append('{');
        if(getName() != null){
            buf.append(NAME_ATTRIBUTE_NAME);
            buf.append('=');
            buf.append(getName());
            buf.append(',');
        }
        buf.append(SHUTDOWN_HOOK_ATTRIBUTE_NAME);
        buf.append('=');
        buf.append(isExistShutdownHook);
        buf.append('}');
        return buf.toString();
    }
}
