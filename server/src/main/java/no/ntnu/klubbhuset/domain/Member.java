package no.ntnu.klubbhuset.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.map.annotate.JsonFilter;

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
@JsonFilter("beanFilter")
public class Member implements Serializable {

    @EmbeddedId
    @JsonbTransient
    MemberKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("email")
    @JoinColumn(name = "email")
    private User user;

    @JsonbTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("oid")
    @JoinColumn(name = "oid")
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
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

    public boolean hasPaid() {
        return hasPaid;
    }

    @Override
    public String toString() {
        try {
            String template = "email: %s, organization: %s and hasPaid: %s";
            return String.format(template, getUser().getEmail(), getOrganization().getName(), hasPaid());
        } catch (NullPointerException ne) {
            return "member not initialized with User, Organization";
        }
    }
}
