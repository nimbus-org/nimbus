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

import jp.ossc.nimbus.beans.ServiceNameEditor;

/**
 * サービス定義&lt;service&gt;要素メタデータ。<p>
 * サービス定義ファイルの&lt;service&gt;要素に記述された内容を格納するメタデータコンテナである。<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">サービス定義ファイルDTD</a>
 */
public class ServiceMetaData extends ObjectMetaData implements Serializable{
    
    private static final long serialVersionUID = 1524948064493968357L;
    
    /**
     * &lt;service&gt;要素の要素名文字列。<p>
     */
    public static final String SERVICE_TAG_NAME = "service";
    
    public static final String INSTANCE_TYPE_SINGLETON = "singleton";
    public static final String INSTANCE_TYPE_FACTORY = "factory";
    public static final String INSTANCE_TYPE_THREADLOCAL = "threadlocal";
    public static final String INSTANCE_TYPE_TEMPLATE = "template";
    
    private static final String DEPENDS_TAG_NAME = "depends";
    private static final String OPT_CONF_TAG_NAME = "optional-config";
    private static final String NAME_ATTRIBUTE_NAME = "name";
    private static final String INIT_STATE_ATTRIBUTE_NAME = "initState";
    private static final String INSTANCE_ATTRIBUTE_NAME = "instance";
    private static final String MANAGEMENT_ATTRIBUTE_NAME = "management";
    private static final String CREATE_TEMPLATE_ATTRIBUTE_NAME = "createTemplate";
    private static final String TEMPLATE_ATTRIBUTE_NAME = "template";
    
    private String name;
    
    private String initState = Service.STATES[Service.STARTED];
    
    private int initStateValue = Service.STARTED;
    
    private String instance = INSTANCE_TYPE_SINGLETON;
    
    private boolean isFactory;
    
    private boolean isTemplate;
    
    private boolean isManagement;
    
    private boolean isCreateTemplate = true;
    
    private transient Element optionalConfig;
    
    private List depends = new ArrayList();
    
    private ManagerMetaData manager;
    
    private String template;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public ServiceMetaData(){
    }
    
    /**
     * 指定したサービス名、実装クラス名のメタデータを持つインスタンスを生成する。<p>
     * @param name サービス名
     * @param code 実装クラス名
     */
    public ServiceMetaData(String name, String code){
        setName(name);
        setCode(code);
    }
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * ServiceMetaDataの親要素は、&lt;manager&gt;要素を表すManagerMetaData、または、&lt;depends&gt;要素を表すDependsMetaDataである。<br>
     * 
     * @param loader 自分をロードしたServiceLoader
     * @param parent 親要素のメタデータ
     * @param manager この&lt;service&gt;要素で定義されたサービスが登録される{@link ServiceManager}の&lt;manager&gt;要素を表すManagerMetaData
     * @see ManagerMetaData
     * @see ServiceMetaData.DependsMetaData
     */
    public ServiceMetaData(
        ServiceLoader loader,
        MetaData parent,
        ManagerMetaData manager
    ){
        super(loader, parent, manager.getName());
        this.manager = manager;
    }
    
    /**
     * この&lt;service&gt;要素のname属性の値を取得する。<p>
     * 
     * @return name属性の値
     */
    public String getName(){
        return name;
    }
    
    /**
     * この&lt;service&gt;要素のname属性の値を設定する。<p>
     * 
     * @param name name属性の値
     */
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * この&lt;service&gt;要素のinitState属性の値を取得する。<p>
     * 
     * @return initState属性の値
     */
    public String getInitState(){
        return initState;
    }
    
    /**
     * この&lt;service&gt;要素のinitState属性の値を設定する。<p>
     * 
     * @param state initState属性の値
     */
    public void setInitState(String state){
        for(int i = 0; i < Service.STATES.length; i++){
            if(Service.STATES[i].equals(state)){
                initStateValue = i;
                initState = state;
                return;
            }
        }
    }
    
    /**
     * この&lt;service&gt;要素のinstance属性の値を取得する。<p>
     * 
     * @return instance属性の値
     */
    public String getInstance(){
        return instance;
    }
    
