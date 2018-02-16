package jp.ossc.nimbus.service.http.httpclient;

import java.io.*;
import java.net.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.*;

import junit.framework.TestCase;
import jp.ossc.nimbus.beans.dataset.*;
import jp.ossc.nimbus.beans.dataset.Header;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.converter.*;
import jp.ossc.nimbus.service.http.*;
import jp.ossc.nimbus.service.http.httpclient.HttpClientFactoryService.HttpClientImpl;

//
/**
 * 
 * @author S.Teshima
 * @version 1.00 作成: 2008/01/28 - S.Teshima
 */

public class HttpClientFactoryServiceTest extends TestCase {

	public HttpClientFactoryServiceTest(String arg0) {
		super(arg0);
	}

	
	 public static void main(String[] args) {
	 junit.textui.TestRunner.run(HttpClientFactoryServiceTest.class); }
	 

	/**
	 * HttpClientFactoryServiceの各プロパティを設定、取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>各getterメソッドを実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。設定した値が正しく取得できることを確認</li>
	 * </ul>
	 */
	public void testSetterGetter() {
		HttpClientFactoryService hcf = new HttpClientFactoryService();
		hcf.setConnectionTimeout(60);
		hcf.setLinger(70);
		hcf.setReceiveBufferSize(5000);
		hcf.setSendBufferSize(5500);
		hcf.setSoTimeout(80);
		hcf.setRequestContentType("application/x-www-form-urlencoded");
		hcf.setRequestCharacterEncoding("UTF-8");
		hcf.setHttpVersion("1.0");

		String[] values = { "gzip" };
		hcf.setRequestHeaders("Content-Encoding", values);

		hcf.setHttpClientParam("A", "a");
		hcf.setRequestDeflateLength(1000000);

		ServiceName name = new ServiceName("DataSetXMLConverter");
		hcf.setRequestStreamConverterServiceName(name);
		hcf.setResponseStreamConverterServiceName(name);

		ServiceName jname = new ServiceName("Journal");
		hcf.setJournalServiceName(jname);

		ServiceName sname = new ServiceName("Sequence");
		hcf.setSequenceServiceName(sname);

		ServiceName thname = new ServiceName("ThreadContext");
		hcf.setThreadContextServiceName(thname);

		ServiceName smname = new ServiceName("Semaphore");
		hcf.setSemaphoreServiceName(smname);

		DataSetXMLConverter xconv = new DataSetXMLConverter();
		hcf.setRequestStreamConverter(xconv);
		hcf.setResponseStreamConverter(xconv);

		assertEquals(60, hcf.getConnectionTimeout());
		assertEquals(70, hcf.getLinger());
		assertEquals(5000, hcf.getReceiveBufferSize());
		assertEquals(5500, hcf.getSendBufferSize());
		assertEquals(80, hcf.getSoTimeout());
		assertEquals("application/x-www-form-urlencoded", hcf
				.getRequestContentType());
		assertEquals("UTF-8", hcf.getRequestCharacterEncoding());
		assertEquals("1.0", hcf.getHttpVersion());
		assertEquals(values, hcf.getRequestHeaders("Content-Encoding"));
		assertEquals("a", hcf.getHttpClientParam("A"));
		assertEquals(1000000, hcf.getRequestDeflateLength());
		assertEquals(xconv, hcf.getRequestStreamConverter());
		assertEquals(xconv, hcf.getResponseStreamConverter());
		assertEquals(name, hcf.getRequestStreamConverterServiceName());
		assertEquals(name, hcf.getResponseStreamConverterServiceName());
		assertEquals(jname, hcf.getJournalServiceName());
		assertEquals(sname, hcf.getSequenceServiceName());
		assertEquals(thname, hcf.getThreadContextServiceName());
		assertEquals(smname, hcf.getSemaphoreServiceName());
	}

	/**
	 * サービス開始、終了するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Sequence、Journal、ThreadContext、Semaphoreの各サービスを定義した定義ファイル<BR>
	 * をロードしてHttpClientFactoryサービスを開始する</li>
	 * <li>HttpClientFactoryService#stopService()を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。</li>
	 * </ul>
	 */
	public void testStartService() {
		try {
			if (!ServiceManagerFactory
					.loadManager("jp/ossc/nimbus/service/http/httpclient/service-client.xml")) {
				System.exit(-1);
			}
			final HttpClientFactory factory = (HttpClientFactory) ServiceManagerFactory
					.getServiceObject("HttpClientFactory");
			HttpClientFactoryService hcf = (HttpClientFactoryService) factory;
			hcf.stopService();

		} catch (Exception e) {
			e.printStackTrace();
			fail("例外発生");
		} finally {
			ServiceManagerFactory
					.unloadManager("jp/ossc/nimbus/service/http/httpclient/service-client.xml");
		}
	}

	/**
	 * Proxy情報(ホスト名：ポート番号)を設定、取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>"localhost:80"lを指定してHttpClientFactoryService#setProxy()を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了することを確認</li>
	 * </ul>
	 */
	public void testSetProxy() {
		HttpClientFactoryService hcf = new HttpClientFactoryService();
		hcf.setProxy("localhost:80");
		assertEquals("localhost:80", hcf.getProxy());
	}

