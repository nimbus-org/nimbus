package jp.ossc.nimbus.beans.dataset;

import junit.framework.TestCase;

public class RecordListPropertySchemaTest extends TestCase {

    public RecordListPropertySchemaTest(String arg0) {
        super(arg0);
    }
     public static void main(String[] args) {
         junit.textui.TestRunner.run(RecordListPropertySchemaTest.class); }
         

        /**
         * スキーマを設定するテスト。
         * <p>
         * 条件：
         * <ul>
         * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
         * <li>"A,recListName"</li>
         * </ul>
         * 確認：
         * <ul>
         * <li>スキーマが正常に設定できる</li>
         * <li>例外PropertySchemaDefineExceptionが発生しないことを確認</li>
         * <li>設定したスキーマがgetSchema()で参照できる</li>
         * <li>nameフィールドにプロパティ名、recordListNameにレコードリスト名が設定される</li>
         * </ul>
         */
    public void testSetGetSchema() {
        try {
            RecordListPropertySchema rps = new RecordListPropertySchema();
            String schema = "A,recListName";
            rps.setSchema(schema);
            assertEquals(schema, rps.getSchema());
            assertEquals("A", rps.name);
            assertEquals("recListName", rps.recordListName);
            
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
     * <li>正しい形式(プロパティ名、レコードリスト名)でないのスキーマを指定してsetSchema(String schema)を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>例外PropertySchemaDefineExceptionが発生するとを確認<BR>
     * メッセージ"Name and Schema must be specified."が返ってくることを確認</li>
     * </ul>
     */
    public void testSetGetSchemaInvalid() {
        try {
            RecordListPropertySchema rps = new RecordListPropertySchema();
            String schema = "A,recListName,test";
            rps.setSchema(schema);
            fail("例外が発生しないのでテスト失敗");
            
            assertEquals(schema, rps.getSchema());
        } catch (PropertySchemaDefineException e) {
            assertEquals("A,recListName,test:The type is illegal.", e.getMessage());
        }
        try {
            RecordListPropertySchema rps = new RecordListPropertySchema();
            String schema = "A";
            rps.setSchema(schema);
            fail("例外が発生しないのでテスト失敗");
            
            assertEquals(schema, rps.getSchema());
        } catch (PropertySchemaDefineException e) {
            assertEquals("Name and Schema must be specified.:null", e.getMessage());
        }
    }


    /**
     * プロパティ名を取得するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
     * <li>"A,recListName"</li>
     * <li> getName()を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>スキーマで指定したプロパティ名が取得できる</li>
     * </ul>
     */
    public void testGetName() {
        try {
            RecordListPropertySchema rps = new RecordListPropertySchema();
            String schema = "A,recListName";
            rps.setSchema(schema);
            assertEquals("A", rps.getName());
            
        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        }
    }


    /**
     * 型を取得するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
     * <li>"A,recListName"</li>
     * <li> getType()を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>RecordList.classが取得できる</li>
     * </ul>
     */
    public void testGetType() {
        try {
            RecordListPropertySchema rps = new RecordListPropertySchema();
            String schema = "A,recListName";
            rps.setSchema(schema);
            assertEquals(RecordList.class, rps.getType());
            
        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        }
    }


    /**
     * プロパティ値(レコードリスト)を設定するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
     * <li>"A,TestRecordList"</li>
     * <li> 生成したRecordListを指定してset(Object val)を実行する</li>
     * <li> get(Object val)を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>指定したプロパティ値が取得できる</li>
     * </ul>
     */
    public void testSetGet() {
        try {
            RecordListPropertySchema rps = new RecordListPropertySchema();
            String schema = "A,TestRecordList";
            rps.setSchema(schema);
            
            //RecordListを生成して値として設定する
            RecordList rlist = new RecordList("TestRecordList", ":A,java.lang.String\n:B,java.lang.String");
            rps.set(rlist);
            
            //get()で取得して検証
            assertEquals(rlist, rps.get(rlist));
            
        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        }
    }


    /**
     * Formatテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
     * <li>"A,recListName"</li>
     * <li> 適当な値を指定してformat(Object val) を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>指定した値が返される(変換されない)</li>
     * </ul>
     */
    public void testFormat() {
        try {
            RecordListPropertySchema rps = new RecordListPropertySchema();
            String schema = "A,recListName";
            rps.setSchema(schema);

            assertEquals("test", rps.format("test"));
            
        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        }
    }


    /**
     * Parseテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
     * <li>"A,recListName"</li>
     * <li> 適当な値を指定してformat(Object val) を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>指定した値が返される(変換されない)</li>
     * </ul>
     */
    public void testParse() {
        try {
            RecordListPropertySchema rps = new RecordListPropertySchema();
            String schema = "A,recListName";
            rps.setSchema(schema);

            assertEquals("test", rps.parse("test"));
            
        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        }
    }


    /**
     * ネストしたレコードリスト名を取得するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
     * <li>"A,recListName"</li>
     * <li> getRecordListName() を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>指定したレコードリスト名"recListName"が返される</li>
     * </ul>
     */
    public void testGetRecordListName() {
        try {
            RecordListPropertySchema rps = new RecordListPropertySchema();
            String schema = "A,recListName";
            rps.setSchema(schema);

            assertEquals("recListName", rps.getRecordListName());
            
        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        }
    }


    /**
     * スキーマの文字列表現を取得するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>次のスキーマを指定してsetSchema(String schema)を実行する</li>
     * <li>"A,recListName"</li>
     * <li> toString()を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>文字列"jp.ossc.nimbus.beans.dataset.RecordListPropertySchema{name=A,recordListName=recListName}"が返される</li>
     * </ul>
     */
    public void testToString() {
        try {
            RecordListPropertySchema rps = new RecordListPropertySchema();
            String schema = "A,recListName";
            rps.setSchema(schema);

            assertEquals("jp.ossc.nimbus.beans.dataset.RecordListPropertySchema{name=A,recordListName=recListName,type=class jp.ossc.nimbus.beans.dataset.RecordList}"
                    , rps.toString());
            
        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        }
    }

}
