package jp.ossc.nimbus.service.test.bug;

/**
 * 不具合情報。<p>
 * 不具合情報を保持するためのインターフェイス。<br>
 *
 * @author M.Ishida
 */
public interface BugRecord {
    
    public String getId();
    
    public void setId(String id);
    
    public BugAttribute[] getBugAttributes();
    
    public void addBugAttribute(BugAttribute attribute);
    
    public String getValue(String name);
    
    public void setValue(String name, String value);
    
    public interface BugAttribute {
        
        public void setName(String name);
        
        public String getName();
        
        public void setValue(String value);
        
        public String getValue();
        
        public interface SelectableBugAttribute extends BugAttribute {
            
            public void setSelectableValues(String[] values);
            
            public String[] getSelectableValues();
            
        }
        
    }
    
}
