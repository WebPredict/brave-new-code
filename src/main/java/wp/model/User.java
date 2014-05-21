package wp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.tool.hbm2x.StringUtils;

import wp.utils.Utils;

@Entity
@Table(name="WebUser")
public class User implements Serializable {

	public static final int	PREMIUM_LIMIT_PRED = 1000;
	public static final int	PREMIUM_LIMIT_RATE = 1000;
	public static final int	DEFAULT_LIMIT_PRED = 200;
	public static final int	DEFAULT_LIMIT_RATE = 100;	
	
	private Long	id;
	private String	userId;
	private String	name;
	private Integer	maxRating;
	private String	emailAddr;
	private String	password;
	private boolean	premium;
	private boolean admin;
	private boolean	ratingsPublic = true;
	private Privacy	privacy = Privacy.PRIVATE;
	private String	description;
	private boolean	disabled = false;
	private Collection<Friend>	friendsList = new HashSet<Friend>();
	private List<UserMessage>	msgList = new ArrayList<UserMessage>();
	private List<Photo>		photoList = new ArrayList<Photo>();
	private Collection<Group>	groupList = new HashSet<Group>();
	private UserStats	userStats;
	private Collection<String>	desirableRatingsSet = new HashSet<String>();
	private String	desirableRatings;
	private CategorySet	categorySet;
	private boolean considerPopups = true;
	private boolean considerAds = true;
	private boolean	countRepeats = true;
	private boolean	ignoreCommonWords = true;
	private int		maxWordsToConsider = 1000;
	private double	headlinesWeight = 3;
	private boolean	weightLinks = true;
	private boolean	returnAnalyzedText = false;
	private boolean	smoothProbs = true;
	private boolean	useCache = true;
	private boolean	usePreciseWordProb = false;
	private boolean	genericizeNumbers = false;
	private boolean ignoreCapitalization = false;
	private boolean	useStrictParser = false;
	private boolean	considerNumLinks = false;
	private boolean	considerNumImages = false;
	private boolean	obfuscateFeed = false;
	private int 	phraseLength = 1;
	private List<Alert>	alertsList = new ArrayList<Alert>();
	private List<AlertResult>	alertResultsList = new ArrayList<AlertResult>();
	private List<Recommendation>	recommendationsList = new ArrayList<Recommendation>();
	private UserConfig	userConfig;
	private String	inheritedFrom;
	private String	code;
	private List<CrawlerImpl>	crawlersList = new ArrayList<CrawlerImpl>();
	private List<Feed>			feedsList = new ArrayList<Feed>();
	private List<WebStat>		webStats = new ArrayList<WebStat>();
	
	@OneToMany(mappedBy="user")
	public List<WebStat> getWebStats() {
		return webStats;
	}

	public void setWebStats(List<WebStat> webStats) {
		this.webStats = webStats;
	}

	@OneToMany(mappedBy="user")
	public List<Feed> getFeedsList() {
		return feedsList;
	}

	public void setFeedsList(List<Feed> feedsList) {
		this.feedsList = feedsList;
	}

