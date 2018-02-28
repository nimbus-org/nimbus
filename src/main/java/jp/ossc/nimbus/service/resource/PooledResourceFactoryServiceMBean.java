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
package jp.ossc.nimbus.service.resource;

import jp.ossc.nimbus.core.*;

/**
 * {@link PooledResourceFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see PooledResourceFactoryService
 */
public interface PooledResourceFactoryServiceMBean extends ServiceBaseMBean{
    
    /**
     * プールが使い尽された時の処理の種別で、新たなオブジェクトが利用できるまでまたは 最大待機時間 に達するまで、プール取り出し要求が待機される。<p>
     */
    public static final String WHEN_EXHAUSTED_BLOCK = "WHEN_EXHAUSTED_BLOCK";
    
    /**
     * プールが使い尽された時の処理の種別で、プール取り出し要求が失敗し、{@link java.util.NoSuchElementException}を投げる。<p>
     */
    public static final String WHEN_EXHAUSTED_FAIL = "WHEN_EXHAUSTED_FAIL";
    
    /**
     * プールが使い尽された時の処理の種別で、新たなオブジェクトが生成される。<p>
     */
    public static final String WHEN_EXHAUSTED_GROW = "WHEN_EXHAUSTED_GROW";
    
    /**
     * プールするオブジェクトを生成する{@link org.apache.commons.pool.PoolableObjectFactory PoolableObjectFactory}インタフェースの実装クラスを指定する。<p>
     *
     * @param clazz {@link org.apache.commons.pool.PoolableObjectFactory PoolableObjectFactory}インタフェースの実装クラス
     */
    public void setPoolableObjectFactoryClass(Class clazz);
    
    /**
     * プールするオブジェクトを生成する{@link org.apache.commons.pool.PoolableObjectFactory PoolableObjectFactory}インタフェースの実装クラスを取得する。<p>
     *
     * @return {@link org.apache.commons.pool.PoolableObjectFactory PoolableObjectFactory}インタフェースの実装クラス
     */
    public Class getPoolableObjectFactoryClass();
    
    /**
     * プールするオブジェクトを生成する{@link org.apache.commons.pool.PoolableObjectFactory PoolableObjectFactory}インタフェースの実装サービス名を指定する。<p>
     *
     * @param name {@link org.apache.commons.pool.PoolableObjectFactory PoolableObjectFactory}インタフェースの実装サービス名
     */
    public void setPoolableObjectFactoryServiceName(ServiceName name);
    
    /**
     * プールするオブジェクトを生成する{@link org.apache.commons.pool.PoolableObjectFactory PoolableObjectFactory}インタフェースの実装サービス名を取得する。<p>
     *
     * @return {@link org.apache.commons.pool.PoolableObjectFactory PoolableObjectFactory}インタフェースの実装サービス名
     */
    public ServiceName getPoolableObjectFactoryServiceName();
    
    /**
     * 同時にプールから取り出すことのできるオブジェクトの最大数を設定する。<p>
     * デフォルトは、{@link org.apache.commons.pool.impl.GenericObjectPool#DEFAULT_MAX_ACTIVE DEFAULT_MAX_ACTIVE}。<br>
     *
     * @param max 同時にプールから取り出すことのできるオブジェクトの最大数
     */
    public void setMaxActive(int max);
    
    /**
     * 同時にプールから取り出すことのできるオブジェクトの最大数を取得する。<p>
     *
     * @return 同時にプールから取り出すことのできるオブジェクトの最大数
     */
    public int getMaxActive();
    
    /**
     * プール内に保持できる未使用のオブジェクトの最大数を設定する。<p>
     * デフォルトは、{@link org.apache.commons.pool.impl.GenericObjectPool#DEFAULT_MAX_IDLE DEFAULT_MAX_IDLE}。<br>
     *
     * @param max プール内に保持できる未使用のオブジェクトの最大数
     */
    public void setMaxIdle(int max);
    
    /**
     * プール内に保持できる未使用のオブジェクトの最大数を取得する。<p>
     *
     * @return プール内に保持できる未使用のオブジェクトの最大数
     */
    public int getMaxIdle();
    
