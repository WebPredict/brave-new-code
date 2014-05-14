package wp.model;

public enum CrawlingIntensity {

	SLOW (20),
	MEDIUM (10),
	FAST (5),
	IMMEDIATE(0);
	
	private int	speed;
	private CrawlingIntensity (int speed) {
		this.speed = speed;
	}
	
	/**
	 * Multiplier for crawling speed: multiplies round trip time by this number for pause time between
	 * crawls.
	 * @return
	 */
	public int	getSpeed () {
		return (speed);
	}
}
