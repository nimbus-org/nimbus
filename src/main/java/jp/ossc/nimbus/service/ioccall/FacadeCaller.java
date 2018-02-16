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
package jp.ossc.nimbus.service.ioccall;

import jp.ossc.nimbus.ioc.*;

/**
 * NimbusIOCのFacadeをコールするインターフェイス。<p>
 *
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public interface FacadeCaller {
    
    /**
     * 同期的なNimbusIOCのコマンド層EJBコールを行う。<p>
     * 
     * @param value 入力コマンド
     * @return コマンド実行結果
     */
    public Command syncCommandCall(Command value);
    
    /**
     * 同期的なNimbusIOCのユニットオブワーク層EJBコールを行う。<p>
     * 複数のコマンドを１つのトランザクション内で処理したい場合に使用する。<br>
     *
     * @param value 入力コマンド集合
     * @return コマンド実行結果集合
     */
    public UnitOfWork syncUnitOfWorkCall(UnitOfWork value);
    
    /**
     * 同期的なNimbusIOCのEJBファサードコールを行う。<p>
     * 複数のトランザクションを順次処理したい場合に使用する。<br>
     * 
     * @param value 入力トランザクション集合
     * @return トランザクション実行結果集合
     */
    public FacadeValue syncFacadeCall(FacadeValue value);
    
    /**
     * 同期的なNimbusIOCのEJBファサードコールを行う。<p>
     * 複数のトランザクション集合を平行処理したい場合に使用する。<br>
     * 
     * @param values 入力トランザクション集合配列
     * @return トランザクション実行結果集合配列
     */
    public FacadeValue[] syncParallelFacadeCall(FacadeValue[] values);
    
    /**
     * 同期的なNimbusIOCのEJBファサードコールを行う。<p>
     * 複数のトランザクション集合を平行処理したい場合に使用する。<br>
     * タイムアウトした応答は、戻りの配列要素にnullを格納する。<br>
     * 
     * @param values 入力トランザクション集合配列
     * @param timeout 入力トランザクション応答タイムアウト[ms]
     * @return トランザクション実行結果集合配列
     */
    public FacadeValue[] syncParallelFacadeCall(
        FacadeValue[] values,
        long timeout
    );
    
    /**
     * 非同期のNimbusIOCのEJBファサードコールを行う。<p>
     * 複数のトランザクションを非同期処理したい場合に使用する。<br>
     * 
     * @param value 入力トランザクション集合
     */
    public void unsyncFacadeCall(FacadeValue value);
    
    /**
     * 非同期のNimbusIOCのEJBファサードコールを行う。<p>
     * 複数のトランザクション集合を非同期処理したい場合に使用する。<br>
     *
     * @param values 入力トランザクション集合配列
     */
    public void unsyncFacadeCall(FacadeValue[] values);
}
