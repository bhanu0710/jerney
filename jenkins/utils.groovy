def gitSha() {

    return sh(
        script: "git rev-parse --short HEAD",
        returnStdout: true
    ).trim()

}

def changedFiles() {

    return sh(
        script: """
        git diff --name-only HEAD~1 HEAD
        """,
        returnStdout: true
    ).trim().split("\n")

}

def backendChanged(files) {

    return files.any { file ->
        file.startsWith("backend/")
    }

}

def frontendChanged(files) {

    return files.any { file ->
        file.startsWith("frontend/")
    }

}

def manifestsChanged(files) {

    return files.any { file ->
        file.startsWith("k8s/")
    }

}

return this
