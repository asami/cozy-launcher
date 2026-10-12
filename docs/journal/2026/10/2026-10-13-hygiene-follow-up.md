# Cozy launcher specification hygiene follow-up

Date: 2026-10-13
Workflow: reasoned local commit during Cozy related-repository synchronization
Independent reviewer: related_sync_cozy_launcher_review13, GPT-6.1 Sol / medium.

Both records are OPEN, nonblocking specification-maintenance items; priority low.
Discovery boundary: acceptance of the existing classpath usability change.
No cleanup, code change or suppression is authorized by these records.
The owned behavior passed CozyLauncherSpec (30 tests); these items predate it.

- **HYG-COZY-LAUNCHER-SPEC-NARRATIVE-01**, owner `cozy-launcher`: pre-existing generic, consecutive GWT wrappers such as `CozyLauncherSpec.scala:57` delegate to older helpers without semantic clauses at their actual boundaries. Source-text checks at lines 1168 and 1252 also depend on implementation spelling and a sibling source tree. Maintenance risk is documentary clarity and brittle validation. Follow-up boundary: improve the older specification scenarios and review whether their documentary checks still express useful constraints. These structures existed at HEAD; they do not undermine the newly changed classpath helper, which supplies actual setup/action/expectation clauses and behavioral assertions.

- **HYG-COZY-LAUNCHER-SPEC-WORK-ARTIFACTS-01**, owner `cozy-launcher`: unchanged fixture helpers at `CozyLauncherSpec.scala:1284` and `:1293` create system temporary directories without cleanup, contrary to the shared `target/` work-artifact convention. Maintenance risk is accumulated fixture artifacts. Follow-up boundary: migrate fixture allocation and cleanup under repository `target/` while preserving behavior coverage. Confirmed pre-existing through `git show HEAD`.
