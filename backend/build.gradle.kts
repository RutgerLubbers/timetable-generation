import net.ltgt.gradle.errorprone.errorprone
import java.nio.file.Paths
import java.util.*

group = "com.ilionx.timetable"
version = "0.0.1-SNAPSHOT"

val javaVersion = JavaVersion.VERSION_21

// quality check tool versions
val checkstyleVersion = "10.13.0"
val errorProneVersion = "2.30.0"
val picnicErrorProneVersion = "0.18.0"
val pmdVersion = "7.4.0"

// dependency versions
val datasourceProxyVersion = "1.10"
val groovyVersion = "4.0.13" // groovy 5 breaks spock for now.
val hawaiiFrameworkVersion = "6.0.0.M11"
val lombokVersion = "1.18.34"
val lombokMapStructBindingVersion = "0.2.0"
val mapStructVersion = "1.6.0"
val modelmapperVersion = "3.2.0"
val orgJsonVersion = "20240303"
val postgresqlVersion = "42.7.3"
val spockVersion = "2.4-M4-groovy-4.0"
//val springBootVersion = see the plugins section
val springBootVersion = "3.3.2"
val timefoldVersion = "1.13.0"

// overrule spring managed dependency versions
// See https://docs.spring.io/spring-boot/docs/current/reference/html/dependency-versions.html
//ext["byte-buddy.version"] = "1.14.4"

plugins {
    val benMamesVersion = "0.51.0"
    val errorProneVersion = "4.0.1"
    val springBootVersion = "3.3.2"
    val springDependencyManagementVersion = "1.1.6"

    // java adds the gradle java tasks.
    java
    // enables:
    // `jvm-test-suite`
    // `test-report-aggregation`
    // See See https://docs.gradle.org/current/userguide/jvm_test_suite_plugin.html

    idea

    // the spring boot plugin reacts to other plugins such as the java plugin.
    // In case dependency management is also available, the Spring Bill Of Materials will be injected containing versions of spring libraries.
    // see https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version springDependencyManagementVersion

    /*
     * The project-report plugin provides file reports on dependencies, tasks, etc.
     * See https://docs.gradle.org/current/userguide/project_report_plugin.html.
     */
    `project-report`

    /*
     * Code quality plugins
     */
    //    checkstyle
    //    pmd
    id("net.ltgt.errorprone") version errorProneVersion

    id("com.github.ben-manes.versions") version benMamesVersion

    /*
     * Code publication to maven repositories
     */
    `maven-publish`
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion

    // withSourcesJar()
}

tasks.named<Jar>("jar") {
    enabled = false
}

configurations {
    all {
        resolutionStrategy {
            cacheDynamicVersionsFor(5, "minutes")
            cacheChangingModulesFor(5, "minutes")
        }
        // exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
        exclude(group = "com.vaadin.external.google", module = "android-json")
        exclude(group = "org.slf4j", module = "slf4j-log4j12")
        exclude(group = "org.slf4j", module = "slf4j-jdk14")
        exclude(group = "log4j", module = "log4j")
        exclude(group = "org.apache.logging")
    }
}

