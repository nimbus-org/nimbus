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
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;

import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.beans.dataset.RecordSchema;
import jp.ossc.nimbus.beans.dataset.PropertySchemaDefineException;

/**
 * 共有コンテキスト用のレコードリスト。<p>
 * 差分更新をサポートする。<br>
 *
 * @author M.Takata
 */
public class SharedContextRecordList extends RecordList implements SharedContextValueDifferenceSupport{
    
    private static final long serialVersionUID = -1910946166181855454l;
    
    protected int updateVersion;
    
    /**
     * 未定義のレコードリストを生成する。<p>
     */
    public SharedContextRecordList(){
    }
    
    /**
     * 未定義のレコードリストを生成する。<p>
     * 
     * @param isSynch 同期化する場合true
     */
    public SharedContextRecordList(boolean isSynch){
        super(isSynch);
    }
    
    /**
     * 未定義のレコードリストを生成する。<p>
     *
     * @param name レコード名
     */
    public SharedContextRecordList(String name){
        super(name);
    }
    
    /**
     * 未定義のレコードリストを生成する。<p>
     *
     * @param name レコード名
     * @param isSynch 同期化する場合true
     */
    public SharedContextRecordList(String name, boolean isSynch){
        super(name, isSynch);
    }
    
    /**
     * 空のレコードリストを生成する。<p>
     *
     * @param name レコード名
     * @param schema スキーマ文字列
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public SharedContextRecordList(String name, String schema)
     throws PropertySchemaDefineException{
        super(name, schema);
    }
    
    /**
     * 空のレコードリストを生成する。<p>
     *
     * @param name レコード名
     * @param schema スキーマ文字列
     * @param isSynch 同期化する場合true
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public SharedContextRecordList(String name, String schema, boolean isSynch)
     throws PropertySchemaDefineException{
        super(name, schema, isSynch);
    }
    
    /**
     * 空のレコードリストを生成する。<p>
     *
     * @param name レコード名
     * @param schema スキーマ
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public SharedContextRecordList(String name, RecordSchema schema)
     throws PropertySchemaDefineException{
        super(name, schema);
    }
    
    /**
     * 空のレコードリストを生成する。<p>
     *
     * @param name レコード名
     * @param schema スキーマ
     * @param isSynch 同期化する場合true
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public SharedContextRecordList(String name, RecordSchema schema, boolean isSynch)
     throws PropertySchemaDefineException{
        super(name, schema, isSynch);
    }
    
    /**
     * 空のレコードリストを生成する。<p>
     *
     * @param name レコード名
     * @param clazz レコードクラス
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public SharedContextRecordList(String name, Class clazz)
     throws PropertySchemaDefineException{
        super(name, clazz);
    }
    
    /**
     * 空のレコードリストを生成する。<p>
     *
     * @param name レコード名
     * @param clazz レコードクラス
     * @param isSynch 同期化する場合true
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public SharedContextRecordList(String name, Class clazz, boolean isSynch)
     throws PropertySchemaDefineException{
        super(name, clazz, isSynch);
    }
    
    public void setUpdateVersion(int version){
        updateVersion = version;
    }
    
    public int getUpdateVersion(){
        return updateVersion;
    }
    
    public Object getUpdateTemplate(){
        SharedContextRecordList clone = (SharedContextRecordList)cloneSchema();
        clone.updateVersion = updateVersion;
        return clone;
    }
    
    public Record createRecord(){
        if(recordClass == null){
            SharedContextRecord record = new SharedContextRecord(recordSchema);
            if(superficialRecordSchema != null){
                record.setSuperficialRecordSchema(superficialRecordSchema);
            }
            return record;
        }else{
            return super.createRecord();
        }
    }
    
    /**
     * 指定されたレコードを追加した場合の差分情報を取得する。<p>
     *
     * @param record レコード
     * @param diff 差分
     * @return 差分
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateAdd(Record record, SharedContextValueDifference diff) throws SharedContextUpdateException{
        if(diff == null){
            diff = new Difference();
        }else if(!(diff instanceof Difference)){
            throw new SharedContextUpdateException("Unsupported type. class=" + diff.getClass().getName());
        }
        ((Difference)diff).add(this, record);
        return diff;
    }
    
    /**
     * 指定されたレコードを、指定したインデックスに挿入した場合の差分情報を取得する。<p>
     *
     * @param index インデックス
     * @param record レコード
     * @param diff 差分
     * @return 差分
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateAdd(int index, Record record, SharedContextValueDifference diff) throws SharedContextUpdateException{
        if(diff == null){
            diff = new Difference();
        }else if(!(diff instanceof Difference)){
            throw new SharedContextUpdateException("Unsupported type. class=" + diff.getClass().getName());
        }
        ((Difference)diff).add(this, index, record);
        return diff;
    }
    
    /**
     * 指定されたレコードを、指定したインデックスのレコードと差し替えた場合の差分情報を取得する。<p>
     *
     * @param index インデックス
     * @param record レコード
     * @param diff 差分
     * @return 差分
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateSet(int index, Record record, SharedContextValueDifference diff) throws SharedContextUpdateException{
        if(diff == null){
            diff = new Difference();
        }else if(!(diff instanceof Difference)){
            throw new SharedContextUpdateException("Unsupported type. class=" + diff.getClass().getName());
        }
        ((Difference)diff).set(this, index, record);
        return diff;
    }
    
    /**
     * 指定されたインデックスのレコードを削除した場合の差分情報を取得する。<p>
     *
     * @param index インデックス
     * @param diff 差分
     * @return 差分
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateRemove(int index, SharedContextValueDifference diff) throws SharedContextUpdateException{
        if(diff == null){
            diff = new Difference();
        }else if(!(diff instanceof Difference)){
            throw new SharedContextUpdateException("Unsupported type. class=" + diff.getClass().getName());
        }
        ((Difference)diff).remove(this, index);
        return diff;
    }
    
    /**
     * 指定されたレコードを削除した場合の差分情報を取得する。<p>
     *
     * @param record 削除するレコード
     * @param diff 差分
     * @return 差分
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateRemove(Record record, SharedContextValueDifference diff) throws SharedContextUpdateException{
        if(diff == null){
            diff = new Difference();
        }else if(!(diff instanceof Difference)){
            throw new SharedContextUpdateException("Unsupported type. class=" + diff.getClass().getName());
        }
        ((Difference)diff).remove(this, record);
        return diff;
    }
    
    /**
     * 全件削除の差分情報を取得する。<p>
     *
     * @param diff 差分
     * @return 差分
     * @exception SharedContextUpdateException 差分情報の取得に失敗した場合
     */
    public SharedContextValueDifference updateClear(SharedContextValueDifference diff) throws SharedContextUpdateException{
        if(diff == null){
            diff = new Difference();
        }else if(!(diff instanceof Difference)){
            throw new SharedContextUpdateException("Unsupported type. class=" + diff.getClass().getName());
        }
        ((Difference)diff).clear(this);
        return diff;
    }
    
