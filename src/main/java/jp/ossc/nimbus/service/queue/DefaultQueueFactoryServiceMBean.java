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
package jp.ossc.nimbus.service.queue;

import jp.ossc.nimbus.core.*;

/**
 * {@link DefaultQueueFactoryService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see DefaultQueueService
 */
public interface DefaultQueueFactoryServiceMBean
 extends FactoryServiceBaseMBean{
    
    /**
     * キューの初期容量を設定する。<p>
     * サービスの生成時に使用される属性なので、生成後の変更はできない。<br>
     * 0以上の値を設定すると有効になる。デフォルト値は、-1で「初期容量を指定しない」である。<br>
     *
     * @param initial キューの初期容量
     */
    public void setInitialCapacity(int initial);
    
    /**
     * キューの初期容量を取得する。<p>
     *
     * @return キューの初期容量
     */
    public int getInitialCapacity();
    
    /**
     * キューの要素数が容量を越えた時に、増加させる容量を設定する。<p>
     * サービスの生成時に使用される属性なので、生成後の変更はできない。<br>
     * 0以上の値を設定すると有効になる。また、有効な初期容量が設定されていない場合は、無効となる。デフォルト値は、-1で「増加容量を指定しない」である。<br>
     *
     * @param increment 増加容量
     */
    public void setCapacityIncrement(int increment);
    
    /**
     * キューの要素数が容量を越えた時に、増加させる容量を取得する。<p>
     *
     * @return 増加容量
     */
    public int getCapacityIncrement();
    
    /**
     * キュー要素をキャッシュするキャッシュサービス名を設定する。<p>
     * この属性が設定されている場合、指定されたキャッシュサービスに、キュー要素をキャッシュする。キュー内部には、{@link jp.ossc.nimbus.service.cache.CachedReference CachedReference}が保持されるため、キュー要素の性質はキャッシュサービスに委ねられる。<br>
     *
     * @param name {@link jp.ossc.nimbus.service.cache.Cache Cache}サービス名
     */
    public void setCacheServiceName(ServiceName name);
    
    /**
     * キュー要素をキャッシュするキャッシュサービス名を取得する。<p>
     *
     * @return {@link jp.ossc.nimbus.service.cache.Cache Cache}サービス名
     */
    public ServiceName getCacheServiceName();
    
    /**
     * キューを初期化する。 <p>
     */
    public void clear();
}