    /**
     * プール内に保持される未使用のオブジェクトの最小数を設定する。<p>
     * この値に達しない場合には排除処理スレッドにて新たなオブジェクトの生成を行う。<br>
     * デフォルトは、{@link org.apache.commons.pool.impl.GenericObjectPool#DEFAULT_MIN_IDLE DEFAULT_MIN_IDLE}。<br>
     *
     * @param min プール内に保持される未使用のオブジェクトの最小数
     */
    public void setMinIdle(int min);
    
    /**
     * プール内に保持される未使用のオブジェクトの最小数を取得する。<p>
     *
     * @return プール内に保持される未使用のオブジェクトの最小数
     */
    public int getMinIdle();
    
    /**
     * プールが使い尽されていて、{@link #setWhenExhaustedAction(String) setWhenExhaustedAction(WHEN_EXHAUSTED_BLOCK)}が設定されている場合の {@link PooledResourceFactoryService#makeResource(String) makeResource(String)}メソッドが例外を投げるまでの最長待機時間(ミリ秒)を設定する。<p>
     * 0より小さな値が設定された場合、makeResource(String)メソッドは無期限に待機する。<br>
     * デフォルトは、{@link org.apache.commons.pool.impl.GenericObjectPool#DEFAULT_MAX_WAIT DEFAULT_MAX_WAIT}。<br>
     *
     * @param maxMillis プールが使い尽された場合の取得での最長待機時間(ミリ秒)
     */
    public void setMaxWaitTime(long maxMillis);
    
    /**
     * プールが使い尽されていて、{@link #setWhenExhaustedAction(String) setWhenExhaustedAction(WHEN_EXHAUSTED_BLOCK)}が設定されている場合の {@link PooledResourceFactoryService#makeResource(String) makeResource(String)}メソッドが例外を投げるまでの最長待機時間(ミリ秒)を取得する。<p>
     *
     * @return プールが使い尽された場合の取得での最長待機時間(ミリ秒)
     */
    public long getMaxWaitTime();
    
    /**
     * オブジェクトがプール内に未使用状態でいられる時間の最小値を設定する。<p>
     * 未使用状態でいる時間が、この値に達すると排除処理の対象となる。<br>
     * デフォルトは、{@link org.apache.commons.pool.impl.GenericObjectPool#DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS}。<br>
     *
     * @param minMillis オブジェクトがプール内に未使用状態でいられる時間の最小値
     */
    public void setMinEvictableIdleTime(long minMillis);
    
    /**
     * オブジェクトがプール内に未使用状態でいられる時間の最小値を取得する。<p>
     *
     * @return オブジェクトがプール内に未使用状態でいられる時間の最小値
     */
    public long getMinEvictableIdleTime();
    
    /**
     * 1度のオブジェクト排除処理で排除スレッドにチェックされるオブジェクトの数を設定する。<p> 
     * 負の値が設定された場合、ceil(getNumIdle())/abs(getNumTestsPerEvictionRun()) 回のチェックを実施する。例えば -n が設定された場合には、1/n の未使用オブジェクトが1度のオブジェクト排除処理でチェックされる。<br>
     * デフォルトは、{@link org.apache.commons.pool.impl.GenericObjectPool#DEFAULT_NUM_TESTS_PER_EVICTION_RUN DEFAULT_NUM_TESTS_PER_EVICTION_RUN}。<br>
     *
     * @param num 1度のオブジェクト排除処理で排除スレッドにチェックされるオブジェクトの数
     */
    public void setNumTestsPerEvictionRun(int num);
    
    /**
     * 1度のオブジェクト排除処理で排除スレッドにチェックされるオブジェクトの数を取得する。<p> 
     *
     * @return 1度のオブジェクト排除処理で排除スレッドにチェックされるオブジェクトの数
     */
    public int getNumTestsPerEvictionRun();
    
    /**
     * プールから取り出される前に プール内のオブジェクトが有効かどうかの確認を行うかどうかを設定する。<p>
     * 有効でないと判断された場合、オブジェクトはプールから破棄され、他のオブジェクトが取り出される。<br>
     * デフォルトは、{@link org.apache.commons.pool.impl.GenericObjectPool#DEFAULT_TEST_ON_BORROW DEFAULT_TEST_ON_BORROW}。<br>
     *
     * @param isTest trueの場合、有効かどうかの確認を行う
     */
    public void setTestOnBorrow(boolean isTest);
    
