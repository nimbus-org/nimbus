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
package jp.ossc.nimbus.service.rush;

import java.util.*;

import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordList;

/**
 * 重み付き乱数リクエストパラメータセレクタ。<p>
 * パラメータリストから、乱数発生で、パラメータマップを選択する。正し、パラメータリストの各行の最後に重みパラメータを付与することで、重みが大きなパラメータ程、出現頻度が高くなる。<br/>
 *
 * @author M.Takata
 */
public class WeightRandomRequestParameterSelector
 implements RequestParameterSelector{
    
    private Map randoms = Collections.synchronizedMap(new HashMap());
    private Map weightListTemplateMap = Collections.synchronizedMap(new HashMap());
    private Map weightListMap = Collections.synchronizedMap(new HashMap());
    
    public Map getParameter(int id, int roopCount, int count, RecordList parameterList){
        if(parameterList == null){
            return null;
        }
        List weightListTemplate = (List)weightListTemplateMap.get(parameterList);
        if(weightListTemplate == null){
            synchronized(weightListTemplateMap){
                if(!weightListTemplateMap.containsKey(parameterList)){
                    weightListTemplate = new ArrayList();
                    for(int i = 0, imax = parameterList.size(); i < imax; i++){
                        final Record params = (Record)parameterList.getRecord(i);
                        final String countStr = params.getStringProperty(params.size() - 1);
                        int tmpCount = 1;
                        try{
                            tmpCount = Integer.parseInt(countStr);
                        }catch(NumberFormatException e){}
                        for(int j = 0; j < tmpCount; j++){
                            weightListTemplate.add(params);
                        }
                    }
                    weightListTemplateMap.put(parameterList, weightListTemplate);
                }else{
                    weightListTemplate = (List)weightListTemplateMap.get(parameterList);
                }
            }
        }
        List weightList = (List)weightListMap.get(parameterList);
        synchronized(weightListTemplate){
            if(weightList == null){
                weightList = new ArrayList();
                weightListMap.put(parameterList, weightList);
            }
            final Integer idVal = new Integer(id);
            if(weightList.size() == 0 && weightListTemplate.size() != 0){
                weightList.addAll(weightListTemplate);
                final Random random = new Random(System.currentTimeMillis() + id);
                randoms.put(idVal, random);
            }
            Random random = (Random)randoms.get(idVal);
            if(random == null){
                random = new Random(System.currentTimeMillis() + id);
                randoms.put(idVal, random);
            }
            final int index = random.nextInt(weightList.size());
            final Record params = (Record)weightList.remove(index);
            return new HashMap(params);
        }
    }
}
