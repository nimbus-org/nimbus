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
package jp.ossc.nimbus.service.test.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.diff.JsonDiff;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.service.test.ChainTestAction;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.TestContext;

/**
 * JSONの差分をファイル保存するテストアクション。
 * <p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Ishida
 */
public class JSONDiffGetActionService extends ServiceBase
        implements TestAction, ChainTestAction.TestActionProcess, TestActionEstimation, JSONDiffGetActionServiceMBean {
    
    private static final long serialVersionUID = 7682175982096275432L;
    protected double expectedCost = Double.NaN;
    
    protected String textFileEncoding;
    protected String diffFileEncoding;
    
    public String getTextFileEncoding() {
        return textFileEncoding;
    }
    
    public void setTextFileEncoding(String encoding) {
        textFileEncoding = encoding;
    }
    
    public String getDiffFileEncoding() {
        return diffFileEncoding;
    }
    
    public void setDiffFileEncoding(String encoding) {
        diffFileEncoding = encoding;
    }
    
    public void startService() throws Exception {
        if (textFileEncoding != null && !Charset.isSupported(textFileEncoding)) {
            throw new IllegalArgumentException("TextFileEncoding is not supported. TextFileEncoding=" + textFileEncoding);
        }
        if (diffFileEncoding != null && !Charset.isSupported(diffFileEncoding)) {
            throw new IllegalArgumentException("DiffFileEncoding is not supported. DiffFileEncoding=" + diffFileEncoding);
        }
    }
    
    /**
     * リソースの内容を読み込んで、JSONの差分をファイル保存する。
     * <p>
     * リソースのフォーマットは、以下。<br>
     * 
     * <pre>
     * original
     * revised
     * </pre>
     * 
     * originalは、比較元となるファイル、またはJSONファイルオブジェクト、文字列オブジェクト、InputStreamオブジェクトを指定するもので、ファイルの場合は比較元のテキストファイルのパスを指定する。ファイルオブジェクトの場合は同一テストケース中に、このTestActionより前に、比較元となるファイルオブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、比較元となるファイルオブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。<br>
     * revisedは、比較元となるファイル、またはJSONファイルオブジェクト、文字列オブジェクト、InputStreamオブジェクトを指定するもので、ファイルの場合は比較元のテキストファイルのパスを指定する。ファイルオブジェクトの場合は同一テストケース中に、このTestActionより前に、比較元となるファイルオブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、比較元となるファイルオブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return 差分ファイル
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception {
        return execute(context, actionId, null, resource);
    }
    
    /**
     * リソースの内容を読み込んで、テキストの差分をファイル保存する。
     * <p>
     * リソースのフォーマットは、以下。<br>
     * 
     * <pre>
     * original
     * revised
     * </pre>
     * 
     * originalは、比較元となるファイル、またはJSONファイルオブジェクト、文字列オブジェクト、InputStreamオブジェクトを指定するもので、ファイルの場合は比較元のテキストファイルのパスを指定する。ファイルオブジェクトの場合は同一テストケース中に、このTestActionより前に、比較元となるファイルオブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、比較元となるファイルオブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。<br>
     * revisedは、比較元となるファイル、またはJSONファイルオブジェクト、文字列オブジェクト、InputStreamオブジェクトを指定するもので、ファイルの場合は比較元のテキストファイルのパスを指定する。ファイルオブジェクトの場合は同一テストケース中に、このTestActionより前に、比較元となるファイルオブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、比較元となるファイルオブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。preResultを使用する場合は、空行を指定する。<br>
     * 
     * @param context コンテキスト
     * @param actionId アクションID
     * @param preResult 比較対象となるファイルオブジェクト
     * @param resource リソース
     * @return 差分ファイル
     * 
     */
    public Object execute(TestContext context, String actionId, Object preResult, Reader resource) throws Exception {
        BufferedReader br = new BufferedReader(resource);
        try {
            File diffFile = new File(context.getCurrentDirectory(), actionId + ".diff");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode orgNode = null;
            JsonNode revNode = null;
            if (preResult != null) {
                if (preResult instanceof File) {
                    BufferedReader reader = null;
                    try {
                        reader = textFileEncoding == null ? new BufferedReader(new InputStreamReader(new FileInputStream((File) preResult)))
                                : new BufferedReader(new InputStreamReader(new FileInputStream((File) preResult), textFileEncoding));
                        revNode = mapper.readTree(reader);
                    } finally {
                        if (reader != null) {
                            reader.close();
                            reader = null;
                        }
                    }
                } else if (preResult instanceof String) {
                    revNode = mapper.readTree((String) preResult);
                } else if (preResult instanceof InputStream) {
                    revNode = mapper.readTree((InputStream) preResult);
                }
            }
            final String orgLine = br.readLine();
            if(orgLine == null || orgLine.length() == 0) {
                throw new Exception("original is Empty.");
            }
            orgNode = getJsonNode(context, orgLine);
            if (revNode == null) {
                final String revLine = br.readLine();
                if(revLine == null || revLine.length() == 0) {
                    throw new Exception("revised is Empty.");
                }
                revNode = getJsonNode(context, revLine);
            }
            JsonNode diff = JsonDiff.asJson(orgNode, revNode);
            createDiffText(diffFile, diff.toString());
            return diffFile;
        } finally {
            br.close();
            br = null;
        }
    }
    
    protected JsonNode getJsonNode(TestContext context, String str) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode result = null;
        Object actionResult = null;
        if (str.indexOf(",") == -1) {
            actionResult = context.getTestActionResult(str);
        } else {
            String[] ids = str.split(",");
            if (ids.length == 2) {
                actionResult = context.getTestActionResult(ids[0], ids[1]);
            }
        }
        if (actionResult != null) {
            if (actionResult instanceof File) {
                BufferedReader reader = null;
                try {
                    reader = textFileEncoding == null ? new BufferedReader(new InputStreamReader(new FileInputStream((File) actionResult)))
                            : new BufferedReader(new InputStreamReader(new FileInputStream((File) actionResult), textFileEncoding));
                    result = mapper.readTree(reader);
                } finally {
                    if (reader != null) {
                        reader.close();
                        reader = null;
                    }
                }
            } else if (actionResult instanceof String) {
                result = mapper.readTree((String) actionResult);
            } else if (actionResult instanceof InputStream) {
                result = mapper.readTree((InputStream) actionResult);
            }
        }
        if (result == null) {
            File orgFile = new File(str);
            if (!orgFile.exists()) {
                orgFile = new File(context.getCurrentDirectory(), str);
            }
            if (orgFile.exists()) {
                BufferedReader reader = null;
                try {
                    reader = textFileEncoding == null ? new BufferedReader(new InputStreamReader(new FileInputStream(orgFile)))
                            : new BufferedReader(new InputStreamReader(new FileInputStream(orgFile), textFileEncoding));
                    result = mapper.readTree(reader);
                } finally {
                    if (reader != null) {
                        reader.close();
                        reader = null;
                    }
                }
            }
        }
        if (result == null) {
            result = mapper.readTree(str);
        }
        return result;
    }
    
    private void createDiffText(File file, String diffString) throws Exception {
        Writer writer = null;
        try {
            writer = diffFileEncoding == null ? new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))
                    : new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), diffFileEncoding));
            writer.write(diffString);
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
                writer = null;
            }
        }
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
}
