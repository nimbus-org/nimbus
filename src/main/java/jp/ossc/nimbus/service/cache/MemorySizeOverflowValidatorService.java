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
 * メモリサイズあふれ検証サービス。<p>
 * 以下に、ヒープメモリの使用サイズが最大ヒープメモリの半分を超えるとあふれるあふれ検証サービスのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="MemorySizeOverflowValidator"
 *                  code="jp.ossc.nimbus.service.cache.MemorySizeOverflowValidatorService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class MemorySizeOverflowValidatorService extends ServiceBase
 implements OverflowValidator, CacheRemoveListener, java.io.Serializable,
            MemorySizeOverflowValidatorServiceMBean{
    
    private static final long serialVersionUID = -8937874822673039671L;
    
    private static final char KILO_UNIT = 'K';
    private static final char MEGA_UNIT = 'M';
    private static final char GIGA_UNIT = 'G';
    
    private static final long KILO_BYTE = 1024;
    private static final long MEGA_BYTE = KILO_BYTE * KILO_BYTE;
    private static final long GIGA_BYTE = MEGA_BYTE * KILO_BYTE;
    
    private String maxHeapMemorySizeStr = "64M";
    private long maxHeapMemorySize = 64 * MEGA_BYTE;
    
    private String highHeapMemorySizeStr = "32M";
    private long highHeapMemorySize = 32 * MEGA_BYTE;
    
    private Set references;
    
    {
        final Runtime runtime = Runtime.getRuntime();
        try{
            maxHeapMemorySize = runtime.maxMemory();
            maxHeapMemorySizeStr = Long.toString(maxHeapMemorySize);
            highHeapMemorySize = maxHeapMemorySize / 2;
            highHeapMemorySizeStr = Long.toString(highHeapMemorySize);
        }catch(NoSuchMethodError err){
        }
    }
    
    /**
     * サービスの生成処理を行う。<p>
     * インスタンス変数の初期化を行う。
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        references = Collections.synchronizedSet(new HashSet());
    }
    
    /**
     * サービスの開始処理を行う。<p>
     * 属性の妥当性チェックを行う。<br>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(maxHeapMemorySize <= highHeapMemorySize){
            throw new IllegalArgumentException(
                "maxHeapMemorySize must be larger than highHeapMemorySize."
            );
        }
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     * インスタンス変数の開放を行う。
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        reset();
        references = null;
    }
    
    // MemorySizeOverflowValidatorServiceMBeanのJavaDoc
    public void setMaxHeapMemorySize(String size)
     throws IllegalArgumentException{
        maxHeapMemorySize = convertMemorySize(size);
        maxHeapMemorySizeStr = size;
    }
    
    // MemorySizeOverflowValidatorServiceMBeanのJavaDoc
    public String getMaxHeapMemorySize(){
        return maxHeapMemorySizeStr;
    }
    
    // MemorySizeOverflowValidatorServiceMBeanのJavaDoc
    public void setHighHeapMemorySize(String size)
     throws IllegalArgumentException{
        highHeapMemorySize = convertMemorySize(size);
        highHeapMemorySizeStr = size;
    }
    
    // MemorySizeOverflowValidatorServiceMBeanのJavaDoc
    public String getHighHeapMemorySize(){
        return highHeapMemorySizeStr;
    }
    
    // MemorySizeOverflowValidatorServiceMBeanのJavaDoc
    public int size(){
        return references == null ? 0 : references.size();
    }
    
    private long convertMemorySize(String size)
     throws IllegalArgumentException{
        long value = 0L;
        boolean isValid = true;
        
        if(size == null){
            isValid = false;
        }else{
            final int length = size.length();
            if(length == 0){
                isValid = false;
            }else{
                final char unit = Character.toUpperCase(
                    size.charAt(length - 1)
                );
                String tmpSize = null;
                long unitValue = 0;
                switch(unit){
                case KILO_UNIT:
                    tmpSize = size.substring(0, length - 1);
                    unitValue = KILO_BYTE;
                    break;
                case MEGA_UNIT:
                    tmpSize = size.substring(0, length - 1);
                    unitValue = MEGA_BYTE;
                    break;
                case GIGA_UNIT:
                    tmpSize = size.substring(0, length - 1);
                    unitValue = GIGA_BYTE;
                    break;
                default:
                    tmpSize = size;
                    unitValue = 1;
                }
                try{
                    value = (long)(Double.parseDouble(tmpSize) * (long)unitValue);
                }catch(NumberFormatException e){
                    isValid = false;
                }
            }
        }
        if(value < 0){
            isValid = false;
        }
        if(!isValid){
            throw new IllegalArgumentException("Invalid size : " + size);
        }
        return value;
    }
    
    /**
     * 高負荷ヒープメモリサイズを取得する。<p>
     *
     * @return 高負荷ヒープメモリサイズ
     */
    protected long getHighHeapMemorySizeValue(){
        return highHeapMemorySize;
    }
    
    /**
     * 最大ヒープメモリサイズを取得する。<p>
     *
     * @return 最大ヒープメモリサイズ
     */
    protected long getMaxHeapMemorySizeValue(){
        return maxHeapMemorySize;
    }
    
    /**
     * キャッシュ参照を追加する。<p>
     * 引数で渡されたキャッシュ参照を保持する。同時に、{@link CachedReference#addCacheRemoveListener(CacheRemoveListener)}で、{@link CacheRemoveListener}として自分自身を登録する。<br>
     *
     * @param ref キャッシュ参照
     */
    public void add(CachedReference ref){
        if(references == null || ref == null){
            return;
        }
        synchronized(references){
            if(!references.contains(ref)){
                references.add(ref);
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
        if(references == null || ref == null){
            return;
        }
        synchronized(references){
            if(references.contains(ref)){
                references.remove(ref);
                ref.removeCacheRemoveListener(this);
            }
        }
    }
    
    /**
     * ヒープメモリの使用率であふれ検証を行う。<p>
     * 以下の計算式で、あふれ数を計算する。但し、計算結果が負の場合は、0とする。<br>
     * キャッシュサイズ×（使用ヒープメモリ‐高負荷ヒープメモリ）÷（最大ヒープメモリ‐高負荷ヒープメモリ）
     *
     * @return あふれ検証を行った結果あふれが発生する場合、あふれ数を返す。あふれない場合は、0を返す
     */
    public int validate(){
        if(references == null || references.size() == 0){
            return 0;
        }
        synchronized(references){
            if(getState() != STARTED){
                return 0;
            }
            float rate = calculateOverflowRate();
            final int overflowSize = (int)(references.size() * rate);
            return overflowSize > 0 ? overflowSize : 0;
        }
    }
    
    /**
     * あふれ検証を実行するために保持している情報を初期化する。<p>
     * {@link #add(CachedReference)}で渡されたキャッシュ参照を全て破棄する。<br>
     */
    public void reset(){
        if(references != null){
            references.clear();
        }
    }
    
    public float calculateOverflowRate(){
        final Runtime runtime = Runtime.getRuntime();
        final long currentHeap = runtime.totalMemory();
        final long currentFree = runtime.freeMemory();
        final long currentUse = currentHeap - currentFree;
        final long highHeap = getHighHeapMemorySizeValue();
        if(currentUse < highHeap){
            return 0.0f;
        }
        final long maxHeap = getMaxHeapMemorySizeValue();
        float rate
             = ((float)(currentUse - highHeap)) / (maxHeap - highHeap);
        rate = Math.min(rate, 1.0F);
        return rate;
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
