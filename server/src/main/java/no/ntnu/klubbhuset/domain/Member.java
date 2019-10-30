package no.ntnu.klubbhuset.domain;

import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Member implements Serializable {

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

    @Override
    public String toString(){
        return null;
    }
}
