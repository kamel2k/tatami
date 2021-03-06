package fr.ippon.tatami.web.rest;

import fr.ippon.tatami.domain.Status;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.security.AuthenticationService;
import fr.ippon.tatami.service.IndexService;
import fr.ippon.tatami.service.TimelineService;
import fr.ippon.tatami.service.util.DomainUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author dmartin
 */
@Controller
public class SearchController {

    private final Log log = LogFactory.getLog(SearchController.class);

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    private IndexService indexService;

    @Inject
    @Named("indexActivated")
    private boolean indexActivated;

    @Inject
    private TimelineService timelineService;

    /**
     * GET  /search/?q=jdubois -> get the status where "jdubois" appears
     */
    @RequestMapping(value = "/rest/search",
            method = RequestMethod.GET,
            produces = "application/json")
    @ResponseBody
    public Collection<Status> listStatusForUser(@RequestParam(value = "q", required = false, defaultValue = "") String q,
                                                @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                @RequestParam(value = "rpp", required = false, defaultValue = "20") Integer rpp) {

        if (log.isDebugEnabled()) {
            log.debug("REST request to search status containing these words (" + q + ").");
        }

        if (!indexActivated) {
            return new ArrayList<Status>();
        }

        final User currentUser = authenticationService.getCurrentUser();
        String domain = DomainUtil.getDomainFromLogin(currentUser.getLogin());
        final List<String> ids = indexService.search(domain, Status.class, null, q, page, rpp, "statusDate", "desc");
        return timelineService.buildStatusList(ids);
    }

}
