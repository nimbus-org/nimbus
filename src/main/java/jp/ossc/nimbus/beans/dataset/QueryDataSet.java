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
package jp.ossc.nimbus.beans.dataset;

import java.util.*;

/**
 * データセットに対するクエリを指定するレコードリストを持つデータセット。<p>
 *
 * @author M.Takata
 */
public class QueryDataSet extends DataSet{
    
    private static final long serialVersionUID = -3546037042303767928L;
    
    /**
     * ヘッダーに対するクエリを指定するレコードリストの名前。<p>
     */
    public static final String HEADER_QUERY_NAME = "HeaderQuery";
    
    /**
     * レコードリストに対するクエリを指定するレコードリストの名前。<p>
     */
    public static final String RECORD_LIST_QUERY_NAME = "RecordListQuery";
    
    /**
     * ネストしたレコードに対するクエリを指定するレコードリストの名前。<p>
     */
    public static final String NESTED_RECORD_QUERY_NAME = "NestedRecordQuery";
    
    /**
     * ネストしたレコードリストに対するクエリを指定するレコードリストの名前。<p>
     */
    public static final String NESTED_RECORD_LIST_QUERY_NAME = "NestedRecordListQuery";
    
    /**
     * 空のデータセットを生成する。<p>
     */
    public QueryDataSet(){
        super();
        setRecordListClass(HEADER_QUERY_NAME, HeaderQueryRecordList.class);
        setRecordListClass(RECORD_LIST_QUERY_NAME, RecordListQueryRecordList.class);
        setRecordListClass(NESTED_RECORD_QUERY_NAME, NestedRecordQueryRecordList.class);
        setRecordListClass(NESTED_RECORD_LIST_QUERY_NAME, NestedRecordListQueryRecordList.class);
    }
    
    /**
     * 空のデータセットを生成する。<p>
     *
     * @param isSynch 同期化する場合true
     */
    public QueryDataSet(boolean isSynch){
        super(isSynch);
        setRecordListClass(HEADER_QUERY_NAME, HeaderQueryRecordList.class);
        setRecordListClass(RECORD_LIST_QUERY_NAME, RecordListQueryRecordList.class);
        setRecordListClass(NESTED_RECORD_QUERY_NAME, NestedRecordQueryRecordList.class);
        setRecordListClass(NESTED_RECORD_LIST_QUERY_NAME, NestedRecordListQueryRecordList.class);
    }
    
    /**
     * 名前付きのデータセットを生成する。<p>
     *
     * @param name 名前
     */
    public QueryDataSet(String name){
        super(name);
        setRecordListClass(HEADER_QUERY_NAME, HeaderQueryRecordList.class);
        setRecordListClass(RECORD_LIST_QUERY_NAME, RecordListQueryRecordList.class);
        setRecordListClass(NESTED_RECORD_QUERY_NAME, NestedRecordQueryRecordList.class);
        setRecordListClass(NESTED_RECORD_LIST_QUERY_NAME, NestedRecordListQueryRecordList.class);
    }
    
    /**
     * 名前付きのデータセットを生成する。<p>
     *
     * @param name 名前
     * @param isSynch 同期化する場合true
     */
    public QueryDataSet(String name, boolean isSynch){
        super(name, isSynch);
        setRecordListClass(HEADER_QUERY_NAME, HeaderQueryRecordList.class);
        setRecordListClass(RECORD_LIST_QUERY_NAME, RecordListQueryRecordList.class);
        setRecordListClass(NESTED_RECORD_QUERY_NAME, NestedRecordQueryRecordList.class);
        setRecordListClass(NESTED_RECORD_LIST_QUERY_NAME, NestedRecordListQueryRecordList.class);
    }
    
    /**
     * ヘッダーに対するクエリを指定するレコードリストを取得する。<p>
     *
     * @return ヘッダーに対するクエリを指定するレコードリスト
     */
    public HeaderQueryRecordList getHeaderQueryRecordList(){
        return (HeaderQueryRecordList)getRecordList(HEADER_QUERY_NAME);
    }
    