    /**
     * この&lt;service&gt;要素のinstance属性の値を設定する。<p>
     * 
     * @param val instance属性の値
     */
    public void setInstance(String val){
        if(INSTANCE_TYPE_SINGLETON.equals(val)){
            instance = val;
        }else if(INSTANCE_TYPE_FACTORY.equals(val)
             || INSTANCE_TYPE_THREADLOCAL.equals(val)){
            isFactory = true;
            instance = val;
        }else if(INSTANCE_TYPE_TEMPLATE.equals(val)){
            isTemplate = true;
            instance = val;
        }else{
            throw new IllegalArgumentException(val);
        }
    }
    
    /**
     * この&lt;service&gt;要素が表すサービスがファクトリかどうかを判定する。<p>
     *
     * @return この&lt;service&gt;要素が表すサービスがファクトリの場合true
     */
    public boolean isFactory(){
        return isFactory;
    }
    
    /**
     * この&lt;service&gt;要素が表すサービスがファクトリかどうかを設定する。<p>
     *
     * @param isFactory trueの場合、この&lt;service&gt;要素が表すサービスがファクトリ
     */
    public void setFactory(boolean isFactory){
        this.isFactory = isFactory;
    }
    
    /**
     * この&lt;service&gt;要素のtemplate属性の値を取得する。<p>
     * 
     * @return template属性の値
     */
    public String getTemplateName(){
        return template;
    }
    
    /**
     * この&lt;service&gt;要素のtemplate属性の値を設定する。<p>
     * 
     * @param name template属性の値
     */
    public void setTemplateName(String name){
        this.template = name;
    }
    
    /**
     * この&lt;service&gt;要素が表すサービスがテンプレートかどうかを判定する。<p>
     *
     * @return この&lt;service&gt;要素が表すサービスがテンプレートの場合true
     */
    public boolean isTemplate(){
        return isTemplate;
    }
    
    /**
     * この&lt;service&gt;要素が表すサービスがテンプレートかどうかを設定する。<p>
     *
     * @param isTemplate trueの場合、この&lt;service&gt;要素が表すサービスがテンプレート
     */
    public void setTemplate(boolean isTemplate){
        this.isTemplate = isTemplate;
    }
    
    /**
     * この&lt;service&gt;要素のmanagement属性の値を取得する。<p>
     * 
     * @return management属性の値
     */
    public boolean isManagement(){
        return isManagement;
    }
    
    /**
     * この&lt;service&gt;要素のmanagement属性の値を設定する。<p>
     * 
     * @param flg management属性の値
     */
    public void setManagement(boolean flg){
        isManagement = flg;
    }
    
    /**
     * この&lt;service&gt;要素のcreateTemplate属性の値を取得する。<p>
     * 
     * @return createTemplate属性の値
     */
    public boolean isCreateTemplate(){
        return isCreateTemplate;
    }
    
    /**
     * この&lt;service&gt;要素のcreateTemplate属性の値を設定する。<p>
     * 
     * @param flg createTemplate属性の値
     */
    public void setCreateTemplate(boolean flg){
        isCreateTemplate = flg;
    }
    
    /**
     * この&lt;service&gt;要素のinitState属性の値を取得する。<p>
     * 
     * @return initState属性の値
     */
    public int getInitStateValue(){
        return initStateValue;
    }
    
    /**
     * この&lt;service&gt;要素のinitState属性の値を設定する。<p>
     * 
     * @param state initState属性の値
     */
    public void setInitStateValue(int state){
        if(state >= 0 && Service.STATES.length > state){
            initStateValue = state;
            initState = Service.STATES[state];
        }
    }
    
    /**
     * この&lt;service&gt;要素の子要素&lt;optional-config&gt;要素を取得する。<p>
     *
     * @return 子要素&lt;optional-config&gt;要素
     */
    public Element getOptionalConfig(){
        return optionalConfig;
    }
    
    /**
     * この&lt;service&gt;要素の子要素&lt;optional-config&gt;要素を設定する。<p>
     *
     * @param option 子要素&lt;optional-config&gt;要素
     */
    public void setOptionalConfig(Element option){
        optionalConfig = option;
    }
    
