package jp.ossc.nimbus.service.aop.interceptor.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class MockHttpServletRequest implements HttpServletRequest {
	protected ServletInputStream inputStream;
	
    protected String address;
    protected String name;
    protected int port;
    protected String characterEncoding;
    protected HashMap map = new HashMap();

	public MockHttpServletRequest() {
		super();
	}
	public MockHttpServletRequest(ServletInputStream inputStream) {
		super();
		this.inputStream = inputStream;
	}

    public String getLocalAddr() {
    	return address;
    }
    public int getLocalPort() {
    	return port;
    }
    public int getRemotePort() {
    	return port;
    }
    public String getLocalName() {
    	return name;
    }
	public String getAuthType() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getContextPath() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public Cookie[] getCookies() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public long getDateHeader(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	public String getHeader(String key) {
        //レスポンス圧縮テスト用。Accept-Encodingが指定されたらgzipを返す
    	String val = "gzip";
    	if(key.equals("Accept-Encoding")){
    		return val;
    	}
        return null;
	}

	public Enumeration getHeaderNames() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public Enumeration getHeaders(String arg0) {
    	//notImplemented();
        //圧縮解除テスト用。Content-Encodingが指定されたらgzipを返す
    	Enumeration vals = new StringTokenizer("gzip");
    	if(arg0.equals("Content-Encoding")){
    		return vals;
    		
    	}
    		
         return null;
	}

	public int getIntHeader(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	public String getMethod() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getPathInfo() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getPathTranslated() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getQueryString() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getRemoteUser() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getRequestURI() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public StringBuffer getRequestURL() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getRequestedSessionId() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getServletPath() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public HttpSession getSession() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public HttpSession getSession(boolean arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public Principal getUserPrincipal() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public boolean isRequestedSessionIdFromCookie() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	public boolean isRequestedSessionIdFromURL() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	public boolean isRequestedSessionIdFromUrl() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	public boolean isRequestedSessionIdValid() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	public boolean isUserInRole(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	public Object getAttribute(String arg0) {
  	  return map.get(arg0);
	}

	public Enumeration getAttributeNames() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getCharacterEncoding() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public int getContentLength() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	public String getContentType() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public ServletInputStream getInputStream() throws IOException {
		return this.inputStream;
	}


	public Locale getLocale() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public Enumeration getLocales() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getParameter(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public Map getParameterMap() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public Enumeration getParameterNames() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String[] getParameterValues(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getProtocol() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public BufferedReader getReader() throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getRealPath(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getRemoteAddr() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getRemoteHost() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}


	public RequestDispatcher getRequestDispatcher(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getScheme() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getServerName() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public int getServerPort() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	public boolean isSecure() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	public void removeAttribute(String arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void setAttribute(String arg0, Object arg1) {
  	  map.put(arg0, arg1);

	}

	public void setCharacterEncoding(String arg0){
    this.characterEncoding = arg0;

	}

}
