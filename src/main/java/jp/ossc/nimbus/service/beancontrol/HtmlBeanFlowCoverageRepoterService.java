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
import jp.ossc.nimbus.util.converter.PaddingConverter;
import jp.ossc.nimbus.util.converter.StringConverter;
import jp.ossc.nimbus.util.converter.PaddingStringConverter;

/**
 * {@link BeanFlowCoverageRepoter}インタフェースのデフォルト実装サービス。<p>
 *
 * @author M.Aono
 */
public class HtmlBeanFlowCoverageRepoterService extends ServiceBase
 implements BeanFlowCoverageRepoter, HtmlBeanFlowCoverageRepoterServiceMBean{
    
    private static final long serialVersionUID = -4272153017484182793L;
    private ServiceName beanFlowInvokerFactoryServiceName;
    private BeanFlowInvokerFactory beanFlowInvokerFactory;
    
    private boolean isReportOnStop;
    private File outputPath;
    private String characterEncoding = "UTF-8";
    
    private final int graphWidth = 620;
    
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
    
    public void setOutputPath(File outputPath){
        this.outputPath = outputPath;
    }
    public File getOutputPath(){
        return outputPath;
    }
    
    public void setCharacterEncoding(String encoding){
        characterEncoding = encoding;
    }
    public String getCharacterEncoding(){
        return characterEncoding;
    }
    
    public void startService() throws Exception{
        if(beanFlowInvokerFactoryServiceName != null){
            beanFlowInvokerFactory = (BeanFlowInvokerFactory)ServiceManagerFactory.getServiceObject(beanFlowInvokerFactoryServiceName);
        }
        if(beanFlowInvokerFactory == null){
            throw new IllegalArgumentException("BeanFlowInvokerFactory is null");
        }
        
        if(outputPath == null || "".equals(outputPath)){
            throw new IllegalArgumentException("OutputPath is null");
        }
        
        if(!outputPath.exists()){
            if(!outputPath.mkdirs()){
                throw new IllegalArgumentException("Output dir can not make. path=" + outputPath);
            }
        }

    }
    
    
    public void stopService() throws Exception{
        if(isReportOnStop){
            report();
        }
    }
    
    public void report() throws Exception{
        Map resourceMap = getResourceData();
        StringConverter converter = new PaddingStringConverter(Integer.toString(resourceMap.size()).length(), '0', PaddingConverter.DIRECTION_RIGHT);
        int count = 0;
        final Iterator resources = resourceMap.entrySet().iterator();
        while(resources.hasNext()){
            Map.Entry flowFileEntry = (Map.Entry)resources.next();
            String flowFileName = (String)flowFileEntry.getKey();
            Map coverageMap = (Map) flowFileEntry.getValue();
            final Iterator coverages = coverageMap.entrySet().iterator();
            while(coverages.hasNext()){
                Map.Entry coverage = (Map.Entry)coverages.next();
                String flowName = (String)coverage.getKey();
                File flowsDir = new File(outputPath , "flows");
                if(!flowsDir.exists()){
                    flowsDir.mkdirs();
                }
                PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(flowsDir,  converter.convert(++count) + ".html")), characterEncoding)));
                try{
                    reportCoverageTable(pw, (BeanFlowCoverage)coverage.getValue());
                }finally{
                    pw.flush();
                    pw.close();
                }
            }
        }
        copyResource("coverage_ok.png", new File(outputPath + "/" + "coverage_ok.png"));
        copyResource("coverage_ng.png", new File(outputPath + "/" + "coverage_ng.png"));
        PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputPath , "index.html")), characterEncoding)));
        try{
            reportFlowTable(pw, resourceMap, converter);
        }finally{
            pw.flush();
            pw.close();
        }
    }
    
    private void copyResource(String resourcePath, File output) throws IOException{
        InputStream is = this.getClass().getResourceAsStream(resourcePath);
        OutputStream os = null;
        try{
            os = new BufferedOutputStream(new FileOutputStream(output));
            byte[] bytes = new byte[1024];
            int len = 0;
            while((len = is.read(bytes, 0, bytes.length)) > 0){
                os.write(bytes, 0, len);
            }
            os.flush();
        }finally{
            if(is != null){
                is.close();
            }
            if(os != null){
                os.close();
            }
        }
    }
    
    
    private void reportFlowTable(PrintWriter pw, Map resourceMap, StringConverter converter) throws IOException{
        
        pw.println("<html>");
        pw.println("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + characterEncoding + "\"/><title>Nimbus BeanFlow Coverage</title></head>");
        pw.println("<body>");
        pw.println("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\" width=\"70%\">");
        pw.println("<tr bgcolor=\"#cccccc\"><th>flowFileName</th><th>flowName</th><th>CoveredCount</th><th>ElementCount</th><th>CoverageRate</th></tr>");
        
        int count = 0;
        final Iterator resources = resourceMap.entrySet().iterator();
        while(resources.hasNext()){
            Map.Entry flowFileEntry = (Map.Entry)resources.next();
            String flowFileName = (String)flowFileEntry.getKey();
            Map coverageMap = (Map) flowFileEntry.getValue();
            pw.println("<tr><td rowspan=\"" + (coverageMap.size() + 1)  + "\"><a target=\"sourcefile\" href=\"" + new File(flowFileName).toURI().toURL() + "\">" + flowFileName + "</a></td></tr>");
            final Iterator coverages = coverageMap.entrySet().iterator();
            while(coverages.hasNext()){
                Map.Entry coverage = (Map.Entry)coverages.next();
                String flowName = (String)coverage.getKey();
                BeanFlowCoverage beanflowCoverage = (BeanFlowCoverage)coverage.getValue();
                pw.println("<tr><td>" + "<a target=\"flowfile\" href=\"" + new File(outputPath + "/flows/" + converter.convert(++count) + ".html").toURI().toURL() + "\">" + flowName + "</a></td>");
                pw.println("<td>" + beanflowCoverage.getCoveredElementCount() + "</td>");
                pw.println("<td>" + beanflowCoverage.getElementCount() + "</td>");
                double coverageRate = (double)beanflowCoverage.getCoveredElementCount() / (double)beanflowCoverage.getElementCount() * 100.0d;
                int width = (int)(graphWidth * (coverageRate / 100d));
                int lackWidth = graphWidth - width;
                pw.print("<td style=\"border:solid 1px #999999;\">");
                if(coverageRate < 100.0d){
                    pw.print("&nbsp;" + (int)coverageRate);
                }else{
                    pw.print((int)coverageRate);
                }
                pw.print("%&nbsp;<img src=\"" + "coverage_ok.png" + "\" width=\"" + width + "\" height=\"18\" alt=\"CoverageRate\">");
                if(lackWidth > 0){
                    pw.print("<img src=\"" + "coverage_ng.png" + "\" width=\"" + lackWidth + "\" height=\"18\" alt=\"CoverageRate\">");
                }
                pw.println("</td>");
                pw.println("</tr>");
            }
        }
        pw.println("</body>");
        pw.println("</html>");
        pw.flush();
    }
    
    private void reportCoverageTable(PrintWriter pw, BeanFlowCoverage beanflowCoverage) throws IOException{
        
        pw.println("<html>");
        pw.println("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + characterEncoding + "\"/><title>Nimbus BeanFlow Coverage</title></head>");
        pw.println("<body>");
        String[] beanFlowCoverage = beanflowCoverage.toString().split(System.getProperty("line.separator"));
        
        boolean isCoverd = true;
        String colorCode = "#98fb98;";
        for(int index = 0; index < beanFlowCoverage.length; index++){
            String coverage = beanFlowCoverage[index].replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;")
                    .replaceAll(" ", "&nbsp;");
            if((coverage != null && coverage.indexOf("!") == 0)){
                coverage = coverage.replaceAll("!", "");
                isCoverd = false;
            }else{
                isCoverd = true;
            }
            if(isCoverd){
                colorCode = "#98fb98;";
            }else{
                colorCode = "#ff4500;";
            }
            pw.print("<span style=\"background-color:" + colorCode  + "\">" + coverage + "<br></span>");
        }
        pw.println("</body>");
        pw.println("</html>");
    }
    
    private Map getResourceData(){
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
         
         return resourceMap;
    }
}
