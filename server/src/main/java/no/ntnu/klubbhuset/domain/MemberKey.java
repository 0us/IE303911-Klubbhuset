package no.ntnu.klubbhuset.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class MemberKey implements Serializable {

    @Column(name = "uid")
    Long uid;

    @Column(name = "oid")
    Long oid;

    @Column(name = "gid")
    Long gid;
}
