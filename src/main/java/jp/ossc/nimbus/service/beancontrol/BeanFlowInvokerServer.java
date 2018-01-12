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
package jp.ossc.nimbus.service.beancontrol;

import java.rmi.*;
import java.util.Set;
import java.util.Map;

import jp.ossc.nimbus.service.beancontrol.interfaces.InvalidConfigurationException;

/**
 * 業務フロー実行サーバ。<p>
 *
 * @author M.Takata
 */
public interface BeanFlowInvokerServer extends Remote{
    
    /**
     * このサーバがリクエスト受付可能かを判定する。<p>
     *
     * @return リクエスト受付可能な場合true
     * @exception RemoteException リモート呼び出しに失敗した場合
     */
    public boolean isAcceptable() throws RemoteException;
    
    /**
     * このサーバに生成されているBeanフローの数を取得する。<p>
     *
     * @return 生成されているBeanフローの数
     * @exception RemoteException リモート呼び出しに失敗した場合
     */
    public int getCurrentFlowCount() throws RemoteException;
    
    /**
     * このサーバのリソース利用量を取得する。<p>
     *
     * @return リソース利用量
     * @exception RemoteException リモート呼び出しに失敗した場合
     */
    public Comparable getResourceUsage() throws RemoteException;
    
    /**
     * この業務フロー実行サーバが保持しているBeanフロー名の集合を取得する。<p>
     *
     * @return Beanフロー名の集合
     * @exception RemoteException リモート呼び出しに失敗した場合
     */
    public Set getBeanFlowNameSet() throws RemoteException;
    
    /**
     * 指定されたBeanフローをこの業務フロー実行サーバが保持しているかどうかを判定する。<p>
     *
     * @param name Beanフロー名
     * @return この業務フロー実行サーバが保持している場合true
     * @exception RemoteException リモート呼び出しに失敗した場合
     */
    public boolean containsFlow(String name) throws RemoteException;
    
    /**
     * サーバ上にBeanフローを生成する。<p>
     *
     * @param flowName Beanフロー名
     * @param caller 呼び出し元のBeanフロー名
     * @param isOverwride オーバーライドされているかどうか
     * @return Beanフローを実行する際の実行ID
     * @exception RemoteException リモート呼び出しに失敗した場合
     * @exception InvalidConfigurationException 指定されたBeanフローが存在しない場合
     */
    public Object createFlow(String flowName, String caller, boolean isOverwride) throws RemoteException, InvalidConfigurationException;
    
    /**
     * 指定された実行IDのBeanフローがサーバ上に生成されているかを判定する。<p>
     *
     * @param id 実行ID
     * @return Beanフローが生成されている場合true
     * @exception RemoteException リモート呼び出しに失敗した場合
     */
    public boolean isExistsFlow(Object id) throws RemoteException;
    
    /**
     * 指定された実行IDのBeanフローの上書きフロー名を取得する。<p>
     *
     * @param id 実行ID
     * @return 上書きフロー名の配列
     * @exception RemoteException リモート呼び出しに失敗した場合
     * @exception NoSuchBeanFlowIdException 指定された実行IDのBeanフローが存在しない場合
     */
    public String[] getOverwrideFlowNames(Object id) throws RemoteException, NoSuchBeanFlowIdException;
    
    /**
     * カバレッジを取得する。<p>
     *
     * @param id 実行ID
     * @return カバレッジ
     * @exception RemoteException リモート呼び出しに失敗した場合
     * @exception NoSuchBeanFlowIdException 指定された実行IDのBeanフローが存在しない場合
     */
    public BeanFlowCoverage getBeanFlowCoverage(Object id) throws RemoteException, NoSuchBeanFlowIdException;
    
    /**
     * Beanフローが定義されているリソースパスを取得する。<p>
     *
     * @param id 実行ID
     * @return リソースパス
     * @exception RemoteException リモート呼び出しに失敗した場合
     * @exception NoSuchBeanFlowIdException 指定された実行IDのBeanフローが存在しない場合
     */
    public String getResourcePath(Object id) throws RemoteException, NoSuchBeanFlowIdException;
    
    /**
     * Beanフローを実行する。<p>
     * 
     * @param id 実行ID
     * @param input Beanフローへの引数
     * @param context コンテキスト情報
     * @return Beanフローの実行結果
     * @exception RemoteException リモート呼び出しに失敗した場合
     * @exception NoSuchBeanFlowIdException 指定された実行IDのBeanフローが存在しない場合
     * @exception Exception Beanフローの実行中に例外が発生した場合
     */
    public Object invokeFlow(Object id, Object input, Map context) throws NoSuchBeanFlowIdException, RemoteException, Exception;
    
    /**
     * Beanフローを非同期実行する。<p>
     * 
     * @param id 実行ID
     * @param input Beanフローへの引数
     * @param context コンテキスト情報
     * @param callback コールバック
     * @param maxAsynchWait 最大非同期実行待機数
     * @exception RemoteException リモート呼び出しに失敗した場合
     * @exception NoSuchBeanFlowIdException 指定された実行IDのBeanフローが存在しない場合
     * @exception Exception Beanフローの実行中に例外が発生した場合
     */
    public void invokeAsynchFlow(Object id, Object input, Map context, BeanFlowAsynchInvokeCallback callback, int maxAsynchWait) throws NoSuchBeanFlowIdException, RemoteException, Exception;
    
