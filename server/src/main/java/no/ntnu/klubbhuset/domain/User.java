package no.ntnu.klubbhuset.domain;

import java.io.Serializable;
import java.util.*;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "AUSER")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    public enum State {
        ACTIVE, INACTIVE
    }

    @Id
    private String uid;

    @JsonbTransient
    private String password;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date joined;

    @PrePersist
    protected void onCreate() {
        joined = new Date();
    }

    @Enumerated(EnumType.STRING)
    private State currentState = State.ACTIVE;

    @OneToMany(mappedBy = "user")
    Set<Member> members;

    private String firstName;
    private String lastName;
    private String email;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "iid", referencedColumnName = "uid")
    private Image avatar;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "auser_properties", joinColumns = @JoinColumn(name = "uid"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> properties = new HashMap<String, String>();


        @ManyToMany
    @JoinTable(name="AUSERGROUP",
            joinColumns = @JoinColumn(name="userid", referencedColumnName = "userid"),
            inverseJoinColumns = @JoinColumn(name="name",referencedColumnName = "name"))
    private List<Group> groups;

    public List<Group> getGroups() {
        if(groups == null) {
            groups = new ArrayList<>();
        }
        return groups;
    }
}
