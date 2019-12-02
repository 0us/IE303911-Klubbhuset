package no.ntnu.klubbhuset.service;

import no.ntnu.klubbhuset.DatasourceProducer;
import no.ntnu.klubbhuset.SaveImages;
import no.ntnu.klubbhuset.domain.Group;
import no.ntnu.klubbhuset.domain.Image;
import no.ntnu.klubbhuset.domain.Member;
import no.ntnu.klubbhuset.domain.Organization;
import no.ntnu.klubbhuset.domain.SecurityGroup;
import no.ntnu.klubbhuset.domain.User;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionRolledbackLocalException;
import javax.inject.Inject;
import javax.persistence.*;
import javax.naming.OperationNotSupportedException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Stateless
@RolesAllowed({SecurityGroup.USER})
public class OrganizationService {

    public static final String IMAGES = "images";
    public static final String IMAGE = "image";

    @Inject
    JsonWebToken principal;

    @Inject
    SaveImages saveImages;

    @PersistenceContext
    EntityManager entityManager;

    @Resource(lookup = DatasourceProducer.JNDI_NAME)
    DataSource dataSource;

    public Response getAllOrganizations() {
        System.out.println("Fetching all organizations");
        List<Organization> organizations = entityManager.createQuery("Select o From Organization o", Organization.class).getResultList();

        if (organizations.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No organizations registered").build();
        }
        System.out.println("organizations = " + organizations);

        return Response.ok(organizations).build();
    }


//    public Response createNewOrganization(String name, String price, String description, FormDataMultiPart multiPart) {
//        Organization organization = new Organization();
//        FormDataBodyPart imageBodyPart = multiPart.getField(IMAGE);
//
//        organization.setName(name);
//        organization.setDescription(description);
//        organization.setPriceOfMembership(BigDecimal.valueOf(Long.parseLong(price))); // todo go through during code review. a bit cumbersome but should work. Maybe change?
//        entityManager.persist(organization);
//
//        if ( imageBodyPart != null ) {
//            if (!saveImages.checkBodyPartIsImage(imageBodyPart)) {
//                entityManager.remove(organization);  // the organization is already persisted to the database. Since the file uploaded is not an image, removing the organization is done
//                return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
//                        .entity("File must be image, no image was uploaded")
//                        .build();
//            }
//
//            InputStream inputStream = imageBodyPart.getValueAs(InputStream.class);
//            ContentDisposition fileDetails = imageBodyPart.getContentDisposition();
//            String filename = fileDetails.getFileName();
//            System.out.println("filename = " + filename);
//            String target = organization.getOid() + File.separator + IMAGES; // todo directory should be organization name or id?
//
//            Image organizationImage = saveImages.saveImage(inputStream, target, filename);
//
//            coupleImageAndOrganization(organization, organizationImage);
//        }
//
//        return Response.status(Response.Status.CREATED).entity(organization).build();
//    }

    @RolesAllowed(value = {Group.USER})
    public Response deleteOrganization(int organizationId) {
        Organization organization = entityManager.find(Organization.class, organizationId);


        if (organization == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No organization with id: " + organizationId).build();
        }

        entityManager.remove(organization);
        return Response.ok("Organization removed from system").build();
    }

