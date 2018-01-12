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

/**
 * XYプロット条件。<p>
 *
 * @author k2-taniguchi
 */
public interface XYPlotCondition extends PlotCondition {
    /**
     * 指定されたインデックスの縦軸の可視状態を設定する。<p>
     *
     * @param index 縦軸のインデックス
     * @param visible 縦軸の可視状態
     */
    public void setRangeAxisVisible(int index, Boolean visible);

    /**
     * 指定されたインデックスの縦軸の可視状態を取得する。<p>
     *
     * @param index 縦軸のインデックス
     * @return 縦軸の可視状態
     */
    public Boolean isRangeAxisVisible(int index);

    public void setDefaultDomainAxisTickLabelFontName(String name);
    public String getDefaultDomainAxisTickLabelFontName();
    public void setDefaultDomainAxisTickLabelFontStyle(int style);
    public int getDefaultDomainAxisTickLabelFontStyle();
    public void setDefaultDomainAxisTickLabelFontSize(int size);
    public int getDefaultDomainAxisTickLabelFontSize();

    public void setDefaultRangeAxisTickLabelFontName(String name);
    public String getDefaultRangeAxisTickLabelFontName();
    public void setDefaultRangeAxisTickLabelFontStyle(int style);
    public int getDefaultRangeAxisTickLabelFontStyle();
    public void setDefaultRangeAxisTickLabelFontSize(int size);
    public int getDefaultRangeAxisTickLabelFontSize();

    public void setDomainAxisTickLabelFontName(int index, String name);
    public String getDomainAxisTickLabelFontName(int index);
    public void setDomainAxisTickLabelFontStyle(int index, int style);
    public int getDomainAxisTickLabelFontStyle(int index);
    public void setDomainAxisTickLabelFontSize(int index, int size);
    public int getDomainAxisTickLabelFontSize(int index);

    public void setRangeAxisTickLabelFontName(int index, String name);
    public String getRangeAxisTickLabelFontName(int index);
    public void setRangeAxisTickLabelFontStyle(int index, int style);
    public int getRangeAxisTickLabelFontStyle(int index);
    public void setRangeAxisTickLabelFontSize(int index, int size);
    public int getRangeAxisTickLabelFontSize(int index);

    public void setDefaultDomainAxisLabelFontName(String name);
    public String getDefaultDomainAxisLabelFontName();
    public void setDefaultDomainAxisLabelFontStyle(int style);
    public int getDefaultDomainAxisLabelFontStyle();
    public void setDefaultDomainAxisLabelFontSize(int size);
    public int getDefaultDomainAxisLabelFontSize();

    public void setDefaultRangeAxisLabelFontName(String name);
    public String getDefaultRangeAxisLabelFontName();
    public void setDefaultRangeAxisLabelFontStyle(int style);
    public int getDefaultRangeAxisLabelFontStyle();
    public void setDefaultRangeAxisLabelFontSize(int size);
    public int getDefaultRangeAxisLabelFontSize();

    public void setDomainAxisLabelFontName(int index, String name);
    public String getDomainAxisLabelFontName(int index);
    public void setDomainAxisLabelFontStyle(int index, int style);
    public int getDomainAxisLabelFontStyle(int index);
    public void setDomainAxisLabelFontSize(int index, int size);
    public int getDomainAxisLabelFontSize(int index);

    public void setRangeAxisLabelFontName(int index, String name);
    public String getRangeAxisLabelFontName(int index);
    public void setRangeAxisLabelFontStyle(int index, int style);
    public int getRangeAxisLabelFontStyle(int index);
    public void setRangeAxisLabelFontSize(int index, int size);
    public int getRangeAxisLabelFontSize(int index);

}
