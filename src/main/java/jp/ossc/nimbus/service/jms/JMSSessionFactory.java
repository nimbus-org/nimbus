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
package jp.ossc.nimbus.service.jms;

import javax.jms.*;

/**
 * JMSセッションファクトリ。<p>
 * 
 * @author M.Takata
 */
public interface JMSSessionFactory{
    
    /**
     * このファクトリが保持しているConnectionを取得する。<p>
     * getSessionメソッドで、Sessionを生成する際に、引数でConnectionを指定しなかった場合は、このConnectionが使用される。<br>
     *
     * @return このファクトリが保持しているConnection。Connectionを保持していない場合はnull。
     */
    public Connection getConnection();
    
    /**
     * このファクトリがConnectionの取得に使用する{@link JMSConnectionFactory}サービスを取得する。<p>
     *
     * @return {@link JMSConnectionFactory}サービス
     */
    public JMSConnectionFactory getConnectionFactory();
    
    /**
     * JMSセッションを取得する。<p>
     * {@link #getConnection()}で取得されるConnectionから、{@link Connection#createSession(boolean, int)}メソッドで生成する。<br>
     * getConnection()がnullを返す場合は、JMSSessionCreateExceptionをthrowする。<br>
     * 引数は、サービスの実装に依存するが、特に設定がなければfalse、Session.AUTO_ACKNOWLEDGE。<br>
     *
     * @return JMSセッション
     * @exception JMSSessionCreateException JMSセッションの生成に失敗した場合
     */
    public Session getSession() throws JMSSessionCreateException;
    
    /**
     * JMSセッションを取得する。<p>
     * {@link #getConnection()}で取得されるConnectionから、{@link Connection#createSession(boolean, int)}メソッドで生成する。<br>
     * getConnection()がnullを返す場合は、JMSSessionCreateExceptionをthrowする。<br>
     *
     * @param transactionMode トランザクションをサポートする場合、true
     * @param ackMode MessageConsumerやJMSクライアントがメッセージを受信した時のACKの返し方のモード
     * @return JMSセッション
     * @exception JMSSessionCreateException JMSセッションの生成に失敗した場合
     */
    public Session getSession(
        boolean transactionMode,
        int ackMode
    ) throws JMSSessionCreateException;
    
    /**
     * JMSセッションを取得する。<p>
     * 引数で指定されたConnectionから、{@link Connection#createSession(boolean, int)}メソッドで生成する。<br>
     * 引数は、サービスの実装に依存するが、特に設定がなければfalse、Session.AUTO_ACKNOWLEDGE。<br>
     *
     * @param con Connection
     * @return JMSセッション
     * @exception JMSSessionCreateException JMSセッションの生成に失敗した場合
     */
    public Session getSession(Connection con) throws JMSSessionCreateException;
    
    /**
     * JMSセッションを取得する。<p>
     * 引数で指定されたConnectionから、{@link Connection#createSession(boolean, int)}メソッドで生成する。<br>
     *
     * @param con Connection
     * @param transactionMode トランザクションをサポートする場合、true
     * @param ackMode MessageConsumerやJMSクライアントがメッセージを受信した時のACKの返し方のモード
     * @return JMSセッション
     * @exception JMSSessionCreateException JMSセッションの生成に失敗した場合
     */
    public Session getSession(
        Connection con,
        boolean transactionMode,
        int ackMode
    ) throws JMSSessionCreateException;
}
