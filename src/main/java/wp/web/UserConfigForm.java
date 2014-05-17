package wp.web;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorForm;

import wp.core.Utils;
import wp.model.CategorySet;
import wp.model.Features;
import wp.model.UserConfig;
import wp.service.UserConfigService;

/**
 * @struts.form name="userConfigForm"
 * @author jeff
 *
 */
public class UserConfigForm extends ValidatorForm {

	private String	maxRating;
	private boolean	considerAds;
	private boolean	considerPopups;
	private String	name;
	private String	description;
	private String	ratingCategories;
	private String	desirableRatings;
	private boolean	ratingsOrderable;
	private String	ratingsSetName;
	private boolean	countRepeats;
	private boolean	ignoreCommonWords;
	private String	maxWordsToConsider = "2000";
	private String	headlinesWeight = "3";
	private String	phraseLength = "1";
	private boolean	weightLinks;
	private boolean smoothing;
	private boolean	useGlobalWordProb;
	private boolean	useCache;
	private boolean	genericizeNumbers;
	private boolean ignoreCapitalization;
	private boolean	considerNumLinks;
	private boolean	considerNumImages;
	private boolean	useStrictParser;
	private List<FeatureForm>	features = new ArrayList<FeatureForm>();
	
	public List<FeatureForm>	getFeaturesList() {
		return (features);
	}

	public FeatureForm	getFeaturesListElement (int idx) {
		while (features.size() <= idx)
			features.add(new FeatureForm());
		return (features.get(idx));
	}
	
	public boolean isConsiderNumLinks() {
		return considerNumLinks;
	}

	public void setConsiderNumLinks(boolean considerNumLinks) {
		this.considerNumLinks = considerNumLinks;
	}

	public boolean isConsiderNumImages() {
		return considerNumImages;
	}

	public void setConsiderNumImages(boolean considerNumImages) {
		this.considerNumImages = considerNumImages;
	}

	public boolean isUseStrictParser() {
		return useStrictParser;
	}

	public void setUseStrictParser(boolean useStrictParser) {
		this.useStrictParser = useStrictParser;
	}

	public boolean isIgnoreCapitalization() {
		return ignoreCapitalization;
	}

	public void setIgnoreCapitalization(boolean ignoreCapitalization) {
		this.ignoreCapitalization = ignoreCapitalization;
	}

	public String getPhraseLength() {
		return phraseLength;
	}

	public void setPhraseLength(String phraseLength) {
		this.phraseLength = phraseLength;
	}
	public boolean isGenericizeNumbers() {
		return genericizeNumbers;
	}

	public void setGenericizeNumbers(boolean genericizeNumbers) {
		this.genericizeNumbers = genericizeNumbers;
	}

	public boolean isUseGlobalWordProb() {
		return useGlobalWordProb;
	}

