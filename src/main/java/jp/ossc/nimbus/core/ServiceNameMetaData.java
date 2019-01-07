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
import org.w3c.dom.*;

import jp.ossc.nimbus.beans.ServiceNameEditor;

/**
 * サービス名を表す要素メタデータ。<p>
 * 
 * @author M.Takata
 */
public class ServiceNameMetaData extends MetaData implements Serializable{
    
    private static final long serialVersionUID = 3198561088823261679L;
    
    /**
     * サービス名を表す要素のmanager-name属性の属性名文字列。<p>
     */
    protected static final String MANAGER_NAME_ATTRIBUTE_NAME = "manager-name";
    
    /**
     * この要素の名前。<p>
     */
    protected String tagName;
    
    /**
     * マネージャ名。<p>
     */
    protected String managerName;
    
    /**
     * サービス名。<p>
     */
    protected String serviceName;
    
    protected ServiceName serviceNameObject;
    
    protected boolean isRelativeManagerName;
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * 
     * @param parent 親要素のメタデータ
     */
    public ServiceNameMetaData(MetaData parent){
        super(parent);
    }
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * 
     * @param parent 親要素のメタデータ
     * @param manager サービスが登録される{@link ServiceManager}の名前
     * @param service サービスの名前
     */
    public ServiceNameMetaData(MetaData parent, String manager, String service){
        this(parent, null, manager, service);
    }
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * 
     * @param parent 親要素のメタデータ
     * @param name この要素の名前
     * @param manager サービスが登録される{@link ServiceManager}の名前
     * @param service サービスの名前
     */
    public ServiceNameMetaData(MetaData parent, String name, String manager, String service){
        super(parent);
        tagName = name;
        managerName = manager;
        serviceName = service;
    }
    
    /**
     * サービス名を取得する。<p>
     * 
     * @return サービス名
     */
    public String getServiceName(){
        return serviceName;
    }
    
    /**
     * サービス名を設定する。<p>
     * 
     * @param name サービス名
     */
    public void setServiceName(String name){
        serviceName = name;
    }
    
    public ServiceName getServiceNameObject(){
        if(serviceNameObject != null){
            return serviceNameObject;
        }
        String serviceNameStr = serviceName;
        if(serviceNameStr != null){
            // システムプロパティの置換
            serviceNameStr = Utility.replaceSystemProperty(serviceNameStr);
            ServiceLoader loader = findServiceLoader(getParent());
            if(loader != null){
                // サービスローダ構成プロパティの置換
                serviceNameStr = Utility.replaceServiceLoderConfig(
                    serviceNameStr,
                    loader.getConfig()
                );
            }
            ServiceMetaData serviceData = findServiceMetaData(getParent());
            if(serviceData != null){
                // サービスプロパティの置換
                serviceNameStr = Utility.replaceServiceProperty(serviceData, serviceNameStr);
            }
            ManagerMetaData managerData = findManagerMetaData(getParent());
            if(managerData != null){
                // マネージャプロパティの置換
                serviceNameStr = Utility.replaceManagerProperty(managerData, serviceNameStr);
            }
            // サーバプロパティの置換
            serviceNameStr = Utility.replaceServerProperty(serviceNameStr);
            final ServiceNameEditor editor = new ServiceNameEditor();
            String managerName = this.managerName == null ? findManagerName(getParent()) : this.managerName;
            editor.setServiceManagerName(managerName);
            if(serviceNameStr.length() != 0 && serviceNameStr.indexOf('#') == -1){
                serviceNameStr = '#' + serviceNameStr;
            }
            editor.setAsText(serviceNameStr);
            if(editor.isRelativeManagerName()){
                isRelativeManagerName = true;
            }
            serviceNameObject = (ServiceName)editor.getValue();
        }
        return serviceNameObject;
    }
    
    protected ServiceLoader findServiceLoader(MetaData metaData){
        if(metaData == null){
            return null;
        }
        if(metaData instanceof ObjectMetaData){
            return ((ObjectMetaData)metaData).getServiceLoader();
        }
        if(metaData instanceof ManagerMetaData){
            return ((ManagerMetaData)metaData).getServiceLoader();
        }
        if(metaData instanceof ServerMetaData){
            return ((ServerMetaData)metaData).getServiceLoader();
        }
        return findServiceLoader(metaData.getParent());
    }
    
