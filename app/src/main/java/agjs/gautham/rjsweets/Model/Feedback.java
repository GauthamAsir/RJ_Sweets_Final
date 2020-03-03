package agjs.gautham.rjsweets.Model;

public class Feedback {

    private String feedQuestions;
    private String otherOpinions;

    public Feedback() {
    }

    public Feedback(String feedQuestions, String otherOpinions) {
        this.feedQuestions = feedQuestions;
        this.otherOpinions = otherOpinions;
    }

    public String getFeedQuestions() {
        return feedQuestions;
    }

    public void setFeedQuestions(String feedQuestions) {
        this.feedQuestions = feedQuestions;
    }

    public String getOtherOpinions() {
        return otherOpinions;
    }

    public void setOtherOpinions(String otherOpinions) {
        this.otherOpinions = otherOpinions;
    }
}
