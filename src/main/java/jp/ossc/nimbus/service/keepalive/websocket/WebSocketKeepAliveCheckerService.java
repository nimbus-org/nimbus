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
package jp.ossc.nimbus.service.keepalive.websocket;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.Arrays;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.keepalive.AbstractKeepAliveCheckerService;
import jp.ossc.nimbus.service.keepalive.KeepAliveChecker;
import jp.ossc.nimbus.service.system.HostResolver;

/**
 * WebSocket Socketサーバの稼動状態をチェックする{@link KeepAliveChecker}インタフェース実装サービス。
 * <p>
 *
 * @author M.Ishida
 */
public class WebSocketKeepAliveCheckerService extends AbstractKeepAliveCheckerService implements KeepAliveChecker, WebSocketKeepAliveCheckerServiceMBean {

    private static final long serialVersionUID = 1L;

    protected HostResolver hostResolver;
    protected ServiceName hostResolverServiceName;

    protected String checkTargetAddress;
    protected int checkTargetPort = -1;
    protected int connectTimeOut = DEFAULT_CONNECT_TIME_OUT;
    protected byte[] requestBytes;
    protected byte[] assertBytes;
    protected boolean isReturnCheckTargetAddress = true;
    protected int responsePort = -1;
    protected String protocol = DEFAULT_WEBSOCKET_PROTOCOL;

    protected InetSocketAddress socketAddress;

    public ServiceName getHostResolverServiceName() {
        return hostResolverServiceName;
    }

    public void setHostResolverServiceName(ServiceName serviceName) {
        hostResolverServiceName = serviceName;
    }

    public HostResolver getHostResolver() {
        return hostResolver;
    }

    public void setHostResolver(HostResolver resolver) {
        hostResolver = resolver;
    }

    public String getCheckTargetAddress() {
        return checkTargetAddress;
    }

    public void setCheckTargetAddress(String address) {
        checkTargetAddress = address;
    }

    public int getCheckTargetPort() {
        return checkTargetPort;
    }

    public void setCheckTargetPort(int port) {
        checkTargetPort = port;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int timeOut) {
        connectTimeOut = timeOut;
    }

    public byte[] getRequestBytes() {
        return requestBytes;
    }

    public void setRequestBytes(byte[] bytes) {
        requestBytes = bytes;
    }

    public byte[] getAssertBytes() {
        return assertBytes;
    }

    public void setAssertBytes(byte[] bytes) {
        assertBytes = bytes;
    }

    public int getResponsePort() {
        return responsePort;
    }

    public void setResponsePort(int port) {
        responsePort = port;
    }

    public boolean isReturnCheckTargetAddress() {
        return isReturnCheckTargetAddress;
    }

    public void setReturnCheckTargetAddress(boolean isReturn) {
        isReturnCheckTargetAddress = isReturn;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void startService() throws Exception {
        if (hostResolverServiceName != null) {
            hostResolver = (HostResolver) ServiceManagerFactory.getServiceObject(hostResolverServiceName);
        }
        if (checkTargetPort == -1) {
            throw new IllegalArgumentException("CheckTargetPort must be specified.");
        }
        socketAddress = checkTargetAddress == null ? new InetSocketAddress(checkTargetPort) : new InetSocketAddress(checkTargetAddress,
                checkTargetPort);
        if (responsePort == -1) {
            responsePort = checkTargetPort;
        }
    }

    public boolean checkAlive() throws Exception {
        Socket socket = new Socket();
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            socket.connect(socketAddress, connectTimeOut);
            if (requestBytes != null) {
                dos = new DataOutputStream(socket.getOutputStream());
            }
            if (assertBytes != null) {
                dis = new DataInputStream(socket.getInputStream());
            }
            if (dos != null) {
                dos.write(requestBytes);
            }
            if (dis != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final byte[] bytes = new byte[1024];
                int length = 0;
                while ((length = dis.read(bytes, 0, 1024)) != -1) {
                    baos.write(bytes, 0, length);
                }
                return Arrays.equals(assertBytes, baos.toByteArray());
            }

        } catch (Exception e) {
            return false;
        } finally {
            if (dos != null) {
                dos.close();
                dos = null;
            }
            if (dis != null) {
                dis.close();
                dis = null;
            }
            if (socket != null && socket.isConnected()) {
                socket.close();
                socket = null;
            }
        }
        return true;
    }

    public Object getHostInfo() throws Exception {
        if (hostResolver == null) {
            return isReturnCheckTargetAddress ? new URI(protocol + "://" + socketAddress.getHostName() + ":" + responsePort) : null;
        }
        InetAddress localAddress = hostResolver.getLocalHost();
        if (localAddress == null) {
            return isReturnCheckTargetAddress ?  new URI(protocol + "://" + socketAddress.getHostName() + ":" + responsePort) : null;
        }
        return  new URI(protocol + "://" + localAddress.getHostAddress() + ":" + responsePort);
    }

}