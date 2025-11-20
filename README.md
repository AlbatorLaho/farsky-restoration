# FarSky Restoration (unofficial)

This is an unofficial effort to restore and reconstruct the [FarSky](https://www.farskyinteractive.com/farsky) video game.

Why, you might ask?
Because it's [basically abandoned](https://steamcommunity.com/games/286340/announcements/detail/3087787366398826689).
The owner also made [comments on itch.io](https://farsky-interactive.itch.io/farsky#post-7729741) indicating that he might open source it himself at some point, but said:
> I am not ready for this yet, plus I don't want any issue with the musician (Lapse) who did the musics.

I've decompiled and deobfuscated most of the code. (with the help of AI)
I plan to update, refactor, and modernize, the code and its dependencies.
Hopefully I (or others) will be able to add new features as well! (like multiplayer or modding)

## Initial setup

I haven't bundled the game resources, since I don't have permission to distribute them.
Though technically I didn't have permission to decompile and refactor the code either...
But that being said, you'll have to copy the game resources from the [official game on itch.io](https://farsky-interactive.itch.io/farsky).

After you've downloaded the game, rename `farsky.jar` to `farsky.zip` and extract it.
Inside should be a `res` folder.
Copy its **_contents_** to `farsky-app/src/main/resources`.
It should look like `resources/sounds` rather than `resources/res/sounds`.

Then copy the `native` folder to the project root, alongside the `farsky-app` folder.
If you're on macOS, you may need to bypass quarantine for all files under `native/macosx`.

At this point it should be ready to build!

## Build

```sh
./gradlew build
```

## Run

```sh
./gradlew run
```

## Shout-out

Huge thanks to the amazing people that made this game back in 2014!
It was truly an amazing game that had a lot of potential!
