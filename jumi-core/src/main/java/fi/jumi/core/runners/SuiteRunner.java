// Copyright © 2011-2012, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.core.runners;

import fi.jumi.actors.*;
import fi.jumi.actors.workers.*;
import fi.jumi.api.drivers.*;
import fi.jumi.core.*;
import fi.jumi.core.drivers.DriverFinder;
import fi.jumi.core.files.*;
import fi.jumi.core.runs.*;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.concurrent.Executor;

@NotThreadSafe
public class SuiteRunner implements Startable, TestClassFinderListener {

    private final SuiteListener suiteListener;
    private final TestClassFinder testClassFinder;
    private final DriverFinder driverFinder;
    private final ActorThread actorThread;
    private final Executor testExecutor;

    private final RunIdSequence runIdSequence = new RunIdSequence();
    private int childRunners = 0;

    public SuiteRunner(SuiteListener suiteListener,
                       TestClassFinder testClassFinder,
                       DriverFinder driverFinder,
                       ActorThread actorThread,
                       Executor testExecutor) {
        this.suiteListener = suiteListener;
        this.testClassFinder = testClassFinder;
        this.driverFinder = driverFinder;
        this.actorThread = actorThread;
        this.testExecutor = testExecutor;
    }

    @Override
    public void start() {
        suiteListener.onSuiteStarted();

        TestClassFinderRunner runner = new TestClassFinderRunner(
                testClassFinder,
                actorThread.bindActor(TestClassFinderListener.class, this)
        );

        WorkerCounter executor = new WorkerCounter(testExecutor);
        executor.execute(runner);
        executor.afterPreviousWorkersFinished(childRunnerListener());
    }

    @Override
    public void onTestClassFound(Class<?> testClass) {
        Driver driver = driverFinder.findTestClassDriver(testClass);

        SuiteNotifier suiteNotifier = new DefaultSuiteNotifier(
                actorThread.bindActor(TestClassListener.class,
                        new DuplicateOnTestFoundEventFilter(
                                new SuiteListenerAdapter(suiteListener, testClass))),
                runIdSequence
        );

        WorkerCounter executor = new WorkerCounter(testExecutor);
        executor.execute(new DriverRunner(driver, testClass, suiteNotifier, executor));
        executor.afterPreviousWorkersFinished(childRunnerListener());
    }

    private ActorRef<WorkerListener> childRunnerListener() {
        fireChildRunnerStarted();

        @NotThreadSafe
        class OnRunnerFinished implements WorkerListener {
            @Override
            public void onAllWorkersFinished() {
                fireChildRunnerFinished();
            }
        }
        return actorThread.bindActor(WorkerListener.class, new OnRunnerFinished());
    }

    private void fireChildRunnerStarted() {
        childRunners++;
    }

    private void fireChildRunnerFinished() {
        childRunners--;
        if (childRunners == 0) {
            suiteListener.onSuiteFinished();
        }
    }
}
