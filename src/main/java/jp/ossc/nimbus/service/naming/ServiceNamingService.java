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
package jp.ossc.nimbus.service.naming;

import java.io.*;
import java.util.*;

import jp.ossc.nimbus.core.*;

/**
 * サービスネーミングサービス。<p>
 * 指定した名前に対応する{@link Service}を取得するネーミングサービス。<br>
 * 指定された名前に対応するサービスが登録されている{@link ServiceManager}の{@link ServiceManager#getService(String)}で取得されるオブジェクトを返す。<br>
 * 指定された名前に対応するサービスの検索は、以下の順序で行われる。<br>
 * <ol>
 *   <li>{@link #setServiceNameReferences(ServiceNameRef[])}で設定された参照名配列に、指定した名前と同じ参照名があれば、参照名が表すサービスを取得する。</li>
 *   <li>{@link #setBootServicePath(String[])}で設定された{@link ServiceManager}の名前の配列から、該当するServiceManagerを取得して、指定した名前のサービスを検索する。</li>
 *   <li>自分自身が登録されているServiceManagerを取得して、指定した名前のサービスを検索する。</li>
 *   <li>{@link #setServicePath(String[])}で設定された{@link ServiceManager}の名前の配列から、該当するServiceManagerを取得して、指定した名前のサービスを検索する。</li>
 * </ol>
 * 
 * @author M.Takata
 */
public class ServiceNamingService extends ServiceBase
 implements ServiceNamingServiceMBean, Serializable{
    
    private static final long serialVersionUID = 3146925421831880202L;
    
    private String[] servicePath;
    private String[] bootServicePath;
    private ServiceNameRef[] serviceNameRefs;
    private Map nameRefMap = new HashMap();
    
    // ServiceNamingServiceMBeanのJavaDoc
    public void setServicePath(String[] path){
        servicePath = path;
    }
    
    // ServiceNamingServiceMBeanのJavaDoc
    public String[] getServicePath(){
        return servicePath;
    }
    
    // ServiceNamingServiceMBeanのJavaDoc
    public void setBootServicePath(String[] path){
        bootServicePath = path;
    }
    
    // ServiceNamingServiceMBeanのJavaDoc
    public String[] getBootServicePath(){
        return bootServicePath;
    }
    
    // ServiceNamingServiceMBeanのJavaDoc
    public void setServiceNameReferences(ServiceNameRef[] refs){
        if(refs == null){
            return;
        }
        final Map map = new HashMap();
        for(int i = 0, max = refs.length; i < max; i++){
            map.put(
                refs[i].getReferenceServiceName(),
                refs[i].getServiceName()
            );
        }
        nameRefMap = map;
        serviceNameRefs = refs;
    }
    
    // ServiceNamingServiceMBeanのJavaDoc
    public ServiceNameRef[] getServiceNameReferences(){
        return serviceNameRefs;
    }
    
    /**
     * 指定された名前に対応する{@link Service}を取得する。<p>
     * 指定された名前に対応するServiceが見つからない場合、nullを返す。<br>
     *
     * @param name 名前
     * @return 指定した名前に対応するService
     */
    public Object find(String name){
        if(name == null){
            return null;
        }
        if(nameRefMap.containsKey(name)){
            return findObject((ServiceName)nameRefMap.get(name));
        }
        Object obj = null;
        if(bootServicePath != null){
            for(int i = 0, max = bootServicePath.length; i < max; i++){
                obj = findObject(bootServicePath[i], name);
                if(obj != null){
                    return obj;
                }
            }
        }
        obj = findObject(getServiceManagerName(), name);
        if(obj != null){
            return obj;
        }
        if(servicePath != null){
            for(int i = 0, max = servicePath.length; i < max; i++){
                obj = findObject(servicePath[i], name);
                if(obj != null){
                    return obj;
                }
            }
        }
        return null;
    }
    
    /**
     * このサービスで返すオブジェクトを取得する。<p>
     *
     * @param name ServiceName
     * @return {@link ServiceName}に対応するオブジェクト
     */
    protected Object findObject(ServiceName name){
        try{
            return ServiceManagerFactory.getService(name);
        }catch(ServiceNotFoundException e){
            return null;
        }
    }
    
    /**
     * このサービスで返すオブジェクトを取得する。<p>
     *
     * @param manager {@link ServiceManager}の名前
     * @param name {@link Service}の名前
     * @return 引数に該当するオブジェクト
     */
    protected Object findObject(String manager, String name){
        try{
            return ServiceManagerFactory.getService(manager, name);
        }catch(ServiceNotFoundException e){
            return null;
        }
    }
}