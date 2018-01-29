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
package jp.ossc.nimbus.service.queue;

import java.io.*;

import jp.ossc.nimbus.service.cache.CachedReference;
import jp.ossc.nimbus.lang.IllegalServiceStateException;

/**
 * �x��Queue�T�[�r�X�B<p>
 *
 * @author M.Takata
 */
public class DelayQueueService extends DefaultQueueService
 implements DelayQueueServiceMBean, Serializable{
    
    private static final long serialVersionUID = 4071276435068575666L;
    
    private long delayTime = -1;
    
    private boolean isDelay = true;
    
    // DelayQueueServiceMBean ��JavaDoc
    public void setDelayTime(long millis){
        delayTime = millis;
    }
    // DelayQueueServiceMBean ��JavaDoc
    public long getDelayTime(){
        return delayTime;
    }
    
    // DelayQueueServiceMBean ��JavaDoc
    public boolean isDelay(){
        return isDelay;
    }
    // DelayQueueServiceMBean ��JavaDoc
    public void setDelay(boolean isDelay){
        boolean isChangeNoDelay = this.isDelay && !isDelay;
        this.isDelay = isDelay;
        
        if(isChangeNoDelay){
            peekMonitor.notifyAllMonitor();
            if(isSafeGetOrder){
                getMonitor.notifyMonitor();
            }else{
                getMonitor.notifyAllMonitor();
            }
        }
    }
    
    protected boolean pushElement(Object element, long timeout){
        if(getState() != STARTED || fourceEndFlg){
            throw new IllegalServiceStateException(this);
        }
        if(!(element instanceof QueueElement)){
            element = new QueueElement(element);
        }
        if(maxThresholdSize > 0
             && (pushMonitor.isWait()
                    || (size() >= maxThresholdSize))
             && !fourceEndFlg
        ){
            try{
                if(timeout == 0){
                    return false;
                }else if(timeout < 0){
                    pushMonitor.initAndWaitMonitor();
                }else{
                    if(!pushMonitor.initAndWaitMonitor(timeout)){
                        return false;
                    }
                }
            }catch(InterruptedException e){
                return false;
            }finally{
                pushMonitor.releaseMonitor();
            }
        }
        
        if(cache == null){
            queueElements.add(element);
        }else{
            final CachedReference ref = cache.add(element);
            if(ref != null){
                ref.addCacheRemoveListener(this);
                queueElements.add(ref);
            }else{
                queueElements.add(element);
            }
        }
        int size = size();
        if(size > maxDepth){
            maxDepth = size;
        }
        count++;
        countDelta++;
        lastPushedTime = System.currentTimeMillis();
        
        peekMonitor.notifyAllMonitor();
        if(getMonitor.isWait()){
            long waitTime = 0;
            if(isDelay){
                Object firstElement = getQueueElement(false);
                if(firstElement != EMPTY){
                    waitTime = delayTime - (System.currentTimeMillis() - ((QueueElement)firstElement).pushTime);
                }
            }
            if(!isDelay || waitTime <= 0){
                if(isSafeGetOrder){
                    getMonitor.notifyMonitor();
                }else{
                    getMonitor.notifyAllMonitor();
                }
            }
        }
        if(pushMonitor.isWait() && size() < maxThresholdSize){
            pushMonitor.notifyMonitor();
        }
        return true;
    }
    
    protected Object getQueueElement(long timeOutMs, boolean isRemove){
        if(delayTime <= 0 || !isDelay){
            return super.getQueueElement(timeOutMs, isRemove);
        }
        long processTime = 0;
        try{
            if(isRemove){
                getMonitor.initMonitor();
            }else{
                peekMonitor.initMonitor();
            }
            // �����I���łȂ��ꍇ
            while(!fourceEndFlg){
                
                long curSleepTime = 0;
                
                // �L���[�ɗ��܂��Ă���ꍇ
                if(size() > 0){
                    // �Q�Ƃ��邾���̏ꍇ
                    // �܂��́A���̃X���b�h����ԍŏ��ɑ҂��Ă����ꍇ
                    if(!isRemove
                        || !isSafeGetOrder
                        || getMonitor.isFirst()
                    ){
                        // �ŏ��̗v�f���Q�Ƃ���
                        Object firstElement = getQueueElement(false);
                        if(firstElement == EMPTY){
                            continue;
                        }
                        final long currentTime = System.currentTimeMillis();
                        curSleepTime = delayTime - (currentTime - ((QueueElement)firstElement).pushTime);
                        
                        // �x�����Ԃ��߂��Ă����ꍇ
                        if(!isDelay || curSleepTime <= 0){
                            // �L���[����擾����
                            final Object element = getQueueElement(isRemove);
                            if(element == EMPTY){
                                continue;
                            }
                            getMonitor.releaseMonitor();
                            
                            // �Q�Ƃł͂Ȃ��A�L���[�ɗ��܂��Ă��āA
                            // ���ɑ҂��Ă���X���b�h������ꍇ
                            if(isRemove && size() > 0 && getMonitor.isWait()){
                                if(isSafeGetOrder){
                                    getMonitor.notifyMonitor();
                                }else{
                                    getMonitor.notifyAllMonitor();
                                }
                            }
                            if(isRemove){
                                if(pushMonitor.isWait() && size() < maxThresholdSize){
                                    pushMonitor.notifyMonitor();
                                }
                                return element == null ? null : ((QueueElement)element).rawElement;
                            }
                        }
                        // �Q�Ƃł͂Ȃ��A���̃X���b�h�����O�ɑ҂��Ă����X���b�h������ꍇ
                        else if(getMonitor.isWait()){
                            // ��ԍŏ��ɑ҂��Ă���X���b�h���N����
                            getMonitor.notifyMonitor();
                        }
                    }
                }
                
                // �L���[�ɗ��܂��Ă��Ȃ��ꍇ
                // �܂��́A���̃X���b�h�����O�ɑ҂��Ă����X���b�h������ꍇ
                if(curSleepTime == 0){
                    curSleepTime = timeOutMs >= 0 ? timeOutMs - processTime : sleepTime;
                }else{
                    curSleepTime = Math.min(curSleepTime, timeOutMs >= 0 ? timeOutMs - processTime : sleepTime);
                }
                
                // �����I���܂��̓^�C���A�E�g�̏ꍇ
                if(fourceEndFlg || timeOutMs == 0 || (timeOutMs > 0 && timeOutMs <= processTime)){
                    break;
                }
                
                // �^�C���A�E�g�w�肪����ꍇ�́A�^�C���A�E�g�܂�sleep����
                // �^�C���A�E�g�w�肪�Ȃ��ꍇ�́AsleepTime��sleep���Ă݂�
                long proc = 0;
                if(timeOutMs >= 0){
                    proc = System.currentTimeMillis();
                }
                try{
                    if(curSleepTime > 0){
                        long waitTime = 0;
                        if(size() != 0 && isDelay){
                            Object firstElement = getQueueElement(false);
                            if(firstElement != EMPTY){
                                waitTime = delayTime - (System.currentTimeMillis() - ((QueueElement)firstElement).pushTime);
                            }
                        }
                        if(size() == 0
                            || !isRemove
                            || (isSafeGetOrder && !getMonitor.isFirst())
                            || waitTime > 0
                        ){
                            if(isRemove){
                                getMonitor.initAndWaitMonitor(curSleepTime);
                            }else{
                                peekMonitor.initAndWaitMonitor(curSleepTime);
                            }
                        }
                    }
                }catch(InterruptedException e){
                    return null;
                }
                if(timeOutMs >= 0){
                    proc = System.currentTimeMillis() - proc;
                    processTime += proc;
                }
            }
            
            // �����I���̏ꍇ
            if(fourceEndFlg){
                final Object element = getQueueElement(isRemove);
                return element == null || element == EMPTY ? null : ((QueueElement)element).rawElement;
            }
            // �^�C���A�E�g�̏ꍇ
            else{
                if(isRemove
                    && size() > 0
                    && getMonitor.isWait()
                ){
                    long waitTime = 0;
                    if(isDelay){
                        Object firstElement = getQueueElement(false);
                        if(firstElement != EMPTY){
                            waitTime = delayTime - (System.currentTimeMillis() - ((QueueElement)firstElement).pushTime);
                        }
                    }
                    if(!isDelay || waitTime <= 0){
                        if(isSafeGetOrder){
                            getMonitor.notifyMonitor();
                        }else{
                            getMonitor.notifyAllMonitor();
                        }
                    }
                }
                
                return null;
            }
        }finally{
            if(isRemove){
                getMonitor.releaseMonitor();
            }else{
                peekMonitor.releaseMonitor();
            }
        }
    }
    
    public static class QueueElement implements Serializable{
        
        private static final long serialVersionUID = 3734646743111972208L;
        
        public Object rawElement;
        public long pushTime;
        
        public QueueElement(Object element){
            this(element, System.currentTimeMillis());
        }
        
        public QueueElement(Object element, long time){
            rawElement = element;
            pushTime = time;
        }
        
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.append('{');
            buf.append("raw=").append(rawElement);
            buf.append(",time=").append(pushTime);
            buf.append('}');
            return buf.toString();
        }
    }
}