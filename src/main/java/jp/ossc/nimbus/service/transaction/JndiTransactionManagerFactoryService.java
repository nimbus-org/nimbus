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

import javax.naming.*;
import javax.transaction.TransactionManager;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.jndi.JndiFinder;

/**
 * TransactionManagerをJNDIから取得するTransactionManagerファクトリ。<p>
 *
 * @author M.Takata
 */
public class JndiTransactionManagerFactoryService
 extends ServiceBase
 implements JndiTransactionManagerFactoryServiceMBean, TransactionManagerFactory{
    
    private static final long serialVersionUID = -3095915948460748531L;
    
    private String transactionManagerName = DEFAULT_TRANSACTION_MANAGER_NAME;
    
    private ServiceName jndiFinderServiceName;
    private JndiFinder jndiFinder;
    private TransactionManager transactionManager;
    
    // JndiTransactionManagerFactoryServiceMBeanのJavaDoc
    public void setTransactionManagerName(String name){
        transactionManagerName = name;
    }
    
    // JndiTransactionManagerFactoryServiceMBeanのJavaDoc
    public String getTransactionManagerName(){
        return transactionManagerName;
    }
    
    // JndiTransactionManagerFactoryServiceMBeanのJavaDoc
    public void setJndiFinderServiceName(ServiceName name){
        jndiFinderServiceName = name;
    }
    
    // JndiTransactionManagerFactoryServiceMBeanのJavaDoc
    public ServiceName getJndiFinderServiceName(){
        return jndiFinderServiceName;
    }
    
    public void startService() throws Exception{
        if(jndiFinderServiceName != null){
            jndiFinder = (JndiFinder)ServiceManagerFactory.getServiceObject(jndiFinderServiceName);
        }
    }
    
    /**
     * TransactionManagerを取得する。<p>
     *
     * @return TransactionManager
     * @exception TransactionManagerFactoryException TransactionManagerの取得に失敗した場合
     */
    public TransactionManager getTransactionManager() throws TransactionManagerFactoryException{
        if(transactionManager == null){
            synchronized(this){
                if(transactionManager == null){
                    try{
                        if(getJndiFinder() == null){
                            final Context context = new InitialContext();
                            transactionManager = (TransactionManager)context.lookup(
                                getTransactionManagerName()
                            );
                        }else{
                            transactionManager = (TransactionManager)getJndiFinder().lookup(getTransactionManagerName());
                        }
                    }catch(NamingException e){
                        throw new TransactionManagerFactoryException(e);
                    }
                }
            }
        }
        return transactionManager;
    }
    
    public void setJndiFinder(JndiFinder finder){
        jndiFinder = finder;
    }
    public JndiFinder getJndiFinder(){
        return jndiFinder;
    }
}