    /**
     * A user can join a organization based on the organization ID. The user has to be logged in and is retrived trough
     * the JWT principal.
     *
     * @param organizationId
     * @return
     */
    public Response joinOrganization(Long organizationId) {

        // Getting user
        User user = getUserFromPrincipal();

        //Getting organization
        Organization organization = entityManager.find(Organization.class, organizationId);

        if (isAlreadyMember(organizationId, user)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("User is already member of organization").build();
        }
        try {
            doJoinOrganization(organization, user, getGroup(Group.USER));
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN.getStatusCode()).entity("Duplicate entries").build();
        }
        return getMembership(organizationId); // todo return better feedback
    }

    public Response getOrgsWhereUserIsMember() {
        User user = getUserFromPrincipal();
        List<Organization> organizations = entityManager.createQuery(
                "select o from Organization o where o in (select m.organization from Member m where m.group =:ogroup and m.user = :user)", Organization.class)
                .setParameter("user", user)
                .setParameter("ogroup", getGroup(Group.USER))
                .getResultList();
        return Response.ok().entity(organizations).build();
    }

    /**
     * Getting an organization based on id
     *
     * @param organizationId Id must be long since database value is BIGINT
     * @return Organization with id = organizationID
     */
    public Response getOrganizationById(Long organizationId) {
        Organization organization = entityManager.find(Organization.class, organizationId);

        if (organization == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Organization not found").build();
        }
        return Response.ok(organization).build();
    }

    public Response getMembers(String organizationId) {
        Long oid;
        try {
            oid = Long.parseLong(organizationId);
        } catch (NumberFormatException ne) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Please only use numbers for id.").build();
        }
        Organization org = entityManager.find(Organization.class, oid);
        Set<Member> members = org.getMembers();
        String json = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
            json = objectMapper.writeValueAsString(members);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Response.ok(json).build();
    }

    public Response createNewOrganization(Map<String, String> map) {
        String imageAsString = map.get("image");

        Organization organization = new Organization();
        organization.setName(map.get("name"));
        organization.setEmailContact(map.get("emailContact"));
        organization.setPriceOfMembership(BigDecimal.valueOf(Long.parseLong(map.get("priceOfMembership"))));
        organization.setDescription(map.get("description"));
        entityManager.persist(organization);

        if (imageAsString != null) {
            uploadImage(imageAsString, organization);
        }

        // add the creator as a member
        doJoinOrganization(organization, getUserFromPrincipal(), getGroup(Group.ADMIN));

        return Response.status(Response.Status.CREATED).entity(organization).build();
    }

    private void uploadImage(String imageAsString, Organization org) {
        byte[] imageAsBytes = Base64.decodeBase64(imageAsString);
        InputStream bias = new ByteArrayInputStream(imageAsBytes);
        String target = String.valueOf(org.getOid());
        Image avatar = saveImages.saveImage(bias, target);
        coupleImageAndOrganization(org, avatar);
    }

    /**
     * Fetch all orgs where current user is admin
     *
     * @return
     */
    public Response getOwnedOrganizationsForUser() {
        User user = getUserFromPrincipal();

        List<Organization> organizations = entityManager.createQuery(
                "select o from Organization o where o in (select m.organization from Member m where m.group =:ogroup and m.user = :user)", Organization.class)
                .setParameter("user", user)
                .setParameter("ogroup", getGroup(Group.ADMIN))
                .getResultList();

        return Response.ok(organizations).build();
    }

    /**
     * get every membership for current user in given org, since users can
     * for example be both an admin and a user in an organization.
     *
     * @param oid
     * @return
     */
    public Response getMembership(long oid) {
        User user = getUserFromPrincipal();
        Organization organization = entityManager.find(Organization.class, oid);
        Member membership = null;
        try {
            membership = (Member) entityManager.createQuery(
                    "select m from Member m where m.user = :user and m.organization = :organization")
                    .setParameter("user", user)
                    .setParameter("organization", organization)
                    .getSingleResult();
        } catch (NonUniqueResultException nure) {
            System.out.println(user.getEmail() + " Should not have two memberships in the same org! Bad DBA!");
            return Response.status(Response.Status.FORBIDDEN.getStatusCode()).entity("Duplicate entries, please contact system administrator").build();
        } catch (NoResultException nre) {
            System.out.println("No membership found");
        }

        if (membership == null) {
            return Response.noContent().build();
        }
        return Response.ok(membership).build();
    }

    // --- Private methods below --- //
    private void coupleImageAndOrganization(Organization organization, Image organizationImage) {
        long oid = organization.getOid();
        long iid = organizationImage.getIid();
        String query = "insert into orgimages (oid, iid) values (" + oid + ", " + iid + ")";
        System.out.println("query = " + query);
        entityManager.createNativeQuery(query).executeUpdate();
    }

    /**
     * Getting a group from the given string
     *
     * @param name the name of the group. The name should always be defined as a constant in Group, e.g "Group.USER"
     * @return the group
     */
    private Group getGroup(String name) {
        return entityManager.createQuery("select g from Group g where g.name = :name", Group.class).setParameter("name", name).getSingleResult();
    }

    /**
     * Getting the user based on JWT principal
     *
     * @return the user that is logged in
     */
    private User getUserFromPrincipal() {
        String userId = principal.getName();
        return entityManager.find(User.class, userId);
    }

    /**
     * Checks if a user already is member of an organization
     *
     * @param organizationId
     * @param user
     * @return True if the user is in organization, false if not
     */
    private boolean isAlreadyMember(Long organizationId, User user) {
        Organization organization = entityManager.find(Organization.class, organizationId);
        Set<Member> members = organization.getMembers();
        Iterator it = members.iterator();
        boolean found = false;
        while (it.hasNext() && !found) {
            Member member = (Member) it.next();
            if (member.getUser().getEmail().equals(user.getEmail())) {
                found = true;
            }
            return found;
        }
        return false;
    }

    /**
     * Creates and persists new member object
     *
     * @param org
     * @param user
     * @param group
     * @return
     */
    private Member doJoinOrganization(Organization org, User user, Group group) throws EntityExistsException {
        Member member = new Member();
        System.out.println(member);
        member.setUser(user);
        member.setOrganization(org);
        member.setGroup(group);
        member.setOrganization(org);
        System.out.println(member);
        entityManager.persist(member);
        entityManager.flush();
        return member;
    }

    public Response updateOrganization(Long organizationId, Organization newOrganization) {
        Organization oldOrganization = entityManager.find(Organization.class, organizationId);
        if (oldOrganization == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Organization with id " + organizationId + "not found").build();
        }

        if (newOrganization.getName() != null && !newOrganization.getName().trim().isEmpty()) {
            oldOrganization.setName(newOrganization.getName());
        }

        if (newOrganization.getUrl() != null) {
            oldOrganization.setUrl(newOrganization.getUrl());
        }

        if (newOrganization.getEmailContact() != null) {
            oldOrganization.setEmailContact(newOrganization.getEmailContact());
        }

        if (newOrganization.getPriceOfMembership() != null) {
            oldOrganization.setPriceOfMembership(newOrganization.getPriceOfMembership());
        }

        if (newOrganization.getDescription() != null) {
            oldOrganization.setDescription(newOrganization.getDescription());
        }

        return Response.ok(oldOrganization).build();
    }
}
