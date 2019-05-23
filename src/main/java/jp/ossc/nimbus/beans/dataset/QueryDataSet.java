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
     * 空のデータセットを生成する。<p>
     */
    public QueryDataSet(){
        super();
        setRecordListClass(HEADER_QUERY_NAME, HeaderQueryRecordList.class);
        setRecordListClass(RECORD_LIST_QUERY_NAME, RecordListQueryRecordList.class);
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
     * 指定されたデータセットに対して、クエリを実行する。<p>
     *
     * @param ds クエリの実行対象となるデータセット
     */
    public void executeQuery(DataSet ds){
        getHeaderQueryRecordList().executeQuery(ds);
        getRecordListQueryRecordList().executeQuery(ds);
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
            return ':' + NAME + ",java.lang.String"
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
            final Integer fromIndex = getFromIndexProperty();
            final Integer maxSize = getMaxSizeProperty();
            if(fromIndex == null){
                if(maxSize != null && maxSize.intValue() < list.size()){
                    list.subList(maxSize.intValue() + 1, list.size()).clear();
                }
            }else{
                if(fromIndex.intValue() < list.size()){
                    list.subList(0, fromIndex.intValue() - 1).clear();
                    if(maxSize != null && maxSize.intValue() < list.size()){
                        list.subList(maxSize.intValue() + 1, list.size()).clear();
                    }
                }else{
                    list.clear();
                }
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
         *
         * @param name レコード名
         */
        public HeaderQueryRecordList(String name){
            super(name);
            setRecordClass(HeaderQueryRecord.class);
        }
        
        /**
         * 空のレコードリストを生成する。<p>
         *
         * @param name レコード名
         * @param isSynch 同期化する場合true
         */
        public HeaderQueryRecordList(String name, boolean isSynch){
            super(name, isSynch);
            setRecordClass(HeaderQueryRecord.class);
        }
        
        /**
         * 指定されたデータセットのヘッダに対して、クエリを実行する。<p>
         *
         * @param ds クエリの実行対象となるデータセット
         */
        public void executeQuery(DataSet ds){
            if(size() == 0){
                return;
            }
            Set headerNames = null;
            Iterator queries = iterator();
            while(queries.hasNext()){
                HeaderQueryRecord query = (HeaderQueryRecord)queries.next();
                if(headerNames == null){
                    headerNames = new HashSet();
                }
                headerNames.add(query.getNameProperty());
                query.executeQuery(ds);
            }
            if(headerNames != null){
                ds.setSuperficialHeaders((String[])headerNames.toArray(new String[headerNames.size()]));
            }
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
         *
         * @param name レコード名
         */
        public RecordListQueryRecordList(String name){
            super(name);
            setRecordClass(RecordListQueryRecord.class);
        }
        
        /**
         * 空のレコードリストを生成する。<p>
         *
         * @param name レコード名
         * @param isSynch 同期化する場合true
         */
        public RecordListQueryRecordList(String name, boolean isSynch){
            super(name, isSynch);
            setRecordClass(RecordListQueryRecord.class);
        }
        
        /**
         * 指定されたデータセットのレコードリストに対して、クエリを実行する。<p>
         *
         * @param ds クエリの実行対象となるデータセット
         */
        public void executeQuery(DataSet ds){
            if(size() == 0){
                return;
            }
            Set listNames = null;
            Iterator queries = iterator();
            while(queries.hasNext()){
                RecordListQueryRecord query = (RecordListQueryRecord)queries.next();
                if(listNames == null){
                    listNames = new HashSet();
                }
                listNames.add(query.getNameProperty());
                query.executeQuery(ds);
            }
            if(listNames != null){
                ds.setSuperficialRecordLists((String[])listNames.toArray(new String[listNames.size()]));
            }
        }
    }
}