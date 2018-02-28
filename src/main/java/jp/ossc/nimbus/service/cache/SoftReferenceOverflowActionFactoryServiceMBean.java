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
import jp.ossc.nimbus.core.FactoryServiceBaseMBean;

/**
 * {@link SoftReferenceOverflowActionFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see SoftReferenceOverflowActionFactoryService
 */
public interface SoftReferenceOverflowActionFactoryServiceMBean
 extends FactoryServiceBaseMBean{
    
    /**
     * あふれたキャッシュオブジェクトをソフト参照にすると同時に永続化するための永続化キャッシュサービスのサービス名を設定する。<p>
     *
     * @param name 永続化キャッシュサービスのサービス名
     */
    public void setPersistCacheServiceName(ServiceName name);
    
    /**
     * あふれたキャッシュオブジェクトをソフト参照にすると同時に永続化するための永続化キャッシュサービスのサービス名を取得する。<p>
     *
     * @return 永続化キャッシュサービスのサービス名
     */
    public ServiceName getPersistCacheServiceName();
    
    /**
     * あふれたキャッシュオブジェクトをソフト参照にすると同時に永続化するための永続化キャッシュマップサービスのサービス名を設定する。<p>
     *
     * @param name 永続化キャッシュマップサービスのサービス名
     */
    public void setPersistCacheMapServiceName(ServiceName name);
    
    /**
     * あふれたキャッシュオブジェクトをソフト参照にすると同時に永続化するための永続化キャッシュマップサービスのサービス名を取得する。<p>
     *
     * @return 永続化キャッシュマップサービスのサービス名
     */
    public ServiceName getPersistCacheMapServiceName();
    
    /**
     * あふれ動作を実行するために保持している情報を初期化する。<p>
     */
    public void reset();
}
