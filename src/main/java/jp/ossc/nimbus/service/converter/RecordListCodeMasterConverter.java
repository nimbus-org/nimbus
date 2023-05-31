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
package jp.ossc.nimbus.service.converter;

import java.util.*;

import jp.ossc.nimbus.beans.dataset.*;
import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.util.converter.ConvertException;

/**
 * {@link RecordList}型コードマスタ変換。<p>
 * {@link RecordList}型のコードマスタを使って、値の変換を行う{@link CodeMasterConverter}実装クラスである。<br>
 * 変換方法は、以下のパターンがある。<br>
 * <ul>
 *   <li>引数で指定されたオブジェクトをプライマリキーとして、RecordListから{@link RecordList#searchByPrimaryKey(Record) プライマリキー検索}を行い、Rercordまたはそのプロパティに変換する。</li>
 *   <li>引数に{@link RecordListCodeMasterConverter.Key}を指定して、RecordListから{@link RecordList#searchByPrimaryKey(Record) プライマリキー検索}を行い、Rercordまたはそのプロパティに変換する。</li>
 *   <li>引数に{@link RecordListCodeMasterConverter.DynamicKey}を指定して、RecordListから{@link RecordList#stockKeySearch(String, Record) 蓄積型キー検索}を行い、Rercordまたはそのプロパティに変換する。</li>
 *   <li>引数に{@link RecordListCodeMasterConverter.DynamicCondition}を指定して、RecordListから{@link RecordList#stockSearch(String) 蓄積型条件検索}を行い、Rercordまたはそのプロパティに変換する。</li>
 *   <li>引数に{@link RecordListCodeMasterConverter.DynamicConditionReal}を指定して、RecordListから{@link RecordList#realSearch(String, Map) リアル型条件検索}を行い、Rercordまたはそのプロパティに変換する。</li>
 * </ul>
 *
 * @author M.Takata
 */
public class RecordListCodeMasterConverter implements CodeMasterConverter{
    
    protected String valueFieldName;
    protected Object notFoundObject;
    protected boolean isThrowOnNotFound;
    protected Object nullKeyObject;
    protected boolean isThrowOnNullKey;
    
    public void setValueFieldName(String name){
        valueFieldName = name;
    }
    public String getValueFieldName(){
        return valueFieldName;
    }
    
    public void setNotFoundObject(Object obj){
        notFoundObject = obj;
    }
    public Object getNotFoundObject(){
        return notFoundObject;
    }
    
    public boolean isThrowOnNotFound(){
        return isThrowOnNotFound;
    }
    public void setThrowOnNotFound(boolean isThrow){
        isThrowOnNotFound = isThrow;
    }
    
    public void setNullKeyObject(Object obj){
        nullKeyObject = obj;
    }
    public Object getNullKeyObject(){
        return nullKeyObject;
    }
    
    public boolean isThrowOnNullKey(){
        return isThrowOnNullKey;
    }
    public void setThrowOnNullKey(boolean isThrow){
        isThrowOnNullKey = isThrow;
    }
    
