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
package jp.ossc.nimbus.service.cache;

import java.util.*;

import junit.framework.*;

/**
 * LRUあふれアルゴリズムサービステスト。<p>
 *
 * @author M.Takata
 */
public class LRUOverflowAlgorithmServiceTest extends TestCase{
    
    public LRUOverflowAlgorithmServiceTest(String theName){
        super(theName);
    }
    
    public static void main(String[] theArgs){
        junit.swingui.TestRunner.main(
            new String[]{LRUOverflowAlgorithmServiceTest.class.getName()}
        );
    }
    
    public static Test suite(){
        return new TestSuite(LRUOverflowAlgorithmServiceTest.class);
    }
    
    public void testDefault1() throws Exception{
        final LRUOverflowAlgorithmService algorithm
             = new LRUOverflowAlgorithmService();
        algorithm.create();
        algorithm.start();
        for(int i = 0; i < 10; i++){
            final String str = "TEST" + i;
            final CachedReference ref = new DefaultCachedReference(str);
            algorithm.add(ref);
            Thread.sleep(50);
            if(i > 4){
                final CachedReference overflow = algorithm.overflow();
                assertNotNull(overflow);
                assertEquals("TEST" + (i - 5), overflow.get(this));
            }
        }
        algorithm.stop();
        algorithm.destroy();
    }
    
    public void testDefault2() throws Exception{
        final LRUOverflowAlgorithmService algorithm
             = new LRUOverflowAlgorithmService();
        algorithm.create();
        algorithm.start();
        for(int i = 0; i < 10; i++){
            final String str = "TEST" + i;
            final CachedReference ref = new DefaultCachedReference(str);
            algorithm.add(ref);
            Thread.sleep(50);
            if(i > 4){
                for(int j = 0; j < 2; j++){
                    final CachedReference overflow = algorithm.overflow();
                    assertNotNull(overflow);
                    assertEquals("TEST" + ((i - 5)*2 + j), overflow.get(this));
                }
            }
        }
        algorithm.stop();
        algorithm.destroy();
    }
    
    public void testDefault3() throws Exception{
        final LRUOverflowAlgorithmService algorithm
             = new LRUOverflowAlgorithmService();
        algorithm.create();
        algorithm.start();
        final List list = new ArrayList();
        for(int i = 0; i < 10; i++){
            final String str = "TEST" + i;
            final CachedReference ref = new DefaultCachedReference(str);
            algorithm.add(ref);
            list.add(ref);
            Thread.sleep(50);
        }
        Iterator refs = list.iterator();
        int count = 0;
        while(refs.hasNext()){
            final CachedReference ref = (CachedReference)refs.next();
            if(count < 5){
                ref.get(this);
            }else{
                break;
            }
            Thread.sleep(50);
            count++;
        }
        refs = list.iterator();
        count = 0;
        while(refs.hasNext()){
            final CachedReference ref = (CachedReference)refs.next();
            assertNotNull(ref);
            final CachedReference overflow = algorithm.overflow();
            assertNotNull(overflow);
            final Object obj = overflow.get(this);
            if(count < 5){
                assertEquals("TEST" + (count + 5), obj);
            }else{
                assertEquals("TEST" + (count - 5), obj);
            }
            count++;
        }
        algorithm.stop();
        algorithm.destroy();
    }
    
    public void testReset() throws Exception{
        final LRUOverflowAlgorithmService algorithm
             = new LRUOverflowAlgorithmService();
        algorithm.create();
        algorithm.start();
        for(int i = 0; i < 10; i++){
            final String str = "TEST" + i;
            final CachedReference ref = new DefaultCachedReference(str);
            algorithm.add(ref);
            Thread.sleep(50);
            if(i > 4){
                if(i == 8){
                    algorithm.reset();
                }
                final CachedReference overflow = algorithm.overflow();
                if(i < 8){
                    assertNotNull(overflow);
                    assertEquals("TEST" + (i - 5), overflow.get(this));
                }else if(i == 8){
                    assertNull(overflow);
                }else{
                    assertNotNull(overflow);
                    assertEquals("TEST" + i, overflow.get(this));
                }
            }
        }
        algorithm.stop();
        algorithm.destroy();
    }
}
