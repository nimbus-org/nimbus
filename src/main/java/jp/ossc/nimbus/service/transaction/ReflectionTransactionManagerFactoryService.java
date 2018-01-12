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
package jp.ossc.nimbus.service.transaction;

import java.lang.reflect.*;
import javax.transaction.TransactionManager;

import jp.ossc.nimbus.core.*;

/**
 * リフレクションを使ってTransactionManagerを取得するTransactionManagerファクトリ。<p>
 *
 * @author M.Takata
 */
public class ReflectionTransactionManagerFactoryService
 extends ServiceBase
 implements ReflectionTransactionManagerFactoryServiceMBean, TransactionManagerFactory{
    
    private static final long serialVersionUID = 2993167610396232822L;
    
    private Constructor factoryConstructor;
    private Object[] factoryConstructorParameters;
    private Method factoryMethod;
    private Object[] factoryMethodParameters;
    private Object factory;
    private TransactionManager transactionManager;
    private Constructor transactionManagerConstructor;
    private Object[] transactionManagerConstructorParameters;
    
    public void setFactoryConstructor(Constructor c){
        factoryConstructor = c;
    }
    public Constructor getFactoryConstructor(){
        return factoryConstructor;
    }
    
    public void setFactoryConstructorParameters(Object[] params){
        factoryConstructorParameters = params;
    }
    public Object[] getFactoryConstructorParameters(){
        return factoryConstructorParameters;
    }
    
    public void setFactoryMethod(Method m){
        factoryMethod = m;
    }
    public Method getFactoryMethod(){
        return factoryMethod;
    }
    
    public void setFactoryMethodParameters(Object[] params){
        factoryMethodParameters = params;
    }
    public Object[] getFactoryMethodParameters(){
        return factoryMethodParameters;
    }
    
    public void setFactory(Object fac){
        factory = fac;
    }
    public Object getFactory(){
        return factory;
    }
    
    public void setTransactionManagerConstructor(Constructor c){
        transactionManagerConstructor = c;
    }
    public Constructor getTransactionManagerConstructor(){
        return transactionManagerConstructor;
    }
    
    public void setTransactionManagerConstructorParameters(Object[] params){
        transactionManagerConstructorParameters = params;
    }
    public Object[] getTransactionManagerConstructorParameters(){
        return transactionManagerConstructorParameters;
    }
    
    public void setTransactionManager(TransactionManager tm){
        transactionManager = tm;
    }
    
    public void startService() throws Exception{
        if(transactionManagerConstructor != null){
            if(transactionManagerConstructorParameters == null){
                transactionManager = (TransactionManager)transactionManagerConstructor.getDeclaringClass().newInstance();
            }else{
                transactionManager = (TransactionManager)transactionManagerConstructor.newInstance(transactionManagerConstructorParameters);
            }
        }
        if(transactionManager == null){
            if(factoryConstructor != null){
                if(factoryConstructorParameters == null){
                    factory = factoryConstructor.getDeclaringClass().newInstance();
                }else{
                    factory = factoryConstructor.newInstance(factoryConstructorParameters);
                }
            }
            if(factoryMethod == null){
                throw new IllegalArgumentException("FactoryMethod is null.");
            }
            transactionManager = (TransactionManager)factoryMethod.invoke(
                factory,
                factoryMethodParameters
            );
        }
    }
    
    /**
     * TransactionManagerを取得する。<p>
     *
     * @return TransactionManager
     * @exception TransactionManagerFactoryException TransactionManagerの取得に失敗した場合
     */
    public TransactionManager getTransactionManager() throws TransactionManagerFactoryException{
        return transactionManager;
    }
}