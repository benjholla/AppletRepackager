package repackager;

import java.awt.Graphics;

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
