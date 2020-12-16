# Release Strategy
This strategy document will be consulted when creating a release version of the mod.

## Identifying a release candidate
A release candidate could be any passing build. This could include:

- When a new feature is added.
- When a major bug is fixed.
- When the repository is in a stable state (all in-progress features are finished).

## Preparing a release
Generally GitHub Actions will take care of preparing a release. For alpha builds, this is sufficient.
For pre-releases and official releases, some manual preperation is needed:

1. All in-progress features need to be finished or temporarily disabled (to be finished in a future release).
2. The commit history and source code will be inspected for changes to document and potentially include in the release change log.

### GitHub Release (https://github.com/cloewen8/Corntopia/releases)
GitHub Actions will automatically create a draft pre-release of the mod.
These releases are to be used as reference for all other platform releases.

1. The "alpha" status in the title and file names need to be replaced with "beta" (for pre-releases) or "release" (for official releases).
2. A brief summary of the release will be created.
3. A change log will be added to the release (if applicable).
4. The release is immediately published.

### Planet Minecraft (https://www.planetminecraft.com/mod/corntopia/)
Planet Minecraft only provides a single download of the mod. As such only the most relevant version of the mod will be released.
Pre-releases and snapshot versions (of Minecraft) will not be included.

1. The page needs to be edited directly.
2. The Media Gallery, Game Version, Progress, Page Content, and Tags are to be updated as needed to reflect new features.
3. The Downloadable File is to be deleted and replaced with the release from GitHub Releases.

### CurseForge (https://www.curseforge.com/minecraft/mc-mods/corntopia)
GitHub Actions will automatically upload the mod file using [Itsmeow's curseforge-upload action](https://github.com/itsmeow/curseforge-upload).

1. The Release Type needs to be set to "Beta" (for pre-releases) or "Release" (for official releases).
2. The Description needs to be set to Markdown. The GitHub release description can then be copied over.
3. The Supported Java Versions needs to be set to the lowest supported version and up.
4. The Supported Minecraft Versions needs to be set (based on major and minor versions).

## How is my distribution?
See room for improvement, don't like how something is handled or want the mod to be distributed to another website?
Consider contributing a change to this document!
