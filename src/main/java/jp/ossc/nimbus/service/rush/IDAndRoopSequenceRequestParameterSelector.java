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
package jp.ossc.nimbus.service.rush;

import java.util.*;

import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordList;

/**
 * クライアント番号、ループ番号リクエストパラメータセレクタ。<p>
 * パラメータリストから、「(クライアント番号 * 区間数 + ループ番号) % パラメータリスト数」で計算したインデックスのパラメータマップを選択する。<br/>
 *
 * @author M.Takata
 */
public class IDAndRoopSequenceRequestParameterSelector
 implements RequestParameterSelector{
    
    private int periodCount;
    
    /**
     * 区間数を設定する。<p>
     *
     * @param 区間数
     */
    public void setPeriodCount(int count){
        periodCount = count;
    }
    
    public Map getParameter(int id, int roopCount, int count, RecordList parameterList){
        Map params = null;
        if(parameterList != null){
            if((id * periodCount + roopCount) > (parameterList.size() - 1)){
                params = new HashMap(
                    parameterList.getRecord(
                        (id * periodCount + roopCount) % parameterList.size()
                    )
                );
            }else{
                params = new HashMap(parameterList.getRecord(
                    id * periodCount + roopCount
                ));
            }
        }
        return params;
    }
}
