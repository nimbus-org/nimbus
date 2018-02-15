package jp.ossc.nimbus.util.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.lang.IllegalArgumentException;

/**
 * 正規表現コンバータ。<p>
 * 
 * @author Y.Yamashina
 */
public class PatternStringConverter
 implements StringConverter, java.io.Serializable{
    
    private static final long serialVersionUID = 9056240502325078689L;
    
    /**
     * 変換パターン配列。<p>
     */
    protected Object[][] convertObjects;
    
    /**
     * マッチングフラグ。<p>
     */
    protected int matchingFlag = -1;
    
    /**
     * 空の正規表現コンバータを生成する。<p>
     */
    public PatternStringConverter(){
    }
    
    /**
     * 指定されたマッチフラグの正規表現コンバータを生成する。<p>
     *
     * @param flags マッチフラグ
     */
    public PatternStringConverter(int flags){
        setMatchingFlag(flags);
    }
    
    /**
     * 指定されたマッチフラグと変換パターンの正規表現コンバータを生成する。<p>
     *
     * @param fromStrs 変換対象正規表現パターン文字列配列
     * @param toStrs 変換後文字列配列
     */
    public PatternStringConverter(
        int flags,
        String[] fromStrs,
        String[] toStrs
    ){
        setMatchingFlag(flags);
        setConvertStrings(fromStrs, toStrs);
    }
    
    /**
     * 正規表現のマッチングを行う時のマッチフラグを設定する。<p>
     * マッチフラグとは、Pattern.CASE_INSENSITIVE、Pattern.MULTILINE、Pattern.DOTALL、Pattern.UNICODE_CASE、Pattern.CANON_EQ などを含むビットマスクである。
     *
     * @param flags マッチフラグ
     */
    public void setMatchingFlag(int flags){
        // マッチングフラグが変更された場合、patternを再作成
        if(matchingFlag != flags && convertObjects != null){
            for(int i = 0; i < convertObjects.length; i++){
                Pattern pattern = (Pattern)convertObjects[i][0];
                
                Pattern newPattern = null;
                if(flags != -1){
                    newPattern = Pattern.compile(pattern.pattern(), flags);
                } else {
                    newPattern = Pattern.compile(pattern.pattern());
                }
                convertObjects[i][0] = newPattern;
            }
        }
        matchingFlag = flags;
    }
    
    /**
     * 正規表現のマッチングを行う時のマッチフラグを取得する。<p>
     *
     * @return マッチフラグ
     */
    public int getMatchingFlag(){
        return matchingFlag;
    }
    
    /**
     * 変換パターンを設定する。<p>
     *
     * @param fromStrs 変換対象正規表現パターン文字列配列
     * @param toStrs 変換後文字列配列
     */
    public void setConvertStrings(String[] fromStrs, String[] toStrs){
        if(toStrs == null && fromStrs == null){
            convertObjects = null;
        }else if((toStrs == null || fromStrs == null)
            || toStrs.length != fromStrs.length){
            throw new IllegalArgumentException("Invalid ConvertStrings.");
        }else{
            final Object[][] convObjs = new Object[toStrs.length][];
            try{
                for(int i = 0; i < toStrs.length; i++){
                    if(fromStrs[i] == null || toStrs[i] == null){
                        throw new IllegalArgumentException(
                            "Invalid ConvertStrings."
                        );
                    }
                    Pattern pattern = null;
                    if(matchingFlag!=-1) {
                    	pattern = Pattern.compile(fromStrs[i], matchingFlag);
                    } else {
                    	pattern = Pattern.compile(fromStrs[i]);
                    }
                    convObjs[i] = new Object[]{pattern, toStrs[i]};
                }
            }catch(PatternSyntaxException pe){
                //定義されたマッチフラグに対応しないビット値が flags に設定された場合
                throw new IllegalArgumentException("Invalid ConvertStrings.");
            }catch(IllegalArgumentException ie){
                //表現の構文が無効である場合
                throw new IllegalArgumentException("Invalid ConvertStrings.");
            }
            
            convertObjects = convObjs;
        }
    }
    
    // ConverterのJavaDoc
    public Object convert(Object obj) throws ConvertException{
        if(obj == null){
            return null;
        }else{
            return convert(
                (String)(obj instanceof String ? obj : String.valueOf(obj))
            );
        }
    }
    
    /**
     * 文字列を変換する。<p>
     * 変換パターン配列を使って変換する。<br>
     *
     * @param str 変換対象の文字列 
     * @return 変換後の文字列
     * @exception ConvertException 変換に失敗した場合
     */
    public String convert(String str) throws ConvertException{
        String result = str;
        final Object[][] convObjects = convertObjects;
        
        if(convObjects != null){
            for(int i = 0; i < convObjects.length; i++){
                Pattern pattern = (Pattern)convObjects[i][0];
                
                Matcher matcher = pattern.matcher(result);
                result = matcher.replaceAll((String)convObjects[i][1]);
            }
        }
        return result;
    }
}
