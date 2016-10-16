package com.mobility.android.data.model;

public class Filter {

    private final String title;
    private boolean checked;

    public Filter(String title, boolean checked) {
        this.title = title;
        this.checked = checked;
    }

    public String getTitle() {
        return title;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}