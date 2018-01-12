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
package jp.ossc.nimbus.service.aop.interceptor;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.util.SynchronizeMonitor;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;

import java.util.*;

/**
 * ブレイクポイントインターセプタ。<p>
 * メソッドの呼び出しに対して、任意の間、スレッドを中断するインターセプタである。<br>
 * 以下に、ブレイクポイントインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="BreakPointInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.BreakPointInterceptorService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class BreakPointInterceptorService extends ServiceBase
 implements Interceptor, BreakPointInterceptorServiceMBean{
    
    private static final long serialVersionUID = -2667830848395155759L;
    
    private SynchronizeMonitor monitor = new WaitSynchronizeMonitor();
    private SynchronizeMonitor listenerMonitor = new WaitSynchronizeMonitor();
    private boolean enabled = true;
    private int breakPoint = BREAK_POINT_IN;
    private long timeout;
    private List threads = new ArrayList();
    
    // BreakPointInterceptorServiceMBeanのJavaDoc
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }
    // BreakPointInterceptorServiceMBeanのJavaDoc
    public boolean isEnabled(){
        return enabled;
    }
    
    // BreakPointInterceptorServiceMBeanのJavaDoc
    public void setMonitor(SynchronizeMonitor monitor){
        this.monitor = monitor;
    }
    // BreakPointInterceptorServiceMBeanのJavaDoc
    public SynchronizeMonitor getMonitor(){
        return monitor;
    }
    
    // BreakPointInterceptorServiceMBeanのJavaDoc
    public void setBreakPoint(int breakPoint){
        this.breakPoint = breakPoint;
    }
    // BreakPointInterceptorServiceMBeanのJavaDoc
    public int getBreakPoint(){
        return breakPoint;
    }
    
    // BreakPointInterceptorServiceMBeanのJavaDoc
    public void setTimeout(long timeout){
        this.timeout = timeout;
    }
    // BreakPointInterceptorServiceMBeanのJavaDoc
    public long getTimeout(){
        return timeout;
    }
    
    // BreakPointInterceptorServiceMBeanのJavaDoc
    public void resume(){
        synchronized(monitor){
            monitor.notifyMonitor();
        }
    }
    
    // BreakPointInterceptorServiceMBeanのJavaDoc
    public void resumeAll(){
        synchronized(monitor){
            monitor.notifyAllMonitor();
        }
    }
    
    // BreakPointInterceptorServiceMBeanのJavaDoc
    public List suspendThreads(){
        synchronized(threads){
            return new ArrayList(threads);
        }
    }
    
    // BreakPointInterceptorServiceMBeanのJavaDoc
    public void waitSuspend() throws InterruptedException{
        if(getState() != STARTED){
            return;
        }
        synchronized(listenerMonitor){
            synchronized(threads){
                if(threads.size() != 0){
                    return;
                }
            }
            listenerMonitor.initMonitor();
            listenerMonitor.waitMonitor();
        }
    }
    
    // BreakPointInterceptorServiceMBeanのJavaDoc
    public boolean waitSuspend(long timeout) throws InterruptedException{
        if(getState() != STARTED){
            return false;
        }
        synchronized(listenerMonitor){
            synchronized(threads){
                if(threads.size() != 0){
                    return true;
                }
            }
            listenerMonitor.initMonitor();
            listenerMonitor.waitMonitor(timeout);
            synchronized(threads){
                if(threads.size() == 0){
                    return false;
                }else{
                    return true;
                }
            }
        }
    }
    
    // BreakPointInterceptorServiceMBeanのJavaDoc
    public void waitSuspend(String threadName) throws InterruptedException{
        if(getState() != STARTED){
            return;
        }
        synchronized(listenerMonitor){
            synchronized(threads){
                if(threads.contains(threadName)){
                    return;
                }
            }
            listenerMonitor.initMonitor();
            listenerMonitor.waitMonitor();
            synchronized(threads){
                if(threads.contains(threadName)){
                    return;
                }
            }
            waitSuspend(threadName);
        }
    }
    
    // BreakPointInterceptorServiceMBeanのJavaDoc
    public boolean waitSuspend(String threadName, long timeout) throws InterruptedException{
        if(getState() != STARTED){
            return false;
        }
        synchronized(listenerMonitor){
            synchronized(threads){
                if(threads.contains(threadName)){
                    return true;
                }
            }
            final long startTime = System.currentTimeMillis();
            listenerMonitor.initMonitor();
            listenerMonitor.waitMonitor(timeout);
            final long waitTime = System.currentTimeMillis() - startTime;
            synchronized(threads){
                if(threads.contains(threadName)){
                    return true;
                }else if(waitTime >= timeout){
                    return false;
                }
            }
            return waitSuspend(threadName, timeout - waitTime);
        }
    }
    
    public void stopService() throws Exception{
        synchronized(listenerMonitor){
            listenerMonitor.notifyAllMonitor();
        }
        synchronized(monitor){
            monitor.notifyAllMonitor();
        }
    }
    
    /**
     * 指定されたブレイクポイントが{@link #BREAK_POINT_IN}の場合、{@link #resume()}もしくは{@link #resumeAll()}が呼び出されるまで待機して、次のインターセプタを呼び出す。ブレイクポイントが{@link #BREAK_POINT_OUT}の場合、次のインターセプタを呼び出した後、{@link #resume()}もしくは{@link #resumeAll()}が呼び出されるまで待機する。<p>
     * サービスが開始されていない場合は、何もせずに次のインターセプタを呼び出す。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても、呼び出し元には伝播されない。
     */
    public Object invoke(
        InvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        if(getState() == STARTED && enabled && breakPoint == BREAK_POINT_IN){
            breakpoint();
        }
        try{
            return chain.invokeNext(context);
        }finally{
            if(getState() == STARTED && enabled && breakPoint == BREAK_POINT_OUT){
                breakpoint();
            }
        }
    }
    
    private void breakpoint(){
        Thread thread = Thread.currentThread();
        try{
            synchronized(listenerMonitor){
                synchronized(threads){
                    threads.add(thread.getName());
                }
                listenerMonitor.notifyAllMonitor();
            }
            synchronized(monitor){
                try{
                    monitor.initMonitor();
                    if(timeout > 0){
                        monitor.waitMonitor(timeout);
                    }else{
                        monitor.waitMonitor();
                    }
                }catch(InterruptedException e){
                }
            }
        }finally{
            synchronized(listenerMonitor){
                synchronized(threads){
                    threads.remove(thread.getName());
                }
            }
        }
    }
}
