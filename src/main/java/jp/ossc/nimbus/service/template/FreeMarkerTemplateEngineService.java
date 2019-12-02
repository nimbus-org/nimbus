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
package jp.ossc.nimbus.service.template;

import java.io.File;
import java.io.Writer;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Reader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Locale;

import freemarker.template.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.*;

/**
 * <a href="https://freemarker.apache.org">FreeMarker</a>を使った{@link TemplateEngine}サービス。<p>
 *
 * @author M.Takata
 */
public class FreeMarkerTemplateEngineService extends ServiceBase implements TemplateEngine, FreeMarkerTemplateEngineServiceMBean{
    
    private Version version;
    private File templateFileRootDirectory;
    private String characterEncoding;
    private Locale locale;
    private Map configrationProps;
    
    private Configuration configuration;
    private Map templateMap;
    
    public void setVersion(Version ver){
        version = ver;
    }
    public Version getVersion(){
        return version;
    }
    
    public void setTemplateFileRootDirectory(File dir){
        templateFileRootDirectory = dir;
    }
    public File getTemplateFileRootDirectory(){
        return templateFileRootDirectory;
    }
    
    public void setCharacterEncoding(String encoding){
        characterEncoding = encoding;
    }
    public String getCharacterEncoding(){
        return characterEncoding;
    }
    
    public void setLocale(Locale lo){
        locale = lo;
    }
    public Locale getLocale(){
        return locale;
    }
    
    public void setConfigurationProperty(String name, Object value){
        configrationProps.put(name, value);
    }
    public Object getConfigurationProperty(String name){
        return configrationProps.get(name);
    }
    
    public void createService() throws Exception{
        templateMap = Collections.synchronizedMap(new HashMap());
        configrationProps = Collections.synchronizedMap(new HashMap());
    }
    
    public void startService() throws Exception{
        configuration = version == null ? new Configuration() : new Configuration(version);
        File templateFileDirectory = null;
        if(templateFileRootDirectory != null){
            templateFileDirectory = templateFileRootDirectory;
            if(!templateFileDirectory.exists()){
                if(getServiceNameObject() != null){
                    ServiceMetaData metaData = ServiceManagerFactory.getServiceMetaData(getServiceNameObject());
                    if(metaData != null){
                        jp.ossc.nimbus.core.ServiceLoader loader = metaData.getServiceLoader();
                        if(loader != null){
                            String filePath = loader.getServiceURL().getFile();
                            if(filePath != null){
                                File serviceDefDir = new File(filePath).getParentFile();
                                File dir = new File(serviceDefDir, templateFileDirectory.getPath());
                                if(dir.exists()){
                                    templateFileDirectory = dir;
                                }
                            }
                        }
                    }
                }
            }
        }
        if(templateFileDirectory == null){
            templateFileDirectory = new File(".");
        }
        configuration.setDirectoryForTemplateLoading(templateFileDirectory);
        if(characterEncoding != null){
            configuration.setDefaultEncoding(characterEncoding);
        }
        if(locale != null){
            configuration.setLocale(locale);
        }
        if(configrationProps.size() != 0){
            final PropertyAccess access = PropertyAccess.getInstance(true);
            final String[] names = (String[])configrationProps.keySet()
                .toArray(new String[configrationProps.size()]);
            for(int i = 0; i < names.length; i++){
                access.set(configuration, names[i], configrationProps.get(names[i]));
            }
        }
    }
    
    public void destroyService() throws Exception{
        templateMap = null;
    }
    
    public void setTemplate(String name, String template){
        setTemplate(name, template, null);
    }
    
    public void setTemplate(String name, String template, String encoding){
        try{
            templateMap.put(name, new Template(name, template, configuration));
        }catch(IOException e){
        }
    }
    
    public void setTemplateFile(String name, File templateFile){
        setTemplateFile(name, templateFile, null);
    }
    
    public void setTemplateFile(String name, File templateFile, String encoding){
        try{
            InputStream is = new FileInputStream(templateFile);
            if(encoding == null){
                encoding = characterEncoding;
            }
            Reader reader = encoding == null ? new InputStreamReader(is) : new InputStreamReader(is, encoding);
            templateMap.put(name, new Template(name, reader, configuration));
        }catch(IOException e){
        }
    }
    
    public String transform(String name, Map dataMap) throws TemplateTransformException{
        StringWriter sw = new StringWriter();
        try{
            transform(name, dataMap, sw);
        }catch(IOException e){
        }finally{
            sw.flush();
        }
        return sw.toString();
    }
    
    public void transform(String name, Map dataMap, Writer writer) throws TemplateTransformException, IOException{
        try{
            Template template = (Template)templateMap.get(name);
            if(template == null){
                template = configuration.getTemplate(name, locale, characterEncoding);
            }
            template.process(dataMap, writer);
        }catch(IOException e){
            throw e;
        }catch(Exception e){
            throw new TemplateTransformException(e);
        }
    }
}