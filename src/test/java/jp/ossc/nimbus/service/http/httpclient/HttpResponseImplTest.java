package jp.ossc.nimbus.service.http.httpclient;

import java.io.*;
import java.util.*;

import org.apache.commons.httpclient.HttpMethodBase;


import jp.ossc.nimbus.beans.dataset.DataSet;
import jp.ossc.nimbus.beans.dataset.Header;
import jp.ossc.nimbus.beans.dataset.PropertySchemaDefineException;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.http.HttpClientFactory;
import jp.ossc.nimbus.service.http.HttpRequest;
import jp.ossc.nimbus.service.http.httpclient.HttpClientFactoryService.HttpClientImpl;
import jp.ossc.nimbus.util.converter.ConvertException;
import jp.ossc.nimbus.util.converter.DataSetXMLConverter;
import junit.framework.TestCase;

public class HttpResponseImplTest extends TestCase {

	public HttpResponseImplTest(String name) {
		super(name);
	}

	
	 public static void main(String[] args) {
	 junit.textui.TestRunner.run(HttpResponseImplTest.class); }
	 

	/**
	 * HttpResponseImplの各プロパティを設定、取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>HttpResponseImplインスタンスを生成する</li>
	 * <li>各getterメソッドを実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。設定した値が正しく取得できることを確認</li>
	 * </ul>
	 */
	public void testSetterGetter() {
		HttpResponseImpl res = new HttpResponseImpl();

		ServiceName name = new ServiceName("DataSetXMLConverter");
		res.setStreamConverterServiceName(name);

		DataSetXMLConverter xconv = new DataSetXMLConverter();
		res.setStreamConverter(xconv);
		
		res.setStatusCode(200);
		res.setStatusMessage("OK");

		assertEquals(xconv, res.getStreamConverter());
		assertEquals(name, res.getStreamConverterServiceName());
		assertEquals(200, res.getStatusCode());
		assertEquals("OK", res.getStatusMessage());
	}


	/**
	 * HTTPメソッドを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>定義ファイルをロードし、HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>HttpRequestImpl#createRequest(論理アクション名)を実行し、HttpRequestを生成</li>
	 * <li>生成したHttpRequestを指定して、HttpClientImpl#createHttpMethod()を実行</li>
	 * <li>生成したMethodを指定して、HttpResponseImpl#setHttpMethod()を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了することを確認</li>
	 * </ul>
	 */
	public void testSetHttpMethod() {
		try {
			if (!ServiceManagerFactory
					.loadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTest.xml")) {
				System.exit(-1);
			}
			final HttpClientFactory factory = (HttpClientFactory) ServiceManagerFactory
					.getServiceObject("HttpClientFactory");

			HttpClientImpl client = (HttpClientImpl)factory.createHttpClient();

	        HttpRequest request = factory.createRequest("login");
			
			HttpRequestImpl rec = (HttpRequestImpl)request;
			HttpMethodBase method = rec.createHttpMethod();
			client.client.executeMethod(method);
			HttpResponseImpl res = new HttpResponseImpl();
			res.setHttpMethod(method);
			
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("例外発生");
		} finally {
			ServiceManagerFactory
					.unloadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTest.xml");
		}

	}

	
	
