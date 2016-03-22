# The role of CDI / Weld #

CDI was intended to be the platform for plugins integration.

However, it doesn't go well - the unsatisfied dependency errors often appear without reason. This could be caused by usage Solder which misbehaves - as proven by SOLDER-339.

However, it's still used, mainly to grab all the available plugin implementations,
```
@Inject Instance<Plugin> ...;
```

and for
```
@PersistenceContext EntityManager em;
```