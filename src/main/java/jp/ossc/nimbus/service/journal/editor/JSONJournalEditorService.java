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
package jp.ossc.nimbus.service.journal.editor;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.journal.JournalEditor;

/**
 * 任意のオブジェクトをJSON形式文字列に編集するジャーナルエディター。<p>
 *
 * @author M.Takata
 */
public class JSONJournalEditorService extends ServiceBase
 implements JournalEditor, JSONJournalEditorServiceMBean{
    
    private static final long serialVersionUID = 8863151772494235330L;
    
    protected static final String DEFAULT_SECRET_STRING = "******";
    protected static final String STRING_ENCLOSURE = "\"";
    
    protected static final String ARRAY_SEPARATOR = ",";
    protected static final String ARRAY_ENCLOSURE_START = "[";
    protected static final String ARRAY_ENCLOSURE_END = "]";
    
    protected static final String OBJECT_ENCLOSURE_START = "{";
    protected static final String OBJECT_ENCLOSURE_END = "}";
    protected static final String PROPERTY_SEPARATOR = ":";
    
    protected static final String NULL_VALUE = "null";
    protected static final String BOOLEAN_VALUE_TRUE = "true";
    protected static final String BOOLEAN_VALUE_FALSE = "false";
    
    protected static final char ESCAPE = '\\';
    
    protected static final char QUOTE = '"';
    protected static final char BACK_SLASH = '\\';
    protected static final char SLASH = '/';
    protected static final char BACK_SPACE = '\b';
    protected static final char CHANGE_PAGE = '\f';
    protected static final char LF = '\n';
    protected static final char CR = '\r';
    protected static final char TAB = '\t';
    
    protected static final String ESCAPE_QUOTE = "\\\"";
    protected static final String ESCAPE_BACK_SLASH = "\\\\";
    protected static final String ESCAPE_SLASH = "\\/";
    protected static final String ESCAPE_BACK_SPACE = "\\b";
    protected static final String ESCAPE_CHANGE_PAGE = "\\f";
    protected static final String ESCAPE_LF = "\\n";
    protected static final String ESCAPE_CR = "\\r";
    protected static final String ESCAPE_TAB = "\\t";
    
    protected boolean isExpandArrayValue = false;
    protected int maxArraySize = -1;
    protected boolean isExpandMapValue = false;
    protected int maxMapSize = -1;
    protected String[] secretProperties;
    protected Set secretPropertySet;
    protected String secretString = DEFAULT_SECRET_STRING;
    protected String[] enabledProperties;
    protected Set enabledPropertySet;
    protected String[] disabledProperties;
    protected Set disabledPropertySet;
    protected boolean isOutputKey = true;
    protected boolean isUnicodeEscape = false;
    
    public void setExpandArrayValue(boolean isExpand){
        isExpandArrayValue = isExpand;
    }
    public boolean isExpandArrayValue(){
        return isExpandArrayValue;
    }
    
    public void setMaxArraySize(int max){
        maxArraySize = max;
    }
    public int getMaxArraySize(){
        return maxArraySize;
    }
    
    public void setMaxMapSize(int max){
        maxMapSize = max;
    }
    public int getMaxMapSize(){
        return maxMapSize;
    }
    
    @Override
    public void setExpandMapValue(boolean isExpand){
        isExpandMapValue = isExpand;
    }
    public boolean isExpandMapValue(){
        return isExpandMapValue;
    }
    
    public void setSecretProperties(String[] names){
        secretProperties = names;
    }
    
    public String[] getSecretProperties(){
        return secretProperties;
    }
    
    public void setSecretString(String str){
        secretString = str;
    }
    
    public String getSecretString(){
        return secretString;
    }
    
    public void setEnabledProperties(String[] names){
        enabledProperties = names;
    }
    
    public String[] getEnabledProperties(){
        return enabledProperties;
    }
    
    public void setDisabledProperties(String[] names){
        disabledProperties = names;
    }
    
    public String[] getDisabledProperties(){
        return disabledProperties;
    }
    
    public void setOutputKey(boolean isOutput){
        isOutputKey = isOutput;
    }
    public boolean isOutputKey(){
        return isOutputKey;
    }
    
    public boolean isUnicodeEscape(){
        return isUnicodeEscape;
    }
    public void setUnicodeEscape(boolean isEscape){
        isUnicodeEscape = isEscape;
    }
    
    protected void preStartService() throws Exception{
        super.preStartService();
        if(secretProperties != null && secretProperties.length != 0){
            secretPropertySet = new HashSet(secretProperties.length);
            for(int i = 0; i < secretProperties.length; i++){
                secretPropertySet.add(secretProperties[i]);
            }
        }
        if(enabledProperties != null && enabledProperties.length != 0){
            enabledPropertySet = new HashSet(enabledProperties.length);
            for(int i = 0; i < enabledProperties.length; i++){
                enabledPropertySet.add(enabledProperties[i]);
            }
        }
        if(disabledProperties != null && disabledProperties.length != 0){
            disabledPropertySet = new HashSet(disabledProperties.length);
            for(int i = 0; i < disabledProperties.length; i++){
                disabledPropertySet.add(disabledProperties[i]);
            }
        }
    }
    
    protected StringBuilder appendName(StringBuilder buf, String name){
        buf.append(STRING_ENCLOSURE);
        buf.append(escape(name));
        buf.append(STRING_ENCLOSURE);
        return buf;
    }
    
    protected boolean isRecursiveCall(Stack<Object> stack){
        return stack.size() > 1;
    }
    
    protected boolean isRecursiveInstance(Object value, Stack<Object> stack){
        if(value == null || stack.size() == 1){
            return false;
        }
        for(int i = 0, imax = stack.size() - 1; i < imax; i++){
            if(stack.get(i) == value){
                return true;
            }
        }
        return false;
    }
    
    protected StringBuilder appendValue(StringBuilder buf, EditorFinder finder, Class type, Object value, Stack stack){
        try{
            stack.push(value);
            if(isRecursiveInstance(value, stack)){
                buf.append("recursive instance");
                return buf;
            }
            if(type == null && value != null){
                type = value.getClass();
            }
            if(value == null){
                if(type == null){
                    buf.append(NULL_VALUE);
                }else if(Number.class.isAssignableFrom(type)
                    || (type.isPrimitive()
                        && (Byte.TYPE.equals(type)
                            || Short.TYPE.equals(type)
                            || Integer.TYPE.equals(type)
                            || Long.TYPE.equals(type)
                            || Float.TYPE.equals(type)
                            || Double.TYPE.equals(type)))
                ){
                    buf.append('0');
                }else if(Boolean.class.equals(type)
                    || Boolean.TYPE.equals(type)
                ){
                    buf.append(BOOLEAN_VALUE_FALSE);
                }else{
                    buf.append(NULL_VALUE);
                }
            }else if(Boolean.class.equals(type)
                || Boolean.TYPE.equals(type)
            ){
                if(((Boolean)value).booleanValue()){
                    buf.append(BOOLEAN_VALUE_TRUE);
                }else{
                    buf.append(BOOLEAN_VALUE_FALSE);
                }
            }else if(Number.class.isAssignableFrom(type)
                || (type.isPrimitive()
                    && (Byte.TYPE.equals(type)
                        || Short.TYPE.equals(type)
                        || Integer.TYPE.equals(type)
                        || Long.TYPE.equals(type)
                        || Float.TYPE.equals(type)
                        || Double.TYPE.equals(type)))
            ){
                if((value instanceof Float && (((Float)value).isNaN() || ((Float)value).isInfinite()))
                        || (value instanceof Double && (((Double)value).isNaN() || ((Double)value).isInfinite()))
                ){
                    buf.append(STRING_ENCLOSURE);
                    buf.append(escape(value.toString()));
                    buf.append(STRING_ENCLOSURE);
                }else{
                    buf.append(value);
                }
            }else if(type.isArray() || Collection.class.isAssignableFrom(type)){
                if(isExpandArrayValue){
                    appendArray(buf, finder, value, stack);
                }else{
                    appendUnknownValue(buf, finder, type, value, stack);
                }
            }else if(CharSequence.class.isAssignableFrom(type)
                 || Character.class.equals(type)
                 || Character.TYPE.equals(type)){
                buf.append(STRING_ENCLOSURE);
                buf.append(escape(value.toString()));
                buf.append(STRING_ENCLOSURE);
            }else if(Map.class.isAssignableFrom(type)){
                if(isExpandMapValue){
                    appendMap(buf, finder, (Map)value, stack);
                }else{
                    appendUnknownValue(buf, finder, type, value, stack);
                }
            }else{
                appendUnknownValue(buf, finder, type, value, stack);
            }
            return buf;
        }finally{
            stack.pop();
        }
    }
    
    protected StringBuilder appendUnknownValue(StringBuilder buf, EditorFinder finder, Class type, Object value, Stack stack){
        JournalEditor editor = finder.findEditor(value);
        if(editor != null && editor != this){
            value = editor.toObject(finder, null, value);
            if(editor instanceof JSONJournalEditorService){
                buf.append(value.toString());
            }else{
                buf.append(STRING_ENCLOSURE);
                buf.append(escape(value.toString()));
                buf.append(STRING_ENCLOSURE);
            }
        }else{
            buf.append(STRING_ENCLOSURE);
            buf.append(escape(value.toString()));
            buf.append(STRING_ENCLOSURE);
        }
        return buf;
    }
    
    protected StringBuilder appendMap(StringBuilder buf, EditorFinder finder, Map map, Stack stack){
        buf.append(OBJECT_ENCLOSURE_START);
        Iterator itr = map.entrySet().iterator();
        boolean isOutput = false;
        int i = 0;
        while(itr.hasNext()){
            Map.Entry entry = (Map.Entry)itr.next();
            Object key = entry.getKey();
            String name = key == null ? null : key.toString();
            if(!isOutputProperty(name)){
                continue;
            }
            if(isOutput){
                buf.append(ARRAY_SEPARATOR);
            }
            if(maxMapSize >= 0 && i >= maxMapSize){
                buf.append("\"... length\":" + map.size());
                break;
            }
            isOutput = true;
            appendProperty(
                buf,
                finder,
                name,
                entry.getValue(),
                stack
            );
            i++;
        }
        buf.append(OBJECT_ENCLOSURE_END);
        return buf;
    }
    
    protected boolean isOutputProperty(String name){
        if(name != null
            && disabledPropertySet != null
            && disabledPropertySet.contains(name)
        ){
            return false;
        }
        if(name != null
            && enabledPropertySet != null
            && !enabledPropertySet.contains(name)
        ){
            return false;
        }
        return true;
    }
    
    protected boolean isSecretProperty(String name){
        return name != null && secretPropertySet != null && secretPropertySet.contains(name);
    }
    
    protected StringBuilder appendProperty(StringBuilder buf, EditorFinder finder, String name, Object value, Stack stack){
        appendName(buf, name);
        buf.append(PROPERTY_SEPARATOR);
        if(isSecretProperty(name)){
            appendValue(buf, finder, null, secretString, stack);
        }else{
            appendValue(buf, finder, null, value, stack);
        }
        return buf;
    }
    
    protected StringBuilder appendArray(StringBuilder buf, EditorFinder finder, Object array, Stack stack){
        buf.append(ARRAY_ENCLOSURE_START);
        if(array.getClass().isArray()){
            for(int i = 0, imax = Array.getLength(array); i < imax; i++){
                if(maxArraySize >= 0 && i >= maxArraySize){
                    buf.append("\"... length=" + imax + '"');
                    break;
                }
                appendValue(buf, finder, null, Array.get(array, i), stack);
                if(i != imax - 1){
                    buf.append(ARRAY_SEPARATOR);
                }
            }
        }else if(List.class.isAssignableFrom(array.getClass())){
            List list = (List)array;
            for(int i = 0, imax = list.size(); i < imax; i++){
                if(maxArraySize >= 0 && i >= maxArraySize){
                    buf.append("\"... length=" + imax + '"');
                    break;
                }
                appendValue(buf, finder, null, list.get(i), stack);
                if(i != imax - 1){
                    buf.append(ARRAY_SEPARATOR);
                }
            }
        }else if(Collection.class.isAssignableFrom(array.getClass())){
            Object[] elements = ((Collection)array).toArray();
            for(int i = 0, imax = elements.length; i < imax; i++){
                if(maxArraySize >= 0 && i >= maxArraySize){
                    buf.append("\"... length=" + imax + '"');
                    break;
                }
                appendValue(buf, finder, null, elements[i], stack);
                if(i != imax - 1){
                    buf.append(ARRAY_SEPARATOR);
                }
            }
        }else if(Enumeration.class.isAssignableFrom(array.getClass())){
            Enumeration enm = (Enumeration)array;
            int i = 0;
            while(enm.hasMoreElements()){
                if(maxArraySize >= 0 && i >= maxArraySize){
                    buf.append("\"... length=?\"");
                    break;
                }
                appendValue(buf, finder, null, enm.nextElement(), stack);
                if(enm.hasMoreElements()){
                    buf.append(ARRAY_SEPARATOR);
                }
                i++;
            }
        }
        buf.append(ARRAY_ENCLOSURE_END);
        return buf;
    }
    
    protected String escape(String str){
        if(str == null || str.length() == 0){
            return str;
        }
        boolean isEscape = false;
        final StringBuilder buf = new StringBuilder();
        for(int i = 0, imax = str.length(); i < imax; i++){
            final char c = str.charAt(i);
            
            switch(c){
            case QUOTE:
                buf.append(ESCAPE_QUOTE);
                isEscape = true;
                break;
            case BACK_SLASH:
                buf.append(ESCAPE_BACK_SLASH);
                isEscape = true;
                break;
            case SLASH:
                buf.append(ESCAPE_SLASH);
                isEscape = true;
                break;
            case BACK_SPACE:
                buf.append(ESCAPE_BACK_SPACE);
                isEscape = true;
                break;
            case CHANGE_PAGE:
                buf.append(ESCAPE_CHANGE_PAGE);
                isEscape = true;
                break;
            case LF:
                buf.append(ESCAPE_LF);
                isEscape = true;
                break;
            case CR:
                buf.append(ESCAPE_CR);
                isEscape = true;
                break;
            case TAB:
                buf.append(ESCAPE_TAB);
                isEscape = true;
                break;
            default:
                if(isUnicodeEscape
                    && !(c == 0x20
                        || c == 0x21
                        || (0x23 <= c && c <= 0x5B)
                        || (0x5D <= c && c <= 0x7E))
                ){
                    isEscape = true;
                    toUnicode(c, buf);
                }else{
                    buf.append(c);
                }
            }
        }
        return isEscape ? buf.toString() : str;
    }
    
    protected StringBuilder toUnicode(char c, StringBuilder buf){
        buf.append(ESCAPE);
        buf.append('u');
        int mask = 0xf000;
        for(int i = 0; i < 4; i++){
            mask = 0xf000 >> (i * 4);
            int val = c & mask;
            val = val << (i * 4);
            switch(val){
            case 0x0000:
                buf.append('0');
                break;
            case 0x1000:
                buf.append('1');
                break;
            case 0x2000:
                buf.append('2');
                break;
            case 0x3000:
                buf.append('3');
                break;
            case 0x4000:
                buf.append('4');
                break;
            case 0x5000:
                buf.append('5');
                break;
            case 0x6000:
                buf.append('6');
                break;
            case 0x7000:
                buf.append('7');
                break;
            case 0x8000:
                buf.append('8');
                break;
            case 0x9000:
                buf.append('9');
                break;
            case 0xa000:
                buf.append('a');
                break;
            case 0xb000:
                buf.append('b');
                break;
            case 0xc000:
                buf.append('c');
                break;
            case 0xd000:
                buf.append('d');
                break;
            case 0xe000:
                buf.append('e');
                break;
            case 0xf000:
                buf.append('f');
                break;
            default:
            }
        }
        return buf;
    }
    
    public Object toObject(EditorFinder finder, Object key, Object value){
        return toString(new StringBuilder(), finder, key == null ? null : key.toString(), value).toString();
    }
    
    protected StringBuilder toString(StringBuilder buf, EditorFinder finder, String key, Object value){
        if(key == null || !isOutputKey()){
            appendValue(buf, finder, null, value, new Stack());
        }else{
            buf.append(OBJECT_ENCLOSURE_START);
            appendProperty(buf, finder, key, value, new Stack());
            buf.append(OBJECT_ENCLOSURE_END);
        }
        return buf;
    }
}
