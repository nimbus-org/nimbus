package jp.ossc.nimbus.service.aop.interceptor.servlet;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChain;
import jp.ossc.nimbus.service.aop.InterceptorChain;
import jp.ossc.nimbus.service.aop.ServletFilterInvocationContext;
import junit.framework.TestCase;

public class HttpServletResponseDeflateInterceptorServiceTest extends TestCase {

	public HttpServletResponseDeflateInterceptorServiceTest(String arg0) {
		super(arg0);
	}
    public static void main(String[] args) {
        junit.textui.TestRunner.run(HttpServletResponseDeflateInterceptorServiceTest.class);
    }
	/**
	 * HttpServletResponseDeflateInterceptorServiceの各プロパティを設定、取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>HttpServletResponseDeflateInterceptorServiceインスタンスを生成する</li>
	 * <li>各setterメソッドを実行</li>
	 * <li>各getterメソッドを実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。設定した値が正しく取得できることを確認</li>
	 * </ul>
	 */
	public void testSetterGetter() {

		HttpServletResponseDeflateInterceptorService ic = new HttpServletResponseDeflateInterceptorService();
		
		String[] contentTypes = new String[]{"text/html"};
		ic.setEnabledContentTypes(contentTypes);
		assertEquals(contentTypes[0], ic.getEnabledContentTypes()[0]);
		String[] contentTypes2 = new String[]{"text/xml"};
		ic.setDisabledContentTypes(contentTypes2);
		assertEquals(contentTypes2[0], ic.getDisabledContentTypes()[0]);
		ic.setDeflateLength(1000000);
		assertEquals(1000000, ic.getDeflateLength());		

	}

	/**
	 * レスポンスを圧縮処理を行うラッパーでラップして、次のインターセプタを呼び出すテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>HttpServletResponseDeflateInterceptorServiceインスタンスを生成する</li>
	 * <li>ServletRequest/Responseのモックを生成</li>
	 * <li>ServletRequestのInputStreamパラメータに変換元XMLストリームを設定</li>
	 * <li>上記インスタンスを使ってコンテキストインスタンスを生成し、<BR>
	 * invokeFilter(context, chain)を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>HttpServletRequestに設定されている変換元のXMLストリームが正しく変換することを確認する</li>
	 * </ul>
	 */
	public void testInvokeFilter() {
		try {
			if (!ServiceManagerFactory
					.loadManager("jp/ossc/nimbus/service/aop/interceptor/servlet/service-definitionTestdef.xml")) {
				System.exit(-1);
			}
			final HttpServletResponseDeflateInterceptorService ic
			= (HttpServletResponseDeflateInterceptorService) ServiceManagerFactory
					.getServiceObject("HttpServletResponseDeflateInterceptor");
			ic.startService();
			
			//ServletRequestのInputStreamパラメータに変換元XMLストリームを設定
			String inxml = "<?xml version=\"1.0\" encoding=\"Shift_JIS\"?>\n" +
			"<dataSet><schema><header name=\"TestHeader\">" +
			":name,java.lang.String,,,\n:password,java.lang.String,,,"  +
							"</header></schema><header name=\"TestHeader\">" +
							"<name>TestName</name><password>TestPassWord</password></header></dataSet>";
			//ServletinputStreamのセット
			MockServletInputStream is = new MockServletInputStream(inxml.getBytes());
			//ServletRequest/Responseのモックを生成
			MockHttpServletRequest req = new MockHttpServletRequest(is);			
			ServletResponse res = new MockHttpServletResponse();
			MockFilterChain chain = new MockFilterChain();
			//エンコーディングセット
			req.setCharacterEncoding("Shift_JIS");
			//コンテキスト作成			
			ServletFilterInvocationContext context = 
				new ServletFilterInvocationContext((ServletRequest)req,res,(javax.servlet.FilterChain)chain);

			//インタセプタインスタンス生成
			DefaultInterceptorChain ichain = new DefaultInterceptorChain();
			//入力ストリームは非圧縮
			ic.invokeFilter(context, (InterceptorChain)ichain);
			
			

		} catch (Throwable e) {
			e.printStackTrace();
			fail("例外発生");
		} finally {
			ServiceManagerFactory
					.unloadManager("jp/ossc/nimbus/service/aop/interceptor/servlet/service-definitionTestdef.xml");
		}
	}

	/**
	 * レスポンス圧縮処理を行わずに、次のインターセプタを呼び出すテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>HttpServletResponseDeflateInterceptorServiceインスタンスを生成する</li>
	 * <li>ServletRequest/Responseのモックを生成</li>
	 * <li>ServletRequestのInputStreamパラメータに変換元XMLストリームを設定</li>
	 * <li>上記インスタンスを使ってコンテキストインスタンスを生成し、<BR>
	 * invokeFilter(context, chain)を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>HttpServletRequestに設定されている変換元のXMLストリームが正しく変換することを確認する</li>
	 * </ul>
	 */
	public void testInvokeFilterNoDef() {
		try {
			if (!ServiceManagerFactory
					.loadManager("jp/ossc/nimbus/service/aop/interceptor/servlet/service-definitionTestdef.xml")) {
				System.exit(-1);
			}
			final HttpServletResponseDeflateInterceptorService ic
			= (HttpServletResponseDeflateInterceptorService) ServiceManagerFactory
					.getServiceObject("HttpServletResponseDeflateInterceptor");
			ic.startService();
			
			//ServletRequestのInputStreamパラメータに変換元XMLストリームを設定
			String inxml = "<?xml version=\"1.0\" encoding=\"Shift_JIS\"?>\n" +
			"<dataSet><schema><header name=\"TestHeader\">" +
			":name,java.lang.String,,,\n:password,java.lang.String,,,"  +
							"</header></schema><header name=\"TestHeader\">" +
							"<name>TestName</name><password>TestPassWord</password></header></dataSet>";
			//ServletinputStreamのセット
			MockServletInputStream is = new MockServletInputStream(inxml.getBytes());
			//ServletRequest/Responseのモックを生成
			MockHttpServletRequest2 req = new MockHttpServletRequest2(is);			
			ServletResponse res = new MockHttpServletResponse();
			MockFilterChain chain = new MockFilterChain();
			//エンコーディングセット
			req.setCharacterEncoding("Shift_JIS");
			//コンテキスト作成			
			ServletFilterInvocationContext context = 
				new ServletFilterInvocationContext((ServletRequest)req,res,(javax.servlet.FilterChain)chain);

			//インタセプタインスタンス生成
			DefaultInterceptorChain ichain = new DefaultInterceptorChain();
			//入力ストリームは非圧縮
			ic.invokeFilter(context, (InterceptorChain)ichain);
			
			

		} catch (Throwable e) {
			e.printStackTrace();
			fail("例外発生");
		} finally {
			ServiceManagerFactory
					.unloadManager("jp/ossc/nimbus/service/aop/interceptor/servlet/service-definitionTestdef.xml");
		}
	}


}