    public int update(SharedContextValueDifference diff) throws SharedContextUpdateException{
        if(!(diff instanceof Difference)){
            throw new SharedContextUpdateException("Unsupported type. class=" + diff.getClass().getName());
        }
        return ((Difference)diff).updateRecordList(this);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        super.writeExternal(out);
        SharedContextRecordList.writeInt(out, updateVersion);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        super.readExternal(in);
        updateVersion = SharedContextRecordList.readInt(in);
    }
    
    /**
     * レコードリスト差分情報。<p>
     *
     * @author M.Takata
     */
    public static class Difference implements SharedContextValueDifference, Externalizable{
        
        private static final long serialVersionUID = -3722608370963750486l;
        
        private int updateVersion;
        private Map recordDiffMap;
        private List transactionList;
        
        public int getUpdateVersion(){
            return updateVersion;
        }
        
        /**
         * レコードを追加する更新を格納する。<p>
         *
         * @param list レコードリスト
         * @param record 追加するレコード
         * @exception SharedContextUpdateException 更新の格納に失敗した場合
         */
        public void add(SharedContextRecordList list, Record record) throws SharedContextUpdateException{
            if(transactionList == null){
                transactionList = new ArrayList();
            }
            transactionList.add(new AddTransaction(record));
            updateVersion = list.getUpdateVersion() + 1;
        }
        
        /**
         * レコードを追加する更新を格納する。<p>
         *
         * @param list レコードリスト
         * @param index 追加するレコードのインデックス
         * @param record 追加するレコード
         * @exception SharedContextUpdateException 更新の格納に失敗した場合
         */
        public void add(SharedContextRecordList list, int index, Record record) throws SharedContextUpdateException{
            if(transactionList == null){
                transactionList = new ArrayList();
            }
            if(index < 0 || index > list.size() - 1){
                throw new SharedContextUpdateException("Illegal index. index=" + index + ", size=" + list.size());
            }
            AddTransaction addTran = new AddTransaction(index, record);
            for(int i = 0; i < transactionList.size(); i++){
                Transaction tran = (Transaction)transactionList.get(i);
                if(tran instanceof CollectionTransaction){
                    throw new SharedContextUpdateException("It is not possible to execute a single operation after a set operation.");
                }
                addTran.updateIndexDifference(tran.getIndexDifference());
            }
            transactionList.add(addTran);
            updateVersion = list.getUpdateVersion() + 1;
        }
        
