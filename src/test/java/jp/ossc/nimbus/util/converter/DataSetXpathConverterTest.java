/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 * 
 * Copyright 2008 The Nimbus Project. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE NIMBUS PROJECT ``AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE NIMBUS PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of the Nimbus Project.
 */
package jp.ossc.nimbus.util.converter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import jp.ossc.nimbus.beans.dataset.DataSet;
import jp.ossc.nimbus.beans.dataset.Header;
import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordList;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * {@link DataSetXpathConverter}テストケース。
 * <p>
 *   必須環境：
 *   <ul>
 *      <li>JDK 5.0 以降</li>
 *      <li>
 *          または、JDK 1.4 + 以下のendorsedモジュール：
 *          <ul>
 *              <li>Xerces 2.9</li>
 *              <li>Xalan 2.7</li>
 *              <li>Xalan Serializer 2.7</li>
 *          </ul>
 *          この場合、テストランナのJVM起動オプションに-Djava.endorsed.dirsを設定する必要あり
 *          -Djava.endorsed.dirs=lib/endorsed
 *      </li>
 *   </ul>
 * </p>
 * @author T.Okada
 */
public class DataSetXpathConverterTest extends TestCase {

    private static final String TEST_DATA_PATH = "jp/ossc/nimbus/util/converter/DataSetXpathConverterTest.xml";
    
    public void testConvertToObject() {
        DataSet inputDataSet = new DataSetXPathConverterTestDataSet();
        
        // 初期データ設定
        Header inputHeader = inputDataSet.getHeader();
        inputHeader.setProperty(DataSetXPathConverterTestDataSet.PROPERTY0, "PROP0");
        inputHeader.setParseProperty(DataSetXPathConverterTestDataSet.PROPERTY1, "PROP1");
        
        BindingStreamConverter converter = new DataSetXpathConverter();
        DataSetXPathConverterTestDataSet dataSet = (DataSetXPathConverterTestDataSet)converter.convertToObject(createTestData(), inputDataSet);
        
        Header header = dataSet.getHeader();
        RecordList recordList = dataSet.getRecordList();
        
        // 初期データ取得
        Assert.assertEquals("PROP0", header.get(DataSetXPathConverterTestDataSet.PROPERTY0));
        // 属性取得
        Assert.assertEquals("ATTR", header.get(DataSetXPathConverterTestDataSet.PROPERTY1));
        // 属性値による指定ノード取得
        Assert.assertEquals("テキスト2-3", header.get(DataSetXPathConverterTestDataSet.PROPERTY2));
        // ノードインデックスによる指定ノード取得
        Assert.assertEquals("ATTR1", header.get(DataSetXPathConverterTestDataSet.PROPERTY3));
        
        // 子ノード取得
        for(int i=0; i<recordList.size(); i++) {
            Record record = (Record)recordList.get(i);
            Assert.assertEquals("テキスト"+(i+1)+"-1", record.get(DataSetXPathConverterTestDataSet.PROPERTY4));
            Assert.assertEquals("テキスト"+(i+1)+"-2", record.get(DataSetXPathConverterTestDataSet.PROPERTY5));
            Assert.assertEquals("テキスト"+(i+1)+"-3", record.get(DataSetXPathConverterTestDataSet.PROPERTY6));
        }
    }
    
    public void testConvertToObject_Error() {
        InputStream inputStream = createTestData();
        BindingStreamConverter converter = new DataSetXpathConverter();
        
        Exception exception = null;
        
        try {
            converter.convertToObject(null, new DataSetXPathConverterTestDataSet());
        } catch (IllegalArgumentException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
        
        try {
            converter.convertToObject(null, null);
        } catch (ConvertException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
        
        try {
            converter.convertToObject(inputStream, new Object());
        } catch (ConvertException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
    }
    
    private InputStream createTestData() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(TEST_DATA_PATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        return inputStream;
    }

}
