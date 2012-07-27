// Copyright © 2011-2012, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.launcher;

import fi.jumi.actors.*;
import fi.jumi.actors.eventizers.Event;
import fi.jumi.actors.queue.*;
import fi.jumi.core.SuiteListener;
import fi.jumi.core.config.Configuration;
import fi.jumi.launcher.remote.SuiteLauncher;

import javax.annotation.concurrent.ThreadSafe;
import java.io.*;
import java.util.Arrays;

@ThreadSafe
public class JumiLauncher implements Closeable {

    private final MessageQueue<Event<SuiteListener>> eventQueue = new MessageQueue<Event<SuiteListener>>();

    private final SuiteOptions suiteOptions = new SuiteOptions();
    private final ActorThread actorThread;
    private final ActorRef<SuiteLauncher> suiteLauncher;

    public JumiLauncher(ActorThread actorThread, ActorRef<SuiteLauncher> suiteLauncher) {
        this.actorThread = actorThread;
        this.suiteLauncher = suiteLauncher;
    }

    public MessageReceiver<Event<SuiteListener>> getEventStream() {
        return eventQueue;
    }

    public void start() {
        suiteLauncher.tell().runTests(suiteOptions, eventQueue);
    }

    public void shutdownDaemon() {
        suiteLauncher.tell().shutdownDaemon();
    }

    @Override
    public void close() {
        suiteLauncher.tell().disconnectFromDaemon(); // TODO: is this needed after we have a way to disconnect all network connections?
        actorThread.stop();
        // TODO: shutdown executors
    }

    public void addToClassPath(File file) {
        suiteOptions.classPath.add(file);
    }

    public void setTestsToInclude(String pattern) {
        suiteOptions.testsToIncludePattern = pattern;
    }

    public void addJvmOptions(String... jvmOptions) {
        suiteOptions.jvmOptions.addAll(Arrays.asList(jvmOptions));
    }

    public void enableMessageLogging() {
        suiteOptions.systemProperties.setProperty(Configuration.LOG_ACTOR_MESSAGES, "true");
    }
}
