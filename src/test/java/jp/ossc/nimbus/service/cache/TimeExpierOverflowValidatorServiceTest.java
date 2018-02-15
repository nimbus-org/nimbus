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
public class TimeExpierOverflowValidatorServiceTest extends TestCase{
    
    public TimeExpierOverflowValidatorServiceTest(String theName){
        super(theName);
    }
    
    public static void main(String[] theArgs){
        junit.swingui.TestRunner.main(
            new String[]{TimeExpierOverflowValidatorServiceTest.class.getName()}
        );
    }
    
    public static Test suite(){
        return new TestSuite(TimeExpierOverflowValidatorServiceTest.class);
    }
    
    public void testDefault() throws Exception{
        final TimeExpierOverflowValidatorService validator
             = new TimeExpierOverflowValidatorService();
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
    
    public void testSetExpierTerm1() throws Exception{
        final TimeExpierOverflowValidatorService validator
             = new TimeExpierOverflowValidatorService();
        validator.setExpierTerm(-1);
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
    
    public void testSetExpierTerm2() throws Exception{
        final TimeExpierOverflowValidatorService validator
             = new TimeExpierOverflowValidatorService();
        validator.setExpierTerm(0);
        validator.create();
        validator.start();
        for(int i = 0; i < 10; i++){
            final String str = "TEST" + i;
            final CachedReference ref = new DefaultCachedReference(str);
            validator.add(ref);
            assertEquals(i + 1, validator.validate());
        }
        validator.stop();
        validator.destroy();
    }
    
    public void testSetExpierTerm3() throws Exception{
        final TimeExpierOverflowValidatorService validator
             = new TimeExpierOverflowValidatorService();
        validator.setExpierTerm(1000);
        validator.create();
        validator.start();
        long start = System.currentTimeMillis();
        for(int i = 0; i < 10; i++){
            final String str = "TEST" + i;
            final CachedReference ref = new DefaultCachedReference(str);
            validator.add(ref);
            long current = System.currentTimeMillis();
            Thread.sleep((300 * (i + 1)) - (current - start));
            current = System.currentTimeMillis();
            if(current - start < 1000){
                assertEquals(0, validator.validate());
            }else{
                assertEquals((current - start - 1000) / 300 + 1, validator.validate());
            }
        }
        validator.stop();
        validator.destroy();
    }
    
    public void testReset() throws Exception{
        final TimeExpierOverflowValidatorService validator
             = new TimeExpierOverflowValidatorService();
        validator.setExpierTerm(0);
        validator.create();
        validator.start();
        for(int i = 0; i < 10; i++){
            final String str = "TEST" + i;
            final CachedReference ref = new DefaultCachedReference(str);
            validator.reset();
            validator.add(ref);
            assertEquals(1, validator.validate());
        }
        validator.stop();
        validator.destroy();
    }
    
    public void testSetPeriod1() throws Exception {
        final TimeExpierOverflowValidatorService validator
            = new TimeExpierOverflowValidatorService();
        validator.setPeriod(-1);
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

    public void testSetPeriod2() throws Exception {
        final TimeExpierOverflowValidatorService validator
            = new TimeExpierOverflowValidatorService();
        validator.setPeriod(0);
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

    public void testSetPeriod3() throws Exception {
        final TimeExpierOverflowValidatorService validator
            = new TimeExpierOverflowValidatorService();
        validator.setPeriod(1500);
        validator.create();
        validator.start();

        final String str = "TEST";
        final CachedReference ref = new DefaultCachedReference(str);
        long start = System.currentTimeMillis();
        if ((start % 1500) != 0) {
            start = start + 1500 - (start % 1500);
            Thread.sleep(1500 - (start % 1500));
        }
        
        validator.add(ref);
        
        long current = System.currentTimeMillis();
        if(start + 500 > current){
            Thread.sleep(start + 500 - current);
        }
        assertEquals(0, validator.validate());
        
        current = System.currentTimeMillis();
        if(start + 1000 > current){
            Thread.sleep(start + 1000 - current);
        }
        assertEquals(0, validator.validate());

        current = System.currentTimeMillis();
        if(start + 1600 > current){
            Thread.sleep(start + 1600 - current);
        }
        assertEquals(1, validator.validate());

        validator.stop();
        validator.destroy();
    }

    public void testSetPeriod4() throws Exception {
        final TimeExpierOverflowValidatorService validator
            = new TimeExpierOverflowValidatorService();
        validator.setPeriod(1500);
        validator.create();
        validator.start();
        
        long start = System.currentTimeMillis();
        if ((start % 1500) != 0) {
            start = start + 1500 - (start % 1500);
            Thread.sleep(1500 - (start % 1500));
        }

        validator.add(new DefaultCachedReference("TEST1"));
        validator.add(new DefaultCachedReference("TEST2"));
        validator.add(new DefaultCachedReference("TEST3"));
        validator.add(new DefaultCachedReference("TEST4"));
        validator.add(new DefaultCachedReference("TEST5"));

        long current = System.currentTimeMillis();
        if(start + 500 > current){
            Thread.sleep(start + 500 - current);
        }
        assertEquals(0, validator.validate());
        
        current = System.currentTimeMillis();
        if(start + 1000 > current){
            Thread.sleep(start + 1000 - current);
        }
        assertEquals(0, validator.validate());

        current = System.currentTimeMillis();
        if(start + 1600 > current){
            Thread.sleep(start + 1600 - current);
        }
        assertEquals(5, validator.validate());

        validator.stop();
        validator.destroy();
    }

    public void testSetPeriod5() throws Exception {
        final TimeExpierOverflowValidatorService validator
            = new TimeExpierOverflowValidatorService();
        validator.setPeriod(1500);
        validator.create();
        validator.start();

        for (int i = 0; i < 10; i++) {
            long millis = System.currentTimeMillis();
            validator.reset();
            final CachedReference ref = new DefaultCachedReference("TEST");
            if ((millis % 1500) != 0) {
                // 次の有効区切りまでスリープ
                Thread.sleep(1500 - (millis % 1500));
            }

            validator.add(ref);
            // 削除されない
            assertEquals(0, validator.validate());
            // 次の有効区切りまでスリープ
            Thread.sleep(1500);
            // 次の有効区切りで削除
            assertEquals(1, validator.validate());
        }

        validator.stop();
        validator.destroy();
    }

    public void testSetExpierTermPeriod1() throws Exception {
        long expierTime = 1000;
        long period = 500;
        
        final TimeExpierOverflowValidatorService validator
            = new TimeExpierOverflowValidatorService();
        validator.setExpierTerm(expierTime);
        validator.setPeriod(period);
        validator.create();
        validator.start();

        final CachedReference ref = new DefaultCachedReference("TEST");
        
        long start = System.currentTimeMillis();
        if ((start % period) != 0) {
            start = start + period - (start % period);
            // 次の有効区切りまでスリープ
            Thread.sleep(period - (start % period));
        }
        
        validator.add(ref);
        assertEquals(0, validator.validate());
        
        long current = System.currentTimeMillis();
        if(start + (period * 1.4) > current){
            Thread.sleep(start + (long)(period * 1.4) - current);
        }
        
        // 有効区切りで削除
        assertEquals(1, validator.validate());
        validator.add(new DefaultCachedReference("TEST2"));
        
        current = System.currentTimeMillis();
        if(start + (period * 1.5) > current){
            Thread.sleep(start + (long)(period * 1.5) - current);
        }
        validator.add(new DefaultCachedReference("TEST3"));
        assertEquals(1, validator.validate());

        current = System.currentTimeMillis();
        if(start + (period * 2.5) > current){
            Thread.sleep(start + (long)(period * 2.5) - current);
        }
        // 有効区切りで3件削除
        assertEquals(3, validator.validate());
    }

    public void testSetExpierTermPeriod2() throws Exception {
        long expierTime = 1000;
        long period = 1500;
        
        final TimeExpierOverflowValidatorService validator
            = new TimeExpierOverflowValidatorService();
        validator.setExpierTerm(expierTime);
        validator.setPeriod(period);
        validator.create();
        validator.start();

        final CachedReference ref = new DefaultCachedReference("TEST");
        
        long start = System.currentTimeMillis();
        if ((start % period) != 0) {
            start = start + period - (start % period);
            // 次の有効区切りまでスリープ
            Thread.sleep(period - (start % period));
        }
        
        validator.add(ref);
        assertEquals(0, validator.validate());
        
        // 有効期間までスリープ
        long current = System.currentTimeMillis();
        if(start + (expierTime * 1.5) > current){
            Thread.sleep(start + (long)(expierTime * 1.5) - current);
        }
        // 有効期間切れで削除
        assertEquals(1, validator.validate());
        validator.add(new DefaultCachedReference("TEST2"));
        
        current = System.currentTimeMillis();
        if(start + (expierTime * 1.5) > current){
            Thread.sleep(start + (long)(expierTime * 1.5) - current);
        }
        validator.add(new DefaultCachedReference("TEST3"));
        
        current = System.currentTimeMillis();
        Thread.sleep((long)((period - (current - 9 * 60 * 60 * 1000) % period) * 1.5));
        // 有効区切りを超えたのですべて削除
        assertEquals(3, validator.validate());
    }

}
