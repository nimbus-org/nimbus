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

import java.io.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.io.Externalizer;

/**
 * キー付きファイルキャッシュ参照。<p>
 *
 * @author M.Takata
 */
public class FileKeyCachedReference extends DefaultKeyCachedReference
 implements Serializable{
    
    private static final long serialVersionUID = 1386986712271917526L;
    
    private transient Externalizer externalizer;
    
    /**
     * 既存のキャッシュファイルからキー付きファイルキャッシュ参照を生成する。<p>
     * 指定されたキャッシュファイルを読み込んでキャッシュキーを取得する。<br>
     * 
     * @param file キャッシュファイル
     * @exception IOException キャッシュファイルの復元に失敗した場合
     * @exception ClassNotFoundException キャッシュファイルの復元結果のクラスが存在しない場合
     */
    public FileKeyCachedReference(File file)
     throws IOException, ClassNotFoundException{
        super(null, file);
        cacheKey = ((MapEntry)deserializeObject(file)).getKey();
    }
    
    /**
     * 既存のキャッシュファイルからキー付きファイルキャッシュ参照を生成する。<p>
     * 指定されたキャッシュファイルを読み込んでキャッシュキーを取得する。<br>
     * 
     * @param file キャッシュファイル
     * @param ext 直列化を行うExternalizer
     * @exception IOException キャッシュファイルの復元に失敗した場合
     * @exception ClassNotFoundException キャッシュファイルの復元結果のクラスが存在しない場合
     */
    public FileKeyCachedReference(File file, Externalizer ext)
     throws IOException, ClassNotFoundException{
        super(null, file);
        externalizer = ext;
        cacheKey = ((MapEntry)deserializeObject(file)).getKey();
    }
    
    /**
     * 指定されたキャッシュキー、キャッシュファイル、キャッシュオブジェクトを保持する新しいキー付きファイルキャッシュ参照を生成する。<p>
     * キャッシュオブジェクトは、直列化してキャッシュファイルに保存する。<br>
     * 
     * @param key キャッシュキー
     * @param file キャッシュファイル
     * @param obj キャッシュオブジェクト
     * @exception IOException キャッシュオブジェクトの直列化に失敗した場合
     */
    public FileKeyCachedReference(Object key, File file, Object obj)
     throws IOException{
        super(key, file);
        serializeObject(file, new MapEntry(key, obj));
    }
    
    /**
     * 指定されたキャッシュキー、キャッシュファイル、キャッシュオブジェクトを保持する新しいキー付きファイルキャッシュ参照を生成する。<p>
     * キャッシュオブジェクトは、直列化してキャッシュファイルに保存する。<br>
     * 
     * @param key キャッシュキー
     * @param file キャッシュファイル
     * @param obj キャッシュオブジェクト
     * @exception IOException キャッシュオブジェクトの直列化に失敗した場合
     */
    public FileKeyCachedReference(Object key, File file, Object obj, Externalizer ext)
     throws IOException{
        super(key, file);
        externalizer = ext;
        serializeObject(file, new MapEntry(key, obj));
    }
    
    /**
     * オブジェクトを指定ファイルにシリアライズして格納する。<p>
     * 
     * @param file キャッシュファイル
     * @param obj キャッシュオブジェクト
     * @exception IOException キャッシュオブジェクトの直列化に失敗した場合
     */
    protected void serializeObject(File file, Object obj)
     throws IOException{
        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
        try{
            if(externalizer == null){
                ObjectOutputStream oos = new ObjectOutputStream(os);
                if(obj != null){
                    synchronized(obj){
                        oos.writeObject(obj);
                    }
                }else{
                    oos.writeObject(obj);
                }
                oos.flush();
            }else{
                if(obj != null){
                    synchronized(obj){
                        externalizer.writeExternal(obj, os);
                    }
                }else{
                    externalizer.writeExternal(obj, os);
                }
            }
        }finally{
            if(os!=null){
                try{
                    os.close();
                }catch(IOException e){}
            }
        }
    }
    
    /**
     * 指定されたファイルからオブジェクトを復元する。<p>
     * 
     * @param file キャッシュファイル
     * @return 復元したキャッシュオブジェクト
     * @exception IOException キャッシュファイルの復元に失敗した場合
     * @exception ClassNotFoundException キャッシュファイルの復元結果のクラスが 存在しない場合
     */
    protected Object deserializeObject(File file)
     throws IOException, ClassNotFoundException{
        Object entry = null;
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        try{
            if(externalizer == null){
                ObjectInputStream ois = new ObjectInputStream(is);
                entry = ois.readObject();
            }else{
                entry = externalizer.readExternal(is);
            }
        }finally{
            if(is!=null){
                try{
                    is.close() ;
                }catch(IOException ex){
                }
            }
        }
        return entry;
    }
    
    /**
     * キャッシュファイルを取得する。<p>
     *
     * @param source 取得元の参照
     * @return キャッシュファイル
     */
    public File getFile(Object source){
        return (File)super.get(source, false);
    }
    
    /**
     * キャッシュされたオブジェクトをキャッシュファイルから復元して取得する。<p>
     * 第二引数がtrueの場合は、{@link #addCacheAccessListener(CacheAccessListener)}で登録された{@link CacheAccessListener}に通知する。但し、第一引数で渡された呼び出し元オブジェクトが通知先のCacheAccessListenerのインスタンスと等しい場合は、通知しない。<br>
     * 自身が保持するキャッシュオブジェクトがnullでない場合は、それを返す。nullの場合は、{@link #addLinkedReference(LinkedReference)}で登録された{@link LinkedReference}から取得を試みる。<br>
     *
     * @param source キャッシュを取得するこのメソッドの呼び出し元オブジェクト
     * @param notify キャッシュアクセスリスナに通知する場合はtrue
     * @return キャッシュオブジェクト
     */
    public Object get(Object source, boolean notify) throws IllegalCachedReferenceException{
        final Object obj = super.get(source, notify);
        if(obj instanceof File){
            File file = (File)obj;
            if(file.exists() && file.canRead()){
                MapEntry entry = null;
                try{
                    entry = ((MapEntry)deserializeObject(file));
                }catch(IOException e){
                    throw new IllegalCachedReferenceException(e);
                }catch(ClassNotFoundException e){
                    throw new IllegalCachedReferenceException(e);
                }
                return entry.getValue();
            }else{
                return null;
            }
        }else{
            return obj;
        }
    }
    
    /**
     * キャッシュオブジェクトを直列化してファイルに保存する。<p>
     * {@link #addCacheChangeListener(CacheChangeListener)}で登録された{@link CacheChangeListener}に通知する。但し、第一引数で渡された呼び出し元オブジェクトが通知先のCacheChangeListenerのインスタンスと等しい場合は、通知しない。<br>
     *
     * @param source キャッシュオブジェクトを変更するこのメソッドの呼び出し元オブジェクト
     * @param obj 設定するキャッシュオブジェクト
     * @exception IllegalCachedReferenceException キャッシュ参照の状態が不正な為キャッシュオブジェクトの設定に失敗した場合
     */
    public void set(Object source, Object obj)
     throws IllegalCachedReferenceException{
        notifyChange(source, obj);
        if(obj != null){
            try{
                serializeObject((File)cacheObj, obj);
            }catch(IOException e){
                throw new IllegalCachedReferenceException(e);
            }
        }else{
            ((File)cacheObj).delete();
        }
    }
    
    /**
     * キャッシュオブジェクトを保存したキャッシュファイルを削除する。<p>
     * {@link #addCacheRemoveListener(CacheRemoveListener)}で登録された{@link CacheRemoveListener}に通知する。但し、第一引数で渡された呼び出し元オブジェクトが通知先のCacheChangeListenerのインスタンスと等しい場合は、通知しない。<br>
     *
     * @param source キャッシュオブジェクトを削除するこのメソッドの呼び出し元オブジェクト
     */
    public void remove(Object source){
        if(cacheObj != null){
            notifyRemoved(source);
            File file = (File)cacheObj;
            if(file.exists() && file.canRead()){
                ((File)cacheObj).delete();
            }
            cacheObj = null;
            if(linkedReferences != null){
                linkedReferences.clear();
            }
        }
    }
    
    /**
     * キャッシュキーとキャッシュオブジェクトを保持する直列化用クラス。<p>
     *
     * @author M.Takata
     */
    public static class MapEntry implements Serializable{
        
        private static final long serialVersionUID = 4635469653838112700L;
        
        /**
         * キャッシュキー。<p>
         */
        protected Object key;
        
        /**
         * キャッシュオブジェクト。<p>
         */
        protected Object value;
        
        /**
         * キャッシュキーとキャッシュオブジェクトを保持するキャッシュエントリを生成する。<p>
         *
         * @param k キャッシュキー
         * @param val キャッシュオブジェクト
         */
        public MapEntry(Object k, Object val){
            key = k;
            value = val;
        }
        
        /**
         * キャッシュキーを取得する。<p>
         * 
         * @return キャッシュキー
         */
        public Object getKey(){
            return key;
        }
        
        /**
         * キャッシュオブジェクトを取得する。<p>
         * 
         * @return キャッシュオブジェクト
         */
        public Object getValue(){
            return value;
        }
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        writeSet(out, linkedReferences);
        writeSet(out, removeListeners);
        writeSet(out, accessListeners);
        writeSet(out, changeListeners);
        ServiceName name = null;
        if(externalizer != null){
            name = getServiceName(externalizer);
        }
        if(name != null){
            out.writeObject(name);
        }else{
            out.writeObject(externalizer);
        }
    }
    
    private void readObject(ObjectInputStream in)
     throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        linkedReferences = readSet(in);
        removeListeners = readSet(in);
        accessListeners = readSet(in);
        changeListeners = readSet(in);
        Object obj = in.readObject();
        if(obj != null){
            if(obj instanceof ServiceName){
                externalizer = (Externalizer)ServiceManagerFactory.getServiceObject(
                    (ServiceName)obj
                );
            }else{
                externalizer = (Externalizer)obj;
            }
        }
    }
}