        /**
         * レコードを差し替えする更新を格納する。<p>
         *
         * @param list レコードリスト
         * @param index 差し替えするレコードのインデックス
         * @param record 差し替えするレコード
         * @exception SharedContextUpdateException 更新の格納に失敗した場合
         */
        public void set(SharedContextRecordList list, int index, Record record) throws SharedContextUpdateException{
            if(transactionList == null){
                transactionList = new ArrayList();
            }
            if(index < 0 || index > list.size() - 1){
                throw new SharedContextUpdateException("Illegal index. index=" + index + ", size=" + list.size());
            }
            SetTransaction setTran = new SetTransaction(index, record);
            for(int i = 0; i < transactionList.size(); i++){
                Transaction tran = (Transaction)transactionList.get(i);
                if(tran instanceof CollectionTransaction){
                    throw new SharedContextUpdateException("It is not possible to execute a single operation after a set operation.");
                }
                setTran.updateIndexDifference(tran.getIndexDifference());
            }
            transactionList.add(setTran);
            updateVersion = list.getUpdateVersion() + 1;
        }
        
        /**
         * レコードを削除する更新を格納する。<p>
         * 指定されたレコードがレコードリストに含まれていない場合は、無視する。<br>
         *
         * @param list レコードリスト
         * @param val 削除するレコード
         * @exception SharedContextUpdateException 更新の格納に失敗した場合
         */
        public void remove(SharedContextRecordList list, Object val) throws SharedContextUpdateException{
            if(transactionList == null){
                transactionList = new ArrayList();
            }
            int index = -1;
            if(val != null){
                SharedContextRecord record = (SharedContextRecord)val;
                index = record.getIndex();
            }
            if(index == -1){
                index = list.indexOf(val);
            }
            if(index != -1){
                RemoveIndexTransaction removeTran = new RemoveIndexTransaction(index);
                for(int i = 0; i < transactionList.size(); i++){
                    Transaction tran = (Transaction)transactionList.get(i);
                    if(tran instanceof CollectionTransaction){
                        throw new SharedContextUpdateException("It is not possible to execute a single operation after a set operation.");
                    }
                    removeTran.updateIndexDifference(tran.getIndexDifference());
                }
                transactionList.add(removeTran);
            }
            updateVersion = list.getUpdateVersion() + 1;
        }
        
        /**
         * レコードを削除する更新を格納する。<p>
         *
         * @param list レコードリスト
         * @param index 削除するレコードのインデックス
         * @exception SharedContextUpdateException 更新の格納に失敗した場合
         */
        public void remove(SharedContextRecordList list, int index) throws SharedContextUpdateException{
            if(transactionList == null){
                transactionList = new ArrayList();
            }
            if(index < 0 || index > list.size() - 1){
                throw new SharedContextUpdateException("Illegal index. index=" + index + ", size=" + list.size());
            }
            RemoveIndexTransaction removeTran = new RemoveIndexTransaction(index);
            for(int i = 0; i < transactionList.size(); i++){
                Transaction tran = (Transaction)transactionList.get(i);
                if(tran instanceof CollectionTransaction){
                    throw new SharedContextUpdateException("It is not possible to execute a single operation after a set operation.");
                }
                removeTran.updateIndexDifference(tran.getIndexDifference());
            }
            transactionList.add(removeTran);
            updateVersion = list.getUpdateVersion() + 1;
        }
        
        /**
         * レコードリストを全て削除する更新を格納する。<p>
         *
         * @param list レコードリスト
         * @exception SharedContextUpdateException 更新の格納に失敗した場合
         */
        public void clear(SharedContextRecordList list) throws SharedContextUpdateException{
            if(transactionList == null){
                transactionList = new ArrayList();
            }
            transactionList.add(new ClearTransaction());
            updateVersion = list.getUpdateVersion() + 1;
        }
        
        /**
         * レコードの集合を追加する更新を格納する。<p>
         *
         * @param list レコードリスト
         * @param c 追加するレコードの集合
         * @exception SharedContextUpdateException 更新の格納に失敗した場合
         */
        public void addAll(SharedContextRecordList list, Collection c) throws SharedContextUpdateException{
            if(transactionList == null){
                transactionList = new ArrayList();
            }
            transactionList.add(new AddAllTransaction(c));
            updateVersion = list.getUpdateVersion() + 1;
        }
        
        /**
         * レコードの集合を追加する更新を格納する。<p>
         *
         * @param list レコードリスト
         * @param index 追加するインデックス
         * @param c 追加するレコードの集合
         * @exception SharedContextUpdateException 更新の格納に失敗した場合
         */
        public void addAll(SharedContextRecordList list, int index, Collection c) throws SharedContextUpdateException{
            if(transactionList == null){
                transactionList = new ArrayList();
            }
            if(index < 0 || index > list.size() - 1){
                throw new SharedContextUpdateException("Illegal index. index=" + index + ", size=" + list.size());
            }
            transactionList.add(new AddAllTransaction(index, c));
            updateVersion = list.getUpdateVersion() + 1;
        }
        
