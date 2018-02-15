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
package jp.ossc.nimbus.service.soap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;

/**
 * Webサービスポートファクトリーサービス。
 * <p>
 *
 * @author M.Ishida
 */
public class WsPortFactoryService extends ServiceBase implements PortFactory, WsPortFactoryServiceMBean {

    private static final long serialVersionUID = 7074638390846720787L;

    private static final String SEPARATOR = ",";
    private static final int PORT_NAME = 0;
    private static final int ENDPOINT_INTERFACE_NAME = 1;

    private ServiceName wsServiceFactoryName;
    private ServiceName[] handlerServiceNames;
    private String nameSpace;

    private Service wsService;
    private List handlerList;
    private Map requestContext;

    /**
     * ポートエイリアスプロパティ キーに[ポートエイリアス名]、値に[ポート名,サービスエンドポイントインターフェース名]のプロパティ
     */
    private Properties portAliasProp;

    public void setHandlerList(List list) {
        handlerList = list;
    }

    public List getHandlerList() {
        return handlerList;
    }

    public void setHandlerServiceNames(ServiceName[] serviceNames) {
        handlerServiceNames = serviceNames;
    }

    public ServiceName[] getHandlerServiceNames() {
        return handlerServiceNames;
    }

    public Properties getPortAliasProp() {
        return portAliasProp;
    }

    public void setPortAliasProp(Properties prop) {
        portAliasProp = prop;
    }

    public ServiceName getWsServiceFactoryName() {
        return wsServiceFactoryName;
    }

    public void setWsServiceFactoryName(ServiceName serviceName) {
        wsServiceFactoryName = serviceName;
    }

    public void setRequestContext(String key, Object obj){
        requestContext.put(key, obj);
    }

    public Map getRequestContextMap(){
        return requestContext;
    }

    public void createService() throws Exception {
        handlerList = new ArrayList();
        requestContext = new HashMap();
    }

    public void startService() throws Exception {
        if (wsServiceFactoryName == null) {
            throw new IllegalArgumentException("WsServiceFactoryName must be specified.");
        }
        if (portAliasProp == null) {
            throw new IllegalArgumentException("portAliasProp must be specified.");
        }
        if (handlerServiceNames != null) {
            for (int i = 0; i < handlerServiceNames.length; i++) {
                try{
                    Handler handler = (Handler) ServiceManagerFactory.getServiceObject(handlerServiceNames[i]);
                    handlerList.add(handler);
                } catch(ClassCastException e){
                    throw new IllegalArgumentException(handlerServiceNames[i] + " is not instanceof Handler." + e);
                }
            }
        }

        WsServiceFactory wsServiceFactory = (WsServiceFactory) ServiceManagerFactory.getServiceObject(wsServiceFactoryName);
        nameSpace = wsServiceFactory.getNameSpace();
        wsService = wsServiceFactory.getService();
    }

    public Object getPort(String portAlias) throws PortException {
        String portNameClassName = portAliasProp.getProperty(portAlias);
        String[] names = portNameClassName.split(SEPARATOR);
        if (names.length < 2) {
            return new PortException("port name or endpoint interface name is illegal : " + portNameClassName);
        }
        try {
            Class endpointInterface = Class.forName(names[ENDPOINT_INTERFACE_NAME]);
            QName portQN = new QName(nameSpace, names[PORT_NAME]);
            Object port = wsService.getPort(portQN, endpointInterface);
            if ((handlerList != null && handlerList.size() > 0) && (port instanceof BindingProvider)) {
                BindingProvider bindingProvider = ((BindingProvider) port);
                bindingProvider.getRequestContext().putAll(requestContext);
                Binding binding = bindingProvider.getBinding();
                List list = binding.getHandlerChain();
                for (int i = 0; i < handlerList.size(); i++) {
                    Handler handler = (Handler) handlerList.get(i);
                    list.add(handler);
                }
                binding.setHandlerChain(list);
            }
            return endpointInterface.cast(port);
        } catch (Exception e) {
            throw new PortException(e);
        }
    }
}
