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

package jp.ossc.nimbus.service.semaphore;

import jp.ossc.nimbus.core.*;

/**
 * セマフォサービス。<p>
 * 
 * @author H.Nakano
 */
public class DefaultSemaphoreService extends ServiceBase
 implements Semaphore, DefaultSemaphoreServiceMBean {
    
    private static final long serialVersionUID = 6475921591298262486L;
    
    /** キャパシティーサイズ */
    private int capacity = -1 ;
    
    /** セマフォ */
    private Semaphore semaphore = null ;
    
    private long sleepTime = -1;
    private long checkInterval = -1;
    
    private String semaphoreClassName = MemorySemaphore.class.getName();
    
    private long timeoutMillis = -1L;
    private int maxWaitCount = -1;
    private long forceFreeTimeoutMillis = -1L;
    private boolean isThreadBinding = true;
    
    // Semaphore のJavaDoc
    public boolean getResource(
        long timeout,
        int count,
        long forceFreeTimeout
    ){
        if(semaphore == null){
            return false;
        }
        return semaphore.getResource(
            timeout,
            count,
            forceFreeTimeout
        );
    }
    
    // Semaphore のJavaDoc
    public boolean getResource(long timeout, int count) {
        if(semaphore == null){
            return false;
        }
        return semaphore.getResource(
            timeout,
            count,
            forceFreeTimeoutMillis
        );
    }
    
    // Semaphore のJavaDoc
    public boolean getResource(long timeout) {
        if(semaphore == null){
            return false;
        }
        return semaphore.getResource(
            timeout,
            maxWaitCount,
            forceFreeTimeoutMillis
        );
    }
    
    // Semaphore のJavaDoc
    public boolean getResource(int count){
        if(semaphore == null){
            return false;
        }
        return semaphore.getResource(
            timeoutMillis,
            count,
            forceFreeTimeoutMillis
        );
    }
    
    // Semaphore のJavaDoc
    public boolean getResource() {
        if(semaphore == null){
            return false;
        }
        return semaphore.getResource(
            timeoutMillis,
            maxWaitCount,
            forceFreeTimeoutMillis
        );
    }
    
    // Semaphore のJavaDoc
    public void freeResource() {
        semaphore.freeResource() ;
    }
    
    // Semaphore のJavaDoc
    public int getResourceCapacity() {
        return capacity;
    }
    
    // Semaphore のJavaDoc
    public void setResourceCapacity(int capa) {
        this.capacity = capa ;
    }
    
    // Semaphore のJavaDoc
    public void setSleepTime(long millis){
        sleepTime = millis;
    }
    
    // Semaphore のJavaDoc
    public long getSleepTime(){
        return sleepTime;
    }
    
    // Semaphore のJavaDoc
    public void setCheckInterval(long millis){
        checkInterval = millis;
    }
    
    // Semaphore のJavaDoc
    public long getCheckInterval(){
        return checkInterval;
    }
    
    // Semaphore のJavaDoc
    public long getTimeoutMillis(){
        return timeoutMillis;
    }
    // Semaphore のJavaDoc
    public void setTimeoutMillis(long timeout){
        timeoutMillis = timeout;
    }
    
    // Semaphore のJavaDoc
    public int getMaxWaitCount(){
        return maxWaitCount;
    }
    // Semaphore のJavaDoc
    public void setMaxWaitCount(int count){
        maxWaitCount = count;
    }
    
    // Semaphore のJavaDoc
    public long getForceFreeTimeoutMillis(){
        return forceFreeTimeoutMillis;
    }
    // Semaphore のJavaDoc
    public void setForceFreeTimeoutMillis(long timeout){
        forceFreeTimeoutMillis = timeout;
    }
    
    // Semaphore のJavaDoc
    public int getResourceRemain() {
        return semaphore == null ? -1 : semaphore.getResourceRemain();
    }
    
    // Semaphore のJavaDoc
    public int getWaitingCount(){
        return semaphore == null ? 0 : semaphore.getWaitingCount();
    }
    
    // Semaphore のJavaDoc
    public void release(){
        semaphore.release();
    }
    
    // Semaphore のJavaDoc
    public void accept(){
        semaphore.accept();
    }
    
    // Semaphore のJavaDoc
    public int getMaxUsedResource(){
        return semaphore == null ? 0 : semaphore.getMaxUsedResource();
    }
    
    // Semaphore のJavaDoc
    public int getMaxWaitedCount(){
        return semaphore == null ? 0 : semaphore.getMaxWaitedCount();
    }
    
    // Semaphore のJavaDoc
    public void setThreadBinding(boolean isBinding){
        isThreadBinding = isBinding;
    }
    
    // Semaphore のJavaDoc
    public boolean isThreadBinding(){
        return isThreadBinding;
    }
    
    // DefaultSemaphoreServiceMBean のJavaDoc
    public void setSemaphoreClassName(String name) {
        semaphoreClassName = name ;
    }
    
    // DefaultSemaphoreServiceMBean のJavaDoc
    public String getSemaphoreClassName(){
        return semaphoreClassName;
    }
    
    /**
     * サービスの開始処理を行います。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        semaphore = (Semaphore)Class.forName(
            semaphoreClassName,
            true,
            NimbusClassLoader.getInstance()
        ).newInstance();
        semaphore.setResourceCapacity(getResourceCapacity());
        if(getSleepTime() > 0){
            semaphore.setSleepTime(getSleepTime());
        }
        if(getCheckInterval() > 0){
            semaphore.setCheckInterval(getCheckInterval());
        }
        semaphore.setThreadBinding(isThreadBinding());
    }
    
    /**
     * サービスの停止処理を行います。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService(){
        semaphore.release();
    }
}
