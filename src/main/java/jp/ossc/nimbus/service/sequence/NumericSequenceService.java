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
package jp.ossc.nimbus.service.sequence;

import java.util.ArrayList;
import java.util.Iterator;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.lang.ServiceException;
import jp.ossc.nimbus.util.CsvArrayList;
import jp.ossc.nimbus.util.StringOperator;

/**
 * �������ԃT�[�r�X�B<p>
 * 
 * @author H.Nakano
 */
public class NumericSequenceService extends ServiceBase
 implements  Sequence, NumericSequenceServiceMBean {
    
    private static final long serialVersionUID = -3368735884570934622L;
    
    private static final String C_ZERO = "0" ; //$NON-NLS-1$
    private static final String C_ZERO_WITH_COMMMA = "0," ; //$NON-NLS-1$
    private static final String C_SEMICORON = ";" ;//$NON-NLS-1$
    private static final String C_NINE = "9" ;//$NON-NLS-1$
    
    //## �����o�[�ϐ��錾 ##
    
    /** �V�[�P���X�ԍ� */
    protected ArrayList mSequenceNo;
    
    /** �t�H�[�}�b�g������ */
    protected String mFormat;
    
    /** �ŏ��l */
    protected String mMin;
    
    /** �ő�l */
    protected String mMax;
    
    /** �J�n���ԍ� */
    protected String mInitialNumber = "";
    
    /** �J�n�t���O(�ŏ���increment�܂�true)*/
    protected boolean mInitialFlag = true;
    
    // NumericSequenceServiceMBean ��JavaDoc
    public void setFormat(String format){
        synchronized(this){
            // format������؂�ŕ�������
            CsvArrayList parser = new CsvArrayList(); 
            parser.split(format,C_SEMICORON);
            if(parser.size() != 2){
                throw new ServiceException(
                    "NUMERICSEQ001",
                    "fromat is invalid format = "+ format
                ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            mMin = parser.getStr(0) ;
            mMax = parser.getStr(1) ;
            if(!StringOperator.isNumeric(mMin)){
                throw new ServiceException(
                    "NUMERICSEQ002",
                    "MIN is not numeric min = " + mMin
                ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if(!StringOperator.isNumeric(mMax)){
                throw new ServiceException(
                    "NUMERICSEQ003",
                    "MAX is not numeric max = " + mMax
                ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            StringBuffer tmpFormat = new StringBuffer() ;
            for(int cnt = 0; cnt < mMax.length(); cnt++){
                tmpFormat.append(C_ZERO_WITH_COMMMA);
                tmpFormat.append(C_NINE);
                if(cnt != mMax.length() - 1){
                    tmpFormat.append(C_SEMICORON);
                }
            }
            mFormat = tmpFormat.toString() ;
        }
    }
    
    // NumericSequenceServiceMBean ��JavaDoc
    public String getFormat(){
        return mFormat;
    }
    
    // NumericSequenceServiceMBean ��JavaDoc
    public String getMinValue(){
        return mMin;
    }
    
    // NumericSequenceServiceMBean ��JavaDoc
    public String getMaxValue(){
        return mMax;
    }
    
    /**
     * �T�[�r�X�̐����������s���B<p>
     * 
     * @exception Exception �T�[�r�X�̐��������Ɏ��s�����ꍇ
     */
    public void createService() throws Exception{
        mSequenceNo = new ArrayList();
    }
    
    /**
     * �T�[�r�X�̊J�n�������s���B<p>
     * 
     * @exception Exception �T�[�r�X�̊J�n�����Ɏ��s�����ꍇ
     */
    public void startService(){
        synchronized(this){
            // format������؂�ŕ�������
            CsvArrayList parser = new CsvArrayList();
            parser.split(mFormat, C_SEMICORON);
            // ����񃊃X�g���C���X�^���V���O����
            mSequenceNo = new ArrayList();
            // �e�������C���X�^���V���O���ă��X�g�Ɋi�[����
            for(Iterator iterator = parser.iterator(); iterator.hasNext();){
                String formatItem = (String)iterator.next();
                final SequenceVariable item
                     = new SimpleSequenceVariable(formatItem);
                mSequenceNo.add(item);
            }
            mInitialFlag = true;
            mInitialNumber = "";
        }
    }
    
    /**
     * �T�[�r�X�̒�~�������s���B<p>
     * 
     * @exception Exception �T�[�r�X�̒�~�����Ɏ��s�����ꍇ
     */
    public void stopService(){
        mSequenceNo.clear();
    }
    
    /**
     * �T�[�r�X�̔j���������s���B<p>
     * 
     * @exception Exception �T�[�r�X�̔j�������Ɏ��s�����ꍇ
     */
    public void destroyService(){
        mSequenceNo = null;
    }
    
    // Sequence��JavaDoc
    public String increment(){
        StringBuffer retStr = new StringBuffer();
        synchronized(this){
            // �����̐[�����擾����
            int maxCnt = mSequenceNo.size();
            // �P�����ڂ���C���N�������g���J�n����
            for(int rCnt = --maxCnt; rCnt >= 0; rCnt--){
                SequenceVariable item = (SequenceVariable)mSequenceNo.get(rCnt);
                //increment
                boolean isOverFlow = item.increment();
                // �I�[�o�[�t���[���Ȃ��ꍇ�͂����オ��Ȃ�
                if(!isOverFlow){
                    break;
                }
            }
            // �J�����g���������������ԕ����𐶐�����
            boolean isFirst = false;
            for(Iterator iterator = mSequenceNo.iterator(); iterator.hasNext();){
                SequenceVariable item = (SequenceVariable)iterator.next();
                if(!isFirst){
                    String tmp = item.getCurrent();
                    if(!tmp.equals(C_ZERO)){
                        isFirst = true;
                    }
                }
                if(isFirst){
                    retStr.append(item.getCurrent());
                }
            }
            if(retStr.toString().compareTo(mMin) < 0){
                retStr = new StringBuffer(increment());
            }
            if(retStr.toString().length() >= mMax.length()
                 && retStr.toString().compareTo(mMax) > 0){
                reset();
                retStr = new StringBuffer(increment());
            }
            // �J�n�t���O��true�ł���΁A�J�n���ԍ��Ƃ��ĕۑ�
            if(mInitialFlag){
                mInitialNumber = retStr.toString();
                mInitialFlag = false;                                    
            }
        }
        return retStr.toString();
    }
    
    // Sequence��JavaDoc
    public void reset(){
        synchronized(this){
            // �J�����g���������������ԕ����𐶐�����
            for(Iterator iterator = mSequenceNo.iterator(); iterator.hasNext();){
                SequenceVariable item = (SequenceVariable)iterator.next();
                item.clear();
            }
            // �J�n�t���O��true�ɂ���B
            mInitialFlag = true;
            mInitialNumber = "";
        }
    }
    
    // Sequence��JavaDoc
    public String getInitial(){
        return mInitialNumber;
    }
    
    // Sequence��JavaDoc
    public String getCurrent(){
        StringBuffer retStr = new StringBuffer();
        synchronized(this){
            // �J�����g���������������ԕ����𐶐�����
            boolean isFirst = false ;
            for(Iterator iterator = mSequenceNo.iterator(); iterator.hasNext();){
                SequenceVariable item = (SequenceVariable)iterator.next();
                if(!isFirst){
                    String tmp = item.getCurrent();
                    if(!tmp.equals(C_ZERO)){
                        isFirst = true;
                    }
                }
                if(isFirst){
                    retStr.append(item.getCurrent());
                }
            }
        }
        return retStr.toString();
    }
}
