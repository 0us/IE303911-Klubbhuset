package no.ntnu.klubbhuset.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Entity
@Table(name = "AUSER")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    public void addSecurityGroup(SecurityGroup securityGroup) {
        if ( securityGroups == null ) {
            securityGroups = new ArrayList<>();
        }
        securityGroups.add(securityGroup);
    }

    public enum State {
        ACTIVE, INACTIVE
    }

    @Column(unique = true)
    @Id
    private String email;

    private String password;

    @JsonbTransient
    public String getPassword() {
        return password;
    }

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date joined;

    @PrePersist
    protected void onCreate() {
        joined = new Date();
    }

    @JsonbTransient
    @Enumerated(EnumType.STRING)
    private State currentState = State.ACTIVE;

    @JsonIgnore
    @JsonbTransient
    @OneToMany(mappedBy = "user")
    Set<Member> members;

    private String firstName;
    private String lastName;
    String phonenumber;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "iid", referencedColumnName = "iid")
    private Image avatar;

    @JsonbTransient
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "auser_properties", joinColumns = @JoinColumn(name = "email"))
    @MapKeyColumn(name = "key_column")
    @Column(name = "value_column")
    private Map<String, String> properties = new HashMap<String, String>();

    @JsonbTransient
    @ManyToMany
    @JoinTable(name = "USERSECURITYROLES",
            joinColumns = @JoinColumn(name = "email", referencedColumnName = "email"),
            inverseJoinColumns = @JoinColumn(name = "name", referencedColumnName = "name"))
    List<SecurityGroup> securityGroups;

    @Override
    public String toString() {
        String template = "email: %s, firstname: %s and lastname: %s";
        return String.format(template, getEmail(), getFirstName(), getLastName());
    }


}
