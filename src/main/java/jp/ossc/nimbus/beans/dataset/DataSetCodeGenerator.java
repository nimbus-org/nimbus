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
package jp.ossc.nimbus.beans.dataset;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import jp.ossc.nimbus.beans.StringArrayEditor;
import jp.ossc.nimbus.core.MetaData;
import jp.ossc.nimbus.core.NimbusEntityResolver;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.DeploymentException;

/**
 * データセットソースコード生成。<p>
 * データセット、レコードリスト、レコードなどの汎用Beanを継承したコンクリートなBeanのソースを生成する。<br>
 * 生成するソースコードの定義は、データセット定義XMLファイルで定義します。<br>
 *
 * @author M.Takata
 * @see <a href="dataset_1_0.dtd">データセット定義ファイルDTD</a>
 */
public class DataSetCodeGenerator{
    
    static{
        NimbusEntityResolver.registerDTD(
            "-//Nimbus//DTD Nimbus DataSet generation 1.0//JA",
            "jp/ossc/nimbus/beans/dataset/dataset_1_0.dtd"
        );
    }
    
    private static final String USAGE_RESOURCE
         = "jp/ossc/nimbus/beans/dataset/DataSetCodeGeneratorUsage.txt";
    
    private DataSetsMetaData dataSetsData;
    private boolean isValidate = true;
    private String encoding;
    
    private DataSetCodeGenerator(){
    }
    
    public void setValidate(boolean isValidate){
        this.isValidate = isValidate;
    }
    public boolean isValidate(){
        return isValidate;
    }
    
    public void setOutputFileEncoding(String encoding){
        this.encoding = encoding;
    }
    
    public File[] generate(File definition, File outDir){
        try{
            return generate(definition.toString(), new FileInputStream(definition), outDir);
        }catch(FileNotFoundException e){
            ServiceManagerFactory.getLogger().write("DSCG_00004", definition, e);
            return new File[0];
        }
    }
    
    public File[] generate(String definitionPath, InputStream definitionIn, File outDir){
        Set files = new LinkedHashSet();
        try{
            final InputSource inputSource = new InputSource(definitionIn);
            final DocumentBuilderFactory domFactory
                 = DocumentBuilderFactory.newInstance();
            domFactory.setValidating(isValidate);
            final DocumentBuilder builder = domFactory.newDocumentBuilder();
            final NimbusEntityResolver resolver = new NimbusEntityResolver();
            builder.setEntityResolver(resolver);
            final MyErrorHandler handler = new MyErrorHandler(definitionPath);
            builder.setErrorHandler(handler);
            final Document doc = builder.parse(inputSource);
            if(handler.isError()){
                ServiceManagerFactory.getLogger().write("DSCG_00004", definitionPath);
                return new File[0];
            }
            dataSetsData = new DataSetsMetaData();
            dataSetsData.importXML(doc.getDocumentElement());
            dataSetsData.generate(outDir, files);
            return (File[])files.toArray(new File[files.size()]);
        }catch(Exception e){
            ServiceManagerFactory.getLogger().write("DSCG_00004", definitionPath, e);
            return (File[])files.toArray(new File[files.size()]);
        }
    }
    
    public void loadDefinition(File definition) throws DeploymentException{
        try{
            loadDefinition(definition.toString(), new FileInputStream(definition));
        }catch(FileNotFoundException e){
            throw new DeploymentException(
                ServiceManagerFactory.getMessageRecordFactory().findEmbedMessage("DSCG_00004", new Object[]{definition}),
                e
            );
        }
    }
    
    public void loadDefinition(String definitionPath, InputStream definitionIn) throws DeploymentException{
        try{
            final InputSource inputSource = new InputSource(definitionIn);
            final DocumentBuilderFactory domFactory
                 = DocumentBuilderFactory.newInstance();
            domFactory.setValidating(isValidate);
            final DocumentBuilder builder = domFactory.newDocumentBuilder();
            final NimbusEntityResolver resolver = new NimbusEntityResolver();
            builder.setEntityResolver(resolver);
            final MyErrorHandler handler = new MyErrorHandler(definitionPath);
            builder.setErrorHandler(handler);
            final Document doc = builder.parse(inputSource);
            if(handler.isError()){
                throw new DeploymentException(
                    ServiceManagerFactory.getMessageRecordFactory().findEmbedMessage("DSCG_00004", new Object[]{definitionPath})
                );
            }
            dataSetsData = new DataSetsMetaData();
            dataSetsData.importXML(doc.getDocumentElement());
        }catch(DeploymentException e){
            throw e;
        }catch(Exception e){
            throw new DeploymentException(
                ServiceManagerFactory.getMessageRecordFactory().findEmbedMessage("DSCG_00004", new Object[]{definitionPath}),
                e
            );
        }
    }
    
    public DataSetsMetaData getDataSetsMetaData(){
        return dataSetsData;
    }
    
    private class MyErrorHandler implements ErrorHandler{
        
        private boolean isError;
        private String path;
        public MyErrorHandler(String path){
            this.path = path;
        }
        
        public void warning(SAXParseException e) throws SAXException{
            ServiceManagerFactory.getLogger().write("DSCG_00001", new Object[]{e.getMessage(), path, Integer.toString(e.getLineNumber()), Integer.toString(e.getColumnNumber())});
        }
        public void error(SAXParseException e) throws SAXException{
            isError = true;
            ServiceManagerFactory.getLogger().write("DSCG_00002", new Object[]{e.getMessage(), path, Integer.toString(e.getLineNumber()), Integer.toString(e.getColumnNumber())});
        }
        public void fatalError(SAXParseException e) throws SAXException{
            isError = true;
            ServiceManagerFactory.getLogger().write("DSCG_00003", new Object[]{e.getMessage(), path, Integer.toString(e.getLineNumber()), Integer.toString(e.getColumnNumber())});
        }
        public boolean isError(){
            return isError;
        }
    }
    
    /**
     * データセット集合定義データ。<p>
     *
     * @author M.Takata
     */
    public class DataSetsMetaData extends MetaData{
        
        private static final long serialVersionUID = 3876649968668319651L;
        
        public static final String TAG_NAME = "dataSets";
        
        protected Map properties;
        protected List records;
        protected List headers;
        protected List recordLists;
        protected List dataSets;
        
        public DataSetsMetaData(){
            super();
        }
        
        public Set getPropertyNameSet(){
            return properties == null ? new HashSet(0) : properties.keySet();
        }
        
        public PropertyMetaData getPropertyMetaData(String name){
            return properties == null ? null : (PropertyMetaData)properties.get(name);
        }
        
        public List getRecordMetaDataList(){
            return records;
        }
        
        public List getHeaderMetaDataList(){
            return headers;
        }
        
        public List getRecordListMetaDataList(){
            return recordLists;
        }
        
        public List getDataSetMetaDataList(){
            return dataSets;
        }
        
        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            
            if(!element.getTagName().equals(TAG_NAME)){
                throw new DeploymentException(
                    "Root tag must be " + TAG_NAME + " : " + element.getTagName()
                );
            }
            
            final Iterator propertyElements = getChildrenByTagName(
                element,
                PropertyMetaData.TAG_NAME
            );
            while(propertyElements.hasNext()){
                if(properties == null){
                    properties = new LinkedHashMap();
                }
                final PropertyMetaData propertyData = new PropertyMetaData(DataSetsMetaData.this);
                propertyData.importXML((Element)propertyElements.next());
                if(properties.containsKey(propertyData.getName())){
                    throw new DeploymentException(
                        "Property is duplicated : " + propertyData.getName()
                    );
                }
                properties.put(
                    propertyData.getName(),
                    propertyData
                );
            }
            
            final Iterator recordElements = getChildrenByTagName(
                element,
                RecordMetaData.TAG_NAME
            );
            while(recordElements.hasNext()){
                if(records == null){
                    records = new ArrayList();
                }
                final RecordMetaData recordData = new RecordMetaData(DataSetsMetaData.this);
                recordData.importXML((Element)recordElements.next());
                records.add(recordData);
            }
            
