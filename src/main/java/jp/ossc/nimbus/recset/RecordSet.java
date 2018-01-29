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
// �p�b�P�[�W
package jp.ossc.nimbus.recset;
//�C���|�[�g
import java.util.*;
import java.sql.*;
import java.io.*;

import org.apache.commons.jexl.*;

import jp.ossc.nimbus.service.crypt.Crypt;
import jp.ossc.nimbus.service.log.*;
import jp.ossc.nimbus.service.codemaster.PartUpdate;
import jp.ossc.nimbus.service.codemaster.PartUpdateRecords;
import jp.ossc.nimbus.service.codemaster.CodeMasterUpdateKey;

/**
 * ���R�[�h�Ǘ��N���X�B<p>
 * �f�[�^�x�[�X���R�[�h�̊Ǘ����s���B
 * 
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class RecordSet implements Serializable, PartUpdate, Cloneable{
    
    private static final long serialVersionUID = -7457366126244404177L;
    
    /**
     * �X�L�[�}��`�ɗp������s�����B<p>
     */
    public  static final String C_SEPARATOR = System.getProperty("line.separator");
    
    /** SQL �X�e�[�g�����g������萔 */
    private static final String C_SET_TOKEN = " SET ";
    private static final String C_UPDATE_TOKEN = "UPDATE ";
    private static final String C_QUESTION_TOKEN = "?";
    private static final String C_EQUAL_TOKEN = "=";
    private static final String C_AND_TOKEN = " AND ";
    private static final String C_OR_TOKEN = " OR ";
    private static final String C_WHERE_TOKEN = " WHERE ";
    private static final String C_VALUES_TOKEN = " VALUES ";
    private static final String C_BRACKETS_END_TOKEN = " ) ";
    private static final String C_BRACKETS_BEGIN_TOKEN = " ( ";
    private static final String C_DELETE_TOKEN = "DELETE FROM ";
    private static final String C_INSERT_TOKEN = "INSERT INTO ";
    private static final String C_ORDER_TOKEN = " ORDER BY ";
    private static final String C_FROM_TOKEN = " FROM ";
    private static final String C_COMMA_TOKEN = ",";
    private static final String C_SELECT_TOKEN = "SELECT ";
    private static final String C_DISTINCT_TOKEN = " DISTINCT ";
    private static final String C_BLANK_TOKEN = " ";
    
    /** �Í����T�[�r�X */
    protected Crypt mCrypt;
    
    
    /** �s�X�L�[�} */
    protected RowSchema mSchema;
    
    /** �s�f�[�^�̃��X�g */
    protected ArrayList mRows;
    
    /** �s�f�[�^���L�[�ŊǗ����Ă���HashMap*/
    protected HashMap mHash;
    
    /** �e�[�u���������� */
    protected String mTableNames;
    
    /** �e�[�u���������� */
    protected String mUpdateTableNames;
    
    /** �\�[�g������ */
    protected String mOrder;
    
    /** WEHRE�啶���� */
    protected StringBuffer where;
    
    /** PreparedStatement�ɖ��ߍ��ރf�[�^��ێ����郊�X�g */
    protected List bindDatas;
    
    /** �R�l�N�V���� */
    protected transient Connection mCon ;
    
    /** ���K�[�I�u�W�F�N�g */
    protected Logger mLogger ;
    
    /** ���sSQL�����O�o�͂��邽�߂̃��O���b�Z�[�W�R�[�h */
    protected String mMessageCode ;
    
    /**
     * DISTINCT�w��t���O�B<p>
     * �����l��false
     */
    protected boolean mDistinctFlg = false;
    
    /**
     * �X�V�y�э폜���ɁA�X�V�y�э폜���悤�Ƃ��������Ǝ��ۂɍX�V�y�э폜�������������������ǂ����̐��������`�F�b�N���邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�Atrue�B<br>
     */
    protected boolean isEnabledRowVersionCheck = true;
    
    /**
     * ���I�����������Ǘ�����}�b�v�B<p>
     */
    protected Map dynamicSearchConditionMap;
    
    /**
     * ���I�����������ʂ�ێ�����}�b�v�B<p>
     */
    protected Map dynamicSearchConditionResultMap;
    
    /**
     * ���I�L�[�������Ǘ�����}�b�v�B<p>
     */
    protected Map dynamicSearchKeyMap;
    
    /**
     * ���I�L�[�������ʂ�ێ�����}�b�v�B<p>
     */
    protected Map dynamicSearchMap;
    
    /**
     * �o�b�`���s���邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Atrue�B
     */
    protected boolean isBatchExecute = true;
    
    protected int[] partUpdateOrderBy;
    
    protected boolean[] partUpdateIsAsc;
    
    /**
     * ��̃C���X�^���X�𐶐�����B<p>
     */
    public RecordSet(){
        mRows = new ArrayList();
        mHash = new HashMap();
    }
    
    /**
     * ���O��ݒ肷��B<p>
     * �f�t�H���g�́Anull�ŁA���O�o�͂���Ȃ��B<br>
     * 
     * @param lg ���O
     */
    public void setLogger(Logger lg){
        mLogger = lg;
    }
    
    /**
     * ���sSQL�����O�o�͂��邽�߂̃��b�Z�[�W�R�[�h��ݒ肷��B<p>
     * �f�t�H���g�́Anull�ŁA���O�o�͂���Ȃ��B<br>
     *
     * @param code ���b�Z�[�W�R�[�h
     */
    public void setMessageCode(String code){
        mMessageCode = code;
    }
    
    /**
     * �X�V�y�э폜���ɁA�X�V�y�э폜���悤�Ƃ��������ƁA���ۂɍX�V�y�э폜�������������������ǂ����̐��������`�F�b�N���邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�Atrue�B<br>
     * true��ݒ肳��Ă���ꍇ�́A{@link #updateRecords()}���Ăяo�������Ƀ`�F�b�N�ɂЂ�������ƁA{@link RowVersionException}��throw�����B<br>
     *
     * @param isEnabled �`�F�b�N����ꍇ�́Atrue
     */
    public void setEnabledRowVersionCheck(boolean isEnabled){
        isEnabledRowVersionCheck = isEnabled;
    }
    
    /**
     * �X�V�y�э폜���ɁA�X�V�y�э폜���悤�Ƃ��������ƁA���ۂɍX�V�y�э폜�������������������ǂ����̐��������`�F�b�N���邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�́A�`�F�b�N����
     */
    public boolean isEnabledRowVersionCheck(){
        return isEnabledRowVersionCheck;
    }
    
    /**
     * �X�L�[�}�̏������������s���B<p>
     * �X�L�[�}������́A<br>
     * ��,�^,����,���R�[�h���,�Í����t���O<br>
     * �����s�R�[�h�ŋ�؂���������Ƃ���B<br>
     * 
     * @param schema �X�L�[�}������
     */
    public void initSchema(String schema){
        mSchema = SchemaManager.findRowSchema(schema);
    }
    
    /**
     * �X�L�[�}�̏������������s���B<p>
     * �t�B�[���h�X�L�[�}������́A<br>
     * ��,�^,����,���R�[�h���,�Í����t���O<br>
     * �Ƃ��A���̔z����w�肷��B<br>
     * 
     * @param filedSchemata �t�B�[���h�X�L�[�}������̔z��
     */
    public void initFieldSchemata(String[] filedSchemata){
        StringBuilder buf = new StringBuilder();
        for(int i = 0; i < filedSchemata.length; i++){
            buf.append(filedSchemata[i]);
            if(i != filedSchemata.length - 1){
                buf.append(C_SEPARATOR);
            }
        }
        initSchema(buf.toString());
    }
    
    /**
     * �X�L�[�}�����擾����B<p>
     *
     * @return �X�L�[�}���
     */
    public RowSchema getRowSchema(){
        return mSchema;
    }
    
    /**
     * ��������e�[�u������ݒ肷��B<p>
     * SELECT���̑Ώۃe�[�u���ƂȂ�B<br>
     * 
     * @param tableStr �P�܂��̓J���}�ŋ�؂�ꂽ�����̃e�[�u����
     */
    public void setFromTable(String tableStr){
        mTableNames = tableStr;
    }
    
    /**
     * �X�V����e�[�u������ݒ肷��B<p>
     * INSERT�ADELETE�AUPDATE���̑Ώۃe�[�u���ƂȂ�B<br>
     * �w�肵�Ȃ��ꍇ��INSERT�ADELETE�AUPDATE���̑Ώۃe�[�u���́A{@link #setFromTable(String)}�Ŏw�肳�ꂽ�e�[�u���Ƃ݂Ȃ��B<br>
     * 
     * @param tableStr �P�܂��̓J���}�ŋ�؂�ꂽ�����̃e�[�u����
     */
    public void setUpdateTable(String tableStr){
        mUpdateTableNames = tableStr;
    }
    
    /**
     * ORDER BY���ݒ肷��B<p>
     * 
     * @param order �P�܂��̓J���}�ŋ�؂�ꂽ�����̗�
     */
    public void setOrderbyStr(String order){
        mOrder = order;
    }
    
    /**
     * JDBC�R�l�N�V������ݒ肷��B<p>
     * 
     * @param con JDBC�R�l�N�V����
     */
    public void setConnection(Connection con){
        mCon = con;
    }
    
    /**
     * JDBC�R�l�N�V�������擾����B<p>
     * 
     * @return JDBC�R�l�N�V����
     */
    public Connection getConnection(){
        return mCon;
    }
    
    /**
     * SELECT���ADISTINCT�w�肷�邩�ǂ�����ݒ肷��B<p>
     * 
     * @param flg DISTINCT�w�肷��ꍇ�́Atrue
     */
    public void setDistinctFlg(boolean flg){
        mDistinctFlg = flg;
    }
    
    /**
     * �Í����I�u�W�F�N�g��ݒ肷��B<p>
     * 
     * @param crypt �Í����I�u�W�F�N�g
     */
    public void setCrypt(Crypt crypt){
        mCrypt = crypt;
    }
    
    /**
     * �Í����I�u�W�F�N�g���擾����B<p>
     * 
     * @return �Í����I�u�W�F�N�g
     */
    public Crypt getCrypt(){
        return mCrypt;
    }
    
    /**
     * �o�b�`���s���邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Atrue�B<br>
     *
     * @param isBatch �o�b�`���s����ꍇ�́Atrue
     */
    public void setBatchExecute(boolean isBatch){
        isBatchExecute = isBatch;
    }
    
    /**
     * �o�b�`���s���邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�o�b�`���s����
     */
    public boolean isBatchExecute(){
        return isBatchExecute;
    }
    
    /**
     * �񖼔z�񂩂��C���f�b�N�X�z��ɕϊ�����B<p>
     *
     * @param colNames �񖼔z��
     * @return ��C���f�b�N�X�z��
     */
    private int[] convertFromColNamesToColIndexes(String[] colNames){
        if(colNames == null || colNames.length == 0){
            return null;
        }
        if(mSchema == null){
            throw new InvalidDataException("Schema not initalize.");
        }
        final int[] colIndexes = new int[colNames.length];
        for(int i = 0; i < colNames.length; i++){
            final FieldSchema field = mSchema.get(colNames[i]);
            if(field == null){
                throw new IllegalArgumentException("Field not found : " + colNames[i]);
            }
            colIndexes[i] = field.getIndex();
        }
        return colIndexes;
    }
    
    /**
     * ���I��������������ݒ肷��B<p>
     * {@link #setDynamicSearchCondition(String, String, int[], boolean[]) setDynamicSearchCondition(null, condition, (int[])null, null)}���Ăяo���̂Ɠ����B<br>
     * �����̓��I��������������ݒ肵�����ꍇ�́A{@link #setDynamicSearchCondition(String, String)}�ŁA���������w�肵�āA������ݒ肷��B<br>
     *
     * @param condition ������
     * @exception Exception ���������s���ȏꍇ
     * @see #setDynamicSearchCondition(String, String, int[], boolean[])
     */
    public void setDynamicSearchCondition(String condition) throws Exception{
        setDynamicSearchCondition(null, condition);
    }
    
    /**
     * ���I�������������i�\�[�g��w��t���j��ݒ肷��B<p>
     * {@link #setDynamicSearchCondition(String, String, String[], boolean[]) setDynamicSearchCondition(null, condition, orderBy, null)}���Ăяo���̂Ɠ����B<br>
     * �����̓��I��������������ݒ肵�����ꍇ�́A{@link #setDynamicSearchCondition(String, String, String[])}�ŁA���������w�肵�āA������ݒ肷��B<br>
     *
     * @param condition ������
     * @param orderBy �\�[�g�񖼔z��
     * @exception Exception ���������s���ȏꍇ
     * @see #setDynamicSearchCondition(String, String, String[], boolean[])
     */
    public void setDynamicSearchCondition(String condition, String[] orderBy) throws Exception{
        setDynamicSearchCondition(null, condition, orderBy);
    }
    
    /**
     * ���I�������������i�\�[�g��w��t���j��ݒ肷��B<p>
     * {@link #setDynamicSearchCondition(String, String, int[], boolean[]) setDynamicSearchCondition(null, condition, orderBy, null)}���Ăяo���̂Ɠ����B<br>
     * �����̓��I��������������ݒ肵�����ꍇ�́A{@link #setDynamicSearchCondition(String, String, int[])}�ŁA���������w�肵�āA������ݒ肷��B<br>
     *
     * @param condition ������
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @exception Exception ���������s���ȏꍇ
     * @see #setDynamicSearchCondition(String, String, int[], boolean[])
     */
    public void setDynamicSearchCondition(String condition, int[] orderBy) throws Exception{
        setDynamicSearchCondition(null, condition, orderBy);
    }
    
    /**
     * ���I�������������i�\�[�g��w��A�\�[�g���w��t���j��ݒ肷��B<p>
     * {@link #setDynamicSearchCondition(String, String, String[], boolean[]) setDynamicSearchCondition(null, condition, orderBy, isAsc)}���Ăяo���̂Ɠ����B<br>
     * �����̓��I��������������ݒ肵�����ꍇ�́A{@link #setDynamicSearchCondition(String, String, String[], boolean[])}�ŁA���������w�肵�āA������ݒ肷��B<br>
     *
     * @param condition ������
     * @param orderBy �\�[�g�񖼔z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @exception Exception ���������s���ȏꍇ
     * @see #setDynamicSearchCondition(String, String, String[], boolean[])
     */
    public void setDynamicSearchCondition(String condition, String[] orderBy, boolean[] isAsc) throws Exception{
        setDynamicSearchCondition(null, condition, orderBy, isAsc);
    }
    
    /**
     * ���I�������������i�\�[�g��w��A�\�[�g���w��t���j��ݒ肷��B<p>
     * {@link #setDynamicSearchCondition(String, String, int[], boolean[]) setDynamicSearchCondition(null, condition, orderBy, isAsc)}���Ăяo���̂Ɠ����B<br>
     * �����̓��I��������������ݒ肵�����ꍇ�́A{@link #setDynamicSearchCondition(String, String, int[], boolean[])}�ŁA���������w�肵�āA������ݒ肷��B<br>
     *
     * @param condition ������
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @exception Exception ���������s���ȏꍇ
     * @see #setDynamicSearchCondition(String, String, int[], boolean[])
     */
    public void setDynamicSearchCondition(String condition, int[] orderBy, boolean[] isAsc) throws Exception{
        setDynamicSearchCondition(null, condition, orderBy, isAsc);
    }
    
    /**
     * ���������w�肵�āA���I��������������ݒ肷��B<p>
     * {@link #setDynamicSearchCondition(String, String, int[], boolean[]) setDynamicSearchCondition(name, condition, (int[])null, null)}���Ăяo���̂Ɠ����B<br>
     *
     * @param name ������
     * @param condition ������
     * @exception Exception ���������s���ȏꍇ
     * @see #setDynamicSearchCondition(String, String, int[], boolean[])
     */
    public void setDynamicSearchCondition(String name, String condition)
     throws Exception{
        setDynamicSearchCondition(name, condition, (int[])null, null);
    }
    
    /**
     * ���������w�肵�āA���I�������������i�\�[�g��w��t���j��ݒ肷��B<p>
     * {@link #setDynamicSearchCondition(String, String, String[], boolean[]) setDynamicSearchCondition(name, condition, orderBy, null)}���Ăяo���̂Ɠ����B<br>
     *
     * @param name ������
     * @param condition ������
     * @param orderBy �\�[�g�񖼔z��
     * @exception Exception ���������s���ȏꍇ
     * @see #setDynamicSearchCondition(String, String, String[], boolean[])
     */
    public void setDynamicSearchCondition(String name, String condition, String[] orderBy)
     throws Exception{
        setDynamicSearchCondition(
            name,
            condition,
            convertFromColNamesToColIndexes(orderBy),
            null
        );
    }
    
    /**
     * ���������w�肵�āA���I�������������i�\�[�g��w��t���j��ݒ肷��B<p>
     * {@link #setDynamicSearchCondition(String, String, int[], boolean[]) setDynamicSearchCondition(name, condition, orderBy, null)}���Ăяo���̂Ɠ����B<br>
     *
     * @param name ������
     * @param condition ������
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @exception Exception ���������s���ȏꍇ
     * @see #setDynamicSearchCondition(String, String, int[], boolean[])
     */
    public void setDynamicSearchCondition(String name, String condition, int[] orderBy)
     throws Exception{
        setDynamicSearchCondition(
            name,
            condition,
            orderBy,
            null
        );
    }
    
    /**
     * ���������w�肵�āA���I�������������i�\�[�g��w��A�\�[�g���w��t���j��ݒ肷��B<p>
     *
     * @param name ������
     * @param condition ������
     * @param orderBy �\�[�g�񖼔z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @exception Exception ���������s���ȏꍇ
     * @see #setDynamicSearchCondition(String, String, int[], boolean[])
     */
    public void setDynamicSearchCondition(String name, String condition, String[] orderBy, boolean[] isAsc)
     throws Exception{
        setDynamicSearchCondition(
            name,
            condition,
            convertFromColNamesToColIndexes(orderBy),
            isAsc
        );
    }
    
    /**
     * ���������w�肵�āA���I�������������i�\�[�g��w��A�\�[�g���w��t���j��ݒ肷��B<p>
     * ���I���������Ƃ́ARecordSet�ɒ~�ς��ꂽ���R�[�h����A�������ɍ��v���郌�R�[�h����������@�\�ł���B<br>
     * �܂��A���I���������ɂ́A���R�[�h��~�ς���ۂɌ�������~�ό^�����ƁA�~�ς��ꂽ���R�[�h���烊�A���Ɍ������郊�A���^����������B<br>
     * ���̃Z�b�^�[�́A�~�ό^�����̂��߂̏����ݒ���s�����̂ŁA���R�[�h��~�ς���O�ɐݒ肵�Ă����Ȃ���΂Ȃ�Ȃ��B<br>
     * �~�ό^�����̗��_�́A�~�ώ��ɓ����Ɍ������s���邽�߁A���ۂ̌������ɂ́A���炩���ߌ������ꂽ���ʂ����o�������ł��邽�߁A�����Ȍ������\�ɂȂ鎖�ł���B�A���A�~�ώ��Ɍ������s���̂ŁA�~�ςƌ����𓯎��ɍs���ꍇ�́A���̌��ʂ͂Ȃ��B<br>
     * �t�Ɍ��_�́A���������炩���ߐݒ肷��K�v�����邽�߁A���������I�ɕς��ꍇ�́A�Ή��ł��Ȃ��B���̂悤�ȏꍇ�́A���A���^����({@link #searchDynamicConditionReal(String, int[], boolean[], Map)})���g�p����B<br>
     * ���̃Z�b�^�[�ɑΉ�����~�ό^�����́A{@link #searchDynamicCondition(String)}�ōs���B<br>
     * <p>
     * �������́A<a href="http://jakarta.apache.org/commons/jexl/">Jakarta Commons Jexl</a>�̎�������g�p����B<br>
     * �~�ό^�����ł́A���R�[�h�̗�̒l���A�񖼂��w�肷�鎖�ŁA�����ŎQ�Ƃ��鎖���ł���B<br>
     * <pre>
     *  ��FA == '1' and B &gt;= 3
     * </pre>
     *
     * @param name ������
     * @param condition ������
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @exception Exception ���������s���ȏꍇ
     * @see #searchDynamicCondition(String)
     */
    public void setDynamicSearchCondition(String name, String condition, int[] orderBy, boolean[] isAsc)
     throws Exception{
        
        if(dynamicSearchConditionMap == null){
            dynamicSearchConditionMap = new HashMap();
        }
        
        if(mSchema == null){
            throw new InvalidDataException("Schema not initalize.");
        }
        
        final Expression exp = ExpressionFactory.createExpression(condition);
        final JexlContext context = JexlHelper.createContext();
        for(int i = 0, imax = mSchema.size(); i < imax; i++){
            final FieldSchema field = mSchema.get(i);
            final String fieldName = field.getFieldName();
            final int fieldType = field.getFieldType();
            Object val = null;
            switch(fieldType){
            case FieldSchema.C_TYPE_INT:
                val = new Integer(0);
                break;
            case FieldSchema.C_TYPE_LONG:
                val = new Long(0);
                break;
            case FieldSchema.C_TYPE_STRING:
            case FieldSchema.C_TYPE_CHAR:
                val = new String();
                break;
            case FieldSchema.C_TYPE_DATE:
            case FieldSchema.C_TYPE_TIMESTAMP:
                val = new java.util.Date();
                break;
            case FieldSchema.C_TYPE_FLOAT:
                val = new Float(0);
                break;
            case FieldSchema.C_TYPE_DOUBLE:
                val = new Double(0);
                break;
            case FieldSchema.C_TYPE_BLOB:
                val = new byte[0];
                break;
            case FieldSchema.C_TYPE_CLOB:
                val = new char[0];
                break;
            default:
            }
            context.getVars().put(fieldName, val);
        }
        Object ret = exp.evaluate(context);
        if(!(ret instanceof Boolean)){
            throw new IllegalArgumentException(
                "Condition is not boolean. condition=" + condition
                    + ", return=" + ret
            );
        }
        dynamicSearchConditionMap.put(
            name,
            exp
        );
        
        if(dynamicSearchConditionResultMap == null){
            dynamicSearchConditionResultMap = new HashMap();
        }
        dynamicSearchConditionResultMap.put(
            name,
            createOrderByMap(orderBy, isAsc)
        );
    }
    
    /**
     * �\�[�g�t���}�b�v�𐶐�����B<p>
     * 
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @return �\�[�g�t���}�b�v
     */
    private Map createOrderByMap(int[] orderBy, boolean[] isAsc){
        final Comparator comp = createOrderByComparator(orderBy, isAsc);
        if(comp == null){
            return new LinkedHashMap();
        }else{
            return new TreeMap(comp);
        }
    }
    
    /**
     * �\�[�g�p��Comparator�𐶐�����B<p>
     * 
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @return �\�[�g�p��Comparator
     */
    private Comparator createOrderByComparator(int[] orderBy, boolean[] isAsc){
        Comparator comp = null;
        if(orderBy != null && orderBy.length != 0){
            comp = new RowDataComparator(mSchema, orderBy, isAsc);
        }
        return comp;
    }

    /**
     * ���I�L�[����������ݒ肷��B<p>
     * {@link #setDynamicSearchKey(String, String[]) setDynamicSearchKey(null, colNames)}���Ăяo���̂Ɠ����B<br>
     * �����̓��I�L�[����������ݒ肵�����ꍇ�́A{@link #setDynamicSearchKey(String, String[])}�ŁA���������w�肵�āA������ݒ肷��B<br>
     *
     * @param colNames �񖼔z��
     * @see #setDynamicSearchKey(String, String[])
     */
    public void setDynamicSearchKey(String[] colNames){
        setDynamicSearchKey(null, colNames);
    }
    
    /**
     * ���I�L�[����������ݒ肷��B<p>
     * {@link #setDynamicSearchKey(String, int[]) setDynamicSearchKey(null, colNames)}���Ăяo���̂Ɠ����B<br>
     * �����̓��I�L�[����������ݒ肵�����ꍇ�́A{@link #setDynamicSearchKey(String, int[])}�ŁA���������w�肵�āA������ݒ肷��B<br>
     *
     * @param colIndexes ��C���f�b�N�X�z��
     * @see #setDynamicSearchKey(String, int[])
     */
    public void setDynamicSearchKey(int[] colIndexes){
        setDynamicSearchKey(null, colIndexes);
    }
    
    /**
     * ���������w�肵�āA���I�L�[����������ݒ肷��B<p>
     *
     * @param name ������
     * @param colNames ��C���f�b�N�X�z��
     * @see #setDynamicSearchKey(String, int[])
     */
    public void setDynamicSearchKey(String name, String[] colNames){
        setDynamicSearchKey(name, convertFromColNamesToColIndexes(colNames));
    }
    
    /**
     * ���������w�肵�āA���I�L�[����������ݒ肷��B<p>
     *
     * @param name ������
     * @param colIndexes ��C���f�b�N�X�z��
     * @see #setDynamicSearchKey(String, int[], int[])
     */
    public void setDynamicSearchKey(String name, int[] colIndexes){
        setDynamicSearchKey(name, colIndexes, null);
    }
    
    /**
     * ���������w�肵�āA���I�L�[���������i�\�[�g��w��j��ݒ肷��B<p>
     *
     * @param name ������
     * @param colNames ��C���f�b�N�X�z��
     * @param orderBy �\�[�g�񖼔z��
     * @see #setDynamicSearchKey(String, String[], String[], boolean[])
     */
    public void setDynamicSearchKey(String name, String[] colNames, String[] orderBy){
        setDynamicSearchKey(
            name,
            colNames,
            orderBy,
            null
        );
    }
    
    /**
     * ���������w�肵�āA���I�L�[���������i�\�[�g��w��j��ݒ肷��B<p>
     *
     * @param name ������
     * @param colIndexes ��C���f�b�N�X�z��
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @see #setDynamicSearchKey(String, int[], int[], boolean[])
     */
    public void setDynamicSearchKey(String name, int[] colIndexes, int[] orderBy){
        setDynamicSearchKey(
            name,
            colIndexes,
            orderBy,
            null
        );
    }
    
    /**
     * ���������w�肵�āA���I�L�[���������i�\�[�g��w��A�\�[�g���w��t���j��ݒ肷��B<p>
     *
     * @param name ������
     * @param colNames ��C���f�b�N�X�z��
     * @param orderBy �\�[�g�񖼔z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @see #setDynamicSearchKey(String, int[], int[], boolean[])
     */
    public void setDynamicSearchKey(
        String name,
        String[] colNames,
        String[] orderBy,
        boolean[] isAsc
    ){
        setDynamicSearchKey(
            name,
            convertFromColNamesToColIndexes(colNames),
            convertFromColNamesToColIndexes(orderBy),
            isAsc
        );
    }
    
    /**
     * ���������w�肵�āA���I�L�[���������i�\�[�g��w��A�\�[�g���w��t���j��ݒ肷��B<p>
     * ���I�L�[�����Ƃ́ARecordSet�ɒ~�ς��ꂽ���R�[�h����A�w�肳�ꂽ��i�����j�̒l�����v���郌�R�[�h����������@�\�ł���B<br>
     * �܂��A���I�L�[�����́A���R�[�h��~�ς���ۂɌ������s�����߁A���R�[�h��~�ς���O�ɏ�����ݒ肵�Ă����Ȃ���΂Ȃ�Ȃ��B<br>
     * ���̃Z�b�^�[�ɑΉ����錟���́A{@link #searchDynamicKey(String, RowData)}�ōs���B<br>
     *
     * @param name ������
     * @param colIndexes ��C���f�b�N�X�z��
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @see #searchDynamicKey(String, RowData)
     */
    public void setDynamicSearchKey(
        String name,
        int[] colIndexes,
        int[] orderBy,
        boolean[] isAsc
    ){
        if(colIndexes == null || colIndexes.length == 0){
            throw new IllegalArgumentException("Column index array is empty.");
        }
        if(dynamicSearchKeyMap == null){
            dynamicSearchKeyMap = new HashMap();
        }
        dynamicSearchKeyMap.put(
            name,
            new DynamicSearchKeyValue(colIndexes, orderBy, isAsc)
        );
    }
    
    /**
     * �V�K���R�[�h���쐬����B<p>
     * ����RecordSet�̃X�L�[�}�������ɐV����RowData���쐬����B<br>
     * �쐬���ꂽRowData�̃g�����U�N�V�������[�h�́A{@link RowData#E_Record_TypeIgnore}�ł���B<br>
     * 
     * @return �V����RowData
     */
    public RowData createNewRecord(){
        return new RowData(mSchema);
    }
    
    /**
     * WHERE�������ݒ肷��B<p>
     * WHERE������́A"WHERE"����n�܂镶������w�肷�鎖�B
     *
     * @param where WHERE������
     */
    public void setWhere(String where){
        this.where = new StringBuffer(where);
    }
    
    /**
     * ��������v���C�}���L�[�����i�[�������R�[�h����WHERE��𐶐����A�ݒ肷��B<p>
     * �v���C�}���L�[�ƂȂ�񖼂�A��B�ŁA���̒l��'1'��'2'�ƂȂ�RowData���w�肵���ꍇ�A<br>
     * <pre>
     *   WHERE A='1' AND B='2'
     * </pre>
     * �Ƃ���WHERE�����傪�ݒ肳���B
     *
     * @param row ���R�[�h
     */
    public void setWhere(RowData row){
        if(row != null){
            setWhere(row.createCodeMasterUpdateKey());
        }
    }
    
    /**
     * ��������v���C�}���L�[�����i�[�������R�[�h�z�񂩂�WHERE��𐶐����A�ݒ肵�܂��B<p>
     * �v���C�}���L�[�ƂȂ�񖼂�A��B�ŁA���̒l��'1'��'2'�ƂȂ�RowData�Ƃ��̒l��'2'��'3'�ƂȂ�RowData�̔z����w�肵���ꍇ�A<br>
     * <pre>
     *   WHERE (A='1' AND B='2') OR (A='3' AND B='4')
     * </pre>
     * �Ƃ���WHERE�����傪�ݒ肳���B
     *
     * @param rows ���R�[�h�z��
     */
    public void setWhere(RowData[] rows){
        if(rows == null || rows.length == 0){
            return;
        }
        final CodeMasterUpdateKey[] keys = new CodeMasterUpdateKey[rows.length];
        for(int i = 0; i < rows.length; i++){
            keys[i] = rows[i].createCodeMasterUpdateKey();
        }
        setWhere(keys);
    }
    
    /**
     * �R�[�h�}�X�^�X�V�L�[����WHERE��𐶐����A�ݒ肵�܂��B<p>
     * �L�[��A��B�ŁA���̒l��'1'��'2'�ƂȂ�CodeMasterUpdateKey���w�肵���ꍇ�A<br>
     * <pre>
     *   WHERE A='1' AND B='2'
     * </pre>
     * �Ƃ���WHERE�����傪�ݒ肳���B
     *
     * @param key �R�[�h�}�X�^�X�V�L�[
     */
    public void setWhere(CodeMasterUpdateKey key){
        if(key == null || key.isRemove()){
            return;
        }
        clearBindData();
        where = new StringBuffer(C_WHERE_TOKEN);
        Iterator entries = key.getKeyMap().entrySet().iterator();
        int index = 0;
        while(entries.hasNext()){
            final Map.Entry entry = (Map.Entry)entries.next();
            final String name = (String)entry.getKey();
            where.append(name);
            where.append(C_EQUAL_TOKEN);
            where.append(C_QUESTION_TOKEN);
            if(entries.hasNext()){
                where.append(C_AND_TOKEN);
            }
            final Object value = entry.getValue();
            setBindData(index++, value);
        }
    }
    
    /**
     * �R�[�h�}�X�^�X�V�L�[�z�񂩂�WHERE��𐶐����A�ݒ肵�܂��B<p>
     * �L�[��A��B�ŁA���̒l��'1'��'2'�ƂȂ�CodeMasterUpdateKey�Ƃ��̒l��'2'��'3'�ƂȂ�CodeMasterUpdateKey�̔z����w�肵���ꍇ�A<br>
     * <pre>
     *   WHERE (A='1' AND B='2') OR (A='3' AND B='4')
     * </pre>
     * �Ƃ���WHERE�����傪�ݒ肳���B
     *
     * @param keys �R�[�h�}�X�^�X�V�L�[�z��
     */
    public void setWhere(CodeMasterUpdateKey[] keys){
        if(keys == null || keys.length == 0){
            return;
        }
        clearBindData();
        final StringBuffer where = new StringBuffer(C_WHERE_TOKEN);
        int index = 0;
        for(int i = 0; i < keys.length; i++){
            CodeMasterUpdateKey key = (CodeMasterUpdateKey)keys[i];
            if(key.isRemove()){
                continue;
            }
            
            if(index != 0){
                where.append(C_OR_TOKEN);
            }
            
            where.append(C_BRACKETS_BEGIN_TOKEN);
            Iterator entries = key.getKeyMap().entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                final String name = (String)entry.getKey();
                where.append(name);
                where.append(C_EQUAL_TOKEN);
                where.append(C_QUESTION_TOKEN);
                if(entries.hasNext()){
                    where.append(C_AND_TOKEN);
                }
                final Object value = entry.getValue();
                setBindData(index++, value);
            }
            where.append(C_BRACKETS_END_TOKEN);
            }
        if(index != 0){
            this.where = where;
        }
    }
    
    /**
     * �����X�V���R�[�h����WHERE��𐶐����A�ݒ肵�܂��B<p>
     * �L�[��A��B�ŁA���̒l��'1'��'2'�ƂȂ�CodeMasterUpdateKey�Ƃ��̒l��'2'��'3'�ƂȂ�CodeMasterUpdateKey���i�[���ꂽPartUpdateRecords���w�肵���ꍇ�A<br>
     * <pre>
     *   WHERE (A='1' AND B='2') OR (A='3' AND B='4')
     * </pre>
     * �Ƃ���WHERE�����傪�ݒ肳���B
     *
     * @param records �����X�V���R�[�h
     */
    public void setWhere(PartUpdateRecords records){
        if(records == null || records.size() == 0
             || (!records.containsAdd() && !records.containsUpdate())
             || records.isFilledRecord()
        ){
            return;
        }
        clearBindData();
        where = new StringBuffer();
        Iterator keys = records.getKeys();
        int index = 0;
        while(keys.hasNext()){
            CodeMasterUpdateKey key = (CodeMasterUpdateKey)keys.next();
            if(key.isRemove() || records.getRecord(key) != null){
                continue;
            }
            
            if(index != 0){
                where.append(C_OR_TOKEN);
            }
            
            where.append(C_BRACKETS_BEGIN_TOKEN);
            Iterator entries = key.getKeyMap().entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                final String name = (String)entry.getKey();
                where.append(name);
                where.append(C_EQUAL_TOKEN);
                where.append(C_QUESTION_TOKEN);
                if(entries.hasNext()){
                    where.append(C_AND_TOKEN);
                }
                final Object value = entry.getValue();
                setBindData(index++, value);
            }
            where.append(C_BRACKETS_END_TOKEN);
        }
        if(where.length() != 0){
            where.insert(0, C_WHERE_TOKEN);
        }
    }
    
    /**
     * WHERE��̒ǉ��������s���B<p>
     * 
     * @param sb SELECT [�t�B�[���h��]... FROM [�e�[�u��]... �܂ł��܂� StringBuffer 
     */
    protected void addWhere(StringBuffer sb){
        if(where != null){
            sb.append(where.toString());
        }
    }
    
    /**
     * PreparedStatement�Ƀo�C���h����l��ݒ肷��B<p>
     *
     * @param index PreparedStatement�̃o�C���h�ϐ��C���f�b�N�X�B�C���f�b�N�X��0����n�܂�
     * @param val PreparedStatement�̃o�C���h�ϐ��l
     */
    public void setBindData(int index, Object val){
        if(bindDatas == null){
            bindDatas = new ArrayList();
        }
        if(bindDatas.size() < index){
            for(int i = bindDatas.size(); i < index; i++){
                bindDatas.add(null);
            }
        }
        if(bindDatas.size() == index){
            bindDatas.add(val);
        }else{
            bindDatas.set(index, val);
        }
    }
    
    /**
     * PreparedStatement�Ƀo�C���h����l���N���A����B<p>
     */
    public void clearBindData(){
        if(bindDatas != null){
            bindDatas.clear();
        }
    }
    
    
    /**
     * PreparedStatement �o�C���h�������s���B<p>
     * 
     * @param ps �o�C���h���� PreparedStatement
     * @exception SQLException
     */
    protected void addBindData(PreparedStatement ps) throws SQLException {
        if(bindDatas != null){
            Iterator itr = bindDatas.iterator();
            int index = 0;
            while(itr.hasNext()){
                Object bindData = itr.next();
                index++;
                if(bindData == null){
                    final ParameterMetaData meta = ps.getParameterMetaData();
                    ps.setNull(index, meta.getParameterType(index));
                }else{
                    ps.setObject(index, bindData);
                }
            }
        }
    }
    
    /**
     * �f�[�^�x�[�X���猟�����āA���R�[�h��~�ς���B<p>
     *
     * @return �������ʂ̃��R�[�h��
     * @throws SQLException
     */
    public int search() throws SQLException{
        return search(-1);
    }
    
    /**
     * �f�[�^�x�[�X���猟�����āA�w�肳�ꂽ�ő僌�R�[�h���܂Ń��R�[�h��~�ς���B<p>
     *
     * @param max �ő僌�R�[�h��
     * @return �������ʂ̃��R�[�h��
     * @throws SQLException
     */
    public int search(int max) throws SQLException{
        StringBuffer sb = new StringBuffer();
        sb.append(C_SELECT_TOKEN);
        if(mDistinctFlg == true){
            sb.append(C_DISTINCT_TOKEN);
        }
        for(int i = 0, imax = mSchema.size(); i < imax; i++){
            if(mSchema.get(i).getFieldKey() == FieldSchema.C_KEY_DUMMY){
                continue;
            }
            if(mSchema.get(i).getPysicalName() != null){
                sb.append(mSchema.get(i).getPysicalName());
                sb.append(C_BLANK_TOKEN) ;
            }
            sb.append(mSchema.get(i).getFieldName());
            sb.append(C_COMMA_TOKEN);
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append(C_FROM_TOKEN).append(mTableNames).append(' ');
        addWhere(sb);
        if(mOrder != null){
            sb.append(C_ORDER_TOKEN).append(mOrder);
        }
        PreparedStatement ps = null;
        try{
            final String statement = sb.toString();
            ps = mCon.prepareStatement(statement);
            if(mLogger != null && mMessageCode != null){
                mLogger.write(mMessageCode, statement);
            }
            addBindData(ps);
            final ResultSet rs = ps.executeQuery();
            int count = 0;
            ByteArrayOutputStream baos = null;
            CharArrayWriter caw = null;
            byte[] byteBuf = null;
            char[] charBuf = null;
            while(rs.next()){
                RowData rd = createNewRecord();
                int rscnt = 1;
                for(int i = 0, imax = mSchema.size(); i < imax;i++){
                    final FieldSchema fs = mSchema.get(i);
                    if(fs.getFieldKey() == FieldSchema.C_KEY_DUMMY){
                        continue;
                    }
                    Object obj = null;
                    switch(fs.getSqlType()){
                        case Types.TIMESTAMP:
                            obj = (java.sql.Timestamp)rs.getTimestamp(rscnt);
                            if(obj != null){
                                obj = new java.util.Date(
                                    ((java.sql.Timestamp)obj).getTime()
                                );
                            }
                            break;
                        case Types.BLOB:
                            final InputStream is = (InputStream)rs.getBinaryStream(rscnt);
                            if(is == null){
                                break;
                            }
                            if(baos == null){
                                baos = new ByteArrayOutputStream();
                                byteBuf = new byte[1024];
                            }
                            try{
                                int readLength = 0;
                                while((readLength = is.read(byteBuf)) != -1){
                                    baos.write(byteBuf, 0, readLength);
                                }
                            }catch(IOException e){
                                throw new SQLException("I/O error in reading BLOB." + e.getMessage());
                            }
                            obj = baos.toByteArray();
                            baos.reset();
                            break;
                        case Types.CLOB:
                            final Reader reader = (Reader)rs.getCharacterStream(rscnt);
                            if(reader == null){
                                break;
                            }
                            if(caw == null){
                                caw = new CharArrayWriter();
                                charBuf = new char[1024];
                            }
                            try{
                                int readLength = 0;
                                while((readLength = reader.read(charBuf)) != -1){
                                    caw.write(charBuf, 0, readLength);
                                }
                            }catch(IOException e){
                                throw new SQLException("I/O error in reading BLOB." + e.getMessage());
                            }
                            obj = caw.toCharArray();
                            caw.reset();
                            break;
                        default:
                            obj = rs.getObject(rscnt);
                    }
                    // �Í���
                    if(fs.isCrypt()){
                        obj = doEncrypt(obj);
                    }
                    rd.setValueNative(i, obj);
                    rscnt++;
                }
                rd.setTransactionMode(RowData.E_Record_TypeRead);
                addRecord(rd);
                count++;
                if(max > 0 && count >= max){
                    break;
                }
            }
            rs.close();
            return count;
        }finally{
            if(ps != null){
                ps.close();
            }
        }
    }
    
    /**
     * �~�ό^���I���������̌������ʂ��擾����B<p>
     * �~�ό^���I�����������s�����߂ɂ́A���R�[�h��~�ς���O�ɁA{@link #setDynamicSearchCondition(String, int[], boolean[])}�œ��I��������������ݒ肵�Ă����K�v������B<br>
     *
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     * @see #setDynamicSearchCondition(String, int[], boolean[])
     */
    public Collection searchDynamicCondition(){
        return searchDynamicCondition(null);
    }
    
    /**
     * �w�肳�ꂽ�������̒~�ό^���I���������̌������ʂ��擾����B<p>
     * �~�ό^���I�����������s�����߂ɂ́A���R�[�h��~�ς���O�ɁA{@link #setDynamicSearchCondition(String, String, int[], boolean[])}�œ��I��������������ݒ肵�Ă����K�v������B<br>
     *
     * @param name ������
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     * @see #setDynamicSearchCondition(String, String, int[], boolean[])
     */
    public Collection searchDynamicCondition(String name){
        if(dynamicSearchConditionResultMap == null
             || dynamicSearchConditionResultMap.size() == 0){
            return new HashSet();
        }
        final Map values = (Map)dynamicSearchConditionResultMap.get(name);
        if(values == null){
            return new HashSet();
        }
        return values.values();
    }
    
    /**
     * �~�ό^���I���������̌������ʂ̃��R�[�h�����Ƀt�B���^�����O����B<p>
     * �~�ό^���I�����������s�����߂ɂ́A���R�[�h��~�ς���O�ɁA{@link #setDynamicSearchCondition(String, int[], boolean[])}�œ��I��������������ݒ肵�Ă����K�v������B<br>
     *
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     * @see #setDynamicSearchCondition(String, int[], boolean[])
     */
    public RecordSet filterDynamicCondition(){
        return filterDynamicCondition((String)null);
    }
    
    /**
     * �w�肳�ꂽ�������̒~�ό^���I���������̌������ʂ̃��R�[�h�����Ƀt�B���^�����O����B<p>
     * �~�ό^���I�����������s�����߂ɂ́A���R�[�h��~�ς���O�ɁA{@link #setDynamicSearchCondition(String, String, int[], boolean[])}�œ��I��������������ݒ肵�Ă����K�v������B<br>
     *
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     * @see #setDynamicSearchCondition(String, String, int[], boolean[])
     */
    public RecordSet filterDynamicCondition(String name){
        Collection records = searchDynamicCondition(name);
        if(size() != records.size()){
            clear();
            final Iterator itr = records.iterator();
            while(itr.hasNext()){
                addRecord((RowData)itr.next());
            }
        }
        return this;
    }
    
    /**
     * ���A���^���I�����������s���B<p>
     * {@link #searchDynamicConditionReal(String, int[], boolean[]) searchDynamicConditionReal(condition, (int[])null, (boolean[])null)}���Ăяo���̂Ɠ����B<br>
     *
     * @param condition ������
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     * @exception Exception ���������s���ȏꍇ
     * @see #searchDynamicConditionReal(String, int[], boolean[])
     */
    public Collection searchDynamicConditionReal(String condition) throws Exception{
        return searchDynamicConditionReal(
            condition,
            (int[])null,
            (boolean[])null
        );
    }
    
    /**
     * ���A���^���I�����������s���B<p>
     * {@link #searchDynamicConditionReal(String, int[], boolean[], Map) searchDynamicConditionReal(condition, (int[])null, (boolean[])null, valueMap)}���Ăяo���̂Ɠ����B<br>
     *
     * @param condition ������
     * @param valueMap ���������̕ϐ��}�b�v
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     * @exception Exception ���������s���ȏꍇ
     * @see #searchDynamicConditionReal(String, int[], boolean[], Map)
     */
    public Collection searchDynamicConditionReal(String condition, Map valueMap) throws Exception{
        return searchDynamicConditionReal(
            condition,
            (int[])null,
            (boolean[])null,
            valueMap
        );
    }
    
    /**
     * ���A���^���I���������i�\�[�g��w��t���j���s���B<p>
     * {@link #searchDynamicConditionReal(String, String[], boolean[]) searchDynamicConditionReal(condition, orderBy, (boolean[])null)}���Ăяo���̂Ɠ����B<br>
     *
     * @param condition ������
     * @param orderBy �\�[�g�񖼔z��
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     * @exception Exception ���������s���ȏꍇ
     * @see #searchDynamicConditionReal(String, String[], boolean[])
     */
    public Collection searchDynamicConditionReal(String condition, String[] orderBy) throws Exception{
        return searchDynamicConditionReal(
            condition,
            orderBy,
            (boolean[])null
        );
    }
    
    /**
     * ���A���^���I���������i�\�[�g��w��t���j���s���B<p>
     * {@link #searchDynamicConditionReal(String, String[], boolean[], Map) searchDynamicConditionReal(condition, orderBy, null, valueMap)}���Ăяo���̂Ɠ����B<br>
     *
     * @param condition ������
     * @param orderBy �\�[�g�񖼔z��
     * @param valueMap ���������̕ϐ��}�b�v
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     * @exception Exception ���������s���ȏꍇ
     * @see #searchDynamicConditionReal(String, String[], boolean[], Map)
     */
    public Collection searchDynamicConditionReal(String condition, String[] orderBy, Map valueMap) throws Exception{
        return searchDynamicConditionReal(
            condition,
            orderBy,
            (boolean[])null,
            valueMap
        );
    }
    
    /**
     * ���A���^���I���������i�\�[�g��w��t���j���s���B<p>
     * {@link #searchDynamicConditionReal(String, int[], boolean[]) searchDynamicConditionReal(condition, orderBy, (boolean[])null)}���Ăяo���̂Ɠ����B<br>
     *
     * @param condition ������
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     * @exception Exception ���������s���ȏꍇ
     * @see #searchDynamicConditionReal(String, int[], boolean[])
     */
    public Collection searchDynamicConditionReal(String condition, int[] orderBy) throws Exception{
        return searchDynamicConditionReal(
            condition,
            orderBy,
            (boolean[])null
        );
    }
    
    /**
     * ���A���^���I���������i�\�[�g��w��t���j���s���B<p>
     * {@link #searchDynamicConditionReal(String, int[], boolean[], Map) searchDynamicConditionReal(condition, orderBy, (boolean[])null, valueMap)}���Ăяo���̂Ɠ����B<br>
     *
     * @param condition ������
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @param valueMap ���������̕ϐ��}�b�v
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     * @exception Exception ���������s���ȏꍇ
     * @see #searchDynamicConditionReal(String, int[], boolean[], Map)
     */
    public Collection searchDynamicConditionReal(String condition, int[] orderBy, Map valueMap) throws Exception{
        return searchDynamicConditionReal(
            condition,
            orderBy,
            (boolean[])null,
            valueMap
        );
    }
    
    /**
     * ���A���^���I���������i�\�[�g��w��A�\�[�g���w��t���j���s���B<p>
     * {@link #searchDynamicConditionReal(String, String[], boolean[], Map) searchDynamicConditionReal(condition, orderBy, isAsc, null)}���Ăяo���̂Ɠ����B<br>
     *
     * @param condition ������
     * @param orderBy �\�[�g�񖼔z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     * @exception Exception ���������s���ȏꍇ
     * @see #searchDynamicConditionReal(String, String[], boolean[], Map)
     */
    public Collection searchDynamicConditionReal(String condition, String[] orderBy, boolean[] isAsc) throws Exception{
        return searchDynamicConditionReal(
            condition,
            orderBy,
            isAsc,
            null
        );
    }
    
    /**
     * ���A���^���I���������i�\�[�g��w��A�\�[�g���w��t���j���s���B<p>
     *
     * @param condition ������
     * @param orderBy �\�[�g�񖼔z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @param valueMap ���������̕ϐ��}�b�v
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     * @exception Exception ���������s���ȏꍇ
     * @see #searchDynamicConditionReal(String, int[], boolean[], Map)
     */
    public Collection searchDynamicConditionReal(String condition, String[] orderBy, boolean[] isAsc, Map valueMap) throws Exception{
        return searchDynamicConditionReal(
            condition,
            convertFromColNamesToColIndexes(orderBy),
            isAsc,
            valueMap
        );
    }
    
    /**
     * ���A���^���I���������i�\�[�g��w��A�\�[�g���w��t���j���s���B<p>
     * {@link #searchDynamicConditionReal(String, int[], boolean[], Map) searchDynamicConditionReal(condition, orderBy, isAsc, null)}���Ăяo���̂Ɠ����B<br>
     *
     * @param condition ������
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     * @exception Exception ���������s���ȏꍇ
     * @see #searchDynamicConditionReal(String, int[], boolean[], Map)
     */
    public Collection searchDynamicConditionReal(String condition, int[] orderBy, boolean[] isAsc) throws Exception{
        return searchDynamicConditionReal(
            condition,
            orderBy,
            isAsc,
            null
        );
    }
    
    /**
     * ���A���^���I���������i�\�[�g��w��A�\�[�g���w��t���j���s���B<p>
     * ���I���������Ƃ́ARecordSet�ɒ~�ς��ꂽ���R�[�h����A�������ɍ��v���郌�R�[�h����������@�\�ł���B<br>
     * �܂��A���I���������ɂ́A���R�[�h��~�ς���ۂɌ�������~�ό^�����ƁA�~�ς��ꂽ���R�[�h���烊�A���Ɍ������郊�A���^����������B<br>
     * ���A���^�����̗��_�́A���������ɁA���I�ɕς��ϐ����w�肵�A���̕ϐ��l������valueMap�ŗ^���鎖���ł��鎖�ł���B<br>
     * <p>
     * �������́A<a href="http://jakarta.apache.org/commons/jexl/">Jakarta Commons Jexl</a>�̎�������g�p����B<br>
     * ���A���^�����ł́A���R�[�h�̗�̒l���A�񖼂��w�肷�鎖�ŁA�����ŎQ�Ƃ��鎖���ł���̂ɉ����āA�C�ӂ̕ϐ����������ɒ�`���A���̒l������valueMap�ŗ^���鎖���ł���B<br>
     * <pre>
     *  ��FA == '1' and B &gt;= 3
     * </pre>
     *
     * @param condition ������
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @param valueMap ���������̕ϐ��}�b�v
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     * @exception Exception ���������s���ȏꍇ
     */
    public Collection searchDynamicConditionReal(String condition, int[] orderBy, boolean[] isAsc, Map valueMap) throws Exception{
        if(size() == 0){
            return new HashSet();
        }
        final Expression exp = ExpressionFactory.createExpression(condition);
        final Map result = createOrderByMap(orderBy, isAsc);
        final JexlContext context = JexlHelper.createContext();
        for(int i = 0, imax = size(); i < imax; i++){
            RowData rd = get(i);
            for(int j = 0, jmax = mSchema.size(); j < jmax; j++){
                final FieldSchema field = mSchema.get(j);
                final String fieldName = field.getFieldName();
                context.getVars().put(fieldName, rd.get(j));
            }
            if(valueMap != null){
                context.getVars().putAll(valueMap);
            }
            final Boolean ret = (Boolean)exp.evaluate(context);
            if(ret != null && ret.booleanValue()){
                result.put(rd, rd);
            }
        }
        return result.values();
    }
    
    /**
     * ���A���^���I���������̌������ʂ̃��R�[�h�����Ƀt�B���^�����O����B<p>
     * {@link #filterDynamicConditionReal(String, int[], boolean[]) filterDynamicConditionReal(condition, (int[])null, (boolean[])null)}���Ăяo���̂Ɠ����B<br>
     *
     * @param condition ������
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     * @see #filterDynamicConditionReal(String, int[], boolean[])
     */
    public RecordSet filterDynamicConditionReal(String condition) throws Exception{
        return filterDynamicConditionReal(condition, (int[])null);
    }
    
    /**
     * ���A���^���I���������̌������ʂ̃��R�[�h�����Ƀt�B���^�����O����B<p>
     * {@link #filterDynamicConditionReal(String, int[], boolean[], Map) filterDynamicConditionReal(condition, (int[])null, (boolean[])null, valueMap)}���Ăяo���̂Ɠ����B<br>
     *
     * @param condition ������
     * @param valueMap ���������̕ϐ��}�b�v
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     * @see #filterDynamicConditionReal(String, int[], boolean[], Map)
     */
    public RecordSet filterDynamicConditionReal(String condition, Map valueMap) throws Exception{
        return filterDynamicConditionReal(condition, (int[])null, valueMap);
    }
    
    /**
     * ���A���^���I���������̌������ʂ̃��R�[�h�����Ƀt�B���^�����O����B<p>
     * {@link #filterDynamicConditionReal(String, String[], boolean[]) filterDynamicConditionReal(condition, orderBy, (boolean[])null)}���Ăяo���̂Ɠ����B<br>
     *
     * @param condition ������
     * @param orderBy �\�[�g�񖼔z��
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     * @see #filterDynamicConditionReal(String, String[], boolean[])
     */
    public RecordSet filterDynamicConditionReal(String condition, String[] orderBy) throws Exception{
        return filterDynamicConditionReal(
            condition,
            orderBy,
            (boolean[])null
        );
    }
    
    /**
     * ���A���^���I���������̌������ʂ̃��R�[�h�����Ƀt�B���^�����O����B<p>
     * {@link #filterDynamicConditionReal(String, String[], boolean[], Map) filterDynamicConditionReal(condition, orderBy, (boolean[])null, valueMap)}���Ăяo���̂Ɠ����B<br>
     *
     * @param condition ������
     * @param orderBy �\�[�g�񖼔z��
     * @param valueMap ���������̕ϐ��}�b�v
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     * @see #filterDynamicConditionReal(String, String[], boolean[], Map)
     */
    public RecordSet filterDynamicConditionReal(String condition, String[] orderBy, Map valueMap) throws Exception{
        return filterDynamicConditionReal(
            condition,
            orderBy,
            (boolean[])null,
            valueMap
        );
    }
    
    /**
     * ���A���^���I���������̌������ʂ̃��R�[�h�����Ƀt�B���^�����O����B<p>
     * {@link #filterDynamicConditionReal(String, int[], boolean[]) filterDynamicConditionReal(condition, orderBy, (boolean[])null)}���Ăяo���̂Ɠ����B<br>
     *
     * @param condition ������
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     * @see #filterDynamicConditionReal(String, int[], boolean[])
     */
    public RecordSet filterDynamicConditionReal(String condition, int[] orderBy) throws Exception{
        return filterDynamicConditionReal(
            condition,
            orderBy,
            (boolean[])null
        );
    }
    
    /**
     * ���A���^���I���������̌������ʂ̃��R�[�h�����Ƀt�B���^�����O����B<p>
     * {@link #filterDynamicConditionReal(String, int[], boolean[], Map) filterDynamicConditionReal(condition, orderBy, (boolean[])null, valueMap)}���Ăяo���̂Ɠ����B<br>
     *
     * @param condition ������
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @param valueMap ���������̕ϐ��}�b�v
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     * @see #filterDynamicConditionReal(String, int[], boolean[], Map)
     */
    public RecordSet filterDynamicConditionReal(String condition, int[] orderBy, Map valueMap) throws Exception{
        return filterDynamicConditionReal(
            condition,
            orderBy,
            (boolean[])null,
            valueMap
        );
    }
    
    /**
     * ���A���^���I���������̌������ʂ̃��R�[�h�����Ƀt�B���^�����O����B<p>
     * {@link #filterDynamicConditionReal(String, String[], boolean[], Map) filterDynamicConditionReal(condition, orderBy, isAsc, null)}���Ăяo���̂Ɠ����B<br>
     *
     * @param condition ������
     * @param orderBy �\�[�g�񖼔z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     * @see #filterDynamicConditionReal(String, String[], boolean[], Map)
     */
    public RecordSet filterDynamicConditionReal(String condition, String[] orderBy, boolean[] isAsc) throws Exception{
        return filterDynamicConditionReal(
            condition,
            orderBy,
            isAsc,
            null
        );
    }
    
    /**
     * ���A���^���I���������̌������ʂ̃��R�[�h�����Ƀt�B���^�����O����B<p>
     *
     * @param condition ������
     * @param orderBy �\�[�g�񖼔z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @param valueMap ���������̕ϐ��}�b�v
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     * @see #filterDynamicConditionReal(String, int[], boolean[], Map)
     */
    public RecordSet filterDynamicConditionReal(String condition, String[] orderBy, boolean[] isAsc, Map valueMap) throws Exception{
        return filterDynamicConditionReal(
            condition,
            convertFromColNamesToColIndexes(orderBy),
            isAsc,
            valueMap
        );
    }
    
    /**
     * ���A���^���I���������̌������ʂ̃��R�[�h�����Ƀt�B���^�����O����B<p>
     * {@link #filterDynamicConditionReal(String, int[], boolean[], Map) filterDynamicConditionReal(condition, orderBy, isAsc, null)}���Ăяo���̂Ɠ����B<br>
     *
     * @param condition ������
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     * @see #filterDynamicConditionReal(String, int[], boolean[], Map)
     */
    public RecordSet filterDynamicConditionReal(String condition, int[] orderBy, boolean[] isAsc) throws Exception{
        return filterDynamicConditionReal(
            condition,
            orderBy,
            isAsc,
            null
        );
    }
    
    /**
     * ���A���^���I���������̌������ʂ̃��R�[�h�����Ƀt�B���^�����O����B<p>
     *
     * @param condition ������
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @param valueMap ���������̕ϐ��}�b�v
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     * @see #searchDynamicConditionReal(String, int[], boolean[], Map)
     */
    public RecordSet filterDynamicConditionReal(String condition, int[] orderBy, boolean[] isAsc, Map valueMap) throws Exception{
        Collection records = searchDynamicConditionReal(condition, orderBy, isAsc, valueMap);
        if(size() != records.size()){
            clear();
            final Iterator itr = records.iterator();
            while(itr.hasNext()){
                addRecord((RowData)itr.next());
            }
        }
        return this;
    }
    
    /**
     * ���I�L�[�����̌������ʂ��擾����B<p>
     * {@link #searchDynamicKey(String, RowData) searchDynamicKey(null, key, (int[])null, (boolean[])null)}���Ăяo���̂Ɠ����B<br>
     *
     * @param key �����L�[
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     * @see #searchDynamicKey(String, RowData)
     */
    public Collection searchDynamicKey(RowData key){
        return searchDynamicKey(null, key);
    }
    
    /**
     * ���������w�肵�āA���I�L�[�����̌������ʂ��擾����B<p>
     * ���I�L�[�����Ƃ́ARecordSet�ɒ~�ς��ꂽ���R�[�h����A�w�肳�ꂽ��i�����j�̒l�����v���郌�R�[�h����������@�\�ł���B<br>
     * �܂��A���I�L�[�����́A���R�[�h��~�ς���ۂɌ������s�����߁A���R�[�h��~�ς���O�ɁA{@link #setDynamicSearchKey(String, int[], int[], boolean[])}�ŏ�����ݒ肵�Ă����Ȃ���΂Ȃ�Ȃ��B<br>
     *
     * @param name ������
     * @param key �����L�[
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     * @see #setDynamicSearchKey(String, int[], int[], boolean[])
     */
    public Collection searchDynamicKey(String name, RowData key){
        if(dynamicSearchKeyMap == null || dynamicSearchMap == null){
            return new HashSet();
        }
        final DynamicSearchKeyValue keyValue
             = (DynamicSearchKeyValue)dynamicSearchKeyMap.get(name);
        if(keyValue == null || keyValue.colIndexes == null){
            return new HashSet();
        }
        final Map map = (Map)dynamicSearchMap.get(keyValue);
        if(map == null){
            return new HashSet();
        }
        final Object values = map.get(
            key.getKey(keyValue.colIndexes)
        );
        if(values == null){
            return new HashSet();
        }
        if(values instanceof Map){
            return ((Map)values).values();
        }else{
            final Set result = new HashSet();
            result.add(values);
            return result;
        }
    }
    
    /**
     * ���������w�肵�āA���I�L�[�����̌������ʁi�\�[�g��w��j���擾����B<p>
     *
     * @param name ������
     * @param key �����L�[
     * @param orderBy �\�[�g�񖼔z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     * @see #setDynamicSearchKey(String, int[], int[], boolean[])
     * @see #searchDynamicKey(String, RowData)
     */
    public Collection searchDynamicKey(
        String name,
        RowData key,
        String[] orderBy,
        boolean[] isAsc
    ){
        return searchDynamicKey(
            name,
            key,
            convertFromColNamesToColIndexes(orderBy),
            isAsc
        );
    }
    
    /**
     * ���������w�肵�āA���I�L�[�����̌������ʁi�\�[�g��w��j���擾����B<p>
     *
     * @param name ������
     * @param key �����L�[
     * @param orderBy �\�[�g�񖼔z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     * @see #setDynamicSearchKey(String, int[], int[], boolean[])
     * @see #searchDynamicKey(String, RowData)
     */
    public Collection searchDynamicKey(
        String name,
        RowData key,
        int[] orderBy,
        boolean[] isAsc
    ){
        final Collection collection = searchDynamicKey(name, key);
        if(collection.size() < 2){
            return collection;
        }
        final List rows = new ArrayList(collection);
        Comparator comp = createOrderByComparator(orderBy, isAsc);
        if(comp == null){
            if(mSchema == null){
                throw new InvalidDataException("Schema not initalize.");
            }
            int[] colIndexes = new int[mSchema.getUniqueKeySize()];
            for(int i = 0; i < colIndexes.length; i++){
                colIndexes[i] = mSchema.getUniqueFieldSchema(i).getIndex();
            }
            comp = new RowDataComparator(mSchema, colIndexes, isAsc);
        }
        Collections.sort(rows, comp);
        return rows;
    }
    
    /**
     * ���I�L�[�����̌������ʂ̃��R�[�h�����Ƀt�B���^�����O����B<p>
     * {@link #filterDynamicKey(String, RowData) filterDynamicKey(null, key)}���Ăяo���̂Ɠ����B<br>
     *
     * @param key �����L�[
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     * @see #filterDynamicKey(String, RowData)
     */
    public RecordSet filterDynamicKey(RowData key){
        return filterDynamicKey(null, key);
    }
    
    /**
     * ���I�L�[�����̌������ʂ̃��R�[�h�����Ƀt�B���^�����O����B<p>
     *
     * @param name ������
     * @param key �����L�[
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     * @see #searchDynamicKey(String, RowData)
     */
    public RecordSet filterDynamicKey(String name, RowData key){
        Collection records = searchDynamicKey(name, key);
        if(size() != records.size()){
            clear();
            final Iterator itr = records.iterator();
            while(itr.hasNext()){
                addRecord((RowData)itr.next());
            }
        }
        return this;
    }
    
    /**
     * ���̃��R�[�h�Z�b�g�̃v���C�}���L�[�ƁA�w�肳�ꂽ���R�[�h�Z�b�g�̊Y������L�[����v���郌�R�[�h���擾����B<p>
     *
     * @param recset ���R�[�h�Z�b�g
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     */
    public Collection searchRecords(RecordSet recset){
        return searchRecords(
            recset,
            (int[])null
        );
    }
    
    /**
     * ���̃��R�[�h�Z�b�g�̃v���C�}���L�[�ƁA�w�肳�ꂽ���R�[�h�Z�b�g�̊Y������L�[����v���郌�R�[�h���擾����B<p>
     *
     * @param recset ���R�[�h�Z�b�g
     * @param orderBy �\�[�g�񖼔z��
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     */
    public Collection searchRecords(RecordSet recset, String[] orderBy){
        return searchRecords(
            recset,
            orderBy,
            (boolean[])null
        );
    }
    
    /**
     * ���̃��R�[�h�Z�b�g�̃v���C�}���L�[�ƁA�w�肳�ꂽ���R�[�h�Z�b�g�̊Y������L�[����v���郌�R�[�h���擾����B<p>
     *
     * @param recset ���R�[�h�Z�b�g
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     */
    public Collection searchRecords(RecordSet recset, int[] orderBy){
        return searchRecords(
            recset,
            orderBy,
            (boolean[])null
        );
    }
    
    /**
     * ���̃��R�[�h�Z�b�g�̃v���C�}���L�[�ƁA�w�肳�ꂽ���R�[�h�Z�b�g�̊Y������L�[����v���郌�R�[�h���擾����B<p>
     *
     * @param recset ���R�[�h�Z�b�g
     * @param orderBy �\�[�g�񖼔z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     */
    public Collection searchRecords(RecordSet recset, String[] orderBy, boolean[] isAsc){
        return searchRecords(
            recset,
            convertFromColNamesToColIndexes(orderBy),
            isAsc
        );
    }
    
    /**
     * ���̃��R�[�h�Z�b�g�̃v���C�}���L�[�ƁA�w�肳�ꂽ���R�[�h�Z�b�g�̊Y������L�[����v���郌�R�[�h���擾����B<p>
     *
     * @param recset ���R�[�h�Z�b�g
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @return �������ʁB�����ɍ��v����RowData�̏W��
     */
    public Collection searchRecords(RecordSet recset, int[] orderBy, boolean[] isAsc){
        if(recset == null || recset.size() == 0){
            return new HashSet();
        }
        if(mSchema == null){
            throw new InvalidDataException("Schema not initalize.");
        }
        final RowSchema keySchema = recset.getRowSchema();
        final int uniqueKeySize = mSchema.getUniqueKeySize();
        final int[] uniqueKeyIndexes = new int[uniqueKeySize];
        for(int i = 0; i < uniqueKeySize; i++){
            final String colName
                 = mSchema.getUniqueFieldSchema(i).getFieldName();
            final FieldSchema field = keySchema.get(colName);
            if(field == null){
                return new HashSet();
            }
            uniqueKeyIndexes[i] = field.getIndex();
        }
        final Map records = createOrderByMap(orderBy, isAsc);
        for(int i = 0, imax = recset.size(); i < imax; i++){
            final RowData key = recset.get(i);
            RowData rec = get(key.getKey(uniqueKeyIndexes));
            if(rec != null){
                records.put(rec, rec);
            }
        }
        return records.values();
    }
    
    /**
     * ���̃��R�[�h�Z�b�g�̃v���C�}���L�[�ƁA�w�肳�ꂽ���R�[�h�Z�b�g�̊Y������L�[����v���郌�R�[�h�����Ƀt�B���^�����O����B<p>
     *
     * @param recset ���R�[�h�Z�b�g
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     */
    public RecordSet filterRecords(RecordSet recset){
        return filterRecords(
            recset,
            (int[])null
        );
    }
    
    /**
     * ���̃��R�[�h�Z�b�g�̃v���C�}���L�[�ƁA�w�肳�ꂽ���R�[�h�Z�b�g�̊Y������L�[����v���郌�R�[�h�����Ƀt�B���^�����O����B<p>
     *
     * @param recset ���R�[�h�Z�b�g
     * @param orderBy �\�[�g�񖼔z��
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     */
    public RecordSet filterRecords(RecordSet recset, String[] orderBy){
        return filterRecords(
            recset,
            orderBy,
            (boolean[])null
        );
    }
    
    /**
     * ���̃��R�[�h�Z�b�g�̃v���C�}���L�[�ƁA�w�肳�ꂽ���R�[�h�Z�b�g�̊Y������L�[����v���郌�R�[�h�����Ƀt�B���^�����O����B<p>
     *
     * @param recset ���R�[�h�Z�b�g
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     */
    public RecordSet filterRecords(RecordSet recset, int[] orderBy){
        return filterRecords(
            recset,
            orderBy,
            (boolean[])null
        );
    }
    
    /**
     * ���̃��R�[�h�Z�b�g�̃v���C�}���L�[�ƁA�w�肳�ꂽ���R�[�h�Z�b�g�̊Y������L�[����v���郌�R�[�h�����Ƀt�B���^�����O����B<p>
     *
     * @param recset ���R�[�h�Z�b�g
     * @param orderBy �\�[�g�񖼔z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     */
    public RecordSet filterRecords(RecordSet recset, String[] orderBy, boolean[] isAsc){
        return filterRecords(
            recset,
            convertFromColNamesToColIndexes(orderBy),
            isAsc
        );
    }
    
    /**
     * ���̃��R�[�h�Z�b�g�̃v���C�}���L�[�ƁA�w�肳�ꂽ���R�[�h�Z�b�g�̊Y������L�[����v���郌�R�[�h�����Ƀt�B���^�����O����B<p>
     *
     * @param recset ���R�[�h�Z�b�g
     * @param orderBy �\�[�g��C���f�b�N�X�z��
     * @param isAsc �\�[�g���Btrue�̏ꍇ�A����
     * @return �t�B���^�����O��́A���̃I�u�W�F�N�g���g�̎Q��
     */
    public RecordSet filterRecords(RecordSet recset, int[] orderBy, boolean[] isAsc){
        Collection records = searchRecords(recset, orderBy, isAsc);
        if(size() != records.size()){
            clear();
            final Iterator itr = records.iterator();
            while(itr.hasNext()){
                addRecord((RowData)itr.next());
            }
        }
        return this;
    }
    
    /**
     * �s�f�[�^�̃��X�g���擾����B<p>
     *
     * @return {@link RowData}��v�f�Ƃ��郊�X�g
     */
    protected ArrayList getList(){
        return mRows;
    }
    
    /**
     * �s�f�[�^���v���C�}���L�[������Ń}�b�s���O���Ă���}�b�v���擾����B<p>
     * 
     * @return �v���C�}���L�[�������{@link RowData}�Ń}�b�s���O���ꂽHashMap
     */
    protected HashMap getHash(){
        return mHash;
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃��R�[�h���擾����B<p>
     * 
     * @param index
     * @return ���R�[�h
     */
    public RowData get(int index){
        return (RowData)mRows.get(index);
    }
    
    /**
     * �w�肳�ꂽ�v���C�}���L�[������ɊY�����郌�R�[�h���擾����B<p>
     * 
     * @param key �v���C�}���L�[������
     * @return ���R�[�h
     */
    public RowData get(String key){
        return (RowData)mHash.get(key);
    }
    
    /**
     * �w�肳�ꂽ�v���C�}���L�[���R�[�h�ɊY�����郌�R�[�h���擾����B<p>
     * 
     * @param key �v���C�}���L�[���R�[�h
     * @return ���R�[�h
     */
    public RowData get(RowData key){
        return get(key.getKey());
    }
    
    /**
     * ���R�[�h�̔z����擾����B<p>
     *
     * @return ���R�[�h�̔z��
     */
    public RowData[] toArray(){
        return (RowData[])mRows.toArray(new RowData[mRows.size()]);
    }
    
    /**
     * ���R�[�h�̃��X�g���擾����B<p>
     *
     * @return ���R�[�h�̃��X�g
     */
    public List toList(){
        return new ArrayList(mRows);
    }
    
    /**
     * �~�ς���Ă��郌�R�[�h�����擾����B<p>
     * 
     * @return ���R�[�h��
     */
    public int size(){
        return mRows.size();
    }
    
    /**
     * �v���C�}���L�[������̏����Ń\�[�g����B<p>
     */
    public void sort(){
        sort((int[])null);
    }
    
    /**
     * �w�肳�ꂽ�񖼂̗���\�[�g�L�[�ɂ��āA�����Ń\�[�g����B<p>
     *
     * @param orderBy �\�[�g�L�[�ƂȂ�񖼔z��
     */
    public void sort(String[] orderBy){
        sort(orderBy, null);
    }
    
    /**
     * �w�肳�ꂽ��C���f�b�N�X�̗���\�[�g�L�[�ɂ��āA�����Ń\�[�g����B<p>
     *
     * @param orderBy �\�[�g�L�[�ƂȂ��C���f�b�N�X�z��
     */
    public void sort(int[] orderBy){
        sort(orderBy, null);
    }
    
    /**
     * �w�肳�ꂽ�񖼂̗���\�[�g�L�[�ɂ��ă\�[�g����B<p>
     *
     * @param orderBy �\�[�g�L�[�ƂȂ�񖼔z��
     * @param isAsc �����\�[�g����ꍇ��true�B�~���\�[�g����ꍇ�́Afalse
     */
    public void sort(String[] orderBy, boolean[] isAsc){
        sort(convertFromColNamesToColIndexes(orderBy), isAsc);
    }
    
    /**
     * �w�肳�ꂽ��C���f�b�N�X�̗���\�[�g�L�[�ɂ��ă\�[�g����B<p>
     *
     * @param orderBy �\�[�g�L�[�ƂȂ��C���f�b�N�X�z��
     * @param isAsc �����\�[�g����ꍇ��true�B�~���\�[�g����ꍇ�́Afalse
     */
    public void sort(int[] orderBy, boolean[] isAsc){
        sort(orderBy, isAsc, true);
    }
    
    /**
     * �w�肳�ꂽ�񖼂̗���\�[�g�L�[�ɂ��ă\�[�g����B<p>
     *
     * @param orderBy �\�[�g�L�[�ƂȂ�񖼔z��
     * @param isAsc �����\�[�g����ꍇ��true�B�~���\�[�g����ꍇ�́Afalse
     * @param isSetRowNum �s�ԍ���ݒ肵�����ꍇ�́Atrue
     */
    public void sort(String[] orderBy, boolean[] isAsc, boolean isSetRowNum){
        sort(convertFromColNamesToColIndexes(orderBy), isAsc, isSetRowNum);
    }
    
    /**
     * �w�肳�ꂽ��C���f�b�N�X�̗���\�[�g�L�[�ɂ��ă\�[�g����B<p>
     *
     * @param orderBy �\�[�g�L�[�ƂȂ��C���f�b�N�X�z��
     * @param isAsc �����\�[�g����ꍇ��true�B�~���\�[�g����ꍇ�́Afalse
     * @param isSetRowNum �s�ԍ���ݒ肵�����ꍇ�́Atrue
     */
    public void sort(int[] orderBy, boolean[] isAsc, boolean isSetRowNum){
        if(mRows.size() < 2){
            return;
        }
        Comparator comp = createOrderByComparator(orderBy, isAsc);
        if(comp == null){
            if(mSchema == null){
                throw new InvalidDataException("Schema not initalize.");
            }
            int[] colIndexes = new int[mSchema.getUniqueKeySize()];
            for(int i = 0; i < colIndexes.length; i++){
                colIndexes[i] = mSchema.getUniqueFieldSchema(i).getIndex();
            }
            comp = new RowDataComparator(mSchema, colIndexes, isAsc);
        }
        Collections.sort(mRows, comp);
        if(isSetRowNum){
            for(int i = 0, imax = mRows.size(); i < imax; i++){
                ((RowData)mRows.get(i)).setRowIndex(i);
            }
        }
    }
    
    /**
     * ���̃��R�[�h�Z�b�g��{@link RowData}����L�[�Ń\�[�g����Comparator�𐶐�����B<p>
     *
     * @return ���̃��R�[�h�Z�b�g��RowData���\�[�g����Comparator
     */
    public Comparator createRowComparator(){
        if(mSchema == null){
            throw new InvalidDataException("Schema not initalize.");
        }
        final int uniqueKeySize = mSchema.getUniqueKeySize();
        if(uniqueKeySize == 0){
            throw new InvalidDataException("Unique key not found.");
        }
        int[] orderBy = new int[uniqueKeySize];
        for(int i = 0; i < uniqueKeySize; i++){
            final FieldSchema fieldSchema = mSchema.getUniqueFieldSchema(i);
            orderBy[i] = fieldSchema.getIndex();
        }
        return createRowComparator(orderBy);
    }
    
    /**
     * ���̃��R�[�h�Z�b�g��{@link RowData}���\�[�g����Comparator�𐶐�����B<p>
     *
     * @param orderBy �\�[�g�L�[�ƂȂ�񖼔z��
     * @return ���̃��R�[�h�Z�b�g��RowData���\�[�g����Comparator
     */
    public Comparator createRowComparator(String[] orderBy){
        return createRowComparator(orderBy, null);
    }
    
    /**
     * ���̃��R�[�h�Z�b�g��{@link RowData}���\�[�g����Comparator�𐶐�����B<p>
     *
     * @param orderBy �\�[�g�L�[�ƂȂ��C���f�b�N�X�z��
     * @return ���̃��R�[�h�Z�b�g��RowData���\�[�g����Comparator
     */
    public Comparator createRowComparator(int[] orderBy){
        return createRowComparator(orderBy, null);
    }
    
    /**
     * ���̃��R�[�h�Z�b�g��{@link RowData}���\�[�g����Comparator�𐶐�����B<p>
     *
     * @param orderBy �\�[�g�L�[�ƂȂ�񖼔z��
     * @param isAsc �����\�[�g����ꍇ��true�B�~���\�[�g����ꍇ�́Afalse
     * @return ���̃��R�[�h�Z�b�g��RowData���\�[�g����Comparator
     */
    public Comparator createRowComparator(String[] orderBy, boolean[] isAsc){
        return createRowComparator(convertFromColNamesToColIndexes(orderBy), isAsc);
    }
    
    /**
     * ���̃��R�[�h�Z�b�g��{@link RowData}���\�[�g����Comparator�𐶐�����B<p>
     *
     * @param orderBy �\�[�g�L�[�ƂȂ��C���f�b�N�X�z��
     * @param isAsc �����\�[�g����ꍇ��true�B�~���\�[�g����ꍇ�́Afalse
     * @return ���̃��R�[�h�Z�b�g��RowData���\�[�g����Comparator
     */
    public Comparator createRowComparator(int[] orderBy, boolean[] isAsc){
        return new RowDataComparator(mSchema, orderBy, isAsc);
    }
    
    /**
     * �ǉ��E�C���E�폜���ꂽRowData���܂�RecordSet���擾����B<p>
     * 
     * @return �ǉ��E�C���E�폜���ꂽRowData���܂�RecordSet
     */
    public RecordSet makeGoneData(){
        RecordSet ret = cloneEmpty();
        for(int i = size(); --i >= 0;){
            RowData rd = get(i);
            int tmode = rd.getTransactionMode();
            if(tmode == RowData.E_Record_TypeDelete
                 || tmode == RowData.E_Record_TypeInsert
                 || tmode == RowData.E_Record_TypeUpdate
                 || tmode == RowData.E_Record_TypeDeleteInsert
            ){
                ret.addRecord(rd.makeGoneData(ret.mSchema));
            }
        }
        return ret;
    }
    
    /**
     * ���R�[�h��}������B<p>
     * �}�����ꂽ���R�[�h�̃g�����U�N�V�������[�h�́A{@link RowData#E_Record_TypeInsert}�ɂȂ�B<br>
     * 
     * @param rd �}������RowData
     * @exception InvalidDataException �v���C�}���L�[���d������ꍇ
     */
    public void insertRecord(RowData rd){
        rd.setTransactionMode(RowData.E_Record_TypeInsert);
        addRecord(rd);
    }
    
    /**
     * ���R�[�h��ǉ�����B<p>
     * �ǉ����ꂽ���R�[�h�̃g�����U�N�V�������[�h�́A�ύX����Ȃ��B<br>
     * 
     * @param rd �ǉ�����RowData
     * @exception InvalidDataException �v���C�}���L�[���d������ꍇ
     */
    public void addRecord(RowData rd){
        String key = rd.getKey() ;
        if(rd.mRowSchema != mSchema && mSchema.equals(rd.mRowSchema)){
            rd.mRowSchema = mSchema;
        }
        if(key != null && key.length() > 0){
            Object tmp = this.mHash.get(key);
            if(tmp == null){
                mHash.put(key, rd);
                mRows.add(rd);
            }else{
                RowData tmpRd = (RowData)tmp;
                if(tmpRd.getTransactionMode() == RowData.E_Record_TypeDelete
                     && rd.getTransactionMode() == RowData.E_Record_TypeInsert){
                    //�ȑO�ݒ�̃��R�[�h��DELETE�A�V�K�ǉ���INSERT�̏ꍇ
                    //�s�f�[�^�̓���ւ�
                    mHash.remove(key);
                    mRows.remove(tmpRd);
                    mHash.put(key, rd);
                    mRows.add(rd);
                    //�g�����U�N�V�������[�h�̕ύX
                    rd.setTransactionModeForce(RowData.E_Record_TypeDeleteInsert);
                    //�ȑO�ݒ�̃��R�[�h�C���f�b�N�X��ݒ�
                    rd.setRowIndex(tmpRd.getRowIndex());
                }else if(tmpRd.getTransactionMode() == RowData.E_Record_TypeInsert
                     && rd.getTransactionMode() == RowData.E_Record_TypeDelete){
                    //�ȑO�ݒ�̃��R�[�h��INSERT�A�V�K�ǉ���DELETE�̏ꍇ
                    //�g�����U�N�V�������[�h�̕ύX
                    tmpRd.setTransactionModeForce(RowData.E_Record_TypeDeleteInsert);
                }else{
                    throw new InvalidDataException("key duplicate") ;
                }
            }
        }else{
            this.mRows.add(rd);
        }
        rd.setRowIndex(this.mRows.size() - 1);
        if(dynamicSearchKeyMap != null && dynamicSearchKeyMap.size() != 0){
            if(dynamicSearchMap == null){
                dynamicSearchMap = new HashMap();
            }
            final Iterator entries = dynamicSearchKeyMap.entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                final DynamicSearchKeyValue keyValue = (DynamicSearchKeyValue)entry.getValue();
                Map map = (Map)dynamicSearchMap.get(keyValue);
                if(map == null){
                    map = new HashMap();
                    dynamicSearchMap.put(keyValue, map);
                }
                final String myKey = rd.getKey(keyValue.colIndexes);
                Object values = map.get(myKey);
                if(values == null){
                    map.put(myKey, rd);
                }else{
                    if(values instanceof Map){
                        ((Map)values).put(rd.getKey(), rd);
                    }else{
                        final Map valMap = createOrderByMap(
                            keyValue.orderBy,
                            keyValue.isAsc
                        );
                        valMap.put(((RowData)values).getKey(), values);
                        valMap.put(rd.getKey(), rd);
                        map.put(myKey, valMap);
                    }
                }
            }
        }
        if(dynamicSearchConditionMap != null
             && dynamicSearchConditionMap.size() != 0){
            final Iterator entries
                 = dynamicSearchConditionMap.entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                final String name = (String)entry.getKey();
                final Expression exp = (Expression)entry.getValue();
                final JexlContext context = JexlHelper.createContext();
                for(int i = 0, imax = mSchema.size(); i < imax; i++){
                    final FieldSchema field = mSchema.get(i);
                    final String fieldName = field.getFieldName();
                    context.getVars().put(fieldName, rd.get(i));
                }
                Boolean ret = null;
                try{
                    ret = (Boolean)exp.evaluate(context);
                }catch(Exception e){
                    // �N����Ȃ��͂�
                    e.printStackTrace();
                }
                if(ret != null && ret.booleanValue()){
                    Map values = (Map)dynamicSearchConditionResultMap.get(name);
                    values.put(rd, rd);
                }
            }
        }
    }
    
    /**
     * ���R�[�h�����ւ���B<p>
     * ����ւ���ꂽ���R�[�h�̃g�����U�N�V�������[�h�́A�ύX����Ȃ��B<br>
     * 
     * @param rd ����ւ���RowData
     */
    public void setRecord(RowData rd){
        String key = rd.getKey();
        if(rd.mRowSchema != mSchema && mSchema.equals(rd.mRowSchema)){
            rd.mRowSchema = mSchema;
        }
        if(key != null && key.length() > 0){
            Object tmp = this.mHash.get(key);
            if(tmp == null){
                mHash.put(key, rd);
                mRows.add(rd);
            }else{
                RowData tmpRd = (RowData)tmp;
                mHash.remove(key);
                mRows.remove(tmpRd.getRowIndex());
                mHash.put(key, rd);
                mRows.add(rd);
                rd.setRowIndex(tmpRd.getRowIndex());
            }
        }else{
            this.mRows.add(rd);
        }
        rd.setRowIndex(this.mRows.size() - 1);
        if(dynamicSearchKeyMap != null && dynamicSearchKeyMap.size() != 0){
            if(dynamicSearchMap == null){
                dynamicSearchMap = new HashMap();
            }
            final Iterator entries = dynamicSearchKeyMap.entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                final DynamicSearchKeyValue keyValue = (DynamicSearchKeyValue)entry.getValue();
                Map map = (Map)dynamicSearchMap.get(keyValue);
                if(map == null){
                    map = new HashMap();
                    dynamicSearchMap.put(keyValue, map);
                }
                final String myKey = rd.getKey(keyValue.colIndexes);
                Object values = map.get(myKey);
                if(values == null){
                    map.put(myKey, rd);
                }else{
                    if(values instanceof Map){
                        ((Map)values).put(rd.getKey(), rd);
                    }else{
                        final Map valMap = createOrderByMap(
                            keyValue.orderBy,
                            keyValue.isAsc
                        );
                        valMap.put(((RowData)values).getKey(), values);
                        valMap.put(rd.getKey(), rd);
                        map.put(myKey, valMap);
                    }
                }
            }
        }
        if(dynamicSearchConditionMap != null
             && dynamicSearchConditionMap.size() != 0){
            final Iterator entries
                 = dynamicSearchConditionMap.entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                final String name = (String)entry.getKey();
                final Expression exp = (Expression)entry.getValue();
                final JexlContext context = JexlHelper.createContext();
                for(int i = 0, imax = mSchema.size(); i < imax; i++){
                    final FieldSchema field = mSchema.get(i);
                    final String fieldName = field.getFieldName();
                    context.getVars().put(fieldName, rd.get(i));
                }
                Boolean ret = null;
                try{
                    ret = (Boolean)exp.evaluate(context);
                }catch(Exception e){
                    // �N����Ȃ��͂�
                    e.printStackTrace();
                }
                if(ret != null && ret.booleanValue()){
                    Map values = (Map)dynamicSearchConditionResultMap.get(name);
                    values.put(rd, rd);
                }
            }
        }
    }
    
    /**
     * �S�Ẵ��R�[�h��ǉ�����B<p>
     * �ǉ����ꂽ���R�[�h�̃g�����U�N�V�������[�h�́A�ύX����Ȃ��B<br>
     * 
     * @param recset �ǉ�����RecordSet
     * @exception InvalidDataException �v���C�}���L�[���d������ꍇ
     */
    public void addAllRecord(RecordSet recset){
        if(recset == null || recset.size() == 0){
            return;
        }
        if(mSchema == null){
            throw new InvalidDataException("Schema not initalize.");
        }
        if(!mSchema.equals(recset.getRowSchema())){
            throw new InvalidDataException("Schema is unmatch.");
        }
        for(int i = 0, imax = recset.size(); i < imax; i++){
            addRecord(recset.get(i));
        }
    }
    
    /**
     * �~�ς��ꂽ�f�[�^���N���A����B<p>
     * �ȉ��̃f�[�^���폜�����B<br>
     * <ul>
     *   <li>���R�[�h</li>
     *   <li>PreparedStatement�ɖ��ߍ��ރf�[�^</li>
     *   <li>�~�ό^���I���������̌�������</li>
     *   <li>���I�L�[�����̌�������</li>
     * </ul>
     */
    public void clear(){
        mRows.clear();
        mHash.clear();
        if(bindDatas != null){
            bindDatas.clear();
        }
        if(dynamicSearchConditionResultMap != null
             && dynamicSearchConditionResultMap.size() != 0){
            final Iterator itr
                 = dynamicSearchConditionResultMap.values().iterator();
            while(itr.hasNext()){
                ((Map)itr.next()).clear();
            }
        }
        if(dynamicSearchMap != null){
            dynamicSearchMap.clear();
        }
    }
    
    /**
     * �ǉ��E�C���E�폜���ꂽRowData���f�[�^�x�[�X�ɔ��f����B<p>
     * 
     * @exception RowVersionException {@link #isEnabledRowVersionCheck()}��true�ŁA�X�V�y�э폜���ɁA�X�V�y�э폜���悤�Ƃ��������ƁA���ۂɍX�V�y�э폜�����������������Ȃ��ꍇ
     * @exception SQLException 
     * @deprecated {@link #updateRecords()}�ɒu���������܂����B
     */
    public void updateRecord() throws SQLException, RowVersionException {
        updateRecords();
    }
    
    /**
     * �ǉ��E�C���E�폜���ꂽRowData���f�[�^�x�[�X�ɔ��f����B<p>
     * 
     * @return �ǉ��E�C���E�폜���ꂽ���R�[�h��
     * @exception RowVersionException {@link #isEnabledRowVersionCheck()}��true�ŁA�X�V�y�э폜���ɁA�X�V�y�э폜���悤�Ƃ��������ƁA���ۂɍX�V�y�э폜�����������������Ȃ��ꍇ
     * @exception SQLException 
     */
    public int updateRecords() throws SQLException, RowVersionException{
        
        int result = 0;
        int updateCnt = 0;
        int insertCnt = 0;
        int deleteCnt = 0;
        for(int i = 0, imax = size(); i < imax; i++){
            RowData rd = get(i);
            switch(rd.getTransactionMode()){
            case  RowData.E_Record_TypeInsert:
                insertCnt++;
                break ;
            case RowData.E_Record_TypeDelete:
                deleteCnt++;
                break ;
            case RowData.E_Record_TypeUpdate:
                updateCnt++ ;
                break ;
            case RowData.E_Record_TypeDeleteInsert:
                deleteCnt++;
                insertCnt++;
                break;
            }
        }
        PreparedStatement psDelete = null;
        PreparedStatement psInsert = null;
        PreparedStatement psUpdate = null;
        try{
            if(deleteCnt > 0){
                int nowDeleteCnt = 0;
                psDelete = createDeletePreparedStatement();
                if(psDelete != null){
                    nowDeleteCnt = executeDelete(psDelete);
                }
                if(isEnabledRowVersionCheck && nowDeleteCnt != deleteCnt){
                    throw new RowVersionException("Delete count is unmatch : expected " + deleteCnt + ", but was " + nowDeleteCnt);
                }
                result += nowDeleteCnt;
            }
            
            if(insertCnt > 0){
                psInsert = createInsertPreparedStatement();
                if (psInsert != null) {
                    result += executeInsert(psInsert);
                }
            }
            
            if(updateCnt > 0){
                int nowUpdateCnt = 0;
                psUpdate = createUpdatePreparedStatement();
                if (psUpdate != null) {
                    nowUpdateCnt = executeUpdate(psUpdate);
                }
                if(isEnabledRowVersionCheck && nowUpdateCnt != updateCnt){
                    throw new RowVersionException("Update count is unmatch : expected " + updateCnt + ", but was " + nowUpdateCnt);
                }
                result += nowUpdateCnt;
            }
        }finally{
            if(psInsert != null){
                psInsert.close();
            }
            if(psUpdate != null){
                psUpdate.close();
            }
            if(psDelete != null){
                psDelete.close();
            }
        }
        return result;
    }
    
    /**
     * INSERT�p��PreparedStatement���쐬����B<p>
     * 
     * @return INSERT�p��PreparedStatement
     * @throws SQLException
     */    
    protected PreparedStatement createInsertPreparedStatement()
     throws SQLException {
        
        PreparedStatement ps = null;
        
        StringBuffer sb = new StringBuffer();
        sb.append(C_INSERT_TOKEN);
        sb.append(
            (this.mUpdateTableNames == null)
                 ? this.mTableNames : this.mUpdateTableNames
        );
        sb.append(C_BRACKETS_BEGIN_TOKEN);
        for(int i = 0, imax = mSchema.size(); i < imax; i++){
            // �X�V���ڂ݂̂�ǉ�
            if(mSchema.get(i).isUpdateField()){
                sb.append(mSchema.get(i).getFieldName());
                if(i != imax - 1){
                    sb.append(C_COMMA_TOKEN);
                }
            }
        }
        if(sb.charAt(sb.length() - 1) == ','){
            sb.delete(sb.length() - 1, sb.length());
        }
        addInsertColmun(sb);
        sb.append(C_BRACKETS_END_TOKEN);
        sb.append(C_VALUES_TOKEN);
        sb.append(C_BRACKETS_BEGIN_TOKEN);
        for(int i = 0, imax = mSchema.size(); i < imax; i++){
            if(mSchema.get(i).isUpdateField()){
                sb.append(C_QUESTION_TOKEN);
                if(i != imax - 1){
                    sb.append(C_COMMA_TOKEN);
                }
            }
        }
        if(sb.charAt(sb.length() - 1) == ','){
            sb.delete(sb.length() - 1, sb.length());
        }
        addInsertField(sb);
        sb.append(C_BRACKETS_END_TOKEN);
        
        ps = mCon.prepareStatement(sb.toString());
        
        if(mLogger != null && mMessageCode != null){
            mLogger.write(mMessageCode, sb.toString());
        }
        return ps;
    }
    
    protected int executeInsert(PreparedStatement ps) throws SQLException{
        
        int insertCnt = 0;
        for(int i = 0, imax = size(); i < imax; i++){
            RowData rd = get(i);
            int param_idx = 1;
            if(rd.getTransactionMode() != RowData.E_Record_TypeInsert 
                && rd.getTransactionMode() != RowData.E_Record_TypeDeleteInsert){
                continue;
            }
            for(int j = 0, jmax = mSchema.size(); j < jmax; j++){
                final FieldSchema fs = mSchema.get(j);
                if(!fs.isUpdateField()){
                    continue;
                }
                if(fs.isRowVersionField()){
                    ps.setObject(param_idx++, new Integer(1));
                }else{
                    Object o = rd.getSqlTypeValue(j);
                    if(o == null){
                        ps.setNull(param_idx++, fs.getSqlType());
                    }else{
                        switch(fs.getFieldType()){
                        case FieldSchema.C_TYPE_BLOB:
                            byte[] bytes = (byte[])o;
                            ps.setBinaryStream(
                                param_idx++,
                                new ByteArrayInputStream(bytes),
                                bytes.length
                            );
                            break;
                        case FieldSchema.C_TYPE_CLOB:
                            char[] chars = (char[])o;
                            ps.setCharacterStream(
                                param_idx++,
                                new CharArrayReader(chars),
                                chars.length
                            );
                            break;
                        default:
                            if(fs.isCrypt()){
                                o = doCrypt(o);
                            }
                            ps.setObject(param_idx++, o);
                        }
                    }
                }
            }
            addInsertBind(ps, param_idx);
            if(isBatchExecute){
                ps.addBatch();
            }else{
                insertCnt += ps.executeUpdate();
            }
        }
        if(isBatchExecute){
            // Insert Batch ���s
            int[] ret = ps.executeBatch();
            if(ret != null){
                for(int i = 0; i < ret.length; i++){
                    if(ret[i] > 0){
                        insertCnt += ret[i];
                    }
                }
            }
            if(insertCnt == 0){
                insertCnt = ps.getUpdateCount();
            }
        }
        return insertCnt;
    }
    
    /**
     * �o�^�J������ǉ�����B<p>
     * INSERT INTO TABLE_NAME (FIELD1,...[�J�����ǉ�����] 
     * 
     * @param sb SQL �X�e�[�g�����g
     */
    protected void addInsertColmun(StringBuffer sb){
    }
    
    /**
     * �o�^Field��ǉ�����B<p>
     * INSERT INTO TABLE_NAME (FIELD1,...[�J�����ǉ�����]
     * VALUES (VALUE1, VALUE2...[Field�ǉ�����] 
     * 
     * @param sb SQL �X�e�[�g�����g
     */
    protected void addInsertField(StringBuffer sb){
    }

    /**
     * �ǉ��o�^Field�Ƀo�C���h����B<p>
     * INSERT INTO TABLE_NAME (FIELD1,...[�J�����ǉ�����])
     * VLAUES (VALUE1,VALUE2...[�o�C���h�ǉ�����]); 
     * 
     * @param ps �v���y�A�h�X�e�[�g�����g
     * @param index �o�C���h�C���f�b�N�X
     */
    protected void addInsertBind(PreparedStatement ps, int index) throws SQLException{
    }
    
    /**
     * UPDATE�p��PreparedStatement���쐬����B<p>
     * 
     * @return UPDATE�p��PreparedStatement
     * @throws SQLException
     */    
    protected PreparedStatement createUpdatePreparedStatement()
     throws SQLException {
        
        PreparedStatement ps = null;
        StringBuffer sb = new StringBuffer();
        sb.append(C_UPDATE_TOKEN);
        sb.append(mUpdateTableNames == null ? mTableNames : mUpdateTableNames);
        sb.append(C_SET_TOKEN);
        for(int i = 0, imax = mSchema.size(); i < imax; i++){
            // �X�V���ڂ݂̂�ǉ�
            if(mSchema.get(i).isUpdateField() && !mSchema.get(i).isUniqueKey()){
                sb.append(mSchema.get(i).getFieldName());
                sb.append(C_EQUAL_TOKEN);
                sb.append(C_QUESTION_TOKEN);
                if(i != imax - 1){
                    sb.append(C_COMMA_TOKEN);
                }
            }
        }
        if(sb.charAt(sb.length() - 1) == ','){
            sb.delete(sb.length() - 1, sb.length());
        }
        addUpdateField(sb);
        boolean whereFlg  = false;
        for(int i = 0, imax = mSchema.size(); i < imax; i++){
            // ���j�[�N�L�[���������ɒǉ�
            FieldSchema fSchema = mSchema.get(i);
            if(fSchema.isUniqueKey() || fSchema.isRowVersionField()){
                if(!whereFlg){
                    sb.append(C_WHERE_TOKEN);
                    whereFlg = true;
                }
                sb.append(fSchema.getFieldName());
                sb.append(C_EQUAL_TOKEN);
                sb.append(C_QUESTION_TOKEN);
                sb.append(C_AND_TOKEN);
            }
        }
        if(whereFlg){
            // �Ō��AND���폜
            sb.delete(sb.length() - 5, sb.length());
        }
        
        ps = mCon.prepareStatement(sb.toString());
        if(mLogger != null && mMessageCode != null){
            mLogger.write(mMessageCode,sb.toString());
        }
        return ps;
     }
     
     protected int executeUpdate(PreparedStatement ps) throws SQLException{
        
        int updateCnt = 0;
        for (int i = 0, imax = size(); i < imax; i++){
            RowData rd = get(i);
            int param_idx = 1;
            if(rd.getTransactionMode() != RowData.E_Record_TypeUpdate){
                continue;
            }
            for(int j = 0, jmax = mSchema.size(); j < jmax; j++){
                final FieldSchema fs = mSchema.get(j);
                // �X�V���ڂ��o�C���h
                if(!fs.isUpdateField() || fs.isUniqueKey()){
                    continue;
                }
                if(fs.isRowVersionField()){
                    ps.setObject(
                        param_idx++,
                        new Integer(rd.getIntValue(j) + 1)
                    );
                }else{
                    Object o = rd.getSqlTypeValue(j);
                    if(o == null){
                        ps.setNull(param_idx++, fs.getSqlType());
                    }else{
                        switch(fs.getFieldType()){
                        case FieldSchema.C_TYPE_BLOB:
                            byte[] bytes = (byte[])o;
                            ps.setBinaryStream(
                                param_idx++,
                                new ByteArrayInputStream(bytes),
                                bytes.length
                            );
                            break;
                        case FieldSchema.C_TYPE_CLOB:
                            char[] chars = (char[])o;
                            ps.setCharacterStream(
                                param_idx++,
                                new CharArrayReader(chars),
                                chars.length
                            );
                            break;
                        default:
                            if(fs.isCrypt()){
                                o = doCrypt(o);
                            }
                            ps.setObject(param_idx++, o);
                        }
                    }
                }
            }
            param_idx = addUpdateBind(ps, param_idx);
            for(int j = 0, jmax = mSchema.size(); j < jmax; j++){
                final FieldSchema fs = mSchema.get(j);
                // ���j�[�N�L�[���o�C���h
                if(!fs.isUniqueKey() && !mSchema.get(j).isRowVersionField()){
                    continue;
                }
                Object o = rd.getSqlTypeValue(j);
                if(o == null){
                    ps.setNull(param_idx++, fs.getSqlType());
                }else{
                    switch(fs.getFieldType()){
                    case FieldSchema.C_TYPE_BLOB:
                        byte[] bytes = (byte[])o;
                        ps.setBinaryStream(
                            param_idx++,
                            new ByteArrayInputStream(bytes),
                            bytes.length
                        );
                        break;
                    case FieldSchema.C_TYPE_CLOB:
                        char[] chars = (char[])o;
                        ps.setCharacterStream(
                            param_idx++,
                            new CharArrayReader(chars),
                            chars.length
                        );
                        break;
                    default:
                        if(fs.isCrypt()){
                            o = doCrypt(o);
                        }
                        ps.setObject(param_idx++, o);
                    }
                }
            }
            if(isBatchExecute){
                ps.addBatch();
            }else{
                updateCnt += ps.executeUpdate();
            }
        }
        if(isBatchExecute){
            // Insert Batch ���s
            int[] ret = ps.executeBatch();
            if(ret != null){
                for(int i = 0; i < ret.length; i++){
                    if(ret[i] > 0){
                        updateCnt += ret[i];
                    }
                }
            }
            if(updateCnt == 0){
                updateCnt = ps.getUpdateCount();
            }
        }
        return updateCnt;
    }
    
    /**
     * �X�VField��ǉ�����B<p>
     * UPDATE TABLE_NAME SET (FIELD1=?,...[�ǉ�����] 
     * 
     * @param sb SQL �X�e�[�g�����g
     */
    protected void addUpdateField(StringBuffer sb){
    }
    
    /**
     * �ǉ��X�VField�Ƀo�C���h����B<p>
     * UPDATE TABLE_NAME SET (FIELD1=?,...[Field�ǉ�����])
     * 
     * @param ps �v���y�A�h�X�e�[�g�����g
     * @param index �o�C���h�C���f�b�N�X
     * @return �C���N�������g���ꂽ�o�C���h�C���f�b�N�X
     */
    protected int addUpdateBind(PreparedStatement ps, int index) throws SQLException{
        return index;
    }
    
    /**
     * DELETE�p��PreparedStatement���쐬����B<p>
     * 
     * @return DELETE�p��PreparedStatement
     * @throws SQLException
     */    
    protected PreparedStatement createDeletePreparedStatement() throws SQLException{
        PreparedStatement ps = null;
        StringBuffer sb = new StringBuffer();
        sb.append(C_DELETE_TOKEN);
        sb.append(mUpdateTableNames == null ? mTableNames : mUpdateTableNames);
        boolean whereFlg  = false;
        for(int i = 0, imax = mSchema.size();i < imax; i++){
            // ���j�[�N�L�[���������ɒǉ�
            if(mSchema.get(i).isUniqueKey()){
                if(!whereFlg){
                    sb.append(C_WHERE_TOKEN);
                    whereFlg = true;
                }
                sb.append(mSchema.get(i).getFieldName());
                sb.append(C_EQUAL_TOKEN);
                sb.append(C_QUESTION_TOKEN);
                sb.append(C_AND_TOKEN);
            }
        }
        if(whereFlg){
            // �Ō��AND���폜
            sb.delete(sb.length() - 5, sb.length());
        }
        
        ps = mCon.prepareStatement(sb.toString());
        if(mLogger != null && mMessageCode != null){
            mLogger.write(mMessageCode, sb.toString());
        }
        return ps;
    }
    
    protected int executeDelete(PreparedStatement ps) throws SQLException{
        
        int deleteCnt = 0;
        for(int i = 0, imax = size(); i < imax; i++){
            RowData rd = this.get(i);
            int param_idx = 1;
            if(rd.getTransactionMode() != RowData.E_Record_TypeDelete
               && rd.getTransactionMode() != RowData.E_Record_TypeDeleteInsert){
                continue;
            }
            for(int j = 0; j < mSchema.size(); j++){
                final FieldSchema fs = mSchema.get(j);
                // ���j�[�N�L�[���o�C���h
                if(!mSchema.get(j).isUniqueKey()){
                    continue;
                }
                Object o = rd.getSqlTypeValue(j);
                if(o == null){
                    ps.setNull(param_idx++, fs.getSqlType());
                }else{
                    switch(fs.getFieldType()){
                    case FieldSchema.C_TYPE_BLOB:
                        byte[] bytes = (byte[])o;
                        ps.setBinaryStream(
                            param_idx++,
                            new ByteArrayInputStream(bytes),
                            bytes.length
                        );
                        break;
                    case FieldSchema.C_TYPE_CLOB:
                        char[] chars = (char[])o;
                        ps.setCharacterStream(
                            param_idx++,
                            new CharArrayReader(chars),
                            chars.length
                        );
                        break;
                    default:
                        if(fs.isCrypt()){
                            o = doCrypt(o);
                        }
                        ps.setObject(param_idx++, rd.get(j));
                    }
                }
            }
            if(isBatchExecute){
                ps.addBatch();
            }else{
                deleteCnt += ps.executeUpdate();
            }
        }
        if(isBatchExecute){
            // Delete Batch ���s
            int[] ret = ps.executeBatch();
            if(ret != null){
                for(int i = 0; i < ret.length; i++){
                    if(ret[i] > 0){
                        deleteCnt += ret[i];
                    }
                }
            }
            if(deleteCnt == 0){
                deleteCnt = ps.getUpdateCount();
            }
        }
        return deleteCnt;
    }
    
    /**
     * ���͂��ꂽ������(�I�u�W�F�N�g)����������
     * @param obj �������ΏۃI�u�W�F�N�g(������)
     * @return ���������ꂽ������(�I�u�W�F�N�g)
     */
    protected Object doEncrypt(Object obj){
        if(mCrypt == null || obj == null){
            return obj;
        }
        if(obj instanceof String){
            obj = mCrypt.doDecode((String)obj);
        }
        return obj;
    }
    
    /**
     * ���͂��ꂽ������(�I�u�W�F�N�g)���Í�������B<p>
     * 
     * @param obj �Í����ΏۃI�u�W�F�N�g(������)
     * @return �Í������ꂽ������(�I�u�W�F�N�g)
     */
    protected Object doCrypt(Object obj){
        if(mCrypt == null || obj == null){
            return obj;
        }
        if(obj instanceof String){
            obj = mCrypt.doEncode((String)obj);
        }
        return obj;
    }
    
    /**
     * ��̕����𐶐�����B<p>
     *
     * @return ��̕���
     */
    public RecordSet cloneEmpty(){
        RecordSet newRecSet = null;
        try{
            newRecSet = (RecordSet)clone();
        }catch(CloneNotSupportedException e){
            //�N����Ȃ�
            throw new RuntimeException(e);
        }
        newRecSet.mRows = new ArrayList();
        newRecSet.mHash = new HashMap();
        if(where != null){
            newRecSet.where = new StringBuffer(where.toString());
        }
        if(bindDatas != null){
            newRecSet.bindDatas = new ArrayList(bindDatas);
        }
        if(dynamicSearchKeyMap != null){
            newRecSet.dynamicSearchKeyMap = new HashMap(dynamicSearchKeyMap);
        }
        if(dynamicSearchConditionMap != null){
            newRecSet.dynamicSearchConditionMap = new HashMap(dynamicSearchConditionMap);
        }
        if(dynamicSearchConditionResultMap != null){
            newRecSet.dynamicSearchConditionResultMap = new HashMap();
            final Iterator entries
                 = dynamicSearchConditionResultMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                Map map = (Map)entry.getValue();
                if(map instanceof SortedMap){
                    map = new TreeMap(((SortedMap)map).comparator());
                }else{
                    map = new LinkedHashMap();
                }
                newRecSet.dynamicSearchConditionResultMap.put(
                    entry.getKey(),
                    map
                );
            }
        }
        newRecSet.dynamicSearchMap = null;
        if(partUpdateOrderBy != null){
            newRecSet.partUpdateOrderBy = new int[partUpdateOrderBy.length];
            System.arraycopy(
                partUpdateOrderBy,
                0,
                newRecSet.partUpdateOrderBy,
                0,
                partUpdateOrderBy.length
            );
        }
        if(partUpdateIsAsc != null){
            newRecSet.partUpdateIsAsc = new boolean[partUpdateIsAsc.length];
            System.arraycopy(
                partUpdateIsAsc,
                0,
                newRecSet.partUpdateIsAsc,
                0,
                partUpdateIsAsc.length
            );
        }
        return newRecSet;
    }
    
    /**
     * ���̃C���X�^���X�̃V�����[�R�s�[�𐶐�����B<p>
     * ���̃C���X�^���X�̃��R�[�h�ƁA�V�����[�R�s�[�����C���X�^���X�́A�������R�[�h�Q�Ƃ����B���R�[�h�ȊO�́A�f�B�[�v�R�s�[����B<br>
     *
     * @return ���̃C���X�^���X�̃V�����[�R�s�[
     */
    public RecordSet shallowCopy(){
        final RecordSet recset = cloneEmpty();
        recset.mRows = new ArrayList(mRows);
        recset.mHash = new HashMap(mHash);
        if(dynamicSearchMap != null){
            recset.dynamicSearchMap = new HashMap();
            final Iterator entries
                 = dynamicSearchMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                final Map map = (Map)entry.getValue();
                final Map newMap = new HashMap();
                recset.dynamicSearchMap.put(entry.getKey(), newMap);
                final Iterator entries2
                     = map.entrySet().iterator();
                while(entries2.hasNext()){
                    Map.Entry entry2 = (Map.Entry)entries2.next();
                    if(entry2.getValue() instanceof Map){
                        newMap.put(
                            entry2.getKey(),
                            new LinkedHashMap((Map)entry2.getValue())
                        );
                    }else{
                        newMap.put(
                            entry2.getKey(),
                            entry2.getValue()
                        );
                    }
                }
            }
        }
        if(recset.dynamicSearchConditionResultMap != null){
            final Iterator entries
                 = recset.dynamicSearchConditionResultMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                Map src = (Map)dynamicSearchConditionResultMap.get(entry.getKey());
                ((Map)entry.getValue()).putAll(src);
            }
        }
        return recset;
    }
    
    /**
     * ���̃C���X�^���X�̃f�B�[�v�R�s�[�𐶐�����B<p>
     * ���̃C���X�^���X�̃��R�[�h�ƁA�f�B�[�v�R�s�[�����C���X�^���X�́A�������R�[�h�ł͂��邪�A�قȂ�Q�Ƃ̃��R�[�h�����B<br>
     *
     * @return ���̃C���X�^���X�̃f�B�[�v�R�s�[
     */
    public RecordSet deepCopy(){
        return (RecordSet)cloneAndUpdate(null);
    }
    
    /**
     *  �����X�V���ɁA�w�肳�ꂽ�񖼂̗���\�[�g�L�[�ɂ��ă\�[�g����悤�ɐݒ肷��B<p>
     *
     * @param orderBy �\�[�g�L�[�ƂȂ�񖼔z��
     * @param isAsc �����\�[�g����ꍇ��true�B�~���\�[�g����ꍇ�́Afalse
     */
    public void setPartUpdateSort(String[] orderBy, boolean[] isAsc){
        setPartUpdateSort(convertFromColNamesToColIndexes(orderBy), isAsc);
    }
    
    /**
     *  �����X�V���ɁA�w�肳�ꂽ��C���f�b�N�X�̗���\�[�g�L�[�ɂ��ă\�[�g����悤�ɐݒ肷��B<p>
     *
     * @param orderBy �\�[�g�L�[�ƂȂ��C���f�b�N�X�z��
     * @param isAsc �����\�[�g����ꍇ��true�B�~���\�[�g����ꍇ�́Afalse
     */
    public void setPartUpdateSort(int[] orderBy, boolean[] isAsc){
        partUpdateOrderBy = orderBy;
        partUpdateIsAsc = isAsc;
    }
    
    /**
     * �X�V�����i�[�����R�[�h�}�X�^�����X�V���R�[�h�𐶐�����B<p>
     *
     * @return  �ǉ��A�폜�A�X�V���R�[�h�̃v���C�}���L�[������{@link CodeMasterUpdateKey}���i�[�����R�[�h�}�X�^�����X�V���R�[�h
     */
    public PartUpdateRecords createPartUpdateRecords(){
        PartUpdateRecords records = new PartUpdateRecords();
        for(int i = size(); --i >= 0;){
            RowData rd = get(i);
            int tmode = rd.getTransactionMode();
            if(tmode == RowData.E_Record_TypeDelete
                 || tmode == RowData.E_Record_TypeInsert
                 || tmode == RowData.E_Record_TypeUpdate
                 || tmode == RowData.E_Record_TypeDeleteInsert){
                records.addRecord(rd.createCodeMasterUpdateKey());
            }
        }
        return records;
    }
    
    /**
     * �����X�V������荞�񂾁A�f�B�[�v�R�s�[�C���X�^���X�𐶐�����B<p>
     *
     * @param records �����X�V���
     * @return �����X�V������荞�񂾁A�f�B�[�v�R�s�[�C���X�^���X
     */
    public PartUpdate cloneAndUpdate(PartUpdateRecords records){
        final RecordSet newRecSet = cloneEmpty();
        
        CodeMasterUpdateKey tmpKey = new CodeMasterUpdateKey();
        CodeMasterUpdateKey key = null;
        final Iterator oldRows = mRows.iterator();
        while(oldRows.hasNext()){
            final RowData oldRow = (RowData)oldRows.next();
            tmpKey = oldRow.createCodeMasterUpdateKey(tmpKey);
            key = records == null ? null : records.getKey(tmpKey);
            RowData newRow = null;
            if(key == null){
                newRow = oldRow.cloneRowData();
            }else{
                switch(key.getUpdateType()){
                case CodeMasterUpdateKey.UPDATE_TYPE_ADD:
                case CodeMasterUpdateKey.UPDATE_TYPE_UPDATE:
                    newRow = (RowData)records.removeRecord(key);
                    break;
                case CodeMasterUpdateKey.UPDATE_TYPE_REMOVE:
                default:
                    records.removeRecord(key);
                    continue;
                }
            }
            if(newRow != null){
                newRecSet.addRecord(newRow);
            }
        }
        if(records != null && records.size() != 0){
            final Iterator entries = records.getRecords().entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                if(((CodeMasterUpdateKey)entry.getKey()).getUpdateType()
                     == CodeMasterUpdateKey.UPDATE_TYPE_ADD){
                    final RowData row = (RowData)entry.getValue();
                    if(row != null){
                        newRecSet.addRecord(row);
                    }
                }
            }
        }
        if(partUpdateOrderBy != null && partUpdateOrderBy.length != 0){
            newRecSet.sort(partUpdateOrderBy, partUpdateIsAsc);
        }
        
        return newRecSet;
    }
    
    /**
     * �w�肳�ꂽ�R�[�h�}�X�^�X�V�L�[�ɊY�����郌�R�[�h���i�[���������X�V�����쐬����B<p>
     *
     * @param key �R�[�h�}�X�^�X�V�L�[
     * @return �X�V���R�[�h���܂񂾕����X�V���
     */
    public PartUpdateRecords fillPartUpdateRecords(CodeMasterUpdateKey key){
        PartUpdateRecords records = new PartUpdateRecords();
        records.addRecord(key);
        return fillPartUpdateRecords(records);
    }
    
    /**
     * �w�肳�ꂽ�����X�V���ɊY�����郌�R�[�h���i�[���������X�V�����쐬����B<p>
     *
     * @param records �����X�V���
     * @return �X�V���R�[�h���܂񂾕����X�V���
     */
    public PartUpdateRecords fillPartUpdateRecords(PartUpdateRecords records){
        if(records == null || records.size() == 0
             || (!records.containsAdd() && !records.containsUpdate())){
            return records;
        }
        records.setFilledRecord(true);
        RowData row = createNewRecord();
        final CodeMasterUpdateKey[] keys = records.getKeyArray();
        for(int i = 0; i < keys.length; i++){
            CodeMasterUpdateKey key = (CodeMasterUpdateKey)keys[i];
            final int updateType = key.getUpdateType();
            
            records.removeRecord(key);
            
            // �����p��RowData�Ɍ����L�[��ݒ肷��
            row.setCodeMasterUpdateKey(key);
            
            // ����RecordSet�̎�L�[�݂̂�������CodeMasterUpdateKey�ɕϊ�����
            key = row.createCodeMasterUpdateKey(key);
            key.setUpdateType(updateType);
            
            // �폜�̏ꍇ�́ACodeMasterUpdateKey�����o�^������
            if(key.isRemove()){
                records.addRecord(key);
                continue;
            }
            
            // �ǉ��܂��͍X�V���ꂽRowData����������
            final RowData searchRow = get(row);
            records.addRecord(key, searchRow);
        }
        return records;
    }
    
    private static class DynamicSearchKeyValue implements Serializable{
        
        private static final long serialVersionUID = -2327997182252855059L;
        
        public int[] colIndexes;
        public int[] orderBy;
        public boolean[] isAsc;
        public DynamicSearchKeyValue(
            int[] colIndexes,
            int[] orderBy,
            boolean[] isAsc
        ){
            this.colIndexes = colIndexes;
            this.orderBy = orderBy;
            this.isAsc = isAsc;
        }
    }
    
    private static class RowDataComparator implements Comparator, Serializable{
        
        private static final long serialVersionUID = -9111641214052663144L;
        
        private RowSchema rowSchema;
        private int[] colIndexes;
        private boolean[] isAsc;
        
        public RowDataComparator(RowSchema schema, String[] colNames){
            this(schema, colNames, null);
        }
        
        public RowDataComparator(RowSchema schema, String[] colNames, boolean[] isAsc){
            rowSchema = schema;
            if(rowSchema == null){
                throw new InvalidDataException("Schema not initalize.");
            }
            if(colNames == null || colNames.length == 0){
                throw new IllegalArgumentException("Column name array is empty.");
            }
            if(isAsc != null && colNames.length != isAsc.length){
                throw new IllegalArgumentException("Length of column name array and sort flag array is unmatch.");
            }
            colIndexes = new int[colNames.length];
            for(int i = 0; i < colNames.length; i++){
                final FieldSchema field = rowSchema.get(colNames[i]);
                if(field == null){
                    throw new IllegalArgumentException("Field not found : " + colNames[i]);
                }
                colIndexes[i] = field.getIndex();
            }
            if(colIndexes == null || colIndexes.length == 0){
                throw new IllegalArgumentException("Column index array is empty.");
            }
            this.isAsc = isAsc;
        }
        
        public RowDataComparator(RowSchema schema, int[] colIndexes){
            this(schema, colIndexes, null);
        }
        
        public RowDataComparator(RowSchema schema, int[] colIndexes, boolean[] isAsc){
            rowSchema = schema;
            if(rowSchema == null){
                throw new InvalidDataException("Schema not initalize.");
            }
            if(colIndexes == null || colIndexes.length == 0){
                throw new IllegalArgumentException("Column index array is empty.");
            }
            if(isAsc != null && colIndexes.length != isAsc.length){
                throw new IllegalArgumentException("Length of column index array and sort flag array is unmatch.");
            }
            this.colIndexes = colIndexes;
            this.isAsc = isAsc;
        }
        
        public int compare(Object o1, Object o2){
            final RowData rd1 = (RowData)o1;
            final RowData rd2 = (RowData)o2;
            if(rd1 == null && rd2 == null){
                return 0;
            }
            if(rd1 != null && rd2 == null){
                return 1;
            }
            if(rd1 == null && rd2 != null){
                return -1;
            }
            for(int i = 0; i < colIndexes.length; i++){
                Object val1 = rd1.get(colIndexes[i]);
                Object val2 = rd2.get(colIndexes[i]);
                if(val1 != null && val2 == null){
                    return (isAsc == null || isAsc[i]) ? 1 : -1;
                }
                if(val1 == null && val2 != null){
                    return (isAsc == null || isAsc[i]) ? -1 : 1;
                }
                if(val1 != null && val2 != null){
                    final FieldSchema field = rowSchema.get(colIndexes[i]);
                    final int fieldType = field.getFieldType();
                    int comp = 0;
                    switch(fieldType){
                    case FieldSchema.C_TYPE_INT:
                    case FieldSchema.C_TYPE_LONG:
                    case FieldSchema.C_TYPE_FLOAT:
                    case FieldSchema.C_TYPE_DOUBLE:
                    case FieldSchema.C_TYPE_STRING:
                    case FieldSchema.C_TYPE_CHAR:
                    case FieldSchema.C_TYPE_DATE:
                    case FieldSchema.C_TYPE_TIMESTAMP:
                        comp = ((Comparable)val1).compareTo(val2);
                        break;
                    case FieldSchema.C_TYPE_BLOB:
                    case FieldSchema.C_TYPE_CLOB:
                    default:
                        comp = val1.hashCode() - val2.hashCode();
                        break;
                    }
                    if(comp != 0){
                        return (isAsc == null || isAsc[i]) ? comp : -1 * comp;
                    }
                }
            }
            return 0;
        }
    }
}
