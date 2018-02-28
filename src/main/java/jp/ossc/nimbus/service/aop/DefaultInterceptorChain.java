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
package jp.ossc.nimbus.service.aop;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.interceptor.MetricsInfo;

/**
 * {@link InterceptorChain}のデフォルト実装。<p>
 * 現在呼び出されているインターセプタの情報をインスタンス変数に格納するので、スレッドセーフではないインターセプタチェーンである。<br>
 *
 * @author M.Takata
 */
public class DefaultInterceptorChain
 implements InterceptorChain, java.io.Serializable, Cloneable{
    
    private static final long serialVersionUID = 3689361711046717596L;
    
    /**
     * {@link InterceptorChainList}内の、現在の処理中の{@link Interceptor}のインデックス。<p>
     * 初期値は、-1。
     */
    protected int currentIndex = -1;
    
    /**
     * チェーンするインターセプタのリスト。<p>
     */
    protected transient InterceptorChainList interceptorChainList;
    
    /**
     * {@link InterceptorChainList}インタフェースを実装したサービスのサービス名。<p>
     */
    protected ServiceName interceptorChainListServiceName;
    
    /**
     * 本来の呼び出し先を呼び出すInvoker。<p>
     */
    protected transient Invoker invoker;
    
    /**
     * {@link Invoker}インタフェースを実装したサービスのサービス名。<p>
     */
    protected ServiceName invokerServiceName;
    
    /**
     * キーが{@link Interceptor}または{@link Invoker}、値が{@link MetricsInfo}のマップ。<p>
     */
    protected ConcurrentMap metricsInfos;
    
    /**
     * 正常応答を返した場合だけ処理時間等の計算を行うかどうかのフラグ。<p>
     * デフォルトはfalse
     */
    protected boolean isCalculateOnlyNormal;
    
    /**
     * 空のインターセプタチェーンを生成する。<p>
     */
    public DefaultInterceptorChain(){}
    
    /**
     * 指定された{@link InterceptorChainList}と{@link Invoker}のインターセプタチェーンを生成する。<p>
     *
     * @param list チェーンするインターセプタのリスト
     * @param invoker 本来の呼び出し先を呼び出すInvoker
     */
    public DefaultInterceptorChain(InterceptorChainList list, Invoker invoker){
        setInterceptorChainList(list);
        setInvoker(invoker);
    }
    
    /**
     * 指定された{@link InterceptorChainList}サービスと{@link Invoker}サービスのインターセプタチェーンを生成する。<p>
     *
     * @param listServiceName チェーンするインターセプタのリストInterceptorChainListサービスのサービス名
     * @param invokerServiceName 本来の呼び出し先を呼び出すInvokerサービスのサービス名
     */
    public DefaultInterceptorChain(
        ServiceName listServiceName,
        ServiceName invokerServiceName
    ){
        setInterceptorChainListServiceName(listServiceName);
        setInvokerServiceName(invokerServiceName);
    }
    
    /**
     * 性能統計を格納するマップを設定する。<p>
     *
     * @param infos 性能統計を格納するマップ。キーが{@link Interceptor}または{@link Invoker}、値が{@link MetricsInfo}
     */
    public void setMetricsInfoMap(ConcurrentMap infos){
        metricsInfos = infos;
    }
    
    /**
     * 性能統計を格納するマップを取得する。<p>
     *
     * @return 性能統計を格納するマップ。キーが{@link Interceptor}または{@link Invoker}、値が{@link MetricsInfo}
     */
    public ConcurrentMap getMetricsInfoMap(){
        return metricsInfos;
    }
    
    /**
     * 正常応答を返した場合だけ処理時間等の計算を行うかどうかを設定する。<p>
     * デフォルトはfalse
     *
     * @param isCalc 正常応答を返した場合だけ処理時間等の計算を行う場合は、true
     */
    public void setCalculateOnlyNormal(boolean isCalc){
        isCalculateOnlyNormal = isCalc;
    }
    
    /**
     * 正常応答を返した場合だけ処理時間等の計算を行うかどうかを判定する。<p>
     *
     * @return trueの場合は、正常応答を返した場合だけ処理時間等の計算を行う
     */
    public boolean isCalculateOnlyNormal(){
        return isCalculateOnlyNormal;
    }
    
    // InterceptorChainのJavaDoc
    public Object invokeNext(InvocationContext context) throws Throwable{
        final InterceptorChainList list = getInterceptorChainList();
        boolean isError = false;
        boolean isException = false;
        long start = 0;
        if(metricsInfos != null){
            start = System.currentTimeMillis();
        }
        if(list == null){
            final Invoker ivk = getInvoker();
            if(ivk != null){
                try{
                    return ivk.invoke(context);
                }catch(Exception e){
                    isException = true;
                    throw e;
                }catch(Error err){
                    isError = true;
                    throw err;
                }finally{
                    if(metricsInfos != null){
                        long end = System.currentTimeMillis();
                        MetricsInfo metricsInfo = (MetricsInfo)metricsInfos.get(ivk);
                        if(metricsInfo == null){
                            metricsInfo = new MetricsInfo(
                                createKey(ivk),
                                isCalculateOnlyNormal
                            );
                            MetricsInfo old = (MetricsInfo)metricsInfos.putIfAbsent(ivk, metricsInfo);
                            if(old != null){
                                metricsInfo = old;
                            }
                        }
                        metricsInfo.calculate(end - start, isException, isError);
                    }
                }
            }else{
                return null;
            }
        }
        int index = getCurrentInterceptorIndex();
        try{
            setCurrentInterceptorIndex(++index);
            final Interceptor interceptor = list.getInterceptor(context, index);
            if(interceptor != null){
                try{
                    return interceptor.invoke(context, this);
                }catch(Exception e){
                    isException = true;
                    throw e;
                }catch(Error err){
                    isError = true;
                    throw err;
                }finally{
                    if(metricsInfos != null){
                        long end = System.currentTimeMillis();
                        MetricsInfo metricsInfo = (MetricsInfo)metricsInfos.get(interceptor);
                        if(metricsInfo == null){
                            metricsInfo = new MetricsInfo(
                                createKey(interceptor),
                                isCalculateOnlyNormal
                            );
                            MetricsInfo old = (MetricsInfo)metricsInfos.putIfAbsent(interceptor, metricsInfo);
                            if(old != null){
                                metricsInfo = old;
                            }
                        }
                        metricsInfo.calculate(end - start, isException, isError);
                    }
                }
            }else{
                final Invoker ivk = getInvoker();
                if(ivk != null){
                    try{
                        return ivk.invoke(context);
                    }catch(Exception e){
                        isException = true;
                        throw e;
                    }catch(Error err){
                        isError = true;
                        throw err;
                    }finally{
                        if(metricsInfos != null){
                            long end = System.currentTimeMillis();
                            MetricsInfo metricsInfo = (MetricsInfo)metricsInfos.get(ivk);
                            if(metricsInfo == null){
                                metricsInfo = new MetricsInfo(
                                    createKey(ivk),
                                    isCalculateOnlyNormal
                                );
                                MetricsInfo old = (MetricsInfo)metricsInfos.putIfAbsent(ivk, metricsInfo);
                                if(old != null){
                                    metricsInfo = old;
                                }
                            }
                            metricsInfo.calculate(end - start, isException, isError);
                        }
                    }
                }else{
                    return null;
                }
            }
        }finally{
            setCurrentInterceptorIndex(--index);
        }
    }
    
    protected String createKey(Object target){
        if(target instanceof Service){
            Service service = (Service)target;
            if(service.getServiceNameObject() != null){
                return service.getServiceNameObject().toString();
            }else if(service.getServiceName() != null){
                return service.getServiceName().toString();
            }else{
                return service.toString();
            }
        }else{
            return target.toString();
        }
    }
    
    // InterceptorChainのJavaDoc
    public int getCurrentInterceptorIndex(){
        return currentIndex;
    }
    
    // InterceptorChainのJavaDoc
    public void setCurrentInterceptorIndex(int index){
        currentIndex = index;
    }
    
    // InterceptorChainのJavaDoc
    public InterceptorChainList getInterceptorChainList(){
        if(interceptorChainListServiceName != null){
            try{
                return (InterceptorChainList)ServiceManagerFactory
                        .getServiceObject(interceptorChainListServiceName);
            }catch(ServiceNotFoundException e){
            }
        }
        return interceptorChainList;
    }
    
    /**
     * このインターセプタチェーンが持つインターセプタのリストを設定する。<p>
     *
     * @param list このインターセプタチェーンが持つインターセプタのリスト
     */
    public void setInterceptorChainList(InterceptorChainList list){
        if(interceptorChainList instanceof ServiceBase){
            interceptorChainListServiceName
                 = ((ServiceBase)list).getServiceNameObject();
        }else if(interceptorChainList instanceof Service){
            final Service service = (Service)list;
            if(service.getServiceManagerName() != null){
                interceptorChainListServiceName = new ServiceName(
                    service.getServiceManagerName(),
                    service.getServiceName()
                );
            }
        }
        if(interceptorChainListServiceName == null){
            interceptorChainList = list;
        }
    }
    
    /**
     * このインターセプタチェーンが持つインターセプタのリストInterceptorChainListサービスのサービス名を設定する。<p>
     *
     * @param name このインターセプタチェーンが持つインターセプタのリストInterceptorChainListサービスのサービス名
     */
    public void setInterceptorChainListServiceName(ServiceName name){
        interceptorChainListServiceName = name;
    }
    
    // InterceptorChainのJavaDoc
    public Invoker getInvoker(){
        if(invokerServiceName != null){
            try{
                return (Invoker)ServiceManagerFactory
                    .getServiceObject(invokerServiceName);
            }catch(ServiceNotFoundException e){
            }
        }
        return invoker;
    }
    
    /**
     * 最後の呼び出しを行うInvokerを設定する。<p>
     *
     * @param invoker 最後の呼び出しを行うInvoker
     */
    public void setInvoker(Invoker invoker){
        if(invoker instanceof ServiceBase){
            invokerServiceName = ((ServiceBase)invoker).getServiceNameObject();
        }else if(invoker instanceof Service){
            final Service service = (Service)invoker;
            if(service.getServiceManagerName() != null){
                invokerServiceName = new ServiceName(
                    service.getServiceManagerName(),
                    service.getServiceName()
                );
            }
        }
        if(invokerServiceName == null){
            this.invoker = invoker;
        }
    }
    
    /**
     * 最後の呼び出しを行うInvokerサービスのサービス名を設定する。<p>
     *
     * @param name 最後の呼び出しを行うInvokerサービスのサービス名
     */
    public void setInvokerServiceName(ServiceName name){
        this.invokerServiceName = name;
    }
    
    // InterceptorChainのJavaDoc
    public InterceptorChain cloneChain(){
        try{
            DefaultInterceptorChain clone = (DefaultInterceptorChain)clone();
            clone.currentIndex = -1;
            return clone;
        }catch(CloneNotSupportedException e){
            return null;
        }
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        if(interceptorChainListServiceName == null){
            out.writeObject(interceptorChainList);
        }
        if(invokerServiceName == null){
            out.writeObject(invoker);
        }
    }
    
    private void readObject(ObjectInputStream in)
     throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        if(interceptorChainListServiceName == null){
            interceptorChainList = (InterceptorChainList)in.readObject();
        }else{
            interceptorChainList = (InterceptorChainList)ServiceManagerFactory
                .getServiceObject(interceptorChainListServiceName);
        }
        if(invokerServiceName == null){
            invoker = (Invoker)in.readObject();
        }else{
            invoker = (Invoker)ServiceManagerFactory
                .getServiceObject(invokerServiceName);
        }
    }
}
