import java.awt.Graphics;

public class TestPayload implements Payload {

	@Override
	public void preInitPayload() {
		System.out.println("Test preInitPayload");
	}

	@Override
	public void postInitPayload() {
		System.out.println("Test postInitPayload");
	}

	@Override
	public void preStartPayload() {
		System.out.println("Test preStartPayload");
	}

	@Override
	public void postStartPayload() {
		System.out.println("Test postStartPayload");
	}

	@Override
	public void prePaintPayload(Graphics g) {
		System.out.println("Test prePaintPayload");
	}

	@Override
	public void postPaintPayload(Graphics g) {
		System.out.println("Test postPaintPayload");
	}

	@Override
	public void preStopPayload() {
		System.out.println("Test preStopPayload");
	}

	@Override
	public void postStopPayload() {
		System.out.println("Test postStopPayload");
	}

	@Override
	public void preDestroyPayload() {
		System.out.println("Test preDestroyPayload");
	}

	@Override
	public void postDestroyPayload() {
		System.out.println("Test postDestroyPayload");
	}

}
