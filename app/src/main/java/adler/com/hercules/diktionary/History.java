/*
Created by: Ogor Anumbor
date      : 2/12/2019
 */

package adler.com.hercules.diktionary;

public class History {
    private String en_word;
    private String en_def;

    public History(String en_word, String en_def){
        this.en_word = en_word;
        this.en_def = en_def;
    }

    public String getWord(){
        return this.en_word;
    }

    public String getDefinition(){
        return this.en_def;
    }
} // end class