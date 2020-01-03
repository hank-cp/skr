/*
 * Copyright (C) 2019-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skr.registry;

import java.io.Serializable;
import java.util.*;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public class SiteEntry implements Serializable {

    public String breadcrumb;

    public List<SiteEntry> siteEntries = new ArrayList<>();

    public EndPointRegistry endPoint;

    /**
     * Build SiteMap by given endPoints. EndPoints could be filtered by permission
     * in advance.
     */
    public static List<SiteEntry> buildSiteMap(List<EndPointRegistry> endPoints) {
        List<SiteEntry> siteMap = new ArrayList<>();
        endPoints.sort(Comparator.comparing(EndPointRegistry::getBreadcrumb));
        endPoints.forEach(endPoint -> {
            String[] breadcrumbPath = endPoint.getBreadcrumb().split(EndPointRegistry.BREADCRUMB_SEPARATOR);
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
