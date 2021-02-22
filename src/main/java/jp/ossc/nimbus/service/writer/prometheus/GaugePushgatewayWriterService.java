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
package jp.ossc.nimbus.service.writer.prometheus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.Gauge.Builder;
import io.prometheus.client.exporter.PushGateway;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.service.writer.MessageWriteException;
import jp.ossc.nimbus.service.writer.MessageWriter;
import jp.ossc.nimbus.service.writer.WritableElement;
import jp.ossc.nimbus.service.writer.WritableRecord;

/**
 * PrometeusのPushgatewayにGaugeを出力する{@link MessageWriter}サービス。<p>
 *
 * @author M.Ishida
 */
public class GaugePushgatewayWriterService extends ServiceBase implements MessageWriter, GaugePushgatewayWriterServiceMBean {
    
    private static final long serialVersionUID = -6451110832122782802L;
    
    protected String name;
    protected String help;
    protected List labelPropertyList;
    protected Map fixedLabelMap;
    protected List valuePropertyList;
    protected Double outputValueOnNullValue;
    
    protected String pushGatewayHostName;
    protected int pushGatewayPort;
    protected String pushGatewayJobName;
    protected String pushGatewayInstance;
    
    protected Map gaugeMap;
    protected boolean isOutputLabel = false;
    protected Collection fixedLabelValues;
    protected CollectorRegistry registry;
    protected PushGateway pushGateway;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getHelp() {
        return help;
    }
    
    public void setHelp(String help) {
        this.help = help;
    }
    
    public List getLabelPropertyList() {
        return labelPropertyList;
    }
    
    public void setLabelPropertyList(List list) {
        labelPropertyList = list;
    }
    
    public Map getFixedLabelMap() {
        return fixedLabelMap;
    }

    public void setFixedLabelMap(Map map) {
        fixedLabelMap = map;
    }
    
    public List getValuePropertyList() {
        return valuePropertyList;
    }
    
    public void setValuePropertyList(List list) {
        valuePropertyList = list;
    }
    
    public Double getOutputValueOnNullValue() {
        return outputValueOnNullValue;
    }
    
    public void setOutputValueOnNullValue(Double outputValue) {
        outputValueOnNullValue = outputValue;
    }
    
    public String getPushGatewayHostName() {
        return pushGatewayHostName;
    }

    public void setPushGatewayHostName(String hostName) {
        pushGatewayHostName = hostName;
    }

    public int getPushGatewayPort() {
        return pushGatewayPort;
    }

    public void setPushGatewayPort(int port) {
        pushGatewayPort = port;
    }

    public String getPushGatewayJobName() {
        return pushGatewayJobName;
    }

    public void setPushGatewayJobName(String jobName) {
        pushGatewayJobName = jobName;
    }

    public String getPushGatewayInstance() {
        return pushGatewayInstance;
    }

    public void setPushGatewayInstance(String instance) {
        pushGatewayInstance = instance;
    }

    public void createService() throws Exception {
        registry = new CollectorRegistry();
        gaugeMap = new LinkedHashMap();
        labelPropertyList = new ArrayList();
        fixedLabelMap = new LinkedHashMap();
        valuePropertyList = new ArrayList();
    }
    
    public void startService() throws Exception {
        if(name == null || "".equals(name)){
            throw new IllegalArgumentException("Name is null or empty.");
        }
        if(valuePropertyList == null || valuePropertyList.isEmpty()){
            throw new IllegalArgumentException("ValuePropertyList is null or empty.");
        }
        if(pushGatewayHostName == null || "".equals(pushGatewayHostName)){
            throw new IllegalArgumentException("PushGatewayHostName is null or empty.");
        }
        if(pushGatewayPort <= 0) {
            throw new IllegalArgumentException("PushGatewayPort is illegal value. PushGatewayPort=" + pushGatewayPort);
        }
        if(pushGatewayJobName == null || "".equals(pushGatewayJobName)){
            pushGatewayJobName = getServiceNameObject().toString();
        }
        
        int labelNamesSize = (labelPropertyList == null ? 0 : labelPropertyList.size()) + (fixedLabelMap == null ? 0 : fixedLabelMap.size());
        List labelNameList = null;
        if(labelNamesSize > 0){
            isOutputLabel = true;
            labelNameList = new ArrayList();
            if(labelPropertyList != null && labelPropertyList.size() > 0){
                labelNameList.addAll(labelPropertyList);
            }
            if(fixedLabelMap != null && fixedLabelMap.size() > 0){
                labelNameList.addAll(fixedLabelMap.keySet());
                fixedLabelValues = fixedLabelMap.values();
            }
        }
        for(int i = 0; i < valuePropertyList.size(); i++){
            String key = (String) valuePropertyList.get(i);
            Builder builder = Gauge.build().name(name + "_" + key.toLowerCase());
            if(help != null && !"".equals(help)){
                builder = builder.help(help + "(" + key + ")");
            }
            if(isOutputLabel){
                builder = builder.labelNames((String[]) labelNameList.toArray(new String[0]));
            }
            gaugeMap.put(key, builder.register(registry));
        }
        pushGateway = new PushGateway(pushGatewayHostName + ":" + pushGatewayPort);
    }
    
    public void write(WritableRecord rec) throws MessageWriteException {
        Map elementMap = rec.getElementMap();
        List labelValueList = null;
        if(isOutputLabel){
            labelValueList = new ArrayList();
            if(labelPropertyList != null){
                for(int i = 0; i < labelPropertyList.size(); i++){
                    String key = (String) labelPropertyList.get(i);
                    WritableElement element = (WritableElement) elementMap.get(key);
                    if(element != null){
                        labelValueList.add(element.toString());
                    }else{
                        labelValueList.add("");
                    }
                }
            }
            if(fixedLabelValues != null){
                labelValueList.addAll(fixedLabelValues);
            }
        }
        String[] labels = (String[]) labelValueList.toArray(new String[] {});
        Iterator itr = gaugeMap.entrySet().iterator();
        while (itr.hasNext()){
            Entry entry = (Entry) itr.next();
            String key = (String) entry.getKey();
            WritableElement valueElement = (WritableElement) elementMap.get(key);
            Object value = valueElement == null ? null : valueElement.getValue();
            Double dValue = null;
            if(value != null){
                if(value instanceof Number){
                    dValue = ((Number) value).doubleValue();
                }else if(value instanceof Boolean){
                    dValue = ((Boolean) value).booleanValue() ? 1d : 0d;
                }else if(value instanceof Date){
                    dValue = (double) (((Date) value).getTime());
                }else{
                    try{
                        dValue = Double.parseDouble(value.toString());
                    }catch (NumberFormatException e){
                        throw new MessageWriteException("Could not parse daouble. value=" + value, e);
                    }
                }
            }else{
                if(outputValueOnNullValue != null){
                    dValue = outputValueOnNullValue;
                }
            }
            if(dValue != null){
                Gauge gauge = (Gauge) entry.getValue();
                if(isOutputLabel){
                    gauge.labels(labels).set(dValue);
                }else{
                    gauge.set(dValue);
                }
            }
        }
        try {
            if(pushGatewayInstance == null || "".equals(pushGatewayInstance)) {
                pushGateway.pushAdd(registry, pushGatewayJobName);
            } else {
                Map map = new HashMap();
                map.put("instance", pushGatewayInstance);
                pushGateway.pushAdd(registry, pushGatewayJobName, map);
            }
        } catch(IOException e) {
            throw new MessageWriteException(e);
        }
    }
    
}
