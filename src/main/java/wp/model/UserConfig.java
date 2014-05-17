package wp.model;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CollectionOfElements;

@Entity
@Table(name="UserConfig")
public class UserConfig implements Serializable {

	public static final String []	ALL_FEATURES = new String [] {"Home", "Controls", "My Account", "Stats", 
		"History", "Recent Ratings", "Recommend", "Find Similar Users", "Alerts", "Page Diffs", "Groups"};

	private Long	id;
	private String	name;
	private String	description;
	private List<String>	features = new ArrayList<String>();
	private Map<String, String>	featuresMap = new HashMap<String, String>();
	private Integer	maxRating;
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
	private boolean	smoothProbs = true;
	private boolean	useCache = true;
	private boolean	usePreciseWordProb = false;
	private boolean	genericizeNumbers = false;
	private boolean ignoreCapitalization = false;
	private boolean	useStrictParser = false;
	private boolean	considerNumLinks = false;
	private boolean	considerNumImages = false;
	private int 	phraseLength = 1;
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(length=1024)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column
	public Integer getMaxRating() {
		return maxRating;
	}
	public void setMaxRating(Integer maxRating) {
		this.maxRating = maxRating;
	}
	
	@Column
	public boolean isConsiderPopups() {
		return considerPopups;
	}
	public void setConsiderPopups(boolean considerPopups) {
		this.considerPopups = considerPopups;
	}
	
	@Transient
	public String	getUrl () {
		try {
			return ("http://www.webpredict.net/wp/PreSignup.do?userConfigId=" + URLEncoder.encode(name, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ("http://www.webpredict.net/wp/PreSignup.do");
	}
	
	@Column
	public boolean isConsiderAds() {
		return considerAds;
	}
	public void setConsiderAds(boolean considerAds) {
		this.considerAds = considerAds;
	}
	
	@CollectionOfElements 
	public List<String> getFeatures() {
		return features;
	}
	
	public void setFeatures(List<String> features) {
		this.features = features;
		this.featuresMap.clear();
		if (features != null)
			for (String feature : features)
				featuresMap.put(feature, feature);
	}
	
	@Transient
	public Map<String, String>	getFeaturesMap () {
		return (featuresMap);
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

}
