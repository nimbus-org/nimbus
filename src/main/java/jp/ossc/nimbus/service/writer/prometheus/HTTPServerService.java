package jp.ossc.nimbus.service.writer.prometheus;

import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import jp.ossc.nimbus.core.ServiceBase;

/**
 * HTTPServer{@link HTTPServer}を起動するためのサービス。<p>
 *
 * @author M.Ishida
 */
public class HTTPServerService extends ServiceBase {

    private static final long serialVersionUID = 5975106195065729828L;

    protected int port;
    
    protected HTTPServer server;
    
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void startService() throws Exception{
        if(port <= 0) {
            throw new IllegalArgumentException("Port is illegal value. port=" + port);
        }
        server = new HTTPServer(port);
        DefaultExports.initialize();
    }
    
    public void stopService() throws Exception{
        server.stop();
    }
    
    public void destroyService() throws Exception{
        server = null;
    }
    
}
