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
package jp.ossc.nimbus.service.keepalive.http;

import java.net.InetAddress;
import java.net.URL;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.http.HttpClient;
import jp.ossc.nimbus.service.http.HttpClientFactory;
import jp.ossc.nimbus.service.http.HttpRequest;
import jp.ossc.nimbus.service.http.HttpResponse;
import jp.ossc.nimbus.service.http.httpclient.HttpClientConnectTimeoutException;
import jp.ossc.nimbus.service.http.httpclient.HttpClientSocketTimeoutException;
import jp.ossc.nimbus.service.keepalive.AbstractKeepAliveCheckerService;
import jp.ossc.nimbus.service.keepalive.KeepAliveChecker;
import jp.ossc.nimbus.service.system.HostResolver;

/**
 * Httpサーバの稼動状態をチェックする{@link KeepAliveChecker}インタフェース実装サービス。
 * <p>
 * 
 * @author M.Ishida
 */
public class HttpKeepAliveCheckerService extends AbstractKeepAliveCheckerService implements KeepAliveChecker, HttpKeepAliveCheckerServiceMBean {
    
    protected ServiceName httpClientFactoryServiceName;
    protected HttpClientFactory httpClientFactory;
    
    protected HostResolver hostResolver;
    protected ServiceName hostResolverServiceName;
    
    protected String checkTargetRequestName = DEFAULT_CHECK_TARGET_REQUEST_NAME;
    protected String assertString;
    protected String protocol;
    protected int port = -1;
    protected String path;
    protected boolean isReturnCheckTargetURL = true;
    protected URL checkTargetUrl;
    
    public ServiceName getHttpClientFactoryServiceName() {
        return httpClientFactoryServiceName;
    }
    
    public void setHttpClientFactoryServiceName(ServiceName serviceName) {
        httpClientFactoryServiceName = serviceName;
    }
    
    public HttpClientFactory getHttpClientFactory() {
        return httpClientFactory;
    }
    
    public void setHttpClientFactory(HttpClientFactory factory) {
        httpClientFactory = factory;
    }
    
    public String getCheckTargetRequestName() {
        return checkTargetRequestName;
    }
    
    public void setCheckTargetRequestName(String requestName) {
        checkTargetRequestName = requestName;
    }
    
    public String getAssertString() {
        return assertString;
    }
    
    public void setAssertString(String string) {
        assertString = string;
    }
    
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
    
    public String getProtocol() {
        return protocol;
    }
    
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public boolean isReturnCheckTargetURL() {
        return isReturnCheckTargetURL;
    }
    
    public void setReturnCheckTargetURL(boolean isReturn) {
        isReturnCheckTargetURL = isReturn;
    }
    
    public void startService() throws Exception {
        if (httpClientFactoryServiceName != null) {
            httpClientFactory = (HttpClientFactory) ServiceManagerFactory.getServiceObject(httpClientFactoryServiceName);
        }
        if (httpClientFactory == null) {
            throw new IllegalArgumentException("HttpClientFactory is null.");
        }
        if (checkTargetRequestName == null || "".equals(checkTargetRequestName)) {
            throw new IllegalArgumentException("CheckTargetRequestName is null.");
        }
        if (hostResolverServiceName != null) {
            hostResolver = (HostResolver) ServiceManagerFactory.getServiceObject(hostResolverServiceName);
        }
        HttpRequest request = httpClientFactory.createRequest(checkTargetRequestName);
        checkTargetUrl = new URL(request.getURL());
        if (protocol == null) {
            protocol = checkTargetUrl.getProtocol();
        }
        if (port == -1) {
            port = checkTargetUrl.getPort();
        }
        if (path == null) {
            path = checkTargetUrl.getPath();
        }
    }
    
    public boolean checkAlive() throws Exception {
        HttpClient client = httpClientFactory.createHttpClient();
        HttpRequest request = httpClientFactory.createRequest(checkTargetRequestName);
        try {
            HttpResponse response = client.executeRequest(request);
            if (response.getStatusCode() == 200) {
                if (assertString != null) {
                    String responseBody = (String) response.getObject();
                    if (assertString.equals(responseBody)) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        } catch (HttpClientConnectTimeoutException e) {
        } catch (HttpClientSocketTimeoutException e) {
        }
        return false;
    }
    
    public Object getHostInfo() throws Exception {
        if (hostResolver == null) {
            return isReturnCheckTargetURL ? checkTargetUrl : null;
        }
        InetAddress localAddress = hostResolver.getLocalHost();
        if (localAddress == null) {
            return isReturnCheckTargetURL ? checkTargetUrl : null;
        }
        return new URL(protocol + "://" + localAddress.getHostAddress() + ":" + port + path);
    }
    
}