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
package jp.ossc.nimbus.service.aop.interceptor.servlet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.ServletFilterInvocationContext;
import jp.ossc.nimbus.service.aop.interceptor.OAuth2ScopeResolver;

/**
 * OAuth2 スコープ解決のサーブレットフィルタ用のデフォルト実装クラス。<p>
 *
 * @author M.Takata
 */
public class DefaultOAuth2ScopeResolverService extends ServiceBase implements OAuth2ScopeResolver, DefaultOAuth2ScopeResolverServiceMBean{
    
    protected boolean isTrimExtention = true;
    protected Pattern pathPattern;
    protected boolean isAppendMethod = false;
    
    public void setTrimExtention(boolean isTrim){
        isTrimExtention = isTrim;
    }
    public boolean isTrimExtention(){
        return isTrimExtention;
    }
    
    public void setPathPattern(String regex){
        pathPattern = Pattern.compile(regex);
    }
    public String getPathPattern(){
        return pathPattern == null ? null : pathPattern.toString();
    }
    
    public void setAppendMethod(boolean isAppend){
        isAppendMethod = isAppend;
    }
    public boolean isAppendMethod(){
        return isAppendMethod;
    }
    
    /**
     * この呼び出しに対するスコープを解決する。<p>
     * リクエストされたパス及びメソッドをスコープとして返す。<br>
     *
     * @param context 呼び出しコンテキスト
     * @return この呼び出しに対するスコープの配列。スコープが解決できない場合は、null
     */
    public String[] resolve(InvocationContext context){
        final ServletFilterInvocationContext sfic = (ServletFilterInvocationContext)context;
        final HttpServletRequest request = (HttpServletRequest)sfic.getServletRequest();
        String reqPath = request.getServletPath();
        if(request.getPathInfo() != null){
            reqPath = reqPath + request.getPathInfo();
        }
        StringBuilder scope = new StringBuilder();
        if(pathPattern != null){
            Matcher m = pathPattern.matcher(reqPath);
            while(m.find()){
                scope.append(m.group());
            }
        }else{
            scope.append(reqPath);
        }
        int index = scope.lastIndexOf(".");
        if(isTrimExtention && index != -1){
            scope.delete(index, scope.length());
        }
        if(isAppendMethod){
            scope.append('$').append(request.getMethod());
        }
        return new String[]{scope.toString()};
    }
}