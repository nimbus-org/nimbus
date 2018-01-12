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
package jp.ossc.nimbus.core;

import java.util.*;

/**
 * ファクトリサービス基底クラス。<p>
 *
 * @author M.Takata
 */
public abstract class FactoryServiceBase extends ServiceBase
 implements FactoryServiceBaseMBean{
    
    private static final long serialVersionUID = -7723361215992951033L;

    /**
     * このファクトリによって生成されたオブジェクトを管理するかどうかを示すフラグ。<p>
     * 管理する場合、true。<br>
     */
    protected volatile boolean isManaged;
    
    /**
     * このファクトリによってスレッド単位にオブジェクトを生成するかどうかを示すフラグ。<p>
     * スレッド単位に生成する場合、true。<br>
     */
    protected volatile boolean isThreadLocal;
    
    /**
     * 管理しているインスタンスを保持する集合。<p>
     */
    protected Set managedInstances = Collections.synchronizedSet(new HashSet());
    
    /**
     * スレッド単位に管理しているインスタンスを保持するThreadLocal。<p>
     */
    protected ThreadLocal threadLocal = new ThreadLocal();
    
    /**
     * サービスの開始時に、ファクトリするオブジェクトの生成を試みてみるかどうかのフラグ。<p>
     * デフォルトでは、true。<br>
     */
    protected boolean isCreateTemplateOnStart = true;
    
    /**
     * コンストラクタ。<p>
     */
    public FactoryServiceBase(){
        super();
    }
    
    // FactoryServiceのJavaDoc
    public void setManagement(boolean isManaged){
        this.isManaged = isManaged;
    }
    
    // FactoryServiceのJavaDoc
    public boolean isManagement(){
        return isManaged;
    }
    
    // FactoryServiceのJavaDoc
    public void setThreadLocal(boolean isThreadLocal){
        this.isThreadLocal = isThreadLocal;
    }
    
    // FactoryServiceのJavaDoc
    public boolean isThreadLocal(){
        return isThreadLocal;
    }
    
    // FactoryServiceのJavaDoc
    public void release(Object service){
        if(managedInstances.size() != 0){
            managedInstances.remove(service);
        }
        threadLocal.set(null);
    }
    
    // FactoryServiceのJavaDoc
    public void release(){
        final Object[] instances = managedInstances.toArray();
        for(int i = 0; i < instances.length; i++){
            release(instances[i]);
        }
        threadLocal = new ThreadLocal();
    }
    
    // FactoryServiceのJavaDoc
    public Object newInstance(){
        Object obj = null;
        if(isManaged){
            synchronized(managedInstances){
                if(isThreadLocal){
                    obj = threadLocal.get();
                }
                if(obj == null){
                    try{
                        obj = createInstance();
                    }catch(Exception e){
                        return null;
                    }
                    if(isThreadLocal){
                        threadLocal.set(obj);
                    }
                }
                managedInstances.add(obj);
            }
        }else{
            if(isThreadLocal){
                obj = threadLocal.get();
            }
            if(obj == null){
                try{
                    obj = createInstance();
                }catch(Exception e){
                    return null;
                }
                if(isThreadLocal){
                    threadLocal.set(obj);
                }
            }
        }
        return obj;
    }
    
    // FactoryServiceBaseMBeanのJavaDoc
    public void setCreateTemplateOnStart(boolean isCreate){
        isCreateTemplateOnStart = isCreate;
    }
    
    // FactoryServiceBaseMBeanのJavaDoc
    public boolean isCreateTemplateOnStart(){
        return isCreateTemplateOnStart;
    }
    
    /**
     * このファクトリで生成したインスタンスの内、管理しているインスタンスの集合を取得する。<p>
     *
     * @return 管理しているインスタンスの集合
     */
    protected Set getManagedInstanceSet(){
        return managedInstances;
    }
    
    /**
     * このファクトリが提供するオブジェクトのインスタンスを生成する。<p>
     *
     * @return このファクトリが提供するオブジェクトのインスタンス
     * @exception Exception 生成中に例外が発生した場合
     */
    protected abstract Object createInstance() throws Exception;
    
    /**
     * このファクトリが提供するオブジェクトのインスタンスをテンプレート用に生成する。<p>
     *
     * @return このファクトリが提供するオブジェクトのインスタンス
     * @exception Exception 生成中に例外が発生した場合
     */
    protected Object createTemplate() throws Exception{
        return createInstance();
    }
    
    /**
     * このファクトリの生成後処理を行う。<p>
     * {@link #createInstance()}を呼び出して、このファクトリが生成するインスタンスの生成が可能かどうかをテストする。<br>
     * 
     * @exception Exception createInstance()に失敗した場合
     */
    protected void postStartService() throws Exception{
        super.postStartService();
        if(isCreateTemplateOnStart){
            final Object obj = createTemplate();
            release(obj);
        }
    }
    
    /**
     * このファクトリの破棄後処理を行う。<p>
     * 管理しているインスタンスをクリアする。<br>
     * 
     * @exception Exception 破棄後処理に失敗した場合
     */
    protected void postDestroyService() throws Exception{
        super.postDestroyService();
        managedInstances.clear();
    }
}
