package jp.ossc.nimbus.service.test.bug;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class IpaBugRecord extends BugRecord {
    
    // タイトル
    protected BugAttribute<String> title;
    // プロジェクト名
    protected BugAttribute<String> projectName;
    // 重要度
    protected BugAttribute<String> severity;
    // 優先度
    protected BugAttribute<String> priority;
    // ステータス
    protected BugAttribute<String> status;
    // 同件ID
    protected BugAttribute<String> duplicateId;
    
    // 起票者
    protected BugAttribute<String> openedBy;
    // バグ発見者
    protected BugAttribute<String> observedBy;
    // 発見日時
    protected BugAttribute<Date> observedDate;
    // 調査担当者
    protected BugAttribute<String> analyzedBy;
    // 調査完了日
    protected BugAttribute<Date> analyzedDate;
    // 処置方針決定日
    protected BugAttribute<Date> determinedDate;
    // 処置担当者
    protected BugAttribute<String> resolvedBy;
    // 処置期限日
    protected BugAttribute<Date> dueDate;
    // 処置完了日
    protected BugAttribute<Date> resolvedDate;
    // 処置承認者
    protected BugAttribute<String> approvedBy;
    // 検証担当者
    protected BugAttribute<String> verifiedBy;
    // 検証完了日
    protected BugAttribute<Date> verifiedDate;
    // リリース日
    protected BugAttribute<Date> releasedDate;
    
    // 発生した機能/サブシステム
    protected BugAttribute<String> component;
    // 発生したバージョン
    protected BugAttribute<String> detectedVersion;
    // 発生した環境
    protected BugAttribute<String> environment;
    // 発生した頻度
    protected BugAttribute<String> frequency;
    // 発生した工程
    protected BugAttribute<String> detectionPhase;
    // 発見手段
    protected BugAttribute<String> detectionActivity;
    // 影響
    protected BugAttribute<String> effect;
    
    // 発生原因
    protected BugAttribute<String> cause;
    // 原因箇所
    protected BugAttribute<String> defectFoundIn;
    // 修正見積もり
    protected BugAttribute<Float> estimatedEffort;
    
    // 解決方法/処置内容
    protected BugAttribute<String> resolution;
    // 解決方法/処置内容
    protected BugAttribute<String> dispositionType;
    // 修正対象
    protected BugAttribute<String> changesMadeTo;
    // 修正済バージョン
    protected BugAttribute<String> correctedVersion;
    
    // バグ区分
    protected BugAttribute<String> defectType;
    // 作り込み工程
    protected BugAttribute<String> insertionPhase;
    
    // 調査工数
    protected BugAttribute<Float> investigationEffort;
    // 処置工数
    protected BugAttribute<Float> dispositionEffort;
    // 発見すべき工程
    protected BugAttribute<String> phaseToBeDetected;
    
    protected Set attributeNameSet;
    protected boolean isEnableSet;
    
    /**
     * 全項目を有効にしてレコードを作成する。
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
        
        this.attributeNameSet = attributeNameSet; 
        this.isEnableSet = isEnableSet;
        
        if(isEnableAttribute("title")) {
            // タイトル
            title = new BugAttribute<String>("title");
            addBugAttribute(title);
        }
        if(isEnableAttribute("projectName")) {
            // プロジェクト名
            projectName = new BugAttribute<String>("projectName");
            addBugAttribute(projectName);
        }
        if(isEnableAttribute("severity")) {
            // 重要度
            severity = new SelectableBugAttribute<String>("severity", SeverityArgs.getValues());
            addBugAttribute(severity);
        }
        if(isEnableAttribute("priority")) {
            // 優先度
            priority = new SelectableBugAttribute<String>("priority", PriorityArgs.getValues());
            addBugAttribute(priority);
        }
        if(isEnableAttribute("status")) {
            // ステータス
            status = new SelectableBugAttribute<String>("status", StatusArgs.getValues());
            addBugAttribute(status);
        }
        
        if(isEnableAttribute("duplicateId")) {
            // 同件ID
            duplicateId = new BugAttribute<String>("duplicateId");
            addBugAttribute(duplicateId);
        }
        if(isEnableAttribute("openedBy")) {
            // 起票者
            openedBy = new BugAttribute<String>("openedBy");
            addBugAttribute(openedBy);
        }
        if(isEnableAttribute("observedBy")) {
            // バグ発見者
            observedBy = new BugAttribute<String>("observedBy");
            addBugAttribute(observedBy);
        }
        if(isEnableAttribute("observedDate")) {
            // 発見日時
            observedDate = new BugAttribute<Date>("observedDate");
            addBugAttribute(observedDate);
        }
        if(isEnableAttribute("analyzedBy")) {
            // 調査担当者
            analyzedBy = new BugAttribute<String>("analyzedBy");
            addBugAttribute(analyzedBy);
        }
        if(isEnableAttribute("analyzedDate")) {
            // 調査完了日
            analyzedDate = new BugAttribute<Date>("analyzedDate");
            addBugAttribute(analyzedDate);
        }
        if(isEnableAttribute("determinedDate")) {
            // 処置方針決定日
            determinedDate = new BugAttribute<Date>("determinedDate");
            addBugAttribute(determinedDate);
        }
        if(isEnableAttribute("resolvedBy")) {
            // 処置担当者
            resolvedBy = new BugAttribute<String>("resolvedBy");
            addBugAttribute(resolvedBy);
        }
        if(isEnableAttribute("dueDate")) {
            // 処置期限日
            dueDate = new BugAttribute<Date>("dueDate");
            addBugAttribute(dueDate);
        }
        if(isEnableAttribute("resolvedDate")) {
            // 処置完了日
            resolvedDate = new BugAttribute<Date>("resolvedDate");
            addBugAttribute(resolvedDate);
        }
        if(isEnableAttribute("approvedBy")) {
            // 処置承認者
            approvedBy = new BugAttribute<String>("approvedBy");
            addBugAttribute(approvedBy);
        }
        if(isEnableAttribute("verifiedBy")) {
            // 検証担当者
            verifiedBy = new BugAttribute<String>("verifiedBy");
            addBugAttribute(verifiedBy);
        }
        if(isEnableAttribute("verifiedDate")) {
            // 検証完了日
            verifiedDate = new BugAttribute<Date>("verifiedDate");
            addBugAttribute(verifiedDate);
        }
        if(isEnableAttribute("releasedDate")) {
            // リリース日
            releasedDate = new BugAttribute<Date>("releasedDate");
            addBugAttribute(releasedDate);
        }
        
        if(isEnableAttribute("component")) {
            // 発生した機能/サブシステム
            component = new BugAttribute<String>("component");
            addBugAttribute(component);
        }
        if(isEnableAttribute("detectedVersion")) {
            // 発生したバージョン
            detectedVersion = new BugAttribute<String>("detectedVersion");
            addBugAttribute(detectedVersion);
        }
        if(isEnableAttribute("environment")) {
            // 発生した環境
            environment = new BugAttribute<String>("environment");
            addBugAttribute(environment);
        }
        if(isEnableAttribute("frequency")) {
            // 発生した頻度
            frequency = new BugAttribute<String>("frequency");
            addBugAttribute(frequency);
        }
        if(isEnableAttribute("detectionPhase")) {
            // 発生した工程
            detectionPhase = new SelectableBugAttribute<String>("detectionPhase", DetectionPhaseArgs.getValues());
            addBugAttribute(detectionPhase);
        }
        if(isEnableAttribute("detectionActivity")) {
            // 発見手段
            detectionActivity = new SelectableBugAttribute<String>("detectionActivity", DetectionActivityArgs.getValues());
            addBugAttribute(detectionActivity);
        }
        if(isEnableAttribute("effect")) {
            // 影響
            effect = new SelectableBugAttribute<String>("effect", EffectArgs.getValues());
            addBugAttribute(effect);
        }
        
        if(isEnableAttribute("cause")) {
            // 発生原因
            cause = new BugAttribute<String>("cause");
            addBugAttribute(cause);
        }
        if(isEnableAttribute("defectFoundIn")) {
            // 原因箇所
            defectFoundIn = new BugAttribute<String>("defectFoundIn");
            addBugAttribute(defectFoundIn);
        }
        if(isEnableAttribute("estimatedEffort")) {
            // 修正見積もり
            estimatedEffort = new BugAttribute<Float>("estimatedEffort");
            addBugAttribute(estimatedEffort);
        }
        
        if(isEnableAttribute("resolution")) {
            // 解決方法/処置内容
            resolution = new BugAttribute<String>("resolution");
            addBugAttribute(resolution);
        }
        if(isEnableAttribute("dispositionType")) {
            // 解決方法/処置内容
            dispositionType = new SelectableBugAttribute<String>("dispositionType", DispositionTypeArgs.getValues());
            addBugAttribute(dispositionType);
        }
        if(isEnableAttribute("changesMadeTo")) {
            // 修正対象
            changesMadeTo = new BugAttribute<String>("changesMadeTo");
            addBugAttribute(changesMadeTo);
        }
        if(isEnableAttribute("correctedVersion")) {
            // 修正済バージョン
            correctedVersion = new BugAttribute<String>("correctedVersion");
            addBugAttribute(correctedVersion);
        }
        
        if(isEnableAttribute("defectType")) {
            // バグ区分
            defectType = new SelectableBugAttribute<String>("defectType", new String[] { "" });
            addBugAttribute(defectType);
        }
        if(isEnableAttribute("insertionPhase")) {
            // 作り込み工程
            insertionPhase = new SelectableBugAttribute<String>("insertionPhase", InsertionPhaseArgs.getValues());
            addBugAttribute(insertionPhase);
        }
        
        if(isEnableAttribute("investigationEffort")) {
            // 調査工数
            investigationEffort = new BugAttribute<Float>("investigationEffort");
            addBugAttribute(investigationEffort);
        }
        if(isEnableAttribute("dispositionEffort")) {
            // 処置工数
            dispositionEffort = new BugAttribute<Float>("dispositionEffort");
            addBugAttribute(dispositionEffort);
        }
        if(isEnableAttribute("phaseToBeDetected")) {
            // 発見すべき工程
            phaseToBeDetected = new SelectableBugAttribute<String>("phaseToBeDetected", DetectionPhaseArgs.getValues());
            addBugAttribute(phaseToBeDetected);
        }
        
    }
    
    private boolean isEnableAttribute(String name) {
        if (attributeNameSet == null) {
            return true;
        }
        return isEnableSet ? attributeNameSet.contains(name) : !attributeNameSet.contains(name);
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
     * 不具合の発生工程/発見すべき工程で選択可能な項目値<br>
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
}
