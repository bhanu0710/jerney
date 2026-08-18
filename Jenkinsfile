pipeline {

    agent any

    tools {
        nodejs 'nodejs'
    }

    stages {

        stage('Clean Workspace') {
            steps {
                cleanWs()
                sh 'echo "hii" '
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Backend Install') {
            steps {
                dir('backend') {
                    sh 'npm ci'
                }
            }
        }

        stage('Backend Lint') {
            steps {
                dir('backend') {
                    sh 'npm run lint'
                }
            }
        }

        stage('Frontend Install') {
            steps {
                dir('frontend') {
                    sh 'npm ci'
                }
            }
        }

        stage('Frontend Build') {
            steps {
                dir('frontend') {
                    sh 'npm run build'
                }
            }
        }

        stage('Frontend Lint') {
            steps {
                dir('frontend') {
                    sh 'npm run lint'
                }
            }
        }

    }

    post {

        success {
            echo "CI completed successfully."
        }

        failure {
            echo "CI failed."
        }

        always {
            cleanWs()
        }
    }

}
