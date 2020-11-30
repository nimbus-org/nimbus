package jp.ossc.nimbus.service.writer.prometheus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    protected String[] labelNames;
    protected String[] labelPropertyNames;
    protected String valuePropertyName;
    protected Double outputValueOnNullValue;
    
    protected Gauge gauge;
    
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
        Builder builder = Gauge.build().name(name);
        if(help != null && !"".equals(help)){
            builder = builder.help(help);
        }
        if(labelNames != null && labelNames.length > 0){
            builder = builder.labelNames(labelNames);
        }
        gauge = builder.register();
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
            if(labelPropertyNames == null || labelPropertyNames.length == 0){
                gauge.set(dValue);
            }else{
                List labelList = new ArrayList();
                for(int i = 0; i < labelPropertyNames.length; i++){
                    WritableElement element = (WritableElement) elementMap.get(labelPropertyNames[i]);
                    if(element != null){
                        labelList.add(element.toString());
                    }else{
                        labelList.add("");
                    }
                }
                gauge.labels((String[]) labelList.toArray(new String[] {})).set(dValue);
            }
        }
    }
    
}
