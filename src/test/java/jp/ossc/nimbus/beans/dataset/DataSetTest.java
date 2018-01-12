package jp.ossc.nimbus.beans.dataset;

import java.util.*;

import junit.framework.TestCase;


//
/**
 * 
 * @author S.Teshima
 * @version 1.00 作成: 2008/01/17 - S.Teshima
 */

public class DataSetTest extends TestCase {
	public DataSetTest(String arg0) {
		super(arg0);
	}

	
	 public static void main(String[] args) {
	 junit.textui.TestRunner.run(DataSetTest.class); }
	 
	/**
	 * 空のデータセットを生成するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>空のデータセットを生成する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>データセットの名前がnullであることを確認</li>
	 * </ul>
	 */
	public void testDataSet() {
		DataSet dataset = new DataSet();
		assertNull(dataset.getName());
	}

	/**
	 * 名前付きのデータセットを生成するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet("TEST_DATASET")で名前付きのDataSetを生成する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>Dataset#getname()が"TEST_DATASET"を返す。</li>
	 * </ul>
	 */
	public void testDataSetString() {
		DataSet dataset = new DataSet("TEST_DATASET");
		assertEquals("TEST_DATASET", dataset.getName());
	}

	/**
	 * データセット名を設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet("TESTDS")で名前付きのDataSetを生成する</li>
	 * <li>データセットの名前が"TEST"であることを確認</li>
	 * <li>Dataset#SetName("TEST_DATASET")で名前を設定する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>Dataset#getname()が"TEST_DATASET"を返す。</li>
	 * </ul>
	 */
	public void testSetName1() {
		DataSet dataset = new DataSet("TESTDS");
		assertEquals("TESTDS", dataset.getName());
		dataset.setName("TEST_DATASET");
		assertEquals("TEST_DATASET", dataset.getName());
	}

	/**
	 * 空のデータセットに名前を設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>データセットの名前がnullであることを確認</li>
	 * <li>Dataset#SetName("TEST_DATASET")で名前を設定する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>Dataset#getname()が"TEST_DATASET"を返す。</li>
	 * </ul>
	 */
	public void testSetName2() {
		DataSet dataset = new DataSet();
		assertNull(dataset.getName());
		dataset.setName("TEST_DATASET");
		assertEquals("TEST_DATASET", dataset.getName());
	}

