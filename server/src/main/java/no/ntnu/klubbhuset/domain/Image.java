package no.ntnu.klubbhuset.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import java.util.Date;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Image {

    @JsonbTransient
    @GeneratedValue
    @Id
    private Long iid;

    @JsonbTransient
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date created;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

    private String url;

    @JsonbTransient
    @OneToOne(mappedBy = "avatar")
    private User user;

    @JsonbTransient
    @ManyToMany(mappedBy = "orgImages")
    Set<Organization> organizations;
}
