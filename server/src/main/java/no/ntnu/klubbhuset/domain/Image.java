package no.ntnu.klubbhuset.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonValue;

import javax.json.bind.annotation.JsonbTransient;
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
