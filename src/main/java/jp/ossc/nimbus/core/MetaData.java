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
 * サービス定義メタデータ。<p>
 * サービス定義の各要素のメタデータの基底クラスである。<br>
 * サービス定義の各要素の基本的機能と、XMLをパースするユーティリティメソッドを持つ。<br>
 * 
 * @author M.Takata
 */
public abstract class MetaData implements Serializable, Cloneable{
    
    private static final long serialVersionUID = -5571905040948580821L;
    
    protected static final String LINE_SEPARATOR
         = System.getProperty("line.separator");
    private static final String INDENT_STRING = "    ";
    
    /**
     * このメタデータの親要素となるメタデータ。<p>
     *
     * @see #getParent()
     */
    private MetaData parent;
    
    private String comment;
    
    private IfDefMetaData ifdefData;
    
    /**
     * 親要素を持たないメタデータを生成する。<p>
     */
    public MetaData(){
    }
    
    /**
     * 親要素を持つメタデータを生成する。<p>
     * 
     * @param parent 親要素のメタデータ
     */
    public MetaData(MetaData parent){
        this.parent = parent;
    }
    
    /**
     * 親要素のメタデータを取得する。<p>
     * 親要素を持たない場合は、nullを返す。
     * 
     * @return 親要素のメタデータ
     */
    public MetaData getParent(){
        return parent;
    }
    
    /**
     * 親要素のメタデータを設定する。<p>
     * 
     * @param parent 親要素のメタデータ
     */
    public void setParent(MetaData parent){
        this.parent = parent;
    }
    
    /**
     * この要素に対するコメントを設定する。<p>
     *
     * @param comment コメント
     */
    public void setComment(String comment){
        this.comment = comment;
    }
    
    /**
     * この要素に対するコメントを取得する。<p>
     *
     * @return コメント
     */
    public String getComment(){
        return comment;
    }
    
    public IfDefMetaData getIfDefMetaData(){
        return ifdefData;
    }
    
    public void setIfDefMetaData(IfDefMetaData ifdef){
        ifdefData = ifdef;
    }
    
    /**
     * このメタデータが表す要素のElementをパースして、自分自身の初期化、及び子要素のメタデータの生成を行う。<p>
     *
     * @param element このメタデータが表す要素のElement
     * @exception DeploymentException 要素の解析、その結果によるメタデータの生成に失敗した場合
     */
    public void importXML(Element element) throws DeploymentException{
        comment = getElementComment(element);
    }
    
    /**
     * このメタデータが表す要素をXML形式で出力する。<p>
     *
     * @return XML形式文字列
     */
    public StringBuilder toXML(StringBuilder buf){
        return buf;
    }
    
    protected StringBuilder appendComment(StringBuilder buf){
        final String comment = getComment();
        if(comment != null){
            buf.append("<!--");
            if(comment.indexOf('\r') != -1
                || comment.indexOf('\n') != -1){
                buf.append(LINE_SEPARATOR);
                buf.append(addIndent(comment));
                buf.append(LINE_SEPARATOR);
            }else{
                buf.append(' ');
                buf.append(comment);
                buf.append(' ');
            }
            buf.append("-->");
            buf.append(LINE_SEPARATOR);
        }
        return buf;
    }
    
    /**
     * 指定された文字列バッファに格納されている文字列を1インデント字下げする。<p>
     *
     * @param buf 文字列バッファ
     * @return 文字列バッファ
     */
    protected StringBuilder addIndent(StringBuilder buf){
        return setIndent(buf, 1);
    }
    
    /**
     * 指定された文字列を1インデント字下げする。<p>
     *
     * @param str 文字列
     * @return 文字列
     */
    protected String addIndent(String str){
        return setIndent(str, 1);
    }
    
    /**
     * 指定された文字列バッファに格納されている文字列を指定インデント字下げする。<p>
     *
     * @param buf 文字列バッファ
     * @param indent インデント数
     * @return 文字列バッファ
     */
    protected StringBuilder setIndent(StringBuilder buf, int indent){
        final String str = buf.toString();
        buf.setLength(0);
        return buf.append(setIndent(str, indent));
    }
    
