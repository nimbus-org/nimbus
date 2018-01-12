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

public class CSVWriterTest extends TestCase{
    
    public CSVWriterTest(String arg0){
        super(arg0);
        File file = new File("target/temp/jp/ossc/nimbus/io");
        if(!file.exists()){
            file.mkdirs();
        }
    }
    
    public static void main(String[] args){
        junit.textui.TestRunner.run(CSVWriterTest.class);
    }
    
    public void test1() throws Exception{
        try{
            CSVWriter writer = new CSVWriter(
                new FileWriter("target/temp/jp/ossc/nimbus/io/csv2.txt")
            );
            try{
                writer.writeElement("1a");
                writer.writeElement("1b");
                writer.writeElement("1c");
                
                writer.newLine();
                writer.writeElement("2a");
                writer.writeElement("2b");
                writer.writeElement("2c");
                writer.writeElement("2d");
                
                writer.newLine();
                writer.newLine();
                
                writer.newLine();
                writer.writeElement("3a");
                writer.writeElement("3,b");
                writer.writeElement("3c");
            }finally{
                writer.close();
            }
            
            CSVReader reader = new CSVReader(
                new FileReader("target/temp/jp/ossc/nimbus/io/csv2.txt")
            );
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
                assertEquals(0, csvList.size());
                
                csvList = reader.readCSVLineList(csvList);
                assertNotNull(csvList);
                assertEquals(0, csvList.size());
                
                csvList = reader.readCSVLineList(csvList);
                assertNotNull(csvList);
                assertEquals(3, csvList.size());
                assertEquals("3a", csvList.get(0));
                assertEquals("3,b", csvList.get(1));
                assertEquals("3c", csvList.get(2));
                
                csvList = reader.readCSVLineList(csvList);
                assertNull(csvList);
            }finally{
                reader.close();
            }
        }finally{
            File file = new File("target/temp/jp/ossc/nimbus/io/csv2.txt");
            if(file.exists()){
                file.delete();
            }
        }
    }
    
    public void test2() throws Exception{
        try{
            CSVWriter writer = new CSVWriter(
                new FileWriter("target/temp/jp/ossc/nimbus/io/csv2.txt")
            );
            try{
                writer.writeCSV(new String[]{"1a", "1b", "1c"});
                
                List csvElements = new ArrayList();
                csvElements.add("2a");
                csvElements.add("2b");
                csvElements.add("2c");
                csvElements.add("2d");
                writer.writeCSV(csvElements);
                
                writer.newLine();
                writer.newLine();
                
                csvElements.clear();
                csvElements.add("3a");
                csvElements.add("3,b");
                csvElements.add("3c");
                writer.writeCSV(csvElements);
            }finally{
                writer.close();
            }
            
            CSVReader reader = new CSVReader(
                new FileReader("target/temp/jp/ossc/nimbus/io/csv2.txt")
            );
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
                assertEquals(0, csvList.size());
                
                csvList = reader.readCSVLineList(csvList);
                assertNotNull(csvList);
                assertEquals(0, csvList.size());
                
                csvList = reader.readCSVLineList(csvList);
                assertNotNull(csvList);
                assertEquals(3, csvList.size());
                assertEquals("3a", csvList.get(0));
                assertEquals("3,b", csvList.get(1));
                assertEquals("3c", csvList.get(2));
                
                csvList = reader.readCSVLineList(csvList);
                assertNull(csvList);
            }finally{
                reader.close();
            }
        }finally{
            File file = new File("target/temp/jp/ossc/nimbus/io/csv2.txt");
            if(file.exists()){
                file.delete();
            }
        }
    }
    
    public void test3() throws Exception{
        try{
            CSVWriter writer = new CSVWriter(
                new FileWriter("target/temp/jp/ossc/nimbus/io/csv2.txt")
            );
            writer.setEnclose(true);
            try{
                writer.writeCSV(new String[]{"1a", "1b", "1c"});
                
                List csvElements = new ArrayList();
                csvElements.add("2a");
                csvElements.add("2b");
                csvElements.add("2c");
                csvElements.add("2d");
                writer.writeCSV(csvElements);
                
                csvElements.clear();
                csvElements.add("3a");
                csvElements.add("3,b");
                csvElements.add("3c");
                writer.writeCSV(csvElements);
                
                csvElements.clear();
                csvElements.add("4a,4b");
                csvElements.add("4\nc");
                csvElements.add("4d");
                writer.writeCSV(csvElements);
            }finally{
                writer.close();
            }
            
            CSVReader reader = new CSVReader(
                new FileReader("target/temp/jp/ossc/nimbus/io/csv2.txt")
            );
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
                assertEquals(3, csvList.size());
                assertEquals("4a,4b", csvList.get(0));
                assertEquals("4" + System.getProperty("line.separator") + "c", csvList.get(1));
                assertEquals("4d", csvList.get(2));
                
                csvList = reader.readCSVLineList(csvList);
                assertNull(csvList);
            }finally{
                reader.close();
            }
        }finally{
            File file = new File("target/temp/jp/ossc/nimbus/io/csv2.txt");
            if(file.exists()){
                file.delete();
            }
        }
    }
}