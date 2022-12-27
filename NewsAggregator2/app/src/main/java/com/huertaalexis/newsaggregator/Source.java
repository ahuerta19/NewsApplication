package com.huertaalexis.newsaggregator;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Source implements Serializable {

    private final String id;
    private final String name;
    private final String category;

    public Source(String id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }
    String getId(){
        return id;
    }
    String getName(){
        return name;
    }
    String getCategory(){
        return category;
    }
    @NonNull
    public String toString() {
        return name;
    }
}