    /**
     * 指定された実行IDのBeanフローを一時停止させる。<p>
     *
     * @param id 実行ID
     * @exception RemoteException リモート呼び出しに失敗した場合
     * @exception NoSuchBeanFlowIdException 指定された実行IDのBeanフローが存在しない場合
     */
    public void suspendFlow(Object id) throws RemoteException, NoSuchBeanFlowIdException;
    
    /**
     * 指定された実行IDのBeanフローが一時停止命令を受けているかを判定する。<p>
     *
     * @param id 実行ID
     * @return 一時停止命令を受けている場合、true
     * @exception RemoteException リモート呼び出しに失敗した場合
     * @exception NoSuchBeanFlowIdException 指定された実行IDのBeanフローが存在しない場合
     */
    public boolean isSuspendFlow(Object id) throws RemoteException, NoSuchBeanFlowIdException;
    
    /**
     * 指定された実行IDのBeanフローが一時停止しているかを判定する。<p>
     *
     * @param id 実行ID
     * @return 一時停止している場合、true
     * @exception RemoteException リモート呼び出しに失敗した場合
     * @exception NoSuchBeanFlowIdException 指定された実行IDのBeanフローが存在しない場合
     */
    public boolean isSuspendedFlow(Object id) throws RemoteException, NoSuchBeanFlowIdException;
    
    /**
     * 指定された実行IDのBeanフローを再開させる。<p>
     *
     * @param id 実行ID
     * @exception RemoteException リモート呼び出しに失敗した場合
     * @exception NoSuchBeanFlowIdException 指定された実行IDのBeanフローが存在しない場合
     */
    public void resumeFlow(Object id) throws RemoteException, NoSuchBeanFlowIdException;
    
    /**
     * 指定された実行IDのBeanフローを停止させる。<p>
     *
     * @param id 実行ID
     * @exception RemoteException リモート呼び出しに失敗した場合
     */
    public void stopFlow(Object id) throws RemoteException;
    
    /**
     * 指定された実行IDのBeanフローが停止命令を受けているかを判定する。<p>
     *
     * @param id 実行ID
     * @return 停止命令を受けている場合、true
     * @exception RemoteException リモート呼び出しに失敗した場合
     */
    public boolean isStopFlow(Object id) throws RemoteException;
    
    /**
     * 指定された実行IDのBeanフローが停止しているかを判定する。<p>
     *
     * @param id 実行ID
     * @return 停止している場合、true
     * @exception RemoteException リモート呼び出しに失敗した場合
     */
    public boolean isStoppedFlow(Object id) throws RemoteException;
    
    /**
     * 指定された実行IDのBeanフローのフロー名を取得する。<p>
     *
     * @param id 実行ID
     * @return フロー名
     * @exception RemoteException リモート呼び出しに失敗した場合
     * @exception NoSuchBeanFlowIdException 指定された実行IDのBeanフローが存在しない場合
     */
    public String getFlowName(Object id) throws RemoteException, NoSuchBeanFlowIdException;
    
    /**
     * 指定された実行IDのBeanフローが現在実行しているフロー名を取得する。<p>
     *
     * @param id 実行ID
     * @return 現在実行しているフロー名
     * @exception RemoteException リモート呼び出しに失敗した場合
     * @exception NoSuchBeanFlowIdException 指定された実行IDのBeanフローが存在しない場合
     */
    public String getCurrentFlowName(Object id) throws RemoteException, NoSuchBeanFlowIdException;
    
    /**
     * 指定された実行IDのBeanフローが現在実行しているステップ名を取得する。<p>
     *
     * @param id 実行ID
     * @return 現在実行しているステップ名
     * @exception RemoteException リモート呼び出しに失敗した場合
     * @exception NoSuchBeanFlowIdException 指定された実行IDのBeanフローが存在しない場合
     */
    public String getCurrentStepName(Object id) throws RemoteException, NoSuchBeanFlowIdException;
    
    /**
     * 指定された実行IDのBeanフローのモニタを初期化する。<p>
     *
     * @param id 実行ID
     * @exception RemoteException リモート呼び出しに失敗した場合
     * @exception NoSuchBeanFlowIdException 指定された実行IDのBeanフローが存在しない場合
     */
    public void clearMonitor(Object id) throws RemoteException, NoSuchBeanFlowIdException;
    
    /**
     * 指定された実行IDのBeanフローの実行を取り消す。<p>
     *
     * @param id 実行ID
     * @exception RemoteException リモート呼び出しに失敗した場合
     */
    public void cancel(Object id) throws RemoteException;
    
    /**
     * 指定された実行IDのBeanフローを終了する。<p>
     *
     * @param id 実行ID
     * @exception RemoteException リモート呼び出しに失敗した場合
     */
    public void end(Object id) throws RemoteException;
}