        /**
         * レコードの集合を削除する更新を格納する。<p>
         *
         * @param list レコードリスト
         * @param c 削除するレコードの集合
         * @exception SharedContextUpdateException 更新の格納に失敗した場合
         */
        public void removeAll(SharedContextRecordList list, Collection c) throws SharedContextUpdateException{
            if(transactionList == null){
                transactionList = new ArrayList();
            }
            transactionList.add(new RemoveAllTransaction(c));
            updateVersion = list.getUpdateVersion() + 1;
        }
        
        /**
         * 指定されたレコードの集合のみを残す更新を格納する。<p>
         *
         * @param list レコードリスト
         * @param c 残すレコードの集合
         * @exception SharedContextUpdateException 更新の格納に失敗した場合
         */
        public void retainAll(SharedContextRecordList list, Collection c) throws SharedContextUpdateException{
            if(transactionList == null){
                transactionList = new ArrayList();
            }
            transactionList.add(new RetainAllTransaction(c));
            updateVersion = list.getUpdateVersion() + 1;
        }
        
        /**
         * 指定されたレコードの差分を更新を格納する。<p>
         *
         * @param list レコードリスト
         * @param index レコードのインデックス
         * @param diff レコードの差分情報
         * @exception SharedContextUpdateException 更新の格納に失敗した場合
         */
        public void updateRecord(SharedContextRecordList list, int index, SharedContextRecord.Difference diff) throws SharedContextUpdateException{
            if(transactionList == null){
                transactionList = new ArrayList();
            }
            if(index < 0 || index > list.size() - 1){
                throw new SharedContextUpdateException("Illegal index. index=" + index + ", size=" + list.size());
            }
            if(recordDiffMap == null){
                recordDiffMap = new HashMap();
            }
            Object key = new Integer(index);
            if(!recordDiffMap.containsKey(key)){
                UpdateTransaction updateTran = new UpdateTransaction(index, diff);
                for(int i = 0; i < transactionList.size(); i++){
                    Transaction tran = (Transaction)transactionList.get(i);
                    if(tran instanceof CollectionTransaction){
                        throw new SharedContextUpdateException("It is not possible to execute a single operation after a set operation.");
                    }
                    updateTran.updateIndexDifference(tran.getIndexDifference());
                }
                recordDiffMap.put(key, diff);
                transactionList.add(updateTran);
            }
            updateVersion = list.getUpdateVersion() + 1;
        }
        
        /**
         * 指定されたレコードの差分情報を取得する。<p>
         *
         * @param index レコードのインデックス
         * @return 差分情報。差分がない場合は、null
         */
        protected SharedContextRecord.Difference getRecordDifference(int index){
            return recordDiffMap == null ? null : (SharedContextRecord.Difference)recordDiffMap.get(new Integer(index));
        }
        
        /**
         * 指定されたレコードリストに更新を反映する。<p>
         *
         * @param list 更新対象のレコードリスト
         * @return 全て更新された場合、1。更新されたものと、更新する必要がなかったものが存在する場合、0。整合性が取れずに、更新できないものが存在する場合、-1。
         * @exception SharedContextUpdateException 更新の反映に失敗した場合
         */
        public int updateRecordList(SharedContextRecordList list) throws SharedContextUpdateException{
            int result = 1;
            if(transactionList != null && transactionList.size() != 0){
                if(SharedContextRecord.compareToUpdateVersion(list.getUpdateVersion(), updateVersion) >= 0){
                    return 0;
                }else if(list.getUpdateVersion() + 1 != updateVersion){
                    return -1;
                }
                for(int i = 0, imax = transactionList.size(); i < imax; i++){
                    final int ret = ((Transaction)transactionList.get(i)).execute(list);
                    switch(ret){
                    case 0:
                        if(result != -1){
                            result = 0;
                        }
                        break;
                    case -1:
                        result = -1;
                        break;
                    case 1:
                        break;
                    }
                }
            }
            list.setUpdateVersion(updateVersion);
            return result;
        }
        
        /**
         * 更新されたかを判定する。<p>
         *
         * @return 更新された場合は、true
         */
        public boolean isUpdate(){
            return transactionList != null && transactionList.size() != 0;
        }
        
        /**
         * 更新のトランザクションリストを取得する。<p>
         *
         * @return 更新トランザクションのリスト
         */
        public List getTransactionList(){
            return transactionList;
        }
        
