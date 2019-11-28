package no.ntnu.klubbhuset.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "ORG_GROUP")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@EqualsAndHashCode(exclude = "users")
public class Group implements Serializable {
    @JsonbTransient
    public static final String USER = "user";
    @JsonbTransient
    public static final String ADMIN = "admin";
    @JsonbTransient
    public static final String[] GROUPS = {USER, ADMIN};

    @Id
    @GeneratedValue
    @JsonIgnore
    @JsonbTransient
    private Long gid;

    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "group")
    @JsonbTransient
    Set<Member> members;

    public Group(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        if(name == null) {
            name = "no name";
        }
        return "Group: gid: " + getGid() + " name: " + name;
    }
}
