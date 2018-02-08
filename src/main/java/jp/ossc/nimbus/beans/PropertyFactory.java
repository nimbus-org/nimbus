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

/**
 * �v���p�e�B�t�@�N�g���B<p>
 * �v���p�e�B�����񂩂�A���̃v���p�e�B�ɃA�N�Z�X���邽�߂�{@link Property}�I�u�W�F�N�g�𐶐�����t�@�N�g���B<br>
 * �ȉ��̂悤�ȁABean�̃v���p�e�B�ɑ΂���A�N�Z�X���@���p�ӂ���Ă���B<br>
 * <table border="1">
 *   <tr bgcolor="#CCCCFF"><th>�A�N�Z�X���@</th><th>Java�\��</th><th>�v���p�e�B������\��</th></tr>
 *   <tr><td>�V���v���v���p�e�B{@link SimpleProperty}</td><td>bean.getHoge()<br>map.get("hoge")</td><td>hoge</td></tr>
 *   <tr><td>�l�X�g�v���p�e�B{@link NestedProperty}</td><td>bean.getHoge().getFuga()</td><td>hoge.fuga</td></tr>
 *   <tr><td>�C���f�b�N�X�v���p�e�B{@link IndexedProperty}</td><td>bean.getHoge(1)<br>((List)bean.getHoge()).get(1)<br>((Object[])bean.getHoge())[1]</td><td>hoge[1]</td></tr>
 *   <tr><td>�}�b�v�v���p�e�B{@link MappedProperty}</td><td>bean.getHoge("fuga")</td><td>hoge(fuga)</td></tr>
 *   <tr><td>�A���v���p�e�B{@link ConcatenatedProperty}</td><td>bean.getHoge() + bean.getFuga()</td><td>hoge+fuga</td></tr>
 *   <tr><td>OR�v���p�e�B{@link OrProperty}</td><td>bean.getHoge() != null ?  bean.getHoge() : bean.getFuga()</td><td>hoge|fuga</td></tr>
 * </table>
 * <p>
 * �ȉ��ɃT���v���R�[�h�������B<br>
 * <pre>
 *     import java.util.*;
 *     import jp.ossc.nimbus.beans.*;
 *     
 *     // Map��List���i�[���ꂽ�l�X�g�����\����Bean�𐶐�����
 *     Map map = new HashMap();
 *     List list = new ArrayList();
 *     list.add("a");
 *     list.add("b");
 *     map.put("A", list);
 *     
 *     // �l�X�g���ꂽ�\����Bean���疖�[�̒l�����o��
 *     Property prop = PropertyFactory.createProperty("A[0]");
 *     String val = (String)prop.getProperty(map);
 * </pre>
 *
 * @author M.Takata
 * @see Property
 * @see SimpleProperty
 * @see NestedProperty
 * @see IndexedProperty
 * @see MappedProperty
 * @see ConcatenatedProperty
 * @see OrProperty
 */
public class PropertyFactory implements java.io.Serializable{
    
    private static final long serialVersionUID = 393005154068498255L;
    
