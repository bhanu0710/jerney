def updateBackend(String image){

    sh """
    yq -i '
    .spec.template.spec.containers[0].image="${image}"
    ' k8s/backend/deployment.yaml
    """

}

def updateFrontend(String image){

    sh """
    yq -i '
    .spec.template.spec.containers[0].image="${image}"
    ' k8s/frontend/deployment.yaml
    """

}

def push(){

    withCredentials([
        usernamePassword(
            credentialsId:'github',
            usernameVariable:'GIT_USER',
            passwordVariable:'GIT_PAT'
        )
    ]){

        sh '''

        git config user.name "Jenkins CI"

        git config user.email "jenkins@local"

        git add k8s/

        git diff --cached --quiet || git commit -m "Update images"

        git remote set-url origin https://${GIT_USER}:${GIT_PAT}@github.com/bhanu0710/jerney.git

        git push origin HEAD:main

        '''

    }

}

return this
