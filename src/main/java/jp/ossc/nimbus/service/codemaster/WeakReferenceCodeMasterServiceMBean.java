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
// パッケージ
// インポート
package jp.ossc.nimbus.service.codemaster;

import java.util.Date;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * 弱参照によるキャッシュ機能つきコードマスターサービス<p>
 * 弱参照を使用したコードマスタ提供を行う
 * @version $Name:  $
 * @author K.Nagai
 * @since 1.0
 */
public interface WeakReferenceCodeMasterServiceMBean extends ServiceBaseMBean {
    /**
     * コードマスターの名前を設定する。
     * @param names コードマスタ名配列
     */
    public void setMasterNames(String[] names) ;
    /**
     * コードマスターの名前を取得。
     * @return コードマスタ名配列
     */
    public String[] getMasterNames() ;
    /**
     * BeanFlowInvokerファクトリ名設定
     * @param name BeanFlowInvokerFactoryサービス名
     */
    public void setBeanFlowInvokerFactoryName(ServiceName name);
    /**
     * BeanFlowInvokerファクトリ名取得
     * @return BeanFlowInvokerFactoryサービス名
     */
    public ServiceName getBeanFlowInvokerFactoryName();
    /**
     * CacheService名設定
     * @param name CacheService名
     */
    public void setCacheServiceName(ServiceName name);
    /**
     * CacheService名取得
     * @return CacheService名
     */
    public ServiceName getCacheServiceName();
    /**
     * 全マスタ更新
     * @param date 時系列に登録するマスタの時刻
     */
    public void codeMasterRefresh(Date date);
    /**
     * 全マスタ更新
     * 時系列に登録されるマスタの時刻は現在時刻となる
     */
    public void codeMasterRefresh();
    /**
     * 指定マスタ更新
     * 時系列に登録されるマスタの時刻は現在時刻となる
     * @param flowName 更新するマスタ名
     */
    public void codeMasterRefresh( String flowName );
    /**
     * 指定マスタ更新
     * @param beanflowName 更新するマスタ名
     * @param date 時系列に登録するマスタの時刻
     */
    public void codeMasterRefresh(String beanflowName,Date date) ;

    /**
     * 全コードマスタを更新する時に更新しないマスタのマスタ名配列を設定する。<p>
     * 指定しない場合は、全てのマスタが全コードマスタ更新時に取得される。<br>
     *
     * @param names マスタ名配列
     */
    public void setNotUpdateAllMasterNames(String[] names);

    /**
     * 全コードマスタを更新する時に更新しないマスタのマスタ名配列を取得する。<p>
     *
     * @return マスタ名配列
     */
    public String[] getNotUpdateAllMasterNames();
}
