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
package jp.ossc.nimbus.beans.dataset;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.validator.*;

/**
 * Vlidator�v���p�e�B�X�L�[�}�����N���X�B<p>
 * ���̃N���X�ɂ́A�v���p�e�B�̃X�L�[�}���Ƃ��āA�ȉ��̏�񂪒�`�ł���B<br>
 * <ul>
 *   <li>���O</li>
 *   <li>�^</li>
 *   <li>���͕ϊ����</li>
 *   <li>�o�͕ϊ����</li>
 *   <li>����</li>
 * </ul>
 * ����ɁA{@link Validator}�T�[�r�X�����w�肷�鎖�ŁAValidator���g�p����ȊO�́A{@link DefaultPropertySchema}�̎d�l�ɏ]���B<br>
 * 
 * @author M.Takata
 */
public class ValidatorPropertySchema extends DefaultPropertySchema{
    
    private static final long serialVersionUID = -4030795163707859320L;
    
    protected ServiceName validatorServiceName;
    
    /**
     * ��̃v���p�e�B�X�L�[�}�𐶐�����B<p>
     */
    public ValidatorPropertySchema(){
    }
    
    /**
     * �v���p�e�B�X�L�[�}�𐶐�����B<p>
     *
     * @param schema �v���p�e�B�̃X�L�[�}��`
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public ValidatorPropertySchema(String schema) throws PropertySchemaDefineException{
        super(schema);
    }
    
    /**
     * ������擾����B<p>
     *
     * @return ����
     */
    public String getConstrain(){
        return validatorServiceName == null
             ? null : validatorServiceName.toString();
    }
    
    /**
     * �v���p�e�B�X�L�[�}�̐���̍��ڂ��p�[�X����B<p>
     *
     * @param schema �v���p�e�B�X�L�[�}�S��
     * @param val �X�L�[�}����
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    protected void parseConstrain(String schema, String val)
     throws PropertySchemaDefineException{
        if(val != null && val.length() != 0){
            final ServiceNameEditor serviceNameEditor
                 = new ServiceNameEditor();
            try{
                serviceNameEditor.setAsText(val);
            }catch(IllegalArgumentException e){
                throw new PropertySchemaDefineException(
                    schema,
                    "Constrain is illegal.",
                    e
                );
            }
            validatorServiceName = (ServiceName)serviceNameEditor.getValue();
        }
    }
    
    // PropertySchema��JavaDoc
    public boolean validate(Object val) throws PropertyValidateException{
        if(validatorServiceName == null){
            return true;
        }
        try{
            final Validator validator = (Validator)ServiceManagerFactory
                .getServiceObject(validatorServiceName);
            return validator.validate(val);
        }catch(ServiceNotFoundException e){
            throw new PropertyValidateException(
                this,
                "Validator is not found : " + validatorServiceName,
                e
            );
        }catch(ValidateException e){
            throw new PropertyValidateException(
                this,
                "Validate error.",
                e
            );
        }
    }
    
    /**
     * ���̃X�L�[�}�̕�����\�����擾����B<p>
     *
     * @return ������\��
     */
    public String toString(){
        final StringBuffer buf = new StringBuffer(getClass().getName());
        buf.append('{');
        buf.append("name=").append(name);
        buf.append(",type=").append(type == null ? null : type.getName());
        if(parseConverter == null && parseConverterName == null){
            buf.append(",parseConverter=null");
        }else if(parseConverter != null){
            buf.append(",parseConverter=").append(parseConverter);
        }else{
            buf.append(",parseConverter=").append(parseConverterName);
        }
        if(formatConverter == null && formatConverterName == null){
            buf.append(",formatConverter=null");
        }else if(formatConverter != null){
            buf.append(",formatConverter=").append(formatConverter);
        }else{
            buf.append(",formatConverter=").append(formatConverterName);
        }
        buf.append(",constrain=")
            .append(validatorServiceName == null
                 ? null : validatorServiceName);
        buf.append('}');
        return buf.toString();
    }
}