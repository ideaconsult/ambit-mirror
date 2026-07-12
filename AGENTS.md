# AGENTS.md — AMBIT database & REST (RESTNet) layer

**Scope:** this document covers only AMBIT's **database access and REST layer** — how it
sits on the `modbcum` (JDBC) and `RESTNet` (Restlet) libraries. AMBIT is much larger
(chemistry, descriptors, rendering, ML, …); those are out of scope here.

## The key mental model: two parallel stacks

`modbcum` and `RESTNet` were **extracted from AMBIT**. The original processor/executor and
some convertor classes still live in-tree under `ambit2.db.*` and `ambit2.rest.*`, and they
have **diverged** from the extracted libraries. So AMBIT runs two parallel stacks:

- **The libraries**, consumed as Maven deps: `modbcum` **1.1.0-SNAPSHOT** and `restnet`
  **1.2.0-SNAPSHOT** (properties in [ambit2-all/pom.xml](ambit2-all/pom.xml), ~L580–581).
- **The in-tree forks**: `ambit2.db.*` (executors/readers) and a few `ambit2.rest.*`
  convertors.

**Consequence — the rule that matters most here:** a bug fixed in `modbcum`/`RESTNet` does
**not** propagate to the in-tree fork of the same class. When you fix a DB
resource-lifecycle bug in the library, check the `ambit2.db.*` / `ambit2.rest.*` twin and
fix it in parallel. Do not assume a library version bump covers the fork.

## Module map (DB/REST only)

| Module | Role |
|---|---|
| `ambit2-all/ambit2-db` | Core JDBC data access. Holds the **forks** (`ambit2.db.StatementExecutor`, `ambit2.db.search.QueryExecutor`, `ambit2.db.UpdateExecutor`, `ambit2.db.PreparedStatementBatchExecutor`, `ambit2.db.DbReader`), plus query objects (`IQueryRetrieval` impls), readers and writers. |
| `ambit2-all/ambit2-dbext`, `ambit2-dbui` | DB extensions / Swing table models. |
| `ambit2-all/ambit2-rest` | REST library on RESTNet: `ambit2.rest.AbstractResource` (base resource), `ambit2.rest.DBConnection` (connection factory), convertors, reporters (`QueryStaXReporter`, URI reporters). |
| `ambit2-apps/ambit2-www` | The deployable web app. `AmbitApplication` wires the router/auth; **`c3p0.properties`** lives here. |
| `ambit2-apps/ambit2-user` | Local-DB auth. `UserRouter` wires RESTNet `DBVerifier` + `DbEnroller`. |
| `ambit2-all/ambit2-base` | Config (`Preferences`, `AMBITAppConfigProperties`). |

## What reuses the library vs. what is a fork

**Reuses the fixed libraries (inherits our fixes automatically):**
- `ambit2.db.StatementExecutor` extends `modbcum` `AbstractDBProcessor` → the tolerant
  `setConnection()` (logs-and-swallows a failed close of the previous connection, never
  throws) and `close()` (nulls the field in `finally`) come from the library.
- ambit2-www streaming resources (`SubstanceResource`, `BundleSubstanceResource`) build
  `net.idea.restnet.db.convertors.OutputStreamConvertor` directly → they get its
  `release()` backstop (connection returned to the pool even if Restlet never calls
  `write()`).
- Auth: the anonymous `DBVerifier` subclass in `UserRouter` and `DbEnroller` are RESTNet
  classes → they get `DBVerifier.verify()` being `synchronized` (the verifier is a
  singleton shared across request threads).

**Forks that have diverged and need parallel maintenance — known open bugs:**
- `ambit2.db.search.QueryExecutor.process()` — on the failure path it does **not** close
  the statement it just prepared (empty `finally`, no `closeResults(rs)` in the catch), so
  a failing query (bad SQL, timeout) leaks the `PreparedStatement`/`Statement` until the
  connection is closed. This is the read workhorse (used across readers, resources, tasks).
  The library twin (`net.idea.modbcum.p.QueryExecutor`) was fixed; this one was not.
