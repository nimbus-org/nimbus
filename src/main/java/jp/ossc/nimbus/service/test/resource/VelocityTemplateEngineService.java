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
package jp.ossc.nimbus.service.test.resource;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.Utility;
import jp.ossc.nimbus.io.CSVReader;
import jp.ossc.nimbus.service.test.TemplateEngine;

/**
 * Apache Velocityを使った{@link TemplateEngine}サービス。<p>
 *
 * @author M.Takata
 */
public class VelocityTemplateEngineService extends ServiceBase implements TemplateEngine, VelocityTemplateEngineServiceMBean{
    
    private static final long serialVersionUID = 710504283828129889L;
    
    private File templateResourceDirectory;
    private Properties properties;
    
    private VelocityEngine engine;
    private CSVReader csvReader;
    
    public void setTemplateResourceDirectory(File dir){
        templateResourceDirectory = dir;
    }
    public File getTemplateResourceDirectory(){
        return templateResourceDirectory;
    }
    
    public void setProperties(Properties props){
        properties = props;
    }
    public Properties getProperties(){
        return properties;
    }
    
    public void setCSVReader(CSVReader reader){
        csvReader = reader;
    }
    
    public void startService() throws Exception{
        engine = new VelocityEngine();
        Properties props = properties == null ? new Properties() : properties;
        if(templateResourceDirectory != null){
            // 1系用設定
            props.setProperty("file.resource.loader.path", templateResourceDirectory.getCanonicalPath());
            // 2系用設定
            props.setProperty("resource.loader.file.path", templateResourceDirectory.getCanonicalPath());
        }
        // 1系用設定
        props.setProperty("file.resource.loader.cache", Boolean.FALSE.toString());
        // 2系用設定
        props.setProperty("resource.loader.file.cache", Boolean.FALSE.toString());
        engine.init(props);
    }
    
    public void stopService() throws Exception{
        engine = null;
    }
    
    /**
     * テンプレートファイルとデータファイルを読み込んで、変換を行い出力ファイルに書き出す。<p>
     * テンプレートファイルは、Apache VelocityのVTL(Velocity Template Language)で記述する。<br>
     * データファイルは、VTLで参照するオブジェクトを、2種類の記述方法を混在させて、複数記述できる。<br>
     * １つは、プロパティ形式で、"変数名=値"で指定する。複数指定する場合は、改行して記述する。<br>
     * もう１つは、CSV形式で、1行目に変数名、2行目にプロパティ名、３行目以降に値を記述する。プロパティ名は、CSV形式で１行のみ記述する。値は、CSV形式で複数行記述できる。値の行の終端を示すには、空行を挿入する。この変数を参照すると、指定した値の行数分のListで、その要素には、プロパティ名と値が格納されたMapが格納されている。<br>
     * 
     *
     * @param tmplateFile テンプレートファイル
     * @param dataFile データファイル
     * @param outputFile 出力ファイル
     * @param encoding 文字エンコーディング。テンプレートファイル、データファイルは、同じ文字エンコーディングである必要があり、出力ファイルも、この文字エンコーディングとなる。
     * @exception Exception 変換に失敗した場合
     */
    public void transform(File tmplateFile, File dataFile, File outputFile, String encoding) throws Exception{
        if(dataFile == null){
            final FileInputStream fis  = new FileInputStream(templateResourceDirectory == null ? tmplateFile : new File(templateResourceDirectory, tmplateFile.getPath()));
            final FileOutputStream fos = new FileOutputStream(outputFile);
            try{
                byte[] buf = new byte[1024];
                int len = 0;
                while((len = fis.read(buf)) != -1){
                    fos.write(buf, 0, len);
                }
            }finally{
                fis.close();
                fos.close();
            }
        }else{
            Template template = encoding == null ? engine.getTemplate(tmplateFile.getPath()) : engine.getTemplate(tmplateFile.getPath(), encoding);
            Context context = new VelocityContext();
            CSVReader reader = csvReader == null ? new CSVReader() : csvReader.cloneReader();
            reader.setReader(encoding == null ? new FileReader(dataFile) : new InputStreamReader(new FileInputStream(dataFile), encoding));
            try{
                String line = null;
                while((line = reader.readLine()) != null){
                    if(line.length() == 0){
                        continue;
                    }
                    final int index = line.indexOf("=");
                    if(index == -1){
                        String name = line;
                        String[] propNames = reader.readCSVLine();
                        List propValues = null;
                        List records = new ArrayList();
                        while((propValues = reader.readCSVLineList(propValues)) != null && propValues.size() != 0){
                            Map record = new HashMap();
                            for(int i = 0; i < propNames.length; i++){
                                String value = replaceProperty((String)propValues.get(i));
                                record.put(propNames[i], value);
                            }
                            records.add(record);
                        }
                        context.put(name, records);
                    }else{
                        String value = replaceProperty(line.substring(index + 1));
                        context.put(line.substring(0, index), value);
                    }
                }
            }finally{
                reader.close();
                reader = null;
            }
            
            Writer writer = encoding == null ? new FileWriter(outputFile) : new OutputStreamWriter(new FileOutputStream(outputFile), encoding);
            try{
                template.merge(context, writer);
            }finally{
                writer.close();
                writer = null;
            }
        }
    }
    
    private String replaceProperty(String textValue) {
        textValue = Utility.replaceSystemProperty(textValue);
        if (getServiceLoader() != null) {
            textValue = Utility.replaceServiceLoderConfig(textValue, getServiceLoader().getConfig());
        }
        if (getServiceManager() != null) {
            textValue = Utility.replaceManagerProperty(getServiceManager(), textValue);
        }
        textValue = Utility.replaceServerProperty(textValue);
        
        return textValue;
    }
}