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
package jp.ossc.nimbus.service.context;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;

import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordSchema;
import jp.ossc.nimbus.beans.dataset.PropertySchema;
import jp.ossc.nimbus.beans.dataset.PropertySchemaDefineException;
import jp.ossc.nimbus.beans.dataset.PropertySetException;
import jp.ossc.nimbus.beans.dataset.PropertyGetException;

/**
 * 共有コンテキスト用のレコード。<p>
 * 差分更新をサポートする。<br>
 *
 * @author M.Takata
 */
public class SharedContextRecord extends Record implements SharedContextValueDifferenceSupport{
    
    private static final long serialVersionUID = 1543899282652907287l;
    
    protected int updateVersion;
    
    /**
     * 未定義のレコードを生成する。<p>
     */
    public SharedContextRecord(){
    }
    
    /**
     * レコードを生成する。<p>
     *
     * @param schema スキーマ文字列
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public SharedContextRecord(String schema) throws PropertySchemaDefineException{
        super(schema);
    }
    
    /**
     * レコードを生成する。<p>
     *
     * @param recordSchema スキーマ文字列から生成されたレコードスキーマ
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public SharedContextRecord(RecordSchema recordSchema){
        super(recordSchema);
    }
    
    /**
     * 表層的なレコードスキーマを設定する。<p>
     *
     * @param schema 表層的なレコードスキーマ
     */
    protected void setSuperficialRecordSchema(RecordSchema schema){
        super.setSuperficialRecordSchema(schema);
    }
    
    public void setUpdateVersion(int version){
        updateVersion = version;
    }
    
    public int getUpdateVersion(){
        return updateVersion;
    }
    
    protected static int compareToUpdateVersion(int ver1, int ver2){
        long version1 = ver1;
        long version2 = ver2;
        final long middle = ((long)Integer.MAX_VALUE - (long)Integer.MIN_VALUE) / 2l;
        
        if(version1 == version2){
            return 0;
        }else{
            if(version1 > version2){
                if((version1 - version2) > middle){
                    version1 = version1 - (long)Integer.MAX_VALUE;
                    return version1 > version2 ? 1 : -1;
                }else{
                    return 1;
                }
            }else{
                if((version2 - version1) > middle){
                    version2 = version2 - (long)Integer.MAX_VALUE;
                    return version1 > version2 ? -1 : 1;
                }else{
                    return -1;
                }
            }
        }
    }
    
