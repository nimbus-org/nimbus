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

import java.util.ArrayList;
import java.util.Iterator;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.lang.ServiceException;
import jp.ossc.nimbus.util.CsvArrayList;
import jp.ossc.nimbus.util.StringOperator;

/**
 * 数字発番サービス。<p>
 * 
 * @author H.Nakano
 */
public class NumericSequenceService extends ServiceBase
 implements  Sequence, NumericSequenceServiceMBean {
    
    private static final long serialVersionUID = -3368735884570934622L;
    
    private static final String C_ZERO = "0" ; //$NON-NLS-1$
    private static final String C_ZERO_WITH_COMMMA = "0," ; //$NON-NLS-1$
    private static final String C_SEMICORON = ";" ;//$NON-NLS-1$
    private static final String C_NINE = "9" ;//$NON-NLS-1$
    
    //## メンバー変数宣言 ##
    
    /** シーケンス番号 */
    protected ArrayList mSequenceNo;
    
    /** フォーマット文字列 */
    protected String mFormat;
    
    /** 最小値 */
    protected String mMin;
    
    /** 最大値 */
    protected String mMax;
    
    /** 開始時番号 */
    protected String mInitialNumber = "";
    
    /** 開始フラグ(最初のincrementまでtrue)*/
    protected boolean mInitialFlag = true;
    
    // NumericSequenceServiceMBean のJavaDoc
    public void setFormat(String format){
        synchronized(this){
            // formatを桁区切りで分解する
            CsvArrayList parser = new CsvArrayList(); 
            parser.split(format,C_SEMICORON);
            if(parser.size() != 2){
                throw new ServiceException(
                    "NUMERICSEQ001",
                    "fromat is invalid format = "+ format
                ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            mMin = parser.getStr(0) ;
            mMax = parser.getStr(1) ;
            if(!StringOperator.isNumeric(mMin)){
                throw new ServiceException(
                    "NUMERICSEQ002",
                    "MIN is not numeric min = " + mMin
                ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if(!StringOperator.isNumeric(mMax)){
                throw new ServiceException(
                    "NUMERICSEQ003",
                    "MAX is not numeric max = " + mMax
                ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            StringBuffer tmpFormat = new StringBuffer() ;
            for(int cnt = 0; cnt < mMax.length(); cnt++){
                tmpFormat.append(C_ZERO_WITH_COMMMA);
                tmpFormat.append(C_NINE);
                if(cnt != mMax.length() - 1){
                    tmpFormat.append(C_SEMICORON);
                }
            }
            mFormat = tmpFormat.toString() ;
        }
    }
    
    // NumericSequenceServiceMBean のJavaDoc
    public String getFormat(){
        return mFormat;
    }
    
    // NumericSequenceServiceMBean のJavaDoc
    public String getMinValue(){
        return mMin;
    }
    
    // NumericSequenceServiceMBean のJavaDoc
    public String getMaxValue(){
        return mMax;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     * 
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        mSequenceNo = new ArrayList();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     * 
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService(){
        synchronized(this){
            // formatを桁区切りで分解する
            CsvArrayList parser = new CsvArrayList();
            parser.split(mFormat, C_SEMICORON);
            // 桁情報リストをインスタンシングする
            mSequenceNo = new ArrayList();
            // 各桁情報をインスタンシングしてリストに格納する
            for(Iterator iterator = parser.iterator(); iterator.hasNext();){
                String formatItem = (String)iterator.next();
                final SequenceVariable item
                     = new SimpleSequenceVariable(formatItem);
                mSequenceNo.add(item);
            }
            mInitialFlag = true;
            mInitialNumber = "";
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     * 
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService(){
        mSequenceNo.clear();
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     * 
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService(){
        mSequenceNo = null;
    }
    
    // SequenceのJavaDoc
    public String increment(){
        StringBuffer retStr = new StringBuffer();
        synchronized(this){
            // 桁数の深さを取得する
            int maxCnt = mSequenceNo.size();
            // １けた目からインクリメントを開始する
            for(int rCnt = --maxCnt; rCnt >= 0; rCnt--){
                SequenceVariable item = (SequenceVariable)mSequenceNo.get(rCnt);
                //increment
                boolean isOverFlow = item.increment();
                // オーバーフローしない場合はけた上がりなし
                if(!isOverFlow){
                    break;
                }
            }
            // カレント文字を合成し発番文字を生成する
            boolean isFirst = false;
            for(Iterator iterator = mSequenceNo.iterator(); iterator.hasNext();){
                SequenceVariable item = (SequenceVariable)iterator.next();
                if(!isFirst){
                    String tmp = item.getCurrent();
                    if(!tmp.equals(C_ZERO)){
                        isFirst = true;
                    }
                }
                if(isFirst){
                    retStr.append(item.getCurrent());
                }
            }
            if(retStr.toString().compareTo(mMin) < 0){
                retStr = new StringBuffer(increment());
            }
            if(retStr.toString().length() >= mMax.length()
                 && retStr.toString().compareTo(mMax) > 0){
                reset();
                retStr = new StringBuffer(increment());
            }
            // 開始フラグがtrueであれば、開始時番号として保存
            if(mInitialFlag){
                mInitialNumber = retStr.toString();
                mInitialFlag = false;                                    
            }
        }
        return retStr.toString();
    }
    
    // SequenceのJavaDoc
    public void reset(){
        synchronized(this){
            // カレント文字を合成し発番文字を生成する
            for(Iterator iterator = mSequenceNo.iterator(); iterator.hasNext();){
                SequenceVariable item = (SequenceVariable)iterator.next();
                item.clear();
            }
            // 開始フラグをtrueにする。
            mInitialFlag = true;
            mInitialNumber = "";
        }
    }
    
    // SequenceのJavaDoc
    public String getInitial(){
        return mInitialNumber;
    }
    
    // SequenceのJavaDoc
    public String getCurrent(){
        StringBuffer retStr = new StringBuffer();
        synchronized(this){
            // カレント文字を合成し発番文字を生成する
            boolean isFirst = false ;
            for(Iterator iterator = mSequenceNo.iterator(); iterator.hasNext();){
                SequenceVariable item = (SequenceVariable)iterator.next();
                if(!isFirst){
                    String tmp = item.getCurrent();
                    if(!tmp.equals(C_ZERO)){
                        isFirst = true;
                    }
                }
                if(isFirst){
                    retStr.append(item.getCurrent());
                }
            }
        }
        return retStr.toString();
    }
}
