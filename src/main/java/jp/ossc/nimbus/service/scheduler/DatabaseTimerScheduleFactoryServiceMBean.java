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
package jp.ossc.nimbus.service.scheduler;

import jp.ossc.nimbus.core.*;

/**
 * {@link DatabaseTimerScheduleFactoryService}サービスのMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface DatabaseTimerScheduleFactoryServiceMBean extends ServiceBaseMBean{
    
    /**
     * {@link jp.ossc.nimbus.service.connection.ConnectionFactory ConnectionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name ConnectionFactoryサービスのサービス名
     */
    public void setConnectionFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.connection.ConnectionFactory ConnectionFactory}サービスのサービス名を取得する。<p>
     *
     * @return ConnectionFactoryサービスのサービス名
     */
    public ServiceName getConnectionFactoryServiceName();
    
    /**
     * スケジュールマスタを取得するSQLを設定する。<p>
     *
     * @param query スケジュールマスタを取得するSQL
     */
    public void setScheduleMasterQuery(String query);
    
    /**
     * スケジュールマスタを取得するSQLを取得する。<p>
     *
     * @return スケジュールマスタを取得するSQL
     */
    public String getScheduleMasterQuery();
    
    /**
     * スケジュールのキーとなるデータの列インデックスを設定する。<p>
     * スケジュールのキーとは、{@link ScheduleFactory#getSchedules(Object)}の引数となるキー文字列である。<br>
     * スケジュールのキーが必要ない場合は、指定する必要はない。<br>
     *
     * @param index 列インデックス
     */
    public void setScheduleKeyQueryIndex(int index);
    
    /**
     * スケジュールのキーとなるデータの列インデックスを取得する。<p>
     *
     * @return 列インデックス
     */
    public int getScheduleKeyQueryIndex();
    
    /**
     * スケジュール名となるデータの列インデックスを設定する。<p>
     * 設定しない場合、スケジュール名は、このサービスのサービス名に通番を振ったものになる。<br>
     * また、設定した場合は、その列の値がNULLになる事は許容しない。<br>
     *
     * @param index 列インデックス
     */
    public void setScheduleNameQueryIndex(int index);
    
    /**
     * スケジュール名となるデータの列インデックスを取得する。<p>
     *
     * @return 列インデックス
     */
    public int getScheduleNameQueryIndex();
    
    /**
     * {@link ScheduleTask}サービスのサービス名となるデータの列インデックスを設定する。<p>
     * 実行するタスクは、任意のScheduleTaskサービス、BeanFlow呼び出しタスク、IOC呼び出しタスクの3種類をサポートしており、この属性は、任意のScheduleTaskサービスを使用するためのものである。<br>
     *
     * @param index 列インデックス
     */
    public void setScheduleTaskServiceNameQueryIndex(int index);
    
    /**
     * {@link ScheduleTask}サービスのサービス名となるデータの列インデックスを取得する。<p>
     *
     * @return 列インデックス
     */
    public int getScheduleTaskServiceNameQueryIndex();
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory BeanFlowInvokerFactory}サービスのサービス名を設定する。<p>
     * 実行するタスクは、任意のScheduleTaskサービス、BeanFlow呼び出しタスク、IOC呼び出しタスクの3種類をサポートしており、この属性は、BeanFlow呼び出しタスクを使用するためのものである。<br>
     *
     * @param name BeanFlowInvokerFactoryサービスのサービス名
     */
    public void setScheduleBeanFlowInvokerFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory BeanFlowInvokerFactory}サービスのサービス名を設定する。<p>
     *
     * @return BeanFlowInvokerFactoryサービスのサービス名
     */
    public ServiceName getScheduleBeanFlowInvokerFactoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory BeanFlowInvokerFactory}サービスのサービス名となるデータの列インデックスを設定する。<p>
     * 実行するタスクは、任意のScheduleTaskサービス、BeanFlow呼び出しタスク、IOC呼び出しタスクの3種類をサポートしており、この属性は、BeanFlow呼び出しタスクを使用するためのものである。<br>
     *
     * @param index 列インデックス
     */
    public void setScheduleBeanFlowInvokerFactoryServiceNameQueryIndex(int index);
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory BeanFlowInvokerFactory}サービスのサービス名となるデータの列インデックスを設定する。<p>
     *
     * @return 列インデックス
     */
    public int getScheduleBeanFlowInvokerFactoryServiceNameQueryIndex();
    
    /**
     * BeanFlow呼び出しタスクで呼び出すBeanFlow名となるデータの列インデックスを設定する。<p>
     * BeanFlow呼び出しタスクを使用する場合は、必ず指定しなければならない。<br>
     *
     * @param index 列インデックス
     */
    public void setScheduleBeanFlowNameQueryIndex(int index);
    
    /**
     * BeanFlow呼び出しタスクで呼び出すBeanFlow名となるデータの列インデックスを取得する。<p>
     *
     * @return 列インデックス
     */
    public int getScheduleBeanFlowNameQueryIndex();
    
    /**
     * {@link jp.ossc.nimbus.service.ioccall.FacadeCaller FacadeCaller}サービスのサービス名を設定する。<p>
     * 実行するタスクは、任意のScheduleTaskサービス、BeanFlow呼び出しタスク、IOC呼び出しタスクの3種類をサポートしており、この属性は、IOC呼び出しタスクを使用するためのものである。<br>
     *
     * @param name FacadeCallerサービスのサービス名
     */
    public void setScheduleFacadeCallerServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.ioccall.FacadeCaller FacadeCaller}サービスのサービス名を取得する。<p>
     *
     * @return FacadeCallerサービスのサービス名
     */
    public ServiceName getScheduleFacadeCallerServiceName();
    
    /**
     * IOC呼び出しタスクで使用する{@link jp.ossc.nimbus.service.ioccall.FacadeCaller FacadeCaller}サービスのサービス名となるデータの列インデックスを設定する。<p>
     *
     * @param index 列インデックス
     */
    public void setScheduleFacadeCallerServiceNameQueryIndex(int index);
    
    /**
     * IOC呼び出しタスクで使用する{@link jp.ossc.nimbus.service.ioccall.FacadeCaller FacadeCaller}サービスのサービス名となるデータの列インデックスを取得する。<p>
     *
     * @return 列インデックス
     */
    public int getScheduleFacadeCallerServiceNameQueryIndex();
    
    /**
     * IOC呼び出しタスクで呼び出すBeanFlow名となるデータの列インデックスを設定する。<p>
     * IOC呼び出しタスクを使用する場合は、必ず指定しなければならない。<br>
     * また、複数のBeanFlowを呼び出す場合は、カンマ区切りでBeanFlow名を指定する。<br>
     *
     * @param index 列インデックス
     */
    public void setScheduleBeanFlowNamesQueryIndex(int index);
    
    /**
     * IOC呼び出しタスクで呼び出すBeanFlow名となるデータの列インデックスを取得する。<p>
     *
     * @return 列インデックス
     */
    public int getScheduleBeanFlowNamesQueryIndex();
    
    public void setScheduleIOCCallTypeQueryIndex(int index);
    public int getScheduleIOCCallTypeQueryIndex();
    
    public void setScheduleIOCCallType(String type);
    public String getScheduleIOCCallType();
    
    public void setScheduleStartTimeQueryIndex(int index);
    public int getScheduleStartTimeQueryIndex();
    
    public void setScheduleStartTimeFormat(String format);
    public String getScheduleStartTimeFormat();
    
    public void setScheduleExecuteWhenOverStartTimeQueryIndex(int index);
    public int getScheduleExecuteWhenOverStartTimeQueryIndex();
    
    public void setScheduleEndTimeQueryIndex(int index);
    public int getScheduleEndTimeQueryIndex();
    
    public void setScheduleEndTimeFormat(String format);
    public String getScheduleEndTimeFormat();
    
    public void setScheduleDelayQueryIndex(int index);
    public int getScheduleDelayQueryIndex();
    
    public void setSchedulePeriodQueryIndex(int index);
    public int getSchedulePeriodQueryIndex();
    
    public void setScheduleCountQueryIndex(int index);
    public int getScheduleCountQueryIndex();
    
    public void setScheduleFixedRateQueryIndex(int index);
    public int getScheduleFixedRateQueryIndex();
    
    public void setScheduleDependsScheduleNamesQueryIndex(int index);
    public int getScheduleDependsScheduleNamesQueryIndex();
    
    public void setScheduleDependencyTimeoutQueryIndex(int index);
    public int getScheduleDependencyTimeoutQueryIndex();
    
    public void setScheduleDependencyConfirmIntervalQueryIndex(int index);
    public int getScheduleDependencyConfirmIntervalQueryIndex();
    
    public void setScheduleErrorLogMessageIdQueryIndex(int index);
    public int getScheduleErrorLogMessageIdQueryIndex();
    
    public void setScheduleErrorLogMessageId(String id);
    public String getScheduleErrorLogMessageId();
    
    public void setScheduleTimeoutLogMessageIdQueryIndex(int index);
    public int getScheduleTimeoutLogMessageIdQueryIndex();
    
    public void setScheduleTimeoutLogMessageId(String id);
    public String getScheduleTimeoutLogMessageId();
    
    public void setScheduleJournalServiceName(ServiceName name);
    public ServiceName getScheduleJournalServiceName();
    
    public void setScheduleJournalServiceNameQueryIndex(int index);
    public int getScheduleJournalServiceNameQueryIndex();
    
    public void setScheduleQueueServiceName(ServiceName name);
    public ServiceName getScheduleQueueServiceName();
    
    public void setScheduleQueueServiceNameQueryIndex(int index);
    public int getScheduleQueueServiceNameQueryIndex();
    
    public void setScheduleGarbageQueueQueryIndex(int index);
    public int getScheduleGarbageQueueQueryIndex();
    
    public void setScheduleStateManagerServiceName(ServiceName name);
    public ServiceName getScheduleStateManagerServiceName();
    
    public void setScheduleStateManagerServiceNameQueryIndex(int index);
    public int getScheduleStateManagerServiceNameQueryIndex();
}