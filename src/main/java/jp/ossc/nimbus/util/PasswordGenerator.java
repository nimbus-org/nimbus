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
package jp.ossc.nimbus.util;

import java.util.*;

/**
 * �p�X���[�h������B<p>
 *
 * @author M.Takata
 */
public class PasswordGenerator{
    
    /**
     * �A���t�@�x�b�g�������z��B<p>
     */
    public static final char[] LOWERCASE_ALPHABET ={
        'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'
    };
    
    /**
     * �A���t�@�x�b�g�啶���z��B<p>
     */
    public static final char[] UPPPERCASE_ALPHABET ={
        'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
    };
    
    /**
     * �����z��B<p>
     */
    public static final char[] NUMBER ={
        '0','1','2','3','4','5','6','7','8','9'
    };
    
    /**
     * �L���z��B<p>
     */
    public static final char[] SYMBOL ={
        '!','"','#','$','%','&','\'','(',')','*','+',',','-','.','/',':',';','<','=','>','?','@','[','\\',']','^','_','`','{','|','}','~'
    };
    
    private final Random random = new Random();
    private final List elements = new ArrayList();
    
    /**
     * ��̃C���X�^���X�𐶐�����B<p>
     */
    public PasswordGenerator(){
    }
    
    /**
     * �w�肵���p�X���[�h�v�f�����C���X�^���X�𐶐�����B<p>
     *
     * @param elements �p�X���[�h�v�f�̔z��
     */
    public PasswordGenerator(PasswordElement[] elements){
        for(int i = 0; i < elements.length; i++){
            addElement(elements[i]);
        }
    }
    
    /**
     * �w�肵���p�X���[�h�v�f��ݒ肷��B<p>
     *
     * @param elements �p�X���[�h�v�f�̔z��
     */
    public setElements(PasswordElement[] elements){
        elements.clear();
        for(int i = 0; i < elements.length; i++){
            addElement(elements[i]);
        }
    }
    
    /**
     * �w�肵���p�X���[�h�v�f��ǉ�����B<p>
     *
     * @param element �p�X���[�h�v�f
     */
    public void addElement(PasswordElement element){
        elements.add(element);
    }
    
    /**
     * �ݒ肳�ꂽ�p�X���[�h�v�f���N���A����B<p>
     */
    public void clear(){
        elements.clear();
    }
    
    /**
     * �w�肳�ꂽ�����̃p�X���[�h�𐶐�����B<p>
     *
     * @param length �p�X���[�h��
     * @return �p�X���[�h
     */
    public String generate(int length){
        final List result = new ArrayList(length);
        Iterator itr = elements.iterator();
        final List curElements = new ArrayList(elements.size());
        while(itr.hasNext()){
            PasswordElement element = (PasswordElement)itr.next();
            curElements.add(element.clone());
        }
        itr = curElements.iterator();
        while(itr.hasNext() && result.size() < length){
            PasswordElement element = (PasswordElement)itr.next();
            if(element.isNecessary()){
                result.add(Character.valueOf(element.next(random)));
                if(!element.hasNext()){
                    itr.remove();
                }
            }
        }
        while(result.size() < length && curElements.size() > 0){
            PasswordElement element = (PasswordElement)curElements.get(random.nextInt(curElements.size()));
            result.add(Character.valueOf(element.next(random)));
            if(!element.hasNext()){
                curElements.remove(element);
            }
        }
        Collections.shuffle(result, random);
        final StringBuilder buf = new StringBuilder();
        itr = result.iterator();
        while(itr.hasNext()){
            buf.append(itr.next());
        }
        return buf.toString();
    }
    
    /**
     * �p�X���[�h�v�f�B<p>
     *
     * @author M.Takata
     */
    public static class PasswordElement implements Cloneable{
        protected final char[] chars;
        protected int minCount = 0;
        protected int maxCount = 0;
        protected int count;
        
        /**
         * �w�肳�ꂽ�������p�X���[�h�̗v�f�Ƃ��Ď��C���X�^���X�𐶐�����B<p>
         *
         * @param chars �p�X���[�h�̗v�f�ƂȂ镶���z��B
         */
        public PasswordElement(char[] chars){
            this.chars = chars;
        }
        
        /**
         * �w�肳�ꂽ�������p�X���[�h�̗v�f�Ƃ��Ď����A�ŏ��o���񐔂ƍő�o���񐔂��w�肵���C���X�^���X�𐶐�����B<p>
         *
         * @param chars �p�X���[�h�̗v�f�ƂȂ镶���z��B
         * @param min �ŏ��o����
         * @param max �ő�o���񐔁B���x�ł��o�����ėǂ��ꍇ�́A0
         */
        public PasswordElement(char[] chars, int min, int max){
            this(chars);
            setMinCount(min);
            setMaxCount(max);
        }
        
        /**
         * �ŏ��o���񐔂�ݒ肷��B<p>
         *
         * @param min �ŏ��o����
         */
        public void setMinCount(int min){
            minCount = min;
        }
        