        /**
         * 指定された種別のトランザクションリストを取得する。<p>
         * 
         * @param type トランザクション種別
         * @return 更新トランザクションのリスト。指定された種別のトランザクションが存在しない場合は、null
         */
        protected List getTransactionList(int type){
            if(transactionList == null || transactionList.size() == 0){
                return null;
            }
            List result = null;
            for(int i = 0, imax = transactionList.size(); i < imax; i++){
                Transaction transaction = (Transaction)transactionList.get(i);
                if(transaction.getType() == type){
                    if(result == null){
                        result = new ArrayList();
                    }
                    result.add(transaction);
                }
            }
            return result;
        }
        
        /**
         * 追加トランザクションのリストを取得する。<p>
         *
         * @return 追加トランザクションのリスト。存在しない場合は、null
         */
        public List getAddTransactionList(){
            return getTransactionList(Transaction.ADD);
        }
        
        /**
         * 差し替えトランザクションのリストを取得する。<p>
         *
         * @return 差し替えトランザクションのリスト。存在しない場合は、null
         */
        public List getSetTransactionList(){
            return getTransactionList(Transaction.SET);
        }
        
        /**
         * 削除トランザクションのリストを取得する。<p>
         *
         * @return 削除トランザクションのリスト。存在しない場合は、null
         */
        public List getRemoveTransactionList(){
            return getTransactionList(Transaction.REMOVE);
        }
        
        /**
         * インデックス指定削除トランザクションのリストを取得する。<p>
         *
         * @return インデックス指定削除トランザクションのリスト。存在しない場合は、null
         */
        public List getRemoveIndexTransactionList(){
            return getTransactionList(Transaction.REMOVEINDEX);
        }
        
        /**
         * 全追加トランザクションのリストを取得する。<p>
         *
         * @return 全追加トランザクションのリスト。存在しない場合は、null
         */
        public List getAddAllTransactionList(){
            return getTransactionList(Transaction.ADDALL);
        }
        
        /**
         * 全削除トランザクションのリストを取得する。<p>
         *
         * @return 全削除トランザクションのリスト。存在しない場合は、null
         */
        public List getRemoveAllTransactionList(){
            return getTransactionList(Transaction.REMOVEALL);
        }
        
        /**
         * 指定要素集合のみを残すトランザクションのリストを取得する。<p>
         *
         * @return 指定要素集合のみを残すトランザクションのリスト。存在しない場合は、null
         */
        public List getRetainAllTransactionList(){
            return getTransactionList(Transaction.RETAINALL);
        }
        
        /**
         * レコード更新トランザクションのリストを取得する。<p>
         *
         * @return レコード更新トランザクションのリスト。存在しない場合は、null
         */
        public List getUpdateTransactionList(){
            return getTransactionList(Transaction.UPDATE);
        }
        
        /**
         * 全件削除トランザクションが存在するか判定する。<p>
         *
         * @return 全件削除トランザクションが存在する場合は、true
         */
        public boolean isClear(){
            List trans = getTransactionList(Transaction.CLEAR);
            return trans == null || trans.size() == 0 ? false : true;
        }
        
        public void writeExternal(ObjectOutput out) throws IOException{
            if(transactionList == null || transactionList.size() == 0){
                SharedContextRecordList.writeInt(out, 0);
            }else{
                SharedContextRecordList.writeInt(out, transactionList.size());
                for(int i = 0, imax = transactionList.size(); i < imax; i++){
                    Transaction tran = (Transaction)transactionList.get(i);
                    out.write(tran.getType());
                    tran.writeExternal(out);
                }
            }
            SharedContextRecordList.writeInt(out, updateVersion);
        }
        
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
            int size = SharedContextRecordList.readInt(in);
            if(size > 0){
                transactionList = new ArrayList(size);
                for(int i = 0; i < size; i++){
                    final int type = in.read();
                    Transaction tran = null;
                    switch(type){
                    case Transaction.ADD:
                        tran = new AddTransaction();
                        break;
                    case Transaction.SET:
                        tran = new SetTransaction();
                        break;
                    case Transaction.REMOVE:
                        tran = new RemoveTransaction();
                        break;
                    case Transaction.CLEAR:
                        tran = new ClearTransaction();
                        break;
                    case Transaction.ADDALL:
                        tran = new AddAllTransaction();
                        break;
                    case Transaction.REMOVEALL:
                        tran = new RemoveAllTransaction();
                        break;
                    case Transaction.RETAINALL:
                        tran = new RetainAllTransaction();
                        break;
                    case Transaction.UPDATE:
                        tran = new UpdateTransaction();
                        break;
                    case Transaction.REMOVEINDEX:
                        tran = new RemoveIndexTransaction();
                        break;
                    default:
                        throw new IOException("Unknown transaction. type=" + type);
                    }
                    tran.readExternal(in);
                    transactionList.add(tran);
                }
            }
            updateVersion = SharedContextRecordList.readInt(in);
        }
        
