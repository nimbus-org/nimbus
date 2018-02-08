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

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import jp.ossc.nimbus.service.aop.*;

/**
 * リクエスト文字コード設定インターセプタ。<p>
 * ISO8859_1でエンコードされたリクエストパラメータを任意の文字コードに変換するインターセプタである。<br>
 *
 * @author M.Takata
 */
public class SetCharacterEncodingInterceptorService
 extends ServletFilterInterceptorService
 implements SetCharacterEncodingInterceptorServiceMBean{
    
    private static final long serialVersionUID = 6562650000038918758L;
    
    protected static final String ISO8859_1 = "ISO8859_1";
    protected static final String QUERY_DELIM = "&";
    
    protected String characterEncoding;
    protected boolean isEncodeQuery;
    
    // SetCharacterEncodingInterceptorServiceMBeanのJavaDoc
    public void setCharacterEncoding(String encoding){
        characterEncoding = encoding;
    }
    
    // SetCharacterEncodingInterceptorServiceMBeanのJavaDoc
    public String getCharacterEncoding(){
        return characterEncoding;
    }
    
    // SetCharacterEncodingInterceptorServiceMBeanのJavaDoc
    public void setEncodeQuery(boolean isEncode){
        isEncodeQuery = isEncode;
    }
    
    // SetCharacterEncodingInterceptorServiceMBeanのJavaDoc
    public boolean isEncodeQuery(){
        return isEncodeQuery;
    }
    
    /**
     * リクエストの文字コードを設定して、次のインターセプタを呼び出す。<p>
     * サービスが開始されていない場合は、何もせずに次のインターセプタを呼び出す。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても、呼び出し元には伝播されない。
     */
    public Object invokeFilter(
        ServletFilterInvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        if(getState() == STARTED){
            final ServletRequest request = context.getServletRequest();
            if(request.getCharacterEncoding() == null && characterEncoding != null){
                request.setCharacterEncoding(characterEncoding);
            }
            if(isEncodeQuery && request instanceof HttpServletRequest){
                final HttpServletRequest httpReq = (HttpServletRequest)request;
                final String query = httpReq.getQueryString();
                if(query != null){
                    final Map queryMap = parseQueryString(query);
                    final Iterator itr = queryMap.keySet().iterator();
                    while(itr.hasNext()){
                        final String key = (String)itr.next();
                        final String[] qvals = (String[])queryMap.get(key);
                        
                        if(qvals != null && qvals.length > 0){
                            String[] vals = request.getParameterValues(key);
                            if(vals == null){
                                continue;
                            }
                            
                            for(int i = 0; i < qvals.length; i++){
                                for(int j = 0; j < vals.length; j++){
                                    if(qvals[i].equals(vals[j])){
                                        vals[j] = new String(
                                            vals[j].getBytes(ISO8859_1),
                                            request.getCharacterEncoding()
                                        );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return chain.invokeNext(context);
    }
    
    protected static Map parseQueryString(String str){
        final Map params = new HashMap();
        final StringBuffer sb = new StringBuffer();
        final StringTokenizer tokens = new StringTokenizer(str, QUERY_DELIM);
        while(tokens.hasMoreTokens()){
            final String pair = tokens.nextToken();
            final int pos = pair.indexOf('=');
            
            String key = null;
            String val = null;
            if(pos == -1){
                key = parseName(pair, sb);
                val = "";
            }else{
                key = parseName(pair.substring(0, pos), sb);
                val = parseName(pair.substring(pos + 1, pair.length()), sb);
            }
            
            String valArray[] = null;
            if(params.containsKey(key)){
                final String[] oldVals = (String[])params.get(key);
                valArray = new String[oldVals.length + 1];
                for(int i = 0; i < oldVals.length; i++){
                   valArray[i] = oldVals[i];
                }
                valArray[oldVals.length] = val;
            }else{
                valArray = new String[1];
                valArray[0] = val;
            }
            params.put(key, valArray);
        }
        return params;
    }
    
    protected static String parseName(String str, StringBuffer sb){
        sb.setLength(0);
        for(int i = 0, max = str.length(); i < max; i++){
            final char c = str.charAt(i);
            switch(c){
            case '+':
                sb.append(' ');
                break;
            case '%':
                try{
                    sb.append(
                        (char)Integer.parseInt(str.substring(i + 1, i + 3), 16)
                    );
                    i += 2;
                }catch(NumberFormatException e){
                    sb.append(c);
                }catch(StringIndexOutOfBoundsException e){
                    String rest = str.substring(i);
                    sb.append(rest);
                    if(rest.length() == 2){
                        i++;
                    }
                }
                break;
            default:
                sb.append(c);
                break;
            }
        }
        return sb.toString();
    }
}