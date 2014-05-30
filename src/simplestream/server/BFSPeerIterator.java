package simplestream.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;

import simplestream.networking.Peer;

/**
 * Searches for an available alternative {@link Peer} to handover to.
 */
public class BFSPeerIterator {

	private final Logger log = Logger.getLogger(getClass());

	/** The {@link Peer}s to explore. */
	Queue<Peer> peerQueue;

	/** The {@link Peer}s that have been explored. */
	List<Peer> seenPeers;

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

	/**
	 * Marks the given {@link Peer} as having been explored and unsuitable.
	 *
	 * @param peer
	 *            The {@link Peer} to mark.
	 */
	private void markPeerAsSeen(Peer peer) {
		log.debug("Marking peer " + peer + " as seen.");
		seenPeers.add(peer);
	}

	/**
	 * Returns the next unseen {@link Peer} and then marks it as seen.
	 *
	 * @throws NoUnseenPeersException
	 *             if all the {@link Peer}s have been seen, and none are
	 *             suitable.
	 */
	public Peer getNextPeer() throws NoUnseenPeersException {
		if (peerQueue.isEmpty()) {
			throw new NoUnseenPeersException();
		}

		Peer nextPeer = peerQueue.poll();

		// Keep popping the head off the queue until we find an unseen peer.
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
