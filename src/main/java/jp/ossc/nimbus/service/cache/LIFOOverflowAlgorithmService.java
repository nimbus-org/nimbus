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

import jp.ossc.nimbus.core.ServiceBase;

/**
 * Last In First Outあふれアルゴリズムサービス。<p>
 * 以下に、LIFOであふれ対象となるキャッシュオブジェクトを決定するあふれアルゴリズムサービスのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="LIFOOverflowAlgorithm"
 *                  code="jp.ossc.nimbus.service.cache.LIFOOverflowAlgorithmService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class LIFOOverflowAlgorithmService extends ServiceBase
 implements OverflowAlgorithm, CacheRemoveListener, java.io.Serializable,
            LIFOOverflowAlgorithmServiceMBean{
    
    private static final long serialVersionUID = 2933104597491523323L;
    
    private List referenceList;
    
    // LIFOOverflowAlgorithmServiceMBeanのJavaDoc
    public int size(){
        return referenceList == null ? 0 : referenceList.size();
    }
    
    /**
     * サービスの生成処理を行う。<p>
     * インスタンス変数の初期化を行う。
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
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
        referenceList = null;
    }
    
    /**
     * キャッシュ参照を追加する。<p>
     * 引数で渡されたキャッシュ参照を保持する。同時に、{@link CachedReference#addCacheRemoveListener(CacheRemoveListener)}で、{@link CacheRemoveListener}として自分自身を登録する。<br>
     *
     * @param ref キャッシュ参照
     */
    public void add(CachedReference ref){
        if(referenceList == null || ref == null){
            return;
        }
        synchronized(referenceList){
            if(!referenceList.contains(ref)){
                referenceList.add(ref);
                ref.addCacheRemoveListener(this);
            }
        }
    }
    
    /**
     * キャッシュ参照を削除する。<p>
     * 引数で渡されたキャッシュ参照を内部で保持している場合は、破棄する。同時に、{@link CachedReference#removeCacheRemoveListener(CacheRemoveListener)}で、{@link CacheRemoveListener}として自分自身を登録解除する。<br>
     *
     * @param ref キャッシュ参照
     */
    public void remove(CachedReference ref){
        if(referenceList == null || ref == null){
            return;
        }
        synchronized(referenceList){
            if(referenceList.contains(ref)){
                referenceList.remove(ref);
                ref.removeCacheRemoveListener(this);
            }
        }
    }
    
    /**
     * 後入れ先出しで、あふれるキャッシュ参照を決定する。<p>
     * {@link #add(CachedReference)}で渡されたキャッシュ参照の中から、最後に追加されたキャッシュ参照を、あふれキャッシュ参照として返す。<br>
     *
     * @return 後入れ先出しのアルゴリズムで決定されたあふれキャッシュ参照
     */
    public CachedReference overflow(){
        if(referenceList == null){
            return null;
        }
        synchronized(referenceList){
            final int size = referenceList.size();
            if(size != 0){
                return (CachedReference)referenceList.remove(size - 1);
            }
            return null;
        }
    }
    
    /**
     * 後入れ先出しで、あふれるキャッシュ参照を決定する。<p>
     * {@link #add(CachedReference)}で渡されたキャッシュ参照の中から、最後に追加されたキャッシュ参照を、あふれキャッシュ参照として返す。<br>
     *
     * @param size あふれ数
     * @return 後入れ先出しのアルゴリズムで決定されたあふれキャッシュ参照
     */
    public CachedReference[] overflow(int size){
        if(referenceList == null || referenceList.size() == 0){
            return null;
        }
        synchronized(referenceList){
            if(referenceList.size() != 0){
                final CachedReference[] result = new CachedReference[Math.min(referenceList.size(), size)];
                for(int i = 0; i < result.length; i++){
                    result[i] = (CachedReference)referenceList.remove(referenceList.size() - 1);
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
        if(referenceList != null){
            referenceList.clear();
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
}
