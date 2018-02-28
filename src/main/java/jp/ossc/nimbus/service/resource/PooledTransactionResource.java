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
package jp.ossc.nimbus.service.resource;

import org.apache.commons.pool.ObjectPool;

/**
 * {@link PooledResourceFactoryService}でプール可能な{@link TransactionResource}の抽象クラス。<p>
 *
 * @author M.Takata
 */
public abstract class PooledTransactionResource implements TransactionResource{
    
    /**
     * プール。<p>
     */
    protected ObjectPool pool;
    
    /**
     * プールから取得したリソース。<p>
     */
    protected Object resource;
    
    /**
     * 引数で指定されたプールからリソースを取得して、インスタンスを生成する。<p>
     *
     * @param pool プール
     */
    public PooledTransactionResource(ObjectPool pool) throws Exception{
        this.pool = pool;
        resource = pool.borrowObject();
    }
    
    /**
     * リソースをプールに返す。<p>
     *
     * @exception Exception リソースをプールに返す処理に失敗した場合
     */
    public void close() throws Exception{
        if(pool != null && resource != null){
            pool.returnObject(resource);
            resource = null;
            pool = null;
        }
    }
    
    /**
     * プールから取得したリソースを取得する。<p>
     *
     * @return プールから取得したリソース
     */
    public Object getObject(){
        return resource;
    }
}