package com.na21k.schedulenotes.data.database.Lists.Languages;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import com.na21k.schedulenotes.data.database.SimpleListItem;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "languages_list_items", indices = {@Index(value = "id")})
public class LanguagesListItem extends SimpleListItem {

    @ColumnInfo(name = "transcription")
    private String transcription;

    @ColumnInfo(name = "translation")
    private String translation;

    @ColumnInfo(name = "explanation")
    private String explanation;

    @ColumnInfo(name = "usage_example_text")
    private String usageExampleText;

    public LanguagesListItem(int id, @NotNull String text, String transcription, String translation,
                             String explanation, String usageExampleText) {
        this.id = id;
        this.text = text;
        this.transcription = transcription;
        this.translation = translation;
        this.explanation = explanation;
        this.usageExampleText = usageExampleText;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getUsageExampleText() {
        return usageExampleText;
    }

    public void setUsageExampleText(String usageExampleText) {
        this.usageExampleText = usageExampleText;
    }
}
