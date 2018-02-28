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
package jp.ossc.nimbus.service.server;

import jp.ossc.nimbus.core.*;

/**
 * {@link DefaultServerService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see DefaultServerService
 */
public interface DefaultServerServiceMBean extends ServiceBaseMBean{
    
    /**
     * サーバがバインドするホスト名を設定する。<p>
     * 設定しない場合は、ローカルホストにバインドする。
     *
     * @param name ホスト名
     */
    public void setHostName(String name);
    
    /**
     * サーバがバインドするホスト名を取得する。<p>
     *
     * @return ホスト名
     */
    public String getHostName();
    
    /**
     * サーバがバインドするポート番号を設定する。<p>
     * デフォルトは、10000。
     *
     * @param port ポート番号
     */
    public void setPort(int port);
    
    /**
     * サーバがバインドするポート番号を取得する。<p>
     *
     * @return ポート番号
     */
    public int getPort();
    
    /**
     * SO_REUSEADDR ソケットオプションを有効または無効にする。<p>
     * デフォルトは、trueで有効。
     *
     * @param isReuse 有効にする場合true
     */
    public void setReuseAddress(boolean isReuse);
    
    /**
     * SO_REUSEADDR ソケットオプションが有効か判定する。<p>
     *
     * @return trueの場合、有効
     */
    public boolean isReuseAddress();
    
    /**
     * 受信バッファバイト数を設定する。<p>
     *
     * @param size 受信バッファバイト数
     */
    public void setReceiveBufferSize(int size);
    
    /**
     * 受信バッファバイト数を取得する。<p>
     *
     * @return 受信バッファバイト数
     */
    public int getReceiveBufferSize();
    
    /**
     * 受信タイムアウト[ms]を設定する。<p>
     *
     * @param timeout 受信タイムアウト
     */
    public void setSoTimeout(int timeout);
    
    /**
     * 受信タイムアウト[ms]を取得する。<p>
     *
     * @return 受信タイムアウト
     */
    public int getSoTimeout();
    
    /**
     * {@link jp.ossc.nimbus.service.queue.QueueHandlerContainer QueueHandlerContainer}サービスのサービス名を設定する。<p>
     *
     * @param name QueueHandlerContainerサービスのサービス名
     */
    public void setQueueHandlerContainerServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.queue.QueueHandlerContainer QueueHandlerContainer}サービスのサービス名を取得する。<p>
     *
     * @return QueueHandlerContainerサービスのサービス名
     */
    public ServiceName getQueueHandlerContainerServiceName();
    
    /**
     * リクエストIDを発行する{@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を設定する。<p>
     *
     * @param name Sequenceサービスのサービス名
     */
    public void setSequenceServiceName(ServiceName name);
    
    /**
     * リクエストIDを発行する{@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を取得する。<p>
     *
     * @return Sequenceサービスのサービス名
     */
    public ServiceName getSequenceServiceName();
    
    /**
     * サーバソケットファクトリサービスのサービス名を設定する。<br>
     *
     * @param name サーバソケットファクトリサービスのサービス名
     */
    public void setServerSocketFactoryServiceName(ServiceName name);
    
    /**
     * サーバソケットファクトリサービスのサービス名を取得する。<br>
     *
     * @return サーバソケットファクトリサービスのサービス名
     */
    public ServiceName getServerSocketFactoryServiceName();
    
    /**
     * ソケットファクトリサービスのサービス名を設定する。<br>
     *
     * @param name ソケットファクトリサービスのサービス名
     */
    public void setSocketFactoryServiceName(ServiceName name);
    
    /**
     * ソケットファクトリサービスのサービス名を取得する。<br>
     *
     * @return ソケットファクトリサービスのサービス名
     */
    public ServiceName getSocketFactoryServiceName();
    
    /**
     * ソケット受付時の処理を行うかどうかを設定する。<p>
     * デフォルトは、falseで、ソケット受付後、クライアントからの書き込みを待つ。<br>
     *
     * @param isHandle 処理を行う場合、true
     */
    public void setHandleAccept(boolean isHandle);
    
    /**
     * ソケット受付時の処理を行うかどうかを判定する。<p>
     *
     * @return trueの場合、処理を行う
     */
    public boolean isHandleAccept();
}
