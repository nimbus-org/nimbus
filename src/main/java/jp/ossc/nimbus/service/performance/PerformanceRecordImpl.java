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
package jp.ossc.nimbus.service.performance;

import java.text.SimpleDateFormat;
import java.util.Date;
//
/**
 *	�e�p�t�H�[�}���X�̓o�^�A�o�͂��s���B
 *	@author 	NRI Hirotaka.Nakano
 *				�X�V�F
 */
public class PerformanceRecordImpl implements PerformanceRecord,
												PerformanceRecordOperator {
	//##	�����o�[�ϐ��錾	##
	/** ���\�[�X�h�c					*/	protected String	mResourceId;
	/** �ŏI���s����					*/	protected Date	mLastProcTime;
	/** �x�X�g��������					*/	protected Date	mBestTime;
	/** ���[�X�g��������					*/	protected Date	mWorstTime;
	/** ���s��						*/	protected long	mProcTimes;
	/** �x�X�g�p�t�H�[�}���X				*/	protected long	mBestPerformance;
	/** ���[�X�g�p�t�H�[�}���X			*/	protected long	mWorstPerformance;
	/** �A�x���[�W�p�t�H�[�}���X			*/	protected long 	mAveragePerformance;
	/** ���O�z��						*/	protected String[] logMsg = new String[2];
	/** ���������� 						*/	protected long mTotalTime;
	//
	/**
	 * �R���X�g���N�^�B<BR>
	 * �e�����o�[�ϐ�������������B<BR>
	 * ���\�[�X�L�[����������ݒ肷��B
	 */
	public PerformanceRecordImpl () {
		/** �e�����o�[�ϐ��̏��������s��	*/
		this.mLastProcTime = null;
		this.mProcTimes = 0;
		this.mBestPerformance = 0;
		this.mWorstPerformance = 0;
		this.mBestTime = null;
		this.mWorstTime = null;
		this.mAveragePerformance = 0;
		this.mTotalTime = 0;
	}

	//
	/**
	 *	�p�t�H�[�}���X�A�b�v���\�b�h<BR>
	 *	�R�[���񐔂��t�o����B<BR>
	 *	�x�X�g�p�t�H�[�}���X�Ƃ̔�r�A�o�^�������s���B<BR>
	 *	���[�X�g�p�t�H�[�}���X�Ƃ̔�r�A�o�^�������s���B<BR>
	 *	�ŏI�R�[��������o�^����B
	 * @param msec �p�t�H�[�}���X�^�C��
	 */
	public void entry (long msec){
		this.mLastProcTime = new Date();
		if(this.mProcTimes == 0){
			this.mBestPerformance = msec;
			this.mWorstPerformance = msec;
			this.mBestTime = this.mLastProcTime;
			this.mWorstTime = this.mLastProcTime;	
		}
		else{
			if (msec < this.mBestPerformance){
				this.mBestPerformance = msec;
				this.mBestTime = this.mLastProcTime;
			}
			if (msec > this.mWorstPerformance){
				this.mWorstPerformance = msec;
				this.mWorstTime = this.mLastProcTime;
			}
		}
		this.mTotalTime = this.mTotalTime + msec;
		this.mProcTimes++;
		this.mAveragePerformance = mTotalTime/this.mProcTimes;
	}
	//
	/**
	 *	��������쐬����B
	 * @return String �p�t�H�[�}���X���
	 */
	public String toString() {
		StringBuffer retStr = new StringBuffer();
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss:SS");
		String LastProcTime = formatter.format(mLastProcTime);
		String WorstTime = formatter.format(mWorstTime);
		String BestTime = formatter.format(mBestTime);
		retStr.append("[").append(mResourceId).append("]:");
		retStr.append("[ProcTimes=").append(mProcTimes).append("]:");
		retStr.append("[LastProcTime=").append(LastProcTime).append("]:");
		retStr.append("[BestPerformance=").append(mBestPerformance).append(",").append(BestTime).append("]:");
		retStr.append("[WorstPerformance=").append(mWorstPerformance).append(",").append(WorstTime).append("]:");
		retStr.append("[AveragePerformance=").append(mAveragePerformance).append("]");
		return retStr.toString();
	}
	//
	/**
	 *	���\�[�X�h�c���o�͂���B
	 * @param id String ���\�[�XID
	 */
	public void setResourceId(String id) {
		this.mResourceId = id ;
	}
	//
	/**
	 *	���\�[�X�h�c���o�͂���B
	 * @return String ���\�[�XID
	 */
	public String getResourceId() {
		return this.mResourceId;
	}
	//
	/**
	 *	�Ăяo���񐔂��o�͂���B
	 *	@return long �Ăяo����
	 */
	public long getCallTime() {
		return this.mProcTimes;
	}
	//
	/**
	 *	�ŏI�R�[���������o�͂���B
	 * @return Date �ŏI�R�[������
	 */
	public Date getLastCallTime() {
		return this.mLastProcTime;
	}
	//
	/**
	 *	�x�X�g�p�t�H�[�}���X���o�͂���B
	 * @return long �x�X�g�p�t�H�[�}���X
	 */
	public long getBestPerformance() {
		return this.mBestPerformance;
	}
	//
	/**
	 *	�x�X�g�p�t�H�[�}���X�������o�͂���B
	 * @return Date �x�X�g�p�t�H�[�}���X����
	 */
	public Date getBestPerformanceTime() {
		return this.mBestTime;
	}
	//
	/**
	 *	���[�X�g�p�t�H�[�}���X���o�͂���B
	 * @return long ���[�X�g�p�t�H�[�}���X
	 */
	public long getWorstPerformance() {
		return this.mWorstPerformance;
	}
	//
	/**
	 *	���[�X�g�p�t�H�[�}���X�������o�͂���B
	 * @return Date ���[�X�g�p�t�H�[�}���X����
	 */
	public Date getWorstPerformanceTime() {
		return this.mWorstTime;
	}
	//
	/**
	 *	�A�x���[�W�p�t�H�[�}���X���o�͂���B
	 * @return Date ���[�X�g�p�t�H�[�}���X����
	 */
	public long getAveragePerformance() {
		return this.mAveragePerformance;
	}
	//
}
