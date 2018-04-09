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
package jp.ossc.nimbus.service.test.bug;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 不具合情報(IPA推奨)を保持するためのクラス。<br>
 * 
 * @author m-ishida
 *
 */
public class IpaBugRecord extends BugRecord {
    
    /**
     * タイトル
     */
    public static final String TITLE = "title";
    
    /**
     * プロジェクト名
     */
    public static final String PROJECT_NAME = "projectName";
    
    /**
     * 重要度
     */
    public static final String SEVERITY = "severity";
    
    /**
     * 優先度
     */
    public static final String PRIORITY = "priority";
    
    /**
     * ステータス
     */
    public static final String STATUS = "status";
    
    /**
     * 同件ID
     */
    public static final String DUPLICATE_ID = "duplicateId";
    
    
    /**
     * 起票者
     */
    public static final String OPENED_BY = "openedBy";
    
    /**
     * バグ発見者
     */
    public static final String OBSERVED_BY = "observedBy";
    
    /**
     * 発見日時
     */
    public static final String OBSERVED_DATE = "observedDate";
    
    /**
     * 調査担当者
     */
    public static final String ANALYZED_BY = "analyzedBy";
    
    /**
     * 調査完了日
     */
    public static final String ANALYZED_DATE = "analyzedDate";
    
    /**
     * 処置方針決定日
     */
    public static final String DETERMINED_DATE = "determinedDate";
    
    /**
     * 処置担当者
     */
    public static final String RESOLVED_BY = "resolvedBy";
    
    /**
     * 処置期限日
     */
    public static final String DUE_DATE = "dueDate";
    
    /**
     * 処置完了日
     */
    public static final String RESOLVED_DATE = "resolvedDate";
    
    /**
     * 処置承認者
     */
    public static final String APPROVED_BY = "approvedBy";
    
    /**
     * 検証担当者
     */
    public static final String VERIFIED_BY = "verifiedBy";
    
    /**
     * 検証完了日
     */
    public static final String VERIFIED_DATE = "verifiedDate";
    
    /**
     * リリース日
     */
    public static final String RELEASED_DATE = "releasedDate";
    
    
    /**
     * 発生した機能/サブシステム
     */
    public static final String COMPONENT = "component";
    
    /**
     * 発生したバージョン
     */
    public static final String DETECTED_VERSION = "detectedVersion";
    
    /**
     * 発生した環境
     */
    public static final String ENVIRONMENT = "environment";
    
    /**
     * 発生した頻度
     */
    public static final String FREQUENCY = "frequency";
    
    /**
     * 発生した工程
     */
    public static final String DETECTION_PHASE = "detectionPhase";
    
    /**
     * 発見手段
     */
    public static final String DETECTION_ACTIVITY = "detectionActivity";
    
    /**
     * 影響
     */
    public static final String EFFECT = "effect";
    
    
    /**
     * 発生原因
     */
    public static final String CAUSE = "cause";
    
    /**
     * 原因箇所
     */
    public static final String DEFECT_FOUND_IN = "defectFoundIn";
    
    /**
     * 修正見積もり
     */
    public static final String ESTIMATED_EFFORT = "estimatedEffort";
    
    
    /**
     * 解決方法/処置内容
     */
    public static final String RESOLUTION = "resolution";
    
    /**
     * 処置区分
     */
    public static final String DISPOSITION_TYPE = "dispositionType";
    
    /**
     * 修正対象
     */
    public static final String CHANGES_MADE_TO = "changesMadeTo";
    
    /**
     * 修正済バージョン
     */
    public static final String CORRECTED_VERSION = "correctedVersion";
    
    
    /**
     * バグ区分
     */
    public static final String DEFECT_TYPE = "defectType";
    
    /**
     * 作り込み工程
     */
    public static final String INSERTION_PHASE = "insertionPhase";
    
    
    /**
     * 調査工数
     */
    public static final String INVESTIGATION_EFFORT = "investigationEffort";
    
    /**
     * 処置工数
     */
    public static final String DISPOSITION_EFFORT = "dispositionEffort";
    
    /**
     * 発見すべき工程
     */
    public static final String PHASE_TO_BE_DETECTED = "phaseToBeDetected";
    
    // タイトル
    protected StringBugAttribute title;
    // プロジェクト名
    protected StringBugAttribute projectName;
    // 重要度
    protected SelectableStringBugAttribute severity;
    // 優先度
    protected SelectableStringBugAttribute priority;
    // ステータス
    protected SelectableStringBugAttribute status;
    // 同件ID
    protected StringBugAttribute duplicateId;
    
    // 起票者
    protected StringBugAttribute openedBy;
    // バグ発見者
    protected StringBugAttribute observedBy;
    // 発見日時
    protected DateBugAttribute observedDate;
    // 調査担当者
    protected StringBugAttribute analyzedBy;
    // 調査完了日
    protected DateBugAttribute analyzedDate;
    // 処置方針決定日
    protected DateBugAttribute determinedDate;
    // 処置担当者
    protected StringBugAttribute resolvedBy;
    // 処置期限日
    protected DateBugAttribute dueDate;
    // 処置完了日
    protected DateBugAttribute resolvedDate;
    // 処置承認者
    protected StringBugAttribute approvedBy;
    // 検証担当者
    protected StringBugAttribute verifiedBy;
    // 検証完了日
    protected DateBugAttribute verifiedDate;
    // リリース日
    protected DateBugAttribute releasedDate;
    
    // 発生した機能/サブシステム
    protected StringBugAttribute component;
    // 発生したバージョン
    protected StringBugAttribute detectedVersion;
    // 発生した環境
    protected StringBugAttribute environment;
    // 発生した頻度
    protected StringBugAttribute frequency;
    // 発生した工程
    protected SelectableStringBugAttribute detectionPhase;
    // 発見手段
    protected SelectableStringBugAttribute detectionActivity;
    // 影響
    protected SelectableStringBugAttribute effect;
    
    // 発生原因
    protected StringBugAttribute cause;
    // 原因箇所
    protected StringBugAttribute defectFoundIn;
    // 修正見積もり
    protected FloatBugAttribute estimatedEffort;
    
    // 解決方法/処置内容
    protected StringBugAttribute resolution;
    // 解決方法/処置内容
    protected SelectableStringBugAttribute dispositionType;
    // 修正対象
    protected StringBugAttribute changesMadeTo;
    // 修正済バージョン
    protected StringBugAttribute correctedVersion;
    
