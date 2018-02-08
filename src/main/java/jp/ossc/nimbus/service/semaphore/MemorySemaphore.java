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

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

import jp.ossc.nimbus.util.SynchronizeMonitor;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;

/**
 * �������Z�}�t�H�B<p>
 *
 * @author H.Nakano
 */
public class MemorySemaphore implements Semaphore, java.io.Serializable{
    
    private static final long serialVersionUID = -408553618405283847L;
    
    //## �����o�[�ϐ��錾 ##
    
    /** �Z�}�t�H���\�[�X�� */
    protected volatile int mResourceCnt = -1;
    
    /** �Z�}�t�H�������� */
    protected volatile int mInitialResource = -1;
    
    /** �����I���t���O */
    protected volatile boolean mFourceEndFlg = false;
    
    /** �Z�}�t�H�l�����j�^ */
    protected transient SynchronizeMonitor getMonitor = new WaitSynchronizeMonitor();
    
    /** �Z�}�t�H�l���X���b�h�W�� */
    protected transient ConcurrentMap usedThreads = new ConcurrentHashMap();
    
    /** �Z�}�t�H�l���X���b�h�W�� */
    protected transient ConcurrentMap threadTasks = new ConcurrentHashMap();
    
    /** �����l���҂��X���b�hSleep����[ms] */
    protected long sleepTime = 10000;
    
    /** ���\�[�X�Ď��Ԋu */
    protected long checkInterval = -1;
    
    /** ���\�[�X�Ď��^�X�N */
    protected transient ResourceChecker checker;
    
    /** �ő僊�\�[�X�g�p������ */
    protected int maxUsedResource;
    
    /** �ő僊�\�[�X�l���҂������� */
    protected int maxWaitedCount;
    
    /** �����J���^�C�}�[ */
    protected transient Timer forceFreeTimer = new Timer(true);
    
    protected boolean isThreadBinding = true;
    
    // Semaphore��JavaDoc
    public boolean getResource(){
        return this.getResource(-1L) ;
    }
    
    // Semaphore��JavaDoc
    public boolean getResource(int maxWaitCount){
        return getResource(-1L, maxWaitCount) ;
    }
    
    // Semaphore��JavaDoc
    public boolean getResource(long timeOutMiliSecond){
        return getResource(timeOutMiliSecond, -1);
    }
    
    // Semaphore��JavaDoc
    public boolean getResource(long timeOutMiliSecond, int maxWaitCount){
        return getResource(timeOutMiliSecond, maxWaitCount, -1);
    }
    
