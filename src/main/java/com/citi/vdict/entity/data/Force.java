package com.citi.vdict.entity.data;

import java.util.ArrayList;
import java.util.List;

public class Force {
	private List<Node> nodes = new ArrayList<>();
	private List<Link> links = new ArrayList<>();

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public void addNode(Node node) {
		this.nodes.add(node);
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public void addLinks(Link link) {
		this.links.add(link);
	}
}
