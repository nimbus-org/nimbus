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
package jp.ossc.nimbus.util.converter;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * サーブレットリクエストパラメータ→DataSetコンバータ。<p>
 * {@link javax.servlet.http.HttpServletRequest#getHeader(String)}で取得できる認証ヘッダを{@link HttpServletRequestAuthorzationHeaderConverter.Authorization}オブジェクトに変換する。<br>
 *
 * @author M.Takata
 */
public class HttpServletRequestAuthorizationHeaderConverter implements Converter{
    
    /**
     * デフォルトの認証ヘッダ名。<p>
     */
    public static final String DEFAULT_AUTHORIZATION_HEADER_NAME = "Authorization";
    
    /**
     * デフォルトのプロキシ認証ヘッダ名。<p>
     */
    public static final String DEFAULT_PROXY_AUTHORIZATION_HEADER_NAME = "Proxy-Authorization";
    
    protected String authorizationHeaderName = DEFAULT_AUTHORIZATION_HEADER_NAME;
    protected String proxyAuthorizationHeaderName = DEFAULT_PROXY_AUTHORIZATION_HEADER_NAME;
    
    /**
     * 認証ヘッダ名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_AUTHORIZATION_HEADER_NAME}。<br>
     *
     * @param name 認証ヘッダ名
     */
    public void setAuthorizationHeaderName(String name){
        authorizationHeaderName = name;
    }
    
    /**
     * 認証ヘッダ名を取得する。<p>
     *
     * @return 認証ヘッダ名
     */
    public String getAuthorizationHeaderName(){
        return authorizationHeaderName;
    }
    
    /**
     * プロキシ認証ヘッダ名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_PROXY_AUTHORIZATION_HEADER_NAME}。<br>
     *
     * @param name プロキシ認証ヘッダ名
     */
    public void setProxyAuthorizationHeaderName(String name){
        proxyAuthorizationHeaderName = name;
    }
    
    /**
     * プロキシ認証ヘッダ名を取得する。<p>
     *
     * @return プロキシ認証ヘッダ名
     */
    public String getProxyAuthorizationHeaderName(){
        return proxyAuthorizationHeaderName;
    }
    
    /**
     * 指定されたjavax.servlet.http.HttpServletRequestを{@link HttpServletRequestAuthorzationHeaderConverter.Authorization 認証オブジェクト}に変換する。<p>
     *
     * @param obj 変換対象のjavax.servlet.http.HttpServletRequest
     * @return 変換後の認証オブジェクト
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convert(Object obj) throws ConvertException{
        if(!(obj instanceof HttpServletRequest)){
            return null;
        }
        HttpServletRequest request = (HttpServletRequest)obj;
        String authorizationHeader = request.getHeader(getAuthorizationHeaderName());
        if(authorizationHeader == null){
            authorizationHeader = request.getHeader(getProxyAuthorizationHeaderName());
            if(authorizationHeader == null){
                return null;
            }
        }
        Authorization authorization = new Authorization();
        authorization.parse(authorizationHeader);
        return authorization;
    }
    
    /**
     * 認証情報。<p>
     *
     * @author M.Takata
     */
    public static class Authorization implements java.io.Serializable{
        
        protected String authScheme;
        protected String token;
        protected Map authParam;
        
        /**
         * 認証スキームを取得する。<p>
         *
         * @return 認証スキーム
         */
        public String getAuthScheme(){
            return authScheme;
        }
        
        /**
         * 認証トークンを取得する。<p>
         *
         * @return 認証トークン
         */
        public String getToken(){
            return token;
        }
        
        /**
         * 認証パラメータを取得する。<p>
         *
         * @return 認証パラメータ
         */
        public Map getAuthParam(){
            return authParam;
        }
        
        protected void parse(String challenge) throws ConvertException{
            int index = challenge.indexOf(' ');
            if(index != -1){
                StringBuilder buf = new StringBuilder(challenge);
                authScheme = buf.substring(0, index).trim();
                buf.delete(0, index + 1);
                while(buf.length() != 0 && Character.isWhitespace(buf.charAt(0))){
                    buf.deleteCharAt(0);
                }
                if(Pattern.matches("[a-zA-Z0-9\\-\\._~\\+\\/]+=*", buf)){
                    token = buf.toString();
                }else{
                    do{
                        index = buf.indexOf("=");
                        if(index == -1){
                            throw new ConvertException("Illegal challenge : " + challenge);
                        }
                        String key = buf.substring(0, index).trim();
                        buf.delete(0, index + 1);
                        while(buf.length() != 0 && Character.isWhitespace(buf.charAt(0))){
                            buf.deleteCharAt(0);
                        }
                        if(buf.length() == 0 || buf.charAt(0) != '"'){
                            throw new ConvertException("Illegal challenge : " + challenge);
                        }
                        index = buf.indexOf("\"", 1);
                        if(index == -1){
                            throw new ConvertException("Illegal challenge : " + challenge);
                        }
                        String value = buf.substring(1, index);
                        if(authParam == null){
                            authParam = new LinkedHashMap();
                        }
                        authParam.put(key, value);
                        buf.delete(0, index + 1);
                        while(buf.length() != 0 && Character.isWhitespace(buf.charAt(0))){
                            buf.deleteCharAt(0);
                        }
                        if(buf.length() != 0 && buf.charAt(0) == ','){
                            buf.deleteCharAt(0);
                        }else{
                            break;
                        }
                    }while(true);
                }
            }else{
                token = challenge;
                return;
            }
        }
        
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.append('{');
            if(authScheme != null){
                buf.append("authScheme=").append(authScheme);
                if(token != null){
                    buf.append(", token=").append(token);
                }else if(authParam != null){
                    buf.append(", authParam=").append(authParam);
                }
            }else{
                buf.append("token=").append(token);
            }
            buf.append('}');
            return buf.toString();
        }
    }
}