repositories {
    mavenCentral()

    maven {
        var uid = System.getenv("TIMEFOLD_UID")
        var pwd = System.getenv("TIMEFOLD_PWD")

        val propertiesFile = Paths.get(".secrets").toFile()
        if (propertiesFile.exists()) {
            val properties = Properties()
            properties.load(propertiesFile.inputStream())
            uid = uid ?: properties["TIMEFOLD_UID"].toString()
            pwd = pwd ?: properties["TIMEFOLD_PWD"].toString()
        }

        if (pwd != null) {
            credentials {
                username = uid
                password = pwd
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }

        metadataSources {
            mavenPom()
            artifact()
            ignoreGradleMetadataRedirection()
        }

        name = "Timefold Solver Enterprise Edition"
        url = uri("https://timefold.jfrog.io/artifactory/releases/")
        println("[build] Configured '${name}' to use '${url}'.")
    }

    mavenLocal()
}

dependencies {

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop:${springBootVersion}")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.hawaiiframework:hawaii-starter-boot:${hawaiiFrameworkVersion}")

    implementation(platform("ai.timefold.solver:timefold-solver-bom:${timefoldVersion}"))
    implementation("ai.timefold.solver:timefold-solver-spring-boot-starter")

    implementation("org.apache.commons:commons-lang3")

    implementation("org.postgresql:postgresql:${postgresqlVersion}")
    implementation("net.ttddyy:datasource-proxy:${datasourceProxyVersion}")

    implementation(platform("org.apache.groovy:groovy-bom:${groovyVersion}"))
    implementation("org.apache.groovy:groovy:${groovyVersion}")

    // mapstruct is used to generate code to map from domain model classes to rest application model classes
    implementation("org.mapstruct:mapstruct:${mapStructVersion}")

    // modelmapper is used to generate code to map from domain model classes to rest application model classes
    implementation("org.modelmapper:modelmapper:${modelmapperVersion}")

    implementation("org.projectlombok:lombok:${lombokVersion}")


    annotationProcessor("org.mapstruct:mapstruct-processor:${mapStructVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:${lombokMapStructBindingVersion}")

    /*
     * Test
     */
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("ai.timefold.solver:timefold-solver-benchmark")
    testImplementation("ai.timefold.solver:timefold-solver-test")

    testImplementation(platform("org.spockframework:spock-bom:${spockVersion}"))
    testImplementation("org.spockframework:spock-core")
    testImplementation("org.spockframework:spock-spring")

    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.projectlombok:lombok:${lombokVersion}")

    // optional dependencies for using Spock
    testRuntimeOnly("net.bytebuddy:byte-buddy") {
        because("allows mocking of classes (in addition to interfaces)")
    }
//    testRuntimeOnly("org.objenesis:objenesis:${objenesisVersion}") {
//        because("allows mocking of classes without default constructor (together with ByteBuddy or CGLIB)")
//    }

    testAnnotationProcessor("org.mapstruct:mapstruct-processor:${mapStructVersion}")
    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    testAnnotationProcessor("org.projectlombok:lombok-mapstruct-binding:${lombokMapStructBindingVersion}")

    /*
     * Code Quality
     */
    errorprone("com.google.errorprone:error_prone_core:${errorProneVersion}")
    // Error Prone Support's additional bug checkers.
    // errorprone("com.uber.nullaway:nullaway:$nullawayVersion")
    errorprone("tech.picnic.error-prone-support:error-prone-contrib:${picnicErrorProneVersion}")

//    pmd("net.sourceforge.pmd:pmd-ant:${pmdVersion}")
//    pmd("net.sourceforge.pmd:pmd-java:${pmdVersion}")
}


tasks.withType<JavaCompile>().configureEach {
    // override default false
    options.isDeprecation = true
    // defaults to use the platform encoding
    options.encoding = Charsets.UTF_8.name()

    // add Xlint to our compiler options (but disable processing because of Spring warnings in code)
    // and make warnings be treated like errors

    // disable "-Werror" to allow automatic refactoring of our code
    options.compilerArgs.addAll(
        arrayOf(
            "-Xlint:all",
            "-Xlint:-processing",
            "-Xmaxerrs",
            "100",
            "-Xmaxwarns",
            "500"
        )
    )
    //options.compilerArgs.addAll(arrayOf("-Xlint:all", "-Xlint:-processing", "-Xmaxerrs", "100", "-Xmaxwarns", "500", "-Werror", "-Amapstruct.defaultComponentModel=spring"))

    options.errorprone {
        disableWarningsInGeneratedCode.set(true)
        allDisabledChecksAsWarnings.set(true)
        allErrorsAsWarnings.set(true)

        // For now disable, discuss
        disable("Var", "CollectorMutability")
        disable("Varifier")
        // The pattern constant first is always null proof, discuss
        disable("YodaCondition")

        // String.format allows more descriptive texts than String.join.
        disable("StringJoin")
        // Disabled, clashes with settings in IntelliJ
        disable("UngroupedOverloads")
        // Disabled, since IntelliJ does this for us:
        disable("BooleanParameter")
        // Disabled, since we do not require to be compliant with:
        disable("Java7ApiChecker", "Java8ApiChecker", "AndroidJdkLibsChecker")

        disable("CanonicalAnnotationSyntax")

        // The auto patch is disabled for now, it _seems_ that having this patching in place makes error-prone
        // only check the checks that can be patched. Can be enabled to fix bugs if there are too many.
        //
        errorproneArgs.addAll(
            "-XepPatchChecks:AutowiredConstructor,DeadException,DefaultCharset,LexicographicalAnnotationAttributeListing,LexicographicalAnnotationListing,MethodCanBeStatic,MissingOverride,MutableConstantField,RemoveUnusedImports,StaticImport,TimeZoneUsage,UnnecessaryFinal,UnnecessarilyFullyQualified",
            "-XepPatchLocation:IN_PLACE"
        )
    }
}


