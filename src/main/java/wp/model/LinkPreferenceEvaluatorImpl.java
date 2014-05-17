package wp.model;

import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import wp.core.Rater;


public class LinkPreferenceEvaluatorImpl implements LinkPreferenceEvaluator {

	private UserStats stats;
	private User	user;
	private HashMap<LinkInfo, Double>	lookup = new HashMap<LinkInfo, Double>();
	
	public LinkPreferenceEvaluatorImpl (User user, UserStats stats) {
		this.stats = stats;
		this.user = user;
	}

	public double howAppealingIsThis(LinkInfo info) {
		if (user == null || user.getDesirableRatings() == null)
			return (1);
		
		if (lookup.containsKey(info))
			return (lookup.get(info));
		
		double	ret = 0;
		try {
			StringBuffer	buf = new StringBuffer();
			if (!StringUtils.isEmpty(info.getText()))
				buf.append(info.getText());
			
			if (StringUtils.isNotEmpty(info.getContext())) {
				if (buf.length() > 0)
					buf.append(" ");
				buf.append(info.getContext());
			}
			Prediction	pred = Rater.getTheRater().predictRatingSimpleText(stats, buf.toString(), null, 255);
			
			boolean		smooth = user.isSmoothProbs();
			boolean		isNumeric = user.getCategorySet().isNumeric();
			
			if (smooth && isNumeric) {
				//List<Double>	numericCats = user.getCategorySet().getAsNumbers();
				
				Integer	maxRating = user.getMaxRating();
				
				try {
					double	predVal = Double.parseDouble(pred.getRating());
					double	diff = Math.abs((double)maxRating - predVal);
					ret = 1d / diff + 1;
				}
				catch (Exception e) {
					
				}
			}
			else {
				Collection<String>	desirable = user.getDesirableRatingsSet();
				
				String	rating = pred.getRating();
				
				if (desirable.contains(rating))
					ret = 1;
				String	second = pred.getSecondGuess();
				if (desirable.contains(second))
					ret = .5;
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		lookup.put(info, ret);
		return ret;
		
	}
}
