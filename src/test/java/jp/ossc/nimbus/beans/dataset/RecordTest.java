package jp.ossc.nimbus.beans.dataset;

import junit.framework.TestCase;

import java.util.*;
import java.math.*;

//
/**
 * 
 * @author S.Teshima
 * @version 1.00 作成: 2008/01/17 - S.Teshima
 */

public class RecordTest extends TestCase {
	public RecordTest(String arg0) {
		super(arg0);
	}

	
	 public static void main(String[] args) {
	 junit.textui.TestRunner.run(RecordTest.class); }
	 
	/**
	 * スキーマ文字列指定して、レコードを生成するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * </ul>
	 */

	public void testRecordString1() {
		try {
			String schema = ":A,java.lang.String,,,";
			new Record(schema);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * スキーマ文字列指定して、レコードを生成するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>"A,java.lang.String,,," (区切り文字：を指定しない)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認</li>
	 * </ul>
	 */

	public void testRecordString2() {
		try {
			String schema = "A,java.lang.String,,,";
			new Record(schema);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
		}
	}

	/**
	 * レコードのスキーマ文字列を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>getSchema()を実行して返される文字列が指定したものと同じことを確認</li>
	 * </ul>
	 */
	public void testGetSchema() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			assertEquals(":A,java.lang.String,,,", rec.getSchema());
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのスキーマ文字列を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを使ってしてRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>getRecordSchema()を実行して返されるレコードスキーマが<BR>
	 * RecordSchema.getInstance(指定したスキーマ)と同じことを確認</li>
	 * </ul>
	 */
	public void testGetRecordSchema() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			assertEquals(RecordSchema.getInstance(schema), rec
					.getRecordSchema());
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"a"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>values.get(name)で指定した値が返されることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyStringObject1() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "a");
			assertEquals("a", rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"B"(存在しない名前)</li>
	 * <li>Object:"a"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySetExceptionが発生することを確認</li>
	 * <li>例外メッセージに"No such property : B"が返されることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyStringObject2() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("B", "a");
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
			assertEquals("No such property : B", e.getMessage());
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(int index, Object val)を実行する</li>
	 * <li>index : 0</li>
	 * <li>Object:"a"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntObject1() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, "a");
			assertEquals("a", rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(int index, Object val)を実行する</li>
	 * <li>index : 1 (存在しないインデックス)</li>
	 * <li>Object:"a"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntObject2() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty(1, "a");
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,boolean,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, boolean val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:Boolean値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testSetPropertyStringBoolean1() {
		try {
			String schema = ":A,boolean,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", true);
			assertEquals(new Boolean(true), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,boolean,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, boolean val)を実行する</li>
	 * <li>name :"B"</li>
	 * <li>val:Boolean値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetPropertyStringBoolean2() {
		try {
			String schema = ":A,boolean,,,";
			Record rec = new Record(schema);
			rec.setProperty("B", true);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,boolean,,,"</li>
	 * <li>次の値を指定してsetProperty(int index, boolean val)を実行する</li>
	 * <li>index : 0</li>
	 * <li>val:Boolean値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntBoolean1() {
		try {
			String schema = ":A,boolean,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, true);
			assertEquals(new Boolean(true), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,boolean,,,"</li>
	 * <li>次の値を指定してsetProperty(int index, boolean val)を実行する</li>
	 * <li>index : 1 (存在しないインデックス)</li>
	 * <li>val:Boolean値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntBoolean2() {
		try {
			String schema = ":A,boolean,,,";
			Record rec = new Record(schema);
			rec.setProperty(1, true);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,byte,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, byte val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:byte値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testSetPropertyStringByte1() {
		try {
			String schema = ":A,byte,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (byte) 1);
			assertEquals(new Byte((byte) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,byte,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, byte val)を実行する</li>
	 * <li>name :"B"</li>
	 * <li>val:byte値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetPropertyStringByte2() {
		try {
			String schema = ":A,byte,,,";
			Record rec = new Record(schema);
			rec.setProperty("B", (byte) 1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,byte,,,"</li>
	 * <li>次の値を指定してsetProperty(int index, byte val)を実行する</li>
	 * <li>index : 0</li>
	 * <li>val:Byte値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntByte1() {
		try {
			String schema = ":A,byte,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (byte) 1);
			assertEquals(new Byte((byte) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,byte,,,"</li>
	 * <li>次の値を指定してsetProperty(int index, byte val)を実行する</li>
	 * <li>index : 1 (存在しないインデックス)</li>
	 * <li>val:byte値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntByte2() {
		try {
			String schema = ":A,byte,,,";
			Record rec = new Record(schema);
			rec.setProperty(1, (byte) 1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,char,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, char val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:char値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testSetPropertyStringChar1() {
		try {
			String schema = ":A,char,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", 'a');
			assertEquals(new Character('a'), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,char,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, char val)を実行する</li>
	 * <li>name :"B"（存在しない名前）</li>
	 * <li>val:char値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetPropertyStringChar2() {
		try {
			String schema = ":A,char,,,";
			Record rec = new Record(schema);
			rec.setProperty("B", 'a');
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,char,,,"</li>
	 * <li>次の値を指定してsetProperty(int index, char val)を実行する</li>
	 * <li>index : 0</li>
	 * <li>val:char値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntChar1() {
		try {
			String schema = ":A,char,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, 'a');
			assertEquals(new Character('a'), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,char,,,"</li>
	 * <li>次の値を指定してsetProperty(int index, char val)を実行する</li>
	 * <li>index : 1 (存在しないインデックス)</li>
	 * <li>val:char値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntChar2() {
		try {
			String schema = ":A,char,,,";
			Record rec = new Record(schema);
			rec.setProperty(1, 'a');
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {

		}
	}


	/**
	 * レコードのプロパティに値をセットするテスト。(Byte->Short)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,short,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Byte val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:byte値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でshort値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyByteToShort() {
		try {
			String schema = ":A,short,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (byte) 1);
			assertEquals(new Short((byte) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。(Byte->int)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,int,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Byte val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:byte値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でint値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyByteToInt() {
		try {
			String schema = ":A,int,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (byte) 1);
			assertEquals(new Integer((byte) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。(short->int)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,int,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, short val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:short値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でint値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyShortToInt() {
		try {
			String schema = ":A,int,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (short) 1);
			assertEquals(new Integer((short) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * レコードのプロパティに値をセットするテスト。(Byte->long)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,long,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Byte val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:byte値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でlong値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyByteToLong() {
		try {
			String schema = ":A,long,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (byte) 1);
			assertEquals(new Long((byte) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。(short->long)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,long,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, short val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:short値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でlong値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyShortToLong() {
		try {
			String schema = ":A,long,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (short) 1);
			assertEquals(new Long((short) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * レコードのプロパティに値をセットするテスト。(intt->long)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,long,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, int val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:int値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でlong値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntToLong() {
		try {
			String schema = ":A,long,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (int) 1);
			assertEquals(new Long((int) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}



	/**
	 * レコードのプロパティに値をセットするテスト。(Byte->BigInteger)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.math.BigInteger,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Byte val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:byte値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でBigInteger値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyByteToBigInteger() {
		try {
			String schema = ":A,java.math.BigInteger,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (byte) 1);
			assertEquals(new BigInteger("1"), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。(short->BigInteger)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.math.BigInteger,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, short val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:short値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でBigInteger値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyShortToBigInteger() {
		try {
			String schema = ":A,java.math.BigInteger,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (short) 1);
			assertEquals(new BigInteger("1"), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * レコードのプロパティに値をセットするテスト。(int->BigInteger)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.math.BigInteger,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, int val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:int値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でBigInteger値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntToBigInteger() {
		try {
			String schema = ":A,java.math.BigInteger,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (int) 1);
			assertEquals(new BigInteger("1"), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * レコードのプロパティに値をセットするテスト。(long->BigInteger)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.math.BigInteger,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, long val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:long値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でBigInteger値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyLongToBigInteger() {
		try {
			String schema = ":A,java.math.BigInteger,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (long) 1);
			assertEquals(new BigInteger("1"), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。(BigInteger->BigInteger)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.math.BigInteger,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, BigInteger val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:BigInteger値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でBigInteger値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyBigInteger() {
		try {
			String schema = ":A,java.math.BigInteger,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, new BigInteger("1"));
			assertEquals(new BigInteger("1"), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * レコードのプロパティに値をセットするテスト。(Byte->float)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,float,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Byte val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:byte値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でfloat値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyByteToFloat() {
		try {
			String schema = ":A,float,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (byte) 1);
			assertEquals(new Float((byte)1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。(short->float)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,float,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, short val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:short値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でfloat値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyShortToFloat() {
		try {
			String schema = ":A,float,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (short) 1);
			assertEquals(new Float((short) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * レコードのプロパティに値をセットするテスト。(int->float)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,javafloat,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, int val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:int値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でfloat値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntToFloat() {
		try {
			String schema = ":A,float,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (int) 1);
			assertEquals(new Float((int) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * レコードのプロパティに値をセットするテスト。(long->float)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,float,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, long val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:long値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でfloat値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyLongToFloat() {
		try {
			String schema = ":A,float,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (long) 1);
			assertEquals(new Float((long) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。(Byte->double)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,double,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Byte val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:byte値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でdouble値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyByteToDouble() {
		try {
			String schema = ":A,double,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (byte) 1);
			assertEquals(new Double((byte)1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。(short->double)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,double,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, short val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:short値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でdouble値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyShortToDouble() {
		try {
			String schema = ":A,double,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (short) 1);
			assertEquals(new Double((short) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * レコードのプロパティに値をセットするテスト。(int->double)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,double,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, int val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:int値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でfloat値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntToDouble() {
		try {
			String schema = ":A,double,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (int) 1);
			assertEquals(new Double((int) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * レコードのプロパティに値をセットするテスト。(long->double)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,double,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, long val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:long値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でdouble値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyLongToDouble() {
		try {
			String schema = ":A,double,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (long) 1);
			assertEquals(new Double((long) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * レコードのプロパティに値をセットするテスト。(float->double)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,double,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, float val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:float値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でdouble値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyFloatToDouble() {
		try {
			String schema = ":A,double,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (float) 1);
			assertEquals(new Double((float) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}



	/**
	 * レコードのプロパティに値をセットするテスト。(Byte->BigDecimal)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.math.BigDecimal,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Byte val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:byte値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でBigDecimal値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyByteToBigDecimal() {
		try {
			String schema = ":A,java.math.BigDecimal,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (byte) 1);
			assertEquals(new BigDecimal("1"), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。(short->BigDecimal)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.math.BigDecimal,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, short val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:short値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でBigDecimal値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyShortToBigDecimal() {
		try {
			String schema = ":A,java.math.BigDecimal,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (short) 1);
			assertEquals(new BigDecimal("1"), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * レコードのプロパティに値をセットするテスト。(int->BigDecimal)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.math.BigDecimal,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, int val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:int値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でBigDecimal値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntToBigDecimal() {
		try {
			String schema = ":A,java.math.BigDecimal,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (int) 1);
			assertEquals(new BigDecimal("1"), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * レコードのプロパティに値をセットするテスト。(long->BigDecimal)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.math.BigDecimal,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, long val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:long値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でBigDecimal値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyLongToBigDecimal() {
		try {
			String schema = ":A,java.math.BigDecimal,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (long) 1);
			assertEquals(new BigDecimal("1"), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。(float->BigDecimal)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.math.BigDecimal,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, float val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:float値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でBigDecimal値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyFloatToBigDecimal() {
		try {
			String schema = ":A,java.math.BigDecimal,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (float) 1);
			assertEquals(new BigDecimal("1"), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * レコードのプロパティに値をセットするテスト。(double->BigDecimal)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.math.BigDecimal,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, double val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:double値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でBigDecimal値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyDoubleToBigDecimal() {
		try {
			String schema = ":A,java.math.BigDecimal,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (double) 1);
			assertEquals(new BigDecimal("1"), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}
	
	/**
	 * レコードのプロパティに値をセットするテスト。(BigDecimal->BigDecimal)
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.math.BigDecimal,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, BigInteger val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:BigInteger値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * <li>getProperty()でBigDecimal値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetPropertyBigDecimal() {
		try {
			String schema = ":A,java.math.BigDecimal,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, new BigDecimal("1"));
			assertEquals(new BigDecimal("1"), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}



	
	
	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,short,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, short val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:short値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testSetPropertyStringShort1() {
		try {
			String schema = ":A,short,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (short) 1);
			assertEquals(new Short((short) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,short,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, short val)を実行する</li>
	 * <li>name :"B"（存在しない名前）</li>
	 * <li>val:short値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetPropertyStringShort2() {
		try {
			String schema = ":A,short,,,";
			Record rec = new Record(schema);
			rec.setProperty("B", (short) 1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,short,,,"</li>
	 * <li>次の値を指定してsetProperty(int index, short val)を実行する</li>
	 * <li>index : 0</li>
	 * <li>val:short値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntShort1() {
		try {
			String schema = ":A,short,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (short) 1);
			assertEquals(new Short((short) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,short,,,"</li>
	 * <li>次の値を指定してsetProperty(int index, short val)を実行する</li>
	 * <li>index : 1 (存在しないインデックス)</li>
	 * <li>val:short値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntShort2() {
		try {
			String schema = ":A,short,,,";
			Record rec = new Record(schema);
			rec.setProperty(1, (short) 1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,int,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, int val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:int値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testSetPropertyStringInt1() {
		try {
			String schema = ":A,int,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (int) 1);
			assertEquals(new Integer((int) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,int,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, int val)を実行する</li>
	 * <li>name :"B"（存在しない名前）</li>
	 * <li>val:int値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetPropertyStringInt2() {
		try {
			String schema = ":A,int,,,";
			Record rec = new Record(schema);
			rec.setProperty("B", (int) 1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,int,,,"</li>
	 * <li>次の値を指定してsetProperty(int index, int val)を実行する</li>
	 * <li>index : 0</li>
	 * <li>val:int値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntInt1() {
		try {
			String schema = ":A,int,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (int) 1);
			assertEquals(new Integer((int) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,int,,,"</li>
	 * <li>次の値を指定してsetProperty(int index, int val)を実行する</li>
	 * <li>index : 1 (存在しないインデックス)</li>
	 * <li>val:int値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntInt2() {
		try {
			String schema = ":A,int,,,";
			Record rec = new Record(schema);
			rec.setProperty(1, (int) 1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,long,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, long val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:long値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testSetPropertyStringLong1() {
		try {
			String schema = ":A,long,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (long) 1);
			assertEquals(new Long((long) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,long,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, long val)を実行する</li>
	 * <li>name :"B"（存在しない名前）</li>
	 * <li>val:long値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetPropertyStringLong2() {
		try {
			String schema = ":A,long,,,";
			Record rec = new Record(schema);
			rec.setProperty("B", (long) 1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,long,,,"</li>
	 * <li>次の値を指定してsetProperty(int index, long val)を実行する</li>
	 * <li>index : 0</li>
	 * <li>val:long値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntLong1() {
		try {
			String schema = ":A,long,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (long) 1);
			assertEquals(new Long((long) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,long,,,"</li>
	 * <li>次の値を指定してsetProperty(int index, long val)を実行する</li>
	 * <li>index : 1 (存在しないインデックス)</li>
	 * <li>val:long値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntLong2() {
		try {
			String schema = ":A,long,,,";
			Record rec = new Record(schema);
			rec.setProperty(1, (long) 1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,float,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, float val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:float値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testSetPropertyStringFloat1() {
		try {
			String schema = ":A,float,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (float) 1);
			assertEquals(new Float((float) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,float,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, float val)を実行する</li>
	 * <li>name :"B"（存在しない名前）</li>
	 * <li>val:float値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetPropertyStringFloat2() {
		try {
			String schema = ":A,float,,,";
			Record rec = new Record(schema);
			rec.setProperty("B", (float) 1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,float,,,"</li>
	 * <li>次の値を指定してsetProperty(int index, float val)を実行する</li>
	 * <li>index : 0</li>
	 * <li>val:float値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntFloat1() {
		try {
			String schema = ":A,float,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (float) 1);
			assertEquals(new Float((float) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,float,,,"</li>
	 * <li>次の値を指定してsetProperty(int index, float val)を実行する</li>
	 * <li>index : 1 (存在しないインデックス)</li>
	 * <li>val:float値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntFloat2() {
		try {
			String schema = ":A,float,,,";
			Record rec = new Record(schema);
			rec.setProperty(1, (float) 1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,double,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, double val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:double値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testSetPropertyStringDouble1() {
		try {
			String schema = ":A,double,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (double) 1);
			assertEquals(new Double((double) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,double,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, double val)を実行する</li>
	 * <li>name :"B"（存在しない名前）</li>
	 * <li>val:double値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetPropertyStringDouble2() {
		try {
			String schema = ":A,double,,,";
			Record rec = new Record(schema);
			rec.setProperty("B", (double) 1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,double,,,"</li>
	 * <li>次の値を指定してsetProperty(int index, double val)を実行する</li>
	 * <li>index : 0</li>
	 * <li>val:double値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生しないことを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntDouble1() {
		try {
			String schema = ":A,double,,,";
			Record rec = new Record(schema);
			rec.setProperty(0, (double) 1);
			assertEquals(new Double((double) 1), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードのプロパティに値をセットするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,double,,,"</li>
	 * <li>次の値を指定してsetProperty(int index, float val)を実行する</li>
	 * <li>index : 1 (存在しないインデックス)</li>
	 * <li>val:double値</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetPropertyIntDouble2() {
		try {
			String schema = ":A,double,,,";
			Record rec = new Record(schema);
			rec.setProperty(1, (double) 1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
		}
	}

	/**
	 * プロパティの値を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"a"</li>
	 * <li>次の値を指定してgetProperty(String name)を実行する</li>
	 * <li>name :"A"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>getProperty("A")を実行して、"a"が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetPropertyString1() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "a");
			assertEquals("a", rec.getProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティの値を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>（値指定なし）</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>getProperty("A")を実行して、nullが返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetPropertyString2() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			assertNull(rec.getProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティの値を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"a"</li>
	 * <li>次の値を指定してgetProperty(String name)を実行する</li>
	 * <li>name :"B"(存在しない名前を指定)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertyGetExceptionが発生</li>
	 * <li>例外メッセージ"No such property : B"が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetPropertyString3() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "a");
			rec.getProperty("B");
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
			assertEquals("No such property : B", e.getMessage());
		}
	}

	/**
	 * プロパティの値を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"a"</li>
	 * <li>次の値を指定してgetProperty(int index)を実行する</li>
	 * <li>int :0</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>getProperty(0)を実行して、"a"が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetPropertyInt1() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "a");
			assertEquals("a", rec.getProperty(0));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティの値を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"a"</li>
	 * <li>次の値を指定してgetProperty(int index)を実行する</li>
	 * <li>int :1(存在しないインデックス)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertyGetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testGetPropertyInt2() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "a");
			assertEquals("a", rec.getProperty(1));
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
		}
	}

	/**
	 * 指定された名前のプロパティをbooleanとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,boolean,,,"</li>
	 * <li>次の値を指定してgetBooleanProperty(String name)を実行する</li>
	 * <li>name :A (値なし)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>falseが返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetBooleanPropertyString1() {
		try {
			String schema = ":A,boolean,,,";
			Record rec = new Record(schema);
			assertEquals(false, rec.getBooleanProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティをbooleanとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,boolean,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:true</li>
	 * <li>次の値を指定してgetBooleanProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>trueが返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetBooleanPropertyString2() {
		try {
			String schema = ":A,boolean,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", true);
			assertEquals(true, rec.getBooleanProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前の文字型プロパティをbooleanとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"1"</li>
	 * <li>次の値を指定してgetBooleanProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>trueが返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetBooleanPropertyString3() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "1");
			assertEquals(true, rec.getBooleanProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前の文字型プロパティをbooleanとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:数字以外の値</li>
	 * <li>次の値を指定してgetBooleanProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>Boolean.valueOf((String)ret).booleanValue()が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetBooleanPropertyString4() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "Test");
			assertEquals(Boolean.valueOf("Test").booleanValue(), rec
					.getBooleanProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のint型プロパティをbooleanとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,int,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, int val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:100</li>
	 * <li>次の値を指定してgetBooleanProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>trueが返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetBooleanPropertyString5() {
		try {
			String schema = ":A,int,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", 1);
			assertEquals(true, rec.getBooleanProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティをbooleanとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.util.Date,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:Date型の値 (Boolean値として取得できない型)</li>
	 * <li>次の値を指定してgetBooleanProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertyGetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testGetBooleanPropertyString6() {
		try {
			String schema = ":A,java.util.Date,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", new Date());
			rec.getBooleanProperty("A");
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
		}
	}

	/**
	 * 指定されたインデックスのプロパティをbooleanとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,boolean,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, boolean val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:true</li>
	 * <li>次の値を指定してgetBooleanProperty(int index)を実行する</li>
	 * <li>int :0</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>getProperty(0)を実行して、trueが返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetBooleanPropertyInt1() {
		try {
			String schema = ":A,boolean,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", true);
			assertEquals(true, rec.getBooleanProperty(0));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定されたインデックスのプロパティをbooleanとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,boolean,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, boolean val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:true</li>
	 * <li>次の値を指定してgetBooleanProperty(int index)を実行する</li>
	 * <li>int :1(存在しないインデックス)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertyGetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testGetBooleanPropertyInt2() {
		try {
			String schema = ":A,boolean,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", true);
			rec.getBooleanProperty(1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
		}
	}

	/**
	 * 指定された名前のプロパティをbyteとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,byte,,,"</li>
	 * <li>次の値を指定してgetByteProperty(String name)を実行する</li>
	 * <li>name :A (値なし)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>0が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetBytePropertyString1() {
		try {
			String schema = ":A,byte,,,";
			Record rec = new Record(schema);
			assertEquals((byte) 0, rec.getByteProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティをbyteとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,byte,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:(byte)1</li>
	 * <li>次の値を指定してgetByteProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>指定したByte値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetBytePropertyString2() {
		try {
			String schema = ":A,byte,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (byte) 1);
			assertEquals((byte) 1, rec.getByteProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前の文字型プロパティをbyteとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"1"</li>
	 * <li>次の値を指定してgetByteProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetBytePropertyString3() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "1");
			assertEquals((byte) 1, rec.getByteProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のint型プロパティをbyteとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,int,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, int val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:100</li>
	 * <li>次の値を指定してgetByteProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>100が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetBytePropertyString4() {
		try {
			String schema = ":A,int,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", 100);
			assertEquals((byte) 100, rec.getByteProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のboolean型プロパティをbyteとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,boolean,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, boolean val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:true</li>
	 * <li>次の値を指定してgetByteProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetBytePropertyString5() {
		try {
			String schema = ":A,boolean,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", true);
			assertEquals((byte) 1, rec.getByteProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティをbyteとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.util.Date,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:Date型の値 (Byte値として取得できない型)</li>
	 * <li>次の値を指定してgetByteProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertyGetExceptionが発生することを確認</li>
	 * <li>例外メッセージに"The type is unmatch. value=" + ret"が返されることを確認</li>
	 * </ul>
	 */
	public void testGetBytePropertyString6() {
		try {
			String schema = ":A,java.util.Date,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", new Date());
			rec.getByteProperty("A");
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
		}
	}

	/**
	 * 指定されたインデックスのプロパティをbyteとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,byte,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, byte val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:1</li>
	 * <li>次の値を指定してgetByteProperty(int index)を実行する</li>
	 * <li>int :0</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>getProperty(0)を実行して、1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetBytePropertyInt1() {
		try {
			String schema = ":A,byte,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (byte) 1);
			assertEquals((byte) 1, rec.getByteProperty(0));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定されたインデックスのプロパティをbyteとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,byte,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, byte val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:1</li>
	 * <li>次の値を指定してgetByteProperty(int index)を実行する</li>
	 * <li>int :1(存在しないインデックス)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertyGetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testGetBytePropertyInt2() {
		try {
			String schema = ":A,byte,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (byte) 1);
			rec.getByteProperty(1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
		}
	}

	/**
	 * 指定された名前のプロパティをshortとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,short,,,"</li>
	 * <li>次の値を指定してgetShortProperty(String name)を実行する</li>
	 * <li>name :A (値なし)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>0が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetShortPropertyString1() {
		try {
			String schema = ":A,short,,,";
			Record rec = new Record(schema);
			assertEquals((short) 0, rec.getShortProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティをshortとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,short,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, short val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:(short)1</li>
	 * <li>次の値を指定してgetShortProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>指定したShort値が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetShortPropertyString2() {
		try {
			String schema = ":A,short,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (short) 1);
			assertEquals((short) 1, rec.getShortProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前の文字型プロパティをshortとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"1"</li>
	 * <li>次の値を指定してgetShortProperty(String name)を実行する</li>
	 * <li>int :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetShortPropertyString3() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "1");
			assertEquals((short) 1, rec.getShortProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のintプロパティをshortとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,int,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, int val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:100</li>
	 * <li>次の値を指定してgetShortProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>100が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetShortPropertyString4() {
		try {
			String schema = ":A,int,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", 100);
			assertEquals((short) 100, rec.getShortProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のbooleanプロパティをshortとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,boolean,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, boolean val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:true</li>
	 * <li>次の値を指定してgetShortProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetShortPropertyString5() {
		try {
			String schema = ":A,boolean,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", true);
			assertEquals((short) 1, rec.getShortProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティをshortとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.util.Date,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:Date型の値 (short値として取得できない型)</li>
	 * <li>次の値を指定してgetShortProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertyGetExceptionが発生することを確認</li>
	 * <li>例外メッセージに"The type is unmatch. value=" + ret"が返されることを確認</li>
	 * </ul>
	 */
	public void testGetShortPropertyString6() {
		try {
			String schema = ":A,java.util.Date,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", new Date());
			rec.getShortProperty("A");
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
		}
	}

	/**
	 * 指定されたインデックスのプロパティをshortとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,short,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, short val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:1</li>
	 * <li>次の値を指定してgetShortProperty(int index)を実行する</li>
	 * <li>int :0</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetShortPropertyInt1() {
		try {
			String schema = ":A,short,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (short) 1);
			assertEquals((short) 1, rec.getShortProperty(0));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定されたインデックスのプロパティをshortとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,short,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, short val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:1</li>
	 * <li>次の値を指定してgetShortProperty(int index)を実行する</li>
	 * <li>int :1(存在しないインデックス)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertyGetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testGetShortPropertyInt2() {
		try {
			String schema = ":A,short,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (short) 1);
			rec.getShortProperty(1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
		}
	}

	/**
	 * 指定された名前のプロパティをintとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,int,,,"</li>
	 * <li>次の値を指定してgetIntProperty(String name)を実行する</li>
	 * <li>name :A (値なし)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>0が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetIntPropertyString1() {
		try {
			String schema = ":A,int,,,";
			Record rec = new Record(schema);
			assertEquals((int) 0, rec.getIntProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティをintとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,int,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, int val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:1</li>
	 * <li>次の値を指定してgetIntProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetIntPropertyString2() {
		try {
			String schema = ":A,int,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (int) 1);
			assertEquals((int) 1, rec.getIntProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のstringプロパティをintとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"1"</li>
	 * <li>次の値を指定してgetIntProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetIntPropertyString3() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "1");
			assertEquals((int) 1, rec.getIntProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のshortプロパティをintとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,short,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, short val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:100</li>
	 * <li>次の値を指定してgetIntProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>100が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetIntPropertyString4() {
		try {
			String schema = ":A,short,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (short) 100);
			assertEquals(100, rec.getIntProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のbooleanプロパティをintとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,boolean,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, boolean val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:true</li>
	 * <li>次の値を指定してgetIntProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetIntPropertyString5() {
		try {
			String schema = ":A,boolean,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", true);
			assertEquals(1, rec.getIntProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティをintとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.util.Date,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:Date型の値 (int値として取得できない型)</li>
	 * <li>次の値を指定してgetIntProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertyGetExceptionが発生することを確認</li>
	 * <li>例外メッセージに"The type is unmatch. value=" + ret"が返されることを確認</li>
	 * </ul>
	 */
	public void testGetIntPropertyString6() {
		try {
			String schema = ":A,java.util.Date,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", new Date());
			rec.getIntProperty("A");
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
		}
	}

	/**
	 * 指定されたインデックスのプロパティをintとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,int,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, int val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:1</li>
	 * <li>次の値を指定してgetIntProperty(int index)を実行する</li>
	 * <li>int :0</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetIntPropertyInt1() {
		try {
			String schema = ":A,int,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", 1);
			assertEquals(1, rec.getIntProperty(0));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定されたインデックスのプロパティをintとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,int,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, int val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:1</li>
	 * <li>次の値を指定してgetIntProperty(int index)を実行する</li>
	 * <li>int :1(存在しないインデックス)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertyGetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testGetIntPropertyInt2() {
		try {
			String schema = ":A,int,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", 1);
			rec.getIntProperty(1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
		}
	}

	/**
	 * 指定された名前のプロパティをlongとして取得する。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,long,,,"</li>
	 * <li>次の値を指定してgetLongProperty(String name)を実行する</li>
	 * <li>name :A (値なし)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>0が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetLongPropertyString1() {
		try {
			String schema = ":A,long,,,";
			Record rec = new Record(schema);
			assertEquals((long) 0, rec.getLongProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティをlongとして取得する。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,long,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, long val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:1</li>
	 * <li>次の値を指定してgetLongProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetLongPropertyString2() {
		try {
			String schema = ":A,long,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (long) 1);
			assertEquals((long) 1, rec.getLongProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のstringプロパティをlongとして取得する。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"1"</li>
	 * <li>次の値を指定してgetLongProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetLongPropertyString3() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "1");
			assertEquals((long) 1, rec.getLongProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のintプロパティをlongとして取得する。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,int,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, int val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:100</li>
	 * <li>次の値を指定してgetLongProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>100が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetLongPropertyString4() {
		try {
			String schema = ":A,int,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", 100);
			assertEquals((long) 100, rec.getLongProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のbooleanプロパティをlongとして取得する。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,boolean,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, boolean val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:true</li>
	 * <li>次の値を指定してgetLongProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetLongPropertyString5() {
		try {
			String schema = ":A,boolean,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", true);
			assertEquals((long) 1, rec.getLongProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティをlongとして取得する。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.util.Date,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:Date型の値 (long値として取得できない型)</li>
	 * <li>次の値を指定してgetLongProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertyGetExceptionが発生することを確認</li>
	 * <li>例外メッセージに"The type is unmatch. value=" + ret"が返されることを確認</li>
	 * </ul>
	 */
	public void testGetLongPropertyString6() {
		try {
			String schema = ":A,java.util.Date,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", new Date());
			rec.getLongProperty("A");
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
		}
	}

	/**
	 * 指定されたインデックスのプロパティをlongとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,long,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, long val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:1</li>
	 * <li>次の値を指定してgetLongProperty(int index)を実行する</li>
	 * <li>int :0</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetLongPropertyInt1() {
		try {
			String schema = ":A,long,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (long) 1);
			assertEquals((long) 1, rec.getLongProperty(0));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定されたインデックスのプロパティをlongとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,long,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, long val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:1</li>
	 * <li>次の値を指定してgetLongProperty(int index)を実行する</li>
	 * <li>int :1(存在しないインデックス)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertyGetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testGetLongPropertyInt2() {
		try {
			String schema = ":A,long,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (long) 1);
			rec.getLongProperty(1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
		}
	}

	/**
	 * 指定された名前のプロパティをfloatとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,float,,,"</li>
	 * <li>次の値を指定してgetFloatProperty(String name)を実行する</li>
	 * <li>name :A (値なし)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>0が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetFloatPropertyString1() {
		try {
			String schema = ":A,float,,,";
			Record rec = new Record(schema);
			assertEquals((float) 0, rec.getFloatProperty("A"), (float) 0);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティをfloatとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,float,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, float val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:1</li>
	 * <li>次の値を指定してgetFloatProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetFloatPropertyString2() {
		try {
			String schema = ":A,float,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (float) 1);
			assertEquals((float) 1, rec.getFloatProperty("A"), (float) 0);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のstringプロパティをfloatとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"1"</li>
	 * <li>次の値を指定してgetFloatProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetFloatPropertyString3() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "1");
			assertEquals((float) 1, rec.getFloatProperty("A"), (float) 0);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のintプロパティをfloatとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,int,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, int val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:100</li>
	 * <li>次の値を指定してgetFloatProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>100が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetFloatPropertyString4() {
		try {
			String schema = ":A,int,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", 100);
			assertEquals((float) 100, rec.getFloatProperty("A"), (float) 0);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のbooleanプロパティをfloatとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,boolean,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, boolean val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:true</li>
	 * <li>次の値を指定してgetFloatProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetFloatPropertyString5() {
		try {
			String schema = ":A,boolean,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", true);
			assertEquals((float) 1, rec.getFloatProperty("A"), (float) 0);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティをfloatとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.util.Date,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:Date型の値 (float値として取得できない型)</li>
	 * <li>次の値を指定してgetFloatProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertyGetExceptionが発生することを確認</li>
	 * <li>例外メッセージに"The type is unmatch. value=" + ret"が返されることを確認</li>
	 * </ul>
	 */
	public void testGetFloatPropertyString6() {
		try {
			String schema = ":A,java.util.Date,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", new Date());
			rec.getFloatProperty("A");
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
		}
	}

	/**
	 * 指定されたインデックスのプロパティをfloatとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,float,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, float val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:1</li>
	 * <li>次の値を指定してgetLongProperty(int index)を実行する</li>
	 * <li>int :0</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetFloatPropertyInt1() {
		try {
			String schema = ":A,float,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (float) 1);
			assertEquals((float) 1, rec.getFloatProperty(0), (float) 0);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定されたインデックスのプロパティをfloatとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,long,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, long val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:1</li>
	 * <li>次の値を指定してgetLongProperty(int index)を実行する</li>
	 * <li>int :1(存在しないインデックス)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertyGetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testGetFloatPropertyInt2() {
		try {
			String schema = ":A,float,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (float) 1);
			rec.getFloatProperty(1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
		}
	}

	/**
	 * 指定された名前のプロパティをdoubleとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,double,,,"</li>
	 * <li>次の値を指定してgetDoubleProperty(String name)を実行する</li>
	 * <li>name :A (値なし)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>0が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetDoublePropertyString1() {
		try {
			String schema = ":A,double,,,";
			Record rec = new Record(schema);
			assertEquals((double) 0, rec.getDoubleProperty("A"), (double) 0);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティをdoubleとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,double,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, double val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:1</li>
	 * <li>次の値を指定してgetDoubleProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetDoublePropertyString2() {
		try {
			String schema = ":A,double,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (double) 1);
			assertEquals((double) 1, rec.getDoubleProperty("A"), (double) 0);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のstringプロパティをdoubleとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"1"</li>
	 * <li>次の値を指定してgetDoubleProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetDoublePropertyString3() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "1");
			assertEquals((double) 1, rec.getDoubleProperty("A"), (double) 0);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のintプロパティをdoubleとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,int,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, int val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:100</li>
	 * <li>次の値を指定してgetDoubleProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>100が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetDoublePropertyString4() {
		try {
			String schema = ":A,int,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", 100);
			assertEquals((double) 100, rec.getDoubleProperty("A"), (double) 0);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のbooleanプロパティをdoubleとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,boolean,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, boolean val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:true</li>
	 * <li>次の値を指定してgetDoubleProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetDoublePropertyString5() {
		try {
			String schema = ":A,boolean,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", true);
			assertEquals((double) 1, rec.getDoubleProperty("A"), (double) 0);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティをdoubleとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.util.Date,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:Date型の値 (float値として取得できない型)</li>
	 * <li>次の値を指定してgetDoubleProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertyGetExceptionが発生することを確認</li>
	 * <li>例外メッセージに"The type is unmatch. value=" + ret"が返されることを確認</li>
	 * </ul>
	 */
	public void testGetDoublePropertyString6() {
		try {
			String schema = ":A,java.util.Date,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", new Date());
			rec.getDoubleProperty("A");
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
		}
	}

	/**
	 * 指定された名前のstringプロパティをdoubleとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"aaa"（数値変換できない文字）</li>
	 * <li>次の値を指定してgetDoubleProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertyGetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testGetDoublePropertyString7() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "0.0.0.0");
			rec.getDoubleProperty("A");
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
		}
	}


	/**
	 * 指定されたインデックスのプロパティをdoubleとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,double,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, float val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:1</li>
	 * <li>次の値を指定してgetDoubleProperty(int index)を実行する</li>
	 * <li>int :0</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>1が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetDoublePropertyInt1() {
		try {
			String schema = ":A,float,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (float) 1);
			assertEquals((float) 1, rec.getDoubleProperty(0), (float) 0);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定されたインデックスのプロパティをdoubleとして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,double,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, long val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:1</li>
	 * <li>次の値を指定してgetDoubleProperty(int index)を実行する</li>
	 * <li>int :1(存在しないインデックス)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertyGetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testGetDoublePropertyInt2() {
		try {
			String schema = ":A,float,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", (float) 1);
			rec.getDoubleProperty(1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
		}
	}

	/**
	 * 指定された名前のプロパティを文字列として取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してgetStringProperty(String name)を実行する</li>
	 * <li>name :A (値なし)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>nullが返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetStringPropertyString1() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			assertNull(rec.getStringProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティを文字列として取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, String val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:"B"</li>
	 * <li>次の値を指定してgetStringProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>"B"が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetStringPropertyString2() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "B");
			assertEquals("B", rec.getStringProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のintプロパティを文字列として取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,int,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, String val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:1</li>
	 * <li>次の値を指定してgetStringProperty(String name)を実行する</li>
	 * <li>name :A</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>"1"が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetStringPropertyString3() {
		try {
			String schema = ":A,int,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", 1);
			assertEquals("1", rec.getStringProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定されたインデックスのプロパティを文字列として取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, String val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:"B"</li>
	 * <li>次の値を指定してgetStringProperty(int index)を実行する</li>
	 * <li>int :0</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>"B"が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetStringPropertyInt1() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "B");
			assertEquals("B", rec.getStringProperty(0));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定されたインデックスのプロパティを文字列として取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, String val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>value:"B"</li>
	 * <li>次の値を指定してgetProperty(int index)を実行する</li>
	 * <li>int :1(存在しないインデックス)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertyGetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testGetStringPropertyInt2() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "B");
			rec.getStringProperty(1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
		}
	}

	/**
	 * 指定された名前のプロパティをフォーマットして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.util.Date,,"jp.ossc.nimbus.util.converter.DateFormatConverter<BR>
	 * {ConvertType=1;Format="yyyy-MM-DD"}","</li>
	 * <li>次の値を指定してsetProperty(String name, String val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Date型の値 </li>
	 * <li>次の値を指定してgetFormatProperty(String name)を実行する</li>
	 * <li>name :A </li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>正しく日付フォーマット(yyyy-MM-DD)されて返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetFormatPropertyString1() {
		try {
			String schema = ":A,java.util.Date,,"
					+ "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\",";
			Record rec = new Record(schema);

			Calendar cal = Calendar.getInstance();
			cal.set(2008, 0, 22);
			Date date = cal.getTime();

			rec.setProperty("A", date);
			assertEquals("2008-01-22", rec.getFormatProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティをフォーマットして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.util.Date,,"jp.ossc.nimbus.util.converter.DateFormatConverter<BR>
	 * {ConvertType=1;Format="yyyy-MM-DD"}","</li>
	 * <li>次の値を指定してsetProperty(String name, String val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Date型の値 </li>
	 * <li>val:Date型の値 </li>
	 * <li>次の値を指定してgetFormatProperty(String name)を実行する</li>
	 * <li>name :B （存在しないプロパティ） </li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertyGetExceptionが発生することを確認</li>
	 * <li>メッセージ"No such property : B"が返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetFormatPropertyString2() {
		try {
			String schema = ":A,java.util.Date,,"
					+ "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\",";
			Record rec = new Record(schema);

			Calendar cal = Calendar.getInstance();
			cal.set(2008, 0, 22);
			Date date = cal.getTime();

			rec.setProperty("A", date);
			rec.getFormatProperty("B");
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
			assertEquals("No such property : B", e.getMessage());
		}
	}

	/**
	 * 指定されたインデックスのプロパティをフォーマットして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.util.Date,,"jp.ossc.nimbus.util.converter.DateFormatConverter<BR>
	 * {ConvertType=1;Format="yyyy-MM-DD"}","</li>
	 * <li>次の値を指定してsetProperty(String name, String val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Date型の値 </li>
	 * <li>次の値を指定してgetFormatProperty(int index)を実行する</li>
	 * <li>index :0 </li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>正しく日付フォーマット(yyyy-MM-DD)されて返ってくることを確認</li>
	 * </ul>
	 */
	public void testGetFormatPropertyInt1() {
		try {
			String schema = ":A,java.util.Date,,"
					+ "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\",";
			Record rec = new Record(schema);

			Calendar cal = Calendar.getInstance();
			cal.set(2008, 0, 22);
			Date date = cal.getTime();

			rec.setProperty("A", date);
			assertEquals("2008-01-22", rec.getFormatProperty(0));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティをフォーマットして取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.util.Date,,"jp.ossc.nimbus.util.converter.DateFormatConverter<BR>
	 * {ConvertType=1;Format="yyyy-MM-DD"}","</li>
	 * <li>次の値を指定してsetProperty(String name, String val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Date型の値 </li>
	 * <li>次の値を指定してgetFormatProperty(int index)を実行する</li>
	 * <li>index :1 (不正なインデックス) </li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertyGetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testGetFormatPropertyInt2() {
		try {
			String schema = ":A,java.util.Date,,"
					+ "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\",";
			Record rec = new Record(schema);

			Calendar cal = Calendar.getInstance();
			cal.set(2008, 0, 22);
			Date date = cal.getTime();

			rec.setProperty("A", date);
			rec.getFormatProperty(1);
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertyGetException e) {
		}
	}

	/**
	 * 指定された名前のプロパティに、指定された値をパースして設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.util.Date,<BR>
	 * "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format="yyyy-MM-DD"}",,"</li>
	 * <li>次の値を指定してsetParseProperty(String name, Object val)を実行する</li>
	 * <li>name:"A" </li>
	 * <li>val: "yyyy-MM-DD"のフォーマットの日付を表した文字列</li>
	 * <li>次の値を指定してgetProperty(String name)を実行する</li>
	 * <li>name :A </li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>指定した日付のDate型オブジェクトが返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetParsePropertyStringObject1() {
		try {
			String schema = ":A,java.util.Date,"
					+ "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\",,";
			Record rec = new Record(schema);
			rec.setParseProperty("A", "2008-01-22");

			Calendar cal = Calendar.getInstance();
			cal.set(2008, 0, 22);
			Date date = cal.getTime();

			String d1 = java.text.DateFormat.getDateInstance().format(date);
			String d2 = java.text.DateFormat.getDateInstance().format(
					(Date) rec.getProperty("A"));
			assertEquals(d1, d2);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティに、指定された値をパースして設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.util.Date,<BR>
	 * "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format="yyyy-MM-DD"}",,"</li>
	 * <li>次の値を指定してsetParseProperty(String name, Object val)を実行する</li>
	 * <li>name:"B" (存在しないプロパティ)</li>
	 * <li>val: "yyyy-MM-DD"のフォーマットの日付を表した文字列</li>
	 * <li>次の値を指定してgetFormatProperty(String name)を実行する</li>
	 * <li>name :B （存在しないプロパティ） </li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySetExceptionが発生することを確認</li>
	 * <li>メッセージ"No such property : B"が返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetParsePropertyStringObject2() {
		try {
			String schema = ":A,java.util.Date,"
					+ "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\",,";
			Record rec = new Record(schema);
			rec.setParseProperty("B", "2008-01-22");
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
			assertEquals("No such property : B", e.getMessage());
		}
	}

	/**
	 * 指定された名前のプロパティに、指定された値をパースして設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.util.Date,<BR>
	 * "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format="yyyy-MM-DD"}",,"</li>
	 * <li>次の値を指定してsetParseProperty(int index, Object val)を実行する</li>
	 * <li>index:0 </li>
	 * <li>val: "yyyy-MM-DD"のフォーマットの日付を表した文字列</li>
	 * <li>次の値を指定してgetProperty(int index)を実行する</li>
	 * <li>int :0</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
	 * <li>指定した日付のDate型オブジェクトが返ってくることを確認</li>
	 * </ul>
	 */
	public void testSetParsePropertyIntObject1() {
		try {
			String schema = ":A,java.util.Date,"
					+ "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\",,";
			Record rec = new Record(schema);
			rec.setParseProperty("A", "2008-01-22");

			Calendar cal = Calendar.getInstance();
			cal.set(2008, 0, 22);
			Date date = cal.getTime();

			String d1 = java.text.DateFormat.getDateInstance().format(date);
			String d2 = java.text.SimpleDateFormat.getDateInstance().format(
					(Date) rec.getProperty(0));
			assertEquals(d1, d2);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 指定された名前のプロパティに、指定された値をパースして設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.util.Date,<BR>
	 * ,"jp.ossc.nimbus.util.converter.DateFormatConverter,{ConvertType=2;Format="yyyy-MM-DD"}","</li>
	 * <li>次の値を指定してsetParseProperty(int index, Object val)を実行する</li>
	 * <li>int :1(存在しないインデックス)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySetExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testSetParsePropertyIntObject2() {
		try {
			String schema = ":A,java.util.Date,"
					+ "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\",,";
			Record rec = new Record(schema);
			rec.setParseProperty(1, "2008-01-22");
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySetException e) {
		}
	}

	/**
	 * 全てのプロパティをクリアするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"a"</li>
	 * <li>clear()実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。getProperty("A")がnullであることを確認</li>
	 * </ul>
	 */
	public void testClear() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "a");
			rec.clear();
			assertTrue(rec.getProperty("A") == null);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * 同じスキーマを持ちデータを持たない空のレコードを複製するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>cloneSchema()を実行してレコードの複製を生成する</li>
	 * <li>双方のレコードに対してgetSchema()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>元レコードと複製レコードのスキーマ情報が等しいことを確認</li>
	 * </ul>
	 */
	public void testCloneSchema() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			Record rec2 =rec.cloneSchema();
			assertEquals(rec.getSchema(), rec2.getSchema());
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードを複製するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"a"</li>
	 * <li>cloneRecord()を実行してレコードの複製を生成する</li>
	 * <li>双方のレコードに対して、次の値を指定してgetProperty(String name)を実行する</li>
	 * <li>name :"A"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>元レコードと複製レコードで取得した値が等しいことを確認</li>
	 * </ul>
	 */
	public void testCloneRecord() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "a");
			Record rec2 =rec.cloneRecord();
			assertEquals(rec.getProperty("A"), rec2.getProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードを文字列表現するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:"a"</li>
	 * <li>toString()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正しく文字列表現されていることを確認</li>
	 * </ul>
	 */
	public void testToString1() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "a");
			assertEquals("{A=a}", rec.toString());
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードを文字列表現するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>値を設定せずにtoString()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>返り文字列が"{}"であることを確認</li>
	 * </ul>
	 */
	public void testToString2() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			assertEquals("{}", rec.toString());
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードサイズ(値の個数)を計算するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:"a"</li>
	 * <li>size()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>返り値が１であることを確認</li>
	 * </ul>
	 */
	public void testSize() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "a");
			assertEquals(1, rec.size());
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * レコードが空かどうかをチェックするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>isEmpty()を実行する</li>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>""(空文字)</li>
	 * <li>isEmpty()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>1回目の返り値がfalse、2回目の返り値がtrueであることを確認する</li>
	 * </ul>
	 */
	public void testIsEmpty() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec1 = new Record(schema);
			assertFalse(rec1.isEmpty());
			schema = "";
			Record rec2 = new Record(schema);
			assertTrue(rec2.isEmpty());
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティ値を格納するmapにkeyが存在するかをチェックするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>containsKey(Object key)を実行する</li>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>""(空文字)</li>
	 * <li>containsKey(Object key)を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>1回目の返り値がtrue、2回目の返り値がfalseであることを確認する</li>
	 * </ul>
	 */
	public void testContainsKey() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec1 = new Record(schema);
			assertTrue(rec1.containsKey("A"));
			schema = "";
			Record rec2 = new Record(schema);
			assertFalse(rec2.containsKey("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティ値を格納するmapにvalueが存在するかをチェックするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>値を設定しないでcontainsValue(Object value)を実行する</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"a"</li>
	 * <li>containsValue(Object value)を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>1回目の返り値がfalse、2回目の返り値がtrueであることを確認する</li>
	 * </ul>
	 */
	public void testContainsValue() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			assertFalse(rec.containsValue("a"));
			rec.setProperty("A", "a");
			assertTrue(rec.containsValue("a"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティ値を取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:"a"</li>
	 * <li>get("A")を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>返り値がgetProperty("A")と等しいことを確認する</li>
	 * </ul>
	 */
	public void testGet() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "a");
			assertEquals(rec.getProperty("A"), rec.get("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティ値を設定するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してput(Object key, Object value)を実行する（１回目）</li>
	 * <li>name :"A"</li>
	 * <li>val  :"a"</li>
	 * <li>次の値を指定してput(Object key, Object value)を実行する（２回目）</li>
	 * <li>name :"A"</li>
	 * <li>val  :"b"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>1回目の返り値がnull、2回目の返り値が"a"(変更前の値)であることを確認する</li>
	 * <li>getProperty("A")の返り値が"b"であることを確認する</li>
	 * </ul>
	 */
	public void testPut() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			assertNull(rec.put("A", "a"));
			assertEquals("a", rec.put("A","b"));
			assertEquals("b", rec.getProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティ値を削除するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:"a"</li>
	 * <li>次の値を指定してremove(Object key)を実行する</li>
	 * <li>key :"A"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>getProperty("A")を実行して値が削除されている（nullである）ことを確認</li>
	 * </ul>
	 */
	public void testRemove() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "a");
			rec.remove("A");
			assertNull(rec.getProperty("A"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * プロパティ値を削除するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>val:"a"</li>
	 * <li>次の値を指定してremove(Object key)を実行する</li>
	 * <li>key :"B"（存在しない名前）</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>nullが返されることを確認</li>
	 * </ul>
	 */
	public void testRemoveInvallid() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "a");
			assertNull(rec.remove("B"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * void putAll(Map t)のテスト
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,\n:B,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>次の２組のキーと値がセットされているmapを指定してputAll(Map t)を実行する</li>
	 * <li>キー ："A" 値："a"</li>
	 * <li>キー ："B" 値："b"</li>
	 * <li>次の値を指定してgetProperty(String name)を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>getProperty("A")の返り値が"a"</li>
	 * <li>getProperty("B")の返り値が"b"</li>
	 * </ul>
	 */
	public void testPutAll() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,java.lang.String,,,";
			Record rec = new Record(schema);
			
			Map t = new HashMap();
			t.put("A", "a");
			t.put("B", "b");
			rec.putAll(t);
			
			assertEquals("a", rec.getProperty("A"));
			assertEquals("b", rec.getProperty("B"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}
	/**
	 * void putAll(Map t)のテスト
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,\n:B,java.lang.String,,,"</li>
	 * <li>次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>nullを指定してputAll(Map t)を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了することを確認</li>
	 * </ul>
	 */
	public void testPutAllNull() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,java.lang.String,,,";
			Record rec = new Record(schema);
			
			rec.putAll(null);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * Set keySet()のテスト
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,\n:B,java.lang.String,,,"</li>
	 * <li>keySet()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>返り値の要素に設定したプロパティ名("A","B")がセットされている</li>
	 * </ul>
	 */
	public void testKetSet() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,java.lang.String,,,";
			Record rec = new Record(schema);
			
			Set s = rec.keySet();
			assertEquals("A", s.toArray()[0]);
			assertEquals("B", s.toArray()[1]);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}
	

	/**
	 * Collection values()のテスト
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,\n:B,java.lang.String,,,"</li>
	 * <li>次の２組の名前と値を指定してsetPropertyを実行する</li>
	 * <li>名前 ："A" 値："a"</li>
	 * <li>名前 ："B" 値："b"</li>
	 * <li>values()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>返り値の要素に設定したプロパティ値（"a","b"）がセットされている</li>
	 * </ul>
	 */
	public void testValues() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,java.lang.String,,,";
			Record rec = new Record(schema);
			rec.setProperty("A", "a");
			rec.setProperty("B", "b");
			
			Collection v = rec.values();
			assertEquals("a", v.toArray()[0]);
			assertEquals("b", v.toArray()[1]);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * Set entrySet()のテスト
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,\n:B,java.lang.String,,,"</li>
	 * <li>entrySet()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>返り値の要素に設定したプロパティ名("A","B")がセットされている</li>
	 * </ul>
	 */
	public void testEntrySet() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,java.lang.String,,,";
			Record rec = new Record(schema);
			
			Set e = rec.keySet();
			assertEquals("A", e.toArray()[0]);
			assertEquals("B", e.toArray()[1]);
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * boolean equals(Object o)のテスト
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,\n:B,java.lang.String,,,"</li>
	 * <li>nullを指定してequals()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>falseが返ってくる</li>
	 * </ul>
	 */
	public void testEquals1() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,java.lang.String,,,";
			Record rec = new Record(schema);
			
			assertFalse(rec.equals(null));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * boolean equals(Object o)のテスト
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,\n:B,java.lang.String,,,"</li>
	 * <li>自分自身を指定してequals()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>trueが返ってくる</li>
	 * </ul>
	 */
	public void testEquals2() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,java.lang.String,,,";
			Record rec = new Record(schema);
			
			assertTrue(rec.equals(rec));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * boolean equals(Object o)のテスト
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,\n:B,java.lang.String,,,"</li>
	 * <li>Record型でないオブジェクト指定してequals()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>falseが返ってくる</li>
	 * </ul>
	 */
	public void testEquals3() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,java.lang.String,,,";
			Record rec = new Record(schema);
			
			assertFalse(rec.equals("aaa"));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * boolean equals(Object o)のテスト
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,\n:B,java.lang.String,,,"</li>
	 * <li次のスキーマを指定したRecordオブジェクトを指定してequals()を実行する</li>
	 * <li>":C,java.lang.String,,,\n:D,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>falseが返ってくる</li>
	 * </ul>
	 */
	public void testEquals4() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,java.lang.String,,,";
			Record rec1 = new Record(schema);
			schema = ":C,java.lang.String,,,\n:D,java.lang.String,,,";
			Record rec2 = new Record(schema);
			
			assertFalse(rec1.equals(rec2));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * boolean equals(Object o)のテスト
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,\n:B,java.lang.String,,,"</li>
	 * <li>次の名前と値を指定してsetPropertyを実行する</li>
	 * <li>名前 ："A" 値："a"</li>
	 * <li次のスキーマを指定したRecordオブジェクトを指定してequals()を実行する</li>
	 * <li>":A,java.lang.String,,,\n:B,java.lang.String,,,"</li>
	 * <li>次の名前と値を指定してsetPropertyを実行する</li>
	 * <li>名前 ："A" 値："a"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>スキーマ、値が同じなのでtrueが返ってくる</li>
	 * </ul>
	 */
	public void testEquals5() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,java.lang.String,,,";
			Record rec1 = new Record(schema);
			rec1.setProperty("A", "a");
			schema = ":A,java.lang.String,,,\n:B,java.lang.String,,,";
			Record rec2 = new Record(schema);
			rec2.setProperty("A", "a");
			
			assertTrue(rec1.equals(rec2));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	/**
	 * boolean equals(Object o)のテスト
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,\n:B,java.lang.String,,,"</li>
	 * <li>値未設定</li>
	 * <li次のスキーマを指定したRecordオブジェクトを指定してequals()を実行する</li>
	 * <li>":A,java.lang.String,,,\n:B,java.lang.String,,,"</li>
	 * <li>次の名前と値を指定してsetPropertyを実行する</li>
	 * <li>名前 ："A" 値："a"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>値が異なるのでfalseが返ってくる</li>
	 * </ul>
	 */
	public void testEquals6() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,java.lang.String,,,";
			Record rec1 = new Record(schema);
			schema = ":A,java.lang.String,,,\n:B,java.lang.String,,,";
			Record rec2 = new Record(schema);
			rec2.setProperty("A", "a");
			
			assertFalse(rec1.equals(rec2));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * boolean equals(Object o)のテスト
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,\n:B,java.lang.String,,,"</li>
	 * <li>次の名前と値を指定してsetPropertyを実行する</li>
	 * <li>名前 ："A" 値："a"</li>
	 * <li次のスキーマを指定したRecordオブジェクトを指定してequals()を実行する</li>
	 * <li>":A,java.lang.String,,,\n:B,java.lang.String,,,"</li>
	 * <li>値未設定</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>値が異なるのでfalseが返ってくる</li>
	 * </ul>
	 */
	public void testEquals7() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,java.lang.String,,,";
			Record rec1 = new Record(schema);
			rec1.setProperty("A", "a");
			schema = ":A,java.lang.String,,,\n:B,java.lang.String,,,";
			Record rec2 = new Record(schema);
			
			assertFalse(rec1.equals(rec2));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}


	/**
	 * int hashCode()のテスト
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を実行する</li>
	 * <li>":A,java.lang.String,,,\n:B,java.lang.String,,,"</li>
	 * <li>次の名前と値を指定してsetPropertyを実行する</li>
	 * <li>名前 ："A" 値："a"</li>
	 * <li>hashCode()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>返り値がrecordSchema.hashCode()+values.hashCode()</li>
	 * </ul>
	 */
	public void testHashCode1() {
		try {
			String schema = ":A,java.lang.String,,,\n:B,java.lang.String,,,";
			Record rec1 = new Record(schema);
			rec1.setProperty("A", "a");
			Record rec2 = new Record(schema);
			rec2.setProperty("A", "a");
			
			assertEquals(rec1.hashCode(), rec2.hashCode());
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

	
	
	/**
	 * レコードオブジェクトを比較するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次のスキーマを指定してRecord(String schema)を2回実行</li>
	 * <li>":A,java.lang.String,,,"</li>
	 * <li>双方のRecordに対し、次の値を指定してsetProperty(String name, Object val)を実行する</li>
	 * <li>name :"A"</li>
	 * <li>Object:"B"</li>
	 * <li>equalsメソッドを実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>双方のRecordが等しい(equalsメソッドがtrue)ことを確認</li>
	 * </ul>
	 */
	public void testEqualsObject() {
		try {
			String schema = ":A,java.lang.String,,,";
			Record rec1 = new Record(schema);
			Record rec2 = new Record(schema);
			rec1.setProperty("A", "a");
			rec2.setProperty("A", "a");
			assertTrue(rec1.equals(rec2));
		} catch (PropertySchemaDefineException e) {
			e.printStackTrace();
			fail("例外発生");
		}
	}

}
