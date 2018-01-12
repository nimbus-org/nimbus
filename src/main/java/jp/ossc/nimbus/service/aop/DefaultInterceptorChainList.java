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

import java.util.*;

/**
 * {@link InterceptorChainList}のデフォルト実装。<p>
 *
 * @author M.Takata
 */
public class DefaultInterceptorChainList implements InterceptorChainList, java.io.Serializable{
    
    private static final long serialVersionUID = -2068320773199878293L;
    
    private final List interceptorList;
    
    /**
     * インターセプタが登録されていないリストを生成する。<p>
     */
    public DefaultInterceptorChainList(){
        interceptorList = new ArrayList();
    }
    
    /**
     * 指定されたインターセプタ配列をチェーンとして持つリストを生成する。<p>
     *
     * @param interceptors 登録するインターセプタの配列
     */
    public DefaultInterceptorChainList(Interceptor[] interceptors){
        this();
        if(interceptors != null){
            for(int i = 0; i < interceptors.length; i++){
                interceptorList.add(interceptors[i]);
            }
        }
    }
    
    /**
     * 指定されたインターセプタをチェーンに追加する。<p>
     *
     * @param interceptor インターセプタ
     */
    public void addInterceptor(Interceptor interceptor){
        interceptorList.add(interceptor);
    }
    
    /**
     * 指定されたインターセプタをチェーンの指定されたインデックスに挿入する。<p>
     *
     * @param index チェーン内のインデックス
     * @param interceptor インターセプタ
     */
    public void addInterceptor(int index, Interceptor interceptor){
        interceptorList.add(index, interceptor);
    }
    
    // InterceptorChainListのJavaDoc
    public Interceptor getInterceptor(InvocationContext context, int index){
        if(interceptorList.size() <= index){
            return null;
        }
        return (Interceptor)interceptorList.get(index);
    }
    
    /**
     * インターセプタのリストを取得する。<p>
     *
     * @return インターセプタのリスト
     */
    public List getInterceptors(){
        return interceptorList;
    }
    
    /**
     * 指定されたインターセプタをリストから削除する。<p>
     *
     * @param interceptor 削除するインターセプタ
     */
    public void removeInterceptor(Interceptor interceptor){
        interceptorList.remove(interceptor);
    }
    
    /**
     * 指定されたインデックスのインターセプタをリストから削除する。<p>
     *
     * @param index チェーン内のインデックス
     */
    public void removeInterceptor(int index){
        interceptorList.remove(index);
    }
    
    /**
     * インターセプタを全て削除する。<p>
     */
    public void clearInterceptor(){
        interceptorList.clear();
    }
    
    /**
     * このリストに登録されているインターセプタの数を取得する。<p>
     *
     * @return このリストに登録されているインターセプタの数
     */
    public int size(){
        return interceptorList.size();
    }
}
