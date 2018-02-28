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
package jp.ossc.nimbus.service.system;

/**
 * プロセス情報。<p>
 *
 * @author M.Takata
 */
public interface ProcessInfo{
    
    /**
     * プロセスIDを取得する。<p>
     *
     * @return プロセスID
     */
    public int getPid();
    
    /**
     * 親プロセスIDを取得する。<p>
     *
     * @return 親プロセスID
     */
    public int getParentPid();
    
    /**
     * プロセス名を取得する。<p>
     *
     * @return プロセス名
     */
    public String getName();
    
    /**
     * 実行コマンドを取得する。<p>
     *
     * @return 実行コマンド
     */
    public String getCommand();
    
    /**
     * オーナーを取得する。<p>
     *
     * @return オーナー
     */
    public String getOwner();
    
    /**
     * ユーザプロセスのCPU使用時間を取得する。<p>
     *
     * @return CPU使用時間[ms]
     */
    public long getUserTimeMillis();
    
    /**
     * システムのCPU使用時間を取得する。<p>
     *
     * @return CPU使用時間[ms]
     */
    public long getSystemTimeMillis();
    
    /**
     * 現在のメモリ使用量を取得する。<p>
     *
     * @return メモリ使用量[byte]
     */
    public long getCurrentMemoryBytes();
    
    /**
     * 現在までの総メモリ使用量を取得する。<p>
     *
     * @return メモリ使用量[byte]
     */
    public long getTotalMemoryBytes();
}