        /**
         * �ő�o���񐔂�ݒ肷��B<p>
         *
         * @param max �ő�o���񐔁B���x�ł��o�����ėǂ��ꍇ�́A0
         */
        public void setMaxCount(int max){
            maxCount = max;
        }
        
        /**
         * �܂��o�����Ă��ǂ����𔻒肷��B<p>
         *
         * @return �܂��o�����Ă��ǂ��ꍇ�́Atrue
         */
        public boolean hasNext(){
            return maxCount == 0 || count < maxCount;
        }
        
        /**
         * �܂��o�����ׂ����𔻒肷��B<p>
         *
         * @return �܂��o�����ׂ����ꍇ�́Atrue
         */
        public boolean isNecessary(){
            return minCount > count;
        }
        
        /**
         * ���̕�����Ԃ��B<p>
         *
         * @param random ����
         * @return ���̕���
         */
        public char next(Random random){
            count++;
            return chars[random.nextInt(chars.length)];
        }
        
        /**
         * �����𐶐�����B<p>
         *
         * @return ����
         */
        public Object clone(){
            PasswordElement clone = null;
            try{
                clone = (PasswordElement)super.clone();
            }catch(CloneNotSupportedException e){
                return null;
            }
            clone.count = 0;
            return clone;
        }
    }
    
    /**
     * �A���t�@�x�b�g�������̃p�X���[�h�v�f�B<p>
     *
     * @author M.Takata
     */
    public static class LowercaseAlphabetElement extends PasswordElement{
        
        /**
         * �A���t�@�x�b�g���������p�X���[�h�̗v�f�Ƃ��Ď��C���X�^���X�𐶐�����B<p>
         */
        public LowercaseAlphabetElement(){
            super(LOWERCASE_ALPHABET);
        }
        
        /**
         * �A���t�@�x�b�g���������p�X���[�h�̗v�f�Ƃ��Ď����A�ŏ��o���񐔂ƍő�o���񐔂��w�肵���C���X�^���X�𐶐�����B<p>
         *
         * @param min �ŏ��o����
         * @param max �ő�o���񐔁B���x�ł��o�����ėǂ��ꍇ�́A0
         */
        public LowercaseAlphabetElement(int min, int max){
            super(LOWERCASE_ALPHABET, min, max);
        }
    }
    
    /**
     * �A���t�@�x�b�g�啶���̃p�X���[�h�v�f�B<p>
     *
     * @author M.Takata
     */
    public static class UppercaseAlphabetElement extends PasswordElement{
        
        /**
         * �A���t�@�x�b�g�啶�����p�X���[�h�̗v�f�Ƃ��Ď��C���X�^���X�𐶐�����B<p>
         */
        public UppercaseAlphabetElement(){
            super(UPPPERCASE_ALPHABET);
        }
        
        /**
         * �A���t�@�x�b�g�啶�����p�X���[�h�̗v�f�Ƃ��Ď����A�ŏ��o���񐔂ƍő�o���񐔂��w�肵���C���X�^���X�𐶐�����B<p>
         *
         * @param min �ŏ��o����
         * @param max �ő�o���񐔁B���x�ł��o�����ėǂ��ꍇ�́A0
         */
        public UppercaseAlphabetElement(int min, int max){
            super(UPPPERCASE_ALPHABET, min, max);
        }
    }
    
    /**
     * �����̃p�X���[�h�v�f�B<p>
     *
     * @author M.Takata
     */
    public static class NumberElement extends PasswordElement{
        
        /**
         * �������p�X���[�h�̗v�f�Ƃ��Ď��C���X�^���X�𐶐�����B<p>
         */
        public NumberElement(){
            super(NUMBER);
        }
        
        /**
         * �������p�X���[�h�̗v�f�Ƃ��Ď����A�ŏ��o���񐔂ƍő�o���񐔂��w�肵���C���X�^���X�𐶐�����B<p>
         *
         * @param min �ŏ��o����
         * @param max �ő�o���񐔁B���x�ł��o�����ėǂ��ꍇ�́A0
         */
        public NumberElement(int min, int max){
            super(NUMBER, min, max);
        }
    }
    
    /**
     * �L���̃p�X���[�h�v�f�B<p>
     *
     * @author M.Takata
     */
    public static class SymbolElement extends PasswordElement{
        
        /**
         * �L�����p�X���[�h�̗v�f�Ƃ��Ď��C���X�^���X�𐶐�����B<p>
         */
        public SymbolElement(){
            super(SYMBOL);
        }
        
        /**
         * �L�����p�X���[�h�̗v�f�Ƃ��Ď����A�ŏ��o���񐔂ƍő�o���񐔂��w�肵���C���X�^���X�𐶐�����B<p>
         *
         * @param min �ŏ��o����
         * @param max �ő�o���񐔁B���x�ł��o�����ėǂ��ꍇ�́A0
         */
        public SymbolElement(int min, int max){
            super(SYMBOL, min, max);
        }
    }
}