package no.ntnu.klubbhuset.domain;

import java.io.Serializable;
import java.util.*;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;

import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnore;


@Entity
@Table(name = "AUSER")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    public void addSecurityGroup(SecurityGroup securityGroup) {
        if(securityGroups == null) {
            securityGroups = new ArrayList<>();
        }
        securityGroups.add(securityGroup);
    }

    public enum State {
        ACTIVE, INACTIVE
    }

    @Id
    @GeneratedValue
    private Long uid;

    @JsonIgnore
    private String password;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date joined;

    @PrePersist
    protected void onCreate() {
        joined = new Date();
    }

    @Enumerated(EnumType.STRING)
    private State currentState = State.ACTIVE;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    Set<Member> members;

    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    String phonenumber;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "iid", referencedColumnName = "iid")
    private Image avatar;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "auser_properties", joinColumns = @JoinColumn(name = "uid"))
    @MapKeyColumn(name = "key_column")
    @Column(name = "value_column")
    private Map<String, String> properties = new HashMap<String, String>();

    @ManyToMany
    @JoinTable(name="USERSECURITYROLES",
            joinColumns = @JoinColumn(name="uid", referencedColumnName = "uid"),
            inverseJoinColumns = @JoinColumn(name="name",referencedColumnName = "name"))
    List<SecurityGroup> securityGroups;

    @Override
    public String toString() {
        return "";
    }
}
