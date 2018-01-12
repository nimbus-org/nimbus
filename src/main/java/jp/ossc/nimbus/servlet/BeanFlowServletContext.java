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
package jp.ossc.nimbus.servlet;

import javax.servlet.http.*;

/**
 * BeanFlowサーブレットの実行コンテキスト。<p>
 * {@link BeanFlowServlet}がBeanFlowを呼び出す時の引数となるオブジェクト。<p>
 *
 * @author M.Takata
 */
public class BeanFlowServletContext{
    
    /**
     * HTTPリクエスト。<p>
     */
    protected HttpServletRequest request;
    
    /**
     * HTTPレスポンス。<p>
     */
    protected HttpServletResponse response;
    
    /**
     * 入力オブジェクト。<p>
     */
    protected Object input;
    
    /**
     * 出力オブジェクト。<p>
     */
    protected Object output;
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     */
    public BeanFlowServletContext(
        HttpServletRequest req,
        HttpServletResponse resp
    ){
        request = req;
        response = resp;
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @param input 入力オブジェクト
     */
    public BeanFlowServletContext(
        HttpServletRequest req,
        HttpServletResponse resp,
        Object input
    ){
        request = req;
        response = resp;
        this.input = input;
    }
    
    /**
     * HTTPリクエストを取得する。<p>
     * 
     * @return HTTPリクエスト
     */
    public HttpServletRequest getRequest(){
        return request;
    }
    
    /**
     * HTTPレスポンスを取得する。<p>
     * 
     * @return HTTPレスポンス
     */
    public HttpServletResponse getResponse(){
        return response;
    }
    
    /**
     * 入力オブジェクトを取得する。<p>
     * 
     * @return 入力オブジェクト
     */
    public Object getInput(){
        return input;
    }
    
    /**
     * 出力オブジェクトを取得する。<p>
     * 
     * @return 出力オブジェクト
     */
    public Object getOutput(){
        return output;
    }
    
    /**
     * 出力オブジェクトを設定する。<p>
     * 
     * @param output 出力オブジェクト
     */
    public void setOutput(Object output){
        this.output = output;
    }
}