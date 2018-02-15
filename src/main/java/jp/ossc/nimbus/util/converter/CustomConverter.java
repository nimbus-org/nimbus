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
package jp.ossc.nimbus.util.converter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * カスタムコンバータ。<p>
 *
 * @author M.Takata
 */
public class CustomConverter
 implements Converter, StringConverter, CharacterConverter, StreamStringConverter, BindingStreamConverter,FormatConverter,
            java.io.Serializable, Cloneable{

    private static final long serialVersionUID = 727589924434574684L;

    protected List converters;

    /**
     * 空のカスタムコンバータを生成する。<p>
     */
    public CustomConverter(){
    }

    /**
     * カスタムコンバータを生成する。<p>
     *
     * @param convs コンバータ配列
     */
    public CustomConverter(Converter[] convs){
        if(convs != null && convs.length != 0){
            converters = new ArrayList();
            for(int i = 0; i < convs.length; i++){
                converters.add(convs[i]);
            }
        }
    }

    /**
     * 変換種別を設定する。<p>
     * 追加されたコンバータのうちで、{@link ReversibleConverter}のインスタンスに変換種別を設定する。<br>
     *
     * @param type 変換種別
     */
    public void setConvertType(int type){
        if(converters != null){
            for(int i = 0, max = converters.size(); i < max; i++){
                if(converters.get(i) instanceof ReversibleConverter){
                    ((ReversibleConverter)converters.get(i)).setConvertType(type);
                }
            }
        }
    }

    /**
     * オブジェクトからストリームへ変換する際の文字エンコーディングを設定する。<p>
     *
     * @param encoding オブジェクトからストリームへ変換する際の文字エンコーディング
     */
    public void setCharacterEncodingToStream(String encoding){
        if(converters != null){
            for(int i = 0, max = converters.size(); i < max; i++){
                if(converters.get(i) instanceof StreamStringConverter){
                    ((StreamStringConverter)converters.get(i)).setCharacterEncodingToStream(encoding);
                }
            }
        }
    }
    public String getCharacterEncodingToStream(){
        if(converters != null){
            for(int i = 0, max = converters.size(); i < max; i++){
                if(converters.get(i) instanceof StreamStringConverter){
                    return ((StreamStringConverter)converters.get(i)).getCharacterEncodingToStream();
                }
            }
        }
        return null;
    }

    /**
     * ストリームからオブジェクトへ変換する際の文字エンコーディングを設定する。<p>
     *
     * @param encoding ストリームからオブジェクトへ変換する際の文字エンコーディング
     */
    public void setCharacterEncodingToObject(String encoding){
        if(converters != null){
            for(int i = 0, max = converters.size(); i < max; i++){
                if(converters.get(i) instanceof StreamStringConverter){
                    ((StreamStringConverter)converters.get(i)).setCharacterEncodingToObject(encoding);
                }
            }
        }
    }
    public String getCharacterEncodingToObject(){
        if(converters != null){
            for(int i = 0, max = converters.size(); i < max; i++){
                if(converters.get(i) instanceof StreamStringConverter){
                    return ((StreamStringConverter)converters.get(i)).getCharacterEncodingToObject();
                }
            }
        }
        return null;
    }

    public StreamStringConverter cloneCharacterEncodingToStream(String encoding){
        try{
            CustomConverter clone = (CustomConverter)clone();
            if(converters != null){
                clone.converters = new ArrayList();
                for(int i = 0, max = converters.size(); i < max; i++){
                    if(converters.get(i) instanceof StreamStringConverter){
                        clone.converters.add(((StreamStringConverter)converters.get(i)).cloneCharacterEncodingToStream(encoding));
                    }else{
                        clone.converters.add(converters.get(i));
                    }
                }
            }
            return clone;
        }catch(CloneNotSupportedException e){
            return null;
        }
    }

    public StreamStringConverter cloneCharacterEncodingToObject(String encoding){
        try{
            CustomConverter clone = (CustomConverter)clone();
            if(converters != null){
                clone.converters = new ArrayList();
                for(int i = 0, max = converters.size(); i < max; i++){
                    if(converters.get(i) instanceof StreamStringConverter){
                        clone.converters.add(((StreamStringConverter)converters.get(i)).cloneCharacterEncodingToObject(encoding));
                    }else{
                        clone.converters.add(converters.get(i));
                    }
                }
            }
            return clone;
        }catch(CloneNotSupportedException e){
            return null;
        }
    }

    public void setFormat(String format) {
        if(converters != null){
            for(int i = 0, max = converters.size(); i < max; i++){
                if(converters.get(i) instanceof FormatConverter){
                    ((FormatConverter)converters.get(i)).setFormat(format);
                }
            }
        }
    }

   /**
     * コンバータを追加する。<p>
     *
     * @param converter コンバータ
     * @return 自分自身のインスタンス
     */
    public Converter add(Converter converter){
        if(converters == null){
            converters = new ArrayList();
        }
        converters.add(converter);
        return this;
    }

    /**
     * コンバータを削除する。<p>
     *
     * @param converter コンバータ
     */
    public void remove(Converter converter){
        if(converters == null){
            return;
        }
        converters.remove(converter);
    }

    /**
     * コンバータを全て削除する。<p>
     */
    public void clear(){
        if(converters == null){
            return;
        }
        converters.clear();
    }

    /**
     * 指定されたオブジェクトを変換する。<p>
     * 追加されたコンバータに順次、変換を依頼して変換結果を返す。<br>
     *
     * @param obj 変換対象のオブジェクト
     * @return 変換後のオブジェクト
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convert(Object obj) throws ConvertException{
        Object tmp = obj;
        if(converters != null){
            for(int i = 0, max = converters.size(); i < max; i++){
                tmp = ((Converter)converters.get(i)).convert(tmp);
            }
        }
        return tmp;
    }

    /**
     * 指定された文字列を変換する。<p>
     * 追加されたコンバータに順次、変換を依頼して変換結果を返す。<br>
     *
     * @param str 変換対象のキャラクタ
     * @return 変換後のキャラクタ
     * @exception ConvertException 変換に失敗した場合
     */
    public String convert(String str) throws ConvertException{
        Object tmp = str;
        if(converters != null){
            for(int i = 0, max = converters.size(); i < max; i++){
                tmp = ((Converter)converters.get(i)).convert(tmp);
            }
        }
        return (String)tmp;
    }

    /**
     * 指定されたキャラクタを変換する。<p>
     * 追加されたキャラクタコンバータに順次、変換を依頼して変換結果を返す。<br>
     * {@link CharacterConverter}以外のConverterは無視される。
     *
     * @param c 変換対象のキャラクタ
     * @return 変換後のキャラクタ
     * @exception ConvertException 変換に失敗した場合
     */
    public char convert(char c) throws ConvertException{
        char tmp = c;
        if(converters != null){
            for(int i = 0, max = converters.size(); i < max; i++){
                if(converters.get(i) instanceof CharacterConverter){
                    tmp = ((CharacterConverter)converters.get(i)).convert(tmp);
                }
            }
        }
        return tmp;
    }

    /**
     * 指定されたキャラクタを変換する。<p>
     * 追加されたコンバータに順次、変換を依頼して変換結果を返す。<br>
     *
     * @param c 変換対象のキャラクタ
     * @return 変換後のキャラクタ
     * @exception ConvertException 変換に失敗した場合
     */
    public Character convert(Character c) throws ConvertException{
        Object tmp = c;
        if(converters != null){
            for(int i = 0, max = converters.size(); i < max; i++){
                tmp = ((Converter)converters.get(i)).convert(tmp);
            }
        }
        return (Character)tmp;
    }

    /**
     * オブジェクトからストリームへ変換する。<p>
     * 追加されたコンバータに順次、変換を依頼して変換結果を返す。
     * 但し、最後のコンバータは、{@link StreamConverter}とみなして、{@link StreamConverter#convertToStream(Object)}を呼び出す。<br>
     *
     * @param obj オブジェクト
     * @return 変換結果を読み取る入力ストリーム
     * @exception ConvertException 変換に失敗した場合
     */
    public InputStream convertToStream(Object obj) throws ConvertException{
        Object tmp = obj;
        if(converters != null && converters.size() > 0){
            if(converters.size() > 1){
                for(int i = 0, max = converters.size() - 1; i < max; i++){
                    tmp = ((Converter)converters.get(i)).convert(tmp);
                }
            }
            tmp = ((StreamConverter)converters.get(converters.size() - 1)).convertToStream(tmp);
        }
        return (InputStream)tmp;
    }

    /**
     * ストリームからオブジェクトへ変換する。<p>
     * 追加されたコンバータに順次、変換を依頼して変換結果を返す。
     * 但し、最初のコンバータは、{@link StreamConverter}とみなして、{@link StreamConverter#convertToObject(InputStream)}を呼び出す。<br>
     *
     * @param is 入力ストリーム
     * @return オブジェクト
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convertToObject(InputStream is) throws ConvertException{
        Object tmp = is;
        if(converters != null && converters.size() > 0){
            tmp = ((StreamConverter)converters.get(0)).convertToObject(is);
            if(converters.size() > 1){
                for(int i = 1, max = converters.size(); i < max; i++){
                    tmp = ((Converter)converters.get(i)).convert(tmp);
                }
            }
        }
        return tmp;
    }

    /**
     * ストリームからオブジェクトへ変換する。<p>
     * 追加されたコンバータに順次、変換を依頼して変換結果を返す。
     * 但し、最初のコンバータは、{@link StreamConverter}、または{@link BindingStreamConverter}とみなして、{@link StreamConverter#convertToObject(InputStream)}または{@link BindingStreamConverter#convertToObject(InputStream, Object)}を呼び出す。<br>
     *
     * @param is 入力ストリーム
     * @param returnType 変換対象のオブジェクト
     * @return オブジェクト
     * @throws ConvertException 変換に失敗した場合
     */
    public Object convertToObject(InputStream is, Object returnType) throws ConvertException{
        Object tmp = is;
        if(converters != null && converters.size() > 0){
            if(converters.get(0) instanceof BindingStreamConverter){
                tmp = ((BindingStreamConverter)converters.get(0)).convertToObject(is, returnType);
            }else{
                tmp = ((StreamConverter)converters.get(0)).convertToObject(is);
            }
            if(converters.size() > 1){
                for(int i = 1, max = converters.size(); i < max; i++){
                    if((converters.get(i) instanceof BindingStreamConverter)
                        && (tmp instanceof InputStream)){
                        tmp = ((BindingStreamConverter)converters.get(i)).convertToObject((InputStream)tmp, returnType);
                    }else{
                        tmp = ((Converter)converters.get(i)).convert(tmp);
                    }
                }
            }
        }
        return tmp;
    }

}
