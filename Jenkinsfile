pipeline {
    agent any 
    tools {
        
        nodejs 'nodejs'
    }
    environment  {
        SCANNER_HOME=tool 'sonarqube'
        IMAGE_NAME = "bhanu/myapp"
        
    }
    stages {
        stage('Cleaning Workspace') {
            steps {
                cleanWs()
            }
        }
        stage('Checkout from Git') {
            steps {
                git credentialsId: 'github', url: 'https://github.com/AmanPathak-DevOps/End-to-End-Kubernetes-Three-Tier-DevSecOps-Project.git'
            }
        }
        stage('Sonarqube Analysis') {
            steps {
                dir('Application-Code/frontend') {
                    withSonarQubeEnv('sonar-server') {
                        sh ''' $SCANNER_HOME/bin/sonar-scanner \
                        -Dsonar.projectName=frontend \
                        -Dsonar.projectKey=frontend '''
                    }
                }
            }
        }
        stage('Quality Check') {
            steps {
                script {
                    waitForQualityGate abortPipeline: false, credentialsId: 'sonar-token' 
                }
            }
        }
        
        stage('Trivy File Scan') {
            steps {
                dir('Application-Code/frontend') {
                    sh 'trivy fs . > trivyfs.txt'
                }
            }
        }
        stage("Docker Image Build") {
            steps {
                script {
                    dir('Application-Code/frontend') {
                            
                            sh ' docker build -t firstbuild .'
                    }
                }
            }
        }
        stage("Push Docker Image") {

    steps {

        withCredentials([usernamePassword(
            credentialsId: 'docker-creds',
            usernameVariable: 'DOCKER_USER',
            passwordVariable: 'DOCKER_PASS'
        )]) {

            sh '''
            docker tag firstbuild:latest $DOCKER_USER/myapp:${BUILD_NUMBER}

            docker tag firstbuild:latest $DOCKER_USER/myapp:latest

            echo "$DOCKER_PASS" | docker login \
                -u "$DOCKER_USER" \
                --password-stdin

            docker push $DOCKER_USER/myapp:${BUILD_NUMBER}

            docker push $DOCKER_USER/myapp:latest

            docker logout
            '''
        }

    }

}
        
        
            
                            
                
            
        
    }
}
