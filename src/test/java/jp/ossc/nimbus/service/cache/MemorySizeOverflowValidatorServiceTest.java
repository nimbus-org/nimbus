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
 * メモリサイズあふれ検証サービステスト。<p>
 *
 * @author M.Takata
 */
public class MemorySizeOverflowValidatorServiceTest extends TestCase{
    
    public MemorySizeOverflowValidatorServiceTest(String theName){
        super(theName);
    }
    
    public static void main(String[] theArgs){
        junit.swingui.TestRunner.main(
            new String[]{MemorySizeOverflowValidatorServiceTest.class.getName()}
        );
    }
    
    public static Test suite(){
        return new TestSuite(MemorySizeOverflowValidatorServiceTest.class);
    }
    
    public void testDefault() throws Exception{
        final MemorySizeOverflowValidatorService validator
             = new MemorySizeOverflowValidatorService();
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
    
    public void testSetMaxHeapMemorySize() throws Exception{
        final MemorySizeOverflowValidatorService validator
             = new MemorySizeOverflowValidatorService();
        try{
            validator.setMaxHeapMemorySize("A");
            fail("IllegalArgumentException must throw.");
        }catch(IllegalArgumentException e){
        }
        try{
            validator.setMaxHeapMemorySize("100KBytes");
            fail("IllegalArgumentException must throw.");
        }catch(IllegalArgumentException e){
        }
        try{
            assertEquals(
                Long.toString(Runtime.getRuntime().maxMemory()),
                validator.getMaxHeapMemorySize()
            );
        }catch(NoSuchMethodError err){
            assertEquals(
                "64M",
                validator.getMaxHeapMemorySize()
            );
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
    
    public void testSetHighHeapMemorySize() throws Exception{
        final MemorySizeOverflowValidatorService validator
             = new MemorySizeOverflowValidatorService();
        try{
            validator.setHighHeapMemorySize("A");
            fail("IllegalArgumentException must throw.");
        }catch(IllegalArgumentException e){
        }
        try{
            validator.setHighHeapMemorySize("100KBytes");
            fail("IllegalArgumentException must throw.");
        }catch(IllegalArgumentException e){
        }
        try{
            assertEquals(
                Long.toString(Runtime.getRuntime().maxMemory() / 2),
                validator.getHighHeapMemorySize()
            );
        }catch(NoSuchMethodError err){
            assertEquals(
                "32M",
                validator.getHighHeapMemorySize()
            );
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
    
    public void testSetMaxAndHighHeapMemorySize() throws Exception{
        final MemorySizeOverflowValidatorService validator
             = new MemorySizeOverflowValidatorService();
        validator.setMaxHeapMemorySize("32M");
        validator.setHighHeapMemorySize("64M");
        validator.create();
        try{
            validator.start();
            fail("IllegalArgumentException must throw.");
        }catch(IllegalArgumentException e){
        }
        System.gc();
        final Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        final long highHeapMemory = usedMemory + 2 * 1024 * 1024;
        final long maxHeapMemory = usedMemory + 3 * 1024 * 1024;
        validator.setHighHeapMemorySize(Long.toString(highHeapMemory));
        validator.setMaxHeapMemorySize(Long.toString(maxHeapMemory));
        validator.create();
        validator.start();
        
        int count = 0;
        while(true){
            final byte[] bytes = new byte[1024];
            final CachedReference ref = new DefaultCachedReference(bytes);
            validator.add(ref);
            count++;
            final int overflowSize = validator.validate();
            usedMemory = runtime.totalMemory() - runtime.freeMemory();
            if(usedMemory < highHeapMemory + bytes.length - 100){
                assertEquals(0, overflowSize);
            }else if(usedMemory > highHeapMemory + bytes.length + 100){
                assertFalse(0 == overflowSize);
                if(usedMemory > maxHeapMemory){
                    assertEquals(count, overflowSize);
                    break;
                }
            }
        }
        
        validator.stop();
        validator.destroy();
    }
    
    public void testReset() throws Exception{
        final MemorySizeOverflowValidatorService validator
             = new MemorySizeOverflowValidatorService();
        System.gc();
        final Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        final long highHeapMemory = usedMemory + 2 * 1024 * 1024;
        final long maxHeapMemory = usedMemory + 3 * 1024 * 1024;
        validator.setHighHeapMemorySize(Long.toString(highHeapMemory));
        validator.setMaxHeapMemorySize(Long.toString(maxHeapMemory));
        validator.create();
        validator.start();
        
        int count = 0;
        while(true){
            final byte[] bytes = new byte[1024];
            final CachedReference ref = new DefaultCachedReference(bytes);
            validator.add(ref);
            count++;
            final int overflowSize = validator.validate();
            usedMemory = runtime.totalMemory() - runtime.freeMemory();
            if(usedMemory < highHeapMemory + bytes.length - 100){
                assertEquals(0, overflowSize);
            }else if(usedMemory > highHeapMemory + bytes.length + 100){
                assertFalse(0 == overflowSize);
                validator.reset();
                assertEquals(0, validator.validate());
                break;
            }
        }
        
        validator.stop();
        validator.destroy();
    }
}
