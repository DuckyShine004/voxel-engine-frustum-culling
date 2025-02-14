package com.duckyshine.app.display;

public class Display {
    private static final Display DISPLAY = new Display();

    private DisplayType displayType;

    private Display() {
        this.displayType = DisplayType.DEFAULT;
    }

    public static Display get() {
        return Display.DISPLAY;
    }

    public void setDisplayType(DisplayType displayType) {
        this.displayType = displayType;
    }

    public DisplayType getDisplayType() {
        return this.displayType;
    }

    public int getWidth() {
        return this.displayType.getWidth();
    }

    public int getHeight() {
        return this.displayType.getHeight();
    }
}
