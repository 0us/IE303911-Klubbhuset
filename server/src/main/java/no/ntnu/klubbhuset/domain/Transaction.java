package no.ntnu.klubbhuset.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
public class Transaction {

    @Id
    String id;

    // Transaction info
    String timeStamp;
    String status;
    Double amount; // in NOK øre
    String text;

    @ManyToOne
    Order orderId;

    // Transaction summary
    int capturedAmount;
    int refundedAmount;
    int remainingAmountToCapture;
    int remainingAmountToRefund;

}
