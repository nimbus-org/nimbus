package jp.ossc.nimbus.service.writer.prometheus;

import java.util.Map;

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link GaugeWriterService}サービスMBeanインタフェース。<p>
 *
 * @author M.Ishida
 */
public interface GaugeWriterServiceMBean extends ServiceBaseMBean {
    
    /**
     * Prometeusに出力する際のNameを取得する。<p>
     * 
     * @return Prometeusに出力する際のName
     */
    public String getName();
    
    /**
     * Prometeusに出力する際のNameを設定する。<p>
     * 
     * @param name Prometeusに出力する際のName
     */
    public void setName(String name);
    
    /**
     * Prometeusに出力する際のHelpを取得する。<p>
     * 
     * @return Prometeusに出力する際のHelp
     */
    public String getHelp();
    
    /**
     * Prometeusに出力する際のHelpを設定する。<p>
     * 
     * @param help Prometeusに出力する際のHelp
     */
    public void setHelp(String help);
    
    /**
     * Prometeusに出力する際のLabelのKey名の配列を取得する。<p>
     * 
     * @return Key名の配列
     */
    public String[] getLabelNames();
    
    /**
     * Prometeusに出力する際のLabelのKey名の配列を設定する。<p>
     * 
     * @param names Key名の配列
     */
    public void setLabelNames(String[] names);
    
    /**
     * Prometeusに出力する際のLabelのValue値を{@link WritableRecord}から取得する際のプロパティ名の配列を取得する。<p>
     * 
     * @return プロパティ名の配列
     */
    public String[] getLabelPropertyNames();
    
    /**
     * Prometeusに出力する際のLabelのValue値を{@link WritableRecord}から取得する際のプロパティ名の配列を設定する。<p>
     * 
     * @param propertyNames プロパティ名の配列
     */
    public void setLabelPropertyNames(String[] propertyNames);
    
    /**
     * Prometeusに出力する際の固定で出力したいLabelのKey名とVallueのMapを取得する。<p>
     * 
     * @return LabelのKey名とVallueのMap
     */
    public Map getFixedLabelMap();

    /**
     * Prometeusに出力する際の固定で出力したいLabelのKey名とVallueのMapを設定する。<p>
     * 
     * @param labelMap LabelのKey名とVallueのMap
     */
    public void setFixedLabelMap(Map labelMap);
    
    /**
     * Prometeusに出力する際のValueの値を{@link WritableRecord}から取得する際のプロパティ名を取得する。<p>
     * 
     * @return プロパティ名
     */
    public String getValuePropertyName();
    
    /**
     * Prometeusに出力する際のValueの値を{@link WritableRecord}から取得する際のプロパティ名を設定する。<p>
     * 
     * @param propertyName プロパティ名
     */
    public void setValuePropertyName(String propertyName);
    
    /**
     * Prometeusに出力する際のValueの値を{@link WritableRecord}から取得した際にnullだった場合にValueとして設定する値を取得する。<p>
     * 
     * @return nullだった場合にValueとして設定する値
     */
    public Double getOutputValueOnNullValue();
    
    /**
     * Prometeusに出力する際のValueの値を{@link WritableRecord}から取得した際にnullだった場合にValueとして設定する値を設定する。<p>
     * 未設定の場合、値がnullだった場合にPrometeusへの出力は行わない。
     * 
     * @param outputValue nullだった場合にValueとして設定する値
     */
    public void setOutputValueOnNullValue(Double outputValue);
}
