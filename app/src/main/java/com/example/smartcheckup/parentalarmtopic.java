package com.example.smartcheckup;

public class parentalarmtopic {

    public String topic;
    public parentalarmtopic(String topic)
    {
        if(topic.isEmpty())
            topic="";
        this.topic=topic;
    }
}
