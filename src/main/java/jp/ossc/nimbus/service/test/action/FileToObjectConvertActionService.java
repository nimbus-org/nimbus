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
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Map;
import java.util.HashMap;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.test.TestContext;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.ChainTestAction;
import jp.ossc.nimbus.service.interpreter.Interpreter;
import jp.ossc.nimbus.util.converter.StreamConverter;
import jp.ossc.nimbus.util.converter.BindingStreamConverter;
import jp.ossc.nimbus.util.converter.StreamStringConverter;
import jp.ossc.nimbus.util.converter.FormatConverter;

/**
 * ファイルからオブジェクトへ変換するテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class FileToObjectConvertActionService extends ServiceBase implements TestAction, TestActionEstimation, ChainTestAction.TestActionProcess, FileToObjectConvertActionServiceMBean{
    
    private static final long serialVersionUID = 6119833930392428119L;
    protected double expectedCost = Double.NaN;
    
    protected String fileEncoding;
    protected ServiceName interpreterServiceName;
    protected Interpreter interpreter;
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
    
    public void setInterpreterServiceName(ServiceName name){
        interpreterServiceName = name;
    }
    public ServiceName getInterpreterServiceName(){
        return interpreterServiceName;
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
    
    public void setInterpreter(Interpreter interpreter){
        this.interpreter = interpreter;
    }
    
    public void setStreamConverter(StreamConverter converter){
        streamConverter = converter;
    }
    
    public void setFormatConverter(FormatConverter converter){
        formatConverter = converter;
    }
    
    public void startService() throws Exception{
        if(interpreterServiceName != null){
            interpreter = (Interpreter)ServiceManagerFactory.getServiceObject(interpreterServiceName);
        }
        if(streamConverterServiceName != null){
            streamConverter = (StreamConverter)ServiceManagerFactory.getServiceObject(streamConverterServiceName);
        }
        if(formatConverterServiceName != null){
            formatConverter = (FormatConverter)ServiceManagerFactory.getServiceObject(formatConverterServiceName);
        }
        if(streamConverter == null && formatConverter == null){
            throw new IllegalArgumentException("Converter is null.");
        }
    }
    
    /**
     * リソースの内容を読み込んで、ファイルをオブジェクトに変換する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * filePath
     * bindingObjectScript|format
     * </pre>
     * filePathは、ファイルのパスを指定する。パスは、絶対パスまたは、相対パスで指定する。<br>
     * bindingObjectScriptは、設定されている{@link StreamConverter}が、{@link BindingStreamConverter}の場合に、変換後オブジェクトとしてバインドするオブジェクトを生成するスクリプトを指定する。スクリプトは、{@link Interpreter#evaluate(String,Map)}で評価され、引数の変数マップには、"context"で{@link TestContext}が渡される。<br>
     * formatは、設定されている{@link FormatConverter}の{@link FormatConverter#setFormat(String) setFormat(format)}に渡すフォーマット文字列を指定する。指定しない場合は、フォーマットは設定しない。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return 変換後オブジェクト
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        return execute(context, actionId, null, resource);
    }
    
    /**
     * リソースの内容を読み込んで、ファイルをオブジェクトに変換する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * filePath
     * bindingObjectScript|format
     * </pre>
     * filePathは、ファイルのパスを指定する。パスは、絶対パスまたは、相対パスで指定する。<br>
     * bindingObjectScriptは、設定されている{@link StreamConverter}が、{@link BindingStreamConverter}の場合に、変換後オブジェクトとしてバインドするオブジェクトを生成するスクリプトを指定する。スクリプトは、{@link Interpreter#evaluate(String,Map)}で評価され、引数の変数マップには、"context"で{@link TestContext}、"preResult"で引数のpreResultが渡される。<br>
     * formatは、設定されている{@link FormatConverter}の{@link FormatConverter#setFormat(String) setFormat(format)}に渡すフォーマット文字列を指定する。指定しない場合は、フォーマットは設定しない。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param preResult 変換後オブジェクトとしてバインドするオブジェクト
     * @param resource リソース
     * @return 変換後オブジェクト
     */
    public Object execute(TestContext context, String actionId, Object preResult, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        File file = null;
        String bindingObjectScript = null;
        String format = null;
        try{
            final String filePath = br.readLine();
            if(filePath == null ){
                throw new Exception("Unexpected EOF on filePath");
            }
            file = new File(filePath);
            if(!file.exists()){
                file = new File(context.getCurrentDirectory(), filePath);
            }
            if(!file.exists()){
                throw new Exception("File not found. filePath=" + filePath);
            }
            String line = null;
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            try{
                while((line = br.readLine()) != null){
                    pw.println(line);
                }
                pw.flush();
                if(streamConverter != null){
                    bindingObjectScript = sw.toString();
                    if(bindingObjectScript.length() == 0){
                        bindingObjectScript = null;
                    }
                }else{
                    format = sw.toString();
                    if(format.length() == 0){
                        format = null;
                    }
                }
            }finally{
                sw.close();
                pw.close();
            }
            if(bindingObjectScript != null){
                if(streamConverter == null){
                    throw new UnsupportedOperationException("StreamConverter is null.");
                }
                if(interpreter == null){
                    throw new UnsupportedOperationException("Interpreter is null.");
                }
                if(!(streamConverter instanceof BindingStreamConverter)){
                    throw new Exception("StreamConverter is not BindingStreamConverter. streamConverter=" + streamConverter.getClass());
                }
            }
        }finally{
            br.close();
            br = null;
        }
        
        if(streamConverter != null){
            Object bindingObject = null;
            if(bindingObjectScript != null){
                final Map params = new HashMap();
                params.put("context", context);
                params.put("preResult", preResult);
                bindingObject = interpreter.evaluate(bindingObjectScript, params);
            }else{
                bindingObject = preResult;
            }
            FileInputStream fis = new FileInputStream(file);
            try{
                StreamConverter sc = streamConverter;
                if(fileEncoding != null
                    && sc instanceof StreamStringConverter
                    && !fileEncoding.equals(((StreamStringConverter)sc).getCharacterEncodingToObject())
                ){
                    sc = ((StreamStringConverter)sc).cloneCharacterEncodingToObject(fileEncoding);
                }
                if(bindingObject != null && (sc instanceof BindingStreamConverter)){
                    return ((BindingStreamConverter)sc).convertToObject(fis, bindingObject);
                }else{
                    return sc.convertToObject(fis);
                }
            }finally{
                fis.close();
            }
        }else{
            InputStreamReader isr = fileEncoding == null ? new InputStreamReader(new FileInputStream(file)) : new InputStreamReader(new FileInputStream(file), fileEncoding);
            StringWriter sw = new StringWriter();
            String str = null;
            try{
                int len = 0;
                char[] buf = new char[1024];
                while((len = isr.read(buf, 0, buf.length)) > 0){
                    sw.write(buf, 0, len);
                }
                str = sw.toString();
            }finally{
                isr.close();
                sw.close();
            }
            FormatConverter fc = formatConverter;
            if(formatConverterServiceName != null){
                fc = (FormatConverter)ServiceManagerFactory.getServiceObject(formatConverterServiceName);
            }
            if(format != null){
                fc.setFormat(format);
            }
            return fc.convert(str);
        }
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
}
