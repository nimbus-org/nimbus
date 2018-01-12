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

import junit.framework.*;

/**
 * キャッシュサイズあふれ検証サービステスト。<p>
 *
 * @author M.Takata
 */
public class CacheSizeOverflowValidatorServiceTest extends TestCase{
    
    public CacheSizeOverflowValidatorServiceTest(String theName){
        super(theName);
    }
    
    public static void main(String[] theArgs){
        junit.swingui.TestRunner.main(
            new String[]{CacheSizeOverflowValidatorServiceTest.class.getName()}
        );
    }
    
    public static Test suite(){
        return new TestSuite(CacheSizeOverflowValidatorServiceTest.class);
    }
    
    public void testDefault() throws Exception{
        final CacheSizeOverflowValidatorService validator
             = new CacheSizeOverflowValidatorService();
        validator.create();
        validator.start();
        for(int i = 0; i < 10; i++){
            final String str = "TEST" + i;
            final CachedReference ref = new DefaultCachedReference(str);
            validator.add(ref);
            assertEquals(0, validator.validate());
        }
        validator.stop();
        validator.destroy();
    }
    
    public void testSetMaxSize1() throws Exception{
        final CacheSizeOverflowValidatorService validator
             = new CacheSizeOverflowValidatorService();
        try{
            validator.setMaxSize(-1);
            fail("IllegalArgumentException must throw.");
        }catch(IllegalArgumentException e){
        }
        validator.create();
        validator.start();
        for(int i = 0; i < 10; i++){
            final String str = "TEST" + i;
            final CachedReference ref = new DefaultCachedReference(str);
            validator.add(ref);
            assertEquals(0, validator.validate());
        }
        validator.stop();
        validator.destroy();
    }
    
    public void testSetMaxSize2() throws Exception{
        final CacheSizeOverflowValidatorService validator
             = new CacheSizeOverflowValidatorService();
        validator.setMaxSize(0);
        validator.create();
        validator.start();
        for(int i = 0; i < 10; i++){
            final String str = "TEST" + i;
            final CachedReference ref = new DefaultCachedReference(str);
            validator.add(ref);
            assertEquals(0, validator.validate());
        }
        validator.stop();
        validator.destroy();
    }
    
    public void testSetMaxSize3() throws Exception{
        final CacheSizeOverflowValidatorService validator
             = new CacheSizeOverflowValidatorService();
        validator.setMaxSize(5);
        validator.create();
        validator.start();
        for(int i = 0; i < 10; i++){
            final String str = "TEST" + i;
            final CachedReference ref = new DefaultCachedReference(str);
            validator.add(ref);
            if(i < 5){
                assertEquals(0, validator.validate());
            }else{
                assertEquals(i - 4, validator.validate());
            }
        }
        validator.stop();
        validator.destroy();
    }
    
    public void testSetMaxSize4() throws Exception{
        final CacheSizeOverflowValidatorService validator
             = new CacheSizeOverflowValidatorService();
        validator.setMaxSize(10);
        validator.create();
        validator.start();
        for(int i = 0; i < 10; i++){
            final String str = "TEST" + i;
            final CachedReference ref = new DefaultCachedReference(str);
            validator.add(ref);
            assertEquals(0, validator.validate());
        }
        validator.stop();
        validator.destroy();
    }
    
    public void testReset() throws Exception{
        final CacheSizeOverflowValidatorService validator
             = new CacheSizeOverflowValidatorService();
        validator.setMaxSize(5);
        validator.create();
        validator.start();
        for(int i = 0; i < 10; i++){
            final String str = "TEST" + i;
            final CachedReference ref = new DefaultCachedReference(str);
            if(i == 5){
                validator.reset();
            }
            validator.add(ref);
            assertEquals(0, validator.validate());
        }
        validator.stop();
        validator.destroy();
    }
}
