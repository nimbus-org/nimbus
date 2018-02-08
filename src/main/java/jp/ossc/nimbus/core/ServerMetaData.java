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
import java.net.*;
import org.w3c.dom.*;

/**
 * サービス定義&lt;server&gt;要素メタデータ。<p>
 * サービス定義ファイルの&lt;server&gt;要素に記述された内容を格納するメタデータコンテナである。<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">サービス定義ファイルDTD</a>
 */
public class ServerMetaData extends MetaData implements Serializable{
    
    private static final long serialVersionUID = -5309966428718081110L;
    
    /**
     * &lt;server&gt;要素の要素名文字列。<p>
     */
    public static final String SERVER_TAG_NAME = "server";
    
    /**
     * &lt;server&gt;要素の子要素&lt;manager-repository&gt;要素の要素名文字列。<p>
     */
    private static final String REPOSITORY_TAG_NAME = "manager-repository";
    
    /**
     * &lt;server&gt;要素の子要素&lt;log&gt;要素の要素名文字列。<p>
     */
    private static final String LOG_TAG_NAME = "log";
    
    /**
     * &lt;server&gt;要素の子要素&lt;message&gt;要素の要素名文字列。<p>
     */
    private static final String MESSAGE_TAG_NAME = "message";
    
    /**
     * &lt;server&gt;要素の子要素として定義された&lt;manager&gt;要素のメタデータを格納するマップ。<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="2">値</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
     *   <tr><td>String</td><td>&lt;manager&gt;要素のname属性の値</td><td>{@link ManagerMetaData}</td><td>&lt;manager&gt;要素のメタデータ</td></tr>
     * </table>
     *
     * @see #getManager(String)
     * @see #getManagers()
     * @see #addManager(ManagerMetaData)
     */
    private final Map managers = new LinkedHashMap();
    
    /**
     * &lt;ref-url&gt;要素で指定されたURLの集合。<p>
     *
     * @see #getReferenceURL()
     */
    private final Set referenceURL = new LinkedHashSet();
    
    /**
     * &lt;server&gt;要素の子要素として定義された&lt;property-editors&gt;要素のメタデータ。<p>
     *
     * @see #getPropertyEditors()
     * @see #addPropertyEditor(String, String)
     */
    private PropertyEditorsMetaData propertyEditors;
    
    /**
     * このメタデータが定義されているサービス定義ファイルのURL。<p>
     */
    private final URL myUrl;
    
    /**
     * &lt;manager-repository&gt;要素のメタデータ。<p>
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
     * &lt;default-log&gt;要素で指定されたLoggerのメタデータ。<p>
     *
     * @see #getDefaultLog()
     */
    private DefaultLogMetaData defaultLog;
    
    /**
     * 自分をロードしたServiceLoader
     */
    private ServiceLoader myLoader;
    
    /**
     * &lt;server-property&gt;要素で指定されたプロパティ。<p>
     */
    private Map properties = new LinkedHashMap();
    
    private String encoding;
    private String docType;
    
    private List ifDefMetaDataList;
    
    /**
     * このメタデータが定義されているサービス定義ファイルのURLを持った、ルートメタデータを生成する。<p>
     *
     * @param loader 自分をロードしたServiceLoader
     * @param url このメタデータが定義されているサービス定義ファイルのURL
     */
    public ServerMetaData(ServiceLoader loader, URL url){
        super();
        myUrl = url;
        myLoader = loader;
    }
    
