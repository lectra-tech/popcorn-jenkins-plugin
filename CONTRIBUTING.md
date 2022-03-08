# Contribution guide

Any contribution is welcome on this project whatever it is:

- reporting a [bug or feature request](./issues)
- remarks or comments via [an issue](./issues)
- documentation or feature enhancement via a Pull-Request

## Enhancements

For any code change follow the instructions below:

- clone this repository
- do your changes inside your clone
  - ensure the project still builds correctly: `mvn verify`
  - for big changes, separate your commits to provide small independent/self-contained/meaningful commits
  - if relevant, close or mention existing issues that your contribution refers to or corrects 
  - push your changes
- open a [Pull-Request](./pulls)
  - describe your change

## Commits

The project enforces the respect of [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/) specification.
A git hook protects committing bad messages.

## File formatting

### Java file formatting

This project uses [google-java-format](https://github.com/google/google-java-format) an opinionated formatting library
via the usage of plugin [fmt-maven-plugin](https://github.com/coveooss/fmt-maven-plugin)

The file formatting is checked both by a git pre-commit hook and with build step. 

Follow instructions on [google-java-format](https://github.com/google/google-java-format#intellij-android-studio-and-other-jetbrains-ides) to setup your IDE correctly.