package org.geoint.gf.audit;

import com.sun.appserv.security.AuditModule;
import gov.ic.geoint.gf.audit.log.AuditCategory;
import gov.ic.geoint.gf.audit.log.AuditLogger;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;

/**
 * Audit module plugin
 */
public class AuditModulePlugin extends AuditModule {

    private static final String PROP_AUDIT_ON = "auditOn";
    private static boolean auditFlag = false;
    private static final AuditLogger logger = AuditLogger.logger();

    /**
     * Check auditing state.
     *
     * @return
     * @returns True if auditing is active currently.
     *
     */
    public static boolean isActive() {
        return auditFlag;
    }

    @Override
    public void init(Properties props) {
        super.init(props);
        String audit = props.getProperty(PROP_AUDIT_ON);
        auditFlag = (audit == null) ? false : Boolean.parseBoolean(audit);
    }

    /**
     * Invoked post web authorization request.
     *
     * @param user the username for whom the authorization was performed
     * @param req the HttpRequest object for the web request
     * @param type the permission type, hasUserDataPermission or
     * hasResourcePermission.
     * @param success the status of the web authorization request
     */
    @Override
    public void webInvocation(String user, HttpServletRequest req,
            String type, boolean success) {
        if (!isActive()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("user '").append((user != null) ? user : "unknown")
                .append("' from ip ").append(req.getRemoteAddr())
                .append(" has ")
                .append((success) ? "successfully " : "failed to ")
                .append("request web resource at ").append(req.getRequestURI());
        logger.log(AuditCategory.REQUEST, sb.toString());
    }

    /**
     * Invoked post ejb authorization request.
     *
     * @param user the username for whom the authorization was performed
     * @param ejb the ejb name for which this authorization was performed
     * @param method the method name for which this authorization was performed
     * @param success the status of the ejb authorization request
     */
    @Override
    public void ejbInvocation(String user, String ejb, String method, boolean success) {
        if (!isActive()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("user '").append((user != null) ? user : "unknown")
                .append("' has ")
                .append((success) ? "successfully " : "failed to ")
                .append("invoke the EJB method ").append(ejb).append("#")
                .append(method);
        logger.log(AuditCategory.REQUEST, sb.toString());
    }

    /**
     * Invoked during validation of the web service request
     *
     * @param uri The URL representation of the web service endpoint
     * @param endpoint The name of the endpoint representation
     * @param success the status of the web service request validation
     */
    @Override
    public void webServiceInvocation(String uri, String endpoint, boolean success) {
        //this doesn't give enough auditing information, skip it
    }

    /**
     * Invoked during validation of the web service request
     *
     * @param endpoint The representation of the web service endpoint
     * @param success the status of the web service request validation
     */
    @Override
    public void ejbAsWebServiceInvocation(String endpoint, boolean success) {
        //this doesn't give enough auditing information, skip it
    }

    /**
     * Invoked post authentication request for a user in a given realm
     *
     * @param user username for whom the authentication request was made
     * @param realm the realm name under which the user is authenticated.
     * @param success the status of the authentication
     */
    @Override
    public void authentication(String user, String realm, boolean success) {
        if (!isActive()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("user ").append((user != null) ? user : "unknown").append(" has ");
        sb.append((success) ? "failed " : "succeeded ");
        sb.append("autentication to realm ").append(realm);
        logger.log(AuditCategory.AUTHENTICATION, sb.toString());
    }

    @Override
    public void serverShutdown() {
        if (!isActive()) {
            return;
        }
        logger.log(AuditCategory.SYSTEM, "server started");
    }

    @Override
    public void serverStarted() {
        if (!isActive()) {
            return;
        }
        logger.log(AuditCategory.SYSTEM, "server started");
    }

}
