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
 * {@link DatabaseConditionScheduleMakerService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface DatabaseConditionScheduleMakerServiceMBean
 extends DefaultScheduleMakerServiceMBean{
    
    /**
     * {jp.ossc.nimbus.service.connection.ConnectionFactory ConnectionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name ConnectionFactoryサービスのサービス名
     */
    public void setConnectionFactoryServiceName(ServiceName name);
    
    /**
     * {jp.ossc.nimbus.service.connection.ConnectionFactory ConnectionFactory}サービスのサービス名を取得する。<p>
     *
     * @return ConnectionFactoryサービスのサービス名
     */
    public ServiceName getConnectionFactoryServiceName();
    
    /**
     * スケジュールの作成有無を判断するSQLを設定する。<p>
     * 必ず、スケジュール作成日付を埋め込みパラメータとして持ち、結果がBoolean型、数値型、文字列型のいずれかになるSQLとする事。<br>
     * Boolean型の場合は、trueの場合、スケジュールを作成する。<br>
     * 数値型の場合は、0以外の値の場合、スケジュールを作成する。<br>
     * 文字列型の場合は、"0"以外の値の場合、スケジュールを作成する。<br>
     * <pre>
     *  例：営業日であればスケジュールを作成する場合
     *   select count(1) from businessday_calendar where date = ?
     * </pre>
     *
     * @param query SQL
     */
    public void setQuery(String query);
    
    /**
     * スケジュールの作成有無を判断するSQLを取得する。<p>
     *
     * @return SQL
     */
    public String getQuery();
    
    /**
     * 日付フォーマットを設定する。<p>
     * {@link #setQuery(String)}で指定したSQL内に埋め込むスケジュール作成日付を文字列として渡したい場合に、その日付フォーマットを指定する。<br>
     * 
     * @param format 日付フォーマット
     */
    public void setDateFormat(String format);
    
    /**
     * 日付フォーマットを取得する。<p>
     * 
     * @return 日付フォーマット
     */
    public String getDateFormat();
}