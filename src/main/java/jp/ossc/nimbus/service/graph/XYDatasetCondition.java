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
 * XYデータセット条件。<p>
 *
 * @author k2-taniguchi
 */
public interface XYDatasetCondition
    extends DatasetCondition {
    /**
     * X軸のFromを取得する。<p>
     *
     * @return X軸のFrom
     */
    public int getXFrom();

    /**
     * X軸のFromを設定する。
     *
     * @param from X軸のFrom
     */
    public void setXFrom(int from);

    /**
     * X軸のToを取得する。<p>
     *
     * @return X軸のTo
     */
    public int getXTo();

    /**
     * X軸のToを設定する。<p>
     *
     * @param to X軸のTo
     */
    public void setXTo(int to);

    /**
     * Y軸のFromを取得する。<p>
     *
     * @return Y軸のFrom
     */
    public int getYFrom();

    /**
     * Y軸のFromを設定する。<p>
     *
     * @param from Y軸のFrom
     */
    public void setYFrom(int from);

    /**
     * Y軸のToを取得する。<p>
     *
     * @return Y軸のTo
     */
    public int getYTo();

    /**
     * Y軸のToを設定する。<p>
     *
     * @param to Y軸のTo
     */
    public void setYTo(int to);

}
