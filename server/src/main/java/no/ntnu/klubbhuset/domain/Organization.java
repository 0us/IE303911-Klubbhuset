package no.ntnu.klubbhuset.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Column(length = 1024)
    private String description;

    @JsonbTransient
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
        try {
            String template = "name: %s, emailcontact: %s and priceofmembership: %s";
            return String.format(template, getName(), getEmailContact(), getPriceOfMembership());
        } catch (NullPointerException ne) {
            return "Organization has not been fully initialized";
        }
    }
}