    // バグ区分
    protected SelectableStringBugAttribute defectType;
    // 作り込み工程
    protected SelectableStringBugAttribute insertionPhase;
    
    // 調査工数
    protected FloatBugAttribute investigationEffort;
    // 処置工数
    protected FloatBugAttribute dispositionEffort;
    // 発見すべき工程
    protected SelectableStringBugAttribute phaseToBeDetected;
    
    protected Set attributeNameSet;
    protected boolean isEnableSet;
    
    /**
     * 全項目を無効にしてレコードを作成する。
     */
    public IpaBugRecord() {
        this(null, true);
    }
    
    /**
     * 有効/無効にする項目名を指定してレコードを作成する。
     * 
     * @param attributeNameSet 項目名の集合
     * @param isEnableSet trueの場合attributeNameSetに設定されている項目名のみ有効となる。falseの場合attributeNameSetに設定されている項目名以外が有効となる。
     */
    public IpaBugRecord(Set attributeNameSet, boolean isEnableSet) {
        
        super();
        
        this.attributeNameSet = attributeNameSet; 
        this.isEnableSet = isEnableSet;
        
        updateEnableAttributes();
    }
    
    public void updateEnableAttributes() {
        super.bugAttributeMap.clear();
        if(isEnableAttribute(TITLE)) {
            // タイトル
            title = new StringBugAttribute(TITLE);
            addBugAttribute(title);
        }
        if(isEnableAttribute(PROJECT_NAME)) {
            // プロジェクト名
            projectName = new StringBugAttribute(PROJECT_NAME);
            addBugAttribute(projectName);
        }
        if(isEnableAttribute(SEVERITY)) {
            // 重要度
            severity = new SelectableStringBugAttribute(SEVERITY, SeverityArgs.getValues());
            addBugAttribute(severity);
        }
        if(isEnableAttribute(PRIORITY)) {
            // 優先度
            priority = new SelectableStringBugAttribute(PRIORITY, PriorityArgs.getValues());
            addBugAttribute(priority);
        }
        if(isEnableAttribute(STATUS)) {
            // ステータス
            status = new SelectableStringBugAttribute(STATUS, StatusArgs.getValues());
            addBugAttribute(status);
        }
        
        if(isEnableAttribute(DUPLICATE_ID)) {
            // 同件ID
            duplicateId = new StringBugAttribute(DUPLICATE_ID);
            addBugAttribute(duplicateId);
        }
        if(isEnableAttribute(OPENED_BY)) {
            // 起票者
            openedBy = new StringBugAttribute(OPENED_BY);
            addBugAttribute(openedBy);
        }
        if(isEnableAttribute(OBSERVED_BY)) {
            // バグ発見者
            observedBy = new StringBugAttribute(OBSERVED_BY);
            addBugAttribute(observedBy);
        }
        if(isEnableAttribute(OBSERVED_DATE)) {
            // 発見日時
            observedDate = new DateBugAttribute(OBSERVED_DATE);
            addBugAttribute(observedDate);
        }
        if(isEnableAttribute(ANALYZED_BY)) {
            // 調査担当者
            analyzedBy = new StringBugAttribute(ANALYZED_BY);
            addBugAttribute(analyzedBy);
        }
        if(isEnableAttribute(ANALYZED_DATE)) {
            // 調査完了日
            analyzedDate = new DateBugAttribute(ANALYZED_DATE);
            addBugAttribute(analyzedDate);
        }
        if(isEnableAttribute(DETERMINED_DATE)) {
            // 処置方針決定日
            determinedDate = new DateBugAttribute(DETERMINED_DATE);
            addBugAttribute(determinedDate);
        }
        if(isEnableAttribute(RESOLVED_BY)) {
            // 処置担当者
            resolvedBy = new StringBugAttribute(RESOLVED_BY);
            addBugAttribute(resolvedBy);
        }
        if(isEnableAttribute(DUE_DATE)) {
            // 処置期限日
            dueDate = new DateBugAttribute(DUE_DATE);
            addBugAttribute(dueDate);
        }
        if(isEnableAttribute(RESOLVED_DATE)) {
            // 処置完了日
            resolvedDate = new DateBugAttribute(RESOLVED_DATE);
            addBugAttribute(resolvedDate);
        }
        if(isEnableAttribute(APPROVED_BY)) {
            // 処置承認者
            approvedBy = new StringBugAttribute(APPROVED_BY);
            addBugAttribute(approvedBy);
        }
        if(isEnableAttribute(VERIFIED_BY)) {
            // 検証担当者
            verifiedBy = new StringBugAttribute(VERIFIED_BY);
            addBugAttribute(verifiedBy);
        }
        if(isEnableAttribute(VERIFIED_DATE)) {
            // 検証完了日
            verifiedDate = new DateBugAttribute(VERIFIED_DATE);
            addBugAttribute(verifiedDate);
        }
        if(isEnableAttribute(RELEASED_DATE)) {
            // リリース日
            releasedDate = new DateBugAttribute(RELEASED_DATE);
            addBugAttribute(releasedDate);
        }
        
        if(isEnableAttribute(COMPONENT)) {
            // 発生した機能/サブシステム
            component = new StringBugAttribute(COMPONENT);
            addBugAttribute(component);
        }
        if(isEnableAttribute(DETECTED_VERSION)) {
            // 発生したバージョン
            detectedVersion = new StringBugAttribute(DETECTED_VERSION);
            addBugAttribute(detectedVersion);
        }
        if(isEnableAttribute(ENVIRONMENT)) {
            // 発生した環境
            environment = new StringBugAttribute(ENVIRONMENT);
            addBugAttribute(environment);
        }
        if(isEnableAttribute(FREQUENCY)) {
            // 発生頻度
            frequency = new StringBugAttribute(FREQUENCY);
            addBugAttribute(frequency);
        }
        if(isEnableAttribute(DETECTION_PHASE)) {
            // 発生した工程
            detectionPhase = new SelectableStringBugAttribute(DETECTION_PHASE, DetectionPhaseArgs.getValues());
            addBugAttribute(detectionPhase);
        }
        if(isEnableAttribute(DETECTION_ACTIVITY)) {
            // 発見手段
            detectionActivity = new SelectableStringBugAttribute(DETECTION_ACTIVITY, DetectionActivityArgs.getValues());
            addBugAttribute(detectionActivity);
        }
        if(isEnableAttribute(EFFECT)) {
            // 影響
            effect = new SelectableStringBugAttribute(EFFECT, EffectArgs.getValues());
            addBugAttribute(effect);
        }
        
        if(isEnableAttribute(CAUSE)) {
            // 発生原因
            cause = new StringBugAttribute(CAUSE);
            addBugAttribute(cause);
        }
        if(isEnableAttribute(DEFECT_FOUND_IN)) {
            // 原因箇所
            defectFoundIn = new StringBugAttribute(DEFECT_FOUND_IN);
            addBugAttribute(defectFoundIn);
        }
        if(isEnableAttribute(ESTIMATED_EFFORT)) {
            // 修正見積もり
            estimatedEffort = new FloatBugAttribute(ESTIMATED_EFFORT);
            addBugAttribute(estimatedEffort);
        }
        
        if(isEnableAttribute(RESOLUTION)) {
            // 解決方法/処置内容
            resolution = new StringBugAttribute(RESOLUTION);
            addBugAttribute(resolution);
        }
        if(isEnableAttribute(DISPOSITION_TYPE)) {
            // 処置区分
            dispositionType = new SelectableStringBugAttribute(DISPOSITION_TYPE, DispositionTypeArgs.getValues());
            addBugAttribute(dispositionType);
        }
        if(isEnableAttribute(CHANGES_MADE_TO)) {
            // 修正対象
            changesMadeTo = new StringBugAttribute(CHANGES_MADE_TO);
            addBugAttribute(changesMadeTo);
        }
        if(isEnableAttribute(CORRECTED_VERSION)) {
            // 修正済バージョン
            correctedVersion = new StringBugAttribute(CORRECTED_VERSION);
            addBugAttribute(correctedVersion);
        }
        
        if(isEnableAttribute(DEFECT_TYPE)) {
            // バグ区分
            defectType = new SelectableStringBugAttribute(DEFECT_TYPE, DefectTypeArgs.getValues());
            addBugAttribute(defectType);
        }
        if(isEnableAttribute(INSERTION_PHASE)) {
            // 作り込み工程
            insertionPhase = new SelectableStringBugAttribute(INSERTION_PHASE, InsertionPhaseArgs.getValues());
            addBugAttribute(insertionPhase);
        }
        
        if(isEnableAttribute(INVESTIGATION_EFFORT)) {
            // 調査工数
            investigationEffort = new FloatBugAttribute(INVESTIGATION_EFFORT);
            addBugAttribute(investigationEffort);
        }
        if(isEnableAttribute(DISPOSITION_EFFORT)) {
            // 処置工数
            dispositionEffort = new FloatBugAttribute(DISPOSITION_EFFORT);
            addBugAttribute(dispositionEffort);
        }
        if(isEnableAttribute(PHASE_TO_BE_DETECTED)) {
            // 発見すべき工程
            phaseToBeDetected = new SelectableStringBugAttribute(PHASE_TO_BE_DETECTED, PhaseToBeDetectedArgs.getValues());
            addBugAttribute(phaseToBeDetected);
        }
    }
    
