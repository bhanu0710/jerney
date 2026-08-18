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

        stage('SonarQube Scan') {

             steps {

                 withSonarQubeEnv('sonar-server') {

                     sh """
                     ${SCANNER_HOME}/bin/sonar-scanner \
                     -Dsonar.projectKey=jerney \
                     -Dsonar.projectName=Jerney
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
