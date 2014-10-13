import org.powerbot.script.Script.Manifest;
import org.powerbot.script.PollingScript;

@Manifest(name = "Example", description = "")
public class Example extends PollingScript {
	@Override
	public void poll() {
		log.info("Polled.");
	}
}