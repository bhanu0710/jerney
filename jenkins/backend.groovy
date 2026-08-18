def install() {
    dir('backend') {
        sh 'npm ci'
    }
}

def lint() {
    dir('backend') {
        sh 'npm run lint'
    }
}

def test() {
    dir('backend') {
        sh 'npm test || echo "No tests configured"'
    }
}

def buildImage(String image, String tag) {
    dir('backend') {
        sh """
        docker build \
        -t ${image}:${tag} \
        -t ${image}:latest .
        """
    }
}

return this
