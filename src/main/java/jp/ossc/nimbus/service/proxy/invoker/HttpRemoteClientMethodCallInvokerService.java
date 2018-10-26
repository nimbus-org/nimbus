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
package jp.ossc.nimbus.service.proxy.invoker;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.Invoker;
import jp.ossc.nimbus.service.aop.MethodInvocationContext;
import jp.ossc.nimbus.service.http.HttpClient;
import jp.ossc.nimbus.service.http.HttpClientFactory;
import jp.ossc.nimbus.service.http.HttpException;
import jp.ossc.nimbus.service.http.HttpRequest;
import jp.ossc.nimbus.service.http.HttpResponse;
import jp.ossc.nimbus.service.keepalive.KeepAliveListener;
import jp.ossc.nimbus.service.proxy.RemoteServiceCallException;

/**
 * HTTPリモートクライアントメソッド呼び出しInvoker。<p>
 * HTTP経由で、リモートサーバ上のサービスを呼び出すためのInvokerである。<br>
 * リモートのServletコンテナに、{@link jp.ossc.nimbus.servlet.RemoteServiceServerServlet RemoteServiceServerServlet}がされていなければならない。<br>
 *
 * @author M.Takata
 */
public class HttpRemoteClientMethodCallInvokerService extends ServiceBase
 implements Invoker, KeepAliveCheckInvoker, Serializable,
            HttpRemoteClientMethodCallInvokerServiceMBean{

    private static final long serialVersionUID = -5888997460845177962L;

    private ServiceName httpClientFactoryServiceName;
    private HttpClientFactory httpClientFactory;
    private ServiceName remoteServiceName;
    private String invokeActionName = DEFAULT_ACTION_NAME_INVOKE;
    private String aliveCheckActionName = DEFAULT_ACTION_NAME_ALIVE_CHECK;
    private String resourceUsageActionName = DEFAULT_ACTION_NAME_RESOURCE_USAGE;

    private InetAddress clientAddress;

    public void setHttpClientFactoryServiceName(ServiceName name){
        httpClientFactoryServiceName = name;
    }
    public ServiceName getHttpClientFactoryServiceName(){
        return httpClientFactoryServiceName;
    }

    public void setRemoteServiceName(ServiceName name){
        remoteServiceName = name;
    }
    public ServiceName getRemoteServiceName(){
        return remoteServiceName;
    }

    public void setInvokeActionName(String name){
        invokeActionName = name;
    }
    public String getInvokeActionName(){
        return invokeActionName;
    }

    public void setAliveCheckActionName(String name){
        aliveCheckActionName = name;
    }
    public String getAliveCheckActionName(){
        return aliveCheckActionName;
    }

    public void setResourceUsageActionName(String name){
        resourceUsageActionName = name;
    }
    public String getResourceUsageActionName(){
        return resourceUsageActionName;
    }

    public void setHttpClientFactory(HttpClientFactory factory){
        httpClientFactory = factory;
    }

    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(httpClientFactoryServiceName != null){
            httpClientFactory = (HttpClientFactory)ServiceManagerFactory
                .getServiceObject(httpClientFactoryServiceName);
        }

        if(httpClientFactory == null){
            throw new IllegalArgumentException("HttpClientFactory is null.");
        }
        clientAddress = InetAddress.getLocalHost();
    }

    /**
     * {@link jp.ossc.nimbus.servlet.RemoteServiceServerServlet RemoteServiceServerServlet}を呼び出す。<p>
     *
     * @param context 呼び出しのコンテキスト情報
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合
     */
    public Object invoke(InvocationContext context) throws Throwable{

        HttpClient client = null;
        final MethodInvocationContext methodContext
             = (MethodInvocationContext)context;
        try{
            ServiceName serviceName = remoteServiceName;
            if(serviceName == null){
                Object target = methodContext.getTargetObject();
                if(target != null && target instanceof ServiceName){
                    serviceName = (ServiceName)target;
                }
            }
            if(serviceName != null){
                methodContext.setTargetObject(serviceName);
            }
            context.setAttribute("ClientAddress", clientAddress);
            client = httpClientFactory.createHttpClient();
            HttpRequest request = httpClientFactory.createRequest(invokeActionName);
            request.setObject(context);
            HttpResponse response = client.executeRequest(request);
            if(response.getStatusCode() != 200){
                throw new RemoteServiceCallException("Http response status error. status=" + response.getStatusCode() + ", message=" + response.getStatusMessage());
            }
            return ((ResponseBag)response.getObject()).getReturn();
        }catch(HttpException e){
            throw new RemoteServiceCallException(e);
        }finally{
            if(client != null){
                try{
                    client.close();
                }catch(HttpException e){}
            }
        }
    }

    public boolean isAlive(){
        HttpClient client = null;
        try{
            client = httpClientFactory.createHttpClient();
            HttpRequest request = httpClientFactory.createRequest(aliveCheckActionName);
            if(remoteServiceName != null){
                request.setParameter("remoteServiceName", remoteServiceName.toString());
            }
            HttpResponse response = client.executeRequest(request);
            return response.getStatusCode() == 200;
        }catch(HttpException e){
            return false;
        }finally{
            if(client != null){
                try{
                    client.close();
                }catch(HttpException e){}
            }
        }
    }

    public void addKeepAliveListener(KeepAliveListener listener){
        throw new UnsupportedOperationException();
    }

    public void removeKeepAliveListener(KeepAliveListener listener){
        throw new UnsupportedOperationException();
    }

    public void clearKeepAliveListener(){
        throw new UnsupportedOperationException();
    }

    public Object getHostInfo(){
        try{
            HttpRequest request = httpClientFactory.createRequest(aliveCheckActionName);
            URL url = new URL(request.getURL());
            return url.getHost() + url.getPort();
        }catch(MalformedURLException e){
            return null;
        }catch(HttpException e){
            return null;
        }
    }

    public Comparable getResourceUsage(){
        HttpClient client = null;
        try{
            client = httpClientFactory.createHttpClient();
            HttpRequest request = httpClientFactory.createRequest(resourceUsageActionName);
            HttpResponse response = client.executeRequest(request);
            if(response.getStatusCode() != 200){
                return null;
            }
            return (Comparable)((ResponseBag)response.getObject()).getReturn();
        }catch(RuntimeException e){
            throw e;
        }catch(Error e){
            throw e;
        }catch(Throwable th){
            return null;
        }finally{
            if(client != null){
                try{
                    client.close();
                }catch(HttpException e){}
            }
        }
    }

    public static class ResponseBag implements Serializable{

        private static final long serialVersionUID = 1194961005713684155L;

        public Serializable returnObject;
        public Throwable throwable;

        public ResponseBag(){}

        public Object getReturn() throws Throwable{
            if(throwable != null){
                throw throwable;
            }
            return returnObject;
        }
    }
}