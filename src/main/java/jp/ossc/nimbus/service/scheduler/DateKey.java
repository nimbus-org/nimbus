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
package jp.ossc.nimbus.service.scheduler;

import java.util.*;

/**
 * ���t�L�[�B<p>
 * �ȉ��Ɏw����@�������B<br>
 * �N�w�� : YEAR@2006<br>
 * ���w�� : MONTH@8<br>
 * ���w�� : DAY@10<br>
 * �������w�� : DAY@END<br>
 * ���g���w�� : DAY@�C�ӂ̃L�[������<br>
 * �T�w�� : WEEK@1<br>
 * �����T�w�� : WEEK@END<br>
 * �j���w�� : WEEK@MONDAY<br>
 * NOT�����FNOT DAY@10<br>
 * OR�����FDAY@10 OR DAY@20<br>
 * AND�����FDAY@10 AND DAY@20<br>
 * ���������F(DAY@10 AND DAY@20) OR WEEK@END<br>
 * <p>
 * ���g���w����g�p����ꍇ�́A���̃L�[�����߂ł���{@link DateEvaluator}�����N���X��ݒ肵�Ȃ���΂Ȃ�Ȃ��B<be>
 *
 * @author M.Takata
 */
public class DateKey{
    
    /**
     * �T�w��v���t�B�N�X������B<p>
     */
    public static final String WEEK_AT = "WEEK@";
    
    /**
     * �T�w��L�[������ ���j���B<p>
     */
    public static final String MONDAY = "MONDAY";
    
    /**
     * �T�w��L�[������ �Ηj���B<p>
     */
    public static final String TUESDAY = "TUESDAY";
    
    /**
     * �T�w��L�[������ ���j���B<p>
     */
    public static final String WEDNESDAY = "WEDNESDAY";
    
    /**
     * �T�w��L�[������ �ؗj���B<p>
     */
    public static final String THURSDAY = "THURSDAY";
    
    /**
     * �T�w��L�[������ ���j���B<p>
     */
    public static final String FRIDAY = "FRIDAY";
    
    /**
     * �T�w��L�[������ �y�j���B<p>
     */
    public static final String SATURDAY = "SATURDAY";
    
    /**
     * �T�w��L�[������ ���j���B<p>
     */
    public static final String SUNDAY = "SUNDAY";
    
    /**
     * �N�w��v���t�B�N�X������B<p>
     */
    public static final String YEAR_AT = "YEAR@";
    
    /**
     * ���w��v���t�B�N�X������B<p>
     */
    public static final String MONTH_AT = "MONTH@";
    
    /**
     * ���w��v���t�B�N�X������B<p>
     */
    public static final String DAY_AT = "DAY@";
    
    /**
     * �����y�ь������w��L�[������B<p>
     */
    public static final String END = "END";
    
    /**
     * AND���Z�q������B<p>
     */
    public static final String AND = "AND";
    
    /**
     * OR���Z�q������B<p>
     */
    public static final String OR = "OR";
    
    /**
     * NOT���Z�q������B<p>
     */
    public static final String NOT = "NOT";
    
    /**
     * �����I�u�W�F�N�g�B<p>
     */
    protected Condition condition;
    
    /**
     * ���t�]���I�u�W�F�N�g�B<p>
     */
    protected DateEvaluator evaluator;
    
    private static final String OPEN = "(";
    private static final String CLOSE = ")";
    private static final String SPACE = " ";
    
    private static final String[] DELIMETERS = new String[]{
        NOT, AND, OR, OPEN, CLOSE, WEEK_AT, YEAR_AT, MONTH_AT, DAY_AT, SPACE
    };
    
    /**
     * �w�肳�ꂽ���t�L�[����������߂�����t�L�[�C���X�^���X�𐶐�����B<p>
     *
     * @param key ���t�L�[������
     */
    public DateKey(String key){
        condition = parse(key, 0);
    }
    
    /**
     * �w�肳�ꂽ���t�L�[����������߂�����t�L�[�C���X�^���X�𐶐�����B<p>
     *
     * @param key ���t�L�[������
     * @param evaluator ���t�]��
     */
    public DateKey(String key, DateEvaluator evaluator){
        this.evaluator = evaluator;
        condition = parse(key, 0);
    }
    
