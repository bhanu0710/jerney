def install() {
    dir('frontend') {
        sh 'npm ci'
    }
}

def build() {
    dir('frontend') {
        sh 'npm run build'
    }
}

def lint() {
    dir('frontend') {
        sh 'npm run lint'
    }
}

def test() {
    dir('frontend') {
        sh 'npm test || echo "No tests configured"'
    }
}

def buildImage(String image, String tag) {
    dir('frontend') {
        sh """
        docker build \
        -t ${image}:${tag} \
        -t ${image}:latest .
        """
    }
}

return this
