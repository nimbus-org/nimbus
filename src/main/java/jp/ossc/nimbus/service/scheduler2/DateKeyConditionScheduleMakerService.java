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
package jp.ossc.nimbus.service.scheduler2;

import java.util.Date;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.scheduler.DateKey;
import jp.ossc.nimbus.service.scheduler.DateEvaluator;


/**
 * DateKey条件スケジュール作成サービス。<p>
 * スケジュールの作成有無の判定をスケジュールタイプをDateKeyと解釈して処理する。<br>
 *
 * @author M.Takata
 */
public class DateKeyConditionScheduleMakerService
 extends DefaultScheduleMakerService
 implements DateKeyConditionScheduleMakerServiceMBean{
    
    private static final long serialVersionUID = 1985942168084980639L;
    
    private String dateKeyStr;
    private DateKey dateKey;
    private ServiceName dateEvaluatorServiceName;
    private DateEvaluator dateEvaluator;
    
    // DateMappingScheduleFactoryServiceMBeanのJavaDoc
    public void setDateKey(String key){
        dateKeyStr = key;
    }
    // DateMappingScheduleFactoryServiceMBeanのJavaDoc
    public String getDateKey(){
        return dateKeyStr;
    }
    
    // DateMappingScheduleFactoryServiceMBeanのJavaDoc
    public void setDateEvaluatorServiceName(ServiceName name){
        dateEvaluatorServiceName = name;
    }
    
    // DateMappingScheduleFactoryServiceMBeanのJavaDoc
    public ServiceName getDateEvaluatorServiceName(){
        return dateEvaluatorServiceName;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(dateEvaluatorServiceName != null){
            dateEvaluator = (DateEvaluator)ServiceManagerFactory
                .getServiceObject(dateEvaluatorServiceName);
        }
        dateKey = new DateKey(dateKeyStr, dateEvaluator);
        super.startService();
    }
    
    /**
     * {@link DateEvaluator}を設定する。<p>
     * 日付拡張キーを使用する場合に設定する。<br>
     *
     * @param evaluator DateEvaluator
     */
    public void setDateEvaluator(DateEvaluator evaluator){
        dateEvaluator = evaluator;
    }
    
    /**
     * この日付で、スケジュールを作成する必要があるかどうかを判定する。<p>
     * 引数の作成日と設定された{@link DateKey}が合致する場合、スケジュールを作成する。<br>
     *
     * @param date 作成日
     * @param master スケジュールマスタ
     * @return trueの場合、作る必要がある
     * @exception ScheduleMakeException 判定に失敗した場合
     */
    protected boolean isNecessaryMake(Date date, ScheduleMaster master)
     throws ScheduleMakeException{
        try{
            return dateKey.equalsDate(date);
        }catch(Exception e){
            throw new ScheduleMakeException(e);
        }
    }
}