    /**
     * ���t�g���L�[����]������{@link DateEvaluator}��ݒ肷��B<p>
     *
     * @param evaluator DateEvaluator
     */
    public void setDateEvaluator(DateEvaluator evaluator){
        this.evaluator = evaluator;
    }
    
    /**
     * ���t�g���L�[����]������{@link DateEvaluator}���擾����B<p>
     *
     * @return DateEvaluator
     */
    public DateEvaluator getDateEvaluator(){
        return evaluator;
    }
    
    /**
     * �w�肳�ꂽ��������w�肳�ꂽ��؂蕶���ŕ�������B<p>
     *
     * @param str ������
     * @param delims ��؂蕶���z��
     * @param tokens ���������g�[�N�����i�[���郊�X�g
     * @return ���������g�[�N�����i�[�������X�g
     */
    protected List parseTokens(String str, String[] delims, List tokens){
        int index = -1;
        int delimLength = 0;
        for(int i = 0; i < delims.length; i++){
            int tmp = str.indexOf(delims[i]);
            if(tmp != -1){
                if(index == -1){
                    index = tmp;
                    delimLength = delims[i].length();
                }else if(index > tmp){
                    index = tmp;
                    delimLength = delims[i].length();
                }
            }
        }
        if(index == -1){
            tokens.add(str);
        }else if(index == 0){
            if(str.length() == delimLength){
                tokens.add(str);
            }else{
                tokens.add(str.substring(0, delimLength));
                return parseTokens(str.substring(delimLength), delims, tokens);
            }
        }else{
            tokens.add(str.substring(0, index));
            tokens.add(str.substring(index, index + delimLength));
            if(str.length() > index + delimLength){
                return parseTokens(
                    str.substring(index + delimLength),
                    delims,
                    tokens
                );
            }
        }
        return tokens;
    }
    
