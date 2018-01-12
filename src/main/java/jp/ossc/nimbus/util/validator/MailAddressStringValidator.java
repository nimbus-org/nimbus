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
package jp.ossc.nimbus.util.validator;

import java.util.regex.*;

/**
 * メールアドレス文字列バリデータ。<p>
 * 
 * @author M.Takata
 */
public class MailAddressStringValidator extends AbstractStringValidator
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 6744750492948377241L;
    
    protected static final String SPECIAL_CHARS = "\\(\\)<>@,;:\\\\\\\"\\.\\[\\]";
    protected static final String VALID_CHARS = "[\\S" + SPECIAL_CHARS + "]";
    protected static final String QUOTED_USER = "(\"[^\"]*\")";
    protected static final String ATOM = VALID_CHARS + '+';
    protected static final String ATOM2 = VALID_CHARS + '*';
    protected static final String WORD = "(" + ATOM + "|" + QUOTED_USER + ")";
    protected static final String WORD2 = "(" + ATOM2 + "|" + QUOTED_USER + ")";
    
    protected static Pattern LEGAL_ASCII_PATTERN = Pattern.compile("^[\\0000-\\0177]+$");
    protected static Pattern EMAIL_PATTERN = Pattern.compile("^(.+)@(.+)$");
    protected static Pattern USER_PATTERN = Pattern.compile("\\S*" + WORD + "(\\." + WORD2 + ")*\\S*$");
    protected static Pattern DOMAIN_PATTERN = Pattern.compile("\\S*" + ATOM + "(\\." + ATOM + ")*\\S*$");
    protected static Pattern IP_DOMAIN_PATTERN = Pattern.compile("^\\[(\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})\\]$");
    protected static Pattern ATOM_PATTERN = Pattern.compile("(" + ATOM + ")");
    
    /**
     * 指定された文字列がメールアドレスとして正しいかを検証する。<p>
     *
     * @param str 検証対象の文字列
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    protected boolean validateString(String str) throws ValidateException{
        if(str.charAt(str.length() - 1) == '.') {
            return false;
        }
        if(!LEGAL_ASCII_PATTERN.matcher(str).matches()){
            return false;
        }
        if(!EMAIL_PATTERN.matcher(str).matches()){
            return false;
        }
        int index = str.indexOf('@');
        final String account = str.substring(0, index);
        if(!USER_PATTERN.matcher(account).matches()){
            return false;
        }
        final String domain = str.substring(index + 1);
        if(IP_DOMAIN_PATTERN.matcher(domain).matches()){
            final String[] ipSegments = domain.split("\\.");
            if(ipSegments.length != 4){
                return false;
            }
            for(int i = 0; i < 4; i++){
                int ipSegment = 0;
                try{
                    ipSegment = Integer.parseInt(ipSegments[i]);
                }catch(NumberFormatException e){
                    return false;
                }
                if(ipSegment > 255){
                    return false;
                }
            }
        }else if(DOMAIN_PATTERN.matcher(domain).matches()){
            final String[] domainSegments = domain.split("\\.");
            if(domainSegments.length < 2){
                return false;
            }
            if(domainSegments[domainSegments.length - 1].length() < 2
                || domainSegments[domainSegments.length - 1].length() > 4){
                return false;
            }
            for(int i = 0; i < domainSegments.length; i++){
                if(!ATOM_PATTERN.matcher(domainSegments[i]).matches()){
                    return false;
                }
            }
        }else{
            return false;
        }
        
        return true;
    }
}