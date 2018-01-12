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

import java.io.IOException;

import javax.servlet.http.*;

/**
 * RESTレスポンス。<p>
 *
 * @author M.Takata
 */
public class RestResponse{
    
    /**
     * HTTPレスポンス。<p>
     */
    protected HttpServletResponse response;
    
    /**
     * レスポンスオブジェクト。<p>
     */
    protected Object responseObject;
    
    /**
     * レスポンスオブジェクトのクラス。<p>
     */
    protected Class responseObjectClass;
    
    /**
     * HTTPステータス。<p>
     */
    protected int status = HttpServletResponse.SC_OK;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public RestResponse(){
    }
    
    /**
     * 指定されたHTTPレスポンスに紐づくインスタンスを生成する。<p>
     *
     * @param response HTTPレスポンス
     */
    public RestResponse(HttpServletResponse response){
        this.response = response;
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
     * 処理結果を設定する。<p>
     *
     * @param status HTTPステータス
     */
    public void setResult(int status){
        response.setStatus(status);
        this.status = status;
    }
    
    /**
     * 処理結果を設定する。<p>
     *
     * @param status HTTPステータス
     * @param message メッセージ
     */
    public void setResult(int status, String message) throws IOException{
        response.sendError(status, message);
        this.status = status;
    }
    
    /**
     * 処理結果のHTTPステータスを取得する。<p>
     *
     * @return HTTPステータス
     */
    public int getResultStatus(){
        return status;
    }
    
    /**
     * レスポンスオブジェクトのクラスを設定する。<p>
     *
     * @param clazz レスポンスオブジェクトのクラス
     */
    protected void setResponseObjectClass(Class clazz){
        responseObjectClass = clazz;
    }
    
    /**
     * レスポンスオブジェクトのクラスを取得する。<p>
     *
     * @return レスポンスオブジェクトのクラス
     */
    public Class getResponseObjectClass(){
        return responseObjectClass;
    }
    
    /**
     * レスポンスオブジェクトを生成する。<p>
     *
     * @return レスポンスオブジェクト
     * @exception InstantiationException 生成に失敗した場合
     * @exception IllegalAccessException 引数なしのコンストラクタにアクセスできない場合
     */
    public Object createResponseObject() throws InstantiationException, IllegalAccessException{
        responseObject = responseObjectClass == null ? null : responseObjectClass.newInstance();
        return responseObject;
    }
    
    /**
     * レスポンスオブジェクトを設定する。<p>
     *
     * @param responseObj レスポンスオブジェクト
     * @exception IllegalArgumentException 指定したレスポンスオブジェクトの型が不正な場合
     */
    public void setResponseObject(Object responseObj) throws IllegalArgumentException{
        if(responseObj != null && responseObjectClass != null && !responseObjectClass.equals(responseObj.getClass())){
            throw new IllegalArgumentException("ResponseObject is not " + responseObjectClass.getName());
        }
        responseObject = responseObj;
    }
    
    /**
     * レスポンスオブジェクトを取得する。<p>
     *
     * @return レスポンスオブジェクト
     */
    public Object getResponseObject(){
        return responseObject;
    }
}