package no.ntnu.klubbhuset.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "VIPPSORDERS")

public class Order {
    @Id
    String id;
    String url;

}
