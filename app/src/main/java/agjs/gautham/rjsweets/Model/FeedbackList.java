package agjs.gautham.rjsweets.Model;

public class FeedbackList {

    public String id_f;
    public String f_q;
    public String f_ans;

    public FeedbackList(String id_f, String f_q, String f_ans) {
        this.id_f = id_f;
        this.f_q = f_q;
        this.f_ans = f_ans;
    }

    public String getId_f() {
        return id_f;
    }

    public void setId_f(String id_f) {
        this.id_f = id_f;
    }

    public String getF_q() {
        return f_q;
    }

    public void setF_q(String f_q) {
        this.f_q = f_q;
    }

    public String getF_ans() {
        return f_ans;
    }

    public void setF_ans(String f_ans) {
        this.f_ans = f_ans;
    }
}
