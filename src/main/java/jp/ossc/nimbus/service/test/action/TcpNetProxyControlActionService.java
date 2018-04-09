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
package jp.ossc.nimbus.service.test.action;

import java.io.BufferedReader;
import java.io.Reader;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.TestContext;
import jp.ossc.nimbus.service.test.proxy.TcpNetProxy;

/**
 * TcpNetProxyを操作するテストアクション。
 * <p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 *
 * @author M.Ishida
 */
public class TcpNetProxyControlActionService extends ServiceBase implements TestAction, TestActionEstimation, TcpNetProxyControlActionServiceMBean {

    private static final long serialVersionUID = 7754526857203225703L;

    protected ServiceName tcpNetProxyServiceName;
    protected TcpNetProxy proxy;
    protected double expectedCost = Double.NaN;

    public ServiceName getTcpNetProxyServiceName() {
        return tcpNetProxyServiceName;
    }

    public void setTcpNetProxyServiceName(ServiceName serviceName) {
        tcpNetProxyServiceName = serviceName;
    }

    public void startService() throws Exception {
        if (tcpNetProxyServiceName != null) {
            proxy = (TcpNetProxy) ServiceManagerFactory.getServiceObject(tcpNetProxyServiceName);
        }
        if (proxy == null) {
            throw new IllegalArgumentException("TcpNetProxy is null.");
        }
    }

    /**
     * リソースの内容を読み込んで、TcpNetProxyに対して指定されたオペレーションを実行する。
     * <p>
     * リソースのフォーマットは、以下。<br>
     *
     * <pre>
     * operation
     * </pre>
     *
     * operationは、open,close,reopen,freeze,unfreeze,closeAllPairs,freezeAllPairs,unfreezeAllPairsのいずれかを指定する。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return 生成されたスケジュールのリスト
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception {
        BufferedReader br = new BufferedReader(resource);
        String operation = null;
        try {
            operation = br.readLine();
            if (operation == null) {
                throw new Exception("Unexpected EOF on operation");
            }
        } finally {
            br.close();
            br = null;
        }

        if (operation.equals(OPERATION_OPEN)) {
            proxy.open();
        } else if (operation.equals(OPERATION_CLOSE)) {
            proxy.close();
        } else if (operation.equals(OPERATION_REOPEN)) {
            proxy.reopen();
        } else if (operation.equals(OPERATION_FREEZE)) {
            proxy.freeze();
        } else if (operation.equals(OPERATION_UNFREEZE)) {
            proxy.unfreeze();
        } else if (operation.equals(OPERATION_CLOSE_ALL_PAIRS)) {
            proxy.closeAllPairs();
        } else if (operation.equals(OPERATION_FREEZE_ALL_PAIRS)) {
            proxy.freezeAllPairs();
        } else if (operation.equals(OPERATION_UNFREEZE_ALL_PAIRS)) {
            proxy.unfreezeAllPairs();
        } else {
            throw new Exception("No supported operation. operation=" + operation);
        }

        return null;
    }

    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }

    public double getExpectedCost() {
        return expectedCost;
    }
}
