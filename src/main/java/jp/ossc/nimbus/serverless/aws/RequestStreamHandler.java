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
package jp.ossc.nimbus.serverless.aws;

import java.io.InputStream;
import java.io.OutputStream;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.util.converter.BeanJSONConverter;
import jp.ossc.nimbus.util.converter.StreamConverter;
import jp.ossc.nimbus.util.converter.BindingStreamConverter;
import jp.ossc.nimbus.util.converter.StreamExchangeConverter;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * 入出力の直列化/非直列化をカスタマイズしたLambdaアプリケーションをJavaプログラムで開発するためのリクエストストリームハンドラ抽象クラス。<p>
 *
 * @author M.Takata
 */
public abstract class RequestStreamHandler<I,O> extends NimbusRequestHandler<InputStream,InputStream,I,O> implements com.amazonaws.services.lambda.runtime.RequestStreamHandler{
    
    protected static final String ENV_INPUT_STREAM_CONVERTER_SERVICE_NAME = "INPUT_STREAM_CONVERTER_SERVICE_NAME";
    protected static final String ENV_OUTPUT_STREAM_CONVERTER_SERVICE_NAME = "OUTPUT_STREAM_CONVERTER_SERVICE_NAME";
    
    protected StreamConverter inputStreamConverter;
    protected StreamConverter outputStreamConverter;
    
    /**
     * 環境変数から入力を変換する{@link StreamConverter}サービスのサービス名を取得する。<p>
     *
     * @return StreamConverterサービスのサービス名
     */
    protected ServiceName getInputStreamConverterServiceName(){
        return getEnvServiceName(ENV_INPUT_STREAM_CONVERTER_SERVICE_NAME);
    }
    
    /**
     * 環境変数から出力を変換する{@link StreamConverter}サービスのサービス名を取得する。<p>
     *
     * @return StreamConverterサービスのサービス名
     */
    protected ServiceName getOutputStreamConverterServiceName(){
        return getEnvServiceName(ENV_OUTPUT_STREAM_CONVERTER_SERVICE_NAME);
    }
    
    protected boolean init(Context context){
        boolean result = super.init(context);
        if(result){
            ServiceName inputStreamConverterServiceName = getInputStreamConverterServiceName();
            if(inputStreamConverterServiceName != null){
                inputStreamConverter = (StreamConverter)ServiceManagerFactory
                    .getServiceObject(inputStreamConverterServiceName);
            }
            ServiceName outputStreamConverterServiceName = getOutputStreamConverterServiceName();
            if(outputStreamConverterServiceName != null){
                outputStreamConverter = (StreamConverter)ServiceManagerFactory
                    .getServiceObject(outputStreamConverterServiceName);
            }
        }
        return result;
    }
    
    public void handleRequest(InputStream is, OutputStream os, Context context){
        InputStream outputIs = null;
        if(!processInit(context)){
            outputIs = processInitError(context);
        }else{
            RequestContext<I, O> rc = processCreateRequestContext(context);
            try{
                if(interceptorChainFactory == null){
                    outputIs = processHandleRequest(is, rc);
                }else{
                    outputIs = processHandleRequestWithInterceptorChain(is, rc);
                }
            }catch(Throwable th){
                outputIs = processHandleError(is, rc, th);
            }
        }
        new StreamExchangeConverter().convert(outputIs, os);
    }
    
    /**
     * 入力変換処理を行う。<p>
     * 入力ストリームを、入力変換の{@link StreamConverter}を使って変換する。<br/>
     *
     * @param input 入力
     * @param context 要求コンテキスト
     * @return 要求コンテキストの入力
     * @exception Throwable 入力変換処理で例外が発生した場合
     */
    protected I processConvertToInput(InputStream is, RequestContext<I, O> context) throws Throwable{
        StreamConverter converter = inputStreamConverter;
        Object inputObj = processCreateInputObject(context);
        if(inputObj == null){
            if(converter == null){
                inputObj = is;
            }else{
                inputObj = converter.convertToObject(is);
            }
        }else{
            if(converter == null){
                inputObj = createBeanJSONConverter().convertToObject(is, inputObj);
            }else{
                if(converter instanceof BindingStreamConverter){
                    inputObj = ((BindingStreamConverter)converter).convertToObject(is, inputObj);
                }else{
                    inputObj = converter.convertToObject(is);
                }
            }
        }
        return (I)inputObj;
    }
    
    /**
     * 要求コンテキストの入力となるオブジェクトを生成する。<p>
     * デフォルトでは、nullを返す。必要に応じて、オーバーライドしてください。<br>
     *
     * @param context 要求コンテキスト
     * @return 要求コンテキストの入力
     * @exception Throwable 入力変換処理で例外が発生した場合
     */
    protected I processCreateInputObject(RequestContext<I, O> context) throws Throwable{
        return null;
    }
    
    /**
     * 出力変換処理を行う。<p>
     * 出力変換の{@link StreamConverter}で変換する。<br/>
     *
     * @param output 出力
     * @return 出力を読みだすストリーム
     */
    protected InputStream processConvertToOutput(O output){
        InputStream is = null;
        if(output != null){
            StreamConverter converter = outputStreamConverter;
            if(converter == null){
                if(output instanceof InputStream){
                    is = (InputStream)output;
                }else{
                    is = createBeanJSONConverter().convertToStream(output);
                }
            }else{
                is = converter.convertToStream(output);
            }
        }
        return is;
    }
    
    protected BeanJSONConverter createBeanJSONConverter(){
        BeanJSONConverter bjc = new BeanJSONConverter();
        bjc.setCharacterEncodingToStream("UTF-8");
        return bjc;
    }
}