	@Column
	public String getCode() {
		if (code == null) {
			if (userId != null) {
				if (emailAddr != null)
					code = String.valueOf(userId.hashCode()) + String.valueOf(emailAddr.hashCode());
				else
					code = String.valueOf(userId.hashCode());
			}
		}
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Transient
	public boolean	getHasFeature (String feature) {
		if (userConfig == null)
			return (true);
		return (userConfig.getFeatures().contains(feature));
	}
	
	@ManyToOne
	public UserConfig getUserConfig() {
		return userConfig;
	}
	public void setUserConfig(UserConfig userConfig) {
		this.userConfig = userConfig;
	}
	
	@Transient	
	public Map<String, String>	getFeaturesMap () {
		return (userConfig == null ? null : userConfig.getFeaturesMap());
	}
	
	@Column
	public String getInheritedFrom() {
		return inheritedFrom;
	}
	public void setInheritedFrom(String inheritedFrom) {
		this.inheritedFrom = inheritedFrom;
	}
	
	@Column
	public Privacy getPrivacy() {
		return privacy;
	}

	public void setPrivacy(Privacy privacy) {
		this.privacy = privacy;
	}

	@Column
	public int getPhraseLength() {
		return phraseLength;
	}
	public void setPhraseLength(int phraseLength) {
		this.phraseLength = phraseLength;
	}
	
	@Column
	public boolean isUseStrictParser() {
		return useStrictParser;
	}
	public void setUseStrictParser(boolean useStrictParser) {
		this.useStrictParser = useStrictParser;
	}
	
	public boolean isObfuscateFeed() {
		return obfuscateFeed;
	}

	public void setObfuscateFeed(boolean obfuscateFeed) {
		this.obfuscateFeed = obfuscateFeed;
	}

	@Column
	public boolean isConsiderNumLinks() {
		return considerNumLinks;
	}
	public void setConsiderNumLinks(boolean considerNumLinks) {
		this.considerNumLinks = considerNumLinks;
	}
	
	@Column
	public boolean isConsiderNumImages() {
		return considerNumImages;
	}
	public void setConsiderNumImages(boolean considerNumImages) {
		this.considerNumImages = considerNumImages;
	}
	
	@Column
	public boolean isIgnoreCapitalization() {
		return ignoreCapitalization;
	}
	public void setIgnoreCapitalization(boolean ignoreCapitalization) {
		this.ignoreCapitalization = ignoreCapitalization;
	}
	@Column
	public boolean isGenericizeNumbers() {
		return genericizeNumbers;
	}
	public void setGenericizeNumbers(boolean genericizeNumbers) {
		this.genericizeNumbers = genericizeNumbers;
	}
	
	@Column
	public boolean isUsePreciseWordProb() {
		return usePreciseWordProb;
	}
	public void setUsePreciseWordProb(boolean usePreciseWordProb) {
		this.usePreciseWordProb = usePreciseWordProb;
	}
	
	public void	initWithUserConfig (UserConfig config) {
		this.userConfig = config;
		if (config != null) {
			this.useCache = config.isUseCache();
			this.usePreciseWordProb = config.isUsePreciseWordProb();
			this.useStrictParser = config.isUseStrictParser();
			this.considerAds = config.isConsiderAds();
			this.considerPopups = config.isConsiderPopups();
			this.considerNumImages = config.isConsiderNumImages();
			this.considerNumLinks = config.isConsiderNumLinks();
			this.genericizeNumbers = config.isGenericizeNumbers();
			setDesirableRatings(config.getDesirableRatings());
			this.smoothProbs = config.isSmoothProbs();
			this.ignoreCapitalization = config.isIgnoreCapitalization();
			this.countRepeats = config.isCountRepeats();
			this.maxRating = config.getMaxRating();
			this.maxWordsToConsider = config.getMaxWordsToConsider();
			this.weightLinks = config.isWeightLinks();
			this.ignoreCommonWords = config.isIgnoreCommonWords();
			this.phraseLength = config.getPhraseLength();
			this.headlinesWeight = config.getHeadlinesWeight();
			
			CategorySet	set = this.getCategorySet();
			set.setRawCategories(config.getCategorySet().getRawCategories());
			set.setName(config.getCategorySet().getName());
		}
	}

	@Column
	public boolean isUseCache() {
		return useCache;
	}
	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}
	
	@Column
	public boolean isSmoothProbs() {
		return smoothProbs;
	}
	public void setSmoothProbs(boolean smoothProbs) {
		this.smoothProbs = smoothProbs;
	}
	
	@Column
	public boolean isReturnAnalyzedText() {
		return returnAnalyzedText;
	}
	public void setReturnAnalyzedText(boolean returnAnalyzedText) {
		this.returnAnalyzedText = returnAnalyzedText;
	}
	
