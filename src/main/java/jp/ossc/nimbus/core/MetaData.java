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
 * �T�[�r�X��`���^�f�[�^�B<p>
 * �T�[�r�X��`�̊e�v�f�̃��^�f�[�^�̊��N���X�ł���B<br>
 * �T�[�r�X��`�̊e�v�f�̊�{�I�@�\�ƁAXML���p�[�X���郆�[�e�B���e�B���\�b�h�����B<br>
 * 
 * @author M.Takata
 */
public abstract class MetaData implements Serializable, Cloneable{
    
    private static final long serialVersionUID = -5571905040948580821L;
    
    protected static final String LINE_SEPARATOR
         = System.getProperty("line.separator");
    private static final String INDENT_STRING = "    ";
    
    /**
     * ���̃��^�f�[�^�̐e�v�f�ƂȂ郁�^�f�[�^�B<p>
     *
     * @see #getParent()
     */
    private MetaData parent;
    
    private String comment;
    
    private IfDefMetaData ifdefData;
    
    /**
     * �e�v�f�������Ȃ����^�f�[�^�𐶐�����B<p>
     */
    public MetaData(){
    }
    
    /**
     * �e�v�f�������^�f�[�^�𐶐�����B<p>
     * 
     * @param parent �e�v�f�̃��^�f�[�^
     */
    public MetaData(MetaData parent){
        this.parent = parent;
    }
    
    /**
     * �e�v�f�̃��^�f�[�^���擾����B<p>
     * �e�v�f�������Ȃ��ꍇ�́Anull��Ԃ��B
     * 
     * @return �e�v�f�̃��^�f�[�^
     */
    public MetaData getParent(){
        return parent;
    }
    
    /**
     * �e�v�f�̃��^�f�[�^��ݒ肷��B<p>
     * 
     * @param parent �e�v�f�̃��^�f�[�^
     */
    public void setParent(MetaData parent){
        this.parent = parent;
    }
    
    /**
     * ���̗v�f�ɑ΂���R�����g��ݒ肷��B<p>
     *
     * @param comment �R�����g
     */
    public void setComment(String comment){
        this.comment = comment;
    }
    
    /**
     * ���̗v�f�ɑ΂���R�����g���擾����B<p>
     *
     * @return �R�����g
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
     * ���̃��^�f�[�^���\���v�f��Element���p�[�X���āA�������g�̏������A�y�юq�v�f�̃��^�f�[�^�̐������s���B<p>
     *
     * @param element ���̃��^�f�[�^���\���v�f��Element
     * @exception DeploymentException �v�f�̉�́A���̌��ʂɂ�郁�^�f�[�^�̐����Ɏ��s�����ꍇ
     */
    public void importXML(Element element) throws DeploymentException{
        comment = getElementComment(element);
    }
    
    /**
     * ���̃��^�f�[�^���\���v�f��XML�`���ŏo�͂���B<p>
     *
     * @return XML�`��������
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
     * �w�肳�ꂽ������o�b�t�@�Ɋi�[����Ă��镶�����1�C���f���g����������B<p>
     *
     * @param buf ������o�b�t�@
     * @return ������o�b�t�@
     */
    protected StringBuilder addIndent(StringBuilder buf){
        return setIndent(buf, 1);
    }
    
    /**
     * �w�肳�ꂽ�������1�C���f���g����������B<p>
     *
     * @param str ������
     * @return ������
     */
    protected String addIndent(String str){
        return setIndent(str, 1);
    }
    
    /**
     * �w�肳�ꂽ������o�b�t�@�Ɋi�[����Ă��镶������w��C���f���g����������B<p>
     *
     * @param buf ������o�b�t�@
     * @param indent �C���f���g��
     * @return ������o�b�t�@
     */
    protected StringBuilder setIndent(StringBuilder buf, int indent){
        final String str = buf.toString();
        buf.setLength(0);
        return buf.append(setIndent(str, indent));
    }
    
    /**
     * �w�肳�ꂽ��������w��C���f���g����������B<p>
     *
     * @param str ������
     * @param indent �C���f���g��
     * @return ������
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
            // �N���Ȃ��͂�
            e.printStackTrace();
        }finally{
            try{
                br.close();
            }catch(IOException e){
                // �N���Ȃ��͂�
                e.printStackTrace();
            }
            sr.close();
        }
        return buf.toString();
    }
    
    /**
     * �w�肵���v�f����A�q�v�f�̌J��Ԃ����������锽���q���擾����B<p>
     * �����Ŏw�肳�ꂽ�����Ώۂ�element��null�̏ꍇ�́Anull��Ԃ��B�܂��A�q�v�f���A������Ȃ��ꍇ�́A�J��Ԃ��v�f�������Ȃ������q��Ԃ��B<br>
     *
     * @param element �v�f
     * @return �q�v�f�̌J��Ԃ����������锽���q
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
     * �w�肵���v�f����A�w�肵�����O�̎q�v�f�̌J��Ԃ����������锽���q���擾����B<p>
     * �����Ŏw�肳�ꂽ�����Ώۂ�element��null�̏ꍇ�́Anull��Ԃ��B�܂��A�w�肳�ꂽ��������v�f��tagName���A������Ȃ��ꍇ�́A�J��Ԃ��v�f�������Ȃ������q��Ԃ��B<br>
     *
     * @param element �v�f
     * @param tagName �v�f��
     * @return �w�肵�����O�̎q�v�f�̌J��Ԃ����������锽���q
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
     * �w�肵���v�f����A�w�肵�����O�ȊO�̎q�v�f�̌J��Ԃ����������锽���q���擾����B<p>
     * �����Ŏw�肳�ꂽ�����Ώۂ�element��null�̏ꍇ�́Anull��Ԃ��B�܂��A�w�肳�ꂽ�v�f��tagName�ȊO�̗v�f���A������Ȃ��ꍇ�́A�J��Ԃ��v�f�������Ȃ������q��Ԃ��B<br>
     *
     * @param element �v�f
     * @param tagNames ���O����v�f��
     * @return �w�肵�����O�ȊO�̎q�v�f�̌J��Ԃ����������锽���q
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
     * �w�肵���v�f����A�C�ӂ̖��O�̗B��K�{�̎q�v�f���擾����B<p>
     * �q�v�f��������`���Ă���ꍇ�́A��O��throw����B�܂��A��`����Ă��Ȃ��ꍇ����O��throw����B<br>
     *
     * @param element �v�f
     * @return �C�ӂ̗B��̎q�v�f
     * @exception DeploymentException �q�v�f��������`���Ă���A�܂��͒�`����Ă��Ȃ��ꍇ
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
     * �w�肵���v�f����A�w�肵�����O�̗B��K�{�̎q�v�f���擾����B<p>
     * �擾�������v�f��������`���Ă���ꍇ�́A��O��throw����B�܂��A��`����Ă��Ȃ��ꍇ����O��throw����B<br>
     *
     * @param element �v�f
     * @param tagName �v�f��
     * @return �w�肵�����O�̎q�v�f
     * @exception DeploymentException �擾�������v�f��������`���Ă���A�܂��͒�`����Ă��Ȃ��ꍇ
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
     * �w�肵���v�f����A�C�ӂ̖��O�̗B��̎q�v�f���擾����B<p>
     * �q�v�f��������`���Ă���ꍇ�́A��O��throw����B<br>
     *
     * @param element �v�f
     * @return �C�ӂ̗B��̎q�v�f
     * @exception DeploymentException �q�v�f��������`���Ă���ꍇ
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
     * �w�肵���v�f����A�w�肵�����O�̗B��̎q�v�f���擾����B<p>
     * �擾�������v�f��������`���Ă���ꍇ�́A��O��throw����B<br>
     * �w�肵�����O�̗v�f����`����Ă��Ȃ��ꍇ�́Anull��Ԃ��B<br>
     * {@link #getOptionalChild(Element, String, Element)}���AgetOptionalChild(element, tagName, null)�ŌĂяo���̂Ɠ����ł���B<br>
     *
     * @param element �v�f
     * @param tagName �v�f��
     * @return �w�肵�����O�̎q�v�f
     * @exception DeploymentException �擾�������v�f��������`���Ă���ꍇ
     * @see #getOptionalChild(Element, String, Element)
     */
    public static Element getOptionalChild(Element element, String tagName)
     throws DeploymentException{
        return getOptionalChild(element, tagName, null);
    }
    
    /**
     * �w�肵���v�f����A�w�肵�����O�̗B��̎q�v�f���擾����B<p>
     * �擾�������v�f��������`���Ă���ꍇ�́A��O��throw����B<br>
     * �w�肵�����O�̗v�f����`����Ă��Ȃ��ꍇ�́A�����Ŏw�肳�ꂽdefaultElement��Ԃ��B<br>
     *
     * @param element �v�f
     * @param tagName �v�f��
     * @param defaultElement �f�t�H���g�l
     * @return �w�肵�����O�̎q�v�f
     * @exception DeploymentException �擾�������v�f��������`���Ă���ꍇ
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
     * �w�肵���v�f�̓��e���擾����B<p>
     * ���e����̏ꍇ�́A�󕶎���Ԃ��B<br>
     * {@link #getElementContent(Element, String)}���AgetElementContent(element,  null)�ŌĂяo���̂Ɠ����ł���B<br>
     *
     * @param element �v�f
     * @return �v�f�̓��e
     * @see #getElementContent(Element, String)
     */
    public static String getElementContent(Element element){
        return getElementContent(element, null);
    }
    
    /**
     * �w�肵���v�f�̓��e���擾����B<p>
     * ���e����̏ꍇ�́A�����Ŏw�肳�ꂽdefaultStr��Ԃ��B<br>
     *
     * @param element �v�f
     * @param defaultStr �f�t�H���g�l
     * @return �v�f�̓��e
     */
    public static String getElementContent(Element element, String defaultStr){
        return getElementContent(element, false, defaultStr);
    }
    
    /**
     * �w�肵���v�f�̓��e���擾����B<p>
     * ���e����̏ꍇ�́A�����Ŏw�肳�ꂽdefaultStr��Ԃ��B<br>
     *
     * @param element �v�f
     * @param isComment �R�����g���ꂽ�v�f�̓��e���擾����ꍇ�Atrue
     * @param defaultStr �f�t�H���g�l
     * @return �v�f�̓��e
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
                    // �R�����g�͖���
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
     * �w�肵���v�f�̃R�����g���擾����B<p>
     * �R�����g�����݂��Ȃ��ꍇ�́Anull��Ԃ��B<br>
     *
     * @param element �v�f
     * @return �v�f�̃R�����g
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
     * �w�肵���v�f����A�w�肵�����O�̗B��K�{�̎q�v�f���擾���A���̓��e���擾����B<p>
     * �擾�������v�f��������`���Ă���ꍇ�́A��O��throw����B�܂��A��`����Ă��Ȃ��ꍇ����O��throw����B<br>
     * {@link #getUniqueChild(Element, String)}�Ŏ擾����Element�������ɂ��āA{@link #getElementContent(Element)}���Ăяo���̂Ɠ����ł���B<br>
     *
     * @param element �v�f
     * @param tagName �v�f��
     * @return �w�肵�����O�̎q�v�f�̓��e
     * @exception DeploymentException �擾�������v�f��������`���Ă���A�܂��͒�`����Ă��Ȃ��ꍇ
     * @see #getUniqueChild(Element, String)
     * @see #getElementContent(Element)
     */
    public static String getUniqueChildContent(Element element, String tagName)
     throws DeploymentException{
        return getElementContent(getUniqueChild(element, tagName));
    }
    
    /**
     * �w�肵���v�f����A�w�肵�����O�̗B��̎q�v�f���擾���A���̓��e���擾����B<p>
     * �擾�������v�f��������`���Ă���ꍇ�́A��O��throw����B<br>
     * {@link #getOptionalChild(Element, String)}�Ŏ擾����Element�������ɂ��āA{@link #getElementContent(Element)}���Ăяo���̂Ɠ����ł���B<br>
     *
     * @param element �v�f
     * @param tagName �v�f��
     * @return �w�肵�����O�̎q�v�f�̓��e
     * @exception DeploymentException �擾�������v�f��������`���Ă���ꍇ
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
     * �w�肵���v�f����A�w�肵�����O�̗B��̎q�v�f���擾���A���̓��e��boolean�l�Ƃ��Ď擾����B<p>
     * �擾�������v�f��������`���Ă���ꍇ�́A��O��throw����B<br>
     * ���e�̕����񂪁A"true"�܂���"yes"�i�啶�������������j�̏ꍇ�Atrue�ƂȂ�B<br>
     *
     * @param element �v�f
     * @param name �v�f��
     * @return �w�肵�����O�̎q�v�f�̓��e��boolean�ɕϊ������l
     * @exception DeploymentException �擾�������v�f��������`���Ă���ꍇ
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
     * �w�肵���v�f����A�w�肵�����O�̕K�{�����̒l���擾����B<p>
     * �w�肵���������̑��������݂��Ȃ��ꍇ�A��O��throw����B<br>
     *
     * @param element �v�f
     * @param name ������
     * @return �����̒l
     * @exception DeploymentException �擾���������������݂��Ȃ��ꍇ
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
     * �w�肵���v�f����A�w�肵�����O�̑����̒l���擾����B<p>
     * �w�肵���������̑��������݂��Ȃ��ꍇ�́Anull��Ԃ��B<br>
     * {@link #getOptionalAttribute(Element, String, String)}���AgetOptionalAttribute(element, name, null)�ŌĂяo���̂Ɠ����ł���B<br>
     *
     * @param element �v�f
     * @param name ������
     * @return �����̒l
     * @see #getOptionalAttribute(Element, String, String)
     */
    public static String getOptionalAttribute(Element element, String name){
        return getOptionalAttribute(element, name, null);
    }
    
    /**
     * �w�肵���v�f����A�w�肵�����O�̑����̒l���擾����B<p>
     * �w�肵���������̑��������݂��Ȃ��ꍇ�́A�����Ŏw�肳�ꂽdefaultStr��Ԃ��B<br>
     *
     * @param element �v�f
     * @param name ������
     * @param defaultStr �f�t�H���g�l
     * @return �����̒l
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
     * �w�肵���v�f����A�w�肵�����O�̑����̒l���擾���A���̓��e��boolean�l�Ƃ��Ď擾����B<p>
     * �擾�������v�f��������`���Ă���ꍇ�́A��O��throw����B<br>
     * ���e�̕����񂪁A"true"�܂���"yes"�i�啶�������������j�̏ꍇ�Atrue�ƂȂ�B<br>
     * �w�肵���������̑��������݂��Ȃ��ꍇ�́Afalse��Ԃ��B<br>
     *
     * @param element �v�f
     * @param name ������
     * @return �����̒l
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
     * �w�肵���v�f����A�w�肵�����O�̑����̒l���擾���A���̓��e��boolean�l�Ƃ��Ď擾����B<p>
     * �擾�������v�f��������`���Ă���ꍇ�́A��O��throw����B<br>
     * ���e�̕����񂪁A"true"�܂���"yes"�i�啶�������������j�̏ꍇ�Atrue�ƂȂ�B<br>
     * �w�肵���������̑��������݂��Ȃ��ꍇ�́A�����Ŏw�肳�ꂽdefaultVal��Ԃ��B<br>
     *
     * @param element �v�f
     * @param name ������
     * @param defaultVal �f�t�H���g�l
     * @return �����̒l
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
     * ���̃C���X�^���X�̕����𐶐�����B<p>
     *
     * @return ���̃C���X�^���X�̕���
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