    /**
     * �w�肳�ꂽ���t�L�[��������A�p�[�X���ď����ɕϊ�����B<p>
     *
     * @param key ���t�L�[������
     * @param index �p�[�X���̕�����C���f�b�N�X
     * @return �p�[�X���ꂽ����
     */
    protected Condition parse(String key, int index){
        List tokenList = parseTokens(key, DELIMETERS, new ArrayList());
        Condition cond = null;
        NotCondition notCond = null;
        try{
            Iterator tokens = tokenList.iterator();
            while(tokens.hasNext()){
                String token = (String)tokens.next();
                Condition curCond = null;
                if(NOT.equalsIgnoreCase(token)){
                    if(notCond == null){
                        notCond = new NotCondition();
                    }else{
                        notCond = null;
                    }
                    continue;
                }else if(AND.equalsIgnoreCase(token)){
                    curCond = new AndCondition(cond);
                    cond = null;
                }else if(OR.equalsIgnoreCase(token)){
                    curCond = new OrCondition(cond);
                    cond = null;
                }else if(WEEK_AT.equalsIgnoreCase(token)){
                    index += token.length();
                    token = (String)tokens.next();
                    curCond = new WeekCondition(token);
                }else if(YEAR_AT.equalsIgnoreCase(token)){
                    index += token.length();
                    token = (String)tokens.next();
                    curCond = new YearCondition(token);
                }else if(MONTH_AT.equalsIgnoreCase(token)){
                    index += token.length();
                    token = (String)tokens.next();
                    curCond = new MonthCondition(token);
                }else if(DAY_AT.equalsIgnoreCase(token)){
                    index += token.length();
                    token = (String)tokens.next();
                    curCond = new DayCondition(token);
                }else if(OPEN.equals(token)){
                    final StringBuffer buf = new StringBuffer();
                    int tmpIndex = index;
                    while(!(token = (String)tokens.next()).equals(CLOSE)){
                        index += token.length();
                        buf.append(token);
                    }
                    index += token.length();
                    curCond = parse(buf.toString(), tmpIndex);
                }else if(CLOSE.equals(token)){
                    throw new IllegalArgumentException(
                        "Invalid potision" + token
                    );
                }else if(SPACE.equals(token)){
                    continue;
                }else{
                    throw new IllegalArgumentException(
                        "Invalid token " + token
                    );
                }
                if(cond == null){
                    cond = curCond;
                }else{
                    cond.add(curCond);
                }
                if(notCond != null){
                    notCond.add(cond);
                    cond = notCond;
                    notCond = null;
                }
                index += token.length();
            }
            if(cond == null && notCond != null){
                cond = notCond;
            }
            cond.validate();
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage() + " : " + index);
        }catch(NoSuchElementException e){
            throw new IllegalArgumentException(
                "The expected token is not found : " + index
            );
        }
        return cond;
    }
    
    /**
     * �w�肳�ꂽ���t���A���̓��t�L�[�ɊY��������t�ł��邩�ǂ����𔻒肷��B<p>
     *
     * @param date ���t
     * @return �w�肳�ꂽ���t���A���̓��t�L�[�ɊY��������t�ł���ꍇ��true
     * @exception Exception ���t�̔���Ɏ��s�����ꍇ
     */
    public boolean equalsDate(Date date) throws Exception{
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return condition.equalsDate(cal);
    }
    
    /**
     * �����B<p>
     *
     * @author M.Takata
     */
    protected interface Condition{
        public boolean equalsDate(Calendar cal) throws Exception;
        public void add(Condition c);
        public void validate();
    }
    
    /**
     * �T�w������B<p>
     *
     * @author M.Takata
     */
    protected class WeekCondition implements Condition{
        protected int numberOfWeek;
        protected int week;
        protected boolean isEnd;
        public WeekCondition(String key){
            if(END.equals(key)){
                isEnd = true;
            }else if(MONDAY.equalsIgnoreCase(key)){
                week = Calendar.MONDAY;
            }else if(TUESDAY.equalsIgnoreCase(key)){
                week = Calendar.TUESDAY;
            }else if(WEDNESDAY.equalsIgnoreCase(key)){
                week = Calendar.WEDNESDAY;
            }else if(THURSDAY.equalsIgnoreCase(key)){
                week = Calendar.THURSDAY;
            }else if(FRIDAY.equalsIgnoreCase(key)){
                week = Calendar.FRIDAY;
            }else if(SATURDAY.equalsIgnoreCase(key)){
                week = Calendar.SATURDAY;
            }else if(SUNDAY.equalsIgnoreCase(key)){
                week = Calendar.SUNDAY;
            }else{
                try{
                    numberOfWeek = Integer.parseInt(key);
                    if(numberOfWeek < 1 || numberOfWeek > 6){
                        throw new IllegalArgumentException(
                            "expected number from 1 to 6"
                        );
                    }
                }catch(NumberFormatException e){
                    throw new IllegalArgumentException(
                        "expected number or Week key(MONDAY,TUESDAY,...etc.) or END"
                    );
                }
            }
        }
        public boolean equalsDate(Calendar cal){
            if(isEnd){
                return cal.getActualMaximum(Calendar.WEEK_OF_MONTH)
                     == cal.get(Calendar.WEEK_OF_MONTH);
            }else if(numberOfWeek == 0){
                return cal.get(Calendar.DAY_OF_WEEK) == week;
            }else{
                return cal.get(Calendar.WEEK_OF_MONTH) == numberOfWeek;
            }
        }
        public void add(Condition c){
            throw new IllegalArgumentException("expected OR or AND.");
        }
        public void validate(){
            if(numberOfWeek == 0 && week == 0 && !isEnd){
                throw new IllegalArgumentException(
                    "WEEK@ is null"
                );
            }
        }
    }
    
    /**
     * �N�w������B<p>
     *
     * @author M.Takata
     */
    protected class YearCondition implements Condition{
        protected int year;
        public YearCondition(String key){
            try{
                year = Integer.parseInt(key);
                if(year < 0){
                    throw new IllegalArgumentException(
                        "expected number more than 1"
                    );
                }
            }catch(NumberFormatException e){
                throw new IllegalArgumentException(
                    "expected number"
                );
            }
        }
        public boolean equalsDate(Calendar cal){
            return cal.get(Calendar.YEAR) == year;
        }
        public void add(Condition c){
            throw new IllegalArgumentException("expected OR or AND.");
        }
        public void validate(){
            if(year == 0){
                throw new IllegalArgumentException(
                    "YEAR@ is null"
                );
            }
        }
    }
    
    /**
     * ���w������B<p>
     *
     * @author M.Takata
     */
    protected class MonthCondition implements Condition{
        protected int month;
        public MonthCondition(String key){
            try{
                month = Integer.parseInt(key);
                if(month < 0 || month > 12){
                    throw new IllegalArgumentException(
                        "expected number from 1 to 12"
                    );
                }
            }catch(NumberFormatException e){
                throw new IllegalArgumentException(
                    "expected number"
                );
            }
        }
        public boolean equalsDate(Calendar cal){
            return cal.get(Calendar.MONTH) + 1 == month;
        }
        public void add(Condition c){
            throw new IllegalArgumentException("expected OR or AND.");
        }
        public void validate(){
            if(month == 0){
                throw new IllegalArgumentException(
                    "MONTH@ is null"
                );
            }
        }
    }
    
    /**
     * ���w������B<p>
     *
     * @author M.Takata
     */
    protected class DayCondition implements Condition{
        protected int day;
        protected boolean isEnd;
        protected String dateKey;
        public DayCondition(String key){
            if(END.equals(key)){
                isEnd = true;
            }else{
                try{
                    day = Integer.parseInt(key);
                    if(day < 0 || day > 31){
                        throw new IllegalArgumentException(
                            "expected number from 1 to 31"
                        );
                    }
                }catch(NumberFormatException e){
                    if(evaluator == null){
                        throw new IllegalArgumentException(
                            "expected number or END"
                        );
                    }
                    dateKey = key;
                }
            }
        }
        public boolean equalsDate(Calendar cal) throws Exception{
            if(isEnd){
                return cal.getActualMaximum(Calendar.DAY_OF_MONTH) == day;
            }else if(dateKey != null){
                return evaluator.equalsDate(dateKey, cal);
            }else{
                return cal.get(Calendar.DAY_OF_MONTH) == day;
            }
        }
        public void add(Condition c){
            throw new IllegalArgumentException("expected OR or AND.");
        }
        public void validate(){
            if(day == 0 && !isEnd){
                throw new IllegalArgumentException(
                    "DAY@ is null"
                );
            }
        }
    }
    
    /**
     * AND�����B<p>
     *
     * @author M.Takata
     */
    protected class AndCondition implements Condition{
        protected Condition condition1;
        protected Condition condition2;
        public AndCondition(Condition c){
            if(c == null){
                throw new IllegalArgumentException(
                    "expected expresion(WEEK@, YEAR@... etc.)."
                );
            }
            condition1 = c;
        }
        public void add(Condition c){
            if(condition2 != null){
                throw new IllegalArgumentException("expected OR or AND.");
            }
            condition2 = c;
        }
        public boolean equalsDate(Calendar cal) throws Exception{
            return condition1.equalsDate(cal) && condition2.equalsDate(cal);
        }
        public void validate(){
            if(condition1 == null || condition2 == null){
                throw new IllegalArgumentException(
                    "Two operands are necessary for AND"
                );
            }
            condition1.validate();
            condition2.validate();
        }
    }
    
    /**
     * OR�����B<p>
     *
     * @author M.Takata
     */
    protected class OrCondition implements Condition{
        protected Condition condition1;
        protected Condition condition2;
        public OrCondition(Condition c){
            if(c == null){
                throw new IllegalArgumentException(
                    "expected expresion(WEEK@, YEAR@... etc.)."
                );
            }
            condition1 = c;
        }
        public void add(Condition c){
            if(condition2 != null){
                throw new IllegalArgumentException("expected OR or AND.");
            }
            condition2 = c;
        }
        public boolean equalsDate(Calendar cal) throws Exception{
            return condition1.equalsDate(cal) || condition2.equalsDate(cal);
        }
        public void validate(){
            if(condition1 == null || condition2 == null){
                throw new IllegalArgumentException(
                    "Two operands are necessary for OR"
                );
            }
            condition1.validate();
            condition2.validate();
        }
    }
    
    /**
     * NOT�����B<p>
     *
     * @author M.Takata
     */
    protected class NotCondition implements Condition{
        protected Condition condition;
        public NotCondition(){
        }
        public void add(Condition c){
            condition = c;
        }
        public boolean equalsDate(Calendar cal) throws Exception{
            return !condition.equalsDate(cal);
        }
        public void validate(){
            if(condition == null){
                throw new IllegalArgumentException(
                    "operands are necessary for NOT"
                );
            }
            condition.validate();
        }
    }
}
