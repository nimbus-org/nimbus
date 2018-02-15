package jp.ossc.nimbus.beans.dataset;

import junit.framework.TestCase;
//
/**
 * 
 * @author S.Teshima
 * @version 1.00 作成: 2008/01/22 - S.Teshima
 */

public class HeaderTest extends TestCase {

	public HeaderTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(HeaderTest.class);
	}

	/**
	 * 同じスキーマを持ちデータを持たない空のヘッダを複製するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のヘッダ名とスキーマを指定してHeader(String name, String schema)を実行する</li>
	 * <li>name: "Testheader"</li>
	 * <li>schema: ":A,java.lang.String,,,"</li>
	 * <li>cloneSchema()を実行してレコードの複製を生成する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>getName()を実行して、元ヘッダーと複製ヘッダーの名前情報が等しいことを確認</li>
	 * <li>getSchema()を実行して、元ヘッダーと複製ヘッダーのスキーマ情報が等しいことを確認</li>
	 * </ul>
	 */
	public void testCloneSchema() {
	    try{
	    	Header header = new Header("Testheader", ":A,java.lang.String,,,");
	    	Record rec = header.cloneSchema();
	    	Header header2 = (Header)rec;
	    	assertEquals("Testheader", header2.getName());
	    	assertEquals(":A,java.lang.String,,,", header2.getSchema());
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}


	/**
	 * ヘッダ名を設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のヘッダ名とスキーマを指定してHeader(String name, String schema)を実行する</li>
	 * <li>name: "Testheader"</li>
	 * <li>schema: ":A,java.lang.String,,,"</li>
	 * <li>次のヘッダ名を指定してsetName(String name)を実行する</li>
	 * <li>name: "TestheaderNew"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>getName()を実行して、名前情報が"TestheaderNew"と等しいことを確認</li>
	 * </ul>
	 */
	public void testSetName() {
	    try{
	    	Header header = new Header("Testheader", ":A,java.lang.String,,,");
	    	header.setName("TestheaderNew");
	    	assertEquals("TestheaderNew", header.getName());
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}

}
