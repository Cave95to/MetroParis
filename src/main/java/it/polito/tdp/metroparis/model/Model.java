package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	private Graph<Fermata, DefaultEdge> grafo ;
	
	// salviamo albero di visita ampiezza dentro la mappa, per ogni vertice (chiave) ci dice chi lo precede (valore)
	private Map <Fermata, Fermata> predecessoreMap;

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
		
		// PROFONDITA
		//DepthFirstIterator<Fermata, DefaultEdge> dfv = new DepthFirstIterator<>(this.grafo, partenza);
		
		/*
		// inizializzo mappa dei predecessori e SALVO RADICE!! preceduta da null perche non verrà mai scoperta dalla visita
		this.predecessoreMap = new HashMap<>();
		this.predecessoreMap.put(partenza, null); // chiave = vertice, valore = predecessore
		
		// implementiamo metodo per salvare il predecessore unico durante la visita, NON USARE THIS INTERNAMENTE
		// ci serve solo se vogliamo ricostruire cammino, NON PER TUTTE LE FERMATE RAGGIUNGIBILI
		// PER POPOLARE MAPPA PREDECESSORI! USARE SOLO CON VISITA PROFONDITA, SE NO C'E DI MEGLIO
		
		bfv.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>(){

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
				
			}

			@Override // SFRUTTIAMO GLI ARCHI ATTRAVERSATI PER SALVARE TUTTI I PREDECESSORI
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				
					DefaultEdge arco = e.getEdge();
					
					Fermata a = grafo.getEdgeSource(arco);
					Fermata b = grafo.getEdgeTarget(arco);
					
					// Source e target in grafo NON orientato ci indicano solo come è stato salvato dal pc l'arco!!!!
					// non sappiamo quindi a priori quale sia vertice predecessore e successore -> CONTROLLI
					
					if(predecessoreMap.containsKey(b) && !predecessoreMap.containsKey(a)) {
						
						// se b è presente nella mappa come chiave, mentre a no -> a è nuovo vertice
						predecessoreMap.put(a, b); // abbiamo raggiunto il nuovo a da b, che sarà predecessore
						
					} else if(predecessoreMap.containsKey(a) && !predecessoreMap.containsKey(b)){
						
						predecessoreMap.put(b, a); // abbiamo raggiunto b come nuovo vertice partendo da a
					}
			}

			@Override // VIENE TROPPO COMPLESSO CICLARE E CERCARE IL PREDECESSORE TRA TUTTI I VERTICI USIAMO GLI ARCHI |^
			public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
				/*System.out.println(e.getVertex());
				Fermata nuova = e.getVertex(); // il vertice attraversato che scopriamo
				Fermata precedente = vertice adiacente a 'nuova' che sia gia stato raggiunto ovvero contenuto nella mappa;
				predecessoreMap.put(nuova, precedente); // NON METTERE THIS !!!
				*//*
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> e) {
				// TODO Auto-generated method stub
				
			}});
		*/
		
		
		// DA QUI INVECE SERVE PER FERMATE RAGGIUNGIBILI
		
		List<Fermata> result = new ArrayList<>();
		
		// AMPIEZZA
		
		while(bfv.hasNext()) {
			 Fermata f = bfv.next();
			 result.add(f);
		}
		
		/* PROFONDITA
		 
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
	
	
	// METODO LUNGO CON TRAVERSAL LISTENER MA SI PUO FARE A MENO SE USIAMO VISITA AMPIEZZA
	
	/*
	public List<Fermata> trovaCammino(Fermata partenza, Fermata arrivo) {
		
		this.fermateRaggiungibili(partenza); // mi serve solo per far creare la mappa dei predecessori
		
		// List<Fermata> result = new ArrayList<>(); ARRAYLIST aggiunge in coda O(1), se in testa O(N) 
		
		List<Fermata> result = new LinkedList<>();   // LINKEDLIST aggiunge in coda e in testa O(1)
		
		// avendo mappa dei predecessori stiamo partendo dal vertice di arrivo e andando a ritroso, quindi :
		// - usiamo arraylist aggiungiamo result.add(f) e poi Collections.reverse
		// - usiamo linkedlist con result.add(0,f) perche aggiunta in testa ha costo O(1)
		
		result.add(arrivo);
		
		Fermata f = arrivo;
		
		while(this.predecessoreMap.get(f) != null) { // finche il predecessore non è null
			
			f = this.predecessoreMap.get(f); // lo prendiamo e lo aggiungiamo
			
			// result.add(f)       -> con arraylist
			result.add(0, f);   // -> con linkedlist
		}
		
		// Collections.reverse(result);    -> solo con arraylist INVERTIAMO LISTA PER AVERE DA ORIGINE A DESTINAZIONE
		
		return result;
	}
	*/
	
	
	// Implementazione di 'trovaCammino' che NON usa il traversal listener ma sfrutta
	// il metodo getParent presente in BreadthFirstIterator
	
	public List<Fermata> trovaCammino2(Fermata partenza, Fermata arrivo) {
		
		BreadthFirstIterator<Fermata, DefaultEdge> bfv = new BreadthFirstIterator<>(this.grafo, partenza) ;
		
		// fai lavorare l'iteratore per trovare tutti i vertici
		while(bfv.hasNext())
			bfv.next() ; // non mi serve il valore
		
		List<Fermata> result = new LinkedList<>() ;
		Fermata f = arrivo ;
		
		while(f!=null) {
			result.add(0,f) ;
			f = bfv.getParent(f) ;
		}
		
		return result ;
		
	}
	
}
