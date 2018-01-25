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
package jp.ossc.nimbus.service.test.proxy.netcrusher;

import java.net.InetAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jp.ossc.nimbus.core.ServiceBase;

import org.netcrusher.core.reactor.NioReactor;
import org.netcrusher.tcp.TcpCrusher;
import org.netcrusher.tcp.TcpCrusherBuilder;

/**
 * TcpCrusherを使用したTcpNetProxy{@link TcpNetProxy}の実装クラス。<p>
 *
 * @author M.Ishida
 */
public class TcpNetProxyService extends ServiceBase implements TcpNetProxyServiceMBean {

    private static final long serialVersionUID = -4869670081081516724L;

    protected String bindAddress;
    protected int bindPort = -1;
    protected String connectAddress;
    protected int connectPort = -1;
    protected boolean isOpenOnStart = true;

    protected NioReactor reactor;
    protected TcpCrusher crusher;

    public String getBindAddress() {
        return bindAddress;
    }

    public void setBindAddress(String address) {
        bindAddress = address;
    }

    public int getBindPort() {
        return bindPort;
    }

    public void setBindPort(int port) {
        bindPort = port;
    }

    public String getConnectAddress() {
        return connectAddress;
    }

    public void setConnectAddress(String address) {
        connectAddress = address;
    }

    public int getConnectPort() {
        return connectPort;
    }

    public void setConnectPort(int port) {
        connectPort = port;
    }

    public boolean isOpenOnStart() {
        return isOpenOnStart;
    }

    public void setOpenOnStart(boolean isOpenOnStart) {
        this.isOpenOnStart = isOpenOnStart;
    }

    public void createService() throws Exception {
        reactor = new NioReactor();
    }

    public void startService() throws Exception {
        if(bindAddress == null){
            bindAddress = InetAddress.getLocalHost().getHostAddress();
        }
        if (bindPort == -1) {
            throw new IllegalArgumentException("BindPort must be specified.");
        }
        if (connectPort == -1) {
            throw new IllegalArgumentException("ConnectPort must be specified.");
        }
        if (connectAddress == null || "".equals(connectAddress)) {
            throw new IllegalArgumentException("ConnectAddress must be specified.");
        }

        crusher = TcpCrusherBuilder.builder().withReactor(reactor).withBindAddress(bindAddress, bindPort)
                .withConnectAddress(connectAddress, connectPort).build();
        if (isOpenOnStart) {
            open();
        }
    }

    public void stopService() throws Exception {
        if (crusher != null) {
            crusher.close();
        }
        if (reactor != null) {
            reactor.close();
        }
    }

    public void open() {
        crusher.open();
    }

    public void close() {
        crusher.close();
    }

    public boolean isOpen() {
        return crusher.isOpen();
    }

    public void closeAllPairs() {
        crusher.closeAllPairs();
    }

    public void reopen() {
        crusher.reopen();
    }

    public void freeze() {
        crusher.freeze();
    }

    public void freezeAllPairs() {
        crusher.freezeAllPairs();
    }

    public void unfreeze() {
        crusher.unfreeze();
    }

    public void unfreezeAllPairs() {
        crusher.unfreezeAllPairs();
    }

    public boolean isFrozen() {
        return crusher.isFrozen();
    }

    public Set getClientAddresses() {
        Set result = new HashSet();
        Collection collect = crusher.getClientAddresses();
        if(collect != null){
            Iterator itr = collect.iterator();
            while(itr.hasNext()){
                result.add(itr.next().toString());
            }
        }
        return result;
    }
}
