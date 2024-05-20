package com.example.firebaserdcrud;

public class DataClass {
    private String datatitle;
    private String dataDesc;
    private String datalang;
    private String dataImg;
    private String key;

    public DataClass(){

    }

    public DataClass(String datatitle, String dataDesc, String datalang, String dataImg) {
        this.datatitle = datatitle;
        this.dataDesc = dataDesc;
        this.datalang = datalang;
        this.dataImg = dataImg;
    }

    public String getDatatitle() {
        return datatitle;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public String getDatalang() {
        return datalang;
    }

    public String getDataImg() {
        return dataImg;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
