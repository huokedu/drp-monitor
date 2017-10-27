package me.sfeer.domain;

public class Topology {

    private Long id;
    private String rssId;
    private String name;
    private String group;
    private String nodes;
    private String links;
    private String areas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRssId() {
        return rssId;
    }

    public void setRssId(String rssId) {
        this.rssId = rssId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getNodes() {
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public String getAreas() {
        return areas;
    }

    public void setAreas(String areas) {
        this.areas = areas;
    }

    @Override
    public String toString() {
        return "Topology{" +
                "id=" + id +
                ", rssId='" + rssId + '\'' +
                ", name='" + name + '\'' +
                ", group='" + group + '\'' +
                ", nodes='" + nodes + '\'' +
                ", links='" + links + '\'' +
                ", areas='" + areas + '\'' +
                '}';
    }
}
