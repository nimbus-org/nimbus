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

import jp.ossc.nimbus.recset.*;
import jp.ossc.nimbus.util.converter.ConvertException;
/**
 * {@link RecordSet}型コードマスタ変換。<p>
 * {@link RecordSet}型のコードマスタを使って、値の変換を行う{@link CodeMasterConverter}実装クラスである。<br>
 * 変換方法は、以下のパターンがある。<br>
 * <ul>
 *   <li>引数で指定されたオブジェクトをプライマリキーとして、RecordSetから{@link RecordSet#get(RowData) RowDataプライマリキー検索}または{@link RecordSet#get(String) 文字列プライマリキー検索}を行い、RowDataまたはそのフィールドに変換する。</li>
 *   <li>引数に{@link RecordSetCodeMasterConverter.Key}を指定して、RecordSetから{@link RecordSet#get(RowData) RowDataプライマリキー検索}または{@link RecordSet#get(String) 文字列プライマリキー検索}を行い、RowDataまたはそのフィールドに変換する。</li>
 *   <li>引数に{@link RecordSetCodeMasterConverter.DynamicKey}を指定して、RecordSetから{@link RecordSet#searchDynamicKey(String, RowData, String[], boolean[]) 蓄積型キー検索}を行い、RowDataまたはそのフィールドに変換する。</li>
 *   <li>引数に{@link RecordSetCodeMasterConverter.DynamicCondition}を指定して、RecordSetから{@link RecordSet#searchDynamicCondition(String) 蓄積型条件検索}を行い、RowDataまたはそのフィールドに変換する。</li>
 *   <li>引数に{@link RecordSetCodeMasterConverter.DynamicConditionReal}を指定して、RecordSetから{@link RecordSet#searchDynamicConditionReal(String, String[], boolean[], Map) リアル型条件検索}を行い、RowDataまたはそのフィールドに変換する。</li>
 * </ul>
 *
 * @author M.Takata
 */
public class RecordSetCodeMasterConverter implements CodeMasterConverter{
    
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
        if(!(master instanceof RecordSet)){
            throw new ConvertException("CodeMaster is not RecordSet!");
        }
        final RecordSet recset = (RecordSet)master;
        RowData row = null;
        try{
            if(obj instanceof DynamicCondition){
                final DynamicCondition cnd = (DynamicCondition)obj;
                final Collection col = recset.searchDynamicCondition(
                    cnd.conditionName
                );
                final Iterator itr = col.iterator();
                if(itr.hasNext()){
                    row = (RowData)itr.next();
                }
            }else if(obj instanceof DynamicConditionReal){
                final DynamicConditionReal cnd = (DynamicConditionReal)obj;
                if(cnd.condition == null){
                    throw new ConvertException("DynamicConditionReal.condition is null.");
                }
                final Collection col = recset.searchDynamicConditionReal(
                    cnd.condition,
                    cnd.orderBy,
                    cnd.isAsc,
                    cnd.valueMap
                );
                final Iterator itr = col.iterator();
                if(itr.hasNext()){
                    row = (RowData)itr.next();
                }
            }else if(obj instanceof DynamicKey){
                final DynamicKey cnd = (DynamicKey)obj;
                RowData key = null;
                if(cnd.keyMap != null){
                    if(cnd.keyMap.size() == 0){
                        if(isThrowOnNullKey){
                            throw new ConvertException("DynamicKey.keyMap is empty.");
                        }
                        return nullKeyObject;
                    }
                    key = recset.createNewRecord();
                    Iterator entries = cnd.keyMap.entrySet().iterator();
                    while(entries.hasNext()){
                        Map.Entry entry = (Map.Entry)entries.next();
                        String fieldName = (String)entry.getKey();
                        Object fieldValue = entry.getValue();
                        FieldSchema field = recset.getRowSchema().get(fieldName);
                        if(field == null){
                            throw new ConvertException("Field '" + fieldName + "' is not found.");
                        }
                        key.setValueNative(
                            field.getIndex(),
                            fieldValue
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
                final Collection col = recset.searchDynamicKey(
                    cnd.conditionName,
                    key,
                    cnd.orderBy,
                    cnd.isAsc
                );
                final Iterator itr = col.iterator();
                if(itr.hasNext()){
                    row = (RowData)itr.next();
                }
            }else if(obj instanceof Key){
                final Key cnd = (Key)obj;
                if(cnd.key != null){
                    row = recset.get(cnd.key);
                }else{
                    RowData key = null;
                    if(cnd.keyMap != null){
                        if(cnd.keyMap.size() == 0){
                            if(isThrowOnNullKey){
                                throw new ConvertException("Key.keyMap is empty.");
                            }
                            return nullKeyObject;
                        }
                        key = recset.createNewRecord();
                        Iterator entries = cnd.keyMap.entrySet().iterator();
                        while(entries.hasNext()){
                            Map.Entry entry = (Map.Entry)entries.next();
                            String fieldName = (String)entry.getKey();
                            Object fieldValue = entry.getValue();
                            FieldSchema field = recset.getRowSchema().get(fieldName);
                            if(field == null){
                                throw new ConvertException("Field '" + fieldName + "' is not found.");
                            }
                            key.setValueNative(
                                field.getIndex(),
                                fieldValue
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
                    row = recset.get(key);
                }
            }else{
                if(obj == null){
                    if(isThrowOnNullKey){
                        throw new ConvertException("Key is null.");
                    }
                    return nullKeyObject;
                }
                if(obj instanceof RowData){
                    row = recset.get((RowData)obj);
                }else{
                    row = recset.get(obj.toString());
                }
            }
        }catch(ConvertException e){
            throw e;
        }catch(Exception e){
            throw new ConvertException(e);
        }
        if(row == null){
            if(isThrowOnNotFound){
                throw new ConvertException(
                    "Record is not found. key=" + obj
                );
            }
            return notFoundObject;
        }
        return valueFieldName == null ? row : row.get(valueFieldName);
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
        public RowData keyRecord;
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
                    FieldSchema fieldSchema = keyRecord.getRowSchema().get(i);
                    buf.append(fieldSchema.getFieldName())
                       .append('=').append(keyRecord.get(i));
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
        public String key;
        public Map keyMap;
        public RowData keyRecord;
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
                    FieldSchema fieldSchema = keyRecord.getRowSchema().get(i);
                    buf.append(fieldSchema.getFieldName())
                       .append('=').append(keyRecord.get(i));
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
