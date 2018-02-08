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
// パッケージ
package jp.ossc.nimbus.recset;
//インポート
import java.util.*;
import java.sql.*;
import java.io.*;

import org.apache.commons.jexl.*;

import jp.ossc.nimbus.service.crypt.Crypt;
import jp.ossc.nimbus.service.log.*;
import jp.ossc.nimbus.service.codemaster.PartUpdate;
import jp.ossc.nimbus.service.codemaster.PartUpdateRecords;
import jp.ossc.nimbus.service.codemaster.CodeMasterUpdateKey;

/**
 * レコード管理クラス。<p>
 * データベースレコードの管理を行う。
 * 
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class RecordSet implements Serializable, PartUpdate, Cloneable{
    
    private static final long serialVersionUID = -7457366126244404177L;
    
    /**
     * スキーマ定義に用いる改行文字。<p>
     */
    public  static final String C_SEPARATOR = System.getProperty("line.separator");
    
    /** SQL ステートメント文字列定数 */
    private static final String C_SET_TOKEN = " SET ";
    private static final String C_UPDATE_TOKEN = "UPDATE ";
    private static final String C_QUESTION_TOKEN = "?";
    private static final String C_EQUAL_TOKEN = "=";
    private static final String C_AND_TOKEN = " AND ";
    private static final String C_OR_TOKEN = " OR ";
    private static final String C_WHERE_TOKEN = " WHERE ";
    private static final String C_VALUES_TOKEN = " VALUES ";
    private static final String C_BRACKETS_END_TOKEN = " ) ";
    private static final String C_BRACKETS_BEGIN_TOKEN = " ( ";
    private static final String C_DELETE_TOKEN = "DELETE FROM ";
    private static final String C_INSERT_TOKEN = "INSERT INTO ";
    private static final String C_ORDER_TOKEN = " ORDER BY ";
    private static final String C_FROM_TOKEN = " FROM ";
    private static final String C_COMMA_TOKEN = ",";
    private static final String C_SELECT_TOKEN = "SELECT ";
    private static final String C_DISTINCT_TOKEN = " DISTINCT ";
    private static final String C_BLANK_TOKEN = " ";
    
    /** 暗号化サービス */
    protected Crypt mCrypt;
    
    
    /** 行スキーマ */
    protected RowSchema mSchema;
    
    /** 行データのリスト */
    protected ArrayList mRows;
    
    /** 行データをキーで管理しているHashMap*/
    protected HashMap mHash;
    
    /** テーブル名文字列 */
    protected String mTableNames;
    
    /** テーブル名文字列 */
    protected String mUpdateTableNames;
    
    /** ソート文字列 */
    protected String mOrder;
    
    /** WEHRE句文字列 */
    protected StringBuffer where;
    
    /** PreparedStatementに埋め込むデータを保持するリスト */
    protected List bindDatas;
    
    /** コネクション */
    protected transient Connection mCon ;
    
    /** ロガーオブジェクト */
    protected Logger mLogger ;
    
    /** 実行SQLをログ出力するためのログメッセージコード */
    protected String mMessageCode ;
    
    /**
     * DISTINCT指定フラグ。<p>
     * 初期値はfalse
     */
    protected boolean mDistinctFlg = false;
    
    /**
     * 更新及び削除時に、更新及び削除しようとした件数と実際に更新及び削除した件数が等しいかどうかの整合性をチェックするかどうかのフラグ。<p>
     * デフォルト、true。<br>
     */
    protected boolean isEnabledRowVersionCheck = true;
    
    /**
     * 動的条件検索を管理するマップ。<p>
     */
    protected Map dynamicSearchConditionMap;
    
    /**
     * 動的条件検索結果を保持するマップ。<p>
     */
    protected Map dynamicSearchConditionResultMap;
    
    /**
     * 動的キー検索を管理するマップ。<p>
     */
    protected Map dynamicSearchKeyMap;
    
    /**
     * 動的キー検索結果を保持するマップ。<p>
     */
    protected Map dynamicSearchMap;
    
    /**
     * バッチ実行するかどうかのフラグ。<p>
     * デフォルトは、true。
     */
    protected boolean isBatchExecute = true;
    
    protected int[] partUpdateOrderBy;
    
    protected boolean[] partUpdateIsAsc;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public RecordSet(){
        mRows = new ArrayList();
        mHash = new HashMap();
    }
    
    /**
     * ログを設定する。<p>
     * デフォルトは、nullで、ログ出力されない。<br>
     * 
     * @param lg ログ
     */
    public void setLogger(Logger lg){
        mLogger = lg;
    }
    
    /**
     * 実行SQLをログ出力するためのメッセージコードを設定する。<p>
     * デフォルトは、nullで、ログ出力されない。<br>
     *
     * @param code メッセージコード
     */
    public void setMessageCode(String code){
        mMessageCode = code;
    }
    
    /**
     * 更新及び削除時に、更新及び削除しようとした件数と、実際に更新及び削除した件数が等しいかどうかの整合性をチェックするかどうかを設定する。<p>
     * デフォルト、true。<br>
     * trueを設定されている場合は、{@link #updateRecords()}を呼び出した時にチェックにひっかかると、{@link RowVersionException}がthrowされる。<br>
     *
     * @param isEnabled チェックする場合は、true
     */
    public void setEnabledRowVersionCheck(boolean isEnabled){
        isEnabledRowVersionCheck = isEnabled;
    }
    
    /**
     * 更新及び削除時に、更新及び削除しようとした件数と、実際に更新及び削除した件数が等しいかどうかの整合性をチェックするかどうかを判定する。<p>
     *
     * @return trueの場合は、チェックする
     */
    public boolean isEnabledRowVersionCheck(){
        return isEnabledRowVersionCheck;
    }
    
    /**
     * スキーマの初期化処理を行う。<p>
     * スキーマ文字列は、<br>
     * 列名,型,長さ,レコード種別,暗号化フラグ<br>
     * を改行コードで区切った文字列とする。<br>
     * 
     * @param schema スキーマ文字列
     */
    public void initSchema(String schema){
        mSchema = SchemaManager.findRowSchema(schema);
    }
    
    /**
     * スキーマの初期化処理を行う。<p>
     * フィールドスキーマ文字列は、<br>
     * 列名,型,長さ,レコード種別,暗号化フラグ<br>
     * とし、その配列を指定する。<br>
     * 
     * @param filedSchemata フィールドスキーマ文字列の配列
     */
    public void initFieldSchemata(String[] filedSchemata){
        StringBuilder buf = new StringBuilder();
        for(int i = 0; i < filedSchemata.length; i++){
            buf.append(filedSchemata[i]);
            if(i != filedSchemata.length - 1){
                buf.append(C_SEPARATOR);
            }
        }
        initSchema(buf.toString());
    }
    
    /**
     * スキーマ情報を取得する。<p>
     *
     * @return スキーマ情報
     */
    public RowSchema getRowSchema(){
        return mSchema;
    }
    
    /**
     * 検索するテーブル名を設定する。<p>
     * SELECT時の対象テーブルとなる。<br>
     * 
     * @param tableStr １つまたはカンマで区切られた複数のテーブル名
     */
    public void setFromTable(String tableStr){
        mTableNames = tableStr;
    }
    
    /**
     * 更新するテーブル名を設定する。<p>
     * INSERT、DELETE、UPDATE時の対象テーブルとなる。<br>
     * 指定しない場合のINSERT、DELETE、UPDATE時の対象テーブルは、{@link #setFromTable(String)}で指定されたテーブルとみなす。<br>
     * 
     * @param tableStr １つまたはカンマで区切られた複数のテーブル名
     */
    public void setUpdateTable(String tableStr){
        mUpdateTableNames = tableStr;
    }
    
    /**
     * ORDER BY句を設定する。<p>
     * 
     * @param order １つまたはカンマで区切られた複数の列名
     */
    public void setOrderbyStr(String order){
        mOrder = order;
    }
    
    /**
     * JDBCコネクションを設定する。<p>
     * 
     * @param con JDBCコネクション
     */
    public void setConnection(Connection con){
        mCon = con;
    }
    
    /**
     * JDBCコネクションを取得する。<p>
     * 
     * @return JDBCコネクション
     */
    public Connection getConnection(){
        return mCon;
    }
    
    /**
     * SELECT時、DISTINCT指定するかどうかを設定する。<p>
     * 
     * @param flg DISTINCT指定する場合は、true
     */
    public void setDistinctFlg(boolean flg){
        mDistinctFlg = flg;
    }
    
    /**
     * 暗号化オブジェクトを設定する。<p>
     * 
     * @param crypt 暗号化オブジェクト
     */
    public void setCrypt(Crypt crypt){
        mCrypt = crypt;
    }
    
    /**
     * 暗号化オブジェクトを取得する。<p>
     * 
     * @return 暗号化オブジェクト
     */
    public Crypt getCrypt(){
        return mCrypt;
    }
    
    /**
     * バッチ実行するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isBatch バッチ実行する場合は、true
     */
    public void setBatchExecute(boolean isBatch){
        isBatchExecute = isBatch;
    }
    
    /**
     * バッチ実行するかどうかを判定する。<p>
     *
     * @return trueの場合、バッチ実行する
     */
    public boolean isBatchExecute(){
        return isBatchExecute;
    }
    
    /**
     * 列名配列から列インデックス配列に変換する。<p>
     *
     * @param colNames 列名配列
     * @return 列インデックス配列
     */
    private int[] convertFromColNamesToColIndexes(String[] colNames){
        if(colNames == null || colNames.length == 0){
            return null;
        }
        if(mSchema == null){
            throw new InvalidDataException("Schema not initalize.");
        }
        final int[] colIndexes = new int[colNames.length];
        for(int i = 0; i < colNames.length; i++){
            final FieldSchema field = mSchema.get(colNames[i]);
            if(field == null){
                throw new IllegalArgumentException("Field not found : " + colNames[i]);
            }
            colIndexes[i] = field.getIndex();
        }
        return colIndexes;
    }
    
    /**
     * 動的条件検索条件を設定する。<p>
     * {@link #setDynamicSearchCondition(String, String, int[], boolean[]) setDynamicSearchCondition(null, condition, (int[])null, null)}を呼び出すのと同じ。<br>
     * 複数の動的条件検索条件を設定したい場合は、{@link #setDynamicSearchCondition(String, String)}で、条件名を指定して、条件を設定する。<br>
     *
     * @param condition 条件式
     * @exception Exception 条件式が不正な場合
     * @see #setDynamicSearchCondition(String, String, int[], boolean[])
     */
    public void setDynamicSearchCondition(String condition) throws Exception{
        setDynamicSearchCondition(null, condition);
    }
    
    /**
     * 動的条件検索条件（ソート列指定付き）を設定する。<p>
     * {@link #setDynamicSearchCondition(String, String, String[], boolean[]) setDynamicSearchCondition(null, condition, orderBy, null)}を呼び出すのと同じ。<br>
     * 複数の動的条件検索条件を設定したい場合は、{@link #setDynamicSearchCondition(String, String, String[])}で、条件名を指定して、条件を設定する。<br>
     *
     * @param condition 条件式
     * @param orderBy ソート列名配列
     * @exception Exception 条件式が不正な場合
     * @see #setDynamicSearchCondition(String, String, String[], boolean[])
     */
    public void setDynamicSearchCondition(String condition, String[] orderBy) throws Exception{
        setDynamicSearchCondition(null, condition, orderBy);
    }
    
    /**
     * 動的条件検索条件（ソート列指定付き）を設定する。<p>
     * {@link #setDynamicSearchCondition(String, String, int[], boolean[]) setDynamicSearchCondition(null, condition, orderBy, null)}を呼び出すのと同じ。<br>
     * 複数の動的条件検索条件を設定したい場合は、{@link #setDynamicSearchCondition(String, String, int[])}で、条件名を指定して、条件を設定する。<br>
     *
     * @param condition 条件式
     * @param orderBy ソート列インデックス配列
     * @exception Exception 条件式が不正な場合
     * @see #setDynamicSearchCondition(String, String, int[], boolean[])
     */
    public void setDynamicSearchCondition(String condition, int[] orderBy) throws Exception{
        setDynamicSearchCondition(null, condition, orderBy);
    }
    
    /**
     * 動的条件検索条件（ソート列指定、ソート順指定付き）を設定する。<p>
     * {@link #setDynamicSearchCondition(String, String, String[], boolean[]) setDynamicSearchCondition(null, condition, orderBy, isAsc)}を呼び出すのと同じ。<br>
     * 複数の動的条件検索条件を設定したい場合は、{@link #setDynamicSearchCondition(String, String, String[], boolean[])}で、条件名を指定して、条件を設定する。<br>
     *
     * @param condition 条件式
     * @param orderBy ソート列名配列
     * @param isAsc ソート順。trueの場合、昇順
     * @exception Exception 条件式が不正な場合
     * @see #setDynamicSearchCondition(String, String, String[], boolean[])
     */
    public void setDynamicSearchCondition(String condition, String[] orderBy, boolean[] isAsc) throws Exception{
        setDynamicSearchCondition(null, condition, orderBy, isAsc);
    }
    
    /**
     * 動的条件検索条件（ソート列指定、ソート順指定付き）を設定する。<p>
     * {@link #setDynamicSearchCondition(String, String, int[], boolean[]) setDynamicSearchCondition(null, condition, orderBy, isAsc)}を呼び出すのと同じ。<br>
     * 複数の動的条件検索条件を設定したい場合は、{@link #setDynamicSearchCondition(String, String, int[], boolean[])}で、条件名を指定して、条件を設定する。<br>
     *
     * @param condition 条件式
     * @param orderBy ソート列インデックス配列
     * @param isAsc ソート順。trueの場合、昇順
     * @exception Exception 条件式が不正な場合
     * @see #setDynamicSearchCondition(String, String, int[], boolean[])
     */
    public void setDynamicSearchCondition(String condition, int[] orderBy, boolean[] isAsc) throws Exception{
        setDynamicSearchCondition(null, condition, orderBy, isAsc);
    }
    
    /**
     * 条件名を指定して、動的条件検索条件を設定する。<p>
     * {@link #setDynamicSearchCondition(String, String, int[], boolean[]) setDynamicSearchCondition(name, condition, (int[])null, null)}を呼び出すのと同じ。<br>
     *
     * @param name 条件名
     * @param condition 条件式
     * @exception Exception 条件式が不正な場合
     * @see #setDynamicSearchCondition(String, String, int[], boolean[])
     */
    public void setDynamicSearchCondition(String name, String condition)
     throws Exception{
        setDynamicSearchCondition(name, condition, (int[])null, null);
    }
    
    /**
     * 条件名を指定して、動的条件検索条件（ソート列指定付き）を設定する。<p>
     * {@link #setDynamicSearchCondition(String, String, String[], boolean[]) setDynamicSearchCondition(name, condition, orderBy, null)}を呼び出すのと同じ。<br>
     *
     * @param name 条件名
     * @param condition 条件式
     * @param orderBy ソート列名配列
     * @exception Exception 条件式が不正な場合
     * @see #setDynamicSearchCondition(String, String, String[], boolean[])
     */
    public void setDynamicSearchCondition(String name, String condition, String[] orderBy)
     throws Exception{
        setDynamicSearchCondition(
            name,
            condition,
            convertFromColNamesToColIndexes(orderBy),
            null
        );
    }
    
    /**
     * 条件名を指定して、動的条件検索条件（ソート列指定付き）を設定する。<p>
     * {@link #setDynamicSearchCondition(String, String, int[], boolean[]) setDynamicSearchCondition(name, condition, orderBy, null)}を呼び出すのと同じ。<br>
     *
     * @param name 条件名
     * @param condition 条件式
     * @param orderBy ソート列インデックス配列
     * @exception Exception 条件式が不正な場合
     * @see #setDynamicSearchCondition(String, String, int[], boolean[])
     */
    public void setDynamicSearchCondition(String name, String condition, int[] orderBy)
     throws Exception{
        setDynamicSearchCondition(
            name,
            condition,
            orderBy,
            null
        );
    }
    
    /**
     * 条件名を指定して、動的条件検索条件（ソート列指定、ソート順指定付き）を設定する。<p>
     *
     * @param name 条件名
     * @param condition 条件式
     * @param orderBy ソート列名配列
     * @param isAsc ソート順。trueの場合、昇順
     * @exception Exception 条件式が不正な場合
     * @see #setDynamicSearchCondition(String, String, int[], boolean[])
     */
    public void setDynamicSearchCondition(String name, String condition, String[] orderBy, boolean[] isAsc)
     throws Exception{
        setDynamicSearchCondition(
            name,
            condition,
            convertFromColNamesToColIndexes(orderBy),
            isAsc
        );
    }
    
    /**
     * 条件名を指定して、動的条件検索条件（ソート列指定、ソート順指定付き）を設定する。<p>
     * 動的条件検索とは、RecordSetに蓄積されたレコードから、条件式に合致するレコードを検索する機能である。<br>
     * また、動的条件検索には、レコードを蓄積する際に検索する蓄積型検索と、蓄積されたレコードからリアルに検索するリアル型検索がある。<br>
     * このセッターは、蓄積型検索のための条件設定を行うもので、レコードを蓄積する前に設定しておかなければならない。<br>
     * 蓄積型検索の利点は、蓄積時に同時に検索が行われるため、実際の検索時には、あらかじめ検索された結果を取り出すだけであるため、高速な検索が可能になる事である。但し、蓄積時に検索を行うので、蓄積と検索を同時に行う場合は、その効果はない。<br>
     * 逆に欠点は、条件をあらかじめ設定する必要があるため、条件が動的に変わる場合は、対応できない。そのような場合は、リアル型検索({@link #searchDynamicConditionReal(String, int[], boolean[], Map)})を使用する。<br>
     * このセッターに対応する蓄積型検索は、{@link #searchDynamicCondition(String)}で行う。<br>
     * <p>
     * 条件式は、<a href="http://jakarta.apache.org/commons/jexl/">Jakarta Commons Jexl</a>の式言語を使用する。<br>
     * 蓄積型検索では、レコードの列の値を、列名を指定する事で、式中で参照する事ができる。<br>
     * <pre>
     *  例：A == '1' and B &gt;= 3
     * </pre>
     *
     * @param name 条件名
     * @param condition 条件式
     * @param orderBy ソート列インデックス配列
     * @param isAsc ソート順。trueの場合、昇順
     * @exception Exception 条件式が不正な場合
     * @see #searchDynamicCondition(String)
     */
    public void setDynamicSearchCondition(String name, String condition, int[] orderBy, boolean[] isAsc)
     throws Exception{
        
        if(dynamicSearchConditionMap == null){
            dynamicSearchConditionMap = new HashMap();
        }
        
        if(mSchema == null){
            throw new InvalidDataException("Schema not initalize.");
        }
        
        final Expression exp = ExpressionFactory.createExpression(condition);
        final JexlContext context = JexlHelper.createContext();
        for(int i = 0, imax = mSchema.size(); i < imax; i++){
            final FieldSchema field = mSchema.get(i);
            final String fieldName = field.getFieldName();
            final int fieldType = field.getFieldType();
            Object val = null;
            switch(fieldType){
            case FieldSchema.C_TYPE_INT:
                val = new Integer(0);
                break;
            case FieldSchema.C_TYPE_LONG:
                val = new Long(0);
                break;
            case FieldSchema.C_TYPE_STRING:
            case FieldSchema.C_TYPE_CHAR:
                val = new String();
                break;
            case FieldSchema.C_TYPE_DATE:
            case FieldSchema.C_TYPE_TIMESTAMP:
                val = new java.util.Date();
                break;
            case FieldSchema.C_TYPE_FLOAT:
                val = new Float(0);
                break;
            case FieldSchema.C_TYPE_DOUBLE:
                val = new Double(0);
                break;
            case FieldSchema.C_TYPE_BLOB:
                val = new byte[0];
                break;
            case FieldSchema.C_TYPE_CLOB:
                val = new char[0];
                break;
            default:
            }
            context.getVars().put(fieldName, val);
        }
        Object ret = exp.evaluate(context);
        if(!(ret instanceof Boolean)){
            throw new IllegalArgumentException(
                "Condition is not boolean. condition=" + condition
                    + ", return=" + ret
            );
        }
        dynamicSearchConditionMap.put(
            name,
            exp
        );
        
        if(dynamicSearchConditionResultMap == null){
            dynamicSearchConditionResultMap = new HashMap();
        }
        dynamicSearchConditionResultMap.put(
            name,
            createOrderByMap(orderBy, isAsc)
        );
    }
    
    /**
     * ソート付きマップを生成する。<p>
     * 
     * @param orderBy ソート列インデックス配列
     * @param isAsc ソート順。trueの場合、昇順
     * @return ソート付きマップ
     */
    private Map createOrderByMap(int[] orderBy, boolean[] isAsc){
        final Comparator comp = createOrderByComparator(orderBy, isAsc);
        if(comp == null){
            return new LinkedHashMap();
        }else{
            return new TreeMap(comp);
        }
    }
    
    /**
     * ソート用のComparatorを生成する。<p>
     * 
     * @param orderBy ソート列インデックス配列
     * @param isAsc ソート順。trueの場合、昇順
     * @return ソート用のComparator
     */
    private Comparator createOrderByComparator(int[] orderBy, boolean[] isAsc){
        Comparator comp = null;
        if(orderBy != null && orderBy.length != 0){
            comp = new RowDataComparator(mSchema, orderBy, isAsc);
        }
        return comp;
    }

    /**
     * 動的キー検索条件を設定する。<p>
     * {@link #setDynamicSearchKey(String, String[]) setDynamicSearchKey(null, colNames)}を呼び出すのと同じ。<br>
     * 複数の動的キー検索条件を設定したい場合は、{@link #setDynamicSearchKey(String, String[])}で、条件名を指定して、条件を設定する。<br>
     *
     * @param colNames 列名配列
     * @see #setDynamicSearchKey(String, String[])
     */
    public void setDynamicSearchKey(String[] colNames){
        setDynamicSearchKey(null, colNames);
    }
    
    /**
     * 動的キー検索条件を設定する。<p>
     * {@link #setDynamicSearchKey(String, int[]) setDynamicSearchKey(null, colNames)}を呼び出すのと同じ。<br>
     * 複数の動的キー検索条件を設定したい場合は、{@link #setDynamicSearchKey(String, int[])}で、条件名を指定して、条件を設定する。<br>
     *
     * @param colIndexes 列インデックス配列
     * @see #setDynamicSearchKey(String, int[])
     */
    public void setDynamicSearchKey(int[] colIndexes){
        setDynamicSearchKey(null, colIndexes);
    }
    
    /**
     * 条件名を指定して、動的キー検索条件を設定する。<p>
     *
     * @param name 条件名
     * @param colNames 列インデックス配列
     * @see #setDynamicSearchKey(String, int[])
     */
    public void setDynamicSearchKey(String name, String[] colNames){
        setDynamicSearchKey(name, convertFromColNamesToColIndexes(colNames));
    }
    
    /**
     * 条件名を指定して、動的キー検索条件を設定する。<p>
     *
     * @param name 条件名
     * @param colIndexes 列インデックス配列
     * @see #setDynamicSearchKey(String, int[], int[])
     */
    public void setDynamicSearchKey(String name, int[] colIndexes){
        setDynamicSearchKey(name, colIndexes, null);
    }
    
    /**
     * 条件名を指定して、動的キー検索条件（ソート列指定）を設定する。<p>
     *
     * @param name 条件名
     * @param colNames 列インデックス配列
     * @param orderBy ソート列名配列
     * @see #setDynamicSearchKey(String, String[], String[], boolean[])
     */
    public void setDynamicSearchKey(String name, String[] colNames, String[] orderBy){
        setDynamicSearchKey(
            name,
            colNames,
            orderBy,
            null
        );
    }
    
    /**
     * 条件名を指定して、動的キー検索条件（ソート列指定）を設定する。<p>
     *
     * @param name 条件名
     * @param colIndexes 列インデックス配列
     * @param orderBy ソート列インデックス配列
     * @see #setDynamicSearchKey(String, int[], int[], boolean[])
     */
    public void setDynamicSearchKey(String name, int[] colIndexes, int[] orderBy){
        setDynamicSearchKey(
            name,
            colIndexes,
            orderBy,
            null
        );
    }
    
    /**
     * 条件名を指定して、動的キー検索条件（ソート列指定、ソート順指定付き）を設定する。<p>
     *
     * @param name 条件名
     * @param colNames 列インデックス配列
     * @param orderBy ソート列名配列
     * @param isAsc ソート順。trueの場合、昇順
     * @see #setDynamicSearchKey(String, int[], int[], boolean[])
     */
    public void setDynamicSearchKey(
        String name,
        String[] colNames,
        String[] orderBy,
        boolean[] isAsc
    ){
        setDynamicSearchKey(
            name,
            convertFromColNamesToColIndexes(colNames),
            convertFromColNamesToColIndexes(orderBy),
            isAsc
        );
    }
    
    /**
     * 条件名を指定して、動的キー検索条件（ソート列指定、ソート順指定付き）を設定する。<p>
     * 動的キー検索とは、RecordSetに蓄積されたレコードから、指定された列（複数可）の値が合致するレコードを検索する機能である。<br>
     * また、動的キー検索は、レコードを蓄積する際に検索を行うため、レコードを蓄積する前に条件を設定しておかなければならない。<br>
     * このセッターに対応する検索は、{@link #searchDynamicKey(String, RowData)}で行う。<br>
     *
     * @param name 条件名
     * @param colIndexes 列インデックス配列
     * @param orderBy ソート列インデックス配列
     * @param isAsc ソート順。trueの場合、昇順
     * @see #searchDynamicKey(String, RowData)
     */
    public void setDynamicSearchKey(
        String name,
        int[] colIndexes,
        int[] orderBy,
        boolean[] isAsc
    ){
        if(colIndexes == null || colIndexes.length == 0){
            throw new IllegalArgumentException("Column index array is empty.");
        }
        if(dynamicSearchKeyMap == null){
            dynamicSearchKeyMap = new HashMap();
        }
        dynamicSearchKeyMap.put(
            name,
            new DynamicSearchKeyValue(colIndexes, orderBy, isAsc)
        );
    }
    
    /**
     * 新規レコードを作成する。<p>
     * このRecordSetのスキーマ情報を元に新しいRowDataを作成する。<br>
     * 作成されたRowDataのトランザクションモードは、{@link RowData#E_Record_TypeIgnore}である。<br>
     * 
     * @return 新しいRowData
     */
    public RowData createNewRecord(){
        return new RowData(mSchema);
    }
    
    /**
     * WHERE条件句を設定する。<p>
     * WHERE条件句は、"WHERE"から始まる文字列を指定する事。
     *
     * @param where WHERE条件句
     */
    public void setWhere(String where){
        this.where = new StringBuffer(where);
    }
    
    /**
     * 検索するプライマリキー情報を格納したレコードからWHERE句を生成し、設定する。<p>
     * プライマリキーとなる列名がAとBで、その値が'1'と'2'となるRowDataを指定した場合、<br>
     * <pre>
     *   WHERE A='1' AND B='2'
     * </pre>
     * というWHERE条件句が設定される。
     *
     * @param row レコード
     */
    public void setWhere(RowData row){
        if(row != null){
            setWhere(row.createCodeMasterUpdateKey());
        }
    }
    
    /**
     * 検索するプライマリキー情報を格納したレコード配列からWHERE句を生成し、設定します。<p>
     * プライマリキーとなる列名がAとBで、その値が'1'と'2'となるRowDataとその値が'2'と'3'となるRowDataの配列を指定した場合、<br>
     * <pre>
     *   WHERE (A='1' AND B='2') OR (A='3' AND B='4')
     * </pre>
     * というWHERE条件句が設定される。
     *
     * @param rows レコード配列
     */
    public void setWhere(RowData[] rows){
        if(rows == null || rows.length == 0){
            return;
        }
        final CodeMasterUpdateKey[] keys = new CodeMasterUpdateKey[rows.length];
        for(int i = 0; i < rows.length; i++){
            keys[i] = rows[i].createCodeMasterUpdateKey();
        }
        setWhere(keys);
    }
    
    /**
     * コードマスタ更新キーからWHERE句を生成し、設定します。<p>
     * キーがAとBで、その値が'1'と'2'となるCodeMasterUpdateKeyを指定した場合、<br>
     * <pre>
     *   WHERE A='1' AND B='2'
     * </pre>
     * というWHERE条件句が設定される。
     *
     * @param key コードマスタ更新キー
     */
    public void setWhere(CodeMasterUpdateKey key){
        if(key == null || key.isRemove()){
            return;
        }
        clearBindData();
        where = new StringBuffer(C_WHERE_TOKEN);
        Iterator entries = key.getKeyMap().entrySet().iterator();
        int index = 0;
        while(entries.hasNext()){
            final Map.Entry entry = (Map.Entry)entries.next();
            final String name = (String)entry.getKey();
            where.append(name);
            where.append(C_EQUAL_TOKEN);
            where.append(C_QUESTION_TOKEN);
            if(entries.hasNext()){
                where.append(C_AND_TOKEN);
            }
            final Object value = entry.getValue();
            setBindData(index++, value);
        }
    }
    
    /**
     * コードマスタ更新キー配列からWHERE句を生成し、設定します。<p>
     * キーがAとBで、その値が'1'と'2'となるCodeMasterUpdateKeyとその値が'2'と'3'となるCodeMasterUpdateKeyの配列を指定した場合、<br>
     * <pre>
     *   WHERE (A='1' AND B='2') OR (A='3' AND B='4')
     * </pre>
     * というWHERE条件句が設定される。
     *
     * @param keys コードマスタ更新キー配列
     */
    public void setWhere(CodeMasterUpdateKey[] keys){
        if(keys == null || keys.length == 0){
            return;
        }
        clearBindData();
        final StringBuffer where = new StringBuffer(C_WHERE_TOKEN);
        int index = 0;
        for(int i = 0; i < keys.length; i++){
            CodeMasterUpdateKey key = (CodeMasterUpdateKey)keys[i];
            if(key.isRemove()){
                continue;
            }
            
            if(index != 0){
                where.append(C_OR_TOKEN);
            }
            
            where.append(C_BRACKETS_BEGIN_TOKEN);
            Iterator entries = key.getKeyMap().entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                final String name = (String)entry.getKey();
                where.append(name);
                where.append(C_EQUAL_TOKEN);
                where.append(C_QUESTION_TOKEN);
                if(entries.hasNext()){
                    where.append(C_AND_TOKEN);
                }
                final Object value = entry.getValue();
                setBindData(index++, value);
            }
            where.append(C_BRACKETS_END_TOKEN);
            }
        if(index != 0){
            this.where = where;
        }
    }
    
    /**
     * 部分更新レコードからWHERE句を生成し、設定します。<p>
     * キーがAとBで、その値が'1'と'2'となるCodeMasterUpdateKeyとその値が'2'と'3'となるCodeMasterUpdateKeyが格納されたPartUpdateRecordsを指定した場合、<br>
     * <pre>
     *   WHERE (A='1' AND B='2') OR (A='3' AND B='4')
     * </pre>
     * というWHERE条件句が設定される。
     *
     * @param records 部分更新レコード
     */
    public void setWhere(PartUpdateRecords records){
        if(records == null || records.size() == 0
             || (!records.containsAdd() && !records.containsUpdate())
             || records.isFilledRecord()
        ){
            return;
        }
        clearBindData();
        where = new StringBuffer();
        Iterator keys = records.getKeys();
        int index = 0;
        while(keys.hasNext()){
            CodeMasterUpdateKey key = (CodeMasterUpdateKey)keys.next();
            if(key.isRemove() || records.getRecord(key) != null){
                continue;
            }
            
            if(index != 0){
                where.append(C_OR_TOKEN);
            }
            
            where.append(C_BRACKETS_BEGIN_TOKEN);
            Iterator entries = key.getKeyMap().entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                final String name = (String)entry.getKey();
                where.append(name);
                where.append(C_EQUAL_TOKEN);
                where.append(C_QUESTION_TOKEN);
                if(entries.hasNext()){
                    where.append(C_AND_TOKEN);
                }
                final Object value = entry.getValue();
                setBindData(index++, value);
            }
            where.append(C_BRACKETS_END_TOKEN);
        }
        if(where.length() != 0){
            where.insert(0, C_WHERE_TOKEN);
        }
    }
    
    /**
     * WHERE句の追加処理を行う。<p>
     * 
     * @param sb SELECT [フィールド名]... FROM [テーブル]... までを含む StringBuffer 
     */
    protected void addWhere(StringBuffer sb){
        if(where != null){
            sb.append(where.toString());
        }
    }
    
    /**
     * PreparedStatementにバインドする値を設定する。<p>
     *
     * @param index PreparedStatementのバインド変数インデックス。インデックスは0から始まる
     * @param val PreparedStatementのバインド変数値
     */
    public void setBindData(int index, Object val){
        if(bindDatas == null){
            bindDatas = new ArrayList();
        }
        if(bindDatas.size() < index){
            for(int i = bindDatas.size(); i < index; i++){
                bindDatas.add(null);
            }
        }
        if(bindDatas.size() == index){
            bindDatas.add(val);
        }else{
            bindDatas.set(index, val);
        }
    }
    
    /**
     * PreparedStatementにバインドする値をクリアする。<p>
     */
    public void clearBindData(){
        if(bindDatas != null){
            bindDatas.clear();
        }
    }
    
    
    /**
     * PreparedStatement バインド処理を行う。<p>
     * 
     * @param ps バインドする PreparedStatement
     * @exception SQLException
     */
    protected void addBindData(PreparedStatement ps) throws SQLException {
        if(bindDatas != null){
            Iterator itr = bindDatas.iterator();
            int index = 0;
            while(itr.hasNext()){
                Object bindData = itr.next();
                index++;
                if(bindData == null){
                    final ParameterMetaData meta = ps.getParameterMetaData();
                    ps.setNull(index, meta.getParameterType(index));
                }else{
                    ps.setObject(index, bindData);
                }
            }
        }
    }
    
    /**
     * データベースから検索して、レコードを蓄積する。<p>
     *
     * @return 検索結果のレコード数
     * @throws SQLException
     */
    public int search() throws SQLException{
        return search(-1);
    }
    
    /**
     * データベースから検索して、指定された最大レコード数までレコードを蓄積する。<p>
     *
     * @param max 最大レコード数
     * @return 検索結果のレコード数
     * @throws SQLException
     */
    public int search(int max) throws SQLException{
        StringBuffer sb = new StringBuffer();
        sb.append(C_SELECT_TOKEN);
        if(mDistinctFlg == true){
            sb.append(C_DISTINCT_TOKEN);
        }
        for(int i = 0, imax = mSchema.size(); i < imax; i++){
            if(mSchema.get(i).getFieldKey() == FieldSchema.C_KEY_DUMMY){
                continue;
            }
            if(mSchema.get(i).getPysicalName() != null){
                sb.append(mSchema.get(i).getPysicalName());
                sb.append(C_BLANK_TOKEN) ;
            }
            sb.append(mSchema.get(i).getFieldName());
            sb.append(C_COMMA_TOKEN);
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append(C_FROM_TOKEN).append(mTableNames).append(' ');
        addWhere(sb);
        if(mOrder != null){
            sb.append(C_ORDER_TOKEN).append(mOrder);
        }
        PreparedStatement ps = null;
        try{
            final String statement = sb.toString();
            ps = mCon.prepareStatement(statement);
            if(mLogger != null && mMessageCode != null){
                mLogger.write(mMessageCode, statement);
            }
            addBindData(ps);
            final ResultSet rs = ps.executeQuery();
            int count = 0;
            ByteArrayOutputStream baos = null;
            CharArrayWriter caw = null;
            byte[] byteBuf = null;
            char[] charBuf = null;
            while(rs.next()){
                RowData rd = createNewRecord();
                int rscnt = 1;
                for(int i = 0, imax = mSchema.size(); i < imax;i++){
                    final FieldSchema fs = mSchema.get(i);
                    if(fs.getFieldKey() == FieldSchema.C_KEY_DUMMY){
                        continue;
                    }
                    Object obj = null;
                    switch(fs.getSqlType()){
                        case Types.TIMESTAMP:
                            obj = (java.sql.Timestamp)rs.getTimestamp(rscnt);
                            if(obj != null){
                                obj = new java.util.Date(
                                    ((java.sql.Timestamp)obj).getTime()
                                );
                            }
                            break;
                        case Types.BLOB:
                            final InputStream is = (InputStream)rs.getBinaryStream(rscnt);
                            if(is == null){
                                break;
                            }
                            if(baos == null){
                                baos = new ByteArrayOutputStream();
                                byteBuf = new byte[1024];
                            }
                            try{
                                int readLength = 0;
                                while((readLength = is.read(byteBuf)) != -1){
                                    baos.write(byteBuf, 0, readLength);
                                }
                            }catch(IOException e){
                                throw new SQLException("I/O error in reading BLOB." + e.getMessage());
                            }
                            obj = baos.toByteArray();
                            baos.reset();
                            break;
                        case Types.CLOB:
                            final Reader reader = (Reader)rs.getCharacterStream(rscnt);
                            if(reader == null){
                                break;
                            }
                            if(caw == null){
                                caw = new CharArrayWriter();
                                charBuf = new char[1024];
                            }
                            try{
                                int readLength = 0;
                                while((readLength = reader.read(charBuf)) != -1){
                                    caw.write(charBuf, 0, readLength);
                                }
                            }catch(IOException e){
                                throw new SQLException("I/O error in reading BLOB." + e.getMessage());
                            }
                            obj = caw.toCharArray();
                            caw.reset();
                            break;
                        default:
                            obj = rs.getObject(rscnt);
                    }
                    // 暗号化
                    if(fs.isCrypt()){
                        obj = doEncrypt(obj);
                    }
                    rd.setValueNative(i, obj);
                    rscnt++;
                }
                rd.setTransactionMode(RowData.E_Record_TypeRead);
                addRecord(rd);
                count++;
                if(max > 0 && count >= max){
                    break;
                }
            }
            rs.close();
            return count;
        }finally{
            if(ps != null){
                ps.close();
            }
        }
    }
    
    /**
     * 蓄積型動的条件検索の検索結果を取得する。<p>
     * 蓄積型動的条件検索を行うためには、レコードを蓄積する前に、{@link #setDynamicSearchCondition(String, int[], boolean[])}で動的条件検索条件を設定しておく必要がある。<br>
     *
     * @return 検索結果。条件に合致したRowDataの集合
     * @see #setDynamicSearchCondition(String, int[], boolean[])
     */
    public Collection searchDynamicCondition(){
        return searchDynamicCondition(null);
    }
    
    /**
     * 指定された条件名の蓄積型動的条件検索の検索結果を取得する。<p>
     * 蓄積型動的条件検索を行うためには、レコードを蓄積する前に、{@link #setDynamicSearchCondition(String, String, int[], boolean[])}で動的条件検索条件を設定しておく必要がある。<br>
     *
     * @param name 条件名
     * @return 検索結果。条件に合致したRowDataの集合
     * @see #setDynamicSearchCondition(String, String, int[], boolean[])
     */
    public Collection searchDynamicCondition(String name){
        if(dynamicSearchConditionResultMap == null
             || dynamicSearchConditionResultMap.size() == 0){
            return new HashSet();
        }
        final Map values = (Map)dynamicSearchConditionResultMap.get(name);
        if(values == null){
            return new HashSet();
        }
        return values.values();
    }
    
    /**
     * 蓄積型動的条件検索の検索結果のレコードだけにフィルタリングする。<p>
     * 蓄積型動的条件検索を行うためには、レコードを蓄積する前に、{@link #setDynamicSearchCondition(String, int[], boolean[])}で動的条件検索条件を設定しておく必要がある。<br>
     *
     * @return フィルタリング後の、このオブジェクト自身の参照
     * @see #setDynamicSearchCondition(String, int[], boolean[])
     */
    public RecordSet filterDynamicCondition(){
        return filterDynamicCondition((String)null);
    }
    
    /**
     * 指定された条件名の蓄積型動的条件検索の検索結果のレコードだけにフィルタリングする。<p>
     * 蓄積型動的条件検索を行うためには、レコードを蓄積する前に、{@link #setDynamicSearchCondition(String, String, int[], boolean[])}で動的条件検索条件を設定しておく必要がある。<br>
     *
     * @return フィルタリング後の、このオブジェクト自身の参照
     * @see #setDynamicSearchCondition(String, String, int[], boolean[])
     */
    public RecordSet filterDynamicCondition(String name){
        Collection records = searchDynamicCondition(name);
        if(size() != records.size()){
            clear();
            final Iterator itr = records.iterator();
            while(itr.hasNext()){
                addRecord((RowData)itr.next());
            }
        }
        return this;
    }
    
    /**
     * リアル型動的条件検索を行う。<p>
     * {@link #searchDynamicConditionReal(String, int[], boolean[]) searchDynamicConditionReal(condition, (int[])null, (boolean[])null)}を呼び出すのと同じ。<br>
     *
     * @param condition 条件式
     * @return 検索結果。条件に合致したRowDataの集合
     * @exception Exception 条件式が不正な場合
     * @see #searchDynamicConditionReal(String, int[], boolean[])
     */
    public Collection searchDynamicConditionReal(String condition) throws Exception{
        return searchDynamicConditionReal(
            condition,
            (int[])null,
            (boolean[])null
        );
    }
    
    /**
     * リアル型動的条件検索を行う。<p>
     * {@link #searchDynamicConditionReal(String, int[], boolean[], Map) searchDynamicConditionReal(condition, (int[])null, (boolean[])null, valueMap)}を呼び出すのと同じ。<br>
     *
     * @param condition 条件式
     * @param valueMap 条件式中の変数マップ
     * @return 検索結果。条件に合致したRowDataの集合
     * @exception Exception 条件式が不正な場合
     * @see #searchDynamicConditionReal(String, int[], boolean[], Map)
     */
    public Collection searchDynamicConditionReal(String condition, Map valueMap) throws Exception{
        return searchDynamicConditionReal(
            condition,
            (int[])null,
            (boolean[])null,
            valueMap
        );
    }
    
    /**
     * リアル型動的条件検索（ソート列指定付き）を行う。<p>
     * {@link #searchDynamicConditionReal(String, String[], boolean[]) searchDynamicConditionReal(condition, orderBy, (boolean[])null)}を呼び出すのと同じ。<br>
     *
     * @param condition 条件式
     * @param orderBy ソート列名配列
     * @return 検索結果。条件に合致したRowDataの集合
     * @exception Exception 条件式が不正な場合
     * @see #searchDynamicConditionReal(String, String[], boolean[])
     */
    public Collection searchDynamicConditionReal(String condition, String[] orderBy) throws Exception{
        return searchDynamicConditionReal(
            condition,
            orderBy,
            (boolean[])null
        );
    }
    
    /**
     * リアル型動的条件検索（ソート列指定付き）を行う。<p>
     * {@link #searchDynamicConditionReal(String, String[], boolean[], Map) searchDynamicConditionReal(condition, orderBy, null, valueMap)}を呼び出すのと同じ。<br>
     *
     * @param condition 条件式
     * @param orderBy ソート列名配列
     * @param valueMap 条件式中の変数マップ
     * @return 検索結果。条件に合致したRowDataの集合
     * @exception Exception 条件式が不正な場合
     * @see #searchDynamicConditionReal(String, String[], boolean[], Map)
     */
    public Collection searchDynamicConditionReal(String condition, String[] orderBy, Map valueMap) throws Exception{
        return searchDynamicConditionReal(
            condition,
            orderBy,
            (boolean[])null,
            valueMap
        );
    }
    
    /**
     * リアル型動的条件検索（ソート列指定付き）を行う。<p>
     * {@link #searchDynamicConditionReal(String, int[], boolean[]) searchDynamicConditionReal(condition, orderBy, (boolean[])null)}を呼び出すのと同じ。<br>
     *
     * @param condition 条件式
     * @param orderBy ソート列インデックス配列
     * @return 検索結果。条件に合致したRowDataの集合
     * @exception Exception 条件式が不正な場合
     * @see #searchDynamicConditionReal(String, int[], boolean[])
     */
    public Collection searchDynamicConditionReal(String condition, int[] orderBy) throws Exception{
        return searchDynamicConditionReal(
            condition,
            orderBy,
            (boolean[])null
        );
    }
    
    /**
     * リアル型動的条件検索（ソート列指定付き）を行う。<p>
     * {@link #searchDynamicConditionReal(String, int[], boolean[], Map) searchDynamicConditionReal(condition, orderBy, (boolean[])null, valueMap)}を呼び出すのと同じ。<br>
     *
     * @param condition 条件式
     * @param orderBy ソート列インデックス配列
     * @param valueMap 条件式中の変数マップ
     * @return 検索結果。条件に合致したRowDataの集合
     * @exception Exception 条件式が不正な場合
     * @see #searchDynamicConditionReal(String, int[], boolean[], Map)
     */
    public Collection searchDynamicConditionReal(String condition, int[] orderBy, Map valueMap) throws Exception{
        return searchDynamicConditionReal(
            condition,
            orderBy,
            (boolean[])null,
            valueMap
        );
    }
    
    /**
     * リアル型動的条件検索（ソート列指定、ソート順指定付き）を行う。<p>
     * {@link #searchDynamicConditionReal(String, String[], boolean[], Map) searchDynamicConditionReal(condition, orderBy, isAsc, null)}を呼び出すのと同じ。<br>
     *
     * @param condition 条件式
     * @param orderBy ソート列名配列
     * @param isAsc ソート順。trueの場合、昇順
     * @return 検索結果。条件に合致したRowDataの集合
     * @exception Exception 条件式が不正な場合
     * @see #searchDynamicConditionReal(String, String[], boolean[], Map)
     */
    public Collection searchDynamicConditionReal(String condition, String[] orderBy, boolean[] isAsc) throws Exception{
        return searchDynamicConditionReal(
            condition,
            orderBy,
            isAsc,
            null
        );
    }
    
    /**
     * リアル型動的条件検索（ソート列指定、ソート順指定付き）を行う。<p>
     *
     * @param condition 条件式
     * @param orderBy ソート列名配列
     * @param isAsc ソート順。trueの場合、昇順
     * @param valueMap 条件式中の変数マップ
     * @return 検索結果。条件に合致したRowDataの集合
     * @exception Exception 条件式が不正な場合
     * @see #searchDynamicConditionReal(String, int[], boolean[], Map)
     */
    public Collection searchDynamicConditionReal(String condition, String[] orderBy, boolean[] isAsc, Map valueMap) throws Exception{
        return searchDynamicConditionReal(
            condition,
            convertFromColNamesToColIndexes(orderBy),
            isAsc,
            valueMap
        );
    }
    
    /**
     * リアル型動的条件検索（ソート列指定、ソート順指定付き）を行う。<p>
     * {@link #searchDynamicConditionReal(String, int[], boolean[], Map) searchDynamicConditionReal(condition, orderBy, isAsc, null)}を呼び出すのと同じ。<br>
     *
     * @param condition 条件式
     * @param orderBy ソート列インデックス配列
     * @param isAsc ソート順。trueの場合、昇順
     * @return 検索結果。条件に合致したRowDataの集合
     * @exception Exception 条件式が不正な場合
     * @see #searchDynamicConditionReal(String, int[], boolean[], Map)
     */
    public Collection searchDynamicConditionReal(String condition, int[] orderBy, boolean[] isAsc) throws Exception{
        return searchDynamicConditionReal(
            condition,
            orderBy,
            isAsc,
            null
        );
    }
    
    /**
     * リアル型動的条件検索（ソート列指定、ソート順指定付き）を行う。<p>
     * 動的条件検索とは、RecordSetに蓄積されたレコードから、条件式に合致するレコードを検索する機能である。<br>
     * また、動的条件検索には、レコードを蓄積する際に検索する蓄積型検索と、蓄積されたレコードからリアルに検索するリアル型検索がある。<br>
     * リアル型検索の利点は、条件式中に、動的に変わる変数を指定し、その変数値を引数valueMapで与える事ができる事である。<br>
     * <p>
     * 条件式は、<a href="http://jakarta.apache.org/commons/jexl/">Jakarta Commons Jexl</a>の式言語を使用する。<br>
     * リアル型検索では、レコードの列の値を、列名を指定する事で、式中で参照する事ができるのに加えて、任意の変数名を式中に定義し、その値を引数valueMapで与える事ができる。<br>
     * <pre>
     *  例：A == '1' and B &gt;= 3
     * </pre>
     *
     * @param condition 条件式
     * @param orderBy ソート列インデックス配列
     * @param isAsc ソート順。trueの場合、昇順
     * @param valueMap 条件式中の変数マップ
     * @return 検索結果。条件に合致したRowDataの集合
     * @exception Exception 条件式が不正な場合
     */
    public Collection searchDynamicConditionReal(String condition, int[] orderBy, boolean[] isAsc, Map valueMap) throws Exception{
        if(size() == 0){
            return new HashSet();
        }
        final Expression exp = ExpressionFactory.createExpression(condition);
        final Map result = createOrderByMap(orderBy, isAsc);
        final JexlContext context = JexlHelper.createContext();
        for(int i = 0, imax = size(); i < imax; i++){
            RowData rd = get(i);
            for(int j = 0, jmax = mSchema.size(); j < jmax; j++){
                final FieldSchema field = mSchema.get(j);
                final String fieldName = field.getFieldName();
                context.getVars().put(fieldName, rd.get(j));
            }
            if(valueMap != null){
                context.getVars().putAll(valueMap);
            }
            final Boolean ret = (Boolean)exp.evaluate(context);
            if(ret != null && ret.booleanValue()){
                result.put(rd, rd);
            }
        }
        return result.values();
    }
    
    /**
     * リアル型動的条件検索の検索結果のレコードだけにフィルタリングする。<p>
     * {@link #filterDynamicConditionReal(String, int[], boolean[]) filterDynamicConditionReal(condition, (int[])null, (boolean[])null)}を呼び出すのと同じ。<br>
     *
     * @param condition 条件式
     * @return フィルタリング後の、このオブジェクト自身の参照
     * @see #filterDynamicConditionReal(String, int[], boolean[])
     */
    public RecordSet filterDynamicConditionReal(String condition) throws Exception{
        return filterDynamicConditionReal(condition, (int[])null);
    }
    
    /**
     * リアル型動的条件検索の検索結果のレコードだけにフィルタリングする。<p>
     * {@link #filterDynamicConditionReal(String, int[], boolean[], Map) filterDynamicConditionReal(condition, (int[])null, (boolean[])null, valueMap)}を呼び出すのと同じ。<br>
     *
     * @param condition 条件式
     * @param valueMap 条件式中の変数マップ
     * @return フィルタリング後の、このオブジェクト自身の参照
     * @see #filterDynamicConditionReal(String, int[], boolean[], Map)
     */
    public RecordSet filterDynamicConditionReal(String condition, Map valueMap) throws Exception{
        return filterDynamicConditionReal(condition, (int[])null, valueMap);
    }
    
    /**
     * リアル型動的条件検索の検索結果のレコードだけにフィルタリングする。<p>
     * {@link #filterDynamicConditionReal(String, String[], boolean[]) filterDynamicConditionReal(condition, orderBy, (boolean[])null)}を呼び出すのと同じ。<br>
     *
     * @param condition 条件式
     * @param orderBy ソート列名配列
     * @return フィルタリング後の、このオブジェクト自身の参照
     * @see #filterDynamicConditionReal(String, String[], boolean[])
     */
    public RecordSet filterDynamicConditionReal(String condition, String[] orderBy) throws Exception{
        return filterDynamicConditionReal(
            condition,
            orderBy,
            (boolean[])null
        );
    }
    
    /**
     * リアル型動的条件検索の検索結果のレコードだけにフィルタリングする。<p>
     * {@link #filterDynamicConditionReal(String, String[], boolean[], Map) filterDynamicConditionReal(condition, orderBy, (boolean[])null, valueMap)}を呼び出すのと同じ。<br>
     *
     * @param condition 条件式
     * @param orderBy ソート列名配列
     * @param valueMap 条件式中の変数マップ
     * @return フィルタリング後の、このオブジェクト自身の参照
     * @see #filterDynamicConditionReal(String, String[], boolean[], Map)
     */
    public RecordSet filterDynamicConditionReal(String condition, String[] orderBy, Map valueMap) throws Exception{
        return filterDynamicConditionReal(
            condition,
            orderBy,
            (boolean[])null,
            valueMap
        );
    }
    
    /**
     * リアル型動的条件検索の検索結果のレコードだけにフィルタリングする。<p>
     * {@link #filterDynamicConditionReal(String, int[], boolean[]) filterDynamicConditionReal(condition, orderBy, (boolean[])null)}を呼び出すのと同じ。<br>
     *
     * @param condition 条件式
     * @param orderBy ソート列インデックス配列
     * @return フィルタリング後の、このオブジェクト自身の参照
     * @see #filterDynamicConditionReal(String, int[], boolean[])
     */
    public RecordSet filterDynamicConditionReal(String condition, int[] orderBy) throws Exception{
        return filterDynamicConditionReal(
            condition,
            orderBy,
            (boolean[])null
        );
    }
    
    /**
     * リアル型動的条件検索の検索結果のレコードだけにフィルタリングする。<p>
     * {@link #filterDynamicConditionReal(String, int[], boolean[], Map) filterDynamicConditionReal(condition, orderBy, (boolean[])null, valueMap)}を呼び出すのと同じ。<br>
     *
     * @param condition 条件式
     * @param orderBy ソート列インデックス配列
     * @param valueMap 条件式中の変数マップ
     * @return フィルタリング後の、このオブジェクト自身の参照
     * @see #filterDynamicConditionReal(String, int[], boolean[], Map)
     */
    public RecordSet filterDynamicConditionReal(String condition, int[] orderBy, Map valueMap) throws Exception{
        return filterDynamicConditionReal(
            condition,
            orderBy,
            (boolean[])null,
            valueMap
        );
    }
    
    /**
     * リアル型動的条件検索の検索結果のレコードだけにフィルタリングする。<p>
     * {@link #filterDynamicConditionReal(String, String[], boolean[], Map) filterDynamicConditionReal(condition, orderBy, isAsc, null)}を呼び出すのと同じ。<br>
     *
     * @param condition 条件式
     * @param orderBy ソート列名配列
     * @param isAsc ソート順。trueの場合、昇順
     * @return フィルタリング後の、このオブジェクト自身の参照
     * @see #filterDynamicConditionReal(String, String[], boolean[], Map)
     */
    public RecordSet filterDynamicConditionReal(String condition, String[] orderBy, boolean[] isAsc) throws Exception{
        return filterDynamicConditionReal(
            condition,
            orderBy,
            isAsc,
            null
        );
    }
    
    /**
     * リアル型動的条件検索の検索結果のレコードだけにフィルタリングする。<p>
     *
     * @param condition 条件式
     * @param orderBy ソート列名配列
     * @param isAsc ソート順。trueの場合、昇順
     * @param valueMap 条件式中の変数マップ
     * @return フィルタリング後の、このオブジェクト自身の参照
     * @see #filterDynamicConditionReal(String, int[], boolean[], Map)
     */
    public RecordSet filterDynamicConditionReal(String condition, String[] orderBy, boolean[] isAsc, Map valueMap) throws Exception{
        return filterDynamicConditionReal(
            condition,
            convertFromColNamesToColIndexes(orderBy),
            isAsc,
            valueMap
        );
    }
    
    /**
     * リアル型動的条件検索の検索結果のレコードだけにフィルタリングする。<p>
     * {@link #filterDynamicConditionReal(String, int[], boolean[], Map) filterDynamicConditionReal(condition, orderBy, isAsc, null)}を呼び出すのと同じ。<br>
     *
     * @param condition 条件式
     * @param orderBy ソート列インデックス配列
     * @param isAsc ソート順。trueの場合、昇順
     * @return フィルタリング後の、このオブジェクト自身の参照
     * @see #filterDynamicConditionReal(String, int[], boolean[], Map)
     */
    public RecordSet filterDynamicConditionReal(String condition, int[] orderBy, boolean[] isAsc) throws Exception{
        return filterDynamicConditionReal(
            condition,
            orderBy,
            isAsc,
            null
        );
    }
    
    /**
     * リアル型動的条件検索の検索結果のレコードだけにフィルタリングする。<p>
     *
     * @param condition 条件式
     * @param orderBy ソート列インデックス配列
     * @param isAsc ソート順。trueの場合、昇順
     * @param valueMap 条件式中の変数マップ
     * @return フィルタリング後の、このオブジェクト自身の参照
     * @see #searchDynamicConditionReal(String, int[], boolean[], Map)
     */
    public RecordSet filterDynamicConditionReal(String condition, int[] orderBy, boolean[] isAsc, Map valueMap) throws Exception{
        Collection records = searchDynamicConditionReal(condition, orderBy, isAsc, valueMap);
        if(size() != records.size()){
            clear();
            final Iterator itr = records.iterator();
            while(itr.hasNext()){
                addRecord((RowData)itr.next());
            }
        }
        return this;
    }
    
    /**
     * 動的キー検索の検索結果を取得する。<p>
     * {@link #searchDynamicKey(String, RowData) searchDynamicKey(null, key, (int[])null, (boolean[])null)}を呼び出すのと同じ。<br>
     *
     * @param key 検索キー
     * @return 検索結果。条件に合致したRowDataの集合
     * @see #searchDynamicKey(String, RowData)
     */
    public Collection searchDynamicKey(RowData key){
        return searchDynamicKey(null, key);
    }
    
    /**
     * 条件名を指定して、動的キー検索の検索結果を取得する。<p>
     * 動的キー検索とは、RecordSetに蓄積されたレコードから、指定された列（複数可）の値が合致するレコードを検索する機能である。<br>
     * また、動的キー検索は、レコードを蓄積する際に検索を行うため、レコードを蓄積する前に、{@link #setDynamicSearchKey(String, int[], int[], boolean[])}で条件を設定しておかなければならない。<br>
     *
     * @param name 条件名
     * @param key 検索キー
     * @return 検索結果。条件に合致したRowDataの集合
     * @see #setDynamicSearchKey(String, int[], int[], boolean[])
     */
    public Collection searchDynamicKey(String name, RowData key){
        if(dynamicSearchKeyMap == null || dynamicSearchMap == null){
            return new HashSet();
        }
        final DynamicSearchKeyValue keyValue
             = (DynamicSearchKeyValue)dynamicSearchKeyMap.get(name);
        if(keyValue == null || keyValue.colIndexes == null){
            return new HashSet();
        }
        final Map map = (Map)dynamicSearchMap.get(keyValue);
        if(map == null){
            return new HashSet();
        }
        final Object values = map.get(
            key.getKey(keyValue.colIndexes)
        );
        if(values == null){
            return new HashSet();
        }
        if(values instanceof Map){
            return ((Map)values).values();
        }else{
            final Set result = new HashSet();
            result.add(values);
            return result;
        }
    }
    
    /**
     * 条件名を指定して、動的キー検索の検索結果（ソート列指定）を取得する。<p>
     *
     * @param name 条件名
     * @param key 検索キー
     * @param orderBy ソート列名配列
     * @param isAsc ソート順。trueの場合、昇順
     * @return 検索結果。条件に合致したRowDataの集合
     * @see #setDynamicSearchKey(String, int[], int[], boolean[])
     * @see #searchDynamicKey(String, RowData)
     */
    public Collection searchDynamicKey(
        String name,
        RowData key,
        String[] orderBy,
        boolean[] isAsc
    ){
        return searchDynamicKey(
            name,
            key,
            convertFromColNamesToColIndexes(orderBy),
            isAsc
        );
    }
    
    /**
     * 条件名を指定して、動的キー検索の検索結果（ソート列指定）を取得する。<p>
     *
     * @param name 条件名
     * @param key 検索キー
     * @param orderBy ソート列名配列
     * @param isAsc ソート順。trueの場合、昇順
     * @return 検索結果。条件に合致したRowDataの集合
     * @see #setDynamicSearchKey(String, int[], int[], boolean[])
     * @see #searchDynamicKey(String, RowData)
     */
    public Collection searchDynamicKey(
        String name,
        RowData key,
        int[] orderBy,
        boolean[] isAsc
    ){
        final Collection collection = searchDynamicKey(name, key);
        if(collection.size() < 2){
            return collection;
        }
        final List rows = new ArrayList(collection);
        Comparator comp = createOrderByComparator(orderBy, isAsc);
        if(comp == null){
            if(mSchema == null){
                throw new InvalidDataException("Schema not initalize.");
            }
            int[] colIndexes = new int[mSchema.getUniqueKeySize()];
            for(int i = 0; i < colIndexes.length; i++){
                colIndexes[i] = mSchema.getUniqueFieldSchema(i).getIndex();
            }
            comp = new RowDataComparator(mSchema, colIndexes, isAsc);
        }
        Collections.sort(rows, comp);
        return rows;
    }
    
    /**
     * 動的キー検索の検索結果のレコードだけにフィルタリングする。<p>
     * {@link #filterDynamicKey(String, RowData) filterDynamicKey(null, key)}を呼び出すのと同じ。<br>
     *
     * @param key 検索キー
     * @return フィルタリング後の、このオブジェクト自身の参照
     * @see #filterDynamicKey(String, RowData)
     */
    public RecordSet filterDynamicKey(RowData key){
        return filterDynamicKey(null, key);
    }
    
    /**
     * 動的キー検索の検索結果のレコードだけにフィルタリングする。<p>
     *
     * @param name 条件名
     * @param key 検索キー
     * @return フィルタリング後の、このオブジェクト自身の参照
     * @see #searchDynamicKey(String, RowData)
     */
    public RecordSet filterDynamicKey(String name, RowData key){
        Collection records = searchDynamicKey(name, key);
        if(size() != records.size()){
            clear();
            final Iterator itr = records.iterator();
            while(itr.hasNext()){
                addRecord((RowData)itr.next());
            }
        }
        return this;
    }
    
    /**
     * このレコードセットのプライマリキーと、指定されたレコードセットの該当するキーが一致するレコードを取得する。<p>
     *
     * @param recset レコードセット
     * @return 検索結果。条件に合致したRowDataの集合
     */
    public Collection searchRecords(RecordSet recset){
        return searchRecords(
            recset,
            (int[])null
        );
    }
    
    /**
     * このレコードセットのプライマリキーと、指定されたレコードセットの該当するキーが一致するレコードを取得する。<p>
     *
     * @param recset レコードセット
     * @param orderBy ソート列名配列
     * @return 検索結果。条件に合致したRowDataの集合
     */
    public Collection searchRecords(RecordSet recset, String[] orderBy){
        return searchRecords(
            recset,
            orderBy,
            (boolean[])null
        );
    }
    
    /**
     * このレコードセットのプライマリキーと、指定されたレコードセットの該当するキーが一致するレコードを取得する。<p>
     *
     * @param recset レコードセット
     * @param orderBy ソート列インデックス配列
     * @return 検索結果。条件に合致したRowDataの集合
     */
    public Collection searchRecords(RecordSet recset, int[] orderBy){
        return searchRecords(
            recset,
            orderBy,
            (boolean[])null
        );
    }
    
    /**
     * このレコードセットのプライマリキーと、指定されたレコードセットの該当するキーが一致するレコードを取得する。<p>
     *
     * @param recset レコードセット
     * @param orderBy ソート列名配列
     * @param isAsc ソート順。trueの場合、昇順
     * @return 検索結果。条件に合致したRowDataの集合
     */
    public Collection searchRecords(RecordSet recset, String[] orderBy, boolean[] isAsc){
        return searchRecords(
            recset,
            convertFromColNamesToColIndexes(orderBy),
            isAsc
        );
    }
    
    /**
     * このレコードセットのプライマリキーと、指定されたレコードセットの該当するキーが一致するレコードを取得する。<p>
     *
     * @param recset レコードセット
     * @param orderBy ソート列インデックス配列
     * @param isAsc ソート順。trueの場合、昇順
     * @return 検索結果。条件に合致したRowDataの集合
     */
    public Collection searchRecords(RecordSet recset, int[] orderBy, boolean[] isAsc){
        if(recset == null || recset.size() == 0){
            return new HashSet();
        }
        if(mSchema == null){
            throw new InvalidDataException("Schema not initalize.");
        }
        final RowSchema keySchema = recset.getRowSchema();
        final int uniqueKeySize = mSchema.getUniqueKeySize();
        final int[] uniqueKeyIndexes = new int[uniqueKeySize];
        for(int i = 0; i < uniqueKeySize; i++){
            final String colName
                 = mSchema.getUniqueFieldSchema(i).getFieldName();
            final FieldSchema field = keySchema.get(colName);
            if(field == null){
                return new HashSet();
            }
            uniqueKeyIndexes[i] = field.getIndex();
        }
        final Map records = createOrderByMap(orderBy, isAsc);
        for(int i = 0, imax = recset.size(); i < imax; i++){
            final RowData key = recset.get(i);
            RowData rec = get(key.getKey(uniqueKeyIndexes));
            if(rec != null){
                records.put(rec, rec);
            }
        }
        return records.values();
    }
    
    /**
     * このレコードセットのプライマリキーと、指定されたレコードセットの該当するキーが一致するレコードだけにフィルタリングする。<p>
     *
     * @param recset レコードセット
     * @return フィルタリング後の、このオブジェクト自身の参照
     */
    public RecordSet filterRecords(RecordSet recset){
        return filterRecords(
            recset,
            (int[])null
        );
    }
    
    /**
     * このレコードセットのプライマリキーと、指定されたレコードセットの該当するキーが一致するレコードだけにフィルタリングする。<p>
     *
     * @param recset レコードセット
     * @param orderBy ソート列名配列
     * @return フィルタリング後の、このオブジェクト自身の参照
     */
    public RecordSet filterRecords(RecordSet recset, String[] orderBy){
        return filterRecords(
            recset,
            orderBy,
            (boolean[])null
        );
    }
    
    /**
     * このレコードセットのプライマリキーと、指定されたレコードセットの該当するキーが一致するレコードだけにフィルタリングする。<p>
     *
     * @param recset レコードセット
     * @param orderBy ソート列インデックス配列
     * @return フィルタリング後の、このオブジェクト自身の参照
     */
    public RecordSet filterRecords(RecordSet recset, int[] orderBy){
        return filterRecords(
            recset,
            orderBy,
            (boolean[])null
        );
    }
    
    /**
     * このレコードセットのプライマリキーと、指定されたレコードセットの該当するキーが一致するレコードだけにフィルタリングする。<p>
     *
     * @param recset レコードセット
     * @param orderBy ソート列名配列
     * @param isAsc ソート順。trueの場合、昇順
     * @return フィルタリング後の、このオブジェクト自身の参照
     */
    public RecordSet filterRecords(RecordSet recset, String[] orderBy, boolean[] isAsc){
        return filterRecords(
            recset,
            convertFromColNamesToColIndexes(orderBy),
            isAsc
        );
    }
    
    /**
     * このレコードセットのプライマリキーと、指定されたレコードセットの該当するキーが一致するレコードだけにフィルタリングする。<p>
     *
     * @param recset レコードセット
     * @param orderBy ソート列インデックス配列
     * @param isAsc ソート順。trueの場合、昇順
     * @return フィルタリング後の、このオブジェクト自身の参照
     */
    public RecordSet filterRecords(RecordSet recset, int[] orderBy, boolean[] isAsc){
        Collection records = searchRecords(recset, orderBy, isAsc);
        if(size() != records.size()){
            clear();
            final Iterator itr = records.iterator();
            while(itr.hasNext()){
                addRecord((RowData)itr.next());
            }
        }
        return this;
    }
    
    /**
     * 行データのリストを取得する。<p>
     *
     * @return {@link RowData}を要素とするリスト
     */
    protected ArrayList getList(){
        return mRows;
    }
    
    /**
     * 行データをプライマリキー文字列でマッピングしているマップを取得する。<p>
     * 
     * @return プライマリキー文字列と{@link RowData}でマッピングされたHashMap
     */
    protected HashMap getHash(){
        return mHash;
    }
    
    /**
     * 指定されたインデックスのレコードを取得する。<p>
     * 
     * @param index
     * @return レコード
     */
    public RowData get(int index){
        return (RowData)mRows.get(index);
    }
    
    /**
     * 指定されたプライマリキー文字列に該当するレコードを取得する。<p>
     * 
     * @param key プライマリキー文字列
     * @return レコード
     */
    public RowData get(String key){
        return (RowData)mHash.get(key);
    }
    
    /**
     * 指定されたプライマリキーレコードに該当するレコードを取得する。<p>
     * 
     * @param key プライマリキーレコード
     * @return レコード
     */
    public RowData get(RowData key){
        return get(key.getKey());
    }
    
    /**
     * レコードの配列を取得する。<p>
     *
     * @return レコードの配列
     */
    public RowData[] toArray(){
        return (RowData[])mRows.toArray(new RowData[mRows.size()]);
    }
    
    /**
     * レコードのリストを取得する。<p>
     *
     * @return レコードのリスト
     */
    public List toList(){
        return new ArrayList(mRows);
    }
    
    /**
     * 蓄積されているレコード数を取得する。<p>
     * 
     * @return レコード数
     */
    public int size(){
        return mRows.size();
    }
    
    /**
     * プライマリキー文字列の昇順でソートする。<p>
     */
    public void sort(){
        sort((int[])null);
    }
    
    /**
     * 指定された列名の列をソートキーにして、昇順でソートする。<p>
     *
     * @param orderBy ソートキーとなる列名配列
     */
    public void sort(String[] orderBy){
        sort(orderBy, null);
    }
    
    /**
     * 指定された列インデックスの列をソートキーにして、昇順でソートする。<p>
     *
     * @param orderBy ソートキーとなる列インデックス配列
     */
    public void sort(int[] orderBy){
        sort(orderBy, null);
    }
    
    /**
     * 指定された列名の列をソートキーにしてソートする。<p>
     *
     * @param orderBy ソートキーとなる列名配列
     * @param isAsc 昇順ソートする場合はtrue。降順ソートする場合は、false
     */
    public void sort(String[] orderBy, boolean[] isAsc){
        sort(convertFromColNamesToColIndexes(orderBy), isAsc);
    }
    
    /**
     * 指定された列インデックスの列をソートキーにしてソートする。<p>
     *
     * @param orderBy ソートキーとなる列インデックス配列
     * @param isAsc 昇順ソートする場合はtrue。降順ソートする場合は、false
     */
    public void sort(int[] orderBy, boolean[] isAsc){
        sort(orderBy, isAsc, true);
    }
    
    /**
     * 指定された列名の列をソートキーにしてソートする。<p>
     *
     * @param orderBy ソートキーとなる列名配列
     * @param isAsc 昇順ソートする場合はtrue。降順ソートする場合は、false
     * @param isSetRowNum 行番号を設定し直す場合は、true
     */
    public void sort(String[] orderBy, boolean[] isAsc, boolean isSetRowNum){
        sort(convertFromColNamesToColIndexes(orderBy), isAsc, isSetRowNum);
    }
    
    /**
     * 指定された列インデックスの列をソートキーにしてソートする。<p>
     *
     * @param orderBy ソートキーとなる列インデックス配列
     * @param isAsc 昇順ソートする場合はtrue。降順ソートする場合は、false
     * @param isSetRowNum 行番号を設定し直す場合は、true
     */
    public void sort(int[] orderBy, boolean[] isAsc, boolean isSetRowNum){
        if(mRows.size() < 2){
            return;
        }
        Comparator comp = createOrderByComparator(orderBy, isAsc);
        if(comp == null){
            if(mSchema == null){
                throw new InvalidDataException("Schema not initalize.");
            }
            int[] colIndexes = new int[mSchema.getUniqueKeySize()];
            for(int i = 0; i < colIndexes.length; i++){
                colIndexes[i] = mSchema.getUniqueFieldSchema(i).getIndex();
            }
            comp = new RowDataComparator(mSchema, colIndexes, isAsc);
        }
        Collections.sort(mRows, comp);
        if(isSetRowNum){
            for(int i = 0, imax = mRows.size(); i < imax; i++){
                ((RowData)mRows.get(i)).setRowIndex(i);
            }
        }
    }
    
    /**
     * このレコードセットの{@link RowData}を主キーでソートするComparatorを生成する。<p>
     *
     * @return このレコードセットのRowDataをソートするComparator
     */
    public Comparator createRowComparator(){
        if(mSchema == null){
            throw new InvalidDataException("Schema not initalize.");
        }
        final int uniqueKeySize = mSchema.getUniqueKeySize();
        if(uniqueKeySize == 0){
            throw new InvalidDataException("Unique key not found.");
        }
        int[] orderBy = new int[uniqueKeySize];
        for(int i = 0; i < uniqueKeySize; i++){
            final FieldSchema fieldSchema = mSchema.getUniqueFieldSchema(i);
            orderBy[i] = fieldSchema.getIndex();
        }
        return createRowComparator(orderBy);
    }
    
    /**
     * このレコードセットの{@link RowData}をソートするComparatorを生成する。<p>
     *
     * @param orderBy ソートキーとなる列名配列
     * @return このレコードセットのRowDataをソートするComparator
     */
    public Comparator createRowComparator(String[] orderBy){
        return createRowComparator(orderBy, null);
    }
    
    /**
     * このレコードセットの{@link RowData}をソートするComparatorを生成する。<p>
     *
     * @param orderBy ソートキーとなる列インデックス配列
     * @return このレコードセットのRowDataをソートするComparator
     */
    public Comparator createRowComparator(int[] orderBy){
        return createRowComparator(orderBy, null);
    }
    
    /**
     * このレコードセットの{@link RowData}をソートするComparatorを生成する。<p>
     *
     * @param orderBy ソートキーとなる列名配列
     * @param isAsc 昇順ソートする場合はtrue。降順ソートする場合は、false
     * @return このレコードセットのRowDataをソートするComparator
     */
    public Comparator createRowComparator(String[] orderBy, boolean[] isAsc){
        return createRowComparator(convertFromColNamesToColIndexes(orderBy), isAsc);
    }
    
    /**
     * このレコードセットの{@link RowData}をソートするComparatorを生成する。<p>
     *
     * @param orderBy ソートキーとなる列インデックス配列
     * @param isAsc 昇順ソートする場合はtrue。降順ソートする場合は、false
     * @return このレコードセットのRowDataをソートするComparator
     */
    public Comparator createRowComparator(int[] orderBy, boolean[] isAsc){
        return new RowDataComparator(mSchema, orderBy, isAsc);
    }
    
    /**
     * 追加・修正・削除されたRowDataを含むRecordSetを取得する。<p>
     * 
     * @return 追加・修正・削除されたRowDataを含むRecordSet
     */
    public RecordSet makeGoneData(){
        RecordSet ret = cloneEmpty();
        for(int i = size(); --i >= 0;){
            RowData rd = get(i);
            int tmode = rd.getTransactionMode();
            if(tmode == RowData.E_Record_TypeDelete
                 || tmode == RowData.E_Record_TypeInsert
                 || tmode == RowData.E_Record_TypeUpdate
                 || tmode == RowData.E_Record_TypeDeleteInsert
            ){
                ret.addRecord(rd.makeGoneData(ret.mSchema));
            }
        }
        return ret;
    }
    
    /**
     * レコードを挿入する。<p>
     * 挿入されたレコードのトランザクションモードは、{@link RowData#E_Record_TypeInsert}になる。<br>
     * 
     * @param rd 挿入するRowData
     * @exception InvalidDataException プライマリキーが重複する場合
     */
    public void insertRecord(RowData rd){
        rd.setTransactionMode(RowData.E_Record_TypeInsert);
        addRecord(rd);
    }
    
    /**
     * レコードを追加する。<p>
     * 追加されたレコードのトランザクションモードは、変更されない。<br>
     * 
     * @param rd 追加するRowData
     * @exception InvalidDataException プライマリキーが重複する場合
     */
    public void addRecord(RowData rd){
        String key = rd.getKey() ;
        if(rd.mRowSchema != mSchema && mSchema.equals(rd.mRowSchema)){
            rd.mRowSchema = mSchema;
        }
        if(key != null && key.length() > 0){
            Object tmp = this.mHash.get(key);
            if(tmp == null){
                mHash.put(key, rd);
                mRows.add(rd);
            }else{
                RowData tmpRd = (RowData)tmp;
                if(tmpRd.getTransactionMode() == RowData.E_Record_TypeDelete
                     && rd.getTransactionMode() == RowData.E_Record_TypeInsert){
                    //以前設定のレコードがDELETE、新規追加がINSERTの場合
                    //行データの入れ替え
                    mHash.remove(key);
                    mRows.remove(tmpRd);
                    mHash.put(key, rd);
                    mRows.add(rd);
                    //トランザクションモードの変更
                    rd.setTransactionModeForce(RowData.E_Record_TypeDeleteInsert);
                    //以前設定のレコードインデックスを設定
                    rd.setRowIndex(tmpRd.getRowIndex());
                }else if(tmpRd.getTransactionMode() == RowData.E_Record_TypeInsert
                     && rd.getTransactionMode() == RowData.E_Record_TypeDelete){
                    //以前設定のレコードがINSERT、新規追加がDELETEの場合
                    //トランザクションモードの変更
                    tmpRd.setTransactionModeForce(RowData.E_Record_TypeDeleteInsert);
                }else{
                    throw new InvalidDataException("key duplicate") ;
                }
            }
        }else{
            this.mRows.add(rd);
        }
        rd.setRowIndex(this.mRows.size() - 1);
        if(dynamicSearchKeyMap != null && dynamicSearchKeyMap.size() != 0){
            if(dynamicSearchMap == null){
                dynamicSearchMap = new HashMap();
            }
            final Iterator entries = dynamicSearchKeyMap.entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                final DynamicSearchKeyValue keyValue = (DynamicSearchKeyValue)entry.getValue();
                Map map = (Map)dynamicSearchMap.get(keyValue);
                if(map == null){
                    map = new HashMap();
                    dynamicSearchMap.put(keyValue, map);
                }
                final String myKey = rd.getKey(keyValue.colIndexes);
                Object values = map.get(myKey);
                if(values == null){
                    map.put(myKey, rd);
                }else{
                    if(values instanceof Map){
                        ((Map)values).put(rd.getKey(), rd);
                    }else{
                        final Map valMap = createOrderByMap(
                            keyValue.orderBy,
                            keyValue.isAsc
                        );
                        valMap.put(((RowData)values).getKey(), values);
                        valMap.put(rd.getKey(), rd);
                        map.put(myKey, valMap);
                    }
                }
            }
        }
        if(dynamicSearchConditionMap != null
             && dynamicSearchConditionMap.size() != 0){
            final Iterator entries
                 = dynamicSearchConditionMap.entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                final String name = (String)entry.getKey();
                final Expression exp = (Expression)entry.getValue();
                final JexlContext context = JexlHelper.createContext();
                for(int i = 0, imax = mSchema.size(); i < imax; i++){
                    final FieldSchema field = mSchema.get(i);
                    final String fieldName = field.getFieldName();
                    context.getVars().put(fieldName, rd.get(i));
                }
                Boolean ret = null;
                try{
                    ret = (Boolean)exp.evaluate(context);
                }catch(Exception e){
                    // 起こらないはず
                    e.printStackTrace();
                }
                if(ret != null && ret.booleanValue()){
                    Map values = (Map)dynamicSearchConditionResultMap.get(name);
                    values.put(rd, rd);
                }
            }
        }
    }
    
    /**
     * レコードを入れ替える。<p>
     * 入れ替えられたレコードのトランザクションモードは、変更されない。<br>
     * 
     * @param rd 入れ替えるRowData
     */
    public void setRecord(RowData rd){
        String key = rd.getKey();
        if(rd.mRowSchema != mSchema && mSchema.equals(rd.mRowSchema)){
            rd.mRowSchema = mSchema;
        }
        if(key != null && key.length() > 0){
            Object tmp = this.mHash.get(key);
            if(tmp == null){
                mHash.put(key, rd);
                mRows.add(rd);
            }else{
                RowData tmpRd = (RowData)tmp;
                mHash.remove(key);
                mRows.remove(tmpRd.getRowIndex());
                mHash.put(key, rd);
                mRows.add(rd);
                rd.setRowIndex(tmpRd.getRowIndex());
            }
        }else{
            this.mRows.add(rd);
        }
        rd.setRowIndex(this.mRows.size() - 1);
        if(dynamicSearchKeyMap != null && dynamicSearchKeyMap.size() != 0){
            if(dynamicSearchMap == null){
                dynamicSearchMap = new HashMap();
            }
            final Iterator entries = dynamicSearchKeyMap.entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                final DynamicSearchKeyValue keyValue = (DynamicSearchKeyValue)entry.getValue();
                Map map = (Map)dynamicSearchMap.get(keyValue);
                if(map == null){
                    map = new HashMap();
                    dynamicSearchMap.put(keyValue, map);
                }
                final String myKey = rd.getKey(keyValue.colIndexes);
                Object values = map.get(myKey);
                if(values == null){
                    map.put(myKey, rd);
                }else{
                    if(values instanceof Map){
                        ((Map)values).put(rd.getKey(), rd);
                    }else{
                        final Map valMap = createOrderByMap(
                            keyValue.orderBy,
                            keyValue.isAsc
                        );
                        valMap.put(((RowData)values).getKey(), values);
                        valMap.put(rd.getKey(), rd);
                        map.put(myKey, valMap);
                    }
                }
            }
        }
        if(dynamicSearchConditionMap != null
             && dynamicSearchConditionMap.size() != 0){
            final Iterator entries
                 = dynamicSearchConditionMap.entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                final String name = (String)entry.getKey();
                final Expression exp = (Expression)entry.getValue();
                final JexlContext context = JexlHelper.createContext();
                for(int i = 0, imax = mSchema.size(); i < imax; i++){
                    final FieldSchema field = mSchema.get(i);
                    final String fieldName = field.getFieldName();
                    context.getVars().put(fieldName, rd.get(i));
                }
                Boolean ret = null;
                try{
                    ret = (Boolean)exp.evaluate(context);
                }catch(Exception e){
                    // 起こらないはず
                    e.printStackTrace();
                }
                if(ret != null && ret.booleanValue()){
                    Map values = (Map)dynamicSearchConditionResultMap.get(name);
                    values.put(rd, rd);
                }
            }
        }
    }
    
    /**
     * 全てのレコードを追加する。<p>
     * 追加されたレコードのトランザクションモードは、変更されない。<br>
     * 
     * @param recset 追加するRecordSet
     * @exception InvalidDataException プライマリキーが重複する場合
     */
    public void addAllRecord(RecordSet recset){
        if(recset == null || recset.size() == 0){
            return;
        }
        if(mSchema == null){
            throw new InvalidDataException("Schema not initalize.");
        }
        if(!mSchema.equals(recset.getRowSchema())){
            throw new InvalidDataException("Schema is unmatch.");
        }
        for(int i = 0, imax = recset.size(); i < imax; i++){
            addRecord(recset.get(i));
        }
    }
    
    /**
     * 蓄積されたデータをクリアする。<p>
     * 以下のデータが削除される。<br>
     * <ul>
     *   <li>レコード</li>
     *   <li>PreparedStatementに埋め込むデータ</li>
     *   <li>蓄積型動的条件検索の検索結果</li>
     *   <li>動的キー検索の検索結果</li>
     * </ul>
     */
    public void clear(){
        mRows.clear();
        mHash.clear();
        if(bindDatas != null){
            bindDatas.clear();
        }
        if(dynamicSearchConditionResultMap != null
             && dynamicSearchConditionResultMap.size() != 0){
            final Iterator itr
                 = dynamicSearchConditionResultMap.values().iterator();
            while(itr.hasNext()){
                ((Map)itr.next()).clear();
            }
        }
        if(dynamicSearchMap != null){
            dynamicSearchMap.clear();
        }
    }
    
    /**
     * 追加・修正・削除されたRowDataをデータベースに反映する。<p>
     * 
     * @exception RowVersionException {@link #isEnabledRowVersionCheck()}がtrueで、更新及び削除時に、更新及び削除しようとした件数と、実際に更新及び削除した件数が等しくない場合
     * @exception SQLException 
     * @deprecated {@link #updateRecords()}に置き換えられました。
     */
    public void updateRecord() throws SQLException, RowVersionException {
        updateRecords();
    }
    
    /**
     * 追加・修正・削除されたRowDataをデータベースに反映する。<p>
     * 
     * @return 追加・修正・削除されたレコード数
     * @exception RowVersionException {@link #isEnabledRowVersionCheck()}がtrueで、更新及び削除時に、更新及び削除しようとした件数と、実際に更新及び削除した件数が等しくない場合
     * @exception SQLException 
     */
    public int updateRecords() throws SQLException, RowVersionException{
        
        int result = 0;
        int updateCnt = 0;
        int insertCnt = 0;
        int deleteCnt = 0;
        for(int i = 0, imax = size(); i < imax; i++){
            RowData rd = get(i);
            switch(rd.getTransactionMode()){
            case  RowData.E_Record_TypeInsert:
                insertCnt++;
                break ;
            case RowData.E_Record_TypeDelete:
                deleteCnt++;
                break ;
            case RowData.E_Record_TypeUpdate:
                updateCnt++ ;
                break ;
            case RowData.E_Record_TypeDeleteInsert:
                deleteCnt++;
                insertCnt++;
                break;
            }
        }
        PreparedStatement psDelete = null;
        PreparedStatement psInsert = null;
        PreparedStatement psUpdate = null;
        try{
            if(deleteCnt > 0){
                int nowDeleteCnt = 0;
                psDelete = createDeletePreparedStatement();
                if(psDelete != null){
                    nowDeleteCnt = executeDelete(psDelete);
                }
                if(isEnabledRowVersionCheck && nowDeleteCnt != deleteCnt){
                    throw new RowVersionException("Delete count is unmatch : expected " + deleteCnt + ", but was " + nowDeleteCnt);
                }
                result += nowDeleteCnt;
            }
            
            if(insertCnt > 0){
                psInsert = createInsertPreparedStatement();
                if (psInsert != null) {
                    result += executeInsert(psInsert);
                }
            }
            
            if(updateCnt > 0){
                int nowUpdateCnt = 0;
                psUpdate = createUpdatePreparedStatement();
                if (psUpdate != null) {
                    nowUpdateCnt = executeUpdate(psUpdate);
                }
                if(isEnabledRowVersionCheck && nowUpdateCnt != updateCnt){
                    throw new RowVersionException("Update count is unmatch : expected " + updateCnt + ", but was " + nowUpdateCnt);
                }
                result += nowUpdateCnt;
            }
        }finally{
            if(psInsert != null){
                psInsert.close();
            }
            if(psUpdate != null){
                psUpdate.close();
            }
            if(psDelete != null){
                psDelete.close();
            }
        }
        return result;
    }
    
    /**
     * INSERT用のPreparedStatementを作成する。<p>
     * 
     * @return INSERT用のPreparedStatement
     * @throws SQLException
     */    
    protected PreparedStatement createInsertPreparedStatement()
     throws SQLException {
        
        PreparedStatement ps = null;
        
        StringBuffer sb = new StringBuffer();
        sb.append(C_INSERT_TOKEN);
        sb.append(
            (this.mUpdateTableNames == null)
                 ? this.mTableNames : this.mUpdateTableNames
        );
        sb.append(C_BRACKETS_BEGIN_TOKEN);
        for(int i = 0, imax = mSchema.size(); i < imax; i++){
            // 更新項目のみを追加
            if(mSchema.get(i).isUpdateField()){
                sb.append(mSchema.get(i).getFieldName());
                if(i != imax - 1){
                    sb.append(C_COMMA_TOKEN);
                }
            }
        }
        if(sb.charAt(sb.length() - 1) == ','){
            sb.delete(sb.length() - 1, sb.length());
        }
        addInsertColmun(sb);
        sb.append(C_BRACKETS_END_TOKEN);
        sb.append(C_VALUES_TOKEN);
        sb.append(C_BRACKETS_BEGIN_TOKEN);
        for(int i = 0, imax = mSchema.size(); i < imax; i++){
            if(mSchema.get(i).isUpdateField()){
                sb.append(C_QUESTION_TOKEN);
                if(i != imax - 1){
                    sb.append(C_COMMA_TOKEN);
                }
            }
        }
        if(sb.charAt(sb.length() - 1) == ','){
            sb.delete(sb.length() - 1, sb.length());
        }
        addInsertField(sb);
        sb.append(C_BRACKETS_END_TOKEN);
        
        ps = mCon.prepareStatement(sb.toString());
        
        if(mLogger != null && mMessageCode != null){
            mLogger.write(mMessageCode, sb.toString());
        }
        return ps;
    }
    
    protected int executeInsert(PreparedStatement ps) throws SQLException{
        
        int insertCnt = 0;
        for(int i = 0, imax = size(); i < imax; i++){
            RowData rd = get(i);
            int param_idx = 1;
            if(rd.getTransactionMode() != RowData.E_Record_TypeInsert 
                && rd.getTransactionMode() != RowData.E_Record_TypeDeleteInsert){
                continue;
            }
            for(int j = 0, jmax = mSchema.size(); j < jmax; j++){
                final FieldSchema fs = mSchema.get(j);
                if(!fs.isUpdateField()){
                    continue;
                }
                if(fs.isRowVersionField()){
                    ps.setObject(param_idx++, new Integer(1));
                }else{
                    Object o = rd.getSqlTypeValue(j);
                    if(o == null){
                        ps.setNull(param_idx++, fs.getSqlType());
                    }else{
                        switch(fs.getFieldType()){
                        case FieldSchema.C_TYPE_BLOB:
                            byte[] bytes = (byte[])o;
                            ps.setBinaryStream(
                                param_idx++,
                                new ByteArrayInputStream(bytes),
                                bytes.length
                            );
                            break;
                        case FieldSchema.C_TYPE_CLOB:
                            char[] chars = (char[])o;
                            ps.setCharacterStream(
                                param_idx++,
                                new CharArrayReader(chars),
                                chars.length
                            );
                            break;
                        default:
                            if(fs.isCrypt()){
                                o = doCrypt(o);
                            }
                            ps.setObject(param_idx++, o);
                        }
                    }
                }
            }
            addInsertBind(ps, param_idx);
            if(isBatchExecute){
                ps.addBatch();
            }else{
                insertCnt += ps.executeUpdate();
            }
        }
        if(isBatchExecute){
            // Insert Batch 実行
            int[] ret = ps.executeBatch();
            if(ret != null){
                for(int i = 0; i < ret.length; i++){
                    if(ret[i] > 0){
                        insertCnt += ret[i];
                    }
                }
            }
            if(insertCnt == 0){
                insertCnt = ps.getUpdateCount();
            }
        }
        return insertCnt;
    }
    
    /**
     * 登録カラムを追加する。<p>
     * INSERT INTO TABLE_NAME (FIELD1,...[カラム追加部分] 
     * 
     * @param sb SQL ステートメント
     */
    protected void addInsertColmun(StringBuffer sb){
    }
    
    /**
     * 登録Fieldを追加する。<p>
     * INSERT INTO TABLE_NAME (FIELD1,...[カラム追加部分]
     * VALUES (VALUE1, VALUE2...[Field追加部分] 
     * 
     * @param sb SQL ステートメント
     */
    protected void addInsertField(StringBuffer sb){
    }

    /**
     * 追加登録Fieldにバインドする。<p>
     * INSERT INTO TABLE_NAME (FIELD1,...[カラム追加部分])
     * VLAUES (VALUE1,VALUE2...[バインド追加部分]); 
     * 
     * @param ps プリペアドステートメント
     * @param index バインドインデックス
     */
    protected void addInsertBind(PreparedStatement ps, int index) throws SQLException{
    }
    
    /**
     * UPDATE用のPreparedStatementを作成する。<p>
     * 
     * @return UPDATE用のPreparedStatement
     * @throws SQLException
     */    
    protected PreparedStatement createUpdatePreparedStatement()
     throws SQLException {
        
        PreparedStatement ps = null;
        StringBuffer sb = new StringBuffer();
        sb.append(C_UPDATE_TOKEN);
        sb.append(mUpdateTableNames == null ? mTableNames : mUpdateTableNames);
        sb.append(C_SET_TOKEN);
        for(int i = 0, imax = mSchema.size(); i < imax; i++){
            // 更新項目のみを追加
            if(mSchema.get(i).isUpdateField() && !mSchema.get(i).isUniqueKey()){
                sb.append(mSchema.get(i).getFieldName());
                sb.append(C_EQUAL_TOKEN);
                sb.append(C_QUESTION_TOKEN);
                if(i != imax - 1){
                    sb.append(C_COMMA_TOKEN);
                }
            }
        }
        if(sb.charAt(sb.length() - 1) == ','){
            sb.delete(sb.length() - 1, sb.length());
        }
        addUpdateField(sb);
        boolean whereFlg  = false;
        for(int i = 0, imax = mSchema.size(); i < imax; i++){
            // ユニークキーを条件文に追加
            FieldSchema fSchema = mSchema.get(i);
            if(fSchema.isUniqueKey() || fSchema.isRowVersionField()){
                if(!whereFlg){
                    sb.append(C_WHERE_TOKEN);
                    whereFlg = true;
                }
                sb.append(fSchema.getFieldName());
                sb.append(C_EQUAL_TOKEN);
                sb.append(C_QUESTION_TOKEN);
                sb.append(C_AND_TOKEN);
            }
        }
        if(whereFlg){
            // 最後のANDを削除
            sb.delete(sb.length() - 5, sb.length());
        }
        
        ps = mCon.prepareStatement(sb.toString());
        if(mLogger != null && mMessageCode != null){
            mLogger.write(mMessageCode,sb.toString());
        }
        return ps;
     }
     
     protected int executeUpdate(PreparedStatement ps) throws SQLException{
        
        int updateCnt = 0;
        for (int i = 0, imax = size(); i < imax; i++){
            RowData rd = get(i);
            int param_idx = 1;
            if(rd.getTransactionMode() != RowData.E_Record_TypeUpdate){
                continue;
            }
            for(int j = 0, jmax = mSchema.size(); j < jmax; j++){
                final FieldSchema fs = mSchema.get(j);
                // 更新項目をバインド
                if(!fs.isUpdateField() || fs.isUniqueKey()){
                    continue;
                }
                if(fs.isRowVersionField()){
                    ps.setObject(
                        param_idx++,
                        new Integer(rd.getIntValue(j) + 1)
                    );
                }else{
                    Object o = rd.getSqlTypeValue(j);
                    if(o == null){
                        ps.setNull(param_idx++, fs.getSqlType());
                    }else{
                        switch(fs.getFieldType()){
                        case FieldSchema.C_TYPE_BLOB:
                            byte[] bytes = (byte[])o;
                            ps.setBinaryStream(
                                param_idx++,
                                new ByteArrayInputStream(bytes),
                                bytes.length
                            );
                            break;
                        case FieldSchema.C_TYPE_CLOB:
                            char[] chars = (char[])o;
                            ps.setCharacterStream(
                                param_idx++,
                                new CharArrayReader(chars),
                                chars.length
                            );
                            break;
                        default:
                            if(fs.isCrypt()){
                                o = doCrypt(o);
                            }
                            ps.setObject(param_idx++, o);
                        }
                    }
                }
            }
            param_idx = addUpdateBind(ps, param_idx);
            for(int j = 0, jmax = mSchema.size(); j < jmax; j++){
                final FieldSchema fs = mSchema.get(j);
                // ユニークキーをバインド
                if(!fs.isUniqueKey() && !mSchema.get(j).isRowVersionField()){
                    continue;
                }
                Object o = rd.getSqlTypeValue(j);
                if(o == null){
                    ps.setNull(param_idx++, fs.getSqlType());
                }else{
                    switch(fs.getFieldType()){
                    case FieldSchema.C_TYPE_BLOB:
                        byte[] bytes = (byte[])o;
                        ps.setBinaryStream(
                            param_idx++,
                            new ByteArrayInputStream(bytes),
                            bytes.length
                        );
                        break;
                    case FieldSchema.C_TYPE_CLOB:
                        char[] chars = (char[])o;
                        ps.setCharacterStream(
                            param_idx++,
                            new CharArrayReader(chars),
                            chars.length
                        );
                        break;
                    default:
                        if(fs.isCrypt()){
                            o = doCrypt(o);
                        }
                        ps.setObject(param_idx++, o);
                    }
                }
            }
            if(isBatchExecute){
                ps.addBatch();
            }else{
                updateCnt += ps.executeUpdate();
            }
        }
        if(isBatchExecute){
            // Insert Batch 実行
            int[] ret = ps.executeBatch();
            if(ret != null){
                for(int i = 0; i < ret.length; i++){
                    if(ret[i] > 0){
                        updateCnt += ret[i];
                    }
                }
            }
            if(updateCnt == 0){
                updateCnt = ps.getUpdateCount();
            }
        }
        return updateCnt;
    }
    
    /**
     * 更新Fieldを追加する。<p>
     * UPDATE TABLE_NAME SET (FIELD1=?,...[追加部分] 
     * 
     * @param sb SQL ステートメント
     */
    protected void addUpdateField(StringBuffer sb){
    }
    
    /**
     * 追加更新Fieldにバインドする。<p>
     * UPDATE TABLE_NAME SET (FIELD1=?,...[Field追加部分])
     * 
     * @param ps プリペアドステートメント
     * @param index バインドインデックス
     * @return インクリメントされたバインドインデックス
     */
    protected int addUpdateBind(PreparedStatement ps, int index) throws SQLException{
        return index;
    }
    
    /**
     * DELETE用のPreparedStatementを作成する。<p>
     * 
     * @return DELETE用のPreparedStatement
     * @throws SQLException
     */    
    protected PreparedStatement createDeletePreparedStatement() throws SQLException{
        PreparedStatement ps = null;
        StringBuffer sb = new StringBuffer();
        sb.append(C_DELETE_TOKEN);
        sb.append(mUpdateTableNames == null ? mTableNames : mUpdateTableNames);
        boolean whereFlg  = false;
        for(int i = 0, imax = mSchema.size();i < imax; i++){
            // ユニークキーを条件文に追加
            if(mSchema.get(i).isUniqueKey()){
                if(!whereFlg){
                    sb.append(C_WHERE_TOKEN);
                    whereFlg = true;
                }
                sb.append(mSchema.get(i).getFieldName());
                sb.append(C_EQUAL_TOKEN);
                sb.append(C_QUESTION_TOKEN);
                sb.append(C_AND_TOKEN);
            }
        }
        if(whereFlg){
            // 最後のANDを削除
            sb.delete(sb.length() - 5, sb.length());
        }
        
        ps = mCon.prepareStatement(sb.toString());
        if(mLogger != null && mMessageCode != null){
            mLogger.write(mMessageCode, sb.toString());
        }
        return ps;
    }
    
    protected int executeDelete(PreparedStatement ps) throws SQLException{
        
        int deleteCnt = 0;
        for(int i = 0, imax = size(); i < imax; i++){
            RowData rd = this.get(i);
            int param_idx = 1;
            if(rd.getTransactionMode() != RowData.E_Record_TypeDelete
               && rd.getTransactionMode() != RowData.E_Record_TypeDeleteInsert){
                continue;
            }
            for(int j = 0; j < mSchema.size(); j++){
                final FieldSchema fs = mSchema.get(j);
                // ユニークキーをバインド
                if(!mSchema.get(j).isUniqueKey()){
                    continue;
                }
                Object o = rd.getSqlTypeValue(j);
                if(o == null){
                    ps.setNull(param_idx++, fs.getSqlType());
                }else{
                    switch(fs.getFieldType()){
                    case FieldSchema.C_TYPE_BLOB:
                        byte[] bytes = (byte[])o;
                        ps.setBinaryStream(
                            param_idx++,
                            new ByteArrayInputStream(bytes),
                            bytes.length
                        );
                        break;
                    case FieldSchema.C_TYPE_CLOB:
                        char[] chars = (char[])o;
                        ps.setCharacterStream(
                            param_idx++,
                            new CharArrayReader(chars),
                            chars.length
                        );
                        break;
                    default:
                        if(fs.isCrypt()){
                            o = doCrypt(o);
                        }
                        ps.setObject(param_idx++, rd.get(j));
                    }
                }
            }
            if(isBatchExecute){
                ps.addBatch();
            }else{
                deleteCnt += ps.executeUpdate();
            }
        }
        if(isBatchExecute){
            // Delete Batch 実行
            int[] ret = ps.executeBatch();
            if(ret != null){
                for(int i = 0; i < ret.length; i++){
                    if(ret[i] > 0){
                        deleteCnt += ret[i];
                    }
                }
            }
            if(deleteCnt == 0){
                deleteCnt = ps.getUpdateCount();
            }
        }
        return deleteCnt;
    }
    
    /**
     * 入力された文字列(オブジェクト)複号化する
     * @param obj 複号化対象オブジェクト(文字列)
     * @return 複号化された文字列(オブジェクト)
     */
    protected Object doEncrypt(Object obj){
        if(mCrypt == null || obj == null){
            return obj;
        }
        if(obj instanceof String){
            obj = mCrypt.doDecode((String)obj);
        }
        return obj;
    }
    
    /**
     * 入力された文字列(オブジェクト)を暗号化する。<p>
     * 
     * @param obj 暗号化対象オブジェクト(文字列)
     * @return 暗号化された文字列(オブジェクト)
     */
    protected Object doCrypt(Object obj){
        if(mCrypt == null || obj == null){
            return obj;
        }
        if(obj instanceof String){
            obj = mCrypt.doEncode((String)obj);
        }
        return obj;
    }
    
    /**
     * 空の複製を生成する。<p>
     *
     * @return 空の複製
     */
    public RecordSet cloneEmpty(){
        RecordSet newRecSet = null;
        try{
            newRecSet = (RecordSet)clone();
        }catch(CloneNotSupportedException e){
            //起こらない
            throw new RuntimeException(e);
        }
        newRecSet.mRows = new ArrayList();
        newRecSet.mHash = new HashMap();
        if(where != null){
            newRecSet.where = new StringBuffer(where.toString());
        }
        if(bindDatas != null){
            newRecSet.bindDatas = new ArrayList(bindDatas);
        }
        if(dynamicSearchKeyMap != null){
            newRecSet.dynamicSearchKeyMap = new HashMap(dynamicSearchKeyMap);
        }
        if(dynamicSearchConditionMap != null){
            newRecSet.dynamicSearchConditionMap = new HashMap(dynamicSearchConditionMap);
        }
        if(dynamicSearchConditionResultMap != null){
            newRecSet.dynamicSearchConditionResultMap = new HashMap();
            final Iterator entries
                 = dynamicSearchConditionResultMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                Map map = (Map)entry.getValue();
                if(map instanceof SortedMap){
                    map = new TreeMap(((SortedMap)map).comparator());
                }else{
                    map = new LinkedHashMap();
                }
                newRecSet.dynamicSearchConditionResultMap.put(
                    entry.getKey(),
                    map
                );
            }
        }
        newRecSet.dynamicSearchMap = null;
        if(partUpdateOrderBy != null){
            newRecSet.partUpdateOrderBy = new int[partUpdateOrderBy.length];
            System.arraycopy(
                partUpdateOrderBy,
                0,
                newRecSet.partUpdateOrderBy,
                0,
                partUpdateOrderBy.length
            );
        }
        if(partUpdateIsAsc != null){
            newRecSet.partUpdateIsAsc = new boolean[partUpdateIsAsc.length];
            System.arraycopy(
                partUpdateIsAsc,
                0,
                newRecSet.partUpdateIsAsc,
                0,
                partUpdateIsAsc.length
            );
        }
        return newRecSet;
    }
    
    /**
     * このインスタンスのシャローコピーを生成する。<p>
     * このインスタンスのレコードと、シャローコピーしたインスタンスは、同じレコード参照を持つ。レコード以外は、ディープコピーする。<br>
     *
     * @return このインスタンスのシャローコピー
     */
    public RecordSet shallowCopy(){
        final RecordSet recset = cloneEmpty();
        recset.mRows = new ArrayList(mRows);
        recset.mHash = new HashMap(mHash);
        if(dynamicSearchMap != null){
            recset.dynamicSearchMap = new HashMap();
            final Iterator entries
                 = dynamicSearchMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                final Map map = (Map)entry.getValue();
                final Map newMap = new HashMap();
                recset.dynamicSearchMap.put(entry.getKey(), newMap);
                final Iterator entries2
                     = map.entrySet().iterator();
                while(entries2.hasNext()){
                    Map.Entry entry2 = (Map.Entry)entries2.next();
                    if(entry2.getValue() instanceof Map){
                        newMap.put(
                            entry2.getKey(),
                            new LinkedHashMap((Map)entry2.getValue())
                        );
                    }else{
                        newMap.put(
                            entry2.getKey(),
                            entry2.getValue()
                        );
                    }
                }
            }
        }
        if(recset.dynamicSearchConditionResultMap != null){
            final Iterator entries
                 = recset.dynamicSearchConditionResultMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                Map src = (Map)dynamicSearchConditionResultMap.get(entry.getKey());
                ((Map)entry.getValue()).putAll(src);
            }
        }
        return recset;
    }
    
    /**
     * このインスタンスのディープコピーを生成する。<p>
     * このインスタンスのレコードと、ディープコピーしたインスタンスは、同じレコードではあるが、異なる参照のレコードを持つ。<br>
     *
     * @return このインスタンスのディープコピー
     */
    public RecordSet deepCopy(){
        return (RecordSet)cloneAndUpdate(null);
    }
    
    /**
     *  部分更新時に、指定された列名の列をソートキーにしてソートするように設定する。<p>
     *
     * @param orderBy ソートキーとなる列名配列
     * @param isAsc 昇順ソートする場合はtrue。降順ソートする場合は、false
     */
    public void setPartUpdateSort(String[] orderBy, boolean[] isAsc){
        setPartUpdateSort(convertFromColNamesToColIndexes(orderBy), isAsc);
    }
    
    /**
     *  部分更新時に、指定された列インデックスの列をソートキーにしてソートするように設定する。<p>
     *
     * @param orderBy ソートキーとなる列インデックス配列
     * @param isAsc 昇順ソートする場合はtrue。降順ソートする場合は、false
     */
    public void setPartUpdateSort(int[] orderBy, boolean[] isAsc){
        partUpdateOrderBy = orderBy;
        partUpdateIsAsc = isAsc;
    }
    
    /**
     * 更新情報を格納したコードマスタ部分更新レコードを生成する。<p>
     *
     * @return  追加、削除、更新レコードのプライマリキーを持つ{@link CodeMasterUpdateKey}を格納したコードマスタ部分更新レコード
     */
    public PartUpdateRecords createPartUpdateRecords(){
        PartUpdateRecords records = new PartUpdateRecords();
        for(int i = size(); --i >= 0;){
            RowData rd = get(i);
            int tmode = rd.getTransactionMode();
            if(tmode == RowData.E_Record_TypeDelete
                 || tmode == RowData.E_Record_TypeInsert
                 || tmode == RowData.E_Record_TypeUpdate
                 || tmode == RowData.E_Record_TypeDeleteInsert){
                records.addRecord(rd.createCodeMasterUpdateKey());
            }
        }
        return records;
    }
    
    /**
     * 部分更新情報を取り込んだ、ディープコピーインスタンスを生成する。<p>
     *
     * @param records 部分更新情報
     * @return 部分更新情報を取り込んだ、ディープコピーインスタンス
     */
    public PartUpdate cloneAndUpdate(PartUpdateRecords records){
        final RecordSet newRecSet = cloneEmpty();
        
        CodeMasterUpdateKey tmpKey = new CodeMasterUpdateKey();
        CodeMasterUpdateKey key = null;
        final Iterator oldRows = mRows.iterator();
        while(oldRows.hasNext()){
            final RowData oldRow = (RowData)oldRows.next();
            tmpKey = oldRow.createCodeMasterUpdateKey(tmpKey);
            key = records == null ? null : records.getKey(tmpKey);
            RowData newRow = null;
            if(key == null){
                newRow = oldRow.cloneRowData();
            }else{
                switch(key.getUpdateType()){
                case CodeMasterUpdateKey.UPDATE_TYPE_ADD:
                case CodeMasterUpdateKey.UPDATE_TYPE_UPDATE:
                    newRow = (RowData)records.removeRecord(key);
                    break;
                case CodeMasterUpdateKey.UPDATE_TYPE_REMOVE:
                default:
                    records.removeRecord(key);
                    continue;
                }
            }
            if(newRow != null){
                newRecSet.addRecord(newRow);
            }
        }
        if(records != null && records.size() != 0){
            final Iterator entries = records.getRecords().entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                if(((CodeMasterUpdateKey)entry.getKey()).getUpdateType()
                     == CodeMasterUpdateKey.UPDATE_TYPE_ADD){
                    final RowData row = (RowData)entry.getValue();
                    if(row != null){
                        newRecSet.addRecord(row);
                    }
                }
            }
        }
        if(partUpdateOrderBy != null && partUpdateOrderBy.length != 0){
            newRecSet.sort(partUpdateOrderBy, partUpdateIsAsc);
        }
        
        return newRecSet;
    }
    
    /**
     * 指定されたコードマスタ更新キーに該当するレコードを格納した部分更新情報を作成する。<p>
     *
     * @param key コードマスタ更新キー
     * @return 更新レコードを含んだ部分更新情報
     */
    public PartUpdateRecords fillPartUpdateRecords(CodeMasterUpdateKey key){
        PartUpdateRecords records = new PartUpdateRecords();
        records.addRecord(key);
        return fillPartUpdateRecords(records);
    }
    
    /**
     * 指定された部分更新情報に該当するレコードを格納した部分更新情報を作成する。<p>
     *
     * @param records 部分更新情報
     * @return 更新レコードを含んだ部分更新情報
     */
    public PartUpdateRecords fillPartUpdateRecords(PartUpdateRecords records){
        if(records == null || records.size() == 0
             || (!records.containsAdd() && !records.containsUpdate())){
            return records;
        }
        records.setFilledRecord(true);
        RowData row = createNewRecord();
        final CodeMasterUpdateKey[] keys = records.getKeyArray();
        for(int i = 0; i < keys.length; i++){
            CodeMasterUpdateKey key = (CodeMasterUpdateKey)keys[i];
            final int updateType = key.getUpdateType();
            
            records.removeRecord(key);
            
            // 検索用のRowDataに検索キーを設定する
            row.setCodeMasterUpdateKey(key);
            
            // このRecordSetの主キーのみを持ったCodeMasterUpdateKeyに変換する
            key = row.createCodeMasterUpdateKey(key);
            key.setUpdateType(updateType);
            
            // 削除の場合は、CodeMasterUpdateKeyだけ登録し直す
            if(key.isRemove()){
                records.addRecord(key);
                continue;
            }
            
            // 追加または更新されたRowDataを検索する
            final RowData searchRow = get(row);
            records.addRecord(key, searchRow);
        }
        return records;
    }
    
    private static class DynamicSearchKeyValue implements Serializable{
        
        private static final long serialVersionUID = -2327997182252855059L;
        
        public int[] colIndexes;
        public int[] orderBy;
        public boolean[] isAsc;
        public DynamicSearchKeyValue(
            int[] colIndexes,
            int[] orderBy,
            boolean[] isAsc
        ){
            this.colIndexes = colIndexes;
            this.orderBy = orderBy;
            this.isAsc = isAsc;
        }
    }
    
    private static class RowDataComparator implements Comparator, Serializable{
        
        private static final long serialVersionUID = -9111641214052663144L;
        
        private RowSchema rowSchema;
        private int[] colIndexes;
        private boolean[] isAsc;
        
        public RowDataComparator(RowSchema schema, String[] colNames){
            this(schema, colNames, null);
        }
        
        public RowDataComparator(RowSchema schema, String[] colNames, boolean[] isAsc){
            rowSchema = schema;
            if(rowSchema == null){
                throw new InvalidDataException("Schema not initalize.");
            }
            if(colNames == null || colNames.length == 0){
                throw new IllegalArgumentException("Column name array is empty.");
            }
            if(isAsc != null && colNames.length != isAsc.length){
                throw new IllegalArgumentException("Length of column name array and sort flag array is unmatch.");
            }
            colIndexes = new int[colNames.length];
            for(int i = 0; i < colNames.length; i++){
                final FieldSchema field = rowSchema.get(colNames[i]);
                if(field == null){
                    throw new IllegalArgumentException("Field not found : " + colNames[i]);
                }
                colIndexes[i] = field.getIndex();
            }
            if(colIndexes == null || colIndexes.length == 0){
                throw new IllegalArgumentException("Column index array is empty.");
            }
            this.isAsc = isAsc;
        }
        
        public RowDataComparator(RowSchema schema, int[] colIndexes){
            this(schema, colIndexes, null);
        }
        
        public RowDataComparator(RowSchema schema, int[] colIndexes, boolean[] isAsc){
            rowSchema = schema;
            if(rowSchema == null){
                throw new InvalidDataException("Schema not initalize.");
            }
            if(colIndexes == null || colIndexes.length == 0){
                throw new IllegalArgumentException("Column index array is empty.");
            }
            if(isAsc != null && colIndexes.length != isAsc.length){
                throw new IllegalArgumentException("Length of column index array and sort flag array is unmatch.");
            }
            this.colIndexes = colIndexes;
            this.isAsc = isAsc;
        }
        
        public int compare(Object o1, Object o2){
            final RowData rd1 = (RowData)o1;
            final RowData rd2 = (RowData)o2;
            if(rd1 == null && rd2 == null){
                return 0;
            }
            if(rd1 != null && rd2 == null){
                return 1;
            }
            if(rd1 == null && rd2 != null){
                return -1;
            }
            for(int i = 0; i < colIndexes.length; i++){
                Object val1 = rd1.get(colIndexes[i]);
                Object val2 = rd2.get(colIndexes[i]);
                if(val1 != null && val2 == null){
                    return (isAsc == null || isAsc[i]) ? 1 : -1;
                }
                if(val1 == null && val2 != null){
                    return (isAsc == null || isAsc[i]) ? -1 : 1;
                }
                if(val1 != null && val2 != null){
                    final FieldSchema field = rowSchema.get(colIndexes[i]);
                    final int fieldType = field.getFieldType();
                    int comp = 0;
                    switch(fieldType){
                    case FieldSchema.C_TYPE_INT:
                    case FieldSchema.C_TYPE_LONG:
                    case FieldSchema.C_TYPE_FLOAT:
                    case FieldSchema.C_TYPE_DOUBLE:
                    case FieldSchema.C_TYPE_STRING:
                    case FieldSchema.C_TYPE_CHAR:
                    case FieldSchema.C_TYPE_DATE:
                    case FieldSchema.C_TYPE_TIMESTAMP:
                        comp = ((Comparable)val1).compareTo(val2);
                        break;
                    case FieldSchema.C_TYPE_BLOB:
                    case FieldSchema.C_TYPE_CLOB:
                    default:
                        comp = val1.hashCode() - val2.hashCode();
                        break;
                    }
                    if(comp != 0){
                        return (isAsc == null || isAsc[i]) ? comp : -1 * comp;
                    }
                }
            }
            return 0;
        }
    }
}
