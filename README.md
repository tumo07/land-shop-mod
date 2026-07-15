# Land Shop

A server-side Fabric mod for Minecraft.

**Description**: Buy Flan claim blocks with Deepslate Coal Ore. Exchange 15x Deepslate Coal Ore for 256 claim blocks using `/buylands`.

## Dependencies

This mod requires the following dependencies to run properly:

- **Minecraft Version**: `26.1.2`
- **Java**: `26` (Due to Minecraft 26.1.2 requirements)
- **Fabric Loader**: `>=0.16.0` (configured with `0.16.14`)
- **Fabric API**: `0.152.1+26.1.2`


## Building from Source

> [!WARNING]
> Because Minecraft 26.1.2 requires Java 25+, and current Gradle versions crash on Java 25+, you cannot build this mod using Gradle right now.

To compile this mod, you must use the provided `build_and_install.bat` script. 

1. Open `build_and_install.bat` in a text editor.
2. The script will automatically prompt you for your Java compiler path and Fabric server directory if they are not already set.
3. Run the script to compile and install the mod directly.