	/**
	 * Proxy情報(ホスト名：ポート番号)を設定、取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>nullを指定してHttpClientFactoryService#setProxy()を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。getProxy()でnullが返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetProxyNull() {
		HttpClientFactoryService hcf = new HttpClientFactoryService();
		hcf.setProxy(null);
		assertNull(hcf.getProxy());
	}

	/**
	 * Proxy情報(ホスト名：ポート番号)を設定、取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>":80"(ホスト名を指定しない不正な値)を指定してHttpClientFactoryService#setProxy()を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外IllegalArgumentExceptionが発生することを確認</li>
	 * <li>例外メッセージに"Illegal proxy : :80"が返されることを確認</li>
	 * </ul>
	 */
	public void testSetProxyInvalid1() {
		try {
			HttpClientFactoryService hcf = new HttpClientFactoryService();
			hcf.setProxy(":80");
			fail("例外が発生しないためテスト失敗 ");
		} catch (IllegalArgumentException e) {
			assertEquals("Illegal proxy : :80", e.getMessage());
		}
	}

	/**
	 * Proxy情報(ホスト名：ポート番号)を設定、取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>"localhost:NoInt"(不正なポート番号)を指定してHttpClientFactoryService#setProxy()を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外IllegalArgumentExceptionが発生することを確認</li>
	 * <li>例外メッセージに"Illegal proxy port : localhost:NoInt"が返されることを確認</li>
	 * </ul>
	 */
	public void testSetProxyInvalid2() {
		try {
			HttpClientFactoryService hcf = new HttpClientFactoryService();
			hcf.setProxy("localhost:NoInt");
			fail("例外が発生しないためテスト失敗 ");
		} catch (IllegalArgumentException e) {
			assertEquals("Illegal proxy port : localhost:NoInt", e.getMessage());
		}
	}

	/**
	 * ローカルアドレス情報(IPアドレス)を設定、取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>"127.0.0.1"を指定してHttpClientFactoryService#setLocalAddress()を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。getLocalAddress()でnullが返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetLocalAddress() {
		try {
			HttpClientFactoryService hcf = new HttpClientFactoryService();
			hcf.setLocalAddress("127.0.0.1");
			assertEquals("127.0.0.1", hcf.getLocalAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * ローカルアドレス情報(IPアドレス)を設定、取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>nullを指定してHttpClientFactoryService#setLocalAddress()を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。getLocalAddress()でnullが返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetLocalAddressNull() {
		try {
			HttpClientFactoryService hcf = new HttpClientFactoryService();
			hcf.setLocalAddress(null);
			assertNull(hcf.getLocalAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

//	/**
//	 * ローカルアドレス情報(IPアドレス)を設定、取得するテスト。
//	 * <p>
//	 * 条件：
//	 * <ul>
//	 * <li>HttpClientFactoryServiceインスタンスを生成する</li>
//	 * <li>不正なアドレスを指定してHttpClientFactoryService#setLocalAddress()を実行</li>
//	 * </ul>
//	 * 確認：
//	 * <ul>
//	 * <li>例外UnknownHostExceptionが発生することを確認</li>
//	 * </ul>
//	 */
//	public void testSetLocalAddressInvalid() {
//		try {
//			HttpClientFactoryService hcf = new HttpClientFactoryService();
//			hcf.setLocalAddress("1:1:1:1:1:1");
//			fail("例外が発生しないためテスト失敗 ");
//		} catch (UnknownHostException e) {
//		}
//	}

	/**
	 * 指定された論理アクション名に該当するHTTPリクエストを設定、取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>アクション名"login"、HttpRequestImplオブジェクトを指定して以下を実行</li>
	 * <li>HttpClientFactoryService#getRequest(String action)を実行</li>
	 * <li>HttpClientFactoryService#setRequest(String action, HttpRequestImpl
	 * request)を実行</li>
	 * <li>HttpClientFactoryService#getRequest(String action)を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>１回目のgetRequest(String action)でnullが返ってくることを確認</li>
	 * <li>２回目のgetRequest(String action)で指定したHttpRequestImplオブジェクトが返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetRequest() {
		HttpClientFactoryService hcf = new HttpClientFactoryService();
		String action = "login";
		assertNull(hcf.getRequest(action));

		HttpRequestImpl request = new PostHttpRequestImpl();
		hcf.setRequest(action, request);
		assertEquals(request, hcf.getRequest(action));
	}

	/**
	 * 指定された論理アクション名に該当するHTTPレスポンスを設定、取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>アクション名"login"、HttpResponseImplオブジェクトを指定して以下を実行</li>
	 * <li>HttpClientFactoryService#getResponse(String action)を実行</li>
	 * <li>HttpClientFactoryService#setResponse(String action, HttpResponseImpl
	 * Response)を実行</li>
	 * <li>HttpClientFactoryService#getResponse(String action)を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>１回目のgetResponse(String action)でnullが返ってくることを確認</li>
	 * <li>２回目のgetResponse(String action)で指定したHttpResponseImplオブジェクトが返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetResponse() {
		HttpClientFactoryService hcf = new HttpClientFactoryService();
		String action = "login";
		assertNull(hcf.getResponse(action));

		HttpResponseImpl response = new HttpResponseImpl();
		hcf.setResponse(action, response);
		assertEquals(response, hcf.getResponse(action));
	}

	/**
	 * 認証情報を設定するを設定、取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>認証スコープ、認証情報の各オブジェクトを指定して以下を実行</li>
	 * <li>HttpClientFactoryService#setCredentials(AuthScope authscope,
	 * Credentials credentials)を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>credentialsMapに指定した値が設定されていることを確認</li>
	 * </ul>
	 */
	public void testSetCredentials() {
		HttpClientFactoryService hcf = new HttpClientFactoryService();
		AuthScope authscope = new AuthScope("localhost", 80);
		Credentials credentials = new UsernamePasswordCredentials("hoge",
				"hoge");

		hcf.setCredentials(authscope, credentials);
		assertEquals(credentials, hcf.credentialsMap.get(authscope));
	}

