package com.simoncherry.mosatsuviewerplus.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Simon on 2017/1/2.
 */

public class GalleryModel extends RealmObject {

    @PrimaryKey
    private long id;
    private int index;
    private String topPath;
    private String bottomPath;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTopPath() {
        return topPath;
    }

    public void setTopPath(String topPath) {
        this.topPath = topPath;
    }

    public String getBottomPath() {
        return bottomPath;
    }

    public void setBottomPath(String bottomPath) {
        this.bottomPath = bottomPath;
    }

    @Override
    public String toString() {
        return "GalleryModel{" +
                "id=" + id +
                ", index=" + index +
                ", topPath='" + topPath + '\'' +
                ", bottomPath='" + bottomPath + '\'' +
                '}';
    }
}
