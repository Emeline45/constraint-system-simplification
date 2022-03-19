package utils;

public class BooleanHolder {
    private Boolean value;

    public BooleanHolder() {
        this(true);
    }
    public BooleanHolder(final Boolean b) {
        this.value = b;
    }
    public BooleanHolder(final boolean b) {
        this.value = b;
    }

    public boolean get() {
        return this.value;
    }

    public void set(final boolean newValue) {
        this.value = newValue;
    }
    public void set(final Boolean newValue) {
        this.value = newValue;
    }
}
