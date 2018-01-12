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
 * キャッシュサイズあふれ検証サービス。<p>
 * 最大キャッシュ数であふれを検証するOverflowValidatorである。<br>
 * 以下に、キャッシュサイズが10を超えるとあふれるあふれ検証サービスのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="CacheSizeOverflowValidator"
 *                  code="jp.ossc.nimbus.service.cache.CacheSizeOverflowValidatorService"&gt;
 *             &lt;attribute name="MaxSize"&gt;10&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class CacheSizeOverflowValidatorService extends ServiceBase
 implements OverflowValidator, CacheRemoveListener, java.io.Serializable,
            CacheSizeOverflowValidatorServiceMBean{
    
    private static final long serialVersionUID = -2810585852541528435L;
    
    /**
     * 最大キャッシュ数。<p>
     */
    private int maxSize;
    
    /**
     * あふれ閾値。<p>
     */
    private int overflowThreshold;
    
    /**
     * キャッシュ参照の集合。<p>
     */
    private Set references;
    
    // CacheSizeOverflowValidatorServiceMBeanのJavaDoc
    public int size(){
        return references == null ? 0 : references.size();
    }
    
    /**
     * サービスの生成処理を行う。<p>
     * インスタンス変数の初期化を行う。<br>
     * 
     * @exception Exception サービスの生成に失敗した場合
     */
    public void createService() throws Exception{
        references = Collections.synchronizedSet(new HashSet());
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     * {@link #reset()}を呼び出す。また、インスタンス変数の参照を破棄する。<br>
     *
     * @exception Exception サービスの破棄に失敗した場合
     */
    public void destroyService() throws Exception{
        reset();
        references = null;
    }
    
    // CacheSizeOverflowValidatorServiceMBeanのJavaDoc
    public void setMaxSize(int size) throws IllegalArgumentException{
        if(size < 0){
            throw new IllegalArgumentException("Invalid size : " + size);
        }
        maxSize = size;
    }
    
    // CacheSizeOverflowValidatorServiceMBeanのJavaDoc
    public int getMaxSize(){
        return maxSize;
    }
    
    // CacheSizeOverflowValidatorServiceMBeanのJavaDoc
    public void setOverflowThreshold(int threshold){
        overflowThreshold = threshold;
    }
    
    // CacheSizeOverflowValidatorServiceMBeanのJavaDoc
    public int getOverflowThreshold(){
        return overflowThreshold;
    }
    
    /**
     * キャッシュ数が最大キャッシュ数を超えているか検証する。<p>
     * 最大キャッシュ数は、{@link #setMaxSize(int)}で設定された値。<br>
     *
     * @return キャッシュ数が最大キャッシュ数を超えている場合、超えている数を返す。超えていない場合は、0を返す
     */
    public int validate(){
        if(references == null || references.size() == 0){
            return 0;
        }
        if(getState() != STARTED){
            return 0;
        }
        if(getMaxSize() == 0){
            return 0;
        }
        int overflowSize = references.size() - getMaxSize();
        if(overflowSize > 0 && getOverflowThreshold() > 0){
            overflowSize = getMaxSize() - getOverflowThreshold();
        }
        return overflowSize > 0 ? overflowSize : 0;
    }
    
    // OverflowValidatorのJavaDoc
    public void add(CachedReference ref){
        if(references == null || ref == null){
            return;
        }
        if(!references.contains(ref)){
            references.add(ref);
            ref.addCacheRemoveListener(this);
        }
    }
    
    // OverflowValidatorのJavaDoc
    public void remove(CachedReference ref){
        if(references == null || ref == null){
            return;
        }
        if(references.contains(ref)){
            references.remove(ref);
            ref.removeCacheRemoveListener(this);
        }
    }
    
    // OverflowValidatorのJavaDoc
    public void reset(){
        if(references != null){
            final Object[] refs = references.toArray();
            for(int i = 0; i < refs.length; i++){
                final CachedReference ref = (CachedReference)refs[i];
                remove(ref);
            }
        }
    }
    
    /**
     * {@link #add(CachedReference)}で追加された{@link CachedReference}のキャッシュオブジェクトが削除された場合に呼び出される。<p>
     * 削除されたキャッシュ参照を{@link #remove(CachedReference)}で、このOverflowValidatorからも削除する。<br>
     *
     * @param ref 削除されたキャッシュオブジェクトのキャッシュ参照
     */
    public void removed(CachedReference ref){
        if(references == null){
            return;
        }
        remove(ref);
    }
}
