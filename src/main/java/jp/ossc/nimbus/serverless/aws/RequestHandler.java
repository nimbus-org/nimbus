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

import com.amazonaws.services.lambda.runtime.Context;

/**
 * LambdaアプリケーションをJavaプログラムで開発するためのリクエストハンドラ抽象クラス。<p>
 *
 * @author M.Takata
 */
public abstract class RequestHandler<I,O> extends NimbusRequestHandler<I,O,I,O> implements com.amazonaws.services.lambda.runtime.RequestHandler<I,O>{
    
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
    public O handleRequest(I input, Context context){
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
}