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
 * ファイルキャッシュ参照。<p>
 * キャッシュオブジェクトを直列化してファイルに保持する。<br>
 *
 * @author M.Takata
 */
public class FileCachedReference extends DefaultCachedReference
 implements Serializable{
    
    private static final long serialVersionUID = -1164595696487398731L;
    
    private transient Externalizer externalizer;
    
    /**
     * 指定されたファイルをキャッシュオブジェクトを直列化したファイルとして保持するキャッシュ参照を生成する。<p>
     *
     * @param file キャッシュオブジェクトを直列化したファイル
     */
    public FileCachedReference(File file){
        super(file);
    }
    
    /**
     * 指定されたファイルをキャッシュオブジェクトを直列化したファイルとして保持するキャッシュ参照を生成する。<p>
     *
     * @param file キャッシュオブジェクトを直列化したファイル
     * @param ext 直列化を行うExternalizer
     */
    public FileCachedReference(File file, Externalizer ext){
        super(file);
        externalizer = ext;
    }
    
    /**
     * 指定されたファイルに指定されたキャッシュオブジェクトを直列化して保持するキャッシュ参照を生成する。<p>
     *
     * @param file キャッシュオブジェクトを直列化するファイル
     * @param obj キャッシュオブジェクト
     * @exception IOException 直列化に失敗した場合
     */
    public FileCachedReference(File file, Object obj) throws IOException{
        super(file);
        serializeObject(file, obj);
    }
    
    /**
     * 指定されたファイルに指定されたキャッシュオブジェクトを直列化して保持するキャッシュ参照を生成する。<p>
     *
     * @param file キャッシュオブジェクトを直列化するファイル
     * @param obj キャッシュオブジェクト
     * @param ext 直列化を行うExternalizer
     * @exception IOException 直列化に失敗した場合
     */
    public FileCachedReference(File file, Object obj, Externalizer ext) throws IOException{
        super(file);
        externalizer = ext;
        serializeObject(file, obj);
    }
    
    /**
     * オブジェクトを指定ファイルにシリアライズして格納する。<p>
     * 
     * @param file キャッシュオブジェクトを直列化するファイル
     * @param obj キャッシュオブジェクト
     * @exception IOException 直列化に失敗した場合
     */
    protected void serializeObject(File file, Object obj)
     throws IOException{
        OutputStream os = new FileOutputStream(file);
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
     * オブジェクトを復元する。<p>
     * 
     * @return 復元オブジェクト
     * @exception IOException キャッシュファイルの復元に失敗した場合
     * @exception ClassNotFoundException キャッシュファイルの復元結果のクラスが 存在しない場合
     */
    public Object deserializeObject() throws IOException, ClassNotFoundException{
        return deserializeObject(getFile());
    }
    
    /**
     * 指定されたファイルからオブジェクトを復元する。<p>
     * 
     * @param file キャッシュオブジェクトが直列化されているファイル
     * @return 復元オブジェクト
     * @exception IOException キャッシュファイルの復元に失敗した場合
     * @exception ClassNotFoundException キャッシュファイルの復元結果のクラスが 存在しない場合
     */
    protected Object deserializeObject(File file)
     throws IOException, ClassNotFoundException{
        Object entry = null;
        InputStream is = new FileInputStream(file);
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
     * 直列化したキャッシュオブジェクトのファイルを取得する。<p>
     *
     * @return 直列化したキャッシュオブジェクトのファイル
     */
    public File getFile(){
        return (File)super.get(null, false);
    }
    
    /**
     * キャッシュされたオブジェクトを取得する。<p>
     * 第二引数がtrueの場合は、{@link #addCacheAccessListener(CacheAccessListener)}で登録された{@link CacheAccessListener}に通知する。但し、第一引数で渡された呼び出し元オブジェクトが通知先のCacheAccessListenerのインスタンスと等しい場合は、通知しない。<br>
     * 自身が保持するキャッシュオブジェクトがnullでない場合は、Fileオブジェクトにキャストして{@link #deserializeObject(File)}を呼び出して本来のキャッシュオブジェクトを復元して返す。但し、復元に失敗した場合は、nullを返す。自身が保持するキャッシュオブジェクトがnullの場合は、{@link #addLinkedReference(LinkedReference)}で登録された{@link LinkedReference}から取得を試みる。取得できた場合は、同様に復元して返す。取得できなかった場合は、nullを返す。<br>
     *
     * @param source キャッシュを取得するこのメソッドの呼び出し元オブジェクト
     * @param notify キャッシュアクセスリスナに通知する場合はtrue
     * @return キャッシュオブジェクト
     */
    public Object get(Object source, boolean notify){
        final Object obj = super.get(source, notify);
        if(obj instanceof File){
            File file = (File)obj;
            if(file.exists() && file.canRead()){
                try{
                    return deserializeObject(file);
                }catch(IOException e){
                    return null;
                }catch(ClassNotFoundException e){
                    return null;
                }
            }else{
                return null;
            }
        }else{
            return obj;
        }
    }
    
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