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

import java.io.File;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.ChainTestAction;
import jp.ossc.nimbus.service.test.TestContext;
import jp.ossc.nimbus.util.converter.StreamConverter;
import jp.ossc.nimbus.util.converter.StreamStringConverter;
import jp.ossc.nimbus.util.converter.FormatConverter;

/**
 * オブジェクトからファイルへ変換するテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class ObjectToFileConvertActionService extends ServiceBase implements TestAction, ChainTestAction.TestActionProcess, TestActionEstimation, ObjectToFileConvertActionServiceMBean{
    
    private static final long serialVersionUID = 3220754313008589978L;
    protected double expectedCost = 0d;
    
    protected String fileEncoding;
    protected ServiceName streamConverterServiceName;
    protected StreamConverter streamConverter;
    protected ServiceName formatConverterServiceName;
    protected FormatConverter formatConverter;
    
    public void setFileEncoding(String encoding){
        fileEncoding = encoding;
    }
    public String getFileEncoding(){
        return fileEncoding;
    }
    
    public void setStreamConverterServiceName(ServiceName name){
        streamConverterServiceName = name;
    }
    public ServiceName getStreamConverterServiceName(){
        return streamConverterServiceName;
    }
    
    public void setFormatConverterServiceName(ServiceName name){
        formatConverterServiceName = name;
    }
    public ServiceName getFormatConverterServiceName(){
        return formatConverterServiceName;
    }
    
    public void setStreamConverter(StreamConverter converter){
        streamConverter = converter;
    }
    
    public void setFormatConverter(FormatConverter converter){
        formatConverter = converter;
    }
    
    public void startService() throws Exception{
        if(streamConverterServiceName != null){
            streamConverter = (StreamConverter)ServiceManagerFactory.getServiceObject(streamConverterServiceName);
        }
        if(formatConverterServiceName != null){
            formatConverter = (FormatConverter)ServiceManagerFactory.getServiceObject(formatConverterServiceName);
        }
    }
    
    /**
     * リソースの内容を読み込んで、オブジェクトをファイルに変換する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * targetObjectId
     * outputFilePath
     * format
     * </pre>
     * targetObjectIdは、変換対象となるオブジェクトを指定するもので、同一テストケース中に、このTestActionより前に、変換対象となるオブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、変換対象となるオブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。<br>
     * outputFilePathは、ファイルのパスを指定する。パスは、絶対パスまたは、相対パスで指定する。空行を指定した場合は、アクションIDに拡張子".obj"を付与したファイル名となる。<br>
     * formatは、設定されている{@link FormatConverter}の{@link FormatConverter#setFormat(String) setFormat(format)}に渡すフォーマット文字列を指定する。指定しない場合は、フォーマットは設定しない。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return 変換後ファイル
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        return execute(context, actionId, null, resource);
    }
    
    
    /**
     * リソースの内容を読み込んで、オブジェクトをファイルに変換する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * targetObjectId
     * outputFilePath
     * format
     * </pre>
     * targetObjectIdは、変換対象となるオブジェクトを指定するもので、同一テストケース中に、このTestActionより前に、変換対象となるオブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、変換対象となるオブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。preResultを使用する場合は、空行を指定する。<br>
     * outputFilePathは、ファイルのパスを指定する。パスは、絶対パスまたは、相対パスで指定する。空行を指定した場合は、アクションIDに拡張子".obj"を付与したファイル名となる。<br>
     * formatは、設定されている{@link FormatConverter}の{@link FormatConverter#setFormat(String) setFormat(format)}に渡すフォーマット文字列を指定する。指定しない場合は、フォーマットは設定しない。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param preResult 変換対象となるオブジェクト
     * @param resource リソース
     * @return 変換後ファイル
     */
    public Object execute(TestContext context, String actionId, Object preResult, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        File outputFile = null;
        Object targetObject = preResult;
        String format = null;
        try{
            final String targetObjectId = br.readLine();
            if(targetObjectId != null && targetObjectId.length() != 0){
                Object actionResult = null;
                if(targetObjectId.indexOf(",") == -1){
                    actionResult = context.getTestActionResult(targetObjectId);
                }else{
                    String[] ids = targetObjectId.split(",");
                    if(ids.length != 2){
                        throw new Exception("Illegal targetObjectId format. id=" + targetObjectId);
                    }
                    actionResult = context.getTestActionResult(ids[0], ids[1]);
                }
                if(actionResult == null){
                    throw new Exception("TestActionResult not found. id=" + targetObjectId);
                }
                targetObject = actionResult;
            }
            final String outputFilePath = br.readLine();
            if(outputFilePath == null || outputFilePath.length() == 0){
                outputFile = new File(context.getCurrentDirectory(), actionId + ".obj");
            }else{
                outputFile = new File(outputFilePath);
                if(!outputFile.isAbsolute()){
                    outputFile = new File(context.getCurrentDirectory(), outputFilePath);
                }
                if(outputFile.getParentFile() != null && !outputFile.getParentFile().exists()){
                    outputFile.getParentFile().mkdirs();
                }
            }
            format = br.readLine();
            if(format != null && format.length() == 0){
                format = null;
            }
        }finally{
            br.close();
            br = null;
        }
        if(streamConverter != null){
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = null;
            try{
                StreamConverter sc = streamConverter;
                if(fileEncoding != null
                    && sc instanceof StreamStringConverter
                    && !fileEncoding.equals(((StreamStringConverter)sc).getCharacterEncodingToStream())
                ){
                    sc = ((StreamStringConverter)sc).cloneCharacterEncodingToStream(fileEncoding);
                }
                is = sc.convertToStream(targetObject);
                int len = 0;
                byte[] buf = new byte[1024];
                while((len = is.read(buf, 0, buf.length)) > 0){
                    fos.write(buf, 0, len);
                }
            }catch(Exception e){
                if(targetObject == null){
                    byte[] buf = "null".getBytes();
                    fos.write(buf, 0, buf.length);
                }else{
                    throw e;
                }
            }finally{
                if(is != null){
                    is.close();
                }
                fos.close();
            }
        }else if(formatConverter != null){
            FormatConverter fc = formatConverter;
            if(formatConverterServiceName != null){
                fc = (FormatConverter)ServiceManagerFactory.getServiceObject(formatConverterServiceName);
            }
            if(format != null){
                fc.setFormat(format);
            }
            OutputStreamWriter osw = fileEncoding == null ? new OutputStreamWriter(new FileOutputStream(outputFile)) : new OutputStreamWriter(new FileOutputStream(outputFile), fileEncoding);
            try{
                Object ret = fc.convert(targetObject);
                char[] chars = ret == null ? "null".toCharArray() : ret.toString().toCharArray();
                osw.write(chars, 0, chars.length);
            }catch(Exception e){
                if(targetObject == null){
                    char[] chars = "null".toCharArray();
                    osw.write(chars, 0, chars.length);
                }else{
                    throw e;
                }
            }finally{
                osw.close();
            }
        }else{
            OutputStreamWriter osw = fileEncoding == null ? new OutputStreamWriter(new FileOutputStream(outputFile)) : new OutputStreamWriter(new FileOutputStream(outputFile), fileEncoding);
            try{
                char[] chars = targetObject == null ? "null".toCharArray() : targetObject.toString().toCharArray();
                osw.write(chars, 0, chars.length);
            }catch(Exception e){
                if(targetObject == null){
                    char[] chars = "null".toCharArray();
                    osw.write(chars, 0, chars.length);
                }else{
                    throw e;
                }
            }finally{
                osw.close();
            }
        }
        return outputFile;
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
}