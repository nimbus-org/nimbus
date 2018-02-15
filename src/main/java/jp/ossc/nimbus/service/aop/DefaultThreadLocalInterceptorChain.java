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

import jp.ossc.nimbus.core.*;

/**
 * デフォルトスレッド単位インターセプタチェーン。<p>
 * 現在呼び出されているインターセプタの情報をスレッド単位にインスタンス変数に格納するので、スレッドセーフなインターセプタチェーンである。<br>
 *
 * @author M.Takata
 */
public class DefaultThreadLocalInterceptorChain
 extends DefaultInterceptorChain implements java.io.Serializable{
    
    private static final long serialVersionUID = 5302115451138234378L;
    
    /**
     * 現在呼び出し中の情報をスレッド単位に保持するThreadLocal。<p>
     */
    protected transient ThreadLocal state = new ThreadLocal(){
        protected synchronized Object initialValue(){
            return new InterceptorChainState();
        }
    };
    
    /**
     * 空のインターセプタチェーンを生成する。<p>
     */
    public DefaultThreadLocalInterceptorChain(){
        super();
    }
    
    /**
     * 指定された{@link InterceptorChainList}と{@link Invoker}のインターセプタチェーンを生成する。<p>
     *
     * @param list チェーンするインターセプタのリスト
     * @param invoker 本来の呼び出し先を呼び出すInvoker
     */
    public DefaultThreadLocalInterceptorChain(
        InterceptorChainList list,
        Invoker invoker
    ){
        super(list, invoker);
    }
    
    /**
     * 指定された{@link InterceptorChainList}サービスと{@link Invoker}サービスのインターセプタチェーンを生成する。<p>
     *
     * @param listServiceName チェーンするインターセプタのリストInterceptorChainListサービスのサービス名
     * @param invokerServiceName 本来の呼び出し先を呼び出すInvokerサービスのサービス名
     */
    public DefaultThreadLocalInterceptorChain(
        ServiceName listServiceName,
        ServiceName invokerServiceName
    ){
        super(listServiceName, invokerServiceName);
    }
    
    // InterceptorChainのJavaDoc
    public int getCurrentInterceptorIndex(){
        InterceptorChainState chainState = (InterceptorChainState)state.get();
        return chainState.currentIndex;
    }
    
    /**
     * 現在のインターセプタのこのインターセプタチェーン上のインデックスを設定する。<p>
     *
     * @param index 現在のインターセプタのこのインターセプタチェーン上のインデックス
     */
    public void setCurrentInterceptorIndex(int index){
        InterceptorChainState chainState = (InterceptorChainState)state.get();
        chainState.currentIndex = index;
    }
    
    // InterceptorChainのJavaDoc
    public InterceptorChain cloneChain(){
        DefaultThreadLocalInterceptorChain clone
             = (DefaultThreadLocalInterceptorChain)super.cloneChain();
        clone.state = new ThreadLocal(){
            protected synchronized Object initialValue(){
                return new InterceptorChainState();
            }
        };
        return clone;
    }
    
    /**
     * 現在呼び出し中のインターセプタの情報を格納するクラス。<p>
     * 
     * @author M.Takata
     */
    protected static class InterceptorChainState
     implements java.io.Serializable{
        
        private static final long serialVersionUID = -24647693558335555L;
        
        /**
         * {@link InterceptorChainList}内の、現在の処理中の{@link Interceptor}のインデックス。<p>
         * 初期値は、-1。
         */
        public int currentIndex = -1;
    }
}
