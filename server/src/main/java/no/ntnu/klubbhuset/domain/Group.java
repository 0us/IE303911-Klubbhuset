package no.ntnu.klubbhuset.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;

import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@Table(name = "AGROUP")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@EqualsAndHashCode(exclude = "users")
public class Group implements Serializable {
    public static final String USER = "user";
    public static final String ADMIN = "admin";
    public static final String[] GROUPS = {USER, ADMIN};

    @Id
    @GeneratedValue
    private Long gid;

    private String name;

    private String project;

    @JsonIgnore
    @OneToMany(mappedBy = "group")
    Set<Member> members;

    public Group(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "";
    }
}
