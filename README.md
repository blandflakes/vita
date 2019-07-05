# Vita

This repository is a declaration of my programming skills, experience, and plans.

`vita.edn` is essentially a computer-readable data structure containing a hopefully up-to-date
representation of my skills.

`next.edn` is conceptually a set of future plans, each key of which may eventually be merged into
`vita.edn`. It is a file tracking my interests; I may not actively be working on any of those items
directly.

`vita.clj` generates HTML and PDF versions of my resume from `vita.edn`.

## Usage

To actually run the generator, use `clj -m vita`