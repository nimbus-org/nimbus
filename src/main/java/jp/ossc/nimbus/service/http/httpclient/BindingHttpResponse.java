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
package jp.ossc.nimbus.service.http.httpclient;

import java.io.IOException;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.http.httpclient.HttpResponseImpl;
import jp.ossc.nimbus.util.converter.BindingStreamConverter;
import jp.ossc.nimbus.util.converter.StreamStringConverter;
import jp.ossc.nimbus.util.converter.ConvertException;

/**
 * ペイロードオブジェクトの型を指定できる{@link jp.ossc.nimbus.service.http.HttpResponse HttpResponse}。
 * <p>
 *     StreamConverterプロパティには{@link BindingStreamConverter}実装クラスを指定する。
 * </p>
 * @author T.Okada
 */
public class BindingHttpResponse extends HttpResponseImpl {

    private Object responseObject;

    /**
     * ペイロードオブジェクトの型を設定する。
     */
    public void setResponseObject(Object responseObject) {
        this.responseObject = responseObject;
    }
    
    /**
     * @see HttpResponseImpl#getObject()
     */
    public Object getObject() throws ConvertException {
        if(outputObject == null && (streamConverter != null || streamConverterServiceName != null)) {
            if(streamConverter == null) {
                if(streamConverterServiceName != null) {
                    streamConverter = (BindingStreamConverter)ServiceManagerFactory.getServiceObject(streamConverterServiceName);
                }
            }
            if(!(streamConverter instanceof BindingStreamConverter)) {
                throw new IllegalArgumentException("The type of StreamConverter is not BindingStreamConverter.");
            }
            
            BindingStreamConverter converter = (BindingStreamConverter)streamConverter;
            
            if(method != null && converter instanceof StreamStringConverter){
                converter = (BindingStreamConverter)((StreamStringConverter)converter)
                    .cloneCharacterEncodingToObject(getCharacterEncoding());
            }
            
            if(inputStream != null) {
                try{
                    outputObject = converter.convertToObject(inputStream, responseObject);
                }finally {
                    try {
                        inputStream.reset();
                    }catch(IOException e) {}
                }
            }
        }
        
        return outputObject;
    }

}