    /**
     * レコードリストに対するクエリを指定するレコードリストを取得する。<p>
     *
     * @return レコードリストに対するクエリを指定するレコードリスト
     */
    public RecordListQueryRecordList getRecordListQueryRecordList(){
        return (RecordListQueryRecordList)getRecordList(RECORD_LIST_QUERY_NAME);
    }
    
    /**
     * ネストしたレコードに対するクエリを指定するレコードリストを取得する。<p>
     *
     * @return ネストしたレコードに対するクエリを指定するレコードリスト
     */
    public NestedRecordQueryRecordList getNestedRecordQueryRecordList(){
        return (NestedRecordQueryRecordList)getRecordList(NESTED_RECORD_QUERY_NAME);
    }
    
    /**
     * ネストしたレコードリストに対するクエリを指定するレコードリストを取得する。<p>
     *
     * @return ネストしたレコードリストに対するクエリを指定するレコードリスト
     */
    public NestedRecordListQueryRecordList getNestedRecordListQueryRecordList(){
        return (NestedRecordListQueryRecordList)getRecordList(NESTED_RECORD_LIST_QUERY_NAME);
    }
    
    /**
     * 指定されたデータセットに対して、クエリを実行する。<p>
     *
     * @param ds クエリの実行対象となるデータセット
     */
    public void executeQuery(DataSet ds){
        getHeaderQueryRecordList().executeQuery(ds);
        getRecordListQueryRecordList().executeQuery(ds);
        getNestedRecordQueryRecordList().executeQuery(ds);
        getNestedRecordListQueryRecordList().executeQuery(ds);
        
        Set nestedRecordNames = new HashSet();
        Set nestedRecordListNames = new HashSet();
        String[] headerNames = ds.getHeaderNames();
        for(int i = 0; i < headerNames.length; i++){
            collectNestedRecordNames(ds, ds.getHeader(headerNames[i]).getRecordSchema(), nestedRecordNames, nestedRecordListNames);
        }
        String[] listNames = ds.getRecordListNames();
        for(int i = 0; i < listNames.length; i++){
            collectNestedRecordNames(ds, ds.getRecordList(listNames[i]).getRecordSchema(), nestedRecordNames, nestedRecordListNames);
        }
        Set targetNestedRecordNames = ds.getNestedRecordSchemaMap().keySet();
        targetNestedRecordNames.removeAll(nestedRecordNames);
        Iterator names = targetNestedRecordNames.iterator();
        while(names.hasNext()){
            ds.setSuperficialNestedRecordSchema((String)names.next(), null);
        }
        Set targetNestedRecordListNames = ds.getNestedRecordListSchemaMap().keySet();
        targetNestedRecordListNames.removeAll(nestedRecordListNames);
        names = targetNestedRecordListNames.iterator();
        while(names.hasNext()){
            ds.setSuperficialNestedRecordListSchema((String)names.next(), null);
        }
    }
    
    protected void collectNestedRecordNames(DataSet ds, RecordSchema schema, Set nestedRecordNames, Set nestedRecordListNames){
        if(schema == null){
            return;
        }
        PropertySchema[] propertySchemata = schema.getPropertySchemata();
        for(int i = 0; i < propertySchemata.length; i++){
            if(propertySchemata[i] instanceof RecordPropertySchema){
                String name = ((RecordPropertySchema)propertySchemata[i]).getRecordName();
                nestedRecordNames.add(name);
                collectNestedRecordNames(ds, ds.getNestedRecordSchema(name), nestedRecordNames, nestedRecordListNames);
            }else if(propertySchemata[i] instanceof RecordListPropertySchema){
                String name = ((RecordListPropertySchema)propertySchemata[i]).getRecordListName();
                nestedRecordListNames.add(name);
                collectNestedRecordNames(ds, ds.getNestedRecordListSchema(name), nestedRecordNames, nestedRecordListNames);
            }
        }
    }
    
    /**
     * クエリを指定してデータを絞り込むレコード。<p>
     *
     * @author M.Takata
     */
    public static abstract class QueryRecord extends Record{
        
        private static final long serialVersionUID = 6511614302904365722L;
        
        /**
         * クエリ対象のデータの名前を指定するプロパティ名。<p>
         */
        public static final String NAME = "name";
        
        /**
         * 出力するプロパティ名の配列を指定するプロパティ名。<p>
         */
        public static final String PROPERTY_NAMES = "propertyNames";
        
