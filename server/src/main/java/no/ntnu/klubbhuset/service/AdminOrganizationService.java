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
import javax.persistence.Query;
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
        Group role = entityManager.find(Group.class, 52L); // magic number is the current gid of admin

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

    public Response harMemberPaid(Long organizationId, User user) {
        Member member;
        Organization organization = entityManager.find(Organization.class, organizationId);
        Query query = entityManager.createQuery("SELECT m from Member m " +
                "where User = :user and Organization = :organization ", Member.class);
        query.setParameter("user", user);
        query.setParameter("organization", organization);
        try {
            member = (Member) query.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return Response.status(Response.Status.fromStatusCode(418)).entity("Is not a member").build();
        }

        if ( !member.isHasPaid() ) {
            return Response.status(Response.Status.fromStatusCode(418)).entity("Is not a member").build();
        }

        return Response.ok().entity("Has paid").build();
    }
}
