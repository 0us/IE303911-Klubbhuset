package no.ntnu.klubbhuset.service;

import no.ntnu.klubbhuset.domain.Group;
import no.ntnu.klubbhuset.domain.Member;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

@Stateless
//@RolesAllowed({Group.ADMIN})
public class AdminOrganizationService {

    @PersistenceContext
    EntityManager entityManager;

    public Response getAllMembers(Long organizationId) {
        List members = entityManager.createQuery("select m from Member m where oid = :oid").setParameter("oid", organizationId).getResultList();

        if (members.isEmpty()) {
            return Response.noContent().entity("There are no members in the organization").build();
        }

        return Response.ok(members).build();
    }
}
