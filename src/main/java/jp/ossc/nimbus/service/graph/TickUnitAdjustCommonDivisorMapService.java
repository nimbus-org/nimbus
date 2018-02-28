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
package jp.ossc.nimbus.service.graph;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

import jp.ossc.nimbus.core.ServiceBase;

public class TickUnitAdjustCommonDivisorMapService extends ServiceBase 
implements TickUnitAdjustCommonDivisorMapServiceMBean 
 , TickUnitAdjustCommonDivisorMap {
    
    private static final long serialVersionUID = 8309634307775145100L;
    
    private String[] commonDivisorMaps;
    private Map commonDivisorMap;
    
    public void setCommonDivisorMap(String[] map) {
        commonDivisorMaps = map;
    }
    public String[] getCommonDivisorMap() {
        return commonDivisorMaps;
    }
    
    public void startService() throws Exception{
        super.startService();
        
        if(commonDivisorMaps != null && commonDivisorMaps.length != 0){
            commonDivisorMap = new LinkedHashMap();
            for(int i = 0; i < commonDivisorMaps.length; i++){
                final int index = commonDivisorMaps[i].lastIndexOf('=');
                if(index == -1
                     || index == commonDivisorMaps[i].length() - 1){
                    throw new IllegalArgumentException(
                        commonDivisorMaps[i]
                    );
                }
                final Condition cond = new Condition(
                        commonDivisorMaps[i].substring(0, index)
                );
                double val = Double.NaN;
                try{
                    val = Double.parseDouble(
                        commonDivisorMaps[i].substring(index + 1).trim()
                    );
                }catch(NumberFormatException e){
                    throw new IllegalArgumentException(
                        commonDivisorMaps[i]
                    );
                }
                commonDivisorMap.put(cond, new Double(val));
            }
        }
    }
    
    public double getCommonDivisor(double val){
        if(commonDivisorMap == null){
            return Double.NaN;
        }
        final Iterator conds = commonDivisorMap.keySet().iterator();
        while(conds.hasNext()){
            final Condition cond = (Condition)conds.next();
            try{
                if(cond.evaluate(new Double(val))){
                    final Double ret = (Double)commonDivisorMap.get(cond);
                    if(ret == null){
                        return Double.NaN;
                    }
                    return ret.doubleValue();
                }
            }catch(Exception e){
                // 起こりえない
                e.printStackTrace();
                continue;
            }
        }
        return Double.NaN;
    }
    
    private static class Condition implements java.io.Serializable{
        public Expression expression;
        
        private static final long serialVersionUID = 5344270184763844609L;
        
        Condition(String cond) throws Exception{
            
            expression = ExpressionFactory.createExpression(cond);
            evaluate(new Double(0));
        }
        
        public boolean evaluate(Double val) throws Exception{
            JexlContext jexlContext = JexlHelper.createContext();
            jexlContext.getVars().put(VALUE, val);
            
            Object exp = expression.evaluate(jexlContext);
            if(exp instanceof Boolean){
                return ((Boolean)exp).booleanValue();
            }else{
                throw new IllegalArgumentException(
                    expression.getExpression()
                );
            }
        }
    } 
}
