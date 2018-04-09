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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.StringWriter;
import java.io.Reader;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.Utility;
import jp.ossc.nimbus.io.RecurciveSearchFile;
import jp.ossc.nimbus.service.test.EvaluateTestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.ChainEvaluateTestAction;
import jp.ossc.nimbus.service.test.TestContext;

/**
 * テキスト評価アクション。<p>
 * テキストファイルの内容を評価する。<br>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class TextEvaluateActionService extends ServiceBase implements EvaluateTestAction, TestActionEstimation, ChainEvaluateTestAction.EvaluateTestActionProcess, TextEvaluateActionServiceMBean{
    
    private static final long serialVersionUID = -6010305153757182392L;
    
    protected String fileEncoding;
    protected int[] matchFlags;
    protected int matchFlag;
    protected double expectedCost = Double.NaN;
    
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
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    public double getExpectedCost() {
        return expectedCost;
    }
    
    /**
     * テキストファイルの内容を評価する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * filePath
     * matchRegexPattern
     * matchType
     * resultFlag
     * </pre>
     * filePathは、テキストファイルのパスを指定する。"${"と"}"に囲まれた文字列は、同名のシステムプロパティ、サービスローダ構成プロパティ、マネージャプロパティ、サーバプロパティと置換される。<br>
     * matchRegexPatternは、合致条件を正規表現で指定する。<br>
     * matchTypeは、全体一致を示す"all"か、部分一致を示す"part"。<br>
     * resultFlagは、条件に合致した場合のメソッドの戻り値を、trueまたはfalseで指定する。<p>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return 合致する場合は、true
     */
    public boolean execute(TestContext context, String actionId, Reader resource) throws Exception{
        return execute(context, actionId, null, resource);
    }
    
    /**
     * テキストファイルの内容を評価する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * filePath
     * matchRegexPattern
     * matchType
     * resultFlag
     * </pre>
     * filePathは、テキストファイルのパスを指定する。"${"と"}"に囲まれた文字列は、同名のシステムプロパティ、サービスローダ構成プロパティ、マネージャプロパティ、サーバプロパティと置換される。空行を指定した場合は、preResultをテキスト文字列として解釈する。<br>
     * matchRegexPatternは、合致条件を正規表現で指定する。<br>
     * matchTypeは、全体一致を示す"all"か、部分一致を示す"part"。<br>
     * resultFlagは、条件に合致した場合のメソッドの戻り値を、trueまたはfalseで指定する。<p>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return 合致する場合は、true
     */
    public boolean execute(TestContext context, String actionId, Object preResult, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        List files = null;
        Pattern pattern = null;
        String matchType = null;
        boolean resultFlag = true;
        try{
            String filePath = br.readLine();
            if(filePath == null){
                throw new Exception("Unexpected EOF on filePath");
            }
            if(filePath.length() != 0){
                files = toFiles(context, filePath);
                if(files == null || files.size() == 0){
                    throw new Exception("File not found. filePath=" + filePath);
                }
            }
            final String matchRegexPattern = br.readLine();
            if(matchRegexPattern == null){
                throw new Exception("Unexpected EOF on matchRegexPattern");
            }
            pattern = Pattern.compile(matchRegexPattern);
            matchType = br.readLine();
            if(matchType == null){
                throw new Exception("Unexpected EOF on matchType");
            }
            if(!"all".equals(matchType) && !"part".equals(matchType)){
                throw new Exception("Unknown matchType : " + matchType);
            }
            String resultFlagStr = br.readLine();
            if(resultFlagStr == null){
                throw new Exception("Unexpected EOF on resultFlag");
            }
            resultFlag = Boolean.valueOf(resultFlagStr.trim()).booleanValue();
        }finally{
            br.close();
            br = null;
        }
        
        String srcStr = null;
        if(files == null){
            srcStr = preResult == null ? "null" : preResult.toString();
            final Matcher matcher = pattern.matcher(srcStr);
            if("all".equals(matchType)){
                return matcher.matches() ? resultFlag : !resultFlag;
            }else{
                return matcher.find() ? resultFlag : !resultFlag;
            }
        }else{
            char[] buf = new char[1024];
            for(int i = 0; i < files.size(); i++){
                File file = (File)files.get(i);
                StringWriter sw = new StringWriter();
                InputStreamReader isr = fileEncoding == null ? new InputStreamReader(new FileInputStream(file)) : new InputStreamReader(new FileInputStream(file), fileEncoding);
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
                final Matcher matcher = pattern.matcher(srcStr);
                if("all".equals(matchType)){
                    if(!(matcher.matches() ? resultFlag : !resultFlag)){
                        return false;
                    }
                }else{
                    if(!(matcher.find() ? resultFlag : !resultFlag)){
                        return false;
                    }
                }
            }
            return true;
        }
    }
    
    protected List toFiles(TestContext context, String path) throws IOException{
        List result = null;
        path = replaceProperty(path);
        if(path.equals(".")){
            path = context.getCurrentDirectory().getPath();
        }else if(path.startsWith("./")){
            path = context.getCurrentDirectory().getPath() + path.substring(1);
        }
        File file = new File(path);
        if(file.exists()){
            result = new ArrayList();
            result.add(file);
        }else{
            File parentFile = file;
            while((parentFile = parentFile.getParentFile()) != null && !parentFile.exists());
            
            if(parentFile == null){
                parentFile = context.getCurrentDirectory();
            }else{
                path = path.substring(parentFile.getPath().length() + 1);
            }
            try{
                RecurciveSearchFile rsf = new RecurciveSearchFile(parentFile);
                File[] files = rsf.listAllTreeFiles(path, RecurciveSearchFile.SEARCH_TYPE_FILE);
                if(files != null && files.length != 0){
                    result = new ArrayList();
                    for(int i = 0; i < files.length; i++){
                        result.add(files[i]);
                    }
                }
            }catch(PatternSyntaxException e){
            }
        }
        return result;
    }
    
    protected String replaceProperty(String textValue){
        
        // システムプロパティの置換
        textValue = Utility.replaceSystemProperty(textValue);
        
        // サービスローダ構成プロパティの置換
        if(getServiceLoader() != null){
            textValue = Utility.replaceServiceLoderConfig(
                textValue,
                getServiceLoader().getConfig()
            );
        }
        
        // マネージャプロパティの置換
        if(getServiceManager() != null){
            textValue = Utility.replaceManagerProperty(
                getServiceManager(),
                textValue
            );
        }
        
        // サーバプロパティの置換
        textValue = Utility.replaceServerProperty(textValue);
        
        return textValue;
    }
}