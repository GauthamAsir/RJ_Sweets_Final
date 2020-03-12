package agjs.gautham.rjsweets.Model;

public class FeedModel {

    private String Bad;
    private String Good;
    private String Great;
    private String Neutral;
    private String VeryBad;

    public FeedModel() {
    }

    public FeedModel(String bad, String good, String great, String neutral, String veryBad) {
        Bad = bad;
        Good = good;
        Great = great;
        Neutral = neutral;
        VeryBad = veryBad;
    }

    public String getBad() {
        return Bad;
    }

    public void setBad(String bad) {
        Bad = bad;
    }

    public String getGood() {
        return Good;
    }

    public void setGood(String good) {
        Good = good;
    }

    public String getGreat() {
        return Great;
    }

    public void setGreat(String great) {
        Great = great;
    }

    public String getNeutral() {
        return Neutral;
    }

    public void setNeutral(String neutral) {
        Neutral = neutral;
    }

    public String getVeryBad() {
        return VeryBad;
    }

    public void setVeryBad(String veryBad) {
        VeryBad = veryBad;
    }
}
