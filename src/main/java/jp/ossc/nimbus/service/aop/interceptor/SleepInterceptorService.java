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

import java.util.Random;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;

/**
 * Sleepインターセプタ。<p>
 * メソッドの呼び出しに対して、一定時間だけsleepするインターセプタである。<br>
 * 以下に、Sleepインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="SleepInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.SleepInterceptorService"&gt;
 *             &lt;attribute name="SleepTime"&gt;500&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class SleepInterceptorService extends ServiceBase
 implements Interceptor, SleepInterceptorServiceMBean{
    
    private static final long serialVersionUID = 4049992073980961148L;
    
    private long sleepTime;
    private int randomSleepTime;
    
    // SleepInterceptorServiceMBeanのJavaDoc
    public void setSleepTime(long time){
        sleepTime = time;
    }
    // SleepInterceptorServiceMBeanのJavaDoc
    public long getSleepTime(){
        return sleepTime;
    }
    
    // SleepInterceptorServiceMBeanのJavaDoc
    public void setRandomSleepTime(int time){
        randomSleepTime = time;
    }
    // SleepInterceptorServiceMBeanのJavaDoc
    public int getRandomSleepTime(){
        return randomSleepTime;
    }
    
    /**
     * 指定された時間だけsleepして、次のインターセプタを呼び出す。<p>
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
        if(getState() == STARTED){
            
            long currentSleepTime = 0;
            if(sleepTime > 0){
                currentSleepTime = sleepTime;
            }else if(randomSleepTime > 0){
                Random random = new Random(System.currentTimeMillis());
                currentSleepTime = random.nextInt(randomSleepTime);
            }
            if(currentSleepTime > 0){
                try{
                    Thread.sleep(currentSleepTime);
                }catch(InterruptedException e){
                    Thread.interrupted();
                }
            }
        }
        return chain.invokeNext(context);
    }
}
