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
package jp.ossc.nimbus.service.writer.publish;

import java.util.Map;

import jp.ossc.nimbus.core.*;

/**
 * {@link ClientConnectionWriterService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see ClientConnectionWriterService
 */
public interface ClientConnectionWriterServiceMBean extends ServiceBaseMBean{
    
    /**
     * 受信する{@link jp.ossc.nimbus.service.publish.ClientConnectionFactory ClientConnectionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name ClientConnectionFactoryサービスのサービス名
     */
    public void setClientConnectionFactoryServiceName(ServiceName name);
    
    /**
     * 受信する{@link jp.ossc.nimbus.service.publish.ClientConnectionFactory ClientConnectionFactory}サービスのサービス名を取得する。<p>
     *
     * @return ClientConnectionFactoryサービスのサービス名
     */
    public ServiceName getClientConnectionFactoryServiceName();
    
    /**
     * 受信する{@link jp.ossc.nimbus.service.publish.MessageReceiver MessageReceiver}サービスのサービス名を設定する。<p>
     *
     * @param name MessageReceiverサービスのサービス名
     */
    public void setMessageReceiverServiceName(ServiceName name);
    
    /**
     * 受信する{@link jp.ossc.nimbus.service.publish.MessageReceiver MessageReceiver}サービスのサービス名を取得する。<p>
     *
     * @return MessageReceiverサービスのサービス名
     */
    public ServiceName getMessageReceiverServiceName();
    
    /**
     * 受信するサブジェクトとキーを設定する。<p>
     *
     * @param subject サブジェクト
     * @param keys キー配列
     */
    public void setSubject(String subject, String[] keys);
    
    /**
     * 受信するサブジェクトとキーのマップを取得する。<p>
     *
     * return サブジェクトとキー配列のマップ
     */
    public Map getSubjectMap();
    
    /**
     * 出力する{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービスのサービス名を設定する。<p>
     *
     * @param name MessageWriterサービスのサービス名
     */
    public void setMessageWriterServiceName(ServiceName name);
    
    /**
     * 出力する{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービスのサービス名を取得する。<p>
     *
     * @return MessageWriterサービスのサービス名
     */
    public ServiceName getMessageWriterServiceName();
    
    /**
     * 受信したメッセージのサブジェクトとキーを設定する{@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を設定する。<p>
     *
     * @param name Contextサービスのサービス名
     */
    public void setContextServiceName(ServiceName name);
    
    /**
     * 受信したメッセージのサブジェクトとキーを設定する{@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を取得する。<p>
     *
     * @return Contextサービスのサービス名
     */
    public ServiceName getContextServiceName();
    
    /**
     * 受信したメッセージのサブジェクトを{@link jp.ossc.nimbus.service.context.Context Context}サービスに設定する時のキーを設定する。<p>
     * 設定しない場合は、サブジェクトをContextサービスに設定しない。<br>
     *
     * @param key サブジェクトをContextサービスに設定する時のキー
     */
    public void setSubjectContextKey(String key);
    
    /**
     * 受信したメッセージのサブジェクトを{@link jp.ossc.nimbus.service.context.Context Context}サービスに設定する時のキーを取得する。<p>
     *
     * @return サブジェクトをContextサービスに設定する時のキー
     */
    public String getSubjectContextKey();
    
    /**
     * 受信したメッセージのキーを{@link jp.ossc.nimbus.service.context.Context Context}サービスに設定する時のキーを設定する。<p>
     * 設定しない場合は、キーをContextサービスに設定しない。<br>
     *
     * @param key キーをContextサービスに設定する時のキー
     */
    public void setKeyContextKey(String key);
    
    /**
     * 受信したメッセージのキーを{@link jp.ossc.nimbus.service.context.Context Context}サービスに設定する時のキーを取得する。<p>
     *
     * @return キーをContextサービスに設定する時のキー
     */
    public String getKeyContextKey();
}
