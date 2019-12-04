package no.ntnu.klubbhuset.domain;

import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@EqualsAndHashCode
@Embeddable
public class MemberKey implements Serializable {

    @Column(name = "email")
    String email;

    @Column(name = "oid")
    Long oid;

    @Column(name = "gid")
    Long gid;
}
