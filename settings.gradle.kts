rootProject.name = "cheat-sheet"

include(":quiz-domain", ":quiz-persistence", ":quiz-app")

project(":quiz-domain").projectDir = file("modules/quiz-domain")
project(":quiz-persistence").projectDir = file("modules/quiz-persistence")
project(":quiz-app").projectDir = file("modules/quiz-app")