    public Set getAttributeNameSet() {
        return attributeNameSet;
    }
    
    public void setAttributeNameSet(Set set) {
        attributeNameSet = set;
    }
    
    public void addAttributeName(String name) {
        if(attributeNameSet == null) {
            attributeNameSet = new HashSet();
        }
        if(!attributeNameSet.contains(name)) {
            attributeNameSet.add(name);
        }
    }
    
    public boolean isEnableSet() {
        return isEnableSet;
    }

    public void setEnableSet(boolean isEnable) {
        isEnableSet = isEnable;
    }

    private boolean isEnableAttribute(String name) {
        if (attributeNameSet == null) {
            return !isEnableSet;
        }
        return isEnableSet ? attributeNameSet.contains(name) : !attributeNameSet.contains(name);
    }
    
    /**
     * タイトル(概要)を取得する。
     * 
     * @return タイトル(概要)
     */
    public String getTitle() {
        return (String)getValue(TITLE);
    }
    
    /**
     * タイトル(概要)を設定する。
     * 
     * @param value タイトル(概要)
     */
    public void setTitle(String value) {
        if(isEnableAttribute(TITLE)) {
            setValue(TITLE, value);
        }
    }
    
    /**
     * プロジェクト名を取得する。
     * 
     * @return プロジェクト名
     */
    public String getProjectName() {
        return (String)getValue(PROJECT_NAME);
    }
    
    /**
     * プロジェクト名を設定する。
     * 
     * @param value プロジェクト名
     */
    public void setProjectName(String value) {
        if(isEnableAttribute(PROJECT_NAME)) {
            setValue(PROJECT_NAME, value);
        }
    }
    
    /**
     * 重要度を取得する。
     * 
     * @return 重要度
     */
    public SeverityArgs getSeverityEnum() {
        return SeverityArgs.getTypeByValue(getSeverity());
    }
    
    /**
     * 重要度を取得する。
     * 
     * @return 重要度
     */
    public String getSeverity() {
        return (String)getValue(SEVERITY);
    }
    
    /**
     * 重要度を設定する。
     * 
     * @param value 重要度
     */
    public void setSeverity(String value) {
        if(isEnableAttribute(SEVERITY)) {
            setValue(SEVERITY, value);
        }
    }
    
    /**
     * 重要度を設定する。
     * 
     * @param value 重要度
     */
    public void setSeverity(SeverityArgs value) {
        if(isEnableAttribute(SEVERITY)) {
            setSeverity(value.getValue());
        }
    }
    
    /**
     * 優先度を取得する。
     * 
     * @return 優先度
     */
    public PriorityArgs getPriorityEnmu() {
        return PriorityArgs.getTypeByValue(getPriority());
    }
    
    /**
     * 優先度を取得する。
     * 
     * @return 優先度
     */
    public String getPriority() {
        return (String)getValue(PRIORITY);
    }
    
    /**
     * 優先度を設定する。
     * 
     * @param value 優先度
     */
    public void setPriority(String value) {
        if(isEnableAttribute(SEVERITY)) {
            setValue(PRIORITY, value);
        }
    }
    
