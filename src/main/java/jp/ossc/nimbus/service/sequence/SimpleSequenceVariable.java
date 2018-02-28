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
package jp.ossc.nimbus.service.sequence;

import jp.ossc.nimbus.util.CsvArrayList;

/**
 * 単純桁数管理クラス。<p>
 * 各桁文字の開始文字、終了文字を管理してカレント文字の桁上がり操作を行う。<br>
 * 
 * @author H.Nakano
 */
public class SimpleSequenceVariable
 implements SequenceVariable, java.io.Serializable{
    
    private static final long serialVersionUID = -2852302890121212311L;
    
    public static final String DELIMITER = "," ; //$NON-NLS-1$
    
    //## メンバー変数宣言 ##
    
    /** 開始値 */
    private String mStartVal;
    /** 終了値 */
    private String mEndVal;
    /** カレント番号 */
    private String mCurrentVal;
    
    /**
     * コンストラクタ。<p>
     * 開始値、終了値をパースしてメンバ変数にセットして現在値に開始値をセットする。<br>
     * 
     * @param format 開始文字,終了文字 形式の文字列
     */
    public SimpleSequenceVariable(String format){
        this(format, null);
    }
    
    /**
     * コンストラクタ。<p>
     * 開始値、終了値をパースしてメンバ変数にセットする。また、指定された現在値をメンバ変数にセットする。<br>
     * 
     * @param format 開始文字,終了文字 形式の文字列
     * @param current 現在値
     */
    public SimpleSequenceVariable(String format, String current){
        // formatを開始値と終了値に分解する
        CsvArrayList parser = new CsvArrayList();
        parser.split(format,DELIMITER);
        
        // 開始値、終了値、現在値をメンバ変数にセットする
        this.mStartVal=parser.getStr(0);
        this.mEndVal=parser.getStr(1);
        this.mCurrentVal=current == null ? this.mStartVal : current;
    }
    
    // SequenceVariable のJavaDoc
    public boolean increment(){
        // 現在値をインクリメントする。
        char [] cValtmp = this.mCurrentVal.toCharArray();
        cValtmp[0]++;
        String incVal = new String(cValtmp);
        // インクリメントした結果が終了値を超えていないかチェックする
        if(incVal.compareTo(mEndVal) > 0){
            mCurrentVal = mStartVal;
            return true;
        }else{
            mCurrentVal = incVal;
        }
        return false;
    }
    
    // SequenceVariable のJavaDoc
    public void clear(){
        mCurrentVal = mStartVal;
    }
    
    // SequenceVariable のJavaDoc
    public String getCurrent(){
        return this.mCurrentVal;
    }
}
