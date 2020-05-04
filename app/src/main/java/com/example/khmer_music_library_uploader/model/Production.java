package com.example.khmer_music_library_uploader.model;

public class Production {
    private String productionId;
    private String productionName;
    private String url;

    public Production(String productionName, String url) {
        this.productionName = productionName;
        this.url = url;
    }

    public Production(String productionId, String productionName,String str) {
        this.productionId = productionId;
        this.productionName = productionName;
    }

    public String getProductionId() {
        return productionId;
    }

    public void setProductionId(String productionId) {
        this.productionId = productionId;
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
