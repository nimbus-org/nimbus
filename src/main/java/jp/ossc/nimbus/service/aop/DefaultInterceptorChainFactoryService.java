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

import java.util.*;
import java.util.regex.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.text.*;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.invoker.*;
import jp.ossc.nimbus.service.aop.interceptor.MetricsInfo;
import jp.ossc.nimbus.service.cache.CacheMap;

/**
 * デフォルトインターセプタチェーンファクトリ。<p>
 * キー文字列に合致する{@link InterceptorChain}を取得するファクトリ。<br>
 * 以下に、特定のキー毎に異なる{@link InterceptorChainList インターセプタチェーンリスト}を持つ{@link InterceptorChain インターセプタチェーン}を取得するインターセプタチェーンファクトリのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="InterceptorChainFactory"
 *                  code="jp.ossc.nimbus.service.aop.DefaultInterceptorChainFactoryService"&gt;
 *             &lt;attribute name="InterceptorChainListMapping"&gt;
 *                 hoge=#HogeInterceptorChainList
 *                 fuga=#FugaInterceptorChainList
 *             &lt;/attribute&gt;
 *             &lt;attribute name="DefaultInterceptorChainListServiceName"&gt;#DefaultInterceptorChainList&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class DefaultInterceptorChainFactoryService extends ServiceBase
 implements InterceptorChainFactory, DefaultInterceptorChainFactoryServiceMBean{
    
    private static final long serialVersionUID = 8047982449933936760L;
    
    private static final Comparator COMP = new MetricsInfoComparator();
    private static final String LINE_SEP = System.getProperty("line.separator");
    
    private Map interceptorChainListMapping;
    private Map keyAndChainListMap;
    private Map interceptorMapping;
    private Map keyAndInterceptorMap;
    private Map invokerMapping;
    private Map keyAndInvokerMap;
    private ServiceName defaultInterceptorChainListServiceName;
    private InterceptorChainList defaultInterceptorChainList;
    private ServiceName defaultInvokerServiceName;
    private Invoker defaultInvoker;
    private boolean isRegexEnabled;
    private int regexMatchFlag;
    private ServiceName interceptorChainCacheMapServiceName;
    private CacheMap chainCache;
    private boolean isUseThreadLocalInterceptorChain = true;
    private boolean isGetMetrics;
    private ConcurrentMap metricsInfos;
    private boolean isCalculateOnlyNormal;
    private String dateFormat = DEFAULT_DATE_FORMAT;
    private boolean isOutputTimestamp = false;
    private boolean isOutputCount = true;
    private boolean isOutputExceptionCount = false;
    private boolean isOutputErrorCount = false;
    private boolean isOutputLastTime = false;
    private boolean isOutputLastExceptionTime = false;
    private boolean isOutputLastErrorTime = false;
    private boolean isOutputBestPerformance = true;
    private boolean isOutputBestPerformanceTime = false;
    private boolean isOutputWorstPerformance = true;
    private boolean isOutputWorstPerformanceTime = false;
    private boolean isOutputAveragePerformance = true;
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setInterceptorChainListMapping(Map mapping){
        interceptorChainListMapping = mapping;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public Map getInterceptorChainListMapping(){
        return interceptorChainListMapping;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setInterceptorMapping(Map mapping){
        interceptorMapping = mapping;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public Map getInterceptorMapping(){
        return interceptorMapping;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setDefaultInterceptorChainListServiceName(ServiceName name){
        defaultInterceptorChainListServiceName = name;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public ServiceName getDefaultInterceptorChainListServiceName(){
        return defaultInterceptorChainListServiceName;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setInvokerMapping(Map mapping){
        invokerMapping = mapping;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public Map getInvokerMapping(){
        return invokerMapping;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setDefaultInvokerServiceName(ServiceName name){
        defaultInvokerServiceName = name;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public ServiceName getDefaultInvokerServiceName(){
        return defaultInvokerServiceName;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setRegexEnabled(boolean isEnable){
        isRegexEnabled = isEnable;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public boolean isRegexEnabled(){
        return isRegexEnabled;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setRegexMatchFlag(int flag){
        regexMatchFlag = flag;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public int getRegexMatchFlag(){
        return regexMatchFlag;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setInterceptorChainCacheMapServiceName(ServiceName name){
        interceptorChainCacheMapServiceName = name;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public ServiceName getInterceptorChainCacheMapServiceName(){
        return interceptorChainCacheMapServiceName;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setUseThreadLocalInterceptorChain(boolean isUse){
        isUseThreadLocalInterceptorChain = isUse;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public boolean isUseThreadLocalInterceptorChain(){
        return isUseThreadLocalInterceptorChain;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setGetMetrics(boolean isGet){
        isGetMetrics = isGet;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public boolean isGetMetrics(){
        return isGetMetrics;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setCalculateOnlyNormal(boolean isCalc){
        isCalculateOnlyNormal = isCalc;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public boolean isCalculateOnlyNormal(){
        return isCalculateOnlyNormal;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setOutputTimestamp(boolean isOutput){
        isOutputTimestamp = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public boolean isOutputTimestamp(){
        return isOutputTimestamp;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setOutputCount(boolean isOutput){
        isOutputCount = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public boolean isOutputCount(){
        return isOutputCount;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setOutputExceptionCount(boolean isOutput){
        isOutputExceptionCount = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public boolean isOutputExceptionCount(){
        return isOutputExceptionCount;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setOutputErrorCount(boolean isOutput){
        isOutputErrorCount = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public boolean isOutputErrorCount(){
        return isOutputErrorCount;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setOutputLastTime(boolean isOutput){
        isOutputLastTime = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public boolean isOutputLastTime(){
        return isOutputLastTime;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setOutputLastExceptionTime(boolean isOutput){
        isOutputLastExceptionTime = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public boolean isOutputLastExceptionTime(){
        return isOutputLastExceptionTime;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setOutputLastErrorTime(boolean isOutput){
        isOutputLastErrorTime = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public boolean isOutputLastErrorTime(){
        return isOutputLastErrorTime;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setOutputBestPerformance(boolean isOutput){
        isOutputBestPerformance = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public boolean isOutputBestPerformance(){
        return isOutputBestPerformance;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setOutputBestPerformanceTime(boolean isOutput){
        isOutputBestPerformanceTime = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public boolean isOutputBestPerformanceTime(){
        return isOutputBestPerformanceTime;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setOutputWorstPerformance(boolean isOutput){
        isOutputWorstPerformance = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public boolean isOutputWorstPerformance(){
        return isOutputWorstPerformance;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setOutputWorstPerformanceTime(boolean isOutput){
        isOutputWorstPerformanceTime = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public boolean isOutputWorstPerformanceTime(){
        return isOutputWorstPerformanceTime;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setOutputAveragePerformance(boolean isOutput){
        isOutputAveragePerformance = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public boolean isOutputAveragePerformance(){
        return isOutputAveragePerformance;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void setDateFormat(String format){
        dateFormat = format;
    }
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public String getDateFormat(){
        return dateFormat;
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public String displayMetricsInfo(){
        final MetricsInfo[] infos = (MetricsInfo[])metricsInfos.values()
            .toArray(new MetricsInfo[metricsInfos.size()]);
        Arrays.sort(infos, COMP);
        final SimpleDateFormat format
             = new SimpleDateFormat(dateFormat);
        final StringBuilder buf = new StringBuilder();
        buf.append("\"No.\"");
        if(isOutputCount){
            buf.append(",\"Count\"");
        }
        if(isOutputExceptionCount){
            buf.append(",\"ExceptionCount\"");
        }
        if(isOutputErrorCount){
            buf.append(",\"ErrorCount\"");
        }
        if(isOutputLastTime){
            buf.append(",\"LastTime\"");
        }
        if(isOutputLastExceptionTime){
            buf.append(",\"LastExceptionTime\"");
        }
        if(isOutputLastErrorTime){
            buf.append(",\"LastErrorTime\"");
        }
        if(isOutputBestPerformance){
            buf.append(",\"Best performance[ms]\"");
        }
        if(isOutputBestPerformanceTime){
            buf.append(",\"Best performance time\"");
        }
        if(isOutputWorstPerformance){
            buf.append(",\"Worst performance[ms]\"");
        }
        if(isOutputWorstPerformanceTime){
            buf.append(",\"Worst performance time\"");
        }
        if(isOutputAveragePerformance){
            buf.append(",\"Average performance[ms]\"");
        }
        buf.append(",\"Method\"");
        buf.append(LINE_SEP);
        for(int i = 0; i < infos.length; i++){
            buf.append('"').append(i + 1).append('"');
            if(isOutputCount){
                buf.append(',').append('"').append(infos[i].getCount()).append('"');
            }
            if(isOutputExceptionCount){
                buf.append(',').append('"').append(infos[i].getExceptionCount())
                    .append('"');
            }
            if(isOutputErrorCount){
                buf.append(',').append('"').append(infos[i].getErrorCount())
                    .append('"');
            }
            if(isOutputLastTime){
                if(infos[i].getLastTime() == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append(',').append('"')
                        .append(format.format(new Date(infos[i].getLastTime())))
                        .append('"');
                }
            }
            if(isOutputLastExceptionTime){
                if(infos[i].getLastExceptionTime() == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append(',').append('"')
                        .append(format.format(
                            new Date(infos[i].getLastExceptionTime()))
                        ).append('"');
                }
            }
            if(isOutputLastErrorTime){
                if(infos[i].getLastErrorTime() == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append('"').append(',')
                        .append(format.format(new Date(infos[i].getLastErrorTime())))
                        .append('"');
                }
            }
            if(isOutputBestPerformance){
                buf.append(',').append('"').append(infos[i].getBestPerformance())
                    .append('"');
            }
            if(isOutputBestPerformanceTime){
                if(infos[i].getBestPerformanceTime() == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append(',').append('"').append(format.format(
                        new Date(infos[i].getBestPerformanceTime())
                        )).append('"');
                }
            }
            if(isOutputWorstPerformance){
                buf.append(',').append('"').append(infos[i].getWorstPerformance())
                    .append('"');
            }
            if(isOutputWorstPerformanceTime){
                if(infos[i].getWorstPerformanceTime() == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append(',').append('"').append(format.format(
                        new Date(infos[i].getWorstPerformanceTime())
                        )).append('"');
                }
            }
            if(isOutputAveragePerformance){
                buf.append(',').append('"').append(infos[i].getAveragePerformance())
                    .append('"');
            }
            buf.append(',').append('"').append(infos[i].getKey()).append('"');
            buf.append(LINE_SEP);
        }
        if(isOutputTimestamp){
            buf.append(format.format(new Date())).append(LINE_SEP);
        }
        return buf.toString();
    }
    
    // DefaultInterceptorChainFactoryServiceMBeanのJavaDoc
    public void reset(){
        metricsInfos.clear();
    }
    
    public void createService() throws Exception{
        keyAndChainListMap = new LinkedHashMap();
        keyAndInterceptorMap = new LinkedHashMap();
        keyAndInvokerMap = new LinkedHashMap();
        metricsInfos = new ConcurrentHashMap();
    }
    
    public void startService() throws Exception{
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setServiceManagerName(getServiceManagerName());
        if(interceptorChainListMapping != null){
            final Iterator keys = interceptorChainListMapping
                .keySet().iterator();
            while(keys.hasNext()){
                final String keyStr = (String)keys.next();
                Object key = keyStr;
                if(isRegexEnabled){
                    key = Pattern.compile(keyStr, regexMatchFlag);
                }
                final String nameStr = (String)interceptorChainListMapping
                    .get(keyStr);
                editor.setAsText(nameStr);
                final ServiceName name = (ServiceName)editor.getValue();
                final InterceptorChainList chainList
                     = (InterceptorChainList)ServiceManagerFactory
                        .getServiceObject(name);
                keyAndChainListMap.put(key, chainList);
            }
        }
        if(interceptorMapping != null){
            final Iterator keys = interceptorMapping
                .keySet().iterator();
            while(keys.hasNext()){
                final String keyStr = (String)keys.next();
                Object key = keyStr;
                if(isRegexEnabled){
                    key = Pattern.compile(keyStr, regexMatchFlag);
                }
                final String nameStr = (String)interceptorMapping
                    .get(keyStr);
                editor.setAsText(nameStr);
                final ServiceName name = (ServiceName)editor.getValue();
                keyAndInterceptorMap.put(key, name);
            }
        }
        if(invokerMapping != null){
            final Iterator keys = invokerMapping
                .keySet().iterator();
            while(keys.hasNext()){
                final String keyStr = (String)keys.next();
                Object key = keyStr;
                if(isRegexEnabled){
                    key = Pattern.compile(keyStr, regexMatchFlag);
                }
                final String nameStr = (String)invokerMapping.get(keyStr);
                editor.setAsText(nameStr);
                final ServiceName name = (ServiceName)editor.getValue();
                keyAndInvokerMap.put(key, name);
            }
        }
        if(defaultInterceptorChainListServiceName != null){
            defaultInterceptorChainList
                 = (InterceptorChainList)ServiceManagerFactory
                    .getServiceObject(defaultInterceptorChainListServiceName);
        }
        if(defaultInvokerServiceName == null){
            final MethodReflectionCallInvokerService invoker
                 = new MethodReflectionCallInvokerService();
            invoker.create();
            invoker.start();
            defaultInvoker = invoker;
        }else{
            defaultInvoker = (Invoker)ServiceManagerFactory
                .getServiceObject(defaultInvokerServiceName);
        }
        if(interceptorChainCacheMapServiceName != null){
            chainCache = (CacheMap)ServiceManagerFactory
                    .getServiceObject(interceptorChainCacheMapServiceName);
        }
    }
    
    public void stopService() throws Exception{
        keyAndChainListMap.clear();
        keyAndInterceptorMap.clear();
        keyAndInvokerMap.clear();
        if(chainCache != null){
            chainCache.clear();
        }
    }
    
    public void destroyService() throws Exception{
        keyAndChainListMap = null;
        keyAndInterceptorMap = null;
        keyAndInvokerMap = null;
        metricsInfos = null;
    }
    
    /**
     * キー文字列に合致する{@link InterceptorChain}を取得する。<p>
     * {@link #isUseThreadLocalInterceptorChain()}がtrueの時は、{@link DefaultThreadLocalInterceptorChain}を返すので、スレッド単位での再利用が可能である。<br>
     * また、{@link #setInterceptorChainCacheMapServiceName(ServiceName)}を指定している場合は、生成したInterceptorChainをキャッシュして返す。<br>
     *
     * @param key キー文字列
     * @return キー文字列に合致するInterceptorChain
     */
    public InterceptorChain getInterceptorChain(Object key){
        final String keyStr = key == null ? null : key.toString();
        if(chainCache != null){
            synchronized(chainCache){
                if(chainCache.containsKey(keyStr)){
                    return (InterceptorChain)chainCache.get(keyStr);
                }
            }
        }
        InterceptorChainList chainList = null;
        Invoker invoker = null;
        if(keyStr == null){
            chainList = defaultInterceptorChainList;
            invoker = defaultInvoker;
        }else if(isRegexEnabled){
            if(keyAndChainListMap.size() != 0){
                final Iterator keys = keyAndChainListMap.keySet().iterator();
                while(keys.hasNext()){
                    final Pattern pattern = (Pattern)keys.next();
                    if(pattern.matcher(keyStr).matches()){
                        chainList = (InterceptorChainList)keyAndChainListMap
                            .get(pattern);
                        break;
                    }
                }
            }
            if(chainList == null && keyAndInterceptorMap.size() != 0){
                final Iterator keys = keyAndInterceptorMap.keySet().iterator();
                while(keys.hasNext()){
                    final Pattern pattern = (Pattern)keys.next();
                    if(pattern.matcher(keyStr).matches()){
                        if(chainList == null){
                            chainList = new DefaultInterceptorChainList();
                        }
                        final ServiceName name
                             = (ServiceName)keyAndInterceptorMap.get(pattern);
                        final Interceptor interceptor
                             = (Interceptor)ServiceManagerFactory
                                .getServiceObject(name);
                        ((DefaultInterceptorChainList)chainList).addInterceptor(
                            interceptor
                        );
                        break;
                    }
                }
            }
            if(keyAndInvokerMap.size() != 0){
                final Iterator keys = keyAndInvokerMap.keySet().iterator();
                while(keys.hasNext()){
                    final Pattern pattern = (Pattern)keys.next();
                    if(pattern.matcher(keyStr).matches()){
                        invoker = (Invoker)keyAndInvokerMap.get(pattern);
                        break;
                    }
                }
            }
        }else{
            if(keyAndChainListMap.size() != 0){
                chainList = (InterceptorChainList)keyAndChainListMap
                    .get(keyStr);
            }
            if(keyAndInvokerMap.size() != 0){
                invoker = (Invoker)keyAndInvokerMap.get(keyStr);
            }
        }
        if(chainList == null){
            chainList = defaultInterceptorChainList;
        }
        if(invoker == null){
            invoker = defaultInvoker;
        }
        if(chainList == null && invoker == null){
            return null;
        }
        InterceptorChain chain = isUseThreadLocalInterceptorChain
            ? new DefaultThreadLocalInterceptorChain(
                chainList,
                invoker
              )
            : new DefaultInterceptorChain(
                chainList,
                invoker
              );
        if(isGetMetrics){
            ((DefaultInterceptorChain)chain).setMetricsInfoMap(metricsInfos);
            ((DefaultInterceptorChain)chain).setCalculateOnlyNormal(isCalculateOnlyNormal);
        }
        if(chainCache != null && isUseThreadLocalInterceptorChain){
            synchronized(chainCache){
                if(chainCache.containsKey(keyStr)){
                    return (InterceptorChain)chainCache.get(keyStr);
                }else{
                    chainCache.put(keyStr, chain);
                }
            }
        }
        return chain;
    }
    
    public InterceptorChainList getInterceptorChainList(Object key){
        final String keyStr = key == null ? null : key.toString();
        InterceptorChainList chainList = null;
        if(keyStr == null){
            chainList = defaultInterceptorChainList;
        }else if(isRegexEnabled){
            if(keyAndChainListMap.size() != 0){
                final Iterator keys = keyAndChainListMap.keySet().iterator();
                while(keys.hasNext()){
                    final Pattern pattern = (Pattern)keys.next();
                    if(pattern.matcher(keyStr).matches()){
                        chainList = (InterceptorChainList)keyAndChainListMap
                            .get(pattern);
                        break;
                    }
                }
            }
            if(chainList == null && keyAndInterceptorMap.size() != 0){
                final Iterator keys = keyAndInterceptorMap.keySet().iterator();
                while(keys.hasNext()){
                    final Pattern pattern = (Pattern)keys.next();
                    if(pattern.matcher(keyStr).matches()){
                        if(chainList == null){
                            chainList = new DefaultInterceptorChainList();
                        }
                        final ServiceName name
                             = (ServiceName)keyAndInterceptorMap.get(pattern);
                        final Interceptor interceptor
                             = (Interceptor)ServiceManagerFactory
                                .getServiceObject(name);
                        ((DefaultInterceptorChainList)chainList).addInterceptor(
                            interceptor
                        );
                        break;
                    }
                }
            }
        }else{
            if(keyAndChainListMap.size() != 0){
                chainList = (InterceptorChainList)keyAndChainListMap
                    .get(keyStr);
            }
        }
        if(chainList == null){
            chainList = defaultInterceptorChainList;
        }
        return chainList;
    }
    
    public Invoker getInvoker(Object key){
        final String keyStr = key == null ? null : key.toString();
        Invoker invoker = null;
        if(keyStr == null){
            invoker = defaultInvoker;
        }else if(isRegexEnabled){
            if(keyAndInvokerMap.size() != 0){
                final Iterator keys = keyAndInvokerMap.keySet().iterator();
                while(keys.hasNext()){
                    final Pattern pattern = (Pattern)keys.next();
                    if(pattern.matcher(keyStr).matches()){
                        invoker = (Invoker)keyAndInvokerMap.get(pattern);
                        break;
                    }
                }
            }
        }else{
            if(keyAndInvokerMap.size() != 0){
                invoker = (Invoker)keyAndInvokerMap.get(keyStr);
            }
        }
        if(invoker == null){
            invoker = defaultInvoker;
        }
        return invoker;
    }
    
    private static class MetricsInfoComparator implements Comparator{
        public int compare(Object o1, Object o2){
            final MetricsInfo info1 = (MetricsInfo)o1;
            final MetricsInfo info2 = (MetricsInfo)o2;
            final long sortKey1 = info1.getAveragePerformance() * info1.getCount();
            final long sortKey2 = info2.getAveragePerformance() * info2.getCount();
            if(sortKey1 > sortKey2){
                return -1;
            }else if(sortKey1 < sortKey2){
                return 1;
            }else{
                return 0;
            }
        }
    }
}
