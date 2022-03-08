# ![](src/main/webapp/images/48x48/popcorn.png) Popcorn Jenkins Plugin

This Jenkins plugin exposes functionalities for the [Popcorn](https://github.com/lectra-tech/popcorn) by [Lectra](https://github.com/lectra-tech) open-source Jenkins platform on Kubernetes.

This plugin is only useful together with [Popcorn](https://github.com/lectra-tech/popcorn) by [Lectra](https://github.com/lectra-tech) itself.

If you want more information on how to use [Popcorn](https://github.com/lectra-tech/popcorn) by [Lectra](https://github.com/lectra-tech) on Kubernetes, follow this [guide](https://github.com/lectra-tech/popcorn).

## Build

The project is automatically built by [Github Actions](actions).

### Branches policy

The project relies on 2 protected branches

* `main`: the *default* branch of the project. It is the one that committers can push to.  
  Pull-Request after successful build and acceptance can be merged into this branch.
* `release`: this branch accepts only fast-forward merges from `main`.  
  When this branch is updated it triggers and automatic release of the project.

### Local build, tests

* Build the project: `mvn package`
* Launch all projects tests: `mvn verify`
* Start a local Jenkins with the plugin deployed: `mvn hpi:run`
  * open your browser on http://localhost:8080/jenkins 

### Upgrading project version

During a release, only the `patch` version of the project semantic version will be updated.
If you want to change the `major` or `minor` version you have to change it manually in the `pom.xml` file and commit your change.

## Release

In order to fire a release, MAINTAINERS have to update `release` branch onto the commit they want, normally `main`.

```
git checkout release
git fetch origin main
git merge -ff-only origin/main
git push origin release
```

By doing so, a Github Actions pipeline will be fired and will:
- remove the SNAPSHOT version of the project
- tag the git repository with the new fixed version
- build and test/verify the project
- publish a Github release with changelog and produced artifacts attached to it
- update the patch version of the project and add SNAPSHOT to it to prepare next development
- update automatically the `main` branch to reflect the changes 

## Sponsor

<img src="https://assets.mylectra.com/images/Logo_Lectra_def_RVB-black-red.svg" height="48px">

This project is provided to the open-source community by [Lectra](https://www.lectra.com) under the [Apache-2](http://www.apache.org/licenses/LICENSE-2.0) license.

Other [Lectra OSS contributions](https://github.com/lectra-tech):
- Popcorn: Jenkins deployment suite on kubernetes
- [Koson](https://github.com/lectra-tech/koson): lightweight Kotlin DSL for JSON
- [ld-react-feature-flags](https://github.com/lectra-tech/ld-react-feature-flags): a [React](https://reactjs.org/) provider to work with [LaunchDarkly](https://launchdarkly.com/) feature flags   