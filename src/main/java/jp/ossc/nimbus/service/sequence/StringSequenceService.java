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

import java.util.*;
import java.io.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.CsvArrayList;
//
/**
 * 文字列発番サービス。<p>
 *
 * @author H.Nakano
 */
public class StringSequenceService extends ServiceBase
 implements Sequence, StringSequenceServiceMBean {
    
    private static final long serialVersionUID = 7784010436618007574L;
    
    private static final String C_SEMICORON = ";" ;  //$NON-NLS-1$
    
    //## メンバー変数宣言 ##
    
    /** シーケンス番号  */
    protected ArrayList mSequenceNo;
    
    /** フォーマット文字列 */
    protected String mFormat;
    
    /** 開始時番号 */
    protected String mInitialNumber = "";
    
    /** 開始フラグ */
    protected boolean mInitialFlag = true;
    
    /** コンテキストサービス名 */
    protected ServiceName contextServiceName;
    
    /** 永続化ファイル名 */
    protected String persistFile;
    
    /** 発番毎永続化フラグ */
    protected boolean isPersistEveryTime;
    
    // StringSequenceServiceMBean のJavaDoc
    public void setFormat(String format){
        mFormat = format;
    }
    
    // StringSequenceServiceMBean のJavaDoc
    public String getFormat(){
        return mFormat;
    }
    
    // StringSequenceServiceMBean のJavaDoc
    public void setContextServiceName(ServiceName name){
        contextServiceName = name;
    }
    
    // StringSequenceServiceMBean のJavaDoc
    public ServiceName getContextServiceName(){
        return contextServiceName;
    }
    
    // StringSequenceServiceMBean のJavaDoc
    public void setPersistFile(String file){
        persistFile = file;
    }
    
    // StringSequenceServiceMBean のJavaDoc
    public String getPersistFile(){
        return persistFile;
    }
    
    // StringSequenceServiceMBean のJavaDoc
    public void setPersistEveryTime(boolean isEveryTime){
        isPersistEveryTime = isEveryTime;
    }
    
    // StringSequenceServiceMBean のJavaDoc
    public boolean isPersistEveryTime(){
        return isPersistEveryTime;
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
    public void startService() throws Exception{
        
        mInitialFlag = true;
        mInitialNumber = "";
        
        // formatを桁区切りで分解する
        CsvArrayList parser = new CsvArrayList();
        parser.split(mFormat, C_SEMICORON);
        
        CsvArrayList persist = null;
        if(persistFile != null){
            final File file = new File(persistFile);
            if(file.exists()){
                final FileReader fr = new FileReader(file);
                final BufferedReader br = new BufferedReader(fr);
                try{
                    persist = new CsvArrayList();
                    persist.split(br.readLine(),C_SEMICORON);
                    if(parser.size() != persist.size()){
                        persist = null;
                    }
                }finally{
                    fr.close();
                }
            }else if(file.getParentFile() != null
                 && !file.getParentFile().exists()){
                file.mkdirs();
            }
        }
        
        // 各桁情報をインスタンシングしてリストに格納する
        for(int i = 0, max = parser.size(); i < max; i++){
            String formatItem = (String)parser.get(i);
            String persistItem = null;
            if(persist != null){
                persistItem = (String)persist.get(i);
            }
            SequenceVariable item = null;
            if(formatItem.startsWith(TimeSequenceVariable.FORMAT_KEY)){
                item = new TimeSequenceVariable(formatItem);
            }else if(formatItem.indexOf(SimpleSequenceVariable.DELIMITER) != -1){
                item = new SimpleSequenceVariable(formatItem, persistItem);
            }else if(formatItem.length() > 2
                && formatItem.charAt(0)
                     == ContextSequenceVariable.DELIMITER
                && formatItem.charAt(formatItem.length() - 1)
                     == ContextSequenceVariable.DELIMITER
            ){
                item = new ContextSequenceVariable(
                    formatItem,
                    contextServiceName
                );
            }else{
                item = new ConstSequenceVariable(formatItem);
            }
            this.mSequenceNo.add(item);
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     * 
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService(){
        if(persistFile != null){
            persistSequence();
        }
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
        StringBuilder retStr = new StringBuilder();
        synchronized(this){
            // 桁数の深さを取得する
            int maxCnt = mSequenceNo.size();
            
            // １けた目からインクリメントを開始する
            for(int rCnt = --maxCnt; rCnt >= 0; rCnt--){
                final SequenceVariable item
                     = (SequenceVariable)mSequenceNo.get(rCnt) ;
                //increment
                boolean isOverFlow = item.increment();
                // オーバーフローしない場合はけた上がりなし
                if(!isOverFlow){
                    break;
                }
            }
            // カレント文字を合成し発番文字を生成する
            for(Iterator iterator = mSequenceNo.iterator(); iterator.hasNext();){
                SequenceVariable item = (SequenceVariable)iterator.next();
                retStr.append(item.getCurrent());
            }
            if(mInitialFlag){
                //開始番号を保存
                mInitialNumber = retStr.toString();
                mInitialFlag = false;
            }
        }
        if(persistFile != null && isPersistEveryTime){
            persistSequence();
        }
        return retStr.toString();
    }
    
    /**
     * 現在発番されている最後の番号をファイルに永続化する。<p>
     */
    protected void persistSequence(){
        FileWriter fw = null;
        try{
            fw = new FileWriter(persistFile, false);
            final StringBuilder buf = new StringBuilder();
            for(Iterator itr = mSequenceNo.iterator(); itr.hasNext();){
                SequenceVariable item = (SequenceVariable)itr.next();
                buf.append(item.getCurrent());
                if(itr.hasNext()){
                    buf.append(C_SEMICORON);
                }
            }
            fw.write(buf.toString(), 0, buf.length());
        }catch(IOException e){
        }finally{
            if(fw != null){
                try{
                    fw.close();
                }catch(IOException e){
                }
            }
        }
    }
    
    // SequenceのJavaDoc
    public void reset(){
        synchronized(this){
            /** カレント文字を合成し発番文字を生成する */
            for(Iterator iterator = mSequenceNo.iterator(); iterator.hasNext();){
                final SequenceVariable item = (SequenceVariable)iterator.next();
                item.clear();
            }
            //開始フラグをtrueにする。
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
        StringBuilder retStr = new StringBuilder();
        synchronized(this){
            for(Iterator iterator = mSequenceNo.iterator(); iterator.hasNext();){
                final SequenceVariable item = (SequenceVariable)iterator.next();
                retStr.append(item.getCurrent());
            }
        }
        return retStr.toString();
    }
}
