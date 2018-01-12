package jp.ossc.nimbus.util.converter;

import java.io.*;
import jp.ossc.nimbus.beans.dataset.*;
import junit.framework.TestCase;

public class DataSetJSONConverterTest extends TestCase {
    
    public DataSetJSONConverterTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(DataSetJSONConverterTest.class);
    }
    
    public void test1() throws Exception{
        DataSet ds = new DataSet("dataset1");
        ds.setHeaderSchema(
            "HeaderName1",
            ":PropertyName1,java.lang.String"
                + '\n' + ":PropertyName2,int"
                + '\n' + ":PropertyName3,int[]"
        );
        Header header = ds.getHeader("HeaderName1");
        header.setProperty("PropertyName1", "value");
        header.setProperty("PropertyName2", 100);
        header.setProperty("PropertyName3", new int[]{1, 2});
        
        DataSetJSONConverter converter = new DataSetJSONConverter();
        InputStream is = converter.convertToStream(ds);
        
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        StringWriter sw = new StringWriter();
        int length = 0;
        char[] buf = new char[1024];
        while((length = isr.read(buf, 0, buf.length)) != -1){
            sw.write(buf, 0, length);
        }
        assertEquals(
            "{\"dataset1\":{\"schema\":{\"header\":{\"HeaderName1\":\":PropertyName1,java.lang.String\\n:PropertyName2,int\\n:PropertyName3,int[]\"}},\"header\":{\"HeaderName1\":{\"PropertyName1\":\"value\",\"PropertyName2\":100,\"PropertyName3\":[1,2]}}}}",
            sw.toString()
        );
    }
    
    public void test2() throws Exception{
        DataSet ds = new DataSet("dataset1");
        ds.setHeaderSchema(
            "HeaderName1",
            ":PropertyName1,java.lang.String"
                + '\n' + ":PropertyName2,int"
                + '\n' + ":PropertyName3,int[]"
        );
        Header header = ds.getHeader("HeaderName1");
        header.setProperty("PropertyName1", "value");
        header.setProperty("PropertyName2", 100);
        header.setProperty("PropertyName3", new int[]{1, 2});
        
        ds.setHeaderSchema(
            "HeaderName2",
            ":PropertyName1,java.lang.String[]"
                + '\n' + ":PropertyName2,java.lang.String"
        );
        header = ds.getHeader("HeaderName2");
        header.setProperty("PropertyName1", new String[]{"value1", "value2"});
        
        DataSetJSONConverter converter = new DataSetJSONConverter();
        InputStream is = converter.convertToStream(ds);
        
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        StringWriter sw = new StringWriter();
        int length = 0;
        char[] buf = new char[1024];
        while((length = isr.read(buf, 0, buf.length)) != -1){
            sw.write(buf, 0, length);
        }
        assertEquals(
            "{\"dataset1\":{\"schema\":{\"header\":{\"HeaderName1\":\":PropertyName1,java.lang.String\\n:PropertyName2,int\\n:PropertyName3,int[]\",\"HeaderName2\":\":PropertyName1,java.lang.String[]\\n:PropertyName2,java.lang.String\"}},\"header\":{\"HeaderName1\":{\"PropertyName1\":\"value\",\"PropertyName2\":100,\"PropertyName3\":[1,2]},\"HeaderName2\":{\"PropertyName1\":[\"value1\",\"value2\"],\"PropertyName2\":null}}}}",
            sw.toString()
        );
    }
    
    public void test3() throws Exception{
        DataSet ds = new DataSet("dataset1");
        ds.setRecordListSchema(
            "RecordListName1",
            ":PropertyName1,java.lang.String"
                + '\n' + ":PropertyName2,int"
                + '\n' + ":PropertyName3,java.lang.String[]"
        );
        RecordList recList = ds.getRecordList("RecordListName1");
        Record rec = recList.createRecord();
        rec.setProperty("PropertyName1", "value1");
        rec.setProperty("PropertyName2", 100);
        rec.setProperty("PropertyName3", new String[]{"value1", "value2"});
        recList.addRecord(rec);
        rec = recList.createRecord();
        rec.setProperty("PropertyName1", "value2");
        rec.setProperty("PropertyName2", 200);
        rec.setProperty("PropertyName3", new String[]{"value1", "value2"});
        recList.addRecord(rec);
        
        DataSetJSONConverter converter = new DataSetJSONConverter();
        InputStream is = converter.convertToStream(ds);
        
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        StringWriter sw = new StringWriter();
        int length = 0;
        char[] buf = new char[1024];
        while((length = isr.read(buf, 0, buf.length)) != -1){
            sw.write(buf, 0, length);
        }
        assertEquals(
            "{\"dataset1\":{\"schema\":{\"recordList\":{\"RecordListName1\":\":PropertyName1,java.lang.String\\n:PropertyName2,int\\n:PropertyName3,java.lang.String[]\"}},\"recordList\":{\"RecordListName1\":[{\"PropertyName1\":\"value1\",\"PropertyName2\":100,\"PropertyName3\":[\"value1\",\"value2\"]},{\"PropertyName1\":\"value2\",\"PropertyName2\":200,\"PropertyName3\":[\"value1\",\"value2\"]}]}}}",
            sw.toString()
        );
    }
    
    public void test4() throws Exception{
        DataSet ds = new DataSet("dataset1");
        ds.setNestedRecordListSchema(
            "NestedRecordListName1",
            ":PropertyName1,java.lang.String"
                + '\n' + ":PropertyName2,int"
                + '\n' + ":PropertyName3,java.lang.String[]"
        );
        ds.setRecordListSchema(
            "RecordListName1",
            ":PropertyName1,java.lang.String"
                + '\n' + "LIST:PropertyName2,NestedRecordListName1"
        );
        RecordList recList = ds.getRecordList("RecordListName1");
        Record rec = recList.createRecord();
        rec.setProperty("PropertyName1", "value");
        RecordList nestedRecList = ds.createNestedRecordList(
            "NestedRecordListName1"
        );
        Record nestedRec = nestedRecList.createRecord();
        nestedRec.setProperty("PropertyName1", "value");
        nestedRec.setProperty("PropertyName2", 100);
        nestedRec.setProperty("PropertyName3", new String[]{"value1", "value2"});
        nestedRecList.addRecord(nestedRec);
        rec.setProperty("PropertyName2", nestedRecList);
        recList.addRecord(rec);
        
        DataSetJSONConverter converter = new DataSetJSONConverter();
        InputStream is = converter.convertToStream(ds);
        
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        StringWriter sw = new StringWriter();
        int length = 0;
        char[] buf = new char[1024];
        while((length = isr.read(buf, 0, buf.length)) != -1){
            sw.write(buf, 0, length);
        }
        assertEquals(
            "{\"dataset1\":{\"schema\":{\"recordList\":{\"RecordListName1\":\":PropertyName1,java.lang.String\\nLIST:PropertyName2,NestedRecordListName1\"},\"nestedRecordList\":{\"NestedRecordListName1\":\":PropertyName1,java.lang.String\\n:PropertyName2,int\\n:PropertyName3,java.lang.String[]\"}},\"recordList\":{\"RecordListName1\":[{\"PropertyName1\":\"value\",\"PropertyName2\":[{\"PropertyName1\":\"value\",\"PropertyName2\":100,\"PropertyName3\":[\"value1\",\"value2\"]}]}]}}}",
            sw.toString()
        );
    }
    
    public void test5() throws Exception{
        DataSet ds = new DataSet("dataset1");
        ds.setHeaderSchema(
            "HeaderName1",
            ":PropertyName1,java.lang.String"
                + '\n' + ":PropertyName2,int"
                + '\n' + ":PropertyName3,int[]"
        );
        Header header = ds.getHeader("HeaderName1");
        header.setProperty("PropertyName1", "value");
        header.setProperty("PropertyName2", 100);
        header.setProperty("PropertyName3", new int[]{1, 2});
        
        DataSetJSONConverter converter = new DataSetJSONConverter();
        converter.setOutputSchema(false);
        InputStream is = converter.convertToStream(ds);
        
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        StringWriter sw = new StringWriter();
        int length = 0;
        char[] buf = new char[1024];
        while((length = isr.read(buf, 0, buf.length)) != -1){
            sw.write(buf, 0, length);
        }
        assertEquals(
            "{\"dataset1\":{\"header\":{\"HeaderName1\":{\"PropertyName1\":\"value\",\"PropertyName2\":100,\"PropertyName3\":[1,2]}}}}",
            sw.toString()
        );
    }
    
    public void test6() throws Exception{
        DataSet ds = new DataSet("dataset1");
        ds.setHeaderSchema(
            "HeaderName1",
            ":PropertyName1,java.lang.String"
                + '\n' + ":PropertyName2,int"
                + '\n' + ":PropertyName3,int[]"
        );
        Header header = ds.getHeader("HeaderName1");
        header.setProperty("PropertyName1", "value");
        header.setProperty("PropertyName2", 100);
        header.setProperty("PropertyName3", new int[]{1, 2});
        
        ds.setRecordListSchema(
            "RecordListName1",
            ":PropertyName1,java.lang.String"
                + '\n' + ":PropertyName2,int"
                + '\n' + ":PropertyName3,java.lang.String[]"
        );
        RecordList recList = ds.getRecordList("RecordListName1");
        Record rec = recList.createRecord();
        rec.setProperty("PropertyName1", "value1");
        rec.setProperty("PropertyName2", 100);
        rec.setProperty("PropertyName3", new String[]{"value1", "value2"});
        recList.addRecord(rec);
        rec = recList.createRecord();
        rec.setProperty("PropertyName1", "value2");
        rec.setProperty("PropertyName2", 200);
        rec.setProperty("PropertyName3", new String[]{"value1", "value2"});
        recList.addRecord(rec);
        
        DataSetJSONConverter converter = new DataSetJSONConverter();
        converter.setOutputPropertyNameOfHeader(false);
        InputStream is = converter.convertToStream(ds);
        
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        StringWriter sw = new StringWriter();
        int length = 0;
        char[] buf = new char[1024];
        while((length = isr.read(buf, 0, buf.length)) != -1){
            sw.write(buf, 0, length);
        }
        assertEquals(
            "{\"dataset1\":{\"schema\":{\"header\":{\"HeaderName1\":\":PropertyName1,java.lang.String\\n:PropertyName2,int\\n:PropertyName3,int[]\"},\"recordList\":{\"RecordListName1\":\":PropertyName1,java.lang.String\\n:PropertyName2,int\\n:PropertyName3,java.lang.String[]\"}},\"header\":{\"HeaderName1\":[\"value\",100,[1,2]]},\"recordList\":{\"RecordListName1\":[{\"PropertyName1\":\"value1\",\"PropertyName2\":100,\"PropertyName3\":[\"value1\",\"value2\"]},{\"PropertyName1\":\"value2\",\"PropertyName2\":200,\"PropertyName3\":[\"value1\",\"value2\"]}]}}}",
            sw.toString()
        );
    }
    
    public void test7() throws Exception{
        DataSet ds = new DataSet("dataset1");
        ds.setHeaderSchema(
            "HeaderName1",
            ":PropertyName1,java.lang.String"
                + '\n' + ":PropertyName2,int"
                + '\n' + ":PropertyName3,int[]"
        );
        Header header = ds.getHeader("HeaderName1");
        header.setProperty("PropertyName1", "value");
        header.setProperty("PropertyName2", 100);
        header.setProperty("PropertyName3", new int[]{1, 2});
        
        ds.setRecordListSchema(
            "RecordListName1",
            ":PropertyName1,java.lang.String"
                + '\n' + ":PropertyName2,int"
                + '\n' + ":PropertyName3,java.lang.String[]"
        );
        RecordList recList = ds.getRecordList("RecordListName1");
        Record rec = recList.createRecord();
        rec.setProperty("PropertyName1", "value1");
        rec.setProperty("PropertyName2", 100);
        rec.setProperty("PropertyName3", new String[]{"value1", "value2"});
        recList.addRecord(rec);
        rec = recList.createRecord();
        rec.setProperty("PropertyName1", "value2");
        rec.setProperty("PropertyName2", 200);
        rec.setProperty("PropertyName3", new String[]{"value1", "value2"});
        recList.addRecord(rec);
        
        DataSetJSONConverter converter = new DataSetJSONConverter();
        converter.setOutputPropertyNameOfRecordList(false);
        InputStream is = converter.convertToStream(ds);
        
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        StringWriter sw = new StringWriter();
        int length = 0;
        char[] buf = new char[1024];
        while((length = isr.read(buf, 0, buf.length)) != -1){
            sw.write(buf, 0, length);
        }
        assertEquals(
            "{\"dataset1\":{\"schema\":{\"header\":{\"HeaderName1\":\":PropertyName1,java.lang.String\\n:PropertyName2,int\\n:PropertyName3,int[]\"},\"recordList\":{\"RecordListName1\":\":PropertyName1,java.lang.String\\n:PropertyName2,int\\n:PropertyName3,java.lang.String[]\"}},\"header\":{\"HeaderName1\":{\"PropertyName1\":\"value\",\"PropertyName2\":100,\"PropertyName3\":[1,2]}},\"recordList\":{\"RecordListName1\":[[\"value1\",100,[\"value1\",\"value2\"]],[\"value2\",200,[\"value1\",\"value2\"]]]}}}",
            sw.toString()
        );
    }
    
    public void test8() throws Exception{
        
        InputStream is = new ByteArrayInputStream(
            "{\"dataset1\":{\"schema\":{\"header\":{\"HeaderName1\":\":PropertyName1,java.lang.String\\n:PropertyName2,int\\n:PropertyName3,int[]\"}},\"header\":{\"HeaderName1\":{\"PropertyName1\":\"value\",\"PropertyName2\":100,\"PropertyName3\":[1,2]}}}}".getBytes("UTF-8")
        );
        
        DataSetJSONConverter converter = new DataSetJSONConverter();
        DataSet ds = (DataSet)converter.convertToObject(is);
        
        assertEquals("dataset1", ds.getName());
        assertEquals(1, ds.getHeaderSize());
        
        Header header = ds.getHeader("HeaderName1");
        assertNotNull(header);
        assertEquals("value", header.getProperty("PropertyName1"));
        assertEquals(100, header.getIntProperty("PropertyName2"));
        assertNotNull(header.getProperty("PropertyName3"));
        assertEquals(int[].class, header.getProperty("PropertyName3").getClass());
        assertEquals(2, ((int[])header.getProperty("PropertyName3")).length);
        assertEquals(1, ((int[])header.getProperty("PropertyName3"))[0]);
        assertEquals(2, ((int[])header.getProperty("PropertyName3"))[1]);
    }
    
    public void test9() throws Exception{
        
        InputStream is = new ByteArrayInputStream(
            "{\"dataset1\":{\"schema\":{\"header\":{\"HeaderName1\":\":PropertyName1,java.lang.String\\n:PropertyName2,int\\n:PropertyName3,int[]\",\"HeaderName2\":\":PropertyName1,java.lang.String[]\\n:PropertyName2,java.lang.String\"}},\"header\":{\"HeaderName1\":{\"PropertyName1\":\"value\",\"PropertyName2\":100,\"PropertyName3\":[1,2]},\"HeaderName2\":{\"PropertyName1\":[\"value1\",\"value2\"],\"PropertyName2\":null}}}}".getBytes("UTF-8")
        );
        
        DataSetJSONConverter converter = new DataSetJSONConverter();
        DataSet ds = (DataSet)converter.convertToObject(is);
        
        assertEquals("dataset1", ds.getName());
        assertEquals(2, ds.getHeaderSize());
        
        Header header = ds.getHeader("HeaderName1");
        assertNotNull(header);
        assertEquals("value", header.getProperty("PropertyName1"));
        assertEquals(100, header.getIntProperty("PropertyName2"));
        assertNotNull(header.getProperty("PropertyName3"));
        assertEquals(int[].class, header.getProperty("PropertyName3").getClass());
        assertEquals(2, ((int[])header.getProperty("PropertyName3")).length);
        assertEquals(1, ((int[])header.getProperty("PropertyName3"))[0]);
        assertEquals(2, ((int[])header.getProperty("PropertyName3"))[1]);
        
        header = ds.getHeader("HeaderName2");
        assertNotNull(header);
        assertNotNull(header.getProperty("PropertyName1"));
        assertEquals(String[].class, header.getProperty("PropertyName1").getClass());
        assertEquals(2, ((String[])header.getProperty("PropertyName1")).length);
        assertEquals("value1", ((String[])header.getProperty("PropertyName1"))[0]);
        assertEquals("value2", ((String[])header.getProperty("PropertyName1"))[1]);
        assertNull(header.getProperty("PropertyName2"));
    }
    
    public void test10() throws Exception{
        
        InputStream is = new ByteArrayInputStream(
            "{\"dataset1\":{\"schema\":{\"recordList\":{\"RecordListName1\":\":PropertyName1,java.lang.String\\n:PropertyName2,int\\n:PropertyName3,java.lang.String[]\"}},\"recordList\":{\"RecordListName1\":[{\"PropertyName1\":\"value1\",\"PropertyName2\":100,\"PropertyName3\":[\"value1\",\"value2\"]},{\"PropertyName1\":\"value2\",\"PropertyName2\":200,\"PropertyName3\":[\"value1\",\"value2\"]}]}}}".getBytes("UTF-8")
        );
        
        DataSetJSONConverter converter = new DataSetJSONConverter();
        DataSet ds = (DataSet)converter.convertToObject(is);
        
        assertEquals("dataset1", ds.getName());
        assertEquals(1, ds.getRecordListSize());
        
        RecordList recordList = ds.getRecordList("RecordListName1");
        assertNotNull(recordList);
        assertEquals(2, recordList.size());
        
        Record record = recordList.getRecord(0);
        assertEquals("value1", record.getProperty("PropertyName1"));
        assertEquals(100, record.getIntProperty("PropertyName2"));
        assertNotNull(record.getProperty("PropertyName3"));
        assertEquals(String[].class, record.getProperty("PropertyName3").getClass());
        assertEquals(2, ((String[])record.getProperty("PropertyName3")).length);
        assertEquals("value1", ((String[])record.getProperty("PropertyName3"))[0]);
        assertEquals("value2", ((String[])record.getProperty("PropertyName3"))[1]);
        
        record = recordList.getRecord(1);
        assertEquals("value2", record.getProperty("PropertyName1"));
        assertEquals(200, record.getIntProperty("PropertyName2"));
        assertNotNull(record.getProperty("PropertyName3"));
        assertEquals(String[].class, record.getProperty("PropertyName3").getClass());
        assertEquals(2, ((String[])record.getProperty("PropertyName3")).length);
        assertEquals("value1", ((String[])record.getProperty("PropertyName3"))[0]);
        assertEquals("value2", ((String[])record.getProperty("PropertyName3"))[1]);
    }
    
    public void test11() throws Exception{
        
        InputStream is = new ByteArrayInputStream(
            "{\"dataset1\":{\"schema\":{\"recordList\":{\"RecordListName1\":\":PropertyName1,java.lang.String\\nLIST:PropertyName2,NestedRecordListName1\"},\"nestedRecordList\":{\"NestedRecordListName1\":\":PropertyName1,java.lang.String\\n:PropertyName2,int\\n:PropertyName3,java.lang.String[]\"}},\"recordList\":{\"RecordListName1\":[{\"PropertyName1\":\"value\",\"PropertyName2\":[{\"PropertyName1\":\"value\",\"PropertyName2\":100,\"PropertyName3\":[\"value1\",\"value2\"]}]}]}}}".getBytes("UTF-8")
        );
        
        DataSetJSONConverter converter = new DataSetJSONConverter();
        DataSet ds = (DataSet)converter.convertToObject(is);
        
        assertEquals("dataset1", ds.getName());
        assertEquals(1, ds.getRecordListSize());
        
        RecordList recordList = ds.getRecordList("RecordListName1");
        assertNotNull(recordList);
        assertEquals(1, recordList.size());
        
        Record record = recordList.getRecord(0);
        assertEquals("value", record.getProperty("PropertyName1"));
        assertNotNull(record.getProperty("PropertyName2"));
        assertTrue(record.getProperty("PropertyName2") instanceof RecordList);
        
        recordList = (RecordList)record.getProperty("PropertyName2");
        assertEquals(1, recordList.size());
        
        record = recordList.getRecord(0);
        assertEquals("value", record.getProperty("PropertyName1"));
        assertEquals(100, record.getIntProperty("PropertyName2"));
        assertNotNull(record.getProperty("PropertyName3"));
        assertEquals(String[].class, record.getProperty("PropertyName3").getClass());
        assertEquals(2, ((String[])record.getProperty("PropertyName3")).length);
        assertEquals("value1", ((String[])record.getProperty("PropertyName3"))[0]);
        assertEquals("value2", ((String[])record.getProperty("PropertyName3"))[1]);
    }
    
    public void test12() throws Exception{
        
        InputStream is = new ByteArrayInputStream(
            "{\"dataset1\":{\"header\":{\"HeaderName1\":{\"PropertyName1\":\"value\",\"PropertyName2\":100,\"PropertyName3\":[1,2]}}}}".getBytes("UTF-8")
        );
        
        DataSetJSONConverter converter = new DataSetJSONConverter();
        DataSet ds = new DataSet("dataset1");
        ds.setHeaderSchema(
            "HeaderName1",
            ":PropertyName1,java.lang.String"
                + '\n' + ":PropertyName2,int"
                + '\n' + ":PropertyName3,int[]"
        );
        ds = (DataSet)converter.convertToObject(is, ds);
        
        assertEquals("dataset1", ds.getName());
        assertEquals(1, ds.getHeaderSize());
        
        Header header = ds.getHeader("HeaderName1");
        assertNotNull(header);
        assertEquals("value", header.getProperty("PropertyName1"));
        assertEquals(100, header.getIntProperty("PropertyName2"));
        assertNotNull(header.getProperty("PropertyName3"));
        assertEquals(int[].class, header.getProperty("PropertyName3").getClass());
        assertEquals(2, ((int[])header.getProperty("PropertyName3")).length);
        assertEquals(1, ((int[])header.getProperty("PropertyName3"))[0]);
        assertEquals(2, ((int[])header.getProperty("PropertyName3"))[1]);
    }
    
    public void test13() throws Exception{
        
        InputStream is = new ByteArrayInputStream(
            "{\"dataset1\":{\"schema\":{\"header\":{\"HeaderName1\":\":PropertyName1,java.lang.String\\n:PropertyName2,int\\n:PropertyName3,int[]\"}},\"header\":{\"HeaderName1\":[\"value\",100,[1,2]]}}}".getBytes("UTF-8")
        );
        
        DataSetJSONConverter converter = new DataSetJSONConverter();
        DataSet ds = (DataSet)converter.convertToObject(is);
        
        assertEquals("dataset1", ds.getName());
        assertEquals(1, ds.getHeaderSize());
        
        Header header = ds.getHeader("HeaderName1");
        assertNotNull(header);
        assertEquals("value", header.getProperty("PropertyName1"));
        assertEquals(100, header.getIntProperty("PropertyName2"));
        assertNotNull(header.getProperty("PropertyName3"));
        assertEquals(int[].class, header.getProperty("PropertyName3").getClass());
        assertEquals(2, ((int[])header.getProperty("PropertyName3")).length);
        assertEquals(1, ((int[])header.getProperty("PropertyName3"))[0]);
        assertEquals(2, ((int[])header.getProperty("PropertyName3"))[1]);
    }
    
    public void test14() throws Exception{
        
        InputStream is = new ByteArrayInputStream(
            "{\"dataset1\":{\"schema\":{\"header\":{\"HeaderName1\":\":PropertyName1,java.lang.String\\n:PropertyName2,int\\n:PropertyName3,int[]\",\"HeaderName2\":\":PropertyName1,java.lang.String[]\\n:PropertyName2,java.lang.String\"}},\"header\":{\"HeaderName1\":[\"value\",100,[1,2]],\"HeaderName2\":[[\"value1\",\"value2\"],null]}}}".getBytes("UTF-8")
        );
        
        DataSetJSONConverter converter = new DataSetJSONConverter();
        DataSet ds = (DataSet)converter.convertToObject(is);
        
        assertEquals("dataset1", ds.getName());
        assertEquals(2, ds.getHeaderSize());
        
        Header header = ds.getHeader("HeaderName1");
        assertNotNull(header);
        assertEquals("value", header.getProperty("PropertyName1"));
        assertEquals(100, header.getIntProperty("PropertyName2"));
        assertNotNull(header.getProperty("PropertyName3"));
        assertEquals(int[].class, header.getProperty("PropertyName3").getClass());
        assertEquals(2, ((int[])header.getProperty("PropertyName3")).length);
        assertEquals(1, ((int[])header.getProperty("PropertyName3"))[0]);
        assertEquals(2, ((int[])header.getProperty("PropertyName3"))[1]);
        
        header = ds.getHeader("HeaderName2");
        assertNotNull(header);
        assertNotNull(header.getProperty("PropertyName1"));
        assertEquals(String[].class, header.getProperty("PropertyName1").getClass());
        assertEquals(2, ((String[])header.getProperty("PropertyName1")).length);
        assertEquals("value1", ((String[])header.getProperty("PropertyName1"))[0]);
        assertEquals("value2", ((String[])header.getProperty("PropertyName1"))[1]);
        assertNull(header.getProperty("PropertyName2"));
    }
    
    public void test15() throws Exception{
        
        InputStream is = new ByteArrayInputStream(
            "{\"dataset1\":{\"schema\":{\"recordList\":{\"RecordListName1\":\":PropertyName1,java.lang.String\\n:PropertyName2,int\\n:PropertyName3,java.lang.String[]\"}},\"recordList\":{\"RecordListName1\":[[\"value1\",100,[\"value1\",\"value2\"]],[\"value2\",200,[\"value1\",\"value2\"]]]}}}".getBytes("UTF-8")
        );
        
        DataSetJSONConverter converter = new DataSetJSONConverter();
        DataSet ds = (DataSet)converter.convertToObject(is);
        
        assertEquals("dataset1", ds.getName());
        assertEquals(1, ds.getRecordListSize());
        
        RecordList recordList = ds.getRecordList("RecordListName1");
        assertNotNull(recordList);
        assertEquals(2, recordList.size());
        
        Record record = recordList.getRecord(0);
        assertEquals("value1", record.getProperty("PropertyName1"));
        assertEquals(100, record.getIntProperty("PropertyName2"));
        assertNotNull(record.getProperty("PropertyName3"));
        assertEquals(String[].class, record.getProperty("PropertyName3").getClass());
        assertEquals(2, ((String[])record.getProperty("PropertyName3")).length);
        assertEquals("value1", ((String[])record.getProperty("PropertyName3"))[0]);
        assertEquals("value2", ((String[])record.getProperty("PropertyName3"))[1]);
        
        record = recordList.getRecord(1);
        assertEquals("value2", record.getProperty("PropertyName1"));
        assertEquals(200, record.getIntProperty("PropertyName2"));
        assertNotNull(record.getProperty("PropertyName3"));
        assertEquals(String[].class, record.getProperty("PropertyName3").getClass());
        assertEquals(2, ((String[])record.getProperty("PropertyName3")).length);
        assertEquals("value1", ((String[])record.getProperty("PropertyName3"))[0]);
        assertEquals("value2", ((String[])record.getProperty("PropertyName3"))[1]);
    }
    
    public void test16() throws Exception{
        
        InputStream is = new ByteArrayInputStream(
            "{\"dataset1\":{\"schema\":{\"recordList\":{\"RecordListName1\":\":PropertyName1,java.lang.String\\nLIST:PropertyName2,NestedRecordListName1\"},\"nestedRecordList\":{\"NestedRecordListName1\":\":PropertyName1,java.lang.String\\n:PropertyName2,int\\n:PropertyName3,java.lang.String[]\"}},\"recordList\":{\"RecordListName1\":[[\"value\",[[\"value\",100,[\"value1\",\"value2\"]]]]]}}}".getBytes("UTF-8")
        );
        
        DataSetJSONConverter converter = new DataSetJSONConverter();
        DataSet ds = (DataSet)converter.convertToObject(is);
        
        assertEquals("dataset1", ds.getName());
        assertEquals(1, ds.getRecordListSize());
        
        RecordList recordList = ds.getRecordList("RecordListName1");
        assertNotNull(recordList);
        assertEquals(1, recordList.size());
        
        Record record = recordList.getRecord(0);
        assertEquals("value", record.getProperty("PropertyName1"));
        assertNotNull(record.getProperty("PropertyName2"));
        assertTrue(record.getProperty("PropertyName2") instanceof RecordList);
        
        recordList = (RecordList)record.getProperty("PropertyName2");
        assertEquals(1, recordList.size());
        
        record = recordList.getRecord(0);
        assertEquals("value", record.getProperty("PropertyName1"));
        assertEquals(100, record.getIntProperty("PropertyName2"));
        assertNotNull(record.getProperty("PropertyName3"));
        assertEquals(String[].class, record.getProperty("PropertyName3").getClass());
        assertEquals(2, ((String[])record.getProperty("PropertyName3")).length);
        assertEquals("value1", ((String[])record.getProperty("PropertyName3"))[0]);
        assertEquals("value2", ((String[])record.getProperty("PropertyName3"))[1]);
    }
    
    public void test17() throws Exception{
        
        InputStream is = new ByteArrayInputStream(
            "{\"dataset1\":{\"header\":{\"HeaderName1\":{\"PropertyName1\":\"value\",\"PropertyName2\":100,\"PropertyName3\":[1,2],\"PropertyName4\":123.4},\"HeaderName2\":{\"PropertyName1\":[\"value1\",\"value2\"],\"PropertyName2\":null}}}}".getBytes("UTF-8")
        );
        
        DataSetJSONConverter converter = new DataSetJSONConverter();
        converter.setIgnoreUnknownElement(true);
        DataSet ds = new DataSet("dataset1");
        ds.setHeaderSchema(
            "HeaderName1",
            ":PropertyName1,java.lang.String"
                + '\n' + ":PropertyName2,int"
                + '\n' + ":PropertyName3,int[]"
        );
        ds = (DataSet)converter.convertToObject(is, ds);
        
        assertEquals("dataset1", ds.getName());
        assertEquals(1, ds.getHeaderSize());
        
        Header header = ds.getHeader("HeaderName1");
        assertNotNull(header);
        assertEquals("value", header.getProperty("PropertyName1"));
        assertEquals(100, header.getIntProperty("PropertyName2"));
        assertNotNull(header.getProperty("PropertyName3"));
        assertEquals(int[].class, header.getProperty("PropertyName3").getClass());
        assertEquals(2, ((int[])header.getProperty("PropertyName3")).length);
        assertEquals(1, ((int[])header.getProperty("PropertyName3"))[0]);
        assertEquals(2, ((int[])header.getProperty("PropertyName3"))[1]);
        try{
            header.getProperty("PropertyName4");
            fail();
        }catch(PropertyGetException e){}
        
        header = ds.getHeader("HeaderName2");
        assertNull(header);
    }
    
    public void test18() throws Exception{
        
        InputStream is = new ByteArrayInputStream(
            "{\"dataset1\":{\"recordList\":{\"RecordListName1\":[{\"PropertyName1\":\"value1\",\"PropertyName2\":100,\"PropertyName3\":[\"value1\",\"value2\"],\"PropertyName4\":123.4}],\"RecordListName2\":[{\"PropertyName1\":\"value1\"}]}}}".getBytes("UTF-8")
        );
        
        DataSetJSONConverter converter = new DataSetJSONConverter();
        converter.setIgnoreUnknownElement(true);
        DataSet ds = new DataSet("dataset1");
        ds.setRecordListSchema(
            "RecordListName1",
            ":PropertyName1,java.lang.String"
                + '\n' + ":PropertyName2,int"
                + '\n' + ":PropertyName3,java.lang.String[]"
        );
        ds = (DataSet)converter.convertToObject(is, ds);
        
        assertEquals("dataset1", ds.getName());
        assertEquals(1, ds.getRecordListSize());
        
        RecordList recordList = ds.getRecordList("RecordListName1");
        assertNotNull(recordList);
        assertEquals(1, recordList.size());
        
        Record record = recordList.getRecord(0);
        assertEquals("value1", record.getProperty("PropertyName1"));
        assertEquals(100, record.getIntProperty("PropertyName2"));
        assertNotNull(record.getProperty("PropertyName3"));
        assertEquals(String[].class, record.getProperty("PropertyName3").getClass());
        assertEquals(2, ((String[])record.getProperty("PropertyName3")).length);
        assertEquals("value1", ((String[])record.getProperty("PropertyName3"))[0]);
        assertEquals("value2", ((String[])record.getProperty("PropertyName3"))[1]);
        try{
            record.getProperty("PropertyName4");
            fail();
        }catch(PropertyGetException e){}
        
        recordList = ds.getRecordList("RecordListName2");
        assertNull(recordList);
    }
}