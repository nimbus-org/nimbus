package jp.ossc.nimbus.service.trade;

import jp.ossc.nimbus.beans.dataset.DataSet;

public interface TechnicalCalc {

    public DataSet calc(TimeSeries<TimeSeries.Element> series);
    
}
