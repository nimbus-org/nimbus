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

import jp.ossc.nimbus.core.*;

/**
 * {@link DateKeyConditionScheduleMakerService}サービスのMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface DateKeyConditionScheduleMakerServiceMBean
 extends DefaultScheduleMakerServiceMBean{
    
    /**
     * 日付キー文字列を設定する。<p>
     * 日付キー文字列は、{@link jp.ossc.nimbus.service.scheduler.DateKey DateKey}で解釈される。<br>
     *
     * @param key 日付キー文字列
     */
    public void setDateKey(String key);
    
    /**
     * 日付キー文字列を取得する。<p>
     *
     * @return 日付キー文字列
     */
    public String getDateKey();
    
    /**
     * {@link jp.ossc.nimbus.service.scheduler.DateEvaluator DateEvaluator}サービスのサービス名を設定する。<p>
     * 日付拡張キーを使用する場合に設定する。<br>
     *
     * @param name DateEvaluatorサービスのサービス名
     */
    public void setDateEvaluatorServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.scheduler.DateEvaluator DateEvaluator}サービスのサービス名を取得する。<p>
     *
     * @return DateEvaluatorサービスのサービス名
     */
    public ServiceName getDateEvaluatorServiceName();
}