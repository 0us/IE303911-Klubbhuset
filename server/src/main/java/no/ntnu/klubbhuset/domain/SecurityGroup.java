package no.ntnu.klubbhuset.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "SECURITYROLES")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude="users")
public class SecurityGroup {
    public static final String USER = "user";
    public static final String ADMIN = "admin";
    public static final String[] GROUPS = {USER, ADMIN};

    @Id
    String name;

    String project;

    @JsonbTransient
    @ManyToMany
    @JoinTable(name="AUSERGROUP",
            joinColumns = @JoinColumn(name="name", referencedColumnName = "name"),
            inverseJoinColumns = @JoinColumn(name="uid",referencedColumnName = "uid"))
    List<User> users;

    public SecurityGroup(String name) {
        this.name = name;
    }
}
