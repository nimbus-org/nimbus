package jp.ossc.nimbus.service.trade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ossc.nimbus.service.trade.TradeSignCalcUtil.PeriodicPrice;

public class MovingAvgCalc implements IndexCalc{

    @Override
    public Map<String, List<?>> calc(Map<String, Object> paramMap, TimeSeries<TimeSeries.Element> elements) {
    
         Map<String, List<?>> calcMap = new HashMap<String, List<?>>();
         Set<String> keySet = paramMap.keySet();
         Iterator<String> ite = keySet.iterator();
         List<Double> dataList = new ArrayList<Double>();
         while(ite.hasNext()) {
             String key = ite.next();
             int param = (Integer)paramMap.get(key);
             for(int i = 0; i < elements.size(); i++){
                 OHLCVTimeSeries.OHLCVElement element = (OHLCVTimeSeries.OHLCVElement)elements.get(i);
                 PeriodicPrice periodicPrice = new PeriodicPrice(param);
                 double average = periodicPrice.addAverage(element.getCloseValue());
                 dataList.add(average);
             }
             calcMap.put(key, dataList);
         }

        return calcMap;
    }

}
