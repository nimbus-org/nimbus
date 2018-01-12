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
// パッケージ
package jp.ossc.nimbus.service.debug;
//インポート

/**
 * デバッグクラス<p>
 * デバッグ情報出力を行う
 * @version $Name:  $
 * @author K.Nagai
 * @since 1.0
 */
public interface Debug {
    /**
     * デバッグ情報出力(例外つき)<p>
     * デバッグ情報を出力する。isXXXを使用して出力可否情報を
     * 問い合わせる前にコールするとサービス例外を発生する。
     * @param str エラーメッセージ
     * @param e 例外 
     * @throws isXXX関数使用前にこの関数をコールした際の例外。
     */
    public void write(String str,Throwable e);
    /**
     * デバッグ情報出力<p>
     * デバッグ情報を出力する。isXXXを使用して出力可否情報を
     * 問い合わせる前にコールするとサービス例外を発生する。
     * @param str エラーメッセージ
     * @throws  isXXX関数使用前にこの関数をコールした際の例外。
     */
    public void write(String str);
    
    /**
     * デバッグ情報DUMP出力<p>
     * デバッグ情報をDUMPし出力する。isXXXを使用して出力可否情報を
     * 問い合わせる前にコールするとサービス例外を発生する。
     * @param object エラーメッセージ
     * @throws  isXXX関数使用前にこの関数をコールした際の例外。
     */
    public void dump(Object object);
    /**
     * デバッグ情報DUMP出力<p>
     * デバッグ情報をDUMPし出力する。isXXXを使用して出力可否情報を
     * 問い合わせる前にコールするとサービス例外を発生する。
     * @param objects エラーメッセージ
     * @throws  isXXX関数使用前にこの関数をコールした際の例外。
     */
    public void dump(Object[] objects);
    /**
     * デバッグ情報DUMP出力<p>
     * デバッグ情報をDUMPし出力する。isXXXを使用して出力可否情報を
     * 問い合わせる前にコールするとサービス例外を発生する。
     * @param msg エラーメッセージ
     * @param object エラーメッセージ
     * @throws  isXXX関数使用前にこの関数をコールした際の例外。
     */
    public void dump(String msg,Object object);
    /**
     * デバッグ情報DUMP出力<p>
     * デバッグ情報をDUMPし出力する。isXXXを使用して出力可否情報を
     * 問い合わせる前にコールするとサービス例外を発生する。
     * @param msg エラーメッセージ
     * @param objects エラーメッセージ
     * @throws  isXXX関数使用前にこの関数をコールした際の例外。
     */
    public void dump(String msg,Object[] objects);
    
    /**
     * デバッグレベル情報出力可否
     * @return デバッグレベルの情報を出力するか
     */
    public boolean isDebug();
    /**
     * インフォーメーションレベル情報出力可否
     * @return インフォーメーションレベルの情報を出力するか
     */
    public boolean isInfo();
    /**
     * 警告レベル情報出力可否
     * @return 警告レベルの情報を出力するか
     */
    public boolean isWarn();
    /**
     * エラーレベル情報出力可否
     * @return エラーレベルの情報を出力するか
     */
    public boolean isError();
    /**
     * 致命的エラーレベル情報出力可否
     * @return 致命的エラーレベルの情報を出力するか
     */
    public boolean isFatalError();
}
