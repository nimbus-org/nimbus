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
package jp.ossc.nimbus.service.codemaster;

import java.util.*;

/**
 * 部分更新レコード。<p>
 *
 * @author M.Takata
 */
public class PartUpdateRecords implements java.io.Serializable{
    
    private static final long serialVersionUID = -4013884085932487925L;
    
    protected List keys;
    
    protected Map keyMap;
    
    protected Map records = new LinkedHashMap();
    
    protected boolean containsAdd;
    
    protected boolean containsUpdate;
    
    protected boolean containsRemove;
    
    protected boolean isFilledRecord = true;
    
    public void addRecord(CodeMasterUpdateKey key){
        addRecord(key, null);
    }
    
    public void addRecord(CodeMasterUpdateKey key, Object record){
        switch(key.getUpdateType()){
        case CodeMasterUpdateKey.UPDATE_TYPE_ADD:
            containsAdd = true;
            if(record == null){
                isFilledRecord = false;
            }
            break;
        case CodeMasterUpdateKey.UPDATE_TYPE_UPDATE:
            containsUpdate = true;
            if(record == null){
                isFilledRecord = false;
            }
            break;
        case CodeMasterUpdateKey.UPDATE_TYPE_REMOVE:
            containsRemove = true;
            break;
        default:
        }
        records.put(key, record);
        if(keys != null && !keys.contains(key)){
            keys.add(key);
        }
        if(keyMap != null && !keyMap.containsKey(key)){
            keyMap.put(key, key);
        }
    }
    
    public Map getRecords(){
        return records;
    }
    
    public Object removeRecord(CodeMasterUpdateKey key){
        Object record = records.remove(key);
        if(keys != null){
            keys.remove(key);
        }
        if(keyMap != null){
            keyMap.remove(key);
        }
        return record;
    }
    
    public Iterator getKeys(){
        return new KeyIterator();
    }
    
    private class KeyIterator implements Iterator, java.io.Serializable{
        private static final long serialVersionUID = -4013884085802487925L;
        private Iterator itr = records.keySet().iterator();
        private CodeMasterUpdateKey current;
        public boolean hasNext(){
            return itr.hasNext();
        }
        public Object next(){
            current = (CodeMasterUpdateKey)itr.next();
            return current;
        }
        public void remove(){
            itr.remove();
            if(keys != null && current != null){
                keys.remove(current);
            }
            if(keyMap != null && current != null){
                keyMap.remove(current);
            }
        }
    };
    
    public CodeMasterUpdateKey[] getKeyArray(){
        return (CodeMasterUpdateKey[])records.keySet().toArray(new CodeMasterUpdateKey[records.size()]);
    }
    
    public Object getRecord(CodeMasterUpdateKey key){
        return records.get(key);
    }
    
    public CodeMasterUpdateKey getKey(CodeMasterUpdateKey key){
        if(keys == null){
            keys = new ArrayList();
            keys.addAll(records.keySet());
        }
        if(keyMap == null){
            keyMap = new HashMap();
            for(int i = 0, imax = keys.size(); i < imax; i++){
                Object keyObj = keys.get(i);
                keyMap.put(keyObj, keyObj);
            }
        }
        return (CodeMasterUpdateKey)keyMap.get(key);
    }
    
    public boolean containsAdd(){
        return containsAdd;
    }
    
    public boolean containsUpdate(){
        return containsUpdate;
    }
    
    public boolean containsRemove(){
        return containsRemove;
    }
    
    public boolean isFilledRecord(){
        return size() != 0 && isFilledRecord;
    }
    
    public void setFilledRecord(boolean isFilled){
        isFilledRecord = isFilled;
    }
    
    public int size(){
        return records.size();
    }
    
    public void clear(){
        records.clear();
        if(keys != null){
            keys.clear();
        }
        if(keyMap != null){
            keyMap.clear();
        }
    }
}