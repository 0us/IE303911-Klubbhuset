package no.ntnu.klubbhuset;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import no.ntnu.klubbhuset.domain.Group;
import no.ntnu.klubbhuset.domain.SecurityGroup;

@Singleton
@Startup
public class RunOnStartup {
    @PersistenceContext
    EntityManager em;

    @PostConstruct
    public void init() {
        long groups = (long) em.createNativeQuery("SELECT count(g.name) from agroup g").getSingleResult();
        long securitygroups = (long) em.createNativeQuery("SELECT count(g.name) from securityroles g").getSingleResult();
        if(groups == 0) {
            em.persist(new Group(Group.USER));
            em.persist(new Group(Group.ADMIN));
        }

        if(securitygroups == 0) {
            em.persist(new SecurityGroup(SecurityGroup.USER));
            em.persist(new SecurityGroup(SecurityGroup.ADMIN));
        }
    }
}