    public Object convert(Object master, Object obj) throws ConvertException{
        if(!(master instanceof RecordList)){
            throw new ConvertException("CodeMaster is not RecordList!");
        }
        final RecordList recList = (RecordList)master;
        Record record = null;
        try{
            if(obj instanceof DynamicCondition){
                final DynamicCondition cnd = (DynamicCondition)obj;
                final List list = recList.createView().searchKeyElement(
                    cnd.conditionName,
                    null
                ).getResultList();
                final Iterator itr = list.iterator();
                if(itr.hasNext()){
                    record = (Record)itr.next();
                }
            }else if(obj instanceof DynamicConditionReal){
                final DynamicConditionReal cnd = (DynamicConditionReal)obj;
                if(cnd.condition == null){
                    throw new ConvertException("DynamicConditionReal.condition is null.");
                }
                final RecordList list = recList.realSearch(
                    cnd.condition,
                    cnd.valueMap
                );
                list.sort(cnd.orderBy, cnd.isAsc);
                final Iterator itr = list.iterator();
                if(itr.hasNext()){
                    record = (Record)itr.next();
                }
            }else if(obj instanceof DynamicKey){
                final DynamicKey cnd = (DynamicKey)obj;
                Record key = null;
                if(cnd.keyMap != null){
                    if(cnd.keyMap.size() == 0){
                        if(isThrowOnNullKey){
                            throw new ConvertException("DynamicKey.keyMap is empty.");
                        }
                        return nullKeyObject;
                    }
                    key = recList.createRecord();
                    Iterator entries = cnd.keyMap.entrySet().iterator();
                    while(entries.hasNext()){
                        Map.Entry entry = (Map.Entry)entries.next();
                        String propName = (String)entry.getKey();
                        Object propValue = entry.getValue();
                        int index = recList.getRecordSchema().getPropertyIndex(propName);
                        if(index == -1){
                            throw new ConvertException("Property '" + propName + "' is not found.");
                        }
                        key.setProperty(
                            index,
                            propValue
                        );
                    }
                }else{
                    if(cnd.keyRecord == null){
                        if(isThrowOnNullKey){
                            throw new ConvertException("DynamicKey.keyRecord is null.");
                        }
                        return nullKeyObject;
                    }
                    key = cnd.keyRecord;
                }
                final List list = recList.createView().searchByElement(
                    key,
                    cnd.conditionName,
                    null
                ).getResultList(cnd.orderBy, cnd.isAsc);
                if(list != null && list.size() > 0){
                    record = (Record)list.get(0);
                }
            }else if(obj instanceof Key){
                final Key cnd = (Key)obj;
                if(cnd.key != null){
                    PropertySchema[] schemata = recList.getRecordSchema().getPrimaryKeyPropertySchemata();
                    if(schemata == null || schemata.length != 1){
                        throw new ConvertException("Size of primary key property not equal 1.");
                    }
                    Record key = recList.createRecord();
                    key.setProperty(schemata[0].getName(), cnd.key);
                    record = recList.searchByPrimaryKey(key);
                }else{
                    Record key = null;
                    if(cnd.keyMap != null){
                        if(cnd.keyMap.size() == 0){
                            if(isThrowOnNullKey){
                                throw new ConvertException("Key.keyMap is empty.");
                            }
                            return nullKeyObject;
                        }
                        key = recList.createRecord();
                        Iterator entries = cnd.keyMap.entrySet().iterator();
                        while(entries.hasNext()){
                            Map.Entry entry = (Map.Entry)entries.next();
                            String propName = (String)entry.getKey();
                            Object propValue = entry.getValue();
                            int index = recList.getRecordSchema().getPropertyIndex(propName);
                            if(index == -1){
                                throw new ConvertException("Property '" + propName + "' is not found.");
                            }
                            key.setProperty(
                                index,
                                propValue
                            );
                        }
                    }else{
                        if(cnd.keyRecord == null){
                            if(isThrowOnNullKey){
                                throw new ConvertException("Key.keyRecord is null.");
                            }
                            return nullKeyObject;
                        }
                        key = cnd.keyRecord;
                    }
                    record = recList.searchByPrimaryKey(key);
                }
            }else{
                if(obj == null){
                    if(isThrowOnNullKey){
                        throw new ConvertException("Key is null.");
                    }
                    return nullKeyObject;
                }
                if(obj instanceof Record){
                    record = recList.searchByPrimaryKey((Record)obj);
                }else{
                    PropertySchema[] schemata = recList.getRecordSchema().getPrimaryKeyPropertySchemata();
                    if(schemata == null || schemata.length != 1){
                        throw new ConvertException("Size of primary key property not equal 1.");
                    }
                    Record key = recList.createRecord();
                    key.setProperty(schemata[0].getName(), obj);
                    record = recList.searchByPrimaryKey(key);
                }
            }
        }catch(ConvertException e){
            throw e;
        }catch(Exception e){
            throw new ConvertException(e);
        }
        if(record == null){
            if(isThrowOnNotFound){
                throw new ConvertException(
                    "Record is not found. key=" + obj
                );
            }
            return notFoundObject;
        }
        return valueFieldName == null ? record : record.get(valueFieldName);
    }
    
