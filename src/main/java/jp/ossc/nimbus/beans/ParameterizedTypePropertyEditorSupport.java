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
package jp.ossc.nimbus.beans;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;

import jp.ossc.nimbus.core.ServiceLoader;
import jp.ossc.nimbus.core.ServiceManager;

/**
 * ParameterizedTypeなプロパティの編集を行うPropertyEditorインタフェース。<p>
 *
 * @author M.Takata
 */
public abstract class ParameterizedTypePropertyEditorSupport extends PropertyEditorSupport implements ParameterizedTypePropertyEditor{
    
    protected ParameterizedType parameterizedType;
    protected transient ServiceLoader loader;
    protected transient ServiceManager manager;
    
    public void setParameterizedType(ParameterizedType type){
        parameterizedType = type;
    }
    
    public void setServiceLoader(ServiceLoader loader){
        this.loader = loader;
    }
    
    public void setServiceManager(ServiceManager manager){
        this.manager = manager;
    }
    
    protected PropertyEditor findEditor(Class<?> type){
        if(loader == null){
            return NimbusPropertyEditorManager.findEditor(type);
        }else{
            return loader.findEditor(type);
        }
    }
    
    protected Object getValue(Type type, String str){
        PropertyEditor editor = null;
        if(type instanceof Class){
            editor = findEditor((Class<?>)type);
        }else if(type instanceof ParameterizedType){
            editor = findEditor((Class<?>)((ParameterizedType)type).getRawType());
        }
        if(editor == null){
            return str;
        }
        if(editor instanceof ServiceNameEditor){
            if(manager != null){
                ((ServiceNameEditor)editor).setServiceManagerName(
                    manager.getServiceName()
                );
            }
        }else if(editor instanceof ServiceNameArrayEditor){
            if(manager != null){
                ((ServiceNameArrayEditor)editor).setServiceManagerName(
                    manager.getServiceName()
                );
            }
        }else if(editor instanceof ParameterizedTypePropertyEditor){
            ParameterizedTypePropertyEditor paraEditor = (ParameterizedTypePropertyEditor)editor;
            paraEditor.setParameterizedType((ParameterizedType)type);
            paraEditor.setServiceLoader(loader);
            paraEditor.setServiceManager(manager);
        }
        editor.setAsText(str);
        return editor.getValue();
    }
    
    protected String getAsText(Type type, Object value){
        if(type instanceof Class){
            PropertyEditor editor = findEditor((Class<?>)type);
            if(editor != null){
                editor.setValue(value);
                return editor.getAsText();
            }
        }else if(type instanceof ParameterizedType){
            PropertyEditor editor = findEditor((Class<?>)((ParameterizedType)type).getRawType());
            if(editor instanceof ServiceNameEditor){
                if(manager != null){
                    ((ServiceNameEditor)editor).setServiceManagerName(
                        manager.getServiceName()
                    );
                }
            }else if(editor instanceof ServiceNameArrayEditor){
                if(manager != null){
                    ((ServiceNameArrayEditor)editor).setServiceManagerName(
                        manager.getServiceName()
                    );
                }
            }
            if(editor != null && editor instanceof ParameterizedTypePropertyEditor){
                ParameterizedTypePropertyEditor paraEditor = (ParameterizedTypePropertyEditor)editor;
                paraEditor.setParameterizedType((ParameterizedType)type);
                paraEditor.setServiceLoader(loader);
                paraEditor.setServiceManager(manager);
                paraEditor.setValue(value);
                return editor.getAsText();
            }
        }
        return value == null ? null : value.toString();
    }
}