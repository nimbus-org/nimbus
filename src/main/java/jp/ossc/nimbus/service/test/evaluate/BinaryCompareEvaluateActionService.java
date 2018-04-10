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
package jp.ossc.nimbus.service.test.evaluate;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.service.test.EvaluateTestAction;
import jp.ossc.nimbus.service.test.TestContext;

/**
 * バイナリファイル比較評価アクション。<p>
 * ２つのバイナリファイルを比較して、内容が等価かどうか評価する。<br>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Ishida
 */
public class BinaryCompareEvaluateActionService extends ServiceBase implements EvaluateTestAction, BinaryCompareEvaluateActionServiceMBean {
    
    private static final long serialVersionUID = -6946310231201742494L;
    
    protected double expectedCost = Double.NaN;
    protected boolean isResultNGOnNotFoundDestFile;
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
    
    public boolean isResultNGOnNotFoundDestFile() {
        return isResultNGOnNotFoundDestFile;
    }
    
    public void setResultNGOnNotFoundDestFile(boolean isResultNG) {
        isResultNGOnNotFoundDestFile = isResultNG;
    }
    
    /**
     * ２つのバイナリファイルを比較して、内容が等価かどうか評価する。
     * <p>
     * リソースのフォーマットは、以下。<br>
     * 
     * <pre>
     * srcFilePath
     * dstFilePath
     * </pre>
     * 
     * srcFilePathは、比較元のテキストファイルのパスを指定する。<br>
     * dstFilePathは、比較先のテキストファイルのパスを指定する。ここで指定したファイルが存在しない場合は、比較を行わずにtrueを返す。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return 比較結果が等しい場合は、true
     */
    public boolean execute(TestContext context, String actionId, Reader resource) throws Exception {
        BufferedReader br = new BufferedReader(resource);
        File srcFile = null;
        File dstFile = null;
        try {
            final String srcFilePath = br.readLine();
            if (srcFilePath == null) {
                throw new Exception("Unexpected EOF on srcFilePath");
            }
            final String dstFilePath = br.readLine();
            if (dstFilePath == null) {
                throw new Exception("Unexpected EOF on dstFilePath");
            }
            srcFile = new File(srcFilePath);
            if (!srcFile.exists()) {
                srcFile = new File(context.getCurrentDirectory(), srcFilePath);
            }
            if (!srcFile.exists()) {
                throw new Exception("File not found. srcFilePath=" + srcFilePath);
            }
            dstFile = new File(dstFilePath);
            if (!dstFile.exists()) {
                dstFile = new File(context.getCurrentDirectory(), dstFilePath);
            }
            if (!dstFile.exists()) {
                return !isResultNGOnNotFoundDestFile;
            }
        } finally {
            br.close();
            br = null;
        }
        
        BufferedInputStream bis_src = null;
        BufferedInputStream bis_dst = null;
        
        try {
            int tmp_src = 0;
            int tmp_dst = 0;
            bis_src = new BufferedInputStream(new FileInputStream(srcFile));
            bis_dst = new BufferedInputStream(new FileInputStream(dstFile));
            while ((tmp_src = bis_src.read()) != -1 || (tmp_dst = bis_dst.read()) != -1) {
                if((tmp_src == -1 && tmp_dst != -1) || (tmp_src != -1 && tmp_dst == -1) || tmp_src != tmp_dst ) {
                    return false;
                }
            }
        } finally {
            try {
                if(bis_src != null) {
                    bis_src.close();
                }
            } catch (IOException e) {
            }
            try {
                if(bis_dst != null) {
                    bis_dst.close();
                }
            } catch (IOException e) {
            }
        }
        return true;
    }
}
