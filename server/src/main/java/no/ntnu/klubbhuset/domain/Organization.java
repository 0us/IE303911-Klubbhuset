package no.ntnu.klubbhuset.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;


@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Organization {

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

    @OneToMany(mappedBy = "organization")
    Set<Member> members;

    @ManyToMany
    @JoinTable(
            name = "orgImages",
            joinColumns = @JoinColumn(name = "oid"),
            inverseJoinColumns = @JoinColumn(name = "iid"))
    Set<Image> orgImages;
}