        /**
         * トランザクション。<p>
         *
         * @author M.Takata
         */
        public static interface Transaction extends Externalizable{
            
            /**
             * 更新種別：追加。<p>
             */
            public static final byte ADD         = 1;
            
            /**
             * 更新種別：差し替え。<p>
             */
            public static final byte SET         = 2;
            
            /**
             * 更新種別：削除。<p>
             */
            public static final byte REMOVE      = 3;
            
            /**
             * 更新種別：全削除。<p>
             */
            public static final byte CLEAR       = 4;
            
            /**
             * 更新種別：集合追加。<p>
             */
            public static final byte ADDALL      = 5;
            
            /**
             * 更新種別：集合削除。<p>
             */
            public static final byte REMOVEALL   = 6;
            
            /**
             * 更新種別：集合残し。<p>
             */
            public static final byte RETAINALL   = 7;
            
            /**
             * 更新種別：レコード更新。<p>
             */
            public static final byte UPDATE      = 8;
            
            /**
             * 更新種別：インデックス指定削除。<p>
             */
            public static final byte REMOVEINDEX = 9;
            
            /**
             * トランザクション種別を取得する。<p>
             *
             * @return トランザクション種別
             * @see #ADD
             * @see #SET
             * @see #REMOVE
             * @see #CLEAR
             * @see #ADDALL
             * @see #REMOVEALL
             * @see #RETAINALL
             * @see #UPDATE
             * @see #REMOVEINDEX
             */
            public byte getType();
            
            /**
             * レコードリストにトランザクションを反映する。<p>
             *
             * @param list レコードリスト
             * @return 更新された場合、1。更新する必要がなかった場合、0。整合性が取れずに、更新できない場合、-1。
             * @exception SharedContextUpdateException 更新の反映に失敗した場合
             */
            public int execute(SharedContextRecordList list) throws SharedContextUpdateException;
            
            public int getIndexDifference();
            public void updateIndexDifference(int diff);
        }
        
        /**
         * 集合トランザクション。<p>
         *
         * @author M.Takata
         */
        public static interface CollectionTransaction extends Transaction{
        }
        
        /**
         * 追加トランザクション。<p>
         *
         * @author M.Takata
         */
        public static class AddTransaction implements Transaction{
            
            private static final long serialVersionUID = 8128732623093350969l;
            
            private int index = -1;
            private Record record;
            public AddTransaction(){
            }
            public AddTransaction(Record record){
                this.record = record;
            }
            public AddTransaction(int index, Record record){
                this.index = index;
                this.record = record;
            }
            public byte getType(){
                return ADD;
            }
            public int execute(SharedContextRecordList list) throws SharedContextUpdateException{
                if(index == -1){
                    list.add(record);
                }else{
                    list.add(index, record);
                }
                return 1;
            }
            
            /**
             * 追加するレコードのインデックスを取得する。<p>
             *
             * @return レコードのインデックス。末尾に追加する場合は、-1
             */
            public int getIndex(){
                return index;
            }
            
            /**
             * 追加するレコードを取得する。<p>
             *
             * @return レコード
             */
            public Record getRecord(){
                return record;
            }
            public int getIndexDifference(){
                return index == -1 ? 0 : index + 1;
            }
            public void updateIndexDifference(int diff){
                if(diff != 0){
                    if(diff > 0){
                        if(index != -1 && index >= (diff - 1)){
                            index++;
                        }
                    }else if(diff < 0){
                        if(index != -1 && index >= (-diff - 1)){
                            index--;
                        }
                    }
                }
            }
            
