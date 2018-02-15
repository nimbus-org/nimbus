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
package jp.ossc.nimbus.service.aop.javassist;

import java.lang.reflect.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;

/**
 * メソッドリフレクション呼び出しインボーカ。<p>
 * リフレクションAPIを使って、アスペクトによってラップされたメソッドの呼び出しを行う。
 * 以下に、サービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="WrappedMethodReflectionCallInvoker"
 *                  code="jp.ossc.nimbus.service.aop.javassist.WrappedMethodReflectionCallInvokerService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class WrappedMethodReflectionCallInvokerService extends ServiceBase
 implements Invoker, java.io.Serializable{
    
    private static final long serialVersionUID = -1539581966728381008L;
    
    /**
     * リフレクションAPIを使って、アスペクトによってラップされたメソッドの呼び出しを行う。<p>
     * アスペクトによってラップされたメソッドは、{@link WrappedMethodInvocationContext#getWrappedTargetMethod()}で取得する。<br>
     * 
     * @param context 呼び出しのコンテキスト情報
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合
     */
    public Object invoke(InvocationContext context) throws Throwable{
        final WrappedMethodInvocationContext methodContext
             = (WrappedMethodInvocationContext)context;
        try{
            return methodContext.getWrappedTargetMethod().invoke(
                methodContext.getTargetObject(),
                methodContext.getParameters()
            );
        }catch(InvocationTargetException e){
            throw e.getTargetException();
        }
    }
}
