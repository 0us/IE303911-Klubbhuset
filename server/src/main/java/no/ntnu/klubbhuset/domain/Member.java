package no.ntnu.klubbhuset.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Member {

    @EmbeddedId
    MemberKey id;

    @ManyToOne
    @MapsId("uid")
    @JoinColumn(name = "uid")
    private User user;

    @ManyToOne
    @MapsId("oid")
    @JoinColumn(name = "oid")
    private Organization organization;

    @ManyToOne
    @MapsId("gid")
    @JoinColumn(name = "gid")
    private Group group;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date created;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

    private boolean hasPaid;
    private boolean needsToPay;
}
