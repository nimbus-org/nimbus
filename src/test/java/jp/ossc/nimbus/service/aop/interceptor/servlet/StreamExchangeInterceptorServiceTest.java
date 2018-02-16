package jp.ossc.nimbus.service.aop.interceptor.servlet;

import jp.ossc.nimbus.beans.dataset.*;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.*;

import jp.ossc.nimbus.service.aop.ServletFilterInvocationContext;
import jp.ossc.nimbus.service.journal.*;
import jp.ossc.nimbus.service.journal.editorfinder.*;
import jp.ossc.nimbus.service.context.*;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.util.converter.DataSetXMLConverter;
import jp.ossc.nimbus.service.aop.*;

//import com.mockobjects.servlet.*;
import junit.framework.TestCase;

public class StreamExchangeInterceptorServiceTest extends TestCase {

	public StreamExchangeInterceptorServiceTest(String arg0) {
		super(arg0);
	}
    public static void main(String[] args) {
        junit.textui.TestRunner.run(StreamExchangeInterceptorServiceTest.class);
    }


	/**
	 * StreamExchangeInterceptorServiceTestの各プロパティを設定、取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>StreamExchangeInterceptorServiceTestインスタンスを生成する</li>
	 * <li>各setterメソッドを実行</li>
	 * <li>各getterメソッドを実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。設定した値が正しく取得できることを確認</li>
	 * </ul>
	 */
	public void testSetterGetter() {

		StreamExchangeInterceptorService ic = new StreamExchangeInterceptorService();
		
		DataSetXMLConverter xconv = new DataSetXMLConverter();
		ic.setRequestStreamConverter(xconv);
		assertEquals(xconv, ic.getRequestStreamConverter());
		ic.setResponseStreamConverter(xconv);
		assertEquals(xconv, ic.getResponseStreamConverter());
		
		Context context = new ThreadContextService();
		ic.setThreadContext(context);
		assertEquals(context, ic.getThreadContext());
		
		Journal journal = new ThreadManagedJournalService();
		ic.setJournal(journal);
		assertEquals(journal, ic.getJournal());
		
		EditorFinder finder = new ObjectMappedEditorFinderService();
		ic.setExchangeEditorFinder(finder);
		assertEquals(finder, ic.getExchangeEditorFinder());
		ic.setExchangeRequestEditorFinder(finder);
		assertEquals(finder, ic.getExchangeRequestEditorFinder());
		ic.setExchangeResponseEditorFinder(finder);
		assertEquals(finder, ic.getExchangeResponseEditorFinder());
		ic.setRequestBytesEditorFinder(finder);
		assertEquals(finder, ic.getRequestBytesEditorFinder());
		ic.setRequestObjectEditorFinder(finder);
		assertEquals(finder, ic.getRequestObjectEditorFinder());
		ic.setResponseBytesEditorFinder(finder);
		assertEquals(finder, ic.getResponseBytesEditorFinder());
		ic.setResponseObjectEditorFinder(finder);
		assertEquals(finder, ic.getResponseObjectEditorFinder());
		ic.setExceptionEditorFinder(finder);
		assertEquals(finder, ic.getExceptionEditorFinder());
		
		ServiceName sname = new ServiceName("Service");
		ic.setRequestStreamConverterServiceName(sname);
		assertEquals(sname, ic.getRequestStreamConverterServiceName());
		ic.setResponseStreamConverterServiceName(sname);
		assertEquals(sname, ic.getResponseStreamConverterServiceName());
		ic.setThreadContextServiceName(sname);
		assertEquals(sname, ic.getThreadContextServiceName());
		ic.setJournalServiceName(sname);
		assertEquals(sname, ic.getJournalServiceName());
		ic.setExchangeEditorFinderServiceName(sname);
		assertEquals(sname, ic.getExchangeEditorFinderServiceName());
		ic.setExchangeRequestEditorFinderServiceName(sname);
		assertEquals(sname, ic.getExchangeRequestEditorFinderServiceName());
		ic.setExchangeResponseEditorFinderServiceName(sname);
		assertEquals(sname, ic.getExchangeResponseEditorFinderServiceName());
		ic.setRequestBytesEditorFinderServiceName(sname);
		assertEquals(sname, ic.getRequestBytesEditorFinderServiceName());
		ic.setRequestObjectEditorFinderServiceName(sname);
		assertEquals(sname, ic.getRequestObjectEditorFinderServiceName());
		ic.setResponseBytesEditorFinderServiceName(sname);
		assertEquals(sname, ic.getResponseBytesEditorFinderServiceName());
		ic.setResponseObjectEditorFinderServiceName(sname);
		assertEquals(sname, ic.getResponseObjectEditorFinderServiceName());
		ic.setExceptionEditorFinderServiceName(sname);
		assertEquals(sname, ic.getExceptionEditorFinderServiceName());

		ic.setResponseContentType("application/x-www-form-urlencoded");
		assertEquals("application/x-www-form-urlencoded", ic.getResponseContentType());

		ic.setRequestObjectAttributeName("TEST");
		assertEquals("TEST", ic.getRequestObjectAttributeName());
		ic.setResponseObjectAttributeName("TEST");
		assertEquals("TEST", ic.getResponseObjectAttributeName());
		ic.setRequestObjectContextKey("TEST");
		assertEquals("TEST", ic.getRequestObjectContextKey());
		ic.setResponseObjectContextKey("TEST");
		assertEquals("TEST", ic.getResponseObjectContextKey());

		ic.setRequestStreamInflate(false);
		assertEquals(false, ic.isRequestStreamInflate());

		ic.setExchangeJournalKey("TEST");
		assertEquals("TEST", ic.getExchangeJournalKey());
		ic.setExchangeRequestJournalKey("TEST");
		assertEquals("TEST", ic.getExchangeRequestJournalKey());
		ic.setExchangeResponseJournalKey("TEST");
		assertEquals("TEST", ic.getExchangeResponseJournalKey());
		ic.setRequestBytesJournalKey("TEST");
		assertEquals("TEST", ic.getRequestBytesJournalKey());
		ic.setRequestObjectJournalKey("TEST");
		assertEquals("TEST", ic.getRequestObjectJournalKey());
		ic.setResponseBytesJournalKey("TEST");
		assertEquals("TEST", ic.getResponseBytesJournalKey());
		ic.setResponseObjectJournalKey("TEST");
		assertEquals("TEST", ic.getResponseObjectJournalKey());
		ic.setExceptionJournalKey("TEST");
		assertEquals("TEST", ic.getExceptionJournalKey());
		

	}