	@Column
	public boolean isCountRepeats() {
		return countRepeats;
	}
	public void setCountRepeats(boolean countRepeats) {
		this.countRepeats = countRepeats;
	}
	
	@Column
	public boolean isIgnoreCommonWords() {
		return ignoreCommonWords;
	}
	public void setIgnoreCommonWords(boolean ignoreCommonWords) {
		this.ignoreCommonWords = ignoreCommonWords;
	}
	
	@Column
	public int getMaxWordsToConsider() {
		return maxWordsToConsider;
	}
	public void setMaxWordsToConsider(int maxWordsToConsider) {
		this.maxWordsToConsider = maxWordsToConsider;
	}
	
	@Column
	public double getHeadlinesWeight() {
		return headlinesWeight;
	}
	public void setHeadlinesWeight(double headlinesWeight) {
		this.headlinesWeight = headlinesWeight;
	}

	@Column
	public boolean isWeightLinks() {
		return weightLinks;
	}
	public void setWeightLinks(boolean weightLinks) {
		this.weightLinks = weightLinks;
	}
	
	public void setUserStats(UserStats userStats) {
		this.userStats = userStats;
	}

	@Column(length=1024)
	public String getDesirableRatings() {
		return desirableRatings;
	}

	public void setDesirableRatings(String desirableRatings) {
		this.desirableRatings = desirableRatings;
		
		desirableRatingsSet.clear();
		if (desirableRatings != null) {
			StringTokenizer	tok = new StringTokenizer(desirableRatings, ",");
			while (tok.hasMoreTokens())
				desirableRatingsSet.add(tok.nextToken().trim());
		}
	}

	@ManyToOne
	public CategorySet	getCategorySet() {
		return categorySet;
	}

	public void setCategorySet(CategorySet catSet) {
		this.categorySet = catSet;
		if (catSet != null && catSet.isNumeric() && maxRating == null) {
			List<String>	cats = catSet.getCategories();
			if (cats != null && cats.size() > 0) {
				maxRating = Integer.parseInt(cats.get(cats.size() - 1));
			}
		}
	}
	
	@ManyToMany(
        cascade={CascadeType.PERSIST, CascadeType.MERGE}
    )
	public Collection<Group> getGroups() {
		return groupList;
	}

	public void setGroups(Collection<Group> groupList) {
		this.groupList = groupList;
	}

	@OneToMany(mappedBy="user")
	@OrderBy("id")
	public Collection<Friend> getFriends() {
		return friendsList;
	}

	@Transient
	public Collection<String>	getDesirableRatingsSet () {
		return (desirableRatingsSet);
	}
	
	@Transient
	public boolean	isFriendsWith (String userId) {		
		if (friendsList != null) {
			for (Friend f : friendsList) {
				if (f.getFriendId().equals(userId))
					return (true);
			}
		}
		return (false);
	}
	
	@Transient
	public Friend	getFriend (String userId) {
		if (friendsList != null) {
			for (Friend f : friendsList) {
				if (f.getFriendId().equals(userId))
					return (f);
			}
		}
		return (null);
	}
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setFriends(Collection<Friend> friendsList) {
		this.friendsList = friendsList;
	}

	@OneToMany(mappedBy="user")
    @OrderBy("date")
	public List<UserMessage> getMsgList() {
		return msgList;
	}

	public void setMsgList(List<UserMessage> msgList) {
		this.msgList = msgList;
	}

	@OneToMany(mappedBy="user")
	@OrderBy("stamp desc")
	public List<Photo> getPhotoList() {
		return photoList;
	}

	public void setPhotoList(List<Photo> photoList) {
		this.photoList = photoList;
	}

	@OneToMany(mappedBy="user")
	public List<Recommendation> getRecommendationsList() {
		return recommendationsList;
	}