        /**
         * 空のインスタンスを生成する。<p>
         */
        public QueryRecord(){
            setSchema(createSchema());
        }
        
        /**
         * このレコードのスキーマ文字列を生成する。<p>
         *
         * @return スキーマ文字列
         */
        protected String createSchema(){
            return ':' + NAME + ",java.lang.String,,,,1"
                 + "\n:" + PROPERTY_NAMES + ",java.lang.String[]";
        }
        
        /**
         * クエリ対象のデータの名前を取得する。<p>
         *
         * @return クエリ対象のデータの名前
         */
        public String getNameProperty(){
            return getStringProperty(NAME);
        }
        
        /**
         * 出力するプロパティ名の配列を取得する。<p>
         *
         * @return 出力するプロパティ名の配列
         */
        public String[] getPropertyNamesProperty(){
            return (String[])getProperty(PROPERTY_NAMES);
        }
        
        /**
         * 指定されたデータセットに対して、クエリを実行する。<p>
         *
         * @param ds クエリの実行対象となるデータセット
         */
        public abstract void executeQuery(DataSet ds);
    }
    
    /**
     * クエリを指定してヘッダを絞り込むレコード。<p>
     *
     * @author M.Takata
     */
    public static class HeaderQueryRecord extends QueryRecord{
        
        private static final long serialVersionUID = 226252055085245480L;
        
        /**
         * 空のインスタンスを生成する。<p>
         */
        public HeaderQueryRecord(){
        }
        
        /**
         * 指定されたデータセットの該当するヘッダに対して、クエリを実行する。<p>
         *
         * @param ds クエリの実行対象となるデータセット
         */
        public void executeQuery(DataSet ds){
            final String name = getNameProperty();
            final Header header = ds.getHeader(name);
            if(header == null){
                return;
            }
            final String[] propertyNames = getPropertyNamesProperty();
            if(propertyNames != null){
                header.setSuperficialProperties(propertyNames);
            }
        }
    }
    
    /**
     * クエリを指定してネストしたレコードを絞り込むレコード。<p>
     *
     * @author M.Takata
     */
    public static class NestedRecordQueryRecord extends QueryRecord{
        
        private static final long serialVersionUID = -5346339769951356900L;
        
        /**
         * 空のインスタンスを生成する。<p>
         */
        public NestedRecordQueryRecord(){
        }
        
        /**
         * 指定されたデータセットの該当するヘッダに対して、クエリを実行する。<p>
         *
         * @param ds クエリの実行対象となるデータセット
         */
        public void executeQuery(DataSet ds){
            final String name = getNameProperty();
            final RecordSchema nestedRecordSchema = ds.getNestedRecordSchema(name);
            if(nestedRecordSchema == null){
                return;
            }
            final String[] propertyNames = getPropertyNamesProperty();
            if(propertyNames != null){
                RecordSchema superficialRecordSchema = nestedRecordSchema.createSuperficialRecordSchema(propertyNames, true);
                ds.setSuperficialNestedRecordSchema(name, superficialRecordSchema);
            }
        }
    }
    
    /**
     * クエリを指定してレコードリストを絞り込むレコード。<p>
     *
     * @author M.Takata
     */
    public static class RecordListQueryRecord extends QueryRecord{
        
        private static final long serialVersionUID = 7380014915156837646L;
        
        public static final String FROM_INDEX = "fromIndex";
        public static final String MAX_SIZE = "maxSize";
        
        /**
         * 空のインスタンスを生成する。<p>
         */
        public RecordListQueryRecord(){
        }
        
        protected String createSchema(){
            return super.createSchema()
                 + "\n:" + FROM_INDEX + ",java.lang.Integer"
                 + "\n:" + MAX_SIZE + ",java.lang.Integer";
        }
        
        /**
         * レコードリストの件数を絞り込むための、開始インデックスを取得する。<p>
         *
         * @return 開始インデックス
         */
        public Integer getFromIndexProperty(){
            return (Integer)getProperty(FROM_INDEX);
        }
        
        /**
         * レコードリストの件数を絞り込むための、最大件数を取得する。<p>
         *
         * @return 最大件数
         */
        public Integer getMaxSizeProperty(){
            return (Integer)getProperty(MAX_SIZE);
        }
        
