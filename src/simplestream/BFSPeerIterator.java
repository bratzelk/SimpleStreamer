package simplestream;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BFSPeerIterator {

	List<Peer> seenPeers;
	Queue<Peer> peerQueue;

	public BFSPeerIterator() {

		seenPeers = new ArrayList<Peer>();
		peerQueue = new LinkedList<Peer>();
	}

	public BFSPeerIterator(List<Peer> peers) {

		this();
		peerQueue.addAll(peers);
	}


	public void addPeer(Peer peer) {
		this.peerQueue.add(peer);
	}

	public void addPeers(List<Peer> peers) {
		this.peerQueue.addAll(peers);
	}

	private void markPeerAsSeen(Peer peer) {
		seenPeers.add(peer);
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