	@Transient
	public Recommendation	findRecommendationByUrl (String urlStr) {
		if (recommendationsList == null)
			return (null);
		for (Recommendation rec : recommendationsList)
			if (rec.getUrlStr().equals(urlStr))
				return (rec);
		
		return (null);
	}

	@Transient
	public AlertResult	findAlertResult (String urlStr, Collection<String> predictions, String contains) {
		List<AlertResult>	resList = getAlertResultsList();
		
		if (resList == null)
			return (null);
		urlStr = Utils.normalizeUrlStr(urlStr);
		
		for (AlertResult res : resList) {
			if (res.getUrlStr().equals(urlStr)) {
				
				if (predictions != null && res.getPrediction() != null && predictions.contains(res.getPrediction()))
					return (res);
				String	lowerResContains = res.getContains() == null ? null : res.getContains().toLowerCase();
				if (StringUtils.isNotEmpty(contains) && Utils.xequals(lowerResContains, contains.toLowerCase()))
					return (res);
			}
		}
		
		return (null);
	}

	@Transient
	public List<Recommendation>	getVisibleRecommendationsList() {
		List<Recommendation>	recs = getRecommendationsList();
		if (recs == null)
			return (null);
		ArrayList<Recommendation>	filtered = new ArrayList<Recommendation>();
		for (Recommendation rec : recs)
			if (!rec.isDontShow())
				filtered.add(rec);
		return (filtered);
	}
	
	public void setRecommendationsList(List<Recommendation> recommendationsList) {
		this.recommendationsList = recommendationsList;
	}

	@OneToMany(mappedBy="user")
	public List<Alert> getAlertsList() {
		return alertsList;
	}

	public void setAlertsList(List<Alert> alertsList) {
		this.alertsList = alertsList;
	}

	@OneToMany(mappedBy="user")
	public List<CrawlerImpl> getCrawlersList() {
		return crawlersList;
	}

	public void setCrawlersList(List<CrawlerImpl> crawlersList) {
		this.crawlersList = crawlersList;
	}

	@OneToMany(mappedBy="user")
	public List<AlertResult> getAlertResultsList() {
		return alertResultsList;
	}

	public void setAlertResultsList(List<AlertResult> alertResultsList) {
		this.alertResultsList = alertResultsList;
	}

	@Column
	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	@Column(length=1024)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column
	public boolean isRatingsPublic() {
		return ratingsPublic;
	}

	public void setRatingsPublic(boolean ratingsPublic) {
		this.ratingsPublic = ratingsPublic;
	}

	@Column
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column
	public boolean isPremium() {
		return premium;
	}

	public void setPremium(boolean premium) {
		this.premium = premium;
	}

	@Column
	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	@Column
	public boolean isConsiderPopups() {
		return considerPopups;
	}

	public void setConsiderPopups(boolean considerPopups) {
		this.considerPopups = considerPopups;
	}

	@Column
	public boolean isConsiderAds() {
		return considerAds;
	}

	public void setConsiderAds(boolean considerAds) {
		this.considerAds = considerAds;
	}

	public User () {
		
	}

	@Column
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@Column
	public Integer getMaxRating() {
		return maxRating;
	}
	public void setMaxRating(Integer maxRating) {
		this.maxRating = maxRating;
	}
	 
	@Column
	public String getEmailAddr() {
		return emailAddr;
	}
	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}
	
	@Column
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Transient
	public String	getMainPhotoId () {
		return (photoList.size() == 0 ? null : photoList.get(0).getId().toString());
	}
	
	@Transient
	public int	getDailyPredictionQuota () {
		return (this.premium ? PREMIUM_LIMIT_PRED : DEFAULT_LIMIT_PRED);
	}
	
	@Transient
	public int	getDailyRatingQuota () {
		return (this.premium ? PREMIUM_LIMIT_RATE : DEFAULT_LIMIT_RATE);
	}
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="userStatsFK")
	public UserStats	getUserStats ()  {
		return (userStats);
	}
		
	public String	toString () {
		return (userId);
	}

}
