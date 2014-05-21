package wp.web;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorForm;

import wp.model.Privacy;
import wp.model.User;
import wp.service.UserService;
import wp.utils.Utils;

/**
 * @struts.form name="userForm"
 * @author jeff
 *
 */
public class UserForm extends ValidatorForm {

	private String	userId;
	private String	emailAddr;
	private String	password;
	private String	maxRating;
	private String password2;
	private boolean	considerAds;
	private boolean	considerPopups;
	private String	name;
	private boolean premium;
	private boolean	ratingsPublic;
	private String	privacy;
	private String	description;
	private String	ratingCategories;
	private String	desirableRatings;
	private boolean	ratingsOrderable;
	private String	ratingsSetName;
	private boolean	countRepeats;
	private boolean	ignoreCommonWords;
	private String		maxWordsToConsider = "10000";
	private String		headlinesWeight = "3";
	private String	phraseLength = "1";
	private boolean	weightLinks;
	private boolean	returnAnalyzedText;
	private boolean smoothing;
	private boolean	useGlobalWordProb;
	private boolean	useCache;
	private boolean	genericizeNumbers;
	private boolean ignoreCapitalization;
	private boolean	considerNumLinks;
	private boolean	considerNumImages;
	private boolean	useStrictParser;
	private boolean	obfuscateFeed;
	
	
	public boolean isObfuscateFeed() {
		return obfuscateFeed;
	}

	public void setObfuscateFeed(boolean obfuscateFeed) {
		this.obfuscateFeed = obfuscateFeed;
	}

	public String getPrivacy() {
		return privacy;
	}

	public void setPrivacy(String privacy) {
		this.privacy = privacy;
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

	public boolean isReturnAnalyzedText() {
		return returnAnalyzedText;
	}

	public void setReturnAnalyzedText(boolean returnAnalyzedText) {
		this.returnAnalyzedText = returnAnalyzedText;
	}

	public User	updatePremiumParams (User params) {
		params.setCountRepeats(countRepeats);
		params.setIgnoreCommonWords(ignoreCommonWords);
		params.setWeightLinks(weightLinks);
		params.setReturnAnalyzedText(returnAnalyzedText);
		params.setSmoothProbs(smoothing);
		params.setUseCache(useCache);
		params.setUsePreciseWordProb(useGlobalWordProb);
		params.setGenericizeNumbers(genericizeNumbers);
		params.setIgnoreCapitalization(ignoreCapitalization);
		params.setConsiderNumImages(considerNumImages);
		params.setConsiderNumLinks(considerNumLinks);
		params.setUseStrictParser(useStrictParser);
		
		try {
			if (!StringUtils.isEmpty(phraseLength))
				params.setPhraseLength(Integer.parseInt(phraseLength));
		}
		catch (NumberFormatException e) {	
		}
		
		try {
			if (!StringUtils.isEmpty(headlinesWeight))
				params.setHeadlinesWeight(Double.parseDouble(headlinesWeight));
		}
		catch (NumberFormatException e) {	
		}
		
		try {
			if (!StringUtils.isEmpty(maxWordsToConsider))
				params.setMaxWordsToConsider(Integer.parseInt(maxWordsToConsider));
		}
		catch (NumberFormatException e) {
		}
		
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
	 */
	public void setRatingCategories(String ratingCategories) {
		this.ratingCategories = ratingCategories;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRatingsPublic() {
		return ratingsPublic;
	}

	public void setRatingsPublic(boolean ratingsPublic) {
		this.ratingsPublic = ratingsPublic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPremium() {
		return premium;
	}

	public void setPremium(boolean premium) {
		this.premium = premium;
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

	public void	bind (User user) {
		userId = user.getUserId();
		emailAddr = user.getEmailAddr();
		password = password2 = user.getPassword();
		maxRating = user.getMaxRating() == null ? "" : String.valueOf(user.getMaxRating());
		considerAds = user.isConsiderAds();
		considerPopups = user.isConsiderPopups();
		premium = user.isPremium();
		obfuscateFeed = user.isObfuscateFeed();
		name = user.getName();
		ratingsPublic = user.isRatingsPublic();
		description = user.getDescription();
		ratingCategories = user.getCategorySet().getRawCategories();
		ratingsOrderable = user.getCategorySet().isOrdered();
		ratingsSetName = user.getCategorySet().getName();
		desirableRatings = user.getDesirableRatings();
		
		Privacy	p = user.getPrivacy();
		switch (p) {
		case PUBLIC:
			privacy = "public";
			break;
			
		case ANON:
			privacy = "anon";
			break;
			
		case PRIVATE:
			privacy = "private";
			break;
		}
		
		if (user.isPremium()) {
			countRepeats = user.isCountRepeats();
			ignoreCommonWords = user.isIgnoreCommonWords();
			maxWordsToConsider = String.valueOf(user.getMaxWordsToConsider());
			headlinesWeight = String.valueOf(user.getHeadlinesWeight());
			weightLinks = user.isWeightLinks();
			returnAnalyzedText = user.isReturnAnalyzedText();
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
	}
	
	public User	extract (UserService userService) throws SQLException {
		
		User	user = userService.findUser(userId);
		if (user == null)
			user = new User();
		user.setEmailAddr(emailAddr);
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
		
		user.setObfuscateFeed(obfuscateFeed);
		user.setConsiderAds(considerAds);
		user.setConsiderPopups(considerPopups);
		user.setRatingsPublic(ratingsPublic);
		user.setName(name);
		user.setPassword(password);
		user.setUserId(userId);
		user.setDescription(description);
		user.getCategorySet().setRawCategories(ratingCategories);
		user.getCategorySet().setOrdered(ratingsOrderable);
		user.getCategorySet().setName(ratingsSetName);
		user.setDesirableRatings(desirableRatings);
		
		if (privacy != null) {
			if (privacy.equals("public"))
				user.setPrivacy(Privacy.PUBLIC);
			else if (privacy.equals("anon"))
				user.setPrivacy(Privacy.ANON);
			else if (privacy.equals("private"))
				user.setPrivacy(Privacy.PRIVATE);	
		}
		if (user.isPremium()) {
			updatePremiumParams(user);
		}
		return (user);
	}
	
	public ActionErrors	validate (ActionMapping mapping, HttpServletRequest request) {
		ActionErrors	errors = super.validate(mapping, request);
	
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
		
		if (errors.size() > 0) 
			request.setAttribute("doNotBind", "true");
		return (errors);
	}
	
	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public String getUserId() {
		return userId;
	}

	/**
	 * @struts.validator type="required"
	 * @struts.validator-args arg0resource="userId"
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmailAddr() {
		return emailAddr;
	}
	
	/**
	 * @struts.validator type="required,email"
	 * @struts.validator-args arg0resource="email"
	 */
	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}
	
	public String getPassword() {
		return password;
	}
	
	/**
	 * @struts.validator type="required"
	 * @struts.validator-args arg0resource="password"
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMaxRating() {
		return maxRating;
	}
	
	public void setMaxRating(String maxRating) {
		this.maxRating = maxRating;
	}
	
}
