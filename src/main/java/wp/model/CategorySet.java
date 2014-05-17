package wp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.hibernate.annotations.CollectionOfElements;

@Entity
public class CategorySet implements Serializable {

	private List<String> categories = new ArrayList<String>();
	private String	name;
	private Long	id;
	private boolean	ordered;
	private Collection<User>	users = new HashSet<User>();
	private Collection<UserConfig>	userConfigs = new HashSet<UserConfig>();
	private boolean	numeric;
	
	public CategorySet () {
		
	}
	
	public CategorySet (User user, String rawCategories, String name) {
		users.add(user);
		setRawCategories(rawCategories);
		this.name = name;
	}

	public CategorySet (UserConfig user, String rawCategories, String name) {
		userConfigs.add(user);
		setRawCategories(rawCategories);
		this.name = name;
	}

	@Transient
	public String	getRawCategories () {
		StringBuffer	buf = new StringBuffer();
		for (String cat : categories) {
			if (buf.length() > 0)
				buf.append(", ");
			buf.append(cat);
		}
		return (buf.toString());
	}
	
	public void setRawCategories(String ratingCategories) {
		categories.clear();
		if (ratingCategories != null) {
			StringTokenizer	tok = new StringTokenizer(ratingCategories, ",");
			numeric = true;
			while (tok.hasMoreTokens()) {
				String	token = tok.nextToken().trim();
				categories.add(token);
				
				try {
					int	val = Integer.parseInt(token);
				}
				catch (NumberFormatException e) {
					numeric = false;
				}
			}
			
			if (numeric) {
				Collections.sort(categories, new Comparator () {

					public int compare(Object arg0, Object arg1) {
						String	r0 = (String)arg0;
						String	r1 = (String)arg1;
						
						return (Integer.parseInt(r0) - Integer.parseInt(r1));
					}
					
				});
			}
			this.ordered = numeric;
		}
	}
	
	@Transient
	public boolean	isNumeric () {
		return (numeric);
	}
	
	@CollectionOfElements 
	public List<String> getCategories() {
		return categories;
	}
	
	public void setCategories(List<String> categories) {
		this.categories = categories;
		
		if (this.categories != null && this.categories.size() > 0) {
			this.numeric = true;
			this.ordered = true;
			
			for (String s : categories) {
				try {
					Integer.parseInt(s);
				}
				catch (Exception e) {
					this.numeric = false;
					this.ordered = false;
					break;
				}
			}
		}
	}
	
	public boolean	contains (String category) {
		return (categories == null ? false : categories.contains(category));
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@OneToMany(mappedBy="categorySet")
	@OrderBy("id")
	public Collection<User> getUsers() {
		return users;
	}
	
	public void	setUsers (Collection<User> users) {
		this.users = users;
	}

	@OneToMany(mappedBy="categorySet")
	@OrderBy("id")
	public Collection<UserConfig> getUserConfigs() {
		return userConfigs;
	}
	
	public void	setUserConfigs (Collection<UserConfig> userConfigs) {
		this.userConfigs = userConfigs;
	}
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isOrdered() {
		return ordered;
	}
	public void setOrdered(boolean ordered) {
		this.ordered = ordered;
	}
	
}