    // Semaphore��JavaDoc
    public boolean getResource(
        long timeOutMiliSecond,
        int maxWaitCount,
        long forceFreeMiliSecond
    ){
        final Thread current = Thread.currentThread();
        if((mResourceCnt <= 0 && maxWaitCount > 0 && getMonitor.getWaitCount() > maxWaitCount)
            || mFourceEndFlg){
            return false;
        }
        getMonitor.initMonitor();
        long timeOutMs = -1 ;
        if(timeOutMiliSecond >= 0){
            timeOutMs = timeOutMiliSecond;
        }
        long processTime = 0;
        try{
            // �����I���łȂ��ꍇ
            while(!mFourceEndFlg){
                // ���\�[�X���]���Ă���ꍇ
                if(mResourceCnt > 0){
                    boolean isGet = false;
                    synchronized(getMonitor){
                        if(mResourceCnt > 0){
                            // ���\�[�X���l������
                            mResourceCnt--;
                            getMonitor.releaseMonitor();
                            final int nowUsed = mInitialResource - mResourceCnt;
                            if(nowUsed > maxUsedResource){
                                maxUsedResource = nowUsed;
                            }
                            isGet = true;
                        }
                    }
                    if(isGet){
                        if(isThreadBinding){
                            // �����J�����Ԃ��w�肳��Ă���ꍇ
                            TimerTask task = null;
                            if(forceFreeMiliSecond > 0){
                                
                                // �����J���^�X�N���^�C�}�[�ɓo�^����
                                task = new ForceFreeTimerTask(current);
                                forceFreeTimer.schedule(task, forceFreeMiliSecond);
                            }
                            
                            // ���\�[�X�g�p���X���b�h�ɓo�^����
                            usedThreads.put(current, current);
                            
                            // �^�X�N�Ǘ��Ƀ^�X�N��o�^����
                            if(threadTasks.containsKey(current)){
                                final Object tasks = threadTasks.get(current);
                                List taskList = null;
                                if(tasks instanceof List){
                                    taskList = (List)tasks;
                                }else{
                                    taskList = new ArrayList();
                                    threadTasks.put(current, taskList);
                                }
                                taskList.add(task);
                            }else{
                                threadTasks.put(current, task);
                            }
                        }
                        if(mResourceCnt > 0 && getMonitor.isWait()){
                            getMonitor.notifyMonitor();
                        }
                        return true;
                    }
                }
                long proc = 0;
                synchronized(current){
                    // ���\�[�X���]���Ă��Ȃ��ꍇ
                    // �܂��́A���̃X���b�h�����O�ɑ҂��Ă����X���b�h������ꍇ
                    
                    // �����I���܂��̓^�C���A�E�g�̏ꍇ
                    if(mFourceEndFlg || (timeOutMs >= 0 && timeOutMs <= processTime)){
                        break;
                    }
                    
                    // �^�C���A�E�g�w�肪����ꍇ�́A�^�C���A�E�g�܂�sleep����
                    // �^�C���A�E�g�w�肪�Ȃ��ꍇ�́AsleepTime��sleep���Ă݂�
                    if(timeOutMs >= 0){
                        proc = System.currentTimeMillis();
                    }
                }
                int priority = current.getPriority();
                try{
                    if(mResourceCnt <= 0){
                        if(priority < Thread.MAX_PRIORITY){
                            try{
                                current.setPriority(priority + 1);
                            }catch(SecurityException e){
                            }
                        }
                        long curSleepTime = timeOutMs >= 0 ? timeOutMs - processTime : sleepTime;
                        if(curSleepTime > 0 && mResourceCnt <= 0){
                            final int nowWaited = getMonitor.getWaitCount() + 1;
                            if(nowWaited > maxWaitedCount){
                                maxWaitedCount = nowWaited;
                            }
                            getMonitor.waitMonitor(curSleepTime);
                        }
                    }
                }catch(InterruptedException e){
                    if(!getMonitor.isNotify()){
                        return false;
                    }
                }finally{
                    try{
                        current.setPriority(priority);
                    }catch(SecurityException e){
                    }
                }
                if(timeOutMs >= 0){
                    proc = System.currentTimeMillis() - proc;
                    processTime += proc;
                }
            }
            
            // �����I���܂��̓^�C���A�E�g�̏ꍇ
            return false;
        }finally{
            getMonitor.releaseMonitor();
        }
    }
    
    // Semaphore��JavaDoc
    public void freeResource(){
        freeResource(Thread.currentThread());
    }
    
    protected void freeResource(Thread usedThread){
        boolean isUsed = false;
        if(isThreadBinding){
            synchronized(usedThread){
                if(usedThreads.containsKey(usedThread)){
                    isUsed = true;
                    final Object tasks = threadTasks.get(usedThread);
                    if(tasks instanceof List){
                        final List taskList = (List)tasks;
                        final TimerTask task = (TimerTask)taskList.remove(0);
                        if(task != null){
                            task.cancel();
                        }
                        if(taskList.size() == 0){
                            threadTasks.remove(usedThread);
                            usedThreads.remove(usedThread);
                        }
                    }else{
                        final TimerTask task = (TimerTask)tasks;
                        if(task != null){
                            task.cancel();
                        }
                        threadTasks.remove(usedThread);
                        usedThreads.remove(usedThread);
                    }
                }
            }
        }
        synchronized(getMonitor){
            if((isThreadBinding && isUsed || !isThreadBinding) && mResourceCnt < mInitialResource){
                if(mResourceCnt < mInitialResource){
                    mResourceCnt++;
                }
            }
        }
        if(mFourceEndFlg || mResourceCnt > 0){
            getMonitor.notifyMonitor();
        }
    }
    
    // Semaphore��JavaDoc
    public int getResourceCapacity(){
        return mInitialResource;
    }
    
