package jp.ossc.nimbus.util.converter;

import java.io.*;
import jp.ossc.nimbus.beans.dataset.*;
import jp.ossc.nimbus.beans.dataset.Record;
import junit.framework.TestCase;
//
/**
 *
 * @author S.Teshima
 * @version 1.00 作成: 2008/01/28 - S.Teshima
 */

public class DataSetXMLConverterTest extends TestCase {

    public DataSetXMLConverterTest(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(DataSetXMLConverterTest.class);
    }


    /**
     * DataSetXMLConverterインスタンスを生成するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>DataSetXMLConverter()を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>convertTypeにDATASET_TO_XMLが設定されていることを確認</li>
     * </ul>
     */
    public void testDataSetXMLConverter() {
        DataSetXMLConverter conv = new DataSetXMLConverter();
        assertEquals(DataSetXMLConverter.DATASET_TO_XML, conv.getConvertType());
    }


    /**
     * 変換種別を指定してDataSetXMLConverterインスタンスを生成するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>変換種別にXML_TO_DATASETを指定して、DataSetXMLConverter#DataSetXMLConverter()を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>convertTypeにXML_TO_DATASETが設定されていることを確認</li>
     * </ul>
     */
    public void testDataSetXMLConverterInt() {
        DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.XML_TO_DATASET);
        assertEquals(DataSetXMLConverter.XML_TO_DATASET, conv.getConvertType());
    }


    /**
     * 変換種別を設定するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>変換種別にXML_TO_DATASETを指定して、DataSetXMLConverter#DataSetXMLConverter()を実行する</li>
     * <li>変換種別にDATASET_TO_XMLを指定して、DataSetXMLConverter#setConvertType(int type)を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>convertTypeにDATASET_TO_XMLが設定されていることを確認</li>
     * </ul>
     */
    public void testSetConvertType() {
        DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.XML_TO_DATASET);
        conv.setConvertType(DataSetXMLConverter.DATASET_TO_XML);
        assertEquals(DataSetXMLConverter.DATASET_TO_XML, conv.getConvertType());
    }



    /**
     * データセット名とデータセットのマッピングを設定するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>DataSet、DataSetXMLConverterの各インスタンスを生成する</li>
     * <li>データセット名に"ds"、データセットに生成したインスタンス名を指定して、<BR>
     * DataSetXMLConverter#setDataSet(String name, DataSet dataSet)を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>dataSetMapに指定した値が設定されていることを確認</li>
     * </ul>
     */
    public void testSetDataSet() {
        DataSet ds = new DataSet();
        DataSetXMLConverter conv = new DataSetXMLConverter();
        conv.setDataSet("ds", ds);
        assertEquals(ds, conv.dataSetMap.get("ds"));
    }


    /**
     * スキーマ情報を出力するかどうかを設定するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>DataSetXMLConverter()を実行する</li>
     * <li>isOutputにtrueを指定して、DataSetXMLConverter#setOutputSchema()を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>DataSetXMLConverter#isOutputSchema()がtrueであることを確認</li>
     * </ul>
     */
    public void testSetOutputSchema() {
        DataSetXMLConverter conv = new DataSetXMLConverter();
        conv.setOutputSchema(true);
        assertTrue(conv.isOutputSchema());
    }


    /**
     * データセット→XML変換時に使用するXSLファイルのパスを設定するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>DataSetXMLConverter()を実行する</li>
     * <li>pathに"C:\XSL\Test.xsl"を指定して、DataSetXMLConverter#setXSLFilePath(String path)を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>DataSetXMLConverter#getXSLFilePath()で"C:\XSL\Test.xsl"が返されることを確認</li>
     * </ul>
     */
    public void testSetXSLFilePath() {
        DataSetXMLConverter conv = new DataSetXMLConverter();
        conv.setXSLFilePath("C:\\XSL\\Test.xsl");
        assertEquals("C:\\XSL\\Test.xsl", conv.getXSLFilePath());
    }


    /**
     * データセット→XML変換時に使用する文字エンコーディングを設定するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>DataSetXMLConverter()を実行する</li>
     * <li>文字エンコーディングに"UTF-8"を指定して、DataSetXMLConverter#DataSetXMLConverter()を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>DataSetXMLConverter#getCharacterEncodingToStream()で"UTF-8"が返されることを確認</li>
     * </ul>
     */
    public void testSetCharacterEncodingToStream() {
        DataSetXMLConverter conv = new DataSetXMLConverter();
        conv.setCharacterEncodingToStream("UTF-8");
        assertEquals("UTF-8", conv.getCharacterEncodingToStream());
    }


    /**
     * XML→データセットXML変換時に使用する文字エンコーディングを設定するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>DataSetXMLConverter()を実行する</li>
     * <li>文字エンコーディングに"UTF-8"を指定して、DataSetXMLConverter#setCharacterEncodingToObject()を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>DataSetXMLConverter#getCharacterEncodingToObject()で"UTF-8"が返されることを確認</li>
     * </ul>
     */
    public void testSetCharacterEncodingToObject() {
        DataSetXMLConverter conv = new DataSetXMLConverter();
        conv.setCharacterEncodingToObject("UTF-8");
        assertEquals("UTF-8", conv.getCharacterEncodingToObject());
    }


    /**
     * 指定したオブジェクトをデータセット→XML変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>Dataset#DataSet()で空のDataSetを生成する</li>
     * <li>次のスキーマを指定してDataset#setHeaderSchema(String name, String schema)を実行する</li>
     * <li>name : "TestHeader"</li>
     * <li>":A,java.util.Date,<BR>
     * "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format="yyyy-MM-DD"}",<BR>
     *  "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format="yyyy-MM-DD"}",<BR>
     *  "@value@ != null"\n:B,java.lang.String,,,"</li>
     * <li>プロパティＡに"2008-01-28"を指定</li>
     * <li>プロパティＢに"TestValue"を指定</li>
     * <li>変換種別にDATASET_TO_XMLを指定して、DataSetXMLConverter#convert(Object obj)を実行する</li>
     * <li>生成したデータセットを指定して、DataSetXMLConverter#を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>DataSetXMLConverter#conv.convert()で次の内容のXMLストリームが返されることを確認。<BR>
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
     * </ul>
     */
    public void testConvertToXML() {
        try {
            DataSet dataset = new DataSet();
            String hname = "TestHeader";
            String schema = ":A,java.util.Date," +
                    "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\"," +
                            "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\"," +
                                    "\"@value@ != null\"\n:B,java.lang.String,,,";
            dataset.setHeaderSchema(hname, schema);
            Header header = dataset.getHeader("TestHeader");
            header.setParseProperty("A", "2008-01-28");
            header.setProperty("B", "TestValue");

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.DATASET_TO_XML);
            InputStream XmlStream = (InputStream)conv.convert(dataset);
            InputStreamReader reader = new InputStreamReader(XmlStream);
            BufferedReader br = new BufferedReader(reader);

            String s;
            StringBuffer sb = new StringBuffer();
            //改行を省いた文字列を出力
            while ((s = br.readLine()) != null){
                sb.append(s);
            }
            br.close();

            assertTrue(sb.toString().endsWith(
                    "<dataSet><schema><header name=\"TestHeader\">" +
                    ":A,java.util.Date," +
                    "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\"," +
                            "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\"," +
                                    "\"@value@ != null\":B,java.lang.String,,," +
                                    "</header></schema><header name=\"TestHeader\">" +
                                    "<A>2008-01-28</A><B>TestValue</B></header></dataSet>"));
        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (ConvertException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (IOException e) {
            e.printStackTrace();
            fail("例外発生");
        }

    }


    /**
     * 指定したオブジェクトをデータセット→XML変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>Dataset#DataSet()で空のDataSetを生成する</li>
     * <li>次のスキーマを指定してDataset#setHeaderSchema(String name, String schema)を実行する</li>
     * <li>name : "TestHeader"</li>
     * <li>":A,java.util.Date,<BR>
     * "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format="yyyy-MM-DD"}",<BR>
     *  "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format="yyyy-MM-DD"}",<BR>
     *  "@value@ != null"\n:B,java.lang.String,,,\n:C,java.lang.String,,,"</li>
     * <li>プロパティＡに"2008-01-28"を指定</li>
     * <li>プロパティＢにNullを指定</li>
     * <li>プロパティＣに"TestValue"を指定</li>
     * <li>変換種別にDATASET_TO_XMLを指定して、DataSetXMLConverter#convert(Object obj)を実行する</li>
     * <li>生成したデータセットを指定して、DataSetXMLConverter#を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>DataSetXMLConverter#conv.convert()で次の内容のXMLストリームが返されることを確認。<BR>
     * (null指定のプロパティは出力されないことを確認する)
     * <PRE>
     * <?xml version="1.0" encoding="UTF-8"?>
     *  <dataSet>
     *   <schema>
     *    <header name="TestHeader">
     *     :A,java.util.Date,
     *     "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format="yyyy-MM-DD"}",
     *     "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format="yyyy-MM-DD"}",
     *     "@value@ != null"
     *     :B,java.lang.String,,,
     *     :,java.lang.String,,,
     *    </header>
     *   </schema>
     *    <header name="TestHeader"><A>2008-01-28</A><C>TestValue</C></header></dataSet>
     * <PRE></li>
     * </ul>
     */
    public void testConvertToXMLNull() {
        try {
            DataSet dataset = new DataSet();
            String hname = "TestHeader";
            String schema = ":A,java.util.Date," +
                    "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\"," +
                            "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\"," +
                                    "\"@value@ != null\"\n:B,java.lang.String,,,\n:C,java.lang.String,,,";
            dataset.setHeaderSchema(hname, schema);
            Header header = dataset.getHeader("TestHeader");
            header.setParseProperty("A", "2008-01-28");
            header.setProperty("B", null);
            header.setProperty("C", "TestValue");

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.DATASET_TO_XML);
            InputStream XmlStream = (InputStream)conv.convert(dataset);
            InputStreamReader reader = new InputStreamReader(XmlStream);
            BufferedReader br = new BufferedReader(reader);

            String s;
            StringBuffer sb = new StringBuffer();
            //改行を省いた文字列を出力
            while ((s = br.readLine()) != null){
                sb.append(s);
            }
            br.close();

            assertTrue(sb.toString().endsWith(
                    "<dataSet><schema><header name=\"TestHeader\">" +
                    ":A,java.util.Date," +
                    "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\"," +
                            "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\"," +
                                    "\"@value@ != null\":B,java.lang.String,,,:C,java.lang.String,,," +
                                    "</header></schema><header name=\"TestHeader\">" +
                                    "<A>2008-01-28</A><C>TestValue</C></header></dataSet>"));
        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (ConvertException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (IOException e) {
            e.printStackTrace();
            fail("例外発生");
        }

    }



    /**
     * 指定したオブジェクトをデータセット→XML変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>Dataset#DataSet()で空のDataSetを生成する</li>
     * <li>次のネストしたレコードリストを含むスキーマを指定してDataset#setHeaderSchema(String name, String schema)を実行する</li>
     * <li>name : "TestHeader"</li>
     * <li>"LIST:HrList,\"HrList\""</li>
     * <li>ネストしたレコードリストのスキーマは":A,java.lang.String\n:B,java.lang.String"</li>
     * <li>プロパティＡに"a"を指定</li>
     * <li>プロパティＢに"b"を指定</li>
     * <li>変換種別にDATASET_TO_XMLを指定して、DataSetXMLConverter#convert(Object obj)を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>DataSetXMLConverter#conv.convert()で次の内容のXMLストリームが返されることを確認。<BR>
     * (null指定のプロパティは出力されないことを確認する)
     * <PRE>
     * <?xml version="1.0" encoding="UTF-8"?>
     *  <dataSet>
     *   <schema>
     *    <header name="TestHeader">
     *     :A,java.util.Date,
     *     "jp.ossc.nimbus.util.converter.DateFormatConverter{2,"yyyy-MM-DD"}",
     *     "jp.ossc.nimbus.util.converter.DateFormatConverter{1,"yyyy-MM-DD"}",
     *     "@value@ != null"
     *     :B,java.lang.String,,,
     *     :,java.lang.String,,,
     *    </header>
     *   </schema>
     *    <header name="TestHeader"><A>2008-01-28</A><C>TestValue</C></header></dataSet>
     * <PRE></li>
     * </ul>
     */
    public void testConvertToXMLWithHeaderNestedRecordList() {
        try {
            DataSet dataset = new DataSet();
            //ネストするレコードリストを作る
            dataset.setNestedRecordListSchema("HrList", ":A,java.lang.String\n:B,java.lang.String");

            String hname = "TestHeader";
            String schema = "LIST:HrList,HrList";
            dataset.setHeaderSchema(hname, schema);
            //Headerの値として設定するネストしたレコードリストを取得して値を設定
            RecordList HrList = dataset.createNestedRecordList("HrList");
            Record nrec1 = HrList.createRecord();
            nrec1.setProperty("A", "a");
            nrec1.setProperty("B", "b");
            HrList.addRecord(nrec1);
            //Headerを取得してネストしたレコードリストを値に設定
            Header header = dataset.getHeader(hname);
            header.setProperty("HrList", HrList);

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.DATASET_TO_XML);
            InputStream XmlStream = (InputStream)conv.convert(dataset);
            InputStreamReader reader = new InputStreamReader(XmlStream);
            BufferedReader br = new BufferedReader(reader);

            String s;
            StringBuffer sb = new StringBuffer();
            //改行を省いた文字列を出力
            while ((s = br.readLine()) != null){
                sb.append(s);
            }
            br.close();

            assertTrue(sb.toString().endsWith(
                    "<header name=\"TestHeader\">LIST:HrList,HrList</header>" +
                    "<nestedRecordList name=\"HrList\">:A,java.lang.String:B,java.lang.String</nestedRecordList>" +
                    "</schema><header name=\"TestHeader\">" +
                    "<HrList><recordList name=\"HrList\"><record><A>a</A><B>b</B></record></recordList></HrList>" +
                    "</header></dataSet>"));
        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (ConvertException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (IOException e) {
            e.printStackTrace();
            fail("例外発生");
        }

    }



    /**
     * 指定したオブジェクトをデータセット→XML変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>Dataset#DataSet()で空のDataSetを生成する</li>
     * <li>次のネストしたレコードリストを含むスキーマを指定してDataset#setHeaderSchema(String name, String schema)を実行する</li>
     * <li>name : "TestHeader"</li>
     * <li>"LIST:HrList,HrList\n:C,java.lang.String"</li>
     * <li>直下のプロパティCには"c"を設定</li>
     * <li>ネストしたレコードリストのスキーマは":A,java.lang.String\n:B,java.lang.String"</li>
     * <li>プロパティＡに"a"を指定</li>
     * <li>プロパティＢに"b"を指定</li>
     * <li>変換種別にDATASET_TO_XMLを指定して、DataSetXMLConverter#convert(Object obj)を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>DataSetXMLConverter#conv.convert()で正しいの内容のXMLストリームが返されることを確認。<BR>
     * (null指定のプロパティは出力されないことを確認する)</li>
     * </ul>
     */
    public void testConvertToXMLWithHeaderNestedRecordList2() {
        try {
            DataSet dataset = new DataSet();
            //ネストするレコードリストを作る
            dataset.setNestedRecordListSchema("HrList", ":A,java.lang.String\n:B,java.lang.String");

            String hname = "TestHeader";
            String schema = "LIST:HrList,HrList\n:C,java.lang.String";
            dataset.setHeaderSchema(hname, schema);
            //Headerの値として設定するネストしたレコードリストを取得して値を設定
            RecordList HrList = dataset.createNestedRecordList("HrList");
            Record nrec1 = HrList.createRecord();
            nrec1.setProperty("A", "a");
            nrec1.setProperty("B", "b");
            HrList.addRecord(nrec1);
            //Headerを取得してネストしたレコードリストを値に設定
            Header header = dataset.getHeader(hname);
            header.setProperty("HrList", HrList);
            header.setProperty("C", "c");

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.DATASET_TO_XML);
            InputStream XmlStream = (InputStream)conv.convert(dataset);
            InputStreamReader reader = new InputStreamReader(XmlStream);
            BufferedReader br = new BufferedReader(reader);

            String s;
            StringBuffer sb = new StringBuffer();
            //改行を省いた文字列を出力
            while ((s = br.readLine()) != null){
                sb.append(s);
            }
            br.close();

            assertTrue(sb.toString().endsWith("<dataSet><schema>" +
                    "<header name=\"TestHeader\">LIST:HrList,HrList:C,java.lang.String</header>" +
                    "<nestedRecordList name=\"HrList\">:A,java.lang.String:B,java.lang.String</nestedRecordList>" +
                    "</schema><header name=\"TestHeader\">" +
                    "<HrList><recordList name=\"HrList\"><record><A>a</A><B>b</B></record></recordList></HrList>" +
                    "<C>c</C></header></dataSet>"));
        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (ConvertException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (IOException e) {
            e.printStackTrace();
            fail("例外発生");
        }

    }



    /**
     * 指定したオブジェクトをデータセット→XML変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>Dataset#DataSet()で空のDataSetを生成する</li>
     * <li>次のネストしたレコードリストを含むスキーマを指定してDataset#RecordListSchema(String name, String schema)を実行する</li>
     * <li>name : "TestRecList"</li>
     * <li>"LIST:RrList,HrList\n:C,java.lang.String"</li>
     * <li>直下のプロパティCには"c"を設定</li>
     * <li>ネストしたレコードリストのスキーマは":A,java.lang.String\n:B,java.lang.String"</li>
     * <li>プロパティＡに"a"を指定</li>
     * <li>プロパティＢに"b"を指定</li>
     * <li>変換種別にDATASET_TO_XMLを指定して、DataSetXMLConverter#convert(Object obj)を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>DataSetXMLConverter#conv.convert()で正しいの内容のXMLストリームが返されることを確認。<BR>
     * (null指定のプロパティは出力されないことを確認する)</li>
     * </ul>
     */
    public void testConvertToXMLWithRecordListNestedRecordList() {
        try {
            DataSet dataset = new DataSet();
            //ネストするレコードリストを作る
            dataset.setNestedRecordListSchema("RrList", ":A,java.lang.String\n:B,java.lang.String");

            String name = "TestRecList";
            String schema = "LIST:RrList,RrList\n:C,java.lang.String";
            dataset.setRecordListSchema(name, schema);
            //Recordの値として設定するネストしたレコードリストを取得して値を設定
            RecordList RrList = dataset.createNestedRecordList("RrList");
            Record nrec1 = RrList.createRecord();
            nrec1.setProperty("A", "a");
            nrec1.setProperty("B", "b");
            RrList.addRecord(nrec1);
            //レコードリストを取得してネストしたレコードリストを値に設定
            Record rec = dataset.getRecordList(name).createRecord();
            rec.setProperty("RrList", RrList);
            rec.setProperty("C", "c");
            dataset.getRecordList(name).addRecord(rec);

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.DATASET_TO_XML);
            InputStream XmlStream = (InputStream)conv.convert(dataset);
            InputStreamReader reader = new InputStreamReader(XmlStream);
            BufferedReader br = new BufferedReader(reader);

            String s;
            StringBuffer sb = new StringBuffer();
            //改行を省いた文字列を出力
            while ((s = br.readLine()) != null){
                sb.append(s);
            }
            br.close();

            assertTrue(sb.toString().endsWith("<dataSet><schema>" +
                    "<recordList name=\"TestRecList\">LIST:RrList,RrList:C,java.lang.String</recordList>" +
                    "<nestedRecordList name=\"RrList\">:A,java.lang.String:B,java.lang.String</nestedRecordList>" +
                    "</schema><recordList name=\"TestRecList\"><record><RrList><recordList name=\"RrList\">" +
                    "<record><A>a</A><B>b</B></record></recordList></RrList><C>c</C></record>" +
                    "</recordList></dataSet>"));
        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (ConvertException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (IOException e) {
            e.printStackTrace();
            fail("例外発生");
        }

    }


    /**
     * 指定したオブジェクトをデータセット→XML変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>Dataset#DataSet()で空のDataSetを生成する</li>
     * <li>次のネストしたレコードリストを含むスキーマを指定してDataset#setHeaderSchema(String name, String schema)を実行する</li>
     * <li>name : "TestHeader"</li>
     * <li>"LIST:HrList,HrList\n:C,java.lang.String"</li>
     * <li>直下のプロパティCには値を指定しない</li>
     * <li>ネストしたレコードリストのスキーマは":A,java.lang.String\n:B,java.lang.String"</li>
     * <li>プロパティＡに"a"を指定</li>
     * <li>プロパティＢに値を指定しない</li>
     * <li>次のネストしたレコードリストを含むスキーマを指定してDataset#RecordListSchema(String name, String schema)を実行する</li>
     * <li>name : "TestRecList"</li>
     * <li>"LIST:RrList,RrList\n:LC,java.lang.String"</li>
     * <li>直下のプロパティLCには値を指定しない</li>
     * <li>ネストしたレコードリストのスキーマは":LA,java.lang.String\n:LB,int"</li>
     * <li>プロパティLＡに"値を指定しない</li>
     * <li>プロパティLＢに1を指定</li>
     * <li>変換種別にDATASET_TO_XMLを指定して、DataSetXMLConverter#convert(Object obj)を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>DataSetXMLConverter#conv.convert()で正しいの内容のXMLストリームが返されることを確認。<BR>
     * (null指定のプロパティは出力されないことを確認する)</li>
     * </ul>
     */
    public void testConvertToXMLWithHeaderAndRecordListNestedRecordList() {
        try {
            DataSet dataset = new DataSet();
            //ネストするレコードリストを作る
            dataset.setNestedRecordListSchema("HrList", ":A,java.lang.String\n:B,java.lang.String");
            dataset.setNestedRecordListSchema("RrList", ":LA,java.lang.String\n:LB,int");

            String name = "test_name";
            String hschema = "LIST:HrList,HrList\n:C,java.lang.String";
            String rschema = "LIST:RrList,RrList\n:LC,java.lang.String";
            dataset.setSchema(name, hschema, rschema);

            //Headerの値として設定するネストしたレコードリストを取得して値を設定
            RecordList HrList = dataset.createNestedRecordList("HrList");
            Record nrec1 = HrList.createRecord();
            nrec1.setProperty("A", "a");
            HrList.addRecord(nrec1);
            //Headerを取得してネストしたレコードリストを値に設定
            Header header = dataset.getHeader(name);
            header.setProperty("HrList", HrList);


            //レコードの値として設定するネストしたレコードリストを取得して値を設定
            RecordList RrList = dataset.createNestedRecordList("RrList");
            Record nrec2 = RrList.createRecord();
            nrec2.setProperty("LB", 1);
            RrList.addRecord(nrec2);
            //RecordListを取得してレコードにネストしたレコードリストを値に設定

            RecordList rlist = dataset.getRecordList(name);
            Record rec = rlist.createRecord();
            rec.setProperty("RrList", RrList);
            rlist.addRecord(rec);

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.DATASET_TO_XML);
            InputStream XmlStream = (InputStream)conv.convert(dataset);
            InputStreamReader reader = new InputStreamReader(XmlStream);
            BufferedReader br = new BufferedReader(reader);

            String s;
            StringBuffer sb = new StringBuffer();
            //改行を省いた文字列を出力
            while ((s = br.readLine()) != null){
                sb.append(s);
            }
            br.close();

            assertTrue(sb.toString().endsWith("<dataSet><schema>" +
                    "<header name=\"test_name\">LIST:HrList,HrList:C,java.lang.String</header>" +
                    "<recordList name=\"test_name\">LIST:RrList,RrList:LC,java.lang.String</recordList>" +
                    "<nestedRecordList name=\"HrList\">:A,java.lang.String:B,java.lang.String</nestedRecordList>" +
                    "<nestedRecordList name=\"RrList\">:LA,java.lang.String:LB,int</nestedRecordList></schema>" +
                    "<header name=\"test_name\"><HrList><recordList name=\"HrList\">" +
                    "<record><A>a</A></record></recordList></HrList></header><recordList name=\"test_name\"><record>" +
                    "<RrList><recordList name=\"RrList\"><record><LB>1</LB></record></recordList></RrList></record>" +
                    "</recordList></dataSet>"));
        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (ConvertException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (IOException e) {
            e.printStackTrace();
            fail("例外発生");
        }

    }



    /**
     * 指定したオブジェクトをデータセット→XML変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>Dataset#DataSet()で空のDataSetを生成する</li>
     * <li>次のネストしたレコードリストを含むスキーマを指定してDataset#setHeaderSchema(String name, String schema)を実行する</li>
     * <li>name : "TestHeader"</li>
     * <li>"LIST:HrList,HrList\n:C,java.lang.String"</li>
     * <li>直下のプロパティCには"c"を設定</li>
     * <li>ネストしたレコードリストのスキーマは":A,java.lang.String\n:B,java.lang.String"</li>
     * <li>プロパティＡに"a"を指定</li>
     * <li>プロパティＢに"b"を指定</li>
     * <li>次のネストしたレコードリストを含むスキーマを指定してDataset#RecordListSchema(String name, String schema)を実行する</li>
     * <li>name : "TestRecList"</li>
     * <li>"LIST:RrList,RrList\n:LC,java.lang.String"</li>
     * <li>直下のプロパティCには"c"を設定</li>
     * <li>ネストしたレコードリストのスキーマは":LA,java.lang.String\n:LB,int"</li>
     * <li>プロパティLＡに"la"を指定</li>
     * <li>プロパティLＢに1を指定</li>
     * <li>変換種別にDATASET_TO_XMLを指定して、DataSetXMLConverter#convert(Object obj)を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>DataSetXMLConverter#conv.convert()で正しいの内容のXMLストリームが返されることを確認。<BR>
     * (null指定のプロパティは出力されないことを確認する)</li>
     * </ul>
     */
    public void testConvertToXMLWithHeaderAndRecordListNestedRecordList2() {
        try {
            DataSet dataset = new DataSet();
            //ネストするレコードリストを作る
            dataset.setNestedRecordListSchema("HrList", ":A,java.lang.String\n:B,java.lang.String");
            dataset.setNestedRecordListSchema("RrList", ":LA,java.lang.String\n:LB,int");

            String name = "test_name";
            String hschema = "LIST:HrList,HrList\n:C,java.lang.String";
            String rschema = "LIST:RrList,RrList\n:LC,java.lang.String";
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
            header.setProperty("C", "c");


            //レコードの値として設定するネストしたレコードリストを取得して値を設定
            RecordList RrList = dataset.createNestedRecordList("RrList");
            Record nrec2 = RrList.createRecord();
            nrec2.setProperty("LA", "la");
            nrec2.setProperty("LB", 1);
            RrList.addRecord(nrec2);
            //RecordListを取得してレコードにネストしたレコードリストを値に設定

            RecordList rlist = dataset.getRecordList(name);
            Record rec = rlist.createRecord();
            rec.setProperty("RrList", RrList);
            rec.setProperty("LC", "lc");
            rlist.addRecord(rec);

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.DATASET_TO_XML);
            InputStream XmlStream = (InputStream)conv.convert(dataset);
            InputStreamReader reader = new InputStreamReader(XmlStream);
            BufferedReader br = new BufferedReader(reader);

            String s;
            StringBuffer sb = new StringBuffer();
            //改行を省いた文字列を出力
            while ((s = br.readLine()) != null){
                sb.append(s);
            }
            br.close();

            assertTrue(sb.toString().endsWith("<dataSet><schema>" +
                    "<header name=\"test_name\">LIST:HrList,HrList:C,java.lang.String</header>" +
                    "<recordList name=\"test_name\">LIST:RrList,RrList:LC,java.lang.String</recordList>" +
                    "<nestedRecordList name=\"HrList\">:A,java.lang.String:B,java.lang.String</nestedRecordList>" +
                    "<nestedRecordList name=\"RrList\">:LA,java.lang.String:LB,int</nestedRecordList>" +
                            "</schema><header name=\"test_name\"><HrList><recordList name=\"HrList\">" +
                            "<record><A>a</A><B>b</B></record></recordList></HrList><C>c</C></header>" +
                            "<recordList name=\"test_name\"><record><RrList><recordList name=\"RrList\">" +
                            "<record><LA>la</LA><LB>1</LB></record></recordList></RrList><LC>lc</LC></record>" +
                            "</recordList></dataSet>"));
        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (ConvertException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (IOException e) {
            e.printStackTrace();
            fail("例外発生");
        }

    }


    /**
     * 指定したオブジェクトをデータセット→XML変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>Dataset#DataSet()で空のDataSetを生成する</li>
     * <li>次のネストしたレコードリストを含むスキーマを指定してDataset#setHeaderSchema(String name, String schema)を実行する</li>
     * <li>name : "TestHeader"</li>
     * <li>"LIST:HrList,HrList\n:C,java.lang.String"</li>
     * <li>直下のプロパティCには"c"を設定</li>
     * <li>ネストしたレコードリストのスキーマは":A,java.lang.String\n:B,java.lang.String"</li>
     * <li>プロパティHrList2にレコードリストを指定</li>
     * <li>プロパティＢに"b"を指定</li>
     * <li>次のネストしたレコードリストを含むスキーマを指定してDataset#RecordListSchema(String name, String schema)を実行する</li>
     * <li>name : "TestRecList"</li>
     * <li>"LIST:RrList,RrList\n:LC,java.lang.String"</li>
     * <li>直下のプロパティCには"c"を設定</li>
     * <li>ネストしたレコードリストのスキーマは":LA,java.lang.String\n:LB,int"</li>
     * <li>プロパティLRrList2にRrList2を指定</li>
     * <li>プロパティLＢに1を指定</li>
     * <li>変換種別にDATASET_TO_XMLを指定して、DataSetXMLConverter#convert(Object obj)を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>DataSetXMLConverter#conv.convert()で正しいの内容のXMLストリームが返されることを確認。<BR>
     * (null指定のプロパティは出力されないことを確認する)</li>
     * </ul>
     */
    public void testConvertToXMLWithHeaderAndRecordListNestedRecordList3() {
        try {
            DataSet dataset = new DataSet();
            //ネストするレコードリストにネストするレコードリストを作る
            dataset.setNestedRecordListSchema("HrList2", ":A,java.lang.String\n:B,int");
            dataset.setNestedRecordListSchema("RrList2", ":A,long\n:B,short");

            //ネストするレコードリストを作る
            dataset.setNestedRecordListSchema("HrList", "LIST:HrList2,HrList2\n:B,java.lang.String");
            dataset.setNestedRecordListSchema("RrList", "LIST:RrList2,RrList2\n:LB,int");

            String name = "test_name";
            String hschema = "LIST:HrList,HrList\n:C,java.lang.String";
            String rschema = "LIST:RrList,RrList\n:LC,java.lang.String";
            dataset.setSchema(name, hschema, rschema);

            //Headerの値として設定するネストしたレコードリストを取得して値を設定

            //ネストのネスト
            RecordList HrList2 = dataset.createNestedRecordList("HrList2");
            Record nnrec1 = HrList2.createRecord();
            nnrec1.setProperty("A", "a");
            nnrec1.setProperty("B", 999);
            HrList2.addRecord(nnrec1);

            //Headerに値を設定
            RecordList HrList = dataset.createNestedRecordList("HrList");
            Record nrec1 = HrList.createRecord();
            nrec1.setProperty("HrList2", HrList2);
            nrec1.setProperty("B", "b");
            HrList.addRecord(nrec1);
            //Headerを取得してネストしたレコードリストを値に設定
            Header header = dataset.getHeader(name);
            header.setProperty("HrList", HrList);
            header.setProperty("C", "c");

            //レコードの値として設定するネストしたレコードリストを取得して値を設定

            //ネストのネスト
            RecordList RrList2 = dataset.createNestedRecordList("RrList2");
            Record nnrec2 = RrList2.createRecord();
            nnrec2.setProperty("A", (long)111);
            nnrec2.setProperty("B", (short)222);
            RrList2.addRecord(nnrec2);

            RecordList RrList = dataset.createNestedRecordList("RrList");
            Record nrec2 = RrList.createRecord();
            nrec2.setProperty("RrList2", RrList2);
            nrec2.setProperty("LB", 1);
            RrList.addRecord(nrec2);
            //RecordListを取得してレコードにネストしたレコードリストを値に設定

            RecordList rlist = dataset.getRecordList(name);
            Record rec = rlist.createRecord();
            rec.setProperty("RrList", RrList);
            rec.setProperty("LC", "lc");
            rlist.addRecord(rec);

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.DATASET_TO_XML);
            InputStream XmlStream = (InputStream)conv.convert(dataset);
            InputStreamReader reader = new InputStreamReader(XmlStream);
            BufferedReader br = new BufferedReader(reader);

            String s;
            StringBuffer sb = new StringBuffer();
            //改行を省いた文字列を出力
            while ((s = br.readLine()) != null){
                sb.append(s);
            }
            br.close();

            assertTrue(sb.toString().endsWith(
                    "<dataSet><schema><header name=\"test_name\">" +
                    "LIST:HrList,HrList:C,java.lang.String</header>" +
                    "<recordList name=\"test_name\">LIST:RrList,RrList:LC,java.lang.String</recordList>" +
                    "<nestedRecordList name=\"HrList2\">:A,java.lang.String:B,int</nestedRecordList>" +
                    "<nestedRecordList name=\"RrList2\">:A,long:B,short</nestedRecordList>" +
                    "<nestedRecordList name=\"HrList\">LIST:HrList2,HrList2:B,java.lang.String</nestedRecordList>" +
                    "<nestedRecordList name=\"RrList\">LIST:RrList2,RrList2:LB,int</nestedRecordList>" +
                    "</schema><header name=\"test_name\"><HrList><recordList name=\"HrList\">" +
                    "<record><HrList2><recordList name=\"HrList2\"><record><A>a</A><B>999</B></record>" +
                    "</recordList></HrList2><B>b</B></record></recordList></HrList><C>c</C></header>" +
                    "<recordList name=\"test_name\"><record><RrList><recordList name=\"RrList\">" +
                    "<record><RrList2><recordList name=\"RrList2\"><record><A>111</A><B>222</B></record>" +
                    "</recordList></RrList2><LB>1</LB></record></recordList></RrList><LC>lc</LC></record>" +
                    "</recordList></dataSet>"));
        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (ConvertException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (IOException e) {
            e.printStackTrace();
            fail("例外発生");
        }

    }


    /**
     * 指定したオブジェクトをデータセット→XML変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>Dataset#DataSet()で空のDataSetを生成する</li>
     * <li>次のスキーマを指定してDataset#setRecordListSchema(String name, String schema)を実行する</li>
     * <li>name : "TestRs"</li>
     * <li>":A,java.util.Date,<BR>
     * "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format="yyyy-MM-DD"}",<BR>
     *  "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format="yyyy-MM-DD"}",<BR>
     *  "@value@ != null"\n:B,java.lang.String,,,"</li>
     * <li>プロパティＡに"2008-01-28"を指定</li>
     * <li>プロパティＢに"TestValue"を指定</li>
     * <li>変換種別にDATASET_TO_XMLを指定して、DataSetXMLConverter#convert(Object obj)を実行する</li>
     * <li>生成したデータセットを指定して、DataSetXMLConverter#を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>DataSetXMLConverter#conv.convert()で次の内容のXMLストリームが返されることを確認。<BR>
     * <PRE>
     * <?xml version="1.0" encoding="UTF-8"?>
     *  <dataSet>
     *   <schema>
     *    <recordList name="TestRs">
     *     :A,java.util.Date,
     *     "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format="yyyy-MM-DD"}",
     *     "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format="yyyy-MM-DD"}",
     *     "@value@ != null"
     *     :B,java.lang.String,,,
     *    </recordList>
     *   </schema>
     *    <recordList name="TestRs"><record><A>2008-01-28</A><B>TestValue</B></record></recordList></dataSet>
     * <PRE></li>
     * </ul>
     */
    public void testConvertToXML2() {
        try {
            DataSet dataset = new DataSet();
            String name = "TestRs";
            String schema = ":A,java.util.Date," +
                    "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\"," +
                            "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\"," +
                                    "\"@value@ != null\"\n:B,java.lang.String,,,";
            dataset.setRecordListSchema(name, schema);
            RecordList rs = dataset.getRecordList("TestRs");
            Record rec = rs.createRecord();
            rec.setParseProperty("A", "2008-01-28");
            rec.setProperty("B", "TestValue");
            rs.addRecord(rec);

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.DATASET_TO_XML);
            InputStream XmlStream = (InputStream)conv.convert(dataset);
            InputStreamReader reader = new InputStreamReader(XmlStream);
            BufferedReader br = new BufferedReader(reader);

            String s;
            StringBuffer sb = new StringBuffer();
            //改行を省いた文字列を出力
            while ((s = br.readLine()) != null){
                sb.append(s);
            }
            br.close();

            assertTrue(sb.toString().endsWith(
                    "<dataSet><schema><recordList name=\"TestRs\">" +
                    ":A,java.util.Date," +
                    "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\"," +
                            "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\"," +
                                    "\"@value@ != null\":B,java.lang.String,,," +
                                    "</recordList></schema><recordList name=\"TestRs\">" +
                                    "<record><A>2008-01-28</A><B>TestValue</B></record></recordList></dataSet>"));
        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (ConvertException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (IOException e) {
            e.printStackTrace();
            fail("例外発生");
        }

    }


    /**
     * 指定したオブジェクトをXML変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>データセット以外のオブジェクト(文字型)を指定して、DataSetXMLConverter#convert(Object obj)を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>例外ConvertExceptionが発生することを確認</li>
     * <li>例外メッセージに"Invalid input type : class java.lang.String"が返されることを確認</li>
     * </ul>
     */
    public void testConvertToXMLInvalid() {
        try {

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.DATASET_TO_XML);
            conv.convert("ABC");
            fail("例外が発生しないためテスト失敗 ");
        } catch (ConvertException e) {
            assertEquals("Invalid input type : class java.lang.String", e.getMessage());
        }
    }



    /**
     * 指定したオブジェクトをXML→データセット変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>次の内容のXMLストリームと変換種別にXML_TO_DATASETを指定して、<BR>
     * DataSetXMLConverter#convert(Object obj)を実行する<BR>
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
     * <PRE>
     * </li>
     * </ul>
     * 確認：
     * <ul>
     * <li>取得したDataSetのヘッダスキーマが変換元の内容と等しいことを確認。</li>
     * <li>取得したDataSetの各プロパティの値が変換元の内容と等しいことを確認。</li>
     * </ul>
     */
    public void testConvertToDataSetFromInputStream() {
        try {
            String inxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<dataSet><schema><header name=\"TestHeader\">" +
            ":A,java.util.Date," +
            "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\"," +
                    "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\"," +
                            "\"@value@ != null\"\n:B,java.lang.String,,," +
                            "</header></schema><header name=\"TestHeader\">" +
                            "<A>2008-01-28</A><B>TestValue</B></header></dataSet>";
            InputStream is = new ByteArrayInputStream(inxml.getBytes());

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.XML_TO_DATASET);
            DataSet dataset = (DataSet)conv.convert(is);

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
     * 指定したオブジェクトをXML→データセット変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>次のネストしたレコードリストを含む内容のXMLストリームと変換種別にXML_TO_DATASETを指定して、<BR>
     * DataSetXMLConverter#convert(Object obj)を実行する<BR>
     * <PRE>
     * <?xml version="1.0" encoding="UTF-8"?>
     * <dataSet>
     *     <schema>
     *         <header name="TestHeader">
     *             LIST:HrList,"HrList"
     *             :C,java.lang.String
     *         </header>
     *         <nestedRecordList name="HrList">
     *             :A,java.lang.String
     *             :B,java.lang.String
     *         </nestedRecordList>
     *     </schema>
     *     <header name="TestHeader">
     *         　<recordList name="HrList">
     *             <A>a</A>
     *             <B>b</B>
     *         　</recordList>
     *         <C>c</C>
     *     </header>
     * </dataSet>
     * <PRE>
     * </li>
     * </ul>
     * 確認：
     * <ul>
     * <li>取得したDataSetのヘッダスキーマが変換元の内容と等しいことを確認。</li>
     * <li>取得したDataSetの各プロパティの値が変換元の内容と等しいことを確認。</li>
     * </ul>
     */
    public void testConvertToDataSetFromInputStreamWithNestedRecordList() {
        try {
            String inxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<dataSet><schema><header name=\"TestHeader\">" +
            "LIST:HrList,HrList\n:C,java.lang.String</header>" +
                            "<nestedRecordList name=\"HrList\">" +
                            ":A,java.lang.String\n:B,java.lang.String" +
                            "</nestedRecordList></schema><header name=\"TestHeader\">" +
                            "<HrList><recordList name=\"HrList\">" +
                            "<record><A>a</A><B>b</B></record></recordList></HrList><C>c</C></header></dataSet>";
            InputStream is = new ByteArrayInputStream(inxml.getBytes());

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.XML_TO_DATASET);
            DataSet dataset = (DataSet)conv.convert(is);

            assertEquals("TestHeader",dataset.getHeader("TestHeader").getName());
            assertEquals("LIST:HrList,HrList\n:C,java.lang.String",dataset.getHeader("TestHeader").getSchema());
            assertEquals("c",dataset.getHeader("TestHeader").getProperty("C"));
            //ネストしたレコードリストの内容が正しいか検証する
            RecordList rlist = (RecordList)dataset.getHeader("TestHeader").getProperty("HrList");
            assertEquals(":A,java.lang.String\n:B,java.lang.String", rlist.getSchema());
            Record rec = rlist.getRecord(0);
            assertEquals("a", rec.getProperty("A"));
            assertEquals("b", rec.getProperty("B"));

        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (ConvertException e) {
            e.printStackTrace();
            fail("例外発生");
        }
    }


    /**
     * 指定したオブジェクトをXML→データセット変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>次のネストしたレコードリストを含む内容のXMLストリームと変換種別にXML_TO_DATASETを指定して、<BR>
     * DataSetXMLConverter#convert(Object obj)を実行する<BR>
     * <PRE>
     * <dataSet>
     *     <schema>
     *         <recordList name="TestRecList">
     *             LIST:RrList,RrList
     *             :C,java.lang.String
     *         </recordList>
     *         <nestedRecordList name="RrList">
     *         :A,java.lang.String
     *         :B,java.lang.String
     *         </nestedRecordList>
     *     </schema>
     *     <recordList name="TestRecList">
     *         <record>
     *             <RrList>
     *                 <recordList name="RrList">
     *                     <record><A>a</A><B>b</B></record>
     *                 </recordList>
     *             </RrList>
     *             <C>c</C>
     *         </record>
     *     </recordList>
     * </dataSet>
     * <PRE>
     * </li>
     * </ul>
     * 確認：
     * <ul>
     * <li>取得したDataSetのヘッダスキーマが変換元の内容と等しいことを確認。</li>
     * <li>取得したDataSetの各プロパティの値が変換元の内容と等しいことを確認。</li>
     * </ul>
     */
    public void testConvertToDataSetFromInputStreamWithNestedRecordList2() {
        try {
            String inxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><dataSet><schema>" +
                    "<recordList name=\"TestRecList\">LIST:RrList,RrList\n:C,java.lang.String</recordList>" +
                    "<nestedRecordList name=\"RrList\">:A,java.lang.String\n:B,java.lang.String</nestedRecordList>" +
                    "</schema><recordList name=\"TestRecList\"><record><RrList><recordList name=\"RrList\">" +
                    "<record><A>a</A><B>b</B></record></recordList></RrList><C>c</C></record>" +
                    "</recordList></dataSet>";
            InputStream is = new ByteArrayInputStream(inxml.getBytes());

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.XML_TO_DATASET);
            DataSet dataset = (DataSet)conv.convert(is);

            assertEquals("TestRecList",dataset.getRecordList("TestRecList").getName());
            assertEquals("LIST:RrList,RrList\n:C,java.lang.String",dataset.getRecordList("TestRecList").getSchema());
            assertEquals("c",dataset.getRecordList("TestRecList").getRecord(0).getProperty("C"));
            //ネストしたレコードリストの内容が正しいか検証する
            RecordList rlist = (RecordList)dataset.getRecordList("TestRecList").getRecord(0).getProperty("RrList");
            assertEquals(":A,java.lang.String\n:B,java.lang.String", rlist.getSchema());
            Record rec = rlist.getRecord(0);
            assertEquals("a", rec.getProperty("A"));
            assertEquals("b", rec.getProperty("B"));

        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (ConvertException e) {
            e.printStackTrace();
            fail("例外発生");
        }
    }


    /**
     * 指定したオブジェクトをXML→データセット変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>次のネストしたレコードリストを含むHeader、RecordListが設定されている内容のXMLストリーム<BR>
     * と変換種別にXML_TO_DATASETを指定して、DataSetXMLConverter#convert(Object obj)を実行する<BR>
     * <PRE>
     * <dataSet>
     *  <schema>
     *      <header name="test_name">
     *          LIST:HrList,HrList
     *          :C,java.lang.String
     *      </header>
     *      <recordList name="test_name">
     *          LIST:RrList,RrList
     *          :LC,java.lang.String
     *      </recordList>
     *      <nestedRecordList name="HrList">
     *          :A,java.lang.String
     *          :B,java.lang.String
     *      </nestedRecordList>
     *      <nestedRecordList name="RrList">
     *          :LA,java.lang.String
     *          :LB,int
     *      </nestedRecordList>
     *  </schema>
     *  <header name="test_name">
     *      <HrList>
     *          <recordList name="HrList">
     *              <record><A>a</A><B>b</B></record>
     *          </recordList>
     *          <C>c</C>
     *      </HrList>
     *  </header>
     *  <recordList name="test_name">
     *      <record>
     *          <RrList>
     *              <recordList name="RrList">
     *                  <record><LA>la</LA><LB>1</LB></record>
     *              </recordList>
     *          </RrList>
     *          <LC>lc</LC>
     *      </record>
     *  </recordList>
     * </dataSet>
     * <PRE>
     * </li>
     * </ul>
     * 確認：
     * <ul>
     * <li>取得したDataSetのヘッダスキーマが変換元の内容と等しいことを確認。</li>
     * <li>取得したDataSetの各プロパティの値が変換元の内容と等しいことを確認。</li>
     * </ul>
     */
    public void testConvertToDataSeWithHeaderAndRecordListtFromInputStreamWithNestedRecordList() {
        try {
            String inxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><dataSet><schema>" +
                    "<header name=\"test_hname\">LIST:HrList,HrList\n:C,java.lang.String</header>" +
                    "<recordList name=\"test_rname\">LIST:RrList,RrList\n:LC,java.lang.String</recordList>" +
                    "<nestedRecordList name=\"HrList\">:A,java.lang.String\n:B,java.lang.String</nestedRecordList>" +
                    "<nestedRecordList name=\"RrList\">:LA,java.lang.String\n:LB,int</nestedRecordList>" +
                            "</schema><header name=\"test_hname\"><HrList><recordList name=\"HrList\">" +
                            "<record><A>a</A><B>b</B></record></recordList></HrList><C>c</C></header>" +
                            "<recordList name=\"test_rname\"><record><RrList><recordList name=\"RrList\">" +
                            "<record><LA>la</LA><LB>1</LB></record></recordList></RrList><LC>lc</LC></record>" +
                            "</recordList></dataSet>";
            InputStream is = new ByteArrayInputStream(inxml.getBytes());

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.XML_TO_DATASET);
            DataSet dataset = (DataSet)conv.convert(is);

            assertEquals("test_hname",dataset.getHeader("test_hname").getName());
            assertEquals("LIST:HrList,HrList\n:C,java.lang.String",dataset.getHeader("test_hname").getSchema());
            assertEquals("c",dataset.getHeader("test_hname").getProperty("C"));
            //ネストしたレコードリストの内容が正しいか検証する
            RecordList rlist = (RecordList)dataset.getHeader("test_hname").getProperty("HrList");
            assertEquals(":A,java.lang.String\n:B,java.lang.String", rlist.getSchema());
            Record rec = rlist.getRecord(0);
            assertEquals("a", rec.getProperty("A"));
            assertEquals("b", rec.getProperty("B"));

            assertEquals("test_rname",dataset.getRecordList("test_rname").getName());
            assertEquals("LIST:RrList,RrList\n:LC,java.lang.String",dataset.getRecordList("test_rname").getSchema());
            assertEquals("lc",dataset.getRecordList("test_rname").getRecord(0).getProperty("LC"));
            //ネストしたレコードリストの内容が正しいか検証する
            rlist = (RecordList)dataset.getRecordList("test_rname").getRecord(0).getProperty("RrList");
            assertEquals(":LA,java.lang.String\n:LB,int", rlist.getSchema());
            rec = rlist.getRecord(0);
            assertEquals("la", rec.getProperty("LA"));
            assertEquals(1, rec.getIntProperty("LB"));

        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (ConvertException e) {
            e.printStackTrace();
            fail("例外発生");
        }
    }



    /**
     * 指定したオブジェクトをXML→データセット変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>次のネストしたレコードリストを含むHeader、RecordListが設定されている内容のXMLストリーム<BR>
     * と変換種別にXML_TO_DATASETを指定して、DataSetXMLConverter#convert(Object obj)を実行する<BR>
     * 一部のプロパティの値を未指定にする
     * <PRE>
     * <dataSet>
     *  <schema>
     *      <header name="test_name">
     *          LIST:HrList,HrList
     *          :C,java.lang.String
     *      </header>
     *      <recordList name="test_name">
     *          LIST:RrList,RrList
     *          :LC,java.lang.String
     *      </recordList>
     *      <nestedRecordList name="HrList">
     *          :A,java.lang.String
     *          :B,java.lang.String
     *      </nestedRecordList>
     *      <nestedRecordList name="RrList">
     *          :LA,java.lang.String
     *          :LB,int
     *      </nestedRecordList>
     *  </schema>
     *  <header name="test_name">
     *      <HrList>
     *          <recordList name="HrList">
     *              <record><A>a</A><B>b</B></record>
     *          </recordList>
     *      </HrList>
     *  </header>
     *  <recordList name="test_name">
     *      <record>
     *          <RrList>
     *              <recordList name="RrList">
     *                  <record><LB>1</LB></record>
     *              </recordList>
     *          </RrList>
     *          <LC>lc</LC>
     *      </record>
     *  </recordList>
     * </dataSet>
     * <PRE>
     * </li>
     * </ul>
     * 確認：
     * <ul>
     * <li>取得したDataSetのヘッダスキーマが変換元の内容と等しいことを確認。</li>
     * <li>取得したDataSetの各プロパティの値が変換元の内容と等しいことを確認。</li>
     * <li>値を指定していないプロパティについてはnullが返されることを確認</li>
     * </ul>
     */
    public void testConvertToDataSeWithHeaderAndRecordListtFromInputStreamWithNestedRecordList2() {
        try {
            String inxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><dataSet><schema>" +
                    "<header name=\"test_hname\">LIST:HrList,HrList\n:C,java.lang.String</header>" +
                    "<recordList name=\"test_rname\">LIST:RrList,RrList\n:LC,java.lang.String</recordList>" +
                    "<nestedRecordList name=\"HrList\">:A,java.lang.String\n:B,java.lang.String</nestedRecordList>" +
                    "<nestedRecordList name=\"RrList\">:LA,java.lang.String\n:LB,int</nestedRecordList>" +
                            "</schema><header name=\"test_hname\"><HrList><recordList name=\"HrList\">" +
                            "<record><A>a</A><B>b</B></record></recordList></HrList></header>" +
                            "<recordList name=\"test_rname\"><record><RrList><recordList name=\"RrList\">" +
                            "<record><LB>1</LB></record></recordList></RrList><LC>lc</LC></record>" +
                            "</recordList></dataSet>";
            InputStream is = new ByteArrayInputStream(inxml.getBytes());

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.XML_TO_DATASET);
            DataSet dataset = (DataSet)conv.convert(is);

            assertEquals("test_hname",dataset.getHeader("test_hname").getName());
            assertEquals("LIST:HrList,HrList\n:C,java.lang.String",dataset.getHeader("test_hname").getSchema());
            assertNull(dataset.getHeader("test_hname").getProperty("C"));
            //ネストしたレコードリストの内容が正しいか検証する
            RecordList rlist = (RecordList)dataset.getHeader("test_hname").getProperty("HrList");
            assertEquals(":A,java.lang.String\n:B,java.lang.String", rlist.getSchema());
            Record rec = rlist.getRecord(0);
            assertEquals("a", rec.getProperty("A"));
            assertEquals("b", rec.getProperty("B"));

            assertEquals("test_rname",dataset.getRecordList("test_rname").getName());
            assertEquals("LIST:RrList,RrList\n:LC,java.lang.String",dataset.getRecordList("test_rname").getSchema());
            assertEquals("lc",dataset.getRecordList("test_rname").getRecord(0).getProperty("LC"));
            //ネストしたレコードリストの内容が正しいか検証する
            rlist = (RecordList)dataset.getRecordList("test_rname").getRecord(0).getProperty("RrList");
            assertEquals(":LA,java.lang.String\n:LB,int", rlist.getSchema());
            rec = rlist.getRecord(0);
            assertNull(rec.getProperty("LA"));
            assertEquals(1, rec.getIntProperty("LB"));

        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (ConvertException e) {
            e.printStackTrace();
            fail("例外発生");
        }
    }



    /**
     * 指定したオブジェクトをXML→データセット変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>次の２階層ネストしたレコードリストを含むHeader、RecordListが設定されている内容のXMLストリーム<BR>
     * と変換種別にXML_TO_DATASETを指定して、DataSetXMLConverter#convert(Object obj)を実行する<BR>
     * 一部のプロパティの値を未指定にする
     * <PRE>
     *  <dataSet>
     *      <schema>
     *          <header name="test_name">
     *              LIST:HrList,HrList
     *              :C,java.lang.String
     *          </header>
     *          <recordList name="test_name">
     *              LIST:RrList,RrList
     *              :LC,java.lang.String
     *          </recordList>
     *          <nestedRecordList name="HrList2">
     *              :A,java.lang.String
     *              :B,int
     *          </nestedRecordList>
     *          <nestedRecordList name="RrList2">
     *              :A,long
     *              :B,short
     *          </nestedRecordList>
     *          <nestedRecordList name="HrList">
     *              LIST:HrList2,HrList2
     *              :B,java.lang.String
     *          </nestedRecordList>
     *          <nestedRecordList name="RrList">
     *              LIST:RrList2,RrList2
     *              :LB,int
     *          </nestedRecordList>
     *      </schema>
     *      <header name="test_name">
     *          <HrList><recordList name="HrList"><record>
     *              <HrList2>
     *                  <recordList name="HrList2">
     *                      <record><A>a</A><B>999</B></record>
     *                  </recordList>
     *              </HrList2>
     *              <B>b</B>
     *          </record></recordList></HrList>
     *          <C>c</C>
     *      </header>
     *      <recordList name="test_name"><record>
     *          <RrList><recordList name="RrList"><record>
     *              <RrList2>
     *                  <recordList name="RrList2">
     *                      <record><A>111</A><B>222</B></record>
     *                  </recordList>
     *              </RrList2>
     *              <LB>1</LB>
     *          </record></recordList></RrList>
     *          <LC>lc</LC>
     *      </record></recordList>
     *  </dataSet>
     * <PRE>
     * </li>
     * </ul>
     * 確認：
     * <ul>
     * <li>取得したDataSetのヘッダスキーマが変換元の内容と等しいことを確認。</li>
     * <li>取得したDataSetの各プロパティの値が変換元の内容と等しいことを確認。</li>
     * </ul>
     */
    public void testConvertToDataSeWithHeaderAndRecordListtFromInputStreamWithNestedRecordList3() {
        try {
            String inxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<dataSet><schema><header name=\"test_hname\">" +
                    "LIST:HrList,HrList\n:C,java.lang.String</header>" +
                    "<recordList name=\"test_rname\">LIST:RrList,RrList\n:LC,java.lang.String</recordList>" +
                    "<nestedRecordList name=\"HrList2\">:A,java.lang.String\n:B,int</nestedRecordList>" +
                    "<nestedRecordList name=\"RrList2\">:A,long\n:B,short</nestedRecordList>" +
                    "<nestedRecordList name=\"HrList\">LIST:HrList2,HrList2\n:B,java.lang.String</nestedRecordList>" +
                    "<nestedRecordList name=\"RrList\">LIST:RrList2,RrList2\n:LB,int</nestedRecordList>" +
                    "</schema><header name=\"test_hname\"><HrList><recordList name=\"HrList\">" +
                    "<record><HrList2><recordList name=\"HrList2\"><record><A>a</A><B>999</B></record>" +
                    "</recordList></HrList2><B>b</B></record></recordList></HrList><C>c</C></header>" +
                    "<recordList name=\"test_rname\"><record><RrList><recordList name=\"RrList\">" +
                    "<record><RrList2><recordList name=\"RrList2\"><record><A>111</A><B>222</B></record>" +
                    "</recordList></RrList2><LB>1</LB></record></recordList></RrList><LC>lc</LC></record>" +
                    "</recordList></dataSet>";
            InputStream is = new ByteArrayInputStream(inxml.getBytes());

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.XML_TO_DATASET);
            DataSet dataset = (DataSet)conv.convert(is);

            assertEquals("test_hname",dataset.getHeader("test_hname").getName());
            assertEquals("LIST:HrList,HrList\n:C,java.lang.String",dataset.getHeader("test_hname").getSchema());
            assertEquals("c",dataset.getHeader("test_hname").getProperty("C"));
            //ネストしたレコードリストの内容が正しいか検証する
            RecordList rlist = (RecordList)dataset.getHeader("test_hname").getProperty("HrList");
            assertEquals("LIST:HrList2,HrList2\n:B,java.lang.String", rlist.getSchema());
            Record rec = rlist.getRecord(0);
            assertEquals("b", rec.getProperty("B"));
            //２階層下のレコードリストチェック
            RecordList nrlist = (RecordList)rec.getProperty("HrList2");
            assertEquals(":A,java.lang.String\n:B,int", nrlist.getSchema());
            Record nrec = nrlist.getRecord(0);
            assertEquals("a", nrec.getProperty("A"));
            assertEquals(999, nrec.getIntProperty("B"));

            assertEquals("test_rname",dataset.getRecordList("test_rname").getName());
            assertEquals("LIST:RrList,RrList\n:LC,java.lang.String",dataset.getRecordList("test_rname").getSchema());
            assertEquals("lc",dataset.getRecordList("test_rname").getRecord(0).getProperty("LC"));
            //ネストしたレコードリストの内容が正しいか検証する
            rlist = (RecordList)dataset.getRecordList("test_rname").getRecord(0).getProperty("RrList");
            assertEquals("LIST:RrList2,RrList2\n:LB,int", rlist.getSchema());
            rec = rlist.getRecord(0);
            assertEquals(1, rec.getIntProperty("LB"));

            //２階層下のレコードリストチェック
            nrlist = (RecordList)rec.getProperty("RrList2");
            assertEquals(":A,long\n:B,short", nrlist.getSchema());
            nrec = nrlist.getRecord(0);
            assertEquals(111, nrec.getLongProperty("A"));
            assertEquals(222, nrec.getShortProperty("B"));

        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (ConvertException e) {
            e.printStackTrace();
            fail("例外発生");
        }
    }




    /**
     * 指定したオブジェクトをXML→データセット変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>次の内容のXMLストリームと変換種別にXML_TO_DATASETを指定して、<BR>
     * DataSetXMLConverter#convert(Object obj)を実行する<BR>
     * <PRE>
     * <?xml version="1.0" encoding="UTF-8"?>
     *  <dataSet>
     *   <schema>
     *    <recordList name="TestRs">
     *     :A,java.util.Date,
     *     "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format="yyyy-MM-DD"}",
     *     "jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format="yyyy-MM-DD"}",
     *     "@value@ != null"
     *     :B,java.lang.String,,,
     *    </recordList>
     *   </schema>
     *    <recordList name="TestRs"><record><A>2008-01-28</A><B>TestValue</B></record></recordList></dataSet>
     * <PRE>
     * </li>
     * </ul>
     * 確認：
     * <ul>
     * <li>取得したDataSetのレコードリストスキーマが変換元の内容と等しいことを確認。<BR>
     * <li>取得したDataSetの各プロパティの値が変換元の内容と等しいことを確認。<BR>
     * </ul>
     */
    public void testConvertToDataSetFromInputStream2() {
        try {
            String inxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<dataSet><schema><recordList name=\"TestRs\">" +
            ":A,java.util.Date," +
            "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\"," +
                    "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\"," +
                            "\"@value@ != null\"\n:B,java.lang.String,,," +
                            "</recordList></schema><recordList name=\"TestRs\">" +
                            "<record><A>2008-01-28</A><B>TestValue</B></record></recordList></dataSet>";
            InputStream is = new ByteArrayInputStream(inxml.getBytes());

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.XML_TO_DATASET);
            DataSet dataset = (DataSet)conv.convert(is);

            assertEquals("TestRs",dataset.getRecordList("TestRs").getName());
            assertEquals(":A,java.util.Date," +
                    "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\"," +
                    "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\"," +
                            "\"@value@ != null\"\n:B,java.lang.String,,,",dataset.getRecordList("TestRs").getSchema());
            assertEquals("2008-01-28",dataset.getRecordList("TestRs").getRecord(0).getFormatProperty("A"));
            assertEquals("TestValue",dataset.getRecordList("TestRs").getRecord(0).getProperty("B"));

        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (ConvertException e) {
            e.printStackTrace();
            fail("例外発生");
        }
    }



    /**
     * 指定したオブジェクトをXML→データセット変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>次の内容のXMLファイルと変換種別にXML_TO_DATASETを指定して、<BR>
     * DataSetXMLConverter#convert(Object obj)を実行する<BR>
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
     * </li>
     * </ul>
     * 確認：
     * <ul>
     * <li>取得したDataSetのヘッダスキーマが変換元の内容と等しいことを確認。<BR>
     * <li>取得したDataSetの各プロパティの値が変換元の内容と等しいことを確認。<BR>
     * </ul>
     */
    public void testConvertToDataSetFromFile() {
        try {
            File xmlf = new File("src/test/resources/jp/ossc/nimbus/util/converter/DataSetXMLConverterTest.xml");
            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.XML_TO_DATASET);
            DataSet dataset = (DataSet)conv.convert(xmlf);

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
     * 指定したオブジェクトをXML変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>データセット以外のオブジェクト(文字型)を指定して、DataSetXMLConverter#convertToStream(Object obj)を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>例外ConvertExceptionが発生することを確認</li>
     * <li>例外メッセージに"Invalid input type : class java.lang.String"が返されることを確認</li>
     * </ul>
     * </ul>
     */
    public void testConvertToDataSetIvalid() {
        try {

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.XML_TO_DATASET);
            conv.convert("ABC");
            fail("例外が発生しないためテスト失敗 ");
        } catch (ConvertException e) {
            assertEquals("Invalid input type : class java.lang.String", e.getMessage());
        }
    }



    /**
     * 指定したオブジェクトをXML→データセット変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>nullと変換種別にXML_TO_DATASETを指定して、<BR>
     * DataSetXMLConverter#convert(Object obj)を実行する<BR></li>
     * </ul>
     * 確認：
     * <ul>
     * <li>nullが返ってくることを確認。<BR>
     * </ul>
     */
    public void testConvertToObjectNull() {
        try {
            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.XML_TO_DATASET);
            assertNull(conv.convert(null));

        } catch (ConvertException e) {
            e.printStackTrace();
            fail("例外発生");
        }
    }

    /**
     * 指定したオブジェクトをXML→データセット変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>変換種別に999(不正な値)を指定して、<BR>
     * DataSetXMLConverter#convert(Object obj)を実行する<BR></li>
     * </ul>
     * 確認：
     * <ul>
     * <li>例外ConvertExceptionが発生することを確認</li>
     * <li>例外メッセージに"Invalid convert type : 999"が返されることを確認</li>
     * </ul>
     */
    public void testConvertToObjectIvalid() {
        try {
            String inxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<dataSet><schema><header name=\"TestHeader\">" +
            ":A,java.util.Date," +
            "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\"," +
                    "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\"," +
                            "\"@value@ != null\"\n:B,java.lang.String,,," +
                            "</header></schema><header name=\"TestHeader\">" +
                            "<A>2008-01-28</A><B>TestValue</B></header></dataSet>";
            InputStream is = new ByteArrayInputStream(inxml.getBytes());

            DataSetXMLConverter conv = new DataSetXMLConverter(999);
            conv.convert(is);
            fail("例外が発生しないためテスト失敗 ");
        } catch (ConvertException e) {
            assertEquals("Invalid convert type : 999", e.getMessage());
        }
    }

    /**
     * 指定したオブジェクトをXML変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>データセット以外のオブジェクト(文字型)を指定して、DataSetXMLConverter#convertToStream(Object obj)を実行する</li>
     * </ul>
     * 確認：
     * <ul>
     * <li>例外ConvertExceptionが発生することを確認</li>
     * <li>例外メッセージに"Invalid input type : class java.lang.String"が返されることを確認</li>
     * </ul>
     */
    public void testConvertToStreamInvalid() {
        try {

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.DATASET_TO_XML);
            conv.convertToStream("ABC");
            fail("例外が発生しないためテスト失敗 ");
        } catch (ConvertException e) {
            assertEquals("Invalid input type : class java.lang.String", e.getMessage());
        }
    }


    /**
     * 指定したオブジェクトをXML→データセット変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>次の内容のXMLストリームと変換種別にXML_TO_DATASETを指定して、<BR>
     * DataSetXMLConverter#convertToObject(Object obj)を実行する<BR>
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
     * </li>
     * </ul>
     * 確認：
     * <ul>
     * <li>取得したDataSetのヘッダスキーマが変換元の内容と等しいことを確認。<BR>
     * <li>取得したDataSetの各プロパティの値が変換元の内容と等しいことを確認。<BR>
     * </ul>
     */
    public void testconvertToObjectFromInputStream() {
        try {
            String inxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<dataSet><schema><header name=\"TestHeader\">" +
            ":A,java.util.Date," +
            "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\"," +
                    "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\"," +
                            "\"@value@ != null\"\n:B,java.lang.String,,," +
                            "</header></schema><header name=\"TestHeader\">" +
                            "<A>2008-01-28</A><B>TestValue</B></header></dataSet>";
            InputStream is = new ByteArrayInputStream(inxml.getBytes());

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.XML_TO_DATASET);
            DataSet dataset = (DataSet)conv.convertToObject(is);

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
     * 指定したオブジェクトをXML→データセット変換するテスト。
     * <p>
     * 条件：
     * <ul>
     * <li>次の内容のXMLストリームと変換種別にXML_TO_DATASETを指定して、<BR>
     * DataSetXMLConverter#convert(Object obj)を実行する<BR>
     * (プロパティBの値なし)
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
     *     :C,java.lang.String,,,
     *    </header>
     *   </schema>
     *    <header name="TestHeader"><A>2008-01-28</A><C>TestValue</C></header></dataSet>
     * <PRE>
     * </li>
     * </ul>
     * 確認：
     * <ul>
     * <li>取得したDataSetのヘッダスキーマが変換元の内容と等しいことを確認。<BR>
     * 取得したDataSetの各プロパティの値が変換元の内容と等しいことを確認。<BR>
     * 指定しなかったプロパティの値についてはnullオブジェクトになる</li>
     * </ul>
     */
    public void testConvertToDataSetFromInputStreamNull() {
        try {
            String inxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<dataSet><schema><header name=\"TestHeader\">" +
            ":A,java.util.Date," +
            "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\"," +
                    "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\"," +
                            "\"@value@ != null\"\n:B,java.lang.String,,,\n:C,java.lang.String,,," +
                            "</header></schema><header name=\"TestHeader\">" +
                            "<A>2008-01-28</A><C>TestValue</C></header></dataSet>";
            InputStream is = new ByteArrayInputStream(inxml.getBytes());

            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.XML_TO_DATASET);
            DataSet dataset = (DataSet)conv.convert(is);

            assertEquals("TestHeader",dataset.getHeader("TestHeader").getName());
            assertEquals(":A,java.util.Date," +
                    "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=2;Format=\"yyyy-MM-DD\"}\"," +
                    "\"jp.ossc.nimbus.util.converter.DateFormatConverter{ConvertType=1;Format=\"yyyy-MM-DD\"}\"," +
                            "\"@value@ != null\"\n:B,java.lang.String,,,\n:C,java.lang.String,,,",dataset.getHeader("TestHeader").getSchema());
            assertEquals("2008-01-28",dataset.getHeader("TestHeader").getFormatProperty("A"));
            assertEquals("TestValue",dataset.getHeader("TestHeader").getProperty("C"));
            //値を指定していないBはnull
            assertNull(dataset.getHeader("TestHeader").getProperty("B"));

        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (ConvertException e) {
            e.printStackTrace();
            fail("例外発生");
        }
    }

    public void testConvertToDataSetNoMatchSchema() {
        try {
            DataSetXMLConverter conv = new DataSetXMLConverter(DataSetXMLConverter.XML_TO_DATASET);
            DataSet dataset = new DataSet();
            dataset.setHeaderSchema("Header1", ":A,java.lang.String");
            dataset.setHeaderSchema("Header2", ":B,int\nLIST:C,ListC");
            dataset.setRecordListSchema("RecordList1", ":C,java.lang.String");
            dataset.setRecordListSchema("RecordList2", ":D,int\nLIST:E,ListC");
            dataset.setNestedRecordListSchema("ListC", ":F,java.util.Date");
            conv.setDataSet("test", dataset);

            // スキーマ定義に存在しないヘッダがある
            String inxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<dataSet name=\"test\">"
                 + "<header name=\"Header1\"><A>a</A></header>"
                 + "<header name=\"Header3\"><Z>z</Z></header>"
                 + "<header name=\"Header2\"><B>100</B></header></dataSet>";
            InputStream is = new ByteArrayInputStream(inxml.getBytes());
            conv.setIgnoreUnknownElement(false);
            try{
                dataset = (DataSet)conv.convert(is);
                fail("Must be detect error.");
            }catch(ConvertException e){
            }
            try{
                is.reset();
            }catch(IOException e){}
            conv.setIgnoreUnknownElement(true);
            try{
                dataset = (DataSet)conv.convert(is);
            }catch(ConvertException e){
                e.printStackTrace();
                fail("Must not be detect error.");
            }
            assertEquals("a", dataset.getHeader("Header1").getProperty("A"));
            assertEquals(new Integer(100), dataset.getHeader("Header2").getProperty("B"));
            assertNull(dataset.getHeader("Header3"));

            // スキーマ定義に存在しないヘッダのプロパティがある
            inxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<dataSet name=\"test\">"
                 + "<header name=\"Header1\"><A>a</A><B>100</B></header>"
                 + "<header name=\"Header2\"><B>100</B></header></dataSet>";
            is = new ByteArrayInputStream(inxml.getBytes());
            conv.setIgnoreUnknownElement(false);
            try{
                dataset = (DataSet)conv.convert(is);
                fail();
            }catch(ConvertException e){
            }
            try{
                is.reset();
            }catch(IOException e){}
            conv.setIgnoreUnknownElement(true);
            try{
                dataset = (DataSet)conv.convert(is);
            }catch(ConvertException e){
                e.printStackTrace();
                fail();
            }
            assertEquals("a", dataset.getHeader("Header1").getProperty("A"));
            try{
                dataset.getHeader("Header1").getProperty("B");
                fail();
            }catch(PropertyGetException e){
            }
            assertEquals(new Integer(100), dataset.getHeader("Header2").getProperty("B"));

            // スキーマ定義に存在しないレコードリストがある
            inxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<dataSet name=\"test\">"
                 + "<header name=\"Header1\"><A>a</A><B>100</B></header>"
                 + "<recordList name=\"RecordList3\"><record><Z>z1</Z></record><record><Z>z2</Z></record></recordList>"
                 + "<recordList name=\"RecordList1\"><record><C>c1</C></record><record><C>c2</C></record></recordList>"
                 + "</dataSet>";
            is = new ByteArrayInputStream(inxml.getBytes());
            conv.setIgnoreUnknownElement(false);
            try{
                dataset = (DataSet)conv.convert(is);
                fail();
            }catch(ConvertException e){
            }
            try{
                is.reset();
            }catch(IOException e){}
            conv.setIgnoreUnknownElement(true);
            try{
                dataset = (DataSet)conv.convert(is);
            }catch(ConvertException e){
                e.printStackTrace();
                fail();
            }
            assertEquals("a", dataset.getHeader("Header1").getProperty("A"));
            assertTrue(dataset.getRecordList("RecordList1").size() == 2);
            assertEquals("c1", dataset.getRecordList("RecordList1").getRecord(0).getProperty("C"));
            assertEquals("c2", dataset.getRecordList("RecordList1").getRecord(1).getProperty("C"));
            assertNull(dataset.getRecordList("RecordList3"));

            // スキーマ定義に存在しないレコードリストのプロパティがある
            inxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<dataSet name=\"test\">"
                 + "<header name=\"Header1\"><A>a</A><B>100</B></header>"
                 + "<recordList name=\"RecordList1\"><record><C>c1</C><D>d</D></record><record><C>c2</C></record></recordList>"
                 + "<recordList name=\"RecordList2\"><record><D>1</D></record><record><D>2</D></record></recordList>"
                 + "</dataSet>";
            is = new ByteArrayInputStream(inxml.getBytes());
            conv.setIgnoreUnknownElement(false);
            try{
                dataset = (DataSet)conv.convert(is);
                fail();
            }catch(ConvertException e){
            }
            try{
                is.reset();
            }catch(IOException e){}
            conv.setIgnoreUnknownElement(true);
            try{
                dataset = (DataSet)conv.convert(is);
            }catch(ConvertException e){
                e.printStackTrace();
                fail();
            }
            assertEquals("a", dataset.getHeader("Header1").getProperty("A"));
            assertTrue(dataset.getRecordList("RecordList1").size() == 2);
            assertEquals("c1", dataset.getRecordList("RecordList1").getRecord(0).getProperty("C"));
            assertEquals("c2", dataset.getRecordList("RecordList1").getRecord(1).getProperty("C"));
            try{
                dataset.getRecordList("RecordList1").getRecord(0).getProperty("D");
                fail();
            }catch(PropertyGetException e){
            }
            assertTrue(dataset.getRecordList("RecordList2").size() == 2);
            assertEquals(1, dataset.getRecordList("RecordList2").getRecord(0).getIntProperty("D"));
            assertEquals(2, dataset.getRecordList("RecordList2").getRecord(1).getIntProperty("D"));

        } catch (PropertySchemaDefineException e) {
            e.printStackTrace();
            fail("例外発生");
        } catch (ConvertException e) {
            e.printStackTrace();
            fail("例外発生");
        }
    }


}



