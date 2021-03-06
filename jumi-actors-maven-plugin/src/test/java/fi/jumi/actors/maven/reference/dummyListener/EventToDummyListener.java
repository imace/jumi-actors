package fi.jumi.actors.maven.reference.dummyListener;

import fi.jumi.actors.eventizers.Event;
import fi.jumi.actors.maven.DummyListener;
import fi.jumi.actors.queue.MessageSender;

public class EventToDummyListener implements MessageSender<Event<DummyListener>> {

    private final DummyListener listener;

    public EventToDummyListener(DummyListener listener) {
        this.listener = listener;
    }

    public void send(Event<DummyListener> message) {
        message.fireOn(listener);
    }
}