    /**
     * 指定された文字列を指定インデント字下げする。<p>
     *
     * @param str 文字列
     * @param indent インデント数
     * @return 文字列
     */
    protected String setIndent(String str, int indent){
        if(str == null){
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        for(int i = 0; i < indent; i++){
            buf.append(INDENT_STRING);
        }
        final String indentString = buf.toString();
        buf.setLength(0);
        final int length = str.length();
        if(length == 0){
            return buf.toString();
        }
        final StringReader sr = new StringReader(str);
        final BufferedReader br = new BufferedReader(sr, length);
        try{
            String line = br.readLine();
            while(line != null){
                buf.append(indentString).append(line);
                line = br.readLine();
                if(line != null){
                    buf.append(LINE_SEPARATOR);
                }
            }
        }catch(IOException e){
            // 起きないはず
            e.printStackTrace();
        }finally{
            try{
                br.close();
            }catch(IOException e){
                // 起きないはず
                e.printStackTrace();
            }
            sr.close();
        }
        return buf.toString();
    }
    
    /**
     * 指定した要素から、子要素の繰り返し処理をする反復子を取得する。<p>
     * 引数で指定された検索対象のelementがnullの場合は、nullを返す。また、子要素が、見つからない場合は、繰り返し要素を持たない反復子を返す。<br>
     *
     * @param element 要素
     * @return 子要素の繰り返し処理をする反復子
     */
    public static Iterator getChildren(Element element){
        if(element == null){
            return null;
        }
        
        final NodeList children = element.getChildNodes();
        final List result = new ArrayList();
        for(int i = 0, max = children.getLength(); i < max; i++){
            final Node currentChild = children.item(i);
            if(currentChild.getNodeType() == Node.ELEMENT_NODE)
            {
                result.add((Element)currentChild);
            }
        }
        return result.iterator();
    }
    
    /**
     * 指定した要素から、指定した名前の子要素の繰り返し処理をする反復子を取得する。<p>
     * 引数で指定された検索対象のelementがnullの場合は、nullを返す。また、指定された検索する要素名tagNameが、見つからない場合は、繰り返し要素を持たない反復子を返す。<br>
     *
     * @param element 要素
     * @param tagName 要素名
     * @return 指定した名前の子要素の繰り返し処理をする反復子
     */
    public static Iterator getChildrenByTagName(
        Element element,
        String tagName
    ){
        if(element == null){
            return null;
        }
        
        final NodeList children = element.getChildNodes();
        final List result = new ArrayList();
        for(int i = 0, max = children.getLength(); i < max; i++){
            final Node currentChild = children.item(i);
            if(currentChild.getNodeType() == Node.ELEMENT_NODE
                 && ((Element)currentChild).getTagName().equals(tagName))
            {
                result.add((Element)currentChild);
            }
        }
        return result.iterator();
    }
    
    /**
     * 指定した要素から、指定した名前以外の子要素の繰り返し処理をする反復子を取得する。<p>
     * 引数で指定された検索対象のelementがnullの場合は、nullを返す。また、指定された要素名tagName以外の要素が、見つからない場合は、繰り返し要素を持たない反復子を返す。<br>
     *
     * @param element 要素
     * @param tagNames 除外する要素名
     * @return 指定した名前以外の子要素の繰り返し処理をする反復子
     */
    public static Iterator getChildrenWithoutTagName(
        Element element,
        String[] tagNames
    ){
        if(element == null){
            return null;
        }
        
        final NodeList children = element.getChildNodes();
        final List result = new ArrayList();
        for(int i = 0, max = children.getLength(); i < max; i++){
            final Node currentChild = children.item(i);
            if(currentChild.getNodeType() == Node.ELEMENT_NODE){
                boolean isMatch = false;
                for(int j = 0; j < tagNames.length; j++){
                    if(((Element)currentChild).getTagName()
                        .equals(tagNames[j])){
                        isMatch = true;
                        break;
                    }
                }
                if(!isMatch){
                    result.add(currentChild);
                }
            }
        }
        return result.iterator();
    }
    
    /**
     * 指定した要素から、任意の名前の唯一必須の子要素を取得する。<p>
     * 子要素が複数定義してある場合は、例外をthrowする。また、定義されていない場合も例外をthrowする。<br>
     *
     * @param element 要素
     * @return 任意の唯一の子要素
     * @exception DeploymentException 子要素が複数定義してある、または定義されていない場合
     */
    public static Element getUniqueChild(Element element)
     throws DeploymentException{
        Element result = null;
        final NodeList children = element.getChildNodes();
        for(int i = 0, max = children.getLength(); i < max; i++){
            final Node currentChild = children.item(i);
            if(currentChild.getNodeType() == Node.ELEMENT_NODE){
                if(result != null){
                    throw new DeploymentException(
                        "Expected only one any tag"
                    );
                }
                result = (Element)currentChild;
            }
        }
        if(result == null){
            throw new DeploymentException(
                "Expected one any tag"
            );
        }
        return result;
    }
    
    /**
     * 指定した要素から、指定した名前の唯一必須の子要素を取得する。<p>
     * 取得したい要素が複数定義してある場合は、例外をthrowする。また、定義されていない場合も例外をthrowする。<br>
     *
     * @param element 要素
     * @param tagName 要素名
     * @return 指定した名前の子要素
     * @exception DeploymentException 取得したい要素が複数定義してある、または定義されていない場合
     */
    public static Element getUniqueChild(Element element, String tagName)
     throws DeploymentException{
        final Iterator children = getChildrenByTagName(element, tagName);
        
        if(children != null && children.hasNext()){
            final Element child = (Element)children.next();
            if(children.hasNext()){
                throw new DeploymentException(
                    "Expected only one " + tagName + " tag"
                );
            }
            return child;
        }else{
            throw new DeploymentException(
                "Expected one " + tagName + " tag"
            );
        }
    }
    
    /**
     * 指定した要素から、任意の名前の唯一の子要素を取得する。<p>
     * 子要素が複数定義してある場合は、例外をthrowする。<br>
     *
     * @param element 要素
     * @return 任意の唯一の子要素
     * @exception DeploymentException 子要素が複数定義してある場合
     */
    public static Element getOptionalChild(Element element)
     throws DeploymentException{
        Element result = null;
        final NodeList children = element.getChildNodes();
        for(int i = 0, max = children.getLength(); i < max; i++){
            final Node currentChild = children.item(i);
            if(currentChild.getNodeType() == Node.ELEMENT_NODE){
                if(result != null){
                    throw new DeploymentException(
                        "Expected only one any tag"
                    );
                }
                result = (Element)currentChild;
            }
        }
        return result;
    }
    
    /**
     * 指定した要素から、指定した名前の唯一の子要素を取得する。<p>
     * 取得したい要素が複数定義してある場合は、例外をthrowする。<br>
     * 指定した名前の要素が定義されていない場合は、nullを返す。<br>
     * {@link #getOptionalChild(Element, String, Element)}を、getOptionalChild(element, tagName, null)で呼び出すのと等価である。<br>
     *
     * @param element 要素
     * @param tagName 要素名
     * @return 指定した名前の子要素
     * @exception DeploymentException 取得したい要素が複数定義してある場合
     * @see #getOptionalChild(Element, String, Element)
     */
    public static Element getOptionalChild(Element element, String tagName)
     throws DeploymentException{
        return getOptionalChild(element, tagName, null);
    }
    
    /**
     * 指定した要素から、指定した名前の唯一の子要素を取得する。<p>
     * 取得したい要素が複数定義してある場合は、例外をthrowする。<br>
     * 指定した名前の要素が定義されていない場合は、引数で指定されたdefaultElementを返す。<br>
     *
     * @param element 要素
     * @param tagName 要素名
     * @param defaultElement デフォルト値
     * @return 指定した名前の子要素
     * @exception DeploymentException 取得したい要素が複数定義してある場合
     */
    public static Element getOptionalChild(
        Element element,
        String tagName,
        Element defaultElement
    ) throws DeploymentException{
        final Iterator children = getChildrenByTagName(element, tagName);
        
        if(children != null && children.hasNext()){
            final Element child = (Element)children.next();
            if(children.hasNext()){
                throw new DeploymentException(
                    "Expected only one " + tagName + " tag"
                );
            }
            return child;
        }else{
            return defaultElement;
        }
    }
    
    /**
     * 指定した要素の内容を取得する。<p>
     * 内容が空の場合は、空文字を返す。<br>
     * {@link #getElementContent(Element, String)}を、getElementContent(element,  null)で呼び出すのと等価である。<br>
     *
     * @param element 要素
     * @return 要素の内容
     * @see #getElementContent(Element, String)
     */
    public static String getElementContent(Element element){
        return getElementContent(element, null);
    }
    
    /**
     * 指定した要素の内容を取得する。<p>
     * 内容が空の場合は、引数で指定されたdefaultStrを返す。<br>
     *
     * @param element 要素
     * @param defaultStr デフォルト値
     * @return 要素の内容
     */
    public static String getElementContent(Element element, String defaultStr){
        return getElementContent(element, false, defaultStr);
    }
    
    /**
     * 指定した要素の内容を取得する。<p>
     * 内容が空の場合は、引数で指定されたdefaultStrを返す。<br>
     *
     * @param element 要素
     * @param isComment コメントされた要素の内容を取得する場合、true
     * @param defaultStr デフォルト値
     * @return 要素の内容
     */
    public static String getElementContent(Element element, boolean isComment, String defaultStr){
        if(element == null){
            return defaultStr;
        }
        
        final NodeList children = element.getChildNodes();
        if(children.getLength() == 0){
            return defaultStr;
        }
        String result = "";
        for(int i = 0, max = children.getLength(); i < max; i++){
            Node currentNode = children.item(i);
            if(isComment){
                switch(currentNode.getNodeType()){
                case Node.COMMENT_NODE:
                    result += currentNode.getNodeValue();
                    break;
                default:
                }
            }else{
                switch(currentNode.getNodeType()){
                case Node.COMMENT_NODE:
                    // コメントは無視
                    break;
                case Node.TEXT_NODE:
                case Node.CDATA_SECTION_NODE:
                    result += currentNode.getNodeValue();
                    break;
                default:
                    result += currentNode.getFirstChild();
                }
            }
        }
        return trim(result);
    }
    
    /**
     * 指定した要素のコメントを取得する。<p>
     * コメントが存在しない場合は、nullを返す。<br>
     *
     * @param element 要素
     * @return 要素のコメント
     */
    public static String getElementComment(Element element){
        Node currentNode = element;
        boolean isComment = false;
        while((currentNode = currentNode.getPreviousSibling()) != null){
            switch(currentNode.getNodeType()){
            case Node.TEXT_NODE:
            case Node.CDATA_SECTION_NODE:
                continue;
            case Node.COMMENT_NODE:
                isComment = true;
                break;
            default:
                return null;
            }
            if(isComment){
                break;
            }
        }
        return currentNode == null ? null : trim(currentNode.getNodeValue());
    }
    
    public static String trim(String str){
        final StringBuilder buf = new StringBuilder();
        int line = 0;
        boolean isFirst = true;
        for(int i = 0, max = str.length(); i < max; i++){
            char c = str.charAt(i);
            switch(c){
            case '\r':
                if(line != 0 || !isFirst){
                    buf.append(c);
                }
                if(i != max - 1 && str.charAt(i + 1) == '\n'){
                    if(line != 0 || !isFirst){
                        buf.append('\n');
                    }
                    i++;
                }
                isFirst = true;
                line++;
                break;
            case '\n':
                if(line != 0 || !isFirst){
                    buf.append(c);
                }
                line++;
                isFirst = true;
                break;
            case '\t':
                if(!isFirst){
                    buf.append(c);
                }
                break;
            case ' ':
                if(!isFirst){
                    buf.append(c);
                }
                break;
            default:
                isFirst = false;
                buf.append(c);
            }
        }
        for(int i = buf.length(); --i >= 0;){
           char c = buf.charAt(i);
           if(Character.isWhitespace(c)){
               buf.deleteCharAt(i);
           }else{
               break;
           }
        }
        return buf.toString();
    }
    
    /**
     * 指定した要素から、指定した名前の唯一必須の子要素を取得し、その内容を取得する。<p>
     * 取得したい要素が複数定義してある場合は、例外をthrowする。また、定義されていない場合も例外をthrowする。<br>
     * {@link #getUniqueChild(Element, String)}で取得したElementを引数にして、{@link #getElementContent(Element)}を呼び出すのと等価である。<br>
     *
     * @param element 要素
     * @param tagName 要素名
     * @return 指定した名前の子要素の内容
     * @exception DeploymentException 取得したい要素が複数定義してある、または定義されていない場合
     * @see #getUniqueChild(Element, String)
     * @see #getElementContent(Element)
     */
    public static String getUniqueChildContent(Element element, String tagName)
     throws DeploymentException{
        return getElementContent(getUniqueChild(element, tagName));
    }
    
    /**
     * 指定した要素から、指定した名前の唯一の子要素を取得し、その内容を取得する。<p>
     * 取得したい要素が複数定義してある場合は、例外をthrowする。<br>
     * {@link #getOptionalChild(Element, String)}で取得したElementを引数にして、{@link #getElementContent(Element)}を呼び出すのと等価である。<br>
     *
     * @param element 要素
     * @param tagName 要素名
     * @return 指定した名前の子要素の内容
     * @exception DeploymentException 取得したい要素が複数定義してある場合
     * @see #getOptionalChild(Element, String)
     * @see #getElementContent(Element)
     */
    public static String getOptionalChildContent(
        Element element,
        String tagName
    ) throws DeploymentException{
        return getElementContent(getOptionalChild(element, tagName));
    }
    
    /**
     * 指定した要素から、指定した名前の唯一の子要素を取得し、その内容をboolean値として取得する。<p>
     * 取得したい要素が複数定義してある場合は、例外をthrowする。<br>
     * 内容の文字列が、"true"または"yes"（大文字小文字無視）の場合、trueとなる。<br>
     *
     * @param element 要素
     * @param name 要素名
     * @return 指定した名前の子要素の内容をbooleanに変換した値
     * @exception DeploymentException 取得したい要素が複数定義してある場合
     */
     public static boolean getOptionalChildBooleanContent(
        Element element,
        String name
    ) throws DeploymentException{
        final Element child = getOptionalChild(element, name);
        if(child != null){
            final String value = getElementContent(child).toLowerCase();
            return Boolean.valueOf(value).booleanValue()
                 || value.equalsIgnoreCase("yes");
        }
        
        return false;
    }
    
    /**
     * 指定した要素から、指定した名前の必須属性の値を取得する。<p>
     * 指定した属性名の属性が存在しない場合、例外をthrowする。<br>
     *
     * @param element 要素
     * @param name 属性名
     * @return 属性の値
     * @exception DeploymentException 取得したい属性が存在しない場合
     */
    public static String getUniqueAttribute(Element element, String name)
     throws DeploymentException{
        
        if(element.hasAttribute(name)){
            return element.getAttribute(name);
        }else{
            throw new DeploymentException(
                name + " attribute is require."
            );
        }
    }
    
    /**
     * 指定した要素から、指定した名前の属性の値を取得する。<p>
     * 指定した属性名の属性が存在しない場合は、nullを返す。<br>
     * {@link #getOptionalAttribute(Element, String, String)}を、getOptionalAttribute(element, name, null)で呼び出すのと等価である。<br>
     *
     * @param element 要素
     * @param name 属性名
     * @return 属性の値
     * @see #getOptionalAttribute(Element, String, String)
     */
    public static String getOptionalAttribute(Element element, String name){
        return getOptionalAttribute(element, name, null);
    }
    
    /**
     * 指定した要素から、指定した名前の属性の値を取得する。<p>
     * 指定した属性名の属性が存在しない場合は、引数で指定されたdefaultStrを返す。<br>
     *
     * @param element 要素
     * @param name 属性名
     * @param defaultStr デフォルト値
     * @return 属性の値
     */
    public static String getOptionalAttribute(
        Element element,
        String name,
        String defaultStr
    ){
        
        if(element.hasAttribute(name)){
            return element.getAttribute(name);
        }else{
            return defaultStr;
        }
    }
    
    /**
     * 指定した要素から、指定した名前の属性の値を取得し、その内容をboolean値として取得する。<p>
     * 取得したい要素が複数定義してある場合は、例外をthrowする。<br>
     * 内容の文字列が、"true"または"yes"（大文字小文字無視）の場合、trueとなる。<br>
     * 指定した属性名の属性が存在しない場合は、falseを返す。<br>
     *
     * @param element 要素
     * @param name 属性名
     * @return 属性の値
     * @see #getOptionalAttribute(Element, String, String)
     */
    public static boolean getOptionalBooleanAttribute(
        Element element,
        String name
    ){
        final String value = getOptionalAttribute(element, name, null);
        if(value != null){
            return Boolean.valueOf(value).booleanValue()
                 || value.equalsIgnoreCase("yes");
        }
        return false;
    }
    
    /**
     * 指定した要素から、指定した名前の属性の値を取得し、その内容をboolean値として取得する。<p>
     * 取得したい要素が複数定義してある場合は、例外をthrowする。<br>
     * 内容の文字列が、"true"または"yes"（大文字小文字無視）の場合、trueとなる。<br>
     * 指定した属性名の属性が存在しない場合は、引数で指定されたdefaultValを返す。<br>
     *
     * @param element 要素
     * @param name 属性名
     * @param defaultVal デフォルト値
     * @return 属性の値
     * @see #getOptionalAttribute(Element, String, String)
     */
    public static boolean getOptionalBooleanAttribute(
        Element element,
        String name,
        boolean defaultVal
    ){
        final String value = getOptionalAttribute(element, name, null);
        if(value != null){
            return Boolean.valueOf(value).booleanValue()
                 || value.equalsIgnoreCase("yes");
        }
        return defaultVal;
    }
    
    /**
     * このインスタンスの複製を生成する。<p>
     *
     * @return このインスタンスの複製
     */
    public Object clone(){
        Object clone = null;
        try{
            clone = super.clone();
        }catch(CloneNotSupportedException ignore){
        }
        return clone;
    }
}
