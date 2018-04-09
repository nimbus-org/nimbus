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
import java.io.File;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.scp.SCPClient;
import jp.ossc.nimbus.service.scp.SCPClientFactory;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.TestContext;

/**
 * SCPでGET/MGET/PUT/MPUTを行うテストアクション。
 * <p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 *
 * @author M.Ishida
 */
public class SCPActionService extends ServiceBase implements TestAction, TestActionEstimation, SCPActionServiceMBean {

    private static final long serialVersionUID = -6285352199755488985L;
    protected double expectedCost = Double.NaN;

    protected ServiceName scpClientFactoryServiceName;
    protected SCPClientFactory scpClientFactory;

    public ServiceName getScpClientFactoryServiceName() {
        return scpClientFactoryServiceName;
    }

    public void setScpClientFactoryServiceName(ServiceName serviceName) {
        scpClientFactoryServiceName = serviceName;
    }

    public void startService() throws Exception {
        if (scpClientFactoryServiceName != null) {
            scpClientFactory = (SCPClientFactory) ServiceManagerFactory.getServiceObject(scpClientFactoryServiceName);
        }
        if (scpClientFactory == null) {
            throw new IllegalArgumentException("SCPClientFactory is null.");
        }
    }

    /**
     * リソースの内容を読み込んで、SCPでGET/MGET/PUT/MPUTを行う。
     * <p>
     * リソースのフォーマットは、以下。<br>
     *
     * <pre>
     * actionType
     * filePath
     * </pre>
     *
     * actionTypeは、GET、MGETまたはPUT、MPUTを指定する。<br>
     * filePathは、以下のフォーマットとなる。<br>
     * GETの場合、取得するファイルのパス,取得後のファイル名<br>
     * MGETの場合、取得するファイルのパス<br>
     * PUTの場合、転送するファイル名,転送先のディレクトリ名,転送先でのファイルの権限（省略可）<br>
     * MPUTの場合、転送するディレクトリ名,転送先のディレクトリ名,転送先でのファイルの権限（省略可）<br>
     * 複数指定する場合は、改行して指定する。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return GET、MGETの場合、取得したファイルのリスト。PUT、MPUTEの場合、null。
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception {
        BufferedReader br = new BufferedReader(resource);
        String actionType = null;
        List pathList = new ArrayList();
        try {
            actionType = br.readLine();
            if (actionType == null) {
                throw new Exception("Unexpected EOF on actionType");
            }
            if (!"GET".equals(actionType) && !"MGET".equals(actionType) && !"PUT".equals(actionType) && !"MPUT".equals(actionType)) {
                throw new Exception("Illegal actionType : " + actionType);
            }
            String filePath = null;
            while ((filePath = br.readLine()) != null) {
                String[] paths = filePath.split(",");
                if ("GET".equals(actionType)) {
                    if (paths.length != 2) {
                        throw new Exception("Illegal filePath : " + filePath);
                    }
                    pathList.add(paths);
                } else if ("MGET".equals(actionType)) {
                    pathList.add(filePath);
                } else if ("PUT".equals(actionType) || "MPUT".equals(actionType)) {
                    if (paths.length < 2) {
                        throw new Exception("Illegal filePath : " + filePath);
                    }
                    pathList.add(paths);
                }
            }
            if (pathList.size() == 0) {
                throw new Exception("Unexpected EOF on filePath");
            }
        } finally {
            br.close();
            br = null;
        }
        List result = null;
        SCPClient client = scpClientFactory.createSCPClient();
        try {
            if ("GET".equals(actionType)) {
                result = new ArrayList();
                for (int i = 0; i < pathList.size(); i++) {
                    String[] paths = (String[]) pathList.get(i);
                    result.add(client.get(paths[0], context.getCurrentDirectory().getCanonicalPath() + "/" + paths[1]));
                }
            } else if ("MGET".equals(actionType)) {
                result = new ArrayList();
                for (int i = 0; i < pathList.size(); i++) {
                    File[] files = client.mget((String) pathList.get(i), context.getCurrentDirectory().getCanonicalPath());
                    if (files != null) {
                        for (int j = 0; j < files.length; j++) {
                            result.add(files[j]);
                        }
                    }
                }
            } else if ("PUT".equals(actionType)) {
                for (int i = 0; i < pathList.size(); i++) {
                    String[] paths = (String[]) pathList.get(i);
                    if (paths.length == 2) {
                        client.put(context.getCurrentDirectory().getCanonicalPath() + "/" + paths[0], paths[1]);
                    } else {
                        client.put(context.getCurrentDirectory().getCanonicalPath() + "/" + paths[0], paths[1], paths[2]);
                    }
                }
            } else if ("MPUT".equals(actionType)) {
                for (int i = 0; i < pathList.size(); i++) {
                    String[] paths = (String[]) pathList.get(i);
                    if (paths.length == 2) {
                        client.mput(context.getCurrentDirectory().getCanonicalPath() + "/" + paths[0], paths[1]);
                    } else {
                        client.mput(context.getCurrentDirectory().getCanonicalPath() + "/" + paths[0], paths[1], paths[2]);
                    }
                }
            }
        } finally {
            client.close();
        }
        return result;
    }

    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }

    public double getExpectedCost() {
        return expectedCost;
    }
}
