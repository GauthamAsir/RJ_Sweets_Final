package agjs.gautham.rjsweets.Model;

public class Feedback {

    private String fno;
    private String fquestion;
    private String great;
    private String good;
    private String neutral;
    private String bad;
    private String very_bad;

    public Feedback() {
    }

    public Feedback(String fno, String fquestion) {
        this.fno = fno;
        this.fquestion = fquestion;
    }

    public String getFno() {
        return fno;
    }

    public void setFno(String fno) {
        this.fno = fno;
    }

    public String getFquestion() {
        return fquestion;
    }

    public void setFquestion(String fquestion) {
        this.fquestion = fquestion;
    }

    public String getGreat() {
        return great;
    }

    public void setGreat(String great) {
        this.great = great;
    }

    public String getGood() {
        return good;
    }

    public void setGood(String good) {
        this.good = good;
    }

    public String getNeutral() {
        return neutral;
    }

    public void setNeutral(String neutral) {
        this.neutral = neutral;
    }

    public String getBad() {
        return bad;
    }

    public void setBad(String bad) {
        this.bad = bad;
    }

    public String getVery_bad() {
        return very_bad;
    }

    public void setVery_bad(String very_bad) {
        this.very_bad = very_bad;
    }
}