        /**
         * 指定された配列を、開始インデックスと最大件数で絞り込む。<p>
         *
         * @param array 配列
         * @return 絞り込んだ配列
         */
        public Object[] shrinkArray(Object[] array){
            if(array == null || array.length == 0){
                return array;
            }
            final Integer fromIndex = getFromIndexProperty();
            final Integer maxSize = getMaxSizeProperty();
            if(fromIndex == null || fromIndex.intValue() <= 0){
                if(maxSize != null && maxSize.intValue() >= 0 && maxSize.intValue() < array.length){
                    Object[] newArray = new Object[maxSize.intValue()];
                    System.arraycopy(array, 0, newArray, 0, newArray.length);
                    return newArray;
                }else{
                    return array;
                }
            }else{
                if(fromIndex.intValue() < array.length){
                    if(maxSize != null && maxSize.intValue() >= 0 && maxSize.intValue() < array.length){
                        Object[] newArray = new Object[maxSize.intValue()];
                        System.arraycopy(array, fromIndex.intValue(), newArray, 0, newArray.length);
                        return newArray;
                    }else{
                        Object[] newArray = new Object[array.length - fromIndex.intValue()];
                        System.arraycopy(array, fromIndex.intValue(), newArray, 0, newArray.length);
                        return newArray;
                    }
                }else{
                    return new Object[0];
                }
            }
        }
        
        /**
         * 指定されたリストを、開始インデックスと最大件数で絞り込む。<p>
         *
         * @param list リスト
         */
        public void shrinkList(List list){
            if(list == null){
                return;
            }
            final Integer fromIndex = getFromIndexProperty();
            final Integer maxSize = getMaxSizeProperty();
            if(fromIndex == null || fromIndex.intValue() <= 0){
                if(maxSize != null && maxSize.intValue() >= 0 && maxSize.intValue() < list.size()){
                    list.subList(maxSize.intValue(), list.size()).clear();
                }
            }else{
                if(fromIndex.intValue() < list.size()){
                    list.subList(0, fromIndex.intValue()).clear();
                    if(maxSize != null && maxSize.intValue() >= 0 && maxSize.intValue() < list.size()){
                        list.subList(maxSize.intValue(), list.size()).clear();
                    }
                }else{
                    list.clear();
                }
            }
        }
        
        /**
         * 指定されたデータセットの該当するレコードリストに対して、クエリを実行する。<p>
         *
         * @param ds クエリの実行対象となるデータセット
         */
        public void executeQuery(DataSet ds){
            final String name = getNameProperty();
            final RecordList list = ds.getRecordList(name);
            if(list == null){
                return;
            }
            final String[] propertyNames = getPropertyNamesProperty();
            if(propertyNames != null){
                list.setSuperficialProperties(propertyNames);
            }
            shrinkList(list);
        }
    }
    
    /**
     * クエリを指定してネストしたレコードリストを絞り込むレコード。<p>
     *
     * @author M.Takata
     */
    public static class NestedRecordListQueryRecord extends QueryRecord{
        
        private static final long serialVersionUID = -854867250438040322L;
        
        /**
         * 空のインスタンスを生成する。<p>
         */
        public NestedRecordListQueryRecord(){
        }
        
        /**
         * 指定されたデータセットの該当するレコードリストに対して、クエリを実行する。<p>
         *
         * @param ds クエリの実行対象となるデータセット
         */
        public void executeQuery(DataSet ds){
            final String name = getNameProperty();
            final RecordSchema nestedRecordListSchema = ds.getNestedRecordListSchema(name);
            if(nestedRecordListSchema == null){
                return;
            }
            final String[] propertyNames = getPropertyNamesProperty();
            if(propertyNames != null){
                RecordSchema superficialRecordSchema = nestedRecordListSchema.createSuperficialRecordSchema(propertyNames, true);
                ds.setSuperficialNestedRecordListSchema(name, superficialRecordSchema);
            }
        }
    }
    
    /**
     * {@link QueryRecord}のレコードリスト。<p>
     *
     * @author M.Takata
     */
    public static abstract class QueryRecordList extends RecordList{
        
