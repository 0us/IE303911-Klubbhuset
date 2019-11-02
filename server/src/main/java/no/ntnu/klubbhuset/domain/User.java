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
    private String email;
    String phonenumber;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "iid", referencedColumnName = "uid")
    private Image avatar;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "auser_properties", joinColumns = @JoinColumn(name = "uid"))
    @MapKeyColumn(name = "key_column")
    @Column(name = "value_column")
    private Map<String, String> properties = new HashMap<String, String>();

    @ManyToMany
    @JoinTable(name="AUSERGROUP",
            joinColumns = @JoinColumn(name="uid", referencedColumnName = "uid"),
            inverseJoinColumns = @JoinColumn(name="name",referencedColumnName = "name"))
    List<Group> groups;

    @Override
    public String toString() {
        return "";
    }
}