    /**
     * 優先度を設定する。
     * 
     * @param value 優先度
     */
    public void setPriority(PriorityArgs value) {
        if(isEnableAttribute(SEVERITY)) {
            setPriority(value.getValue());
        }
    }
    
    /**
     * ステータスを取得する。
     * 
     * @return ステータス
     */
    public StatusArgs getStatusEnum() {
        return StatusArgs.getTypeByValue(getStatus());
    }
    
    /**
     * ステータスを取得する。
     * 
     * @return ステータス
     */
    public String getStatus() {
        return (String)getValue(STATUS);
    }
    
    /**
     * ステータスを設定する。
     * 
     * @param value ステータス
     */
    public void setStatus(String value) {
        if(isEnableAttribute(STATUS)) {
            setValue(STATUS, value);
        }
    }
    
    /**
     * ステータスを設定する。
     * 
     * @param value ステータス
     */
    public void setStatus(StatusArgs value) {
        if(isEnableAttribute(STATUS)) {
            setStatus(value.getValue());
        }
    }
    
    /**
     * 同件IDを取得する。
     * 
     * @return 同件ID
     */
    public String getDuplicateId() {
        return (String)getValue(DUPLICATE_ID);
    }
    
    /**
     * 同件IDを設定する。
     * 
     * @param value 同件ID
     */
    public void setDuplicateId(String value) {
        if(isEnableAttribute(DUPLICATE_ID)) {
            setValue(DUPLICATE_ID, value);
        }
    }
    
    /**
     * 起票者を取得する。
     * 
     * @return 起票者
     */
    public String getOpenedBy() {
        return (String)getValue(OPENED_BY);
    }
    
    /**
     * 起票者を設定する。
     * 
     * @param value 起票者
     */
    public void setOpenedBy(String value) {
        if(isEnableAttribute(OPENED_BY)) {
            setValue(OPENED_BY, value);
        }
    }
    
    /**
     * バグ発見者を取得する。
     * 
     * @return バグ発見者
     */
    public String getObservedBy() {
        return (String)getValue(OBSERVED_BY);
    }
    
    /**
     * バグ発見者を設定する。
     * 
     * @param value バグ発見者
     */
    public void setObservedBy(String value) {
        if(isEnableAttribute(OBSERVED_BY)) {
            setValue(OBSERVED_BY, value);
        }
    }
    
    /**
     * 発見日時を取得する。
     * 
     * @return 発見日時
     */
    public Date getObservedDate() {
        return (Date)getValue(OBSERVED_DATE);
    }
    
    /**
     * 発見日時を設定する。
     * 
     * @param value 発見日時
     */
    public void setObservedDate(Date value) {
        if(isEnableAttribute(OBSERVED_DATE)) {
            setValue(OBSERVED_DATE, value);
        }
    }
    
    /**
     * 調査担当者を取得する。
     * 
     * @return 調査担当者
     */
    public String getAnalyzedBy() {
        return (String)getValue(ANALYZED_BY);
    }
    
    /**
     * 調査担当者を設定する。
     * 
     * @param value 調査担当者
     */
    public void setAnalyzedBy(String value) {
        if(isEnableAttribute(ANALYZED_BY)) {
            setValue(ANALYZED_BY, value);
        }
    }
    
    /**
     * 調査完了日を取得する。
     * 
     * @return 調査完了日
     */
    public Date getAnalyzedDate() {
        return (Date)getValue(ANALYZED_DATE);
    }
    
    /**
     * 調査完了日を設定する。
     * 
     * @param value 調査完了日
     */
    public void setAnalyzedDate(Date value) {
        if(isEnableAttribute(ANALYZED_DATE)) {
            setValue(ANALYZED_DATE, value);
        }
    }
    
    /**
     * 処置方針決定日を取得する。
     * 
     * @return 処置方針決定日
     */
    public Date getDeterminedDate() {
        return (Date)getValue(DETERMINED_DATE);
    }
    
    /**
     * 処置方針決定日を設定する。
     * 
     * @param value 処置方針決定日
     */
    public void setDeterminedDate(Date value) {
        if(isEnableAttribute(DETERMINED_DATE)) {
            setValue(DETERMINED_DATE, value);
        }
    }
    
    /**
     * 処置担当者を取得する。
     * 
     * @return 処置担当者
     */
    public String getResolvedBy() {
        return (String)getValue(RESOLVED_BY);
    }
    
    /**
     * 処置担当者を設定する。
     * 
     * @param value 処置担当者
     */
    public void setResolvedBy(String value) {
        if(isEnableAttribute(RESOLVED_BY)) {
            setValue(RESOLVED_BY, value);
        }
    }
    
    /**
     * 処置期限日を取得する。
     * 
     * @return 処置期限日
     */
    public Date getDueDate() {
        return (Date)getValue(DUE_DATE);
    }
    
    /**
     * 処置期限日を設定する。
     * 
     * @param value 処置期限日
     */
    public void setDueDate(Date value) {
        if(isEnableAttribute(DUE_DATE)) {
            setValue(DUE_DATE, value);
        }
    }
    
    /**
     * 処置完了日を取得する。
     * 
     * @return 処置完了日
     */
    public Date getResolvedDate() {
        return (Date)getValue(RESOLVED_DATE);
    }
    
    /**
     * 処置完了日を設定する。
     * 
     * @param value 処置完了日
     */
    public void setResolvedDate(Date value) {
        if(isEnableAttribute(RESOLVED_DATE)) {
            setValue(RESOLVED_DATE, value);
        }
    }
    
    /**
     * 処置承認者を取得する。
     * 
     * @return 処置承認者
     */
    public String getApprovedBy() {
        return (String)getValue(APPROVED_BY);
    }
    
    /**
     * 処置承認者を設定する。
     * 
     * @param value 処置承認者
     */
    public void setApprovedBy(String value) {
        if(isEnableAttribute(APPROVED_BY)) {
            setValue(APPROVED_BY, value);
        }
    }
    
    /**
     * 検証担当者を取得する。
     * 
     * @return 検証担当者
     */
    public String getVerifiedBy() {
        return (String)getValue(VERIFIED_BY);
    }
    
    /**
     * 検証担当者を設定する。
     * 
     * @param value 検証担当者
     */
    public void setVerifiedBy(String value) {
        if(isEnableAttribute(VERIFIED_BY)) {
            setValue(VERIFIED_BY, value);
        }
    }
    
