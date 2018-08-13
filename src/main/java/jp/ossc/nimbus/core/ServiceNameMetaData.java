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
     */
    public ServiceNameMetaData(MetaData parent, String manager){
        super(parent);
        serviceName = manager;
    }
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * 
     * @param parent 親要素のメタデータ
     * @param manager サービスが登録される{@link ServiceManager}の名前
     * @param service サービスの名前
     */
    public ServiceNameMetaData(MetaData parent, String manager, String service){
        super(parent);
        serviceName = manager + '#' + service;
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
        serviceName = manager + '#' + service;
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
            final MetaData parent = getParent();
            if(parent != null && parent instanceof ObjectMetaData){
                ObjectMetaData objData = (ObjectMetaData)parent;
                if(objData.getServiceLoader() != null){
                    // サービスローダ構成プロパティの置換
                    serviceNameStr = Utility.replaceServiceLoderConfig(
                        serviceNameStr,
                        objData.getServiceLoader().getConfig()
                    );
                }
            }
            String managerName = null;
            if(parent != null && parent instanceof ServiceMetaData){
                ServiceMetaData serviceData = (ServiceMetaData)parent;
                if(serviceData.getManager() != null){
                    // マネージャプロパティの置換
                    serviceNameStr = Utility.replaceManagerProperty(serviceData.getManager(), serviceNameStr);
                    managerName = serviceData.getManager().getName();
                }
            }
            // サーバプロパティの置換
            serviceNameStr = Utility.replaceServerProperty(serviceNameStr);
            final ServiceNameEditor editor = new ServiceNameEditor();
            editor.setServiceManagerName(managerName);
            editor.setAsText(serviceNameStr);
            if(editor.isRelativeManagerName()){
                isRelativeManagerName = true;
            }
            serviceNameObject = (ServiceName)editor.getValue();
        }
        return serviceNameObject;
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
        if(managerName == null){
            if(serviceName == null){
                managerName = ServiceManager.DEFAULT_NAME;
            }else if(serviceName.indexOf('#') == -1){
                isRelativeManagerName = true;
                managerName = serviceName;
                serviceName = null;
            }else{
                serviceName = null;
            }
        }else{
            serviceName = null;
        }
        
        String content = getElementContent(element);
        if(content != null && content.length() != 0){
            serviceName = managerName + '#' + content;
        }else{
            throw new DeploymentException(
                "Content of '" + tagName + "' element must not be null."
            );
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append('<').append(tagName).append('>');
        if(serviceName != null){
            buf.append(serviceName);
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
}
