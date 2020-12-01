package jp.ossc.nimbus.service.writer.prometheus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.prometheus.client.Gauge;
import io.prometheus.client.Gauge.Builder;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.service.writer.MessageWriteException;
import jp.ossc.nimbus.service.writer.MessageWriter;
import jp.ossc.nimbus.service.writer.WritableElement;
import jp.ossc.nimbus.service.writer.WritableRecord;

/**
 * PrometeusにGaugeを出力する{@link MessageWriter}サービス。<p>
 *
 * @author M.Ishida
 */
public class GaugeWriterService extends ServiceBase implements MessageWriter, GaugeWriterServiceMBean {
    
    private static final long serialVersionUID = -6451110832122782802L;
    
    protected String name;
    protected String help;
    protected List labelPropertyList;
    protected Map fixedLabelMap;
    protected List valuePropertyList;
    protected Double outputValueOnNullValue;
    
    protected Map gaugeMap;
    protected boolean isOutputLabel = false;
    protected Collection fixedLabelValues;
    
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
    
    public void createService() throws Exception {
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
            gaugeMap.put(key, builder.register());
        }
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
    }
    
}
