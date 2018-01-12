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
package jp.ossc.nimbus.service.beancontrol;

import java.util.*;
import java.io.*;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory;

/**
 * {@link BeanFlowCoverageRepoter}インタフェースのデフォルト実装サービス。<p>
 *
 * @author M.Takata
 */
public class ConsoleBeanFlowCoverageRepoterService extends ServiceBase
 implements BeanFlowCoverageRepoter, ConsoleBeanFlowCoverageRepoterServiceMBean{
    
    private static final long serialVersionUID = -7822567483640889307L;
    
    private ServiceName beanFlowInvokerFactoryServiceName;
    private BeanFlowInvokerFactory beanFlowInvokerFactory;
    
    private boolean isReportOnStop;
    private boolean isDetail;
    
    public ServiceName getBeanFlowInvokerFactoryServiceName(){
        return beanFlowInvokerFactoryServiceName;
    }
    public void setBeanFlowInvokerFactoryServiceName(ServiceName name){
        beanFlowInvokerFactoryServiceName = name;
    }
    
    public void setReportOnStop(boolean isReport){
        isReportOnStop = isReport;
    }
    public boolean isReportOnStop(){
        return isReportOnStop;
    }
    
    public void setDetail(boolean isDetail){
        this.isDetail = isDetail;
    }
    public boolean isDetail(){
        return isDetail;
    }
    
    public String displayReport(){
        StringWriter sw = new StringWriter();
        try{
            report(new PrintWriter(sw));
        }catch(IOException e){}
        return sw.toString();
    }
    
    public void startService() throws Exception{
        if(beanFlowInvokerFactoryServiceName != null){
            beanFlowInvokerFactory = (BeanFlowInvokerFactory)ServiceManagerFactory.getServiceObject(beanFlowInvokerFactoryServiceName);
        }
        if(beanFlowInvokerFactory == null){
            throw new IllegalArgumentException("BeanFlowInvokerFactory is null");
        }
    }
    
    public void stopService() throws Exception{
        if(isReportOnStop){
            report();
        }
    }
    
    public void report() throws Exception{
        report(new PrintWriter(System.out));
    }
    private void report(PrintWriter pw) throws IOException{
        final Map resourceMap = new TreeMap();
        final Iterator flowNames = beanFlowInvokerFactory.getBeanFlowKeySet().iterator();
        while(flowNames.hasNext()){
            BeanFlowInvoker invoker = beanFlowInvokerFactory.createFlow((String)flowNames.next());
            Map coverageMap = (Map)resourceMap.get(invoker.getResourcePath());
            if(coverageMap == null){
                coverageMap = new TreeMap();
                resourceMap.put(invoker.getResourcePath(), coverageMap);
            }
            coverageMap.put(invoker.getFlowName(), invoker.getBeanFlowCoverage());
            invoker.end();
        }
        final Iterator resources = resourceMap.entrySet().iterator();
        while(resources.hasNext()){
            Map.Entry resource = (Map.Entry)resources.next();
            pw.println("///////////////////////////////////////////////////////");
            pw.println("fileName:" + resource.getKey());
            Map coverageMap = (Map)resource.getValue();
            final Iterator coverages = coverageMap.entrySet().iterator();
            while(coverages.hasNext()){
                Map.Entry flowCoverage = (Map.Entry)coverages.next();
                pw.println("#######################################################");
                pw.println("flowName:" + flowCoverage.getKey());
                BeanFlowCoverage coverage = (BeanFlowCoverage)flowCoverage.getValue();
                pw.println("coverage:" + coverage.getCoveredElementCount() + "/" + coverage.getElementCount() + "(" + (int)((double)coverage.getCoveredElementCount() / (double)coverage.getElementCount() * 100.0d) + "%)");
                if(isDetail){
                    pw.println(coverage);
                }
            }
        }
        pw.flush();
    }
}