	/**
	 * サービス開始、終了するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のサービスを定義した定義ファイルをロードしてサービスを開始する</li>
	 * <li>requestStreamConverterService</li>
	 * <li>responseStreamConverterService</li>
	 * <li>threadContextService</li>
	 * <li>journalService</li>
	 * <li>exchangeEditorFinderService</li>
	 * <li>exchangeRequestEditorFinderService</li>
	 * <li>exchangeResponseEditorFinderService</li>
	 * <li>requestBytesEditorFinderService</li>
	 * <li>requestObjectEditorFinderService</li>
	 * <li>responseBytesEditorFinderService</li>
	 * <li>responseObjectEditorFinderService</li>
	 * <li>exceptionEditorFinderService</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。</li>
	 * </ul>
	 */
	public void testStartService() {
		try {
			if (!ServiceManagerFactory
					.loadManager("jp/ossc/nimbus/service/aop/interceptor/servlet/service-definitionTest.xml")) {
				System.exit(-1);
			}
			final StreamExchangeInterceptorService ic
			= (StreamExchangeInterceptorService) ServiceManagerFactory
					.getServiceObject("StreamExchangeInterceptor");
			ic.startService();
			assertTrue(ic.getRequestStreamConverter() instanceof DataSetXMLConverter);
			assertTrue(ic.getResponseStreamConverter() instanceof DataSetXMLConverter);

			ic.stopService();

		} catch (Exception e) {
			e.printStackTrace();
			fail("例外発生");
		} finally {
			ServiceManagerFactory
					.unloadManager("jp/ossc/nimbus/service/aop/interceptor/servlet/service-definitionTest.xml");
		}
	}

