package wp.model;


public interface LinkPreferenceEvaluator {

	/**
	 * 
	 * @param linkContext
	 * @return 0 = I hate it, 1 = I love it
	 */
	public double	howAppealingIsThis (LinkInfo info);
}
