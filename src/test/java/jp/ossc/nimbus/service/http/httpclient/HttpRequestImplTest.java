package jp.ossc.nimbus.service.http.httpclient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.http.HttpClientFactory;
import jp.ossc.nimbus.service.http.HttpRequestCreateException;
import jp.ossc.nimbus.service.http.httpclient.HttpClientFactoryService.HttpClientImpl;
import junit.framework.TestCase;

public class HttpRequestImplTest extends TestCase {

	
	 public static void main(String[] args) {
	 junit.textui.TestRunner.run(HttpRequestImplTest.class); }
	 
	
	

	public HttpRequestImplTest(String arg0) {
		super(arg0);
	}


	/**
	 * HttpRequestにヘッダ情報を設定するテスト。
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
	 * <li>生成したHttpRequestに対してsetContentType()を実行し、ContentTypeヘッダ情報設定</li>
	 * <li>生成したHttpRequestに対してsetHeader(),addHeader()を実行しヘッダ情報設定</li>
	 * <li>生成したHttpRequestを指定して、HttpClientImpl#executeRequest(request)を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>setContentType()で設定したヘッダ情報が優先されることを確認</li>
	 * <li>ssetHeader(),addHeader()設定したヘッダ情報が反映されることを確認</li>
	 * </ul>
	 */
	public void testRequestSetHeader() {
		try {
			if (!ServiceManagerFactory
					.loadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTest2.xml")) {
				System.exit(-1);
			}
			final HttpClientFactory factory = (HttpClientFactory) ServiceManagerFactory
					.getServiceObject("HttpClientFactory");
			HttpClientImpl client = (HttpClientImpl) factory.createHttpClient();

//			DataSet requestDs = new DataSet("Login");
//	        requestDs.setHeaderSchema(
//	            "UserInfo",
//	            ":name,java.lang.String,,,\n"
//	             + ":age,int,,,"
//	        );
//	        Header userInfo = requestDs.getHeader("UserInfo");
//	        userInfo.setProperty("name", "hoge");
//	        userInfo.setProperty("age", 25);
	        
	        HttpRequestImpl request = (HttpRequestImpl)factory.createRequest("login");
//	        request.setObject(requestDs);
	        
	        //ヘッダ情報設定
	        request.setContentType("text/html");
	        request.addHeader("Accept","text/html");
	        request.addHeader("Accept","text/html");
	        request.setHeader("Accept-Language","jp");
	        //settr,getterの動作確認
	        request.setHttpVersion("1.1");
	        assertEquals("1.1", request.getHttpVersion());
	        request.setDoAuthentication(true);
	        assertTrue(request.isDoAuthentication());
	        request.setFollowRedirects(false);
	        assertFalse(request.isFollowRedirects());
	        request.setHttpMethodParam("TEST", "test");
	        request.setHttpMethodParam("TEST1", "test1");
	        assertEquals("test", request.getHttpMethodParam("TEST"));
	        assertEquals("test1", request.getHttpMethodParam("TEST1"));
	        assertTrue(request.getHttpMethodParamNameSet().contains("TEST"));
	        assertTrue(request.getHttpMethodParamNameSet().contains("TEST1"));
	        

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
					assertTrue(s.endsWith("text/html;charset=Shift_JIS"));
				}
				if(s.startsWith("Accept:")){
					assertTrue(s.endsWith("text/html"));
				}
				if(s.startsWith("Accept-Language:")){
					assertTrue(s.endsWith("jp"));
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


	/**
	 * HttpRequestにパラメータ情報、入力ストリームを設定するテスト。
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
	 * <li>送信するデータセットをXML入力ストリームで生成し、HttpRequest#setInputStream()でセット<BR>
	 * <li>生成したHttpRequestに対してsetParameter(),setParameter()を実行し、パラメータ情報設定</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>設定したパラメータ情報、入力ストリームが正しく反映されることを確認</li>
	 * </ul>
	 */
	public void testRequestSetParamQuely() {
		try {
			if (!ServiceManagerFactory
					.loadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTest2.xml")) {
				System.exit(-1);
			}
			final HttpClientFactory factory = (HttpClientFactory) ServiceManagerFactory
					.getServiceObject("HttpClientFactory");
			HttpClientImpl client = (HttpClientImpl) factory.createHttpClient();

	        //入力ストリームを設定
			String inxml = "TEST1234567890";
			InputStream is = new ByteArrayInputStream(inxml.getBytes());
	        
	        HttpRequestImpl request = (HttpRequestImpl)factory.createRequest("login");
	        request.setInputStream(is);
	        
	        //パラメータ情報設定
	        request.setContentType("text/html");
	        request.setParameter("nameA","valueA");
	        request.setParameter("nameA","valueB");
	        
	        String[] vals = new String[]{"valueB1","valueB2"};
	        request.setParameters("nameB", vals);
	        
	        //パラメータ情報確認
	        assertEquals("valueA", request.getParameter("nameA"));
	        String[] getvals = request.getParameters("nameB");
	        assertEquals(vals[0], getvals[0]);
	        assertEquals(vals[1], getvals[1]);
	        
	        

			client.executeRequest(request);
			
	        //入力ストリームが正しく送信されたかの確認
			/*Proxyテスト用プログラム(jp.ossc.nimbus.service.http.proxy.TestHttpProcessService)
			 * の出力ファイルの内容を確認し、HTTPリクエストデータを検証
			 */
			
			BufferedReader br = new BufferedReader(
					new FileReader("target/temp/jp/ossc/nimbus/service/http/httpclient/help_output.txt"));
			
			String s;
			StringBuffer sb = new StringBuffer();
			//Requestヘッダの検証
			while((s = br.readLine()) != null){
				sb.append(s);				
			}
			br.close();
			assertTrue(sb.toString().endsWith("TEST1234567890"));

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

	

}
