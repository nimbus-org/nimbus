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

import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link MemoryCacheMapService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see MemoryCacheService
 */
public interface MemoryCacheMapServiceMBean
 extends AbstractCacheMapServiceMBean{
    
    /**
     * 永続化先となる{@link CacheMap}サービスのサービス名を設定する。<p>
     *
     * @param name CacheMapサービスのサービス名
     */
    public void setPersistCacheMapServiceName(ServiceName name);
    
    /**
     * 永続化先となる{@link CacheMap}サービスのサービス名を取得する。<p>
     *
     * @return CacheMapサービスのサービス名
     */
    public ServiceName getPersistCacheMapServiceName();
    
    /**
     * サービスの開始時に、永続化先となる{@link CacheMap}からキャッシュエントリを取得して、このキャッシュに読み込みを行うかどうかを設定する。<p>
     * デフォルトは、trueで読み込みを行う。<br>
     *
     * @param isLoad 読み込みを行う場合はtrue
     */
    public void setLoadOnStart(boolean isLoad);
    
    /**
     * サービスの開始時に、永続化先となる{@link CacheMap}からキャッシュエントリを取得して、このキャッシュに読み込みを行うかどうかを判定する。<p>
     *
     * @return trueの場合、読み込みを行う
     */
    public boolean isLoadOnStart();
    
    /**
     * サービスの停止時に、永続化先となる{@link CacheMap}にキャッシュエントリを保存するかどうかを設定する。<p>
     * デフォルトは、trueで保存する。<br>
     *
     * @param isSave 保存する場合はtrue
     */
    public void setSaveOnStop(boolean isSave);
    
    /**
     * サービスの停止時に、永続化先となる{@link CacheMap}にキャッシュエントリを保存するかどうかを判定する。<p>
     *
     * @return trueの場合は、保存する
     */
    public boolean isSaveOnStop();
    
    /**
     * 永続化先となる{@link CacheMap}からキャッシュエントリを取得して、このキャッシュに読み込みを行う。<p>
     *
     * @exception Exception 読み込みに失敗した場合
     */
    public void load() throws Exception;
    
    /**
     * 永続化先となる{@link CacheMap}にキャッシュエントリを保存する。<p>
     *
     * @exception Exception 保存に失敗した場合
     */
    public void save() throws Exception;
}
