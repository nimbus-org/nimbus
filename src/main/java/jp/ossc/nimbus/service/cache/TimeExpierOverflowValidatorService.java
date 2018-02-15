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
 * キャッシュ有効時間あふれ検証サービス。<p>
 * キャッシュ有効時間であふれを検証するOverflowValidatorである。<br>
 * 以下に、キャッシュしてから10秒を超えるとあふれるあふれ検証サービスのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="TimeExpierOverflowValidator"
 *                  code="jp.ossc.nimbus.service.cache.TimeExpierOverflowValidatorService"&gt;
 *             &lt;attribute name="ExpierTerm"&gt;10000&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class TimeExpierOverflowValidatorService extends ServiceBase
 implements OverflowValidator, CacheRemoveListener, java.io.Serializable,
            TimeExpierOverflowValidatorServiceMBean{
    
    private static final long serialVersionUID = -5221705956555820688L;
    
    /**
     * キャッシュ有効期間。<p>
     */
    private long expierTerm = -1;
    
    /**
     * キャッシュ有効区切り
     */
    private long period = -1;

    /**
     * キャッシュ参照と登録時間の集合。<p>
     */
    private Map references;
    
    // TimeExpierOverflowValidatorServiceMBeanのJavaDoc
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
        references = Collections.synchronizedMap(new LinkedHashMap());
    }

    /**
     * サービスを開始する。<p>
     * このサービスを利用可能な状態にする。<br>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception {
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
    
    // TimeExpierOverflowValidatorServiceMBeanのJavaDoc
    public void setExpierTerm(long millis) throws IllegalArgumentException{
        expierTerm = millis;
    }
    
    // TimeExpierOverflowValidatorServiceMBeanのJavaDoc
    public long getExpierTerm(){
        return expierTerm;
    }
    
    // TimeExpierOverflowValidatorServiceMBeanのJavaDoc
    public void setPeriod(long millis) throws IllegalArgumentException {
        period = millis;
    }

    // TimeExpierOverflowValidatorServiceMBeanのJavaDoc
    public long getPeriod() {
        return period;
    }

    /**
     * キャッシュが有効時間を過ぎているか検証する。<p>
     *
     * @return キャッシュが有効時間を過ぎている場合、過ぎているキャッシュの数を返す。過ぎていない場合は、0を返す
     */
    public int validate(){
        if(references == null || references.size() == 0){
            return 0;
        }
        synchronized(references){
            if(getState() != STARTED){
                return 0;
            }
            final long currentTime = System.currentTimeMillis();
            if(getExpierTerm() < 0
                && period <= 0
            ){
                return 0;
            }
            final Object[] dates = references.values().toArray();

            boolean validateExpierTime = true;
            if (expierTerm < 0) {
                // 有効期間の検証は行わない
                validateExpierTime = false;
            }
            boolean validatePeriod = true;
            if (period <= 0) {
                // 有効区切りの検証は行わない
                validatePeriod = false;
            }

            int count = 0;
            for(; count < dates.length; count++){
                // キャッシュ登録時刻[ms]
                long refMillis = ((Date)dates[count]).getTime();
  
                // 有効期間
                if(validateExpierTime
                    && currentTime < refMillis + expierTerm){
                    validateExpierTime = false;
                }
                
                // 有効区切り
                if(validatePeriod
                    && currentTime < refMillis + (period - (refMillis - 9 * 60 * 60 * 1000) % period)){
                    validatePeriod = false;
                }
                
                if (!validateExpierTime
                    && !validatePeriod) {
                    break;
                }
            }

            return count;
        }
    }

    // OverflowValidatorのJavaDoc
    public void add(CachedReference ref){
        if(references == null || ref == null){
            return;
        }
        synchronized(references){
            if(!references.containsKey(ref)){
                references.put(ref, new Date());
                ref.addCacheRemoveListener(this);
            }
        }
    }
    
    // OverflowValidatorのJavaDoc
    public void remove(CachedReference ref){
        if(references == null || ref == null){
            return;
        }
        synchronized(references){
            if(references.containsKey(ref)){
                references.remove(ref);
                ref.removeCacheRemoveListener(this);
            }
        }
    }
    
    // OverflowValidatorのJavaDoc
    public void reset(){
        if(references != null){
            synchronized(references){
                final Object[] refs = references.keySet().toArray();
                for(int i = 0; i < refs.length; i++){
                    final CachedReference ref = (CachedReference)refs[i];
                    remove(ref);
                }
                references.clear();
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