        private static final long serialVersionUID = -7927096100456215102L;
        
        /**
         * 空のレコードリストを生成する。<p>
         */
        public QueryRecordList(){
            super();
        }
        
        /**
         * 空のレコードリストを生成する。<p>
         *
         * @param name レコード名
         */
        public QueryRecordList(String name){
            super(name);
        }
        
        /**
         * 空のレコードリストを生成する。<p>
         *
         * @param name レコード名
         * @param isSynch 同期化する場合true
         */
        public QueryRecordList(String name, boolean isSynch){
            super(name, isSynch);
        }
        
        /**
         * 指定されたデータセットに対して、クエリを実行する。<p>
         *
         * @param ds クエリの実行対象となるデータセット
         */
        public abstract void executeQuery(DataSet ds);
        
        /**
         * 指定された名前の{@link QueryRecord}を取得する。<p>
         *
         * @param name QueryRecordの名前
         * @return 指定された名前のQueryRecord。存在しない場合は、null
         */
        public QueryRecord getQueryRecord(String name){
            Record key = createRecord();
            key.setProperty(QueryRecord.NAME, name);
            return (QueryRecord)searchByPrimaryKey(key);
        }
    }
    
    /**
     * {@link HeaderQueryRecord}のレコードリスト。<p>
     *
     * @author M.Takata
     */
    public static class HeaderQueryRecordList extends QueryRecordList{
        
        private static final long serialVersionUID = 4155067348408070025L;
        
        /**
         * 空のレコードリストを生成する。<p>
         */
        public HeaderQueryRecordList(){
            super();
            setRecordClass(HeaderQueryRecord.class);
        }
        
        /**
         * 空のレコードリストを生成する。<p>
         *
         * @param name レコードリスト名
         */
        public HeaderQueryRecordList(String name){
            super(name);
            setRecordClass(HeaderQueryRecord.class);
        }
        
        /**
         * 空のレコードリストを生成する。<p>
         *
         * @param name レコードリスト名
         * @param isSynch 同期化する場合true
         */
        public HeaderQueryRecordList(String name, boolean isSynch){
            super(name, isSynch);
            setRecordClass(HeaderQueryRecord.class);
        }
        
        /**
         * 指定されたデータセットのヘッダに対して、クエリを実行する。<p>
         * データセットの表層的なヘッダ名の設定と、ヘッダの表層的なスキーマの設定を行う。
         *
         * @param ds クエリの実行対象となるデータセット
         */
        public void executeQuery(DataSet ds){
            Set headerNames = null;
            if(size() != 0){
                Iterator queries = iterator();
                while(queries.hasNext()){
                    HeaderQueryRecord query = (HeaderQueryRecord)queries.next();
                    if(headerNames == null){
                        headerNames = new HashSet();
                    }
                    headerNames.add(query.getNameProperty());
                    query.executeQuery(ds);
                }
            }
            ds.setSuperficialHeaders(headerNames != null ? (String[])headerNames.toArray(new String[headerNames.size()]) : new String[0]);
        }
    }
    
    /**
     * {@link RecordListQueryRecord}のレコードリスト。<p>
     *
     * @author M.Takata
     */
    public static class RecordListQueryRecordList extends QueryRecordList{
        
      private static final long serialVersionUID = 1734087472138599124L;
        
        /**
         * 空のレコードリストを生成する。<p>
         */
        public RecordListQueryRecordList(){
            super();
            setRecordClass(RecordListQueryRecord.class);
        }
        
        /**
         * 空のレコードリストを生成する。<p>
         *
         * @param name レコードリスト名
         */
        public RecordListQueryRecordList(String name){
            super(name);
            setRecordClass(RecordListQueryRecord.class);
        }
        
        /**
         * 空のレコードリストを生成する。<p>
         *
         * @param name レコードリスト名
         * @param isSynch 同期化する場合true
         */
        public RecordListQueryRecordList(String name, boolean isSynch){
            super(name, isSynch);
            setRecordClass(RecordListQueryRecord.class);
        }
        