    /**
     * 検証完了日を取得する。
     * 
     * @return 検証完了日
     */
    public Date getVerifiedDate() {
        return (Date)getValue(VERIFIED_DATE);
    }
    
    /**
     * 検証完了日を設定する。
     * 
     * @param value 検証完了日
     */
    public void setVerifiedDate(Date value) {
        if(isEnableAttribute(VERIFIED_DATE)) {
            setValue(VERIFIED_DATE, value);
        }
    }
    
    /**
     * リリース日を取得する。
     * 
     * @return リリース日
     */
    public Date getReleasedDate() {
        return (Date)getValue(RELEASED_DATE);
    }
    
    /**
     * リリース日を設定する。
     * 
     * @param value リリース日
     */
    public void setReleasedDate(Date value) {
        if(isEnableAttribute(RELEASED_DATE)) {
            setValue(RELEASED_DATE, value);
        }
    }
    
    /**
     * 発生した機能/サブシステムを取得する。
     * 
     * @return 発生した機能/サブシステム
     */
    public String getComponent() {
        return (String)getValue(COMPONENT);
    }
    
    /**
     * 発生した機能/サブシステムを設定する。
     * 
     * @param value 発生した機能/サブシステム
     */
    public void setComponent(String value) {
        if(isEnableAttribute(COMPONENT)) {
            setValue(COMPONENT, value);
        }
    }
    
    /**
     * 発生したバージョンを取得する。
     * 
     * @return 発生したバージョン
     */
    public String getDetectedVersion() {
        return (String)getValue(DETECTED_VERSION);
    }
    
    /**
     * 発生したバージョンを設定する。
     * 
     * @param value 発生したバージョン
     */
    public void setDetectedVersion(String value) {
        if(isEnableAttribute(DETECTED_VERSION)) {
            setValue(DETECTED_VERSION, value);
        }
    }
    
    /**
     * 発生した環境を取得する。
     * 
     * @return 発生した環境
     */
    public String getEnvironment() {
        return (String)getValue(ENVIRONMENT);
    }
    
    /**
     * 発生した環境を設定する。
     * 
     * @param value 発生した環境
     */
    public void setEnvironment(String value) {
        if(isEnableAttribute(ENVIRONMENT)) {
            setValue(ENVIRONMENT, value);
        }
    }
    
    /**
     * 発生頻度を取得する。
     * 
     * @return 発生頻度
     */
    public String getFrequency() {
        return (String)getValue(FREQUENCY);
    }
    
    /**
     * 発生頻度を設定する。
     * 
     * @param value 発生頻度
     */
    public void setFrequency(String value) {
        if(isEnableAttribute(FREQUENCY)) {
            setValue(FREQUENCY, value);
        }
    }
    
    /**
     * 発生した工程を取得する。
     * 
     * @return 発生した工程
     */
    public DetectionPhaseArgs getDetectionPhaseEnum() {
        return DetectionPhaseArgs.getTypeByValue(getDetectionPhase());
    }
    
    /**
     * 発生した工程を取得する。
     * 
     * @return 発生した工程
     */
    public String getDetectionPhase() {
        return (String)getValue(DETECTION_PHASE);
    }
    
    /**
     * 発生した工程を設定する。
     * 
     * @param value 発生した工程
     */
    public void setDetectionPhase(String value) {
        if(isEnableAttribute(DETECTION_PHASE)) {
            setValue(DETECTION_PHASE, value);
        }
    }
    
    /**
     * 発生した工程を設定する。
     * 
     * @param value 発生した工程
     */
    public void setDetectionPhase(DetectionPhaseArgs value) {
        if(isEnableAttribute(DETECTION_PHASE)) {
            setDetectionPhase(value.getValue());
        }
    }
    
    /**
     * 発見手段を取得する。
     * 
     * @return 発見手段
     */
    public DetectionActivityArgs getDetectionActivityEnum() {
        return DetectionActivityArgs.getTypeByValue(getDetectionActivity());
    }
    
    /**
     * 発見手段を取得する。
     * 
     * @return 発見手段
     */
    public String getDetectionActivity() {
        return (String)getValue(DETECTION_ACTIVITY);
    }
    
    /**
     * 発見手段を設定する。
     * 
     * @param value 発見手段
     */
    public void setDetectionActivity(String value) {
        if(isEnableAttribute(DETECTION_ACTIVITY)) {
            setValue(DETECTION_ACTIVITY, value);
        }
    }
    
    /**
     * 発見手段を設定する。
     * 
     * @param value 発見手段
     */
    public void setDetectionActivity(DetectionActivityArgs value) {
        if(isEnableAttribute(DETECTION_ACTIVITY)) {
            setDetectionActivity(value.getValue());
        }
    }
    
    /**
     * 影響を取得する。
     * 
     * @return 影響
     */
    public EffectArgs getEffectEnum() {
        return EffectArgs.getTypeByValue(getEffect());
    }
    
    /**
     * 影響を取得する。
     * 
     * @return 影響
     */
    public String getEffect() {
        return (String)getValue(EFFECT);
    }
    
    /**
     * 影響を設定する。
     * 
     * @param value 影響
     */
    public void setEffect(String value) {
        if(isEnableAttribute(EFFECT)) {
            setValue(EFFECT, value);
        }
    }
    
    /**
     * 影響を設定する。
     * 
     * @param value 影響
     */
    public void setEffect(EffectArgs value) {
        if(isEnableAttribute(EFFECT)) {
            setEffect(value.getValue());
        }
    }
    
    /**
     * 発生原因を取得する。
     * 
     * @return 発生原因
     */
    public String getCause() {
        return (String)getValue(CAUSE);
    }
    
    /**
     * 発生原因を設定する。
     * 
     * @param value 発生原因
     */
    public void setCause(String value) {
        if(isEnableAttribute(CAUSE)) {
            setValue(CAUSE, value);
        }
    }
    
    /**
     * 原因箇所を取得する。
     * 
     * @return 原因箇所
     */
    public String getDefectFoundIn() {
        return (String)getValue(DEFECT_FOUND_IN);
    }
    
    /**
     * 原因箇所を設定する。
     * 
     * @param value 原因箇所
     */
    public void setDefectFoundIn(String value) {
        if(isEnableAttribute(DEFECT_FOUND_IN)) {
            setValue(DEFECT_FOUND_IN, value);
        }
    }
    
