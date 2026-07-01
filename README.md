# Land Shop

A server-side Fabric mod for Minecraft.

**Description**: Buy Flan claim blocks with Deepslate Coal Ore. Exchange 15x Deepslate Coal Ore for 256 claim blocks using `/buylands`.

## Dependencies

This mod requires the following dependencies to build and run properly:

- **Minecraft**: `26.1.2`
- **Java**: `21` or higher
- **Fabric Loader**: `>=0.16.0` (configured with `0.16.14`)
- **Fabric API**: `0.152.1+26.1.2`
- **Flan Mod**: `26.1.2-1.12.7-fabric` (Required for PlayerClaimData API access; loaded at runtime from the mods folder)

## Building from Source

To build the mod from source, run the following Gradle command in the root directory:

```bash
gradlew build
```
The compiled jar will be located in the `build/libs` directory.