	/**
	 * プロキシ認証情報を設定するを設定、取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>認証スコープ、認証情報の各オブジェクトを指定して以下を実行</li>
	 * <li>HttpClientFactoryService#setProxyCredentials(AuthScope authscope,
	 * Credentials credentials)を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>proxyCredentialsMapに指定した値が設定されていることを確認</li>
	 * </ul>
	 */
	public void testSetProxyCredentials() {
		HttpClientFactoryService hcf = new HttpClientFactoryService();
		AuthScope authscope = new AuthScope("localhost", 80);
		Credentials credentials = new UsernamePasswordCredentials("hoge",
				"hoge");

		hcf.setProxyCredentials(authscope, credentials);
		assertEquals(credentials, hcf.proxyCredentialsMap.get(authscope));
	}

	/**
	 * 指定された論理アクション名に該当するHTTPリクエストを発行するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の内容の定義ファイルをロードし、HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>HttpVersion：1.1</li>
	 * <li>RequestDeflateLength：1000000</li>
	 * <li>RequestContentType：application/xml</li>
	 * <li>RequestCharacterEncoding：UTF-8</li>
	 * <li>RequestHeaders(Accept-Encoding)：gzip</li>
	 * <li>RequestHeaders(Content-Encoding)：gzip</li>
	 * <li>RequestStreamConverterServiceName：#DateSetXMLConverter</li>
	 * <li>HttpClientFactoryService#createRequest(String action)を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>HttpRequestオブジェクトにに指定した値が設定されていることを確認</li>
	 * </ul>
	 */
	public void testCreateRequest() {
		try {
			if (!ServiceManagerFactory
					.loadManager("jp/ossc/nimbus/service/http/httpclient/service-client1.xml")) {
				System.exit(-1);
			}
			final HttpClientFactory factory = (HttpClientFactory) ServiceManagerFactory
					.getServiceObject("HttpClientFactory");
			HttpRequestImpl request = (HttpRequestImpl) factory
					.createRequest("help");

			assertEquals("1.1", request.getHttpVersion());
			assertEquals(1000000, request.getDeflateLength());
			assertEquals("application/xml", request.getContentType());
			assertEquals("UTF-8", request.getCharacterEncoding());
			String[] hparam = (String[]) request.headerMap
					.get("Accept-Encoding");
			assertEquals("gzip", hparam[0]);
			hparam = (String[]) request.headerMap.get("Content-Encoding");
			assertEquals("gzip", hparam[0]);
			assertEquals("DataSetXMLConverter", request
					.getStreamConverterServiceName().getServiceName());

		} catch (HttpRequestCreateException e) {
			e.printStackTrace();
			fail("例外発生");
		}finally{
			ServiceManagerFactory
					.unloadManager("jp/ossc/nimbus/service/http/httpclient/service-client1.xml");
		}

	}

	/**
	 * 指定された論理アクション名に該当するHTTPリクエストを生成するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>不正なアクション名を指定して以下を実行</li>
	 * <li>HttpClientFactoryService#createRequest(String action)を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外HttpRequestCreateExceptionが発生することを確認</li>
	 * <li>例外メッセージに"No action."が返されることを確認</li>
	 * </ul>
	 */
	public void testCreateRequestInvalid() {
		try {
			HttpClientFactoryService hcf = new HttpClientFactoryService();
			hcf.createRequest("InvalidAction");
			fail("例外が発生しないためテスト失敗 ");
		} catch (HttpRequestCreateException e) {
			assertEquals("No action.", e.getMessage());
		}
	}

	/**
	 * 指定された論理アクション名に該当するHttpClientを生成するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の内容の定義ファイルをロードし、HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>LocalAddress：127.0.0.1</li>
	 * <li>Proxy：localhost:8280</li>
	 * <li>HttpVersion：1.1</li>
	 * <li>ConnectionTimeout：500</li>
	 * <li>SoTimeout：1000</li>
	 * <li>Linger：500</li>
	 * <li>RequestDeflateLength：1000000</li>
	 * <li>RequestContentType：application/xml</li>
	 * <li>RequestCharacterEncoding：UTF-8</li>
	 * <li>RequestHeaders(Accept-Encoding)：gzip</li>
	 * <li>RequestHeaders(Content-Encoding)：gzip</li>
	 * <li>RequestStreamConverterServiceName：#DateSetXMLConverter</li>
	 * <li>HttpClientParam(ConnectionTimeout)：500</li>
	 * <li>HttpClientFactoryService#createHttpClient()を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>HttpClienttオブジェクトにに指定した値が設定されていることを確認</li>
	 * </ul>
	 */
	public void testCreateHttpClient() {
		try {
			if (!ServiceManagerFactory
					.loadManager("jp/ossc/nimbus/service/http/httpclient/service-client1.xml")) {
				System.exit(-1);
			}
			final HttpClientFactory factory = (HttpClientFactory) ServiceManagerFactory
					.getServiceObject("HttpClientFactory");
			HttpClientImpl client = (HttpClientImpl) factory.createHttpClient();

			assertEquals("localhost", client.client.getHostConfiguration()
					.getProxyHost());
			assertEquals(8280, client.client.getHostConfiguration()
					.getProxyPort());
			assertEquals("127.0.0.1", client.client.getHostConfiguration()
					.getLocalAddress().getHostAddress());
			assertEquals(500, client.client.getHttpConnectionManager()
					.getParams().getConnectionTimeout());
			assertEquals(1000, client.client.getHttpConnectionManager()
					.getParams().getSoTimeout());
			assertEquals(500, client.client.getHttpConnectionManager()
					.getParams().getLinger());
			assertEquals(5000, client.client.getHttpConnectionManager()
					.getParams().getReceiveBufferSize());
			assertEquals(5000, client.client.getHttpConnectionManager()
					.getParams().getSendBufferSize());

		} catch (HttpRequestCreateException e) {
			e.printStackTrace();
			fail("例外発生");
		} finally {
			ServiceManagerFactory
					.unloadManager("jp/ossc/nimbus/service/http/httpclient/service-client1.xml");
		}
	}

