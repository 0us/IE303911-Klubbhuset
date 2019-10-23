package no.ntnu.randa.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Image {

    @GeneratedValue
    @Id
    private Long iid;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date created;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

    private String url;

    @OneToOne(mappedBy = "avatar")
    private User user;

    @ManyToMany(mappedBy = "orgImages")
    Set<Organization> organizations;
}
