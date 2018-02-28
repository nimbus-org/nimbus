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
 * フィルターを持つRESTリクエスト。<p>
 * フィルターとは、HTTPリクエストのクエリパラメータである。<br>
 *
 * @author M.Takata
 */
public class FilterRestRequest extends RestRequest{
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public FilterRestRequest(){
    }
    
    /**
     * 指定されたHTTPリクエストに紐づくインスタンスを生成する。<p>
     *
     * @param request HTTPリクエスト
     */
    public FilterRestRequest(HttpServletRequest request){
        super(request);
    }
    
    /**
     * 指定されたフィルタ値を取得する。<p>
     *
     * @param name フィルタ名
     * @return フィルタ値
     */
    public String getFilterValue(String name){
        return request.getParameter(name);
    }
    
    /**
     * 指定されたフィルタ値の配列を取得する。<p>
     *
     * @param name フィルタ名
     * @return フィルタ値配列
     */
    public String[] getFilterValues(String name){
        return request.getParameterValues(name);
    }
    
    /**
     * フィルタのマップを取得する。<p>
     *
     * @return フィルタのマップ
     */
    public Map getFilterMap(){
        return request.getParameterMap();
    }
}