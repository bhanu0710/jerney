def gitSha(){

    return sh(
        script:'git rev-parse --short HEAD',
        returnStdout:true
    ).trim()

}

return this
