package mcit.ddr.innovation.enums;

public enum PersonType {

    Hokmi("حکمی"),
    Haqiqi("حقیقی");


    private final String displayName;

    PersonType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}