	public void setUseGlobalWordProb(boolean useGlobalWordProb) {
		this.useGlobalWordProb = useGlobalWordProb;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public boolean isSmoothing() {
		return smoothing;
	}

	public void setSmoothing(boolean smoothing) {
		this.smoothing = smoothing;
	}

	public UserConfig	updatePremiumParams (UserConfig params) {
			
		return (params);
	}
	
	public boolean isCountRepeats() {
		return countRepeats;
	}
	public void setCountRepeats(boolean countRepeats) {
		this.countRepeats = countRepeats;
	}
	public boolean isIgnoreCommonWords() {
		return ignoreCommonWords;
	}
	public void setIgnoreCommonWords(boolean ignoreCommonWords) {
		this.ignoreCommonWords = ignoreCommonWords;
	}
	public String getMaxWordsToConsider() {
		return maxWordsToConsider;
	}
	public void setMaxWordsToConsider(String maxWordsToConsider) {
		this.maxWordsToConsider = maxWordsToConsider;
	}
	public String getHeadlinesWeight() {
		return headlinesWeight;
	}
	public void setHeadlinesWeight(String headlinesWeight) {
		this.headlinesWeight = headlinesWeight;
	}
	public boolean isWeightLinks() {
		return weightLinks;
	}
	public void setWeightLinks(boolean weightLinks) {
		this.weightLinks = weightLinks;
	}
	
	public boolean isRatingsOrderable() {
		return ratingsOrderable;
	}

	public void setRatingsOrderable(boolean ratingsOrderable) {
		this.ratingsOrderable = ratingsOrderable;
	}

	public String getRatingsSetName() {
		return ratingsSetName;
	}

	public void setRatingsSetName(String ratingsSetName) {
		this.ratingsSetName = ratingsSetName;
	}

	public String getDesirableRatings() {
		return desirableRatings;
	}

	public void setDesirableRatings(String desirableRatings) {
		this.desirableRatings = desirableRatings;
	}

	public String getRatingCategories() {
		return ratingCategories;
	}

	/**
	 * @struts.validator type="required"
	 * @struts.validator-args arg0resource="ratingCategories"
	 */public void setRatingCategories(String ratingCategories) {
		this.ratingCategories = ratingCategories;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isConsiderAds() {
		return considerAds;
	}

	public void setConsiderAds(boolean considerAds) {
		this.considerAds = considerAds;
	}

	public boolean isConsiderPopups() {
		return considerPopups;
	}

	public void setConsiderPopups(boolean considerPopups) {
		this.considerPopups = considerPopups;
	}

	public void	bind (UserConfig user) {
		if (user != null) {
			maxRating = user.getMaxRating() == null ? "" : String.valueOf(user.getMaxRating());
			considerAds = user.isConsiderAds();
			considerPopups = user.isConsiderPopups();
			name = user.getName();
			description = user.getDescription();
			ratingCategories = user.getCategorySet().getRawCategories();
			ratingsOrderable = user.getCategorySet().isOrdered();
			ratingsSetName = user.getCategorySet().getName();
			desirableRatings = user.getDesirableRatings();
			
			countRepeats = user.isCountRepeats();
			ignoreCommonWords = user.isIgnoreCommonWords();
			maxWordsToConsider = String.valueOf(user.getMaxWordsToConsider());
			headlinesWeight = String.valueOf(user.getHeadlinesWeight());
			weightLinks = user.isWeightLinks();
			smoothing = user.isSmoothProbs();
			useCache = user.isUseCache();
			useGlobalWordProb = user.isUsePreciseWordProb();
			genericizeNumbers = user.isGenericizeNumbers();
			phraseLength = String.valueOf(user.getPhraseLength());
			ignoreCapitalization = user.isIgnoreCapitalization();
			useStrictParser = user.isUseStrictParser();
			considerNumLinks = user.isConsiderNumLinks();
			considerNumImages = user.isConsiderNumImages();
		}
		
		List<String>	theirFeatures = user == null ? new ArrayList<String>() : user.getFeatures();
		features.clear();
		for (Features f : Features.values()) {
			FeatureForm	ff = new FeatureForm();
			ff.setName(f.getPrettyPrint());
			if (theirFeatures != null && theirFeatures.contains(f.getPrettyPrint()))
				ff.setKeep(true);
			else
				ff.setKeep(false);
			features.add(ff);
		}
		
	}
	
	public UserConfig	extract (UserConfigService userService) throws SQLException {
		
		UserConfig	user = userService.findByName(name);
		if (user == null)
			user = new UserConfig();
		if (!StringUtils.isEmpty(maxRating)) {
			try {
				int		maxRatingInt = Integer.parseInt(maxRating);
				user.setMaxRating(maxRatingInt);
			}
			catch (NumberFormatException e) {
				user.setMaxRating(null);
			}
		}
		else
			user.setMaxRating(null);
		user.setConsiderAds(considerAds);
		user.setConsiderPopups(considerPopups);
		user.setName(name);
		user.setDescription(description);
		
		if (user.getCategorySet() == null) {
			CategorySet	set = new CategorySet(user, ratingCategories, ratingsSetName);
			user.setCategorySet(set);
		}
		user.getCategorySet().setRawCategories(ratingCategories);
		user.getCategorySet().setOrdered(ratingsOrderable);
		user.getCategorySet().setName(ratingsSetName);
		user.setDesirableRatings(desirableRatings);
		
		user.setCountRepeats(countRepeats);
		user.setIgnoreCommonWords(ignoreCommonWords);
		user.setWeightLinks(weightLinks);
		user.setSmoothProbs(smoothing);
		user.setUseCache(useCache);
		user.setUsePreciseWordProb(useGlobalWordProb);
		user.setGenericizeNumbers(genericizeNumbers);
		user.setIgnoreCapitalization(ignoreCapitalization);
		user.setConsiderNumImages(considerNumImages);
		user.setConsiderNumLinks(considerNumLinks);
		user.setUseStrictParser(useStrictParser);
		
		List<String>	theirFeatures = user == null ? new ArrayList<String>() : user.getFeatures();
		for (FeatureForm ff : features) {
			String	feature = ff.getName();
			if (ff.isKeep()) {
				if (!theirFeatures.contains(feature))
					theirFeatures.add(feature);
			}
			else {
				if (theirFeatures.contains(feature))
					theirFeatures.remove(feature);
			}
		}
		user.setFeatures(theirFeatures);
		
		try {
			if (!StringUtils.isEmpty(phraseLength))
				user.setPhraseLength(Integer.parseInt(phraseLength));
		}
		catch (NumberFormatException e) {	
		}
		
		try {
			if (!StringUtils.isEmpty(headlinesWeight))
				user.setHeadlinesWeight(Double.parseDouble(headlinesWeight));
		}
		catch (NumberFormatException e) {	
		}
		
		try {
			if (!StringUtils.isEmpty(maxWordsToConsider))
				user.setMaxWordsToConsider(Integer.parseInt(maxWordsToConsider));
		}
		catch (NumberFormatException e) {
		}
		return (user);
	}
	
	public void	validateSecondPass (ActionMessages errors, ActionMapping mapping, HttpServletRequest request) {
		//ActionErrors	errors = super.validate(mapping, request);
	
		if (StringUtils.isEmpty(ratingCategories)) 
			errors.add("ratingCategories", new ActionMessage("error.emptyCategories"));
		else {
			StringTokenizer	tok = new StringTokenizer(ratingCategories, ",");
			
			HashSet<String>	ratingSet = new HashSet<String>();
			while (tok.hasMoreTokens()) {
				String	next = tok.nextToken().trim();
				if (StringUtils.isEmpty(next) || next.length() > 30) {
					errors.add("ratingCategories", new ActionMessage("error.invalidCategories", next));
					break;
				}
				if (Utils.hasVulgarities(next)) {
					errors.add("ratingCategories", new ActionMessage("error.vulgarities", next));
					break;
				}
				if (ratingSet.contains(next)) {
					errors.add("ratingCategories", new ActionMessage("error.duplicateRatings", next));
					break;
				}
				ratingSet.add(next);
			}
			
			if (!StringUtils.isEmpty(desirableRatings)) {
				tok = new StringTokenizer(desirableRatings, ", ");
				
				while (tok.hasMoreTokens()) {
					String	next = tok.nextToken().trim();
					if (!ratingSet.contains(next)) {
						errors.add("desirableRatings", new ActionMessage("error.desirableRatings", next));
						break;
					}
				}
			}
			if (!StringUtils.isEmpty(maxRating)) {
				if (!ratingSet.contains(maxRating))
					errors.add("maxRating", new ActionMessage("error.maxRating", maxRating));
				
			}
		}
		
		if (Utils.hasVulgarities(name)) {
			errors.add("name", new ActionMessage("error.vulgarities", name));
		}
		if (Utils.hasVulgarities(description)) {
			errors.add("description", new ActionMessage("error.vulgarities", description));
		}
		
		//return (errors);
	}
	
	public String getMaxRating() {
		return maxRating;
	}
	
	public void setMaxRating(String maxRating) {
		this.maxRating = maxRating;
	}
	
}
