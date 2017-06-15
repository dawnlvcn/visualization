package com.citi.vdict.entity.view;

import com.citi.vdict.entity.data.Edge;
import com.citi.vdict.entity.data.Graph;
import com.citi.vdict.entity.data.Vertex;

public class Force implements BaseView {

	@Override
	public Object getViewData() {
		Graph<String> graph = new Graph<>();
		graph.addVertex(new Vertex<String>("user","user",10));
		graph.addVertex(new Vertex<String>("table1","table",10));
		graph.addVertex(new Vertex<String>("table2","table",10));
		graph.addVertex(new Vertex<String>("table3","table",10));
		graph.addVertex(new Vertex<String>("view1","view",8));
		graph.addVertex(new Vertex<String>("view2","view",9));
		graph.addVertex(new Vertex<String>("function1","function",9));
		graph.addVertex(new Vertex<String>("stored procedure1","procedure",7));
		graph.addVertex(new Vertex<String>("stored procedure2","procedure",9));
		graph.addVertex(new Vertex<String>("col_comment1","comment",5));
		graph.addVertex(new Vertex<String>("col_comment2","comment",5));
		graph.addVertex(new Vertex<String>("table_comment1","comment",6));
		graph.addVertex(new Vertex<String>("column1","column",7));
		graph.addVertex(new Vertex<String>("column2","column",9));
		graph.addVertex(new Vertex<String>("column3","column",9));
		
		
		graph.addEdge(new Edge("user","table1",10));
		graph.addEdge(new Edge("user","table2",10));
		graph.addEdge(new Edge("user","table3",10));		
		graph.addEdge(new Edge("view1","table3",9));
		graph.addEdge(new Edge("view1","table2",9));
		graph.addEdge(new Edge("view2","table2",9));
		graph.addEdge(new Edge("view2","table1",9));
		graph.addEdge(new Edge("function1","table1",9));
		graph.addEdge(new Edge("function1","table3",9));
		graph.addEdge(new Edge("stored procedure1","table2",9));
		graph.addEdge(new Edge("stored procedure1","table3",9));
		graph.addEdge(new Edge("stored procedure2","table3",9));
		graph.addEdge(new Edge("col_comment1","table2",9));
		graph.addEdge(new Edge("col_comment1","table3",9));
		graph.addEdge(new Edge("col_comment2","table1",9));
		graph.addEdge(new Edge("col_comment2","table2",9));
		graph.addEdge(new Edge("col_comment2","table3",9));
		graph.addEdge(new Edge("table_comment1","table3",9));
		graph.addEdge(new Edge("column1","table2",9));
		graph.addEdge(new Edge("column1","table3",9));
		graph.addEdge(new Edge("column2","table1",9));
		graph.addEdge(new Edge("column2","table3",9));
		graph.addEdge(new Edge("column3","table3",9));
		
		return graph;
	}

}