	/**
	 * サービス開始、終了するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>コンバータのサービスを定義していない定義ファイルをロードしてサービスを開始する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>サービスの開始に失敗する。例外IllegalArgumentExceptionが発生することを確認する</li>
	 * </ul>
	 */
//	public void testStartServiceInvalid() {
//		try {
//			if (!ServiceManagerFactory
//					.loadManager("jp/ossc/nimbus/service/aop/interceptor/servlet/service-definition_Invalid.xml")) {
//				System.exit(-1);
//			}
//			final StreamExchangeInterceptorService ic
//			= (StreamExchangeInterceptorService) ServiceManagerFactory
//					.getServiceObject("StreamExchangeInterceptor");
//			ic.startService();
//			fail("例外が発生しないためテスト失敗 ");
//
//		} catch (IllegalArgumentException e) {
//		} catch (Exception e) {
//		} finally {
//			ServiceManagerFactory
//					.unloadManager("jp/ossc/nimbus/service/aop/interceptor/servlet/service-definition_Invalid.xml");
//		}
//	}


	/**
	 * Converterを使ってストリームと特定オブジェクトの交換を行うテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のサービスを定義した定義ファイルをロードしてサービスを開始する</li>
	 * <li>requestStreamConverterService</li>
	 * <li>responseStreamConverterService</li>
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
					.loadManager("jp/ossc/nimbus/service/aop/interceptor/servlet/service-definitionTest.xml")) {
				System.exit(-1);
			}
			final StreamExchangeInterceptorService ic
			= (StreamExchangeInterceptorService) ServiceManagerFactory
					.getServiceObject("StreamExchangeInterceptor");
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
			ic.setRequestStreamInflate(false);
			ic.invokeFilter(context, (InterceptorChain)ichain);
			//変換後のデータ検証
			DataSet ds = (DataSet)req.getAttribute(ic.requestObjectAttributeName);
			assertEquals("TestName", ds.getHeader("TestHeader").getProperty("name"));
			assertEquals("TestPassWord", ds.getHeader("TestHeader").getProperty("password"));
			
			
			ic.stopService();
			

		} catch (Throwable e) {
			e.printStackTrace();
			fail("例外発生");
		} finally {
			ServiceManagerFactory
					.unloadManager("jp/ossc/nimbus/service/aop/interceptor/servlet/service-definitionTest.xml");
		}
	}



	/**
	 * 入力ストリームの圧縮を解除するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>HttpServletRequestオブジェクトと圧縮データのストリームを指定して<BR>
	 * decompress(HttpServletRequest request, InputStream is) を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常に解凍が行われたことを確認する</li>
	 * </ul>
	 */
	public void testDecompress() {
		try{
		StreamExchangeInterceptorService ic = new StreamExchangeInterceptorService();
		//ServletRequest/Responseのモックを生成
		MockHttpServletRequest req = new MockHttpServletRequest();			
		//エンコーディングセット
		//圧縮データのストリーム生成(HttpRequestImpl#compress()で圧縮データストリームを作成)
		String inxml = "<?xml version=\"1.0\" encoding=\"Shift_JIS\"?>\n" +
		"<dataSet><schema><header name=\"TestHeader\">" +
		":name,java.lang.String,,,\n:password,java.lang.String,,,"  +
						"</header></schema><header name=\"TestHeader\">" +
						"<name>TestName</name><password>TestPassWord</password></header></dataSet>";
		InputStream is = compress(inxml.getBytes());
		BufferedInputStream ois = new BufferedInputStream(ic.decompress(req, is));
		BufferedReader br = new BufferedReader(new InputStreamReader(ois));
		assertEquals("<?xml version=\"1.0\" encoding=\"Shift_JIS\"?>", br.readLine());
		assertEquals("<dataSet><schema><header name=\"TestHeader\">:name,java.lang.String,,,", br.readLine());
		assertEquals(":password,java.lang.String,,,</header></schema><header name=\"TestHeader\">" +
				"<name>TestName</name><password>TestPassWord</password></header></dataSet>", br.readLine());
		
		}catch (IOException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

    /**
     * 入力ストリームを圧縮する。<p>
     */
    protected InputStream compress(byte[] inputBytes) throws IOException {
        // ヘッダー[Content-Encoding]の値を取得
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream os = baos;
        // gzip圧縮
        os = new GZIPOutputStream(os);
        os.write(inputBytes, 0, inputBytes.length);
        os.flush();
        os.close();
        return new ByteArrayInputStream(baos.toByteArray());
    }

 
    
	
}
