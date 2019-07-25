package org.skr.registry.service;

import org.skr.registry.EndPointRegistry;
import org.skr.registry.PermissionRegistry;
import org.skr.registry.RealmRegistry;
import org.skr.registry.SiteEntry;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;

@Service
public interface RegistryService<
        Realm extends RealmRegistry,
        Permission extends PermissionRegistry,
        EndPoint extends EndPointRegistry> {

    List<Realm> listRealms();

    Realm getRealm(String code);

    Realm registerRealm(@NotNull Realm realm,
                        @NotNull List<Permission> permissions,
                        @NotNull List<EndPoint> endPoints);

    void unregisterRealm(Realm realm);

    List<Permission> listPermissions(Realm realm);

    Permission getPermission(String permissionCode);

    List<EndPoint> listEndPoints(Realm realm);

    EndPoint getEndPoint(String url);

    /**
     * Build SiteMap by given endPoints. EndPoints could be filtered by permission
     * in advance.
     */
    default List<SiteEntry> buildSiteMap(List<EndPoint> endPoints) {
        List<SiteEntry> siteMap = new ArrayList<>();
        endPoints.sort(Comparator.comparing(EndPointRegistry::getBreadcrumb));
        endPoints.forEach(endPoint -> {
            String[] breadcrumbPath = endPoint.getBreadcrumb().split(EndPoint.BREADCRUMB_SEPARATOR);
            // find match siteEntry
            List<SiteEntry> matchingLevel = siteMap;
            SiteEntry[] matchedSiteEntries = new SiteEntry[breadcrumbPath.length];
            for (int i=0; i<breadcrumbPath.length; i++) {
                String pathNode = breadcrumbPath[i];
                Optional<SiteEntry> matchedSiteEntry = matchingLevel.stream()
                        .filter(siteEntry -> Objects.equals(siteEntry.breadcrumb, pathNode))
                        .findAny();
                if (matchedSiteEntry.isPresent()) {
                    // find matched
                    matchedSiteEntries[i] = matchedSiteEntry.get();
                    matchingLevel = matchedSiteEntry.get().siteEntries;
                } else {
                    // no matched SiteEntry, clean rest
                    for (int j=i; j<breadcrumbPath.length; j++) {
                        matchedSiteEntries[j] = null;
                    }
                    break;
                }
            }

            for (int k=0; k<breadcrumbPath.length; k++) {
                if (matchedSiteEntries[k] != null) continue;
                // slot is empty, means new entry in siteMap.
                SiteEntry newSiteEntry = new SiteEntry();
                newSiteEntry.breadcrumb = breadcrumbPath[k];
                // if last node, set endPoint
                if (k == breadcrumbPath.length-1) newSiteEntry.endPoint = endPoint;
                matchedSiteEntries[k] = newSiteEntry;
                // add to siteMap
                if (k == 0) {
                    siteMap.add(newSiteEntry);
                } else {
                    matchedSiteEntries[k-1].siteEntries.add(newSiteEntry);
                }
            }
        });
        return siteMap;
    }
}
