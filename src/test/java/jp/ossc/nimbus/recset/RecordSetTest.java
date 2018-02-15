// パッケージ
package jp.ossc.nimbus.recset;
//インポート

import junit.framework.TestCase;

/**
 * ファイル操作クラス<p>
 * ファイルのコピーやリネームと言った操作を行う
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 * @see 
 */
public class RecordSetTest extends TestCase {
	//メッセージファイルパスを１個指定した場合
	public void testInsertRecord() throws Exception{
		TestRecSet tmp = new TestRecSet() ;
		RowData rd = tmp.createNewRecord() ;
		rd.setValue("OPERATIONDATE","20030303");
		rd.setValue("GYOUMUCD","111") ;
		rd.setValue("MEMOSEQ",1) ;
		rd.setValue("LINENO",1) ;
		rd.setValue("MEMODATA","aaaa") ;
		tmp.insertRecord(rd);
		rd = tmp.createNewRecord() ;
		rd.setValue("OPERATIONDATE","20030303");
		rd.setValue("GYOUMUCD","111") ;
		rd.setValue("MEMOSEQ",1) ;
		rd.setValue("LINENO",1) ;
		rd.setValue("MEMODATA","aaaa") ;
		tmp.insertRecord(rd);
	}

}
