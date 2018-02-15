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
 * CPU使用時間。<p>
 *
 * @author M.Takata
 */
public interface CpuTimes{
    
    /**
     * ユーザプロセスのCPU使用時間を取得する。<p>
     * 
     * @return ユーザプロセスのCPU使用時間[ms]
     */
    public long getUserTimeMillis();
    
    /**
     * システムのCPU使用時間を取得する。<p>
     * 
     * @return システムのCPU使用時間[ms]
     */
    public long getSystemTimeMillis();
    
    /**
     * CPU未使用時間を取得する。<p>
     * 
     * @return CPU未使用時間[ms]
     */
    public long getIdleTimeMillis();
    
    /**
     * CPU総使用時間を取得する。<p>
     * 
     * @return CPU総使用時間[ms]
     */
    public long getTotalTimeMillis();
    
    /**
     * ユーザプロセスのCPU使用率を取得する。<p>
     * 
     * @return ユーザプロセスのCPU使用率
     */
    public float getUserRate();
    
    /**
     * システムのCPU使用率を取得する。<p>
     * 
     * @return システムのCPU使用率
     */
    public float getSystemRate();
    
    /**
     * CPU未使用率を取得する。<p>
     * 
     * @return CPU未使用率
     */
    public float getIdleRate();
    
    /**
     * このCPU使用時間に指定されたCPU使用時間を加算する。<p>
     *
     * @param times 加算するCPU使用時間
     */
    public void add(CpuTimes times);
}