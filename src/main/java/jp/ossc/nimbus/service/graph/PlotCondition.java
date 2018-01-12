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
package jp.ossc.nimbus.service.graph;

import java.util.Iterator;

/**
 * プロット条件。<p>
 *
 * @author k2-taniguchi
 */
public interface PlotCondition {

    /**
     * プロット名を取得する。<p>
     *
     * @return プロット名
     */
    public String getName();

    /**
     * プロット名を設定する。<p>
     *
     * @param name プロット名
     */
    public void setName(String name);

    /**
     * データセット条件を追加する。<p>
     *
     * @param dsCondition データセット条件
     */
    public void addDatasetCondition(DatasetCondition dsCondition);

    /**
     * 指定されたデータセット名のデータセット条件配列を取得する。<p>
     *
     * @param dsName データセット名
     * @return データセット条件配列
     */
    public DatasetCondition[] getDatasetConditions(String dsName);

    /**
     * データセット条件配列を取得する。<p>
     *
     * @return データセット条件配列
     */
    public DatasetCondition[] getDatasetConditions();

    /**
     * データセット名のイテレータを取得する。<p>
     *
     * @return データセット名のイテレータ
     */
    public Iterator getDatasetNames();

    /**
     * 有効なデータセット名配列を取得する。<p>
     *
     * @return 有効なデータセット名配列
     */
    public String[] getEnableDatasetNames();

    /**
     * 有効なデータセット名を追加する。<p>
     *
     * @param dsName 有効なデータセット名
     */
    public void addEnableDatasetName(String dsName);

    /**
     * 設定順のデータセット名配列を取得する。<p>
     *
     * @return データセット名配列
     */
    public String[] getDatasetNameOrder();

    /**
     * 任意の設定順でデータセット名を追加する。<p>
     *
     * @param dsName データセット名
     */
    public void addDatasetNameOrder(String dsName);

}
