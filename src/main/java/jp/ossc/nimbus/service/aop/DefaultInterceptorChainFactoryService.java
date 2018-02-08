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
 * �f�t�H���g�C���^�[�Z�v�^�`�F�[���t�@�N�g���B<p>
 * �L�[������ɍ��v����{@link InterceptorChain}���擾����t�@�N�g���B<br>
 * �ȉ��ɁA����̃L�[���ɈقȂ�{@link InterceptorChainList �C���^�[�Z�v�^�`�F�[�����X�g}������{@link InterceptorChain �C���^�[�Z�v�^�`�F�[��}���擾����C���^�[�Z�v�^�`�F�[���t�@�N�g���̃T�[�r�X��`��������B<br>
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
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setInterceptorChainListMapping(Map mapping){
        interceptorChainListMapping = mapping;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public Map getInterceptorChainListMapping(){
        return interceptorChainListMapping;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setInterceptorMapping(Map mapping){
        interceptorMapping = mapping;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public Map getInterceptorMapping(){
        return interceptorMapping;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setDefaultInterceptorChainListServiceName(ServiceName name){
        defaultInterceptorChainListServiceName = name;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public ServiceName getDefaultInterceptorChainListServiceName(){
        return defaultInterceptorChainListServiceName;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setInvokerMapping(Map mapping){
        invokerMapping = mapping;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public Map getInvokerMapping(){
        return invokerMapping;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setDefaultInvokerServiceName(ServiceName name){
        defaultInvokerServiceName = name;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public ServiceName getDefaultInvokerServiceName(){
        return defaultInvokerServiceName;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setRegexEnabled(boolean isEnable){
        isRegexEnabled = isEnable;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public boolean isRegexEnabled(){
        return isRegexEnabled;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setRegexMatchFlag(int flag){
        regexMatchFlag = flag;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public int getRegexMatchFlag(){
        return regexMatchFlag;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setInterceptorChainCacheMapServiceName(ServiceName name){
        interceptorChainCacheMapServiceName = name;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public ServiceName getInterceptorChainCacheMapServiceName(){
        return interceptorChainCacheMapServiceName;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setUseThreadLocalInterceptorChain(boolean isUse){
        isUseThreadLocalInterceptorChain = isUse;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public boolean isUseThreadLocalInterceptorChain(){
        return isUseThreadLocalInterceptorChain;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setGetMetrics(boolean isGet){
        isGetMetrics = isGet;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public boolean isGetMetrics(){
        return isGetMetrics;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setCalculateOnlyNormal(boolean isCalc){
        isCalculateOnlyNormal = isCalc;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public boolean isCalculateOnlyNormal(){
        return isCalculateOnlyNormal;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setOutputTimestamp(boolean isOutput){
        isOutputTimestamp = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public boolean isOutputTimestamp(){
        return isOutputTimestamp;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setOutputCount(boolean isOutput){
        isOutputCount = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public boolean isOutputCount(){
        return isOutputCount;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setOutputExceptionCount(boolean isOutput){
        isOutputExceptionCount = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public boolean isOutputExceptionCount(){
        return isOutputExceptionCount;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setOutputErrorCount(boolean isOutput){
        isOutputErrorCount = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public boolean isOutputErrorCount(){
        return isOutputErrorCount;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setOutputLastTime(boolean isOutput){
        isOutputLastTime = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public boolean isOutputLastTime(){
        return isOutputLastTime;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setOutputLastExceptionTime(boolean isOutput){
        isOutputLastExceptionTime = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public boolean isOutputLastExceptionTime(){
        return isOutputLastExceptionTime;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setOutputLastErrorTime(boolean isOutput){
        isOutputLastErrorTime = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public boolean isOutputLastErrorTime(){
        return isOutputLastErrorTime;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setOutputBestPerformance(boolean isOutput){
        isOutputBestPerformance = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public boolean isOutputBestPerformance(){
        return isOutputBestPerformance;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setOutputBestPerformanceTime(boolean isOutput){
        isOutputBestPerformanceTime = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public boolean isOutputBestPerformanceTime(){
        return isOutputBestPerformanceTime;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setOutputWorstPerformance(boolean isOutput){
        isOutputWorstPerformance = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public boolean isOutputWorstPerformance(){
        return isOutputWorstPerformance;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setOutputWorstPerformanceTime(boolean isOutput){
        isOutputWorstPerformanceTime = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public boolean isOutputWorstPerformanceTime(){
        return isOutputWorstPerformanceTime;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setOutputAveragePerformance(boolean isOutput){
        isOutputAveragePerformance = isOutput;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public boolean isOutputAveragePerformance(){
        return isOutputAveragePerformance;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public void setDateFormat(String format){
        dateFormat = format;
    }
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
    public String getDateFormat(){
        return dateFormat;
    }
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
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
    
    // DefaultInterceptorChainFactoryServiceMBean��JavaDoc
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
     * �L�[������ɍ��v����{@link InterceptorChain}���擾����B<p>
     * {@link #isUseThreadLocalInterceptorChain()}��true�̎��́A{@link DefaultThreadLocalInterceptorChain}��Ԃ��̂ŁA�X���b�h�P�ʂł̍ė��p���\�ł���B<br>
     * �܂��A{@link #setInterceptorChainCacheMapServiceName(ServiceName)}���w�肵�Ă���ꍇ�́A��������InterceptorChain���L���b�V�����ĕԂ��B<br>
     *
     * @param key �L�[������
     * @return �L�[������ɍ��v����InterceptorChain
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
