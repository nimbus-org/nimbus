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

import java.lang.reflect.*;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import jp.ossc.nimbus.core.*;

/**
 * Jakarta Commons Poolを使って、リソースをプールするResourceFactoryサービス。<p>
 * 属性で設定された{@link org.apache.commons.pool.PoolableObjectFactory}を使って、プールするリソースを生成して、{@link org.apache.commons.pool.impl.GenericObjectPool}にプールする。<br>
 *
 * @author M.Takata
 */
public class PooledResourceFactoryService extends ServiceBase
 implements ResourceFactory, PooledResourceFactoryServiceMBean{
    
    private static final long serialVersionUID = -4615328544007250967L;
    
    private Class poolableObjectFactoryClass;
    private ServiceName poolableObjectFactoryServiceName;
    private GenericObjectPool pool;
    private int maxActive = GenericObjectPool.DEFAULT_MAX_ACTIVE;
    private int maxIdle = GenericObjectPool.DEFAULT_MAX_IDLE;
    private int minIdle = GenericObjectPool.DEFAULT_MIN_IDLE;
    private long maxWait = GenericObjectPool.DEFAULT_MAX_WAIT;
    private long minEvictableIdleTime
         = GenericObjectPool.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
    private int numTestsPerEvictionRun
         = GenericObjectPool.DEFAULT_NUM_TESTS_PER_EVICTION_RUN;
    private boolean isTestOnBorrow = GenericObjectPool.DEFAULT_TEST_ON_BORROW;
    private boolean isTestOnReturn = GenericObjectPool.DEFAULT_TEST_ON_RETURN;
    private boolean isTestWhileIdle = GenericObjectPool.DEFAULT_TEST_WHILE_IDLE;
    private long timeBetweenEvictionRuns
         = GenericObjectPool.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
    private String whenExhaustedActionName = WHEN_EXHAUSTED_BLOCK;
    private byte whenExhaustedActionValue
         = GenericObjectPool.DEFAULT_WHEN_EXHAUSTED_ACTION;
    private Class pooledTransactionResourceClass
         = DefaultPooledTransactionResource.class;
    private Constructor transactionResourceConstructor;
    
    public void setPoolableObjectFactoryClass(Class clazz){
        poolableObjectFactoryClass = clazz;
    }
    public Class getPoolableObjectFactoryClass(){
        return poolableObjectFactoryClass;
    }
    
    public void setPoolableObjectFactoryServiceName(ServiceName name){
        poolableObjectFactoryServiceName = name;
    }
    public ServiceName getPoolableObjectFactoryServiceName(){
        return poolableObjectFactoryServiceName;
    }
    
    public void setMaxActive(int max){
        maxActive = max;
    }
    public int getMaxActive(){
        return maxActive;
    }
    
    public void setMaxIdle(int max){
        maxIdle = max;
    }
    public int getMaxIdle(){
        return maxIdle;
    }
    
    public void setMinIdle(int min){
        minIdle = min;
    }
    public int getMinIdle(){
        return minIdle;
    }
    
    public void setMaxWaitTime(long maxMillis){
        maxWait = maxMillis;
    }
    public long getMaxWaitTime(){
        return maxWait;
    }
    
    public void setMinEvictableIdleTime(long minMillis){
        minEvictableIdleTime = minMillis;
    }
    public long getMinEvictableIdleTime(){
        return minEvictableIdleTime;
    }
    
    public void setNumTestsPerEvictionRun(int num){
        numTestsPerEvictionRun = num;
    }
    public int getNumTestsPerEvictionRun(){
        return numTestsPerEvictionRun;
    }
    
    public void setTestOnBorrow(boolean isTest){
        isTestOnBorrow = isTest;
    }
    public boolean isTestOnBorrow(){
        return isTestOnBorrow;
    }
    
    public void setTestOnReturn(boolean isTest){
        isTestOnReturn = isTest;
    }
    public boolean isTestOnReturn(){
        return isTestOnReturn;
    }
    
    public void setTestWhileIdle(boolean isTest){
        isTestWhileIdle = isTest;
    }
    public boolean isTestWhileIdle(){
        return isTestWhileIdle;
    }
    
    public void setTimeBetweenEvictionRuns(long millis){
        timeBetweenEvictionRuns = millis;
    }
    public long getTimeBetweenEvictionRuns(){
        return timeBetweenEvictionRuns;
    }
    
    public void setWhenExhaustedAction(String action)
     throws IllegalArgumentException{
        if(WHEN_EXHAUSTED_BLOCK.equals(action)){
            whenExhaustedActionValue = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
        }else if(WHEN_EXHAUSTED_BLOCK.equals(action)){
            whenExhaustedActionValue = GenericObjectPool.WHEN_EXHAUSTED_FAIL;
        }else if(WHEN_EXHAUSTED_BLOCK.equals(action)){
            whenExhaustedActionValue = GenericObjectPool.WHEN_EXHAUSTED_GROW;
        }else{
            throw new IllegalArgumentException(action);
        }
        whenExhaustedActionName = action;
    }
    public String getWhenExhaustedAction(){
        return whenExhaustedActionName;
    }
    
    public void startService() throws Exception{
        PoolableObjectFactory factory = null;
        if(poolableObjectFactoryClass != null){
            factory = (PoolableObjectFactory)poolableObjectFactoryClass
                .newInstance();
        }else if(poolableObjectFactoryServiceName != null){
            factory = (PoolableObjectFactory)ServiceManagerFactory
                .getServiceObject(poolableObjectFactoryServiceName);
        }else{
            throw new IllegalArgumentException(
                "poolableObjectFactoryClass or poolableObjectFactoryServiceName must be specified."
            );
        }
        
        if(pooledTransactionResourceClass == null){
            throw new IllegalArgumentException(
                "pooledTransactionResourceClass must be specified."
            );
        }
        transactionResourceConstructor = pooledTransactionResourceClass
            .getConstructor(new Class[]{ObjectPool.class});
        
        pool = new GenericObjectPool(factory);
        pool.setMaxActive(maxActive);
        pool.setMaxIdle(maxIdle);
        pool.setMinIdle(minIdle);
        pool.setMaxWait(maxWait);
        pool.setMinEvictableIdleTimeMillis(minEvictableIdleTime);
        pool.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        pool.setTestOnBorrow(isTestOnBorrow);
        pool.setTestOnReturn(isTestOnReturn);
        pool.setTestWhileIdle(isTestWhileIdle);
        pool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRuns);
        pool.setWhenExhaustedAction(whenExhaustedActionValue);
    }
    
    public void stopService() throws Exception{
        if(pool != null){
            pool.close();
            pool = null;
        }
    }
    
    public TransactionResource makeResource(String key) throws Exception{
        return (TransactionResource)transactionResourceConstructor
            .newInstance(new Object[]{pool});
    }
    
    public void clear() throws Exception{
        if(pool != null){
            pool.clear();
        }
    }
    
    public int getActiveNum(){
        return pool == null ? 0 : pool.getNumActive();
    }
    
    public int getIdleNum(){
        return pool == null ? 0 : pool.getNumIdle();
    }
}