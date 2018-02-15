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
package jp.ossc.nimbus.service.context;

import java.util.*;
import jp.ossc.nimbus.core.*;
//
/**
 * スレッドコンテキストサービス。<p>
 * スレッド毎のコンテキストでキー、値を管理できる。<br>
 * また、子スレッドからの値の参照も可能である。<br>
 * 以下に、サービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="Context"
 *                  code="jp.ossc.nimbus.service.context.ThreadContextService"&gt;
 *             &lt;attribute name="HOME_PATH"&gt;/home&lt;/attribute&gt;
 *             &lt;attribute name="DOMAIN"&gt;nimbus.ossc.jp&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author H.Nakano
 */
public class ThreadContextService extends ServiceBase
 implements Context, ThreadContextServiceMBean{
    
    private static final long serialVersionUID = -7304455455493489289L;
    
    /**
     * コンテキスト格納用スレッドローカル。<p>
     */
    protected ThreadLocal threadLocal;
    
    /**
     * デフォルト値格納用のマップ。<p>
     */
    protected Map defaultMap;
    
    /**
     * サービスの生成処理を行う。<p>
     * インスタンス変数の初期化を行う。<br>
     * 
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        defaultMap = new HashMap();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     * 
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(threadLocal == null){
            clearAllThreadContext();
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     * 
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        clearAllThreadContext();
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     * インスタンス変数の破棄を行う。<br>
     * 
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        defaultMap = null;
    }
    
    // ContextのJavaDoc
    public void clear(){
        Map map = (Map)threadLocal.get();
        init(map);
    }
    
    private void init(Map map){
        if(map != null){
            map.clear();
            map.putAll(defaultMap);
            final Iterator entries = map.entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                final Object val = entry.getValue();
                if(val == null){
                    continue;
                }else if(val instanceof ServiceName){
                    final ServiceName name = (ServiceName)val;
                    Object service = null;
                    try{
                        service = ServiceManagerFactory.getServiceObject(name);
                    }catch(ServiceNotFoundException e){
                    }
                    entry.setValue(service);
                }else if(val instanceof ServiceName[]){
                    final ServiceName[] names = (ServiceName[])val;
                    final Object[] services = new Object[names.length];
                    for(int i = 0; i < names.length; i++){
                        try{
                            services[i] = ServiceManagerFactory
                                .getServiceObject(names[i]);
                        }catch(ServiceNotFoundException e){
                        }
                    }
                    entry.setValue(services);
                }
            }
        }
    }
    
    // ContextのJavaDoc
    public int size(){
        Map map = (Map)threadLocal.get();
        if(map != null){
            return map.size();
        }else{
            return 0;
        }
    }
    
    // ContextのJavaDoc
    public boolean isEmpty(){
        Map map = (Map)threadLocal.get();
        if(map != null){
            return map.isEmpty();
        }else{
            return false;
        }
    }
    
    // ContextのJavaDoc
    public boolean containsKey(Object key){
        Map map = (Map)threadLocal.get();
        if(map != null){
            return map.containsKey(key);
        }else{
            return false;
        }
    }
    
    // ContextのJavaDoc
    public boolean containsValue(Object value){
        Map map = (Map)threadLocal.get();
        if(map != null){
            return map.containsValue(value);
        }else{
            return false;
        }
    }
    
    // ContextのJavaDoc
    public Set entrySet(){
        Map map = (Map)threadLocal.get();
        if(map != null){
            return map.entrySet();
        }else{
            return new HashSet();
        }
    }
    
    // ContextのJavaDoc
    public Object get(Object key){
        Map map = (Map)threadLocal.get();
        return map.get(key);
    }
    
    // ContextのJavaDoc
    public Object put(Object key, Object value){
        if(threadLocal == null){
            return defaultMap.put(key, value);
        }
        Map map = (Map)threadLocal.get();
        return map.put(key,value);
    }
    
    // ContextのJavaDoc
    public Object remove(Object key){
        Map map = (Map)threadLocal.get();
        if(map != null){
            return map.remove(key);
        }else{
            return null;
        }
    }
    
    // ContextのJavaDoc
    public void putAll(Map t){
        Map map = (Map)threadLocal.get();
        map.putAll(t);
    }
    
    // ContextのJavaDoc
    public Set keySet(){
        Map map = (Map)threadLocal.get();
        return map.keySet();
    }
    
    // ContextのJavaDoc
    public Collection values(){
        Map map = (Map)threadLocal.get();
        return map.values();
    }
    
    // ContextのJavaDoc
    public Map all(){
        Map map = (Map)threadLocal.get();
        return new HashMap(map);
    }
    
    public Map getDefaultMap(){
        return defaultMap;
    }
    
    // ThreadContextServiceMBeanのJavaDoc
    public void clearAllThreadContext(){
        threadLocal = new ThreadLocal(){
            protected synchronized Object initialValue(){
                final Map map = Collections.synchronizedMap(new HashMap());
                init(map);
                return map;
            }
        };
    }
    
    /**
     * サポートしない。<p>
     *
     * @exception UnsupportedOperationException 必ずthrowする
     */
    public void load() throws Exception{
        throw new UnsupportedOperationException();
    }
    
    /**
     * サポートしない。<p>
     *
     * @exception UnsupportedOperationException 必ずthrowする
     */
    public void loadKey() throws Exception{
        throw new UnsupportedOperationException();
    }
    
    /**
     * サポートしない。<p>
     *
     * @param key キー
     * @exception UnsupportedOperationException 必ずthrowする
     */
    public void load(Object key) throws Exception{
        throw new UnsupportedOperationException();
    }
    
    /**
     * サポートしない。<p>
     *
     * @exception UnsupportedOperationException 必ずthrowする
     */
    public void save() throws Exception{
        throw new UnsupportedOperationException();
    }
    
    /**
     * サポートしない。<p>
     *
     * @param key キー
     * @exception UnsupportedOperationException 必ずthrowする
     */
    public void save(Object key) throws Exception{
        throw new UnsupportedOperationException();
    }
}