    /**
     * 指定された名前のプロパティに、指定された値を更新した場合の差分情報を取得する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateProperty(String name, Object val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        RecordSchema recordSchema = getRecordSchema();
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema = recordSchema.getPropertySchema(name);
        if(propertySchema == null){
            throw new PropertySetException(null, "No such property : " + name);
        }
        return updateProperty(recordSchema.getPropertyIndex(name), val, diff);
    }
    
    /**
     * 指定されたインデックスのプロパティに、指定された値を更新した場合の差分情報を取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateProperty(int index, Object val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        if(getRecordList() == null){
            if(diff == null){
                diff = new Difference();
            }else if(!(diff instanceof Difference)){
                throw new SharedContextUpdateException("Unsupported type. class=" + diff.getClass().getName());
            }
            ((Difference)diff).updateProperty(this, index, val);
        }else{
            if(diff == null){
                diff = new SharedContextRecordList.Difference();
            }else if(!(diff instanceof SharedContextRecordList.Difference)){
                throw new SharedContextUpdateException("Unsupported type. class=" + diff.getClass().getName());
            }
            Difference recordDiff = ((SharedContextRecordList.Difference)diff).getRecordDifference(this.index);
            if(recordDiff == null){
                recordDiff = new Difference();
            }
            recordDiff.updateProperty(this, index, val);
            ((SharedContextRecordList.Difference)diff).updateRecord((SharedContextRecordList)getRecordList(), this.index, recordDiff);
        }
        return diff;
    }
    
    /**
     * 指定された名前のプロパティに、指定された値を更新した場合の差分情報を取得する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateProperty(String name, boolean val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        return updateProperty(name, val ? Boolean.TRUE : Boolean.FALSE, diff);
    }
    
    /**
     * 指定されたインデックスのプロパティに、指定された値を更新した場合の差分情報を取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateProperty(int index, boolean val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        return updateProperty(index, val ? Boolean.TRUE : Boolean.FALSE, diff);
    }
    
    /**
     * 指定された名前のプロパティに、指定された値を更新した場合の差分情報を取得する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateProperty(String name, byte val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        return updateProperty(name, new Byte(val), diff);
    }
    
    /**
     * 指定されたインデックスのプロパティに、指定された値を更新した場合の差分情報を取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateProperty(int index, byte val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        return updateProperty(index, new Byte(val), diff);
    }
    
    /**
     * 指定された名前のプロパティに、指定された値を更新した場合の差分情報を取得する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateProperty(String name, char val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        return updateProperty(name, new Character(val), diff);
    }
    
    /**
     * 指定されたインデックスのプロパティに、指定された値を更新した場合の差分情報を取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateProperty(int index, char val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        return updateProperty(index, new Character(val), diff);
    }
    
    /**
     * 指定された名前のプロパティに、指定された値を更新した場合の差分情報を取得する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateProperty(String name, short val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        return updateProperty(name, new Short(val), diff);
    }
    
    /**
     * 指定されたインデックスのプロパティに、指定された値を更新した場合の差分情報を取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateProperty(int index, short val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        return updateProperty(index, new Short(val), diff);
    }
    
    /**
     * 指定された名前のプロパティに、指定された値を更新した場合の差分情報を取得する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateProperty(String name, int val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        return updateProperty(name, new Integer(val), diff);
    }
    
    /**
     * 指定されたインデックスのプロパティに、指定された値を更新した場合の差分情報を取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateProperty(int index, int val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        return updateProperty(index, new Integer(val), diff);
    }
    
    /**
     * 指定された名前のプロパティに、指定された値を更新した場合の差分情報を取得する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateProperty(String name, long val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        return updateProperty(name, new Long(val), diff);
    }
    
    /**
     * 指定されたインデックスのプロパティに、指定された値を更新した場合の差分情報を取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateProperty(int index, long val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        return updateProperty(index, new Long(val), diff);
    }
    
    /**
     * 指定された名前のプロパティに、指定された値を更新した場合の差分情報を取得する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateProperty(String name, float val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        return updateProperty(name, new Float(val), diff);
    }
    
    /**
     * 指定されたインデックスのプロパティに、指定された値を更新した場合の差分情報を取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateProperty(int index, float val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        return updateProperty(index, new Float(val), diff);
    }
    
    /**
     * 指定された名前のプロパティに、指定された値を更新した場合の差分情報を取得する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateProperty(String name, double val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        return updateProperty(name, new Double(val), diff);
    }
    
    /**
     * 指定されたインデックスのプロパティに、指定された値を更新した場合の差分情報を取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateProperty(int index, double val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        return updateProperty(index, new Double(val), diff);
    }
    
    /**
     * 指定された名前のプロパティに、指定された値をパースして更新した場合の差分情報を取得する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateParseProperty(String name, Object val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        RecordSchema recordSchema = getRecordSchema();
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema = recordSchema.getPropertySchema(name);
        if(propertySchema == null){
            throw new PropertySetException(null, "No such property : " + name);
        }
        return updateProperty(recordSchema.getPropertyIndex(name), propertySchema.parse(val), diff);
    }
    
    /**
     * 指定されたインデックスのプロパティに、指定された値をパースして更新した場合の差分情報を取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @param diff 差分
     * @return 差分
     * @exception PropertySetException プロパティの設定に失敗した場合
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateParseProperty(int index, Object val, SharedContextValueDifference diff) throws PropertySetException, SharedContextUpdateException{
        RecordSchema recordSchema = getRecordSchema();
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(index);
        if(propertySchema == null){
            throw new PropertySetException(null, "No such property : " + index);
        }
        return updateProperty(index, propertySchema.parse(val), diff);
    }
    
    public int update(SharedContextValueDifference diff) throws SharedContextUpdateException{
        if(!(diff instanceof Difference)){
            throw new SharedContextUpdateException("Unsupported type. class=" + diff.getClass().getName());
        }
        return ((Difference)diff).updateRecord(this);
    }
    
    protected static void writeInt(ObjectOutput out, int val) throws IOException{
        if(val >= Byte.MIN_VALUE && val <= Byte.MAX_VALUE){
            out.writeByte((byte)1);
            out.writeByte((byte)val);
        }else if(val >= Short.MIN_VALUE && val <= Short.MAX_VALUE){
            out.writeByte((byte)2);
            out.writeShort((short)val);
        }else{
            out.writeByte((byte)3);
            out.writeInt(val);
        }
    }
    
    protected static int readInt(ObjectInput in) throws IOException{
        final int type = in.readByte();
        switch(type){
        case 1:
            return in.readByte();
        case 2:
            return in.readShort();
        default:
            return in.readInt();
        }
    }
    
    protected void writeExternalValues(ObjectOutput out) throws IOException{
        super.writeExternalValues(out);
        SharedContextRecord.writeInt(out, updateVersion);
    }
    
    protected void readExternalValues(ObjectInput in) throws IOException, ClassNotFoundException{
        super.readExternalValues(in);
        updateVersion = SharedContextRecord.readInt(in);
    }
    
    /**
     * レコード差分情報。<p>
     *
     * @author M.Takata
     */
    public static class Difference implements SharedContextValueDifference, Externalizable{
        
