package no.ntnu.klubbhuset.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;

import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnore;

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
        return "";
    }
}