    /**
     * プールから取り出される前に プール内のオブジェクトが有効かどうかの確認を行うかどうかを判定する。<p>
     *
     * @return trueの場合、有効かどうかの確認を行う
     */
    public boolean isTestOnBorrow();
    
    /**
     * プールに戻す前に オブジェクトが有効かどうかの確認を行うかどうかを設定する。<p>
     * 有効でないと判断された場合、オブジェクトはプールから破棄される。<br>
     * デフォルトは、{@link org.apache.commons.pool.impl.GenericObjectPool#DEFAULT_TEST_ON_RETURN DEFAULT_TEST_ON_RETURN}。<br>
     *
     * @param isTest trueの場合、有効かどうかの確認を行う
     */
    public void setTestOnReturn(boolean isTest);
    
    /**
     * プールに戻す前に オブジェクトが有効かどうかの確認を行うかどうかを判定する。<p>
     *
     * @return trueの場合、有効かどうかの確認を行う
     */
    public boolean isTestOnReturn();
    
    /**
     * オブジェクト排除処理時にプール内のオブジェクトが有効かどうかの確認を行うかどうかを設定する。<p>
     * 有効でないと判断されたオブジェクトはプールから破棄される。<br>
     * デフォルトは、{@link org.apache.commons.pool.impl.GenericObjectPool#DEFAULT_TEST_WHILE_IDLE DEFAULT_TEST_WHILE_IDLE}。<br>
     *
     * @param isTest trueの場合、有効かどうかの確認を行う
     */
    public void setTestWhileIdle(boolean isTest);
    
    /**
     * オブジェクト排除処理時にプール内のオブジェクトが有効かどうかの確認を行うかどうかを判定する。<p>
     *
     * @return trueの場合、有効かどうかの確認を行う
     */
    public boolean isTestWhileIdle();
    
    /**
     * 未使用オブジェクト排除処理が次の実行までの間スリープする時間(ミリ秒)を設定する。<p>
     * 負の値が設定された場合、排除スレッドは起動しない。<br>
     * デフォルトは、{@link org.apache.commons.pool.impl.GenericObjectPool#DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS}。<br>
     *
     * @param millis 未使用オブジェクト排除処理が次の実行までの間スリープする時間(ミリ秒)
     */
    public void setTimeBetweenEvictionRuns(long millis);
    
    /**
     * 未使用オブジェクト排除処理が次の実行までの間スリープする時間(ミリ秒)を取得する。<p>
     *
     * @return 未使用オブジェクト排除処理が次の実行までの間スリープする時間(ミリ秒)
     */
    public long getTimeBetweenEvictionRuns();
    
    /**
     * プールが使い尽されている場合(取り出すことのできるオブジェクトが最大数に達した場合)に行う処理の種別を設定する。<p>
     *
     * @param action プールが使い尽されている場合(取り出すことのできるオブジェクトが最大数に達した場合)に行う処理の種別
     * @exception IllegalArgumentException 引数が不正な種別の場合
     * @see #WHEN_EXHAUSTED_BLOCK
     * @see #WHEN_EXHAUSTED_FAIL
     * @see #WHEN_EXHAUSTED_GROW
     */
    public void setWhenExhaustedAction(String action)
     throws IllegalArgumentException;
    
    /**
     * プールが使い尽されている場合(取り出すことのできるオブジェクトが最大数に達した場合)に行う処理の種別を取得する。<p>
     *
     * @return プールが使い尽されている場合(取り出すことのできるオブジェクトが最大数に達した場合)に行う処理の種別
     */
    public String getWhenExhaustedAction();
    
    /**
     * プール内にある使用されていないオブジェクトを削除し、関連するリソースを開放する。<p>
     *
     * @exception Exception 何らかの理由で失敗した場合
     */
    public void clear() throws Exception;
    
    /**
     * 現在プールから取り出されて使用中のオブジェクトの数を取得する。<p>
     *
     * @return 現在プールから取り出されて使用中のオブジェクトの数
     */
    public int getActiveNum();
    
    /**
     * 現在プールされていて、未使用のオブジェクトの数を取得する。<p>
     *
     * @return 現在プールされていて、未使用のオブジェクトの数
     */
    public int getIdleNum();
}
