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
package jp.ossc.nimbus.service.resource.http;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.http.*;
import jp.ossc.nimbus.service.resource.*;

/**
 * HttpClientファクトリ。<p>
 *
 * @author M.Takata
 */
public class HttpClientFactoryService extends ServiceBase
 implements ResourceFactory, HttpClientFactoryServiceMBean{
    
    private static final long serialVersionUID = -4952869498749452801L;
    
    private ServiceName httpClientFactoryServiceName;
    private HttpClientFactory httpClientFactory;
    
    public void setHttpClientFactoryServiceName(ServiceName name){
        httpClientFactoryServiceName = name;
    }
    public ServiceName getHttpClientFactoryServiceName(){
        return httpClientFactoryServiceName;
    }
    
    public void setHttpClientFactory(HttpClientFactory factory) {
        this.httpClientFactory = factory;
    }
    
    public void startService() throws Exception{
        if(httpClientFactoryServiceName != null){
            httpClientFactory = (HttpClientFactory)ServiceManagerFactory
                .getServiceObject(httpClientFactoryServiceName);
        }
        if(httpClientFactory == null) {
            throw new IllegalArgumentException("HttpClientFactoryServiceName or HttpClientFactory must be specified.");
        }
    }
    
    public void stopService() throws Exception{
        httpClientFactory = null;
    }
    
    public TransactionResource makeResource(String key) throws Exception{
        if(httpClientFactory == null){
            return null;
        }
        return new HttpClientTransactionResource(
            httpClientFactory.createHttpClient()
        );
    }
    
    public class HttpClientTransactionResource implements TransactionResource{
        protected HttpClient client;
        
        public HttpClientTransactionResource(HttpClient client){
            this.client = client;
        }
        
        /**
         * トランザクション制御はサポートしないため、何もしない。<p>
         */
        public void commit() throws Exception{
            // 何もしない
        }
        
        /**
         * トランザクション制御はサポートしないため、何もしない。<p>
         */
        public void rollback() throws Exception{
            // 何もしない
        }
        
        /**
         * {@link HttpClient#close()}を呼び出す。<p>
         */
        public void close() throws Exception{
            client.close();
        }
        
        /**
         * {@link HttpClient}を取得する。<p>
         *
         * @return HttpClient
         */
        public Object getObject(){
            return client;
        }
    }
}
