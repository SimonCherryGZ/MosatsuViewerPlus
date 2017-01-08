package com.simoncherry.mosatsuviewerplus.event;

import java.io.File;

/**
 * Created by Simon on 2017/1/7.
 */

public class ImageChangeEvent {

    private File file;
    private String path;

    public ImageChangeEvent(File file) {
        this.file = file;
    }

    public ImageChangeEvent(String path) {
        this.path = path;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
