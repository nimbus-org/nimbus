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

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.service.test.proxy.TcpNetProxy;

/**
 * {@link TcpNetProxyService}のMBeanインタフェース。<p>
 *
 * @author M.Ishida
 */
public interface TcpNetProxyServiceMBean extends ServiceBaseMBean, TcpNetProxy {

    /**
     * TcpCrusherに設定するBindAddressを取得する。<p>
     *
     * @return TcpCrusherに設定するBindAddress
     */
    public String getBindAddress();

    /**
     * TcpCrusherに設定するBindAddressを設定する。<p>
     *
     * @param address TcpCrusherに設定するBindAddress
     */
    public void setBindAddress(String address);

    /**
     * TcpCrusherに設定するBindPortを取得する。<p>
     *
     * @return TcpCrusherに設定するBindPort
     */
    public int getBindPort();

    /**
     * TcpCrusherに設定するBindPortを設定する。<p>
     *
     * @param port TcpCrusherに設定するBindPort
     */
    public void setBindPort(int port);

    /**
     * プロキシの接続先アドレスを取得する。<p>
     *
     * @return プロキシの接続先アドレス
     */
    public String getConnectAddress();

    /**
     * プロキシの接続先アドレスを設定する。<p>
     *
     * @param address プロキシの接続先アドレス
     */
    public void setConnectAddress(String address);

    /**
     * プロキシの接続先Portを取得する。<p>
     *
     * @return プロキシの接続先Port
     */
    public int getConnectPort();

    /**
     * プロキシの接続先Portを設定する。<p>
     *
     * @param port プロキシの接続先Port
     */
    public void setConnectPort(int port);

    /**
     * サービス開始時にオープンするかを取得する。<p>
     *
     * @return サービス開始時にオープンする場合、true
     */
    public boolean isOpenOnStart();

    /**
     * サービス開始時にオープンするかを設定する。<p>
     *
     * @param isOpenOnStart サービス開始時にオープンする場合、true
     */
    public void setOpenOnStart(boolean isOpenOnStart);
}