        private static final long serialVersionUID = 7551646648749330023l;
        
        private int updateVersion;
        private Map updateValueMap;
        
        public int getUpdateVersion(){
            return updateVersion;
        }
        
        /**
         * 指定されたプロパティの更新を格納する。<p>
         * 現在のプロパティの値と差分がない場合は、無視される。<br>
         *
         * @param record 更新対象のレコード
         * @param index プロパティのインデックス
         * @param value 更新する値
         * @exception SharedContextUpdateException 更新の格納に失敗した場合
         */
        public void updateProperty(SharedContextRecord record, int index, Object value) throws SharedContextUpdateException{
            Integer key = new Integer(index);
            try{
                Object old = record.getProperty(index);
                if((old == null && value != null)
                    || (old != null && value == null)
                    || (old != null && !old.equals(value))
                ){
                    if(updateValueMap == null){
                        updateValueMap = new HashMap();
                    }
                    updateValueMap.put(key, value);
                }else if(updateValueMap != null && updateValueMap.containsKey(key)){
                    updateValueMap.remove(key);
                }
            }catch(PropertyGetException e){
                throw new SharedContextUpdateException(e);
            }
            updateVersion = record.getUpdateVersion() + 1;
        }
        
        /**
         * 指定されたレコードに更新を反映する。<p>
         *
         * @param record 更新対象のレコード
         * @return 更新された場合、1。更新する必要がなかった場合、0。整合性が取れずに、更新できない場合、-1。
         * @exception SharedContextUpdateException 更新の反映に失敗した場合
         */
        public int updateRecord(SharedContextRecord record) throws SharedContextUpdateException{
            if(updateValueMap != null && updateValueMap.size() != 0){
                if(SharedContextRecord.compareToUpdateVersion(record.getUpdateVersion(), updateVersion) >= 0){
                    return 0;
                }else if(record.getUpdateVersion() + 1 != updateVersion){
                    return -1;
                }
                try{
                    for(Iterator itr = updateValueMap.entrySet().iterator(); itr.hasNext();){
                        Map.Entry entry = (Map.Entry)itr.next();
                        record.setProperty(((Integer)entry.getKey()).intValue(), entry.getValue());
                    }
                }catch(PropertySetException e){
                    throw new SharedContextUpdateException(e);
                }
            }
            record.setUpdateVersion(updateVersion);
            return 1;
        }
        
        /**
         * 更新のあったプロパティのインデックスを取得する。<p>
         *
         * @return インデックスの配列。更新がない場合は、null
         */
        public int[] getUpdatePropertyIndexs(){
            if(updateValueMap == null || updateValueMap.size() == 0){
                return null;
            }
            int[] result = new int[updateValueMap.size()];
            int index = 0;
            for(Iterator itr = updateValueMap.keySet().iterator(); itr.hasNext();){
                result[index++] = ((Integer)itr.next()).intValue();
            }
            return result;
        }
        
        /**
         * 指定されたプロパティの更新された値を取得する。<p>
         *
         * @param index プロパティのインデックス
         * @return プロパティの値。更新されていない場合は、null
         */
        public Object getUpdateProperty(int index){
            return updateValueMap == null ? null : updateValueMap.get(new Integer(index));
        }
        
        /**
         * 指定されたプロパティの更新を削除する。<p>
         *
         * @param index プロパティのインデックス
         */
        public void removeUpdateProperty(int index){
            if(updateValueMap != null){
                updateValueMap.remove(new Integer(index));
            }
        }
        
        /**
         * 指定されたプロパティが更新されたかを判定する。<p>
         *
         * @param index プロパティのインデックス
         * @return 更新された場合は、true
         */
        public boolean isUpdate(int index){
            return updateValueMap == null ? false : updateValueMap.containsKey(new Integer(index));
        }
        
        /**
         * 更新されたかを判定する。<p>
         *
         * @return 更新された場合は、true
         */
        public boolean isUpdate(){
            return updateValueMap != null && updateValueMap.size() != 0;
        }
        
        public void writeExternal(ObjectOutput out) throws IOException{
            out.writeObject(updateValueMap);
            SharedContextRecord.writeInt(out, updateVersion);
        }
        
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
            updateValueMap = (Map)in.readObject();
            updateVersion = SharedContextRecord.readInt(in);
        }
    }
}