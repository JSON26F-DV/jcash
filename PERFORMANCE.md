# Performance and monitoring notes

This is a small JavaFX desktop app; no container is used.

## Tuning examples

- **Application:** use `BigDecimal` for money, keep UI handlers short, and avoid repeated work in list rendering.
- **Data access:** JDBI binds named parameters (prepared statements), closes handles safely, and runs balance/record changes in one transaction. The database has indexes on mobile number and transaction sender/receiver plus date.
- **JVM:** example launch options: `-Xms256m -Xmx512m -XX:+UseG1GC`. Adjust the heap only after measuring the app on its target machine.

## Observe resource use (Linux)

- CPU/process and memory: `top` or `htop`; system memory: `free -h`.
- Disk I/O: `iostat -xz 1` (install the `sysstat` package if needed).
- JVM flags and runtime: `jcmd <pid> VM.flags` and `jcmd <pid> GC.heap_info`.
- GC activity: `jstat -gc <pid> 1s`.
- Live JVM charts: `jconsole` (attach to the running Java process).

Compare CPU, memory, and I/O before and after a representative workload. Avoid collecting PINs or other sensitive values during monitoring.
