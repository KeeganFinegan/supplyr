package com.supplyr.supplyr.domain;

public class LowestAskHighestBidDto {

    private double lowestAsk;
    private double highestBid;

    public LowestAskHighestBidDto() {
    }

    public double getLowestAsk() {
        return lowestAsk;
    }

    public void setLowestAsk(double lowestAsk) {
        this.lowestAsk = lowestAsk;
    }

    public double getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(double highestBid) {
        this.highestBid = highestBid;
    }

    @Override
    public String toString() {
        return "LowestAskHighestBidDto{" +
                "lowestAsk=" + lowestAsk +
                ", highestBid=" + highestBid +
                '}';
    }
}
