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
package jp.ossc.nimbus.util.converter;

/**
 * ������p�f�B���O�R���o�[�^�B<p>
 * 
 * @author M.Takata
 */
public class PaddingStringConverter
 implements StringConverter, PaddingConverter, java.io.Serializable{
    
    private static final long serialVersionUID = -3962004369893317399L;
    
    /**
     * �f�t�H���g�̃p�f�B���O�����B<p>
     * ���p�X�y�[�X�B<br>
     */
    public static final char DEFAULT_PADDING_LITERAL = ' ';
    
    /**
     * �f�t�H���g�̃p�f�B���O�����B<p>
     * {@link #DIRECTION_LEFT ���l��}�B<br>
     */
    public static final int DEFAULT_PADDING_DIRECTION = DIRECTION_LEFT;
    
    /**
     * �ϊ���ʁB<p>
     */
    protected int convertType;
    
    /**
     * �p�f�B���O�����B<p>
     */
    protected char paddingLiteral = DEFAULT_PADDING_LITERAL;
    
    /**
     * �p�f�B���O���B<p>
     */
    protected int paddingLength = -1;
    
    /**
     * �p�f�B���O�����B<p>
     */
    protected int paddingDirection = DEFAULT_PADDING_DIRECTION;
    
    /**
     * �S�p�����𒷂�2�Ɛ����邩�̃t���O�B<p>
     */
    protected boolean isCountTwiceByZenkaku;
    
    /**
     * �p�f�B���O���s��������p�f�B���O�R���o�[�^�𐶐�����B<p>
     */
    public PaddingStringConverter(){
        this(PADDING, DEFAULT_PADDING_LITERAL, -1, DEFAULT_PADDING_DIRECTION);
    }
    
    /**
     * �w�肳�ꂽ�p�f�B���O�ϊ����s��������p�f�B���O�R���o�[�^�𐶐�����B<p>
     *
     * @param length �p�f�B���O��
     * @see #PADDING
     */
    public PaddingStringConverter(int length){
        this(PADDING, DEFAULT_PADDING_LITERAL, length, DEFAULT_PADDING_DIRECTION);
    }
    
    /**
     * �w�肳�ꂽ�p�f�B���O�ϊ����s��������p�f�B���O�R���o�[�^�𐶐�����B<p>
     *
     * @param length �p�f�B���O��
     * @param literal �p�f�B���O����
     * @param direction �p�f�B���O����
     * @see #PADDING
     */
    public PaddingStringConverter(
        int length,
        char literal,
        int direction
    ){
        this(PADDING, literal, length, direction);
    }
    
    /**
     * �w�肳�ꂽ�p�[�X�ϊ����s��������p�f�B���O�R���o�[�^�𐶐�����B<p>
     *
     * @param literal �p�f�B���O����
     * @param direction �p�f�B���O����
     * @see #PARSE
     */
    public PaddingStringConverter(
        char literal,
        int direction
    ){
        this(PARSE, literal, -1, direction);
    }
    
    /**
     * �w�肳�ꂽ�ϊ���ʂ̕�����p�f�B���O�R���o�[�^�𐶐�����B<p>
     *
     * @param type �ϊ����
     * @param literal �p�f�B���O����
     * @param length �p�f�B���O��
     * @param direction �p�f�B���O����
     * @see #PADDING
     * @see #PARSE
     */
    public PaddingStringConverter(
        int type,
        char literal,
        int length,
        int direction
    ){
        setConvertType(type);
        setPaddingLiteral(literal);
        setPaddingLength(length);
        setPaddingDirection(direction);
    }
    
    // ReversibleConverter��JavaDoc
    public void setConvertType(int type){
        switch(type){
        case PADDING:
        case PARSE:
            convertType = type;
            break;
        default:
            throw new IllegalArgumentException(
                "Invalid convert type : " + type
            );
        }
    }
    
    /**
     * �ϊ���ʂ��擾����B<p>
     *
     * @return �ϊ����
     * @see #setConvertType(int)
     */
    public int getConvertType(){
        return convertType;
    }
    
    // PaddingConverter��JavaDoc
    public void setPaddingLiteral(char literal){
        paddingLiteral = literal;
    }
    
    /**
     * �p�f�B���O�������擾����B<p>
     *
     * @return �p�f�B���O����
     */
    public char getPaddingLiteral(){
        return paddingLiteral;
    }
    
    // PaddingConverter��JavaDoc
    public void setPaddingLength(int length){
        paddingLength = length;
    }
    
    /**
     * �p�f�B���O�����擾����B<p>
     *
     * @return �p�f�B���O��
     */
    public int getPaddingLength(){
        return paddingLength;
    }
    
    // PaddingConverter��JavaDoc
    public void setPaddingDirection(int direct){
        switch(direct){
        case DIRECTION_LEFT:
        case DIRECTION_RIGHT:
        case DIRECTION_CENTER:
            paddingDirection = direct;
            break;
        default:
            throw new IllegalArgumentException(
                "Invalid padding direction : " + direct
            );
        }
    }
    
    /**
     * �p�f�B���O�������擾����B<p>
     *
     * @return �p�f�B���O����
     */
    public int getPaddingDirection(){
        return paddingDirection;
    }
    
    /**
     * �S�p�����𒷂�2�Ɛ����邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�S�p�����𒷂�2�Ɛ�����
     */
    public boolean isCountTwiceByZenkaku(){
        return isCountTwiceByZenkaku;
    }
    
    /**
     * �S�p�����𒷂�2�Ɛ����邩�ǂ�����ݒ肷��B<p>
     *
     * @param isTwice �S�p�����𒷂�2�Ɛ�����ꍇtrue
     */
    public void setCountTwiceByZenkaku(boolean isTwice){
        isCountTwiceByZenkaku = isTwice;
    }
    
    // Converter��JavaDoc
    public Object convert(Object obj) throws ConvertException{
        return convert(
            obj == null ? (String)null : 
                (String)(obj instanceof String ? obj : String.valueOf(obj))
        );
    }
    
    /**
     * �������ϊ�����B<p>
     * �ϊ�������z��ƕϊ��L�����N�^�z����g���ĕϊ�����B<br>
     *
     * @param str �ϊ��Ώۂ̕����� 
     * @return �ϊ���̕�����
     * @exception ConvertException �ϊ��Ɏ��s�����ꍇ
     */
    public String convert(String str) throws ConvertException{
        switch(convertType){
        case PARSE:
            if(str == null || str.length() == 0
                || str.indexOf(paddingLiteral) == -1){
                return str;
            }
            return parse(str);
        case PADDING:
        default:
            return padding(str);
        }
    }
    
    protected int countLength(CharSequence str){
        if(isCountTwiceByZenkaku){
            int length = 0;
            for(int i = 0, imax = str.length(); i < imax; i++){
                char c = str.charAt(i);
                if(c <= '\u007F' || (0xFF61 <= c && c <= 0xFF9F)){
                    length++;
                }else{
                    length+=2;
                }
            }
            return length;
        }else{
            return str.length();
        }
    }
    
    /**
     * �w�肳�ꂽ��������p�f�B���O����B<p>
     *
     * @param str ������
     * @return �p�f�B���O���ꂽ������
     * @exception ConvertException �p�f�B���O�Ɏ��s�����ꍇ
     */
    public String padding(String str) throws ConvertException{
        if(paddingLength <= 0
             || (str != null && countLength(str) >= paddingLength)){
            return str;
        }
        final StringBuffer buf = new StringBuffer();
        if(str != null){
            buf.append(str);
        }
        switch(paddingDirection){
        case DIRECTION_CENTER:
            return paddingCenter(buf).toString();
        case DIRECTION_RIGHT:
            return paddingRight(buf).toString();
        case DIRECTION_LEFT:
        default:
            return paddingLeft(buf).toString();
        }
    }
    
    protected StringBuffer paddingCenter(StringBuffer buf)
     throws ConvertException{
        int length = countLength(buf);
        int cnter = (paddingLength - length) / 2;
        for(int i = paddingLength - length; --i >= 0;){
            if(cnter > i){
                buf.append(paddingLiteral);
            }else{
                buf.insert(0, paddingLiteral);
            }
        }
        return buf;
    }
    
    protected StringBuffer paddingRight(StringBuffer buf)
     throws ConvertException{
        int length = countLength(buf);
        for(int i = paddingLength - length; --i >= 0;){
            buf.insert(0, paddingLiteral);
        }
        return buf;
    }
    
    protected StringBuffer paddingLeft(StringBuffer buf)
     throws ConvertException{
        int length = countLength(buf);
        for(int i = paddingLength - length; --i >= 0;){
            buf.append(paddingLiteral);
        }
        return buf;
    }
    
    /**
     * �w�肳�ꂽ��������p�[�X����B<p>
     *
     * @param str ������
     * @return �p�[�X���ꂽ������
     * @exception ConvertException �p�[�X�Ɏ��s�����ꍇ
     */
    public String parse(String str) throws ConvertException{
        final StringBuffer buf = new StringBuffer(str);
        switch(paddingDirection){
        case DIRECTION_CENTER:
            return parseCenter(buf).toString();
        case DIRECTION_RIGHT:
            return parseRight(buf).toString();
        case DIRECTION_LEFT:
        default:
            return parseLeft(buf).toString();
        }
    }
    
    protected StringBuffer parseCenter(StringBuffer buf)
     throws ConvertException{
        int startIndex = -1;
        int length = countLength(buf);
        for(int i = 0, max = length; i < max; i++){
            if(buf.charAt(i) != paddingLiteral){
                startIndex = i;
                break;
            }
        }
        if(startIndex == -1){
            return buf;
        }
        if(startIndex != 0){
            buf.delete(0, startIndex);
        }
        length = countLength(buf);
        if(length <= 1){
            return buf;
        }
        int endIndex = -1;
        for(int i = length ; --i >= 0;){
            if(buf.charAt(i) != paddingLiteral){
                endIndex = i;
                break;
            }
        }
        if(endIndex != length - 1){
            buf.delete(endIndex + 1, length);
        }
        return buf;
    }
    
    protected StringBuffer parseRight(StringBuffer buf)
     throws ConvertException{
        int startIndex = -1;
        int length = countLength(buf);
        for(int i = 0, max = length; i < max; i++){
            if(buf.charAt(i) != paddingLiteral){
                startIndex = i;
                break;
            }
        }
        if(startIndex == -1){
            return buf;
        }
        if(startIndex != 0){
            buf.delete(0, startIndex);
        }
        return buf;
    }
    
    protected StringBuffer parseLeft(StringBuffer buf)
     throws ConvertException{
        int endIndex = -1;
        int length = countLength(buf);
        for(int i = length ; --i >= 0;){
            if(buf.charAt(i) != paddingLiteral){
                endIndex = i;
                break;
            }
        }
        if(endIndex != length - 1){
            buf.delete(endIndex + 1, length);
        }
        return buf;
    }
}
