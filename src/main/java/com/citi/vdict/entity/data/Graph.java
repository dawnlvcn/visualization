package com.citi.vdict.entity.data;

import java.util.ArrayList;
import java.util.List;

public class Graph<T> {
	private List<Vertex<T>> nodes = new ArrayList<>();
	private List<Edge> links = new ArrayList<>();

	public List<Vertex<T>> getNodes() {
		return nodes;
	}

	public void setVertex(List<Vertex<T>> nodes) {
		this.nodes = nodes;
	}

	public void addVertex(Vertex<T> node) {
		this.nodes.add(node);
	}

	public List<Edge> getLinks() {
		return links;
	}

	public void setEdges(List<Edge> links) {
		this.links = links;
	}

	public void addEdge(Edge link) {
		this.links.add(link);
	}
}
