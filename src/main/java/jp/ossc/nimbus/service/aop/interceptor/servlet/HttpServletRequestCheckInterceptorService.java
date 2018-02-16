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
import java.util.regex.*;

import javax.servlet.*;
import javax.servlet.http.*;

import jp.ossc.nimbus.service.aop.*;

/**
 * HTTPリクエストチェックインターセプタ。<p>
 * 以下に、HTTPリクエストヘッダのContent-Typeが"application/x-www-form-urlencoded"でなかったり、HTTPメソッドがPOSTでなかった場合、HTTPステータス400を返すHTTPリクエストチェックインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="HttpServletRequestCheckInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.servlet.HttpServletRequestCheckInterceptorService"&gt;
 *             &lt;attribute name="ValidContentTypes"&gt;application/x-www-form-urlencoded&lt;/attribute&gt;
 *             &lt;attribute name="ValidMethods"&gt;POST&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class HttpServletRequestCheckInterceptorService
 extends ServletFilterInterceptorService
 implements HttpServletRequestCheckInterceptorServiceMBean{
    
    private static final long serialVersionUID = -8791823240259229953L;
    
    protected int maxContentLength = -1;
    protected int minContentLength = -1;
    protected boolean isAllowNullContentType = true;
    protected String[] validContentTypes;
    protected Set validContentTypeValueSet;
    protected String[] invalidContentTypes;
    protected ContentType[] invalidContentTypeValues;
    protected boolean isAllowNullCharacterEncoding = true;
    protected String[] validCharacterEncodings;
    protected String[] invalidCharacterEncodings;
    protected boolean isAllowNullLocale = true;
    protected String[] validLocales;
    protected Pattern[] validLocalePatterns;
    protected String[] validProtocols;
    protected String[] validRemoteAddrs;
    protected Pattern[] validRemoteAddrPatterns;
    protected String[] validRemoteHosts;
    protected Pattern[] validRemoteHostPatterns;
    protected int[] validRemotePorts;
    protected String[] validSchemata;
    protected String[] validServerNames;
    protected Pattern[] validServerNamePatterns;
    protected String[] validMethods;
    protected String[] invalidMethods;
    protected Properties headerEquals;
    protected Map headerEqualsMap;
    protected int errorStatus = HttpServletResponse.SC_BAD_REQUEST;
    protected boolean isThrowOnError;
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setMaxContentLength(int max){
        maxContentLength = max;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public int getMaxContentLength(){
        return maxContentLength;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setMinContentLength(int min){
        minContentLength = min;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public int getMinContentLength(){
        return minContentLength;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setAllowNullContentType(boolean isAllow){
        isAllowNullContentType = isAllow;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public boolean isAllowNullContentType(){
        return isAllowNullContentType;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setValidContentTypes(String[] types){
        validContentTypes = types;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public String[] getValidContentTypes(){
        return validContentTypes;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setInvalidContentTypes(String[] types){
        invalidContentTypes = types;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public String[] getInvalidContentTypes(){
        return invalidContentTypes;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setAllowNullCharacterEncoding(boolean isAllow){
        isAllowNullCharacterEncoding = isAllow;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public boolean isAllowNullCharacterEncoding(){
        return isAllowNullCharacterEncoding;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setValidCharacterEncodings(String[] encodings){
        validCharacterEncodings = encodings;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public String[] getValidCharacterEncodings(){
        return validCharacterEncodings;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setInvalidCharacterEncodings(String[] encodings){
        invalidCharacterEncodings = encodings;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public String[] getInvalidCharacterEncodings(){
        return invalidCharacterEncodings;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setAllowNullLocale(boolean isAllow){
        isAllowNullLocale = isAllow;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public boolean isAllowNullLocale(){
        return isAllowNullLocale;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setValidLocales(String[] locales){
        validLocales = locales;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public String[] getValidLocales(){
        return validLocales;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setValidProtocols(String[] protocols){
        validProtocols = protocols;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public String[] getValidProtocols(){
        return validProtocols;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setValidRemoteAddrs(String[] addrs){
        validRemoteAddrs = addrs;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public String[] getValidRemoteAddrs(){
        return validRemoteAddrs;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setValidRemoteHosts(String[] hosts){
        validRemoteHosts = hosts;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public String[] getValidRemoteHosts(){
        return validRemoteHosts;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setValidRemotePorts(int[] ports){
        validRemotePorts = ports;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public int[] getValidRemotePorts(){
        return validRemotePorts;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setValidSchemata(String[] schemata){
        validSchemata = schemata;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public String[] getValidSchemata(){
        return validSchemata;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setValidServerNames(String[] names){
        validServerNames = names;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public String[] getValidServerNames(){
        return validServerNames;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setValidMethods(String[] methods){
        validMethods = methods;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public String[] getValidMethods(){
        return validMethods;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setInvalidMethods(String[] methods){
        invalidMethods = methods;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public String[] getInvalidMethods(){
        return invalidMethods;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setHeaderEquals(Properties cond){
        headerEquals = cond;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public Properties getHeaderEquals(){
        return headerEquals;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setErrorStatus(int status){
        errorStatus = status;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public int getErrorStatus(){
        return errorStatus;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public void setThrowOnError(boolean isThrow){
        isThrowOnError = isThrow;
    }
    
    // HttpServletRequestCheckInterceptorServiceMBeanのJavaDoc
    public boolean isThrowOnError(){
        return isThrowOnError;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService() throws Exception{
        if(validLocales != null && validLocales.length != 0){
            validLocalePatterns = new Pattern[validLocales.length];
            for(int i = 0; i < validLocales.length; i++){
                validLocalePatterns[i] = Pattern.compile(validLocales[i]);
            }
        }
        
        if(validRemoteAddrs != null && validRemoteAddrs.length != 0){
            validRemoteAddrPatterns = new Pattern[validRemoteAddrs.length];
            for(int i = 0; i < validRemoteAddrs.length; i++){
                validRemoteAddrPatterns[i]
                     = Pattern.compile(validRemoteAddrs[i]);
            }
        }
        
        if(validRemoteHosts != null && validRemoteHosts.length != 0){
            validRemoteHostPatterns = new Pattern[validRemoteHosts.length];
            for(int i = 0; i < validRemoteHosts.length; i++){
                validRemoteHostPatterns[i]
                     = Pattern.compile(validRemoteHosts[i]);
            }
        }
        
        if(validServerNames != null && validServerNames.length != 0){
            validServerNamePatterns = new Pattern[validServerNames.length];
            for(int i = 0; i < validServerNames.length; i++){
                validServerNamePatterns[i]
                     = Pattern.compile(validServerNames[i]);
            }
        }
        
        if(headerEquals != null){
            headerEqualsMap = new HashMap();
            final Iterator names = headerEquals.keySet().iterator();
            while(names.hasNext()){
                final String name = (String)names.next();
                headerEqualsMap.put(
                    name,
                    Pattern.compile(headerEquals.getProperty(name))
                );
            }
        }
        
        if(validContentTypes != null){
            validContentTypeValueSet = new HashSet();
            for(int i = 0; i < validContentTypes.length; i++){
                validContentTypeValueSet.add(new ContentType(validContentTypes[i]));
            }
        }
        if(invalidContentTypes != null){
            invalidContentTypeValues = new ContentType[invalidContentTypes.length];
            for(int i = 0; i < invalidContentTypes.length; i++){
                invalidContentTypeValues[i] = new ContentType(invalidContentTypes[i]);
            }
        }
    }
    
    /**
     * HTTPリクエストのチェックをして、次のインターセプタを呼び出す。<p>
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
        final ServletRequest request = context.getServletRequest();
        final ServletResponse response = context.getServletResponse();
        if(getState() == STARTED){
            final int contentLength = request.getContentLength();
            if(maxContentLength >= 0
                && contentLength >= maxContentLength){
                return fail(
                    request,
                    response,
                    "MaxContentLength is " + maxContentLength
                        + " : " + contentLength
                );
            }
            if(minContentLength >= 0
                && contentLength <= minContentLength){
                return fail(
                    request,
                    response,
                    "MinContentLength is " + minContentLength
                        + " : " + contentLength
                );
            }
            
            final String contentType = request.getContentType();
            if(contentType == null){
                if(!isAllowNullContentType){
                    return fail(
                        request,
                        response,
                        "ContentType is null."
                    );
                }
            }else if(validContentTypeValueSet != null || invalidContentTypeValues != null){
                ContentType contentTypeValue = new ContentType(contentType);
                if(validContentTypeValueSet != null){
                    if(!validContentTypeValueSet.contains(contentTypeValue)){
                        return fail(
                            request,
                            response,
                            "ContentType is invalid : " + contentType
                        );
                    }
                }
                if(invalidContentTypeValues != null){
                    for(int i = 0; i < invalidContentTypeValues.length; i++){
                        if(invalidContentTypeValues[i].getMediaType()
                                .equalsIgnoreCase(contentTypeValue.getMediaType())
                        ){
                            Map invalidParams = invalidContentTypeValues[i].getParameters();
                            if(invalidParams == null){
                                return fail(
                                    request,
                                    response,
                                    "ContentType is invalid : " + contentType
                                );
                            }
                            Map params = contentTypeValue.getParameters();
                            if(params != null){
                                boolean invalid = true;
                                Iterator entries = invalidParams.entrySet().iterator();
                                while(entries.hasNext()){
                                    Map.Entry entry = (Map.Entry)entries.next();
                                    if(params.containsKey(entry.getKey())){
                                        Object value = params.get(entry.getKey());
                                        if((value != null && entry.getValue() == null)
                                            || (value == null && entry.getValue() != null)
                                            || (entry.getValue() != null && !entry.getValue().equals(value))
                                        ){
                                            invalid = false;
                                        }
                                    }
                                }
                                if(invalid){
                                    return fail(
                                        request,
                                        response,
                                        "ContentType is invalid : " + contentType
                                    );
                                }
                            }
                        }
                    }
                }
            }
            
            final String encoding = request.getCharacterEncoding();
            if(encoding == null){
                if(!isAllowNullCharacterEncoding){
                    return fail(
                        request,
                        response,
                        "CharacterEncoding is null."
                    );
                }
            }else{
                if(validCharacterEncodings != null){
                    boolean success = false;
                    for(int i = 0; i < validCharacterEncodings.length; i++){
                        if(encoding.equals(validCharacterEncodings[i])){
                            success = true;
                            break;
                        }
                    }
                    if(!success){
                        return fail(
                            request,
                            response,
                            "CharacterEncoding is invalid : " + encoding
                        );
                    }
                }
                if(invalidCharacterEncodings != null){
                    for(int i = 0; i < invalidCharacterEncodings.length; i++){
                        if(encoding.equals(invalidCharacterEncodings[i])){
                            return fail(
                                request,
                                response,
                                "CharacterEncoding is invalid : " + encoding
                            );
                        }
                    }
                }
            }
            
            final Enumeration locales = request.getLocales();
            if(!locales.hasMoreElements()){
                if(!isAllowNullLocale){
                    return fail(
                        request,
                        response,
                        "Locale is null."
                    );
                }
            }else{
                if(validLocales != null){
                    boolean success = false;
                    while(locales.hasMoreElements()){
                        final String locale
                             = ((Locale)locales.nextElement()).toString();
                        for(int i = 0; i < validLocales.length; i++){
                            final Matcher m
                                 = validLocalePatterns[i].matcher(locale);
                            if(m.matches()){
                                success = true;
                                break;
                            }
                        }
                        if(success){
                            break;
                        }
                    }
                    if(!success){
                        return fail(
                            request,
                            response,
                            "Locale is invalid : " + locales
                        );
                    }
                }
            }
            
            if(validProtocols != null){
                final String protocol = request.getProtocol();
                boolean success = false;
                for(int i = 0; i < validProtocols.length; i++){
                    if(protocol.equals(validProtocols[i])){
                        success = true;
                        break;
                    }
                }
                if(!success){
                    return fail(
                        request,
                        response,
                        "Protocol is invalid : " + protocol
                    );
                }
            }
            
            if(validRemoteAddrs != null){
                final String addr = request.getRemoteAddr();
                boolean success = false;
                for(int i = 0; i < validRemoteAddrs.length; i++){
                    final Matcher m
                         = validRemoteAddrPatterns[i].matcher(addr);
                    if(m.matches()){
                        success = true;
                        break;
                    }
                }
                if(!success){
                    return fail(
                        request,
                        response,
                        "Remote address is invalid : " + addr
                    );
                }
            }
            
            if(validRemoteHosts != null){
                final String host = request.getRemoteHost();
                boolean success = false;
                for(int i = 0; i < validRemoteHosts.length; i++){
                    final Matcher m
                         = validRemoteHostPatterns[i].matcher(host);
                    if(m.matches()){
                        success = true;
                        break;
                    }
                }
                if(!success){
                    return fail(
                        request,
                        response,
                        "Remote host is invalid : " + host
                    );
                }
            }
            
            if(validRemotePorts != null){
                final int port = request.getRemotePort();
                boolean success = false;
                for(int i = 0; i < validRemotePorts.length; i++){
                    if(port == validRemotePorts[i]){
                        success = true;
                        break;
                    }
                }
                if(!success){
                    return fail(
                        request,
                        response,
                        "Remote port is invalid : " + port
                    );
                }
            }
            
            if(validSchemata != null){
                final String scheme = request.getScheme();
                boolean success = false;
                for(int i = 0; i < validSchemata.length; i++){
                    if(scheme.equals(validSchemata[i])){
                        success = true;
                        break;
                    }
                }
                if(!success){
                    return fail(
                        request,
                        response,
                        "Scheme is invalid : " + scheme
                    );
                }
            }
            
            if(validServerNames != null){
                final String serverName = request.getServerName();
                boolean success = false;
                for(int i = 0; i < validServerNames.length; i++){
                    final Matcher m
                         = validServerNamePatterns[i].matcher(serverName);
                    if(m.matches()){
                        success = true;
                        break;
                    }
                }
                if(!success){
                    return fail(
                        request,
                        response,
                        "Server name is invalid : " + serverName
                    );
                }
            }
            
            if(request instanceof HttpServletRequest){
                final HttpServletRequest httpReq = (HttpServletRequest)request;
                
                final String method = httpReq.getMethod();
                if(validMethods != null && method != null){
                    boolean success = false;
                    for(int i = 0; i < validMethods.length; i++){
                        if(method.equals(validMethods[i])){
                            success = true;
                            break;
                        }
                    }
                    if(!success){
                        return fail(
                            request,
                            response,
                            "Method is invalid : " + method
                        );
                    }
                }
                if(invalidMethods != null && method != null){
                    for(int i = 0; i < invalidMethods.length; i++){
                        if(method.equals(invalidMethods[i])){
                            return fail(
                                request,
                                response,
                                "Method is invalid : " + method
                            );
                        }
                    }
                }
                
                if(headerEqualsMap != null && headerEqualsMap.size() != 0){
                    final Iterator names = headerEqualsMap.keySet().iterator();
                    while(names.hasNext()){
                        final String name = (String)names.next();
                        final String value = httpReq.getHeader(name);
                        if(value == null){
                            return fail(
                                request,
                                response,
                                "Header " + name + " is invalid : " + value
                            );
                        }
                        final Pattern p = (Pattern)headerEqualsMap.get(name);
                        final Matcher m = p.matcher(value);
                        if(!m.matches()){
                            return fail(
                                request,
                                response,
                                "Header " + name + " is invalid : " + value
                            );
                        }
                    }
                }
            }
        }
        return chain.invokeNext(context);
    }
    
    protected Object fail(
        ServletRequest request,
        ServletResponse response,
        String message
    ) throws HttpServletRequestCheckException{
        if(isThrowOnError){
            throw new HttpServletRequestCheckException(message);
        }
        if(response instanceof HttpServletResponse){
            ((HttpServletResponse)response).setStatus(errorStatus);
        }
        return null;
    }
    
    private static class ContentType implements java.io.Serializable{
        private static final long serialVersionUID = -2168875657048050381L;
        private final String mediaType;
        private final int hashCode;
        private Map parameters;
        public ContentType(String contentType){
            String[] types = contentType.split(";");
            mediaType = types[0].trim();
            int hash = mediaType.toLowerCase().hashCode();
            if(types.length > 1){
                parameters = new HashMap();
                for(int i = 1; i < types.length; i++){
                    String parameter = types[i].trim();
                    final int index = parameter.indexOf('=');
                    if(index != -1){
                        parameters.put(parameter.substring(0, index).toLowerCase(), parameter.substring(index + 1).toLowerCase());
                    }else{
                        parameters.put(parameter.toLowerCase(), null);
                    }
                }
                hash += parameters.hashCode();
            }
            hashCode = hash;
        }
        public String getMediaType(){
            return mediaType;
        }
        public Map getParameters(){
            return parameters;
        }
        public int hashCode(){
            return hashCode;
        }
        public boolean equals(Object obj){
            if(obj == null || !(obj instanceof ContentType)){
                return false;
            }
            if(obj == this){
                return true;
            }
            ContentType cmp = (ContentType)obj;
            if(!mediaType.equalsIgnoreCase(cmp.mediaType)){
                return false;
            }
            if(parameters == null && cmp.parameters == null){
                return true;
            }else if((parameters == null && cmp.parameters != null)
                || (parameters != null && cmp.parameters == null)
            ){
                return false;
            }else{
                return parameters.equals(cmp.parameters);
            }
        }
        public String toString(){
            StringBuilder buf = new StringBuilder(mediaType);
            if(parameters != null){
                Iterator itr = parameters.entrySet().iterator();
                while(itr.hasNext()){
                    Map.Entry entry = (Map.Entry)itr.next();
                    buf.append(entry.getKey());
                    if(entry.getValue() != null){
                        buf.append('=').append(entry.getValue());
                    }
                    if(itr.hasNext()){
                        buf.append("; ");
                    }
                }
            }
            return buf.toString();
        }
    }
}
