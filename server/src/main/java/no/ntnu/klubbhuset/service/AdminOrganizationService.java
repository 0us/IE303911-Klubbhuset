package no.ntnu.klubbhuset.service;

import no.ntnu.klubbhuset.domain.Group;
import no.ntnu.klubbhuset.domain.Member;
import no.ntnu.klubbhuset.domain.Organization;
import no.ntnu.klubbhuset.domain.SecurityGroup;
import no.ntnu.klubbhuset.domain.User;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Response;
import java.util.Set;

@Stateless
@RolesAllowed({SecurityGroup.USER})
public class AdminOrganizationService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    JsonWebToken principal;

    /**
     * Getting all members of an organization
     *
     * @param organizationId
     * @return
     */
    public Response getAllMembers(Long organizationId) {
        Organization organization = entityManager.find(Organization.class, organizationId);
        Set<Member> members = organization.getMembers();

        if ( members.isEmpty() ) {
            return Response.noContent().entity("There are no members in the organization").build();
        }

        return Response.ok(members).build();
    }


    public boolean isAdminOfOrganization(Long organizationId) {
        boolean result = false;

        User user = entityManager.find(User.class, principal.getName());
        Organization organization = entityManager.find(Organization.class, organizationId);
        Group role = getAdminRole();

        Member member;
        TypedQuery<Member> query = entityManager
                .createQuery("select m from Member m " +
                                "where m.user = :user " +
                                "and m.organization = :organization " +
                                "and m.group = :role",
                        Member.class)
                .setParameter("user", user)
                .setParameter("organization", organization)
                .setParameter("role", role);

        try {
            member = query.getSingleResult(); // Throws error if no member is found
        } catch (NoResultException e) {
            return false;
        }

        if ( member != null ) { // might be redundant
            result = true;
        }

        return result;
    }

    private Group getAdminRole() {
        try {
            TypedQuery<Group> query = entityManager.createQuery("select g from Group g where g.name = :name", Group.class);
            query.setParameter("name", "admin");
            return query.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Response hasMemberPaid(Long organizationId, User user) {
        System.out.println("AdminOrganizationService.harMemberPaid");
        Member member;
        Organization organization = entityManager.find(Organization.class, organizationId);
        TypedQuery<Member> query = entityManager.createQuery("SELECT m from Member m "
                        + "where m.organization = :organization "
                        + "and m.user = :user"
                , Member.class
        );
        query.setParameter("user", user);
        query.setParameter("organization", organization);
        try {
            member = query.getSingleResult();
        } catch (NoResultException e) {
            e.getMessage();
            return Response.status(Response.Status.NO_CONTENT).entity("Is not a member").build();
        }

        if ( !member.hasPaid() ) {
            return Response.status(Response.Status.PAYMENT_REQUIRED).entity("Has NOT paid").build();
        }

        return Response.ok().entity("Has paid").build();
    }
}