            final Iterator headerElements = getChildrenByTagName(
                element,
                HeaderMetaData.TAG_NAME
            );
            while(headerElements.hasNext()){
                if(headers == null){
                    headers = new ArrayList();
                }
                final HeaderMetaData headerData = new HeaderMetaData(DataSetsMetaData.this);
                headerData.importXML((Element)headerElements.next());
                headers.add(headerData);
            }
            
            final Iterator recordListElements = getChildrenByTagName(
                element,
                RecordListMetaData.TAG_NAME
            );
            while(recordListElements.hasNext()){
                if(recordLists == null){
                    recordLists = new ArrayList();
                }
                final RecordListMetaData recordListData = new RecordListMetaData(DataSetsMetaData.this);
                recordListData.importXML((Element)recordListElements.next());
                recordLists.add(recordListData);
            }
            
            final Iterator dataSetElements = getChildrenByTagName(
                element,
                DataSetMetaData.TAG_NAME
            );
            while(dataSetElements.hasNext()){
                if(dataSets == null){
                    dataSets = new ArrayList();
                }
                final DataSetMetaData dataSetData = new DataSetMetaData(DataSetsMetaData.this);
                dataSetData.importXML((Element)dataSetElements.next());
                dataSets.add(dataSetData);
            }
        }
        
