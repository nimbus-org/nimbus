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
 * Jakarta HttpClientを使ったHTTP PUTリクエスト。<p>
 *
 * @author M.Takata
 */
public class PutHttpRequestImpl extends HttpRequestImpl{
    
    /**
     * サポートしない。<p>
     *
     * @param name リクエストパラメータ名
     * @param value リクエストパラメータ
     */
    public void setParameter(String name, String value){
        throw new UnsupportedOperationException();
    }
    
    /**
     * サポートしない。<p>
     *
     * @param name リクエストパラメータ名
     * @param value リクエストパラメータ
     */
    public void setParameters(String name, String[] value){
        throw new UnsupportedOperationException();
    }
    
    /**
     * {@link PutMethod}を生成する。<p>
     *
     * @return PutMethod
     * @exception Exception PutMethodの生成に失敗した場合
     */
    protected HttpMethodBase instanciateHttpMethod() throws Exception{
        return new PutMethod();
    }
    
    /**
     * サポートしない。<p>
     *
     * @param method HTTPメソッド
     * @param params リクエストパラメータ
     * @exception Exception リクエストパラメータの設定に失敗した場合
     */
    protected void initParameter(
        HttpMethodBase method,
        Map params
    ) throws Exception{
        throw new UnsupportedOperationException();
    }
    
    /**
     * 入力ストリームから読み込んだデータをリクエストのボディとして出力する。<p>
     *
     * @param method HTTPメソッド
     * @param is 入力ストリーム
     * @exception Exception リクエストのボディ出力に失敗した場合
     */
    protected void initInputStream(
        HttpMethodBase method,
        InputStream is
    ) throws Exception{
        ((EntityEnclosingMethod)method).setRequestEntity(
            new InputStreamRequestEntity(is)
        );
    }
}
