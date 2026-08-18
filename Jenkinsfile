pipeline {

    agent any

    tools {
        nodejs 'nodejs'
    }

    environment {

    SCANNER_HOME = tool 'sonarqube'

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


        stage('Set Image Tag') {
            steps {
                script {
                    env.IMAGE_TAG = sh(
                        script: "git rev-parse --short HEAD",
                        returnStdout: true
                    ).trim()
        
                    echo "Image Tag: ${env.IMAGE_TAG}"
        }
    }
}

        stage('SonarQube Scan') {

             steps {

                 withSonarQubeEnv('sonar-server') {

                     sh """
                     ${SCANNER_HOME}/bin/sonar-scanner \
                     -Dsonar.projectKey=jerney \
                     -Dsonar.projectName=jerney
                      """

        }

    }

}



        stage('Quality Check') {
            steps {
                script {
                    waitForQualityGate abortPipeline: true, credentialsId: 'sonar-token' 
                }
            }
        }


        stage('Filesystem Scan') {
    
          parallel {
            stage('Trivy Backend FS') {
    
                  steps {
    
                      dir('backend') {
    
                         sh '''
                         trivy fs \
                         --severity HIGH,CRITICAL \
                         .
                         '''
    
            }
    
        }
    
    }
    
    
            stage('Trivy Frontend FS') {
    
                steps {
            
                    dir('frontend') {
            
                        sh '''
                        trivy fs \
                        --severity HIGH,CRITICAL \
                        .
                        '''
                    }
                }
            }
    
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


    stage('Build Docker Images') {

        parallel {
    
            stage('Backend') {
                steps {
                    dir('backend') {
                        sh """
                        docker build \
                        -t bhanu0710/jerney-backend:${IMAGE_TAG} .
                        """
                    }
                }
            }
    
            stage('Frontend') {
                steps {
                    dir('frontend') {
                        sh """
                        docker build \
                        -t bhanu0710/jerney-frontend:${IMAGE_TAG} .
                        """
                }
            }
        }

    }

}



    stage('Trivy Image Scan') {

        parallel {
    
            stage('Backend Image') {
                steps {
                    sh """
                    trivy image \
                    --severity HIGH,CRITICAL \
                    bhanu0710/jerney-backend:${IMAGE_TAG}
                    """
                }
            }
    
            stage('Frontend Image') {
                steps {
                    sh """
                    trivy image \
                    --severity HIGH,CRITICAL \
                    bhanu0710/jerney-frontend:${IMAGE_TAG}
                    """
            }
        }

    }

}



    stage('Push Images') {

        steps {
    
            withCredentials([
                usernamePassword(
                    credentialsId: 'docker-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )
            ]) {
    
                sh """
                echo \$DOCKER_PASS | docker login \
                    -u \$DOCKER_USER \
                    --password-stdin
    
                docker push bhanu0710/jerney-backend:${IMAGE_TAG}
                docker push bhanu0710/jerney-frontend:${IMAGE_TAG}
    
                docker tag bhanu0710/jerney-backend:${IMAGE_TAG} bhanu0710/jerney-backend:latest
                docker tag bhanu0710/jerney-frontend:${IMAGE_TAG} bhanu0710/jerney-frontend:latest
    
                docker push bhanu0710/jerney-backend:latest
                docker push bhanu0710/jerney-frontend:latest
    
                docker logout
                """
        }
    }
}



    stage('Update Kubernetes Manifests') {

        steps {
    
            sh """
            yq -i '
            .spec.template.spec.containers[0].image =
            "bhanu0710/jerney-backend:${IMAGE_TAG}"
            ' k8s/backend/deployment.yaml
    
            yq -i '
            .spec.template.spec.containers[0].image =
            "bhanu0710/jerney-frontend:${IMAGE_TAG}"
            ' k8s/frontend/deployment.yaml
            """
    }

}



    stage('Commit GitOps Changes') {

        steps {
    
            withCredentials([
                usernamePassword(
                    credentialsId: 'github',
                    usernameVariable: 'GIT_USER',
                    passwordVariable: 'GIT_TOKEN'
                )
            ]) {
    
                sh """
                git config user.name "Jenkins CI"
                git config user.email "jenkins@local"
    
                git add k8s/
    
                git commit -m "Update images to ${IMAGE_TAG}" || true
    
                git push https://${GIT_USER}:${GIT_TOKEN}@github.com/bhanu0710/Jerney.git HEAD:main
                """
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

            sh """
               docker system prune -af
               """
            cleanWs()
        }
    }

}
