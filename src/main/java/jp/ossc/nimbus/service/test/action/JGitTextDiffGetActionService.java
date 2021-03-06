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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.HistogramDiff;
import org.eclipse.jgit.diff.MyersDiff;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.service.test.ChainTestAction;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.TestContext;

/**
 * テキストの差分をファイル保存するテストアクション。
 * <p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Ishida
 */
public class JGitTextDiffGetActionService extends ServiceBase implements TestAction, ChainTestAction.TestActionProcess, TestActionEstimation, JGitTextDiffGetActionServiceMBean {
    
    private static final long serialVersionUID = 7682175982096275432L;
    protected double expectedCost = Double.NaN;
    
    protected int diffAlgorithmType = DIFF_ALGORITHM_TYPE_JGIT_HISTGRAM;
    protected String textFileEncoding;
    protected String diffFileEncoding;
    
    public int getDiffAlgorithmType() {
        return diffAlgorithmType;
    }

    public void setDiffAlgorithmType(int algorithmType) {
        diffAlgorithmType = algorithmType;
    }

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
     * リソースの内容を読み込んで、テキストの差分をファイル保存する。
     * <p>
     * リソースのフォーマットは、以下。<br>
     * 
     * <pre>
     * original
     * revised
     * </pre>
     * 
     * originalは、比較元となるファイル、またはファイルオブジェクトを指定するもので、ファイルの場合は比較元のテキストファイルのパスを指定する。ファイルオブジェクトの場合は同一テストケース中に、このTestActionより前に、比較元となるファイルオブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、比較元となるファイルオブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。<br>
     * revisedは、比較元となるファイル、またはファイルオブジェクトを指定するもので、ファイルの場合は比較元のテキストファイルのパスを指定する。ファイルオブジェクトの場合は同一テストケース中に、このTestActionより前に、比較元となるファイルオブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、比較元となるファイルオブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return 差分ファイル
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
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
     * originalは、比較元となるファイル、またはファイルオブジェクトを指定するもので、ファイルの場合は比較元のテキストファイルのパスを指定する。ファイルオブジェクトの場合は同一テストケース中に、このTestActionより前に、比較元となるファイルオブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、比較元となるファイルオブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。<br>
     * revisedは、比較元となるファイル、またはファイルオブジェクトを指定するもので、ファイルの場合は比較元のテキストファイルのパスを指定する。ファイルオブジェクトの場合は同一テストケース中に、このTestActionより前に、比較元となるファイルオブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、比較元となるファイルオブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。preResultを使用する場合は、空行を指定する。<br>
     * 
     * @param context コンテキスト
     * @param actionId アクションID
     * @param preResult 比較対象となるファイルオブジェクト
     * @param resource リソース
     * @return 差分ファイル
     * 
     */
    public Object execute(TestContext context, String actionId, Object preResult, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        try {
            File diffFile = new File(context.getCurrentDirectory(), actionId + ".diff");
            String orgLine = br.readLine();
            File orgFile = getTargetFile(context, orgLine);
            File revFile = null;
            if(preResult != null && preResult instanceof File) {
                revFile = (File)preResult;
            }
            if(revFile == null) {
                String revLine = br.readLine();
                revFile = getTargetFile(context, revLine);
            }
            switch (diffAlgorithmType) {
            case DIFF_ALGORITHM_TYPE_JGIT_HISTGRAM:
                histgram(diffFile, orgFile, revFile);
                break;
            case DIFF_ALGORITHM_TYPE_JGIT_MYERS:
                myers(diffFile, orgFile, revFile);
                break;
            default:
                throw new IllegalArgumentException("DiffAlgorithmType is illegal. DiffAlgorithmType=" + diffAlgorithmType);
            }
            return diffFile;
        } finally {
            br.close();
            br = null;
        }
    }
    
    protected File getTargetFile(TestContext context, String str) throws Exception {
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
                return (File)actionResult;
            } else {
                throw new Exception("Target action result is not File Object. ActionId=" + str);
            }
        }
        File result = new File(str);
        if (!result.exists()) {
            result = new File(context.getCurrentDirectory(), str);
        }
        if (result.exists()) {
            return result;
        } else {
            throw new Exception("Target file is not found. args=" + str);
        }
    }
    
    protected void histgram(File diffFile, File orgFile, File revFile) throws Exception {
        DiffAlgorithm diffAlgorithm = new HistogramDiff();
        RawText org = createRawText(orgFile);
        RawText rev = createRawText(revFile);
        EditList editList = diffAlgorithm.diff(RawTextComparator.DEFAULT, org, rev);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DiffFormatter formatter = null;
        try {
            formatter = new DiffFormatter(baos);
            formatter.format(editList, org, rev);
            createDiffText(diffFile, baos.toString());
        } finally {
            if (formatter != null) {
                formatter.flush();
                formatter = null;
            }
            if (baos != null) {
                baos.close();
                baos = null;
            }
        }
    }
    
    protected void myers(File diffFile, File orgFile, File revFile) throws Exception {
        RawText org = createRawText(orgFile);
        RawText rev = createRawText(revFile);
        EditList editList = MyersDiff.INSTANCE.diff(RawTextComparator.DEFAULT, org, rev);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DiffFormatter formatter = null;
        try {
            formatter = new DiffFormatter(baos);
            formatter.format(editList, org, rev);
            createDiffText(diffFile, baos.toString());
        } finally {
            if (formatter != null) {
                formatter.flush();
                formatter = null;
            }
            if (baos != null) {
                baos.close();
                baos = null;
            }
        }
    }
    
    private RawText createRawText(File file) throws Exception {
        BufferedReader reader = null;
        ByteArrayOutputStream baos = null;
        try {
            Reader isr = textFileEncoding == null ? new InputStreamReader(new FileInputStream(file))
                    : new InputStreamReader(new FileInputStream(file), textFileEncoding);
            reader = new ExBufferedReader(isr);
            baos = new ByteArrayOutputStream();
            String line = null;
            while ((line = reader.readLine()) != null) {
                baos.write(line.getBytes());
            }
            return new RawText(baos.toByteArray());
        } finally {
            if (baos != null) {
                baos.close();
                baos = null;
            }
            if (reader != null) {
                reader.close();
                reader = null;
            }
        }
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
    
    protected class ExBufferedReader extends BufferedReader {
        
        public ExBufferedReader(Reader in) {
            super(in);
        }
     
        public ExBufferedReader(Reader in, int sz) {
            super(in, sz);
        }
     
        public String readLine() throws IOException {
            int num = 0;
            StringBuilder sb = new StringBuilder();
            while ((num = read()) >= 0) {
                sb.append((char) num);
                switch ((char) num) {
                case '\r':
                case '\n':
                    return sb.toString();
                default:
                    break;
                }
            }
            return sb.length() == 0 ? null : sb.toString();
        }
    }
}
