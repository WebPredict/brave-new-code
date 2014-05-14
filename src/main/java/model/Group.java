package wp.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="WebGroup")
public class Group implements Serializable {

	private Long	id;
	private String	groupId;
	private String	name;
	private String	ownerId;
	private Collection<User>	members = new HashSet<User>();
	private String	description;
	private Date	creationTime = new Date();
	
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

	@Column(length=2048)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column
	public Date getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	
	@Column
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	@Column
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	
	@ManyToMany(
	        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
	        mappedBy = "groups"
	    )
	public Collection<User> getMembers() {
		return members;
	}
	public void setMembers(Collection<User> members) {
		this.members = members;
	}
	
}
