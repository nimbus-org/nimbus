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
package jp.ossc.nimbus.core;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;

/**
 * コンストラクタ定義&lt;constructor&gt;要素メタデータ。<p>
 * サービス定義ファイルの&lt;constructor&gt;要素に記述された内容を格納するメタデータコンテナである。<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">サービス定義ファイルDTD</a>
 */
public class ConstructorMetaData extends MetaData
 implements Serializable{
    
    private static final long serialVersionUID = 1385893693509473372L;
    
    /**
     * &lt;constructor&gt;要素の要素名文字列。<p>
     */
    public static final String CONSTRUCTOR_TAG_NAME = "constructor";
    
    /**
     * 子要素&lt;invoke&gt;を表すメタデータ。<p>
     */
    protected InvokeMetaData invoke;
    
    /**
     * 子要素&lt;static-invoke&gt;を表すメタデータ。<p>
     */
    protected StaticInvokeMetaData staticInvoke;
    
    /**
     * 子要素&lt;static-field=ref&gt;を表すメタデータ。<p>
     */
    protected StaticFieldRefMetaData staticFieldRef;
    
    /**
     * 子要素&lt;argument&gt;を格納するリスト。<p>
     */
    protected List arguments = new ArrayList();
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * 
     * @param parent 親要素のメタデータ
     */
    public ConstructorMetaData(ObjectMetaData parent){
        super(parent);
    }
    
    /**
     * この&lt;constructor&gt;要素の子要素&lt;invoke&gt;要素を表す{@link InvokeMetaData}を取得する。<p>
     *
     * @return 子要素&lt;invoke&gt;要素を表すInvokeMetaData
     */
    public InvokeMetaData getInvoke(){
        return invoke;
    }
    
    /**
     * この&lt;constructor&gt;要素の子要素&lt;invoke&gt;要素を表す{@link InvokeMetaData}を設定する。<p>
     *
     * @param data 子要素&lt;invoke&gt;要素を表すInvokeMetaData
     */
    public void setInvoke(InvokeMetaData data){
        invoke = data;
    }
    
    /**
     * この&lt;constructor&gt;要素の子要素&lt;static-invoke&gt;要素を表す{@link StaticInvokeMetaData}を取得する。<p>
     *
     * @return 子要素&lt;static-invoke&gt;要素を表すStaticInvokeMetaData
     */
    public StaticInvokeMetaData getStaticInvoke(){
        return staticInvoke;
    }
    
    /**
     * この&lt;constructor&gt;要素の子要素&lt;static-invoke&gt;要素を表す{@link StaticInvokeMetaData}を設定する。<p>
     *
     * @param data 子要素&lt;static-invoke&gt;要素を表すStaticInvokeMetaData
     */
    public void setStaticInvoke(StaticInvokeMetaData data){
        staticInvoke = data;
    }
    
    /**
     * この&lt;constructor&gt;要素の子要素&lt;static-field-ref&gt;要素を表す{@link StaticInvokeMetaData}を取得する。<p>
     *
     * @return 子要素&lt;static-field-ref&gt;要素を表すStaticFieldRefMetaData
     */
    public StaticFieldRefMetaData getStaticFieldRef(){
        return staticFieldRef;
    }
    
    /**
     * この&lt;constructor&gt;要素の子要素&lt;static-field-ref&gt;要素を表す{@link StaticFieldRefMetaData}を設定する。<p>
     *
     * @param data 子要素&lt;static-field-ref&gt;要素を表すStaticFieldRefMetaData
     */
    public void setStaticFieldRef(StaticFieldRefMetaData data){
        staticFieldRef = data;
    }
    
    /**
     * この&lt;constructor&gt;要素の子要素&lt;argument&gt;要素を表す{@link ArgumentMetaData}の集合を取得する。<p>
     *
     * @return 子要素&lt;argument&gt;要素を表すArgumentMetaDataの集合
     */
    public Collection getArguments(){
        return arguments;
    }
    
    /**
     * この&lt;constructor&gt;要素の子要素&lt;argument&gt;要素を表す{@link ArgumentMetaData}を追加する。<p>
     *
     * @param arg 子要素&lt;argument&gt;要素を表すArgumentMetaData
     */
    public void addArgument(ArgumentMetaData arg){
        arguments.add(arg);
    }
    
    /**
     * この&lt;constructor&gt;要素の子要素&lt;argument&gt;要素を表す{@link ArgumentMetaData}を削除する。<p>
     *
     * @param arg 子要素&lt;argument&gt;要素を表すArgumentMetaData
     */
    public void removeArgument(ArgumentMetaData arg){
        arguments.remove(arg);
    }
    
    /**
     * この&lt;constructor&gt;要素の子要素&lt;argument&gt;要素を表す{@link ArgumentMetaData}を全て削除する。<p>
     */
    public void clearArguments(){
        arguments.clear();
    }
    
    /**
     * &lt;constructor&gt;要素のElementをパースして、自分自身の初期化、及び子要素のメタデータの生成を行う。<p>
     *
     * @param element &lt;constructor&gt;要素のElement
     * @exception DeploymentException &lt;constructor&gt;要素の解析、その結果によるメタデータの生成に失敗した場合
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        
        if(!element.getTagName().equals(CONSTRUCTOR_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + CONSTRUCTOR_TAG_NAME + " : "
                 + element.getTagName()
            );
            
        }
        
        final Element invokeElement = getOptionalChild(
            element,
            InvokeMetaData.INVOKE_TAG_NAME
        );
        if(invokeElement != null){
            final InvokeMetaData invokeData
                 = new InvokeMetaData(this);
            invokeData.importXML(invokeElement);
            if(invokeData.getTarget() == null){
                throw new DeploymentException("Target is null." + invokeData);
            }
            invoke = invokeData;
            return;
        }
        
        final Element staticInvokeElement = getOptionalChild(
            element,
            StaticInvokeMetaData.STATIC_INVOKE_TAG_NAME
        );
        if(staticInvokeElement != null){
            final StaticInvokeMetaData staticInvokeData
                 = new StaticInvokeMetaData(this);
            staticInvokeData.importXML(staticInvokeElement);
            staticInvoke = staticInvokeData;
            return;
        }
        
        final Element staticFieldRefElement = getOptionalChild(
            element,
            StaticFieldRefMetaData.STATIC_FIELD_REF_TAG_NAME
        );
        if(staticFieldRefElement != null){
            final StaticFieldRefMetaData staticFieldRefData
                 = new StaticFieldRefMetaData(this);
            staticFieldRefData.importXML(staticFieldRefElement);
            staticFieldRef = staticFieldRefData;
            return;
        }
        
        final Iterator argElements = getChildrenByTagName(
            element,
            ArgumentMetaData.ARGUMENT_TAG_NAME
        );
        while(argElements.hasNext()){
            final ArgumentMetaData argData
                 = new ArgumentMetaData(this, (ObjectMetaData)getParent());
            argData.importXML((Element)argElements.next());
            addArgument(argData);
        }
    }
    
    public void importIfDef() throws DeploymentException{
        if(invoke != null){
            invoke.importIfDef();
        }
        if(staticInvoke != null){
            staticInvoke.importIfDef();
        }
        if(staticFieldRef != null){
            staticFieldRef.importIfDef();
        }
        
        if(arguments.size() != 0){
            for(int i = 0, imax = arguments.size(); i < imax; i++){
                MetaData argument = (MetaData)((MetaData)arguments.get(i));
                argument.importIfDef();
            }
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append('<').append(CONSTRUCTOR_TAG_NAME).append('>');
        if(arguments.size() != 0){
            buf.append(LINE_SEPARATOR);
            for(int i = 0, imax = arguments.size(); i < imax; i++){
                buf.append(
                    addIndent(((MetaData)arguments.get(i)).toXML(new StringBuilder()))
                );
                buf.append(LINE_SEPARATOR);
            }
        }else{
            MetaData data = invoke;
            if(data == null){
                data = staticInvoke;
            }
            if(data == null){
                data = staticFieldRef;
            }
            if(data != null){
                buf.append(LINE_SEPARATOR);
                buf.append(
                    addIndent(data.toXML(new StringBuilder()))
                );
                buf.append(LINE_SEPARATOR);
            }
        }
        buf.append("</").append(CONSTRUCTOR_TAG_NAME).append('>');
        return buf;
    }
    
    public Object clone(){
        ConstructorMetaData clone = (ConstructorMetaData)super.clone();
        if(invoke != null){
            clone.invoke = (InvokeMetaData)invoke.clone();
            clone.invoke.setParent(clone);
        }
        if(staticInvoke != null){
            clone.staticInvoke = (StaticInvokeMetaData)staticInvoke.clone();
            clone.staticInvoke.setParent(clone);
        }
        if(staticFieldRef != null){
            clone.staticFieldRef = (StaticFieldRefMetaData)staticFieldRef.clone();
            clone.staticFieldRef.setParent(clone);
        }
        
        if(arguments.size() != 0){
            clone.arguments = new ArrayList(arguments.size());
            for(int i = 0, imax = arguments.size(); i < imax; i++){
                MetaData argument = (MetaData)((MetaData)arguments.get(i)).clone();
                argument.setParent(clone);
                clone.arguments.add(argument);
            }
        }
        return clone;
    }
}
