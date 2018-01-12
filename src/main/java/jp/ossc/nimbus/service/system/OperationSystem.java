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
 * オペレーションシステム。<p>
 *
 * @author M.Takata
 */
public interface OperationSystem{
    
    /**
     * オペレーションシステム名を取得する。<p>
     *
     * @return オペレーションシステム名
     */
    public String getName();
    
    /**
     * CPU数を取得する。<p>
     *
     * @return CPU数
     */
    public int getCpuNumbers();
    
    /**
     * CPUのクロック数を取得する。<p>
     *
     * @return CPUクロック数
     */
    public long getCpuFrequency();
    
    /**
     * オペレーションシステムが起動してからの経過時間[s]を取得する。<p>
     *
     * @return オペレーションシステムが起動してからの経過時間[s]
     */
    public long getUptimeInSeconds();
    
    /**
     * CPUの使用時間を取得する。<p>
     *
     * @return CPU使用時間
     */
    public CpuTimes getCpuTimes();
    
    /**
     * 前回CPU使用時間を取得した時からの差分CPU使用時間を取得する。<p>
     *
     * @return 差分CPU使用時間
     */
    public CpuTimes getCpuTimesDelta();
    
    /**
     * 引数で指定したCPU使用時間からの差分CPU使用時間を取得する。<p>
     *
     * @return 差分CPU使用時間
     */
    public CpuTimes getCpuTimesDelta(CpuTimes prev);
    
    /**
     * 物理メモリの使用状況を取得する。<p>
     *
     * @return 物理メモリ使用状況
     */
    public MemoryInfo getPhysicalMemoryInfo();
    
    /**
     * スワップメモリの使用状況を取得する。<p>
     *
     * @return スワップメモリ使用状況
     */
    public MemoryInfo getSwapMemoryInfo();
    
    /**
     * このJavaプロセスのプロセスIDを取得する。<p>
     *
     * @return プロセスID
     */
    public int getPid();
    
    /**
     * 指定されたプロセスを終了させる。<p>
     *
     * @param pid プロセスID
     * @return プロセスが存在しない場合false
     */
    public boolean kill(int pid);
    
    /**
     * 指定されたプロセスの情報を取得する。<p>
     *
     * @param pid プロセスID
     * @return プロセス情報
     */
    public ProcessInfo getProcessInfo(int pid);
    
    /**
     * 指定されたプロセスを終了させる。<p>
     *
     * @param command コマンドの正規表現
     * @return プロセスが存在しない場合false
     */
    public boolean kill(String command);
    
    /**
     * 指定されたプロセスの情報を取得する。<p>
     *
     * @param command コマンドの正規表現
     * @return プロセス情報
     */
    public ProcessInfo getProcessInfo(String command);
    
    /**
     * 全てのプロセスの情報を取得する。<p>
     *
     * @return プロセス情報の配列
     */
    public ProcessInfo[] getProcessInfos();
}
