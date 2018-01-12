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
package jp.ossc.nimbus.service.codemaster;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import jp.ossc.nimbus.core.*;

/**
 * {@link CodeMasterService}のMBeanインタフェース。<p>
 *
 * @author S.Yoshihara
 */
public interface CodeMasterServiceMBean extends ServiceBaseMBean, CodeMasterFinder {

    /**
     * マスタ名配列を設定する。<p>
     *
     * @param names マスタ名の配列
     */
    public void setMasterNames(String[] names);

    /**
     * マスタ名配列を取得する。<p>
     *
     * @return マスタ名の配列
     */
    public String[] getMasterNames();

    /**
     * マスタ取得のためにIOC呼び出しを行うための{@link jp.ossc.nimbus.service.ioccall.FacadeCaller FacadeCaller}サービスのサービス名を設定する。<p>
     * この属性か{@link #setBeanFlowInvokerFactoryServiceName(ServiceName)}のどちらか一方は、必ず設定しなければならない。<br>
     *
     * @param name FacadeCallerサービスのサービス名
     */
    public void setFacadeCallerServiceName(ServiceName name);

    /**
     * マスタ取得のためにIOC呼び出しを行うための{@link jp.ossc.nimbus.service.ioccall.FacadeCaller FacadeCaller}サービスのサービス名を取得する。<p>
     *
     * @return FacadeCallerサービスのサービス名
     */
    public ServiceName getFacadeCallerServiceName();

    /**
     * {@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を設定する。<p>
     * マスタ取得のためにIOC呼び出しを行う場合に、{@link jp.ossc.nimbus.ioc.FacadeValue FacadeValue}のヘッダにリクエストIDを付加するのに使用する。<br>
     *
     * @param name Sequenceサービスのサービス名
     */
    public void setSequenceServiceName(ServiceName name);

    /**
     * {@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を取得する。<p>
     *
     * @return Sequenceサービスのサービス名
     */
    public ServiceName getSequenceServiceName();

    /**
     * ユーザIDを設定する。<p>
     * マスタ取得のためにIOC呼び出しを行う場合に、{@link jp.ossc.nimbus.ioc.FacadeValue FacadeValue}のヘッダにユーザIDを付加するのに使用する。<br>
     *
     * @param id ユーザID
     */
    public void setUserId(String id);

    /**
     * ユーザIDを取得する。<p>
     *
     * @return ユーザID
     */
    public String getUserId();

    /**
     * マスタ取得のために{@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker BeanFlowInvoker}呼び出しを行うための{@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory BeanFlowInvokerFactory}サービスのサービス名を設定する。<p>
     * この属性か{@link #setFacadeCallerServiceName(ServiceName)}のどちらか一方は、必ず設定しなければならない。<br>
     *
     * @param name BeanFlowInvokerFactoryサービスのサービス名
     */
    public void setBeanFlowInvokerFactoryServiceName(ServiceName name);

    /**
     * マスタ取得のために{@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker BeanFlowInvoker}呼び出しを行うための{@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory BeanFlowInvokerFactory}サービスのサービス名を取得する。<p>
     *
     * @return BeanFlowInvokerFactoryサービスのサービス名
     */
    public ServiceName getBeanFlowInvokerFactoryServiceName();

    /**
     * JMSトピックを受信するjavax.jms.TopicSubscriberを生成する{@link jp.ossc.nimbus.service.jms.JMSMessageConsumerFactory}サービスのサービス名を設定する。<p>
     * JMSトピックでのマスタ更新を行う場合に使用する。JMSトピックでのマスタ更新を行わない場合は、設定する必要はない。<br>
     *
     * @param name JMSMessageConsumerFactoryサービスのサービス名
     */
    public void setJMSTopicSubscriberFactoryServiceName(ServiceName name);

    /**
     * JMSトピックを受信するjavax.jms.TopicSubscriberを生成する{@link jp.ossc.nimbus.service.jms.JMSMessageConsumerFactory}サービスのサービス名を取得する。<p>
     *
     * @return JMSMessageConsumerFactoryサービスのサービス名
     */
    public ServiceName getJMSTopicSubscriberFactoryServiceName();

    /**
     * JMSトピックを受信するjavax.jms.TopicSubscriberを生成する{@link jp.ossc.nimbus.service.jms.JMSMessageConsumerFactory}サービスのサービス名を設定する。<p>
     * JMSトピックでのマスタ更新を行う場合に使用する。JMSトピックでのマスタ更新を行わない場合は、設定する必要はない。<br>
     *
     * @param names JMSMessageConsumerFactoryサービスのサービス名配列
     */
    public void setJMSTopicSubscriberFactoryServiceNames(ServiceName[] names);

    /**
     * JMSトピックを受信するjavax.jms.TopicSubscriberを生成する{@link jp.ossc.nimbus.service.jms.JMSMessageConsumerFactory}サービスのサービス名を取得する。<p>
     *
     * @return JMSMessageConsumerFactoryサービスのサービス名配列
     */
    public ServiceName[] getJMSTopicSubscriberFactoryServiceNames();

    /**
     * {@link jp.ossc.nimbus.service.publish.MessageReceiver MessageReceiver}サービスから{@link jp.ossc.nimbus.service.publish.Message Message}を受信する際のサブジェクト名を設定する。<p>
     *
     * @param subject サブジェクト名
     */
    public void setSubjects(String[] subject);

    /**
     * {@link jp.ossc.nimbus.service.publish.MessageReceiver MessageReceiver}サービスから{@link jp.ossc.nimbus.service.publish.Message Message}を受信する際のサブジェクト名を取得する。<p>
     *
     * @return サブジェクト名
     */
    public String[] getSubjects();

