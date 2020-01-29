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

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.StringTokenizer;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.util.converter.Converter;
import jp.ossc.nimbus.util.converter.BindingConverter;
import jp.ossc.nimbus.util.converter.BeanJSONConverter;
import jp.ossc.nimbus.util.converter.StreamConverter;
import jp.ossc.nimbus.util.converter.BindingStreamConverter;
import jp.ossc.nimbus.util.converter.StringStreamConverter;
import jp.ossc.nimbus.util.converter.StreamStringConverter;
import jp.ossc.nimbus.util.converter.BeanExchangeConverter;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

/**
 * APIGateway経由でのLambdaアプリケーションをJavaプログラムで開発するためのリクエストハンドラ抽象クラス。<p>
 * <table border="1">
 *   <tr bgcolor="#CCCCFF"><th>環境変数名</th><th>値</th></tr>
 *   <tr><td>{@link #ENV_INPUT_CONVERTER_SERVICE_NAME}</td><td>入力を変換する{@link Converter}サービスのサービス名を指定する</td></tr>
 *   <tr><td>{@link #ENV_OUTPUT_CONVERTER_SERVICE_NAME}</td><td>出力を変換する{@link Converter}サービスのサービス名を指定する</td></tr>
 * </table>
 *
 * @author M.Takata
 */
public abstract class APIGatewayRequestHandler<I,O> extends NimbusRequestHandler<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent,I,O> implements com.amazonaws.services.lambda.runtime.RequestHandler<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent>{
    
    protected static final String ENV_INPUT_CONVERTER_SERVICE_NAME = "INPUT_CONVERTER_SERVICE_NAME";
    protected static final String ENV_OUTPUT_CONVERTER_SERVICE_NAME = "OUTPUT_CONVERTER_SERVICE_NAME";
    
    protected static final String HEADER_NAME_CONTENT_TYPE = "Content-Type";
    
    protected Converter inputConverter;
    protected Converter outputConverter;
    
    /**
     * 環境変数から入力を変換する{@link Converter}サービスのサービス名を取得する。<p>
     *
     * @return Converterサービスのサービス名
     */
    protected ServiceName getInputConverterServiceName(){
        return getEnvServiceName(ENV_INPUT_CONVERTER_SERVICE_NAME);
    }
    
    /**
     * 環境変数から出力を変換する{@link Converter}サービスのサービス名を取得する。<p>
     *
     * @return Converterサービスのサービス名
     */
    protected ServiceName getOutputConverterServiceName(){
        return getEnvServiceName(ENV_OUTPUT_CONVERTER_SERVICE_NAME);
    }
    
    protected boolean init(Context context){
        boolean result = super.init(context);
        if(result){
            ServiceName inputConverterServiceName = getInputConverterServiceName();
            if(inputConverterServiceName != null){
                inputConverter = (Converter)ServiceManagerFactory
                    .getServiceObject(inputConverterServiceName);
            }
            ServiceName outputConverterServiceName = getOutputConverterServiceName();
            if(outputConverterServiceName != null){
                outputConverter = (Converter)ServiceManagerFactory
                    .getServiceObject(outputConverterServiceName);
            }
        }
        return result;
    }
    
