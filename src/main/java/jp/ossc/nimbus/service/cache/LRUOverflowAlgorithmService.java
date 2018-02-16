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

import jp.ossc.nimbus.core.ServiceBase;

/**
 * Least Recently Used あふれアルゴリズムサービス。<p>
 * 以下に、LRUであふれ対象となるキャッシュオブジェクトを決定するあふれアルゴリズムサービスのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="LRUOverflowAlgorithm"
 *                  code="jp.ossc.nimbus.service.cache.LRUOverflowAlgorithmService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class LRUOverflowAlgorithmService extends ServiceBase
 implements OverflowAlgorithm, CacheRemoveListener, CacheAccessListener,
            java.io.Serializable, LRUOverflowAlgorithmServiceMBean{
    
    private static final long serialVersionUID = 2274633140923055371L;
    
    private Map referenceMap;
    private List referenceList;
    private long overflowCount;
    private long overflowCachedTime;
    
    // LRUOverflowAlgorithmServiceMBeanのJavaDoc
    public int size(){
        return referenceMap == null ? 0 : referenceMap.size();
    }
    
    // LRUOverflowAlgorithmServiceMBeanのJavaDoc
    public long getOverflowCount(){
        return overflowCount;
    }
    
    // LRUOverflowAlgorithmServiceMBeanのJavaDoc
    public long getAverageOverflowCachedTime(){
        return overflowCount == 0 ? 0l : (overflowCachedTime / overflowCount);
    }
    
    // LRUOverflowAlgorithmServiceMBeanのJavaDoc
    public String displayReferenceTimes(){
        if(referenceMap == null){
            return "";
        }
        synchronized(referenceMap){
            if(referenceMap.size() == 0){
                return "";
            }
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            Collections.sort(referenceList);
            TimeCachedReference timeRef
                 = (TimeCachedReference)referenceList.get(referenceList.size() - 1);
            final long currentTime = System.currentTimeMillis();
            final double unitTime = (currentTime - timeRef.getLastAccessTime()) / 100.0d;
            final Iterator itr = referenceList.iterator();
            while(itr.hasNext()){
                timeRef = (TimeCachedReference)itr.next();
                CachedReference ref = timeRef.getCachedReference();
                final long interval = currentTime - timeRef.getLastAccessTime();
                final long cachedTime = currentTime - timeRef.getCachedTime();
                final int point = (int)(interval / unitTime);
                for(int i = 0; i < point; i++){
                    pw.print('*');
                }
                pw.print('　');
                pw.print(Long.toString(interval));
                pw.print('(');
                pw.print(Long.toString(cachedTime));
                pw.print(')');
                if(ref instanceof KeyCachedReference){
                    pw.print('　');
                    pw.print(((KeyCachedReference)ref).getKey());
                }
                pw.println("<br>");
            }
            return sw.toString();
        }
    }
    
    /**
     * サービスの生成処理を行う。<p>
     * インスタンス変数の初期化を行う。
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        referenceMap = Collections.synchronizedMap(new HashMap());
        referenceList = Collections.synchronizedList(new ArrayList());
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     * インスタンス変数の開放を行う。
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        reset();
        referenceMap = null;
        referenceList = null;
    }
    
    /**
     * キャッシュ参照を追加する。<p>
     * 引数で渡されたキャッシュ参照を保持する。同時に、{@link CachedReference#addCacheAccessListener(CacheAccessListener)}で、{@link CacheAccessListener}として自分自身を登録する。また、{@link CachedReference#addCacheRemoveListener(CacheRemoveListener)}で、{@link CacheRemoveListener}として自分自身を登録する。<br>
     *
     * @param ref キャッシュ参照
     */
    public void add(CachedReference ref){
        if(referenceMap == null || ref == null){
            return;
        }
        synchronized(referenceMap){
            if(!referenceMap.containsKey(ref)){
                TimeCachedReference tmc = new TimeCachedReference(ref);
                referenceMap.put(ref, tmc);
                referenceList.add(tmc);
                ref.addCacheAccessListener(this);
                ref.addCacheRemoveListener(this);
            }
        }
    }
    
    /**
     * キャッシュ参照を削除する。<p>
     * 引数で渡されたキャッシュ参照を内部で保持している場合は、破棄する。同時に、{@link CachedReference#removeCacheAccessListener(CacheAccessListener)}で、{@link CacheAccessListener}として自分自身を登録解除する。また、{@link CachedReference#removeCacheRemoveListener(CacheRemoveListener)}で、{@link CacheRemoveListener}として自分自身を登録解除する。<br>
     *
     * @param ref キャッシュ参照
     */
    public void remove(CachedReference ref){
        if(referenceMap == null || ref == null){
            return;
        }
        synchronized(referenceMap){
            if(referenceMap.containsKey(ref)){
                TimeCachedReference tmc = (TimeCachedReference)referenceMap.remove(ref);
                referenceList.remove(tmc);
                ref.removeCacheAccessListener(this);
                ref.removeCacheRemoveListener(this);
            }
        }
    }
    
    /**
     * 参照されてない時間が最も長いキャッシュ参照をあふれさせる。<p>
     * {@link #add(CachedReference)}で渡されたキャッシュ参照の中から、参照されてない時間が最も長いキャッシュ参照を、あふれキャッシュ参照として返す。<br>
     *
     * @return 参照されてない時間が最も長いキャッシュ参照
     */
    public CachedReference overflow(){
        if(referenceMap == null){
            return null;
        }
        synchronized(referenceMap){
            if(referenceMap.size() != 0){
                Collections.sort(referenceList);
                TimeCachedReference tmc = (TimeCachedReference)referenceList.remove(0);
                referenceMap.remove(tmc.getCachedReference());
                overflowCachedTime += (System.currentTimeMillis() - tmc.getCachedTime());
                overflowCount++;
                return tmc.getCachedReference();
            }
            return null;
        }
    }
    
    /**
     * 参照されてない時間が最も長いキャッシュ参照をあふれさせる。<p>
     * {@link #add(CachedReference)}で渡されたキャッシュ参照の中から、参照されてない時間が最も長いキャッシュ参照を、あふれキャッシュ参照として返す。<br>
     *
     * @param size あふれ数
     * @return 参照されてない時間が最も長いキャッシュ参照
     */
    public CachedReference[] overflow(int size){
        if(referenceMap == null || referenceMap.size() == 0){
            return null;
        }
        synchronized(referenceMap){
            if(referenceMap.size() != 0){
                final CachedReference[] result = new CachedReference[Math.min(referenceMap.size(), size)];
                Collections.sort(referenceList);
                for(int i = 0; i < result.length; i++){
                    TimeCachedReference tmc = (TimeCachedReference)referenceList.remove(0);
                    referenceMap.remove(tmc.getCachedReference());
                    result[i] = tmc.getCachedReference();
                }
                return result;
            }
            return null;
        }
    }
    
    /**
     * あふれアルゴリズムを実行するために保持している情報を初期化する。<p>
     * {@link #add(CachedReference)}で渡されたキャッシュ参照を全て破棄する。<br>
     */
    public void reset(){
        synchronized(referenceMap){
            if(referenceMap != null){
                referenceMap.clear();
            }
            if(referenceList != null){
                referenceList.clear();
            }
        }
    }
    
    /**
     * キャッシュから削除されたキャッシュ参照の通知を受ける。<p>
     * {@link #remove(CachedReference)}を呼び出す。<br>
     *
     * @param ref キャッシュから削除されたキャッシュ参照
     */
    public void removed(CachedReference ref){
        remove(ref);
    }
    
    /**
     * 参照されたキャッシュ参照の通知を受ける。<p>
     * 内部で保持しているキャッシュ参照のリストに含まれる場合は、最後尾に移動させる。<br>
     *
     * @param ref 参照されたキャッシュ参照
     */
    public void accessed(CachedReference ref){
        if(referenceMap == null){
            return;
        }
        synchronized(referenceMap){
            if(referenceMap != null && referenceMap.containsKey(ref)){
                TimeCachedReference tmcRef = (TimeCachedReference)referenceMap.get(ref);
                if(tmcRef != null){
                    tmcRef.access();
                }
            }
        }
    }
    
    private static class TimeCachedReference
     implements java.io.Serializable, Comparable{
        
        private static final long serialVersionUID = 836007284114395682L;
        
        private CachedReference reference;
        private long lastAccessTime;
        private long cachedTime;
        
        public TimeCachedReference(CachedReference ref){
            reference = ref;
            lastAccessTime = System.currentTimeMillis();
            cachedTime = lastAccessTime;
        }
        public CachedReference getCachedReference(){
            return reference;
        }
        
        public void access(){
            lastAccessTime = System.currentTimeMillis();
        }
        
        public long getLastAccessTime(){
            return lastAccessTime;
        }
        
        public long getCachedTime(){
            return cachedTime;
        }
        
        public int compareTo(Object arg0) {
            if(arg0 == null || !(arg0 instanceof TimeCachedReference)){
                return 1;
            }
            if(arg0 == this){
                return 0;
            }
            TimeCachedReference comp = (TimeCachedReference)arg0;
            final long diff = lastAccessTime - comp.getLastAccessTime();
            if(diff == 0){
                return 0;
            }else if(diff > 0){
                return 1;
            }else{
                return -1;
            }
        }
     }
}