    /**
     * {@link jp.ossc.nimbus.service.publish.Message Message}を受信する{@link jp.ossc.nimbus.service.publish.MessageReceiver MessageReceiver}サービスのサービス名を設定する。<p>
     * Messageでのマスタ更新を行う場合に使用する。Messageでのマスタ更新を行わない場合は、設定する必要はない。<br>
     *
     * @param name MessageReceiverサービスのサービス名
     */
    public void setMessageReceiverServiceName(ServiceName name);

    /**
     * {@link jp.ossc.nimbus.service.publish.Message Message}を受信する{@link jp.ossc.nimbus.service.publish.MessageReceiver MessageReceiver}サービスのサービス名を取得する。<p>
     *
     * @return MessageReceiverサービスのサービス名
     */
    public ServiceName getMessageReceiverServiceName();

    /**
     * JMSトピックでマスタ更新を行う場合のマスタ名と通知マスタ名のマッピングを設定する。<p>
     * マスタ名と通知マスタ名が同じ場合は、設定する必要はない。<br>
     *
     * @param mapping マスタ名と通知マスタ名のマッピング。通知マスタ名=マスタ名1,マスタ名2,...
     */
    public void setNotifyMasterNameMapping(Properties mapping);

    /**
     * JMSトピックでマスタ更新を行う場合のマスタ名と通知マスタ名のマッピングを取得する。<p>
     *
     * @return マスタ名と通知マスタ名のマッピング。通知マスタ名=マスタ名1,マスタ名2,...
     */
    public Properties getNotifyMasterNameMapping();

    /**
     * サービスの開始時に取得するマスタのマスタ名配列を設定する。<p>
     * 指定しない場合は、全てのマスタがサービスの開始時に取得される。<br>
     *
     * @param names マスタ名配列
     */
    public void setStartMasterNames(String[] names);

    /**
     * サービスの開始時に取得するマスタのマスタ名配列を取得する。<p>
     *
     * @return マスタ名配列
     */
    public String[] getStartMasterNames();

    /**
     * サービスの開始時に取得しないマスタのマスタ名配列を設定する。<p>
     * 指定しない場合は、全てのマスタがサービスの開始時に取得される。<br>
     *
     * @param names マスタ名配列
     */
    public void setNotStartMasterNames(String[] names);

    /**
     * サービスの開始時に取得しないマスタのマスタ名配列を取得する。<p>
     *
     * @return マスタ名配列
     */
    public String[] getNotStartMasterNames();

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

    /**
     * サービスの開始時に取得するマスタのマスタ名と入力オブジェクトのマッピングを設定する。<p>
     * 入力が必要ないマスタの場合は、マッピングを設定する必要はない。<br>
     *
     * @param map マスタ名と入力オブジェクトのマップ
     */
    public void setStartMasterInputMap(Map map);

    /**
     * サービスの開始時に取得するマスタのマスタ名と入力オブジェクトのマッピングを取得する。<p>
     *
     * @return マスタ名と入力オブジェクトのマップ
     */
    public Map getStartMasterInputMap();

    /**
     * マスタをファイルとして永続化するディレクトリを設定する。<p>
     *
     * @param dir 永続化するディレクトリ
     */
    public void setPersistDir(String dir);

    /**
     * マスタをファイルとして永続化するディレクトリを取得する。<p>
     *
     * @return 永続化するディレクトリ
     */
    public String getPersistDir();

    /**
     * サービスの開始時にファイルに永続化されているマスタを読み込むかどうかを設定する。<p>
     * ファイルから読み込まれた場合は、マスタ取得業務フローは実行しない。<br>
     * デフォルトは、falseで読み込まない。<br>
     *
     * @param isLoad 開始時に読み込む場合は、true
     */
    public void setLoadOnStart(boolean isLoad);

    /**
     * ファイルに永続化されているマスタをサービスの開始時に読み込むかどうかを判定する。<p>
     *
     * @return trueの場合、開始時に読み込む
     */
    public boolean isLoadOnStart();

    /**
     * サービスの停止時にマスタをファイルに永続化するかどうかを設定する。<p>
     * デフォルトは、falseで永続化しない。<br>
     *
     * @param isSave 停止時に永続化する場合は、true
     */
    public void setSaveOnStop(boolean isSave);

    /**
     * サービスの停止時にマスタをファイルに永続化するかどうかを判定する。<p>
     *
     * @return trueの場合は、停止時に永続化する
     */
    public boolean isSaveOnStop();

    /**
     * 全てのマスタをファイルに永続化する。<p>
     *
     * @exception IOException 永続化に失敗した場合
     */
    public void save() throws IOException;

    /**
     * 指定したマスタをファイルに永続化する。<p>
     *
     * @param key マスタのキー
     * @return 指定したマスタが存在しなかった場合false
     * @exception IOException 永続化に失敗した場合
     */
    public boolean save(String key) throws IOException;

    /**
     * 全てのマスタを永続化されたファイルから読み込む。<p>
     *
     * @exception IOException 読み込みに失敗した場合
     * @exception ClassNotFoundException 読み込みんだマスタのクラスが存在しない場合
     */
    public void load() throws IOException, ClassNotFoundException;

    /**
     * 指定されたマスタを永続化されたファイルから読み込む。<p>
     *
     * @param key マスタのキー
     * @return 指定したマスタが存在しなかった場合false
     * @exception IOException 読み込みに失敗した場合
     * @exception ClassNotFoundException 読み込みんだマスタのクラスが存在しない場合
     */
    public boolean load(String key) throws IOException, ClassNotFoundException;

    /**
     * 永続化ファイルを全て削除する。<p>
     *
     * @exception IOException 永続化ファイルの削除に失敗した場合
     */
    public void clearPersist() throws IOException;
}
