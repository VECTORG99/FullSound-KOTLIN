# FullSound (Android)

CI status:

- Android CI: ![Android CI](https://github.com/VECTORG99/FullSound-KOTLIN/actions/workflows/android-ci.yml/badge.svg)
- Quick Check: ![Quick Check](https://github.com/VECTORG99/FullSound-KOTLIN/actions/workflows/quick-check.yml/badge.svg)

## Cómo funciona
- En cada push/PR se ejecutan builds y tests.
- El workflow Android CI ejecuta build completo, tests unitarios e instrumentados y sube el APK.
- El workflow Quick Check hace una verificación rápida (assemble, lint, unit tests) para cualquier rama.
