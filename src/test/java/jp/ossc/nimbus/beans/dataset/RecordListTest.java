package jp.ossc.nimbus.beans.dataset;

import java.util.*;

import junit.framework.TestCase;
//
/**
 * 
 * @author S.Teshima
 * @version 1.00 作成: 2008/01/22 - S.Teshima
 */

public class RecordListTest extends TestCase {

	public RecordListTest(String arg0) {
		super(arg0);
	}

    public static void main(String[] args) {
		junit.textui.TestRunner.run(RecordListTest.class);
	}

	/**
	 * 空のレコードリストを生成するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>正常終了。例外PropertySchemaDefineExceptionが発生しないことを確認</li>
     * <li>RecordList#getname()が"TestRecordList"を返す。</li>
     * <li>RecordList#getSchema()が":A,java.lang.String,,,"を返す。</li>
     * <li>RecordList#getRecordSchema()がRecordSchema.getInstance(指定したスキーマ)を返す。</li>
     * <li>RecordList#isEmpty()がtrueを返す。</li>
	 * </ul>
	 */
	public void testRecordList() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,");
	    	assertEquals("TestRecordList", rlist.getName());
	    	assertEquals(":A,java.lang.String,,,", rlist.getSchema());
	    	assertEquals(RecordSchema.getInstance(":A,java.lang.String,,,"), rlist.getRecordSchema());
	    	assertTrue(rlist.isEmpty());
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}

	/**
	 * 空のレコードリストを生成するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : "A,java.lang.String,,," (区切り文字：を指定しない)</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>例外PropertySchemaDefineExceptionが発生することを確認</li>
	 * </ul>
	 */
	public void testRecordListInvalid() {
	    try{
	    	new RecordList("TestRecordList", "A,java.lang.String,,,");
			fail("例外が発生しないためテスト失敗 ");
		} catch (PropertySchemaDefineException e) {
		}
	}

	/**
	 * レコードを追加し、取得するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する</li>
	 * <li>RecordList#addRecord(int index, Record r)でレコードを追加する</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>RecordList#getRecord(int index)で追加したレコードを取得。</li>
	 * </ul>
	 */
	public void testAddRecord() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,");
	    	Record rec1 = rlist.createRecord();
	    	rlist.addRecord(rec1);
	    	Record rec2 = rlist.createRecord();
	    	rlist.addRecord(1,rec2);
	    	assertEquals(rec1, rlist.getRecord(0));
	    	assertEquals(rec2, rlist.getRecord(1));
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}

	/**
	 * 指定されたインデックスのレコードを置き換えるテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する</li>
	 * <li>RecordList#addRecord(int index, Record r)でレコードを追加する</li>
	 * <li>次のスキーマを指定してRecord(String schema)を生成する</li>
	 * <li>":B,int,,," </li>
	 * <li>RecordList#setRecord(int index, Record r)でレコードを置き換える</li>
	 * </ul>
	 * 確認：
	 * <ul>
	 * <li>RecordList#getRecord(int index)でレコードを取得し、置き換えられているか確認</li>
	 * </ul>
	 */
	public void testSetRecord() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,");
	    	Record rec1 = rlist.createRecord();
	    	rlist.addRecord(0,rec1);
	    	Record rec2 = new Record(":B,int,,,");
	    	rlist.setRecord(0, rec2);
	    	assertEquals(rec2, rlist.getRecord(0));
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}


	/**
	 * 指定されたレコードを削除するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#removeRecord(Record r)でレコード指定削除をする</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>RecordList#size()が0</li>
	 * </ul>
	 */
	public void testRemoveRecordRecord() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,");
	    	Record rec = rlist.createRecord();
	    	rlist.addRecord(rec);
	    	rlist.removeRecord(rec);
	    	assertEquals(0, rlist.size());
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}


	/**
	 * 指定されたインデックスのレコードを削除するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する</li>
	 * <li>RecordList#addRecord(int index, Record r)でレコードを追加する</li>
	 * <li>RecordList#removeRecord(int index)でレコード指定削除をする</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>RecordList#size()が0</li>
	 * </ul>
	 */
	public void testRemoveRecordInt() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,");
	    	Record rec = rlist.createRecord();
	    	rlist.addRecord(rec);
	    	rlist.removeRecord(0);
	    	assertEquals(0, rlist.size());
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}


	/**
	 * 指定されたプロパティ名のプロパティをソートキーにして、昇順でソートするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "2")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(２つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "3")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "3")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(３つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#sort(String[] orderBy)でプロパティ名"A"を指定してソートする</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>RecordがプロパティAの値で昇順に格納されていることを確認する</li>
	 * </ul>
	 */
	public void testSortStringArray() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "2");
	    	rec1.setProperty("B", "2");
	    	rlist.addRecord(rec1);

	    	Record rec2 = rlist.createRecord();
	    	rec2.setProperty("A", "3");
	    	rec2.setProperty("B", "3");
	    	rlist.addRecord(rec2);

	    	Record rec3 = rlist.createRecord();
	    	rec3.setProperty("A", "1");
	    	rec3.setProperty("B", "1");
	    	rlist.addRecord(rec3);
	    	
	    	String skey[] = {"A"};
	    	rlist.sort(skey);

	    	assertEquals(rec3, rlist.getRecord(0));
	    	assertEquals(rec1, rlist.getRecord(1));
	    	assertEquals(rec2, rlist.getRecord(2));
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}


	/**
	 * 指定されたプロパティインデックスのプロパティをソートキーにして、昇順でソートするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "2")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(２つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "3")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "3")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(３つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#sort(int[] orderBy)でプロパティ名"A"のindex(0)を指定してソートする</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>RecordがプロパティAの値で昇順に格納されていることを確認する</li>
	 * </ul>
	 */
	public void testSortIntArray() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "2");
	    	rec1.setProperty("B", "2");
	    	rlist.addRecord(rec1);

	    	Record rec2 = rlist.createRecord();
	    	rec2.setProperty("A", "3");
	    	rec2.setProperty("B", "3");
	    	rlist.addRecord(rec2);

	    	Record rec3 = rlist.createRecord();
	    	rec3.setProperty("A", "1");
	    	rec3.setProperty("B", "1");
	    	rlist.addRecord(rec3);
	    	
	    	int skey[] = {0};
	    	rlist.sort(skey);

	    	assertEquals(rec3, rlist.getRecord(0));
	    	assertEquals(rec1, rlist.getRecord(1));
	    	assertEquals(rec2, rlist.getRecord(2));
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}


	/**
	 * 指定されたプロパティインデックスのプロパティをソートキーにしてソートするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(２つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(３つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "2")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(４つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "2")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(５つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "3")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(６つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "3")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#sort(int[] orderBy, boolean[] isAsc)でプロパティ名"A"が降順、<BR>
	 * プロパティ名"B"が昇順になる指定をしてソートする</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>Recordが指定した通りにソートされて格納されていることを確認する</li>
	 * </ul>
	 */
	public void testSortIntArrayBooleanArray() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "1");
	    	rec1.setProperty("B", "1");
	    	rlist.addRecord(rec1);

	    	Record rec2 = rlist.createRecord();
	    	rec2.setProperty("A", "1");
	    	rec2.setProperty("B", "2");
	    	rlist.addRecord(rec2);

	    	Record rec3 = rlist.createRecord();
	    	rec3.setProperty("A", "2");
	    	rec3.setProperty("B", "1");
	    	rlist.addRecord(rec3);
	    	
	    	Record rec4 = rlist.createRecord();
	    	rec4.setProperty("A", "2");
	    	rec4.setProperty("B", "2");
	    	rlist.addRecord(rec4);

	    	Record rec5 = rlist.createRecord();
	    	rec5.setProperty("A", "3");
	    	rec5.setProperty("B", "1");
	    	rlist.addRecord(rec5);

	    	Record rec6 = rlist.createRecord();
	    	rec6.setProperty("A", "3");
	    	rec6.setProperty("B", "2");
	    	rlist.addRecord(rec6);
	    	
	    	int skey[] = {0,1};
	    	boolean asc[] = {false,true};
	    	rlist.sort(skey,asc);

	    	assertEquals(rec5, rlist.getRecord(0));
	    	assertEquals(rec6, rlist.getRecord(1));
	    	assertEquals(rec3, rlist.getRecord(2));
	    	assertEquals(rec4, rlist.getRecord(3));
	    	assertEquals(rec1, rlist.getRecord(4));
	    	assertEquals(rec2, rlist.getRecord(5));
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}


	/**
	 * 指定されたプロパティインデックスのプロパティをソートキーにしてソートするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#sort(int[] orderBy, boolean[] isAsc)でプロパティ名"A"が降順、<BR>
	 * プロパティ名"B"が昇順になる指定をしてソートする</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>正常終了することを確認</li>
	 * </ul>
	 */
	public void testSortIntArrayBooleanArrayOneValue() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "1");
	    	rec1.setProperty("B", "1");
	    	rlist.addRecord(rec1);

	    	
	    	int skey[] = {0,1};
	    	boolean asc[] = {false,true};
	    	rlist.sort(skey,asc);

    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}



	/**
	 * 指定されたプロパティインデックスのプロパティをソートキーにしてソートするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(２つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(３つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "2")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(４つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "2")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(５つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "3")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(６つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "3")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#sort(String[] orderBy, boolean[] isAsc)でプロパティ名"A"が降順、<BR>
	 * プロパティ名"B"が昇順になる指定をしてソートする</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>Recordが指定した通りにソートされて格納されていることを確認する</li>
	 * </ul>
	 */
	public void testSortStringArrayBooleanArray() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "1");
	    	rec1.setProperty("B", "1");
	    	rlist.addRecord(rec1);

	    	Record rec2 = rlist.createRecord();
	    	rec2.setProperty("A", "1");
	    	rec2.setProperty("B", "2");
	    	rlist.addRecord(rec2);

	    	Record rec3 = rlist.createRecord();
	    	rec3.setProperty("A", "2");
	    	rec3.setProperty("B", "1");
	    	rlist.addRecord(rec3);
	    	
	    	Record rec4 = rlist.createRecord();
	    	rec4.setProperty("A", "2");
	    	rec4.setProperty("B", "2");
	    	rlist.addRecord(rec4);

	    	Record rec5 = rlist.createRecord();
	    	rec5.setProperty("A", "3");
	    	rec5.setProperty("B", "1");
	    	rlist.addRecord(rec5);

	    	Record rec6 = rlist.createRecord();
	    	rec6.setProperty("A", "3");
	    	rec6.setProperty("B", "2");
	    	rlist.addRecord(rec6);
	    	
	    	String skey[] = {"A","B"};
	    	boolean asc[] = {false,true};
	    	rlist.sort(skey,asc);

	    	assertEquals(rec5, rlist.getRecord(0));
	    	assertEquals(rec6, rlist.getRecord(1));
	    	assertEquals(rec3, rlist.getRecord(2));
	    	assertEquals(rec4, rlist.getRecord(3));
	    	assertEquals(rec1, rlist.getRecord(4));
	    	assertEquals(rec2, rlist.getRecord(5));
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}

	/**
	 * 指定されたプロパティインデックスのプロパティをソートキーにしてソートするテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#sort(String[] orderBy, boolean[] isAsc)でプロパティ名"A"が降順、<BR>
	 * プロパティ名"B"が昇順になる指定をしてソートする</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>正常終了することを確認</li>
	 * </ul>
	 */
	public void testSortStringArrayBooleanArrayOneValue() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "1");
	    	rec1.setProperty("B", "1");
	    	rlist.addRecord(rec1);
	    	
	    	String skey[] = {"A","B"};
	    	boolean asc[] = {false,true};
	    	rlist.sort(skey,asc);

    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}

	/**
	 * boolean contains(Object o)のテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(２つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "2")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(３つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "3")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "3")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>各レコードを指定してRecordList#contains(Object o)を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>trueが返ってくることを確認</li>
	 * </ul>
	 */
	public void testContains() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "1");
	    	rec1.setProperty("B", "1");
	    	rlist.addRecord(rec1);

	    	Record rec2 = rlist.createRecord();
	    	rec2.setProperty("A", "2");
	    	rec2.setProperty("B", "2");
	    	rlist.addRecord(rec2);

	    	Record rec3 = rlist.createRecord();
	    	rec3.setProperty("A", "3");
	    	rec3.setProperty("B", "3");
	    	rlist.addRecord(rec3);
	    	
	    	assertTrue(rlist.contains(rec1));
	    	assertTrue(rlist.contains(rec2));
	    	assertTrue(rlist.contains(rec3));
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}


	/**
	 * Map実装のテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(２つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "2")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(３つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "3")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "3")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>subList(int fromIndex, int toIndex)の返り値がrecords.subList(fromIndex, toIndex)</li>
     * <li>Object[] toArray()の返り値がrecords.toArray()</li>
     * <li>indexOf(Object o)の返り値がrecords.indexOf(o)</li>
     * <li>lastIndexOf(Object o)の返り値がrecords.lastIndexOf(o)</li>
	 * </ul>
	 */
	public void testMap() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "1");
	    	rec1.setProperty("B", "1");
	    	rlist.addRecord(rec1);

	    	Record rec2 = rlist.createRecord();
	    	rec2.setProperty("A", "2");
	    	rec2.setProperty("B", "2");
	    	rlist.addRecord(rec2);

	    	Record rec3 = rlist.createRecord();
	    	rec3.setProperty("A", "3");
	    	rec3.setProperty("B", "3");
	    	rlist.addRecord(rec3);
	    	
	    	assertEquals(rlist.subList(0, 1).get(0), rlist.records.subList(0, 1).get(0));
	    	assertEquals(rlist.toArray()[0], rlist.records.toArray()[0]);
	    	assertEquals(rlist.indexOf(rec1), rlist.records.indexOf(rec1));
	    	assertEquals(rlist.lastIndexOf(rec1), rlist.records.lastIndexOf(rec1));
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}

	/**
	 * add(Object o)のテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#add(Object o)でレコードを追加する</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>trueが返ってくることを確認する</li>
     * <li>Object get(int index)の返り値がrecords.get(index)と同じことを確認する</li>
	 * </ul>
	 */
	public void testAdd() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "1");
	    	rec1.setProperty("B", "1");
	    	
	    	assertTrue(rlist.add(rec1));
	    	assertEquals(rlist.get(0), rlist.records.get(0));
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}


	/**
	 * add(Object o)のテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#add(null)でレコードを追加する</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>falseが返ってくることを確認する</li>
	 * </ul>
	 */
	public void testAddNull() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	assertFalse(rlist.add(null));
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}


	/**
	 * add(Object o)のテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#add(Record型以外)でレコードを追加する</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>DataSetException("Not record : " + o)が返ってくることを確認する</li>
	 * </ul>
	 */
	public void testAddInvalid() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	assertFalse(rlist.add("ABC"));
			fail("例外が発生しないためテスト失敗 ");
    	}catch(DataSetException e){
			assertEquals("Not record : ABC", e.getMessage());
    	}
	}


	/**
	 * add(int index, Object o)のテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#add(int index,Object o)でレコードを追加する</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>Object get(int index)の返り値がrecords.get(index)と同じことを確認する</li>
	 * </ul>
	 */
	public void testAddIndex() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "1");
	    	rec1.setProperty("B", "1");
	    	
	    	rlist.add(0, rec1);
	    	assertEquals(rlist.get(0), rlist.records.get(0));
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}


	/**
	 * add(int index,Object o)のテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#add(int index, null)でレコードを追加する</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>正常終了することを確認する</li>
	 * </ul>
	 */
	public void testAddIndexNull() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	rlist.add(0,null);
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}


	/**
	 * add(int index, Object o)のテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#add(int index, Record型以外)でレコードを追加する</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>DataSetException("Not record : " + o)が返ってくることを確認する</li>
	 * </ul>
	 */
	public void testAddIndexInvalid() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	rlist.add(0, "ABC");
			fail("例外が発生しないためテスト失敗 ");
    	}catch(DataSetException e){
			assertEquals("Not record : ABC", e.getMessage());
    	}
	}


	/**
	 * boolean containsAll(Collection c)のテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(２つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "2")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(３つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "3")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "3")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>List型オブジェクトに上記Recordオブジェクトを追加し、containsAll(Collection c)<BR>
	 * に指定して実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>trueが返ってくることを確認</li>
	 * </ul>
	 */
	public void testContainsAll() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "1");
	    	rec1.setProperty("B", "1");
	    	rlist.addRecord(rec1);

	    	Record rec2 = rlist.createRecord();
	    	rec2.setProperty("A", "2");
	    	rec2.setProperty("B", "2");
	    	rlist.addRecord(rec2);

	    	Record rec3 = rlist.createRecord();
	    	rec3.setProperty("A", "3");
	    	rec3.setProperty("B", "3");
	    	rlist.addRecord(rec3);
	    	
	    	Collection list = new ArrayList();
	    	list.add(rec1);
	    	list.add(rec2);
	    	list.add(rec3);
	    	
	    	assertTrue(rlist.containsAll(list));
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}


	/**
	 * addAll(Collection c)のテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(２つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "2")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(３つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "3")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "3")を実行する</li>
	 * <li>List型オブジェクトに上記Recordオブジェクトを追加し、containsAll(Collection c)<BR>
	 * に指定して実行</li>
	 * <li>addAll(Collection c)に指定して実行</li>
	 * <li>containsAll(Collection c)に指定して実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>最初のcontainsAllではfalse、それ以外はtrueが返ってくることを確認</li>
	 * </ul>
	 */
	public void testAddAll() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "1");
	    	rec1.setProperty("B", "1");

	    	Record rec2 = rlist.createRecord();
	    	rec2.setProperty("A", "2");
	    	rec2.setProperty("B", "2");

	    	Record rec3 = rlist.createRecord();
	    	rec3.setProperty("A", "3");
	    	rec3.setProperty("B", "3");
	    	
	    	Collection list = new ArrayList();
	    	list.add(rec1);
	    	list.add(rec2);
	    	list.add(rec3);
	    	
	    	assertFalse(rlist.containsAll(list));
	    	assertTrue(rlist.addAll(list));
	    	assertTrue(rlist.containsAll(list));

	    }catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}

	/**
	 * addAll(Collection c)のテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(２つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "2")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(３つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "3")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "3")を実行する</li>
	 * <li>NullのList型オブジェクトをaddAll(Collection c)に指定して実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>falseが返ってくることを確認</li>
	 * </ul>
	 */
	public void testAddAllNull() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "1");
	    	rec1.setProperty("B", "1");

	    	Record rec2 = rlist.createRecord();
	    	rec2.setProperty("A", "2");
	    	rec2.setProperty("B", "2");

	    	Record rec3 = rlist.createRecord();
	    	rec3.setProperty("A", "3");
	    	rec3.setProperty("B", "3");
	    	
	    	Collection list = new ArrayList();
	    	
	    	assertFalse(rlist.addAll(list));

	    }catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}

	/**
	 * addAll(int index, Collection c)のテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(２つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "2")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(３つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "3")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "3")を実行する</li>
	 * <li>List型オブジェクトに上記Recordオブジェクトを追加し、containsAll(Collection c)<BR>
	 * に指定して実行</li>
	 * <li>addAll(int index, Collection c)に指定して実行</li>
	 * <li>containsAll(Collection c)に指定して実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>最初のcontainsAllではfalse、それ以外はtrueが返ってくることを確認</li>
	 * </ul>
	 */
	public void testAddAllIndex() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "1");
	    	rec1.setProperty("B", "1");

	    	Record rec2 = rlist.createRecord();
	    	rec2.setProperty("A", "2");
	    	rec2.setProperty("B", "2");

	    	Record rec3 = rlist.createRecord();
	    	rec3.setProperty("A", "3");
	    	rec3.setProperty("B", "3");
	    	
	    	Collection list = new ArrayList();
	    	list.add(rec1);
	    	list.add(rec2);
	    	list.add(rec3);
	    	
	    	assertFalse(rlist.containsAll(list));
	    	assertTrue(rlist.addAll(0, list));
	    	assertTrue(rlist.containsAll(list));

	    }catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}

	/**
	 * addAll(int index, Collection c)のテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(２つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "2")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(３つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "3")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "3")を実行する</li>
	 * <li>NullのList型オブジェクトをaddAll(int index, Collection c)に指定して実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>falseが返ってくることを確認</li>
	 * </ul>
	 */
	public void testAddAllIndexNull() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "1");
	    	rec1.setProperty("B", "1");

	    	Record rec2 = rlist.createRecord();
	    	rec2.setProperty("A", "2");
	    	rec2.setProperty("B", "2");

	    	Record rec3 = rlist.createRecord();
	    	rec3.setProperty("A", "3");
	    	rec3.setProperty("B", "3");
	    	
	    	Collection list = new ArrayList();
	    	
	    	assertFalse(rlist.addAll(0,list));

	    }catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}

	/**
	 * removeAll(Collection c)のテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(２つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "2")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(３つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "3")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "3")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>List型オブジェクトに上記で作成したRecordオブジェクトを追加し、removeAll(Collection c)<BR>
	 * に指定して実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>removeAll実行前の要素数が3、実行後0になることを確認</li>
	 * </ul>
	 */
	public void testRemoveAll() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    		    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "1");
	    	rec1.setProperty("B", "1");
	    	rlist.addRecord(rec1);

	    	Record rec2 = rlist.createRecord();
	    	rec2.setProperty("A", "2");
	    	rec2.setProperty("B", "2");
	    	rlist.addRecord(rec2);

	    	Record rec3 = rlist.createRecord();
	    	rec3.setProperty("A", "3");
	    	rec3.setProperty("B", "3");
	    	rlist.addRecord(rec3);
	    	
	    	Collection list = new ArrayList();
	    	list.add(rec1);
	    	list.add(rec2);
	    	list.add(rec3);
	    	
	    	assertEquals(3, rlist.size());
	    	rlist.removeAll(list);
	    	assertEquals(0, rlist.size());

	    }catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}


	/**
	 * retainAll(Collection c)のテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(２つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "2")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(３つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "3")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "3")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>List型オブジェクトに上記で作成した３つ目のRecordオブジェクトを追加し、retainAll(Collection c)<BR>
	 * に指定して実行</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>removeAll実行前の要素数が3、実行後1になることを確認</li>
     * <li>残っているレコードがretainAll指定した３つ目のレコードであることを確認</li>
	 * </ul>
	 */
	public void testRetainAll() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    		    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "1");
	    	rec1.setProperty("B", "1");
	    	rlist.addRecord(rec1);

	    	Record rec2 = rlist.createRecord();
	    	rec2.setProperty("A", "2");
	    	rec2.setProperty("B", "2");
	    	rlist.addRecord(rec2);

	    	Record rec3 = rlist.createRecord();
	    	rec3.setProperty("A", "3");
	    	rec3.setProperty("B", "3");
	    	rlist.addRecord(rec3);
	    	
	    	Collection list = new ArrayList();
	    	list.add(rec3);
	    	
	    	assertEquals(3, rlist.size());
	    	rlist.retainAll(list);
	    	assertEquals(1, rlist.size());
	    	assertEquals(rec3, rlist.getRecord(0));

	    }catch(PropertySchemaDefineException e){
    		e.printStackTrace();
			fail("例外発生");
    	}
	}


	
	/**
	 * 全てのレコードを削除するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(２つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "2")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(３つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "3")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "3")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#clear()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>RecordList#isEmpty()がtrueを返す。</li>
	 * </ul>
	 */
	public void testClear() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "1");
	    	rec1.setProperty("B", "1");
	    	rlist.addRecord(rec1);

	    	Record rec2 = rlist.createRecord();
	    	rec2.setProperty("A", "2");
	    	rec2.setProperty("B", "2");
	    	rlist.addRecord(rec2);

	    	Record rec3 = rlist.createRecord();
	    	rec3.setProperty("A", "3");
	    	rec3.setProperty("B", "3");
	    	rlist.addRecord(rec3);
	    	
	    	rlist.clear();

	    	assertTrue(rlist.isEmpty());
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
    	}
	}


	/**
	 * レコードリストを複製するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(２つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "2")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(３つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "3")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "3")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#cloneSchema()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>RecordListのスキーマのみ複製されていることを確認</li>
     * <li>RecordList#isEmpty()がtrueを返す。</li>
	 * </ul>
	 */
	public void testCloneSchema() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "1");
	    	rec1.setProperty("B", "1");
	    	rlist.addRecord(rec1);

	    	Record rec2 = rlist.createRecord();
	    	rec2.setProperty("A", "2");
	    	rec2.setProperty("B", "2");
	    	rlist.addRecord(rec2);

	    	Record rec3 = rlist.createRecord();
	    	rec3.setProperty("A", "3");
	    	rec3.setProperty("B", "3");
	    	rlist.addRecord(rec3);
	    	
	    	RecordList rlist2 = rlist.cloneSchema();

	    	assertEquals(rlist.getSchema(),rlist2.getSchema());
	    	assertTrue(rlist2.isEmpty());
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
    	}
	}


	/**
	 * レコードリストを複製するテスト。
	 * <p>
	 * 条件：
	 * <ul>
	 * <li>次の名前とスキーマを指定してRecordList(String name, String schema)を実行する</li>
	 * <li>name    : "TestRecordList"</li>
	 * <li>schema  : ":A,java.lang.String,,,"<BR>      ":B,java.lang.String,,,"</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(１つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "1")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "1")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(２つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "2")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "2")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#createRecord()で新しいレコードを生成する(３つ目のレコード作成)</li>
	 * <li>パラメータAに値を設定する。Record#setProperty("A", "3")を実行する</li>
	 * <li>パラメータBに値を設定する。Record#setProperty("B", "3")を実行する</li>
	 * <li>RecordList#addRecord(Record r)でレコードを追加する</li>
	 * <li>RecordList#cloneRecordList()を実行する</li>
	 * </ul>
	 * 確認：
	 * <ul>
     * <li>RecordListのスキーマ、レコードが複製されていることを確認</li>
	 * </ul>
	 */
	public void testCloneRecordList() {
	    try{
	    	RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String,,,\n" +
	    			":B,java.lang.String,,,");
	    	
	    	Record rec1 = rlist.createRecord();
	    	rec1.setProperty("A", "1");
	    	rec1.setProperty("B", "1");
	    	rlist.addRecord(rec1);

	    	Record rec2 = rlist.createRecord();
	    	rec2.setProperty("A", "2");
	    	rec2.setProperty("B", "2");
	    	rlist.addRecord(rec2);

	    	Record rec3 = rlist.createRecord();
	    	rec3.setProperty("A", "3");
	    	rec3.setProperty("B", "3");
	    	rlist.addRecord(rec3);
	    	
	    	RecordList rlist2 = rlist.cloneRecordList();

	    	assertEquals(rlist.getSchema(),rlist2.getSchema());
	    	assertEquals(rlist.records.size(),rlist2.records.size());
    	}catch(PropertySchemaDefineException e){
    		e.printStackTrace();
    	}
	}
}