    public static class DynamicCondition{
        public String conditionName;
        public String toString(){
            return "DynamicCondition{conditionName=" + conditionName + '}';
        }
    }
    
    public static class DynamicConditionReal{
        public String condition;
        public Map valueMap;
        public String[] orderBy;
        public boolean[] isAsc;
        public String toString(){
            StringBuilder buf = new StringBuilder("DynamicConditionReal{");
            buf.append("condition=").append(condition);
            buf.append(", valueMap=").append(valueMap);
            buf.append(", orderBy=");
            if(orderBy == null){
                buf.append(orderBy);
            }else{
                buf.append('[');
                for(int i = 0; i < orderBy.length; i++){
                    buf.append(orderBy[i]);
                    if(i != orderBy.length - 1){
                        buf.append(", ");
                    }
                }
                buf.append(']');
            }
            buf.append(", isAsc=");
            if(isAsc == null){
                buf.append(isAsc);
            }else{
                buf.append('[');
                for(int i = 0; i < isAsc.length; i++){
                    buf.append(isAsc[i]);
                    if(i != isAsc.length - 1){
                        buf.append(", ");
                    }
                }
                buf.append(']');
            }
            buf.append('}');
            return buf.toString();
        }
    }
    
    public static class DynamicKey{
        public String conditionName;
        public Map keyMap;
        public Record keyRecord;
        public String[] orderBy;
        public boolean[] isAsc;
        public String toString(){
            StringBuilder buf = new StringBuilder("DynamicKey{");
            buf.append("conditionName=").append(conditionName);
            buf.append(", keyMap=").append(keyMap);
            buf.append(", keyRecord=");
            if(keyRecord == null){
                buf.append(keyRecord);
            }else{
                buf.append('{');
                for(int i = 0; i < keyRecord.size(); i++){
                    buf.append(keyRecord.getRecordSchema().getPropertyName(i))
                       .append('=').append(keyRecord.getProperty(i));
                    if(i != orderBy.length - 1){
                        buf.append(", ");
                    }
                }
                buf.append('}');
            }
            buf.append(", orderBy=");
            if(orderBy == null){
                buf.append(orderBy);
            }else{
                buf.append('[');
                for(int i = 0; i < orderBy.length; i++){
                    buf.append(orderBy[i]);
                    if(i != orderBy.length - 1){
                        buf.append(", ");
                    }
                }
                buf.append(']');
            }
            buf.append(", isAsc=");
            if(isAsc == null){
                buf.append(isAsc);
            }else{
                buf.append('[');
                for(int i = 0; i < isAsc.length; i++){
                    buf.append(isAsc[i]);
                    if(i != isAsc.length - 1){
                        buf.append(", ");
                    }
                }
                buf.append(']');
            }
            buf.append('}');
            return buf.toString();
        }
    }
    
    public static class Key{
        public Object key;
        public Map keyMap;
        public Record keyRecord;
        public String toString(){
            StringBuilder buf = new StringBuilder("Key{");
            buf.append("key=").append(key);
            buf.append(", keyMap=").append(keyMap);
            buf.append(", keyRecord=");
            if(keyRecord == null){
                buf.append(keyRecord);
            }else{
                buf.append('{');
                for(int i = 0; i < keyRecord.size(); i++){
                    buf.append(keyRecord.getRecordSchema().getPropertyName(i))
                       .append('=').append(keyRecord.getProperty(i));
                    if(i != keyRecord.size() - 1){
                        buf.append(", ");
                    }
                }
                buf.append('}');
            }
            buf.append('}');
            return buf.toString();
        }
    }
}
