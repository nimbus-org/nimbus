package jp.ossc.nimbus.service.converter;

import jp.ossc.nimbus.core.*;

/**
 * {@link CodeMasterConverterService}サービスMBeanインタフェース。<p>
 *
 * @author M.Takata
 */
public interface CodeMasterConverterServiceMBean extends ServiceBaseMBean{
    
    public void setThreadContextServiceName(ServiceName name);
    public ServiceName getThreadContextServiceName();
    
    public void setThreadContextKey(String key);
    public String getThreadContextKey();
    
    public void setCodeMasterFinderServiceName(ServiceName name);
    public ServiceName getCodeMasterFinderServiceName();
    
    public void setMasterName(String name);
    public String getMasterName();
    
    public void setCodeMasterConverterServiceName(ServiceName name);
    public ServiceName getCodeMasterConverterServiceName();
}