    /**
     * リクエスト処理全体の制御を行う。<p>
     * <ol>
     * <li>{@link #processInit(Context) 初期化処理}を行う。初期化処理で失敗した場合は、{@link #processInitError(Context) 初期化エラー処理}を行う。</li>
     * <li>初期化処理に成功すると、{@link #processCreateRequestContext(Object, Context) 要求コンテキストの生成}を行う。</li>
     * <li>{@link InterceptorChainFactory}が設定されていない場合は、{@link #processHandleRequest(RequestContext) リクエスト処理}にを行う。設定されている場合は、{@link #processHandleRequestWithInterceptorChain(RequestContext) インターセプタ付きリクエスト処理}を行う。</li>
     * <li>例外が発生した場合は、{@link #processHandleError(RequestContext, Throwable) エラー処理}を行う。</li>
     * <li>{@link #processConvertToOutput(RequestContext) 出力変換処理}を行う。</li>
     * </ol>
     *
     * @param input 入力
     * @param context コンテキスト
     * @return 出力
     */
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context){
        if(!processInit(context)){
            return processInitError(context);
        }else{
            RequestContext<I, O> rc = processCreateRequestContext(input, context);
            try{
                if(interceptorChainFactory == null){
                    processHandleRequest(rc);
                }else{
                    processHandleRequestWithInterceptorChain(rc);
                }
            }catch(Throwable th){
                processHandleError(rc, th);
            }
            return processConvertToOutput(rc);
        }
    }
    
    protected RequestContext<I,O> processCreateRequestContext(APIGatewayProxyRequestEvent request, Context context){
        APIGatewayRequestContext<I,O> rc = new APIGatewayRequestContext<I,O>(request, context);
        rc.setInput(processConvertToRequestContextInput(request, context));
        return rc;
    }
    
    /**
     * 入力変換処理を行う。<p>
     * {@link APIGatewayProxyRequestEvent リクエスト}のパラメータやボディを、入力変換の{@link Converter}を使って変換する。<br/>
     *
     * @param request リクエスト
     * @param context コンテキスト
     * @return 要求コンテキストの入力
     */
    protected I processConvertToRequestContextInput(APIGatewayProxyRequestEvent request, Context context){
        Converter converter = inputConverter;
        if(converter != null && converter instanceof StreamStringConverter){
            String characterEncoding = getCharacterEncoding(request);
            if(characterEncoding != null && !characterEncoding.equalsIgnoreCase(((StreamStringConverter)converter).getCharacterEncodingToObject())){
                converter = ((StreamStringConverter)converter).cloneCharacterEncodingToObject(characterEncoding);
            }
        }
        Object inputObj = processCreateInputObject(request, context);
        String method = request.getHttpMethod();
        if("GET".equals(method)){
            Map<String,Object> inputMap = new HashMap<String,Object>();
            Map<String,String> pathParams = request.getPathParameters();
            if(pathParams != null && pathParams.size() != 0){
                inputMap.putAll(pathParams);
            }
            Map<String,String> queryParams = request.getQueryStringParameters();
            if(queryParams != null && queryParams.size() != 0){
                inputMap.putAll(queryParams);
            }
            Map<String,List<String>> mvQueryParams = request.getMultiValueQueryStringParameters();
            if(mvQueryParams != null && mvQueryParams.size() != 0){
                inputMap.putAll(mvQueryParams);
            }
            
            if(inputObj == null){
                if(converter == null){
                    inputObj = inputMap;
                }else{
                    inputObj = converter.convert(inputMap);
                }
            }else{
                if(converter == null){
                    inputObj = new BeanExchangeConverter().convert(inputMap, inputObj);
                }else if(converter instanceof BindingConverter){
                    inputObj = ((BindingConverter)converter).convert(inputMap, inputObj);
                }else{
                    inputObj = converter.convert(inputMap);
                }
            }
        }else{
            String body = request.getBody();
            if(body != null && body.length() > 0){
                if(inputObj == null){
                    if(converter == null){
                        String encoding = getCharacterEncoding(request);
                        inputObj = createBeanJSONConverter(encoding).convertToObject(
                            createStringStreamConverter(encoding).convertToStream(body)
                        );
                    }else if(converter instanceof StreamConverter){
                        inputObj = ((StreamConverter)converter).convertToObject(
                            createStringStreamConverter(getCharacterEncoding(request)).convertToStream(body)
                        );
                    }else{
                        inputObj = converter.convert(body);
                    }
                }else{
                    if(converter == null){
                        String encoding = getCharacterEncoding(request);
                        inputObj = createBeanJSONConverter(encoding).convertToObject(
                            createStringStreamConverter(encoding).convertToStream(body),
                            inputObj
                        );
                    }else if(converter instanceof BindingStreamConverter){
                        inputObj = ((BindingStreamConverter)converter).convertToObject(
                            createStringStreamConverter(getCharacterEncoding(request)).convertToStream(body),
                            inputObj
                        );
                    }else if(converter instanceof BindingConverter){
                        inputObj = ((BindingConverter)converter).convert(body, inputObj);
                    }else{
                        inputObj = converter.convert(body);
                    }
                }
            }
        }
        return (I)inputObj;
    }
    
    /**
     * 要求コンテキストの入力となるオブジェクトを生成する。<p>
     * デフォルトでは、nullを返す。必要に応じて、オーバーライドしてください。<br>
     *
     * @param request リクエスト
     * @param context コンテキスト
     * @return 要求コンテキストの入力
     */
    protected I processCreateInputObject(APIGatewayProxyRequestEvent request, Context context){
        return null;
    }
    
    /**
     * 出力変換処理を行う。<p>
     * 要求コンテキストから出力を取り出し、出力変換の{@link Converter}で変換して、レスポンスのボディに設定する。<br/>
     *
     * @param context 要求コンテキスト
     * @return レスポンス
     */
    protected APIGatewayProxyResponseEvent processConvertToOutput(RequestContext<I, O> context){
        APIGatewayRequestContext<I,O> rc = (APIGatewayRequestContext)context;
        O output = rc.getOutput();
        APIGatewayProxyResponseEvent response = rc.getResponse();
        if(output != null){
            Converter converter = outputConverter;
            Object outputBody = null;
            if(converter == null){
                String encoding = getCharacterEncoding(rc.getRequest());
                outputBody = createStringStreamConverter(encoding).convertToObject(
                    createBeanJSONConverter(encoding).convertToStream(output)
                );
            }else if(converter instanceof StreamConverter){
                outputBody = createStringStreamConverter(getCharacterEncoding(rc.getRequest())).convertToObject(
                    ((StreamConverter)converter).convertToStream(output)
                );
            }else{
                outputBody = converter.convert(output);
            }
            if(outputBody != null){
                response.setBody(outputBody.toString());
            }
        }
        return response;
    }
    
    protected StringStreamConverter createStringStreamConverter(String encoding){
        StringStreamConverter ssc = new StringStreamConverter();
        ssc.setCharacterEncodingToObject(encoding);
        ssc.setCharacterEncodingToStream(encoding);
        return ssc;
    }
    
    protected BeanJSONConverter createBeanJSONConverter(String encoding){
        BeanJSONConverter bjc = new BeanJSONConverter();
        bjc.setCharacterEncodingToObject(encoding);
        bjc.setCharacterEncodingToStream(encoding);
        return bjc;
    }
    
    protected String getCharacterEncoding(APIGatewayProxyRequestEvent request){
        String characterEncoding = "UTF-8";
        final String contentType = request.getHeaders().get("Content-Type");
        if(contentType == null){
            return characterEncoding;
        }
        final StringTokenizer tokens
             = new StringTokenizer(contentType, ";");
        while(tokens.hasMoreTokens()){
            final String token = tokens.nextToken();
            if(token.indexOf("charset") != -1){
                final int index = token.indexOf('=');
                if(index <= 0
                     || index == token.length() - 1){
                    continue;
                }
                final String charset = token.substring(index + 1).trim();
                if(charset.length() != 0){
                    characterEncoding = charset;
                    break;
                }
            }
        }
        return characterEncoding;
    }
}