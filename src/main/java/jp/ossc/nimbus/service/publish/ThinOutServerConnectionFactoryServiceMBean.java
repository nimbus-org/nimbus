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
package jp.ossc.nimbus.service.publish;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link ThinOutServerConnectionFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see ThinOutServerConnectionFactoryService
 */
public interface ThinOutServerConnectionFactoryServiceMBean extends ServiceBaseMBean{
    
    /**
     * {@link ServerConnectionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name ServerConnectionFactoryサービスのサービス名
     */
    public void setServerConnectionFactoryServiceName(ServiceName name);
    
    /**
     * {@link ServerConnectionFactory}サービスのサービス名を取得する。<p>
     *
     * @return ServerConnectionFactoryサービスのサービス名
     */
    public ServiceName getServerConnectionFactoryServiceName();
    
    /**
     * {@link ThinOutFilter}サービスのサービス名配列を設定する。<p>
     *
     * @param name ThinOutFilterサービスのサービス名配列
     */
    public void setThinOutFilterServiceNames(ServiceName[] name);
    
    /**
     * {@link ThinOutFilter}サービスのサービス名配列を取得する。<p>
     *
     * @return ThinOutFilterサービスのサービス名配列
     */
    public ServiceName[] getThinOutFilterServiceNames();
    
    /**
     * 間引き対象となったメッセージが最後のメッセージだった場合に送信するための監視間隔[ms]を設定する。<p>
     * デフォルトは、1秒。<br>
     *
     * @param interval 監視間隔[ms]
     */
    public void setThinOutTimeoutCheckInterval(long interval);
    
    /**
     * 間引き対象となったメッセージが最後のメッセージだった場合に送信するための監視間隔[ms]を取得する。<p>
     *
     * @return 監視間隔[ms]
     */
    public long getThinOutTimeoutCheckInterval();
    
    /**
     * 間引き対象となったメッセージが最後のメッセージだった場合に送信するための、間引きタイムアウト[ms]を設定する。<p>
     * デフォルトは、3秒。<br>
     *
     * @param timeout 間引きタイムアウト[ms]
     */
    public void setThinOutTimeout(long timeout);
    
    /**
     * 間引き対象となったメッセージが最後のメッセージだった場合に送信するための、間引きタイムアウト[ms]を取得する。<p>
     *
     * @return 間引きタイムアウト[ms]
     */
    public long getThinOutTimeout();
}
