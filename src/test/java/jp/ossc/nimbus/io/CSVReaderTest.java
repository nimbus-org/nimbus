/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 * 
 * Copyright 2003 The Nimbus Project. All rights reserved.
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
package jp.ossc.nimbus.io;

import java.io.*;
import java.net.*;
import java.util.*;

import junit.framework.TestCase;

public class CSVReaderTest extends TestCase{
    
    public CSVReaderTest(String arg0){
        super(arg0);
    }
    
    public static void main(String[] args){
        junit.textui.TestRunner.run(CSVReaderTest.class);
    }
    
    public void test1() throws Exception{
        CSVReader reader = new CSVReader(
            new FileReader("src/test/resources/jp/ossc/nimbus/io/csv1.txt")
        );
        try{
            String[] csvArray = reader.readCSVLine();
            assertNotNull(csvArray);
            assertEquals(3, csvArray.length);
            assertEquals("1a", csvArray[0]);
            assertEquals("1b", csvArray[1]);
            assertEquals("1c", csvArray[2]);
            
            List csvList = reader.readCSVLineList();
            assertNotNull(csvList);
            assertEquals(4, csvList.size());
            assertEquals("2a", csvList.get(0));
            assertEquals("2b", csvList.get(1));
            assertEquals("2c", csvList.get(2));
            assertEquals("2d", csvList.get(3));
            
            List csvList2 = reader.readCSVLineList();
            assertNotNull(csvList2);
            assertEquals(0, csvList2.size());
            assertFalse(csvList == csvList2);
            
            csvList2 = reader.readCSVLineList(csvList);
            assertNotNull(csvList2);
            assertEquals(0, csvList2.size());
            assertTrue(csvList == csvList2);
            
            csvList = reader.readCSVLineList(csvList);
            assertNotNull(csvList);
            assertEquals(3, csvList.size());
            assertEquals("3a", csvList.get(0));
            assertEquals("3,b", csvList.get(1));
            assertEquals("3c", csvList.get(2));
            
            csvList = reader.readCSVLineList(csvList);
            assertNotNull(csvList);
            assertEquals(3, csvList.size());
            assertEquals("4a", csvList.get(0));
            assertEquals("4b", csvList.get(1));
            assertEquals("", csvList.get(2));
            
            csvList = reader.readCSVLineList(csvList);
            assertNotNull(csvList);
            assertEquals(3, csvList.size());
            assertEquals("\"5a", csvList.get(0));
            assertEquals("5b\"", csvList.get(1));
            assertEquals("\"5", csvList.get(2));
            
            csvList = reader.readCSVLineList(csvList);
            assertNotNull(csvList);
            assertEquals(2, csvList.size());
            assertEquals("c\"", csvList.get(0));
            assertEquals("5d", csvList.get(1));
            
            csvList = reader.readCSVLineList(csvList);
            assertNull(csvList);
        }finally{
            reader.close();
        }
    }
    
    public void test2() throws Exception{
        CSVReader reader = new CSVReader(
            new FileReader("src/test/resources/jp/ossc/nimbus/io/csv1.txt")
        );
        reader.setIgnoreEmptyLine(true);
        reader.setIgnoreLineEndSeparator(true);
        reader.setEnclosed(true);
        try{
            List csvList = reader.readCSVLineList();
            assertNotNull(csvList);
            assertEquals(3, csvList.size());
            assertEquals("1a", csvList.get(0));
            assertEquals("1b", csvList.get(1));
            assertEquals("1c", csvList.get(2));
            
            csvList = reader.readCSVLineList(csvList);
            assertNotNull(csvList);
            assertEquals(4, csvList.size());
            assertEquals("2a", csvList.get(0));
            assertEquals("2b", csvList.get(1));
            assertEquals("2c", csvList.get(2));
            assertEquals("2d", csvList.get(3));
            
            csvList = reader.readCSVLineList(csvList);
            assertNotNull(csvList);
            assertEquals(3, csvList.size());
            assertEquals("3a", csvList.get(0));
            assertEquals("3,b", csvList.get(1));
            assertEquals("3c", csvList.get(2));
            
            csvList = reader.readCSVLineList(csvList);
            assertNotNull(csvList);
            assertEquals(2, csvList.size());
            assertEquals("4a", csvList.get(0));
            assertEquals("4b", csvList.get(1));
            
            csvList = reader.readCSVLineList(csvList);
            assertNotNull(csvList);
            assertEquals(3, csvList.size());
            assertEquals("5a,5b", csvList.get(0));
            assertEquals("5" + System.getProperty("line.separator") + "c", csvList.get(1));
            assertEquals("5d", csvList.get(2));
            
            csvList = reader.readCSVLineList(csvList);
            assertNull(csvList);
        }finally{
            reader.close();
        }
    }
    
    public void test3() throws Exception{
        CSVReader reader = new CSVReader(
            new FileReader("src/test/resources/jp/ossc/nimbus/io/csv1.txt")
        );
        reader.setIgnoreLineEndSeparator(true);
        reader.setEnclosed(true);
        try{
            final CSVReader.CSVIterator iterator = reader.iterator();
            assertTrue(iterator.hasNext());
            CSVReader.CSVElements csvElements = iterator.nextElements();
            assertNotNull(csvElements);
            assertEquals(3, csvElements.size());
            assertEquals("1a", csvElements.get(0));
            assertEquals("1b", csvElements.get(1));
            assertEquals("1c", csvElements.get(2));
            
            assertTrue(iterator.hasNext());
            csvElements = iterator.nextElements();
            assertNotNull(csvElements);
            assertEquals(4, csvElements.size());
            assertEquals("2a", csvElements.getString(0));
            assertEquals("2b", csvElements.getString(1));
            assertEquals("2c", csvElements.getString(2));
            assertEquals("2d", csvElements.getString(3));
            
            reader.skipCSVLine(2);
            
            assertTrue(iterator.hasNext());
            csvElements = iterator.nextElements();
            assertNotNull(csvElements);
            assertEquals(3, csvElements.size());
            assertEquals("3a", csvElements.get(0));
            assertEquals("3,b", csvElements.get(1));
            assertEquals("3c", csvElements.get(2));
            
            assertTrue(iterator.hasNext());
            csvElements = iterator.nextElements();
            assertNotNull(csvElements);
            assertEquals(2, csvElements.size());
            assertEquals("4a", csvElements.get(0));
            assertEquals("4b", csvElements.get(1));
            
            assertTrue(iterator.hasNext());
            csvElements = iterator.nextElements();
            assertNotNull(csvElements);
            assertEquals(3, csvElements.size());
            assertEquals("5a,5b", csvElements.get(0));
            assertEquals("5" + System.getProperty("line.separator") + "c", csvElements.get(1));
            assertEquals("5d", csvElements.get(2));
            
            assertFalse(iterator.hasNext());
        }finally{
            reader.close();
        }
    }
}