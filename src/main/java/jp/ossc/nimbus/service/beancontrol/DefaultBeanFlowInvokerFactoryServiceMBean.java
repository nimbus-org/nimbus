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

import jp.ossc.nimbus.core.*;
import java.util.*;

/**
 * {@link DefaultBeanFlowInvokerFactoryService}のMBeanインタフェース。<p>
 * 
 * @author H.Nakano
 */
public interface DefaultBeanFlowInvokerFactoryServiceMBean
 extends ServiceBaseMBean{
    
    /** 更新時刻フォーマット文字列 */
    public static final String TIME_FORMAT = "yyyy.MM.dd HH:mm:ss";
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.resource.ResourceManagerFactory ResourceManagerFactory}サービスのサービス名を設定する。<p>
     *
     * @param name ResourceManagerFactoryサービスのサービス名
     */
    public void setResourceManagerFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.resource.ResourceManagerFactory ResourceManagerFactory}サービスのサービス名を取得する。<p>
     *
     * @return ResourceManagerFactoryサービスのサービス名
     */
    public ServiceName getResourceManagerFactoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を設定する。<p>
     * 
     * @param name Contextサービスのサービス名
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を取得する。<p>
     * 
     * @return Contextサービスのサービス名
     */
    public ServiceName getThreadContextServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.log.Logger Logger}サービスのサービス名を設定する。<p>
     * 
     * @param name Loggerサービスのサービス名
     */
    public void setLogServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.log.Logger Logger}サービスのサービス名を取得する。<p>
     * 
     * @return Loggerサービスのサービス名
     */
    public ServiceName getLogServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.journal.Journal Journal}サービスのサービス名を設定する。<p>
     *
     * @param name Journalサービスのサービス名
     */
    public void setJournalServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.journal.Journal Journal}サービスのサービス名を取得する。<p>
     *
     * @return Journalサービスのサービス名
     */
    public ServiceName getJournalServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を設定する。<p>
     *
     * @param name EditorFinderサービスのサービス名
     */
    public void setEditorFinderServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を取得する。<p>
     *
     * @return EditorFinderサービスのサービス名
     */
    public ServiceName getEditorFinderServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}サービスのサービス名を設定する。<p>
     * 
     * @param name Interpreterサービスのサービス名
     */
    public void setInterpreterServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}サービスのサービス名を取得する。<p>
     * 
     * @return Interpreterサービスのサービス名
     */
    public ServiceName getInterpreterServiceName();
    
    /**
     * test属性評価用の{@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}サービスのサービス名を設定する。<p>
     * 
     * @param name Interpreterサービスのサービス名
     */
    public void setTestInterpreterServiceName(ServiceName name);
    
    /**
     * test属性評価用の{@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}サービスのサービス名を取得する。<p>
     * 
     * @return Interpreterサービスのサービス名
     */
    public ServiceName getTestInterpreterServiceName();
    
    /**
     * expression要素評価用の{@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}サービスのサービス名を設定する。<p>
     * 
     * @param name Interpreterサービスのサービス名
     */
    public void setExpressionInterpreterServiceName(ServiceName name);
    
    /**
     * expression要素評価用の{@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}サービスのサービス名を取得する。<p>
     * 
     * @return Interpreterサービスのサービス名
     */
    public ServiceName getExpressionInterpreterServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.template.TemplateEngine TemplateEngine}サービスのサービス名を設定する。<p>
     * 
     * @param name TemplateEngineサービスのサービス名
     */
    public void setTemplateEngineServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.template.TemplateEngine TemplateEngine}サービスのサービス名を取得する。<p>
     * 
     * @return TemplateEngineサービスのサービス名
     */
    public ServiceName getTemplateEngineServiceName();
    
    /**
     * 実行中のBeanFlowを管理するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isManage 管理する場合は、true
     */
    public void setManageExecBeanFlow(boolean isManage);
    
    /**
     * 実行中のBeanFlowを管理するかどうかを判定する。<p>
     *
     * @return trueの場合、管理する
     */
    public boolean isManageExecBeanFlow();
    
    /**
     * {@link BeanFlowInvokerAccess}インタフェースの実装クラスを設定する。<p>
     * デフォルトは、{@link BeanFlowInvokerAccessImpl}。<br>
     *
     * @param clazz BeanFlowInvokerAccessインタフェースの実装クラス
     */
    public void setBeanFlowInvokerAccessClass(Class clazz);
    
    /**
     * {@link BeanFlowInvokerAccess}インタフェースの実装クラスを取得する。<p>
     *
     * @return BeanFlowInvokerAccessインタフェースの実装クラス
     */
    public Class getBeanFlowInvokerAccessClass();
    
    /**
     * フロー定義XMLを、DTDで検証するかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param validate 検証する場合、true
     */
    public void setValidate(boolean validate);
    
    /**
     * フロー定義XMLを、DTDで検証するかどうかを判定する。<p>
     *
     * @return trueの場合、検証する
     */
    public boolean isValidate();
    
    /**
     * {@link jp.ossc.nimbus.service.aop.InterceptorChainFactory InterceptorChainFactory}サービスのサービス名を設定する。<p>
     *
     * @param name InterceptorChainFactoryサービスのサービス名
     */
    public void setInterceptorChainFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.aop.InterceptorChainFactory InterceptorChainFactory}サービスのサービス名を取得する。<p>
     *
     * @return InterceptorChainFactoryサービスのサービス名
     */
    public ServiceName getInterceptorChainFactoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.transaction.TransactionManagerFactory TransactionManagerFactory}サービスのサービス名を設定する。<p>
     * 設定しない場合は、{@link jp.ossc.nimbus.service.transaction.JndiTransactionManagerFactoryService JndiTransactionManagerFactoryService}が適用される。<br>
     *
     * @param name TransactionManagerFactoryサービスのサービス名
     */
    public void setTransactionManagerFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.transaction.TransactionManagerFactory TransactionManagerFactory}サービスのサービス名を取得する。<p>
     *
     * @return TransactionManagerFactoryサービスのサービス名
     */
    public ServiceName getTransactionManagerFactoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.queue.QueueHandlerContainer QueueHandlerContainer}サービスのサービス名を設定する。<p>
     * 設定しない場合は、{@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker#invokeAsynchFlow(Object, jp.ossc.nimbus.service.beancontrol.BeanFlowMonitor, boolean, int)}をサポートしない。<br>
     *
     * @param name QueueHandlerContainerサービスのサービス名
     */
    public void setAsynchInvokeQueueHandlerContainerServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.queue.QueueHandlerContainer QueueHandlerContainer}サービスのサービス名を取得する。<p>
     *
     * @return QueueHandlerContainerサービスのサービス名
     */
    public ServiceName getAsynchInvokeQueueHandlerContainerServiceName();
    
    /**
     * フロー定義XMLの存在するディレクトリを設定する。<p>
     * 
     * @param dirPaths フロー定義XMLの存在するディレクトリパス配列
     */
    public void setDirPaths(String dirPaths[]);
    
    /**
     * フロー定義ファイルの存在するディレクトリを取得する。<p>
     * 
     * @return フロー定義ファイルのディレクトリパス配列
     */
    public String[] getDirPaths();
    
    /**
     * フロー定義XMLのパスを設定する。<p>
     * 
     * @param paths フロー定義XMLの存在するパス配列
     */
    public void setPaths(String paths[]);
    
    /**
     * フロー定義ファイルのパスを取得する。<p>
     * 
     * @return フロー定義ファイルのパス配列
     */
    public String[] getPaths();
    
    /**
     * ハンドリング中にエラーが発生した場合に出力するログのメッセージIDを設定する。<p>
     * デフォルトは、nullで、ログを出力しない。<br>
     *
     * @param id ログのメッセージID
     */
    public void setAsynchInvokeErrorLogMessageId(String id);
    
    /**
     * ハンドリング中にエラーが発生した場合に出力するログのメッセージIDを取得する。<p>
     * 
     * @return ログのメッセージID
     */
    public String getAsynchInvokeErrorLogMessageId();
    
    /**
     * ハンドリング中にエラーが発生し、規定のリトライ回数を越えた場合に出力するログのメッセージIDを設定する。<p>
     * デフォルトは、nullで、ログを出力しない。<br>
     *
     * @param id ログのメッセージID
     */
    public void setAsynchInvokeRetryOverErrorLogMessageId(String id);
    
    /**
     * ハンドリング中にエラーが発生し、規定のリトライ回数を越えた場合に出力するログのメッセージIDを取得する。<p>
     * 
     * @return ログのメッセージID
     */
    public String getAsynchInvokeRetryOverErrorLogMessageId();
    
    /**
     * ジャーナルの出力サイズを記録する{@link jp.ossc.nimbus.service.performance.PerformanceRecorder PerformanceRecorder}サービスのサービス名を設定する。<p>
     *
     * @param name PerformanceRecorderサービスのサービス名
     */
    public void setJournalPerformanceRecorderServiceName(ServiceName name);
    
    /**
     * ジャーナルの出力サイズを記録する{@link jp.ossc.nimbus.service.performance.PerformanceRecorder PerformanceRecorder}サービスのサービス名を取得する。<p>
     *
     * @return PerformanceRecorderサービスのサービス名
     */
    public ServiceName getJournalPerformanceRecorderServiceName();
    
    /**
     * ジャーナルの出力サイズの統計情報を収集するかどうかを設定する。<p>
     * デフォルトは、falseで収集しない。<br>
     *
     * @param isCollect 収集する場合true
     */
    public void setCollectJournalMetrics(boolean isCollect);
    
    /**
     * ジャーナルの出力サイズの統計情報を収集するかどうかを判定する。<p>
     *
     * @return trueの場合、収集する
     */
    public boolean isCollectJournalMetrics();
    
    /**
     * ジャーナルの出力サイズの統計情報で、出力回数を出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputJournalMetricsCount(boolean isOutput);
    
    /**
     * ジャーナルの出力サイズの統計情報で、出力回数を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputJournalMetricsCount();
    
    /**
     * ジャーナルの出力サイズの統計情報で、最終時刻を出力するかどうかを設定する。<p>
     * デフォルトは、falseで出力しない。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputJournalMetricsLastTime(boolean isOutput);
    
    /**
     * ジャーナルの出力サイズの統計情報で、最終時刻を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputJournalMetricsLastTime();
    
    /**
     * ジャーナルの出力サイズの統計情報で、最小サイズを出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputJournalMetricsBestSize(boolean isOutput);
    
    /**
     * ジャーナルの出力サイズの統計情報で、最小サイズを出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputJournalMetricsBestSize();
    
    /**
     * ジャーナルの出力サイズの統計情報で、最小サイズ時刻を出力するかどうかを設定する。<p>
     * デフォルトは、falseで出力しない。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputJournalMetricsBestSizeTime(boolean isOutput);
    
    /**
     * ジャーナルの出力サイズの統計情報で、最小サイズ時刻を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputJournalMetricsBestSizeTime();
    
    /**
     * ジャーナルの出力サイズの統計情報で、最大サイズを出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputJournalMetricsWorstSize(boolean isOutput);
    
    /**
     * ジャーナルの出力サイズの統計情報で、最大サイズを出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputJournalMetricsWorstSize();
    
    /**
     * ジャーナルの出力サイズの統計情報で、最大サイズ時刻を出力するかどうかを設定する。<p>
     * デフォルトは、falseで出力しない。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputJournalMetricsWorstSizeTime(boolean isOutput);
    
    /**
     * ジャーナルの出力サイズの統計情報で、最大サイズ時刻を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputJournalMetricsWorstSizeTime();
    
    /**
     * ジャーナルの出力サイズの統計情報で、平均サイズを出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputJournalMetricsAverageSize(boolean isOutput);
    
    /**
     * ジャーナルの出力サイズの統計情報で、平均サイズ時刻を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputJournalMetricsAverageSize();
    
    /**
     * ジャーナルの出力サイズの統計情報で、統計情報出力時刻を出力するかどうかを設定する。<p>
     * デフォルトは、falseで出力しない。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputJournalMetricsTimestamp(boolean isOutput);
    
    /**
     * ジャーナルの出力サイズの統計情報で、統計情報出力時刻を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputJournalMetricsTimestamp();
    
    /**
     * JTAを使用する場合の、デフォルトのトランザクションタイムアウト[s]を設定する。<p>
     * デフォルトは、nullで、タイムアウトを設定しない。<br>
     *
     * @param timeout デフォルトのトランザクションタイムアウト[s]
     */
    public void setDefaultTransactionTimeout(Integer timeout);
    
    /**
     * JTAを使用する場合の、デフォルトのトランザクションタイムアウト[s]を取得する。<p>
     *
     * @return デフォルトのトランザクションタイムアウト[s]
     */
    public Integer getDefaultTransactionTimeout();
    
    
    /**
     * ジャーナルの出力サイズの統計情報を初期化する。<p>
     */
    public void resetJournalMetrics();
    
    /**
     * ジャーナルの出力サイズの統計情報を表示する。<p>
     *
     * @return 統計情報文字列
     */
    public String displayJournalMetricsInfo();
    
    /**
     * フロー定義XMLを再読み込みする。<p>
     */
    public void reload();
    
    /**
     * 指定されたフローをサスペンドする。<p>
     * フロー実行インスタンスは返却するが、実行するとサスペンド状態で待たされる。<br>
     *
     * @param key フローキー
     */
    public void suspend(String key);
    
    /**
     * 指定されたフローをサスペンド解除する。<p>
     * 
     * @param key フローキー
     */
    public void resume(String key);
    
    /**
     * 指定されたフローを強制終了する。<p>
     * 
     * @param key フローキー
     */
    public void stop(String key);
    
    /**
     * 指定されたフローの実行インスタンスを取得不可能にする。<p>
     * 
     * @param key フローキー
     */
    public void ignore(String key);
    
    /**
     * 無効対象にしたフローに対して復元し有効にする。<p>
     * 
     * @param key フローキー
     */
    public void unIgnore(String key);
    
    /**
     * サスペンド中のフローキーのリストを取得する。<p>
     * 
     * @return サスペンド中のフローキーのリスト
     */
    public ArrayList getSuspendList();
    
    /**
     * 無効対象にしたフローキーのリストを取得する。<p>
     * 
     * @return 無効対象にしたフローキーのリスト
     */
    public ArrayList getIgnoreList();
    
    /**
     * 実行中フローのフローキーのリストを取得する。<p>
     * 
     * @return 実行中フローのフローキーのリスト
     */
    public ArrayList getExecFlowList();
    
    /**
     * フロー定義XMLを再読み込みする時刻を設定する。<p>
     * 
     * @param time フロー定義XMLを再読み込みする時刻。yyyy.MM.dd hh:mm:ss
     */
    public void setRefreshTime(String time);
    
    /**
     * フロー定義XMLを読み込んだ最終時刻を取得する。<p>
     * 
     * @return フロー定義XMLを読み込んだ最終時刻。yyyy.MM.dd hh:mm:ss
     */
    public String getLastRrefreshTime();
    
    /**
     * フロー定義XMLを次回再読み込みする時刻を取得する。<p>
     * 
     * @return フロー定義XMLを次回再読み込みする時刻
     */
    public String getNextRefreshTime();
}
