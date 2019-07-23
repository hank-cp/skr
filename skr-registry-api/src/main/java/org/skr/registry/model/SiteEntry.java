package org.skr.registry.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SiteEntry implements Serializable {

    public String breadcrumb;

    public List<SiteEntry> siteEntries = new ArrayList<>();

    public EndPointRegistry endPoint;

}
