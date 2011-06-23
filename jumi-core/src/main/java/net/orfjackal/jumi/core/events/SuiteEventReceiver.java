// Copyright © 2011, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package net.orfjackal.jumi.core.events;

import net.orfjackal.jumi.core.SuiteListener;
import net.orfjackal.jumi.core.actors.*;

public class SuiteEventReceiver implements MessageSender<Event<SuiteListener>> {
    private final SuiteListener listener;

    public SuiteEventReceiver(SuiteListener listener) {
        this.listener = listener;
    }

    public void send(Event<SuiteListener> message) {
        message.fireOn(listener);
    }
}