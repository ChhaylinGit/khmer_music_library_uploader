package com.example.khmer_music_library_uploader.model;

public class Production {
     String productionName="";
     String url;

    public Production(String productionName, String url) {
        this.productionName = productionName;
        this.url = url;
    }

    public String getProductionName() {
        return productionName;
    }

    public void setProductionName(String productionName) {
        this.productionName = productionName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
