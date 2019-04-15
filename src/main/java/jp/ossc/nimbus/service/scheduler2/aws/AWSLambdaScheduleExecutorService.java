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
package jp.ossc.nimbus.service.scheduler2.aws;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import jp.ossc.nimbus.service.scheduler2.ScheduleStateControlException;
import jp.ossc.nimbus.util.converter.BeanJSONConverter;
import jp.ossc.nimbus.util.converter.ConvertException;
import jp.ossc.nimbus.util.converter.Converter;
import jp.ossc.nimbus.util.converter.DateFormatConverter;

/**
 * AWS Lambdaを呼び出すスケジュール実行。<p>
 *
 * @author M.Ishida
 */
public class AWSLambdaScheduleExecutorService extends AWSWebServiceScheduleExecutorService implements AWSLambdaScheduleExecutorServiceMBean {
    
    private static final long serialVersionUID = 6075236051813713742L;
    
    protected String encoding = DEFAULT_ENCODING;
    
    {
        type = DEFAULT_EXECUTOR_TYPE;
    }
    
    public String getEncoding() {
        return encoding;
    }
    
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    
    public void startService() throws Exception {
        super.startService();
        
        BeanJSONConverter beanJSONConverter = new BeanJSONConverter();
        DateFormatConverter dfc = new DateFormatConverter();
        dfc.setFormat("yyyy/MM/dd HH:mm:ss.SSS");
        dfc.setConvertType(DateFormatConverter.DATE_TO_STRING);
        beanJSONConverter.setFormatConverter(java.util.Date.class, dfc);
        ByteBufferToStringConverter byteBufferToStringConverter = new ByteBufferToStringConverter();
        if(encoding != null){
            byteBufferToStringConverter.setEncoding(encoding);
        }
        beanJSONConverter.setFormatConverter(ByteBuffer.class, byteBufferToStringConverter);
        addAutoInputConvertMappings(beanJSONConverter);
        addAutoOutputConvertMappings(beanJSONConverter);
    }
    
    public boolean controlState(String id, int cntrolState) throws ScheduleStateControlException {
        return false;
    }
    
    public static class ByteBufferToStringConverter implements Converter {
        
        protected String encoding;
        
        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }
        
        public Object convert(Object obj) throws ConvertException {
            if(obj == null){
                return null;
            }
            try{
                return encoding == null ? new String(((ByteBuffer) obj).array()) : new String(((ByteBuffer) obj).array(), encoding);
            }catch (UnsupportedEncodingException e){
                throw new ConvertException(e);
            }
        }
        
    }
}