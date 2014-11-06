package nemmstrategy_shortterm;

public class BidOffer implements Comparable<BidOffer> {

	// Holds all information regarding a bid or offer in the market
	// Note that currently all bids and offer volumes are positive
	// numbers (although this should be changed later I think)
	
	private double certVolume;
	private double price;
	private double[] utility;
	private double shareCleared;

	public BidOffer() {
		certVolume = 0.0;
		price = 0.0;
		shareCleared = 0.0;
	}
	
	@Override
	public int compareTo(BidOffer o) {
		// Compare based on utility
		int lastCmp;
		if (price == o.getPrice()) {lastCmp = 0;}
		else if (price < o.getPrice()) {lastCmp = -1;}
		else {lastCmp = 1;}
		return lastCmp;
	}

	public double getCertVolume() {
		return certVolume;
	}

	public void setCertVolume(double certVolume) {
		this.certVolume = certVolume;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double[] getUtility() {
		return utility;
	}

	public void setUtility(double[] utility) {
		this.utility = utility;
	}
	
	public double getShareCleared() {
		return shareCleared;
	}

	public void setShareCleared(double shareCleared) {
		this.shareCleared = shareCleared;
	}
	
}
