package agjs.gautham.rjsweets.Model;

public class Flags {

    private boolean splashUpdateCheck;
    private boolean makeOrders;
    private Double latestVersion;
    private boolean mandatoryLatestUpdate;
    private String makeOrdersReason;

    public Flags() {
    }

    public Flags(boolean splashUpdateCheck, boolean makeOrders, Double latestVersion, boolean mandatoryLatestUpdate, String makeOrdersReason) {
        this.splashUpdateCheck = splashUpdateCheck;
        this.makeOrders = makeOrders;
        this.latestVersion = latestVersion;
        this.mandatoryLatestUpdate = mandatoryLatestUpdate;
        this.makeOrdersReason = makeOrdersReason;
    }

    public boolean isSplashUpdateCheck() {
        return splashUpdateCheck;
    }

    public void setSplashUpdateCheck(boolean splashUpdateCheck) {
        this.splashUpdateCheck = splashUpdateCheck;
    }

    public boolean isMakeOrders() {
        return makeOrders;
    }

    public void setMakeOrders(boolean makeOrders) {
        this.makeOrders = makeOrders;
    }

    public Double getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(Double latestVersion) {
        this.latestVersion = latestVersion;
    }

    public boolean isMandatoryLatestUpdate() {
        return mandatoryLatestUpdate;
    }

    public void setMandatoryLatestUpdate(boolean mandatoryLatestUpdate) {
        this.mandatoryLatestUpdate = mandatoryLatestUpdate;
    }

    public String getMakeOrdersReason() {
        return makeOrdersReason;
    }

    public void setMakeOrdersReason(String makeOrdersReason) {
        this.makeOrdersReason = makeOrdersReason;
    }
}
