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
package jp.ossc.nimbus.core;

import java.io.*;
import java.net.*;
import java.util.*;

import junit.framework.TestCase;

//import jp.ossc.nimbus.service.message.MessageRecordFactory;
//import jp.ossc.nimbus.service.log.Logger;
import jp.ossc.nimbus.service.repository.Repository;

public class ServiceManagerFactoryTest extends TestCase{
   
    public ServiceManagerFactoryTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(ServiceManagerFactoryTest.class);
    }
    
    /**
     * クラスパス上のデフォルトサービス定義ファイルのロードとアンロードのテスト。<p>
     * 条件：
     * <ul>
     *   <li>デフォルト名のサービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()を呼び出してServiceManagerが取得できる。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service0"のサービスが取得できる。</li>
     *   <li>ServiceManagerFactory#unloadManager()の呼び出し後は、ServiceManager#getService(String)を呼び出してサービス名"Service0"のサービスが取得できない。</li>
     * </ul>
     */
    public void testLoadOnClassPath1() throws Exception {
        File def = null;
        try{
            def = TestUtility.copyOnClassPath("nimbus-service.xml");
            assertTrue(ServiceManagerFactory.loadManager());
            assertTrue(ServiceManagerFactory.checkLoadManagerCompleted());
            final ServiceManager manager = ServiceManagerFactory.findManager();
            assertNotNull(manager);
            try{
                manager.getService("Service0");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            ServiceManagerFactory.unloadManager();
            try{
                manager.getService("Service0");
                fail("Service0 was not unloaded.");
            }catch(ServiceNotFoundException e){
            }
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * 存在しないクラスパス上のデフォルトサービス定義ファイルのロードとアンロードのテスト。<p>
     * 条件：
     * <ul>
     *   <li>デフォルト名のサービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置かない。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()がfalseを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()の戻り値がnull。</li>
     *   <li>ServiceManagerFactory#unloadManager()の呼び出しが行える。</li>
     * </ul>
     */
    public void testLoadOnClassPath2() throws Exception {
        try{
            assertFalse(ServiceManagerFactory.loadManager());
            assertTrue(ServiceManagerFactory.checkLoadManagerCompleted());
            final ServiceManager manager = ServiceManagerFactory.findManager();
            assertNull(manager);
        }finally{
            ServiceManagerFactory.unloadManager();
        }
    }
    
    /**
     * クラスパス上の任意のサービス定義ファイルのロードとアンロードのテスト。<p>
     * 条件：
     * <ul>
     *   <li>デフォルト名ではないサービス定義ファイル<a href="resources/nimbus-service1.xml">"nimbus-service1.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にデフォルト名ではないサービス定義ファイル"nimbus-service1.xml"を指定して呼び出し、戻り値でtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()を呼び出してServiceManagerが取得できる。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service1"のサービスが取得できる。</li>
     *   <li>ServiceManagerFactory#unloadManager(String)の呼び出し後は、ServiceManager#getService(String)を呼び出してサービス名"Service1"のサービスが取得できない。</li>
     * </ul>
     */
    public void testLoadOnClassPath3() throws Exception {
        File def = null;
        try{
            def = TestUtility.copyOnClassPath("nimbus-service1.xml");
            assertTrue(
                ServiceManagerFactory.loadManager("nimbus-service1.xml")
            );
            assertTrue(ServiceManagerFactory.checkLoadManagerCompleted());
            final ServiceManager manager = ServiceManagerFactory.findManager();
            assertNotNull(manager);
            try{
                manager.getService("Service1");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            ServiceManagerFactory.unloadManager("nimbus-service1.xml");
            try{
                manager.getService("Service1");
                fail();
            }catch(ServiceNotFoundException e){
            }
        }finally{
            ServiceManagerFactory.unloadManager("nimbus-service1.xml");
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * クラスパス上の任意の存在しないサービス定義ファイルのロードとアンロードのテスト。<p>
     * 条件：
     * <ul>
     *   <li>デフォルト名ではないサービス定義ファイル<a href="resources/nimbus-service1.xml">"nimbus-service1.xml"</a>をクラスパス上に置かない。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数に存在しないサービス定義ファイル"nimbus-service1.xml"を指定して呼び出し、戻り値でfalseを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()を呼び出してServiceManagerが取得できない。</li>
     *   <li>ServiceManagerFactory#unloadManager(String)の呼び出しができる。</li>
     * </ul>
     */
    public void testLoadOnClassPath4() throws Exception {
        try{
            assertFalse(
                ServiceManagerFactory.loadManager("nimbus-service10.xml")
            );
            assertTrue(ServiceManagerFactory.checkLoadManagerCompleted());
            final ServiceManager manager = ServiceManagerFactory.findManager();
            assertNull(manager);
        }finally{
            ServiceManagerFactory.unloadManager("nimbus-service10.xml");
        }
    }
    
    /**
     * DTDを使った正しいサービス定義ファイルの検証のテスト。<p>
     * 条件：
     * <ul>
     *   <li>デフォルト名のサービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String, boolean, boolean)の引数にデフォルト名のサービス定義ファイル"nimbus-service.xml"、false、trueを指定して、呼び出した戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager(String)で、"Nimbus"を指定して呼び出し、ServiceManagerが取得できる。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service0"のサービスが取得できる。</li>
     *   <li>ServiceManagerFactory#unloadManager(String)の呼び出し後は、ServiceManager#getService(String)を呼び出してサービス名"Service0"のサービスが取得できない。</li>
     * </ul>
     */
    public void testLoadWithValidate1() throws Exception{
        File def = null;
        try{
            def = TestUtility.copyOnClassPath("nimbus-service.xml");
            assertTrue(
                ServiceManagerFactory.loadManager(
                    "nimbus-service.xml",
                    false,
                    true
                )
            );
            assertTrue(ServiceManagerFactory.checkLoadManagerCompleted());
            final ServiceManager manager
                 = ServiceManagerFactory.findManager(ServiceManager.DEFAULT_NAME);
            assertNotNull(manager);
            try{
                manager.getService("Service0");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            ServiceManagerFactory.unloadManager();
            try{
                manager.getService("Service0");
                fail("Service0 was not unloaded.");
            }catch(ServiceNotFoundException e){
            }
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * DTDを使った誤ったサービス定義ファイルの検証のテスト。<p>
     * 条件：
     * <ul>
     *   <li>デフォルト名のサービス定義ファイル<a href="resources/nimbus-service11.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String, boolean, boolean)の引数にデフォルト名のサービス定義ファイル"nimbus-service.xml"、false、trueを指定して、呼び出した戻り値がfalseを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()の戻り値がnull。</li>
     *   <li>ServiceManagerFactory#unloadManager()の呼び出しが行える。</li>
     * </ul>
     */
    public void testLoadWithValidate2() throws Exception{
        File def = null;
        try{
            def = TestUtility.copyOnClassPath(
                "nimbus-service11.xml",
                "nimbus-service.xml"
            );
            assertFalse(
                ServiceManagerFactory.loadManager(
                    "nimbus-service.xml",
                    false,
                    true
                )
            );
            assertTrue(ServiceManagerFactory.checkLoadManagerCompleted());
            final ServiceManager manager = ServiceManagerFactory.findManager();
            assertNull(manager);
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * DTDを使わない誤ったサービス定義ファイルの検証のテスト。誤った要素は無視される。<p>
     * 条件：
     * <ul>
     *   <li>デフォルト名のサービス定義ファイル<a href="resources/nimbus-service11.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String, boolean, boolean)の引数にデフォルト名のサービス定義ファイル"nimbus-service.xml"、false、falseを指定して、呼び出した戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()を呼び出し、ServiceManagerが取得できる。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service0"のサービスが取得できない。</li>
     *   <li>ServiceManagerFactory#unloadManager()の呼び出しが行える。</li>
     * </ul>
     */
    public void testLoadWithValidate3() throws Exception{
        File def = null;
        try{
            def = TestUtility.copyOnClassPath(
                "nimbus-service11.xml",
                "nimbus-service.xml"
            );
            assertTrue(
                ServiceManagerFactory.loadManager(
                    "nimbus-service.xml",
                    false,
                    false
                )
            );
            assertTrue(ServiceManagerFactory.checkLoadManagerCompleted());
            final ServiceManager manager
                 = ServiceManagerFactory.findManager();
            assertNotNull(manager);
            try{
                manager.getService("Service0");
                fail("Service0 was not unloaded.");
            }catch(ServiceNotFoundException e){
            }
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * ローカルパス上の任意のサービス定義ファイルのロードとアンロードのテスト。<p>
     * 条件：
     * <ul>
     *   <li>デフォルト名ではないサービス定義ファイル<a href="resources/nimbus-service1.xml">"nimbus-service1.xml"</a>をテンポラリ領域に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()を呼び出してServiceManagerが取得できる。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service1"のサービスが取得できる。</li>
     *   <li>ServiceManagerFactory#unloadManager(String)の呼び出し後は、ServiceManager#getService(String)を呼び出してサービス名"Service1"のサービスが取得できない。</li>
     * </ul>
     */
    public void testLoadOnDir1() throws Exception {
        final File def = TestUtility.copyOnTemp("nimbus-service1.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager(def.getCanonicalPath())
            );
            assertTrue(ServiceManagerFactory.checkLoadManagerCompleted());
            final ServiceManager manager = ServiceManagerFactory.findManager();
            assertNotNull(manager);
            try{
                manager.getService("Service1");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            ServiceManagerFactory.unloadManager(def.getCanonicalPath());
            try{
                manager.getService("Service1");
                fail("Service1 was not unloaded.");
            }catch(ServiceNotFoundException e){
            }
        }finally{
            ServiceManagerFactory.unloadManager(def.getCanonicalPath());
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * サービスの属性設定のテスト。<p>
     * 条件：
     * <ul>
     *   <li>デフォルト名ではないサービス定義ファイル<a href="resources/nimbus-service2.xml">"nimbus-service2.xml"</a>をテンポラリ領域に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()を呼び出してServiceManagerが取得できる。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service2"のサービスが取得できる。</li>
     *   <li>取得したサービスのService#getState()の値がService#STARTEDである。</li>
     *   <li>ServiceManager#getServiceObject(String)を呼び出してサービス名"Service2"のサービスオブジェクトが取得できる。</li>
     *   <li>取得したサービスオブジェクトから、サービス定義で設定した属性の値が取得できる。</li>
     * </ul>
     */
    public void testServiceAttributeSetting1() throws Exception {
        final File def = TestUtility.copyOnTemp("nimbus-service2.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager(def.getCanonicalPath())
            );
            assertTrue(ServiceManagerFactory.checkLoadManagerCompleted());
            final ServiceManager manager = ServiceManagerFactory.findManager();
            assertNotNull(manager);
            Service service2 = null;
            try{
                service2 = manager.getService("Service2");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertEquals(Service.STARTED, service2.getState());
            TestServiceBase serviceObj2 = null;
            try{
                serviceObj2 = (TestServiceBase)manager.getServiceObject(
                    "Service2"
                );
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertEquals("AAA", serviceObj2.getString());
            final String[] stringArray = serviceObj2.getStringArray();
            assertEquals("AAA", stringArray[0]);
            assertEquals("BBB", stringArray[1]);
            assertEquals("CCC", stringArray[2]);
            assertEquals(100, serviceObj2.getInt());
            assertEquals(
                new java.net.URL("http://nimbus.org/index.html"),
                serviceObj2.getURL()
            );
            final Properties prop = serviceObj2.getProperties();
            assertEquals("1", prop.getProperty("key1"));
            assertEquals("2", prop.getProperty("key2"));
            assertEquals("3", prop.getProperty("key3"));
            assertEquals(200l, serviceObj2.longField);
        }finally{
            ServiceManagerFactory.unloadManager(def.getCanonicalPath());
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * サービスの属性設定のテスト。誤ったattribute要素を含む。<p>
     * 条件：
     * <ul>
     *   <li>デフォルト名ではないサービス定義ファイル<a href="resources/nimbus-service3.xml">"nimbus-service3.xml"</a>をテンポラリ領域に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()を呼び出してServiceManagerが取得できる。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service2"のサービスが取得できる。</li>
     *   <li>取得したサービスのService#getState()の値がService#STARTEDである。</li>
     *   <li>ServiceManager#getServiceObject(String)を呼び出してサービス名"Service2"のサービスオブジェクトが取得できる。</li>
     *   <li>取得したサービスオブジェクトから、サービス定義で正しく設定した属性の値が設定した通りに取得できる。</li>
     *   <li>取得したサービスオブジェクトから、サービス定義で誤って設定した属性の値がデフォルト値として取得できる。</li>
     * </ul>
     */
    public void testServiceAttributeSetting2() throws Exception {
        final File def = TestUtility.copyOnTemp("nimbus-service3.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager(def.getCanonicalPath())
            );
            assertTrue(ServiceManagerFactory.checkLoadManagerCompleted());
            final ServiceManager manager = ServiceManagerFactory.findManager();
            assertNotNull(manager);
            Service service2 = null;
            try{
                service2 = manager.getService("Service2");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertEquals(Service.STARTED, service2.getState());
            TestServiceBase serviceObj2 = null;
            try{
                serviceObj2 = (TestServiceBase)manager.getServiceObject(
                    "Service2"
                );
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertEquals(0, serviceObj2.getInt());
            assertEquals(
                new java.net.URL("http://nimbus.org/index.html"),
                serviceObj2.getURL()
            );
        }finally{
            ServiceManagerFactory.unloadManager(def.getCanonicalPath());
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * サービスの依存関係のテスト。Service3はService1に依存している。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service4.xml">"nimbus-service4.xml"</a>をテンポラリ領域に置く。</li>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service1.xml">"nimbus-service1.xml"</a>をテンポラリ領域に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service4.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がfalseを返す。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service3"のサービスが取得できる。</li>
     *   <li>取得した"Service3"のサービスのService#getState()の値がService#DESTROYEDである。</li>
     *   <li>ServiceManager#getServiceObject(String)を呼び出してサービス名"Service3"のサービスオブジェクトが取得できる。</li>
     *   <li>取得した"Service3"のサービスオブジェクトのTestServiceBase#getString()を呼び出して、サービス定義で設定した値が取得できない。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service1"のサービスが取得できない。</li>
     *   <li>サービス定義ファイル"nimbus-service1.xml"をロードした後、取得した"Service3"のサービスのService#getState()の値がService#STARTEDである。</li>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service1.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>"Service3"のサービスのService#getState()の値がService#STARTEDである。</li>
     *   <li>"Service3"のサービスオブジェクトのTestServiceBase#getString()を呼び出して、サービス定義で設定した値が取得できる。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service1"のサービスが取得できる。</li>
     *   <li>取得した"Service1"のサービスのService#getState()の値がService#STARTEDである。</li>
     *   <li>サービス定義ファイル"nimbus-service4.xml"をServiceManagerFactory#unloadManager(String)でアンロードした後、ServiceManager#getService(String)を呼び出してサービス名"Service3"のサービスが取得できない。</li>
     *   <li>サービス定義ファイル"nimbus-service4.xml"をServiceManagerFactory#unloadManager(String)でアンロードした後、ServiceManager#getService(String)を呼び出してサービス名"Service1"のサービスが取得できる。</li>
     *   <li>サービス定義ファイル"nimbus-service1.xml"をServiceManagerFactory#unloadManager(String)でアンロードした後、ServiceManager#getService(String)を呼び出してサービス名"Service1"のサービスが取得できない。</li>
     * </ul>
     */
    public void testServiceDependency1() throws Exception {
        final File def4 = TestUtility.copyOnTemp("nimbus-service4.xml");
        final File def1 = TestUtility.copyOnTemp("nimbus-service1.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager(def4.getCanonicalPath())
            );
            assertFalse(ServiceManagerFactory.checkLoadManagerCompleted());
            final ServiceManager manager = ServiceManagerFactory.findManager();
            assertNotNull(manager);
            Service service3 = null;
            try{
                service3 = manager.getService("Service3");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertEquals(Service.CREATED, service3.getState());
            TestServiceBase serviceObj3 = null;
            try{
                serviceObj3 = (TestServiceBase)manager.getServiceObject(
                    "Service3"
                );
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(serviceObj3.getString());
            Service service1 = null;
            try{
                service1 = manager.getService("Service1");
            }catch(ServiceNotFoundException e){
                service1 = null;
            }
            assertNull(service1);
            
            assertTrue(
                ServiceManagerFactory.loadManager(def1.getCanonicalPath())
            );
            assertTrue(ServiceManagerFactory.checkLoadManagerCompleted());
            assertEquals(Service.STARTED, service3.getState());
            assertEquals(ServiceManager.DEFAULT_NAME, serviceObj3.getString());
            try{
                service1 = manager.getService("Service1");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(service1);
            assertEquals(Service.STARTED, service1.getState());
            ServiceManagerFactory.unloadManager(def4.getCanonicalPath());
            try{
                service3 = manager.getService("Service3");
            }catch(ServiceNotFoundException e){
                service3 = null;
            }
            assertNull(service3);
            try{
                service1 = manager.getService("Service1");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(service1);
            assertEquals(Service.STARTED, service1.getState());
            ServiceManagerFactory.unloadManager(def1.getCanonicalPath());
            try{
                service1 = manager.getService("Service1");
            }catch(ServiceNotFoundException e){
                service1 = null;
            }
            assertNull(service1);
        }finally{
            ServiceManagerFactory.unloadManager(def1.getCanonicalPath());
            ServiceManagerFactory.unloadManager(def4.getCanonicalPath());
            if(def1 != null){
                def1.delete();
            }
            if(def4 != null){
                def4.delete();
            }
        }
    }
    
    /**
     * 依存関係のあるサービスの再デプロイのテスト。<p>
     * 条件：
     * <ul>
     *   <li>デフォルト名ではないサービス定義ファイル<a href="resources/nimbus-service4.xml">"nimbus-service4.xml"</a>をテンポラリ領域に置く。</li>
     *   <li>デフォルト名ではないサービス定義ファイル<a href="resources/nimbus-service1.xml">"nimbus-service1.xml"</a>をテンポラリ領域に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service4.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がfalseを返す。</li>
     *   <li>ServiceManagerFactory#findManager()を呼び出してServiceManagerが取得できる。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service3"のサービスが取得できる。</li>
     *   <li>ServiceManager#getServiceObject(String)を呼び出してサービス名"Service3"のサービスオブジェクトが取得できる。</li>
     *   <li>取得した"Service3"のサービスオブジェクトのTestServiceBase#getString()を呼び出して、サービス定義で設定した値が取得できない。</li>
     *   <li>取得した"Service3"のサービスのService#getState()の値がService#DESTROYEDである。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service1"のサービスが取得できない。</li>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service1.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>取得した"Service3"のサービスオブジェクトのTestServiceBase#getString()を呼び出して、サービス定義で設定した値が取得できる。</li>
     *   <li>取得した"Service3"のサービスのService#getState()の値がService#STARTEDである。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service1"のサービスが取得できる。</li>
     *   <li>取得した"Service1"のサービスのService#getState()の値がService#STARTEDである。</li>
     *   <li>サービス定義ファイル"nimbus-service4.xml"をServiceManagerFactory#unloadManager(String)でアンロードした後、ServiceManager#getService(String)を呼び出してサービス名"Service1"、"Service3"のサービスが取得できない。</li>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service1.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service1"と"Service3"のサービスが取得できる。</li>
     *   <li>サービス定義ファイル"nimbus-service1.xml"と"nimbus-service4.xml"をServiceManagerFactory#unloadManager(String)でアンロードした後、ServiceManager#getService(String)を呼び出してサービス名"Service1"と"Service3"のサービスが取得できない。</li>
     * </ul>
     */
    public void testServiceDependency2() throws Exception {
        final File def4 = TestUtility.copyOnTemp("nimbus-service4.xml");
        final File def1 = TestUtility.copyOnTemp("nimbus-service1.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager(def4.getCanonicalPath())
            );
            assertFalse(ServiceManagerFactory.checkLoadManagerCompleted());
            final ServiceManager manager = ServiceManagerFactory.findManager();
            assertNotNull(manager);
            Service service3 = null;
            try{
                service3 = manager.getService("Service3");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(service3);
            TestServiceBase serviceObj3 = null;
            try{
                serviceObj3 = (TestServiceBase)manager
                    .getServiceObject("Service3");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(serviceObj3);
            assertNotNull(serviceObj3.getString());
            assertEquals(Service.CREATED, service3.getState());
            Service service1 = null;
            try{
                service1 = manager.getService("Service1");
            }catch(ServiceNotFoundException e){
            }
            assertNull(service1);
            
            assertTrue(
                ServiceManagerFactory.loadManager(def1.getCanonicalPath())
            );
            assertTrue(ServiceManagerFactory.checkLoadManagerCompleted());
            assertEquals(ServiceManager.DEFAULT_NAME, serviceObj3.getString());
            assertEquals(Service.STARTED, service3.getState());
            try{
                service1 = manager.getService("Service1");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(service1);
            assertEquals(Service.STARTED, service1.getState());
            ServiceManagerFactory.unloadManager(def1.getCanonicalPath());
            service3 = null;
            try{
                service3 = manager.getService("Service3");
            }catch(ServiceNotFoundException e){
            }
            assertNotNull(service3);
            assertEquals(Service.STOPPED, service3.getState());
            service1 = null;
            try{
                service1 = manager.getService("Service1");
            }catch(ServiceNotFoundException e){
            }
            assertNull(service1);
            ServiceManagerFactory.loadManager(def1.getCanonicalPath());
            assertTrue(ServiceManagerFactory.checkLoadManagerCompleted());
            service3 = null;
            try{
                service3 = manager.getService("Service3");
            }catch(ServiceNotFoundException e){
            }
            assertNotNull(service3);
            service1 = null;
            try{
                service1 = manager.getService("Service1");
            }catch(ServiceNotFoundException e){
            }
            assertNotNull(service1);
            ServiceManagerFactory.unloadManager(def4.getCanonicalPath());
            ServiceManagerFactory.unloadManager(def1.getCanonicalPath());
            service3 = null;
            try{
                service3 = manager.getService("Service3");
            }catch(ServiceNotFoundException e){
            }
            assertNull(service3);
            service1 = null;
            try{
                service1 = manager.getService("Service1");
            }catch(ServiceNotFoundException e){
            }
            assertNull(service1);
        }finally{
            ServiceManagerFactory.unloadManager(def1.getCanonicalPath());
            ServiceManagerFactory.unloadManager(def4.getCanonicalPath());
            if(def1 != null){
                def1.delete();
            }
            if(def4 != null){
                def4.delete();
            }
        }
    }
    
    /**
     * Mapインタフェースを実装したサービスのテスト。<p>
     * 条件：
     * <ul>
     *   <li>デフォルト名のサービス定義ファイル<a href="resources/nimbus-service7.xml">"nimbus-service7.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()を呼び出してServiceManagerが取得できる。</li>
     *   <li>ServiceManager#getServiceObject(String)を呼び出してサービス名"Service1"のサービスが取得できる。</li>
     *   <li>取得したサービス"Service1"をMapにキャストできる。</li>
     *   <li>サービス定義に定義した属性の値が、Mapから取得できる。</li>
     * </ul>
     */
    public void testMapService() throws Exception {
        File def = null;
        try{
            def = TestUtility.copyOnClassPath(
                "nimbus-service7.xml",
                "nimbus-service.xml"
            );
            ServiceManagerFactory.loadManager();
            ServiceManagerFactory.checkLoadManagerCompleted();
            final ServiceManager manager
                 = ServiceManagerFactory.findManager();
            final Map map = (Map)manager.getServiceObject("Service1");
            assertEquals("Nimbus1", map.get("string1"));
            assertEquals("Nimbus2", map.get("string2"));
            assertEquals(new Integer(100), map.get("int"));
            assertEquals(
                new URL("http://nimbus.org/index.html"),
                map.get("URL")
            );
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * ServiceBaseを継承せずにServiceインタフェースを直接実装したサービスのテスト。<p>
     * 条件：
     * <ul>
     *   <li>デフォルト名ではないサービス定義ファイル<a href="resources/nimbus-service8.xml">"nimbus-service8.xml"</a>をテンポラリ領域に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service8.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service2"のサービスが取得できる。</li>
     *   <li>ServiceManager#getServiceObject(String)を呼び出してサービス名"Service2"のサービスオブジェクトが取得できる。</li>
     *   <li>取得したサービスのService#getState()の値がService#STARTEDである。</li>
     *   <li>取得したサービスオブジェクトから、サービス定義で設定した属性の値が取得できる。</li>
     * </ul>
     */
    public void testService() throws Exception {
        final File def = TestUtility.copyOnTemp("nimbus-service8.xml");
        try{
            ServiceManagerFactory.loadManager(def.getCanonicalPath());
            ServiceManagerFactory.checkLoadManagerCompleted();
            final ServiceManager manager
                 = ServiceManagerFactory.findManager();
            final Service service1 = manager.getService("Service1");
            final TestService serviceObj1
                 = (TestService)manager.getServiceObject("Service1");
            assertEquals(Service.STARTED, service1.getState());
            assertEquals("AAA", serviceObj1.getString());
            final String[] stringArray = serviceObj1.getStringArray();
            assertEquals("AAA", stringArray[0]);
            assertEquals("BBB", stringArray[1]);
            assertEquals("CCC", stringArray[2]);
            assertEquals(100, serviceObj1.getInt());
            assertEquals(
                new java.net.URL("http://nimbus.org/index.html"),
                serviceObj1.getURL()
            );
            final Properties prop = serviceObj1.getProperties();
            assertEquals("1", prop.getProperty("key1"));
            assertEquals("2", prop.getProperty("key2"));
            assertEquals("3", prop.getProperty("key3"));
        }finally{
            ServiceManagerFactory.unloadManager(def.getCanonicalPath());
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * Serviceインタフェースを実装していないPOJOサービスのテスト。<p>
     * 条件：
     * <ul>
     *   <li>デフォルト名ではないサービス定義ファイル<a href="resources/nimbus-service9.xml">"nimbus-service9.xml"</a>をテンポラリ領域に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service9.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service2"のサービスが取得できる。</li>
     *   <li>ServiceManager#getServiceObject(String)を呼び出してサービス名"Service2"のサービスオブジェクトが取得できる。</li>
     *   <li>取得したサービスのService#getState()の値がService#STARTEDである。</li>
     *   <li>取得したサービスオブジェクトから、サービス定義で設定した属性の値が取得できる。</li>
     * </ul>
     */
    public void testPOJOService() throws Exception {
        final File def = TestUtility.copyOnTemp("nimbus-service9.xml");
        try{
            ServiceManagerFactory.loadManager(def.getCanonicalPath());
            ServiceManagerFactory.checkLoadManagerCompleted();
            final ServiceManager manager
                 = ServiceManagerFactory.findManager();
            final Service service1 = manager.getService("Service1");
            final TestObject serviceObj1
                 = (TestObject)manager.getServiceObject("Service1");
            assertEquals(Service.STARTED, service1.getState());
            assertEquals("AAA", serviceObj1.getString());
            final String[] stringArray = serviceObj1.getStringArray();
            assertEquals("AAA", stringArray[0]);
            assertEquals("BBB", stringArray[1]);
            assertEquals("CCC", stringArray[2]);
            assertEquals(100, serviceObj1.getInt());
            assertEquals(
                new java.net.URL("http://nimbus.org/index.html"),
                serviceObj1.getURL()
            );
            final Properties prop = serviceObj1.getProperties();
            assertEquals("1", prop.getProperty("key1"));
            assertEquals("2", prop.getProperty("key2"));
            assertEquals("3", prop.getProperty("key3"));
        }finally{
            ServiceManagerFactory.unloadManager(def.getCanonicalPath());
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * ServiceBaseSupportインタフェースを実装したサービスのテスト。<p>
     * 条件：
     * <ul>
     *   <li>デフォルト名ではないサービス定義ファイル<a href="resources/nimbus-service10.xml">"nimbus-service10.xml"</a>をテンポラリ領域に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service10.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service2"のサービスが取得できる。</li>
     *   <li>ServiceManager#getServiceObject(String)を呼び出してサービス名"Service2"のサービスオブジェクトが取得できる。</li>
     *   <li>取得したサービスのService#getState()の値がService#STARTEDである。</li>
     *   <li>取得したサービスオブジェクトから、サービス定義で設定した属性の値が取得できる。</li>
     * </ul>
     */
    public void testServiceBaseSupport() throws Exception {
        final File def = TestUtility.copyOnTemp("nimbus-service10.xml");
        try{
            ServiceManagerFactory.loadManager(def.getCanonicalPath());
            ServiceManagerFactory.checkLoadManagerCompleted();
            final ServiceManager manager
                 = ServiceManagerFactory.findManager();
            final Service service1 = manager.getService("Service1");
            final TestServiceBaseSupport serviceObj1
                 = (TestServiceBaseSupport)manager.getServiceObject("Service1");
            assertEquals(Service.STARTED, service1.getState());
            assertEquals("AAA", serviceObj1.getString());
            final String[] stringArray = serviceObj1.getStringArray();
            assertEquals("AAA", stringArray[0]);
            assertEquals("BBB", stringArray[1]);
            assertEquals("CCC", stringArray[2]);
            assertEquals(100, serviceObj1.getInt());
            assertEquals(
                new java.net.URL("http://nimbus.org/index.html"),
                serviceObj1.getURL()
            );
            final Properties prop = serviceObj1.getProperties();
            assertEquals("1", prop.getProperty("key1"));
            assertEquals("2", prop.getProperty("key2"));
            assertEquals("3", prop.getProperty("key3"));
        }finally{
            ServiceManagerFactory.unloadManager(def.getCanonicalPath());
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * サービス定義ファイルのリロードのテスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     *   <li>リロード前に、サービス定義ファイル<a href="resources/nimbus-service1.xml">"nimbus-service.xml"</a>をクラスパス上に上書きする。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にサービス定義ファイル"nimbus-service.xml"を指定して呼び出し、戻り値でtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()を呼び出してServiceManagerが取得できる。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service0"のサービスが取得できる。</li>
     *   <li>ServiceManagerFactory#loadManager(String, boolean)の引数にサービス定義ファイル"nimbus-service.xml"と、trueを指定して呼び出し、戻り値でtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()を呼び出してServiceManagerが取得できる。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service0"のサービスが取得できない。</li>
     *   <li>ServiceManager#getService(String)を呼び出してサービス名"Service1"のサービスが取得できる。</li>
     *   <li>ServiceManagerFactory#unloadManager(String)の呼び出しが行える。</li>
     * </ul>
     */
    public void testReload() throws Exception {
        File def1 = null;
        File def2 = null;
        try{
            def1 = TestUtility.copyOnClassPath("nimbus-service.xml");
            assertTrue(
                ServiceManagerFactory.loadManager("nimbus-service.xml")
            );
            assertTrue(ServiceManagerFactory.checkLoadManagerCompleted());
            ServiceManager manager = ServiceManagerFactory.findManager();
            assertNotNull(manager);
            Service service = null;
            try{
                service = manager.getService("Service0");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(service);
            def2 = TestUtility.copyOnClassPath(
                "nimbus-service1.xml",
                "nimbus-service.xml"
            );
            assertTrue(
                ServiceManagerFactory.loadManager("nimbus-service.xml", true)
            );
            assertTrue(ServiceManagerFactory.checkLoadManagerCompleted());
            manager = ServiceManagerFactory.findManager();
            assertNotNull(manager);
            try{
                service = manager.getService("Service0");
            }catch(ServiceNotFoundException e){
                service = null;
            }
            assertNull(service);
            service = null;
            try{
                service = manager.getService("Service1");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(service);
            ServiceManagerFactory.unloadManager("nimbus-service.xml");
        }finally{
            ServiceManagerFactory.unloadManager("nimbus-service.xml");
            if(def1 != null){
                def1.delete();
            }
            if(def2 != null){
                def2.delete();
            }
        }
    }
    
    /**
     * 起動できなかったサービスの存在確認のテスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service4.xml">"nimbus-service4.xml"</a>をテンポラリ領域に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service4.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted(Set)がfalseを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted(Set)の引数に渡したS     *   <li>ServiceManagerFactory#checkLoadManagerCompleted(Set)の引数に渡したSetのサイズが１である。</li>
     * </ul>
     */
    public void testCheckLoadManagerCompleted1() throws Exception {
        final File def = TestUtility.copyOnTemp("nimbus-service4.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager(def.getCanonicalPath())
            );
            final Set fails = new HashSet();
            assertFalse(
                ServiceManagerFactory.checkLoadManagerCompleted(fails)
            );
            assertTrue(fails.contains(new ServiceName(ServiceManager.DEFAULT_NAME, "Service3")));
            assertEquals(1, fails.size());
        }finally{
            ServiceManagerFactory.unloadManager(def.getCanonicalPath());
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * 起動できなかったサービスの存在確認のテスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service1.xml">"nimbus-service1.xml"</a>をテンポラリ領域に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service1.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted(Set)がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted(Set)の引数に渡したSetのサイズが０である。</li>
     * </ul>
     */
    public void testCheckLoadManagerCompleted2() throws Exception {
        final File def = TestUtility.copyOnTemp("nimbus-service1.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager(def.getCanonicalPath())
            );
            final Set fails = new HashSet();
            assertTrue(
                ServiceManagerFactory.checkLoadManagerCompleted(fails)
            );
            assertEquals(0, fails.size());
        }finally{
            ServiceManagerFactory.unloadManager(def.getCanonicalPath());
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * ServiceManager群の取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をテンポラリ領域に置く。</li>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service12.xml">"nimbus-service12.xml"</a>をテンポラリ領域に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service12.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManagers()で、"Nimbus"と"Nimbus2"のServiceManagerが取得できる。</li>
     * </ul>
     */
    public void testFindManagers1() throws Exception {
        final File def1 = TestUtility.copyOnTemp("nimbus-service.xml");
        final File def2 = TestUtility.copyOnTemp("nimbus-service12.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager(def1.getCanonicalPath())
            );
            assertTrue(
                ServiceManagerFactory.loadManager(def2.getCanonicalPath())
            );
            final ServiceManager[] managers
                 = ServiceManagerFactory.findManagers();
            assertNotNull(managers);
            assertEquals(2, managers.length);
            final Set names = new HashSet();
            names.add(managers[0].getServiceName());
            names.add(managers[1].getServiceName());
            assertTrue(names.contains(ServiceManager.DEFAULT_NAME));
            assertTrue(names.contains("Nimbus2"));
        }finally{
            ServiceManagerFactory.unloadManager(def1.getCanonicalPath());
            ServiceManagerFactory.unloadManager(def2.getCanonicalPath());
            if(def1 != null){
                def1.delete();
            }
            if(def2 != null){
                def2.delete();
            }
        }
    }
    
    /**
     * ServiceManager群の取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をテンポラリ領域に置く。</li>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service1.xml">"nimbus-service12.xml"</a>をテンポラリ領域に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service1.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManagers()で、"Nimbus"のServiceManagerが取得できる。</li>
     * </ul>
     */
    public void testFindManagers2() throws Exception {
        final File def1 = TestUtility.copyOnTemp("nimbus-service.xml");
        final File def2 = TestUtility.copyOnTemp("nimbus-service1.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager(def1.getCanonicalPath())
            );
            assertTrue(
                ServiceManagerFactory.loadManager(def2.getCanonicalPath())
            );
            final ServiceManager[] managers
                 = ServiceManagerFactory.findManagers();
            assertNotNull(managers);
            assertEquals(1, managers.length);
            assertEquals(ServiceManager.DEFAULT_NAME, managers[0].getServiceName());
        }finally{
            ServiceManagerFactory.unloadManager(def1.getCanonicalPath());
            ServiceManagerFactory.unloadManager(def2.getCanonicalPath());
            if(def1 != null){
                def1.delete();
            }
            if(def2 != null){
                def2.delete();
            }
        }
    }
    
    /**
     * ServiceManager群の取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイルを用意しない。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#findManagers()で、長さ０のServiceManager配列が取得できる。</li>
     * </ul>
     */
    public void testFindManagers3() throws Exception {
        final ServiceManager[] managers = ServiceManagerFactory.findManagers();
        assertNotNull(managers);
        assertEquals(0, managers.length);
    }
    
    /**
     * デフォルトServiceManagerの取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をテンポラリ領域に置く。</li>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service12.xml">"nimbus-service12.xml"</a>をテンポラリ領域に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service12.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()で、"Nimbus"のServiceManagerが取得できる。</li>
     * </ul>
     */
    public void testFindManager1() throws Exception {
        final File def1 = TestUtility.copyOnTemp("nimbus-service.xml");
        final File def2 = TestUtility.copyOnTemp("nimbus-service12.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager(def1.getCanonicalPath())
            );
            assertTrue(
                ServiceManagerFactory.loadManager(def2.getCanonicalPath())
            );
            final ServiceManager manager = ServiceManagerFactory.findManager();
            assertNotNull(manager);
            assertEquals(ServiceManager.DEFAULT_NAME, manager.getServiceName());
        }finally{
            ServiceManagerFactory.unloadManager(def1.getCanonicalPath());
            ServiceManagerFactory.unloadManager(def2.getCanonicalPath());
            if(def1 != null){
                def1.delete();
            }
            if(def2 != null){
                def2.delete();
            }
        }
    }
    
    /**
     * デフォルトServiceManagerの取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をテンポラリ領域に置く。</li>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service1.xml">"nimbus-service12.xml"</a>をテンポラリ領域に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service1.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()で、"Nimbus"のServiceManagerが取得できる。</li>
     * </ul>
     */
    public void testFindManager2() throws Exception {
        final File def1 = TestUtility.copyOnTemp("nimbus-service.xml");
        final File def2 = TestUtility.copyOnTemp("nimbus-service1.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager(def1.getCanonicalPath())
            );
            assertTrue(
                ServiceManagerFactory.loadManager(def2.getCanonicalPath())
            );
            final ServiceManager manager = ServiceManagerFactory.findManager();
            assertNotNull(manager);
            assertEquals(ServiceManager.DEFAULT_NAME, manager.getServiceName());
        }finally{
            ServiceManagerFactory.unloadManager(def1.getCanonicalPath());
            ServiceManagerFactory.unloadManager(def2.getCanonicalPath());
            if(def1 != null){
                def1.delete();
            }
            if(def2 != null){
                def2.delete();
            }
        }
    }
    
    /**
     * デフォルトServiceManagerの取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイルを用意しない。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#findManager()で、nullが取得できる。</li>
     * </ul>
     */
    public void testFindManager3() throws Exception {
        final ServiceManager manager = ServiceManagerFactory.findManager();
        assertNull(manager);
    }
    
    /**
     * ServiceManagerの取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service12.xml">"nimbus-service12.xml"</a>をテンポラリ領域に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service12.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager(String)で、引数にnullを指定して呼び出し、nullが取得できる。</li>
     *   <li>ServiceManagerFactory#findManager(String)で、引数に"Nimbus2"を指定して呼び出し、ServiceManager"Nimbus2"が取得できる。</li>
     * </ul>
     */
    public void testFindManager4() throws Exception {
        final File def1 = TestUtility.copyOnTemp("nimbus-service12.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager(def1.getCanonicalPath())
            );
            ServiceManager manager = ServiceManagerFactory.findManager(null);
            assertNull(manager);
            manager = ServiceManagerFactory.findManager("Nimbus2");
            assertNotNull(manager);
            assertEquals("Nimbus2", manager.getServiceName());
        }finally{
            ServiceManagerFactory.unloadManager(def1.getCanonicalPath());
            if(def1 != null){
                def1.delete();
            }
        }
    }
    
    /**
     * ServiceManagerの登録・登録解除テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service1.xml">"nimbus-service1.xml"</a>をテンポラリ領域に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service1.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()で、ServiceManagerが取得できる。</li>
     *   <li>取得したServiceManagerをServiceManagerFactory#unregisterManager(String)を呼び出して、戻り値trueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()で、ServiceManagerが取得できない。</li>
     *   <li>取得したServiceManagerをServiceManagerFactory#registerManager(String, ServiceManager)を呼び出して、戻り値trueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()で、ServiceManagerが取得できる。</li>
     *   <li>登録したServiceManagerと、取得したServiceManagerが等しい。</li>
     * </ul>
     */
    public void testManagerRegisterAndUnregister1() throws Exception {
        final File def = TestUtility.copyOnTemp("nimbus-service1.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager(def.getCanonicalPath())
            );
            final ServiceManager manager = ServiceManagerFactory.findManager();
            assertNotNull(manager);
            assertTrue(ServiceManagerFactory
                .unregisterManager(manager.getServiceName()));
            ServiceManager tmpManager = ServiceManagerFactory.findManager();
            assertNull(tmpManager);
            assertTrue(ServiceManagerFactory
                .registerManager(manager.getServiceName(), manager));
            tmpManager = ServiceManagerFactory.findManager();
            assertNotNull(tmpManager);
            assertEquals(manager, tmpManager);
        }finally{
            ServiceManagerFactory.unloadManager(def.getCanonicalPath());
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * ServiceManagerの登録・登録解除テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service1.xml">"nimbus-service1.xml"</a>をテンポラリ領域に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service1.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()で、ServiceManagerが取得できる。</li>
     *   <li>取得したServiceManagerをServiceManagerFactory#registerManager(String, ServiceManager)を呼び出して、戻り値falseを返す。</li>
     *   <li>取得したServiceManagerをServiceManagerFactory#unregisterManager(String)を呼び出して、戻り値trueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()で、ServiceManagerが取得できない。</li>
     *   <li>取得したServiceManagerをServiceManagerFactory#unregisterManager(String)を呼び出して、戻り値falseを返す。</li>
     * </ul>
     */
    public void testManagerRegisterAndUnregister2() throws Exception {
        final File def = TestUtility.copyOnTemp("nimbus-service1.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager(def.getCanonicalPath())
            );
            final ServiceManager manager = ServiceManagerFactory.findManager();
            assertNotNull(manager);
            assertFalse(ServiceManagerFactory
                .registerManager(manager.getServiceName(), manager));
            assertTrue(ServiceManagerFactory
                .unregisterManager(manager.getServiceName()));
            ServiceManager tmpManager = ServiceManagerFactory.findManager();
            assertNull(tmpManager);
            assertTrue(ServiceManagerFactory
                .unregisterManager(manager.getServiceName()));
        }finally{
            ServiceManagerFactory.unloadManager(def.getCanonicalPath());
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * ServiceManagerの登録確認テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service1.xml">"nimbus-service1.xml"</a>をテンポラリ領域に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>ServiceManagerFactory#loadManager(String)の引数にテンポラリにコピーしたサービス定義ファイル"nimbus-service1.xml"のファイル名を指定して呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#findManager()で、ServiceManager"Nimbus"が取得できる。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus2"を指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>ServiceManagerFactory#findManager()で、ServiceManager"Nimbus2"が取得できない。</li>
     * </ul>
     */
    public void testIsRegisteredManager() throws Exception {
        final File def = TestUtility.copyOnTemp("nimbus-service1.xml");
        try{
            assertFalse(ServiceManagerFactory
                .isRegisteredManager(ServiceManager.DEFAULT_NAME));
            assertTrue(
                ServiceManagerFactory.loadManager(def.getCanonicalPath())
            );
            assertTrue(ServiceManagerFactory
                .isRegisteredManager(ServiceManager.DEFAULT_NAME));
            ServiceManager manager = ServiceManagerFactory.findManager();
            assertNotNull(manager);
            assertEquals(ServiceManager.DEFAULT_NAME, manager.getServiceName());
            assertFalse(ServiceManagerFactory.isRegisteredManager("Nimbus2"));
            manager = ServiceManagerFactory.findManager("Nimbus2");
            assertNull(manager);
        }finally{
            ServiceManagerFactory.unloadManager(def.getCanonicalPath());
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * Serviceの取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getService(String, String)の引数に"Nimbus"と"Service0"を指定して呼び出し、Service"Nimbus#Service0"が取得できる。</li>
     *   <li>ServiceManagerFactory#getService(String, String)の引数に"Nimbus2"と"Service0"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     *   <li>ServiceManagerFactory#getService(String, String)の引数に"Nimbus"と"Service1"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     * </ul>
     */
    public void testGetService1() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(ServiceManagerFactory.loadManager());
            Service service = null;
            try{
                service = ServiceManagerFactory.getService(
                    ServiceManager.DEFAULT_NAME,
                    "Service0"
                );
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(service);
            assertEquals(
                ServiceManager.DEFAULT_NAME,
                service.getServiceManagerName()
            );
            assertEquals("Service0", service.getServiceName());
            service = null;
            try{
                service = ServiceManagerFactory.getService(
                    "Nimbus2",
                    "Service0"
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals("Nimbus2", e.getServiceManagerName());
                assertEquals("Service0", e.getServiceName());
            }
            assertNull(service);
            service = null;
            try{
                service = ServiceManagerFactory.getService(
                    ServiceManager.DEFAULT_NAME,
                    "Service1"
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals(
                    ServiceManager.DEFAULT_NAME,
                    e.getServiceManagerName()
                );
                assertEquals("Service1", e.getServiceName());
            }
            assertNull(service);
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * Serviceの取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getService(ServiceName)の引数にServiceName"Nimbus#Service0"を指定して呼び出し、Service"Nimbus#Service0"が取得できる。</li>
     *   <li>ServiceManagerFactory#getService(ServiceName)の引数にServiceName"Nimbus2#Service0"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     *   <li>ServiceManagerFactory#getService(ServiceName)の引数にServiceName"Nimbus#Service1"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     * </ul>
     */
    public void testGetService2() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(ServiceManagerFactory.loadManager());
            Service service = null;
            try{
                service = ServiceManagerFactory.getService(
                    new ServiceName(ServiceManager.DEFAULT_NAME, "Service0")
                );
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(service);
            assertEquals(
                ServiceManager.DEFAULT_NAME,
                service.getServiceManagerName()
            );
            assertEquals("Service0", service.getServiceName());
            service = null;
            try{
                service = ServiceManagerFactory.getService(
                    new ServiceName("Nimbus2", "Service0")
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals("Nimbus2", e.getServiceManagerName());
                assertEquals("Service0", e.getServiceName());
            }
            assertNull(service);
            service = null;
            try{
                service = ServiceManagerFactory.getService(
                    new ServiceName(ServiceManager.DEFAULT_NAME, "Service1")
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals(
                    ServiceManager.DEFAULT_NAME,
                    e.getServiceManagerName()
                );
                assertEquals("Service1", e.getServiceName());
            }
            assertNull(service);
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * Serviceの取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getService(String)の引数に"Service0"を指定して呼び出し、Service"Nimbus#Service0"が取得できる。</li>
     *   <li>ServiceManagerFactory#getService(String)の引数に"Service1"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     * </ul>
     */
    public void testGetService3() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(ServiceManagerFactory.loadManager());
            Service service = null;
            try{
                service = ServiceManagerFactory.getService("Service0");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(service);
            assertEquals(
                ServiceManager.DEFAULT_NAME,
                service.getServiceManagerName()
            );
            assertEquals("Service0", service.getServiceName());
            service = null;
            try{
                service = ServiceManagerFactory.getService("Service1");
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals(
                    ServiceManager.DEFAULT_NAME,
                    e.getServiceManagerName()
                );
                assertEquals("Service1", e.getServiceName());
            }
            assertNull(service);
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * ServiceMetaDataの取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getServiceMetaData(String, String)の引数に"Nimbus"と"Service0"を指定して呼び出し、Service"Nimbus#Service0"のServiceMetaDataが取得できる。</li>
     *   <li>ServiceManagerFactory#getServiceMetaData(String, String)の引数に"Nimbus2"と"Service0"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     *   <li>ServiceManagerFactory#getServiceMetaData(String, String)の引数に"Nimbus"と"Service1"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     * </ul>
     */
    public void testGetServiceMetaData1() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(ServiceManagerFactory.loadManager());
            ServiceMetaData data = null;
            try{
                data = ServiceManagerFactory.getServiceMetaData(
                    ServiceManager.DEFAULT_NAME,
                    "Service0"
                );
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(data);
            assertEquals("Service0", data.getName());
            data = null;
            try{
                data = ServiceManagerFactory.getServiceMetaData(
                    "Nimbus2",
                    "Service0"
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals("Nimbus2", e.getServiceManagerName());
                assertEquals("Service0", e.getServiceName());
            }
            assertNull(data);
            data = null;
            try{
                data = ServiceManagerFactory.getServiceMetaData(
                    ServiceManager.DEFAULT_NAME,
                    "Service1"
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals(
                    ServiceManager.DEFAULT_NAME,
                    e.getServiceManagerName()
                );
                assertEquals("Service1", e.getServiceName());
            }
            assertNull(data);
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * ServiceMetaDataの取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getServiceMetaData(ServiceName)の引数にServiceName"Nimbus#Service0"を指定して呼び出し、Service"Nimbus#Service0"のServiceMetaDataが取得できる。</li>
     *   <li>ServiceManagerFactory#getServiceMetaData(ServiceName)の引数にServiceName"Nimbus2#Service0"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     *   <li>ServiceManagerFactory#getServiceMetaData(ServiceName)の引数にServiceName"Nimbus#Service1"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     *   <li>ServiceManagerFactory#getServiceMetaData(ServiceName)の引数にnullを指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できない。</li>
     * </ul>
     */
    public void testGetServiceMetaData2() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(ServiceManagerFactory.loadManager());
            ServiceMetaData data = null;
            try{
                data = ServiceManagerFactory.getServiceMetaData(
                    new ServiceName(ServiceManager.DEFAULT_NAME, "Service0")
                );
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(data);
            assertEquals("Service0", data.getName());
            data = null;
            try{
                data = ServiceManagerFactory.getServiceMetaData(
                    new ServiceName("Nimbus2", "Service0")
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals("Nimbus2", e.getServiceManagerName());
                assertEquals("Service0", e.getServiceName());
            }
            assertNull(data);
            data = null;
            try{
                data = ServiceManagerFactory.getServiceMetaData(
                    new ServiceName(ServiceManager.DEFAULT_NAME, "Service1")
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals(
                    ServiceManager.DEFAULT_NAME,
                    e.getServiceManagerName()
                );
                assertEquals("Service1", e.getServiceName());
            }
            assertNull(data);
            data = null;
            try{
                data = ServiceManagerFactory.getServiceMetaData(
                    (ServiceName)null
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals(null, e.getServiceManagerName());
                assertEquals(null, e.getServiceName());
            }
            assertNull(data);
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * ServiceMetaDataの取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getServiceMetaData(String)の引数に"Service0"を指定して呼び出し、Service"Nimbus#Service0"のServiceMetaDataが取得できる。</li>
     *   <li>ServiceManagerFactory#getServiceMetaData(String)の引数に"Service1"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     *   <li>ServiceManagerFactory#unloadManager()を呼び出し、正常にアンロードする。</li>
     *   <li>ServiceManagerFactory#getServiceMetaData(String)の引数に"Service0"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     * </ul>
     */
    public void testGetServiceMetaData3() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(ServiceManagerFactory.loadManager());
            ServiceMetaData data = null;
            try{
                data = ServiceManagerFactory.getServiceMetaData("Service0");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(data);
            assertEquals("Service0", data.getName());
            data = null;
            try{
                data = ServiceManagerFactory.getServiceMetaData("Service1");
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals("Nimbus", e.getServiceManagerName());
                assertEquals("Service1", e.getServiceName());
            }
            assertNull(data);
            ServiceManagerFactory.unloadManager();
            data = null;
            try{
                data = ServiceManagerFactory.getServiceMetaData("Service0");
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals("Nimbus", e.getServiceManagerName());
                assertEquals("Service0", e.getServiceName());
            }
            assertNull(data);
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * Serviceオブジェクトの取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getServiceObject(String, String)の引数に"Nimbus"と"Service0"を指定して呼び出し、Serviceオブジェクト"Nimbus#Service0"が取得できる。</li>
     *   <li>ServiceManagerFactory#getServiceObject(String, String)の引数に"Nimbus2"と"Service0"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     *   <li>ServiceManagerFactory#getServiceObject(String, String)の引数に"Nimbus"と"Service1"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     * </ul>
     */
    public void testGetServiceObject1() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(ServiceManagerFactory.loadManager());
            Object serviceObj = null;
            try{
                serviceObj = ServiceManagerFactory.getServiceObject(
                    ServiceManager.DEFAULT_NAME,
                    "Service0"
                );
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(serviceObj);
            assertEquals(TestServiceBase.class, serviceObj.getClass());
            serviceObj = null;
            try{
                serviceObj = ServiceManagerFactory.getServiceObject(
                    "Nimbus2",
                    "Service0"
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals("Nimbus2", e.getServiceManagerName());
                assertEquals("Service0", e.getServiceName());
            }
            assertNull(serviceObj);
            serviceObj = null;
            try{
                serviceObj = ServiceManagerFactory.getServiceObject(
                    ServiceManager.DEFAULT_NAME,
                    "Service1"
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals(
                    ServiceManager.DEFAULT_NAME,
                    e.getServiceManagerName()
                );
                assertEquals("Service1", e.getServiceName());
            }
            assertNull(serviceObj);
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * Serviceオブジェクトの取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getServiceObject(ServiceName)の引数にServiceName"Nimbus#Service0"を指定して呼び出し、Serviceオブジェクト"Nimbus#Service0"が取得できる。</li>
     *   <li>ServiceManagerFactory#getServiceObject(ServiceName)の引数にServiceName"Nimbus2#Service0"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     *   <li>ServiceManagerFactory#getServiceObject(ServiceName)の引数にServiceName"Nimbus#Service1"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     *   <li>ServiceManagerFactory#getServiceObject(ServiceName)の引数にnullを指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できない。</li>
     * </ul>
     */
    public void testGetServiceObject2() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(ServiceManagerFactory.loadManager());
            Object serviceObj = null;
            try{
                serviceObj = ServiceManagerFactory.getServiceObject(
                    new ServiceName(ServiceManager.DEFAULT_NAME, "Service0")
                );
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(serviceObj);
            assertEquals(TestServiceBase.class, serviceObj.getClass());
            serviceObj = null;
            try{
                serviceObj = ServiceManagerFactory.getServiceObject(
                    new ServiceName("Nimbus2", "Service0")
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals("Nimbus2", e.getServiceManagerName());
                assertEquals("Service0", e.getServiceName());
            }
            assertNull(serviceObj);
            serviceObj = null;
            try{
                serviceObj = ServiceManagerFactory.getServiceObject(
                    new ServiceName(ServiceManager.DEFAULT_NAME, "Service1")
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals(
                    ServiceManager.DEFAULT_NAME,
                    e.getServiceManagerName()
                );
                assertEquals("Service1", e.getServiceName());
            }
            assertNull(serviceObj);
            serviceObj = null;
            try{
                serviceObj = ServiceManagerFactory.getServiceObject(
                    (ServiceName)null
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals(null, e.getServiceManagerName());
                assertEquals(null, e.getServiceName());
            }
            assertNull(serviceObj);
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * Serviceの取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getService(String)の引数に"Service0"を指定して呼び出し、Service"Nimbus#Service0"が取得できる。</li>
     *   <li>ServiceManagerFactory#getService(String)の引数に"Service1"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     *   <li>ServiceManagerFactory#unloadManager()を呼び出し、正常にアンロードする。</li>
     *   <li>ServiceManagerFactory#getService(String)の引数に"Service0"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     * </ul>
     */
    public void testGetServiceObject3() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(ServiceManagerFactory.loadManager());
            Object serviceObj = null;
            try{
                serviceObj = ServiceManagerFactory.getServiceObject("Service0");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(serviceObj);
            assertEquals(TestServiceBase.class, serviceObj.getClass());
            serviceObj = null;
            try{
                serviceObj = ServiceManagerFactory.getServiceObject("Service1");
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals(
                    ServiceManager.DEFAULT_NAME,
                    e.getServiceManagerName()
                );
                assertEquals("Service1", e.getServiceName());
            }
            assertNull(serviceObj);
            ServiceManagerFactory.unloadManager();
            serviceObj = null;
            try{
                serviceObj = ServiceManagerFactory.getServiceObject("Service0");
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals(
                    ServiceManager.DEFAULT_NAME,
                    e.getServiceManagerName()
                );
                assertEquals("Service0", e.getServiceName());
            }
            assertNull(serviceObj);
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * ServiceStateBroadcasterの取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getServiceStateBroadcaster(String, String)の引数に"Nimbus"と"Service0"を指定して呼び出し、"Service0"のServiceStateBroadcasterが取得できる。</li>
     *   <li>取得したServiceStateBroadcasterのServiceStateBroadcaster#addServiceStateListener(ServiceStateListener)を呼び出して、独自ServiceStateListenerを登録する。</li>
     *   <li>ServiceManagerFactory#getServiceStateBroadcaster(String, String)の引数に"Nimbus2"と"Service0"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     *   <li>ServiceManagerFactory#getServiceStateBroadcaster(String, String)の引数に"Nimbus"と"Service1"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     *   <li>ServiceManagerFactory#unloadManager()を呼び出し、正常にアンロードする。</li>
     *   <li>独自ServiceStateListenerのServiceStateListener#stateChanged(ServiceStateChangeEvent)が呼び出され、サービス"Service0"が停止されたのを検知する。</li>
     * </ul>
     */
    public void testGetServiceStateBroadcaster1() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        class MyServiceStateListener implements ServiceStateListener{
            public boolean isStateChanged = false;
            public void stateChanged(ServiceStateChangeEvent e)
             throws Exception{
                final Service service = ServiceManagerFactory
                    .getService("Service0");
                assertEquals(service, e.getService());
                assertEquals(Service.STOPPED, e.getService().getState());
                isStateChanged = true;
            }
            public boolean isEnabledState(int state){
                return state == Service.STOPPED;
            }
        }
        MyServiceStateListener listener = null;
        try{
            assertTrue(ServiceManagerFactory.loadManager());
            ServiceStateBroadcaster ssb = null;
            try{
                ssb = ServiceManagerFactory.getServiceStateBroadcaster(
                    ServiceManager.DEFAULT_NAME,
                    "Service0"
                );
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(ssb);
            listener = new MyServiceStateListener();
            ssb.addServiceStateListener(listener);
            ssb = null;
            try{
                ssb = ServiceManagerFactory.getServiceStateBroadcaster(
                    "Nimbus2",
                    "Service0"
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals("Nimbus2", e.getServiceManagerName());
                assertEquals("Service0", e.getServiceName());
            }
            assertNull(ssb);
            ssb = null;
            try{
                ssb = ServiceManagerFactory.getServiceStateBroadcaster(
                    ServiceManager.DEFAULT_NAME,
                    "Service1"
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals(
                    ServiceManager.DEFAULT_NAME,
                    e.getServiceManagerName()
                );
                assertEquals("Service1", e.getServiceName());
            }
            assertNull(ssb);
            ServiceManagerFactory.unloadManager(def.getCanonicalPath());
            if(listener != null){
                assertTrue(listener.isStateChanged);
            }
        }finally{
            ServiceManagerFactory.unloadManager(def.getCanonicalPath());
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * ServiceStateBroadcasterの取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getServiceStateBroadcaster(ServiceName)の引数に"Nimbus#Service0"を指定して呼び出し、"Service0"のServiceStateBroadcasterが取得できる。</li>
     *   <li>取得したServiceStateBroadcasterのServiceStateBroadcaster#addServiceStateListener(ServiceStateListener)を呼び出して、独自ServiceStateListenerを登録する。</li>
     *   <li>ServiceManagerFactory#getServiceStateBroadcaster(ServiceName)の引数に"Nimbus2#Service0"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     *   <li>ServiceManagerFactory#getServiceStateBroadcaster(ServiceName)の引数に"Nimbus#Service1"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     *   <li>ServiceManagerFactory#getServiceStateBroadcaster(ServiceName)の引数にnullを指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できない。</li>
     *   <li>ServiceManagerFactory#unloadManager()を呼び出し、正常にアンロードする。</li>
     *   <li>独自ServiceStateListenerのServiceStateListener#stateChanged(ServiceStateChangeEvent)が呼び出され、サービス"Service0"が停止されたのを検知する。</li>
     * </ul>
     */
    public void testGetServiceStateBroadcaster2() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        class MyServiceStateListener implements ServiceStateListener{
            public boolean isStateChanged = false;
            public void stateChanged(ServiceStateChangeEvent e)
             throws Exception{
                final Service service = ServiceManagerFactory
                    .getService("Service0");
                assertEquals(service, e.getService());
                assertEquals(Service.STOPPED, e.getService().getState());
                isStateChanged = true;
            }
            public boolean isEnabledState(int state){
                return state == Service.STOPPED;
            }
        }
        MyServiceStateListener listener = null;
        try{
            assertTrue(ServiceManagerFactory.loadManager());
            ServiceStateBroadcaster ssb = null;
            try{
                ssb = ServiceManagerFactory.getServiceStateBroadcaster(
                    new ServiceName(
                        ServiceManager.DEFAULT_NAME,
                        "Service0"
                    )
                );
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(ssb);
            listener = new MyServiceStateListener();
            ssb.addServiceStateListener(listener);
            ssb = null;
            try{
                ssb = ServiceManagerFactory.getServiceStateBroadcaster(
                    new ServiceName("Nimbus2", "Service0")
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals("Nimbus2", e.getServiceManagerName());
                assertEquals("Service0", e.getServiceName());
            }
            assertNull(ssb);
            ssb = null;
            try{
                ssb = ServiceManagerFactory.getServiceStateBroadcaster(
                    new ServiceName(
                        ServiceManager.DEFAULT_NAME,
                        "Service1"
                    )
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals(
                    ServiceManager.DEFAULT_NAME,
                    e.getServiceManagerName()
                );
                assertEquals("Service1", e.getServiceName());
            }
            assertNull(ssb);
            ssb = null;
            try{
                ssb = ServiceManagerFactory.getServiceStateBroadcaster(
                    (ServiceName)null
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals(null, e.getServiceManagerName());
                assertEquals(null, e.getServiceName());
            }
            assertNull(ssb);
            ServiceManagerFactory.unloadManager();
            if(listener != null){
                assertTrue(listener.isStateChanged);
            }
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * ServiceStateBroadcasterの取得テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getServiceStateBroadcaster(String)の引数に"Service0"を指定して呼び出し、"Service0"のServiceStateBroadcasterが取得できる。</li>
     *   <li>取得したServiceStateBroadcasterのServiceStateBroadcaster#addServiceStateListener(ServiceStateListener)を呼び出して、独自ServiceStateListenerを登録する。</li>
     *   <li>ServiceManagerFactory#getServiceStateBroadcaster(String)の引数に"Service1"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     *   <li>ServiceManagerFactory#unloadManager()を呼び出し、正常にアンロードする。</li>
     *   <li>独自ServiceStateListenerのServiceStateListener#stateChanged(ServiceStateChangeEvent)が呼び出され、サービス"Service0"が停止されたのを検知する。</li>
     *   <li>ServiceManagerFactory#getServiceStateBroadcaster(String)の引数に"Service0"を指定して呼び出し、ServiceNotFoundExceptionが発生する。</li>
     *   <li>発生したServiceNotFoundExceptionから、取得しようとしたサービス名が取得できる。</li>
     * </ul>
     */
    public void testGetServiceStateBroadcaster3() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        class MyServiceStateListener implements ServiceStateListener{
            public boolean isStateChanged = false;
            public void stateChanged(ServiceStateChangeEvent e)
             throws Exception{
                final Service service = ServiceManagerFactory
                    .getService("Service0");
                assertEquals(service, e.getService());
                assertEquals(Service.STOPPED, e.getService().getState());
                isStateChanged = true;
            }
            public boolean isEnabledState(int state){
                return state == Service.STOPPED;
            }
        }
        MyServiceStateListener listener = null;
        try{
            assertTrue(ServiceManagerFactory.loadManager());
            ServiceStateBroadcaster ssb = null;
            try{
                ssb = ServiceManagerFactory.getServiceStateBroadcaster(
                    "Service0"
                );
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(ssb);
            listener = new MyServiceStateListener();
            ssb.addServiceStateListener(listener);
            ssb = null;
            try{
                ssb = ServiceManagerFactory.getServiceStateBroadcaster(
                    "Service1"
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals(
                    ServiceManager.DEFAULT_NAME,
                    e.getServiceManagerName()
                );
                assertEquals("Service1", e.getServiceName());
            }
            assertNull(ssb);
            ServiceManagerFactory.unloadManager(def.getCanonicalPath());
            if(listener != null){
                assertTrue(listener.isStateChanged);
            }
            ssb = null;
            try{
                ssb = ServiceManagerFactory.getServiceStateBroadcaster(
                    "Service0"
                );
                fail("ServiceNotFoundException must throw.");
            }catch(ServiceNotFoundException e){
                assertEquals(
                    ServiceManager.DEFAULT_NAME,
                    e.getServiceManagerName()
                );
                assertEquals("Service0", e.getServiceName());
            }
            assertNull(ssb);
        }finally{
            ServiceManagerFactory.unloadManager(def.getCanonicalPath());
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * Serviceの登録・登録解除テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getService(String)で、Service"Service0"が取得できる。</li>
     *   <li>取得したServiceをServiceManagerFactory#unregisterService(String, String)を、引数に"Nimbus"と"Service0"を指定して呼び出して、戻り値trueを返す。</li>
     *   <li>ServiceManagerFactory#getService(String)で、Service"Service0"が取得できない。</li>
     *   <li>ServiceManagerFactory#registerService(String, String, Service)の引数に"Nimbus"、"Service0"、取得したServiceを指定して呼び出して、戻り値trueを返す。</li>
     *   <li>ServiceManagerFactory#getService(String)で、Service"Service0"が取得できる。</li>
     *   <li>登録したServiceと、取得したServiceが等しい。</li>
     * </ul>
     */
    public void testServiceRegisterAndUnregister1() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(ServiceManagerFactory.loadManager());
            Service service = null;
            try{
                service = ServiceManagerFactory.getService("Service0");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(service);
            assertTrue(
                ServiceManagerFactory.unregisterService(
                    service.getServiceManagerName(),
                    service.getServiceName()
                )
            );
            Service tmpService = null;
            try{
                tmpService = ServiceManagerFactory.getService("Service0");
            }catch(ServiceNotFoundException e){
            }
            assertNull(tmpService);
            assertTrue(
                ServiceManagerFactory.registerService(
                    service.getServiceManagerName(),
                    service.getServiceName(),
                    service
                )
            );
            try{
                tmpService = ServiceManagerFactory.getService("Service0");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(tmpService);
            assertEquals(service, tmpService);
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * Serviceの登録・登録解除テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service1.xml">"nimbus-service1.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getService(String)に引数"Service0"を指定して呼び出し、Service"Service0"が取得できる。</li>
     *   <li>取得したServiceをServiceManagerFactory#registerService(String, String, Service)を呼び出して、戻り値falseを返す。</li>
     *   <li>取得したServiceをServiceManagerFactory#unregisterService(String, String)を、引数に"Nimbus"と"Service0"を指定して呼び出して、戻り値trueを返す。</li>
     *   <li>ServiceManagerFactory#getService(String)に引数"Service0"を指定して呼び出し、Service"Service0"が取得できない。</li>
     *   <li>取得したServiceをServiceManagerFactory#unregisterService(String, String)を、引数に"Nimbus"と"Service0"を指定して呼び出して、戻り値trueを返す。</li>
     * </ul>
     */
    public void testServiceRegisterAndUnregister2() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(ServiceManagerFactory.loadManager());
            Service service = null;
            try{
                service = ServiceManagerFactory.getService("Service0");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(service);
            assertFalse(
                ServiceManagerFactory.registerService(
                    service.getServiceManagerName(),
                    service.getServiceName(),
                    service
                )
            );
            assertTrue(
                ServiceManagerFactory.unregisterService(
                    service.getServiceManagerName(),
                    service.getServiceName()
                )
            );
            Service tmpService = null;
            try{
                tmpService = ServiceManagerFactory.getService("Service0");
            }catch(ServiceNotFoundException e){
            }
            assertNull(tmpService);
            assertTrue(
                ServiceManagerFactory.unregisterService(
                    service.getServiceManagerName(),
                    service.getServiceName()
                )
            );
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * Serviceオブジェクトの登録・登録解除テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getServiceObject(String)で、Serviceオブジェクト"Service0"が取得できる。</li>
     *   <li>取得したServiceオブジェクトをServiceManagerFactory#unregisterService(String, String)を、引数に"Nimbus"と"Service0"を指定して呼び出して、戻り値trueを返す。</li>
     *   <li>ServiceManagerFactory#getServiceObject(String)で、Serviceオブジェクト"Service0"が取得できない。</li>
     *   <li>ServiceManagerFactory#registerService(String, String, Service)の引数に"Nimbus"、"Service0"、取得したServiceオブジェクトを指定して呼び出して、戻り値trueを返す。</li>
     *   <li>ServiceManagerFactory#getServiceObject(String)で、Serviceオブジェクト"Service0"が取得できる。</li>
     *   <li>登録したServiceオブジェクトと、取得したServiceオブジェクトが等しい。</li>
     * </ul>
     */
    public void testServiceRegisterAndUnregister3() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(ServiceManagerFactory.loadManager());
            Object serviceObj = null;
            try{
                serviceObj = ServiceManagerFactory.getServiceObject("Service0");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(serviceObj);
            assertTrue(
                ServiceManagerFactory.unregisterService(
                    ServiceManager.DEFAULT_NAME,
                    "Service0"
                )
            );
            Object tmpObj = null;
            try{
                tmpObj = ServiceManagerFactory.getServiceObject("Service0");
            }catch(ServiceNotFoundException e){
            }
            assertNull(tmpObj);
            assertTrue(
                ServiceManagerFactory.registerService(
                    ServiceManager.DEFAULT_NAME,
                    "Service0",
                    serviceObj
                )
            );
            try{
                tmpObj = ServiceManagerFactory.getServiceObject("Service0");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(tmpObj);
            assertEquals(serviceObj, tmpObj);
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * Serviceインタフェースを実装していないServiceオブジェクトの登録・登録解除テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service9.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getServiceObject(String)で、Serviceオブジェクト"Service1"が取得できる。</li>
     *   <li>取得したServiceオブジェクトをServiceManagerFactory#unregisterService(String, String)を、引数に"Nimbus"と"Service1"を指定して呼び出して、戻り値trueを返す。</li>
     *   <li>ServiceManagerFactory#getServiceObject(String)で、Serviceオブジェクト"Service0"が取得できない。</li>
     *   <li>ServiceManagerFactory#registerService(String, String, Service)の引数に"Nimbus"、"Service1"、取得したServiceオブジェクトを指定して呼び出して、戻り値trueを返す。</li>
     *   <li>ServiceManagerFactory#getServiceObject(String)で、Serviceオブジェクト"Service1"が取得できる。</li>
     *   <li>登録したServiceオブジェクトと、取得したServiceオブジェクトが等しい。</li>
     * </ul>
     */
    public void testServiceRegisterAndUnregister4() throws Exception {
        final File def = TestUtility.copyOnClassPath(
            "nimbus-service9.xml",
            "nimbus-service.xml"
        );
        try{
            assertTrue(ServiceManagerFactory.loadManager());
            Object serviceObj = null;
            try{
                serviceObj = ServiceManagerFactory.getServiceObject("Service1");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(serviceObj);
            assertTrue(
                ServiceManagerFactory.unregisterService(
                    ServiceManager.DEFAULT_NAME,
                    "Service1"
                )
            );
            Object tmpObj = null;
            try{
                tmpObj = ServiceManagerFactory.getServiceObject("Service1");
            }catch(ServiceNotFoundException e){
            }
            assertNull(tmpObj);
            assertTrue(
                ServiceManagerFactory.registerService(
                    ServiceManager.DEFAULT_NAME,
                    "Service1",
                    serviceObj
                )
            );
            try{
                tmpObj = ServiceManagerFactory.getServiceObject("Service1");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(tmpObj);
            assertEquals(serviceObj, tmpObj);
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * Serviceの登録確認テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#isRegisteredService(String, String)の引数に"Nimbus"と"Service0"を指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#isRegisteredService(String, String)の引数に"Nimbus"と"Service0"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#getService(String)の引数に"Service0"を指定して呼び出し、Service"Service0"が取得できる。</li>
     *   <li>ServiceManagerFactory#isRegisteredService(String, String)の引数に"Nimbus2"と"Service0"を指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>ServiceManagerFactory#getService(String, String)の引数に"Nimbus2"と"Service0"を指定して呼び出し、Service"Service0"が取得できない。</li>
     *   <li>ServiceManagerFactory#isRegisteredService(String, String)の引数に"Nimbus"と"Service1"を指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>ServiceManagerFactory#getService(String)の引数に"Service1"を指定して呼び出し、Service"Service1"が取得できない。</li>
     *   <li>ServiceManagerFactory#unloadManager()を呼び出し、正常にアンロードする。</li>
     *   <li>ServiceManagerFactory#isRegisteredService(String, String)の引数に"Nimbus"と"Service0"を指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>ServiceManagerFactory#getService(String)の引数に"Service0"を指定して呼び出し、Service"Service0"が取得できない。</li>
     * </ul>
     */
    public void testIsRegisteredService1() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertFalse(
                ServiceManagerFactory.isRegisteredService(
                    ServiceManager.DEFAULT_NAME,
                    "Service0"
                )
            );
            assertTrue(
                ServiceManagerFactory.loadManager()
            );
            assertTrue(
                ServiceManagerFactory.isRegisteredService(
                    ServiceManager.DEFAULT_NAME,
                    "Service0"
                )
            );
            Service service = null;
            try{
                service = ServiceManagerFactory.getService("Service0");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(service);
            assertEquals(
                ServiceManager.DEFAULT_NAME,
                service.getServiceManagerName()
            );
            assertEquals("Service0", service.getServiceName());
            assertFalse(
                ServiceManagerFactory.isRegisteredService(
                    "Nimbus2",
                    "Service0"
                )
            );
            service = null;
            try{
                service = ServiceManagerFactory.getService(
                    "Nimbus2",
                    "Service0"
                );
            }catch(ServiceNotFoundException e){
            }
            assertNull(service);
            assertFalse(
                ServiceManagerFactory.isRegisteredService(
                    "Nimbus",
                    "Service1"
                )
            );
            service = null;
            try{
                service = ServiceManagerFactory.getService("Service1");
            }catch(ServiceNotFoundException e){
            }
            assertNull(service);
            ServiceManagerFactory.unloadManager();
            assertFalse(
                ServiceManagerFactory.isRegisteredService(
                    ServiceManager.DEFAULT_NAME,
                    "Service0"
                )
            );
            service = null;
            try{
                service = ServiceManagerFactory.getService(
                    ServiceManager.DEFAULT_NAME,
                    "Service0"
                );
            }catch(ServiceNotFoundException e){
            }
            assertNull(service);
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * Serviceの登録確認テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#isRegisteredService(ServiceName)の引数に"Nimbus#Service0"を指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#isRegisteredService(ServiceName)の引数に"Nimbus#Service0"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#getService(String)の引数に"Service0"を指定して呼び出し、Service"Service0"が取得できる。</li>
     *   <li>ServiceManagerFactory#isRegisteredService(ServiceName)の引数に"Nimbus2#Service0"を指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>ServiceManagerFactory#getService(String, String)の引数に"Nimbus2"と"Service0"を指定して呼び出し、Service"Service0"が取得できない。</li>
     *   <li>ServiceManagerFactory#isRegisteredService(ServiceName)の引数に"Nimbus#Service1"を指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>ServiceManagerFactory#getService(String)の引数に"Service1"を指定して呼び出し、Service"Service1"が取得できない。</li>
     *   <li>ServiceManagerFactory#isRegisteredService(ServiceName)の引数にnullを指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>ServiceManagerFactory#unloadManager()を呼び出し、正常にアンロードする。</li>
     *   <li>ServiceManagerFactory#isRegisteredService(ServiceName)の引数に"Nimbus#Service0"を指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>ServiceManagerFactory#getService(String)の引数に"Service0"を指定して呼び出し、Service"Service0"が取得できない。</li>
     * </ul>
     */
    public void testIsRegisteredService2() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertFalse(
                ServiceManagerFactory.isRegisteredService(
                    new ServiceName(ServiceManager.DEFAULT_NAME, "Service0")
                )
            );
            assertTrue(
                ServiceManagerFactory.loadManager()
            );
            assertTrue(
                ServiceManagerFactory.isRegisteredService(
                    new ServiceName(ServiceManager.DEFAULT_NAME, "Service0")
                )
            );
            Service service = null;
            try{
                service = ServiceManagerFactory.getService("Service0");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(service);
            assertEquals(
                ServiceManager.DEFAULT_NAME,
                service.getServiceManagerName()
            );
            assertEquals("Service0", service.getServiceName());
            assertFalse(
                ServiceManagerFactory.isRegisteredService(
                    new ServiceName("Nimbus2", "Service0")
                )
            );
            service = null;
            try{
                service = ServiceManagerFactory.getService(
                    "Nimbus2",
                    "Service0"
                );
            }catch(ServiceNotFoundException e){
            }
            assertNull(service);
            assertFalse(
                ServiceManagerFactory.isRegisteredService(
                    new ServiceName(ServiceManager.DEFAULT_NAME, "Service1")
                )
            );
            service = null;
            try{
                service = ServiceManagerFactory.getService("Service1");
            }catch(ServiceNotFoundException e){
            }
            assertNull(service);
            assertFalse(ServiceManagerFactory.isRegisteredService(null));
            ServiceManagerFactory.unloadManager();
            assertFalse(
                ServiceManagerFactory.isRegisteredService(
                    new ServiceName(ServiceManager.DEFAULT_NAME, "Service0")
                )
            );
            service = null;
            try{
                service = ServiceManagerFactory.getService(
                    ServiceManager.DEFAULT_NAME,
                    "Service0"
                );
            }catch(ServiceNotFoundException e){
            }
            assertNull(service);
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    private class MyRepository implements Repository{
        private final Map managerMap = new Hashtable();
        boolean isNormalRegister = true;
        boolean isNormalUnregister = true;
        boolean isNormalGet = true;
        public Object get(String name){
            if(!isNormalGet){
                return null;
            }
            return (Service)managerMap.get(name);
        }
        public boolean register(String name, Object manager){
            if(!isNormalRegister){
                return false;
            }
            if(managerMap.containsKey(name)){
                return false;
            }
            managerMap.put(name, manager);
            return true;
        }
        public boolean unregister(String name){
            if(!isNormalUnregister){
                return false;
            }
            managerMap.remove(name);
            return true;
        }
        public boolean isRegistered(String name){
            if(!isNormalGet){
                return false;
            }
            return managerMap.containsKey(name);
        }
        public Set nameSet(){
            if(!isNormalGet){
                return new HashSet();
            }
            return new HashSet(managerMap.keySet());
        }
        public Set registeredSet(){
            if(!isNormalGet){
                return new HashSet();
            }
            return new HashSet(managerMap.values());
        }
    }
    
    /**
     * マネージャRepositoryの変更テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#setManagerRepository(Repository)の引数に独自Repositoryオブジェクト１を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト１のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#setManagerRepository(Repository)の引数に独自Repositoryオブジェクト２を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト２のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト１のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でfalseが返る。</li>
     * </ul>
     */
    public void testSetManagerRepository1() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager()
            );
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            final MyRepository repository1 = new MyRepository();
            assertTrue(ServiceManagerFactory.setManagerRepository(repository1));
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            assertTrue(repository1.isRegistered(ServiceManager.DEFAULT_NAME));
            
            final MyRepository repository2 = new MyRepository();
            assertTrue(ServiceManagerFactory.setManagerRepository(repository2));
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            assertTrue(repository2.isRegistered(ServiceManager.DEFAULT_NAME));
            assertFalse(repository1.isRegistered(ServiceManager.DEFAULT_NAME));
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * 登録できないマネージャRepositoryへの変更テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#setManagerRepository(Repository)の引数に独自Repositoryオブジェクト１を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト１のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#setManagerRepository(Repository)の引数に独自Repositoryオブジェクト２を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト２のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト１のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>独自Repositoryオブジェクト１を、Repository#register(String, Object)で登録できない状態にする。</li>
     *   <li>ServiceManagerFactory#setManagerRepository(Repository)の引数に独自Repositoryオブジェクト１を指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト２のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト１のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でfalseが返る。</li>
     * </ul>
     */
    public void testSetManagerRepository2() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager()
            );
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            final MyRepository repository1 = new MyRepository();
            assertTrue(ServiceManagerFactory.setManagerRepository(repository1));
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            assertTrue(repository1.isRegistered(ServiceManager.DEFAULT_NAME));
            
            final MyRepository repository2 = new MyRepository();
            assertTrue(ServiceManagerFactory.setManagerRepository(repository2));
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            assertTrue(repository2.isRegistered(ServiceManager.DEFAULT_NAME));
            assertFalse(repository1.isRegistered(ServiceManager.DEFAULT_NAME));
            
            repository1.isNormalRegister = false;
            assertFalse(
                ServiceManagerFactory.setManagerRepository(repository1)
            );
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            assertFalse(repository1.isRegistered(ServiceManager.DEFAULT_NAME));
            assertTrue(repository2.isRegistered(ServiceManager.DEFAULT_NAME));
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * 取得できないマネージャRepositoryからの正常なマネージャRepositoryへの変更テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#setManagerRepository(Repository)の引数に独自Repositoryオブジェクト１を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト１のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#setManagerRepository(Repository)の引数に独自Repositoryオブジェクト２を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト２のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト１のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>独自Repositoryオブジェクト２を、Repository#get(String)で取得できない状態にする。</li>
     *   <li>ServiceManagerFactory#setManagerRepository(Repository)の引数に独自Repositoryオブジェクト１を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>独自Repositoryオブジェクト２を、Repository#get(String)で取得できる状態にする。</li>
     *   <li>独自Repositoryオブジェクト２のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>独自Repositoryオブジェクト１のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     * </ul>
     */
    public void testSetManagerRepository3() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager()
            );
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            final MyRepository repository1 = new MyRepository();
            assertTrue(ServiceManagerFactory.setManagerRepository(repository1));
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            assertTrue(repository1.isRegistered(ServiceManager.DEFAULT_NAME));
            
            final MyRepository repository2 = new MyRepository();
            assertTrue(ServiceManagerFactory.setManagerRepository(repository2));
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            assertTrue(repository2.isRegistered(ServiceManager.DEFAULT_NAME));
            assertFalse(repository1.isRegistered(ServiceManager.DEFAULT_NAME));
            
            repository2.isNormalGet = false;
            assertTrue(
                ServiceManagerFactory.setManagerRepository(repository1)
            );
            assertFalse(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            assertFalse(repository1.isRegistered(ServiceManager.DEFAULT_NAME));
            repository2.isNormalGet = true;
            assertTrue(repository2.isRegistered(ServiceManager.DEFAULT_NAME));
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * 登録解除できないマネージャRepositoryへの変更テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#setManagerRepository(Repository)の引数に独自Repositoryオブジェクト１を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト１のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#setManagerRepository(Repository)の引数に独自Repositoryオブジェクト２を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト２のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト１のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>独自Repositoryオブジェクト２を、Repository#unregister(String)で登録解除できない状態にする。</li>
     *   <li>ServiceManagerFactory#setManagerRepository(Repository)の引数に独自Repositoryオブジェクト１を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト２のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト１のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     * </ul>
     */
    public void testSetManagerRepository4() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(ServiceManagerFactory.loadManager());
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            final MyRepository repository1 = new MyRepository();
            assertTrue(ServiceManagerFactory.setManagerRepository(repository1));
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            assertTrue(repository1.isRegistered(ServiceManager.DEFAULT_NAME));
            
            final MyRepository repository2 = new MyRepository();
            assertTrue(ServiceManagerFactory.setManagerRepository(repository2));
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            assertTrue(repository2.isRegistered(ServiceManager.DEFAULT_NAME));
            assertFalse(repository1.isRegistered(ServiceManager.DEFAULT_NAME));
            
            repository2.isNormalUnregister = false;
            assertTrue(
                ServiceManagerFactory.setManagerRepository(repository1)
            );
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            assertTrue(repository1.isRegistered(ServiceManager.DEFAULT_NAME));
            assertTrue(repository2.isRegistered(ServiceManager.DEFAULT_NAME));
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * 同じマネージャRepositoryへの変更テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#setManagerRepository(Repository)の引数に独自Repositoryオブジェクト１を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト１のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#setManagerRepository(Repository)の引数に独自Repositoryオブジェクト２を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト２のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト１のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>ServiceManagerFactory#setManagerRepository(Repository)の引数に独自Repositoryオブジェクト２を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト２のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     * </ul>
     */
    public void testSetManagerRepository5() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager()
            );
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            final MyRepository repository1 = new MyRepository();
            assertTrue(ServiceManagerFactory.setManagerRepository(repository1));
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            assertTrue(repository1.isRegistered(ServiceManager.DEFAULT_NAME));
            
            final MyRepository repository2 = new MyRepository();
            assertTrue(ServiceManagerFactory.setManagerRepository(repository2));
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            assertTrue(repository2.isRegistered(ServiceManager.DEFAULT_NAME));
            assertFalse(repository1.isRegistered(ServiceManager.DEFAULT_NAME));
            
            assertTrue(
                ServiceManagerFactory.setManagerRepository(repository2)
            );
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            assertTrue(repository2.isRegistered(ServiceManager.DEFAULT_NAME));
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * デフォルトのマネージャRepositoryへの変更テスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#setManagerRepository(Repository)の引数に独自Repositoryオブジェクト１を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト１のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#setManagerRepository(Repository)の引数に独自Repositoryオブジェクト２を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト２のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト１のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でfalseが返る。</li>
     *   <li>ServiceManagerFactory#setManagerRepository(Repository)の引数にnullを指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>ServiceManagerFactory#isRegisteredManager(String)の引数に"Nimbus"を指定して呼び出し、戻り値でtrueが返る。</li>
     *   <li>独自Repositoryオブジェクト２のRepository#isRegistered(String)の引数に"Nimbus"を指定して呼び出し、戻り値でfalseが返る。</li>
     * </ul>
     */
    public void testSetManagerRepository6() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(
                ServiceManagerFactory.loadManager()
            );
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            final MyRepository repository1 = new MyRepository();
            assertTrue(ServiceManagerFactory.setManagerRepository(repository1));
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            assertTrue(repository1.isRegistered(ServiceManager.DEFAULT_NAME));
            
            final MyRepository repository2 = new MyRepository();
            assertTrue(ServiceManagerFactory.setManagerRepository(repository2));
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            assertTrue(repository2.isRegistered(ServiceManager.DEFAULT_NAME));
            assertFalse(repository1.isRegistered(ServiceManager.DEFAULT_NAME));
            
            assertTrue(
                ServiceManagerFactory.setManagerRepository((Repository)null)
            );
            assertTrue(
                ServiceManagerFactory.isRegisteredManager(
                    ServiceManager.DEFAULT_NAME
                )
            );
            assertFalse(repository2.isRegistered(ServiceManager.DEFAULT_NAME));
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * ServiceLoaderの登録・登録解除のテスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getLoaders()を呼び出し、戻り値の集合のサイズが1である。</li>
     *   <li>ServiceManagerFactory#getLoader(URL)の引数に"nimbus-service.xml"のURLを指定して呼び出し、戻り値がnullでない。</li>
     *   <li>ServiceManagerFactory#unregisterLoader(ServiceLoader)の引数に取得したServiceLoaderを指定して呼び出す。</li>
     *   <li>ServiceManagerFactory#getLoader(URL)の引数に"nimbus-service.xml"のURLを指定して呼び出し、戻り値がnullである。</li>
     *   <li>ServiceManagerFactory#registerLoader(ServiceLoader)の引数に取得したServiceLoaderを指定して呼び出す。</li>
     *   <li>ServiceManagerFactory#getLoader(URL)の引数に"nimbus-service.xml"のURLを指定して呼び出し、戻り値がnullでない。</li>
     *   <li>ServiceManagerFactory#unloadManager()を呼び出す。</li>
     *   <li>ServiceManagerFactory#getLoader(URL)の引数に"nimbus-service.xml"のURLを指定して呼び出し、戻り値がnullである。</li>
     * </ul>
     */
    public void testRegisterAndUnregisterLoader() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        try{
            assertTrue(ServiceManagerFactory.loadManager());
            Collection loaders = ServiceManagerFactory.getLoaders();
            assertEquals(1, loaders.size());
            final ServiceLoader loader = ServiceManagerFactory.getLoader(
                def.toURL()
            );
            assertNotNull(loader);
            ServiceManagerFactory.unregisterLoader(loader);
            assertNull(ServiceManagerFactory.getLoader(def.toURL()));
            ServiceManagerFactory.registerLoader(loader);
            assertNotNull(ServiceManagerFactory.getLoader(def.toURL()));
            ServiceManagerFactory.unloadManager();
            assertNull(ServiceManagerFactory.getLoader(def.toURL()));
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
        }
    }
    
    public static class MyServiceLoader extends DefaultServiceLoaderService{

        private static final long serialVersionUID = -8181574796579007327L;
    }
    
    /**
     * ServiceLoader実装クラス設定のテスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#setServiceLoaderClass(Class)の引数にServiceLoaderインタフェースを実装していないクラスMyDummyServiceLoaderを指定して呼び出し、IllegalArgumentExceptionが発生する。</li>
     *   <li>ServiceManagerFactory#setServiceLoaderClass(Class)の引数にServiceLoaderインタフェースを実装したクラスMyServiceLoaderを指定して呼び出し、正常に設定できる。</li>
     *   <li>ServiceManagerFactory#getServiceLoaderClass()を呼び出し、戻り値でMyServiceLoaderクラスが取得できる。</li>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getLoader(URL)の引数に"nimbus-service.xml"のURLを指定して呼び出し、戻り値がMyServiceLoaderのインスタンスである。</li>
     *   <li>ServiceManagerFactory#setServiceLoaderClass(Class)の引数にServiceLoaderインタフェースを実装しているが、外部からインスタンス化できないローカルクラスLocalMyServiceLoaderを指定して呼び出し、正常に設定できる。</li>
     *   <li>ServiceManagerFactory#getServiceLoaderClass()を呼び出し、戻り値でLocalMyServiceLoaderクラスが取得できる。</li>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がfalseを返す。</li>
     * </ul>
     */
    public void testServiceLoaderClass1() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        class MyDummyServiceLoader{}
        class LocalMyServiceLoader extends DefaultServiceLoaderService{
            private static final long serialVersionUID = -5571466369298423736L;
        }
        try{
            try{
                ServiceManagerFactory.setServiceLoaderClass(
                    MyDummyServiceLoader.class
                );
                fail("Argument of method setServiceLoaderClass(Class) must be ServiceLoader.");
            }catch(IllegalArgumentException e){
            }
            ServiceManagerFactory.setServiceLoaderClass(MyServiceLoader.class);
            assertEquals(
                MyServiceLoader.class, 
                ServiceManagerFactory.getServiceLoaderClass()
            );
            assertTrue(ServiceManagerFactory.loadManager());
            ServiceLoader loader = ServiceManagerFactory.getLoader(
                def.toURL()
            );
            assertTrue((loader instanceof MyServiceLoader));
            ServiceManagerFactory.unloadManager();
            
            ServiceManagerFactory.setServiceLoaderClass(
                LocalMyServiceLoader.class
            );
            assertEquals(
                LocalMyServiceLoader.class, 
                ServiceManagerFactory.getServiceLoaderClass()
            );
            assertFalse(ServiceManagerFactory.loadManager());
        }finally{
            ServiceManagerFactory.unloadManager();
            ServiceManagerFactory.setServiceLoaderClass(null);
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * システムプロパティによるServiceLoader実装クラス設定のテスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>System.setProperty(String, String)の引数に"jp.ossc.nimbus.core.loader"とServiceLoaderインタフェースを実装していないクラスMyDummyServiceLoaderのクラス名を指定して呼び出す。</li>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getLoader(URL)の引数に"nimbus-service.xml"のURLを指定して呼び出し、戻り値がMyDummyServiceLoaderのインスタンスでない。</li>
     *   <li>ServiceManagerFactory#unloadManager()を呼び出す。</li>
     *   <li>System.setProperty(String, String)の引数に"jp.ossc.nimbus.core.loader"と存在しないクラスUsoUsoServiceLoaderのクラス名を指定して呼び出す。</li>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getLoader(URL)の引数に"nimbus-service.xml"のURLを指定して呼び出し、戻り値がUsoUsoServiceLoaderのインスタンスでない。</li>
     *   <li>ServiceManagerFactory#unloadManager()を呼び出す。</li>
     *   <li>System.setProperty(String, String)の引数に"jp.ossc.nimbus.core.loader"とServiceLoaderインタフェースを実装したクラスMyServiceLoaderのクラス名を指定して呼び出す。</li>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getLoader(URL)の引数に"nimbus-service.xml"のURLを指定して呼び出し、戻り値がMyServiceLoaderのインスタンスである。</li>
     * </ul>
     */
    public void testServiceLoaderClass2() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        class MyDummyServiceLoader{}
        try{
            System.setProperty(
                "jp.ossc.nimbus.core.loader",
                MyDummyServiceLoader.class.getName()
            );
            assertTrue(ServiceManagerFactory.loadManager());
            ServiceLoader loader = ServiceManagerFactory.getLoader(
                def.toURL()
            );
            assertFalse((loader instanceof MyDummyServiceLoader));
            ServiceManagerFactory.unloadManager();
            
            System.setProperty(
                "jp.ossc.nimbus.core.loader",
                "UsoUsoServiceLoader"
            );
            assertTrue(ServiceManagerFactory.loadManager());
            loader = ServiceManagerFactory.getLoader(
                def.toURL()
            );
            assertFalse(
                "UsoUsoServiceLoader".equals(loader.getClass().getName())
            );
            ServiceManagerFactory.unloadManager();
            
            System.setProperty(
                "jp.ossc.nimbus.core.loader",
                MyServiceLoader.class.getName()
            );
            assertTrue(ServiceManagerFactory.loadManager());
            loader = ServiceManagerFactory.getLoader(
                def.toURL()
            );
            assertTrue((loader instanceof MyServiceLoader));
            System.setProperty(
                "jp.ossc.nimbus.core.loader",
                ""
            );
        }finally{
            ServiceManagerFactory.unloadManager();
            ServiceManagerFactory.setServiceLoaderClass(null);
            if(def != null){
                def.delete();
            }
        }
    }
    
    public static class MyServiceManager extends DefaultServiceManagerService{
        private static final long serialVersionUID = -4462377972355321537L;
    }
    
    /**
     * ServiceManager実装クラス設定のテスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#setServiceManagerClass(Class)の引数にServiceManagerインタフェースを実装していないクラスMyDummyServiceManagerを指定して呼び出し、IllegalArgumentExceptionが発生する。</li>
     *   <li>ServiceManagerFactory#setServiceManagerClass(Class)の引数にServiceManagerインタフェースを実装したクラスMyServiceManagerを指定して呼び出し、正常に設定できる。</li>
     *   <li>ServiceManagerFactory#getServiceManagerClass()を呼び出し、戻り値でMyServiceManagerクラスが取得できる。</li>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()を呼び出し、戻り値がMyServiceManagerのインスタンスである。</li>
     *   <li>ServiceManagerFactory#setServiceManagerClass(Class)の引数にServiceManagerインタフェースを実装しているが、外部からインスタンス化できないローカルクラスLocalMyServiceManagerを指定して呼び出し、正常に設定できる。</li>
     *   <li>ServiceManagerFactory#getServiceManagerClass()を呼び出し、戻り値でLocalMyServiceManagerクラスが取得できる。</li>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がfalseを返す。</li>
     * </ul>
     */
    public void testServiceManagerClass1() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        class MyDummyServiceManager{}
        class LocalMyServiceManager extends DefaultServiceManagerService{
            private static final long serialVersionUID = 6353430722805581225L;
        }
        try{
            try{
                ServiceManagerFactory.setServiceManagerClass(
                    MyDummyServiceManager.class
                );
                fail("Argument of method setServiceManagerClass(Class) must be ServiceManager.");
            }catch(IllegalArgumentException e){
            }
            ServiceManagerFactory.setServiceManagerClass(
                MyServiceManager.class
            );
            assertEquals(
                MyServiceManager.class, 
                ServiceManagerFactory.getServiceManagerClass()
            );
            assertTrue(ServiceManagerFactory.loadManager());
            ServiceManager manager = ServiceManagerFactory.findManager();
            assertTrue((manager instanceof MyServiceManager));
            ServiceManagerFactory.unloadManager();
            
            ServiceManagerFactory.setServiceManagerClass(
                LocalMyServiceManager.class
            );
            assertEquals(
                LocalMyServiceManager.class, 
                ServiceManagerFactory.getServiceManagerClass()
            );
            assertFalse(ServiceManagerFactory.loadManager());
        }finally{
            ServiceManagerFactory.unloadManager();
            ServiceManagerFactory.setServiceManagerClass(null);
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * システムプロパティによるServiceManager実装クラス設定のテスト。<p>
     * 条件：
     * <ul>
     *   <li>サービス定義ファイル<a href="resources/nimbus-service.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>System.setProperty(String, String)の引数に"jp.ossc.nimbus.core.manager"とServiceManagerインタフェースを実装していないクラスMyDummyServiceManagerのクラス名を指定して呼び出す。</li>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()を呼び出し、戻り値がMyDummyServiceManagerのインスタンスでない。</li>
     *   <li>ServiceManagerFactory#unloadManager()を呼び出す。</li>
     *   <li>System.setProperty(String, String)の引数に"jp.ossc.nimbus.core.manager"と存在しないクラスUsoUsoServiceManagerのクラス名を指定して呼び出す。</li>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()を呼び出し、戻り値がUsoUsoServiceManagerのインスタンスでない。</li>
     *   <li>ServiceManagerFactory#unloadManager()を呼び出す。</li>
     *   <li>System.setProperty(String, String)の引数に"jp.ossc.nimbus.core.manager"とServiceManagerインタフェースを実装したクラスMyServiceManagerのクラス名を指定して呼び出す。</li>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()を呼び出し、戻り値がMyServiceManagerのインスタンスである。</li>
     * </ul>
     */
    public void testServiceManagerClass2() throws Exception {
        final File def = TestUtility.copyOnClassPath("nimbus-service.xml");
        class MyDummyServiceManager{}
        try{
            System.setProperty(
                "jp.ossc.nimbus.core.manager",
                MyDummyServiceManager.class.getName()
            );
            assertTrue(ServiceManagerFactory.loadManager());
            ServiceManager manager = ServiceManagerFactory.findManager();
            assertFalse((manager instanceof MyDummyServiceManager));
            ServiceManagerFactory.unloadManager();
            
            System.setProperty(
                "jp.ossc.nimbus.core.manager",
                "UsoUsoServiceManager"
            );
            assertTrue(ServiceManagerFactory.loadManager());
            manager = ServiceManagerFactory.findManager();
            assertFalse(
                "UsoUsoServiceManager".equals(manager.getClass().getName())
            );
            ServiceManagerFactory.unloadManager();
            
            System.setProperty(
                "jp.ossc.nimbus.core.manager",
                MyServiceManager.class.getName()
            );
            assertTrue(ServiceManagerFactory.loadManager());
            manager = ServiceManagerFactory.findManager();
            assertTrue((manager instanceof MyServiceManager));
            System.setProperty(
                "jp.ossc.nimbus.core.manager",
                ""
            );
        }finally{
            ServiceManagerFactory.unloadManager();
            ServiceManagerFactory.setServiceManagerClass(null);
            if(def != null){
                def.delete();
            }
        }
    }
    
    /**
     * デフォルトLoggerのテスト。<p>
     * 条件：
     * <ul>
     *   <li>デフォルト名のサービス定義ファイル<a href="resources/nimbus-service5.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManagerFactory#findManager()を呼び出してServiceManagerが取得できる。</li>
     *   <li>ServiceManager#getServiceObject(String)を呼び出してサービス名"Service0"のサービスオブジェクトが取得できる。</li>
     *   <li>ServiceManagerFactory#getLogger()を呼び出してLoggerオブジェクトが取得できる。</li>
     *   <li>取得したLoggerで、Logger#debug(String)を呼び出し、標準出力にデバッグメッセージが出力される。</li>
     *   <li>ServiceManager#getLogger()を呼び出してLoggerオブジェクトが取得できる。</li>
     *   <li>取得したLoggerで、Logger#debug(String)を呼び出し、標準出力にデバッグメッセージが出力される。</li>
     *   <li>TestServiceBase#getLogger()を呼び出してLoggerオブジェクトが取得できる。</li>
     *   <li>取得したLoggerで、Logger#write(String)を呼び出し、標準出力に指定したメッセージIDのメッセージが出力される。</li>
     * </ul>
     */
/*    public void testDefaultLogger() throws Exception {
        File def = null;
        PrintStream ps = null;
        final PrintStream out = System.out;
        final PipedInputStream pis = new PipedInputStream();
        final Set fails = new HashSet();
        try{
            def = TestUtility.copyOnClassPath(
                "nimbus-service5.xml",
                "nimbus-service.xml"
            );
            assertTrue(ServiceManagerFactory.loadManager());
            assertTrue(ServiceManagerFactory.checkLoadManagerCompleted());
            ps = new PrintStream(new PipedOutputStream(pis));
            ServiceManagerFactory.DEFAULT_LOGGER.stop();
            System.setOut(ps);
            ServiceManagerFactory.DEFAULT_LOGGER.start();
            final BufferedReader br = new BufferedReader(
                new InputStreamReader(pis)
            );
            final ServiceManager manager = ServiceManagerFactory.findManager();
            assertNotNull(manager);
            TestServiceBase serviceObj0 = null;
            try{
                serviceObj0 = (TestServiceBase)manager.getServiceObject(
                    "Service0"
                );
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            Logger logger = ServiceManagerFactory.getLogger();
            assertNotNull(logger);
            logger.debug("テスト開始");
            logger = manager.getLogger();
            assertNotNull(logger);
            logger.debug("テスト");
            logger = serviceObj0.getLogger();
            assertNotNull(logger);
            logger.write("SVCM_00035");
            MessageRecordFactory msgRecFactory
                 = ServiceManagerFactory.getMessageRecordFactory();
            assertNotNull(msgRecFactory);
            final String message = msgRecFactory.findMessage("SVCM_00035");
            assertNotNull(message);
            final Thread thread = new Thread(
                new Runnable(){
                    public void run(){
                        try{
                            String line = null;
                            while((line = br.readLine()) != null){
                                if(line.indexOf("テスト開始") != -1){
                                    break;
                                }
                            }
                            line = br.readLine();
                            assertNotNull(line);
                            assertTrue(line.endsWith(",,DEBUG,テスト"));
                            line = br.readLine();
                            assertNotNull(line);
                            assertTrue(
                                line.endsWith(
                                    ",SVCM_00035,SYSTEM_ERROR," + message
                                )
                            );
                        }catch(Exception e){
                            final StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            fail(sw.toString());
                        }catch(junit.framework.AssertionFailedError e){
                            fails.add(e);
                        }
                    }
                }
            );
            thread.setDaemon(true);
            thread.start();
            final Thread currentThread = Thread.currentThread();
            final Thread watcher = new Thread(
                new Runnable(){
                    public void run(){
                        try{
                            Thread.sleep(1000);
                            currentThread.interrupt();
                        }catch(InterruptedException e){
                        }
                    }
                }
            );
            watcher.start();
            try{
                thread.join();
                watcher.interrupt();
            }catch(InterruptedException e){
            }
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
            ServiceManagerFactory.DEFAULT_LOGGER.stop();
            System.setOut(out);
            ServiceManagerFactory.DEFAULT_LOGGER.start();
            if(ps != null){
                ps.close();
            }
            pis.close();
            if(fails.size() != 0){
                throw (Error)fails.iterator().next();
            }
        }
    }*/
    
    /**
     * マネージャLoggerのテスト。<p>
     * 条件：
     * <ul>
     *   <li>デフォルト名のサービス定義ファイル<a href="resources/nimbus-service13.xml">"nimbus-service.xml"</a>をクラスパス上に置く。</li>
     * </ul>
     * 確認：
     * <ul>
     *   <li>ServiceManagerFactory#loadManager()を呼び出し、戻り値がtrueを返す。</li>
     *   <li>ServiceManagerFactory#checkLoadManagerCompleted()がtrueを返す。</li>
     *   <li>ServiceManagerFactory#getLogger()を呼び出してLoggerオブジェクトが取得できる。</li>
     *   <li>取得したLoggerで、Logger#debug(String)を呼び出し、標準出力にデバッグメッセージが出力される。</li>
     *   <li>ServiceManagerFactory#findManager()を呼び出してServiceManagerオブジェクトが取得できる。</li>
     *   <li>取得したServiceManagerで、ServiceManager#getLogger()を呼び出してLoggerオブジェクトが取得できる。</li>
     *   <li>取得したLoggerで、Logger#write(String)を呼び出し、ファイルに指定したメッセージIDのメッセージが出力される。</li>
     *   <li>ServiceManager#getServiceObject(String)を呼び出してサービス名"Service0"のサービスオブジェクトが取得できる。</li>
     *   <li>ServiceBase#getLogger()を呼び出してLoggerオブジェクトが取得できる。</li>
     *   <li>取得したLoggerで、Logger#write(String)を呼び出し、ファイルに指定したメッセージIDのメッセージが出力される。</li>
     * </ul>
     */
/*    public void testManagerLogger() throws Exception {
        File def = null;
        File messageDef = null;
        PrintStream ps = null;
        final PrintStream out = System.out;
        final PipedInputStream pis = new PipedInputStream();
        final Set fails = new HashSet();
        try{
            def = TestUtility.copyOnClassPath(
                "nimbus-service13.xml",
                "nimbus-service.xml"
            );
            messageDef = TestUtility.copyOnClassPath("Message1.def");
            assertTrue(ServiceManagerFactory.loadManager());
            assertTrue(ServiceManagerFactory.checkLoadManagerCompleted());
            ps = new PrintStream(new PipedOutputStream(pis));
            ServiceManagerFactory.DEFAULT_LOGGER.stop();
            System.setOut(ps);
            ServiceManagerFactory.DEFAULT_LOGGER.start();
            final BufferedReader br = new BufferedReader(
                new InputStreamReader(pis)
            );
            Logger logger = ServiceManagerFactory.getLogger();
            assertNotNull(logger);
            logger.debug("テスト開始");
            final ServiceManager manager = ServiceManagerFactory.findManager();
            assertNotNull(manager);
            logger = manager.getLogger();
            assertNotNull(logger);
            logger.write("TEST39999");
            TestServiceBase serviceObj0 = null;
            try{
                serviceObj0 = (TestServiceBase)manager.getServiceObject(
                    "Service0"
                );
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            logger = serviceObj0.getLogger();
            assertNotNull(logger);
            logger.write("TEST49999");
            final Thread thread = new Thread(
                new Runnable(){
                    public void run(){
                        try{
                            String line = null;
                            while((line = br.readLine()) != null){
                                if(line.indexOf("テスト開始") != -1){
                                    break;
                                }
                            }
                        }catch(Exception e){
                            final StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            fail(sw.toString());
                        }catch(junit.framework.AssertionFailedError e){
                            fails.add(e);
                        }
                    }
                }
            );
            thread.setDaemon(true);
            thread.start();
            final Thread currentThread = Thread.currentThread();
            final Thread watcher = new Thread(
                new Runnable(){
                    public void run(){
                        try{
                            Thread.sleep(1000);
                            currentThread.interrupt();
                        }catch(InterruptedException e){
                        }
                    }
                }
            );
            watcher.start();
            try{
                thread.join();
                watcher.interrupt();
            }catch(InterruptedException e){
            }
            MessageRecordFactory msgRecFactory = null;
            try{
                msgRecFactory = (MessageRecordFactory)ServiceManagerFactory
                    .getServiceObject("ApplicationMessage");
            }catch(ServiceNotFoundException e){
                fail(e.getMessage());
            }
            assertNotNull(msgRecFactory);
            BufferedReader br2 = null;
            try{
                br2 = new BufferedReader(
                    new FileReader("temp/ServiceManagerFactoryTest.log")
                );
                String message = msgRecFactory.findMessage("TEST39999");
                assertNotNull(message);
                String line = br2.readLine();
                assertNotNull(line);
                assertTrue(
                    line.endsWith(",TEST39999,APPLICATION_ERROR," + message)
                );
                message = msgRecFactory.findMessage("TEST49999");
                assertNotNull(message);
                line = br2.readLine();
                assertNotNull(line);
                assertTrue(
                    line.endsWith(",TEST49999,APPLICATION_FATAL," + message)
                );
            }finally{
                if(br2 != null){
                    br2.close();
                }
            }
        }finally{
            ServiceManagerFactory.unloadManager();
            if(def != null){
                def.delete();
            }
            if(messageDef != null){
                messageDef.delete();
            }
            ServiceManagerFactory.DEFAULT_LOGGER.stop();
            System.setOut(out);
            ServiceManagerFactory.DEFAULT_LOGGER.start();
            if(ps != null){
                ps.close();
            }
            pis.close();
            if(fails.size() != 0){
                throw (Error)fails.iterator().next();
            }
        }
    }*/
}