    protected ServiceMetaData findServiceMetaData(MetaData metaData){
        if(metaData == null){
            return null;
        }
        if(metaData instanceof ServiceMetaData){
            return (ServiceMetaData)metaData;
        }
        return findServiceMetaData(metaData.getParent());
    }
    
    protected ManagerMetaData findManagerMetaData(MetaData metaData){
        if(metaData == null){
            return null;
        }
        if(metaData instanceof ServiceMetaData){
            return ((ServiceMetaData)metaData).getManager();
        }
        if(metaData instanceof ManagerMetaData){
            return (ManagerMetaData)metaData;
        }
        return findManagerMetaData(metaData.getParent());
    }
    
    protected String findManagerName(MetaData metaData){
        if(metaData == null){
            return null;
        }
        if(metaData instanceof ServiceMetaData){
            ManagerMetaData managerData = ((ServiceMetaData)metaData).getManager();
            if(managerData != null){
                return managerData.getName();
            }
        }
        if(metaData instanceof ManagerMetaData){
            return ((ManagerMetaData)metaData).getName();
        }
        return findManagerName(metaData.getParent());
    }
    
    /**
     * マネージャ名が相対指定だったかを判定する。<p>
     *
     * @return マネージャ名が相対指定だった場合、true
     */
    public boolean isRelativeManagerName(){
        return isRelativeManagerName;
    }
    
    /**
     * サービス名を表す要素のElementをパースして、自分自身の初期化を行う。<p>
     *
     * @param element サービス名を表す要素のElement
     * @exception DeploymentException サービス名を表す要素の解析に失敗した場合
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        
        tagName = element.getTagName();
        
        String managerName = getOptionalAttribute(
            element,
            MANAGER_NAME_ATTRIBUTE_NAME
        );
        if(managerName != null){
            this.managerName = managerName;
        }
        
        String content = getElementContent(element);
        if(content != null && content.length() != 0){
            serviceName = content;
        }else{
            throw new DeploymentException(
                "Content of '" + tagName + "' element must not be null."
            );
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append('<').append(tagName).append('>');
        if(getServiceNameObject() != null){
            buf.append(getServiceNameObject());
        }
        buf.append("</").append(tagName).append('>');
        return buf;
    }
    
    /**
     * 引数のobjがこのオブジェクトと等しいか調べる。<p>
     * {@link Service}が登録されている{@link ServiceManager}の名前とServiceの名前の両方が等しい場合のみtrueを返す。<br>
     *
     * @param obj 比較対象のオブジェクト
     * @return 等しい場合true
     */
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(this == obj){
            return true;
        }
        if(obj instanceof ServiceNameMetaData){
            final ServiceNameMetaData name = (ServiceNameMetaData)obj;
            if((serviceName == null && name.serviceName != null)
                || (serviceName != null && name.serviceName == null)){
                return false;
            }else if(serviceName != null && name.serviceName != null
                && !serviceName.equals(name.serviceName)){
                return false;
            }
            return true;
        }
        return false;
    }
    
    /**
     * ハッシュ値を取得する。<p>
     *
     * @return ハッシュ値
     */
    public int hashCode(){
        return serviceName != null ? serviceName.hashCode() : 0;
    }
    
    /**
     * このインスタンスの文字列表現を取得する。<p>
     *
     * @return 文字列表現
     */
    public String toString(){
        final StringBuffer buf = new StringBuffer();
        buf.append(super.toString());
        buf.append('{');
        if(managerName != null){
            buf.append(managerName);
            buf.append('#');
        }
        buf.append(serviceName);
        buf.append('}');
        return buf.toString();
    }
    
    /**
     * このインスタンスの複製を生成する。<p>
     *
     * @return このインスタンスの複製
     */
    public Object clone(){
        ServiceNameMetaData clone = (ServiceNameMetaData)super.clone();
        clone.serviceNameObject = null;
        clone.isRelativeManagerName = false;
        return clone;
    }
}
