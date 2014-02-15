import java.awt.Graphics;

/**
Except from http://www.msci.memphis.edu/~simmonsj/c4302/notes/staging/chap3.html

To run an applet, the browser creates an instance of its class. The instance, or object, 
occupies a chunk of memory and holds any information pertaining to the applet. After constructing 
the instance, the browser calls its init() method, then its start() method, then its paint(g) method.
...
When the applet is finally removed from the browser, say at browser shutdown, the browser calls its 
destroy() method for final cleanup.

This interface specifies pre and post payload insertion points.
*/
public interface Payload {

	public void preInitPayload();
	public void postInitPayload();
	
	public void preStartPayload();
	public void postStartPayload();
	
	public void prePaintPayload(Graphics g);
	public void postPaintPayload(Graphics g);
	
	public void preStopPayload();
	public void postStopPayload();
	
	public void preDestroyPayload();
	public void postDestroyPayload();
	
}
