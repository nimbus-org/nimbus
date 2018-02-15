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
     * サービス名を表す要素のmanager-name属性の値。<p>
     */
    protected String managerName;
    
    /**
     * サービス名を表す要素の内容で指定されたサービス名。<p>
     */
    protected String serviceName;
    
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
        managerName = manager;
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
        managerName = manager;
        serviceName = service;
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
     * サービス名を表す要素のmanager-name属性の値を取得する。<p>
     * manager-name属性が省略されていた場合は、{@link ServiceManager#DEFAULT_NAME}を返す。<br>
     * 
     * @return サービス名を表す要素のmanager-name属性の値
     */
    public String getManagerName(){
        return managerName;
    }
    
    /**
     * サービス名を表す要素のmanager-name属性の値を設定する。<p>
     * 
     * @param name サービス名を表す要素のmanager-name属性の値
     */
    public void setManagerName(String name){
        managerName = name;
    }
    
    /**
     * サービス名を表す要素の内容で指定されたサービス名を取得する。<p>
     * 内容が指定されていない場合は、nullを返す。<br>
     * 
     * @return サービス名を表す要素の内容
     */
    public String getServiceName(){
        return serviceName;
    }
    
    /**
     * サービス名を表す要素の内容で指定されたサービス名を設定する。<p>
     * 
     * @param name サービス名を表す要素の内容
     */
    public void setServiceName(String name){
        serviceName = name;
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
        
        managerName = getOptionalAttribute(
            element,
            MANAGER_NAME_ATTRIBUTE_NAME,
            managerName == null ? ServiceManager.DEFAULT_NAME : managerName
        );
        
        String content = getElementContent(element);
        if(content != null && content.length() != 0){
            if(content != null){
                // システムプロパティの置換
                content = Utility.replaceSystemProperty(content);
                final MetaData parent = getParent();
                if(parent != null && parent instanceof ObjectMetaData){
                    ObjectMetaData objData = (ObjectMetaData)parent;
                    if(objData.getServiceLoader() != null){
                        // サービスローダ構成プロパティの置換
                        content = Utility.replaceServiceLoderConfig(
                            content,
                            objData.getServiceLoader().getConfig()
                        );
                    }
                }
                if(parent != null && parent instanceof ServiceMetaData){
                    ServiceMetaData serviceData = (ServiceMetaData)parent;
                    if(serviceData.getManager() != null){
                        // マネージャプロパティの置換
                        content = Utility.replaceManagerProperty(serviceData.getManager(), content);
                    }
                }
                // サーバプロパティの置換
                content = Utility.replaceServerProperty(content);
            }
            if(content.indexOf('#') != -1){
                final ServiceNameEditor editor = new ServiceNameEditor();
                editor.setServiceManagerName(managerName);
                editor.setAsText(content);
                final ServiceName editName = (ServiceName)editor.getValue();
                if(!editName.getServiceManagerName().equals(managerName)){
                    managerName = editName.getServiceManagerName();
                }
                serviceName = editName.getServiceName();
            }else{
                serviceName = content;
            }
        }else{
            throw new DeploymentException(
                "Content of '" + tagName + "' element must not be null."
            );
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append('<').append(tagName).append('>');
        if(managerName != null){
            buf.append(managerName);
        }
        if(serviceName != null){
            buf.append('#').append(serviceName);
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
            if((managerName == null && name.managerName != null)
                || (managerName != null && name.managerName == null)){
                return false;
            }else if(managerName != null && name.managerName != null
                && !managerName.equals(name.managerName)){
                return false;
            }
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
        return (managerName != null ? managerName.hashCode() : 0)
            + (serviceName != null ? serviceName.hashCode() : 0);
    }
}
