package jp.ossc.nimbus.service.writer.prometheus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    protected String[] labelNames;
    protected String[] labelPropertyNames;
    protected Map fixedLabelMap;
    protected String valuePropertyName;
    protected Double outputValueOnNullValue;
    
    protected String pushGatewayHostName;
    protected int pushGatewayPort;
    protected String pushGatewayJobName;
    protected String pushGatewayInstance;
    
    protected Gauge gauge;
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
    
    public String[] getLabelNames() {
        return labelNames;
    }
    
    public void setLabelNames(String[] names) {
        labelNames = names;
    }
    
    public String[] getLabelPropertyNames() {
        return labelPropertyNames;
    }
    
    public void setLabelPropertyNames(String[] propertyNames) {
        labelPropertyNames = propertyNames;
    }
    
    public Map getFixedLabelMap() {
        return fixedLabelMap;
    }

    public void setFixedLabelMap(Map labelMap) {
        fixedLabelMap = labelMap;
    }
    
    public String getValuePropertyName() {
        return valuePropertyName;
    }
    
    public void setValuePropertyName(String propertyName) {
        valuePropertyName = propertyName;
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
        fixedLabelMap = new LinkedHashMap();
    }
    
    public void startService() throws Exception {
        if(name == null || "".equals(name)){
            throw new IllegalArgumentException("Name is null or empty.");
        }
        if(valuePropertyName == null || "".equals(valuePropertyName)){
            throw new IllegalArgumentException("ValuePropertyName is null or empty.");
        }
        if(!(labelNames == null && labelPropertyNames == null) && (labelNames.length != labelPropertyNames.length)){
            throw new IllegalArgumentException("LabelNames or LabelPropertyNames is illegal value.");
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
        
        Builder builder = Gauge.build().name(name);
        if(help != null && !"".equals(help)){
            builder = builder.help(help);
        }
        int labelNamesSize = (labelNames == null ? 0 : labelNames.length) + (fixedLabelMap == null ? 0 : fixedLabelMap.size());
        if(labelNamesSize > 0) {
            isOutputLabel = true;
            String[] tmpLabelNames = new String[labelNamesSize];
            if(labelNames != null && labelNames.length > 0){
                System.arraycopy(labelNames, 0, tmpLabelNames, 0, labelNames.length);
            }
            if(fixedLabelMap != null && fixedLabelMap.size() > 0) {
                String[] fixedLabels = (String[])fixedLabelMap.keySet().toArray(new String[0]);
                System.arraycopy(fixedLabels, 0, tmpLabelNames, labelNamesSize - fixedLabels.length, fixedLabels.length);
                fixedLabelValues = fixedLabelMap.values();
            }
            builder = builder.labelNames(tmpLabelNames);
        }
        gauge = builder.register(registry);
        pushGateway = new PushGateway(pushGatewayHostName + ":" + pushGatewayPort);
    }
    
    public void write(WritableRecord rec) throws MessageWriteException {
        Map elementMap = rec.getElementMap();
        WritableElement valueElement = (WritableElement) elementMap.get(valuePropertyName);
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
                try {
                    dValue = Double.parseDouble(value.toString());
                } catch(NumberFormatException e) {
                    throw new MessageWriteException("Could not parse daouble. value=" + value, e);
                }
            }
        }else{
            if(outputValueOnNullValue != null){
                dValue = outputValueOnNullValue;
            }
        }
        if(dValue != null){
            if(isOutputLabel){
                List labelValueList = new ArrayList();
                if(labelPropertyNames != null) {
                    for(int i = 0; i < labelPropertyNames.length; i++){
                        WritableElement element = (WritableElement) elementMap.get(labelPropertyNames[i]);
                        if(element != null){
                            labelValueList.add(element.toString());
                        }else{
                            labelValueList.add("");
                        }
                    }
                }
                if(fixedLabelValues != null) {
                    labelValueList.addAll(fixedLabelValues);
                }
                gauge.labels((String[]) labelValueList.toArray(new String[] {})).set(dValue);
            }else{
                gauge.set(dValue);
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
    
}
