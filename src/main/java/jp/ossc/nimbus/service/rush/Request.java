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

/**
 * リクエスト。<p>
 *
 * @author M.Takata
 */
public abstract class Request{
    protected int count = 1;
    protected String randomAllGroup;
    protected String randomGroup;
    protected String sequenceGroup;
    protected int sequenceCount;
    protected ThreadLocal localCount = new ThreadLocal(){
        protected synchronized Object initialValue() {
            return new Counter();
        }
    };
    
    /**
     * 要求回数を設定する。<p>
     * デフォルトは、1。<br/>
     *
     * @param count 要求回数
     */
    public void setCount(int count){
        this.count = count;
    }
    
    /**
     * 要求回数を取得する。<p>
     *
     * @return 要求回数
     */
    public int getCount(){
        return count;
    }
    
    /**
     * ランダムグループ名を設定する。<p>
     * 同じランダムグループ内のどれか１つのリクエストがランダムに選択されて処理される。<br>
     *
     * @param group ランダムグループ名
     */
    public void setRandomGroup(String group){
        randomGroup = group;
    }
    
    /**
     * ランダムグループ名を取得する。<p>
     *
     * @return ランダムグループ名
     */
    public String getRandomGroup(){
        return randomGroup;
    }
    
    /**
     * ランダムオールグループ名を設定する。<p>
     * 同じランダムオールグループ内のリクエストは、順不同で全て処理される。<br>
     *
     * @param group ランダムオールグループ名
     */
    public void setRandomAllGroup(String group){
        randomAllGroup = group;
    }
    
    /**
     * ランダムオールグループ名を取得する。<p>
     *
     * @return ランダムオールグループ名
     */
    public String getRandomAllGroup(){
        return randomAllGroup;
    }
    
    /**
     * 直列グループ名を設定する。<p>
     * ランダムグループ内の同じ直列グループ内のリクエストは、同一順序で処理される。<br>
     *
     * @param group 直列グループ名
     */
    public void setSequenceGroup(String group){
        sequenceGroup = group;
    }
    
    /**
     * 直列グループ名を取得する。<p>
     *
     * @return 直列グループ名
     */
    public String getSequenceGroup(){
        return sequenceGroup;
    }
    
    /**
     * 直列グループ内での要求回数を設定する。<p>
     *
     * @param count 直列グループ内での要求回数
     */
    public void setSequenceCount(int count){
        sequenceCount = count;
    }
    
    /**
     * 直列グループ内での要求回数を取得する。<p>
     *
     * @return 直列グループ内での要求回数
     */
    public int getSequenceCount(){
        return sequenceCount;
    }
    
    /**
     * 初期化する。<p>
     * 
     * @param client ラッシュクライアント
     * @param requestId リクエスト番号
     * @exception Exception 初期化時に例外が発生した場合
     */
    public abstract void init(RushClient client, int requestId) throws Exception;
    
    public void resetCount(){
        final Counter counter = (Counter)localCount.get();
        counter.count = count;
    }
    
    public boolean isRemainCount(){
        final Counter counter = (Counter)localCount.get();
        return counter.count > 0;
    }
    
    public void countDown(){
        final Counter counter = (Counter)localCount.get();
        counter.count--;
    }
    
    public int currentCount(){
        final Counter counter = (Counter)localCount.get();
        return count - counter.count;
    }
    
    public int remainCount(){
        final Counter counter = (Counter)localCount.get();
        return counter.count;
    }
    
    protected static class Counter{
        int count = 0;
    }
}