    /**
     * 修正見積もりを取得する。
     * 
     * @return 修正見積もり
     */
    public String getEstimatedEffort() {
        return (String)getValue(ESTIMATED_EFFORT);
    }
    
    /**
     * 修正見積もりを設定する。
     * 
     * @param value 修正見積もり
     */
    public void setEstimatedEffort(String value) {
        if(isEnableAttribute(ESTIMATED_EFFORT)) {
            setValue(ESTIMATED_EFFORT, value);
        }
    }
    
    /**
     * 解決方法/処置内容を取得する。
     * 
     * @return 解決方法/処置内容
     */
    public String getResolution() {
        return (String)getValue(RESOLUTION);
    }
    
    /**
     * 解決方法/処置内容を設定する。
     * 
     * @param value 解決方法/処置内容
     */
    public void setResolution(String value) {
        if(isEnableAttribute(RESOLUTION)) {
            setValue(RESOLUTION, value);
        }
    }
    
    /**
     * 処置区分を取得する。
     * 
     * @return 処置区分
     */
    public DispositionTypeArgs getDispositionTypeEnum() {
        return DispositionTypeArgs.getTypeByValue(getDispositionType());
    }
    
    /**
     * 処置区分を取得する。
     * 
     * @return 処置区分
     */
    public String getDispositionType() {
        return (String)getValue(DISPOSITION_TYPE);
    }
    
    /**
     * 処置区分を設定する。
     * 
     * @param value 処置区分
     */
    public void setDispositionType(String value) {
        if(isEnableAttribute(DISPOSITION_TYPE)) {
            setValue(DISPOSITION_TYPE, value);
        }
    }
    
    /**
     * 処置区分を設定する。
     * 
     * @param value 処置区分
     */
    public void setDispositionType(DispositionTypeArgs value) {
        if(isEnableAttribute(DISPOSITION_TYPE)) {
            setDispositionType(value.getValue());
        }
    }
    
    /**
     * 修正対象を取得する。
     * 
     * @return 修正対象
     */
    public String getChangesMadeTo() {
        return (String)getValue(CHANGES_MADE_TO);
    }
    
    /**
     * 修正対象を設定する。
     * 
     * @param value 修正対象
     */
    public void setChangesMadeTo(String value) {
        if(isEnableAttribute(CHANGES_MADE_TO)) {
            setValue(CHANGES_MADE_TO, value);
        }
    }
    
    /**
     * 修正済バージョンを取得する。
     * 
     * @return 修正済バージョン
     */
    public String getCorrectedVersion() {
        return (String)getValue(CORRECTED_VERSION);
    }
    
    /**
     * 修正済バージョンを設定する。
     * 
     * @param value 修正済バージョン
     */
    public void setCorrectedVersion(String value) {
        if(isEnableAttribute(CORRECTED_VERSION)) {
            setValue(CORRECTED_VERSION, value);
        }
    }
    
    /**
     * バグ区分を取得する。
     * 
     * @return バグ区分
     */
    public DefectTypeArgs getDefectTypeEnum() {
        return DefectTypeArgs.getTypeByValue(getDefectType());
    }
    
    /**
     * バグ区分を取得する。
     * 
     * @return バグ区分
     */
    public String getDefectType() {
        return (String)getValue(DEFECT_TYPE);
    }
    
    /**
     * バグ区分を設定する。
     * 
     * @param value バグ区分
     */
    public void setDefectType(String value) {
        if(isEnableAttribute(DEFECT_TYPE)) {
            setValue(DEFECT_TYPE, value);
        }
    }
    
    /**
     * バグ区分を設定する。
     * 
     * @param value バグ区分
     */
    public void setDefectType(DefectTypeArgs value) {
        if(isEnableAttribute(DEFECT_TYPE)) {
            setDefectType(value.getValue());
        }
    }
    
    /**
     * 作り込み工程を取得する。
     * 
     * @return 作り込み工程
     */
    public InsertionPhaseArgs getInsertionPhaseEnum() {
        return InsertionPhaseArgs.getTypeByValue(getInsertionPhase());
    }
    
    /**
     * 作り込み工程を取得する。
     * 
     * @return 作り込み工程
     */
    public String getInsertionPhase() {
        return (String)getValue(INSERTION_PHASE);
    }
    
    /**
     * 作り込み工程を設定する。
     * 
     * @param value 作り込み工程
     */
    public void setInsertionPhase(String value) {
        if(isEnableAttribute(INSERTION_PHASE)) {
            setValue(INSERTION_PHASE, value);
        }
    }
    
    /**
     * 作り込み工程を設定する。
     * 
     * @param value 作り込み工程
     */
    public void setInsertionPhase(InsertionPhaseArgs value) {
        if(isEnableAttribute(INSERTION_PHASE)) {
            setInsertionPhase(value.getValue());
        }
    }
    
    /**
     * 調査工数を取得する。
     * 
     * @return 調査工数
     */
    public String getInvestigationEffort() {
        return (String)getValue(INVESTIGATION_EFFORT);
    }
    
    /**
     * 調査工数を設定する。
     * 
     * @param value 調査工数
     */
    public void setInvestigationEffort(String value) {
        if(isEnableAttribute(INVESTIGATION_EFFORT)) {
            setValue(INVESTIGATION_EFFORT, value);
        }
    }
    
    /**
     * 処置工数を取得する。
     * 
     * @return 処置工数
     */
    public String getDispositionEffort() {
        return (String)getValue(DISPOSITION_EFFORT);
    }
    
    /**
     * 処置工数を設定する。
     * 
     * @param value 処置工数
     */
    public void setDispositionEffort(String value) {
        if(isEnableAttribute(DISPOSITION_EFFORT)) {
            setValue(DISPOSITION_EFFORT, value);
        }
    }
    
    /**
     * 発見すべき工程を取得する。
     * 
     * @return 発見すべき工程
     */
    public PhaseToBeDetectedArgs getPhaseToBeDetectedEnum() {
        return PhaseToBeDetectedArgs.getTypeByValue(getPhaseToBeDetected());
    }
    
    /**
     * 発見すべき工程を取得する。
     * 
     * @return 発見すべき工程
     */
    public String getPhaseToBeDetected() {
        return (String)getValue(PHASE_TO_BE_DETECTED);
    }
    
