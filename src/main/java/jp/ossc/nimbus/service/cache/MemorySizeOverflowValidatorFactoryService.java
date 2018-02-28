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

import jp.ossc.nimbus.core.*;

/**
 * メモリサイズあふれ検証ファクトリ。<p>
 * {@link MemorySizeOverflowValidatorService}を生成するファクトリサービスである。<br>
 *
 * @author M.Takata
 * @see MemorySizeOverflowValidatorService
 */
public class MemorySizeOverflowValidatorFactoryService
 extends ServiceFactoryServiceBase
 implements MemorySizeOverflowValidatorFactoryServiceMBean, java.io.Serializable{
    
    private static final long serialVersionUID = -4609416416411165086L;
    
    private final MemorySizeOverflowValidatorService template
         = new MemorySizeOverflowValidatorService();
    
    /**
     * {@link MemorySizeOverflowValidatorService}サービスを生成する。<p>
     *
     * @return MemorySizeOverflowValidatorServiceサービス
     * @exception Exception MemorySizeOverflowValidatorServiceの生成・起動に失敗した場合
     * @see MemorySizeOverflowValidatorService
     */
    protected Service createServiceInstance() throws Exception{
        final MemorySizeOverflowValidatorService validator
             = new MemorySizeOverflowValidatorService();
        validator.setHighHeapMemorySize(template.getHighHeapMemorySize());
        validator.setMaxHeapMemorySize(template.getMaxHeapMemorySize());
        return validator;
    }
    
    // MemorySizeOverflowValidatorFactoryServiceMBeanのJavaDoc
    public void setMaxHeapMemorySize(String size)
     throws IllegalArgumentException{
        template.setMaxHeapMemorySize(size);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final MemorySizeOverflowValidatorService validator
                 = (MemorySizeOverflowValidatorService)instances.next();
            validator.setMaxHeapMemorySize(size);
        }
    }
    
    // MemorySizeOverflowValidatorFactoryServiceMBeanのJavaDoc
    public String getMaxHeapMemorySize(){
        return template.getMaxHeapMemorySize();
    }
    
    // MemorySizeOverflowValidatorFactoryServiceMBeanのJavaDoc
    public void setHighHeapMemorySize(String size)
     throws IllegalArgumentException{
        template.setHighHeapMemorySize(size);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final MemorySizeOverflowValidatorService validator
                 = (MemorySizeOverflowValidatorService)instances.next();
            validator.setHighHeapMemorySize(size);
        }
    }
    
    // MemorySizeOverflowValidatorFactoryServiceMBeanのJavaDoc
    public String getHighHeapMemorySize(){
        return template.getHighHeapMemorySize();
    }
    
    // MemorySizeOverflowValidatorFactoryServiceMBeanのJavaDoc
    public void reset(){
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final MemorySizeOverflowValidatorService validator
                 = (MemorySizeOverflowValidatorService)instances.next();
            validator.reset();
        }
    }
}
