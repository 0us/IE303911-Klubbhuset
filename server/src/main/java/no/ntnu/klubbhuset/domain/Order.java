package no.ntnu.klubbhuset.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data

public class Order {
    @Id
    String id;
    String url;

}
