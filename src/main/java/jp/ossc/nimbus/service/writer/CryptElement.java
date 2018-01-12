package jp.ossc.nimbus.service.writer;

import jp.ossc.nimbus.service.crypt.Crypt;

/**
 * 任意の文字列を暗号化する{@link WritableElement}実装クラス。<p>
 */
public class CryptElement extends SimpleElement {
    
    private static final long serialVersionUID = -1736395385754458771L;
    
    /** 暗号化サービス */
    private Crypt crypt;
    
    /**
     * 暗号化サービスを設定する。<p>
     * 
     * @param crypt 暗号化サービス
     */
    public void setCrypt(Crypt crypt){
        this.crypt = crypt;
    }
    
    /**
     * 暗号化サービスを取得する。<p>
     * 
     * @return 暗号化サービス
     */
    public Crypt getCrypt(){
        return crypt;
    }
    
    /**
     * この要素(文字列)を暗号化して取得する。<p>
     * 
     * @return この要素(文字列)を暗号化したもの
     */
    public String toString(){
        if(mValue == null){
            return null;
        }
        
        String ret = null;
        if(crypt != null
            && mValue instanceof String){
            ret = crypt.doEncode((String) mValue);
        }
        
        return convertString(ret);
    }

    /**
     * この要素(文字列)を暗号化して取得する。<p>
     * {@link #toString()}と同じ値を返す。<br>
     * 
     * @return この要素(文字列)を暗号化したもの
     */
    public Object toObject(){
        return toString();
    }

}