    /**
     * �w�肳�ꂽ�v���p�e�B������ŕ\�����ꂽ�v���p�e�B�ɃA�N�Z�X���邽�߂�{@link Property}�I�u�W�F�N�g�𐶐�����B<p>
     *
     * @param prop �v���p�e�B������
     * @return �v���p�e�B������ŕ\�����ꂽ�v���p�e�B�ɃA�N�Z�X���邽�߂�Property�I�u�W�F�N�g
     * @exception �w�肳�ꂽ�v���p�e�B�����񂪕s���ȏꍇ
     */
    public static Property createProperty(String prop)
     throws IllegalArgumentException{
        Property property = null;
        final StringBuffer buf = new StringBuffer();
        boolean isEscape = false;
        boolean isIndexedStart = false;
        boolean isMappedStart = false;
        boolean isGroupStart = false;
        for(int i = 0, max = prop.length(); i < max; i++){
            final char c = prop.charAt(i);
            switch(c){
            case '\\':
                if(isEscape){
                    buf.append(c);
                    isEscape = false;
                }else{
                    if(i == max - 1){
                        buf.append(c);
                    }else{
                        isEscape = true;
                    }
                }
                break;
            case '.':
                if(isEscape || isMappedStart || isIndexedStart){
                    buf.append(c);
                    isEscape = false;
                }else{
                    if(i == max - 1){
                        throw new IllegalArgumentException("'.' must not be last. : " + prop);
                    }
                    if(property == null){
                        if(buf.length() == 0){
                            throw new IllegalArgumentException("Before '.', a literal is required. : " + prop);
                        }
                        property = new NestedProperty();
                        ((NestedProperty)property).setThisProperty(new SimpleProperty(buf.toString()));
                    }else if(buf.length() == 0){
                        Property tmpProperty = property;
                        property = new NestedProperty();
                        ((NestedProperty)property).setThisProperty(tmpProperty);
                    }else{
                        SimpleProperty next = new SimpleProperty(buf.toString());
                        if(property instanceof NestedProperty){
                            ((NestedProperty)property).setNestedProperty(next);
                        }else if(property instanceof OrProperty){
                            ((OrProperty)property).setSecondProperty(next);
                        }else if(property instanceof ConcatenatedProperty){
                            ((ConcatenatedProperty)property).setConcatenatedProperty(next);
                        }
                        Property tmpProperty = property;
                        property = new NestedProperty();
                        ((NestedProperty)property).setThisProperty(tmpProperty);
                    }
                    buf.setLength(0);
                }
                break;
            case '|':
                if(isEscape || isMappedStart || isIndexedStart){
                    buf.append(c);
                    isEscape = false;
                }else{
                    if(i == max - 1){
                        throw new IllegalArgumentException("'|' must not be last. : " + prop);
                    }
                    if(property == null){
                        if(buf.length() == 0){
                            throw new IllegalArgumentException("Before '|', a literal is required. : " + prop);
                        }
                        property = new OrProperty();
                        ((OrProperty)property).setFirstProperty(new SimpleProperty(buf.toString()));
                    }else if(buf.length() == 0){
                        Property tmpProperty = property;
                        property = new OrProperty();
                        ((OrProperty)property).setFirstProperty(tmpProperty);
                    }else{
                        SimpleProperty next = new SimpleProperty(buf.toString());
                        if(property instanceof NestedProperty){
                            ((NestedProperty)property).setNestedProperty(next);
                        }else if(property instanceof OrProperty){
                            ((OrProperty)property).setSecondProperty(next);
                        }else if(property instanceof ConcatenatedProperty){
                            ((ConcatenatedProperty)property).setConcatenatedProperty(next);
                        }
                        Property tmpProperty = property;
                        property = new OrProperty();
                        ((OrProperty)property).setFirstProperty(tmpProperty);
                    }
                    buf.setLength(0);
                }
                break;
            case '+':
                if(isEscape || isMappedStart || isIndexedStart){
                    buf.append(c);
                    isEscape = false;
                }else{
                    if(i == max - 1){
                        throw new IllegalArgumentException("'+' must not be last. : " + prop);
                    }
                    if(property == null){
                        if(buf.length() == 0){
                            throw new IllegalArgumentException("Before '+', a literal is required. : " + prop);
                        }
                        property = new ConcatenatedProperty();
                        ((ConcatenatedProperty)property).setThisProperty(new SimpleProperty(buf.toString()));
                    }else if(buf.length() == 0){
                        Property tmpProperty = property;
                        property = new ConcatenatedProperty();
                        ((ConcatenatedProperty)property).setThisProperty(tmpProperty);
                    }else{
                        SimpleProperty next = new SimpleProperty(buf.toString());
                        if(property instanceof NestedProperty){
                            ((NestedProperty)property).setNestedProperty(next);
                        }else if(property instanceof OrProperty){
                            ((OrProperty)property).setSecondProperty(next);
                        }else if(property instanceof ConcatenatedProperty){
                            ((ConcatenatedProperty)property).setConcatenatedProperty(next);
                        }
                        Property tmpProperty = property;
                        property = new ConcatenatedProperty();
                        ((ConcatenatedProperty)property).setThisProperty(tmpProperty);
                    }
                    buf.setLength(0);
                }
                break;
            case '[':
                buf.append(c);
                if(isEscape || isMappedStart || isIndexedStart){
                    isEscape = false;
                }else{
                    if(i == max - 1){
                        throw new IllegalArgumentException("'[' must not be last. : " + prop);
                    }
                    if(property != null){
                        boolean isNext = false;
                        if(property instanceof NestedProperty){
                            isNext = ((NestedProperty)property).getNestedProperty() != null;
                        }else if(property instanceof OrProperty){
                            isNext = ((OrProperty)property).getSecondProperty() != null;
                        }else if(property instanceof ConcatenatedProperty){
                            isNext = ((ConcatenatedProperty)property).getConcatenatedProperty() != null;
                        }else{
                            isNext = true;
                        }
                        if(isNext){
                            Property tmpProperty = property;
                            property = new NestedProperty();
                            ((NestedProperty)property).setThisProperty(tmpProperty);
                        }
                    }
                    isIndexedStart = true;
                }
                break;
            case ']':
                buf.append(c);
                if(isEscape || isMappedStart){
                    isEscape = false;
                }else{
                    if(!isIndexedStart){
                        throw new IllegalArgumentException("Before ']', '[' is required. : " + prop);
                    }
                    if(buf.length() == 0){
                        throw new IllegalArgumentException("Before ']', a literal is required. : " + prop);
                    }
                    if(property == null){
                        property = new IndexedProperty();
                        property.parse(buf.toString());
                    }else{
                        IndexedProperty next = new IndexedProperty();
                        next.parse(buf.toString());
                        if(property instanceof NestedProperty){
                            ((NestedProperty)property).setNestedProperty(next);
                        }else if(property instanceof OrProperty){
                            ((OrProperty)property).setSecondProperty(next);
                        }else if(property instanceof ConcatenatedProperty){
                            ((ConcatenatedProperty)property).setConcatenatedProperty(next);
                        }
                    }
                    buf.setLength(0);
                    isIndexedStart = false;
                }
                break;
            case '(':
                buf.append(c);
                if(isEscape || isMappedStart || isIndexedStart){
                    isEscape = false;
                }else{
                    if(i == max - 1){
                        throw new IllegalArgumentException("'(' must not be last. : " + prop);
                    }
                    if(property != null){
                        boolean isNext = false;
                        if(property instanceof NestedProperty){
                            isNext = ((NestedProperty)property).getNestedProperty() != null;
                        }else if(property instanceof OrProperty){
                            isNext = ((OrProperty)property).getSecondProperty() != null;
                        }else if(property instanceof ConcatenatedProperty){
                            isNext = ((ConcatenatedProperty)property).getConcatenatedProperty() != null;
                        }else{
                            isNext = true;
                        }
                        if(isNext){
                            Property tmpProperty = property;
                            property = new NestedProperty();
                            ((NestedProperty)property).setThisProperty(tmpProperty);
                        }
                    }
                    isMappedStart = true;
                }
                break;
            case ')':
                buf.append(c);
                if(isEscape || isIndexedStart){
                    isEscape = false;
                }else{
                    if(!isMappedStart){
                        throw new IllegalArgumentException("Before ')', '(' is required. : " + prop);
                    }
                    if(buf.length() == 0){
                        throw new IllegalArgumentException("Before ')', a literal is required. : " + prop);
                    }
                    if(property == null){
                        property = new MappedProperty();
                        property.parse(buf.toString());
                    }else{
                        MappedProperty next = new MappedProperty();
                        next.parse(buf.toString());
                        if(property instanceof NestedProperty){
                            ((NestedProperty)property).setNestedProperty(next);
                        }else if(property instanceof OrProperty){
                            ((OrProperty)property).setSecondProperty(next);
                        }else if(property instanceof ConcatenatedProperty){
                            ((ConcatenatedProperty)property).setConcatenatedProperty(next);
                        }
                    }
                    buf.setLength(0);
                    isMappedStart = false;
                }
                break;
            case '{':
                if(isEscape || isMappedStart || isIndexedStart){
                    buf.append(c);
                    isEscape = false;
                }else{
                    if(i == max - 1){
                        throw new IllegalArgumentException("'{' must not be last. : " + prop);
                    }
                    boolean isEscape2 = false;
                    boolean isIndexedStart2 = false;
                    boolean isMappedStart2 = false;
                    int groupStartCount = 0;
                    boolean isBreak = false;
                    final StringBuffer groupBuf = new StringBuffer();
                    for(; ++i < max;){
                        final char c2 = prop.charAt(i);
                        switch(c2){
                        case '\\':
                            groupBuf.append(c2);
                            if(isEscape2){
                                isEscape2 = false;
                            }else{
                                isEscape2 = true;
                            }
                            break;
                        case '(':
                            groupBuf.append(c2);
                            if(isEscape2 || isMappedStart2 || isIndexedStart2){
                                isEscape2 = false;
                            }else{
                                isMappedStart2 = true;
                            }
                            break;
                        case ')':
                            groupBuf.append(c2);
                            if(isEscape2 || isIndexedStart2){
                                isEscape2 = false;
                            }else{
                                isMappedStart2 = false;
                            }
                            break;
                        case '[':
                            groupBuf.append(c2);
                            if(isEscape2 || isMappedStart2 || isIndexedStart2){
                                isEscape2 = false;
                            }else{
                                isIndexedStart2 = true;
                            }
                            break;
                        case ']':
                            groupBuf.append(c2);
                            if(isEscape2 || isMappedStart2){
                                isEscape2 = false;
                            }else{
                                isIndexedStart2 = false;
                            }
                            break;
                        case '{':
                            groupBuf.append(c2);
                            if(isEscape2 || isMappedStart2 || isIndexedStart2){
                                isEscape2 = false;
                            }else{
                                groupStartCount++;
                            }
                            break;
                        case '}':
                            if(isEscape2 || isMappedStart2 || isIndexedStart2){
                                isEscape2 = false;
                            }else{
                                if(groupStartCount == 0){
                                    isBreak = true;
                                }else{
                                    groupBuf.append(c2);
                                    groupStartCount--;
                                }
                            }
                            break;
                        default:
                            groupBuf.append(c2);
                        }
                        if(isBreak){
                            break;
                        }
                    }
                    if(!isBreak){
                        throw new IllegalArgumentException("'{' must be terminated with '}'. : " + prop);
                    }
                    Property next = createProperty(groupBuf.toString());
                    if(property == null){
                        property = next;
                    }else{
                        if(property instanceof NestedProperty){
                            ((NestedProperty)property).setNestedProperty(next);
                        }else if(property instanceof OrProperty){
                            ((OrProperty)property).setSecondProperty(next);
                        }else if(property instanceof ConcatenatedProperty){
                            ((ConcatenatedProperty)property).setConcatenatedProperty(next);
                        }
                    }
                }
                break;
            default:
                if(isEscape){
                    buf.append('\\');
                    isEscape = false;
                }
                buf.append(c);
                if(i == max - 1){
                    SimpleProperty next = new SimpleProperty(buf.toString());
                    if(property == null){
                        property = next;
                    }else if(property instanceof NestedProperty){
                        ((NestedProperty)property).setNestedProperty(next);
                    }else if(property instanceof OrProperty){
                        ((OrProperty)property).setSecondProperty(next);
                    }else if(property instanceof ConcatenatedProperty){
                        ((ConcatenatedProperty)property).setConcatenatedProperty(next);
                    }
                }
                break;
            }
            if(i == max - 1){
                if(isIndexedStart){
                    throw new IllegalArgumentException("'[' must be terminated with ']'. : " + prop);
                }else if(isMappedStart){
                    throw new IllegalArgumentException("'(' must be terminated with ')'. : " + prop);
                }
            }
        }
        return property;
    }
}
