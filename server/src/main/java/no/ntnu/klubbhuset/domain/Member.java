package no.ntnu.klubbhuset.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.map.annotate.JsonFilter;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonFilter("beanFilter")
public class Member implements Serializable {

    @EmbeddedId
    @JsonbTransient
    MemberKey id;

    @ManyToOne
    @MapsId("email")
    @JoinColumn(name = "email")
    @JsonbTransient
    private User user;

    @JsonbTransient
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
