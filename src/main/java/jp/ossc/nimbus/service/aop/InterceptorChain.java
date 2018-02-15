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
package jp.ossc.nimbus.service.aop;

/**
 * インターセプタチェーン。<p>
 * {@link #invokeNext(InvocationContext)}を呼び出す事で、{@link InterceptorChainList}に登録された{@link Interceptor}を順次呼び出す。<br>
 * また、全てのInterceptorを呼び出すと、本来の呼び出し先を呼び出す{@link Invoker}を呼び出す。<br>
 *
 * @author M.Takata
 */
public interface InterceptorChain{
    
    /**
     * 次のインターセプタを呼び出す。最後のインタセプタを呼び出した後は、本来の呼び出し先を呼び出す{@link Invoker}を呼び出す。<p>
     *
     * @param context 呼び出しのコンテキスト情報
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合
     */
    public Object invokeNext(InvocationContext context) throws Throwable;
    
    /**
     * 現在のインターセプタのこのインターセプタチェーン上のインデックスを取得する。<p>
     *
     * @return 現在のインターセプタのこのインターセプタチェーン上のインデックス
     */
    public int getCurrentInterceptorIndex();
    
    /**
     * 現在のインターセプタのこのインターセプタチェーン上のインデックスを設定する。<p>
     *
     * @param index 現在のインターセプタのこのインターセプタチェーン上のインデックス
     */
    public void setCurrentInterceptorIndex(int index);
    
    /**
     * このインターセプタチェーンが持つインターセプタのリストを取得する。<p>
     *
     * @return このインターセプタチェーンが持つインターセプタのリスト
     */
    public InterceptorChainList getInterceptorChainList();
    
    /**
     * 最後の呼び出しを行うInvokerを取得する。<p>
     *
     * @return 最後の呼び出しを行うInvoker
     */
    public Invoker getInvoker();
    
    /**
     * 最後の呼び出しを行うInvokerを設定する。<p>
     *
     * @param invoker 最後の呼び出しを行うInvoker
     */
    public void setInvoker(Invoker invoker);
    
    /**
     * このインスタンスの複製を作る。<p>
     *
     * @return 複製
     */
    public InterceptorChain cloneChain();
}
