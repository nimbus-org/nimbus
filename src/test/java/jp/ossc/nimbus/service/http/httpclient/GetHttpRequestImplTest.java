package jp.ossc.nimbus.service.http.httpclient;

import org.apache.commons.httpclient.*;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.http.HttpClientFactory;
import jp.ossc.nimbus.service.http.HttpRequestCreateException;
import junit.framework.TestCase;

public class GetHttpRequestImplTest extends TestCase {

	public GetHttpRequestImplTest(String arg0) {
		super(arg0);
	}
	public static void main(String[] args) {
		 junit.textui.TestRunner.run(GetHttpRequestImplTest.class); }

	
	/**
	 * リクエストパラメータをクエリとして設定するテスト。
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
	 * <li>生成したHttpRequestを指定して、HttpClientImpl#createHttpMethod()を実行</li>
	 * <li>生成したHttpMethod、パラメータマップを指定して、GetHttpClientImpl#initParameter()を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>指定したパラメータがクエリとして設定されることをGetHttpClientImpl#getQueryString()で確認</li>
	 * </ul>
	 */
	public void testExecuteRequestGET() {
		try {
			if (!ServiceManagerFactory
					.loadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTestGET.xml")) {
				System.exit(-1);
			}
			final HttpClientFactory factory = (HttpClientFactory) ServiceManagerFactory
					.getServiceObject("HttpClientFactory");

	        
	        GetHttpRequestImpl request = (GetHttpRequestImpl)factory.createRequest("login");
	        
	        HttpMethodBase method = request.createHttpMethod();
	        //パラメータ情報設定
	        request.setParameter("nameA","valueA");
	        
	        String[] vals = new String[]{"valueB1","valueB2"};
	        request.setParameters("nameB", vals);
	        
	        //リクエストパラメータをクエリとして設定
	        request.initParameter(method, request.getParameterMap());
	        //正しく設定されているか確認
	        assertEquals("nameA=valueA&nameB=valueB1&nameB=valueB2", method.getQueryString());
	        

		} catch (HttpRequestCreateException e) {
			e.printStackTrace();
			fail("例外発生");
		} catch (Exception e) {
			e.printStackTrace();
			fail("例外発生");
		} finally {
			ServiceManagerFactory
					.unloadManager("jp/ossc/nimbus/service/http/httpclient/service-clientTestGET.xml");
		}
	}


}
