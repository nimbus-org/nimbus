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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * プロット条件。<p>
 *
 * @author k2-taniguchi
 */
public class PlotConditionImpl implements PlotCondition, java.io.Serializable {
    
    private static final long serialVersionUID = -5120322206925145655L;
    
    /** プロット名 */
    private String name;
    /** キーにデータセット名、値にデータセット条件リスト */
    private Map dsConditionMap = null;
    /** データセット条件リスト */
    private List dsConditionList = null;
    /** 設定順のデータセット名セット */
    private LinkedHashSet dsNameOrderSet = null;
    /** 有効なデータセット名リスト */
    private Set enableDsNameSet = null;

    /**
     * コンストラクタ。<p>
     */
    public PlotConditionImpl() {
    }

    // PlotConditionのJavaDoc
    public void setName(String name) {
        this.name = name;
    }

    // PlotConditionのJavaDoc
    public String getName() {
        return name;
    }

    // PlotConditionのJavaDoc
    public void addDatasetCondition(DatasetCondition dsCondition) {
        if (dsConditionMap == null) {
            dsConditionMap = new HashMap();
        }
        if (dsConditionList == null) {
            dsConditionList = new ArrayList();
        }
        
        if (dsConditionMap.containsKey(dsCondition.getName())) {
            List conditions = (List) dsConditionMap.get(dsCondition.getName());
            conditions.add(dsCondition);
            dsConditionList.add(dsCondition);
        } else {
            List conditions = new ArrayList();
            conditions.add(dsCondition);
            // キーにデータセット名、値にデータセット条件リスト
            dsConditionMap.put(dsCondition.getName(), conditions);
            dsConditionList.add(dsCondition);
        }
    }

    // PlotConditionのJavaDoc
    public DatasetCondition[] getDatasetConditions(String dsName) {
        if (dsConditionMap == null) {
            return new DatasetConditionImpl[0];
        }
        
        if (dsConditionMap.containsKey(dsName)) {
            List conditions = (List) dsConditionMap.get(dsName);
            return (DatasetCondition[]) conditions.toArray(new DatasetCondition[conditions.size()]);
        }

        return new DatasetConditionImpl[0];
    }
    public DatasetCondition[] getDatasetConditions(){
        if (dsConditionList == null) {
            return new DatasetConditionImpl[0];
        }
        return (DatasetCondition[])dsConditionList.toArray(new DatasetCondition[dsConditionList.size()]);
    }
    
    public Map getDatasetConditionMap() {
        if (dsConditionMap == null) {
            return null;
        }
        return dsConditionMap;
    }
    
    public void addDatasetConditionMap(Map map) {
        if (map == null || map.size() == 0) {
            return;
        }
        
        if (dsConditionMap == null) {
            dsConditionMap = new HashMap();
        }
        if (dsConditionList == null) {
            dsConditionList = new ArrayList();
        }
        
        Iterator itr = map.keySet().iterator();
        while (itr.hasNext()) {
            String dsName = (String) itr.next();
            dsConditionList.addAll((List)map.get(dsName));
            if (dsConditionMap.containsKey(dsName)) {
                List list = (List) dsConditionMap.get(dsName);
                list.addAll((Collection) map.get(dsName));
            } else {
                dsConditionMap.put(dsName, map.get(dsName));
            }
        }
    }

    // PlotConditionのJavaDoc
    public void addDatasetNameOrder(String dsName) {
        if (dsNameOrderSet == null) {
            dsNameOrderSet = new LinkedHashSet();
        }
        dsNameOrderSet.add(dsName);
    }

    // PlotConditionのJavaDoc
    public String[] getDatasetNameOrder() {
        if (dsNameOrderSet == null) {
            return new String[0];
        }
        
        return (String[]) dsNameOrderSet.toArray(new String[dsNameOrderSet.size()]);
    }

    public void setDatasetNameOrderSet(LinkedHashSet orders) {
        dsNameOrderSet = orders;
    }
    
    public LinkedHashSet getDatasetNameOrderSet() {
        return dsNameOrderSet;
    }
    
    // PlotConditionのJavaDoc
    public void addEnableDatasetName(String dsName) {
        if (enableDsNameSet == null) {
            enableDsNameSet = new HashSet();
        }
        enableDsNameSet.add(dsName);
    }

    // PlotConditionのJavaDoc
    public String[] getEnableDatasetNames() {
        if (enableDsNameSet == null) {
            return new String[0];
        }
        
        return (String[]) enableDsNameSet.toArray(new String[enableDsNameSet.size()]);
    }
    
    public void setEnableDatasetNameSet(Set names) {
        enableDsNameSet = names;
    }
    
    public Set getEnableDatasetNameSet() {
        return enableDsNameSet;
    }

    // PlotConditionのJavaDoc
    public Iterator getDatasetNames() {
        if (dsConditionMap == null) {
            dsConditionMap = new HashMap();
        }
        return dsConditionMap.keySet().iterator();
    }

}
