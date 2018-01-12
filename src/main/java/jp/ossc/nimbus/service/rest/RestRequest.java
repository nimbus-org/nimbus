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

import java.util.Map;

import javax.servlet.http.*;

/**
 * RESTリクエスト。<p>
 *
 * @author M.Takata
 */
public class RestRequest{
    
    /**
     * HTTPリクエスト。<p>
     */
    protected HttpServletRequest request;
    
    /**
     * リクエストURI。<p>
     */
    protected String uri;
    
    /**
     * パスパラメータマップ。<p>
     */
    protected Map pathParameterMap;
    
    /**
     * リクエストオブジェクト。<p>
     */
    protected Object requestObject;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public RestRequest(){
    }
    
    /**
     * 指定されたHTTPリクエストに紐づくインスタンスを生成する。<p>
     *
     * @param request HTTPリクエスト
     */
    public RestRequest(HttpServletRequest request){
        this.request = request;
        final String contextPath = request.getContextPath();
        final String requestURI = request.getRequestURI();
        uri = requestURI.startsWith(contextPath) ? requestURI.substring(contextPath.length()) : requestURI;
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
     * リクエストURIを取得する。<p>
     *
     * @return リクエストURI
     */
    public String getURI(){
        return uri;
    }
    
    /**
     * パスパラメータマップを設定する。<p>
     *
     * @param map パスパラメータマップ
     */
    protected void setPathParameterMap(Map map){
        pathParameterMap = map;
    }
    
    /**
     * パスパラメータマップを取得する。<p>
     *
     * @return パスパラメータマップ
     */
    public Map getPathParameterMap(){
        return pathParameterMap;
    }
    
    /**
     * 指定したパスパラメータを取得する。<p>
     *
     * @param name パラメータ名
     * @return パスパラメータ
     */
    public String getPathParameter(String name){
        return pathParameterMap == null ? null : (String)pathParameterMap.get(name);
    }
    
    /**
     * リクエストオブジェクトを設定する。<p>
     *
     * @param requestObj リクエストオブジェクト
     */
    protected void setRequestObject(Object requestObj){
        requestObject = requestObj;
    }
    
    /**
     * リクエストオブジェクトを取得する。<p>
     *
     * @return リクエストオブジェクト
     */
    public Object getRequestObject(){
        return requestObject;
    }
}