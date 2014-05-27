package simplestream.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;

import simplestream.networking.Peer;

public class BFSPeerIterator {

	
	private final Logger log = Logger.getLogger(getClass());
	
	
	List<Peer> seenPeers;
	Queue<Peer> peerQueue;
		

	public BFSPeerIterator() {

		seenPeers = new ArrayList<Peer>();
		peerQueue = new LinkedList<Peer>();
		
		log.debug("Starting the BFSPeerIterator");
	}

	public BFSPeerIterator(List<Peer> peers) {

		this();
		peerQueue.addAll(peers);
	}


	public void addPeer(Peer peer) {
		this.peerQueue.add(peer);
	}

	public void addPeers(Collection<Peer> alternativeHosts) {
		this.peerQueue.addAll(alternativeHosts);
	}

	private void markPeerAsSeen(Peer peer) {
		seenPeers.add(peer);
		
		log.debug("Marking Peer: " + peer + "as seen.");
	}

	// returns the next unseen peer and then marks it as seen
	public Peer getNextPeer() throws NoUnseenPeersException {

		if (peerQueue.isEmpty()) {
			throw new NoUnseenPeersException();
		}

		// Get the head of the queue
		Peer nextPeer = peerQueue.poll();

		// Keep popping the head off the queue until we find a peer we haven't seen
		while (!peerQueue.isEmpty() && seenPeers.contains(nextPeer)) {
			nextPeer = peerQueue.poll();
		}

		if (nextPeer == null) {
			throw new NoUnseenPeersException();
		} else {
			markPeerAsSeen(nextPeer);
			return nextPeer;
		}

	}

}
