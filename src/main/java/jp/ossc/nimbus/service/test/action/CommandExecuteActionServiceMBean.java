package jp.ossc.nimbus.service.test.action;

/**
 * {@link CommandExecuteActionService}のMBeanインタフェース<p>
 * 
 * @author T.Takakura
 * @see CommandExecuteActionService
 */
public interface CommandExecuteActionServiceMBean {
    
    /**
     * コマンド実行時に適用する環境変数を設定する。<p>
     *
     * @param environments 環境変数。変数名=値の配列で指定する。
     */
    public void setEnvironments(String[] environments);
    
    /**
     * コマンド実行時に適用する環境変数を取得する。<p>
     *
     * @return 環境変数。変数名=値の配列で指定する。
     */
    public String[] getEnvironments();
    
    /**
     * ログファイルの終了待ちをする場合の、ログファイルチェック間隔[ms]を設定する。<p>
     * デフォルトは、1秒。<br>
     *
     * @param interval チェック間隔
     */
    public void setCheckInterval(long interval);
    
    /**
     * ログファイルの終了待ちをする場合の、ログファイルチェック間隔[ms]を取得する。<p>
     *
     * @return チェック間隔
     */
    public long getCheckInterval();
    
    /**
     * このアクションのリソース定義を作成する際のデフォルトの想定コストを設定する。<p>
     * 
     * @param cost 想定コスト
     */
    public void setExpectedCost(double cost);
    
    /**
     * このアクションのリソース定義を作成する際のデフォルトの想定コストを取得する。<p>
     * 
     * @return 想定コスト
     */
    public double getExpectedCost();

}
