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
package jp.ossc.nimbus.service.cache;

import java.util.*;
import java.io.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.io.Externalizer;

/**
 * ファイルキャッシュマップサービス。<p>
 * 以下に、キャッシュオブジェクトをJVMのテンポラリディレクトリに直列化してキャッシュするファイルキャッシュマップサービスのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="FileCacheMap"
 *                  code="jp.ossc.nimbus.service.cache.FileCacheMapService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class FileCacheMapService extends AbstractCacheMapService
 implements java.io.Serializable, FileCacheMapServiceMBean{
    
    private static final long serialVersionUID = 4620703085265406262L;
    
    // メッセージID定義
    private static final String FCM__ = "FCM__";
    private static final String FCM__0 = FCM__ + 0;
    private static final String FCM__00 = FCM__0 + 0;
    private static final String FCM__000 = FCM__00 + 0;
    private static final String FCM__0000 = FCM__000 + 0;
    private static final String FCM__00002 = FCM__0000 + 2;
    
    private static final String JVM_TMP_DIR = "java.io.tmpdir";
    
    private String outputDirectory;
    private File directory;
    private String prefix;
    private String suffix = DEFAULT_SUFFIX;
    private boolean isDeleteOnExitWithJVM = true;
    private boolean isLoadOnStart;
    private boolean isFileShared;
    private boolean isDeleteOnLoadError;
    
    private volatile boolean isLoading;
    
    private ServiceName externalizerServiceName;
    private Externalizer externalizer;
    
    // FileCacheMapServiceMBeanのJavaDoc
    public void setOutputDirectory(String path)
     throws IllegalArgumentException{
        if(path != null){
            final File dir = new File(path);
            if(dir.exists()){
                if(!dir.isDirectory()){
                    throw new IllegalArgumentException(
                        "Path is illegal : " + path
                    );
                }
            }else{
                if(!dir.mkdirs()){
                    throw new IllegalArgumentException(
                        "Path is illegal : " + path
                    );
                }
            }
            directory = dir;
            outputDirectory = path;
        }
    }
    
    // FileCacheMapServiceMBeanのJavaDoc
    public String getOutputDirectory(){
        return outputDirectory;
    }
    
    // FileCacheMapServiceMBeanのJavaDoc
    public void setFileShared(boolean isShared){
        isFileShared = isShared;
    }
    
    // FileCacheMapServiceMBeanのJavaDoc
    public boolean isFileShared(){
        return isFileShared;
    }
    
    // FileCacheMapServiceMBeanのJavaDoc
    public void setOutputPrefix(String prefix){
        this.prefix = prefix;
    }
    
    // FileCacheMapServiceMBeanのJavaDoc
    public String getOutputPrefix(){
        return prefix;
    }
    
    // FileCacheMapServiceMBeanのJavaDoc
    public void setOutputSuffix(String suffix){
        this.suffix = suffix;
    }
    
    // FileCacheMapServiceMBeanのJavaDoc
    public String getOutputSuffix(){
        return suffix;
    }
    
    // FileCacheMapServiceMBeanのJavaDoc
    public void setLoadOnStart(boolean isLoad){
        isLoadOnStart = isLoad;
    }
    
    // FileCacheMapServiceMBeanのJavaDoc
    public boolean isLoadOnStart(){
        return isLoadOnStart;
    }
    
    // FileCacheMapServiceMBeanのJavaDoc
    public void setDeleteOnLoadError(boolean isDelete){
        isDeleteOnLoadError = isDelete;
    }
    
    // FileCacheMapServiceMBeanのJavaDoc
    public boolean isDeleteOnLoadError(){
        return isDeleteOnLoadError;
    }
    
    // FileCacheMapServiceMBeanのJavaDoc
    public void setExternalizerServiceName(ServiceName name){
        externalizerServiceName = name;
    }
    
    // FileCacheMapServiceMBeanのJavaDoc
    public ServiceName getExternalizerServiceName(){
        return externalizerServiceName;
    }
    
    public void setExternalizer(Externalizer ext){
        externalizer = ext;
    }
    
    public Externalizer getExternalizer(){
        return externalizer;
    }
    
    /**
     * 指定されたキーに関連付けられたキャッシュオブジェクトを保存するファイルを作成する。<p>
     *
     * @param key キャッシュのキー
     * @return キャッシュオブジェクトを保存するファイル
     * @exception IOException ファイルが作成できなかった場合
     */
    protected File createFile(Object key) throws IOException{
        File file = null;
        String prefix = this.prefix;
        if(prefix == null){
            if(key != null){
                synchronized(key){
                    prefix = key.toString();
                }
            }else{
                prefix = "null";
            }
        }
        if(directory != null){
            file = File.createTempFile(
                createPrefix(prefix),
                suffix,
                directory
            );
        }else{
            file = File.createTempFile(
                createPrefix(prefix),
                suffix
            );
        }
        if(isDeleteOnExitWithJVM()){
            file.deleteOnExit();
        }
        return file;
    }
    
    private String createPrefix(String prefix){
        if(prefix.length() > 2){
            return prefix;
        }else{
            final StringBuilder buf = new StringBuilder(prefix);
            for(int i = 0, max = 3 - prefix.length(); i < max; i++){
                buf.append('_');
            }
            return buf.toString();
        }
    }
    
    // FileCacheMapServiceMBeanのJavaDoc
    public void setDeleteOnExitWithJVM(boolean isDeleteOnExit){
        isDeleteOnExitWithJVM = isDeleteOnExit;
    }
    
    // FileCacheMapServiceMBeanのJavaDoc
    public boolean isDeleteOnExitWithJVM(){
        return isDeleteOnExitWithJVM;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(externalizerServiceName != null){
            externalizer = (Externalizer)ServiceManagerFactory.getServiceObject(externalizerServiceName);
        }
        if(isLoadOnStart()){
            load();
        }
    }
    
    /**
     * キャッシュファイルの読み込みを行う。<p>
     *
     * @exception Exception キャッシュファイルの読み込みに失敗した場合
     */
    protected void load() throws Exception{
        if(isLoading || references == null){
            return;
        }
        try{
            isLoading = true;
            
            synchronized(references){
                final Object[] refs = references.values().toArray();
                for(int i = 0; i < refs.length; i++){
                    final FileKeyCachedReference ref
                         = (FileKeyCachedReference)refs[i];
                    if(!ref.getFile(this).exists()){
                        remove(ref.getKey());
                    }
                }
            }
            
            File dir = directory;
            if(dir == null){
                final String tmpFileStr = System.getProperty(JVM_TMP_DIR);
                if(tmpFileStr != null){
                    final File tmpFile = new File(tmpFileStr);
                    if(tmpFile.exists() && tmpFile.isDirectory()){
                        dir = tmpFile;
                    }
                }
            }
            if(dir != null){
                final File[] list = dir.listFiles(
                    new FilenameFilter(){
                        private final String pre = prefix != null
                             ? createPrefix(prefix) : null;
                        public boolean accept(File dir, String name){
                            if(pre == null){
                                return name.endsWith(suffix);
                            }else{
                                return name.startsWith(pre)
                                     && name.endsWith(suffix);
                            }
                        }
                    }
                );
                for(int i = 0; i < list.length; i++){
                    if(!containsFile(list[i])){
                        if(list[i] != null && list[i].exists()){
                            try{
                                final FileKeyCachedReference ref
                                     = new FileKeyCachedReference(list[i], externalizer);
                                put(ref.getKey(), ref);
                            }catch(IOException e){
                                if(isDeleteOnLoadError){
                                    list[i].delete();
                                }
                            }catch(ClassNotFoundException e){
                                if(isDeleteOnLoadError){
                                    list[i].delete();
                                }
                            }
                        }
                    }
                }
            }
        }finally{
            isLoading = false;
        }
    }
    
    /**
     * このキャッシュに指定されたキャッシュファイルのキャッシュ参照が含まれているか調べる。<p>
     *
     * @param file キャッシュファイル
     * @return 含まれている場合true
     */
    protected boolean containsFile(File file){
        if(references == null || file == null){
            return false;
        }
        boolean result = false;
        synchronized(references){
            final Iterator refs = references.values().iterator();
            while(refs.hasNext()){
                final FileKeyCachedReference ref
                    = (FileKeyCachedReference)refs.next();
                if(file.equals(ref.getFile(this))){
                    return true;
                }
            }
        }
        return result;
    }
    
    /**
     * ファイルキャッシュ参照を生成する。<p>
     * ファイルキャッシュ参照の生成に失敗した場合は、nullを返す。
     *
     * @param key キャッシュキー
     * @param obj キャッシュオブジェクト
     * @return ファイルキャッシュ参照
     */
    protected KeyCachedReference createKeyCachedReference(
        Object key,
        Object obj
    ){
        File file = null;
        try{
            file = createFile(key);
            FileKeyCachedReference ref = new FileKeyCachedReference(
                key,
                file,
                obj,
                externalizer
            );
            return ref;
        }catch(IOException e){
            if(file != null){
                file.delete();
            }
            return null;
        }
    }
    
    // CacheMapのJavaDoc
    public int size(){
        if(references == null){
            return 0;
        }
        if(isFileShared()){
            try{
                load();
            }catch(Exception e){
                getLogger().write(FCM__00002, e);
            }
        }
        return super.size();
    }
    
    // CacheMapのJavaDoc
    public boolean isEmpty(){
        if(references == null){
            return true;
        }
        if(isFileShared()){
            try{
                load();
            }catch(Exception e){
                getLogger().write(FCM__00002, e);
            }
        }
        return super.isEmpty();
    }
    
    // CacheMapのJavaDoc
    public boolean containsKey(Object key){
        if(references == null){
            return false;
        }
        if(isFileShared()){
            try{
                load();
            }catch(Exception e){
                getLogger().write(FCM__00002, e);
            }
        }
        return super.containsKey(key);
    }
    
    // CacheMapのJavaDoc
    public boolean containsValue(Object value){
        if(references == null){
            return false;
        }
        if(isFileShared()){
            try{
                load();
            }catch(Exception e){
                getLogger().write(FCM__00002, e);
            }
        }
        return super.containsValue(value);
    }
    
    // CacheMapのJavaDoc
    public Object get(Object key){
        if(references == null){
            return null;
        }
        if(isFileShared()){
            try{
                load();
            }catch(Exception e){
                getLogger().write(FCM__00002, e);
            }
        }
        return super.get(key);
    }
    
    /**
     * 指定したキーのキャッシュ参照を追加する。<p>
     *
     * @param key キャッシュのキー
     * @param ref キャッシュ参照
     */
    protected void put(Object key, KeyCachedReference ref){
        if(references == null){
            return;
        }
        if(isFileShared()){
            try{
                load();
            }catch(Exception e){
                getLogger().write(FCM__00002, e);
            }
        }
        super.put(key, ref);
    }
    
    // CacheMapのJavaDoc
    public Object remove(Object key){
        if(references == null){
            return null;
        }
        if(isFileShared()){
            try{
                load();
            }catch(Exception e){
                getLogger().write(FCM__00002, e);
            }
        }
        return super.remove(key);
    }
    
    // CacheMapのJavaDoc
    public void putAll(Map map){
        if(references == null || map == null || map.size() == 0){
            return;
        }
        if(isFileShared()){
            try{
                load();
            }catch(Exception e){
                getLogger().write(FCM__00002, e);
            }
        }
        super.putAll(map);
    }
    
    // CacheMapのJavaDoc
    public void clear(){
        if(references == null || references.size() == 0){
            return;
        }
        if(isFileShared()){
            try{
                load();
            }catch(Exception e){
                getLogger().write(FCM__00002, e);
            }
        }
        super.clear();
    }
    
    // CacheMapのJavaDoc
    public Set keySet(){
        if(isFileShared()){
            try{
                load();
            }catch(Exception e){
                getLogger().write(FCM__00002, e);
            }
        }
        return super.keySet();
    }
    
    // CacheMapのJavaDoc
    public Collection values(){
        if(isFileShared()){
            try{
                load();
            }catch(Exception e){
                getLogger().write(FCM__00002, e);
            }
        }
        return super.values();
    }
    
    // CacheMapのJavaDoc
    public Set entrySet(){
        if(isFileShared()){
            try{
                load();
            }catch(Exception e){
                getLogger().write(FCM__00002, e);
            }
        }
        return super.entrySet();
    }
}
