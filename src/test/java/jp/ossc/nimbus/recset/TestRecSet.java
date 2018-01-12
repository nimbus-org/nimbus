// パッケージ
// インポート
package jp.ossc.nimbus.recset;

/**
 * ファイル操作クラス<p>
 * ファイルのコピーやリネームと言った操作を行う
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 * @see 
 */
public class TestRecSet extends RecordSet {

    private static final long serialVersionUID = -393903869827407791L;
    /** スキーマ定義 */
    private static final String C_SCHEMA =
       "OPERATIONDATE,CHAR,8,3,0" + C_SEPARATOR
     + "GYOUMUCD,CHAR,3,3,0" + C_SEPARATOR
     + "MEMOSEQ,INT,2,3,0" + C_SEPARATOR
	 + "LINENO,INT,3,3,0" + C_SEPARATOR
	 + "MEMODATA,VARCHAR,100,3,0" + C_SEPARATOR
	 + "ROWVERSION,INT,9,1,0";
    /** テーブル名 */
    private static final String TABLE_NAME = "COMMON_MEMO_DATA";
	public TestRecSet() {
		/**
		 * コンストラクター
		 */
		super();
        super.initSchema(C_SCHEMA);
        super.setFromTable(TABLE_NAME);
        super.setOrderbyStr("MEMOSEQ,LINENO");
	}

}
