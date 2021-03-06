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
import javax.persistence.NonUniqueResultException;
import javax.persistence.Parameter;
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
     * @param organizationId The org id
     * @return response
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
        System.out.println("user = " + user);
        Organization organization = entityManager.find(Organization.class, organizationId);
        System.out.println("organization = " + organization);
        Group adminRole = getAdminRole();
        System.out.println("role = " + adminRole);

        Member member;
        TypedQuery<Member> query = entityManager
                .createQuery("select m from Member m " +
                                "where m.user = :user " +
                                "and m.organization = :organization " +
                                "and m.group = :role",
                        Member.class)
                .setParameter("user", user)
                .setParameter("organization", organization)
                .setParameter("role", adminRole);

        System.out.println(query);
        for( Parameter param : query.getParameters()) {
            System.out.println(param.getName() + ": " + query.getParameterValue(param));
        }

        try {
            member = query.getSingleResult(); // Throws error if no member is found
            System.out.println("member = " + member);
        } catch (NoResultException e) {
            System.out.println("No member found");
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
        System.out.println("AdminOrganizationService.hasMemberPaid");

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
            System.out.println(e.getMessage());
            return Response.status(Response.Status.NO_CONTENT.getStatusCode()).entity("Is not a member").build();
        } catch (NonUniqueResultException nure) {
            System.out.println(user.getEmail() + " Should not have two memberships in the same org! Bad DBA!");
            return Response.status(Response.Status.FORBIDDEN.getStatusCode()).entity("Duplicate entries, please contact system administrator").build();
        }


        if (!member.hasPaid() ) {
            return Response.status(Response.Status.PAYMENT_REQUIRED).entity("Has NOT paid").build();
        }

        return Response.ok().entity("Has paid").build();
    }
}