    /**
     * この&lt;service&gt;要素の子要素&lt;depends&gt;要素を表す{@link ServiceMetaData.DependsMetaData}のリストを取得する。<p>
     *
     * @return 子要素&lt;depends&gt;要素を表すDependsMetaDataのリスト
     */
    public List getDepends(){
        return depends;
    }
    
    /**
     * この&lt;service&gt;要素の子要素&lt;depends&gt;要素を表す{@link ServiceMetaData.DependsMetaData}を追加する。<p>
     *
     * @param depends 子要素&lt;depends&gt;要素を表すDependsMetaData
     */
    public void addDepends(DependsMetaData depends){
        this.depends.add(depends);
    }
    
    /**
     * この&lt;service&gt;要素の子要素&lt;depends&gt;要素を表す{@link ServiceMetaData.DependsMetaData}を削除する。<p>
     *
     * @param depends 子要素&lt;depends&gt;要素を表すDependsMetaData
     */
    public void removeDepends(DependsMetaData depends){
        this.depends.remove(depends);
    }
    
    /**
     * この&lt;service&gt;要素の子要素&lt;depends&gt;要素を表す{@link ServiceMetaData.DependsMetaData}を全て削除する。<p>
     */
    public void clearDepends(){
        this.depends.clear();
    }
    
    /**
     * この&lt;service&gt;要素で定義されたサービスが登録されるサービスマネージャを定義する&lt;manager&gt;要素を表すManagerMetaDataを取得する。<p>
     *
     * @return この&lt;service&gt;要素で定義されたサービスが登録されるサービスマネージャを定義する&lt;manager&gt;要素を表すManagerMetaData
     */
    public ManagerMetaData getManager(){
        return manager;
    }
    
    /**
     * この&lt;service&gt;要素で定義されたサービスが登録されるサービスマネージャを定義する&lt;manager&gt;要素を表すManagerMetaDataを設定する。<p>
     *
     * @param manager この&lt;service&gt;要素で定義されたサービスが登録されるサービスマネージャを定義する&lt;manager&gt;要素を表すManagerMetaData
     */
    public void setManager(ManagerMetaData manager){
        this.manager = manager;
    }
    
    /**
     * &lt;depends&gt;要素を生成する。<p>
     *
     * @param managerName 依存するサービスが登録されているマネージャ名
     * @param serviceName 依存するサービスのサービス名
     * @return &lt;depends&gt;要素のメタデータ
     */
    public DependsMetaData createDependsMetaData(
        String managerName,
        String serviceName
    ){
        return new DependsMetaData(
            managerName,
            serviceName
        );
    }
    
    /**
     * 要素名がserviceである事をチェックする。<p>
     *
     * @param element service要素
     * @exception DeploymentException 要素名がserviceでない場合
     */
    protected void checkTagName(Element element) throws DeploymentException{
        if(!element.getTagName().equals(SERVICE_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + SERVICE_TAG_NAME + " : "
                 + element.getTagName()
            );
        }
    }
    
