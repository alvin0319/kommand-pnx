# kommand-pnx
A library for PowerNukkitX that adds easier usage on Kotlin with DSL support.

# Installation
## Gradle (Groovy)
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'dev.minjae:kommand-pnx:version'
}
```
## Gradle (Kotlin DSL)
```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("dev.minjae:kommand-pnx:version")
}
```

## Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url> 
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>dev.minjae</groupId>
        <artifactId>kommand-pnx</artifactId>
        <version>version</version>
    </dependency>
</dependencies>
```
Replace version with commit hash, or tag version.

# Example Usage
```kotlin
override fun onEnable() {
    logger.info("Hello World from onEnable()!")
    val command = command("test", "Test Command with Kommand!") {
        commandParameters.clear()
        overload("testOverload") {
            param("testIntParam", CommandParamType.INT)
            param("testPlayerParam", CommandParamType.TARGET)
        }
        param("testStringParam", CommandParamType.STRING)

        onExecute { commandSender, _, mutableEntry, commandLogger ->
            val value = mutableEntry.value
            if (value.size == 1) {
                val paramFirstValue = value[0]
                when (paramFirstValue) {
                    is StringNode -> {
                        commandLogger.addSuccess("Hello World from onExecute()! ${paramFirstValue.get<String>()}")
                            .output()
                    }

                    else -> throw IllegalArgumentException("Invalid paramFirstValue type: ${paramFirstValue::class.simpleName}")
                }
            } else {
                val firstValue = value[0].get<Int>()
                val secondValue = value[1].get<ArrayList<Player>>()
                commandLogger.addSuccess("Hello World from onExecute()! $firstValue, ${secondValue.firstOrNull()?.name ?: ""}")
                    .output()
            }
            1
        }
    }
    server.commandMap.register("test", command)
}

```