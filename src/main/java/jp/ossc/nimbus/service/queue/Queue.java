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
package jp.ossc.nimbus.service.queue;

/**
 * Queueインタフェース。<p>
 * 
 * @author H.Nakano
 */
public interface Queue{
    
    /**
     * キューにデータを投入する。<p>
     * 
     * @param item 投入オブジェクト
     */
    public void push(Object item);
    
    /**
     * キューにデータを投入する。<p>
     * 
     * @param item 投入オブジェクト
     * @param timeout タイムアウト[ms]
     * @return タイムアウトした場合false
     */
    public boolean push(Object item, long timeout);
    
    /**
     * キューからデータを取り出す。<p>
     * キューからデータが取得できるまで、無限に待つ。<br>
     * 
     * @return キュー取得オブジェクト
     */
    public Object get();
    
    /**
     * キューからデータを取り出す。<p>
     * 指定した時間が過ぎるまでにキューからデータが取得できない場合は、nullが返る。<br>
     * 
     * @param timeOutMs タイムアウト[ms]
     * @return キュー取得オブジェクト
     */
    public Object get(long timeOutMs);
    
    /**
     * キューからデータを読む。<p>
     * 参照するだけで、キューからデータは取り出さない。<br>
     * 
     * @return キュー取得オブジェクト
     */
    public Object peek();
    
    /**
     * キューからデータを読む。<br>
     * 参照するだけで、キューからデータは取り出さない。<br>
     * 指定した時間が過ぎるまでにキューからデータが読めない場合は、nullが返る。<br>
     * 
     * @param timeOutMs タイムアウト[ms]
     * @return キュー取得オブジェクト
     */
    public Object peek(long timeOutMs);
    
    /**
     * キューから指定したデータを削除する。<p>
     *
     * @param item 削除対象のオブジェクト
     * @return 削除されたオブジェクト
     */
    public Object remove(Object item);
    
    /**
     * キューを初期化する。<p>
     */
    public void clear();
    
    /**
     * キューサイズを取得する。<p>
     * 
     * @return キュー格納件数
     */
    public int size();
    
    /**
     * キューに投入された件数を取得する。<p>
     *
     * @return キュー投入件数
     */
    public long getCount();
    
    /**
     * キュー取得待ち数を取得する。<p>
     *
     * @return キュー取得待ち数
     */
    public int getWaitCount();
    
    /**
     * キュー取得待ちを開始する。<p>
     * {@link #release()}呼出し後に、キュー取得待ちを受け付けるようにする。
     */
    public void accept();
    
    /**
     * キュー取得待ちを開放し、キュー取得待ちを受け付けないようにする。<p>
     */
    public void release();
}
