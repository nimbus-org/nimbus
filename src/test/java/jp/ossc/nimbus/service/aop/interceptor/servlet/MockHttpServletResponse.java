package jp.ossc.nimbus.service.aop.interceptor.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class MockHttpServletResponse implements HttpServletResponse {

	public void addCookie(Cookie arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void addDateHeader(String arg0, long arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void addHeader(String arg0, String arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void addIntHeader(String arg0, int arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public boolean containsHeader(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	public String encodeRedirectURL(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String encodeRedirectUrl(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String encodeURL(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String encodeUrl(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public void sendError(int arg0) throws IOException {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void sendError(int arg0, String arg1) throws IOException {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void sendRedirect(String arg0) throws IOException {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void setDateHeader(String arg0, long arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void setHeader(String arg0, String arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void setIntHeader(String arg0, int arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void setStatus(int arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void setStatus(int arg0, String arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void flushBuffer() throws IOException {
		// TODO 自動生成されたメソッド・スタブ

	}

	public int getBufferSize() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	public String getCharacterEncoding() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getContentType() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public Locale getLocale() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public PrintWriter getWriter() throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public boolean isCommitted() {
		// TODO 自動生成されたメソッド・スタブ
		return true;
	}

	public void reset() {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void resetBuffer() {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void setBufferSize(int arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void setCharacterEncoding(String arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void setContentLength(int arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void setContentType(String arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void setLocale(Locale arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