    /**
     * 発見すべき工程を設定する。
     * 
     * @param value 発見すべき工程
     */
    public void setPhaseToBeDetected(String value) {
        if(isEnableAttribute(PHASE_TO_BE_DETECTED)) {
            setValue(PHASE_TO_BE_DETECTED, value);
        }
    }
    
    /**
     * 発見すべき工程を設定する。
     * 
     * @param value 発見すべき工程
     */
    public void setPhaseToBeDetected(PhaseToBeDetectedArgs value) {
        if(isEnableAttribute(PHASE_TO_BE_DETECTED)) {
            setPhaseToBeDetected(value.getValue());
        }
    }
    
    public BugRecord cloneBugAttribute() {
        BugRecord clone = new IpaBugRecord();
        Iterator itr = bugAttributeMap.entrySet().iterator();
        while(itr.hasNext()) {
            Entry entry = (Entry)itr.next();
            BugAttribute<?> attribute = (BugAttribute<?>)entry.getValue();
            clone.addBugAttribute(attribute.clone());
        }
        ((IpaBugRecord)clone).setAttributeNameSet(attributeNameSet);
        ((IpaBugRecord)clone).setEnableSet(isEnableSet);
        return clone;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{id=" + id + ", ");
        if(uniqueId == null) {
            sb.append("scenarioGroupId=null, scenarioId=null, testCaseId=null, ");
        } else {
            sb.append("scenarioGroupId=" + uniqueId.getScenarioGroupId() + ", scenarioId=" + uniqueId.getScenarioId() + ", testCaseId=" + uniqueId.getTestCaseId() + ", ");
        }
        sb.append("entryDate=" + entryDate + ", ");
        sb.append("updateDate=" + updateDate);
        Iterator itr = bugAttributeMap.entrySet().iterator();
        while(itr.hasNext()) {
            Entry entry = (Entry)itr.next();
            if(isEnableAttribute((String)entry.getKey())) {
                sb.append(", " + entry.getKey() + "=" + ((BugAttribute<?>)entry.getValue()).getValue());
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * 不具合の重要度で選択可能な項目値<br>
     * S,A,B,Cを選択可能<br>
     */
    public enum SeverityArgs {
        S("1"), A("2"), B("3"), C("4");
        private String value;
        
        private SeverityArgs(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static String[] getValues() {
            List result = new ArrayList();
            SeverityArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                result.add(values[i].getValue());
            }
            return (String[]) result.toArray(new String[0]);
        }
        
        public static SeverityArgs getTypeByValue(String value) {
            SeverityArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                if (values[i].getValue().equals(value)) {
                    return values[i];
                }
            }
            return null;
        }
    }
    
    /**
     * 不具合の優先度で選択可能な項目値<br>
     * High,Middle,Lowを選択可能<br>
     */
    public enum PriorityArgs {
        High("1"), Middle("2"), Low("3");
        private String value;
        
        private PriorityArgs(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static String[] getValues() {
            List result = new ArrayList();
            PriorityArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                result.add(values[i].getValue());
            }
            return (String[]) result.toArray(new String[0]);
        }
        
        public static PriorityArgs getTypeByValue(String value) {
            PriorityArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                if (values[i].getValue().equals(value)) {
                    return values[i];
                }
            }
            return null;
        }
    }
    
    /**
     * 不具合のステータスで選択可能な項目値<br>
     * New:起票済<br>
     * Assigned:担当者割当済<br>
     * Analyzed:調査済<br>
     * Resolved:処置済<br>
     * Verified:検証済<br>
     * Closed:完了<br>
     */
    public enum StatusArgs {
        New("0"), Assigned("1"), Analyzed("2"), Resolved("3"), Verified("4"), Closed("5");
        private String value;
        
        private StatusArgs(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static String[] getValues() {
            List result = new ArrayList();
            StatusArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                result.add(values[i].getValue());
            }
            return (String[]) result.toArray(new String[0]);
        }
        
        public static StatusArgs getTypeByValue(String value) {
            StatusArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                if (values[i].getValue().equals(value)) {
                    return values[i];
                }
            }
            return null;
        }
    }
    
    /**
     * 不具合の発生工程で選択可能な項目値<br>
     * Coding:コーディング<br>
     * UnitTest:単体テスト<br>
     * IntegrationTest:結合テスト<br>
     * SystemTest:システムテスト<br>
     * Other:その他<br>
     */
    public enum DetectionPhaseArgs {
        Coding("1"), UnitTest("2"), IntegrationTest("3"), SystemTest("4"), Other("9");
        private String value;
        
        private DetectionPhaseArgs(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static String[] getValues() {
            List result = new ArrayList();
            DetectionPhaseArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                result.add(values[i].getValue());
            }
            return (String[]) result.toArray(new String[0]);
        }
        
        public static DetectionPhaseArgs getTypeByValue(String value) {
            DetectionPhaseArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                if (values[i].getValue().equals(value)) {
                    return values[i];
                }
            }
            return null;
        }
    }
    
    /**
     * 不具合の発生手段で選択可能な項目値<br>
     * CodeReview:コードレビュー<br>
     * Test:テスト<br>
     * Operation:運用<br>
     * Other:その他<br>
     */
    public enum DetectionActivityArgs {
        CodeReview("1"), Test("2"), Operation("3"), Other("9");
        private String value;
        
        private DetectionActivityArgs(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static String[] getValues() {
            List result = new ArrayList();
            DetectionActivityArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                result.add(values[i].getValue());
            }
            return (String[]) result.toArray(new String[0]);
        }
        
