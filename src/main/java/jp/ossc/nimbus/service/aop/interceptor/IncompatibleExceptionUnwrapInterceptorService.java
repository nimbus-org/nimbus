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
package jp.ossc.nimbus.service.aop.interceptor;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;

/**
 * クラス互換性のない例外のラップを解除してthrowするインターセプタ。<p>
 *
 * @author M.Takata
 */
public class IncompatibleExceptionUnwrapInterceptorService
 extends ServiceBase
 implements Interceptor, IncompatibleExceptionUnwrapInterceptorServiceMBean{
    
    private static final long serialVersionUID = -5138861407594201071L;

    /**
     * 次のインターセプタを呼び出し、クラス互換性のない例外が発生するとラップ処理を行う。<p>
     * サービスが開始されていない場合は、何もせずに次のインターセプタを呼び出す。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても、呼び出し元には伝播されない。
     */
    public Object invoke(
        InvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        if(getState() == STARTED){
            Object ret = null;
            try{
                ret = chain.invokeNext(context);
            }catch(Throwable th){
                throw unwrapIncompatibleException(th);
            }
            return ret;
        }else{
            return chain.invokeNext(context);
        }
    }
    
    protected Throwable unwrapIncompatibleException(Throwable th){
        Throwable result = th;
        if(th instanceof IncompatibleExceptionWrapExeption){
            result = ((IncompatibleExceptionWrapExeption)th).unwrap();
        }
        Throwable cause = IncompatibleExceptionWrapExeption.getCause(result);
        if(cause != null){
            Throwable wrapCuase = unwrapIncompatibleException(cause);
            if(cause != wrapCuase){
                IncompatibleExceptionWrapExeption.setCause(result, wrapCuase);
            }
        }
        return result;
    }
}