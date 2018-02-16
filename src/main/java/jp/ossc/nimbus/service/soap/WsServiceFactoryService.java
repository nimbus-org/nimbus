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

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceMetaData;

/**
 * Webサービスファクトリーサービス。
 * <p>
 *
 * @author M.Ishida
 */
public class WsServiceFactoryService extends ServiceBase implements WsServiceFactory, WsServiceFactoryServiceMBean {

    protected String wsdlPath;
    protected String nameSpace;
    protected String localPart;
    protected String webServiceClassName;

    protected Class webServiceClass;
    protected URL wsdlURL;

    public String getWsdlPath() {
        return wsdlPath;
    }

    public void setWsdlPath(String path) {
        wsdlPath = path;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String name) {
        nameSpace = name;
    }

    public String getLocalPart() {
        return localPart;
    }

    public void setLocalPart(String part) {
        localPart = part;
    }

    public String getWebServiceClassName() {
        return webServiceClassName;
    }

    public void setWebServiceClassName(String name) {
        webServiceClassName = name;
    }

    public void startService() throws Exception {
        if (wsdlPath == null || "".equals(wsdlPath)) {
            throw new IllegalArgumentException("WsdlPath must be specified.");
        }
        if (nameSpace == null || "".equals(nameSpace)) {
            throw new IllegalArgumentException("NameSpace must be specified.");
        }
        if (localPart == null || "".equals(localPart)) {
            throw new IllegalArgumentException("LocalPart must be specified.");
        }
        if (webServiceClassName == null || "".equals(webServiceClassName)) {
            throw new IllegalArgumentException("WebServiceClassName must be specified.");
        }
        if (wsdlPath != null) {
            URL url = null;
            File localFile = new File(wsdlPath);
            if (localFile.exists()) {
                if (!localFile.isFile()) {
                    throw new IllegalArgumentException("WsdlPath must be file : " + localFile);
                }
                try {
                    wsdlURL = localFile.toURI().toURL();
                } catch (MalformedURLException e) {
                    // この例外は発生しないはず
                }
            } else {
                File serviceDefDir = null;
                if (getServiceNameObject() != null) {
                    ServiceMetaData metaData = ServiceManagerFactory.getServiceMetaData(getServiceNameObject());
                    if (metaData != null) {
                        jp.ossc.nimbus.core.ServiceLoader loader = metaData.getServiceLoader();
                        if (loader != null) {
                            String filePath = loader.getServiceURL().getFile();
                            if (filePath != null) {
                                serviceDefDir = new File(filePath).getParentFile();
                            }
                        }
                    }
                }
                localFile = new File(serviceDefDir, wsdlPath);
                if (localFile.exists()) {
                    if (!localFile.isFile()) {
                        throw new IllegalArgumentException("WsdlPath must be file : " + localFile);
                    }
                    try {
                        wsdlURL = localFile.toURI().toURL();
                    } catch (MalformedURLException e) {
                        // この例外は発生しないはず
                    }
                } else {
                    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                    final URL resource = classLoader.getResource(wsdlPath);
                    if (resource != null) {
                        wsdlURL = resource;
                    }
                }
            }
        }
        try {
            webServiceClass = Class.forName(webServiceClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("WebServiceClassName Illegal. " + webServiceClassName);
        }
    }

    public Service getService() throws WsServiceException {

        final QName serviceQName = new QName(nameSpace, localPart);

        final Class[] types = { URL.class, QName.class };
        Constructor constructor;
        try {
            constructor = webServiceClass.getConstructor(types);
        } catch (SecurityException e) {
            throw new WsServiceException(e);
        } catch (NoSuchMethodException e) {
            throw new WsServiceException(e);
        }
        Object[] args = { wsdlURL, serviceQName };
        Service service;
        try {
            service = (Service) constructor.newInstance(args);
        } catch (IllegalArgumentException e) {
            throw new WsServiceException(e);
        } catch (InstantiationException e) {
            throw new WsServiceException(e);
        } catch (IllegalAccessException e) {
            throw new WsServiceException(e);
        } catch (InvocationTargetException e) {
            throw new WsServiceException(e);
        }
        return service;
    }

}