	/**
	 * 指定された論理アクション名に該当するHttpClientを生成し、クッキー情報を設定、取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の内容の定義ファイルをロードし、HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>HttpClientFactoryService#createHttpClient()を実行</li>
	 * <li>次の値を指定して、HttpClientImpl#ddCookie(javax.servlet.http.Cookie
	 * cookie)を実行</li>
	 * <li>次の値を指定してクッキーオブジェクトを生成し、HttpClientImpl#ddCookie(javax.servlet.http.Cookie
	 * cookie)を実行</li>
	 * <li>name:"TestCookie" value:"TestValue"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>HttpClienttオブジェクトにに指定した値が設定されていることを確認</li>
	 * </ul>
	 */
	public void testCreateHttpClientCookie() {
		try {
			HttpClientFactoryService factory = new HttpClientFactoryService();
			HttpClientImpl client = (HttpClientImpl) factory.createHttpClient();

			javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(
					"TestCookie", "TestValue");
			cookie.setDomain("localhost");
			cookie.setMaxAge(1000);
			client.addCookie(cookie);
			javax.servlet.http.Cookie[] result = client.getCookies();

			assertEquals("TestCookie", result[0].getName());
			assertEquals("TestValue", result[0].getValue());
		} catch (HttpRequestCreateException e) {
			e.printStackTrace();
			fail("例外発生");
		} catch (Exception e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された論理アクション名に該当するリクエストを発行するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の内容の定義ファイルをロードし、HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>RequestContentType：application/xml</li>
	 * <li>RequestCharacterEncoding：Shift_JIS</li>
	 * <li>RequestStreamConverterServiceName：#DataSetXMLConverter</li>
	 * <li>ResponseStreamConverterServiceName：#ResponseStreamConverter</li>
	 * <li>ResponseHeaders：ContentType=application/xml</li>
	 * <li>Proxy：#localhost:8280</li>
	 * <li>論理アクション名"login"のリクエスト情報を定義</li>
	 * <li>HttpClientFactoryService#createHttpClient()を実行し、HttpClientを生成</li>
	 * <li>HttpRequestImpl#createRequest(論理アクション名)を実行し、HttpRequestを生成</li>
	 * <li>送信するデータセットを次の内容で生成し、HttpRequest#setObject()でセット<BR>
	 * スキーマ：:name,java.lang.String,,,\n:age,int,,,<BR>
	 * 値　　　：name=hoge,age=25 </li>
	 * <li>生成したHttpRequestを指定して、HttpClientImpl#executeRequest(request)を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>送信先で指定した次の値がHTTPリクエストに正しく設定されていることを確認</li>
	 * </ul>
	 */
	public void testExecuteRequestWithDataSet() {
		try {
			if (!ServiceManagerFactory
					.loadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTest.xml")) {
				System.exit(-1);
			}
			final HttpClientFactory factory = (HttpClientFactory) ServiceManagerFactory
					.getServiceObject("HttpClientFactory");
			HttpClientImpl client = (HttpClientImpl) factory.createHttpClient();

			DataSet requestDs = new DataSet("Login");
	        requestDs.setHeaderSchema(
	            "UserInfo",
	            ":name,java.lang.String,,,\n"
	             + ":age,int,,,"
	        );
	        Header userInfo = requestDs.getHeader("UserInfo");
	        userInfo.setProperty("name", "hoge");
	        userInfo.setProperty("age", 25);
	        
	        HttpRequest request = factory.createRequest("login");
	        request.setObject(requestDs);

			client.executeRequest(request);
			
			/*Proxyテスト用プログラム(jp.ossc.nimbus.service.http.proxy.TestHttpProcessService)
			 * の出力ファイルの内容を確認し、HTTPリクエストデータを検証
			 */
			
			BufferedReader br = new BufferedReader(
					new FileReader("target/temp/jp/ossc/nimbus/service/http/httpclient/help_output.txt"));
			
			String s;
			StringBuffer sb = new StringBuffer();
			//Requestヘッダの検証
			while((s = br.readLine()) != null){
				if(s.startsWith("Content-Type:")){
					assertTrue(s.endsWith("application/xml;charset=Shift_JIS"));
				}
				sb.append(s);				
			}
			br.close();
			//DataSet内容の検証
			assertTrue(sb.toString().endsWith(
					"<dataSet name=\"Login\"><schema>" +
					"<header name=\"UserInfo\">:name,java.lang.String,,,:age,int,,,</header>" +
					"</schema><header name=\"UserInfo\">" +
					"<name>hoge</name><age>25</age></header></dataSet>"));

		} catch (HttpRequestCreateException e) {
			e.printStackTrace();
			fail("例外発生");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("例外発生");
		} catch (IOException e) {
			e.printStackTrace();
			fail("例外発生");
		} finally {
			ServiceManagerFactory
					.unloadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTest.xml");
		}
	}

	/**
	 * 指定された論理アクション名に該当するリクエストを発行するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の内容の定義ファイルをロードし、HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>RequestContentType：application/xml</li>
	 * <li>RequestCharacterEncoding：Shift_JIS</li>
	 * <li>RequestStreamConverterServiceName：#DataSetXMLConverter</li>
	 * <li>ResponseStreamConverterServiceName：#ResponseStreamConverter</li>
	 * <li>Proxy：#localhost:8280</li>
	 * <li>次のパラメータ定義と併せて論理アクション名"login"のリクエスト情報を定義<BR>
	 * sectionCode=22,account=05961,password=05961</li>
	 * <li>HttpClientFactoryService#createHttpClient()を実行し、HttpClientを生成</li>
	 * <li>HttpRequestImpl#createRequest(論理アクション名)を実行し、HttpRequestを生成</li>
	 * <li>生成したHttpRequestを指定して、HttpClientImpl#executeRequest(request)を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>送信先で指定した次の値がHTTPリクエストに正しく設定されていることを確認</li>
	 * <li>レスポンスの内容が正しく設定されていることを確認</li>
	 * </ul>
	 */
	public void testExecuteRequestWithoutData() {
		try {
			if (!ServiceManagerFactory
					.loadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTest2.xml")) {
				System.exit(-1);
			}
			final HttpClientFactory factory = (HttpClientFactory) ServiceManagerFactory
					.getServiceObject("HttpClientFactory");
			HttpClientImpl client = (HttpClientImpl) factory.createHttpClient();

	        
	        HttpRequest request = factory.createRequest("login");

	        HttpResponse res = client.executeRequest(request);
			//レスポンス内容の確認
			assertEquals(200, res.getStatusCode());
			assertEquals("testdata", res.getObject());
			
			/*Proxyテスト用プログラム(jp.ossc.nimbus.service.http.proxy.TestHttpProcessService)
			 * の出力ファイルの内容を確認し、HTTPリクエストデータを検証
			 */
			
			BufferedReader br = new BufferedReader(
					new FileReader("target/temp/jp/ossc/nimbus/service/http/httpclient/help_output.txt"));
			
			String s;
			StringBuffer sb = new StringBuffer();
			//Requestヘッダの検証
			while((s = br.readLine()) != null){
				if(s.startsWith("Content-Type:")){
					assertTrue(s.endsWith("application/xml;charset=Shift_JIS"));
				}
				sb.append(s);				
			}
			br.close();
			//DataSet内容の検証
			assertTrue(sb.toString().endsWith("sectionCode=022&account=059641&password=059641"));

		} catch (HttpRequestCreateException e) {
			e.printStackTrace();
			fail("例外発生");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("例外発生");
		} catch (IOException e) {
			e.printStackTrace();
			fail("例外発生");
		} finally {
			ServiceManagerFactory
					.unloadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTest2.xml");
		}
	}


//	/**
//	 * 指定された論理アクション名に該当するリクエストを発行するテスト。
//	 * <p>
//	 * 条件：
//	 * <ul>
//	 * <li>次の内容の定義ファイルをロードし、HttpClientFactoryServiceインスタンスを生成する</li>
//	 * <li>Journal,Sequenceサービス定義あり</li>
//	 * <li>RequestContentType：application/xml</li>
//	 * <li>RequestCharacterEncoding：Shift_JIS</li>
//	 * <li>RequestHeaders：Accept-Encoding=gzip</li>
//	 * <li>RequestStreamConverterServiceName：#DataSetXMLConverter</li>
//	 * <li>ResponseStreamConverterServiceName：#ResponseStreamConverter</li>
//	 * <li>Proxy：#localhost:8280</li>
//	 * <li>論理アクション名"login"のリクエスト情報を定義</li>
//	 * <li>HttpClientFactoryService#createHttpClient()を実行し、HttpClientを生成</li>
//	 * <li>HttpRequestImpl#createRequest(論理アクション名)を実行し、HttpRequestを生成</li>
//	 * <li>送信するデータセットを次の内容で生成し、HttpRequest#setObject()でセット<BR>
//	 * スキーマ：:name,java.lang.String,,,\n:age,int,,,<BR>
//	 * 値　　　：name=hoge,age=25 </li>
//	 * <li>Proxy：#localhost:8280</li>
//	 * <li>生成したHttpRequestを指定して、HttpClientImpl#executeRequest(request)を実行</li>
//	 * </ul>
//	 * 確認：
//	 * <ul>
//	 * <li>送信先で指定した次の値がHTTPリクエストに正しく設定されていることを確認</li>
//	 * <li>レスポンスの内容が正しく設定されていることを確認</li>
//	 * </ul>
//	 */
//	public void testExecuteRequestWithJournal() {
//		try {
//			if (!ServiceManagerFactory
//					.loadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTest3.xml")) {
//				System.exit(-1);
//			}
//			final HttpClientFactory factory = (HttpClientFactory) ServiceManagerFactory
//					.getServiceObject("HttpClientFactory");
//			HttpClientImpl client = (HttpClientImpl) factory.createHttpClient();
//
//			DataSet requestDs = new DataSet("Login");
//	        requestDs.setHeaderSchema(
//	            "UserInfo",
//	            ":name,java.lang.String,,,\n"
//	             + ":age,int,,,"
//	        );
//	        Header userInfo = requestDs.getHeader("UserInfo");
//	        userInfo.setProperty("name", "hoge");
//	        userInfo.setProperty("age", 25);
//	        
//	        HttpRequest request = factory.createRequest("login");
//	        request.setObject(requestDs);
//
//	        HttpResponse res = client.executeRequest(request);
//			//レスポンス内容の確認
//			assertEquals(200, res.getStatusCode());
//			assertEquals("testdata", res.getObject());
//				        
//			
//			/*Proxyテスト用プログラム(jp.ossc.nimbus.service.http.proxy.TestHttpProcessService)
//			 * の出力ファイルの内容を確認し、HTTPリクエストデータを検証
//			 */
//			
//			BufferedReader br = new BufferedReader(
//					new FileReader("jp/ossc/nimbus/service/http/httpclient/help_output.txt"));
//			
//			String s;
//			StringBuffer sb = new StringBuffer();
//			//Requestヘッダの検証
//			while((s = br.readLine()) != null){
//				if(s.startsWith("Content-Type:")){
//					assertTrue(s.endsWith("application/xml;charset=Shift_JIS"));
//				}
//				if(s.startsWith("Accept-Encoding")){
//					assertTrue(s.endsWith("gzip"));
//				}
//				sb.append(s);				
//			}
//			br.close();
//			//DataSet内容の検証
//			assertTrue(sb.toString().endsWith(
//					"<dataSet name=\"Login\"><schema>" +
//					"<header name=\"UserInfo\">:name,java.lang.String,,,:age,int,,,</header>" +
//					"</schema><header name=\"UserInfo\">" +
//					"<name>hoge</name><age>25</age></header></dataSet>"));
//			//Journalの内容検証
//			HttpClientFactoryService hcf = (HttpClientFactoryService)factory;
//			final ObjectMappedEditorFinderService finder = 
//				(ObjectMappedEditorFinderService) ServiceManagerFactory.getServiceObject("JournalEditorFinder");
//			
//			assertEquals("",hcf.journal.getCurrentJournalString(finder));	
//			
//			
//			
//		} catch (HttpRequestCreateException e) {
//			e.printStackTrace();
//			fail("例外発生");
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			fail("例外発生");
//		} catch (IOException e) {
//			e.printStackTrace();
//			fail("例外発生");
//		} finally {
//			ServiceManagerFactory
//					.unloadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTest3.xml");
//		}
//	}

		/**
		 * 指定された論理アクション名に該当するリクエストを発行するテスト。
		 * <p>
		 * 条件：
		 * <ul>
		 * <li>次の内容の定義ファイルをロードし、HttpClientFactoryServiceインスタンスを生成する</li>
		 * <li>Journal,Sequenceサービス定義あり</li>
		 * <li>RequestContentType：application/xml</li>
		 * <li>RequestCharacterEncoding：指定なし</li>
		 * <li>RequestHeaders：Accept-Encoding=gzip</li>
		 * <li>RequestStreamConverterServiceName：#DataSetXMLConverter</li>
		 * <li>ResponseStreamConverterServiceName：指定なし</li>
		 * <li>Proxy：#localhost:8280</li>
		 * <li>論理アクション名"login"のリクエスト情報を定義</li>
		 * <li>HttpClientFactoryService#createHttpClient()を実行し、HttpClientを生成</li>
		 * <li>HttpRequestImpl#createRequest(論理アクション名)を実行し、HttpRequestを生成</li>
		 * <li>送信するデータセットを次の内容で生成し、HttpRequest#setObject()でセット<BR>
		 * スキーマ：:name,java.lang.String,,,\n:age,int,,,<BR>
		 * 値　　　：name=hoge,age=25 </li>
		 * <li>Proxy：#localhost:8280</li>
	     * <li>レスポンスのbodyはNullが返ってくることを想定</li>
		 * <li>生成したHttpRequestを指定して、HttpClientImpl#executeRequest(request)を実行</li>
		 * </ul>
		 * 確認：
		 * <ul>
		 * <li>送信先で指定した次の値がHTTPリクエストに正しく設定されていることを確認</li>
		 * </ul>
		 */
		public void testExecuteRequestWithNoRequestCharacterEncoding() {
			try {
				if (!ServiceManagerFactory
						.loadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTest4.xml")) {
					System.exit(-1);
				}
				final HttpClientFactory factory = (HttpClientFactory) ServiceManagerFactory
						.getServiceObject("HttpClientFactory");
				HttpClientImpl client = (HttpClientImpl) factory.createHttpClient();

				DataSet requestDs = new DataSet("Login");
		        requestDs.setHeaderSchema(
		            "UserInfo",
		            ":name,java.lang.String,,,\n"
		             + ":age,int,,,"
		        );
		        Header userInfo = requestDs.getHeader("UserInfo");
		        userInfo.setProperty("name", "hoge");
		        userInfo.setProperty("age", 25);
		        
		        HttpRequest request = factory.createRequest("login");
		        request.setObject(requestDs);

		        HttpResponse res = client.executeRequest(request);
				//レスポンス内容の確認
				assertEquals(200, res.getStatusCode());
				assertNull(res.getObject());
				
				
				/*Proxyテスト用プログラム(jp.ossc.nimbus.service.http.proxy.TestHttpProcessService)
				 * の出力ファイルの内容を確認し、HTTPリクエストデータを検証
				 */
				
				BufferedReader br = new BufferedReader(
						new FileReader("target/temp/jp/ossc/nimbus/service/http/httpclient/help_output.txt"));
				
				String s;
				StringBuffer sb = new StringBuffer();
				//Requestヘッダの検証
				while((s = br.readLine()) != null){
					if(s.startsWith("Content-Type:")){
						assertTrue(s.endsWith("application/xml"));
					}
					if(s.startsWith("Accept-Encoding")){
						assertTrue(s.endsWith("gzip"));
					}
					sb.append(s);				
				}
				br.close();
				//DataSet内容の検証
				assertTrue(sb.toString().endsWith(
						"<dataSet name=\"Login\"><schema>" +
						"<header name=\"UserInfo\">:name,java.lang.String,,,:age,int,,,</header>" +
						"</schema><header name=\"UserInfo\">" +
						"<name>hoge</name><age>25</age></header></dataSet>"));
				
				
			} catch (HttpRequestCreateException e) {
				e.printStackTrace();
				fail("例外発生");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				fail("例外発生");
			} catch (IOException e) {
				e.printStackTrace();
				fail("例外発生");
			} finally {
				ServiceManagerFactory
						.unloadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTest4.xml");
			}
	}


		/**
		 * 指定された論理アクション名に該当するリクエストを発行するテスト。
		 * <p>
		 * 条件：
		 * <ul>
		 * <li>次の内容の定義ファイルをロードし、HttpClientFactoryServiceインスタンスを生成する</li>
		 * <li>RequestContentType：application/xml</li>
		 * <li>RequestHeaders(Accept-Encoding)：gzip</li>
		 * <li>RequestHeaders(Content-Encoding)：gzip(リクエストデータを圧縮)</li>
		 * <li>RequestStreamConverterServiceName：#DataSetXMLConverter</li>
		 * <li>ResponseStreamConverterServiceName：#ResponseStreamConverter</li>
		 * <li>ResponseHeaders：ContentType=application/xml</li>
		 * <li>Proxy：#localhost:8280</li>
		 * <li>論理アクション名"login"のリクエスト情報を定義</li>
		 * <li>HttpClientFactoryService#createHttpClient()を実行し、HttpClientを生成</li>
		 * <li>HttpRequestImpl#createRequest(論理アクション名)を実行し、HttpRequestを生成</li>
		 * <li>送信するデータセットを次の内容で生成し、HttpRequest#setObject()でセット<BR>
		 * スキーマ：:name,java.lang.String,,,\n:age,int,,,<BR>
		 * 値　　　：name=hoge,age=25 </li>
		 * <li>生成したHttpRequestを指定して、HttpClientImpl#executeRequest(request)を実行</li>
		 * </ul>
		 * 確認：
		 * <ul>
		 * <li>送信先で指定した次の値がHTTPリクエストに正しく設定されていることを確認</li>
		 * </ul>
		 */
		public void testExecuteRequestWithCompressDataSet() {
			try {
				if (!ServiceManagerFactory
						.loadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTestComp.xml")) {
					System.exit(-1);
				}
				final HttpClientFactory factory = (HttpClientFactory) ServiceManagerFactory
						.getServiceObject("HttpClientFactory");
				HttpClientImpl client = (HttpClientImpl) factory.createHttpClient();

				DataSet requestDs = new DataSet("Login");
		        requestDs.setHeaderSchema(
		            "UserInfo",
		            ":name,java.lang.String,,,\n"
		             + ":age,int,,,"
		        );
		        Header userInfo = requestDs.getHeader("UserInfo");
		        userInfo.setProperty("name", "hoge");
		        userInfo.setProperty("age", 25);
		        
		        HttpRequest request = factory.createRequest("login");
		        request.setObject(requestDs);

		        HttpResponse res = client.executeRequest(request);
				//レスポンス内容の確認
				assertEquals(200, res.getStatusCode());
				assertEquals("testdata", res.getObject());
				
				/*Proxyテスト用プログラム(jp.ossc.nimbus.service.http.proxy.TestHttpProcessService)
				 * の出力ファイルの内容を確認し、HTTPリクエストデータを検証
				 */
				
				BufferedReader br = new BufferedReader(
						new FileReader("target/temp/jp/ossc/nimbus/service/http/httpclient/help_output.txt"));
				
				String s;
				StringBuffer sb = new StringBuffer();
				//Requestヘッダの検証
				while((s = br.readLine()) != null){
					if(s.startsWith("Content-Type:")){
						assertTrue(s.endsWith("application/xml"));
					}
					if(s.startsWith("Content-Encoding:")){
						assertTrue(s.endsWith("gzip"));
					}
					if(s.startsWith("Accept-Encoding:")){
						assertTrue(s.endsWith("gzip"));
					}
					sb.append(s);				
				}
				br.close();
				//DataSet内容の検証
				assertTrue(sb.toString().endsWith(
						"<dataSet name=\"Login\"><schema>" +
						"<header name=\"UserInfo\">:name,java.lang.String,,,:age,int,,,</header>" +
						"</schema><header name=\"UserInfo\">" +
						"<name>hoge</name><age>25</age></header></dataSet>"));

			} catch (HttpRequestCreateException e) {
				e.printStackTrace();
				fail("例外発生");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				fail("例外発生");
			} catch (IOException e) {
				e.printStackTrace();
				fail("例外発生");
			} catch (Exception e) {
				e.printStackTrace();
				fail("例外発生");
			} finally {
				ServiceManagerFactory
						.unloadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTestComp.xml");
			}
		}


		/**
		 * 指定された論理アクション名に該当するリクエストを発行するテスト。
		 * <p>
		 * 条件：
		 * <ul>
		 * <li>次の内容の定義ファイルをロードし、HttpClientFactoryServiceインスタンスを生成する</li>
		 * <li>RequestContentType：application/xml</li>
		 * <li>RequestDeflateLength：1000（1000バイト以下のファイルは圧縮しない）</li>
		 * <li>RequestHeaders(Accept-Encoding)：gzip</li>
		 * <li>RequestHeaders(Content-Encoding)：gzip(リクエストデータを圧縮)</li>
		 * <li>RequestStreamConverterServiceName：#DataSetXMLConverter</li>
		 * <li>ResponseStreamConverterServiceName：#ResponseStreamConverter</li>
		 * <li>ResponseHeaders：ContentType=application/xml</li>
		 * <li>Proxy：#localhost:8280</li>
		 * <li>論理アクション名"login"のリクエスト情報を定義</li>
		 * <li>HttpClientFactoryService#createHttpClient()を実行し、HttpClientを生成</li>
		 * <li>HttpRequestImpl#createRequest(論理アクション名)を実行し、HttpRequestを生成</li>
		 * <li>送信するデータセット(1000バイト以下)を次の内容で生成し、HttpRequest#setObject()でセット<BR>
		 * スキーマ：:name,java.lang.String,,,\n:age,int,,,<BR>
		 * 値　　　：name=hoge,age=25 </li>
		 * <li>生成したHttpRequestを指定して、HttpClientImpl#executeRequest(request)を実行</li>
		 * </ul>
		 * 確認：
		 * <ul>
		 * <li>送信先で指定した次の値がHTTPリクエストに正しく設定されていることを確認</li>
		 * <li>非圧縮扱いになるのでヘッダ内からContent-Encodingが削除されていることを確認</li>
		 * </ul>
		 */
		public void testExecuteRequestDeflateLength () {
			try {
				if (!ServiceManagerFactory
						.loadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTestDeflen.xml")) {
					System.exit(-1);
				}
				final HttpClientFactory factory = (HttpClientFactory) ServiceManagerFactory
						.getServiceObject("HttpClientFactory");
				HttpClientImpl client = (HttpClientImpl) factory.createHttpClient();

				DataSet requestDs = new DataSet("Login");
		        requestDs.setHeaderSchema(
		            "UserInfo",
		            ":name,java.lang.String,,,\n"
		             + ":age,int,,,"
		        );
		        Header userInfo = requestDs.getHeader("UserInfo");
		        userInfo.setProperty("name", "hoge");
		        userInfo.setProperty("age", 25);
		        
		        HttpRequest request = factory.createRequest("login");
		        request.setObject(requestDs);

		        HttpResponse res = client.executeRequest(request);
				//レスポンス内容の確認
				assertEquals(200, res.getStatusCode());
				assertEquals("testdata", res.getObject());
				
				/*Proxyテスト用プログラム(jp.ossc.nimbus.service.http.proxy.TestHttpProcessService)
				 * の出力ファイルの内容を確認し、HTTPリクエストデータを検証
				 */
				
				BufferedReader br = new BufferedReader(
						new FileReader("target/temp/jp/ossc/nimbus/service/http/httpclient/help_output.txt"));
				
				String s;
				StringBuffer sb = new StringBuffer();
				//Requestヘッダの検証
				while((s = br.readLine()) != null){
					if(s.startsWith("Content-Type:")){
						assertTrue(s.endsWith("application/xml"));
					}
					if(s.startsWith("Content-Encoding:")){
						fail("Content-Encodinが削除されてないため失敗");
					}
					sb.append(s);				
				}
				br.close();
				//DataSet内容の検証
				assertTrue(sb.toString().endsWith(
						"<dataSet name=\"Login\"><schema>" +
						"<header name=\"UserInfo\">:name,java.lang.String,,,:age,int,,,</header>" +
						"</schema><header name=\"UserInfo\">" +
						"<name>hoge</name><age>25</age></header></dataSet>"));

			} catch (HttpRequestCreateException e) {
				e.printStackTrace();
				fail("例外発生");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				fail("例外発生");
			} catch (IOException e) {
				e.printStackTrace();
				fail("例外発生");
			} catch (Exception e) {
				e.printStackTrace();
				fail("例外発生");
			} finally {
				ServiceManagerFactory
						.unloadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTestDeflen.xml");
			}
		}	
}
