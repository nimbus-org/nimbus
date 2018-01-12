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
 * チャート条件。<p>
 *
 * @author k2-taniguchi
 */
public interface ChartCondition {

    /**
     * プロット条件を追加する。<p>
     *
     * @param plotCondition プロット条件
     */
    public void addPlotCondition(PlotCondition plotCondition);

    /**
     * 指定されたプロット名のプロット条件配列を取得する。<p>
     *
     * @param plotName プロット名
     * @return プロット条件配列
     */
    public PlotCondition[] getPlotConditions(String plotName);

    /**
     * プロット条件配列を取得する。<p>
     *
     * @return プロット条件配列
     */
    public PlotCondition[] getPlotConditions();

    /**
     * プロット名のイテレータを取得する。<p>
     *
     * @return プロット名のイテレータ
     */
    public Iterator getPlotNames();

    /**
     * JFreeChartのタイトルを設定する。<p>
     *
     * @param title タイトル
     */
    public void setTitle(String title);

    /**
     * JFreeChartのタイトルを取得する。<p>
     *
     * @return タイトル
     */
    public String getTitle();

    /**
     * JFreeChartのタイトルフォント名を設定する。<p>
     *
     * @param name フォント名
     */
    public void setTitleFontName(String name);

    /**
     * JFreeChartのタイトルフォント名を取得する。<p>
     *
     * @return フォント名
     */
    public String getTitleFontName();

    /**
     * JFreeChartのタイトルフォントスタイルを設定する。<p>
     *
     * @param style フォントスタイル
     */
    public void setTitleFontStyle(int style);

    /**
     * JFreeChartのタイトルフォントスタイルを取得する。<p>
     *
     * @return フォントスタイル
     */
    public int getTitleFontStyle();

    /**
     * JFreeChartのタイトルフォントサイズを設定する。<p>
     *
     * @param size フォントサイズ
     */
    public void setTitleFontSize(int size);

    /**
     * JFreeChartのタイトルフォントサイズを取得する。<p>
     *
     * @return フォントサイズ
     */
    public int getTitleFontSize();

    /**
     * デフォルトのサブタイトルフォント名を設定する。<p>
     *
     * @param name フォント名
     */
    public void setDefaultSubtitleFontName(String name);

    /**
     * デフォルトのサブタイトルフォント名を取得する。<p>
     *
     * @return フォント名
     */
    public String getDefaultSubtitleFontName();

    /**
     * デフォルトのサブタイトルフォントスタイルを設定する。<p>
     *
     * @param style フォントスタイル
     */
    public void setDefaultSubtitleFontStyle(int style);

    /**
     * デフォルトのサブタイトルフォントスタイルを取得する。<p>
     *
     * @return フォントスタイル
     */
    public int getDefaultSubtitleFontStyle();

    /**
     * デフォルトのサブタイトルフォントサイズを設定する。<p>
     *
     * @param size フォントサイズ
     */
    public void setDefaultSubtitleFontSize(int size);

    /**
     * デフォルトのサブタイトルフォントサイズを取得する。<p>
     *
     * @return フォントサイズ
     */
    public int getDefaultSubtitleFontSize();

    /**
     * 指定されたインデックスのサブタイトルフォント名を設定する。<p>
     *
     * @param index サブタイトルインデックス
     * @param name フォント名
     */
    public void setSubtitleFontName(int index, String name);

    /**
     * 指定されたインデックスのサブタイトルフォント名を取得する。<p>
     *
     * @param index サブタイトルインデックス
     * @return フォント名
     */
    public String getSubtitleFontName(int index);

    /**
     * 指定されたインデックスのサブタイトルフォントスタイルを設定する。<p>
     *
     * @param index サブタイトルインデックス
     * @param style フォントスタイル
     */
    public void setSubtitleFontStyle(int index, int style);

    /**
     * 指定されたインデックスのサブタイトルフォントスタイルを取得する。<p>
     *
     * @param index サブタイトルインデックス
     * @return フォントスタイル
     */
    public int getSubtitleFontStyle(int index);

    /**
     * 指定されたインデックスのサブタイトルフォントサイズを設定する。<p>
     *
     * @param index サブタイトルインデックス
     * @param size フォントサイズ
     */
    public void setSubtitleFontSize(int index, int size);

    /**
     * 指定されたインデックスのサブタイトルフォントサイズを取得する。<p>
     *
     * @param index サブタイトルインデックス
     * @return フォントサイズ
     */
    public int getSubtitleFontSize(int index);

}
