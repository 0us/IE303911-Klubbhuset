package no.ntnu.klubbhuset.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity @Table(name = "AGROUP")
@Data @AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode(exclude="users")
public class Group implements Serializable {
    public static final String USER = "user";
    public static final String ADMIN = "admin";
    public static final String[] GROUPS = {USER, ADMIN};

    @Id
    private Long gid;

    private String name;

    private String project;

    @OneToMany(mappedBy = "group")
    Set<Member> members;

    @JsonbTransient
    @ManyToMany
    @JoinTable(name="AUSERGROUP",
            joinColumns = @JoinColumn(name="name", referencedColumnName = "name"),
            inverseJoinColumns = @JoinColumn(name="userid",referencedColumnName = "userid"))
    private List<User> users;

    public Group(String name) {
        this.name = name;
    }
}
