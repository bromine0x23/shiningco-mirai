package xyz.bromine0x23.shiningco.plugins.live;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/app/plugins/live")
public class LivePluginController {

	private final LivePlugin plugin;

	public LivePluginController(LivePlugin plugin) {
		this.plugin = plugin;
	}

	@PostMapping("bililive-recorder-webhook")
	public ResponseEntity<Void> webhook(@RequestBody BililiveRecorderEvent event) {
		plugin.handle(event);
		return ResponseEntity.ok().build();
	}

}
