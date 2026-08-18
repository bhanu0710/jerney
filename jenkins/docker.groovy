def login() {

    withCredentials([
        usernamePassword(
            credentialsId: 'docker-creds',
            usernameVariable: 'DOCKER_USER',
            passwordVariable: 'DOCKER_PASS'
        )
    ]) {

        sh '''
        echo "$DOCKER_PASS" | docker login \
        -u "$DOCKER_USER" \
        --password-stdin
        '''
    }
}

def push(String image,String tag){

    sh """
    docker push ${image}:${tag}
    docker push ${image}:latest
    """
}

def logout(){

    sh 'docker logout'

}

return this
