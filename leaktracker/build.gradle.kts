/*
 * Copyright 2020 Appmattus Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") version "1.4.0"
}

apply(from = "$rootDir/gradle/scripts/jacoco.gradle.kts")
apply(from = "$rootDir/gradle/scripts/bintray.gradle.kts")
apply(from = "$rootDir/gradle/scripts/dokka-javadoc.gradle.kts")

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    compileOnly("androidx.annotation:annotation:1.1.0")

    testImplementation("junit:junit:4.13")
}

tasks.withType<Test> {
    testLogging {
        events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.SHORT
    }
}

tasks.withType<DokkaTask> {
    outputDirectory.set(buildDir.resolve("reports/dokka"))

    dokkaSourceSets {
        configureEach {
            skipDeprecated.set(true)

            sourceLink {
                localDirectory.set(rootDir)
                remoteUrl.set(URL("https://github.com/appmattus/leaktracker/blob/main/"))
                remoteLineSuffix.set("#L")
            }
        }
    }
}

tasks.named("test") {
    finalizedBy(tasks.named("jacocoTestReport"))
}

tasks.named("check") {
    dependsOn(rootProject.tasks.named("markdownlint"))
}
