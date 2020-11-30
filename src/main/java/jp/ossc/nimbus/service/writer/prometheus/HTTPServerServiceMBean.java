package jp.ossc.nimbus.service.writer.prometheus;

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link HTTPServerService}サービスMBeanインタフェース。<p>
 *
 * @author M.Ishida
 */
public interface HTTPServerServiceMBean extends ServiceBaseMBean {
    
    /**
     * HTTPServerを起動する際のポートを取得する。<p>
     * 
     * @return ポート
     */
    public int getPort();
    
    /**
     * HTTPServerを起動する際のポートを設定する。<p>
     * 
     * @param port ポート
     */
    public void setPort(int port);
    
}