        /**
         * 指定されたデータセットのレコードリストに対して、クエリを実行する。<p>
         * データセットの表層的なレコードリスト名の設定と、レコードリストの表層的なスキーマの設定、件数の絞り込みを行う。<br>
         * レコードリストの件数の絞り込みは、レコードリストにデータを追加した後に、このメソッドを実行した場合のみ有効。<br>
         *
         * @param ds クエリの実行対象となるデータセット
         */
        public void executeQuery(DataSet ds){
            Set listNames = null;
            if(size() != 0){
                Iterator queries = iterator();
                while(queries.hasNext()){
                    RecordListQueryRecord query = (RecordListQueryRecord)queries.next();
                    if(listNames == null){
                        listNames = new HashSet();
                    }
                    listNames.add(query.getNameProperty());
                    query.executeQuery(ds);
                }
            }
            ds.setSuperficialRecordLists(listNames != null ? (String[])listNames.toArray(new String[listNames.size()]) : new String[0]);
        }
    }
    
    /**
     * {@link NestedRecordQueryRecord}のレコードリスト。<p>
     *
     * @author M.Takata
     */
    public static class NestedRecordQueryRecordList extends QueryRecordList{
        
        private static final long serialVersionUID = -6470555870853893000L;
        
        /**
         * 空のレコードリストを生成する。<p>
         */
        public NestedRecordQueryRecordList(){
            super();
            setRecordClass(NestedRecordQueryRecord.class);
        }
        
        /**
         * 空のレコードリストを生成する。<p>
         *
         * @param name レコードリスト名
         */
        public NestedRecordQueryRecordList(String name){
            super(name);
            setRecordClass(NestedRecordQueryRecord.class);
        }
        
        /**
         * 空のレコードリストを生成する。<p>
         *
         * @param name レコードリスト名
         * @param isSynch 同期化する場合true
         */
        public NestedRecordQueryRecordList(String name, boolean isSynch){
            super(name, isSynch);
            setRecordClass(NestedRecordQueryRecord.class);
        }
        
        /**
         * 指定されたデータセットのネスとしたレコードに対して、クエリを実行する。<p>
         * ネスとしたレコードに、表層的なスキーマを設定する。但し、ネストしたレコードを生成する前に、このメソッドを呼び出した場合のみ有効。<br>
         *
         * @param ds クエリの実行対象となるデータセット
         */
        public void executeQuery(DataSet ds){
            if(size() == 0){
                return;
            }
            Iterator queries = iterator();
            while(queries.hasNext()){
                NestedRecordQueryRecord query = (NestedRecordQueryRecord)queries.next();
                query.executeQuery(ds);
            }
        }
    }
    
    /**
     * {@link NestedRecordListQueryRecord}のレコードリスト。<p>
     *
     * @author M.Takata
     */
    public static class NestedRecordListQueryRecordList extends QueryRecordList{
        
        private static final long serialVersionUID = -2512099138542793919L;
        
        /**
         * 空のレコードリストを生成する。<p>
         */
        public NestedRecordListQueryRecordList(){
            super();
            setRecordClass(NestedRecordListQueryRecord.class);
        }
        
        /**
         * 空のレコードリストを生成する。<p>
         *
         * @param name レコードリスト名
         */
        public NestedRecordListQueryRecordList(String name){
            super(name);
            setRecordClass(NestedRecordListQueryRecord.class);
        }
        
        /**
         * 空のレコードリストを生成する。<p>
         *
         * @param name レコードリスト名
         * @param isSynch 同期化する場合true
         */
        public NestedRecordListQueryRecordList(String name, boolean isSynch){
            super(name, isSynch);
            setRecordClass(NestedRecordListQueryRecord.class);
        }
        
        /**
         * 指定されたデータセットのネスとしたレコードリストに対して、クエリを実行する。<p>
         * ネスとしたレコードリストに、表層的なスキーマを設定する。但し、ネストしたレコードリストを生成する前に、このメソッドを呼び出した場合のみ有効。<br>
         *
         * @param ds クエリの実行対象となるデータセット
         */
        public void executeQuery(DataSet ds){
            if(size() == 0){
                return;
            }
            Iterator queries = iterator();
            while(queries.hasNext()){
                NestedRecordListQueryRecord query = (NestedRecordListQueryRecord)queries.next();
                query.executeQuery(ds);
            }
        }
    }
}