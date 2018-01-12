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

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.http.HttpClientFactory;
import jp.ossc.nimbus.service.keepalive.AbstractKeepAliveCheckerServiceMBean;
import jp.ossc.nimbus.service.system.HostResolver;

/**
 * {@link HttpKeepAliveCheckerService}のMBeanインタフェース
 * <p>
 * 
 * @author M.Ishida
 * @see HttpKeepAliveCheckerService
 */
public interface HttpKeepAliveCheckerServiceMBean extends AbstractKeepAliveCheckerServiceMBean {
    
    public static final String DEFAULT_CHECK_TARGET_REQUEST_NAME = "CheckTargetRequest";
    
    public ServiceName getHttpClientFactoryServiceName();
    
    public void setHttpClientFactoryServiceName(ServiceName serviceName);
    
    public HttpClientFactory getHttpClientFactory();
    
    public void setHttpClientFactory(HttpClientFactory factory);
    
    public String getCheckTargetRequestName();
    
    public void setCheckTargetRequestName(String requestName);
    
    public String getAssertString();
    
    public void setAssertString(String string);
    
    public ServiceName getHostResolverServiceName();
    
    public void setHostResolverServiceName(ServiceName serviceName);
    
    public HostResolver getHostResolver();
    
    public void setHostResolver(HostResolver resolver);
    
    public String getProtocol();
    
    public void setProtocol(String protocol);
    
    public int getPort();
    
    public void setPort(int port);
    
    public String getPath();
    
    public void setPath(String path);
    
    public boolean isReturnCheckTargetURL();
    
    public void setReturnCheckTargetURL(boolean isReturn);
}