/**
 * Testing
 */
testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useSpock(spockVersion)
        }

        val itest by registering(JvmTestSuite::class) {
            useSpock(spockVersion)

            val itestDependencies = dependencies

            itestDependencies.implementation(sourceSets["main"].output)
            itestDependencies.implementation(sourceSets["test"].output)

            configurations.implementation {
                dependencies.forEach { dep -> itestDependencies.implementation(dep) }
            }
            configurations.runtimeOnly {
                dependencies.forEach { dep -> itestDependencies.implementation(dep) }
            }
            configurations.testImplementation {
                dependencies.forEach { dep -> itestDependencies.implementation(dep) }
            }
            configurations.testRuntimeOnly {
                dependencies.forEach { dep -> itestDependencies.implementation(dep) }
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

tasks.named<Test>("test") {
    testLogging {
        events("passed", "skipped", "failed")
    }
}
tasks.named<Test>("itest") {
    testLogging {
        events("passed", "skipped", "failed")
    }
}
tasks.named("check") {
    dependsOn(":itest")
}

///**
// * Checkstyle
// */
//checkstyle {
//    toolVersion = checkstyleVersion
//    // configProperties = mapOf(
//    //         "checkstyle.cache.file" to file("checkstyle.cache")
//    // )
//}
//
//tasks.withType<Checkstyle>().configureEach {
//    reports {
//        xml.required.set(true)
//        html.required.set(true)
//        html.stylesheet =
//            resources.text.fromFile("config/checkstyle/checkstyle-no-frames-severity-sorted.xsl")
//    }
//    isShowViolations = false
//}
//
//
///**
// * PMD
// */
//pmd {
//    toolVersion = pmdVersion
//    isConsoleOutput = true
//    isIgnoreFailures = false
//    //    rulesMinimumPriority.set(5)
//    threads.set(4)
//    ruleSetConfig = resources.text.fromFile(projectDir.path + "/config/pmd/pmd.xml")
//    // clear the default list of rules, otherwise this will override our custom configuration.
//    ruleSets = listOf<String>()
//}
//
//
///**
// * Maven publications
// */
//publishing {
//    publications {
//        create<MavenPublication>("bootJar") {
//            groupId = "com.vodafoneziggo.naas.swamp"
//            artifactId = "swamp-be"
//            artifact(tasks.getByName("bootJar"))
//        }
//    }
//    repositories {
//        maven {
//            var artifactoryUrl = System.getenv("ARTIFACTORY_URL")
//            var artifactoryUid = System.getenv("ARTIFACTORY_UID")
//            var artifactoryPwd = System.getenv("ARTIFACTORY_PWD")
//
//            val propertiesFile = Paths.get(".secrets").toFile()
//            if (propertiesFile.exists()) {
//                val properties = Properties()
//                properties.load(propertiesFile.inputStream())
//                artifactoryUrl = artifactoryUrl ?: properties["artifactory_url"].toString()
//                artifactoryUid = artifactoryUid ?: properties["artifactory_uid"].toString()
//                artifactoryPwd = artifactoryPwd ?: properties["artifactory_pwd"].toString()
//            }
//
//            if (artifactoryPwd != null) {
//                credentials {
//                    username = artifactoryUid
//                    password = artifactoryPwd
//                }
//                authentication {
//                    create<BasicAuthentication>("basic")
//                }
//            }
//
//            if (rootProject.version.toString().contains("-SNAPSHOT")) {
//                url = uri("${artifactoryUrl}/win-libs-snapshots/")
//            } else {
//                url = uri("${artifactoryUrl}/win-libs-releases/")
//            }
//            name = "jfrogArtifactory"
//            println("[build] Configured '${name}' to use '${url}'.")
//        }
//    }
//}
