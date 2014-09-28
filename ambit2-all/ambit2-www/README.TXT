https://github.com/lbovet/docson
http://jeremydorn.com/json-editor/

SELECT idchemical,format,type_structure,structure,uncompress(structure) FROM ambit2.structure 
where idchemical in (143849,143845,148838,143862,143863,143853,143854,143855,143857,143850)


ambit2 db


** END NESTED EXCEPTION **

 duration: 0 ms, connection-id: 3619, statement-id: 1504991, resultset-id: 1533374, message: Possible incomplete traversal of result set. Cursor was left on row 1 of 2 rows when it was closed.

You should consider re-formulating your query to return only the rows you are interested in using.
Tue Aug 12 08:27:19 EEST 2014 WARN: Profiler Event: [WARN] 

** BEGIN NESTED EXCEPTION ** 

java.lang.Throwable

STACKTRACE:

java.lang.Throwable
	at com.mysql.jdbc.log.LogUtils.expandProfilerEventIfNecessary(LogUtils.java:49)
	at com.mysql.jdbc.log.StandardLogger.logInternal(StandardLogger.java:293)
	at com.mysql.jdbc.log.StandardLogger.logWarn(StandardLogger.java:240)
	at com.mysql.jdbc.profiler.LoggingProfilerEventHandler.consumeEvent(LoggingProfilerEventHandler.java:44)
	at com.mysql.jdbc.ResultSetImpl.realClose(ResultSetImpl.java:7396)
	at com.mysql.jdbc.ResultSetImpl.close(ResultSetImpl.java:918)
	at ambit2.db.UpdateExecutor.execute(UpdateExecutor.java:94)
	at ambit2.db.UpdateExecutor.execute(UpdateExecutor.java:1)
	at ambit2.db.StatementExecutor.process(StatementExecutor.java:140)
	at ambit2.db.processors.DBSubstanceWriter.importSubstanceRecord(DBSubstanceWriter.java:155)
	at ambit2.db.processors.DBSubstanceWriter.process(DBSubstanceWriter.java:200)
	at ambit2.dbsubstance.test.Tox21ReaderTest.write(Tox21ReaderTest.java:107)
	at ambit2.dbsubstance.test.Tox21ReaderTest.testWriteTox21(Tox21ReaderTest.java:76)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:606)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)
	at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:28)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:76)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:50)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:193)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:52)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:191)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:42)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:184)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:236)
	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:49)
	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:467)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:683)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:390)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:197)


** END NESTED EXCEPTION **

 duration: 0 ms, connection-id: 3619, statement-id: 1504991, resultset-id: 1533405, message: Possible incomplete traversal of result set. Cursor was left on row 1 of 2 rows when it was closed.

You should consider re-formulating your query to return only the rows you are interested in using.
