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
 * キー付き直列化キャッシュ参照。<p>
 *
 * @author M.Takata
 */
public class SerializeKeyCachedReference extends DefaultKeyCachedReference
 implements Serializable{
    
    private static final long serialVersionUID = -2231571982502593180L;
    
    private transient Externalizer externalizer;
    
    /**
     * 指定されたキャッシュキー、キャッシュオブジェクトを保持する新しいキー付きキャッシュ参照を生成する。<p>
     * キャッシュオブジェクトは、直列化して保存する。<br>
     * 
     * @param key キャッシュキー
     * @param obj キャッシュオブジェクト
     * @exception IOException キャッシュオブジェクトの直列化に失敗した場合
     */
    public SerializeKeyCachedReference(Object key, Object obj)
     throws IOException{
        super(key, null);
        serializeObject(obj);
    }
    
    /**
     * 指定されたキャッシュキー、キャッシュオブジェクトを保持する新しいキー付きキャッシュ参照を生成する。<p>
     * キャッシュオブジェクトは、直列化して保存する。<br>
     * 
     * @param key キャッシュキー
     * @param obj キャッシュオブジェクト
     * @exception IOException キャッシュオブジェクトの直列化に失敗した場合
     */
    public SerializeKeyCachedReference(Object key, Object obj, Externalizer ext)
     throws IOException{
        super(key, null);
        externalizer = ext;
        serializeObject(obj);
    }
    
    /**
     * オブジェクトをシリアライズして格納する。<p>
     * 
     * @param obj キャッシュオブジェクト
     * @exception IOException 直列化に失敗した場合
     */
    protected void serializeObject(Object obj)
     throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(externalizer == null){
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.flush();
        }else{
            externalizer.writeExternal(obj, baos);
        }
        cacheObj = baos.toByteArray();
    }
    
    /**
     * オブジェクトを復元する。<p>
     * 
     * @param bytes 直列化バイト配列
     * @return 復元オブジェクト
     * @exception IOException キャッシュファイルの復元に失敗した場合
     * @exception ClassNotFoundException キャッシュファイルの復元結果のクラスが 存在しない場合
     */
    protected Object deserializeObject(byte[] bytes)
     throws IOException, ClassNotFoundException{
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Object entry = null;
        if(externalizer == null){
            ObjectInputStream ois = new ObjectInputStream(bais);
            entry = ois.readObject();
        }else{
            entry = externalizer.readExternal(bais);
        }
        return entry;
    }
    
    /**
     * 直列化したキャッシュオブジェクトを取得する。<p>
     *
     * @return 直列化したキャッシュオブジェクト
     */
    public byte[] getBytes(){
        return (byte[])super.get(null, false);
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
    public Object get(Object source, boolean notify){
        final Object obj = super.get(source, notify);
        if(obj instanceof byte[]){
            try{
                return deserializeObject((byte[])obj);
            }catch(IOException e){
                return null;
            }catch(ClassNotFoundException e){
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
                serializeObject(obj);
            }catch(IOException e){
                throw new IllegalCachedReferenceException(e);
            }
        }else{
            cacheObj = null;
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
            cacheObj = null;
            if(linkedReferences != null){
                linkedReferences.clear();
            }
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