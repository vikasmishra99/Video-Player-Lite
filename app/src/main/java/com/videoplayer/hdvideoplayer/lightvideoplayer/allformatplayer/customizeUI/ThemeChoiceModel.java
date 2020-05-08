package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.customizeUI;

public class ThemeChoiceModel {
    int color;
    int id;

    public ThemeChoiceModel(int color, int id){
        this.color = color;
        this.id = id;
    }
    public int getColor(){
        return color;
    }
    public int getId(){
        return id;
    }
    public void setColor(int value){
        this.color = value;
    }
    public void setId(int value){
        this.id = value;
    }
}