            public void writeExternal(ObjectOutput out) throws IOException{
                SharedContextRecordList.writeInt(out, index);
                out.writeObject(record);
            }
            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
                index = SharedContextRecordList.readInt(in);
                record = (Record)in.readObject();
            }
        }
        
        /**
         * 差し替えトランザクション。<p>
         *
         * @author M.Takata
         */
        public static class SetTransaction implements Transaction{
            
            private static final long serialVersionUID = -5758065331528848206l;
            
            private int index;
            private Record record;
            public SetTransaction(){
            }
            public SetTransaction(int index, Record record){
                this.index = index;
                this.record = record;
            }
            public byte getType(){
                return SET;
            }
            public int execute(SharedContextRecordList list) throws SharedContextUpdateException{
                list.set(index, record);
                return 1;
            }
            
            /**
             * 差し替えるレコードのインデックスを取得する。<p>
             *
             * @return レコードのインデックス
             */
            public int getIndex(){
                return index;
            }
            
            /**
             * 差し替えるレコードを取得する。<p>
             *
             * @return レコード
             */
            public Record getRecord(){
                return record;
            }
            public int getIndexDifference(){
                return 0;
            }
            public void updateIndexDifference(int diff){
                if(diff != 0){
                    if(diff > 0){
                        if(index != -1 && index >= (diff - 1)){
                            index++;
                        }
                    }else if(diff < 0){
                        if(index != -1 && index >= (-diff - 1)){
                            index--;
                        }
                    }
                }
            }
            
            public void writeExternal(ObjectOutput out) throws IOException{
                SharedContextRecordList.writeInt(out, index);
                out.writeObject(record);
            }
            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
                index = SharedContextRecordList.readInt(in);
                record = (Record)in.readObject();
            }
        }
        
        /**
         * 削除トランザクション。<p>
         *
         * @author M.Takata
         */
        public static class RemoveTransaction implements Transaction{
            
            private static final long serialVersionUID = 1033170205471015665l;
            
            private Object obj;
            private transient int index;
            public RemoveTransaction(){
            }
            public RemoveTransaction(Object obj){
                this.obj = obj;
            }
            public byte getType(){
                return REMOVE;
            }
            public int execute(SharedContextRecordList list) throws SharedContextUpdateException{
                index = list.indexOf(obj);
                list.remove(obj);
                return 1;
            }
            
            /**
             * 削除するレコードを取得する。<p>
             *
             * @return レコード
             */
            public Object getObject(){
                return obj;
            }
            public int getIndexDifference(){
                return -(index + 1);
            }
            public void updateIndexDifference(int diff){
                if(diff != 0){
                    if(diff > 0){
                        if(index != -1 && index >= (diff - 1)){
                            index++;
                        }
                    }else if(diff < 0){
                        if(index != -1 && index >= (-diff - 1)){
                            index--;
                        }
                    }
                }
            }
            
            public void writeExternal(ObjectOutput out) throws IOException{
                out.writeObject(obj);
            }
            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
                obj = in.readObject();
            }
        }
        
        /**
         * インデックス指定削除トランザクション。<p>
         *
         * @author M.Takata
         */
        public static class RemoveIndexTransaction implements Transaction{
            
            private static final long serialVersionUID = 2195335463770024605l;
            
            private int index;
            public RemoveIndexTransaction(){
            }
            public RemoveIndexTransaction(int index){
                this.index = index;
            }
            public byte getType(){
                return REMOVEINDEX;
            }
            public int execute(SharedContextRecordList list) throws SharedContextUpdateException{
                list.remove(index);
                return 1;
            }
            
            /**
             * 削除レコードのインデックスを取得する。<p>
             *
             * @return レコードのインデックス
             */
            public int getIndex(){
                return index;
            }
            public int getIndexDifference(){
                return -(index + 1);
            }
            public void updateIndexDifference(int diff){
                if(diff != 0){
                    if(diff > 0){
                        if(index != -1 && index >= (diff - 1)){
                            index++;
                        }
                    }else if(diff < 0){
                        if(index != -1 && index >= (-diff - 1)){
                            index--;
                        }
                    }
                }
            }
            
            public void writeExternal(ObjectOutput out) throws IOException{
                SharedContextRecordList.writeInt(out, index);
            }
            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
                index = SharedContextRecordList.readInt(in);
            }
        }
        
        /**
         * 全削除トランザクション。<p>
         *
         * @author M.Takata
         */
        public static class ClearTransaction implements CollectionTransaction{
            
            private static final long serialVersionUID = 3750183579398518562l;
            
            public ClearTransaction(){
            }
            public byte getType(){
                return CLEAR;
            }
            public int execute(SharedContextRecordList list) throws SharedContextUpdateException{
                list.clear();
                return 1;
            }
            public int getIndexDifference(){
                return 0;
            }
            public void updateIndexDifference(int diff){
            }
            public void writeExternal(ObjectOutput out) throws IOException{
            }
            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
            }
        }
        
        /**
         * 集合追加トランザクション。<p>
         *
         * @author M.Takata
         */
        public static class AddAllTransaction implements CollectionTransaction{
            
            private static final long serialVersionUID = 3963122806966988635l;
            
            private int index = -1;
            private Collection c;
            public AddAllTransaction(){
            }
            public AddAllTransaction(Collection c){
                this.c = new ArrayList(c);
            }
            public AddAllTransaction(int index, Collection c){
                this.index = index;
                this.c = new ArrayList(c);
            }
            public byte getType(){
                return ADDALL;
            }
            public int execute(SharedContextRecordList list) throws SharedContextUpdateException{
                if(index == -1){
                    list.addAll(c);
                }else{
                    list.addAll(index, c);
                }
                return 1;
            }
            
            /**
             * 追加するレコード集合のインデックスを取得する。<p>
             *
             * @return レコード集合のインデックス。末尾に追加する場合は、-1
             */
            public int getIndex(){
                return index;
            }
            
            /**
             * 追加するレコード集合を取得する。<p>
             *
             * @return レコード集合
             */
            public Collection getRecords(){
                return c;
            }
            
            public int getIndexDifference(){
                return 0;
            }
            public void updateIndexDifference(int diff){
            }
            
            public void writeExternal(ObjectOutput out) throws IOException{
                SharedContextRecordList.writeInt(out, index);
                out.writeObject(c);
            }
            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
                index = SharedContextRecordList.readInt(in);
                c = (Collection)in.readObject();
            }
        }
        
        /**
         * 集合削除トランザクション。<p>
         *
         * @author M.Takata
         */
        public static class RemoveAllTransaction implements CollectionTransaction{
            
            private static final long serialVersionUID = 6369556618192183260l;
            
            private Collection c;
            public RemoveAllTransaction(){
            }
            public RemoveAllTransaction(Collection c){
                this.c = new ArrayList(c);
            }
            public byte getType(){
                return REMOVEALL;
            }
            public int execute(SharedContextRecordList list) throws SharedContextUpdateException{
                list.removeAll(c);
                return 1;
            }
            
            /**
             * 削除するレコード集合を取得する。<p>
             *
             * @return レコード集合
             */
            public Collection getRecords(){
                return c;
            }
            public int getIndexDifference(){
                return 0;
            }
            public void updateIndexDifference(int diff){
            }
            
            public void writeExternal(ObjectOutput out) throws IOException{
                out.writeObject(c);
            }
            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
                c = (Collection)in.readObject();
            }
        }
        
        /**
         * 集合残しトランザクション。<p>
         *
         * @author M.Takata
         */
        public static class RetainAllTransaction implements CollectionTransaction{
            
            private static final long serialVersionUID = -5032406110083319746l;
            
            private Collection c;
            public RetainAllTransaction(){
            }
            public RetainAllTransaction(Collection c){
                this.c = new ArrayList(c);
            }
            public byte getType(){
                return RETAINALL;
            }
            public int execute(SharedContextRecordList list) throws SharedContextUpdateException{
                list.retainAll(c);
                return 1;
            }
            
            /**
             * 残すレコード集合を取得する。<p>
             *
             * @return レコード集合
             */
            public Collection getRecords(){
                return c;
            }
            public int getIndexDifference(){
                return 0;
            }
            public void updateIndexDifference(int diff){
            }
            
            public void writeExternal(ObjectOutput out) throws IOException{
                out.writeObject(c);
            }
            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
                c = (Collection)in.readObject();
            }
        }
        
        /**
         * レコード更新トランザクション。<p>
         *
         * @author M.Takata
         */
        public static class UpdateTransaction implements Transaction{
            
            private static final long serialVersionUID = 490945073091204092l;
            
            private int index;
            private SharedContextRecord.Difference diff;
            public UpdateTransaction(){
            }
            public UpdateTransaction(int index, SharedContextRecord.Difference diff){
                this.index = index;
                this.diff = diff;
            }
            public byte getType(){
                return UPDATE;
            }
            
            /**
             * 更新されたかを判定する。<p>
             *
             * @return 更新された場合は、true
             */
            public boolean isUpdate(){
                return diff.isUpdate();
            }
            
            public int execute(SharedContextRecordList list) throws SharedContextUpdateException{
                SharedContextRecord record = null;
                try{
                    record = (SharedContextRecord)list.getRecord(index);
                }catch(IndexOutOfBoundsException e){
                    throw new SharedContextUpdateException(e);
                }
                return record.update(diff);
            }
            
            /**
             * 更新するレコードのインデックスを取得する。<p>
             *
             * @return レコードのインデックス
             */
            public int getIndex(){
                return index;
            }
            
            /**
             * 更新するレコードの差分情報を取得する。<p>
             *
             * @return レコードの差分情報
             */
            public SharedContextRecord.Difference getRecordDifference(){
                return diff;
            }
            
            public int getIndexDifference(){
                return 0;
            }
            public void updateIndexDifference(int diff){
                if(diff != 0){
                    if(diff > 0){
                        if(index != -1 && index >= (diff - 1)){
                            index++;
                        }
                    }else if(diff < 0){
                        if(index != -1 && index >= (-diff - 1)){
                            index--;
                        }
                    }
                }
            }
            
            public void writeExternal(ObjectOutput out) throws IOException{
                SharedContextRecordList.writeInt(out, index);
                out.writeObject(diff);
            }
            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
                index = SharedContextRecordList.readInt(in);
                diff = (SharedContextRecord.Difference)in.readObject();
            }
        }
    }
}