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

/**
 * サーバ情報サービス。<p>
 * 以下に、サービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="ServerInfo"
 *                  code="jp.ossc.nimbus.service.context.ServerInfoService"&gt;
 *             &lt;attribute name="HOME_PATH"&gt;/home&lt;/attribute&gt;
 *             &lt;attribute name="DOMAIN"&gt;nimbus.ossc.jp&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class ServerInfoService extends DefaultContextService
 implements ServerInfo, ServerInfoServiceMBean, java.io.Serializable{
    
    private static final long serialVersionUID = -6514773824356484808L;
    
    private static final String[] CAN_NOT_MODIFY_KEYS = new String[]{
        JAVA_VERSION_KEY,
        JAVA_VENDOR_KEY,
        JAVA_VM_NAME_KEY,
        JAVA_VM_VERSION_KEY,
        JAVA_VM_VENDOR_KEY,
        OS_NAME_KEY,
        OS_VERSION_KEY,
        OS_ARCH_KEY,
        TOTAL_MEMORY_KEY,
        USED_MEMORY_KEY,
        FREE_MEMORY_KEY,
        MAX_MEMORY_KEY,
        AVAILABLE_PROCESSORS_KEY,
        HOST_NAME_KEY,
        HOST_ADDRESS_KEY,
        ACTIVE_THREAD_COUNT_KEY,
        ACTIVE_THREAD_GROUP_COUNT_KEY
    };
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        super.startService();
        
        context.put(JAVA_VERSION_KEY, System.getProperty("java.version"));
        context.put(JAVA_VENDOR_KEY, System.getProperty("java.vendor"));
        context.put(JAVA_VM_NAME_KEY, System.getProperty("java.vm.name"));
        context.put(JAVA_VM_VERSION_KEY, System.getProperty("java.vm.version"));
        context.put(JAVA_VM_VENDOR_KEY, System.getProperty("java.vm.vendor"));
        context.put(OS_NAME_KEY, System.getProperty("os.name"));
        context.put(OS_VERSION_KEY, System.getProperty("os.version"));
        context.put(OS_ARCH_KEY, System.getProperty("os.arch"));
        try{
            context.put(
                MAX_MEMORY_KEY,
                new Long(Runtime.getRuntime().maxMemory())
            );
        }catch(NoSuchMethodError e){
            context.put(MAX_MEMORY_KEY, new Long(-1));
        }
        try{
            context.put(
                AVAILABLE_PROCESSORS_KEY,
                new Integer(Runtime.getRuntime().availableProcessors())
            );
        }catch(NoSuchMethodError e){
            context.put(AVAILABLE_PROCESSORS_KEY, new Integer(-1));
        }
        try{
            context.put(
                HOST_NAME_KEY,
                java.net.InetAddress.getLocalHost().getHostName()
            );
        }catch(java.net.UnknownHostException e){
            context.put(HOST_NAME_KEY, "UNKOWN");
        }
        try{
            context.put(
                HOST_ADDRESS_KEY,
                java.net.InetAddress.getLocalHost().getHostAddress()
            );
        }catch(java.net.UnknownHostException e){
            context.put(HOST_ADDRESS_KEY, "UNKOWN");
        }
    }
    
    /**
     * 指定されたキーに関連付けられたコンテキスト情報を取得する。<p>
     * 但し、キーに{@link #TOTAL_MEMORY_KEY}、{@link #USED_MEMORY_KEY}、{@link #FREE_MEMORY_KEY}、{@link #ACTIVE_THREAD_COUNT_KEY}、{@link #ACTIVE_THREAD_GROUP_COUNT_KEY}のいずれかを指定した場合には、コンテキスト情報として静的に格納されている値ではなく、動的に値を取得する。<br>
     *
     * @param key キー
     * @return キーに関連付けられたコンテキスト情報。該当するコンテキスト情報がない場合は、null
     */
    public Object get(Object key){
        if(key != null){
            if(key.equals(TOTAL_MEMORY_KEY)){
                return new Long(getTotalMemory());
            }else if(key.equals(USED_MEMORY_KEY)){
                return new Long(getUsedMemory());
            }else if(key.equals(FREE_MEMORY_KEY)){
                return new Long(getFreeMemory());
            }else if(key.equals(ACTIVE_THREAD_COUNT_KEY)){
                return new Integer(getActiveThreadCount());
            }else if(key.equals(ACTIVE_THREAD_GROUP_COUNT_KEY)){
                return new Integer(getActiveThreadGroupCount());
            }
        }
        return super.get(key);
    }
    
    private boolean isModifiableKey(Object key){
        if(key != null){
            for(int i = 0; i < CAN_NOT_MODIFY_KEYS.length; i++){
                if(key.equals(CAN_NOT_MODIFY_KEYS[i])){
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * 指定されたコンテキスト情報を指定されたキー情報に関連付けて設定する。<p>
     * 但し、定数で予約されているキーを指定すると、例外をthrowする。<br>
     *
     * @param key キー
     * @param value コンテキスト情報
     * @return 指定されたキーに関連付けられていたコンテキスト情報。存在しない場合は、null
     * @exception IllegalArgumentException 定数で予約されているキーを指定した場合
     */
    public Object put(Object key, Object value) throws IllegalArgumentException{
        if(!isModifiableKey(key)){
            throw new IllegalArgumentException("Can not modify. " + key);
        }
        return super.put(key, value);
    }
    
    /**
     * コンテキスト情報のキー集合を取得する。<p>
     * 但し、このキー集合は、変更不可である。<br>
     *
     * @return コンテキスト情報のキー集合
     */
    public Set keySet(){
        final Set result = new HashSet(super.keySet());
        result.add(TOTAL_MEMORY_KEY);
        result.add(USED_MEMORY_KEY);
        result.add(FREE_MEMORY_KEY);
        result.add(ACTIVE_THREAD_COUNT_KEY);
        result.add(ACTIVE_THREAD_GROUP_COUNT_KEY);
        return Collections.unmodifiableSet(result);
    }
    
    /**
     * コンテキスト情報の集合を取得する。<p>
     * 但し、このコンテキスト情報の集合は、変更不可である。<br>
     *
     * @return コンテキスト情報の集合
     */
    public Collection values(){
        final Set result = new HashSet(super.values());
        result.add(get(TOTAL_MEMORY_KEY));
        result.add(get(USED_MEMORY_KEY));
        result.add(get(FREE_MEMORY_KEY));
        result.add(get(ACTIVE_THREAD_COUNT_KEY));
        result.add(get(ACTIVE_THREAD_GROUP_COUNT_KEY));
        return Collections.unmodifiableCollection(result);
    }
    
    /**
     * 指定されたキーに関連付けられたコンテキスト情報を削除する。<p>
     * 但し、定数で予約されているキーを指定すると、例外をthrowする。<br>
     *
     * @param key キー
     * @return 削除されたコンテキスト情報。削除するコンテキスト情報がない場合は、null
     * @exception IllegalArgumentException 定数で予約されているキーを指定した場合
     */
    public Object remove(Object key){
        if(!isModifiableKey(key)){
            throw new IllegalArgumentException("Can not modify. " + key);
        }
        return super.remove(key);
    }
    
    /**
     * 予約キー以外の全てのコンテキスト情報を削除する。<p>
     */
    public void clear(){
        final Iterator keys  = super.keySet().iterator();
        while(keys.hasNext()){
            final Object key = keys.next();
            if(isModifiableKey(key)){
                super.remove(key);
            }
        }
    }
    
    /**
     * 指定されたキーに関連付けられたコンテキスト情報が存在するか調べる。<p>
     *
     * @param key キー
     * @return 指定されたキーに関連付けられたコンテキスト情報が存在する場合true
     */
    public boolean containsKey(Object key){
        if(key != null){
            if(key.equals(TOTAL_MEMORY_KEY)){
                return true;
            }else if(key.equals(USED_MEMORY_KEY)){
                return true;
            }else if(key.equals(FREE_MEMORY_KEY)){
                return true;
            }else if(key.equals(ACTIVE_THREAD_COUNT_KEY)){
                return true;
            }else if(key.equals(ACTIVE_THREAD_GROUP_COUNT_KEY)){
                return true;
            }
        }
        return super.containsKey(key);
    }
    
    /**
     * 指定されたコンテキスト情報が存在するか調べる。<p>
     *
     * @param value コンテキスト情報
     * @return 指定されたコンテキスト情報が存在する場合true
     */
    public boolean containsValue(Object value){
        if(value != null){
            if(value instanceof Integer){
                int intValue = ((Integer)value).intValue();
                if(getActiveThreadCount() == intValue
                    || getActiveThreadGroupCount() == intValue
                ){
                    return true;
                }
            }else if(value instanceof Long){
                long longValue = ((Long)value).longValue();
                if(getTotalMemory() == longValue
                    || getUsedMemory() == longValue
                    || getFreeMemory() == longValue
                ){
                    return true;
                }
            }
        }
        return super.containsValue(value);
    }
    
    /**
     * コンテキスト情報のエントリ集合を取得する。<p>
     * 但し、予約キーのエントリは、変更不可である。また、集合に対する変更も不可である。<br>
     *
     * @return コンテキスト情報のエントリ集合
     */
    public Set entrySet(){
        final Set result = new HashSet();
        final Iterator entries = super.entrySet().iterator();
        while(entries.hasNext()){
            final Map.Entry entry = (Map.Entry)entries.next();
            if(isModifiableKey(entry.getKey())){
                result.add(entry);
            }else{
                result.add(new UnmodifiedEntry(entry));
            }
        }
        return Collections.unmodifiableSet(result);
    }
    
    /**
     * 保持しているコンテキスト情報の数を取得する。<p>
     *
     * @return 保持しているコンテキスト情報の数
     */
    public int size(){
        return super.size() + 5;
    }
    
    /**
     * 指定されたマップに含まれる全てのキーと値をコンテキスト情報として設定する。<p>
     * 但し、定数で予約されているキーが含まれている場合、例外をthrowする。<br>
     *
     * @param t コンテキスト情報として設定するマップ
     * @exception IllegalArgumentException 定数で予約されているキーが含まれている場合
     */
    public void putAll(Map t) throws IllegalArgumentException{
        final Iterator keys = t.keySet().iterator();
        while(keys.hasNext()){
            final Object key = keys.next();
            put(key, t.get(key));
        }
    }
    
    /**
     * 「名前(keyのtoString()) : 値(valueのtoString()) 改行」という形式でリスト出力する。<p>
     *
     * @return リスト文字列
     */
    public String list(){
        final StringBuilder buf = new StringBuilder();
        synchronized(context){
            final Object[] staticKeys = context.keySet().toArray();
            final Object[] variableKeys = new Object[]{
                TOTAL_MEMORY_KEY,
                USED_MEMORY_KEY,
                FREE_MEMORY_KEY,
                ACTIVE_THREAD_COUNT_KEY,
                ACTIVE_THREAD_GROUP_COUNT_KEY
            };
            final Object[] keys
                 = new Object[staticKeys.length + variableKeys.length];
            System.arraycopy(staticKeys, 0, keys, 0, staticKeys.length);
            System.arraycopy(
                variableKeys,
                0,
                keys,
                staticKeys.length,
                variableKeys.length
            );
            
            for(int i = 0; i < keys.length; i++){
                buf.append(keys[i]);
                buf.append(" : ");
                buf.append(get(keys[i]));
                buf.append('\n');
            }
        }
        return buf.toString();
    }
    
    // ServerInfoのJavaDoc
    public String getJavaVersion(){
        return (String)get(JAVA_VERSION_KEY);
    }
    
    // ServerInfoのJavaDoc
    public String getJavaVendor(){
        return (String)get(JAVA_VENDOR_KEY);
    }
    
    // ServerInfoのJavaDoc
    public String getJavaVMName(){
        return (String)get(JAVA_VM_NAME_KEY);
    }
    
    // ServerInfoのJavaDoc
    public String getJavaVMVersion(){
        return (String)get(JAVA_VM_VERSION_KEY);
    }
    
    // ServerInfoのJavaDoc
    public String getJavaVMVendor(){
        return (String)get(JAVA_VM_VENDOR_KEY);
    }
    
    // ServerInfoのJavaDoc
    public String getOSName(){
        return (String)get(OS_NAME_KEY);
    }
    
    // ServerInfoのJavaDoc
    public String getOSVersion(){
        return (String)get(OS_VERSION_KEY);
    }
    
    // ServerInfoのJavaDoc
    public String getOSArch(){
        return (String)get(OS_ARCH_KEY);
    }
    
    // ServerInfoのJavaDoc
    public long getTotalMemory(){
        return Runtime.getRuntime().totalMemory();
    }
    
    // ServerInfoのJavaDoc
    public long getUsedMemory(){
        return getTotalMemory() - getFreeMemory();
    }
    
    // ServerInfoのJavaDoc
    public long getFreeMemory(){
        return Runtime.getRuntime().freeMemory();
    }
    
    // ServerInfoのJavaDoc
    public long getMaxMemory(){
        final Long maxMemory = (Long)get(MAX_MEMORY_KEY);
        if(maxMemory == null){
            return -1;
        }else{
            return maxMemory.longValue();
        }
    }
    
    // ServerInfoのJavaDoc
    public int getAvailableProcessors(){
        final Integer availableProcessors
             = (Integer)get(AVAILABLE_PROCESSORS_KEY);
        if(availableProcessors == null){
            return -1;
        }else{
            return availableProcessors.intValue();
        }
    }
    
    // ServerInfoのJavaDoc
    public String getHostName(){
        return (String)get(HOST_NAME_KEY);
    }
    
    // ServerInfoのJavaDoc
    public String getHostAddress(){
        return (String)get(HOST_ADDRESS_KEY);
    }
    
    // ServerInfoServiceMBeanのJavaDoc
    public String listSystemProperties(){
        final Properties prop = System.getProperties();
        final String sep = System.getProperty("line.separator");
        final StringBuilder buf = new StringBuilder();
        final Object[] keys = prop.keySet().toArray();
        for(int i = 0; i < keys.length; i++){
            buf.append(keys[i]).append('=').append(prop.get(keys[i]));
            if(i !=  keys.length - 1){
                buf.append(sep);
            }
        }
        return buf.toString();
    }
    
    private ThreadGroup getRootThreadGroup(){
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        while(group.getParent() != null){
            group = group.getParent();
        }
        return group;
    }
    
    // ServerInfoのJavaDoc
    public int getActiveThreadCount(){
        return getRootThreadGroup().activeCount();
    }
    
    // ServerInfoのJavaDoc
    public int getActiveThreadGroupCount(){
        return getRootThreadGroup().activeGroupCount();
    }
    
    private class UnmodifiedEntry implements Map.Entry, java.io.Serializable{
        
        private static final long serialVersionUID = -6773950200246057903L;
        
        private Object key;
        public UnmodifiedEntry(Map.Entry entry){
            this.key = entry.getKey();
        }
        public UnmodifiedEntry(Object key){
            this.key = key;
        }
        public Object getKey(){
            return key;
        }
        public Object getValue(){
            return ServerInfoService.this.get(key);
        }
        public Object setValue(Object value){
            throw new IllegalArgumentException("Can not modify. " + key);
        }
        public boolean equals(Object o){
            if(o == null){
                return false;
            }
            if(o == this){
                return true;
            }
            if(!(o instanceof Map.Entry)){
                return false;
            }
            final Map.Entry entry = (Map.Entry)o;
            return (getKey() == null ? entry.getKey() == null
                     : getKey().equals(entry.getKey()))
                 && (getValue()==null ? entry.getValue() == null
                     : getValue().equals(entry.getValue()));
        }
        public int hashCode(){
            return (getKey() == null ? 0 : getKey().hashCode()) ^
                (getValue() == null ? 0 : getValue().hashCode());
        }
    }
}