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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.StringWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.service.test.EvaluateTestAction;
import jp.ossc.nimbus.service.test.TestContext;

/**
 * テキスト比較評価アクション。<p>
 * ２つのテキストファイルを比較して、内容が等価かどうか評価する。<br>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class TextCompareEvaluateActionService extends ServiceBase implements EvaluateTestAction, TextCompareEvaluateActionServiceMBean{
    
    private static final long serialVersionUID = -6946310231201742494L;
    
    protected String fileEncoding;
    protected int[] matchFlags;
    protected int matchFlag;
    protected boolean isOutputFileAfterEdit;
    protected String fileAfterEditExtention = DEFAULT_AFTER_EDIT_FILE_EXTENTION;
    protected double expectedCost = 0d;
    protected boolean isResultNGOnNotFoundDestFile;
    
    public void setFileEncoding(String encoding){
        fileEncoding = encoding;
    }
    public String getFileEncoding(){
        return fileEncoding;
    }
    
    public void setMatchFlags(int[] flags){
        matchFlags = flags;
        matchFlag = 0;
        if(matchFlags != null){
            for(int i = 0; i < matchFlags.length; i++){
                matchFlag |= matchFlags[i];
            }
        }
    }
    public int[] getMatchFlags(){
        return matchFlags;
    }
    
    public boolean isOutputFileAfterEdit(){
        return isOutputFileAfterEdit;
    }
    public void setOutputFileAfterEdit(boolean isOutput){
        isOutputFileAfterEdit = isOutput;
    }
    
    public void setFileAfterEditExtention(String extention){
        if(extention.charAt(0) != '.'){
            extention = '.' + extention;
        }
        fileAfterEditExtention = extention;
    }
    public String getFileAfterEditExtention(){
        return fileAfterEditExtention;
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    public double getExpectedCost() {
        return expectedCost;
    }
    
    public boolean isResultNGOnNotFoundDestFile(){
        return isResultNGOnNotFoundDestFile;
    }
    public void setResultNGOnNotFoundDestFile(boolean isResultNG){
        isResultNGOnNotFoundDestFile = isResultNG;
    }
    
    /**
     * ２つのテキストファイルを比較して、内容が等価かどうか評価する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * srcFilePath
     * dstFilePath
     * ignoreRegexPattern
     * </pre>
     * srcFilePathは、比較元のテキストファイルのパスを指定する。<br>
     * dstFilePathは、比較先のテキストファイルのパスを指定する。ここで指定したファイルが存在しない場合は、比較を行わずにtrueを返す。<br>
     * ignoreRegexPatternは、比較時に無視する内容を正規表現で指定する。複数指定する場合は、改行して指定する。この正規表現に一致する内容は、空文字に置換して比較する。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return 比較結果が等しい場合は、true
     */
    public boolean execute(TestContext context, String actionId, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        File srcFile = null;
        File dstFile = null;
        List ignorePatternList = null;
        try{
            final String srcFilePath = br.readLine();
            if(srcFilePath == null){
                throw new Exception("Unexpected EOF on srcFilePath");
            }
            final String dstFilePath = br.readLine();
            if(dstFilePath == null){
                throw new Exception("Unexpected EOF on dstFilePath");
            }
            srcFile = new File(srcFilePath);
            if(!srcFile.exists()){
                srcFile = new File(context.getCurrentDirectory(), srcFilePath);
            }
            if(!srcFile.exists()){
                throw new Exception("File not found. srcFilePath=" + srcFilePath);
            }
            dstFile = new File(dstFilePath);
            if(!dstFile.exists()){
                dstFile = new File(context.getCurrentDirectory(), dstFilePath);
            }
            if(!dstFile.exists()){
                return !isResultNGOnNotFoundDestFile;
            }
            String line = null;
            while((line = br.readLine()) != null){
                Pattern pattern = matchFlag == 0 ? Pattern.compile(line) : Pattern.compile(line, matchFlag);
                if(ignorePatternList == null){
                    ignorePatternList = new ArrayList();
                }
                ignorePatternList.add(pattern);
            }
        }finally{
            br.close();
            br = null;
        }
        
        String srcStr = null;
        StringWriter sw = new StringWriter();
        InputStreamReader isr = fileEncoding == null ? new InputStreamReader(new FileInputStream(srcFile)) : new InputStreamReader(new FileInputStream(srcFile), fileEncoding);
        char[] buf = new char[1024];
        int len = 0;
        try{
            while((len = isr.read(buf, 0 , buf.length)) > 0){
                sw.write(buf, 0, len);
            }
            srcStr = sw.toString();
            sw.close();
            sw = null;
        }finally{
            isr.close();
            isr = null;
        }
        String dstStr = null;
        sw = new StringWriter();
        isr = fileEncoding == null ? new InputStreamReader(new FileInputStream(dstFile)) : new InputStreamReader(new FileInputStream(dstFile), fileEncoding);
        try{
            while((len = isr.read(buf, 0 , buf.length)) > 0){
                sw.write(buf, 0, len);
            }
            dstStr = sw.toString();
            sw.close();
            sw = null;
        }finally{
            isr.close();
            isr = null;
        }
        if(ignorePatternList != null){
            for(int i = 0; i < ignorePatternList.size(); i++){
                Pattern pattern = (Pattern)ignorePatternList.get(i);
                Matcher matcher = pattern.matcher(srcStr);
                srcStr = matcher.replaceAll("");
                matcher = pattern.matcher(dstStr);
                dstStr = matcher.replaceAll("");
            }
        }
        if(isOutputFileAfterEdit){
            File editSrcFile = new File(srcFile.getPath() + fileAfterEditExtention);
            OutputStreamWriter osw = fileEncoding == null ? new OutputStreamWriter(new FileOutputStream(editSrcFile)) : new OutputStreamWriter(new FileOutputStream(editSrcFile), fileEncoding);
            try{
                char[] chars = srcStr.toCharArray();
                osw.write(chars, 0, chars.length);
                osw.flush();
            }finally{
                osw.close();
            }
            File editDstFile = new File(dstFile.getPath() + fileAfterEditExtention);
            osw = fileEncoding == null ? new OutputStreamWriter(new FileOutputStream(editDstFile)) : new OutputStreamWriter(new FileOutputStream(editDstFile), fileEncoding);
            try{
                char[] chars = dstStr.toCharArray();
                osw.write(chars, 0, chars.length);
                osw.flush();
            }finally{
                osw.close();
            }
        }
        
        return srcStr.equals(dstStr);
    }
}
