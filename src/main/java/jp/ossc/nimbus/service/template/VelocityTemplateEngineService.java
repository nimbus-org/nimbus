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
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collections;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import jp.ossc.nimbus.core.*;

/**
 * Apache Velocityを使った{@link TemplateEngine}サービス。<p>
 *
 * @author M.Takata
 */
public class VelocityTemplateEngineService extends ServiceBase implements TemplateEngine, VelocityTemplateEngineServiceMBean{
    
    private static final long serialVersionUID = 857696261805500113L;
    
    private File templateFileRootDirectory;
    private Properties properties;
    private Map templateMap;
    private String characterEncoding;
    
    private VelocityEngine engine;
    private String stringRespositoryName;
    
    public void setTemplateFileRootDirectory(File dir){
        templateFileRootDirectory = dir;
    }
    public File getTemplateFileRootDirectory(){
        return templateFileRootDirectory;
    }
    
    public void setProperties(Properties props){
        properties = props;
    }
    public Properties getProperties(){
        return properties;
    }
    
    public void setCharacterEncoding(String encoding){
        characterEncoding = encoding;
    }
    public String getCharacterEncoding(){
        return characterEncoding;
    }
    
    public void setTemplate(String name, String template){
        setTemplate(name, template, null);
    }
    
    public void setTemplate(String name, String template, String encoding){
        if(encoding == null){
            encoding = characterEncoding;
        }
        TemplateResource resource = new TemplateResource();
        resource.template = template;
        resource.encoding = encoding;
        templateMap.put(name, resource);
        if(engine != null){
            StringResourceRepository repository = (StringResourceRepository)engine.getApplicationAttribute(stringRespositoryName);
            if(encoding == null){
                repository.putStringResource(name, template);
            }else{
                repository.putStringResource(name, template, encoding);
            }
        }
    }
    
    public void setTemplateFile(String name, File templateFile){
        setTemplateFile(name, templateFile, null);
    }
    
    public void setTemplateFile(String name, File templateFile, String encoding){
        TemplateResource resource = new TemplateResource();
        resource.templateFile = templateFile;
        resource.encoding = encoding == null ? characterEncoding : encoding;
        templateMap.put(name, resource);
    }
    
    public void createService() throws Exception{
        templateMap = Collections.synchronizedMap(new HashMap());
    }
    
    public void startService() throws Exception{
        engine = new VelocityEngine();
        Properties props = properties == null ? new Properties() : properties;
        props.setProperty(RuntimeConstants.RESOURCE_LOADER, "string, file");
        if(templateFileRootDirectory != null){
            File resourceDir = templateFileRootDirectory;
            if(!resourceDir.exists()){
                if(getServiceNameObject() != null){
                    ServiceMetaData metaData = ServiceManagerFactory.getServiceMetaData(getServiceNameObject());
                    if(metaData != null){
                        jp.ossc.nimbus.core.ServiceLoader loader = metaData.getServiceLoader();
                        if(loader != null){
                            String filePath = loader.getServiceURL().getFile();
                            if(filePath != null){
                                File serviceDefDir = new File(filePath).getParentFile();
                                File dir = new File(serviceDefDir, resourceDir.getPath());
                                if(dir.exists()){
                                    resourceDir = dir;
                                }
                            }
                        }
                    }
                }
            }
            props.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, resourceDir.getCanonicalPath());
        }
        props.setProperty("string." + RuntimeConstants.RESOURCE_LOADER + '.' + StringResourceLoader.REPOSITORY_STATIC, "false");
        stringRespositoryName = props.getProperty(StringResourceLoader.REPOSITORY_NAME);
        if(stringRespositoryName == null){
            stringRespositoryName = StringResourceLoader.REPOSITORY_NAME_DEFAULT;
        }
        engine.init(props);
        if(templateMap.size() != 0){
            StringResourceRepository repository = (StringResourceRepository)engine.getApplicationAttribute(stringRespositoryName);
            Iterator entries = templateMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                String name = (String)entry.getKey();
                TemplateResource resource = (TemplateResource)entry.getValue();
                if(resource.template != null){
                    if(resource.encoding == null){
                        repository.putStringResource(name, resource.template);
                    }else{
                        repository.putStringResource(name, resource.template, resource.encoding);
                    }
                }
            }
        }
    }
    
    public void stopService() throws Exception{
        engine = null;
    }
    
    public void destroyService() throws Exception{
        templateMap = null;
    }
    
    public String transform(String name, Map dataMap) throws TemplateTransformException{
        TemplateResource resource = (TemplateResource)templateMap.get(name);
        String templateName = name;
        if(resource != null && resource.templateFile != null){
            templateName = resource.templateFile.getPath();
        }
        Template template = null;
        if(resource == null){
            template = characterEncoding == null ? engine.getTemplate(templateName) : engine.getTemplate(templateName, characterEncoding);
        }else{
            template = resource.encoding == null ? engine.getTemplate(templateName) : engine.getTemplate(templateName, resource.encoding);
        }
        if(template == null){
            throw new TemplateTransformException("Template not found. name=" + name);
        }
        StringWriter sw = new StringWriter();
        try{
            template.merge(new VelocityContext(dataMap), sw);
        }catch(Exception e){
            throw new TemplateTransformException("Transform failed. name=" + name, e);
        }
        return sw.toString();
    }
    
    public void transform(String name, Map dataMap, Writer writer) throws TemplateTransformException, IOException{
        try{
            writer.write(transform(name, dataMap));
        }catch(IOException e){
            throw e;
        }
    }
    
    private static class TemplateResource implements Serializable{
        private static final long serialVersionUID = 3495957954376521011L;
        public String template;
        public File templateFile;
        public String encoding;
    }
}