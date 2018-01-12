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

import jp.ossc.nimbus.core.*;

/**
 * {@link AbstractCacheMapFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see AbstractCacheMapFactoryService
 */
public interface AbstractCacheMapFactoryServiceMBean
 extends FactoryServiceBaseMBean{
    
    /**
     * あふれ制御を行うOverflowControllerインタフェースを実装したサービスのサービス名の配列を設定する。<p>
     *
     * @param names あふれ制御を行うOverflowControllerインタフェースを実装したサービスのサービス名の配列
     */
    public void setOverflowControllerServiceNames(ServiceName[] names);
    
    /**
     * あふれ制御を行うOverflowControllerインタフェースを実装したサービスのサービス名の配列を取得する。<p>
     *
     * @return あふれ制御を行うOverflowControllerインタフェースを実装したサービスのサービス名の配列
     */
    public ServiceName[] getOverflowControllerServiceNames();
    
    /**
     * サービスの停止時にキャッシュをクリアするかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isClear サービスの停止時にキャッシュをクリアする場合は、true
     */
    public void setClearOnStop(boolean isClear);
    
    /**
     * サービスの停止時にキャッシュをクリアするかどうかを調べる。<p>
     *
     * @return サービスの停止時にキャッシュをクリアする場合は、true
     */
    public boolean isClearOnStop();
    
    /**
     * サービスの破棄時にキャッシュをクリアするかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isClear サービスの破棄時にキャッシュをクリアする場合は、true
     */
    public void setClearOnDestroy(boolean isClear);
    
    /**
     * サービスの破棄時にキャッシュをクリアするかどうかを調べる。<p>
     *
     * @return サービスの破棄時にキャッシュをクリアする場合は、true
     */
    public boolean isClearOnDestroy();
    
    /**
     * 管理対象のキャッシュの中に指定されたキーを含むキャッシュが存在するかどうかを調べる。<p>
     *
     * @param key キャッシュのキー
     * @return 管理対象のキャッシュの中に指定されたキーを含むキャッシュが存在する場合は、true
     */
    public boolean containsKey(Object key);
    
    /**
     * 管理対象のキャッシュの中に指定された値を含むキャッシュが存在するかどうかを調べる。<p>
     *
     * @param value キャッシュの値
     * @return 管理対象のキャッシュの中に指定された値を含むキャッシュが存在する場合は、true
     */
    public boolean containsValue(Object value);
    
    /**
     * 管理対象のキャッシュの中から指定されたキーのキャッシュ参照を取得する。<p>
     * 指定されたキーをキャッシュする管理対象キャッシュが存在する場合、その最初に見つかったキャッシュが保持するキャッシュ参照を返す。指定されたキーをキャッシュする管理対象キャッシュが存在しない場合、nullを返す。<br>
     *
     * @param key キャッシュのキー
     * @return キーに対応するキャッシュ参照の一つ
     */
    public KeyCachedReference getCachedReference(Object key);
    
    /**
     * 管理対象のキャッシュの中から指定されたキーのキャッシュ値を取得する。<p>
     * 指定されたキーをキャッシュする管理対象キャッシュが存在する場合、その最初に見つかったキャッシュが保持するキャッシュ値を返す。指定されたキーをキャッシュする管理対象キャッシュが存在しない場合、nullを返す。<br>
     *
     * @param key キャッシュのキー
     * @return キーに対応するキャッシュ値の一つ
     */
    public Object get(Object key);
    
    /**
     * 指定されたオブジェクトを指定されたキーと関連付けて、管理対象の全てのキャッシュにキャッシュする。<p>
     *
     * @param key キャッシュのキー
     * @param value キャッシュ値
     */
    public void put(Object key, Object value);
    
    /**
     * 指定されたマップを、管理対象の全てのキャッシュにキャッシュする。<p>
     *
     * @param map キャッシュするキーと値のマップ
     */
    public void putAll(Map map);
    
    /**
     * 指定されたキーに該当するキャッシュ値を、管理対象の全てのキャッシュから削除する。<p>
     *
     * @param key キャッシュのキー
     */
    public void remove(Object key);
    
    /**
     * 全てのキャッシュ値を、管理対象の全てのキャッシュから削除する。<p>
     */
    public void clear();
}
