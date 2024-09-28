package rize.os.platform.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class IncompleteEventRepublication
{
    private final IncompleteEventPublications incompleteEvents;

    @Scheduled(fixedDelay = 5000, initialDelay = 5000)
    public void republishIncompleteEvents()
    {
        log.info("Republishing incomplete events");
        incompleteEvents.resubmitIncompletePublicationsOlderThan(Duration.ofSeconds(5));
    }
}
