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
 * グループコンテキスト。<p>
 * 複数のコンテキストをグルーピングして、１つのコンテキストとして、コンテキスト情報が取得できるようにする。<br>
 * コンテキスト情報の追加はサポートしない。<br>
 * 以下に、サービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="Context"
 *                  code="jp.ossc.nimbus.service.context.GroupContextService"&gt;
 *             &lt;attribute name="ContextServiceNames"&gt;
 *                 #Context1
 *                 #Context2
 *             &lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="Context1"
 *                  code="jp.ossc.nimbus.service.context.DefaultContextService"&gt;
 *             &lt;attribute name="HOME_PATH"&gt;/home&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="Context2"
 *                  code="jp.ossc.nimbus.service.context.DefaultContextService"&gt;
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
public class GroupContextService extends ServiceBase
 implements Context, GroupContextServiceMBean, java.io.Serializable{
    
    private static final long serialVersionUID = -5282880238704983055L;
    
    /**
     * グルーピングするコンテキストサービス名配列。<p>
     */
    protected ServiceName[] contextServiceNames;
    
    protected Context[] contexts;
    
    // GroupContextServiceMBeanのJavaDoc
    public void setContextServiceNames(ServiceName[] names){
        contextServiceNames = names;
    }
    
    // GroupContextServiceMBeanのJavaDoc
    public ServiceName[] getContextServiceNames(){
        return contextServiceNames;
    }
    
    /**
     * Contextを設定する。
     */
    public void setContexts(Context[] contexts) {
        this.contexts = contexts;
    }

    // ContextのJavaDoc
    public Object get(Object key){
        Context context = null;
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    Context ctx = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    if(ctx.containsKey(key)){
                        context = ctx;
                        break;
                    }
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i=0; i<contexts.length; i++) {
                Context ctx = contexts[i];
                if(ctx.containsKey(key)){
                    context = ctx;
                    break;
                }
            }
        }
        return context == null ? null : context.get(key);
    }
    
    /**
     * サポートしない。<br>
     *
     * @param key キー
     * @param value コンテキスト情報
     * @return 指定されたキーに関連付けられていたコンテキスト情報。存在しない場合は、null
     * @exception UnsupportedOperationException
     */
    public Object put(Object key, Object value){
        throw new UnsupportedOperationException();
    }
    
    /**
     * コンテキスト情報のキー集合を取得する。<p>
     * 但し、このキー集合は、変更不可である。<br>
     *
     * @return コンテキスト情報のキー集合
     */
    public Set keySet(){
        final Set result = new HashSet();
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    result.addAll(context.keySet());
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i =0; i<contexts.length; i++){
                final Context context = contexts[i];
                result.addAll(context.keySet());
            }
        }
        return Collections.unmodifiableSet(result);
    }
    
    /**
     * コンテキスト情報の集合を取得する。<p>
     * 但し、このコンテキスト情報の集合は、変更不可である。<br>
     *
     * @return コンテキスト情報の集合
     */
    public Collection values() {
        final Map result = new HashMap();
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    result.putAll(context);
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i =0; i<contexts.length; i++){
                final Context context = contexts[i];
                result.putAll(context);
            }
        }
        return Collections.unmodifiableCollection(result.values());
    }
    
    /**
     * 全てのコンテキスト情報を取得する。<p>
     * 但し、このコンテキスト情報は、変更不可である。<br>
     *
     * @return コンテキスト情報
     */
    public Map all(){
        final Map result = new HashMap();
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    result.putAll(context);
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i =0; i<contexts.length; i++){
                final Context context = contexts[i];
                result.putAll(context);
            }
        }
        return Collections.unmodifiableMap(result);
    }
    
    /**
     * 指定されたキーに関連付けられたコンテキスト情報を削除する。<p>
     * グルーピングする全てのコンテキストから削除し、最後に削除したオブジェクトを返す。<br>
     *
     * @param key キー
     * @return 削除されたコンテキスト情報。削除するコンテキスト情報がない場合は、null
     */
    public Object remove(Object key) {
        Object result = null;
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    if(context.containsKey(key)){
                        result = context.remove(key);
                    }
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i =0; i<contexts.length; i++){
                final Context context = contexts[i];
                if(context.containsKey(key)){
                    result = context.remove(key);
                }
            }
        }
        return result;
    }
    
    /**
     * グルーピングする全てのコンテキスト情報を削除する。<p>
     */
    public void clear(){
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    context.clear();
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i =0; i<contexts.length; i++){
                final Context context = contexts[i];
                context.clear();
            }
        }
    }
    
    // ContextのJavaDoc
    public boolean isEmpty(){
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    if(!context.isEmpty()){
                        return false;
                    }
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i =0; i<contexts.length; i++){
                final Context context = contexts[i];
                if(!context.isEmpty()){
                    return false;
                }
            }
        }
        return true;
    }
    
    // ContextのJavaDoc
    public boolean containsKey(Object key){
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    if(context.containsKey(key)){
                        return true;
                    }
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i =0; i<contexts.length; i++){
                final Context context = contexts[i];
                if(context.containsKey(key)){
                    return true;
                }
            }
        }
        return false;
    }
    
    // ContextのJavaDoc
    public boolean containsValue(Object value) {
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    if(context.containsValue(value)){
                        return true;
                    }
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i =0; i<contexts.length; i++){
                final Context context = contexts[i];
                if(context.containsValue(value)){
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * コンテキスト情報のエントリ集合を取得する。<p>
     * 但し、エントリ集合に対する変更は不可である。<br>
     *
     * @return コンテキスト情報のエントリ集合
     */
    public Set entrySet(){
        final Map result = new HashMap();
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    final Object[] entries = context.entrySet().toArray();
                    for(int j = 0; j < entries.length; j++){
                        result.put(
                            ((Map.Entry)entries[j]).getKey(),
                            entries[j]
                        );
                    }
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i =0; i<contexts.length; i++){
                final Context context = contexts[i];
                final Object[] entries = context.entrySet().toArray();
                for(int j = 0; j < entries.length; j++){
                    result.put(
                        ((Map.Entry)entries[j]).getKey(),
                        entries[j]
                    );
                }
            }
        }
        return Collections.unmodifiableSet(new HashSet(result.values()));
    }
    
    // ContextのJavaDoc
    public int size(){
        int size = 0;
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    size += context.size();
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i = 0; i < contexts.length; i++){
                final Context context = contexts[i];
                size += context.size();
            }
        }
        return size;
    }
    
    /**
     * サポートしない。<p>
     *
     * @param t コンテキスト情報として設定するマップ
     * @exception UnsupportedOperationException
     */
    public void putAll(Map t){
        throw new UnsupportedOperationException();
    }
    
    // ContextのJavaDoc
    public Object get(String key){
        return get((Object)key);
    }
    
    // ContextのJavaDoc
    public Object remove(String key) {
        return remove((Object)key);
    }
    
    // ContextのJavaDoc
    public String list(){
        final StringBuilder buf = new StringBuilder();
        synchronized(this){
            for(Iterator ite = keySet().iterator(); ite.hasNext();){
                Object key = ite.next();
                buf.append(key);
                buf.append(" : ");
                buf.append(get(key));
                buf.append('\n');
            }
        }
        return buf.toString();
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