	/**
	 * 名前を持たないHeader のスキーマを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setHeaderSchema(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>Dataset#getHeader()で名前なしのHeaderを取得できる。</li>
	 * <li>取得したHeaderのスキーマが設定値と一致している</li>
	 * </ul>
	 */
	public void testSetHeaderSchemaString() {
		try {
			DataSet dataset = new DataSet();
			String schema = ":A,java.lang.String,,,";
			dataset.setHeaderSchema(schema);
			Header header = dataset.getHeader();
			assertEquals(schema, header.schema);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 名前を持たないHeader のスキーマを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setHeaderSchema(String schema)を実行する</li>
	 * <li>"A,java.lang.String,,," （クラス区切りのない不正な指定）</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetHeaderSchemaStringInvalid() {
		try {
			DataSet dataset = new DataSet();
			String schema = "A,java.lang.String,,,";
			dataset.setHeaderSchema(schema);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
		}
	}

	/**
	 * 指定した名前のHeader のスキーマを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setHeaderSchema(String name, String schema)を実行する</li>
	 * <li>name : "test_header"</li>
	 * <li>schema: ":A,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>Dataset#getHeader(String name)でHeaderを取得できる。</li>
	 * </ul>
	 */
	public void testSetHeaderSchemaStringString() {
		try {
			DataSet dataset = new DataSet();
			String hname = "test_header";
			String schema = ":A,java.lang.String,,,";
			dataset.setHeaderSchema(hname, schema);
			Header header = dataset.getHeader(hname);
			assertEquals(schema, header.schema);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定した名前のHeader のスキーマを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setHeaderSchema(String name, String schema)を実行する</li>
	 * <li>name : "test_header"</li>
	 * <li>"A,java.lang.String,,," （クラス区切りのない不正な指定）</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetHeaderSchemaStringStringInvalid() {
		try {
			DataSet dataset = new DataSet();
			String hname = "test_header";
			String schema = "A,java.lang.String,,,";
			dataset.setHeaderSchema(hname, schema);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
		}
	}

	/**
	 * 名前を持たないRecordList のスキーマを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setRecordListSchema(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>Dataset#getRecordList()で名前なしのRecordListを取得できる。</li>
	 * </ul>
	 */
	public void testSetRecordListSchemaString() {
		try {
			DataSet dataset = new DataSet();
			String schema = ":A,java.lang.String,,,";
			dataset.setRecordListSchema(schema);
			RecordList rlist = dataset.getRecordList();
			assertEquals(schema, rlist.schema);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 名前を持たないRecordList のスキーマを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setRecordListSchema(String schema)を実行する</li>
	 * <li>"A,java.lang.String,,," （クラス区切りのない不正な指定）</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetRecordListSchemaStringInvalid() {
		try {
			DataSet dataset = new DataSet();
			String schema = "A,java.lang.String,,,";
			dataset.setRecordListSchema(schema);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
		}
	}

	/**
	 * 指定した名前のRecordList のスキーマを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setRecordListSchema(String name, String
	 * schema)を実行する</li>
	 * <li>name : "test_rlist"</li>
	 * <li>schema: ":A,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>Dataset#getRecordList(String name)でRecordListを取得できる。</li>
	 * </ul>
	 */
	public void testSetRecordListSchemaStringString() {
		try {
			DataSet dataset = new DataSet();
			String name = "test_rlist";
			String schema = ":A,java.lang.String,,,";
			dataset.setRecordListSchema(name, schema);
			RecordList rlist = dataset.getRecordList(name);
			assertEquals(schema, rlist.schema);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定した名前のRecordList のスキーマを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setRecordListSchema(String name, String
	 * schema)を実行する</li>
	 * <li>name : "test_rlist"</li>
	 * <li>"A,java.lang.String,,," （クラス区切りのない不正な指定）</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetRecordListSchemaStringStringInvalid() {
		try {
			DataSet dataset = new DataSet();
			String name = "test_rlist";
			String schema = "A,java.lang.String,,,";
			dataset.setRecordListSchema(name, schema);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
		}
	}
	

	/**
	 * 指定した名前のネストRecordList のスキーマを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setNestedRecordListSchema(String name, String
	 * schema)を実行する</li>
	 * <li>name : "test_nestlist"</li>
	 * <li>schema: ":A,java.lang.String\n:B,int"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>getNestedRecordListSchema(name)で指定した名前のネストRecordListのスキーマをを取得できる。</li>
	 * </ul>
	 */
	public void testSetGetNestedRecordListSchema() {
		try {
			DataSet dataset = new DataSet();
			String name = "test_nestlist";
			String schema = ":A,java.lang.String\n:B,int";
			dataset.setNestedRecordListSchema(name, schema);
			assertEquals(schema, dataset.getNestedRecordListSchema(name).getSchema());
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * 指定した名前のネストRecordList のスキーマを取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>ネストRecordListは設定しない</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>getNestedRecordListSchema(name)でnullが返される。</li>
	 * </ul>
	 */
	public void testGetNestedRecordListSchemaNull() {
		try {
			DataSet dataset = new DataSet();
			assertNull(dataset.getNestedRecordListSchema("test"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * 定義された順に並んだネストしたレコードリスト名配列を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setNestedRecordListSchema(String name, String
	 * schema)を３回実行する(xxの部分は3,1,6の順で指定する)</li>
	 * <li>name : "test_nestlistxx"</li>
	 * <li>schema: ":Axx,java.lang.String\n:Bxx,int"</li>
	 * <li>getNestedRecordListSchemaNames()を実行する</li>
	 * <li>getNestedRecordListSchemaSize()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>定義された順に並んだネストしたレコードリスト名配列を取得できる。</li>
	 * <li>定義されたネストしたレコードリスト数を取得できる。</li>
	 * </ul>
	 */
	public void testGetNestedRecordListSchemaNames() {
		try {
			DataSet dataset = new DataSet();
			
			String name = "test_nestlist3";
			String schema = ":A3,java.lang.String\n:B3,int";
			dataset.setNestedRecordListSchema(name, schema);
			
			name = "test_nestlist1";
			schema = ":A1,java.lang.String\n:B1,int";
			dataset.setNestedRecordListSchema(name, schema);
			
			name = "test_nestlist6";
			schema = ":A6,java.lang.String\n:B6,int";
			dataset.setNestedRecordListSchema(name, schema);
			
			String[] names = new String[]{"test_nestlist3","test_nestlist1","test_nestlist6"};
			
			assertTrue(Arrays.equals(names, dataset.getNestedRecordListSchemaNames()));
			assertEquals(names.length, dataset.getNestedRecordListSchemaSize());
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * 定義された順に並んだネストしたレコードリスト名配列を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>ネストしたレコードリスト未指定でgetNestedRecordListSchemaNames()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>要素数０の文字型配列が返される</li>
	 * </ul>
	 */
	public void testGetNestedRecordListSchemaNamesNotExist() {
		try {
			DataSet dataset = new DataSet();
			
			
			String[] names = new String[0];
			
			assertTrue(Arrays.equals(names, dataset.getNestedRecordListSchemaNames()));
			assertEquals(names.length, dataset.getNestedRecordListSchemaSize());
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * ネストしたレコードリストのマップを取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setNestedRecordListSchema(String name, String
	 * schema)を３回実行する(xxの部分は3,1,6の順で指定する)</li>
	 * <li>name : "test_nestlistxx"</li>
	 * <li>schema: ":Axx,java.lang.String\n:Bxx,int"</li>
	 * <li>getNestedRecordListSchemaMap()を実行して、ネストしたレコードリストのマップを取得する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>マップの内容（キーはレコードリスト名、値はレコードスキーマ）が正しいか検証する</li>
	 * </ul>
	 */
	public void testGetNestedRecordListSchemaMap() {
		try {
			DataSet dataset = new DataSet();
			
			String name = "test_nestlist3";
			String schema = ":A3,java.lang.String\n:B3,int";
			dataset.setNestedRecordListSchema(name, schema);
			
			name = "test_nestlist1";
			schema = ":A1,java.lang.String\n:B1,int";
			dataset.setNestedRecordListSchema(name, schema);
			
			name = "test_nestlist6";
			schema = ":A6,java.lang.String\n:B6,int";
			dataset.setNestedRecordListSchema(name, schema);
			
			//mapを取得して登録されている内容を検証する
			Map map = dataset.getNestedRecordListSchemaMap();
			assertEquals(":A3,java.lang.String\n:B3,int",
					map.get("test_nestlist3"));
			assertEquals(":A1,java.lang.String\n:B1,int",
					map.get("test_nestlist1"));
			assertEquals(":A6,java.lang.String\n:B6,int",
					map.get("test_nestlist6"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * ネストしたレコードリストのマップを取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>getNestedRecordListSchemaMap()を実行して、ネストしたレコードリストのマップを取得する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>空のマップが返される</li>
	 * </ul>
	 */
	public void testGetNestedRecordListSchemaMapEmpty() {
		try {
			DataSet dataset = new DataSet();
			
			//mapを取得して登録されている内容を検証する
			Map map = dataset.getNestedRecordListSchemaMap();
			assertEquals(0,map.size());
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}
	
	/**
	 * 名前を持たないHeaderとRecordList のスキーマを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setSchema(String headerSchema, String
	 * recordListSchema)を実行する</li>
	 * <li>headerSchema ":A,java.lang.String,,,"</li>
	 * <li>recordListSchema ":B,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>Dataset#getHeader()で名前なしのHeaderを取得できる。</li>
	 * <li>Dataset#getRecordList()で名前なしのRecordListを取得できる。</li>
	 * </ul>
	 */
	public void testSetSchemaStringString() {
		try {
			DataSet dataset = new DataSet();
			String hschema = ":A,java.lang.String,,,";
			String rschema = ":B,java.lang.String,,,";
			dataset.setSchema(hschema, rschema);

			Header header = dataset.getHeader();
			assertEquals(hschema, header.schema);

			RecordList rlist = dataset.getRecordList();
			assertEquals(rschema, rlist.schema);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 名前を持たないHeaderとRecordList のスキーマを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setSchema(String headerSchema, String
	 * recordListSchema)を実行する</li>
	 * <li>headerSchema ""A,java.lang.String,,," （クラス区切りのない不正な指定）"</li>
	 * <li>recordListSchema ":B,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetSchemaStringStringInvalid() {
		try {
			DataSet dataset = new DataSet();
			String hschema = "A,java.lang.String,,,";
			String rschema = ":B,java.lang.String,,,";
			dataset.setSchema(hschema, rschema);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
		}
	}

	/**
	 * 指定した名前のHeaderとRecordList のスキーマを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setSchema(String name,String headerSchema, String
	 * recordListSchema)を実行する</li>
	 * <li>name : "test_name"</li>
	 * <li>headerSchema ":A,java.lang.String,,,"</li>
	 * <li>recordListSchema ":B,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>Dataset#getHeader(String name)でHeaderを取得できる。</li>
	 * <li>Dataset#getRecordList(String name)でRecordListを取得できる。</li>
	 * </ul>
	 */
	public void testSetSchemaStringStringString() {
		try {
			DataSet dataset = new DataSet();
			String name = "test_name";
			String hschema = ":A,java.lang.String,,,";
			String rschema = ":B,java.lang.String,,,";
			dataset.setSchema(name, hschema, rschema);

			Header header = dataset.getHeader(name);
			assertEquals(hschema, header.schema);

			RecordList rlist = dataset.getRecordList(name);
			assertEquals(rschema, rlist.schema);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定した名前のHeaderとRecordList のスキーマを設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setSchema(String name,String headerSchema, String
	 * recordListSchema)を実行する</li>
	 * <li>name : "test_name"</li>
	 * <li>headerSchema "A,java.lang.String,,," （クラス区切りのない不正な指定）</li>
	 * <li>recordListSchema ":B,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetSchemaStringStringStringInvalid() {
		try {
			DataSet dataset = new DataSet();
			String name = "test_name";
			String hschema = "A,java.lang.String,,,";
			String rschema = ":B,java.lang.String,,,";
			dataset.setSchema(name, hschema, rschema);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
		}
	}

	/**
	 * 定義された順に並んだヘッダー名配列を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setHeaderSchema(String name, String
	 * schema)を2回実行する</li>
	 * <li>name : "test_header1"</li>
	 * <li>":A,java.lang.String,,," </li>
	 * <li>name : "test_header2"</li>
	 * <li>":A,java.lang.String,,," </li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>Dataset#getHeaderNames()で定義された順で名前の配列を取得できる。</li>
	 * </ul>
	 */
	public void testGetHeaderNames() {
		try {
			DataSet dataset = new DataSet();
			String hname = "test_header1";
			String schema = ":A,java.lang.String,,,";
			dataset.setHeaderSchema(hname, schema);
			hname = "test_header2";
			dataset.setHeaderSchema(hname, schema);
			assertEquals("test_header1", dataset.getHeaderNames()[0]);
			assertEquals("test_header2", dataset.getHeaderNames()[1]);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * ヘッダーの数を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setHeaderSchema(String name, String
	 * schema)を2回実行する</li>
	 * <li>name : "test_header1"</li>
	 * <li>":A,java.lang.String,,," </li>
	 * <li>name : "test_header2"</li>
	 * <li>":A,java.lang.String,,," </li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>Dataset#getHeaderSize()でヘッダ数を取得できる。</li>
	 * </ul>
	 */
	public void testGetHeaderSize() {
		try {
			DataSet dataset = new DataSet();
			String hname = "test_header1";
			String schema = ":A,java.lang.String,,,";
			dataset.setHeaderSchema(hname, schema);
			hname = "test_header2";
			dataset.setHeaderSchema(hname, schema);
			assertEquals(2, dataset.getHeaderSize());
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * ヘッダーのmapを取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setHeaderSchema(String name, String
	 * schema)を2回実行する</li>
	 * <li>name : "test_header1"</li>
	 * <li>":A,java.lang.String,,," </li>
	 * <li>name : "test_header2"</li>
	 * <li>":A,java.lang.String,,," </li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>Dataset#getHeaderMap()でヘッダーのmap（キーはヘッダ名）を取得できる。</li>
	 * </ul>
	 */
	public void testGetHeaderMap() {
		try {
			DataSet dataset = new DataSet();
			String hname = "test_header1";
			String schema = ":A,java.lang.String,,,";
			dataset.setHeaderSchema(hname, schema);
			hname = "test_header2";
			dataset.setHeaderSchema(hname, schema);

			assertEquals(dataset.getHeader("test_header1"), dataset
					.getHeaderMap().get("test_header1"));
			assertEquals(dataset.getHeader("test_header2"), dataset
					.getHeaderMap().get("test_header2"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * ヘッダーを追加するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のヘッダ名とスキーマを指定してHeader(String name, String schema)を実行する</li>
	 * <li>name: "test_header"</li>
	 * <li>schema: ":A,java.lang.String,,,"</li>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#addHeader(Header header)を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>Dataset#getHeader(String name)でHeaderを取得できる。</li>
	 * </ul>
	 */
	public void testAddHeader() {
		try {
			String hname = "test_header";
			String schema = ":A,java.lang.String,,,";
			Header header = new Header(hname, schema);

			DataSet dataset = new DataSet();
			dataset.addHeader(header);
			Header header2 = dataset.getHeader(hname);
			assertEquals(schema, header2.schema);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 定義された順に並んだレコードリスト名配列を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setRecordListSchema(String name, String
	 * schema)を2回実行する</li>
	 * <li>name : "test_rlist1"</li>
	 * <li>":A,java.lang.String,,," </li>
	 * <li>name : "test_rlist2"</li>
	 * <li>":A,java.lang.String,,," </li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>Dataset#getRecordListNames()で定義された順で名前の配列を取得できる。</li>
	 * </ul>
	 */
	public void testGetRecordListNames() {
		try {
			DataSet dataset = new DataSet();
			String name = "test_rlist1";
			String schema = ":A,java.lang.String,,,";
			dataset.setRecordListSchema(name, schema);
			name = "test_rlist2";
			dataset.setRecordListSchema(name, schema);
			assertEquals("test_rlist1", dataset.getRecordListNames()[0]);
			assertEquals("test_rlist2", dataset.getRecordListNames()[1]);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードリストの数を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setRecordListSchema(String name, String
	 * schema)を2回実行する</li>
	 * <li>name : "test_rlist1"</li>
	 * <li>":A,java.lang.String,,," </li>
	 * <li>name : "test_rlist2"</li>
	 * <li>":A,java.lang.String,,," </li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>Dataset#getRecordListSize()でレコードリストの数を取得できる。</li>
	 * </ul>
	 */
	public void testGetRecordListSize() {
		try {
			DataSet dataset = new DataSet();
			String name = "test_rlist1";
			String schema = ":A,java.lang.String,,,";
			dataset.setRecordListSchema(name, schema);
			name = "test_rlist2";
			dataset.setRecordListSchema(name, schema);
			assertEquals(2, dataset.getRecordListSize());
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードリストのmapを取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setRecordListSchema(String name, String
	 * schema)を2回実行する</li>
	 * <li>name : "test_rlist1"</li>
	 * <li>":A,java.lang.String,,," </li>
	 * <li>name : "test_rlist2"</li>
	 * <li>":A,java.lang.String,,," </li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>Dataset#getRecordListMap()でレコードリストのmap（キーはレコードリスト名）を取得できる。</li>
	 * </ul>
	 */
	public void testGetRecordListMap() {
		try {
			DataSet dataset = new DataSet();
			String name = "test_rlist1";
			String schema = ":A,java.lang.String,,,";
			dataset.setRecordListSchema(name, schema);
			name = "test_rlist2";
			dataset.setRecordListSchema(name, schema);

			assertEquals(dataset.getRecordList("test_rlist1"), dataset
					.getRecordListMap().get("test_rlist1"));
			assertEquals(dataset.getRecordList("test_rlist2"), dataset
					.getRecordListMap().get("test_rlist2"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードリストを追加するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のレコードリスト名とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name: "test_rlist"</li>
	 * <li>schema: ":A,java.lang.String,,,"</li>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#addRecordList(RecordList recList)を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>Dataset#getRecordList(String name)でRecordListを取得できる。</li>
	 * </ul>
	 */
	public void testAddRecordList() {
		try {
			String name = "test_rlist";
			String schema = ":A,java.lang.String,,,";
			RecordList rlist = new RecordList(name, schema);

			DataSet dataset = new DataSet();
			dataset.addRecordList(rlist);
			RecordList rlist2 = dataset.getRecordList(name);
			assertEquals(schema, rlist2.schema);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * 指定した名前のネストした{@link RecordList レコードリスト}を生成するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setNestedRecordListSchema(String name, String
	 * schema)を３回実行する(xxの部分は3,1,6の順で指定する)</li>
	 * <li>name : "test_nestlistxx"</li>
	 * <li>schema: ":Axx,java.lang.String\n:Bxx,int"</li>
	 * <li>createNestedRecordList(String name)を実行して、ネストしたレコードリストを取得する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>レコードリストの内容（名前、スキーマ）が正しいか検証する</li>
	 * </ul>
	 */
	public void testCreateNestedRecordList() {
		try {
			DataSet dataset = new DataSet();
			
			String name = "test_nestlist3";
			String schema = ":A3,java.lang.String\n:B3,int";
			dataset.setNestedRecordListSchema(name, schema);
			
			name = "test_nestlist1";
			schema = ":A1,java.lang.String\n:B1,int";
			dataset.setNestedRecordListSchema(name, schema);
			
			name = "test_nestlist6";
			schema = ":A6,java.lang.String\n:B6,int";
			dataset.setNestedRecordListSchema(name, schema);
			
			//レコードリストを取得して（名前、スキーマ）が正しいか検証する
			RecordList rlist = dataset.createNestedRecordList("test_nestlist1");
			assertEquals("test_nestlist1",rlist.getName());
			assertEquals(":A1,java.lang.String\n:B1,int",rlist.getSchema());
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * 指定した名前のネストした{@link RecordList レコードリスト}を生成するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setNestedRecordListSchema(String name, String
	 * schema)を３回実行する(xxの部分は3,1,6の順で指定する)</li>
	 * <li>name : "test_nestlistxx"</li>
	 * <li>schema: ":Axx,java.lang.String\n:Bxx,int"</li>
	 * <li>設定した名前でない値を指定してcreateNestedRecordList(String name)を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>nullが返される</li>
	 * </ul>
	 */
	public void testCreateNestedRecordListNotExist() {
		try {
			DataSet dataset = new DataSet();
			
			String name = "test_nestlist3";
			String schema = ":A3,java.lang.String\n:B3,int";
			dataset.setNestedRecordListSchema(name, schema);
			
			name = "test_nestlist1";
			schema = ":A1,java.lang.String\n:B1,int";
			dataset.setNestedRecordListSchema(name, schema);
			
			name = "test_nestlist6";
			schema = ":A6,java.lang.String\n:B6,int";
			dataset.setNestedRecordListSchema(name, schema);
			
			//存在しないレコードリストを取得する
			assertNull(dataset.createNestedRecordList("test_nestlist"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * 指定した名前のネストした{@link RecordList レコードリスト}を生成するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>createNestedRecordList(String name)を実行して、ネストしたレコードリストを取得する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>レコードリスト未登録なのでnullが返される</li>
	 * </ul>
	 */
	public void testCreateNestedRecordListNotExist2() {
		try {
			DataSet dataset = new DataSet();
			
			
			//レコードリスト未登録の状態でレコードリストを取得する
			assertNull(dataset.createNestedRecordList("test_nestlist"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	
	/**
	 * データセットのヘッダーのデータとレコードリストのレコードを削除するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setSchema(String name,String headerSchema, String
	 * recordListSchema)を実行する</li>
	 * <li>name : "test_name"</li>
	 * <li>headerSchema ":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"a"</li>
	 * <li>recordListSchema ":B,java.lang.String,,,"</li>
	 * <li>レコードを追加し、次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"B"</li>
	 * <li>Object:"b"</li>
	 * 
	 * <li>Dataset#clear()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>ヘッダーの値が存在しない(Header#getProperty("A"))nullを返す</li>
	 * <li>レコードリストのレコード数(rRecordList#size())が0を返す</li>
	 * </ul>
	 */
	public void testClear() {
		try {
			DataSet dataset = new DataSet();
			String name = "test_name";
			String hschema = ":A,java.lang.String,,,";
			String rschema = ":B,java.lang.String,,,";
			dataset.setSchema(name, hschema, rschema);

			Header header = dataset.getHeader(name);
			header.setProperty("A", "a");
			RecordList rlist = dataset.getRecordList(name);
			Record rec = rlist.createRecord();
			rec.setProperty("B", "b");
			rlist.addRecord(rec);

			dataset.clear();
			assertNull(header.getProperty("A"));
			assertEquals(0, rlist.size());
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 同じスキーマを持ちデータを持たない空のデータセットを複製するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setSchema(String name,String headerSchema, String
	 * recordListSchema)を実行する</li>
	 * <li>name : "test_name"</li>
	 * <li>headerSchema ":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"a"</li>
	 * <li>recordListSchema ":B,java.lang.String,,,"</li>
	 * <li>レコードを追加し、次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"B"</li>
	 * <li>Object:"b"</li>
	 * 
	 * <li>Dataset#cloneSchema()を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>複製のデータセットに対して、次の確認を行う</li>
	 * <li>Header、recordListのスキーマが複製元と同じ</li>
	 * <li>Header、recordListのデータは存在しない</li>
	 * </ul>
	 */
	public void testCloneSchema() {
		try {
			DataSet dataset = new DataSet();
			String name = "test_name";
			String hschema = ":A,java.lang.String,,,";
			String rschema = ":B,java.lang.String,,,";
			dataset.setSchema(name, hschema, rschema);

			Header header = dataset.getHeader(name);
			header.setProperty("A", "a");
			RecordList rlist = dataset.getRecordList(name);
			Record rec = rlist.createRecord();
			rec.setProperty("B", "b");
			rlist.addRecord(rec);

			DataSet dataset2 = dataset.cloneSchema();
			Header header2 = dataset2.getHeader(name);
			RecordList rlist2 = dataset2.getRecordList(name);

			assertEquals(header.schema, header2.schema);
			assertEquals(rlist.schema, rlist2.schema);
			assertNull(header2.values);
			assertEquals(0, rlist2.size());
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * データセットを複製するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setSchema(String name,String headerSchema, String
	 * recordListSchema)を実行する</li>
	 * <li>name : "test_name"</li>
	 * <li>headerSchema ":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"a"</li>
	 * <li>recordListSchema ":B,java.lang.String,,,"</li>
	 * <li>レコードを追加し、次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"B"</li>
	 * <li>Object:"b"</li>
	 * 
	 * <li>Dataset#cloneDataSet()を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>複製のデータセットに対して、次の確認を行う</li>
	 * <li>Header、recordListのスキーマが複製元と同じ</li>
	 * <li>Header、recordListのデータは複製元と同じ</li>
	 * </ul>
	 */
	public void testCloneDataSet() {
		try {
			DataSet dataset = new DataSet();
			String name = "test_name";
			String hschema = ":A,java.lang.String,,,";
			String rschema = ":B,java.lang.String,,,";
			dataset.setSchema(name, hschema, rschema);

			Header header = dataset.getHeader(name);
			header.setProperty("A", "a");
			RecordList rlist = dataset.getRecordList(name);
			Record rec = rlist.createRecord();
			rec.setProperty("B", "b");
			rlist.addRecord(rec);

			DataSet dataset2 = dataset.cloneDataSet();

			Header header2 = dataset2.getHeader(name);
			assertEquals(header.schema, header2.schema);
			assertEquals(header.getProperty("A"), header2.getProperty("A"));

			RecordList rlist2 = dataset2.getRecordList(name);
			 Record rec2 = rlist2.getRecord(0);
			 assertEquals(rlist.schema, rlist2.schema);
			 assertEquals(rec.getProperty("B"), rec2.getProperty("B"));

		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * ネストしたレコードリストを含むデータセットを複製するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のネストしたレコードリストを含むスキーマを指定してDataset#setSchema(String name,String headerSchema, String
	 * recordListSchema)を実行する</li>
	 * <li>name : "test_name"</li>
	 * <li>headerSchema "LIST:HrList,\"HrList\""</li>
	 * <li>recordListSchema "LIST:RrList,\"RrList\"</li>
	 * <li>レコードリスト"HrList"、"RrList"については事前にsetNestedRecordListSchemaでスキーマを定義する必要がある</li>
	 * 
	 * <li>Dataset#cloneDataSet()を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>複製のデータセットに対して、次の確認を行う</li>
	 * <li>Header、recordListのスキーマが複製元と同じ</li>
	 * <li>Header、recordListのデータは複製元と同じ</li>
	 * </ul>
	 */
	public void testCloneDataSetNestedRecordList() {
		try {
			DataSet dataset = new DataSet();
			//ネストするレコードリストを作る
			dataset.setNestedRecordListSchema("HrList", ":A,java.lang.String\n:B,java.lang.String");
			dataset.setNestedRecordListSchema("RrList", ":C,java.lang.String\n:D,int");
			
			String name = "test_name";
			String hschema = "LIST:HrList,HrList";
			String rschema = "LIST:RrList,RrList";
			dataset.setSchema(name, hschema, rschema);

			//Headerの値として設定するネストしたレコードリストを取得して値を設定
			RecordList HrList = dataset.createNestedRecordList("HrList");
			Record nrec1 = HrList.createRecord();
			nrec1.setProperty("A", "a");
			nrec1.setProperty("B", "b");
			HrList.addRecord(nrec1);
			//Headerを取得してネストしたレコードリストを値に設定
			Header header = dataset.getHeader(name);
			header.setProperty("HrList", HrList);


			//レコードの値として設定するネストしたレコードリストを取得して値を設定
			RecordList RrList = dataset.createNestedRecordList("RrList");
			Record nrec2 = RrList.createRecord();
			nrec2.setProperty("C", "c");
			nrec2.setProperty("D", 1);
			RrList.addRecord(nrec2);
			//RecordListを取得してレコードにネストしたレコードリストを値に設定

			RecordList rlist = dataset.getRecordList(name);
			Record rec = rlist.createRecord();
			rec.setProperty("RrList", RrList);
			rlist.addRecord(rec);

			DataSet dataset2 = dataset.cloneDataSet();

			//コピー先のHeaderのネストしたレコードリストの内容が正しいか検証する			
			Header header2 = dataset2.getHeader(name);
			assertEquals(header.schema, header2.schema);
			Record copyr =  ((RecordList)header2.getProperty("HrList")).getRecord(0);
			assertEquals(HrList.getSchema(), ((RecordList)header2.getProperty("HrList")).getSchema());
			assertEquals("a", copyr.get("A"));
			assertEquals("b", copyr.get("B"));

			//コピー先のRecordListのネストしたレコードリストの内容が正しいか検証する
			RecordList rlist2 = dataset2.getRecordList(name);
			assertEquals(rlist.schema, rlist2.schema);
			Record rec2 = rlist2.getRecord(0);

			copyr =  ((RecordList)rec2.getProperty("RrList")).getRecord(0);
			assertEquals(RrList.getSchema(), ((RecordList)rec2.getProperty("RrList")).getSchema());
			assertEquals("c", copyr.getProperty("C"));
			assertEquals(1, copyr.getIntProperty("D"));

		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * 同じスキーマを持ちデータを持たない空のデータセットを複製するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet()で空のDataSetを生成する</li>
	 * <li>次のスキーマを指定してDataset#setSchema(String name,String headerSchema, String
	 * recordListSchema)を実行する</li>
	 * <li>name : "test_name"</li>
	 * <li>headerSchema ":A,java.lang.String,,,"</li>
	 * <li>recordListSchema ":A,java.lang.String,,,"</li>
	 * <li>次の値を指定して各Header、RecordのsetProperty(String name, long val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:1</li>
	 * <li>Dataset#clone()を実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>複製のデータセットに対して、次の確認を行う</li>
	 * <li>Header、recordListのスキーマが複製元と同じ</li>
	 * <li>Header、recordListのデータは存在しない</li>
	 * </ul>
	 */
	public void testClone() {
		try {
			DataSet dataset = new DataSet();
			String name = "test_name";
			String hschema = ":A,java.lang.String,,,";
			String rschema = ":B,java.lang.String,,,";
			dataset.setSchema(name, hschema, rschema);

			Header header = dataset.getHeader(name);
			header.setProperty("A", "a");
			RecordList rlist = dataset.getRecordList(name);
			Record rec = rlist.createRecord();
			rec.setProperty("B", "b");
			rlist.addRecord(rec);

			DataSet dataset2 = (DataSet)dataset.cloneDataSet();

			Header header2 = dataset2.getHeader(name);
			assertEquals(header.schema, header2.schema);
			assertEquals(header.getProperty("A"), header2.getProperty("A"));

			RecordList rlist2 = dataset2.getRecordList(name);
			 Record rec2 = rlist2.getRecord(0);
			 assertEquals(rlist.schema, rlist2.schema);
			 assertEquals(rec.getProperty("B"), rec2.getProperty("B"));

		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * データセットの文字列表現を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>Dataset#DataSet("TEST_DATASET")で名前付きのDataSetを生成する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>Dataset#toString()が"{name=TEST_DATASET}"で終わるメッセージを返す。</li>
	 * </ul>
	 */
	public void testToString() {
		DataSet dataset = new DataSet("TEST_DATASET");
		assertTrue(dataset.toString().endsWith("{name=TEST_DATASET}"));
	}

}
