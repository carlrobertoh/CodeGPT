package ee.carlrobert.codegpt;

import ee.carlrobert.codegpt.telemetry.core.service.TelemetryMessageBuilder;
import ee.carlrobert.codegpt.telemetry.core.util.Lazy;

public class TelemetryService {

    private static final TelemetryService INSTANCE = new TelemetryService();

    private final Lazy<TelemetryMessageBuilder> builder = new Lazy<>(() -> new TelemetryMessageBuilder(TelemetryService.class.getClassLoader()));

    public static TelemetryMessageBuilder instance() {
        return INSTANCE.builder.get();
    }
}

