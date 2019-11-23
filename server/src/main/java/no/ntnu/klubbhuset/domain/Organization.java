package no.ntnu.klubbhuset.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
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

    @JsonbTransient
    @OneToMany(mappedBy = "organization")
    Set<Member> members;

    @JsonbTransient
    @ManyToMany
    @JoinTable(
            name = "orgImages",
            joinColumns = @JoinColumn(name = "oid"),
            inverseJoinColumns = @JoinColumn(name = "iid"))
    Set<Image> orgImages;

    @Override
    public String toString() {
        String template = "name: %s, emailcontact: %s and priceofmembership: %s";
        return String.format(template, getName(), getEmailContact(), getPriceOfMembership());
    }
}
