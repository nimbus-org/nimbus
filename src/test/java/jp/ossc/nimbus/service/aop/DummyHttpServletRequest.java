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
package jp.ossc.nimbus.service.aop;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

public class DummyHttpServletRequest extends DummyServletRequest
 implements HttpServletRequest{
    
    protected String authType;
    protected List cookies = new ArrayList();
    protected Map headers = new HashMap();
    protected String method;
    protected String pathInfo;
    protected String pathTranslated;
    protected String contextPath;
    protected String queryString;
    protected String remoteUser;
    protected Set roles = new HashSet();
    protected Principal principal;
    protected String requestedSessionId;
    protected String requestURI;
    protected String servletPath;
    protected boolean isRequestedSessionIdValid;
    
    public String getAuthType(){
        return authType;
    }
    public void setAuthType(String type){
        authType = type;
    }
    public Cookie[] getCookies(){
        return (Cookie[])cookies.toArray(new Cookie[cookies.size()]);
    }
    public void addCookies(Cookie cookie){
        cookies.add(cookie);
        if("jsessionid".equals(cookie.getName())){
            requestedSessionId = cookie.getValue();
        }
    }
    public long getDateHeader(String name){
        String val = getHeader(name);
        if(val == null){
            return -1;
        }
        SimpleDateFormat format = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss zzz",
            getLocale()
        );
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try{
            return format.parse(val).getTime();
        }catch(ParseException e){
            return -1;
        }
    }
    public String getHeader(String name){
        List vals = (List)headers.get(name);
        return vals != null && vals.size() != 0 ? (String)vals.get(0) : null;
    }
    public Enumeration getHeaders(String name){
        List vals = (List)headers.get(name);
        if(vals == null){
            return new IteratorEnumeration();
        }
        return new IteratorEnumeration(vals.iterator());
    }
    public Enumeration getHeaderNames(){
        return new IteratorEnumeration(headers.keySet().iterator());
    }
    public int getIntHeader(String name){
        String val = getHeader(name);
        if(val == null){
            return -1;
        }
        return Integer.parseInt(val);
    }
    public void addHeader(String name, String val){
        List vals = (List)headers.get(name);
        if(vals == null){
            vals = new ArrayList();
            headers.put(name, vals);
        }
        vals.add(val);
    }
    public void setHeader(String name, String val){
        List vals = (List)headers.get(name);
        if(vals == null){
            vals = new ArrayList();
            headers.put(name, vals);
        }else{
            vals.clear();
        }
        vals.add(val);
    }
    public String getMethod(){
        return method;
    }
    public void setMethod(String val){
        method = val;
    }
    public String getPathInfo(){
        return pathInfo;
    }
    public void setPathInfo(String val){
        pathInfo = val;
    }
    public String getPathTranslated(){
        return pathTranslated;
    }
    public void setPathTranslated(String val){
        pathTranslated = val;
    }
    public String getContextPath(){
        return contextPath;
    }
    public void setContextPath(String path){
        contextPath = path;
    }
    public String getQueryString(){
        return queryString;
    }
    public void setQueryString(String query){
        queryString = query;
    }
    public String getRemoteUser(){
        return remoteUser;
    }
    public void setRemoteUser(String user){
        remoteUser = user;
    }
    public boolean isUserInRole(String role){
        return roles.contains(role);
    }
    public void addRole(String role){
        roles.add(role);
    }
    public Principal getUserPrincipal(){
        return principal;
    }
    public void setUserPrincipal(Principal pri){
        principal = pri;
    }
    public String getRequestedSessionId(){
        return requestedSessionId;
    }
    public void setRequestedSessionId(String id){
        requestedSessionId = id;
    }
    public String getRequestURI(){
        return requestURI;
    }
    public void setRequestURI(String uri){
        requestURI = uri;
    }
    public StringBuffer getRequestURL(){
        StringBuffer url = new StringBuffer();
        String scheme = getScheme();
        int port = getServerPort();
        if(port < 0){
            port = 80;
        }
        url.append(scheme);
        url.append("://");
        url.append(getServerName());
        if((scheme.equals("http") && (port != 80))
            || (scheme.equals("https") && (port != 443))){
            url.append(':');
            url.append(port);
        }
        url.append(getRequestURI());
        
        return url;
    }
    public String getServletPath(){
        return servletPath;
    }
    public void setServletPath(String path){
        servletPath = path;
    }
    public HttpSession getSession(boolean create){
        return null;
    }
    public HttpSession getSession(){
        return getSession(true);
    }
    public boolean isRequestedSessionIdValid(){
        return isRequestedSessionIdValid;
    }
    public boolean isRequestedSessionIdFromCookie(){
        if(requestedSessionId != null){
            for(int i = 0, imax = cookies.size(); i < imax; i++){
                Cookie cookie = (Cookie)cookies.get(i);
                if("jsessionid".equals(cookie.getName())){
                    return true;
                }
            }
        }
        return false;
    }
    public boolean isRequestedSessionIdFromURL(){
        if(requestedSessionId != null){
            for(int i = 0, imax = cookies.size(); i < imax; i++){
                Cookie cookie = (Cookie)cookies.get(i);
                if("jsessionid".equals(cookie.getName())){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    public boolean isRequestedSessionIdFromUrl(){
        return isRequestedSessionIdFromURL();
    }
}