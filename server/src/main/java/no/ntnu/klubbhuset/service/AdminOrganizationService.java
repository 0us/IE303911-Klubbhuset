package no.ntnu.klubbhuset.service;

import no.ntnu.klubbhuset.domain.Member;
import no.ntnu.klubbhuset.domain.Organization;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;
import java.util.Set;

@Stateless
//@RolesAllowed({Group.ADMIN})
public class AdminOrganizationService {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * Getting all members of an organization
     * @param organizationId
     * @return
     */
    public Response getAllMembers(Long organizationId) {
        Organization organization = entityManager.find(Organization.class, organizationId);
        Set<Member> members = organization.getMembers();

        if (members.isEmpty()) {
            return Response.noContent().entity("There are no members in the organization").build();
        }

        return Response.ok(members).build();
    }
}
