package ee.carlrobert.codegpt.settings.service;

public class FillInTheMiddle {
    private final String prefix;
    private final String suffix;
    private final String middle;
    private final String eot;

    public FillInTheMiddle(String prefix, String suffix, String middle, String eot) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.middle = middle;
        this.eot = eot;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getMiddle() {
        return middle;
    }

    public String getEot() {
        return eot;
    }
}
