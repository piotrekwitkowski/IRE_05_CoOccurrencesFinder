class Bigram {
    private String w1;
    private String w2;
    private double ratio;

    Bigram(String w1, String w2) {
        this.w1 = w1;
        this.w2 = w2;
    }

    public String getW1() {
        return w1;
    }

    public String getW2() {
        return w2;
    }

}