    /**
     * テンプレートを適用したメタデータを生成する。<p>
     *
     * @param loader ServiceLoader
     * @return テンプレート適用後のメタデータ
     * @exception ServiceNotFoundException テンプレートとして指定されているサービスメタデータが見つからない場合
     */
    public ServiceMetaData applyTemplate(ServiceLoader loader) throws ServiceNotFoundException{
        if(getTemplateName() == null){
            return this;
        }
        ServiceNameEditor editor = new ServiceNameEditor();
        if(getManager() != null){
            editor.setServiceManagerName(getManager().getName());
        }
        editor.setAsText(getTemplateName());
        ServiceName temlateServiceName = (ServiceName)editor.getValue();
        ServiceMetaData templateData = null;
        if(loader != null){
            templateData = loader.getServiceMetaData(
                temlateServiceName.getServiceManagerName(),
                temlateServiceName.getServiceName()
            );
        }
        if(templateData == null){
            templateData = ServiceManagerFactory.getServiceMetaData(temlateServiceName);
        }
        templateData = (ServiceMetaData)templateData.clone();
        templateData = templateData.applyTemplate(loader);
        
        ServiceMetaData result = (ServiceMetaData)clone();
        if(result.code == null){
            result.code = templateData.code;
        }
        if(result.constructor == null && templateData.constructor != null){
            ConstructorMetaData cons = (ConstructorMetaData)templateData.constructor.clone();
            cons.setParent(result);
            result.constructor = cons;
        }
        if(templateData.fields.size() != 0){
            Iterator entries = templateData.fields.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                if(!result.fields.containsKey(entry.getKey())){
                    FieldMetaData field = (FieldMetaData)entry.getValue();
                    field = (FieldMetaData)field.clone();
                    field.setParent(result);
                    result.fields.put(entry.getKey(), field);
                }
            }
        }
        if(templateData.attributes.size() != 0){
            Iterator entries = templateData.attributes.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                if(!result.attributes.containsKey(entry.getKey())){
                    AttributeMetaData attr = (AttributeMetaData)entry.getValue();
                    attr = (AttributeMetaData)attr.clone();
                    attr.setParent(result);
                    result.attributes.put(entry.getKey(), attr);
                }
            }
        }
        if(templateData.invokes.size() != 0){
            result.invokes.addAll(templateData.invokes);
        }
        if(result.optionalConfig == null){
            result.optionalConfig = templateData.optionalConfig;
        }
        for(int i = 0; i < templateData.depends.size(); i++){
            DependsMetaData deps = (DependsMetaData)templateData.depends.get(i);
            deps = (DependsMetaData)deps.clone();
            deps.setParent(result);
            result.depends.add(deps);
        }
        return result;
    }
    
    protected void importCodeAttribute(Element element) throws DeploymentException{
        if(template == null){
            super.importCodeAttribute(element);
        }else{
            code = getOptionalAttribute(element, CODE_ATTRIBUTE_NAME);
        }
    }
    
    /**
     * &lt;service&gt;要素のElementをパースして、自分自身の初期化、及び子要素のメタデータの生成を行う。<p>
     *
     * @param element &lt;service&gt;要素のElement
     * @exception DeploymentException &lt;service&gt;要素の解析、その結果によるメタデータの生成に失敗した場合
     */
    public void importXML(Element element) throws DeploymentException{
        
        template = getOptionalAttribute(
            element,
            TEMPLATE_ATTRIBUTE_NAME
        );
        if(template == null){
            name = getUniqueAttribute(element, NAME_ATTRIBUTE_NAME);
        }else{
            ServiceNameEditor editor = new ServiceNameEditor();
            if(getManager() != null){
                editor.setServiceManagerName(getManager().getName());
            }
            editor.setAsText(template);
            ServiceName temlateServiceName = (ServiceName)editor.getValue();
            name = temlateServiceName.getServiceName();
        }
        
        if(name != null){
            // システムプロパティの置換
            name = Utility.replaceSystemProperty(name);
            if(getServiceLoader() != null){
                // サービスローダ構成プロパティの置換
                name = Utility.replaceServiceLoderConfig(
                    name,
                    getServiceLoader().getConfig()
                );
            }
            if(manager != null){
                // マネージャプロパティの置換
                name = Utility.replaceManagerProperty(manager, name);
            }
            // サーバプロパティの置換
            name = Utility.replaceServerProperty(name);
        }
        
        String tmpInitState
             = getOptionalAttribute(element, INIT_STATE_ATTRIBUTE_NAME);
        if(tmpInitState != null){
            // システムプロパティの置換
            tmpInitState = Utility.replaceSystemProperty(tmpInitState);
            if(getServiceLoader() != null){
                // サービスローダ構成プロパティの置換
                tmpInitState = Utility.replaceServiceLoderConfig(
                    tmpInitState,
                    getServiceLoader().getConfig()
                );
            }
            if(manager != null){
                // マネージャプロパティの置換
                tmpInitState = Utility.replaceManagerProperty(manager, tmpInitState);
            }
            // サーバプロパティの置換
            tmpInitState = Utility.replaceServerProperty(tmpInitState);
        }
        
        if(Service.STATES[Service.CREATED].equals(tmpInitState)){
            initState = getOptionalAttribute(
                element,
                INIT_STATE_ATTRIBUTE_NAME
            );
            initStateValue = Service.CREATED;
        }else if(Service.STATES[Service.STARTED].equals(tmpInitState)){
            initState = getOptionalAttribute(
                element,
                INIT_STATE_ATTRIBUTE_NAME
            );
            initStateValue = Service.STARTED;
        }
        setInstance(
            getOptionalAttribute(
                element,
                INSTANCE_ATTRIBUTE_NAME,
                INSTANCE_TYPE_SINGLETON
            )
        );
        final String management = getOptionalAttribute(
            element,
            MANAGEMENT_ATTRIBUTE_NAME
        );
        if(management != null){
            isManagement = Boolean.valueOf(management).booleanValue();
        }
        final String createTemplate = getOptionalAttribute(
            element,
            CREATE_TEMPLATE_ATTRIBUTE_NAME
        );
        if(createTemplate != null){
            isCreateTemplate = Boolean.valueOf(createTemplate).booleanValue();
        }
        
        super.importXML(element);
    }
    
    protected void importXMLInner(Element element, IfDefMetaData ifdefData) throws DeploymentException{
        
        super.importXMLInner(element, ifdefData);
        
        final boolean ifdefMatch
            = ifdefData == null ? true : ifdefData.isMatch();
        
        final Element optionalConfig = getOptionalChild(element, OPT_CONF_TAG_NAME);
        if(ifdefMatch){
            this.optionalConfig = optionalConfig;
        }
        
        final Iterator dependsElements = getChildrenByTagName(
            element,
            DEPENDS_TAG_NAME
        );
        while(dependsElements.hasNext()){
            final Element dependsElement = (Element)dependsElements.next();
            final DependsMetaData dependsData = new DependsMetaData(manager.getName());
            if(ifdefData != null){
                dependsData.setIfDefMetaData(ifdefData);
                ifdefData.addChild(dependsData);
            }
            dependsData.importXML(dependsElement);
            if(ifdefMatch){
                depends.add(dependsData);
            }
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append('<').append(SERVICE_TAG_NAME);
        if(name != null){
            buf.append(' ').append(NAME_ATTRIBUTE_NAME)
                .append("=\"").append(name).append("\"");
        }
        if(code != null){
            buf.append(' ').append(CODE_ATTRIBUTE_NAME)
                .append("=\"").append(code).append("\"");
        }
        if(initState != null
             && !Service.STATES[Service.STARTED].equals(initState)){
            buf.append(' ').append(INIT_STATE_ATTRIBUTE_NAME)
                .append("=\"").append(initState).append("\"");
        }
        if(instance != null && !INSTANCE_TYPE_SINGLETON.equals(instance)){
            buf.append(' ').append(INSTANCE_ATTRIBUTE_NAME)
                .append("=\"").append(instance).append("\"");
        }
        if(isManagement){
            buf.append(' ').append(MANAGEMENT_ATTRIBUTE_NAME)
                .append("=\"").append(isManagement).append("\"");
        }
        if(!isCreateTemplate){
            buf.append(' ').append(CREATE_TEMPLATE_ATTRIBUTE_NAME)
                .append("=\"").append(isCreateTemplate).append("\"");
        }
        
        if(constructor == null && fields.size() == 0
             && attributes.size() == 0 && invokes.size() == 0
             && depends.size() == 0
             && (ifDefMetaDataList == null || ifDefMetaDataList.size() == 0)
        ){
            buf.append("/>");
        }else{
            buf.append('>');
            if(constructor != null && constructor.getIfDefMetaData() == null){
                buf.append(LINE_SEPARATOR);
                buf.append(
                    addIndent(constructor.toXML(new StringBuilder()))
                );
            }
            if(fields.size() != 0){
                buf.append(LINE_SEPARATOR);
                final Iterator datas = fields.values().iterator();
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
            if(attributes.size() != 0){
                buf.append(LINE_SEPARATOR);
                final Iterator datas = attributes.values().iterator();
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
            if(invokes.size() != 0){
                buf.append(LINE_SEPARATOR);
                final Iterator datas = invokes.iterator();
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
            if(depends.size() != 0){
                buf.append(LINE_SEPARATOR);
                final Iterator datas = depends.iterator();
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
            buf.append("</").append(SERVICE_TAG_NAME).append('>');
        }
        return buf;
    }
    
    /**
     * このインスタンスの複製を生成する。<p>
     *
     * @return このインスタンスの複製
     */
    public Object clone(){
        ServiceMetaData clone = (ServiceMetaData)super.clone();
        clone.depends = new ArrayList();
        for(int i = 0; i < depends.size(); i++){
            DependsMetaData deps = (DependsMetaData)depends.get(i);
            deps = (DependsMetaData)deps.clone();
            deps.setParent(clone);
            clone.depends.add(deps);
        }
        return clone;
    }
    
    /**
     * このインスタンスの文字列表現を取得する。<p>
     *
     * @return 文字列表現
     */
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        buf.append(getClass().getName());
        buf.append('@');
        buf.append(hashCode());
        buf.append('{');
        if(getName() != null){
            buf.append(NAME_ATTRIBUTE_NAME);
            buf.append('=');
            buf.append(getName());
            buf.append(',');
        }
        buf.append(CODE_ATTRIBUTE_NAME);
        buf.append('=');
        buf.append(getCode());
        buf.append('}');
        return buf.toString();
    }
    
    /**
     * 依存関係定義&lt;depends&gt;要素メタデータ。<p>
     * サービス定義ファイルの&lt;depends&gt;要素に記述された内容を格納するメタデータコンテナである。<p>
     *
     * @author M.Takata
     * @see <a href="nimbus-service_1_0.dtd">サービス定義ファイルDTD</a>
     */
    public class DependsMetaData extends ServiceNameMetaData
     implements Serializable{
        
        private static final long serialVersionUID = -2867707582371947233L;
        
        private ServiceMetaData serviceData;
        
        /**
         * 指定されたサービスマネージャに登録されたサービスに依存する事を表す依存関係定義のインスタンスを生成する。<p>
         *
         * @param manager 依存するサービスが登録されるサービスマネージャ名
         */
        public DependsMetaData(String manager){
            super(ServiceMetaData.this, manager);
        }
        
        /**
         * 指定されたサービスに依存する事を表す依存関係定義のインスタンスを生成する。<p>
         *
         * @param manager 依存するサービスが登録されるサービスマネージャ名
         * @param service 依存するサービス名
         */
        public DependsMetaData(String manager, String service){
            super(ServiceMetaData.this, DEPENDS_TAG_NAME, manager, service);
        }
        
        /**
         * &lt;depends&gt;要素の子要素の&lt;service&gt;要素を表すServiceMetaDataを取得する。<p>
         *
         * @return &lt;service&gt;要素を表すServiceMetaData
         */
        public ServiceMetaData getService(){
            return serviceData;
        }
        
        /**
         * &lt;depends&gt;要素のElementをパースして、自分自身の初期化、及び子要素のメタデータの生成を行う。<p>
         *
         * @param element &lt;depends&gt;要素のElement
         * @exception DeploymentException &lt;depends&gt;要素の解析、その結果によるメタデータの生成に失敗した場合
         */
        public void importXML(Element element) throws DeploymentException{
            
            if(!element.getTagName().equals(DEPENDS_TAG_NAME)){
                throw new DeploymentException(
                    "Tag must be " + DEPENDS_TAG_NAME + " : "
                     + element.getTagName()
                );
            }
            tagName = element.getTagName();
            final Element serviceElement = getOptionalChild(
                element,
                SERVICE_TAG_NAME
            );
            final boolean ifdefMatch
                = DependsMetaData.this.getIfDefMetaData() == null ? true : DependsMetaData.this.getIfDefMetaData().isMatch();
            if(serviceElement == null){
                final ServiceMetaData parent = (ServiceMetaData)getParent();
                if(parent != null){
                    setManagerName(parent.getManager().getName());
                }
                super.importXML(element);
            }else{
                final ServiceMetaData serviceData
                     = new ServiceMetaData(myLoader, this, manager);
                serviceData.importXML(serviceElement);
                serviceData.setIfDefMetaData(DependsMetaData.this.getIfDefMetaData());
                setServiceName(serviceData.getName());
                if(ifdefMatch){
                    this.serviceData = serviceData;
                    setManagerName(manager.getName());
                }
            }
        }
    }
}
