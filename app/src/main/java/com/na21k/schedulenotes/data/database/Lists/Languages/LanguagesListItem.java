package com.na21k.schedulenotes.data.database.Lists.Languages;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import com.na21k.schedulenotes.data.database.SimpleListItem;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "languages_list_items",
        indices = {@Index(value = "id"), @Index(value = "is_archived")})
public class LanguagesListItem extends SimpleListItem {

    @ColumnInfo(name = "transcription")
    private String transcription;

    @ColumnInfo(name = "translation")
    private String translation;

    @ColumnInfo(name = "explanation")
    private String explanation;

    @ColumnInfo(name = "usage_example_text")
    private String usageExampleText;

    @ColumnInfo(name = "is_archived", defaultValue = "0")
    private boolean isArchived;

    public LanguagesListItem(int id, @NotNull String text, String transcription, String translation,
                             String explanation, String usageExampleText) {
        this.id = id;
        this.text = text;
        this.transcription = transcription;
        this.translation = translation;
        this.explanation = explanation;
        this.usageExampleText = usageExampleText;
        isArchived = false;
    }

    public LanguagesListItem(LanguagesListItem languagesListItem) {
        this(languagesListItem.id, languagesListItem.text, languagesListItem.transcription,
                languagesListItem.translation, languagesListItem.explanation,
                languagesListItem.usageExampleText);
        isArchived = languagesListItem.isArchived;
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

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }
}
