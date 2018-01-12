package jp.ossc.nimbus.service.aop.interceptor.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletInputStream;

public class MockServletInputStream extends ServletInputStream {

	static int off = 0;
	protected byte[] b;
	
	public MockServletInputStream(byte[] b) {
		super();
		this.b = b;
	}

	public int read() throws IOException {
		return 0;
	}
	public int read(byte[]b) throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(this.b);
		if (off < this.b.length){
			int len = is.read(b, off, b.length);
			off = off + len;
			return len;
		}
		else{
			return -1;
		}
	}
	
}
