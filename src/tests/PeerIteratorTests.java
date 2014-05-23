package tests;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import simplestream.BFSPeerIterator;
import simplestream.NoUnseenPeersException;
import simplestream.Peer;


public class PeerIteratorTests {

	BFSPeerIterator peerIterator;

	@Before
	public void init() {
		peerIterator = new BFSPeerIterator();
	}

	private Peer createRandomPeer() {
		Random r = new Random();
		String randomHostname =
			r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
		int randomPort = r.nextInt((65535) + 1);
		Peer randomPeer = new Peer(randomHostname, randomPort);
		return randomPeer;
	}

	@Test
	public void emptyIteratorShouldCauseException() {
		try {
			peerIterator.getNextPeer();
			Assert.fail("Expected a NoUnseenPeersException.");
		} catch (NoUnseenPeersException e) {
			// TODO Auto-generated catch block
		}
	}

	@Test
	public void iteratorReturnsCorrectSinglePeer() {
		Peer initialPeer = createRandomPeer();
		peerIterator.addPeer(initialPeer);
		Peer returnedPeer = null;
		try {
			returnedPeer = peerIterator.getNextPeer();
		} catch (NoUnseenPeersException e) {
			Assert.fail();
		}
		assertTrue(initialPeer.equals(returnedPeer));
	}

}
