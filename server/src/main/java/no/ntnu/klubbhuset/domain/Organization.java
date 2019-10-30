package no.ntnu.klubbhuset.domain;

import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;


@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Organization implements Serializable {

    @GeneratedValue
    @Id
    private Long oid;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date created;

    private String name;
    private String url;
    private String emailContact;
    private BigDecimal priceOfMembership;
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "organization")
    Set<Member> members;

    @ManyToMany
    @JoinTable(
            name = "orgImages",
            joinColumns = @JoinColumn(name = "oid"),
            inverseJoinColumns = @JoinColumn(name = "iid"))
    Set<Image> orgImages;

    @Override
    public String toString() {
        return "";
    }
}
