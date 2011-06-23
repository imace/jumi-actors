// Copyright © 2011, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package net.orfjackal.jumi.core.events;

import net.orfjackal.jumi.api.drivers.TestId;
import net.orfjackal.jumi.core.SuiteListener;
import net.orfjackal.jumi.core.actors.*;

public class SuiteEventSender implements SuiteListener {
    private final MessageSender<Event<SuiteListener>> sender;

    public SuiteEventSender(MessageSender<Event<SuiteListener>> sender) {
        this.sender = sender;
    }

    public void onSuiteStarted() {
        sender.send(new SuiteStartedEvent());
    }

    public void onSuiteFinished() {
        sender.send(new SuiteFinishedEvent());
    }

    public void onTestFound(TestId id, String name) {
        sender.send(new TestFoundEvent(id, name));
    }
}