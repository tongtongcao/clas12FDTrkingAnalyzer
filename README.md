The package builds a platform for analysis of forward tracking.

Installation: mvn clean install (Java 21 as default)

Plenty of tools have been developed base on the platform.
Commands for the tools are in the bin folder.

An example for usage of a tool:

path-to-installation/bin/studyBgEffectsOnValidTracks pure_input.hipo bg_input.hipo

The tool analyzes background effects on tracking efficiency at all levels of reconstruction through event-by-event comparison between pure and background MC samples.
