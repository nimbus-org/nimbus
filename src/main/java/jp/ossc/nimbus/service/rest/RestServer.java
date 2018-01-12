/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 * 
 * Copyright 2009 The Nimbus2 Project. All rights reserved.
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
 * policies, either expressed or implied, of the Nimbus2 Project.
 */
package jp.ossc.nimbus.service.rest;

/**
 * RESTサーバ。<p>
 * RESTリクエストを処理して、RESTレスポンスに処理結果を格納する。<br>
 *
 * @author M.Takata
 */
public interface RestServer{
    
    /**
     * POSTメソッドのRESTリクエストの処理を行う。<p>
     *
     * @param request RESTリクエスト
     * @param response RESTレスポンス
     * @exception Throwable 処理中に例外が発生した場合
     */
    public void processPost(PostRestRequest request, PostRestResponse response) throws Throwable;
    
    /**
     * GETメソッドのRESTリクエストの処理を行う。<p>
     *
     * @param request RESTリクエスト
     * @param response RESTレスポンス
     * @exception Throwable 処理中に例外が発生した場合
     */
    public void processGet(GetRestRequest request, GetRestResponse response) throws Throwable;
    
    /**
     * HEADメソッドのRESTリクエストの処理を行う。<p>
     *
     * @param request RESTリクエスト
     * @param response RESTレスポンス
     * @exception Throwable 処理中に例外が発生した場合
     */
    public void processHead(HeadRestRequest request, HeadRestResponse response) throws Throwable;
    
    /**
     * PUTメソッドのRESTリクエストの処理を行う。<p>
     *
     * @param request RESTリクエスト
     * @param response RESTレスポンス
     * @exception Throwable 処理中に例外が発生した場合
     */
    public void processPut(PutRestRequest request, PutRestResponse response) throws Throwable;
    
    /**
     * DELETEメソッドのRESTリクエストの処理を行う。<p>
     *
     * @param request RESTリクエスト
     * @param response RESTレスポンス
     * @exception Throwable 処理中に例外が発生した場合
     */
    public void processDelete(DeleteRestRequest request, DeleteRestResponse response) throws Throwable;
    
    /**
     * OPTIONSメソッドのRESTリクエストの処理を行う。<p>
     *
     * @param request RESTリクエスト
     * @param response RESTレスポンス
     * @exception Throwable 処理中に例外が発生した場合
     */
    public void processOptions(OptionsRestRequest request, OptionsRestResponse response) throws Throwable;
}