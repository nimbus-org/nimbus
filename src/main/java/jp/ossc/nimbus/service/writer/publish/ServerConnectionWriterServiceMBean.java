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

import jp.ossc.nimbus.core.*;

/**
 * {@link ServerConnectionWriterService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see ServerConnectionWriterService
 */
public interface ServerConnectionWriterServiceMBean extends ServiceBaseMBean{
    
    /**
     * 送信する{@link jp.ossc.nimbus.service.publish.ServerConnectionFactory ServerConnectionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name ServerConnectionFactoryサービスのサービス名
     */
    public void setServerConnectionFactoryServiceName(ServiceName name);
    
    /**
     * 送信する{@link jp.ossc.nimbus.service.publish.ServerConnectionFactory ServerConnectionFactory}サービスのサービス名を取得する。<p>
     *
     * @return ServerConnectionFactoryサービスのサービス名
     */
    public ServiceName getServerConnectionFactoryServiceName();
    
    /**
     * 送信するメッセージのサブジェクトを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得する際のキーを設定する。<p>
     *
     * @param key WritableRecordのキー
     */
    public void setSubjectKey(String key);
    
    /**
     * 送信するメッセージのサブジェクトを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得する際のキーを取得する。<p>
     *
     * @return WritableRecordのキー
     */
    public String getSubjectKey();
    
    /**
     * 送信するメッセージのサブジェクトを設定する。<p>
     * デフォルトは、サービス名。<br>
     *
     * @param subject サブジェクト
     */
    public void setSubject(String subject);
    
    /**
     * 送信するメッセージのサブジェクトを取得する。<p>
     *
     * @return サブジェクト
     */
    public String getSubject();
    
    /**
     * 送信するメッセージのキーを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得する際のキーを設定する。<p>
     *
     * @param key WritableRecordのキー
     */
    public void setKeyKey(String key);
    
    /**
     * 送信するメッセージのキーを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得する際のキーを取得する。<p>
     *
     * @return WritableRecordのキー
     */
    public String getKeyKey();
    
    /**
     * 送信するメッセージのキーを設定する。<p>
     *
     * @param key キー
     */
    public void setKey(String key);
    
    /**
     * 送信するメッセージのキーを取得する。<p>
     *
     * @return キー
     */
    public String getKey();
    
    /**
     * 非同期送信するかどうかを設定する。<p>
     * デフォルトは、falseで、同期送信。<br>
     *
     * @param isAsynch 非同期送信する場合、true
     */
    public void setAsynchSend(boolean isAsynch);
    
    /**
     * 非同期送信するかどうかを判定する。<p>
     *
     * @return trueの場合、非同期送信する
     */
    public boolean isAsynchSend();
}
