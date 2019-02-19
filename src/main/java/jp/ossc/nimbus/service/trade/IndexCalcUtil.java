package jp.ossc.nimbus.service.trade;

import java.util.ArrayList;
import java.util.List;


public class IndexCalcUtil {
    
	public static class LongWrapper {
    	public long price;
    	public LongWrapper(long price){
    		this.price = price;
    	}
    }
	
	public static class DoubleWrapper {
    	public double price;
    	public DoubleWrapper(double price){
    		this.price = price;
    	}
    }
    
    public static class PeriodicHighLowPrice{
    	private final boolean highLowType;
    	private final int period;
    	private final List<DoubleWrapper> priceList;
    	private int currentIndex = -1;
    	private double currentPrice;
    	
    	public PeriodicHighLowPrice(boolean highLowType, int period){
    		this.highLowType = highLowType;
    		this.period = period;
    		priceList = new ArrayList<DoubleWrapper>(period);
    	}
    	
    	public double getCurrentPrice(){
    		return currentPrice;
    	}
    	
    	public double add(double price){
    		if(priceList.size() < period){
    			if(currentIndex == -1){
    				currentIndex = 0;
    				currentPrice = price;
    			}else if(highLowType ? currentPrice < price : currentPrice > price){
					currentIndex = priceList.size();
					currentPrice = price;
    			}
    			priceList.add(new DoubleWrapper(price));
    		}else{
    			DoubleWrapper old = priceList.remove(0);
    			old.price = price;
    			priceList.add(old);
    			currentIndex--;
    			if(currentIndex < 0){
    				currentPrice = 0L;
    				for(int i = 0, imax = priceList.size(); i < imax; i++){
    					DoubleWrapper pw = priceList.get(i);
    					if(currentPrice==0L){
    						currentPrice = pw.price;
    						continue;
    					}
        				if(highLowType ? currentPrice < pw.price : currentPrice > pw.price){
        					currentPrice = pw.price;
        					currentIndex = i;
        				}
    				}
    			}else{
    				if(highLowType ? currentPrice < price : currentPrice > price){
    					currentIndex = priceList.size() - 1;
    					currentPrice = price;
    				}
    			}
    		}
    		return currentPrice;
    	}
    }
    
    public static class PeriodicPrice{
    	private final List<DoubleWrapper> priceList;
    	private final List<DoubleWrapper> averageList;
    	private final int period;
    	private double sum;
    	private double sumAverage;
    	public PeriodicPrice(int period){
    		this.period = period;
    		priceList = new ArrayList<DoubleWrapper>(period);
    		averageList = new ArrayList<DoubleWrapper>(period);
    	}
    	public double addTotal(double price){
    		if(priceList.size() < period){
    			sum += price;
    			priceList.add(new DoubleWrapper(price));
    			return Double.NaN;
    		}else{
    			DoubleWrapper old = priceList.remove(0);
    			sum -= old.price;
    			old.price = price;
    			priceList.add(old);
    			sum += price;
    		}
    		return sum;
    	}
    	
    	public int averageSize(){
    		return averageList.size();
    	}
    	
    	public double addAverage(double price){
    		if(priceList.size() < period - 1){
    			sum += price;
    			priceList.add(new DoubleWrapper(price));
    			return Double.NaN;
    		}else{
    			DoubleWrapper old = null;
    			if(priceList.size() > period - 1){
        			old = priceList.remove(0);
        			sum -= old.price;
    			}
    		    if(old == null){
    		    	old = new DoubleWrapper(price);
    		    }
    			old.price = price;
    			priceList.add(old);
    			sum += price;
    		}
    		return (double)sum / (double)period;
    	}
    	
    	public double addAverage(double price, long startIndex){
    		if(averageList.size()  < period + startIndex - 1){
    			sumAverage += price;
    			averageList.add(new DoubleWrapper(price));
    			return Double.NaN;
    		}else{
    			DoubleWrapper old = null;
    			if(averageList.size() > period - 1){
        			old = averageList.remove(0);
        			sumAverage -= old.price;

    			}
    			old = new DoubleWrapper(price);

    			old.price = price;
    			averageList.add(old);
    			sumAverage += price;
    		}
    		return (double)sumAverage / (double)period;
    	}
    	
    	public double addAverage2(double price){
    		if(averageList.size() < period - 1){
    			sumAverage += price;
    			averageList.add(new DoubleWrapper(price));
    			return Double.NaN;
    		}else{
    			DoubleWrapper old = null;
    			if(averageList.size() > period - 1){
        			old = averageList.remove(0);
        			sumAverage -= old.price;

    			}
    			old = new DoubleWrapper(price);
    			old.price = price;
    			averageList.add(old);
    			sumAverage += price;
    		}
    		return (double)sumAverage / (double)period;
    	}
    	
    	public double[] addDevitation(double price, int devitation, int index, OHLCVTimeSeries element){
    		double[] devitations = new double[2];
    		double average = addAverage(price);
    		if(Double.isNaN(average)){
    			devitations[0] = average;
    			devitations[1] = average;
    			return devitations;
    		}
    		double variance = 0d;
    		for(int i = index - (period - 1); i <= index; i++){
        		variance  += (((OHLCVTimeSeries.OHLCVElement)element.get(i)).getCloseValue() - average)*
        				(((OHLCVTimeSeries.OHLCVElement)element.get(i)).getCloseValue() - average);
    		}   
    		
    		double avg = variance / (double)period;
    		
        	devitations[0] =  average + Math.sqrt(avg) * devitation;
        	devitations[1] = average - Math.sqrt(avg) * devitation;
        	
        	return devitations;

    		}
    	}
}