    // Semaphore��JavaDoc
    public void setResourceCapacity(int capa) {
        if(mInitialResource == -1){
            mInitialResource = capa ;
            mResourceCnt = capa ;
        }
    }
    
    // Semaphore��JavaDoc
    public int getResourceRemain() {
        return mResourceCnt;
    }
    
    // Semaphore��JavaDoc
    public int getWaitingCount(){
        return getMonitor.getWaitCount();
    }
    
    /**
     * �Z�}�t�H�ɑ΂��Ė����擾�҂�������X���b�h��sleep���鎞�Ԃ�ݒ肷��B<p>
     * �������Z�}�t�H�҂��̐擪�łȂ��ꍇ�́A�Ă�sleep����B<br>
     * �f�t�H���g�́A10�b�B
     *
     * @param millis �Z�}�t�H�ɑ΂��Ė����擾�҂�������X���b�h��sleep���鎞��[ms]
     */
    public void setSleepTime(long millis){
        sleepTime = millis;
    }
    
    // Semaphore��JavaDoc
    public long getSleepTime(){
        return sleepTime;
    }
    
    // Semaphore��JavaDoc
    public void setCheckInterval(long millis){
        checkInterval = millis;
    }
    
    // Semaphore��JavaDoc
    public long getCheckInterval(){
        return checkInterval;
    }
    
    // Semaphore��JavaDoc
    public synchronized void release(){
        if(checker != null){
            checker.isStop = true;
            checker = null;
        }
        mFourceEndFlg = true;
        while(getMonitor.isWait()){
            getMonitor.notifyMonitor();
        }
        while(usedThreads.size() != 0){
            Object[] threads = usedThreads.keySet().toArray();
            for(int i = 0; i < threads.length; i++){
                freeResource((Thread)threads[i]);
            }
        }
        mResourceCnt = mInitialResource;
        forceFreeTimer.cancel();
        forceFreeTimer = new Timer(true);
    }
    
    // Semaphore��JavaDoc
    public synchronized void accept(){
        mFourceEndFlg = false;
        if(checkInterval > 0){
            checker = new ResourceChecker();
            checker.start();
        }
    }
    
    // Semaphore��JavaDoc
    public int getMaxUsedResource(){
        return maxUsedResource;
    }
    
    // Semaphore��JavaDoc
    public int getMaxWaitedCount(){
        return maxWaitedCount;
    }
    
    // Semaphore ��JavaDoc
    public void setThreadBinding(boolean isBinding){
        isThreadBinding = isBinding;
    }
    
    // Semaphore ��JavaDoc
    public boolean isThreadBinding(){
        return isThreadBinding;
    }
    
    /**
     * �f�V���A���C�Y���s���B<p>
     *
     * @param in �f�V���A���C�Y�̌����ƂȂ�X�g���[��
     * @exception IOException �ǂݍ��݂Ɏ��s�����ꍇ
     * @exception ClassNotFoundException �f�V���A���C�Y���悤�Ƃ����I�u�W�F�N�g�̃N���X��������Ȃ��ꍇ
     */
    private void readObject(java.io.ObjectInputStream in)
     throws java.io.IOException, ClassNotFoundException{
        in.defaultReadObject();
        getMonitor = new WaitSynchronizeMonitor();
        usedThreads = new ConcurrentHashMap();
        threadTasks = new ConcurrentHashMap();
        forceFreeTimer = new Timer(true);
    }
    
    protected class ForceFreeTimerTask extends TimerTask{
        
        protected Thread usedThread;
        
        public ForceFreeTimerTask(Thread usedThread){
            this.usedThread = usedThread;
        }
        
        public void run(){
            freeResource(usedThread);
        }
    }
    
    protected class ResourceChecker extends Thread{
        
        public boolean isStop;
        
        public ResourceChecker(){
            super("Nimbus SemaphoreResourceCheckDaemon");
            setDaemon(true);
        }
        
        public void run(){
            while(!isStop && checkInterval > 0){
                try{
                    Thread.sleep(checkInterval);
                }catch(InterruptedException e){}
                if(mResourceCnt > 0){
                    getMonitor.notifyMonitor();
                }
            }
        }
    }
}