    /**
     * このメタデータが定義されているサービス定義ファイルのURLを取得する。<p>
     * 
     * @return このメタデータが定義されているサービス定義ファイルのURL
     */
    public URL getURL(){
        return myUrl;
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
     * この&lt;server&gt;要素の子要素から、指定されたサービス名を持つ&lt;manager&gt;要素のメタデータを取得する。<p>
     * 指定されたサービス名の&lt;manager&gt;要素が、この&lt;server&gt;要素の子要素に定義されてない場合は、nullを返す。<br>
     *
     * @param name サービス名
     * @return &lt;manager&gt;要素のメタデータ
     */
    public ManagerMetaData getManager(String name){
        return (ManagerMetaData)managers.get(name);
    }
    
    /**
     * この&lt;server&gt;要素の子要素として定義されている&lt;manager&gt;要素の集合を取得する。<p>
     * この&lt;server&gt;要素の子要素に&lt;manager&gt;要素が１つも定義されていない場合は、空の集合を返す。<br>
     * 
     * @return &lt;manager&gt;要素の集合
     */
    public Collection getManagers(){
        return managers.values();
    }
    
    /**
     * &lt;server&gt;要素の子要素として定義されている&lt;manager&gt;要素のメタデータを登録する。<p>
     *
     * @param manager &lt;manager&gt;要素のメタデータ
     */
    public void addManager(ManagerMetaData manager){
        manager.setParent(this);
        manager.setServiceLoader(getServiceLoader());
        managers.put(manager.getName(), manager);
    }
    
    /**
     * &lt;server&gt;要素の子要素として定義されている&lt;manager&gt;要素のメタデータを削除する。<p>
     *
     * @param name &lt;manager&gt;要素のname属性の値
     */
    public void removeManager(String name){
        managers.remove(name);
    }
    
    /**
     * &lt;server&gt;要素の子要素として定義されている&lt;manager&gt;要素のメタデータを全て削除する。<p>
     */
    public void clearManagers(){
        managers.clear();
    }
    
    /**
     * &lt;ref-url&gt;要素で指定されたURLの集合を取得する。<p>
     * &lt;ref-url&gt;要素が指定されていない場合は、空の集合を返す。<br>
     *
     * @return &lt;ref-url&gt;要素で指定されたメタデータの集合
     */
    public Set getReferenceURL(){
        Set urls = new HashSet();
        Iterator itr = referenceURL.iterator();
        while(itr.hasNext()){
            urls.add(((RefURLMetaData)itr.next()).getURL());
        }
        return urls;
    }
    
    /**
     * &lt;ref-url&gt;要素で指定されたURLを追加する。<p>
     *
     * @param urlStr &lt;ref-url&gt;要素で指定されたURL文字列
     */
    public void addReferenceURL(String urlStr){
        RefURLMetaData data = new RefURLMetaData(this);
        data.setURL(urlStr);
        referenceURL.add(data);
    }
    
    /**
     * &lt;property-editors&gt;要素で指定された型とjava.beans.PropertyEditorのマッピングを取得する。<p>
     *
     * @return &lt;property-editors&gt;要素で指定された型とjava.beans.PropertyEditorのマッピング
     */
    public Map getPropertyEditors(){
        Map result = new HashMap();
        if(propertyEditors == null){
            return result;
        }
        Iterator types = propertyEditors.getPropertyEditorTypes().iterator();
        while(types.hasNext()){
            String type = (String)types.next();
            result.put(type, propertyEditors.getPropertyEditor(type));
        }
        return result;
    }
    
    /**
     * &lt;property-editors&gt;要素で指定された型とjava.beans.PropertyEditorのマッピングを追加する。<p>
     *
     * @param type java.beans.PropertyEditorが編集するクラス名
     * @param editor java.beans.PropertyEditor実装クラス名
     */
    public void addPropertyEditor(String type, String editor){
        if(propertyEditors == null){
            propertyEditors = new PropertyEditorsMetaData(this);
        }
        propertyEditors.setPropertyEditor(type, editor);
    }
    
    /**
     * &lt;property-editors&gt;要素で指定された型のjava.beans.PropertyEditorを削除する。<p>
     *
     * @param type java.beans.PropertyEditorが編集するクラス名
     */
    public void removePropertyEditor(String type){
        if(propertyEditors == null){
            return;
        }
        propertyEditors.removePropertyEditor(type);
    }
    
    /**
     * &lt;property-editors&gt;要素で定義されたjava.beans.PropertyEditorのマッピングを全て削除する。<p>
     */
    public void clearPropertyEditors(){
        propertyEditors.clearPropertyEditors();
    }
    
    /**
     * &lt;manager-repository&gt;要素で指定されたリポジトリのメタデータを取得する。<p>
     * &lt;manager-repository&gt;要素が指定されていない場合は、nullを返す。<br>
     *
     * @return &lt;manager-repository&gt;要素で指定されたリポジトリのメタデータ
     */
    public ServiceNameMetaData getRepository(){
        return repository;
    }
    
    /**
     * &lt;manager-repository&gt;要素で指定されたリポジトリのメタデータを設定する。<p>
     *
     * @param data &lt;manager-repository&gt;要素で指定されたリポジトリのメタデータ
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
     * &lt;default-log&gt;要素で指定されたLoggerのメタデータを取得する。<p>
     * &lt;default-log&gt;要素が指定されていない場合は、nullを返す。<br>
     *
     * @return &lt;default-log&gt;要素で指定されたLoggerのメタデータ
     */
    public DefaultLogMetaData getDefaultLog(){
        return defaultLog;
    }
    
    /**
     * &lt;default-log&gt;要素で指定されたLoggerのメタデータを設定する。<p>
     *
     * @param data &lt;default-log&gt;要素で指定されたLoggerのメタデータ
     */
    public void setDefaultLog(DefaultLogMetaData data){
        defaultLog = data;
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
     * 指定されたプロパティ名の&lt;server-property&gt;要素で指定されたプロパティ値を取得する。<p>
     * 該当するプロパティ名の&lt;server-property&gt;要素が指定されていない場合は、nullを返す。<br>
     *
     * @param property プロパティ名
     * @return &lt;server-property&gt;要素で指定されたプロパティ値
     */
    public String getProperty(String property){
        ServerPropertyMetaData propData
            = (ServerPropertyMetaData)properties.get(property);
        return propData == null ? null : propData.getValue();
    }
    
    /**
     * &lt;server-property&gt;要素で指定されたプロパティを取得する。<p>
     * &lt;server-property&gt;要素が指定されていない場合は、空のPropertiesを返す。<br>
     *
     * @return &lt;server-property&gt;要素で指定されたプロパティ
     */
    public Properties getProperties(){
        final Properties props = new Properties();
        Iterator propDatas = properties.values().iterator();
        while(propDatas.hasNext()){
            ServerPropertyMetaData propData = (ServerPropertyMetaData)propDatas.next();
            props.setProperty(propData.getName(), propData.getValue() == null ? "" : propData.getValue());
        }
        return props;
    }
    
    /**
     * 指定されたプロパティ名の&lt;server-property&gt;要素で指定されたプロパティ値を設定する。<p>
     *
     * @param property プロパティ名
     * @param value &lt;server-property&gt;要素で指定されたプロパティ値
     */
    public void setProperty(String property, String value){
        ServerPropertyMetaData propData
            = (ServerPropertyMetaData)properties.get(property);
        if(propData == null){
            propData = new ServerPropertyMetaData(this);
        }
        propData.setName(property);
        propData.setValue(value);
        properties.put(property, propData);
    }
    
    /**
     * 指定されたプロパティ名の&lt;server-property&gt;要素で指定されたプロパティを削除する。<p>
     *
     * @param property プロパティ名
     */
    public void removeProperty(String property){
        properties.remove(property);
    }
    
    /**
     * &lt;server-property&gt;要素で指定されたプロパティを全て削除する。<p>
     */
    public void clearProperties(){
        properties.clear();
    }
    
    /**
     * &lt;manager-repository&gt;要素を生成する。<p>
     *
     * @param managerName リポジトリサービスが登録されているマネージャ名
     * @param serviceName リポジトリサービスのサービス名
     * @return &lt;manager-repository&gt;要素のメタデータ
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
    
    public void setDocType(String str){
        docType = str;
    }
    
    public String getDocType(){
        return docType;
    }
    
    public void setEncoding(String encoding){
        this.encoding = encoding;
    }
    
    public String getEncoding(){
        return encoding;
    }
    
    /**
     * &lt;server&gt;要素のElementをパースして、自分自身の初期化、及び子要素のメタデータの生成を行う。<p>
     *
     * @param element &lt;server&gt;要素のElement
     * @exception DeploymentException &lt;server&gt;要素の解析、その結果によるメタデータの生成に失敗した場合
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        
        if(!element.getTagName().equals(SERVER_TAG_NAME)){
            throw new DeploymentException(
                "Root tag must be " + SERVER_TAG_NAME + " : "
                 + element.getTagName()
            );
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
            ServerPropertyMetaData.SERVER_PROPERTY_TAG_NAME
        );
        while(propElements.hasNext()){
            ServerPropertyMetaData propData = new ServerPropertyMetaData(this);
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
        
        final Iterator refURLElements = getChildrenByTagName(
            element,
            RefURLMetaData.REF_URL_TAG_NAME
        );
        while(refURLElements.hasNext()){
            final RefURLMetaData refURLData
                 = new RefURLMetaData(this);
            if(ifdefData != null){
                refURLData.setIfDefMetaData(ifdefData);
                ifdefData.addChild(refURLData);
            }
            refURLData.importXML((Element)refURLElements.next());
            if(ifdefMatch
                 && refURLData.getURL() != null
                 && refURLData.getURL().length() != 0){
                referenceURL.add(refURLData);
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
        
        final Element logElement = getOptionalChild(
            element,
            LOG_TAG_NAME
        );
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
        
        final Element defaultLogElement = getOptionalChild(
            element,
            DefaultLogMetaData.DEFAULT_LOG_TAG_NAME
        );
        if(defaultLogElement != null){
            if(ifdefMatch && defaultLog != null){
                throw new DeploymentException("Element of " + DefaultLogMetaData.DEFAULT_LOG_TAG_NAME + " is duplicated.");
            }
            DefaultLogMetaData tmp = new DefaultLogMetaData(this);
            if(ifdefData != null){
                tmp.setIfDefMetaData(ifdefData);
                ifdefData.addChild(tmp);
            }
            tmp.importXML(defaultLogElement);
            if(ifdefMatch){
                defaultLog = tmp;
            }
        }
        
        final Element editorsElement
             = getOptionalChild(element, PropertyEditorsMetaData.PROPERTY_EDITORS_TAG_NAME);
        if(editorsElement != null){
            PropertyEditorsMetaData tmp = null;
            if(ifdefMatch){
                if(propertyEditors == null){
                    tmp = new PropertyEditorsMetaData(this);
                }else{
                    tmp = propertyEditors;
                }
            }else{
                tmp = new PropertyEditorsMetaData(this);
            }
            if(ifdefData != null){
                tmp.setIfDefMetaData(ifdefData);
                ifdefData.addChild(tmp);
            }
            tmp.importXML(editorsElement);
            if(ifdefMatch){
                propertyEditors = tmp;
            }
        }
        
        final Iterator managerElements = getChildrenByTagName(
            element,
            ManagerMetaData.MANAGER_TAG_NAME
        );
        while(managerElements.hasNext()){
            final ManagerMetaData managerData
                 = new ManagerMetaData(myLoader, this);
            if(ifdefData != null){
                managerData.setIfDefMetaData(ifdefData);
                ifdefData.addChild(managerData);
            }
            managerData.importXML((Element)managerElements.next());
            if(ifdefMatch && getManager(managerData.getName()) != null){
                throw new DeploymentException("Name of manager is duplicated. name=" + managerData.getName());
            }
            if(ifdefMatch){
                addManager(managerData);
            }
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        buf.append("<?xml version=\"1.0\"");
        if(encoding != null){
            buf.append(" encoding=\"");
            buf.append(encoding);
            buf.append("\"");
        }
        buf.append("?>");
        buf.append(LINE_SEPARATOR);
        if(docType != null){
            buf.append(docType);
            buf.append(LINE_SEPARATOR);
            buf.append(LINE_SEPARATOR);
        }
        appendComment(buf);
        buf.append('<').append(SERVER_TAG_NAME).append('>');
        if(properties.size() != 0){
            buf.append(LINE_SEPARATOR);
            final StringBuilder subBuf = new StringBuilder();
            final Iterator props = properties.values().iterator();
            while(props.hasNext()){
                final ServerPropertyMetaData propData
                    = (ServerPropertyMetaData)props.next();
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
        if(referenceURL.size() != 0){
            buf.append(LINE_SEPARATOR);
            final Iterator urls = referenceURL.iterator();
            while(urls.hasNext()){
                final RefURLMetaData refURLData = (RefURLMetaData)urls.next();
                if(refURLData.getIfDefMetaData() != null){
                    continue;
                }
                refURLData.toXML(buf);
                if(urls.hasNext()){
                    buf.append(LINE_SEPARATOR);
                }
            }
        }
        if(propertyEditors != null
            && propertyEditors.getIfDefMetaData() == null){
            buf.append(LINE_SEPARATOR);
            propertyEditors.toXML(buf);
        }
        if(defaultLog != null && defaultLog.getIfDefMetaData() == null){
            buf.append(LINE_SEPARATOR);
            buf.append(
                addIndent(defaultLog.toXML(new StringBuilder()))
            );
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
        if(managers.size() != 0){
            final Iterator datas = managers.values().iterator();
            buf.append(LINE_SEPARATOR);
            while(datas.hasNext()){
                MetaData managerData = (MetaData)datas.next();
                if(managerData.getIfDefMetaData() != null){
                    continue;
                }
                buf.append(
                    addIndent(managerData.toXML(new StringBuilder()))
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
        buf.append("</").append(SERVER_TAG_NAME).append('>');
        return buf;
    }
}