        public static DetectionActivityArgs getTypeByValue(String value) {
            DetectionActivityArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                if (values[i].getValue().equals(value)) {
                    return values[i];
                }
            }
            return null;
        }
    }
    
    /**
     * 不具合の影響で選択可能な項目値<br>
     * Functionality:機能性<br>
     * Usability:使用性<br>
     * Security:セキュリティ<br>
     * Performance:パフォーマンス<br>
     * Other:その他<br>
     */
    public enum EffectArgs {
        Functionality("1"), Usability("2"), Security("3"), Performance("4"), Other("9");
        private String value;
        
        private EffectArgs(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static String[] getValues() {
            List result = new ArrayList();
            EffectArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                result.add(values[i].getValue());
            }
            return (String[]) result.toArray(new String[0]);
        }
        
        public static EffectArgs getTypeByValue(String value) {
            EffectArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                if (values[i].getValue().equals(value)) {
                    return values[i];
                }
            }
            return null;
        }
    }
    
    /**
     * 不具合の処置区分で選択可能な項目値<br>
     * Revise:処置する<br>
     * ReviseTheNextEdition:次版で処置する<br>
     * NoRevise:処置しない<br>
     * Other:その他<br>
     */
    public enum DispositionTypeArgs {
        Revise("1"), ReviseTheNextEdition("2"), NoRevise("3"), Other("9");
        private String value;
        
        private DispositionTypeArgs(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static String[] getValues() {
            List result = new ArrayList();
            DispositionTypeArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                result.add(values[i].getValue());
            }
            return (String[]) result.toArray(new String[0]);
        }
        
        public static DispositionTypeArgs getTypeByValue(String value) {
            DispositionTypeArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                if (values[i].getValue().equals(value)) {
                    return values[i];
                }
            }
            return null;
        }
    }
    
    /**
     * バグ区分で選択可能な項目値<br>
     * DescriptionRrrorInRequestDefinition:記述誤り(要求定義)<br>
     * LackOfFunctionInRequestDefinition:機能の欠如(要求定義)<br>
     * DefinitionErrorOfFunction:機能の定義誤り<br>
     * DataErrorInDesign:データの誤り(設計)<br>
     * AlgorithmError:アルゴリズム/制御の誤り<br>
     * InterfaceErrorInDesign:インターフェイスの誤り(設計)<br>
     * TimingErrorInDesign:タイミングの誤り(設計)<br>
     * ResourceErrorInDesign:リソースの誤り(設計)<br>
     * ErrorCheckErrorInDesign:エラーチェックの誤り(設計)<br>
     * DescriptionRrrorInDesign:記述誤り(設計)<br>
     * LackOfFunctionInDesign:機能の欠如(設計)<br>
     * FunctionDesignError:機能の設計誤り<br>
     * DataErrorInCoding:データの誤り(実装)<br>
     * LogicError:ロジックの誤り<br>
     * InterfaceErrorInCoding:インターフェイスの誤り(実装)<br>
     * TimingErrorInCoding:タイミングの誤り(実装)<br>
     * ResourceErrorInCoding:リソースの誤り(実装)<br>
     * ErrorCheckErrorInCoding:エラーチェックの誤り(実装)<br>
     * LackOfFunctionInCoding:機能の欠如(実装)<br>
     * FunctionCodingError:機能の実装誤り<br>
     * IntegrationError:統合の誤り<br>
     * DatabaseError:データベースの誤り<br>
     * OSSoftwareError:OS/ソフトウェアの誤り<br>
     * Other:その他<br>
     */
    public enum DefectTypeArgs {
        DescriptionRrrorInRequestDefinition("01"),
        LackOfFunctionInRequestDefinition("02"),
        DefinitionErrorOfFunction("03"),
        DataErrorInDesign("04"),
        AlgorithmError("05"),
        InterfaceErrorInDesign("06"),
        TimingErrorInDesign("07"),
        ResourceErrorInDesign("08"),
        ErrorCheckErrorInDesign("09"),
        DescriptionRrrorInDesign("10"),
        LackOfFunctionInDesign("11"),
        FunctionDesignError("12"),
        DataErrorInCoding("13"),
        LogicError("14"),
        InterfaceErrorInCoding("15"),
        TimingErrorInCoding("16"),
        ResourceErrorInCoding("17"),
        ErrorCheckErrorInCoding("18"),
        LackOfFunctionInCoding("19"),
        FunctionCodingError("20"),
        IntegrationError("21"),
        DatabaseError("22"),
        OSSoftwareError("23"),
        Other("99");
        
        private String value;
        
        private DefectTypeArgs(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static String[] getValues() {
            List result = new ArrayList();
            DefectTypeArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                result.add(values[i].getValue());
            }
            return (String[]) result.toArray(new String[0]);
        }
        
        public static DefectTypeArgs getTypeByValue(String value) {
            DefectTypeArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                if (values[i].getValue().equals(value)) {
                    return values[i];
                }
            }
            return null;
        }
    }
    
    /**
     * 不具合の作り込み工程で選択可能な項目値<br>
     * SystemRequirementDefinition:システム要求定義<br>
     * SystemArchitectureDesign:システムアーキテクチャ設計<br>
     * SoftwareRequirementDefinition:ソフトウェア要求定義<br>
     * SoftwareArchitectureDesign:ソフトウェアアーキテクチャ設計<br>
     * SoftwareDetailedDesign:ソフトウェア詳細設計<br>
     * Coding:コーディング<br>
     */
    public enum InsertionPhaseArgs {
        SystemRequirementDefinition("1"), SystemArchitectureDesign("2"), SoftwareRequirementDefinition("3"), SoftwareArchitectureDesign(
                "4"), SoftwareDetailedDesign("5"), Coding("6"), Other("9");
        private String value;
        
        private InsertionPhaseArgs(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static String[] getValues() {
            List result = new ArrayList();
            InsertionPhaseArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                result.add(values[i].getValue());
            }
            return (String[]) result.toArray(new String[0]);
        }
        
        public static InsertionPhaseArgs getTypeByValue(String value) {
            InsertionPhaseArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                if (values[i].getValue().equals(value)) {
                    return values[i];
                }
            }
            return null;
        }
    }
    
    /**
     * 発見すべき工程で選択可能な項目値<br>
     * Coding:コーディング<br>
     * UnitTest:単体テスト<br>
     * IntegrationTest:結合テスト<br>
     * SystemTest:システムテスト<br>
     * Other:その他<br>
     */
    public enum PhaseToBeDetectedArgs {
        Coding("1"), UnitTest("2"), IntegrationTest("3"), SystemTest("4"), Other("9");
        private String value;
        
        private PhaseToBeDetectedArgs(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static String[] getValues() {
            List result = new ArrayList();
            PhaseToBeDetectedArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                result.add(values[i].getValue());
            }
            return (String[]) result.toArray(new String[0]);
        }
        
        public static PhaseToBeDetectedArgs getTypeByValue(String value) {
            PhaseToBeDetectedArgs[] values = values();
            for (int i = 0; i < values.length; i++) {
                if (values[i].getValue().equals(value)) {
                    return values[i];
                }
            }
            return null;
        }
    }
    
}
