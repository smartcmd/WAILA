# 🔍 WAILA - What Am I Looking At

> 🎮 An AllayMC server plugin that lets you easily view block information!

[![AllayMC](https://img.shields.io/badge/AllayMC-0.2.1-blue)](https://github.com/AllayMC/Allay)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

## ✨ Features

### 📦 Basic Block Information
- 🏷️ Block identifier display
- ⏱️ Break time (dynamically calculated based on held tool)
- ✅ Harvestability check
- 📍 Block coordinates and distance

### 🎯 Special Block Support
| Block Type         | Displayed Info    |
|--------------------|-------------------|
| 🍰 Cake            | Remaining slices  |
| 🌾 Crops           | Growth stage      |
| 🔴 Redstone Wire   | Signal strength   |
| 🚪 Doors/Trapdoors | Open/Closed state |
| 🗑️ Composter      | Compost level     |

### 🏭 Block Entity Support
| Block Entity                    | Displayed Info            |
|---------------------------------|---------------------------|
| 🔥 Furnace/Blast Furnace/Smoker | Stored XP, fuel, progress |
| ⚗️ Brewing Stand                | Brew time, fuel           |
| 💎 Beacon                       | Effect types              |
| 📦 Containers                   | Item count/capacity       |
| 🎵 Jukebox                      | Currently playing record  |
| ⚡ Comparator                    | Output signal strength    |
| 🎹 Note Block                   | Note pitch                |

## 📥 Installation

1. Download the latest plugin JAR file
2. Place the JAR file in the `plugins` folder of your AllayMC server
3. Restart the server
4. Start using it! 🎉

## 🎮 Usage

No commands needed! Simply:

1. 👀 Look at any block
2. 📱 Information will automatically display in the ActionBar
3. 🔄 Auto-refreshes every 100ms

## 🌍 Language Support

| Language          | Code    | Status |
|-------------------|---------|--------|
| 🇺🇸 English (US) | `en_US` | ✅      |
| 🇬🇧 English (UK) | `en_GB` | ✅      |
| 🇨🇳 简体中文         | `zh_CN` | ✅      |
| 🇹🇼 繁體中文         | `zh_TW` | ✅      |

Language automatically switches based on the player's client settings!

## 🛠️ Development

### Build the Project

```bash
./gradlew build
```

### Project Structure

```
src/main/java/me/daoge/waila/
├── 📄 WAILA.java           # Main plugin class
├── 📄 TrKeys.java          # Translation key constants
├── 📁 info/
│   └── 📄 BlockInfoBuilder.java  # Block info builder
└── 📁 util/
    ├── 📄 RayCastUtil.java      # Raycast utility
    └── 📄 RayCastResult.java    # Raycast result
```

### Dependencies

- ☕ Java 21+
- 🎮 AllayMC API 0.2.1+

## 📜 Credits

This project is ported from [LSE_Waila](https://github.com/ZMBlocks/LSE_Waila). Thanks to the original author **小小的子沐呀** for the open-source contribution! 🙏

## 📄 License

This project is licensed under the MIT License.

<p align="center">
  Made with ❤️ for AllayMC
</p>
