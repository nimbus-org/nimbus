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

/**
 * デフォルトコンテキスト。<p>
 * 任意の定数値や変数値などを、キーに関連付けて格納し、提供する。<br>
 * 以下に、サービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="Context"
 *                  code="jp.ossc.nimbus.service.context.DefaultContextService"&gt;
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
public class DefaultContextService extends ServiceBase
 implements Context, DefaultContextServiceMBean, java.io.Serializable{
    
    private static final long serialVersionUID = 920050666611019516L;
    
    /**
     * コンテキスト情報を格納するマップ。<p>
     */
    protected Map context;
    
    protected ServiceName contextStoreServiceName;
    
    protected ContextStore contextStore;
    
    protected boolean isLoadOnStart;
    
    protected boolean isLoadKeyOnStart;
    
    protected boolean isSaveOnStop;
    
    protected boolean isClearBeforeSave = true;
    
    protected boolean isLoadedOnStart;
    
    public void setContextStoreServiceName(ServiceName name){
        contextStoreServiceName = name;
    }
    public ServiceName getContextStoreServiceName(){
        return contextStoreServiceName;
    }
    
    public void setLoadOnStart(boolean isLoad){
        isLoadOnStart = isLoad;
    }
    public boolean isLoadOnStart(){
        return isLoadOnStart;
    }
    
    public void setLoadKeyOnStart(boolean isLoad){
        isLoadKeyOnStart = isLoad;
    }
    public boolean isLoadKeyOnStart(){
        return isLoadKeyOnStart;
    }
    
    public void setSaveOnStop(boolean isSave){
        isSaveOnStop = isSave;
    }
    public boolean isSaveOnStop(){
        return isSaveOnStop;
    }
    
    public void setClearBeforeSave(boolean isClear){
        isClearBeforeSave = isClear;
    }
    public boolean isClearBeforeSave(){
        return isClearBeforeSave;
    }
    
    public void setContextStore(ContextStore store){
        contextStore = store;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     * インスタンス変数を初期化する。<br>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        context = createContext();
    }
    
    protected Map createContext(){
        return Collections.synchronizedMap(new HashMap());
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(contextStoreServiceName != null){
            contextStore = (ContextStore)ServiceManagerFactory.getServiceObject(contextStoreServiceName);
        }
        if(contextStore == null && (isLoadOnStart || isLoadKeyOnStart || isSaveOnStop)){
            throw new IllegalArgumentException("ContextStore is null.");
        }
        if(isLoadOnStart){
            loadOnStart();
        }else if(isLoadKeyOnStart){
            loadKeyOnStart();
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        if(isSaveOnStop){
            save();
        }
        context.clear();
        isLoadedOnStart = false;
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     * インスタンス変数を破棄する。<br>
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        context = null;
    }
    
    protected synchronized void loadOnStart() throws Exception{
        if(isLoadedOnStart){
            return;
        }
        load();
        isLoadedOnStart = true;
    }
    
    public synchronized void load() throws Exception{
        if(contextStore != null){
            contextStore.load(this);
        }
    }
    
    protected synchronized void loadKeyOnStart() throws Exception{
        if(isLoadedOnStart){
            return;
        }
        loadKey();
        isLoadedOnStart = true;
    }
    
    public synchronized void loadKey() throws Exception{
        if(contextStore != null){
            contextStore.loadKey(this);
        }
    }
    
    public void load(Object key) throws Exception{
        if(contextStore != null){
            contextStore.load(this, key);
        }
    }
    
    public synchronized void save() throws Exception{
        if(contextStore != null){
            if(isClearBeforeSave){
                contextStore.clear();
            }
            contextStore.save(this);
        }
    }
    
    public void save(Object key) throws Exception{
        if(contextStore != null){
            contextStore.save(this, key);
        }
    }
    
    // ContextのJavaDoc
    public Object get(Object key) {
        Object val = context.get(key);
        if(val == null){
            return null;
        }else if(val instanceof ServiceName){
            final ServiceName name = (ServiceName)val;
            try{
                val = ServiceManagerFactory.getServiceObject(name);
            }catch(ServiceNotFoundException e){
                val = null;
            }
        }
        return val;
    }
    
    // ContextのJavaDoc
    public Object put(Object key, Object value) {
        return context == null ? null : context.put(key,value);
    }
    
    // ContextのJavaDoc
    public Object put(String key, String value) {
        return put((Object)key, (Object)value);
    }
    
    // ContextのJavaDoc
    public Set keySet(){
        return context == null ? null : context.keySet();
    }
    
    // ContextのJavaDoc
    public Collection values() {
        return context == null ? null : context.values();
    }
    
    // ContextのJavaDoc
    public Object remove(Object key) {
        return context == null ? null : context.remove(key);
    }
    
    // ContextのJavaDoc
    public void clear() {
        if(context != null){
            context.clear() ;
        }
    }
    
    // ContextのJavaDoc
    public boolean isEmpty() {
        return context == null ? true : context.isEmpty();
    }
    
    // ContextのJavaDoc
    public boolean containsKey(Object key) {
        return context == null ? false : context.containsKey(key);
    }
    
    // ContextのJavaDoc
    public boolean containsValue(Object value) {
        return context == null ? false : context.containsValue(value);
    }
    
    // ContextのJavaDoc
    public Set entrySet(){
        return context == null ? new HashSet() : context.entrySet();
    }
    
    // ContextのJavaDoc
    public int size() {
        return context == null ? 0 : context.size();
    }
    
    // ContextのJavaDoc
    public void putAll(Map t) {
        if(context != null){
            context.putAll(t);
        }
    }
    
    /**
     * コンテキスト情報の複製を保持するハッシュテーブルを生成する。<p>
     *
     * @return コンテキスト情報の複製を保持するハッシュテーブル
     */
    public Hashtable cloneHash(){
        return context == null ? new Hashtable() : (Hashtable)new Hashtable(context);
    }
    
    // ContextのJavaDoc
    public Object get(String key) {
        return get((Object)key);
    }
    
    // ContextのJavaDoc
    public Object remove(String key){
        return remove((Object)key);
    }
    
    // ContextのJavaDoc
    public String list() {
        StringBuilder buf = new StringBuilder();
        if(context != null){
            synchronized (context) {
                for (Iterator ite = context.keySet().iterator(); ite.hasNext();) {
                    Object key = ite.next();
                    buf.append(key);
                    buf.append(" : ");
                    buf.append(context.get(key));
                    buf.append('\n');
                }
            }
        }
        return buf.toString();
    }
    
    public Map all(){
        return context == null ? new HashMap() : new HashMap(context);
    }
}
