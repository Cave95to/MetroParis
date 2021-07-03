package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	Graph<Fermata, DefaultEdge> grafo ;

	public void creaGrafo() {
		this.grafo = new SimpleGraph<>(DefaultEdge.class) ;
		
		MetroDAO dao = new MetroDAO() ;
		List<Fermata> fermate = dao.getAllFermate() ;
		
		// Aggiungiamo vertci
		
//      non serve fare for e aggiungere un vertice alla volta		
//		for(Fermata f : fermate) {
//			this.grafo.addVertex(f) ;
//		}
		
//		usiamo classe esterna GRAPHS di questo grafo metti tutte le fermate
		Graphs.addAllVertices(this.grafo, fermate) ;
		
		// Aggiungiamo gli archi
		
		// doopo ciclo for, per ogni coppia vediamo se sono collegate
//		for(Fermata f1: this.grafo.vertexSet()) {
//			for(Fermata f2: this.grafo.vertexSet()) {
//				if(!f1.equals(f2) && dao.fermateCollegate(f1, f2)) {
//					this.grafo.addEdge(f1, f2) ;
//				}
//			}
//		}
		
		// sfruttiamo oggetti connessioni del db per creare gli archi, ma necessario passare tutte le fermate
		List<Connessione> connessioni = dao.getAllConnessioni(fermate) ;
		for(Connessione c: connessioni) {
			this.grafo.addEdge(c.getStazP(), c.getStazA()) ;
		}
		
		System.out.format("Grafo creato con %d vertici e %d archi\n",
				this.grafo.vertexSet().size(), this.grafo.edgeSet().size()) ;
//		System.out.println(this.grafo) ;
		
		/*
		// supponiamo di avere fermata e voler conoscere gli archi uscenti
		
		Fermata f;
		
		Set<DefaultEdge> archi = this.grafo.edgesOf(f);
		
		// data la fermata e ogni arco uscente vogliamo trovare verice opposto
		
		for(DefaultEdge e : archi) {
			
			// 1 METODO, archi non orientati.. non sappiamo chi sia origine e chi destinazione, necessari controlli
			/*Fermata f1 = this.grafo.getEdgeSource(e);
			Fermata f2 = this.grafo.getEdgeTarget(e);
			
			if(f.equals(f1)) {
				// il vertice opposto è f2
			} else {
				// il vertice opposto è f1
			}
			*/
			
			// 2 METODO VELOCE ma sempre dentro ciclo
			//Fermata f1 = Graphs.getOppositeVertex(this.grafo, e, f);
		//}
		
		// 3 METODO PASSO DIRETTAMENTE A LISTA DI VERTICI ADIACENTI, successori o predecessori
		//List<Fermata> fermateAdiacenti = Graphs.successorListOf(this.grafo, f);
		
	}
	
	// metodo per visita al grafo AMPIEZZA VS PROFONDITA
	public List<Fermata> fermateRaggiungibili(Fermata partenza) {
		
		// AMPIEZZA
		BreadthFirstIterator<Fermata, DefaultEdge> bfv = new BreadthFirstIterator<>(this.grafo, partenza);
		
		 List<Fermata> result = new ArrayList<>();
		 
		 while(bfv.hasNext()) {
			 Fermata f = bfv.next();
			 result.add(f);
		 }
		 
		
		/* PROFONDITA
		 DepthFirstIterator<Fermata, DefaultEdge> dfv = new DepthFirstIterator<>(this.grafo, partenza);
			
		 List<Fermata> result = new ArrayList<>();
		 
		 while(dfv.hasNext()) {
			 Fermata f = dfv.next();
			 result.add(f);
		 }
		 */
		 
		 
		 return result;
	}
	
	
	public Fermata trovaFermata(String nome) {
		
		for(Fermata f : this.grafo.vertexSet()) {
			
			if(f.getNome().equals(nome))
				return f;
			
		}
		
		return null;
	}
	
}
