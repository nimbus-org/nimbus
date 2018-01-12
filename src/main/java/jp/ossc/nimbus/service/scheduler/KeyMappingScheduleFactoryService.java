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
package jp.ossc.nimbus.service.scheduler;

import java.util.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.ServiceNameEditor;

/**
 * キーマッピングスケジュールファクトリ。<p>
 *
 * @author M.Takata
 */
public class KeyMappingScheduleFactoryService extends ServiceBase
 implements KeyMappingScheduleFactoryServiceMBean, ScheduleFactory{
    
    private static final long serialVersionUID = -2802344763761233335L;
    
    protected String[] keyAndScheduleFactoryServiceName;
    protected Map attrKeyAndScheduleFactory;
    protected Map keyAndScheduleFactory;
    protected ServiceName defaultScheduleFactoryServiceName;
    protected ScheduleFactory defaultScheduleFactory;
    
    // KeyMappingScheduleFactoryServiceMBeanのJavaDoc
    public void setKeyAndScheduleFactoryServiceName(String[] mapping){
        keyAndScheduleFactoryServiceName = mapping;
    }
    // KeyMappingScheduleFactoryServiceMBeanのJavaDoc
    public String[] getKeyAndScheduleFactoryServiceName(){
        return keyAndScheduleFactoryServiceName;
    }
    
    // KeyMappingScheduleFactoryServiceMBeanのJavaDoc
    public void setDefaultScheduleFactoryServiceName(ServiceName name){
        defaultScheduleFactoryServiceName = name;
    }
    // KeyMappingScheduleFactoryServiceMBeanのJavaDoc
    public ServiceName getDefaultScheduleFactoryServiceName(){
        return defaultScheduleFactoryServiceName;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        keyAndScheduleFactory = new HashMap();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(keyAndScheduleFactoryServiceName != null){
            ServiceNameEditor editor = new ServiceNameEditor();
            editor.setServiceManagerName(getServiceManagerName());
            for(int i = 0; i < keyAndScheduleFactoryServiceName.length; i++){
                String tmp = keyAndScheduleFactoryServiceName[i];
                int index = tmp.indexOf('=');
                if(index == -1 || index == tmp.length() - 1){
                    throw new IllegalArgumentException(
                        "keyAndScheduleFactoryServiceName is \"key=ScheduleFactoryServiceName\"." + tmp
                    );
                }
                final String key = tmp.substring(0, index);
                final String nameStr = tmp.substring(index + 1);
                editor.setAsText(nameStr);
                ServiceName name = (ServiceName)editor.getValue();
                ScheduleFactory factory = (ScheduleFactory)ServiceManagerFactory
                    .getServiceObject(name);
                keyAndScheduleFactory.put(createKey(key), factory);
            }
        }
        if(attrKeyAndScheduleFactory != null){
            final Iterator keys = attrKeyAndScheduleFactory.keySet().iterator();
            while(keys.hasNext()){
                final Object key = keys.next();
                ScheduleFactory factory
                     = (ScheduleFactory)attrKeyAndScheduleFactory.get(key);
                keyAndScheduleFactory.put(createKey(key), factory);
            }
        }
        if(defaultScheduleFactoryServiceName != null){
            defaultScheduleFactory = (ScheduleFactory)ServiceManagerFactory
                .getServiceObject(defaultScheduleFactoryServiceName);
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        keyAndScheduleFactory.clear();
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        keyAndScheduleFactory = null;
    }
    
    /**
     * 設定されたキーオブジェクトを、適切なキーオブジェクトに変換する。<p>
     * 変換せずにそのまま返す。<br>
     * サブクラスで必要に応じてオーバーライドすること。<br>
     *
     * @param key 設定されたキーオブジェクト
     * @return 適切なキーオブジェクト
     * @exception Exception 変換に失敗した場合
     */
    protected Object createKey(Object key) throws Exception{
        return key;
    }
    
    /**
     * キーと{@link ScheduleFactory}サービスのマッピングを設定する。<p>
     *
     * @param mapping キーとScheduleFactoryサービスのマッピング
     */
    public void setKeyAndScheduleFactory(Map mapping){
        attrKeyAndScheduleFactory = mapping;
    }
    
    /**
     * キーに該当するスケジュールを取得する。<p>
     *
     * @param key キー
     * @return スケジュール配列
     */
    public Schedule[] getSchedules(Object key){
        ScheduleFactory factory
             = (ScheduleFactory)keyAndScheduleFactory.get(key);
        if(factory == null && defaultScheduleFactory != null){
            factory = defaultScheduleFactory;
        }
        return factory == null ? new Schedule[0] : factory.getSchedules(key);
    }
}