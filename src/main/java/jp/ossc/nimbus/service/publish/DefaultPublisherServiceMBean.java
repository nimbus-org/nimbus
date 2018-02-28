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
 * パブリッシャーサービスの管理JMXインターフェイス<p>
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public interface DefaultPublisherServiceMBean extends ServiceBaseMBean{

    public void setProtocolServiceName(ServiceName name);

    public ServiceName getProtocolServiceName();

    public ServiceName getPublishContainerFactoryServiceName();

    public void setPublishContainerFactoryServiceName(ServiceName name);

    public void setJMSMessageConsumerFactoryServiceNames(ServiceName[] names);

    public ServiceName[] getJMSMessageConsumerFactoryServiceNames();

    public void setQueueServiceNames(ServiceName[] names);

    public ServiceName[] getQueueServiceNames();

    public void setMessageFilterServiceNames(ServiceName[] names);

    public ServiceName[] getMessageFilterServiceNames();

    public void setServerBindAddress(String address);

    public String getServerBindAddress();

    public void setPort(int port);

    public int getPort();

    public void setContainerNum(int num);

    public int getContainerNum();

    public int getServantNum();

    public boolean isServerSocketChannelBlocking();

    public void setServerSocketChannelBlocking(boolean isBlocking);

    public boolean isSocketChannelBlocking();

    public void setSocketChannelBlocking(boolean isBlocking);

    public void setServerSocketSoTimeout(int timeout);

    public int getServerSocketSoTimeout();

    public void setServerSocketReceiveBufferSize(int size);

    public int getServerSocketReceiveBufferSize();

    public void setSocketSoTimeout(int timeout);

    public int getSocketSoTimeout();

    public void setSocketReceiveBufferSize(int size);

    public int getSocketReceiveBufferSize();

    public void setSocketSendBufferSize(int size);

    public int getSocketSendBufferSize();

    public void setSocketTcpNoDelay(boolean noDelay);

    public boolean isSocketTcpNoDelay();

    public void setSocketSoLinger(int time);

    public int getSocketSoLinger();

    public boolean isKeepAlive();

    public void setKeepAlive(boolean isKeepAlive);

    public void setServantGarbageInterval(long millis);

    public long getServantGarbageInterval();

    public void setAnalyzeQueueServiceName(ServiceName name);

    public ServiceName getAnalyzeQueueServiceName();

    public void setAnalyzeThreadSize(int size);

    public int getAnalyzeThreadSize();

    public void setMessageReceiverServiceName(ServiceName name);

    public ServiceName getMessageReceiverServiceName();

    public long getReceiveCount();

    public long getPublishCount();

    public long getServantsSendMessageParamCreateCountAverage();
}
