package jp.ossc.nimbus.beans.dataset;

import junit.framework.TestCase;
//
/**
 * 
 * @author   S.Teshima 
 * @version  1.00 作成: 2008/01/18 -　S.Teshima
 */

public class RecordSchemaTest extends TestCase {

	public RecordSchemaTest(String arg0) {
        super(arg0);
    }
    
 
 	public static void main(String[] args) {
        junit.textui.TestRunner.run(RecordSchemaTest.class);
    }
 	
    /**
     * レコードスキーマを取得するテスト。<p>
     * 条件：
     * <ul>
     *   <li>次のスキーマを指定してgetInstance(String schema)を実行する</li>
     *   <li>":A,java.lang.String,,,"  + 改行</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>レコードスキーマが正常に取得できる</li>
     *   <li>例外PropertySchemaDefineExceptionが発生しないことを確認</li>
     * </ul>
     */
	public void testGetInstance1() {
		try {
			String schema = ":A,java.lang.String,,,\n";
			RecordSchema.getInstance(schema);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

    /**
     * 同じスキーマ定義のレコードスキーマを取得するテスト。<p>
     * 条件：
     * <ul>
      *  <li>次のスキーマを指定してgetInstance(String schema)を2回実行する</li>
     *   <li>":A,java.lang.String,,,"  + 改行</li>
    * </ul>
     * 確認：
     * <ul>
     *   <li>レコードスキーマが正常に取得できる</li>
     *   <li>例外PropertySchemaDefineExceptionが発生しない</li>
     *   <li>2回目で取得したインスタンスが新しく生成されていない(1回目と同じオブジェクト)</li>
     * </ul>
     */
	public void testGetInstance2() {
		try {
			String schema = ":A,java.lang.String,,,\n";
			RecordSchema rsm1 = RecordSchema.getInstance(schema);
			assertEquals(rsm1.getSchema(), schema);
			RecordSchema rsm2 = RecordSchema.getInstance(schema);
			assertEquals(rsm1, rsm2);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

    /**
     * レコードスキーマを設定するテスト。<p>
     * 条件：
     * <ul>
     *   <li>プロパティ数は１個</li>
     *   <li>プロパティスキーマ実装クラス指定なし</li>
     *   <li>プロパティスキーマ定義内容（型のみ）</li>
     *   <li>次の内容で指定する ":A,java.lang.String,,,"</li>
     *   <li>最後に改行があるときとないときで確認する</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>レコードスキーマが正常に設定できる</li>
     *   <li>例外PropertySchemaDefineExceptionが発生しない</li>
     * </ul>
     */
	public void testSetSchema1() {
		try {
			String schema = ":A,java.lang.String,,,\n";
			RecordSchema rsm1 = RecordSchema.getInstance(schema);
			assertEquals(rsm1.getSchema(), schema);
			schema = ":A,java.lang.String,,,";
			RecordSchema rsm2 = RecordSchema.getInstance(schema);
			assertEquals(rsm2.getSchema(), schema);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
     * レコードスキーマを設定するテスト。<p>
     * 条件：
     * <ul>
     *   <li>プロパティ数は２個</li>
     *   <li>プロパティスキーマ実装クラス指定なし</li>
     *   <li>プロパティスキーマ定義内容（型のみ）</li>
     *   <li>次の内容で指定する<BR> ":A,java.lang.String,,," <BR> ":B,int,,,"</li>
     *   <li>改行はCRLF,CR,LFで確認する</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>レコードスキーマが正常に設定できる</li>
     *   <li>例外PropertySchemaDefineExceptionが発生しない</li>
     * </ul>
     */
	public void testSetSchema2() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,int,,,";
			RecordSchema rsm1 = RecordSchema.getInstance(schema);
			assertEquals(rsm1.getSchema(), schema);
			schema = ":A,java.lang.String,,,\r:B,int,,,";
			RecordSchema rsm2 = RecordSchema.getInstance(schema);
			assertEquals(rsm2.getSchema(), schema);
			schema = ":A,java.lang.String,,,\r\n:B,int,,,";
			RecordSchema rsm3 = RecordSchema.getInstance(schema);
			assertEquals(rsm3.getSchema(), schema);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
     * レコードスキーマを設定するテスト。<p>
     * 条件：
     * <ul>
     *   <li>プロパティ数は２個</li>
     *   <li>プロパティスキーマ実装クラス指定なし</li>
     *   <li>プロパティスキーマ定義内容（型のみ）</li>
     *   <li>２つのプロパティスキーマ定義の間に改行のみ指定する</li>
     *   <li>次の内容で指定する<BR> ":A,java.lang.String,,," <BR><BR> ":B,int,,,"</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>レコードスキーマが正常に設定できる</li>
     *   <li>例外PropertySchemaDefineExceptionが発生しない</li>
     * </ul>
     */
	public void testSetSchema3() {
		try {
			String schema = ":A,java.lang.String,,,\n\n:B,int,,,";
			RecordSchema rsm1 = RecordSchema.getInstance(schema);
			assertEquals(rsm1.getSchema(), schema);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
     * レコードスキーマを設定するテスト。<p>
     * 条件：
     * <ul>
     *   <li>プロパティ数は２個</li>
     *   <li>プロパティスキーマ実装クラス指定なし</li>
     *   <li>プロパティスキーマ定義内容（型のみ）</li>
     *   <li>２つ目の定義のプロパティスキーマ実装クラスの区切り文字「:」を指定しない</li>
     *   <li>次の内容で指定する<BR> ":A,java.lang.String,,," <BR> "B,int,,,"</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>例外PropertySchemaDefineExceptionが発生する</li>
     *   <li>例外メッセージ"The class name of PropertySchema is not specified."を返す</li>
     * </ul>
     */
	public void testSetSchema4() {
		try {
			String schema = ":A,java.lang.String,,,\nB,int,,,";
			RecordSchema.getInstance(schema);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
			assertEquals("B,int,,,:The class name of PropertySchema is not specified.", e.getMessage());
		}
	}

	/**
     * レコードスキーマを設定するテスト。<p>
     * 条件：
     * <ul>
     *   <li>プロパティ数は２個</li>
     *   <li>プロパティスキーマ実装クラス指定なし</li>
     *   <li>プロパティスキーマ定義内容（型のみ）</li>
     *   <li>２つ目の定義はプロパティスキーマ実装クラスの区切り文字「:」のみ指定</li>
     *   <li>次の内容で指定する<BR> ":A,java.lang.String,,," <BR> ":,,,,"</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>レコードスキーマが正常に設定できる</li>
     *   <li>例外PropertySchemaDefineExceptionが発生しない</li>
     * </ul>
     */
	public void testSetSchema5() {
		try {
			String schema = ":A,java.lang.String,,,\n:,,,,";
			RecordSchema rsm1 = RecordSchema.getInstance(schema);
			assertEquals(rsm1.getSchema(), schema);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
     * レコードスキーマを設定するテスト。<p>
     * 条件：
     * <ul>
     *   <li>プロパティ数は２個</li>
     *   <li>プロパティスキーマ実装クラス指定あり（実在しないクラス）</li>
     *   <li>プロパティスキーマ定義内容（型のみ）</li>
     *   <li>次の内容で指定する <BR> "DUMMYCLASS:A,java.lang.String,,," <BR> "DUMMYCLASS:B,int,,,"</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>例外PropertySchemaDefineExceptionが発生する</li>
     *   <li>例外メッセージ"The class name of PropertySchema is illegal."を返す</li>
     * </ul>
     */
	public void testSetSchema6() {
		try {
			String schema = "DUMMYCLASS:A,java.lang.String,,,\nDUMMYCLASS:B,,,,";
			RecordSchema.getInstance(schema);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
			assertEquals("DUMMYCLASS:A,java.lang.String,,," +
					":The class name of PropertySchema is illegal.", e.getMessage());
		}
	}

	/**
     * レコードスキーマを設定するテスト。<p>
     * 条件：
     * <ul>
     *   <li>プロパティ数は２個</li>
     *   <li>プロパティスキーマ実装クラス指定なし</li>
     *   <li>プロパティスキーマ定義内容（型のみ）</li>
     *   <li>次の内容で指定する <BR> ":A,java.lang.String,,," <BR> ":B,int,,,"</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>レコードスキーマが正常に設定できる</li>
     *   <li>例外PropertySchemaDefineExceptionが発生しない</li>
     *   <li>プロパティスキーマインスタンスが正常に取得できている</li>
     *   <li>propertySchemaMapに２つのスキーマ情報がセットされている</li>
     * </ul>
     */
	public void testSetSchema7() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,int,,,";
			RecordSchema rsm1 = RecordSchema.getInstance(schema);
			assertEquals(rsm1.getSchema(), schema);
			assertTrue(rsm1.propertySchemaMap.size() == 2);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
     * レコードスキーマを設定するテスト。<p>
     * 条件：
     * <ul>
     *   <li>スキーマが異なる３つのレコードスキーマインスタンスを生成</li>
     *   <li>１つ目 <BR> ":A,java.lang.String,,," <BR> ":B,int,,,"</li>
     *   <li>２つ目 <BR> ":A,java.lang.String,,," <BR> ":C,int,,,"</li>
     *   <li>３つ目 <BR> ":B,java.lang.String,,," <BR> ":C,int,,,"(重複する定義)</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>例外PropertySchemaDefineExceptionが発生しない</li>
     *   <li>２つ目と３つ目でプロパティスキーマのインスタンスが生成されない<BR>
     *ことを確認</li>
     * </ul>
     */
	public void testSetSchema8() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,int,,,";
			RecordSchema.getInstance(schema);
			schema = ":A,java.lang.String,,,\n:C,int,,,";
			RecordSchema.getInstance(schema);
			int scnt1 = RecordSchema.propertySchemaManager.size();
			schema = ":B,int,,,\n:C,int,,,";
			RecordSchema.getInstance(schema);
			int scnt2 = RecordSchema.propertySchemaManager.size();
			assertEquals(scnt1, scnt2);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
     * レコードスキーマを取得するテスト。<p>
     * 条件：
     * <ul>
     *   <li>プロパティ数は２個</li>
     *   <li>プロパティスキーマ実装クラス指定なし</li>
     *   <li>プロパティスキーマ定義内容（型のみ）</li>
     *   <li>次の内容で指定する <BR> ":A,java.lang.String,,," <BR> ":B,int,,,"</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>getSchema()で取得したレコードスキーマが定義した内容と一致している</li>
     * </ul>
     */
	public void testGetSchema() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,int,,,";
			RecordSchema rsm = RecordSchema.getInstance(schema);
			assertEquals(rsm.getSchema(), ":A,java.lang.String,,,\n:B,int,,,");
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
     * プロパティ名を取得するテスト。<p>
     * 条件：
     * <ul>
     *   <li>プロパティ数は２個</li>
     *   <li>プロパティスキーマ実装クラス指定なし</li>
     *   <li>プロパティスキーマ定義内容（型のみ）</li>
     *   <li>次の内容で指定する <BR> ":A,java.lang.String,,," <BR> ":B,int,,,"</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>getPropertyName(0)で取得したプロパティ名が定義した内容と一致している</li>
     *   <li>getPropertyName(1)で取得したプロパティ名が定義した内容と一致している</li>
     * </ul>
     */
	public void testGetPropertyName1() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,int,,,";
			RecordSchema rsm = RecordSchema.getInstance(schema);
			assertEquals(rsm.getPropertyName(0), "A");
			assertEquals(rsm.getPropertyName(1), "B");
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
     * プロパティ名を取得するテスト。<p>
     * 条件：
     * <ul>
     *   <li>プロパティ数は２個</li>
     *   <li>プロパティスキーマ実装クラス指定なし</li>
     *   <li>プロパティスキーマ定義内容（型のみ）</li>
     *   <li>次の内容で指定する <BR> ":A,java.lang.String,,," <BR> ":B,int,,,"</li>
     *   <li>getPropertyName(index)で正しくないインデックスを指定</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>getPropertyName(-1)で取得したプロパティ名がnull</li>
     *   <li>getPropertyName(2)で取得したプロパティ名がnull</li>
     * </ul>
     */
	public void testGetPropertyName2() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,int,,,";
			RecordSchema rsm = RecordSchema.getInstance(schema);
			assertNull(rsm.getPropertyName(-1));
			assertNull(rsm.getPropertyName(2));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
     * 指定したプロパティ名のインデックスを取得するテスト。<p>
     * 条件：
     * <ul>
     *   <li>プロパティ数は２個</li>
     *   <li>プロパティスキーマ実装クラス指定なし</li>
     *   <li>プロパティスキーマ定義内容（型のみ）</li>
     *   <li>次の内容で指定する <BR> ":A,java.lang.String" <BR> ":B,int"</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>getPropertyIndex("A")で取得したインデックスが定義した内容と一致している</li>
     *   <li>getPropertyIndex("B")で取得したインデックスが定義した内容と一致している</li>
     * </ul>
     */
	public void testGetPropertyIndex() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,int,,,";
			RecordSchema rsm = RecordSchema.getInstance(schema);
			assertEquals(rsm.getPropertyIndex("A"), 0);
			assertEquals(rsm.getPropertyIndex("B"), 1);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
     * 指定されたインデックスのプロパティスキーマを取得するテスト。<p>
     * 条件：
     * <ul>
     *   <li>プロパティ数は２個</li>
     *   <li>プロパティスキーマ実装クラス指定なし</li>
     *   <li>プロパティスキーマ定義内容（型のみ）</li>
     *   <li>次の内容で指定する <BR> ":A,java.lang.String,,," <BR> ":B,int,,,"</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>getPropertySchema(0).getSchema()で取得したスキーマが定義した内容と一致している</li>
     *   <li>getPropertySchema(1).getSchema()で取得したスキーマが定義した内容と一致している</li>
     * </ul>
     */
	public void testGetPropertySchemaInt1() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,int,,,";
			RecordSchema rsm = RecordSchema.getInstance(schema);
			assertEquals(rsm.getPropertySchema(0).getSchema(), "A,java.lang.String,,,");
			assertEquals(rsm.getPropertySchema(1).getSchema(), "B,int,,,");
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
     * 指定されたインデックスのプロパティスキーマを取得するテスト。<p>
     * 条件：
     * <ul>
     *   <li>プロパティ数は２個</li>
     *   <li>プロパティスキーマ実装クラス指定なし</li>
     *   <li>プロパティスキーマ定義内容（型のみ）</li>
     *   <li>次の内容で指定する <BR> ":A,java.lang.String,,," <BR> ":B,int,,,"</li>
     *   <li>getPropertySchema(index)で正しくないインデックスを指定</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>getPropertySchema(-1)で取得したスキーマがnull</li>
     *   <li>getPropertySchema(2)で取得したスキーマがnull</li>
     * </ul>
     */
	public void testGetPropertySchemaInt2() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,int,,,";
			RecordSchema rsm = RecordSchema.getInstance(schema);
			assertNull(rsm.getPropertySchema(-1));
			assertNull(rsm.getPropertySchema(2));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
     * 指定したプロパティ名のプロパティスキーマを取得するテスト。<p>
     * 条件：
     * <ul>
     *   <li>プロパティ数は２個</li>
     *   <li>プロパティスキーマ実装クラス指定なし</li>
     *   <li>プロパティスキーマ定義内容（型のみ）</li>
     *   <li>次の内容で指定する <BR> ":A,java.lang.String" <BR> ":B,int"</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>getPropertySchema("A").getSchema()で取得したプロパティスキーマが定義した内容と一致している</li>
     *   <li>getPropertySchema("B").getSchema()で取得したプロパティスキーマが定義した内容と一致している</li>
     * </ul>
     */
	public void testGetPropertySchemaString1() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,int,,,";
			RecordSchema rsm = RecordSchema.getInstance(schema);
			assertEquals(rsm.getPropertySchema("A").getSchema(), "A,java.lang.String,,,");
			assertEquals(rsm.getPropertySchema("B").getSchema(), "B,int,,,");
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
     * 指定したプロパティ名のプロパティスキーマを取得するテスト。<p>
     * 条件：
     * <ul>
     *   <li>プロパティ数は２個</li>
     *   <li>プロパティスキーマ実装クラス指定なし</li>
     *   <li>プロパティスキーマ定義内容（型のみ）</li>
     *   <li>次の内容で指定する <BR> ":A,java.lang.String" <BR> ":B,int"</li>
     *   <li>nullを指定</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>getPropertySchema(null文字列).getSchema()で取得したプロパティスキーマがnull</li>
     * </ul>
     */
	public void testGetPropertySchemaString2() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,int,,,";
			RecordSchema rsm = RecordSchema.getInstance(schema);
			assertNull(rsm.getPropertySchema((String)null));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
     * プロパティスキーマ配列を取得するテスト。<p>
     * 条件：
     * <ul>
     *   <li>プロパティ数は２個</li>
     *   <li>プロパティスキーマ実装クラス指定なし</li>
     *   <li>プロパティスキーマ定義内容（型のみ）</li>
     *   <li>次の内容で指定する <BR> ":A,java.lang.String,,," <BR> ":B,int,,,"</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>getPropertySchemata()で取得したプロパティスキーマ配列の要素数が2個</li>
     *   <li>getPropertySchemata()で取得したプロパティスキーマ配列の各要素から取得した<BR>
     *   スキーマ文字列が定義した内容と一致する</li>
     * </ul>
     */
	public void testGetPropertySchemata() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,int,,,";
			RecordSchema rsm = RecordSchema.getInstance(schema);
			assertTrue(rsm.getPropertySchemata().length == 2);
			assertEquals(rsm.getPropertySchemata()[0].getSchema(), "A,java.lang.String,,,");
			assertEquals(rsm.getPropertySchemata()[1].getSchema(), "B,int,,,");
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
     * プロパティの数を取得するテスト。<p>
     * 条件：
     * <ul>
     *   <li>プロパティ数は２個</li>
     *   <li>プロパティスキーマ実装クラス指定なし</li>
     *   <li>プロパティスキーマ定義内容（型のみ）</li>
     *   <li>次の内容で指定する <BR> ":A,java.lang.String,,," <BR> ":B,int,,,"</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>getPropertySize()で取得したプロパティ数が2個</li>
     * </ul>
     */
	public void testGetPropertySize() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,int,,,";
			RecordSchema rsm = RecordSchema.getInstance(schema);
			assertTrue(rsm.getPropertySize() == 2);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
     * レコードスキーマ文字列を取得するテスト。<p>
     * 条件：
     * <ul>
     *   <li>プロパティ数は２個</li>
     *   <li>プロパティスキーマ実装クラス指定なし</li>
     *   <li>プロパティスキーマ定義内容（型のみ）</li>
     *   <li>次の内容で指定する <BR> ":A,java.lang.String" <BR> ":B,int"</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>toString()で取得した文字列が<BR>
     *   "{name=A,type=java.lang.String,parseConverter=null,<BR>
     *   formatConverter=null,constrain=null};<BR>
     *   jp.ossc.nimbus.beans.dataset.DefaultPropertySchema<BR>
     *   {name=B,type=java.lang.Integer,parseConverter=null,<BR>
     *   formatConverter=null,constrain=null}}"と一致するか</li>
     * </ul>
     */
	public void testToString() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,int,,,";
			RecordSchema rsm = RecordSchema.getInstance(schema);
			assertEquals(rsm.toString(), "{jp.ossc.nimbus.beans.dataset.DefaultPropertySchema" +
					"{name=A,type=java.lang.String,parseConverter=null," +
					"formatConverter=null,constrain=null};" +
					"jp.ossc.nimbus.beans.dataset.DefaultPropertySchema" +
					"{name=B,type=int,parseConverter=null," +
					"formatConverter=null,constrain=null}}");
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

}
