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
package jp.ossc.nimbus.service.http.httpclient;

import java.io.*;
import java.util.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

/**
 * Jakarta HttpClientを使ったHTTP GETリクエスト。<p>
 *
 * @author M.Takata
 */
public class GetHttpRequestImpl extends HttpRequestImpl{
    
    /**
     * {@link GetMethod}を生成する。<p>
     *
     * @return GetMethod
     * @exception Exception GetMethodの生成に失敗した場合
     */
    protected HttpMethodBase instanciateHttpMethod() throws Exception{
        return new GetMethod();
    }
    
    /**
     * リクエストパラメータをクエリとして設定する。<p>
     *
     * @param method HTTPメソッド
     * @param params リクエストパラメータ
     * @exception Exception クエリの設定に失敗した場合
     */
    protected void initParameter(
        HttpMethodBase method,
        Map params
    ) throws Exception{
        final Iterator names = params.keySet().iterator();
        final List values = new ArrayList();
        while(names.hasNext()){
            String name = (String)names.next();
            Object val = params.get(name);
            if(val instanceof String[]){
                String[] vals = (String[])val;
                for(int i = 0; i < vals.length; i++){
                    values.add(
                        new NameValuePair(name, vals[i])
                    );
                }
            }else{
                values.add(
                    new NameValuePair(
                        name,
                        val == null ? null : val.toString()
                    )
                );
            }
        }
        if(values.size() > 0){
            method.setQueryString(
                (NameValuePair[])values.toArray(
                    new NameValuePair[values.size()]
                )
            );
        }
    }
    
    /**
     * サポートしない。<p>
     *
     * @param method HTTPメソッド
     * @param is 入力ストリーム
     * @exception Exception UnsupportedOperationExceptionを投げる
     */
    protected void initInputStream(
        HttpMethodBase method,
        InputStream is
    ) throws Exception{
        throw new UnsupportedOperationException();
    }
}