	/**
	 * ヘッダー情報を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>定義ファイルをロードし、HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>HttpRequestImpl#createRequest(論理アクション名)を実行し、HttpRequestを生成</li>
	 * <li>HttpRequestImpl#executeRequest()を実行し、HttpResponseを生成</li>
	 * <li>レスポンスのヘッダーにContent-Type=text/html;charset=Shift_JISが含まれている</li>
	 * <li>HttpResponseImpl#getHeadermap()を実行してmapを生成</li>
	 * <li>HttpResponseImpl#getHeader(),getHeaders()を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>HttpResponseImpl#getHeader(),getHeaders()が正しい結果が返されることを確認</li>
	 * </ul>
	 */
	public void testGetHeaderMap() {
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

			HttpResponseImpl res = (HttpResponseImpl)client.executeRequest(request);
			res.getHeaderMap();
			assertEquals("text/html;charset=Shift_JIS",res.getHeader("Content-Type"));
			assertEquals("text/html;charset=Shift_JIS",res.getHeaders("Content-Type")[0]);
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("例外発生");
		} finally {
			ServiceManagerFactory
					.unloadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTest.xml");
		}

	}
	
	/**
	 * ヘッダー情報を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>定義ファイルをロードし、HttpClientFactoryServiceインスタンスを生成する</li>
	 * <li>HttpRequestImpl#createRequest(論理アクション名)を実行し、HttpRequestを生成</li>
	 * <li>HttpRequestImpl#executeRequest()を実行し、HttpResponseを生成</li>
	 * <li>レスポンスのヘッダーにContent-Type=text/html;charset=Shift_JISが含まれている</li>
	 * <li>HttpResponseImpl#getHeader(),getHeaders()を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>HttpResponseImpl#getHeader(),getHeaders()が正しい結果が返されることを確認</li>
	 * </ul>
	 */
	public void testGetHeaderMapNull() {
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

			HttpResponseImpl res = (HttpResponseImpl)client.executeRequest(request);
			assertEquals("text/html;charset=Shift_JIS",res.getHeader("Content-Type"));
			assertEquals("text/html;charset=Shift_JIS",res.getHeaders("Content-Type")[0]);
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("例外発生");
		} finally {
			ServiceManagerFactory
					.unloadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTest.xml");
		}

	}

	/**
	 * HttpResponseのヘッダー名を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の値を指定してHttpResponse#headerMapを生成</li>
	 * <li>name=ContentType,value={"application/xml"}</li>
	 * <li>HttpResponse#getHeaderNameSetを実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>指定した内容が返り値に含まれていることを確認</li>
	 * </ul>
	 */
	public void testGetHeader() {
		HttpResponseImpl res = new HttpResponseImpl();
		res.headerMap = new HashMap();

		String[] vals = new String[] { "application/xml" };
		res.headerMap.put("Content-Type", vals);

		assertEquals("Content-Type", res.getHeaderNameSet().toArray()[0]
				.toString());
		assertEquals("application/xml", res.getHeader("Content-Type"));
		assertEquals("application/xml", res.getHeaders("Content-Type")[0]);
	}

	public void testGetInputStream() {
		try {
			HttpResponseImpl res = new HttpResponseImpl();
			res.setInputStream(System.in);
			assertEquals(System.in, res.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			fail("例外発生");
		}

	}


	/**
	 * HttpResponseのストリームを設定、取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>XMLストリームデータをデータセットに変換して取得する</li>
	 * <li>次の内容をDataSetXMLConverterで変換する<BR>	
	 * <PRE>
	 * <?xml version="1.0" encoding="UTF-8"?>
	 *  <dataSet>
	 *   <schema>
	 *    <header name="TestHeader">
	 *     :A,java.util.Date,
	 *     "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format="yyyy-MM-DD"}",
	 *     "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format="yyyy-MM-DD"}",
	 *     "@value@ != null"
	 *     :B,java.lang.String,,,
	 *    </header>
	 *   </schema>
	 *    <header name="TestHeader"><A>2008-01-28</A><B>TestValue</B></header></dataSet>
	 * <PRE></li>	 
	 * <li>上記のストリームデータとコンバータをプロパティに設定して、<BR>
	 * HttpResponse#getObjectを実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正しい値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetObject() {
		try {
			HttpResponseImpl res = new HttpResponseImpl();
			//CharacterEncodingを設定しておく
			res.headerMap = new HashMap();

			String[] vals = new String[] { "application/xml;charset=Shift_JIS" };
			res.headerMap.put("Content-Type", vals);

			String inxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<dataSet><schema><header name=\"TestHeader\">" +
			":A,java.util.Date," +
			"\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\"," +
					"\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\"," +
							"\"@value@ != null\"\n:B,java.lang.String,,," +
							"</header></schema><header name=\"TestHeader\">" +
							"<A>2008-01-28</A><B>TestValue</B></header></dataSet>";
			//入力ストリームとコンバーターをプロパティにセット
			InputStream is = new ByteArrayInputStream(inxml.getBytes());
			res.setInputStream(is);
			DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.XML_TO_DATASET);
			res.setStreamConverter(conv);
			
			//
			DataSet dataset = (DataSet)res.getObject();
			
			assertEquals("TestHeader",dataset.getHeader("TestHeader").getName());
			assertEquals(":A,java.util.Date," +
					"\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\"," +
					"\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\"," +
							"\"@value@ != null\"\n:B,java.lang.String,,,",dataset.getHeader("TestHeader").getSchema());
			assertEquals("2008-01-28",dataset.getHeader("TestHeader").getFormatProperty("A"));
			assertEquals("TestValue",dataset.getHeader("TestHeader").getProperty("B"));
			
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		} catch (ConvertException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * HttpResponseのストリームを設定、取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>XMLストリームデータをデータセットに変換して取得する</li>
	 * <li>次の内容をDataSetXMLConverterで変換する<BR>	
	 * <PRE>
	 * <?xml version="1.0" encoding="UTF-8"?>
	 *  <dataSet>
	 *   <schema>
	 *    <header name="TestHeader">
	 *     :A,java.util.Date,
	 *     "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format="yyyy-MM-DD"}",
	 *     "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format="yyyy-MM-DD"}",
	 *     "@value@ != null"
	 *     :B,java.lang.String,,,
	 *    </header>
	 *   </schema>
	 *    <header name="TestHeader"><A>2008-01-28</A><B>TestValue</B></header></dataSet>
	 * <PRE></li>	 
	 * <li>上記のストリームデータとコンバータをプロパティに設定して、<BR>
	 * HttpResponse#getObjectを実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正しい値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetObjectByServiceName() {
		try {
			//コンバータサービスの定義ファイルをロード
			if (!ServiceManagerFactory
					.loadManager("jp/ossc/nimbus/service/http/httpclient/service-conv.xml")) {
				System.exit(-1);
			}
			HttpResponseImpl res = new HttpResponseImpl();
			//CharacterEncodingを設定しておく
			res.headerMap = new HashMap();

			String[] vals = new String[] { "application/xml;charset=Shift_JIS" };
			res.headerMap.put("Content-Type", vals);

			String inxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<dataSet><schema><header name=\"TestHeader\">" +
			":A,java.util.Date," +
			"\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\"," +
					"\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\"," +
							"\"@value@ != null\"\n:B,java.lang.String,,," +
							"</header></schema><header name=\"TestHeader\">" +
							"<A>2008-01-28</A><B>TestValue</B></header></dataSet>";
			//入力ストリームとコンバーターサービス名をプロパティにセット
			InputStream is = new ByteArrayInputStream(inxml.getBytes());
			res.setInputStream(is);
			ServiceName name = new ServiceName("DataSetXMLConverter");
			res.setStreamConverterServiceName(name);
			
			//
			DataSet dataset = (DataSet)res.getObject();
			
			assertEquals("TestHeader",dataset.getHeader("TestHeader").getName());
			assertEquals(":A,java.util.Date," +
					"\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\"," +
					"\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\"," +
							"\"@value@ != null\"\n:B,java.lang.String,,,",dataset.getHeader("TestHeader").getSchema());
			assertEquals("2008-01-28",dataset.getHeader("TestHeader").getFormatProperty("A"));
			assertEquals("TestValue",dataset.getHeader("TestHeader").getProperty("B"));
			
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		} catch (ConvertException e) {
			e.printStackTrace();
			fail("例外発生");
		} finally {
		ServiceManagerFactory
				.unloadManager("jp/ossc/nimbus/service/http/httpclient/service-conv.xml");
		}
	}


	/**
	 * ヘッダーのCharacterEncodingを取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の値を指定してHttpResponse#headerMapを生成</li>
	 * <li>name=ContentType,value={"application/xml"}(文字セット指定なし)</li>
	 * <li>HttpResponse#getCharacterEncodingを実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>"ISO8859_1"が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetCharacterEncodingNoCharSet() {
		HttpResponseImpl res = new HttpResponseImpl();
		res.headerMap = new HashMap();

		String[] vals = new String[] { "application/xml" };
		res.headerMap.put("Content-Type", vals);

		assertEquals("ISO8859_1", res.getCharacterEncoding());
	}

	/**
	 * ヘッダーのCharacterEncodingを取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の値を指定してHttpResponse#headerMapを生成</li>
	 * <li>name=ContentType,value={"application/xml;charset=Shift_JIS"}</li>
	 * <li>HttpResponse#getCharacterEncodingを実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>"Shift_JIS"が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetCharacterEncoding() {
		HttpResponseImpl res = new HttpResponseImpl();
		res.headerMap = new HashMap();

		String[] vals = new String[] { "application/xml;charset=Shift_JIS" };
		res.headerMap.put("Content-Type", vals);

		assertEquals("Shift_JIS", res.getCharacterEncoding());
	}

}
