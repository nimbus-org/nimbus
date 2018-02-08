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
package jp.ossc.nimbus.service.scheduler2;

import java.io.Serializable;

/**
 * �W�z�M���N�G�X�g�B<p>
 * 
 * @author M.Takata
 */
public class ConcentrateRequest implements Serializable{
    
    private static final long serialVersionUID = -5534337631451607056L;
    
    /**
     * �W�z�M��ʕ�����F���W�B<p>
     */
    public static final String PROCESS_TYPE_GET = "GET";
    
    /**
     * �W�z�M��ʕ�����F�z�M�B<p>
     */
    public static final String PROCESS_TYPE_PUT = "PUT";
    
    /**
     * �W�z�M��ʕ�����F�]���B<p>
     */
    public static final String PROCESS_TYPE_FORWARD = "FORWARD";
    
    /**
     * �W�z�M��ʁF���W�B<p>
     */
    public static final int PROCESS_TYPE_VALUE_GET = 1;
    
    /**
     * �W�z�M��ʁF�z�M�B<p>
     */
    public static final int PROCESS_TYPE_VALUE_PUT = 2;
    
    /**
     * �W�z�M��ʁF�]���B<p>
     */
    public static final int PROCESS_TYPE_VALUE_FORWARD = 3;
    
    private String key;
    private String source;
    private String destination;
    private int processType;
    
    /**
     * ��̃C���X�^���X�𐶐�����B<p>
     */
    public ConcentrateRequest(){
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param key �L�[
     * @param type �W�z�M���
     * @param src �W�z�M�����
     * @param dest �W�z�M������
     * @see #PROCESS_TYPE_VALUE_GET
     * @see #PROCESS_TYPE_VALUE_PUT
     * @see #PROCESS_TYPE_VALUE_FORWARD
     */
    public ConcentrateRequest(String key, int type, String src, String dest){
        this.key = key;
        source = src;
        destination = dest;
        processType = type;
    }
    
    /**
     * �L�[���擾����B<p>
     *
     * @return �L�[
     */
    public String getKey(){
        return key;
    }
    
    /**
     * �L�[��ݒ肷��B<p>
     *
     * @param key �L�[
     */
    public void setKey(String key){
        this.key = key;
    }
    
    /**
     * �W�z�M�������擾����B<p>
     *
     * @return �W�z�M�����
     */
    public String getSource(){
        return source;
    }
    
    /**
     * �W�z�M������ݒ肷��B<p>
     *
     * @param src �W�z�M�����
     */
    public void setSource(String src){
        source = src;
    }
    
    /**
     * �W�z�M��������擾����B<p>
     *
     * @return �W�z�M������
     */
    public String getDestination(){
        return destination;
    }
    
    /**
     * �W�z�M�������ݒ肷��B<p>
     *
     * @param dest �W�z�M������
     */
    public void setDestination(String dest){
        destination = dest;
    }
    
    /**
     * �W�z�M��ʂ��擾����B<p>
     *
     * @return �W�z�M���
     * @see #PROCESS_TYPE_VALUE_GET
     * @see #PROCESS_TYPE_VALUE_PUT
     * @see #PROCESS_TYPE_VALUE_FORWARD
     */
    public int getProcessType(){
        return processType;
    }
    
    /**
     * �W�z�M��ʂ�ݒ肷��B<p>
     *
     * @param type �W�z�M���
     * @see #PROCESS_TYPE_VALUE_GET
     * @see #PROCESS_TYPE_VALUE_PUT
     * @see #PROCESS_TYPE_VALUE_FORWARD
     */
    public void setProcessType(int type){
        processType = type;
    }
    
    /**
     * �W�z�M��ʂ��W�z�M��ʕ�����ɕϊ�����B<p>
     *
     * @param type �W�z�M���
     * @return �W�z�M��ʕ�����
     * @see #PROCESS_TYPE_VALUE_GET
     * @see #PROCESS_TYPE_VALUE_PUT
     * @see #PROCESS_TYPE_VALUE_FORWARD
     * @see #PROCESS_TYPE_GET
     * @see #PROCESS_TYPE_PUT
     * @see #PROCESS_TYPE_FORWARD
     */
    public static String toProcessTypeString(int type){
        switch(type){
        case PROCESS_TYPE_VALUE_GET:
            return PROCESS_TYPE_GET;
        case PROCESS_TYPE_VALUE_PUT:
            return PROCESS_TYPE_PUT;
        case PROCESS_TYPE_VALUE_FORWARD:
            return PROCESS_TYPE_FORWARD;
        default:
            return null;
        }
    }
    
    /**
     * �W�z�M��ʕ�������W�z�M��ʂɕϊ�����B<p>
     *
     * @param type �W�z�M��ʕ�����
     * @return �W�z�M���
     * @see #PROCESS_TYPE_GET
     * @see #PROCESS_TYPE_PUT
     * @see #PROCESS_TYPE_FORWARD
     * @see #PROCESS_TYPE_VALUE_GET
     * @see #PROCESS_TYPE_VALUE_PUT
     * @see #PROCESS_TYPE_VALUE_FORWARD
     */
    public static int toProcessType(String type){
        if(type == null){
            return 0;
        }else if(PROCESS_TYPE_GET.equals(type.toUpperCase())){
            return PROCESS_TYPE_VALUE_GET;
        }else if(PROCESS_TYPE_PUT.equals(type.toUpperCase())){
            return PROCESS_TYPE_VALUE_PUT;
        }else if(PROCESS_TYPE_FORWARD.equals(type.toUpperCase())){
            return PROCESS_TYPE_VALUE_FORWARD;
        }else{
            return 0;
        }
    }
    
    public String toString(){
        final StringBuffer buf = new StringBuffer(super.toString());
        buf.append("{key=").append(key);
        buf.append(",source=").append(source);
        buf.append(",destination=").append(destination);
        buf.append(",source=").append(source);
        buf.append(",processType=").append(toProcessTypeString(processType)).append('}');
        return buf.toString();
    }
}