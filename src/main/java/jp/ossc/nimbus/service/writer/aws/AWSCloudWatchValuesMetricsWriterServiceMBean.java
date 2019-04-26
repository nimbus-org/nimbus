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
package jp.ossc.nimbus.service.writer.aws;

import java.util.Map;

/**
 * {@link AWSCloudWatchValuesMetricsWriterService}のMBeanインタフェース<p>
 * 
 * @author M.Ishida
 * @see AWSCloudWatchValuesMetricsWriterService
 */
public interface AWSCloudWatchValuesMetricsWriterServiceMBean extends AbstractAWSCloudWatchMetricsWriterServiceMBean {
    
    /**
     * メトリクス名に付加するPrefixを取得する。<p>
     * 
     * @return メトリクス名に付加するPrefix
     */
    public String getMetricsNamePrefix();

    /**
     * メトリクス名に付加するPrefixを設定する。<p>
     * 
     * @param prefix メトリクス名に付加するPrefix
     */
    public void setMetricsNamePrefix(String prefix);

    /**
     * メトリクス名に付加するPostfixを取得する。<p>
     * 
     * @return メトリクス名に付加するPostfix
     */
    public String getMetricsNamePostfix();

    /**
     * メトリクス名に付加するPostfixを設定する。<p>
     * 
     * @param postfix メトリクス名に付加するPostfix
     */
    public void setMetricsNamePostfix(String postfix);

    /**
     * jp.ossc.nimbus.service.writer.WritableRecordから取得した値に対する単位のMappingを取得する。<p>
     * 
     * @return WritableRecordから取得した値に対する単位のMapping
     */
    public Map getUnitMapping();

    /**
     * jp.ossc.nimbus.service.writer.WritableRecordから取得した値に対する単位のMappingを設定する。<p>
     * WritableRecordに設定されているキーに対して、com.amazonaws.services.cloudwatch.model.StandardUnitの文字列を設定する。<br>
     * WritableRecordに設定されているキーに対して、StandardUnitが設定されていない場合はDefaultUnitが使用される。<br>
     * 
     * @param mapping WritableRecordから取得した値に対する単位のMapping
     */
    public void setUnitMapping(Map mapping);

    /**
     * デフォルトのStandardUnitを取得する。<p>
     * 
     * @return デフォルトのStandardUnit
     */
    public String getDefaultUnit();

    /**
     * デフォルトのStandardUnitを設定する。<p>
     * UnitMappingにWritableRecordに設定されているキーが存在しない場合に使用する。<br>
     * デフォルトはNoneで単位なし<br>
     * 
     * @param unit デフォルトのStandardUnit
     */
    public void setDefaultUnit(String unit);
    
    /**
     * メトリクスに出力するWritableRecordのプロパティ名の配列を取得する。<p>
     * 
     * @return プロパティ名の配列
     */
    public String[] getEnableRecordPropertyNames();
    
    /**
     * メトリクスに出力するWritableRecordのプロパティ名の配列を設定する。<p>
     * 指定しない場合は、すべてが対象となる。<br>
     * 
     * @param enablePropertyNames プロパティ名の配列
     */
    public void setEnableRecordPropertyNames(String[] enablePropertyNames);
    
    /**
     * メトリクスに出力しないWritableRecordのプロパティ名の配列を取得する。<p>
     * 
     * @return プロパティ名の配列
     */
    public String[] getDisableRecordPropertyNames();
    
    /**
     * メトリクスに出力しないWritableRecordのプロパティ名の配列を設定する。<p>
     * 指定しない場合は、すべてが対象となる。<br>
     * 
     * @param disablePropertyNames プロパティ名の配列
     */
    public void setDisableRecordPropertyNames(String[] disablePropertyNames);
    
}