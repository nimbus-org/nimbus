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
package jp.ossc.nimbus.service.beancontrol.interfaces;

import jp.ossc.nimbus.service.beancontrol.BeanFlowMonitor;
import jp.ossc.nimbus.service.beancontrol.BeanFlowCoverage;
import jp.ossc.nimbus.service.beancontrol.BeanFlowAsynchInvokeCallback;

/**
 * Beanフロー実行インタフェース。<p>
 * 
 * @author H.Nakano
 */
public interface BeanFlowInvoker{
    
    /**
     * Beanフローを実行する。<p>
     * 
     * @param obj Beanフローへの引数
     * @return Beanフローの実行結果
     * @exception Exception Beanフローの実行中に例外が発生した場合
     */
    public Object invokeFlow(Object obj) throws Exception;
    
    /**
     * Beanフローを実行する。<p>
     * 
     * @param obj Beanフローへの引数
     * @param monitor モニター
     * @return Beanフローの実行結果
     * @exception Exception Beanフローの実行中に例外が発生した場合
     */
    public Object invokeFlow(Object obj, BeanFlowMonitor monitor) throws Exception;
    
    /**
     * Beanフローを非同期実行する。<p>
     * 
     * @param obj Beanフローへの引数
     * @param monitor モニター
     * @param isReply 応答が必要な場合は、true
     * @param maxAsynchWait 最大非同期実行待機数
     * @return Beanフローの実行コンテキスト
     * @exception Exception Beanフローの非同期実行処理で例外が発生した場合
     */
    public Object invokeAsynchFlow(Object obj, BeanFlowMonitor monitor, boolean isReply, int maxAsynchWait) throws Exception;
    
    /**
     * Beanフローの非同期実行の結果を取得する。<p>
     * 
     * @param context Beanフローの実行コンテキスト
     * @param monitor モニター
     * @param timeout タイムアウト[ms]。-1を指定した場合は、無限待ち
     * @param isCancel タイムアウト時に非同期実行をキャンセルするかどうか。trueを指定した場合は、キャンセルする
     * @return Beanフローの非同期実行結果
     * @exception BeanFlowAsynchTimeoutException 指定されたタイムアウトを過ぎても応答がない場合
     * @exception Exception Beanフローの実行中に例外が発生した場合
     */
    public Object getAsynchReply(Object context, BeanFlowMonitor monitor, long timeout, boolean isCancel) throws BeanFlowAsynchTimeoutException, Exception;
    
    /**
     * Beanフローを非同期実行する。<p>
     * 
     * @param obj Beanフローへの引数
     * @param monitor モニター
     * @param callback コールバック
     * @param maxAsynchWait 最大非同期実行待機数
     * @return Beanフローの実行コンテキスト
     * @exception Exception Beanフローの非同期実行処理で例外が発生した場合
     */
    public Object invokeAsynchFlow(Object obj, BeanFlowMonitor monitor, BeanFlowAsynchInvokeCallback callback, int maxAsynchWait) throws Exception;
    
    /**
     * Beanフロー監視オブジェクトを生成する。<p>
     *
     * @return このBeanフローを監視するオブジェクト
     */
    public BeanFlowMonitor createMonitor();
    
    /**
     * Beanフローの名称を取得する。<p>
     * 
     * @return Beanフローの名称
     */
    public String getFlowName();
    
    /**
     * 上書きBeanフロー名を取得する。<p>
     *
     * @return 上書きBeanフロー名の配列
     */
    public String[] getOverwrideFlowNames();
    
    /**
     * カバレッジを取得する。<p>
     *
     * @return カバレッジ
     */
    public BeanFlowCoverage getBeanFlowCoverage();
    
    /**
     * Beanフローが定義されているリソースパスを取得する。<p>
     *
     * @return リソースパス
     */
    public String getResourcePath();
    
    /**
     * 終了する。<p>
     */
    public void end();
}
