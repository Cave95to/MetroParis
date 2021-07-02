package it.polito.tdp.metroparis.model;

import org.jgrapht.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;

public class ProvaGrafo {

	public static void main(String[] args) {
		
		Graph<String, DefaultEdge> grafo = new SimpleGraph<>(DefaultEdge.class);
		
		Graph<String, DefaultEdge> grafo2 = new SimpleDirectedGraph<>(DefaultEdge.class);
		
		grafo.addVertex("UNO");
		grafo.addVertex("DUE");
		grafo.addVertex("TRE");
		
		grafo.addEdge("UNO", "TRE");
		grafo.addEdge("DUE", "TRE");
		
		grafo2.addVertex("UNO");
		grafo2.addVertex("DUE");
		grafo2.addVertex("TRE");
		
		grafo2.addEdge("UNO", "TRE");
		grafo2.addEdge("DUE", "TRE");
		
		System.out.println(grafo); // ([UNO, DUE, TRE], [{UNO,TRE}, {DUE,TRE}])    GRAFFE NO ORIENTAMENTO
		
		System.out.println(grafo2); // ([UNO, DUE, TRE], [(UNO,TRE), (DUE,TRE)])   TONDE SI ORIENTAMENTO
	}

}
