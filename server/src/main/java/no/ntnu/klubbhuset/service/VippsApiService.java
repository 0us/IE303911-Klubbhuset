package no.ntnu.klubbhuset.service;

import no.ntnu.klubbhuset.domain.Order;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class VippsApiService {

    @PersistenceContext
    EntityManager entityManager;


    public void vippsResponse(Order order) {
       entityManager.persist(order);
        System.out.println("VippsApiService.vippsResponse");
        System.out.println("order = " + order);
    }
}
