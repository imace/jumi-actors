// Copyright © 2011-2012, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package example;

import example.generated.*;
import fi.jumi.actors.eventizers.*;
import fi.jumi.actors.queue.MessageSender;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class CodeGenerationTest {

    @Test
    public void generates_a_working_eventizer() {
        ExampleListener target = mock(ExampleListener.class);
        Eventizer<ExampleListener> eventizer = new ExampleListenerEventizer();
        MessageSender<Event<ExampleListener>> backend = eventizer.newBackend(target);
        ExampleListener frontend = eventizer.newFrontend(backend);

        frontend.onSomething();

        verify(target).onSomething();
    }
}
