package jp.ossc.nimbus.service.trade;

import java.util.List;
import java.util.Map;

public interface IndexCalc {


    /**
     * 引数に設定されている各keyに対する時系列データを計算する。
     * @param param
     * @return
     */
    public Map<String, List<?>> calc(Map<String, Object> param, TimeSeries<TimeSeries.Element> elements);
}
