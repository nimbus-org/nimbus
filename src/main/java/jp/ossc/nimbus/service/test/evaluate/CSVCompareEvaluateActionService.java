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
import java.io.Reader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.io.CSVReader;
import jp.ossc.nimbus.io.CSVWriter;
import jp.ossc.nimbus.service.test.EvaluateTestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.TestContext;

/**
 * CSVファイル比較評価アクション。<p>
 * ２つのCSVファイルを比較して、内容が等価かどうか評価する。<br>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class CSVCompareEvaluateActionService extends ServiceBase implements EvaluateTestAction, TestActionEstimation, CSVCompareEvaluateActionServiceMBean{
    
    private static final long serialVersionUID = -7771606827015084764L;
    
    protected String fileEncoding;
    protected boolean isOutputFileAfterEdit;
    protected String fileAfterEditExtention = DEFAULT_AFTER_EDIT_FILE_EXTENTION;
    protected CSVReader csvReader;
    protected CSVWriter csvWriter;
    protected double expectedCost = 0d;
    protected boolean isResultNGOnNotFoundDestFile;
    
    public void setFileEncoding(String encoding){
        fileEncoding = encoding;
    }
    public String getFileEncoding(){
        return fileEncoding;
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
    
    /**
     * 対象のCSVファイルの読み込みに使用する{@link CSVReader}を設定する。<p>
     *
     * @param reader CSVReader
     */
    public void setCSVReader(CSVReader reader){
        csvReader = reader;
    }
    
    /**
     * 比較対象のみに編集を行ったCSVファイルの書き込みに使用する{@link CSVWriter}を設定する。<p>
     *
     * @param writer CSVWriter
     */
    public void setCSVWriter(CSVWriter writer){
        csvWriter = writer;
    }
    
    public boolean isResultNGOnNotFoundDestFile(){
        return isResultNGOnNotFoundDestFile;
    }
    public void setResultNGOnNotFoundDestFile(boolean isResultNG){
        isResultNGOnNotFoundDestFile = isResultNG;
    }
    
    /**
     * ２つのCSVファイルを比較して、内容が等価かどうか評価する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * srcFilePath
     * dstFilePath
     * ignoreCSVElements
     * </pre>
     * srcFilePathは、比較元のCSVファイルのパスを指定する。<br>
     * dstFilePathは、比較先のCSVファイルのパスを指定する。ここで指定したファイルが存在しない場合は、比較を行わずにtrueを返す。<br>
     * ignoreCSVElementsは、比較時に無視するCSV要素を要素名または要素インデックスで指定する。要素名を指定する場合は、CSVファイルの一行目にCSV要素名ヘッダ行が必要である。要素インデックスは、0から開始する。複数指定する場合は、カンマ区切りで指定する。<br>
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
        int[] ignoreIndexes = null;
        String[] ignoreNames = null;
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
            String ignoreCSVElementsStr = br.readLine();
            if(ignoreCSVElementsStr != null){
                final String[] ignoreCSVElements = CSVReader.toArray(
                    ignoreCSVElementsStr,
                    ',',
                    '\\',
                    '"',
                    "null",
                    "#",
                    false,
                    false,
                    true,
                    false
                );
                if(ignoreCSVElements != null && ignoreCSVElements.length != 0){
                    try{
                        ignoreIndexes = new int[ignoreCSVElements.length];
                        for(int i = 0; i < ignoreCSVElements.length; i++){
                            ignoreIndexes[i] = Integer.parseInt(ignoreCSVElements[i]);
                        }
                        
                    }catch(NumberFormatException e){
                        ignoreNames = ignoreCSVElements;
                        ignoreIndexes = null;
                    }
                }
            }
        }finally{
            br.close();
            br = null;
        }
        
        InputStreamReader srcisr = null;
        InputStreamReader dstisr = null;
        CSVReader srccsvr = null;
        CSVReader dstcsvr = null;
        OutputStreamWriter srcosw = null;
        OutputStreamWriter dstosw = null;
        CSVWriter srccsvw = null;
        CSVWriter dstcsvw = null;
        boolean result = true;
        try{
            srcisr = fileEncoding == null ? new InputStreamReader(new FileInputStream(srcFile)) : new InputStreamReader(new FileInputStream(srcFile), fileEncoding);
            dstisr = fileEncoding == null ? new InputStreamReader(new FileInputStream(dstFile)) : new InputStreamReader(new FileInputStream(dstFile), fileEncoding);
            srccsvr = csvReader == null ? new CSVReader() : csvReader.cloneReader();
            dstcsvr = csvReader == null ? new CSVReader() : csvReader.cloneReader();
            srccsvr.setReader(srcisr);
            dstcsvr.setReader(dstisr);
            if(isOutputFileAfterEdit){
                File editSrcFile = new File(srcFile.getPath() + fileAfterEditExtention);
                File editDstFile = new File(dstFile.getPath() + fileAfterEditExtention);
                srcosw = fileEncoding == null ? new OutputStreamWriter(new FileOutputStream(editSrcFile)) : new OutputStreamWriter(new FileOutputStream(editSrcFile), fileEncoding);
                dstosw = fileEncoding == null ? new OutputStreamWriter(new FileOutputStream(editDstFile)) : new OutputStreamWriter(new FileOutputStream(editDstFile), fileEncoding);
                srccsvw = csvWriter == null ? new CSVWriter() : csvWriter.cloneWriter();
                dstcsvw = csvWriter == null ? new CSVWriter() : csvWriter.cloneWriter();
                srccsvw.setWriter(srcosw);
                dstcsvw.setWriter(dstosw);
            }
            List srccsv = new ArrayList();
            List dstcsv = new ArrayList();
            boolean[] ignores = null;
            if(ignoreNames != null && ignoreNames.length != 0){
                Set ignoreNameSet = new HashSet();
                for(int i = 0; i < ignoreNames.length; i++){
                    ignoreNameSet.add(ignoreNames[i]);
                }
                
                srccsv = srccsvr.readCSVLineList(srccsv);
                if(srccsv != null && srccsv.size() != 0){
                    ignores = new boolean[srccsv.size()];
                    for(int i = 0; i < srccsv.size(); i++){
                        ignores[i] = ignoreNameSet.contains(srccsv.get(i));
                    }
                }
                if(isOutputFileAfterEdit){
                    writeCSV(srccsvw, srccsv, ignores);
                }
                dstcsv = dstcsvr.readCSVLineList(dstcsv);
                if(isOutputFileAfterEdit){
                    writeCSV(dstcsvw, dstcsv, ignores);
                }
                if(!compareCSVList(srccsv, dstcsv, ignores)){
                    return false;
                }
            }else if(ignoreIndexes != null && ignoreIndexes.length != 0){
                
                srccsv = srccsvr.readCSVLineList(srccsv);
                if(srccsv != null && srccsv.size() != 0){
                    ignores = new boolean[srccsv.size()];
                    for(int i = 0, imax = ignoreIndexes.length; i < imax; i++){
                        if(ignores.length > ignoreIndexes[i]){
                            ignores[ignoreIndexes[i]] = true;
                        }
                    }
                }
                if(isOutputFileAfterEdit){
                    writeCSV(srccsvw, srccsv, ignores);
                }
                dstcsv = dstcsvr.readCSVLineList(dstcsv);
                if(isOutputFileAfterEdit){
                    writeCSV(dstcsvw, dstcsv, ignores);
                }
                if(!compareCSVList(srccsv, dstcsv, ignores)){
                    return false;
                }
            }
            do{
                if(srccsv != null){
                    srccsv = srccsvr.readCSVLineList(srccsv);
                }
                if(dstcsv != null){
                    dstcsv = dstcsvr.readCSVLineList(dstcsv);
                }
                if(isOutputFileAfterEdit){
                    writeCSV(srccsvw, srccsv, ignores);
                    writeCSV(dstcsvw, dstcsv, ignores);
                }
                result &= compareCSVList(srccsv, dstcsv, ignores);
            }while(isOutputFileAfterEdit ? (srccsv != null || dstcsv != null) : result);
        }finally{
            if(srcisr != null){
                try{
                    srcisr.close();
                }catch(IOException e){}
            }
            if(dstisr != null){
                try{
                    dstisr.close();
                }catch(IOException e){}
            }
            if(srccsvr != null){
                try{
                    srccsvr.close();
                }catch(IOException e){}
            }
            if(dstcsvr != null){
                try{
                    dstcsvr.close();
                }catch(IOException e){}
            }
            if(isOutputFileAfterEdit){
                srccsvw.flush();
                dstcsvw.flush();
            }
            if(srcosw != null){
                try{
                    srcosw.close();
                }catch(IOException e){}
            }
            if(dstosw != null){
                try{
                    dstosw.close();
                }catch(IOException e){}
            }
            if(srccsvw != null){
                try{
                    srccsvw.close();
                }catch(IOException e){}
            }
            if(dstcsvw != null){
                try{
                    dstcsvw.close();
                }catch(IOException e){}
            }
        }
        
        return result;
    }
    
    protected boolean compareCSVList(List src, List dst, boolean[] ignores){
        if(src == null){
            return dst == null ? true : false;
        }
        if(src.size() != dst.size()){
            return false;
        }
        for(int i = 0; i < src.size(); i++){
            if(ignores != null && i < ignores.length && ignores[i]){
                continue;
            }
            if(!src.get(i).equals(dst.get(i))){
                return false;
            }
        }
        return true;
    }
    
    protected void writeCSV(CSVWriter writer, List csv, boolean[] ignores) throws IOException{
        if(csv == null){
            return;
        }
        for(int i = 0; i < csv.size(); i++){
            if(ignores != null && i < ignores.length && ignores[i]){
                continue;
            }
            writer.writeElement(csv.get(i));
        }
        writer.newLine();
    }
}