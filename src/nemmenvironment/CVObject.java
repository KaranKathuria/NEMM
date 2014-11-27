package nemmenvironment;

public class CVObject {
	
	private double currentsupplyratio;
	private double futuresupplyratio;
	private double currentbank;
	private double futurebank;
	private double currentticksupply;
	private double currenttickdemand;
	private double futureticksupply;	//The producion in, and only in, the future tick
	private double futuretickdemand;	//The demand in, and only in, the future tick
	private double betweentickscumulativedemand;
	private double betweentickscumulativesupply;

	public double getBetweentickscumulativedemand() {
		return betweentickscumulativedemand;
	}


	public void setBetweentickscumulativedemand(double betweentickscumulativedemand) {
		this.betweentickscumulativedemand = betweentickscumulativedemand;
	}


	public double getBetweentickscumulativesupply() {
		return betweentickscumulativesupply;
	}


	public void setBetweentickscumulativesupply(double betweentickscumulativesupply) {
		this.betweentickscumulativesupply = betweentickscumulativesupply;
	}


	public CVObject() {
		// Constructor initialises everything to 0
		currentsupplyratio = 0;
		futuresupplyratio = 0;
		currentbank = 0;
		futurebank = 0;
		currentticksupply = 0;
		currenttickdemand = 0;
		futureticksupply = 0;
		futuretickdemand = 0;
		betweentickscumulativedemand = 0;
		betweentickscumulativesupply = 0;
	}


	public double getCurrentsupplyratio() {
		return currentsupplyratio;
	}
	public void setCurrentsupplyratio(double currentsupplyratio) {
		this.currentsupplyratio = currentsupplyratio;
	}
	public double getFuturesupplyratio() {
		return futuresupplyratio;
	}
	public void setFuturesupplyratio(double futuresupplyratio) {
		this.futuresupplyratio = futuresupplyratio;
	}
	public double getCurrentbank() {
		return currentbank;
	}
	public void setCurrentbank(double currentbank) {
		this.currentbank = currentbank;
	}
	public double getFuturebank() {
		return futurebank;
	}
	public void setFuturebank(double futurebank) {
		this.futurebank = futurebank;
	}
	public double getFutureticksupply() {
		return futureticksupply;
	}
	public void setFutureticksupply(double futureticksupply) {
		this.futureticksupply = futureticksupply;
	}
	public double getFuturetickdemand() {
		return futuretickdemand;
	}
	public void setFuturetickdemand(double futuretickdemand) {
		this.futuretickdemand = futuretickdemand;
	}


	public double getCurrentticksupply() {
		return currentticksupply;
	}


	public void setCurrentticksupply(double currentticksupply) {
		this.currentticksupply = currentticksupply;
	}


	public double getCurrenttickdemand() {
		return currenttickdemand;
	}


	public void setCurrenttickdemand(double currenttickdemand) {
		this.currenttickdemand = currenttickdemand;
	}
	
	
	
	
}
