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
package jp.ossc.nimbus.service.writer.prometheus;

import java.util.List;
import java.util.Map;

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link GaugePushgatewayWriterService}サービスMBeanインタフェース。<p>
 *
 * @author M.Ishida
 */
public interface GaugePushgatewayWriterServiceMBean extends ServiceBaseMBean {
    
    /**
     * Prometeusに出力する際のNameを取得する。<p>
     * 
     * @return Prometeusに出力する際のName
     */
    public String getName();
    
    /**
     * Prometeusに出力する際のNameを設定する。<p>
     * 
     * @param name Prometeusに出力する際のName
     */
    public void setName(String name);
    
    /**
     * Prometeusに出力する際のHelpを取得する。<p>
     * 
     * @return Prometeusに出力する際のHelp
     */
    public String getHelp();
    
    /**
     * Prometeusに出力する際のHelpを設定する。<p>
     * 
     * @param help Prometeusに出力する際のHelp
     */
    public void setHelp(String help);
    
    /**
     * Prometeusに出力する際のLabelのKey名のリストを取得する。<p>
     * 
     * @return Key名のリスト
     */
    public List getLabelPropertyList();
    
    /**
     * Prometeusに出力する際のLabelのKey名のリストを設定する。<p>
     * 
     * @param names Key名のリスト
     */
    public void setLabelPropertyList(List list);
    
    /**
     * Prometeusに出力する際の固定で出力したいLabelのKey名とVallueのMapを取得する。<p>
     * 
     * @return LabelのKey名とVallueのMap
     */
    public Map getFixedLabelMap();

    /**
     * Prometeusに出力する際の固定で出力したいLabelのKey名とVallueのMapを設定する。<p>
     * 
     * @param labelMap LabelのKey名とVallueのMap
     */
    public void setFixedLabelMap(Map labelMap);
    
    /**
     * Prometeusに出力する際のValueの値を{@link WritableRecord}から取得する際のプロパティ名のリストを取得する。<p>
     * 
     * @return プロパティ名のリスト
     */
    public List getValuePropertyList();
    
    /**
     * Prometeusに出力する際のValueの値を{@link WritableRecord}から取得する際のプロパティ名のリストを設定する。<p>
     * 
     * @param propertyName プロパティ名のリスト
     */
    public void setValuePropertyList(List list);
    
    /**
     * Prometeusに出力する際のValueの値を{@link WritableRecord}から取得した際にnullだった場合にValueとして設定する値を取得する。<p>
     * 
     * @return nullだった場合にValueとして設定する値
     */
    public Double getOutputValueOnNullValue();
    
    /**
     * Prometeusに出力する際のValueの値を{@link WritableRecord}から取得した際にnullだった場合にValueとして設定する値を設定する。<p>
     * 未設定の場合、値がnullだった場合にPrometeusへの出力は行わない。
     * 
     * @param outputValue nullだった場合にValueとして設定する値
     */
    public void setOutputValueOnNullValue(Double outputValue);
    
    /**
     * PrometeusのPushgatewayのホスト名を取得する。<p>
     * 
     * @return Pushgatewayのホスト名
     */
    public String getPushGatewayHostName();

    /**
     * PrometeusのPushgatewayのホスト名を設定する。<p>
     * 
     * @param hostName Pushgatewayのホスト名
     */
    public void setPushGatewayHostName(String hostName);

    /**
     * PrometeusのPushgatewayのポートを取得する。<p>
     * 
     * @return Pushgatewayのポート
     */
    public int getPushGatewayPort();

    /**
     * PrometeusのPushgatewayのポートを設定する。<p>
     * 
     * @param hostName Pushgatewayのポート
     */
    public void setPushGatewayPort(int port);

    /**
     * PrometeusのPushgatewayに出力する際のJob名を取得する。<p>
     * 
     * @return Job名
     */
    public String getPushGatewayJobName();

    /**
     * PrometeusのPushgatewayに出力する際のJob名を設定する。<p>
     * 
     * @param jobName Job名
     */
    public void setPushGatewayJobName(String jobName);
    
    /**
     * PrometeusのPushgatewayに出力する際のInstanceを取得する。<p>
     * 
     * @return Instance
     */
    public String getPushGatewayInstance();

    /**
     * PrometeusのPushgatewayに出力する際のInstanceを設定する。<p>
     * 
     * @param instance Instance
     */
    public void setPushGatewayInstance(String instance);
    
    
}
