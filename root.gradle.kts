plugins {
    id("fabric-loom") version "1.7-SNAPSHOT" apply false
    id("net.fabricmc.fabric-loom") version "1.17.+" apply false
    id("com.replaymod.preprocess") version "221276c"
}

subprojects {
    repositories {
        //mavenLocal()
        mavenCentral()
        maven("https://libraries.minecraft.net/")
        maven("https://repo.spongepowered.org/repository/maven-public/")
        maven("https://github.com/jitsi/jitsi-maven-repository/raw/master/releases/")
        maven("https://maven.fabricmc.net/")
        maven("https://jitpack.io")
    }
}

preprocess {
    val mc12101 = createNode("1.21.1", 12101, "mojmap")
    val mc12100 = createNode("1.21", 12100, "mojmap")
    val mc12006 = createNode("1.20.6", 12006, "mojmap")
    val mc12005 = createNode("1.20.5", 12005, "mojmap")
    val mc12004 = createNode("1.20.4", 12004, "mojmap")
    val mc12002 = createNode("1.20.2", 12002, "mojmap")
    val mc12001 = createNode("1.20.1", 12001, "mojmap")
    val mc11904 = createNode("1.19.4", 11904, "mojmap")
    val mc11802 = createNode("1.18.2", 11802, "mojmap")
    val mc11800 = createNode("1.18", 11800, "mojmap")
    val mc11701 = createNode("1.17.1", 11701, "mojmap")
    val mc11605 = createNode("1.16.5", 11605, "mojmap")

    mc12101.link(mc12100)
    mc12100.link(mc12006)
    mc12006.link(mc12005)
    mc12005.link(mc12004)
    mc12004.link(mc12002)
    mc12002.link(mc12001)
    mc12001.link(mc11904)
    mc11904.link(mc11802, file("versions/mapping-1.19.4-1.18.2.txt"))
    mc11802.link(mc11800)
    mc11800.link(mc11701, file("versions/mapping-1.18.2-1.17.1.txt"))
    mc11701.link(mc11605, file("versions/mapping-1.17.1-1.16.5.txt"))

    val mc26100 = createNode("26.1", 26100, null)
    // 26.1 不链接预处理图，独立构建
    // 源码已迁移到 Mojang 官方命名，可直接编译
    // mc26100.link(mc12101, file("versions/mapping-1.21.1-26.1.txt"))
}