package no.ntnu.klubbhuset.resource;

import no.ntnu.klubbhuset.domain.Order;
import no.ntnu.klubbhuset.service.VippsApiService;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("vipps/v2/payments/{orderId}")
public class VippsApiResource {
    @PathParam("orderId")
    private String orderId;

    @Inject
    VippsApiService vippsApiService;

    @POST
    public void vippsResponse(Order order) {
        vippsApiService.vippsResponse(order);

    }

}
