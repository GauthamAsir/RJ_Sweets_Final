package agjs.gautham.rjsweets.Model;

public class AppUpdate {

    private String app_name;
    private String changelog;
    private String update_url;
    private Double version;

    public AppUpdate() {
    }

    public AppUpdate(String app_name, String changelog, String update_url, Double version) {
        this.app_name = app_name;
        this.changelog = changelog;
        this.update_url = update_url;
        this.version = version;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public String getUpdate_url() {
        return update_url;
    }

    public void setUpdate_url(String update_url) {
        this.update_url = update_url;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }
}