- `ambit2.db.UpdateExecutor.execute()` — statement caching is force-disabled
  (`setUseCache` hard-wired to `false`), and the `finally` only re-caches
  `if (isUseCache())`, so the prepared statement is **neither cached nor closed** — every
  insert/update/delete leaks one statement until connection close. Same regression the
  library twin already fixed.

**Not a concern:**
- `ambit2.rest.StreamConvertor` — a fork with no instantiations anywhere; **dead leftover**
  (it even closes the reporter in an outer `finally` before `write()` runs). Safe to delete.
- `ambit2.rest.RemoteStreamConvertor` (used by `ProxyResource`) streams a remote HTTP/Solr
  service, not a pooled DB connection, so the connection-stranding concern does not apply.

## Connection lifecycle & the c3p0 pool

- **Acquire:** `ambit2.rest.DBConnection.getConnection()` →
  `net.idea.modbcum.c.DatasourceFactory.getDataSource(...)` → the `modbcum` c3p0 pool
  (`DataSourceC3P0`) → `Connection`. Config comes from `AMBITAppConfigProperties`
  (host/port/db/user) plus the pool file below.
- **Pool config:** [ambit2-apps/ambit2-www/src/main/resources/c3p0.properties](ambit2-apps/ambit2-www/src/main/resources/c3p0.properties).
  Notable settings: `maxPoolSize=128`, `checkoutTimeout=60000` (callers wait up to 60 s for
  a connection when the pool is exhausted, then fail), `testConnectionOnCheckin=true`,
  `preferredTestQuery=/* ping */ SELECT 1`. **Critically, `unreturnedConnectionTimeout=0`
  (disabled)** — there is **no** pool-level reclamation of a checked-out-but-never-returned
  connection. So any connection/statement leak accumulates until the pool is exhausted and
  requests stall for `checkoutTimeout` then fail. This is why the fork leaks above matter in
  production.
- **Ownership rules (from modbcum — apply to forks too):** a processor closes the
  connection only when `closeConnection == true`; mark lent/shared connections
  `setCloseConnection(false)`; `setConnection()` must stay tolerant (never throw on a failed
  close of a stale previous connection); streaming convertors must release on **both**
  `write()`'s `finally` **and** `OutputRepresentation.release()`.

## REST resource pattern

Concrete resources extend `ambit2.rest.AbstractResource` and implement `createQuery()` and
`createConvertor(variant)`. `getRepresentation()` calls `convertor.process(queryObject)` —
note this base does **not** replicate RESTNet `QueryResource`'s check-out-connection-in-the-
resource pattern; the reporter's connection is wired through the reader/executor path.
Object/RDF convertors (`AbstractObjectConvertor`, `RDFJenaConvertor`) build fully in memory
and close the reporter synchronously in `process()`; streaming responses use RESTNet's
`OutputStreamConvertor` (has the `release()` backstop).

## Build & test notes (DB/REST)

- The `modbcum` 1.1.0-SNAPSHOT and `restnet` 1.2.0-SNAPSHOT snapshots must be available in
  the local repo first (build/install those repos before AMBIT).
- Source/target is Java 8; `mvn package`/release needs the JDK 8 toolchain
  (`maven-jar-plugin` crashes on JDK 11+), while `compile`/tests run on JDK 11/17. Keep the
  IDE's JDK aligned with the build to avoid `target/classes` corruption.
- DB driver is MySQL Connector/J 5.1.x (`com.mysql.jdbc.Driver`).
- `c3p0.properties` must be on the classpath (it is, under `ambit2-www` resources).

## When changing DB/REST code here

1. If the change mirrors a `modbcum`/`RESTNet` class, fix the in-tree fork **and** confirm
   the library twin; they drift independently.
2. Every prepared statement needs exactly one owner — cache it (and let `close()` reap it)
   or close it in a `finally`. Never leave it to the connection.
3. Cleanup must survive broken output streams and abandoned representations; because
   `unreturnedConnectionTimeout=0`, the pool will not save a leaked connection.
