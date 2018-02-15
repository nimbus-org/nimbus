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
package jp.ossc.nimbus.service.context;

/**
 * サーバ情報。<p>
 *
 * @author M.Takata
 */
public interface ServerInfo extends Context{
    
    /**
     * JREのバージョン情報を取得するキー。<p>
     */
    public static final String JAVA_VERSION_KEY = "JAVA_VERSION";
    
    /**
     * JREのベンダ情報を取得するキー。<p>
     */
    public static final String JAVA_VENDOR_KEY = "JAVA_VENDOR";
    
    /**
     * JVMの名前を取得するキー。<p>
     */
    public static final String JAVA_VM_NAME_KEY = "JAVA_VM_NAME";
    
    /**
     * JVMのバージョン情報を取得するキー。<p>
     */
    public static final String JAVA_VM_VERSION_KEY = "JAVA_VM_VERSION";
    
    /**
     * JVMのベンダ情報を取得するキー。<p>
     */
    public static final String JAVA_VM_VENDOR_KEY = "JAVA_VM_VENDOR";
    
    /**
     * OSの名前を取得するキー。<p>
     */
    public static final String OS_NAME_KEY = "OS_NAME";
    
    /**
     * OSのバージョン情報を取得するキー。<p>
     */
    public static final String OS_VERSION_KEY = "OS_VERSION";
    
    /**
     * OSのアーキテクチャ情報を取得するキー。<p>
     */
    public static final String OS_ARCH_KEY = "OS_ARCH";
    
    /**
     * ヒープメモリの現在の総容量を取得するキー。<p>
     */
    public static final String TOTAL_MEMORY_KEY = "TOTAL_MEMORY";
    
    /**
     * ヒープメモリの現在の使用量を取得するキー。<p>
     */
    public static final String USED_MEMORY_KEY = "USED_MEMORY";
    
    /**
     * ヒープメモリの現在の空き容量を取得するキー。<p>
     */
    public static final String FREE_MEMORY_KEY = "FREE_MEMORY";
    
    /**
     * ヒープメモリの最大容量を取得するキー。<p>
     */
    public static final String MAX_MEMORY_KEY = "MAX_MEMORY";
    
    /**
     * 使用可能なCPUの数を取得するキー。<p>
     */
    public static final String AVAILABLE_PROCESSORS_KEY
         = "AVAILABLE_PROCESSORS";
    
    /**
     * ホスト名を取得するキー。<p>
     */
    public static final String HOST_NAME_KEY = "HOST_NAME";
    
    /**
     * ホストのアドレスを取得するキー。<p>
     */
    public static final String HOST_ADDRESS_KEY = "HOST_ADDRESS";
    
    /**
     * 現在アクティブなスレッド数を取得するキー。<p>
     */
    public static final String ACTIVE_THREAD_COUNT_KEY = "ACTIVE_THREAD_COUNT";
    
    /**
     * 現在アクティブなスレッドグループ数を取得するキー。<p>
     */
    public static final String ACTIVE_THREAD_GROUP_COUNT_KEY
         = "ACTIVE_THREAD_GROUP_COUNT";
    
    /**
     * JREのバージョン情報を取得する。<p>
     *
     * @return JREのバージョン情報
     */
    public String getJavaVersion();
    
    /**
     * JREのベンダ情報を取得する。<p>
     *
     * @return JREのベンダ情報
     */
    public String getJavaVendor();
    
    /**
     * JVMの名前を取得する。<p>
     *
     * @return JVMの名前
     */
    public String getJavaVMName();
    
    /**
     * JVMのバージョン情報を取得する。<p>
     *
     * @return JVMのバージョン情報
     */
    public String getJavaVMVersion();
    
    /**
     * JVMのベンダ情報を取得する。<p>
     *
     * @return JVMのベンダ情報
     */
    public String getJavaVMVendor();
    
    /**
     * OSの名前を取得する。<p>
     *
     * @return OSの名前
     */
    public String getOSName();
    
    /**
     * OSのバージョン情報を取得する。<p>
     *
     * @return OSのバージョン情報
     */
    public String getOSVersion();
    
    /**
     * OSのアーキテクチャ情報を取得する。<p>
     *
     * @return OSのアーキテクチャ情報
     */
    public String getOSArch();
    
    /**
     * 現在のヒープメモリの総容量[byte]を取得する。<p>
     *
     * @return 現在のヒープメモリの総容量[byte]
     */
    public long getTotalMemory();
    
    /**
     * 現在のヒープメモリの使用量[byte]を取得する。<p>
     *
     * @return 現在のヒープメモリの使用量[byte]
     */
    public long getUsedMemory();
    
    /**
     * 現在のヒープメモリの空き容量[byte]を取得する。<p>
     *
     * @return 現在のヒープメモリの空き容量[byte]
     */
    public long getFreeMemory();
    
    /**
     * ヒープメモリの最大容量[byte]を取得する。<p>
     *
     * @return ヒープメモリの最大容量[byte]
     */
    public long getMaxMemory();
    
    /**
     * 使用可能なCPUの数を取得する。<p>
     *
     * @return 使用可能なCPUの数
     */
    public int getAvailableProcessors();
    
    /**
     * ホスト名を取得する。<p>
     *
     * @return ホスト名
     */
    public String getHostName();
    
    /**
     * ホストのアドレスを取得する。<p>
     *
     * @return ホストのアドレス
     */
    public String getHostAddress();
    
    /**
     * 現在アクティブなスレッド数を取得する。<p>
     *
     * @return 現在アクティブなスレッド数
     */
    public int getActiveThreadCount();
    
    /**
     * 現在アクティブなスレッドグループ数を取得する。<p>
     *
     * @return 現在アクティブなスレッドグループ数
     */
    public int getActiveThreadGroupCount();
}