        private void writeCode(File file, CodeGenerator generator, Set files) throws Exception{
            if(file.getParentFile() != null && !file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            if(files.contains(file)){
                ServiceManagerFactory.getLogger().write("DSCG_00005", file);
            }
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            generator.writeCode(pw);
            pw.flush();
            String source = sw.toString();
            FileOutputStream fos = new FileOutputStream(file);
            try{
                OutputStreamWriter osw = encoding == null ? new OutputStreamWriter(fos) : new OutputStreamWriter(fos, encoding);
                osw.write(source, 0, source.length());
                osw.flush();
            }finally{
                fos.close();
            }
            files.add(file);
        }
        
        public void generate(File outDir, Set files) throws Exception{
            if(records != null){
                for(int i = 0; i < records.size(); i++){
                    RecordMetaData data = (RecordMetaData)records.get(i);
                    File file = data.getFile(outDir);
                    writeCode(file, data, files);
                }
            }
            if(headers != null){
                for(int i = 0; i < headers.size(); i++){
                    HeaderMetaData data = (HeaderMetaData)headers.get(i);
                    File file = data.getFile(outDir);
                    writeCode(file, data, files);
                }
            }
            if(recordLists != null){
                for(int i = 0; i < recordLists.size(); i++){
                    RecordListMetaData data = (RecordListMetaData)recordLists.get(i);
                    File file = data.getFile(outDir);
                    writeCode(file, data, files);
                }
            }
            if(dataSets != null){
                for(int i = 0; i < dataSets.size(); i++){
                    DataSetMetaData data = (DataSetMetaData)dataSets.get(i);
                    File file = data.getFile(outDir);
                    writeCode(file, data, files);
                    if(data.headers != null){
                        Iterator itr = data.headers.values().iterator();
                        while(itr.hasNext()){
                            HeaderMetaData headerData = (HeaderMetaData)itr.next();
                            if(headerData.schema != null){
                                file = headerData.getFile(outDir);
                                writeCode(file, headerData, files);
                            }
                        }
                    }
                    if(data.recordLists != null){
                        Iterator itr = data.recordLists.values().iterator();
                        while(itr.hasNext()){
                            RecordListMetaData recordListData = (RecordListMetaData)itr.next();
                            if(recordListData.recordClassName != null){
                                file = recordListData.getFile(outDir);
                                writeCode(file, recordListData, files);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * プロパティ定義データ。<p>
     *
     * @author M.Takata
     */
    public class PropertyMetaData extends jp.ossc.nimbus.core.PropertyMetaData{
        
        private static final long serialVersionUID = 7835225760732994370L;
        
        public static final String TAG_NAME = "property";
        
        public PropertyMetaData(MetaData parent){
            super(parent);
        }
        protected String getTagName(){
            return TAG_NAME;
        }
    }
    
    /**
     * ソース生成。<p>
     *
     * @author M.Takata
     */
    public interface CodeGenerator{
        public File getFile(File dir);
        public void writeCode(PrintWriter pw);
    }
    
    /**
     * レコード定義データ。<p>
     *
     * @author M.Takata
     */
    public class RecordMetaData extends MetaData implements CodeGenerator{
        
        private static final long serialVersionUID = 5515496152289298757L;
        
        public static final String TAG_NAME = "record";
        public static final String TAG_NAME_SCHEMA = "schema";
        public static final String ATTRIBUTE_NAME_CODE = "code";
        public static final String ATTRIBUTE_NAME_EXTENDS = "extends";
        public static final String ATTRIBUTE_NAME_ABSTRACT = "abstract";
        public static final String ATTRIBUTE_NAME_TYPE = "type";
        
        public static final String SCHEMA_TYPE_SET = "set";
        public static final String SCHEMA_TYPE_REPLACE = "replace";
        public static final String SCHEMA_TYPE_APPEND = "append";
        
        protected String packageName;
        protected String className;
        protected String superClass = Record.class.getName();
        protected boolean isAbstract;
        protected String schema;
        protected String schemaType;
        
        public RecordMetaData(MetaData parent){
            super(parent);
        }
        
        public String getPackageName(){
            return packageName;
        }
        
        public String getClassName(){
            return className;
        }
        
        public String getFullClassName(){
            return packageName == null ? className : (packageName + '.' + className);
        }
        
        public String getSuperClassName(){
            return superClass;
        }
        
        public boolean isAbstract(){
            return isAbstract;
        }
        
        public String getSchema(){
            return schema;
        }
        
        public String getSchemaType(){
            return schemaType;
        }
        
        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            
            if(!element.getTagName().equals(TAG_NAME)){
                throw new DeploymentException(
                    "Root tag must be " + TAG_NAME + " : " + element.getTagName()
                );
            }
            String code = Utility.replaceProperty(dataSetsData, getUniqueAttribute(element, ATTRIBUTE_NAME_CODE));
            className = code;
            if(code.indexOf('.') != -1){
                className = code.substring(code.lastIndexOf('.') + 1);
                packageName = code.substring(0, code.lastIndexOf('.'));
            }
            superClass = Utility.replaceProperty(dataSetsData, getOptionalAttribute(element, ATTRIBUTE_NAME_EXTENDS, superClass));
            isAbstract = getOptionalBooleanAttribute(element, ATTRIBUTE_NAME_ABSTRACT);
            final Element schemaElement = getUniqueChild(element, TAG_NAME_SCHEMA);
            schema = Utility.replaceProperty(dataSetsData, getElementContent(schemaElement));
            schemaType = Utility.replaceProperty(dataSetsData, getOptionalAttribute(schemaElement, ATTRIBUTE_NAME_TYPE, "set"));
        }
        
        public File getFile(File dir){
            String filePath = null;
            if(packageName == null){
                filePath = className + ".java";
            }else{
                filePath = packageName.replaceAll("\\.", "/") + '/' + className + ".java";
            }
            return new File(dir, filePath);
        }
        
        public void writeCode(PrintWriter pw){
            if(packageName != null){
                pw.println("package " + packageName + ';');
            }
            pw.println();
            pw.print("public ");
            if(isAbstract){
                pw.print("abstract ");
            }
            pw.println("class " + className + " extends " + superClass + "{");
            RecordSchema recordSchema = RecordSchema.getInstance(schema);
            final boolean isAppend = RecordMetaData.SCHEMA_TYPE_APPEND.equals(schemaType);
            for(int i = 0; i < recordSchema.getPropertySize(); i++){
                PropertySchema propSchema = recordSchema.getPropertySchema(i);
                pw.println("    public static final String " + propSchema.getName().toUpperCase() + " = \"" + propSchema.getName() + "\";");
                if(isAppend){
                    pw.println("    public static final int " + propSchema.getName().toUpperCase() + "_INDEX = " + superClass + ".PROPERTY_INDEX_OFFSET + " + i + ";");
                }else{
                    pw.println("    public static final int " + propSchema.getName().toUpperCase() + "_INDEX = " + i + ";");
                }
            }
            if(isAppend){
                pw.println("    protected static final int PROPERTY_INDEX_OFFSET = " + superClass + ".PROPERTY_INDEX_OFFSET + " + recordSchema.getPropertySize() + ";");
            }else{
                pw.println("    protected static final int PROPERTY_INDEX_OFFSET = " + recordSchema.getPropertySize() + ";");
            }
            pw.println("    ");
            pw.println("    public " + className + "(){");
            if(RecordMetaData.SCHEMA_TYPE_SET.equals(schemaType)){
                pw.println("        super(\"" + Utility.escapeLineSeparator(schema) + "\");");
            }else if(RecordMetaData.SCHEMA_TYPE_REPLACE.equals(schemaType)){
                pw.println("        replaceSchema(\"" + Utility.escapeLineSeparator(schema) + "\");");
            }else if(RecordMetaData.SCHEMA_TYPE_APPEND.equals(schemaType)){
                pw.println("        appendSchema(\"" + Utility.escapeLineSeparator(schema) + "\");");
            }
            pw.println("    }");
            pw.println("    ");
            for(int i = 0, imax = recordSchema.getPropertySize(); i < imax; i++){
                PropertySchema propSchema = recordSchema.getPropertySchema(i);
                if(propSchema.getType().isPrimitive()){
                    pw.println("    public " + toPrimitiveClassName(propSchema.getType()) + " " + Utility.createGetterName(propSchema.getName(), propSchema.getType()) + "(){");
                    if(Boolean.TYPE.equals(propSchema.getType())){
                        pw.println("        return getBooleanProperty(" + propSchema.getName().toUpperCase() + "_INDEX);");
                    }else if(Byte.TYPE.equals(propSchema.getType())){
                        pw.println("        return getByteProperty(" + propSchema.getName().toUpperCase() + "_INDEX);");
                    }else if(Short.TYPE.equals(propSchema.getType())){
                        pw.println("        return getShortProperty(" + propSchema.getName().toUpperCase() + "_INDEX);");
                    }else if(Integer.TYPE.equals(propSchema.getType())){
                        pw.println("        return getIntProperty(" + propSchema.getName().toUpperCase() + "_INDEX);");
                    }else if(Long.TYPE.equals(propSchema.getType())){
                        pw.println("        return getLongProperty(" + propSchema.getName().toUpperCase() + "_INDEX);");
                    }else if(Float.TYPE.equals(propSchema.getType())){
                        pw.println("        return getFloatProperty(" + propSchema.getName().toUpperCase() + "_INDEX);");
                    }else if(Double.TYPE.equals(propSchema.getType())){
                        pw.println("        return getDoubleProperty(" + propSchema.getName().toUpperCase() + "_INDEX);");
                    }else if(Character.TYPE.equals(propSchema.getType())){
                        pw.println("        Character c = ((Character)getProperty(" + propSchema.getName().toUpperCase() + "_INDEX));");
                        pw.println("        return c == null ? (char)0 : c.charValue();");
                    }
                }else if(propSchema.getType().isArray()){
                    pw.println("    public " + toArrayClassName(propSchema.getType()) + " " + Utility.createGetterName(propSchema.getName(), propSchema.getType()) + "(){");
                    pw.println("        return (" + toArrayClassName(propSchema.getType()) + ")getProperty(" + propSchema.getName().toUpperCase() + "_INDEX);");
                }else{
                    pw.println("    public " + propSchema.getType().getName() + " " + Utility.createGetterName(propSchema.getName(), propSchema.getType()) + "(){");
                    pw.println("        return (" + propSchema.getType().getName() + ")getProperty(" + propSchema.getName().toUpperCase() + "_INDEX);");
                }
                pw.println("    }");
                pw.println("    ");
                if(propSchema.getType().isPrimitive()){
                    pw.println("    public void " + Utility.createSetterName(propSchema.getName()) + "(" + toPrimitiveClassName(propSchema.getType()) + " val){");
                }else if(propSchema.getType().isArray()){
                    pw.println("    public void " + Utility.createSetterName(propSchema.getName()) + "(" + toArrayClassName(propSchema.getType()) + " val){");
                }else{
                    pw.println("    public void " + Utility.createSetterName(propSchema.getName()) + "(" + propSchema.getType().getName() + " val){");
                }
                pw.println("        setProperty(" + propSchema.getName().toUpperCase() + "_INDEX, val);");
                pw.println("    }");
                pw.println("    ");
            }
            pw.println("    protected void writeSchema(java.io.ObjectOutput out) throws java.io.IOException{}");
            pw.println("    ");
            pw.println("    protected void readSchema(java.io.ObjectInput in) throws java.io.IOException, ClassNotFoundException{}");
            pw.println("}");
        }
    }
    
    private static String toArrayClassName(Class clazz){
        Class componentType = clazz.getComponentType();
        if(componentType == null){
            return clazz.getName();
        }else if(componentType.isPrimitive()){
            if(Boolean.TYPE.equals(componentType)){
                return "boolean[]";
            }else if(Byte.TYPE.equals(componentType)){
                return "byte[]";
            }else if(Short.TYPE.equals(componentType)){
                return "short[]";
            }else if(Integer.TYPE.equals(componentType)){
                return "int[]";
            }else if(Long.TYPE.equals(componentType)){
                return "long[]";
            }else if(Float.TYPE.equals(componentType)){
                return "float[]";
            }else if(Double.TYPE.equals(componentType)){
                return "double[]";
            }else if(Character.TYPE.equals(componentType)){
                return "char[]";
            }else{
                return componentType.getName() + "[]";
            }
        }else{
            return componentType.getName() + "[]";
        }
    }
    
    private static String toPrimitiveClassName(Class clazz){
        if(Boolean.TYPE.equals(clazz)){
            return "boolean";
        }else if(Byte.TYPE.equals(clazz)){
            return "byte";
        }else if(Short.TYPE.equals(clazz)){
            return "short";
        }else if(Integer.TYPE.equals(clazz)){
            return "int";
        }else if(Long.TYPE.equals(clazz)){
            return "long";
        }else if(Float.TYPE.equals(clazz)){
            return "float";
        }else if(Double.TYPE.equals(clazz)){
            return "double";
        }else if(Character.TYPE.equals(clazz)){
            return "char";
        }else{
            return clazz.getName();
        }
    }
    
    /**
     * ヘッダ定義データ。<p>
     *
     * @author M.Takata
     */
    public class HeaderMetaData extends MetaData implements CodeGenerator{
        
        private static final long serialVersionUID = 7980846939747068932L;
        
        public static final String TAG_NAME = "header";
        public static final String TAG_NAME_SCHEMA = "schema";
        public static final String ATTRIBUTE_NAME_CODE = "code";
        public static final String ATTRIBUTE_NAME_EXTENDS = "extends";
        public static final String ATTRIBUTE_NAME_ABSTRACT = "abstract";
        public static final String ATTRIBUTE_NAME_NAME = "name";
        public static final String ATTRIBUTE_NAME_TYPE = "type";
        
        protected String name;
        protected String packageName;
        protected String className;
        protected String superClass = Header.class.getName();
        protected boolean isAbstract;
        protected String schema;
        protected String schemaType;
        
        public HeaderMetaData(MetaData parent){
            super(parent);
        }
        
        public String getName(){
            return name;
        }
        
        public String getPackageName(){
            return packageName;
        }
        
        public String getClassName(){
            return className;
        }
        
        public String getFullClassName(){
            return packageName == null ? className : (packageName + '.' + className);
        }
        
        public String getSuperClassName(){
            return superClass;
        }
        
        public boolean isAbstract(){
            return isAbstract;
        }
        
        public String getSchema(){
            return schema;
        }
        
        public String getSchemaType(){
            return schemaType;
        }
        
        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            
            if(!element.getTagName().equals(TAG_NAME)){
                throw new DeploymentException(
                    "Root tag must be " + TAG_NAME + " : " + element.getTagName()
                );
            }
            String code = Utility.replaceProperty(dataSetsData, getUniqueAttribute(element, ATTRIBUTE_NAME_CODE));
            className = code;
            if(code.indexOf('.') != -1){
                className = code.substring(code.lastIndexOf('.') + 1);
                packageName = code.substring(0, code.lastIndexOf('.'));
            }
            superClass = Utility.replaceProperty(dataSetsData, getOptionalAttribute(element, ATTRIBUTE_NAME_EXTENDS, superClass));
            isAbstract = getOptionalBooleanAttribute(element, ATTRIBUTE_NAME_ABSTRACT);
            name = Utility.replaceProperty(dataSetsData, getOptionalAttribute(element, ATTRIBUTE_NAME_NAME));
            
            final Element schemaElement = (getParent() instanceof DataSetsMetaData&& Header.class.getName().equals(superClass)) ? getUniqueChild(element, TAG_NAME_SCHEMA) : getOptionalChild(element, TAG_NAME_SCHEMA);
            if(schemaElement != null){
                schema = Utility.replaceProperty(dataSetsData, getElementContent(schemaElement));
                schemaType = Utility.replaceProperty(dataSetsData, getOptionalAttribute(schemaElement, ATTRIBUTE_NAME_TYPE, "set"));
            }
        }
        
        public File getFile(File dir){
            String filePath = null;
            if(packageName == null){
                filePath = className + ".java";
            }else{
                filePath = packageName.replaceAll("\\.", "/") + '/' + className + ".java";
            }
            return new File(dir, filePath);
        }
        
        public void writeCode(PrintWriter pw){
            if(!(getParent() instanceof DataSetsMetaData) && schema == null){
                if(name == null){
                    pw.println("        setHeaderClass(null, " + getFullClassName() + ".class);");
                }else{
                    pw.println("        setHeaderClass(\"" + name + "\", " + getFullClassName() + ".class);");
                }
            }else{
                if(packageName != null){
                    pw.println("package " + packageName + ';');
                }
                pw.println();
                pw.print("public ");
                if(isAbstract){
                    pw.print("abstract ");
                }
                pw.println("class " + className + " extends " + superClass + "{");
                RecordSchema recordSchema = null;
                if(schema != null){
                    recordSchema = RecordSchema.getInstance(schema);
                    final boolean isAppend = RecordMetaData.SCHEMA_TYPE_APPEND.equals(schemaType);
                    for(int i = 0; i < recordSchema.getPropertySize(); i++){
                        PropertySchema propSchema = recordSchema.getPropertySchema(i);
                        pw.println("    public static final String " + propSchema.getName().toUpperCase() + " = \"" + propSchema.getName() + "\";");
                        if(isAppend){
                            pw.println("    public static final int " + propSchema.getName().toUpperCase() + "_INDEX = " + superClass + ".PROPERTY_INDEX_OFFSET + " + i + ";");
                        }else{
                            pw.println("    public static final int " + propSchema.getName().toUpperCase() + "_INDEX = " + i + ";");
                        }
                    }
                    pw.println("    ");
                    if(isAppend){
                        pw.println("    protected static final int PROPERTY_INDEX_OFFSET = " + superClass + ".PROPERTY_INDEX_OFFSET + " + recordSchema.getPropertySize() + ";");
                    }else{
                        pw.println("    protected static final int PROPERTY_INDEX_OFFSET = " + recordSchema.getPropertySize() + ";");
                    }
                }
                pw.println("    public " + className + "(){");
                if(RecordMetaData.SCHEMA_TYPE_SET.equals(schemaType)){
                    if(name == null){
                        pw.println("        super(null, \"" + Utility.escapeLineSeparator(schema) + "\");");
                    }else{
                        pw.println("        super(\"" + name + "\", \"" + Utility.escapeLineSeparator(schema) + "\");");
                    }
                }else if(RecordMetaData.SCHEMA_TYPE_REPLACE.equals(schemaType)){
                    if(name != null){
                        pw.println("        setName(\"" + name + "\");");
                    }
                    pw.println("        replaceSchema(\"" + Utility.escapeLineSeparator(schema) + "\");");
                }else if(RecordMetaData.SCHEMA_TYPE_APPEND.equals(schemaType)){
                    if(name != null){
                        pw.println("        setName(\"" + name + "\");");
                    }
                    pw.println("        appendSchema(\"" + Utility.escapeLineSeparator(schema) + "\");");
                }
                pw.println("    }");
                pw.println("    ");
                pw.println("    public " + className + "(String name){");
                if(RecordMetaData.SCHEMA_TYPE_SET.equals(schemaType)){
                    pw.println("        super(name, \"" + Utility.escapeLineSeparator(schema) + "\");");
                }else if(RecordMetaData.SCHEMA_TYPE_REPLACE.equals(schemaType)){
                    pw.println("        setName(name);");
                    pw.println("        replaceSchema(\"" + Utility.escapeLineSeparator(schema) + "\");");
                }else if(RecordMetaData.SCHEMA_TYPE_APPEND.equals(schemaType)){
                    pw.println("        setName(name);");
                    pw.println("        appendSchema(\"" + Utility.escapeLineSeparator(schema) + "\");");
                }
                pw.println("    }");
                if(recordSchema != null){
                    pw.println("    ");
                    for(int i = 0, imax = recordSchema.getPropertySize(); i < imax; i++){
                        PropertySchema propSchema = recordSchema.getPropertySchema(i);
                        if(propSchema.getType().isPrimitive()){
                            pw.println("    public " + toPrimitiveClassName(propSchema.getType()) + " " + Utility.createGetterName(propSchema.getName(), propSchema.getType()) + "(){");
                            if(Boolean.TYPE.equals(propSchema.getType())){
                                pw.println("        return getBooleanProperty(" + propSchema.getName().toUpperCase() + "_INDEX);");
                            }else if(Byte.TYPE.equals(propSchema.getType())){
                                pw.println("        return getByteProperty(" + propSchema.getName().toUpperCase() + "_INDEX);");
                            }else if(Short.TYPE.equals(propSchema.getType())){
                                pw.println("        return getShortProperty(" + propSchema.getName().toUpperCase() + "_INDEX);");
                            }else if(Integer.TYPE.equals(propSchema.getType())){
                                pw.println("        return getIntProperty(" + propSchema.getName().toUpperCase() + "_INDEX);");
                            }else if(Long.TYPE.equals(propSchema.getType())){
                                pw.println("        return getLongProperty(" + propSchema.getName().toUpperCase() + "_INDEX);");
                            }else if(Float.TYPE.equals(propSchema.getType())){
                                pw.println("        return getFloatProperty(" + propSchema.getName().toUpperCase() + "_INDEX);");
                            }else if(Double.TYPE.equals(propSchema.getType())){
                                pw.println("        return getDoubleProperty(" + propSchema.getName().toUpperCase() + "_INDEX);");
                            }else if(Character.TYPE.equals(propSchema.getType())){
                                pw.println("        Character c = ((Character)getProperty(" + propSchema.getName().toUpperCase() + "_INDEX));");
                                pw.println("        return c == null ? (char)0 : c.charValue();");
                            }
                        }else if(propSchema.getType().isArray()){
                            pw.println("    public " + toArrayClassName(propSchema.getType()) + " " + Utility.createGetterName(propSchema.getName(), propSchema.getType()) + "(){");
                            pw.println("        return (" + toArrayClassName(propSchema.getType()) + ")getProperty(" + propSchema.getName().toUpperCase() + "_INDEX);");
                        }else{
                            pw.println("    public " + propSchema.getType().getName() + " " + Utility.createGetterName(propSchema.getName(), propSchema.getType()) + "(){");
                            pw.println("        return (" + propSchema.getType().getName() + ")getProperty(" + propSchema.getName().toUpperCase() + "_INDEX);");
                        }
                        pw.println("    }");
                        pw.println("    ");
                        if(propSchema.getType().isPrimitive()){
                            pw.println("    public void " + Utility.createSetterName(propSchema.getName()) + "(" + toPrimitiveClassName(propSchema.getType()) + " val){");
                        }else if(propSchema.getType().isArray()){
                            pw.println("    public void " + Utility.createSetterName(propSchema.getName()) + "(" + toArrayClassName(propSchema.getType()) + " val){");
                        }else{
                            pw.println("    public void " + Utility.createSetterName(propSchema.getName()) + "(" + propSchema.getType().getName() + " val){");
                        }
                        pw.println("        setProperty(" + propSchema.getName().toUpperCase() + "_INDEX, val);");
                        pw.println("    }");
                        pw.println("    ");
                    }
                }
                pw.println("    protected void writeSchema(java.io.ObjectOutput out) throws java.io.IOException{}");
                pw.println("    ");
                pw.println("    protected void readSchema(java.io.ObjectInput in) throws java.io.IOException, ClassNotFoundException{}");
                pw.println("}");
            }
        }
    }
    
    /**
     * レコードリスト定義データ。<p>
     *
     * @author M.Takata
     */
    public class RecordListMetaData extends MetaData implements CodeGenerator{
        
        private static final long serialVersionUID = 8369293626987943298L;
        
        public static final String TAG_NAME = "recordList";
        public static final String TAG_NAME_INDEX = "index";
        public static final String ATTRIBUTE_NAME_CODE = "code";
        public static final String ATTRIBUTE_NAME_EXTENDS = "extends";
        public static final String ATTRIBUTE_NAME_ABSTRACT = "abstract";
        public static final String ATTRIBUTE_NAME_RECORD_CODE = "recordCode";
        public static final String ATTRIBUTE_NAME_NAME = "name";
        public static final String ATTRIBUTE_NAME_SYNCHRONIZED = "synchronized";
        
        protected String name;
        protected String packageName;
        protected String className;
        protected String superClass = RecordList.class.getName();
        protected boolean isAbstract;
        protected String recordPackageName;
        protected String recordClassName;
        protected Map indexMap;
        protected boolean isSynchronized = true;
        
        public RecordListMetaData(MetaData parent){
            super(parent);
        }
        
        public String getName(){
            return name;
        }
        
        public String getPackageName(){
            return packageName;
        }
        
        public String getClassName(){
            return className;
        }
        
        public String getFullClassName(){
            return packageName == null ? className : (packageName + '.' + className);
        }
        
        public String getSuperClassName(){
            return superClass;
        }
        
        public boolean isAbstract(){
            return isAbstract;
        }
        
        public String getRecordPackageName(){
            return recordPackageName;
        }
        
        public String getRecordClassName(){
            return recordClassName;
        }
        
        public String getFullRecordClassName(){
            return recordPackageName == null ? recordClassName : (recordPackageName + '.' + recordClassName);
        }
        
        public Set getIndexNameSet(){
            return indexMap == null ? new HashSet(0) : indexMap.keySet();
        }
        
        public String[] getIndex(String name){
            return indexMap == null ? null : (String[])indexMap.get(name);
        }
        
        public boolean isSynchronized(){
            return isSynchronized;
        }
        
        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            
            if(!element.getTagName().equals(TAG_NAME)){
                throw new DeploymentException(
                    "Root tag must be " + TAG_NAME + " : " + element.getTagName()
                );
            }
            String code = Utility.replaceProperty(dataSetsData, getUniqueAttribute(element, ATTRIBUTE_NAME_CODE));
            className = code;
            if(code.indexOf('.') != -1){
                className = code.substring(code.lastIndexOf('.') + 1);
                packageName = code.substring(0, code.lastIndexOf('.'));
            }
            String recordCode = Utility.replaceProperty(
                dataSetsData,
                (getParent() instanceof DataSetsMetaData) ? getUniqueAttribute(element, ATTRIBUTE_NAME_RECORD_CODE) : getOptionalAttribute(element, ATTRIBUTE_NAME_RECORD_CODE)
            );
            recordClassName = recordCode;
            if(recordCode != null && recordCode.indexOf('.') != -1){
                recordClassName = recordCode.substring(recordCode.lastIndexOf('.') + 1);
                recordPackageName = recordCode.substring(0, recordCode.lastIndexOf('.'));
            }
            superClass = Utility.replaceProperty(dataSetsData, getOptionalAttribute(element, ATTRIBUTE_NAME_EXTENDS, superClass));
            isAbstract = getOptionalBooleanAttribute(element, ATTRIBUTE_NAME_ABSTRACT);
            name = Utility.replaceProperty(dataSetsData, getOptionalAttribute(element, ATTRIBUTE_NAME_NAME));
            isSynchronized = getOptionalBooleanAttribute(element, ATTRIBUTE_NAME_SYNCHRONIZED);
            
            final Iterator indexElements = getChildrenByTagName(
                element,
                TAG_NAME_INDEX
            );
            while(indexElements.hasNext()){
                if(indexMap == null){
                    indexMap = new HashMap();
                }
                final Element indexElement = (Element)indexElements.next();
                if(indexElement != null){
                    String indexName = getUniqueAttribute(indexElement, ATTRIBUTE_NAME_NAME);
                    String indexStr = Utility.replaceProperty(dataSetsData, getElementContent(indexElement));
                    StringArrayEditor editor = new StringArrayEditor();
                    editor.setAsText(indexStr);
                    String[] indexProps = (String[])editor.getValue();
                    indexMap.put(indexName, indexProps);
                }
            }
        }
        
        public File getFile(File dir){
            String filePath = null;
            if(packageName == null){
                filePath = className + ".java";
            }else{
                filePath = packageName.replaceAll("\\.", "/") + '/' + className + ".java";
            }
            return new File(dir, filePath);
        }
        
        public void writeCode(PrintWriter pw){
            if(recordClassName == null){
                if(name == null){
                    pw.println("        setRecordListClass(null, " + getFullClassName() + ".class);");
                }else{
                    pw.println("        setRecordListClass(\"" + name + "\", " + getFullClassName() + ".class);");
                }
            }else{
                if(packageName != null){
                    pw.println("package " + packageName + ';');
                }
                pw.println();
                pw.print("public ");
                if(isAbstract){
                    pw.print("abstract ");
                }
                pw.println("class " + className + " extends " + superClass + "{");
                pw.println("    public " + className + "(){");
                if(name == null){
                    pw.println("        this(null);");
                }else{
                    pw.println("        this(\"" + name + "\");");
                }
                pw.println("    }");
                pw.println("    ");
                pw.println("    public " + className + "(String name){");
                pw.println("        super(name, " + getFullRecordClassName() + ".class" + ", " + isSynchronized + ");");
                if(indexMap != null){
                    Iterator entries = indexMap.entrySet().iterator();
                    while(entries.hasNext()){
                        Map.Entry entry = (Map.Entry)entries.next();
                        pw.print("        setIndex(\"" + entry.getKey() + "\", new String[]{");
                        String[] props = (String[])entry.getValue();
                        for(int i = 0, imax = props.length; i < imax; i++){
                            pw.print(props[i] == null ? "null" : ('"' + props[i] + '"'));
                            if(i != imax - 1){
                                pw.print(", ");
                            }
                        }
                        pw.println("});");
                    }
                }
                pw.println("    }");
                pw.println("    ");
                pw.println("    public " + getFullRecordClassName() + ' ' + Utility.createCreaterName(recordClassName) + "(){");
                pw.println("        return (" + getFullRecordClassName() + ")createRecord();");
                pw.println("    }");
                pw.println("    ");
                pw.println("    protected void writeSchema(java.io.ObjectOutput out) throws java.io.IOException{}");
                pw.println("    ");
                pw.println("    protected void readSchema(java.io.ObjectInput in) throws java.io.IOException, ClassNotFoundException{}");
                pw.println("}");
            }
        }
    }
    
    /**
     * ネストレコードリスト定義データ。<p>
     *
     * @author M.Takata
     */
    public class NestedRecordListMetaData extends MetaData implements CodeGenerator{
        
        private static final long serialVersionUID = -547306924656306470L;
        
        public static final String TAG_NAME = "nestedRecordList";
        public static final String ATTRIBUTE_NAME_CODE = "code";
        public static final String ATTRIBUTE_NAME_NAME = "name";
        
        protected String name;
        protected String code;
        
        public NestedRecordListMetaData(MetaData parent){
            super(parent);
        }
        
        public String getName(){
            return name;
        }
        
        public String getFullClassName(){
            return code;
        }
        
        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            
            if(!element.getTagName().equals(TAG_NAME)){
                throw new DeploymentException(
                    "Root tag must be " + TAG_NAME + " : " + element.getTagName()
                );
            }
            code = Utility.replaceProperty(dataSetsData, getUniqueAttribute(element, ATTRIBUTE_NAME_CODE));
            name = Utility.replaceProperty(dataSetsData, getUniqueAttribute(element, ATTRIBUTE_NAME_NAME));
        }
        
        public File getFile(File dir){
            return null;
        }
        
        public void writeCode(PrintWriter pw){
            if(name == null){
                pw.println("        setNestedRecordListClass(null, " + code + ".class);");
            }else{
                pw.println("        setNestedRecordListClass(\"" + name + "\", " + code + ".class);");
            }
        }
    }
    
    /**
     * ネストレコード定義データ。<p>
     *
     * @author M.Takata
     */
    public class NestedRecordMetaData extends MetaData implements CodeGenerator{
        
        private static final long serialVersionUID = 5542852046600369680L;
        
        public static final String TAG_NAME = "nestedRecord";
        public static final String ATTRIBUTE_NAME_CODE = "code";
        public static final String ATTRIBUTE_NAME_NAME = "name";
        
        protected String name;
        protected String code;
        
        public NestedRecordMetaData(MetaData parent){
            super(parent);
        }
        
        public String getName(){
            return name;
        }
        
        public String getFullClassName(){
            return code;
        }
        
        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            
            if(!element.getTagName().equals(TAG_NAME)){
                throw new DeploymentException(
                    "Root tag must be " + TAG_NAME + " : " + element.getTagName()
                );
            }
            code = Utility.replaceProperty(dataSetsData, getUniqueAttribute(element, ATTRIBUTE_NAME_CODE));
            name = Utility.replaceProperty(dataSetsData, getUniqueAttribute(element, ATTRIBUTE_NAME_NAME));
        }
        
        public File getFile(File dir){
            return null;
        }
        
        public void writeCode(PrintWriter pw){
            if(name == null){
                pw.println("        setNestedRecordClass(null, " + code + ".class);");
            }else{
                pw.println("        setNestedRecordClass(\"" + name + "\", " + code + ".class);");
            }
        }
    }
    
    /**
     * データセット定義データ。<p>
     *
     * @author M.Takata
     */
    public class DataSetMetaData extends MetaData implements CodeGenerator{
        
        private static final long serialVersionUID = 6664682114622861929L;
        
        public static final String TAG_NAME = "dataSet";
        public static final String ATTRIBUTE_NAME_CODE = "code";
        public static final String ATTRIBUTE_NAME_NAME = "name";
        public static final String ATTRIBUTE_NAME_EXTENDS = "extends";
        public static final String ATTRIBUTE_NAME_ABSTRACT = "abstract";
        public static final String ATTRIBUTE_NAME_SYNCHRONIZED = "synchronized";
        
        protected String packageName;
        protected String className;
        protected String name;
        protected String superClass = DataSet.class.getName();
        protected boolean isAbstract;
        protected Map headers;
        protected Map recordLists;
        protected Map nestedRecords;
        protected Map nestedRecordLists;
        protected boolean isSynchronized = true;
        
        public DataSetMetaData(MetaData parent){
            super(parent);
        }
        
        public String getPackageName(){
            return packageName;
        }
        
        public String getClassName(){
            return className;
        }
        
        public String getFullClassName(){
            return packageName == null ? className : (packageName + '.' + className);
        }
        
        public String getSuperClassName(){
            return superClass;
        }
        
        public boolean isAbstract(){
            return isAbstract;
        }
        
        public Set getHeaderMetaDataNameSet(){
            return headers == null ? new HashSet(0) : headers.keySet();
        }
        
        public HeaderMetaData getHeaderMetaData(String name){
            return headers == null ? null : (HeaderMetaData)headers.get(name);
        }
        
        public Set getRecordListMetaDataNameSet(){
            return recordLists == null ? new HashSet(0) : recordLists.keySet();
        }
        
        public RecordListMetaData getRecordListMetaData(String name){
            return recordLists == null ? null : (RecordListMetaData)recordLists.get(name);
        }
        
        public Set getNestedRecordMetaDataNameSet(){
            return nestedRecords == null ? new HashSet(0) : nestedRecords.keySet();
        }
        
        public NestedRecordMetaData getNestedRecordMetaData(String name){
            return nestedRecords == null ? null : (NestedRecordMetaData)nestedRecords.get(name);
        }
        
        public Set getNestedRecordListMetaDataNameSet(){
            return nestedRecordLists == null ? new HashSet(0) : nestedRecordLists.keySet();
        }
        
        public NestedRecordListMetaData getNestedRecordListMetaData(String name){
            return nestedRecordLists == null ? null : (NestedRecordListMetaData)nestedRecordLists.get(name);
        }
        
        public boolean isSynchronized(){
            return isSynchronized;
        }
        
        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            
            if(!element.getTagName().equals(TAG_NAME)){
                throw new DeploymentException(
                    "Root tag must be " + TAG_NAME + " : " + element.getTagName()
                );
            }
            String code = Utility.replaceProperty(dataSetsData, getUniqueAttribute(element, ATTRIBUTE_NAME_CODE));
            className = code;
            if(code.indexOf('.') != -1){
                className = code.substring(code.lastIndexOf('.') + 1);
                packageName = code.substring(0, code.lastIndexOf('.'));
            }
            superClass = Utility.replaceProperty(dataSetsData, getOptionalAttribute(element, ATTRIBUTE_NAME_EXTENDS, superClass));
            isAbstract = getOptionalBooleanAttribute(element, ATTRIBUTE_NAME_ABSTRACT);
            name = Utility.replaceProperty(dataSetsData, getOptionalAttribute(element, ATTRIBUTE_NAME_NAME));
            isSynchronized = getOptionalBooleanAttribute(element, ATTRIBUTE_NAME_SYNCHRONIZED);
            
            final Iterator headerElements = getChildrenByTagName(
                element,
                HeaderMetaData.TAG_NAME
            );
            while(headerElements.hasNext()){
                if(headers == null){
                    headers = new LinkedHashMap();
                }
                final HeaderMetaData headerData = new HeaderMetaData(DataSetMetaData.this);
                headerData.importXML((Element)headerElements.next());
                if(headers.containsKey(headerData.name)){
                    throw new DeploymentException(
                        "Header is duplicated : " + headerData.name
                    );
                }
                headers.put(headerData.name, headerData);
            }
            
            final Iterator recordListElements = getChildrenByTagName(
                element,
                RecordListMetaData.TAG_NAME
            );
            while(recordListElements.hasNext()){
                if(recordLists == null){
                    recordLists = new LinkedHashMap();
                }
                final RecordListMetaData recordListData = new RecordListMetaData(DataSetMetaData.this);
                recordListData.importXML((Element)recordListElements.next());
                if(recordLists.containsKey(recordListData.name)){
                    throw new DeploymentException(
                        "RecordList is duplicated : " + recordListData.name
                    );
                }
                recordLists.put(recordListData.name, recordListData);
            }
            
            final Iterator nestedRecordElements = getChildrenByTagName(
                element,
                NestedRecordMetaData.TAG_NAME
            );
            while(nestedRecordElements.hasNext()){
                if(nestedRecords == null){
                    nestedRecords = new LinkedHashMap();
                }
                final NestedRecordMetaData nestedRecordData = new NestedRecordMetaData(DataSetMetaData.this);
                nestedRecordData.importXML((Element)nestedRecordElements.next());
                if(nestedRecords.containsKey(nestedRecordData.name)){
                    throw new DeploymentException(
                        "NestedRecord is duplicated : " + nestedRecordData.name
                    );
                }
                nestedRecords.put(nestedRecordData.name, nestedRecordData);
            }
            
            final Iterator nestedRecordListElements = getChildrenByTagName(
                element,
                NestedRecordListMetaData.TAG_NAME
            );
            while(nestedRecordListElements.hasNext()){
                if(nestedRecordLists == null){
                    nestedRecordLists = new LinkedHashMap();
                }
                final NestedRecordListMetaData nestedRecordListData = new NestedRecordListMetaData(DataSetMetaData.this);
                nestedRecordListData.importXML((Element)nestedRecordListElements.next());
                if(nestedRecordLists.containsKey(nestedRecordListData.name)){
                    throw new DeploymentException(
                        "NestedRecordList is duplicated : " + nestedRecordListData.name
                    );
                }
                nestedRecordLists.put(nestedRecordListData.name, nestedRecordListData);
            }
        }
        
        public File getFile(File dir){
            String filePath = null;
            if(packageName == null){
                filePath = className + ".java";
            }else{
                filePath = packageName.replaceAll("\\.", "/") + '/' + className + ".java";
            }
            return new File(dir, filePath);
        }
        
        public void writeCode(PrintWriter pw){
            if(packageName != null){
                pw.println("package " + packageName + ';');
            }
            pw.println();
            pw.print("public ");
            if(isAbstract){
                pw.print("abstract ");
            }
            pw.println("class " + className + " extends " + superClass + "{");
            pw.println("    ");
            if(headers != null){
                Iterator itr = headers.keySet().iterator();
                while(itr.hasNext()){
                    String name = (String)itr.next();
                    if(name != null){
                        pw.println("    public static final String HEADER_" + name.toUpperCase() + " = \"" + name + "\";");
                    }
                }
            }
            if(recordLists != null){
                Iterator itr = recordLists.keySet().iterator();
                while(itr.hasNext()){
                    String name = (String)itr.next();
                    if(name != null){
                        pw.println("    public static final String RECORD_LIST_" + name.toUpperCase() + " = \"" + name + "\";");
                    }
                }
            }
            if(nestedRecords != null){
                Iterator itr = nestedRecords.keySet().iterator();
                while(itr.hasNext()){
                    String name = (String)itr.next();
                    if(name != null){
                        pw.println("    public static final String NESTED_RECORD_" + name.toUpperCase() + " = \"" + name + "\";");
                    }
                }
            }
            if(nestedRecordLists != null){
                Iterator itr = nestedRecordLists.keySet().iterator();
                while(itr.hasNext()){
                    String name = (String)itr.next();
                    if(name != null){
                        pw.println("    public static final String NESTED_RECORD_LIST_" + name.toUpperCase() + " = \"" + name + "\";");
                    }
                }
            }
            pw.println("    public " + className + "(){");
            if(name == null){
                pw.println("        this(null, " + isSynchronized + ");");
            }else{
                pw.println("        this(\"" + name + "\", " + isSynchronized + ");");
            }
            pw.println("    }");
            pw.println("    protected " + className + "(String name, boolean isSynch){");
            pw.println("        super(name, isSynch);");
            if(headers != null){
                Iterator itr = headers.values().iterator();
                while(itr.hasNext()){
                    HeaderMetaData headerData = (HeaderMetaData)itr.next();
                    if(headerData.schema != null){
                        String tmpSchema = headerData.schema;
                        headerData.schema = null;
                        headerData.writeCode(pw);
                        headerData.schema = tmpSchema;
                    }else{
                        headerData.writeCode(pw);
                    }
                }
            }
            if(recordLists != null){
                Iterator itr = recordLists.values().iterator();
                while(itr.hasNext()){
                    RecordListMetaData recordListData = (RecordListMetaData)itr.next();
                    if(recordListData.recordClassName != null){
                        String tmpRecordCode = recordListData.recordClassName;
                        recordListData.recordClassName = null;
                        recordListData.writeCode(pw);
                        recordListData.recordClassName = tmpRecordCode;
                    }else{
                        recordListData.writeCode(pw);
                    }
                }
            }
            if(nestedRecords != null){
                Iterator itr = nestedRecords.values().iterator();
                while(itr.hasNext()){
                    NestedRecordMetaData nestedRecordData = (NestedRecordMetaData)itr.next();
                    nestedRecordData.writeCode(pw);
                }
            }
            if(nestedRecordLists != null){
                Iterator itr = nestedRecordLists.values().iterator();
                while(itr.hasNext()){
                    NestedRecordListMetaData nestedRecordListData = (NestedRecordListMetaData)itr.next();
                    nestedRecordListData.writeCode(pw);
                }
            }
            pw.println("    }");
            if(headers != null){
                Iterator itr = headers.values().iterator();
                while(itr.hasNext()){
                    HeaderMetaData headerData = (HeaderMetaData)itr.next();
                    if(headerData.name == null){
                        continue;
                    }
                    pw.println("    public " + headerData.getFullClassName() + " " + Utility.createGetterName(headerData.name + "Header", null) + "(){");
                    pw.println("        return (" + headerData.getFullClassName() + ")getHeader(HEADER_" + headerData.name.toUpperCase() + ");");
                    pw.println("    }");
                    pw.println("    public void " + Utility.createSetterName(headerData.name + "Header") + "(" + headerData.getFullClassName() + " header){");
                    pw.println("        setHeader(HEADER_" + headerData.name.toUpperCase() + ", header);");
                    pw.println("    }");
                }
            }
            if(recordLists != null){
                Iterator itr = recordLists.values().iterator();
                while(itr.hasNext()){
                    RecordListMetaData recordListData = (RecordListMetaData)itr.next();
                    if(recordListData.name == null){
                        continue;
                    }
                    pw.println("    public " + recordListData.getFullClassName() + " " + Utility.createGetterName(recordListData.name + "RecordList", null) + "(){");
                    pw.println("        return (" + recordListData.getFullClassName() + ")getRecordList(RECORD_LIST_" + recordListData.name.toUpperCase() + ");");
                    pw.println("    }");
                }
            }
            if(nestedRecords != null){
                Iterator itr = nestedRecords.values().iterator();
                while(itr.hasNext()){
                    NestedRecordMetaData nestedRecordData = (NestedRecordMetaData)itr.next();
                    pw.println("    public " + nestedRecordData.code + " " + Utility.createCreaterName(nestedRecordData.name + "NestedRecord") + "(){");
                    pw.println("        return (" + nestedRecordData.code + ")createNestedRecord(NESTED_RECORD_" + nestedRecordData.name.toUpperCase() + ");");
                    pw.println("    }");
                }
            }
            if(nestedRecordLists != null){
                Iterator itr = nestedRecordLists.values().iterator();
                while(itr.hasNext()){
                    NestedRecordListMetaData nestedRecordListData = (NestedRecordListMetaData)itr.next();
                    pw.println("    public " + nestedRecordListData.code + " " + Utility.createCreaterName(nestedRecordListData.name + "NestedRecordList") + "(){");
                    pw.println("        return (" + nestedRecordListData.code + ")createNestedRecordList(NESTED_RECORD_LIST_" + nestedRecordListData.name.toUpperCase() + ");");
                    pw.println("    }");
                }
            }
            pw.println("}");
        }
    }
    
    private static class Utility extends jp.ossc.nimbus.core.Utility{
        private static final String CREATE_METHOD_PREFIX = "create";
        private static final String IS_METHOD_PREFIX = "is";
        private static final String GET_METHOD_PREFIX = "get";
        private static final String SET_METHOD_PREFIX = "set";
        
        private Utility(){}
        
        public static String replaceProperty(DataSetsMetaData metaData, String str){
            String result = str;
            result = Utility.replaceSystemProperty(result);
            if(result == null || metaData.properties == null){
                return result;
            }
            final int startIndex = result.indexOf(SYSTEM_PROPERTY_START);
            if(startIndex == -1){
                return result;
            }
            final int endIndex = result.indexOf(SYSTEM_PROPERTY_END);
            if(endIndex == -1 || startIndex > endIndex){
                return result;
            }
            final String propStr = result.substring(
                startIndex + SYSTEM_PROPERTY_START.length(),
                endIndex
            );
            String prop = null;
            if(propStr != null && propStr.length() != 0){
                PropertyMetaData propData = (PropertyMetaData)metaData.properties.get(propStr);
                if(propData != null){
                    prop = propData.getValue();
                }
            }
            if(prop == null){
                return result.substring(0, endIndex + SYSTEM_PROPERTY_END.length())
                 + replaceProperty(
                    metaData,
                    result.substring(endIndex + SYSTEM_PROPERTY_END.length())
                 );
            }else{
                result = result.substring(0, startIndex) + prop
                     + result.substring(endIndex + SYSTEM_PROPERTY_END.length());
            }
            if(result.indexOf(SYSTEM_PROPERTY_START) != -1){
                return replaceProperty(metaData, result);
            }
            return result;
        }
        
        public static String escapeLineSeparator(String str){
            if(str == null || str.length() == 0){
                return str;
            }
            if(str.indexOf("\r\n") != -1){
                str = str.replaceAll("\r\n", "\\\\n");
            }
            if(str.indexOf("\n") != -1){
                str = str.replaceAll("\n", "\\\\n");
            }
            if(str.indexOf("\r") != -1){
                str = str.replaceAll("\r", "\\\\n");
            }
            return str;
        }
        
        public static String createCreaterName(String property){
            StringBuilder result = new StringBuilder(property);
            final int len = result.length();
            if(len != 0 && !Character.isUpperCase(result.charAt(0))){
                char capital = Character.toUpperCase(result.charAt(0));
                result.deleteCharAt(0).insert(0, capital);
            }
            return result.insert(0, CREATE_METHOD_PREFIX).toString();
        }
        
        public static String createGetterName(String property, Class type){
            StringBuilder result = new StringBuilder(property);
            final int len = result.length();
            if(len != 0 && !Character.isUpperCase(result.charAt(0))){
                char capital = Character.toUpperCase(result.charAt(0));
                result.deleteCharAt(0).insert(0, capital);
            }
            return result.insert(0, (type != null && Boolean.TYPE.equals(type)) ? IS_METHOD_PREFIX : GET_METHOD_PREFIX).toString();
        }
        
        public static String createSetterName(String property){
            StringBuilder result = new StringBuilder(property);
            final int len = result.length();
            if(len != 0 && !Character.isUpperCase(result.charAt(0))){
                char capital = Character.toUpperCase(result.charAt(0));
                result.deleteCharAt(0).insert(0, capital);
            }
            return result.insert(0, SET_METHOD_PREFIX).toString();
        }
    }
    
    /**
     * 使用方法を標準出力に表示する。<p>
     */
    private static void usage(){
        try{
            System.out.println(
                getResourceString(USAGE_RESOURCE)
            );
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * リソースを文字列として読み込む。<p>
     *
     * @param name リソース名
     * @exception IOException リソースが存在しない場合
     */
    private static String getResourceString(String name) throws IOException{
        
        // リソースの入力ストリームを取得
        InputStream is = DataSetCodeGenerator.class.getClassLoader()
            .getResourceAsStream(name);
        
        // メッセージの読み込み
        StringBuilder buf = new StringBuilder();
        BufferedReader reader = null;
        final String separator = System.getProperty("line.separator");
        try{
            reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = reader.readLine()) != null){
                buf.append(line).append(separator);
            }
        }finally{
            if(reader != null){
                try{
                    reader.close();
                }catch(IOException e){
                }
            }
        }
        return unicodeConvert(buf.toString());
    }
    
    /**
     * ユニコードエスケープ文字列を含んでいる可能性のある文字列をデフォルトエンコーディングの文字列に変換する。<p>
     *
     * @param str ユニコードエスケープ文字列を含んでいる可能性のある文字列
     * @return デフォルトエンコーディングの文字列
     */
    private static String unicodeConvert(String str){
        char c;
        int len = str.length();
        StringBuilder buf = new StringBuilder(len);
        
        for(int i = 0; i < len; ){
            c = str.charAt(i++);
            if(c == '\\'){
                c = str.charAt(i++);
                if(c == 'u'){
                    int value = 0;
                    for(int j = 0; j < 4; j++){
                        c = str.charAt(i++);
                        switch(c){
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            value = (value << 4) + (c - '0');
                            break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                            value = (value << 4) + 10 + (c - 'a');
                            break;
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            value = (value << 4) + 10 + (c - 'A');
                            break;
                        default:
                            throw new IllegalArgumentException(
                                "Failed to convert unicode : " + c
                            );
                        }
                    }
                    buf.append((char)value);
                }else{
                    switch(c){
                    case 't':
                        c = '\t';
                        break;
                    case 'r':
                        c = '\r';
                        break;
                    case 'n':
                        c = '\n';
                        break;
                    case 'f':
                        c = '\f';
                        break;
                    default:
                    }
                    buf.append(c);
                }
            }else{
                buf.append(c);
            }
        }
        return buf.toString();
    }
    
    public static void main(String[] args) throws Exception{
        
        if(args.length == 0 || args[0].equals("-help")){
            // 使用方法を表示する
            usage();
            return;
        }
        
        boolean option = false;
        String key = null;
        File dest = new File(".");
        String encoding = null;
        boolean verbose = false;
        boolean validate = true;
        final List definitionFiles = new ArrayList();
        for(int i = 0; i < args.length; i++){
            if(option){
                if(key.equals("-d")){
                    dest = new File(args[i]);
                }else if(key.equals("-validate")){
                    validate = Boolean.valueOf(args[i]).booleanValue();
                }else if(key.equals("-encoding")){
                    encoding = args[i];
                }
                option = false;
                key = null;
            }else{
                if(args[i].equals("-d")){
                    option = true;
                    key = args[i];
                }else if(args[i].equals("-validate")){
                    option = true;
                    key = args[i];
                }else if(args[i].equals("-encoding")){
                    option = true;
                    key = args[i];
                }else if(args[i].equals("-v")){
                    verbose = true;
                }else{
                  definitionFiles.add(new File(args[i]));
                }
            }
        }
        
        final DataSetCodeGenerator generator = new DataSetCodeGenerator();
        generator.isValidate = validate;
        generator.encoding = encoding;
        for(int i = 0; i < definitionFiles.size(); i++){
            File[] files = generator.generate((File)definitionFiles.get(i), dest);
            if(verbose){
                for(int j = 0; j < files.length; j++){
                    System.out.println(files[j]);
                }
            }
        }
        Thread.sleep(500);
    }
}
