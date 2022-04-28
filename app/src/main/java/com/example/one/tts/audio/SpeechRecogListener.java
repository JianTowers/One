package com.example.one.tts.audio;

import com.example.one.tts.audio.entity.ContentRes;

public interface SpeechRecogListener {
    void recogMessage(ContentRes contentRes);
    void recogErr(Exception e);
    void recogBefore();
    void recogStart();
    void recogStop();
    void recogEnd();

}