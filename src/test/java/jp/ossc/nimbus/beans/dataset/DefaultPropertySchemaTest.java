package jp.ossc.nimbus.beans.dataset;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
import java.math.*;

import junit.framework.TestCase;

//
/**
 * 
 * @author S.Teshima
 * @version 1.00 作成: 2008/01/18 - S.Teshima
 */

public class DefaultPropertySchemaTest extends TestCase {

	public DefaultPropertySchemaTest(String arg0) {
		super(arg0);
	}

	
	 public static void main(String[] args) {
	 junit.textui.TestRunner.run(DefaultPropertySchemaTest.class); }
	 

	/**
	 * スキーマを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>"A,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>スキーマが正常に設定できる</li>
	 * <li>例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testSetSchema1() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,,,";
			dps.setSchema(schema);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}

	}

	/**
	 * スキーマを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>null文字、空文字のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認</li>
	 * <li>例外メッセージ"The schema is insufficient."を返す</li>
	 * </ul>
	 */
	public void testSetSchema2() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = null;
			dps.setSchema(schema);
			fail("例外が発生しないためテスト失敗");
		} catch (PropertySchemaDefineException e) {
			assertEquals("null:The schema is insufficient.", e.getMessage());
		}
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "";
			dps.setSchema(schema);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
			assertEquals(":The schema is insufficient.", e.getMessage());
		}
	}

	/**
	 * スキーマ文字列を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>"A,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>getSchema()で取得したレコードスキーマが定義した内容と一致している</li>
	 * </ul>
	 */
	public void testGetSchema() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,,,";
			dps.setSchema(schema);
			assertEquals("A,java.lang.String,,,", dps.getSchema());
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * スキーマ文字列を解析するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してparseCSV(String text)を実行する</li>
	 * <li>"A,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>返り値のListの要素数が4</li>
	 * <li>返り値のListの1番目の要素が"A"</li>
	 * <li>返り値のListの2番目の要素が"java.lang.String"</li>
	 * </ul>
	 */
	public void testParseCSV1() {
		String schema = "A,java.lang.String,,,";
		List pcsv = DefaultPropertySchema.parseCSV(schema);
		assertEquals(pcsv.size(), 4);
		assertEquals(pcsv.get(0), "A");
		assertEquals(pcsv.get(1), "java.lang.String");
	}

	/**
	 * スキーマ文字列を解析するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のブロックエスケープ「"」を含むスキーマを指定してparseCSV(String text)を実行する</li>
	 * <li>"A,java.lang.String,"jp.ossc.nimbus.util.converter.PaddingStringConverter<BR>
	 * {1,1,1,*}",,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>返り値のListの1要素数が4</li>
	 * <li>返り値のListの1番目の要素が"A"</li>
	 * <li>返り値のListの2番目の要素が"java.lang.String"</li>
	 * <li>返り値のListの3番目の要素が"jp.ossc.nimbus.util.converter.PaddingStringConverter{ConvertType=1;PaddingLength=1;PaddingLiteral=*}"</li>
	 * </ul>
	 */
	public void testParseCSV2() {
		String schema = "A,java.lang.String,\"jp.ossc.nimbus.util.converter.PaddingStringConverter{ConvertType=1;PaddingLength=1;PaddingLiteral=*}\",,";
		List pcsv = DefaultPropertySchema.parseCSV(schema);
		assertEquals(pcsv.size(), 4);
		assertEquals(pcsv.get(0), "A");
		assertEquals(pcsv.get(1), "java.lang.String");
		assertEquals(pcsv.get(2),
				"jp.ossc.nimbus.util.converter.PaddingStringConverter{ConvertType=1;PaddingLength=1;PaddingLiteral=*}");
	}

	/**
	 * スキーマ文字列を解析するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のブロックエスケープ「"」が続くパターンを含むスキーマを指定してparseCSV(String text)を実行する</li>
	 * <li>"A,java.lang.String,"",,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>返り値のListの1要素数が4</li>
	 * <li>返り値のListの1番目の要素が"A"</li>
	 * <li>返り値のListの2番目の要素が"java.lang.String"</li>
	 * </ul>
	 */
	public void testParseCSV3() {
		String schema = "A,java.lang.String,\"\",,";
		List pcsv = DefaultPropertySchema.parseCSV(schema);
		assertEquals(pcsv.size(), 4);
		assertEquals(pcsv.get(0), "A");
		assertEquals(pcsv.get(1), "java.lang.String");
	}

	/**
	 * スキーマ文字列を解析するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のエスケープ「\」を含むスキーマを指定してparseCSV(String text)を実行する</li>
	 * <li>"A,java.lang.String,jp.ossc.nimbus.util.converter.PaddingStringConverter,<BR>
	 * {1\,1\,1\,*},,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>返り値のListの1要素数が4</li>
	 * <li>返り値のListの1番目の要素が"A"</li>
	 * <li>返り値のListの2番目の要素が"java.lang.String"</li>
	 * <li>返り値のListの3番目の要素が"jp.ossc.nimbus.util.converter.PaddingStringConverter{1,1,1,*}"</li>
	 * </ul>
	 */
	public void testParseCSV4() {
		String schema = "A,java.lang.String,jp.ossc.nimbus.util.converter.PaddingStringConverter{ConvertType=1;PaddingLength=1;PaddingLiteral=\\,},,";
		List pcsv = DefaultPropertySchema.parseCSV(schema);
		assertEquals(pcsv.size(), 4);
		assertEquals(pcsv.get(0), "A");
		assertEquals(pcsv.get(1), "java.lang.String");
		assertEquals(pcsv.get(2),
				"jp.ossc.nimbus.util.converter.PaddingStringConverter{ConvertType=1;PaddingLength=1;PaddingLiteral=,}");
	}

	/**
	 * スキーマ文字列を解析するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のエスケープ「\」が続くパターンを含むスキーマを指定してparseCSV(String text)を実行する</li>
	 * <li>"A\\,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>返り値のListの1要素数が4</li>
	 * <li>返り値のListの1番目の要素が"A\"</li>
	 * <li>返り値のListの2番目の要素が"java.lang.String"</li>
	 * </ul>
	 */
	public void testParseCSV5() {
		String schema = "A\\\\,java.lang.String,,,";
		List pcsv = DefaultPropertySchema.parseCSV(schema);
		assertEquals(pcsv.size(), 4);
		assertEquals(pcsv.get(0), "A\\");
		assertEquals(pcsv.get(1), "java.lang.String");
	}

	/**
	 * スキーマ文字列を解析するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のエスケープ「\」の後にブロックエスケープ「"」が続くパターンを含むスキーマを指定して<BR>
	 * parseCSV(String text)を実行する</li>
	 * <li>"A\"A,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>返り値のListの1要素数が4</li>
	 * <li>返り値のListの1番目の要素が"A"A"</li>
	 * <li>返り値のListの2番目の要素が"java.lang.String"</li>
	 * </ul>
	 */
	public void testParseCSV6() {
		String schema = "A\\\"A,java.lang.String,,,";
		List pcsv = DefaultPropertySchema.parseCSV(schema);
		assertEquals(pcsv.size(), 4);
		assertEquals(pcsv.get(0), "A\"A");
		assertEquals(pcsv.get(1), "java.lang.String");
	}

	/**
	 * スキーマ文字列を解析するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のエスケープ「\」の後に通常文字が続くパターンを含むスキーマを指定して<BR>
	 * parseCSV(String text)を実行する</li>
	 * <li>"A\A,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>返り値のListの1要素数が4</li>
	 * <li>返り値のListの1番目の要素が"A\A"</li>
	 * <li>返り値のListの2番目の要素が"java.lang.String"</li>
	 * </ul>
	 */
	public void testParseCSV7() {
		String schema = "A\\A,java.lang.String,,,";
		List pcsv = DefaultPropertySchema.parseCSV(schema);
		assertEquals(pcsv.size(), 4);
		assertEquals(pcsv.get(0), "A\\A");
		assertEquals(pcsv.get(1), "java.lang.String");
	}

	/**
	 * スキーマ文字列を解析するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のブロックエスケープ「"」の後にエスケープ「\」が続くパターンを含むスキーマを指定して<BR>
	 * parseCSV(String text)を実行する</li>
	 * <li>"A"\A,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>返り値のListの1要素数が4</li>
	 * <li>返り値のListの1番目の要素が"A"\A"</li>
	 * <li>返り値のListの2番目の要素が"java.lang.String"</li>
	 * </ul>
	 */
	public void testParseCSV8() {
		String schema = "A\\\"\\A,java.lang.String,,,";
		List pcsv = DefaultPropertySchema.parseCSV(schema);
		assertEquals(pcsv.size(), 4);
		assertEquals(pcsv.get(0), "A\"\\A");
		assertEquals(pcsv.get(1), "java.lang.String");
	}

	/**
	 * プロパティスキーマの各項目をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してparseSchemata(String schema, List schemata)を実行する<BR>
	 * schemataにはparseCSV(schema)を指定</li>
	 * <li>"A,java.lang.String,"jp.ossc.nimbus.util.converter.PaddingStringConverter
	 * {ConvertType=1;PaddingLength=1;PaddingLiteral=*}","jp.ossc.nimbus.util.converter.PaddingStringConverter{ConvertType=1;PaddingLength=1;PaddingLiteral=*}","@value@ !=
	 * null""</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>スキーマが正常に設定できる</li>
	 * <li>例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testParseSchemata() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,"
					+ "\"jp.ossc.nimbus.util.converter.PaddingStringConverter{ConvertType=1;PaddingLength=1;PaddingLiteral=*}\","
					+ "\"jp.ossc.nimbus.util.converter.PaddingStringConverter{ConvertType=1;PaddingLength=1;PaddingLiteral=*}\","
					+ "\"@value@ != null\"";
			dps.parseSchemata(schema, DefaultPropertySchema.parseCSV(schema));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}

	}

	/**
	 * プロパティスキーマの型をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してparseType(String schema, String val)を実行する</li>
	 * <li>schema:"A,java.lang.String,,," val:"java.lang.String"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しない</li>
	 * </ul>
	 */
	public void testParseType1() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,,,";
			String val = "java.lang.String";
			dps.parseType(schema, val);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティスキーマの型をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してparseType(String schema, String val)を実行する</li>
	 * <li>schema:"A,java.lang.String,,," val:null文字</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しない</li>
	 * </ul>
	 */
	public void testParseType2() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,,,";
			String val = null;
			dps.parseType(schema, val);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティスキーマの型をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してparseType(String schema, String val)を実行する</li>
	 * <li>schema:"A,DUMMYCLASS,,," val:"DUMMYCLASS"（存在しないクラス）</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認<BR>
	 * 例外メッセージ"The type is illegal."を返す</li>
	 * </ul>
	 */
	public void testParseType3() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,DUMMYCLASS,,,";
			String val = "DUMMYCLASS";
			dps.parseType(schema, val);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
			assertEquals("A,DUMMYCLASS,,,:The type is illegal.", e.getMessage());
		}
	}

	/**
	 * プロパティスキーマの入力変換の項目をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと変換項目文字列を指定してparseParseConverter(String schema, String
	 * val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,jp.ossc.nimbus.util.converter.DataSetXMLConverter,<BR>
	 * jp.ossc.nimbus.util.converter.DataSetXMLConverter,"@value@ != null""</li>
	 * <li>変換項目
	 * "jp.ossc.nimbus.util.converter.DataSetXMLConverter"(Converterクラス指定)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しない</li>
	 * <li>parseConverterに指定したConverterクラスが設定されている</li>
	 * </ul>
	 */
	public void testParseParseConverter1() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,jp.ossc.nimbus.util.converter.DataSetXMLConverter,"
					+ "jp.ossc.nimbus.util.converter.DataSetXMLConverter,\"@value@ != null\"";
			String val = "jp.ossc.nimbus.util.converter.DataSetXMLConverter";
			dps.parseParseConverter(schema, val);
			assertTrue(dps.parseConverter instanceof jp.ossc.nimbus.util.converter.DataSetXMLConverter);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティスキーマの入力変換の項目をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと変換項目文字列を指定してparseParseConverter(String schema, String
	 * val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,Nimbus#DataSetXMLConverter,<BR>
	 * Nimbus#DataSetXMLConverter,"@value@ != null""</li>
	 * <li>変換項目 "Nimbus#DataSetXMLConverter"(サービス名)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しない</li>
	 * <li>parseConverterNameに指定したServiceNameオブジェクトが設定されている</li>
	 * </ul>
	 */
	public void testParseParseConverter2() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,Nimbus#DataSetXMLConverter,"
					+ "Nimbus#DataSetXMLConverter,\"@value@ != null\"";
			String val = "Nimbus#DataSetXMLConverter";
			dps.parseParseConverter(schema, val);
			assertEquals(dps.parseConverterName.getServiceManagerName(),
					"Nimbus");
			assertEquals(dps.parseConverterName.getServiceName(),
					"DataSetXMLConverter");
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティスキーマの入力変換の項目をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと変換項目文字列を指定してparseParseConverter(String schema, String
	 * val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,#TestService,<BR>
	 * #TestService,"@value@ != null""</li>
	 * <li>変換項目 "#TestService"(マネージャー名が設定されていない不正なサービス名)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認<BR>
	 * </ul>
	 */
	public void testParseParseConverter3() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,#TestService,"
					+ "#TestService,\"@value@ != null\"";
			String val = "#TestService";
			dps.parseParseConverter(schema, val);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {

		}
	}

	/**
	 * プロパティスキーマの出力変換の項目をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと変換項目文字列を指定してparseFormatConverter(String schema, String
	 * val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,jp.ossc.nimbus.util.converter.DataSetXMLConverter,<BR>
	 * jp.ossc.nimbus.util.converter.DataSetXMLConverter,"@value@ != null""</li>
	 * <li>変換項目
	 * "jp.ossc.nimbus.util.converter.DataSetXMLConverter"(Converterクラス指定)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しない</li>
	 * <li>formatConverterに指定したConverterクラスが設定されている</li>
	 * </ul>
	 */
	public void testParseFormatConverter1() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,jp.ossc.nimbus.util.converter.DataSetXMLConverter,"
					+ "jp.ossc.nimbus.util.converter.DataSetXMLConverter,\"@value@ != null\"";
			String val = "jp.ossc.nimbus.util.converter.DataSetXMLConverter";
			dps.parseFormatConverter(schema, val);
			assertTrue(dps.formatConverter instanceof jp.ossc.nimbus.util.converter.DataSetXMLConverter);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティスキーマの出力変換の項目をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと変換項目文字列を指定してparseFormatConverter(String schema, String
	 * val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,Nimbus#DataSetXMLConverter,<BR>
	 * Nimbus#DataSetXMLConverter,"@value@ != null""</li>
	 * <li>変換項目 "Nimbus#DataSetXMLConverter"(サービス名)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しない</li>
	 * <li>formatConverterNameにServiceNameオブジェクトが設定されている</li>
	 * </ul>
	 */
	public void testParseFormatConverter2() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,Nimbus#DataSetXMLConverter,"
					+ "Nimbus#DataSetXMLConverter,\"@value@ != null\"";
			String val = "Nimbus#DataSetXMLConverter";
			dps.parseFormatConverter(schema, val);
			assertEquals(dps.formatConverterName.getServiceManagerName(),
					"Nimbus");
			assertEquals(dps.formatConverterName.getServiceName(),
					"DataSetXMLConverter");
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティスキーマの出力変換の項目をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと変換項目文字列を指定してparseFormatConverter(String schema, String
	 * val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.PaddingStringConverter,<BR>
	 * {1,1,1,*}","jp.ossc.nimbus.util.converter.PaddingStringConverter,{1,1,1,*}","@value@ !=
	 * null""</li>
	 * <li>変換項目 "#TestService"(マネージャー名が設定されていない不正なサービス名)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認<BR>
	 * </ul>
	 */
	public void testParseFormatConverter3() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,#TestService,"
					+ "#TestService,\"@value@ != null\"";
			String val = "#TestService";
			dps.parseFormatConverter(schema, val);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {

		}
	}

	/**
	 * プロパティスキーマの変換項目をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと変換項目文字列を指定してparseConverter(String schema, String val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,jp.ossc.nimbus.util.converter.DataSetXMLConverter,<BR>
	 * jp.ossc.nimbus.util.converter.DataSetXMLConverter,"@value@ != null""</li>
	 * <li>変換項目 "jp.ossc.nimbus.util.converter.DataSetXMLConverter"(プロパティなし)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しない</li>
	 * </ul>
	 */
	public void testParseConverter1() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,jp.ossc.nimbus.util.converter.DataSetXMLConverter,"
					+ "jp.ossc.nimbus.util.converter.DataSetXMLConverter,\"@value@ != null\"";
			String val = "jp.ossc.nimbus.util.converter.DataSetXMLConverter";
			dps.parseConverter(schema, val);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティスキーマの変換項目をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと変換項目文字列を指定してparseConverter(String schema, String val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.PaddingStringConverter<BR>
	 * {1,1,1,*}","jp.ossc.nimbus.util.converter.PaddingStringConverter{1,1,1,*}","@value@ !=
	 * null""</li>
	 * <li>変換項目
	 * "jp.ossc.nimbus.util.converter.PaddingStringConverter,{1,1,1,*}"(プロパティあり)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しない</li>
	 * </ul>
	 */
	public void testParseConverter2() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,"
					+ "\"jp.ossc.nimbus.util.converter.PaddingStringConverter{ConvertType=1;PaddingLength=1;PaddingLiteral=*}\","
					+ "\"jp.ossc.nimbus.util.converter.PaddingStringConverter{ConvertType=2;PaddingLength=1;PaddingLiteral=*}\","
					+ "\"@value@ != null\"";
			String val = "jp.ossc.nimbus.util.converter.PaddingStringConverter{ConvertType=2;PaddingLength=1;PaddingLiteral=*}";
			dps.parseConverter(schema, val);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティスキーマの変換項目をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと変換項目文字列を指定してparseConverter(String schema, String val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,Nimbus#DataSetXMLConverter,<BR>
	 * Nimbus#DataSetXMLConverter,"@value@ != null""</li>
	 * <li>変換項目 "Nimbus#DataSetXMLConverter"(サービス名)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しない</li>
	 * </ul>
	 */
	public void testParseConverter3() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,"
					+ "Nimbus#DataSetXMLConverter,"
					+ "Nimbus#DataSetXMLConverter," + "\"@value@ != null\"";
			String val = "Nimbus#DataSetXMLConverter";
			dps.parseConverter(schema, val);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティスキーマの変換項目をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと変換項目文字列を指定してparseConverter(String schema, String val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,#TestService<BR>
	 * #TestService,"@value@ != null""</li>
	 * <li>変換項目 "#TestService"(マネージャー名が設定されていない不正なサービス名)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認<BR>
	 * 例外メッセージ"Converter is illegal."を返す</li>
	 * </ul>
	 */
	public void testParseConverter4() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,#TestService,"
					+ "#TestService,\"@value@ != null\"";
			String val = "#TestService";
			dps.parseConverter(schema, val);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
			assertEquals(
					"A,java.lang.String,#TestService,#TestService,\"@value@ != null\""
							+ ":Converter is illegal.", e.getMessage());
		}
	}

	/**
	 * プロパティスキーマの変換項目をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと変換項目文字列を指定してparseConverter(String schema, String val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.beans.dataset.DataSet,<BR>
	 * jp.ossc.nimbus.beans.dataset.DataSet,"@value@ != null""</li>
	 * <li>変換項目 "jp.ossc.nimbus.beans.dataset.DataSet"(コンバータとして実装されていないクラス)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認<BR>
	 * 例外メッセージ"Converter dose not implement Converter."を返す</li>
	 * </ul>
	 */
	public void testParseConverter5() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,jp.ossc.nimbus.beans.dataset.DataSet,"
					+ "jp.ossc.nimbus.beans.dataset.DataSet,\"@value@ != null\"";
			String val = "jp.ossc.nimbus.beans.dataset.DataSet";
			dps.parseConverter(schema, val);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
			assertEquals(
					"A,java.lang.String,jp.ossc.nimbus.beans.dataset.DataSet,"
							+ "jp.ossc.nimbus.beans.dataset.DataSet,\"@value@ != null\""
							+ ":Converter dose not implement Converter.", e
							.getMessage());
		}
	}

	/**
	 * コンバータのプロパティを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと変換項目文字列を指定してparseConverter(String schema, String val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.CustomConverter{1}",,"</li>
	 * <li>変換項目
	 * "jp.ossc.nimbus.util.converter.CustomConverter,{1}"(プロパティ指定が不必要なコンバータを誤って指定)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認<BR>
	 * 例外メッセージ"The property injection of this converter is not supported."を返す</li>
	 * </ul>
	 */
	public void testInitConverter() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,\"jp.ossc.nimbus.util.converter.CustomConverter{1}\",,";
			String val = "jp.ossc.nimbus.util.converter.CustomConverter{1}";
			dps.parseConverter(schema, val);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
		}
	}

	/**
	 * フォーマットコンバータのプロパティを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと変換項目文字列を指定してparseParseConverter(String schema, String
	 * val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.DateFormatConverter<BR>
	 * {1,"YYYY-MM-DD"}",,"</li>
	 * <li>変換項目
	 * "jp.ossc.nimbus.util.converter.DateFormatConverter,{1,"YYYY-MM-DD"}"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しない</li>
	 * <li>コンバータのgetterメソッドで取得した値が設定値と等しい</li>
	 * </ul>
	 */

	public void testInitFormatConverter1() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"YYYY-MM-DD\"}\",,";
			String val = "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"YYYY-MM-DD\"}";
			dps.parseParseConverter(schema, val);
			jp.ossc.nimbus.util.converter.DateFormatConverter dfc = new jp.ossc.nimbus.util.converter.DateFormatConverter();
			dfc = (jp.ossc.nimbus.util.converter.DateFormatConverter) dps.parseConverter;
			assertEquals(dfc.getConvertType(), 1);
			assertEquals(dfc.getFormat(), "YYYY-MM-DD");
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * フォーマットコンバータのプロパティを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと変換項目文字列を指定してparseConverter(String schema, String val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.DateFormatConverter<BR> {"
	 * ","YYYY-MM-DD"}",,"</li>
	 * <li>変換項目 "jp.ossc.nimbus.util.converter.DateFormatConverter{"
	 * ","YYYY-MM-DD"}" (数字以外を指定)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認<BR>
	 * 例外メッセージ"The property injection of this converter is
	 * "convertType,format""を返す</li>
	 * </ul>
	 */
	public void testInitFormatConverter2() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,\"jp.ossc.nimbus.util.converter.DateFormatConverter"
					+ "{ConvertType=\" \";Format=\"YYYY-MM-DD\"}\",,";
			String val = "jp.ossc.nimbus.util.converter.DateFormatConverter{\" \",\"YYYY-MM-DD\"}";
			dps.parseConverter(schema, val);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
		}
	}

	/**
	 * パディングコンバータのプロパティを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと変換項目文字列を指定してparseConverter(String schema, String val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.PaddingStringConverter<BR>
	 * {ConvertType=1;PaddingDirection=1;PaddingLength=1;PaddingLiteral=*}",,"</li>
	 * <li>変換項目 "jp.ossc.nimbus.util.converter.PaddingStringConverter{ConvertType=1;PaddingDirection=1;PaddingLength=1;PaddingLiteral=*}"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しない</li>
	 * <li>コンバータのgetterメソッドで取得した値が設定値と等しい</li>
	 * </ul>
	 */
	public void testInitPaddingConverter1() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,\"jp.ossc.nimbus.util.converter.PaddingStringConverter{ConvertType=1;PaddingDirection=1;PaddingLength=1;PaddingLiteral=*}\",,";
			String val = "jp.ossc.nimbus.util.converter.PaddingStringConverter{ConvertType=1;PaddingDirection=1;PaddingLength=1;PaddingLiteral=*}";
			dps.parseParseConverter(schema, val);
			jp.ossc.nimbus.util.converter.PaddingStringConverter psc = new jp.ossc.nimbus.util.converter.PaddingStringConverter();
			psc = (jp.ossc.nimbus.util.converter.PaddingStringConverter) dps.parseConverter;
			assertEquals(psc.getConvertType(), 1);
			assertEquals(psc.getPaddingDirection(), 1);
			assertEquals(psc.getPaddingLength(), 1);
			assertEquals(psc.getPaddingLiteral(), '*');
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * パディングコンバータのプロパティを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと変換項目文字列を指定してparseConverter(String schema, String val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.PaddingStringConverter<BR> {"
	 * ",1,1,*}",,"</li>
	 * <li>変換項目 "jp.ossc.nimbus.util.converter.PaddingStringConverter{"
	 * ",1,1,*}"(プロパティ指定の誤り。数字以外を指定)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認<BR>
	 * 例外メッセージ"The property injection of this converter is
	 * "convertType,paddingLength,paddingDirection,paddingLiteral""を返す</li>
	 * </ul>
	 */
	public void testInitPaddingConverter2() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,\"jp.ossc.nimbus.util.converter.PaddingStringConverter"
					+ "{\" \",1,1,*}\",,";
			String val = "jp.ossc.nimbus.util.converter.PaddingStringConverter{ConvertType=\" \";PaddingDirection=1;PaddingLength=1;PaddingLiteral=*}";
			dps.parseConverter(schema, val);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
		}
	}

	/**
	 * 可逆コンバータのプロパティを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと変換項目文字列を指定してparseParseConverter(String schema, String
	 * val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.DataSetXMLConverter<BR>
	 * {1}",,"</li>
	 * <li>変換項目 "jp.ossc.nimbus.util.converter.DataSetXMLConverter{ConvertType=1}"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しない</li>
	 * <li>コンバータのgetterメソッドで取得した値が設定値と等しい</li>
	 * </ul>
	 */
	public void testInitReversibleConverter1() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,\"jp.ossc.nimbus.util.converter.DataSetXMLConverter{ConvertType=1}\",,";
			String val = "jp.ossc.nimbus.util.converter.DataSetXMLConverter{ConvertType=1}";
			dps.parseParseConverter(schema, val);
			jp.ossc.nimbus.util.converter.DataSetXMLConverter dxc = new jp.ossc.nimbus.util.converter.DataSetXMLConverter();
			dxc = (jp.ossc.nimbus.util.converter.DataSetXMLConverter) dps.parseConverter;
			assertEquals(dxc.getConvertType(), 1);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 可逆コンバータのプロパティを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと変換項目文字列を指定してparseConverter(String schema, String val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.DataSetXMLConverter<BR> {"
	 * "}",,"</li>
	 * <li>変換項目 "jp.ossc.nimbus.util.converter.DateFormatConverter{"
	 * "}"(プロパティ指定の誤り。数字以外を指定)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認<BR>
	 * 例外メッセージ"The property injection of this converter is "convertType""を返す</li>
	 * </ul>
	 */
	public void testInitReversibleConverter3() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,\"jp.ossc.nimbus.util.converter.DataSetXMLConverter"
					+ "{\" \"}\",,";
			String val = "jp.ossc.nimbus.util.converter.DataSetXMLConverter{ConvertType=\" \"}";
			dps.parseConverter(schema, val);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
		}
	}

	/**
	 * プロパティスキーマの制約の項目をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと制約文字列を指定してparseConstrain(String schema, String val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.DataSetXMLConverter<BR>
	 * {1}",,"</li>
	 * <li>制約文字列 ""(何も指定しない)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しない</li>
	 * </ul>
	 */
	public void testParseConstrain1() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,\"jp.ossc.nimbus.util.converter.DataSetXMLConverter{1}\",,";
			String val = "";
			dps.parseConstrain(schema, val);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティスキーマの制約の項目をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと制約文字列を指定してparseConstrain(String schema, String val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.DataSetXMLConverter<BR>
	 * {1}",,"@value@ != null""</li>
	 * <li>制約文字列 "@value@ != nul"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しない</li>
	 * </ul>
	 */
	public void testParseConstrain2() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,\"jp.ossc.nimbus.util.converter.DataSetXMLConverter{1}\","
					+ ",\"@value@ != null\"";
			String val = "@value@ != null";
			dps.parseConstrain(schema, val);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティスキーマの制約の項目をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと制約文字列を指定してparseConstrain(String schema, String val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.DataSetXMLConverter<BR>
	 * {1}",,"@value.length@ != 0""</li>
	 * <li>制約文字列 "@value.length@ != 0"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しない</li>
	 * </ul>
	 */
	public void testParseConstrain3() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,\"jp.ossc.nimbus.util.converter.DataSetXMLConverter{1}\","
					+ ",\"@value.length@ != 0\"";
			String val = "@value.length@ != 0";
			dps.parseConstrain(schema, val);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティスキーマの制約の項目をパースするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマと制約文字列を指定してparseConstrain(String schema, String val)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.DataSetXMLConverter<BR>
	 * {1}",,"@valu@ != nul""</li>
	 * <li>制約文字列 "@valu@ != nul"("value"の綴りが正しくない)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認<BR>
	 * 例外メッセージ""Illegal constrain :
	 * 
	 * @valu@ != nul"を返す</li>
	 *        </ul>
	 */
	public void testParseConstrain4() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,\"jp.ossc.nimbus.util.converter.DataSetXMLConverter{ConvertType=1}\","
					+ ",\"@valu@ != nul\"";
			String val = "@valu@ != nul";
			dps.parseConstrain(schema, val);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
			assertEquals(
					"jp.ossc.nimbus.beans.dataset.DefaultPropertySchema{name=null,type=null,parseConverter=null,"
							+ "formatConverter=null,constrain=null}:Illegal constrain : @valu@ != nul",
					e.getMessage());
		}
	}

	/**
	 * スキーマに指定した名前を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>"A,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>getName()で取得した名前が定義した内容と一致している</li>
	 * </ul>
	 */
	public void testGetName() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,,,";
			dps.setSchema(schema);
			assertEquals(dps.getName(), "A");
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * スキーマに指定した型を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>"A,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>getType()で取得した名前が定義した内容と一致している</li>
	 * </ul>
	 */
	public void testGetType() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,,,";
			dps.setSchema(schema);
			assertTrue(dps.getType() == java.lang.String.class);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * スキーマに指定したパース用コンバータを取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.DataSetXMLConverter<BR>
	 * {ConvertType=1}",,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>getParseConverter()で取得したConverterが定義した内容と一致している</li>
	 * </ul>
	 */
	public void testGetParseConverter() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String," +
					"\"jp.ossc.nimbus.util.converter.DataSetXMLConverter{ConvertType=1}\",,";
			dps.setSchema(schema);
			assertTrue(dps.getParseConverter() instanceof 
					jp.ossc.nimbus.util.converter.DataSetXMLConverter); 
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * スキーマに指定したパース用コンバータを取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,Nimbus#DataSetXMLConverter,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>parseConverterNameに指定したServiceNameオブジェクトが設定されている</li>
	 * </ul>
	 */
	public void testGetParseConverterService() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,Nimbus#DataSetXMLConverter,,";
			dps.setSchema(schema);
			assertEquals(dps.parseConverterName.getServiceManagerName(),
					"Nimbus");
			assertEquals(dps.parseConverterName.getServiceName(),
					"DataSetXMLConverter");
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * スキーマに指定したフォーマット用コンバータを取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,<BR>
	 * ,"jp.ossc.nimbus.util.converter.DataSetXMLConverter,{ConvertType=1}","</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>getFormatConverter()で取得したConverterが定義した内容と一致している</li>
	 * </ul>
	 */
	public void testGetFormatConverter() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,," +
					"\"jp.ossc.nimbus.util.converter.DataSetXMLConverter{ConvertType=1}\",";
			dps.setSchema(schema);
			assertTrue(dps.getFormatConverter() instanceof 
					jp.ossc.nimbus.util.converter.DataSetXMLConverter); 
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * スキーマに指定したフォーマット用コンバータを取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,,"Nimbus#DataSetXMLConverter,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>formatConverterNameにServiceNameオブジェクトが設定されている</li>
	 * </ul>
	 */
	public void testGetFormatConverterService() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,,Nimbus#DataSetXMLConverter,";
			dps.setSchema(schema);
			assertEquals(dps.formatConverterName.getServiceManagerName(),
			"Nimbus");
			assertEquals(dps.formatConverterName.getServiceName(),
			"DataSetXMLConverter");
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * スキーマに指定した制約式を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.DataSetXMLConverter<BR> {"
	 * "}",,"@value@ != null""</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>getConstrain()で取得した文字列が"@value@ != null"と一致している</li>
	 * </ul>
	 */
	public void testGetConstrain() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,jp.ossc.nimbus.util.converter.DataSetXMLConverter,"
					+ "jp.ossc.nimbus.util.converter.DataSetXMLConverter,\"@value@ != null\"";
			dps.setSchema(schema);
			assertEquals(dps.getConstrain(), "@value@ != null");
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * スキーマに指定した制約式を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.DataSetXMLConverter<BR> {"
	 * "}",,"(制約指定なし)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>getConstrain()で取得した文字列がNull</li>
	 * </ul>
	 */
	public void testGetConstrainNull() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,jp.ossc.nimbus.util.converter.DataSetXMLConverter,"
					+ "jp.ossc.nimbus.util.converter.DataSetXMLConverter,";
			dps.setSchema(schema);
			assertEquals(dps.getConstrain(), null);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティの値の設定チェックテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,,,"</li>
	 * <li>次の値を指定してset(Object val)を実行する</li>
	 * <li>"文字列"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外ClassNotFoundExceptionが発生しないことを確認</li>
	 * <li>返り値と設定値が同じことを確認</li>
	 * </ul>
	 */
	public void testSet() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,,,";
			dps.setSchema(schema);
			String val = "文字列";
			assertEquals(dps.set(val), val);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティの値の設定チェックテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,int,,,</li>
	 * <li>次の値を指定してset(Object val)を実行する</li>
	 * <li>Map型の値 (型の不一致)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaCheckExceptionが発生するを確認</li>
	 * </ul>
	 */
	public void testSetBadValue() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,int,,,";
			dps.setSchema(schema);
			Map val = new HashMap();
			assertEquals(dps.set(val), val);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaCheckException e) {
		}
	}

	/**
	 * プロパティ値をフォーマットして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.util.Date,,"jp.ossc.nimbus.util.converter.DateFormatConverter<BR>
	 * {ConvertType=1;Format="yyyy-MM-DD"}","</li>
	 * <li>次の値を指定してformat(Object val)を実行する</li>
	 * <li>Date型オブジェクト</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>返り値が指定したフォーマット"yyyy-MM-DD"で正しく変換されることを確認
	 */
	public void testFormat() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.util.Date,," +
					"\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\",";
			dps.setSchema(schema);
			Calendar cal = Calendar.getInstance();
			cal.set(2008, 0, 22);
			Date date = cal.getTime();
			assertEquals(dps.format(date), "2008-01-22");
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * プロパティの値の設定チェックテスト(Date)。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.util.Date,,,"(出力変換指定なし)</li>
	 * <li>次の値を指定してformat(Object val)を実行する</li>
	 * <li>Date型オブジェクト</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外ClassNotFoundExceptionが発生しないことを確認</li>
	 * <li>返り値が指定した値と同じで"yyyy/MM/dd"の形式で返されることを確認
	 */
	public void testFormatNullDate() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.util.Date,,,";
			dps.setSchema(schema);
			Calendar cal = Calendar.getInstance();
			cal.set(2008, 0, 22);
			Date date = cal.getTime();
			String s = (String)dps.format(date);
			assertTrue(s.startsWith("2008/01/22"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティの値の設定チェックテスト(BigDecimal)。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.math.BigDecimal,,,"(出力変換指定なし)</li>
	 * <li>値を指定してformat(Object val)を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外ClassNotFoundExceptionが発生しないことを確認</li>
	 * <li>返り値が指定した値を文字列化したものが返されることを確認
	 */
	public void testFormatNullBigDecimal() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.math.BigDecimal,,,";
			dps.setSchema(schema);
			BigDecimal val = new BigDecimal("12345678901234567890.12345678");
			assertEquals("12345678901234567890.12345678", (String)dps.format(val));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティの値の設定チェックテスト(BigDecimal配列)。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.math.BigDecimal[],,,"(出力変換指定なし)</li>
	 * <li>値を指定してformat(Object val)を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外ClassNotFoundExceptionが発生しないことを確認</li>
	 * <li>返り値が指定した値を文字列化したものが返されることを確認
	 */
	public void testFormatNullBigDecimalArray() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.math.BigDecimal[],,,";
			dps.setSchema(schema);
			BigDecimal[] val = new BigDecimal[]{new BigDecimal("12345678901234567890.12345678"),
					new BigDecimal("99945678901234567890.12345678")};
			assertEquals("12345678901234567890.12345678," +
					"99945678901234567890.12345678", (String)dps.format(val));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティの値の設定チェックテスト(BigInteger)。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.math.BigInteger,,,"(出力変換指定なし)</li>
	 * <li>値を指定してformat(Object val)を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外ClassNotFoundExceptionが発生しないことを確認</li>
	 * <li>返り値が指定した値を文字列化したものが返されることを確認
	 */
	public void testFormatNullBigInteger() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.math.BigInteger,,,";
			dps.setSchema(schema);
			BigInteger val = new BigInteger("12345678901234567890");
			assertEquals("12345678901234567890", (String)dps.format(val));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティの値の設定チェックテスト(BigInteger配列)。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.math.BigInteger[],,,"(出力変換指定なし)</li>
	 * <li>値を指定してformat(Object val)を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外ClassNotFoundExceptionが発生しないことを確認</li>
	 * <li>返り値が指定した値を文字列化したものが返されることを確認
	 */
	public void testFormatNullBigIntegerArray() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.math.BigInteger[],,,";
			dps.setSchema(schema);
			BigDecimal[] val = new BigDecimal[]{new BigDecimal("12345678901234567890"),
					new BigDecimal("99945678901234567890")};
			assertEquals("12345678901234567890," +
					"99945678901234567890", (String)dps.format(val));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * プロパティの値の設定チェックテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.util.Date,<BR>
	 * "jp.ossc.nimbus.util.converter.DateFormatConverter,{ConvertType=1,Format="yyyy-MM-DD"}",,"</li>
	 * <li>次の値を指定してparse(Object val)を実行する</li>
	 * <li>"yyyy-MM-DD"のフォーマットの日付を表した文字列</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外ClassNotFoundExceptionが発生しないことを確認</li>
	 * <li>返り値がDate型オブジェクトで正しく変換されることを確認
	 */
	public void testParse() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String," +
					"\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\",,";
			dps.setSchema(schema);
			Calendar cal = Calendar.getInstance();
			cal.set(2008, 0, 22);
			Date date = cal.getTime();
			assertEquals(dps.parse(date), "2008-01-22");
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティの値の設定チェックテスト。(Date)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.util.Date,,,"(入力変換指定なし)</li>
	 * <li>次の値を指定してparse(Object val)を実行する</li>
	 * <li>Date型オブジェクト</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外ClassNotFoundExceptionが発生しないことを確認</li>
	 * <li>返り値が指定した値と同じことを確認
	 */
	public void testParseNullDate() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.util.Date,,,";
			dps.setSchema(schema);
			String vals = "2008/02/04 12:11:10 100";
			Date val = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS").parse(vals);
			assertEquals(val, (Date)dps.parse(vals));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}catch (java.text.ParseException e) {
			e.printStackTrace();
			fail("例外発生");
		}

	}
	
	/**
	 * プロパティの値の設定チェックテスト(BigDecimal)。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.math.BigDecimal,,,"(入力変換指定なし)</li>
	 * <li>次の値を指定してparse(Object val)を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外ClassNotFoundExceptionが発生しないことを確認</li>
	 * <li>返り値が指定した値が返されることを確認
	 */
	public void testParseNullBigDecimal() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.math.BigDecimal,,,";
			dps.setSchema(schema);
			String vals = "12345678901234567890.12345678";
			BigDecimal val = new BigDecimal(vals);
			assertEquals(val, (BigDecimal)dps.parse(vals));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	
	/**
	 * プロパティの値の設定チェックテスト(BigDecimal配列)。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.math.BigDecimal[],,,"(入力変換指定なし)</li>
	 * <li>次の値を指定してparse(Object val)を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外ClassNotFoundExceptionが発生しないことを確認</li>
	 * <li>返り値が指定した値が返されることを確認
	 */
	public void testParseNullBigDecimalArray() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.math.BigDecimal[],,,";
			dps.setSchema(schema);
			String vals = "12345678901234567890.12345678,99945678901234567890.12345678";
			BigDecimal[] val = new BigDecimal[]{new BigDecimal("12345678901234567890.12345678"),
					new BigDecimal("99945678901234567890.12345678")};
			BigDecimal[] pval = (BigDecimal[])dps.parse(vals);
			assertEquals(val[0], pval[0]);
			assertEquals(val[1], pval[1]);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	
	/**
	 * プロパティの値の設定チェックテスト(BigInteger)。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.math.BigInteger,,,"(入力変換指定なし)</li>
	 * <li>次の値を指定してparse(Object val)を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外ClassNotFoundExceptionが発生しないことを確認</li>
	 * <li>返り値が指定した値が返されることを確認
	 */
	public void testParseNullBigInteger() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.math.BigInteger,,,";
			dps.setSchema(schema);
			String vals = "12345678901234567890";
			BigInteger val = new BigInteger(vals);
			assertEquals(val, (BigInteger)dps.parse(vals));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	
	/**
	 * プロパティの値の設定チェックテスト(BigInteger配列)。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.math.BigInteger[],,,"(入力変換指定なし)</li>
	 * <li>次の値を指定してparse(Object val)を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外ClassNotFoundExceptionが発生しないことを確認</li>
	 * <li>返り値が指定した値が返されることを確認
	 */
	public void testParseNullBigIntegerArray() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.math.BigInteger[],,,";
			dps.setSchema(schema);
			String vals = "12345678901234567890,99945678901234567890";
			BigInteger[] val = new BigInteger[]{new BigInteger("12345678901234567890"),
					new BigInteger("99945678901234567890")};
			BigInteger[] pval = (BigInteger[])dps.parse(vals);
			assertEquals(val[0], pval[0]);
			assertEquals(val[1], pval[1]);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}
	
	
	
	/**
	 * プロパティの値のチェックテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,,,"</li>
	 * <li>次の値を指定してcheckSchema(Object val)を実行する</li>
	 * <li>"文字列"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外ClassNotFoundExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testCheckSchema() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,,,";
			dps.setSchema(schema);
			String val = "文字列";
			dps.checkSchema(val);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティの値のチェックテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,int,,,</li>
	 * <li>次の値を指定してcheckSchema(Object val)を実行する</li>
	 * <li>Map型の値 (型の不一致)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaCheckExceptionが発生するを確認</li>
	 * </ul>
	 */
	public void testCheckSchemaInvalidValue() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,int,,,";
			dps.setSchema(schema);
			Map val = new HashMap();
			dps.checkSchema(val);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaCheckException e) {
		}
	}

	/**
	 * プロパティの値がスキーマ定義の型に適合しているかのチェックテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,,,</li>
	 * <li>次の値を指定してcheckType(Object val)を実行する</li>
	 * <li>"文字列"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外ClassNotFoundExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testCheckTypeStr() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,,,";
			dps.setSchema(schema);
			String val = "文字列";
			dps.checkType(val);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティの値がスキーマ定義の型に適合しているかのチェックテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,int,,,</li>
	 * <li>次の値を指定してcheckType(Object val)を実行する</li>
	 * <li>Integer型の値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外ClassNotFoundExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testCheckTypeInt() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,int,,,";
			dps.setSchema(schema);
			int val = 1;
			dps.checkType(new Integer(val));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティの値がスキーマ定義の型に適合しているかのチェックテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,int,,,</li>
	 * <li>次の値を指定してcheckType(Object val)を実行する</li>
	 * <li>null</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外ClassNotFoundExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testCheckTypeNull1() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,int,,,";
			dps.setSchema(schema);
			dps.checkType(null);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティの値がスキーマ定義の制約に適合しているかチェックのチェックテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.DataSetXMLConverter<BR>
	 * {1}",,"@value@ != null""</li>
	 * <li>次の値を指定してcheckConstrain(Object val)を実行する</li>
	 * <li>nullでない文字列</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaCheckExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testCheckConstrain() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String,\"jp.ossc.nimbus.util.converter.DataSetXMLConverter{ConvertType=1}\"" +
					",,\"@value@ != null\"";
			dps.setSchema(schema);
			dps.validate("test");
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティの値がスキーマ定義の制約に適合しているかチェックのチェックテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.DataSetXMLConverter<BR>
	 * {1}",,"@value.length@ > 5""</li>
	 * <li>次の値を指定してvalidate(Object val)を実行する</li>
	 * <li>1234 （制約に反する）</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>戻り値がfalseであることを確認</li>
	 * </ul>
	 */
	public void testCheckConstrainNull() {
		DefaultPropertySchema dps = new DefaultPropertySchema();
		String schema = "A,java.lang.String,\"jp.ossc.nimbus.util.converter.DataSetXMLConverter{ConvertType=1}\"" +
				",,\"@value.length@ > 5\"";
		dps.setSchema(schema);
		assertFalse(dps.validate("1234"));
	}

	/**
	 * スキーマの文字列表現を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
	 * <li>スキーマ："A,java.lang.String,"jp.ossc.nimbus.util.converter.DataSetXMLConverter<BR>
	 * {1}","jp.ossc.nimbus.util.converter.DataSetXMLConverter{1}","@value.length@ > 5""</li>
	 * <li>次の値を指定してtoString()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>次の文字列が返されることを確認</li>
	 * <li>"jp.ossc.nimbus.beans.dataset.DefaultPropertySchema{name=A,<BR>
	 * type=java.lang.String,parseConverter=jp.ossc.nimbus.util.converter.DataSetXMLConverter<BR>
	 * で始まる文字列であることを確認
	 * 
	 * constrain=null}"</li>
	 * </ul>
	 */
	public void testToString() {
		try {
			DefaultPropertySchema dps = new DefaultPropertySchema();
			String schema = "A,java.lang.String," +
					"\"jp.ossc.nimbus.util.converter.DataSetXMLConverter{ConvertType=1}\"," +
					"\"jp.ossc.nimbus.util.converter.DataSetXMLConverter{ConvertType=1}\"," +
					"\"@value@ != null \"";
			dps.setSchema(schema);
			assertTrue(dps.toString().startsWith("jp.ossc.nimbus.beans.dataset" +
					".DefaultPropertySchema{name=A,type=java.lang.String," +
					"parseConverter=jp.ossc.nimbus.util.converter.DataSetXMLConverter")); 
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

}
